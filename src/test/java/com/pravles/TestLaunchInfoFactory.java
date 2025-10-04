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
