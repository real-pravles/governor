/*
 * Copyright 2026 Pravles Redneckoff
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

package com.pravles.governor;

import com.pravles.ProdLaunchInfoFactory;
import com.pravles.processengine.util.LaunchInfoFactory;
import com.pravles.processengine.util.ProcessEngineLauncher;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.help.HelpFormatter;

import java.io.IOException;

@Slf4j
public class GovernorApp {
    public static void main(final String[] args) {
        final GovernorApp app = new GovernorApp();
        app.run(args);
    }

    void run(final String[] args) {
        final Options options = createOptions();
        final CommandLineParser parser = new DefaultParser();
        final HelpFormatter formatter = HelpFormatter.builder()
                .get();
        try {
            CommandLine cmd = parser.parse(options, args);

            if (cmd.hasOption("s")) {
                System.out.println("S switch is ON");
                // your logic here
            } else {
                System.out.println("S switch is OFF");
            }

        } catch (final ParseException e) {
            log.error("Command-line parsing error", e);
            try {
                formatter.printOptions(options);
            } catch (final IOException ex) {
                log.error("", ex);
            }
        }
        /*
        final LaunchInfoFactory lif = new ProdLaunchInfoFactory();
        new ProcessEngineLauncher().run(lif);

         */
    }

    private Options createOptions() {
        final Options options = new Options();
        final Option dOption = Option.builder("d")
                .longOpt("dailyWorkload")
                .desc("Read Singularity CSV file from stdin and write daily workloads to stdout")
                .build();
        options.addOption(dOption);
        return options;
    }
}
