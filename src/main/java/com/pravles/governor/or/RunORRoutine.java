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

import com.google.ortools.Loader;
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverStatus;
import com.google.ortools.sat.IntVar;
import com.google.ortools.sat.LinearExpr;
import com.google.ortools.sat.LinearExprBuilder;
import com.pravles.processengine.api.ActivityFunction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class RunORRoutine implements ActivityFunction {
    @Override
    public Map<String, Object> apply(final Map<String, Object> ctx) {
        // Load OR-Tools native library
        Loader.loadNativeLibraries();

        // Define time slots (one week)
        List<TimeSlot> timeSlots = Arrays.asList(
                new TimeSlot(0, "Monday", 2.0),
                new TimeSlot(1, "Tuesday", 2.0),
                new TimeSlot(2, "Wednesday", 2.0),
                new TimeSlot(3, "Thursday", 2.0),
                new TimeSlot(4, "Friday", 2.0),
                new TimeSlot(5, "Saturday", 10.0),
                new TimeSlot(6, "Sunday", 10.0)
        );

        // Define tasks - now as single tasks with total hours
        List<Task> tasks = Arrays.asList(
                new Task("novel", "Novel", 10, 20.0),        // 20 hours total
                new Task("substack", "SubStack", 5, 8.0)     // 8 hours total
        );

        // Create the CP-SAT model
        CpModel model = new CpModel();

        // Decision variables: hours[i][j] = how many hours of task i in slot j
        // Scale by 10 to work with integers (e.g., 2.5 hours = 25 tenths)
        IntVar[][] hours = new IntVar[tasks.size()][timeSlots.size()];

        for (int i = 0; i < tasks.size(); i++) {
            for (int j = 0; j < timeSlots.size(); j++) {
                Task task = tasks.get(i);
                TimeSlot slot = timeSlots.get(j);

                // Can assign between 0 and min(task_remaining, slot_available) hours
                long maxHours = (long)(Math.min(task.totalHoursNeeded, slot.availableHours) * 10);
                hours[i][j] = model.newIntVar(0, maxHours,
                        "task_" + i + "_slot_" + j + "_hours");
            }
        }

        // Constraint 1: Each task must have exactly its required total hours scheduled
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
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

        // Optional Constraint 3: Minimum hours per session (avoid tiny fragments)
        // If you work on a task in a slot, work at least 1 hour on it
        for (int i = 0; i < tasks.size(); i++) {
            for (int j = 0; j < timeSlots.size(); j++) {
                // Create boolean: is this task worked on in this slot?
                IntVar isWorked = model.newBoolVar("task_" + i + "_slot_" + j + "_worked");

                // If hours > 0, then isWorked = 1
                model.addGreaterOrEqual(hours[i][j], 1).onlyEnforceIf(isWorked);
                model.addEquality(hours[i][j], 0).onlyEnforceIf(isWorked.not());

                // If working on it, must do at least 1 hour (10 in scaled units)
                model.addGreaterOrEqual(hours[i][j], 10).onlyEnforceIf(isWorked);
            }
        }

        // Objective: Maximize priority-weighted work scheduled
        // (In this model, all work must be scheduled, so we optimize for priority placement)
        LinearExprBuilder objective = LinearExpr.newBuilder();
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            for (int j = 0; j < timeSlots.size(); j++) {
                // Reward: priority * hours for each task-slot assignment
                // Higher priority work in earlier slots gets bonus
                long priorityWeight = task.priority;
                objective.addTerm(hours[i][j], priorityWeight);
            }
        }
        model.maximize(objective);

        // Solve the model
        CpSolver solver = new CpSolver();
        solver.getParameters().setRandomSeed(0);
        solver.getParameters().setMaxTimeInSeconds(10.0);

        System.out.println("Solving with Google OR-Tools (task splitting enabled)...");
        CpSolverStatus status = solver.solve(model);

        if (status == CpSolverStatus.OPTIMAL || status == CpSolverStatus.FEASIBLE) {
            System.out.println("\nStatus: " + status);
            System.out.println("Objective value: " + solver.objectiveValue());
            System.out.println("\n=== SCHEDULE ===");

            // Print schedule grouped by time slot
            for (TimeSlot slot : timeSlots) {
                List<String> workInSlot = new ArrayList<>();
                double totalHoursInSlot = 0;

                for (int i = 0; i < tasks.size(); i++) {
                    long hoursValue = solver.value(hours[i][slot.index]);
                    if (hoursValue > 0) {
                        double actualHours = hoursValue / 10.0;
                        totalHoursInSlot += actualHours;
                        workInSlot.add(String.format("%s: %.1fh",
                                tasks.get(i).project, actualHours));
                    }
                }

                if (!workInSlot.isEmpty()) {
                    System.out.printf("\n%s (%.1f/%.1fh used):\n",
                            slot.day, totalHoursInSlot, slot.availableHours);
                    for (String work : workInSlot) {
                        System.out.println("  - " + work);
                    }
                }
            }

            // Print total hours per task (verification)
            System.out.println("\n=== TASK COMPLETION ===");
            for (int i = 0; i < tasks.size(); i++) {
                Task task = tasks.get(i);
                double totalScheduled = 0;

                for (int j = 0; j < timeSlots.size(); j++) {
                    totalScheduled += solver.value(hours[i][j]) / 10.0;
                }

                System.out.printf("%s: %.1f/%.1f hours scheduled\n",
                        task.project, totalScheduled, task.totalHoursNeeded);
            }

            // Statistics
            System.out.println("\n=== SOLVER STATISTICS ===");
            System.out.println("Wall time: " + solver.wallTime() + "s");
            System.out.println("Branches: " + solver.numBranches());

        } else {
            System.out.println("No solution found. Status: " + status);
        }

        return ctx;
    }
}
