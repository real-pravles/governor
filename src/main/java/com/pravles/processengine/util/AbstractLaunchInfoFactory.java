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

package com.pravles.processengine.util;

import com.pravles.processengine.api.ActivityFunction;
import com.pravles.processengine.api.ConditionFunction;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public abstract class AbstractLaunchInfoFactory implements LaunchInfoFactory {
    protected abstract void initFnBindings(final Map<String,
            ActivityFunction> fnBindings);

    protected abstract void initCnBindings(final Map<String,
            ConditionFunction> cnBindings);
    protected abstract List<PpmnDiagramInfo> composeDiagramInfos();

    protected abstract Map<String, Object> composeInitialContext();

    public final ProcessEngineLaunchInfo createLaunchInfo() {
        final Map<String, ActivityFunction> fnBindings = new HashMap<>();
        initFnBindings(fnBindings);

        final Map<String, ConditionFunction> cnBindings = new HashMap<>();
        initCnBindings(cnBindings);

        return ProcessEngineLaunchInfo.builder()
                .initCtx(composeInitialContext())
                .fnBindings(fnBindings)
                .cnBindings(cnBindings)
                .diagrams(composeDiagramInfos())
                .build();
    }


    protected InputStream istream(final String name) {
        return getClass().getResourceAsStream(name);
    }
}
