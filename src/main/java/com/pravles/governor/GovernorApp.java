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

import java.io.File;

public class GovernorApp {
    public static void main(final String[] args) {
        final GovernorApp app = new GovernorApp();
        app.run(args);
    }

    void run(final String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java -jar governor.jar governor.edn");
            return;
        }
        final File governorSettingsFile = new File(args[0]);
        if (!(governorSettingsFile.exists() && governorSettingsFile.canRead() && governorSettingsFile.canRead()
                && governorSettingsFile.isFile())) {
            System.err.println(String.format("File'%s' does not exist and/or " +
                            "is not readable and/or is not a file",
                    governorSettingsFile.getAbsolutePath()));
            return;
        }

        final LaunchInfoFactory lif =
                new ProdLaunchInfoFactory(governorSettingsFile.getAbsolutePath());
        new ProcessEngineLauncher().run(lif);
    }
}
