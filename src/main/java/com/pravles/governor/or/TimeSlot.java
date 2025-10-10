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

public class TimeSlot {
    int index;
    String day;
    double availableHours;
    String dateTime;
    String rule;

    TimeSlot(int index, String day, double availableHours,
             String dateTime, String rule) {
        this.index = index;
        this.day = day;
        this.availableHours = availableHours;
        this.dateTime = dateTime;
        this.rule = rule;
    }
}
