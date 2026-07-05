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

import com.pravles.processengine.util.LaunchInfoFactory;
import com.pravles.processengine.util.ProcessEngineLauncher;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static com.pravles.TestLaunchInfoFactory.EXECUTED_ACTIVITIES;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GovernorAppTest {
    @Test
    public void givenCall_whenRun_thenProduceCorrectResult() {
        // Given
        final LaunchInfoFactory lif = new TestLaunchInfoFactory();

        // When
        final Map<String, Object> actualCtx =
                new ProcessEngineLauncher().run(lif);

        // Then
        final List<String> executedActivities = (List<String>)
                actualCtx.get(EXECUTED_ACTIVITIES);
        assertEquals(1, executedActivities.size());
        assertEquals(TestLaunchInfoFactory.HELLO_WORLD,
                executedActivities.get(0));
    }
}