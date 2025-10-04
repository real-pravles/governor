/*
 * Copyright 2025 Pravles Redneckoff
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the “Software”), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
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
