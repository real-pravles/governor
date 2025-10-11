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
import com.pravles.governor.LowCodeUtils;
import com.pravles.processengine.api.ActivityFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RunORRoutine implements ActivityFunction {
    private final CreateModel CREATE_MODEL_FN = new CreateModelVersion5();

    @Override
    public Map<String, Object> apply(final Map<String, Object> ctx) {
        // Load OR-Tools native library
        Loader.loadNativeLibraries();


        // Define time slots (one week)
        final List<TimeSlot> timeSlots = (List<TimeSlot>)ctx.get("time-slots");

        // Define tasks - now as single tasks with total hours
        final List<Activity> tasks = (List<Activity>) ctx.get("effort-estimates");

        final IntVar[][] hours = new IntVar[tasks.size()][timeSlots.size()];
        final CpModel model = CREATE_MODEL_FN.apply(CreateModelInput.builder()
                        .hours(hours)
                        .tasks(tasks)
                        .timeSlots(timeSlots)
                .build());


        // Solve the model
        CpSolver solver = new CpSolver();
        solver.getParameters().setRandomSeed(0);
        solver.getParameters().setMaxTimeInSeconds(10.0);

        System.out.println("Solving with Google OR-Tools (task splitting enabled)...");
        final CpSolverStatus status = solver.solve(model);

        final boolean includeSolverStatsInScheduleFile =
                LowCodeUtils.extractBooleanSetting((List) ctx.get("low-code"),
                        "include-solver-stats-in-schedule-file?", false);


        final String summary = composeMessage(status, solver, timeSlots, tasks,
                hours, includeSolverStatsInScheduleFile);

        ctx.put("summary", summary);

        return ctx;
    }

    private String composeMessage(final CpSolverStatus status,
                                  final CpSolver solver,
                                  final List<TimeSlot> timeSlots,
                                  final List<Activity> tasks,
                                  final IntVar[][] hours,
                                  final boolean includeSolverStats) {
        final StringBuilder sb = new StringBuilder();
        final String nl = System.lineSeparator();

        if (status == CpSolverStatus.OPTIMAL || status == CpSolverStatus.FEASIBLE) {

            sb.append(nl);
            sb.append("Status: " + status);
            sb.append(nl);
            sb.append("Objective value: " + solver.objectiveValue());
            sb.append(nl);
            sb.append("=== SCHEDULE ===");

            // Print schedule grouped by time slot
            for (TimeSlot slot : timeSlots) {
                List<String> workInSlot = new ArrayList<>();
                double totalHoursInSlot = 0;

                for (int i = 0; i < tasks.size(); i++) {
                    long hoursValue = solver.value(hours[i][slot.index]);
                    if (hoursValue > 0) {
                        double actualHours = hoursValue / 10.0;
                        totalHoursInSlot += actualHours;
                        final Activity activity = tasks.get(i);
                        workInSlot.add(String.format("%s.%s: %.1fh",
                                activity.project,
                                activity.id,
                                actualHours));
                    }
                }

                if (!workInSlot.isEmpty()) {
                    sb.append(nl);
                    sb.append(String.format("%s (%.1f/%.1fh used):", slot.day
                            , totalHoursInSlot, slot.availableHours));
                    sb.append(nl);
                    for (String work : workInSlot) {
                        sb.append("  - " + work);
                        sb.append(nl);
                    }
                }
            }

            // Print total hours per task (verification)
            sb.append(nl);
            sb.append("=== TASK COMPLETION ===");
            sb.append(nl);
            for (int i = 0; i < tasks.size(); i++) {
                Activity task = tasks.get(i);
                double totalScheduled = 0;

                for (int j = 0; j < timeSlots.size(); j++) {
                    totalScheduled += solver.value(hours[i][j]) / 10.0;
                }

                sb.append(String.format("%s: %.1f/%.1f hours scheduled",
                        task.project, totalScheduled, task.totalHoursNeeded
                        ));
                sb.append(nl);
            }

            // Statistics
            if (includeSolverStats) {
                sb.append(nl);
                sb.append("=== SOLVER STATISTICS ===");
                sb.append(nl);
                sb.append("Wall time: " + solver.wallTime() + "s" + nl);
                sb.append("Branches: " + solver.numBranches() + nl);
            }
        } else {
            sb.append("No solution found. Status: " + status + nl);
        }

        return sb.toString();
    }
}
