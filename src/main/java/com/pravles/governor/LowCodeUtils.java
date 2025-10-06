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

package com.pravles.governor;

import clojure.lang.Keyword;

import java.util.List;
import java.util.Optional;

public final class LowCodeUtils {
    private LowCodeUtils() {

    }

    public static boolean extractBooleanSetting(final List lowCode,
                                         final String name,
                                         final boolean defaultValue) {

        final Optional<List> settingValueOpt =
                lowCode.stream().filter(x -> x instanceof List)
                        .map(x -> (List) x)
                        .filter(x -> ((List) x).size() == 2)
                        .filter(x -> ((List) x).get(0)
                                .equals(Keyword.intern(name)))
                        .findFirst();
        if (settingValueOpt.isEmpty()) {
            return defaultValue;
        }
        return (boolean) settingValueOpt.get().get(1);
    }
}
