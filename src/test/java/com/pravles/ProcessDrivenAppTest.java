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

import com.pravles.processengine.util.LaunchInfoFactory;
import com.pravles.processengine.util.ProcessEngineLauncher;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static com.pravles.TestLaunchInfoFactory.EXECUTED_ACTIVITIES;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ProcessDrivenAppTest {
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