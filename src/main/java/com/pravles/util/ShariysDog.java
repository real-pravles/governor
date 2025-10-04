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
