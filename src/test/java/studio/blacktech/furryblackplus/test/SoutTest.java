/*
 * Copyright (C) 2021 Alceatraz @ BlackTechStudio
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the BTS Anti Commercial & GNU Affero General
 * Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * BTS Anti Commercial & GNU Affero General Public License for more details.
 *
 * You should have received a copy of the BTS Anti Commercial & GNU Affero
 * General Public License along with this program.
 *
 */

package studio.blacktech.furryblackplus.test;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

public class SoutTest {


    public static void main(String[] args) {


        System.setOut(new TestPrintStream(System.out));

        System.out.println("2423423");


    }


    private static class TestPrintStream extends PrintStream {


        public TestPrintStream(@NotNull OutputStream out) {
            super(out);
        }

        public TestPrintStream(@NotNull OutputStream out, boolean autoFlush) {
            super(out, autoFlush);
        }

        public TestPrintStream(@NotNull OutputStream out, boolean autoFlush, @NotNull String encoding) throws UnsupportedEncodingException {
            super(out, autoFlush, encoding);
        }

        public TestPrintStream(OutputStream out, boolean autoFlush, Charset charset) {
            super(out, autoFlush, charset);
        }

        public TestPrintStream(@NotNull String fileName) throws FileNotFoundException {
            super(fileName);
        }

        public TestPrintStream(@NotNull String fileName, @NotNull String csn) throws FileNotFoundException, UnsupportedEncodingException {
            super(fileName, csn);
        }

        public TestPrintStream(String fileName, Charset charset) throws IOException {
            super(fileName, charset);
        }

        public TestPrintStream(@NotNull File file) throws FileNotFoundException {
            super(file);
        }

        public TestPrintStream(@NotNull File file, @NotNull String csn) throws FileNotFoundException, UnsupportedEncodingException {
            super(file, csn);
        }

        public TestPrintStream(File file, Charset charset) throws IOException {
            super(file, charset);
        }

        @Override
        public void println(@Nullable String x) {
            super.println("This ? " + x);
        }
    }


}
