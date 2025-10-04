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

import com.pravles.processengine.api.Engine;
import com.pravles.processengine.impl.EngineImpl;

import java.util.HashMap;
import java.util.Map;
import java.io.InputStream;

public class ProcessEngineLauncher {
    public Map<String, Object> run(final LaunchInfoFactory lif) {
        return run(lif.createLaunchInfo());
    }
    public Map<String, Object> run(final ProcessEngineLaunchInfo li) {
        final Map<String, InputStream> diagramInputStreamsByProcessIds =
                new HashMap<>();
        li.getDiagrams()
                        .stream()
                                .forEach(dd ->
                                        diagramInputStreamsByProcessIds
                                                .put(dd.getDiagramId(),
                                                        dd.getInputStream()));

        final Engine engine = new EngineImpl();
        return engine.runWithSubprocesses(diagramInputStreamsByProcessIds,
                li.getInitCtx(),
                li.getFnBindings(),
                li.getCnBindings(),
                PpmnDiagramInfo.ROOT);
    }
}
