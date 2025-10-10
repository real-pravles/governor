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
import com.google.ortools.Loader;
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverStatus;
import com.google.ortools.sat.IntVar;
import com.google.ortools.sat.LinearExpr;
import com.google.ortools.sat.LinearExprBuilder;
import com.pravles.governor.LowCodeUtils;
import com.pravles.processengine.api.ActivityFunction;

import java.util.List;
import java.util.Map;

public class CreateDefaultModel implements CreateModel{
    @Override
    public CpModel apply(final CreateModelInput input) {

        // Define time slots (one week)
        final List<TimeSlot> timeSlots = (List<TimeSlot>)input.getTimeSlots();

        // Define tasks - now as single tasks with total hours
        final List<Activity> tasks = (List<Activity>) input.getTasks();

        // Create the CP-SAT model
        final CpModel model = new CpModel();

        // Decision variables: hours[i][j] = how many hours of task i in slot j
        // Scale by 10 to work with integers (e.g., 2.5 hours = 25 tenths)
        final IntVar[][] hours = input.getHours();

        for (int i = 0; i < tasks.size(); i++) {
            for (int j = 0; j < timeSlots.size(); j++) {
                Activity task = tasks.get(i);
                TimeSlot slot = timeSlots.get(j);

                // Can assign between 0 and min(task_remaining, slot_available) hours
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

            // Sum of hours across all slots must equal total needed (scaled by 10)
            model.addEquality(totalTaskHours, (long)(task.totalHoursNeeded * 10));
        }

        // Constraint 2: Don't exceed available hours per time slot
        for (int j = 0; j < timeSlots.size(); j++) {
            TimeSlot slot = timeSlots.get(j);
            LinearExprBuilder slotHours = LinearExpr.newBuilder();

            for (int i = 0; i < tasks.size(); i++) {
                slotHours.add(hours[i][j]);
            }

            // Must not exceed available hours (scaled by 10)
            model.addLessOrEqual(slotHours, (long)(slot.availableHours * 10));
        }

        // Optional Constraint 3: Task-specific minimum hours per session
        // Novel: If worked on, must be at least 1 hour
        // SubStack: If worked on, must be at least 15 minutes (0.25 hours)
        // We use: hours[i][j] == 0 OR hours[i][j] >= minHoursScaled
        for (int i = 0; i < tasks.size(); i++) {
            Activity task = tasks.get(i);
            long minHoursScaled = (long)(task.minSessionHours * 10);
            long maxPossibleHours = (long)(Math.min(task.totalHoursNeeded,
                    timeSlots.get(i < timeSlots.size() ? i : 0).availableHours) * 10);

            for (int j = 0; j < timeSlots.size(); j++) {
                // Create boolean variable indicating if we work on this task in this slot
                IntVar isWorked = model.newBoolVar("task_" + i + "_slot_" + j + "_worked");

                // Big-M formulation to enforce: hours == 0 OR hours >= minHours
                // If isWorked == 0: hours[i][j] == 0
                // If isWorked == 1: hours[i][j] >= minHoursScaled

                long M = maxPossibleHours + 1; // Big-M value

                // hours[i][j] <= M * isWorked
                // (if isWorked = 0, then hours must be 0)
                LinearExprBuilder lhs = LinearExpr.newBuilder();
                lhs.add(hours[i][j]);
                lhs.addTerm(isWorked, -M);
                model.addLessOrEqual(lhs, 0);

                // hours[i][j] >= minHoursScaled * isWorked
                // (if isWorked = 1, then hours >= minHours; if 0, then >= 0)
                LinearExprBuilder lhs2 = LinearExpr.newBuilder();
                lhs2.add(hours[i][j]);
                lhs2.addTerm(isWorked, -minHoursScaled);
                model.addGreaterOrEqual(lhs2, 0);
            }
        }

        // Objective: Maximize priority-weighted work scheduled
        // (In this model, all work must be scheduled, so we optimize for priority placement)
        LinearExprBuilder objective = LinearExpr.newBuilder();
        for (int i = 0; i < tasks.size(); i++) {
            Activity task = tasks.get(i);
            for (int j = 0; j < timeSlots.size(); j++) {
                // Reward: priority * hours for each task-slot assignment
                // Higher priority work in earlier slots gets bonus
                long priorityWeight = task.priority;
                objective.addTerm(hours[i][j], priorityWeight);
            }
        }
        model.maximize(objective);
        return model;
    }
}
