/*
 * This file is part of Governor.
 *
 * Governor is free software: you can redistribute it and/or modify it under
 *  the terms of the GNU General Public License as published by the Free
 *  Software Foundation, either version 3 of the License, or (at your option)
 *  any later version.
 *
 * Governor is distributed in the hope that it will be useful, but WITHOUT
 *  ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Governor. If not, see <https://www.gnu.org/licenses/>.
 */

package com.pravles.governor.or;

import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.IntVar;
import com.google.ortools.sat.LinearExpr;
import com.google.ortools.sat.LinearExprBuilder;

import java.util.List;

public class CreateModelVersion5 implements CreateModel {

    // Threshold to determine high-priority tasks
    private static final int HIGH_PRIORITY_THRESHOLD = 10;

    // Percentage of each slot that should go to high-priority tasks
    private static final double HIGH_PRIORITY_ALLOCATION = 0.8; // 80%

    @Override
    public CpModel apply(final CreateModelInput input) {

        final List<TimeSlot> timeSlots = input.getTimeSlots();
        final List<Activity> tasks = input.getTasks();
        final CpModel model = new CpModel();
        final IntVar[][] hours = input.getHours();

        // Initialize decision variables
        for (int i = 0; i < tasks.size(); i++) {
            for (int j = 0; j < timeSlots.size(); j++) {
                Activity task = tasks.get(i);
                TimeSlot slot = timeSlots.get(j);
                long maxHours = (long)(Math.min(task.totalHoursNeeded, slot.availableHours) * 10);
                hours[i][j] = model.newIntVar(0, maxHours,
                        "task_" + i + "_slot_" + j + "_hours");
            }
        }

        // Constraint 1: Each task must have exactly its required total hours scheduled
        for (int i = 0; i < tasks.size(); i++) {
            Activity task = tasks.get(i);
            LinearExprBuilder totalTaskHours = LinearExpr.newBuilder();
            for (int j = 0; j < timeSlots.size(); j++) {
                totalTaskHours.add(hours[i][j]);
            }
            model.addEquality(totalTaskHours, (long)(task.totalHoursNeeded * 10));
        }

        // Constraint 2: Don't exceed available hours per time slot
        for (int j = 0; j < timeSlots.size(); j++) {
            TimeSlot slot = timeSlots.get(j);
            LinearExprBuilder slotHours = LinearExpr.newBuilder();
            for (int i = 0; i < tasks.size(); i++) {
                slotHours.add(hours[i][j]);
            }
            model.addLessOrEqual(slotHours, (long)(slot.availableHours * 10));
        }

        // Constraint 3: Task-specific minimum hours per session
        for (int i = 0; i < tasks.size(); i++) {
            Activity task = tasks.get(i);
            long minHoursScaled = (long)(task.minSessionHours * 10);

            for (int j = 0; j < timeSlots.size(); j++) {
                IntVar isWorked = model.newBoolVar("task_" + i + "_slot_" + j + "_worked");

                // Find the maximum possible hours for this task in any slot
                long maxPossibleHours = 0;
                for (TimeSlot slot : timeSlots) {
                    long candidate = (long)(Math.min(task.totalHoursNeeded, slot.availableHours) * 10);
                    if (candidate > maxPossibleHours) {
                        maxPossibleHours = candidate;
                    }
                }
                long M = maxPossibleHours + 1;

                // Big-M constraints
                LinearExprBuilder lhs = LinearExpr.newBuilder();
                lhs.add(hours[i][j]);
                lhs.addTerm(isWorked, -M);
                model.addLessOrEqual(lhs, 0);

                LinearExprBuilder lhs2 = LinearExpr.newBuilder();
                lhs2.add(hours[i][j]);
                lhs2.addTerm(isWorked, -minHoursScaled);
                model.addGreaterOrEqual(lhs2, 0);
            }
        }

        // NEW Constraint 4: Quota-based allocation per slot
        // High-priority tasks must get at least 80% of each slot's capacity
        for (int j = 0; j < timeSlots.size(); j++) {
            TimeSlot slot = timeSlots.get(j);
            long slotCapacity = (long)(slot.availableHours * 10); // scaled

            // Sum hours from high-priority tasks in this slot
            LinearExprBuilder highPriorityHours = LinearExpr.newBuilder();
            for (int i = 0; i < tasks.size(); i++) {
                Activity task = tasks.get(i);
                if (task.priority >= HIGH_PRIORITY_THRESHOLD) {
                    highPriorityHours.add(hours[i][j]);
                }
            }

            // High-priority tasks must get at least 80% of this slot
            long minHighPriorityHours = (long)(slotCapacity * HIGH_PRIORITY_ALLOCATION);
            model.addGreaterOrEqual(highPriorityHours, minHighPriorityHours);
        }

        // OBJECTIVE: Use quality and priority to decide which task goes where
        // Quality helps decide which high-priority task gets which high-quality slot
        LinearExprBuilder objective = LinearExpr.newBuilder();
        for (int i = 0; i < tasks.size(); i++) {
            Activity task = tasks.get(i);
            for (int j = 0; j < timeSlots.size(); j++) {
                TimeSlot slot = timeSlots.get(j);

                // Weight combines quality, priority, and slight time preference
                long slotQuality = slot.quality;
                long taskPriority = task.priority;
                long timeBonus = timeSlots.size() - j; // Prefer earlier slots as tiebreaker

                // Combined weight
                long weight = slotQuality * taskPriority * 1000 + timeBonus;

                objective.addTerm(hours[i][j], weight);
            }
        }
        model.maximize(objective);

        return model;
    }
}