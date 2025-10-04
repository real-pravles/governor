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

package com.pravles.util;

import clojure.java.api.Clojure;
import clojure.lang.IFn;
import clojure.lang.RT;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class ShariysDog {

    public static final String TOP_LEVEL_FUNCTION_NAME = "гав";

    private ShariysDog() {

    }

    public static IFn woof(final String scriptFile, final String namespace,
                          final String functionName) {
        try {
            RT.loadResourceScript(scriptFile);
        } catch (final IOException e) {
            log.error("Can't initialize Clojure", e);
        }
        return Clojure.var(namespace, functionName);
    }

    public static <T> T woof(final String fname, final Object param1) {
        try {
            RT.loadResourceScript(String.format("clj/%s.clj", fname));
        } catch (final IOException e) {
            log.error("Can't initialize Clojure", e);
        }
        return (T) Clojure.var(fname, TOP_LEVEL_FUNCTION_NAME).invoke(param1);
    }

    public static <T> T woof(final String fname, final Object param1,
                                  final Object param2) {
        try {
            RT.loadResourceScript(String.format("clj/%s.clj", fname));
        } catch (final IOException e) {
            log.error("Can't initialize Clojure", e);
        }
        return (T) Clojure.var(fname, TOP_LEVEL_FUNCTION_NAME).invoke(param1, param2);
    }

    public static <T> T woof(final String fname, final Object param1,
                                  final Object param2, final Object param3) {
        try {
            RT.loadResourceScript(String.format("clj/%s.clj", fname));
        } catch (final IOException e) {
            log.error("Can't initialize Clojure", e);
        }
        return (T) Clojure.var(fname, TOP_LEVEL_FUNCTION_NAME).invoke(param1, param2, param3);
    }

    public static <T> T woof(final String fname, final Object param1,
                                  final Object param2, final Object param3,
                                  final Object param4) {
        try {
            RT.loadResourceScript(String.format("clj/%s.clj", fname));
        } catch (final IOException e) {
            log.error("Can't initialize Clojure", e);
        }
        return (T) Clojure.var(fname, TOP_LEVEL_FUNCTION_NAME).invoke(param1, param2,
                param3, param4);
    }

    public static <T> T woof(final String fname, final Object param1,
                                  final Object param2, final Object param3,
                                  final Object param4, final Object param5) {
        try {
            RT.loadResourceScript(String.format("clj/%s.clj", fname));
        } catch (final IOException e) {
            log.error("Can't initialize Clojure", e);
        }
        return (T) Clojure.var(fname, TOP_LEVEL_FUNCTION_NAME).invoke(param1, param2,
                param3, param4, param5);
    }

    public static IFn woof(final String fname) {
        try {
            RT.loadResourceScript(String.format("clj/%s.clj", fname));
        } catch (final IOException e) {
            log.error("Can't initialize Clojure", e);
        }
        return Clojure.var(fname, TOP_LEVEL_FUNCTION_NAME);
    }
}
