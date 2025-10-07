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

import com.pravles.processengine.util.LaunchInfoFactory;
import com.pravles.processengine.util.ProcessEngineLauncher;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.pravles.TestUtils.assertFilesEqual;
import static com.pravles.governor.TestLaunchInfoFactory.EXECUTED_ACTIVITIES;
import static java.lang.String.format;
import static org.apache.commons.io.FileUtils.cleanDirectory;
import static org.apache.commons.io.FileUtils.copyDirectory;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GovernorAppTest {
    static Stream<Arguments> scenarios() {
        return Stream.of(
                Arguments.of("01")
        );
    }

    @ParameterizedTest
    @MethodSource("scenarios")
    public void givenCall_whenRun_thenProduceCorrectResult(
            final String scenario) throws IOException {
        // Given
        final String baseDir = String.format("src/test/resources/scenarios/%s", scenario);
        final String inputDir = format("%s/input", baseDir);
        final String actualOutputDir = format("%s/actual", baseDir);
        final String governorSettingsFile = format("%s/governor.edn",
                actualOutputDir);
        final String expectedOutputDir = format( "%s/expected",
                baseDir);

        new File(actualOutputDir).mkdir();
        cleanDirectory(new File(actualOutputDir));
        copyDirectory(new File(inputDir),
                new File(actualOutputDir));

        final LaunchInfoFactory lif =
                new TestLaunchInfoFactory(governorSettingsFile);

        // When
        final Map<String, Object> actualCtx =
                new ProcessEngineLauncher().run(lif);

        // Then
        final Collection<File> expectedFiles =
                FileUtils.listFiles(new File(expectedOutputDir), null,
                        true);
        final Collection<File> actualFiles =
                FileUtils.listFiles(new File(actualOutputDir), null,
                        true);

        assertEquals(expectedFiles.size(), actualFiles.size());

        expectedFiles
                .stream()
                .forEach(expectedFile -> {
                    final String expectedFilePath = expectedFile.getPath();
                    final String actualFilePath =
                            expectedFilePath.replace(expectedOutputDir,
                                    actualOutputDir);
                    try {
                        assertFilesEqual(new File(expectedFilePath),
                                new File(actualFilePath));
                    } catch (IOException e) {
                        e.printStackTrace();
                        Assertions.fail(e.getMessage());
                    }
                });

    }
}