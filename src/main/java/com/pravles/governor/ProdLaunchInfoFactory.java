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

import com.pravles.processengine.api.ActivityFunction;
import com.pravles.processengine.api.ConditionFunction;
import com.pravles.processengine.util.AbstractLaunchInfoFactory;
import com.pravles.processengine.util.PpmnDiagramInfo;
import com.pravles.util.ClojureActivityFunction;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.pravles.processengine.util.PpmnDiagramInfo.ROOT;
import static java.util.Arrays.asList;

@RequiredArgsConstructor
public class ProdLaunchInfoFactory extends AbstractLaunchInfoFactory {
    private final String baseDir;

    @Override
    protected void initFnBindings(final Map<String, ActivityFunction> fnBindings) {
        Arrays.asList("validate-input",
                        "output-error-message",
                        "extract-data-from-low-code-file",
                        "create-OR-input",
                        "run-OR",
                        "calc-tech-ceiling",
                        "TBD")
                .stream()
                .forEach(f -> {
                    fnBindings.put(f,
                            new ClojureActivityFunction(f));
                });

        fnBindings.put("привет-мир",
                new ClojureActivityFunction("hello-world"));
    }

    @Override
    protected void initCnBindings(final Map<String, ConditionFunction> cnBindings) {

    }

    @Override
    protected Map<String, Object> composeInitialContext() {
        final Map<String, Object> ctx = new HashMap<>();
        ctx.put("baseDir", baseDir);
        return ctx;
    }

    @Override
    protected List<PpmnDiagramInfo> composeDiagramInfos() {
        return asList(PpmnDiagramInfo
                        .builder()
                        .diagramId(ROOT)
                        .inputStream(istream("/main.ppmn.fodg"))
                        .build());
    }
}
