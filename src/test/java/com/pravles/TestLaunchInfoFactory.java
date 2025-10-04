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

package com.pravles;

import com.pravles.processengine.TestActivity;
import com.pravles.processengine.api.ActivityFunction;

import java.util.ArrayList;
import java.util.Map;

public class TestLaunchInfoFactory extends ProdLaunchInfoFactory {

    public static final String HELLO_WORLD = "hello-world";
    public static final String EXECUTED_ACTIVITIES = "executedActivities";

    @Override
    protected void initFnBindings(Map<String, ActivityFunction> fnBindings) {
        fnBindings.put("привет-мир",
                new TestActivity(HELLO_WORLD));
    }

    @Override
    protected Map<String, Object> composeInitialContext() {
        final Map<String, Object> ctx = super.composeInitialContext();
        ctx.put(EXECUTED_ACTIVITIES, new ArrayList<>());
        return ctx;
    }
}
