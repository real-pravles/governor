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

import com.pravles.governor.or.RunORRoutine;
import com.pravles.processengine.api.ActivityFunction;
import com.pravles.util.ClojureActivityFunction;

import java.util.Map;

public class DetermineActivitiesToScheduleLaunchInfoFactory extends TestLaunchInfoFactory {
    public DetermineActivitiesToScheduleLaunchInfoFactory(String baseDir) {
        super(baseDir);
    }

    @Override
    protected void initFnBindings(Map<String, ActivityFunction> fnBindings) {
        super.initFnBindings(fnBindings);

        fnBindings.put("run-OR", new ClojureActivityFunction("hello-world"));
    }
}
