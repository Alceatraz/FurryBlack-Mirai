/*
 * Copyright (C) 2021 Alceatraz @ BlackTechStudio
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the BTS Anti-Commercial & GNU Affero General
 * Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * BTS Anti-Commercial & GNU Affero General Public License for more details.
 *
 * You should have received a copy of the BTS Anti-Commercial & GNU Affero
 * General Public License along with this program.
 *
 */


package studio.blacktech.furryblackplus.core.common.logger.support;


import studio.blacktech.furryblackplus.FurryBlack;
import studio.blacktech.furryblackplus.core.common.logger.base.LoggerX;
import studio.blacktech.furryblackplus.core.common.time.TimeTool;


@SuppressWarnings("unused")


public final class PrintLogger extends LoggerX {


    public PrintLogger(String name) {
        super(name);
    }

    public PrintLogger(Class<?> clazz) {
        super(clazz);
    }


    // =================================================================================================================


    @Override
    public void bypassImpl(String message) {
        if (message == null) return;
        String result = "[" + TimeTool.datetime() + "][BYPASS][" + this.name + "]" + message;
        FurryBlack.terminalPrintLine(result);
    }

    @Override
    public void bypassImpl(Throwable throwable) {
        if (throwable == null) return;
        String result = "[" + TimeTool.datetime() + "][BYPASS][" + this.name + "]" + extractStackTrace(throwable);
        FurryBlack.terminalPrintLine(result);
    }

    @Override
    public void bypassImpl(String message, Throwable throwable) {
        String result = "[" + TimeTool.datetime() + "][BYPASS][" + this.name + "]" + message + "\r\n" + extractStackTrace(throwable);
        FurryBlack.terminalPrintLine(result);
    }


    // =================================================================================================================


    @Override
    public void fatalImpl(String message) {
        if (message == null) return;
        String result = Color.BRIGHT_MAGENTA + "[" + TimeTool.datetime() + "][FATAL][" + this.name + "]" + message + Color.RESET;
        FurryBlack.terminalPrintLine(result);
    }

    @Override
    public void fatalImpl(Throwable throwable) {
        if (throwable == null) return;
        String result = Color.BRIGHT_MAGENTA + "[" + TimeTool.datetime() + "][FATAL][" + this.name + "]" + extractStackTrace(throwable) + Color.RESET;
        FurryBlack.terminalPrintLine(result);
    }

    @Override
    public void fatalImpl(String message, Throwable throwable) {
        String result = Color.BRIGHT_MAGENTA + "[" + TimeTool.datetime() + "][FATAL][" + this.name + "]" + message + "\r\n" + extractStackTrace(throwable) + Color.RESET;
        FurryBlack.terminalPrintLine(result);
    }


    // =================================================================================================================


    @Override
    public void errorImpl(String message) {
        if (message == null) return;
        String result = Color.BRIGHT_RED + "[" + TimeTool.datetime() + "][ERROR][" + this.name + "]" + message + Color.RESET;
        FurryBlack.terminalPrintLine(result);
    }

    @Override
    public void errorImpl(Throwable throwable) {
        if (throwable == null) return;
        String result = Color.BRIGHT_RED + "[" + TimeTool.datetime() + "][ERROR][" + this.name + "]" + extractStackTrace(throwable) + Color.RESET;
        FurryBlack.terminalPrintLine(result);
    }

    @Override
    public void errorImpl(String message, Throwable throwable) {
        String result = Color.BRIGHT_RED + "[" + TimeTool.datetime() + "][ERROR][" + this.name + "]" + message + "\r\n" + extractStackTrace(throwable) + Color.RESET;
        FurryBlack.terminalPrintLine(result);
    }


    // =================================================================================================================


    @Override
    public void warnImpl(String message) {
        if (message == null) return;
        String result = Color.BRIGHT_YELLOW + "[" + TimeTool.datetime() + "][WARN][" + this.name + "]" + message + Color.RESET;
        FurryBlack.terminalPrintLine(result);
    }

    @Override
    public void warnImpl(Throwable throwable) {
        if (throwable == null) return;
        String result = Color.BRIGHT_YELLOW + "[" + TimeTool.datetime() + "][WARN][" + this.name + "]" + extractStackTrace(throwable) + Color.RESET;
        FurryBlack.terminalPrintLine(result);
    }

    @Override
    public void warnImpl(String message, Throwable throwable) {
        String result = Color.BRIGHT_YELLOW + "[" + TimeTool.datetime() + "][WARN][" + this.name + "]" + message + "\r\n" + extractStackTrace(throwable) + Color.RESET;
        FurryBlack.terminalPrintLine(result);
    }


    // =================================================================================================================


    @Override
    public void hintImpl(String message) {
        if (message == null) return;
        String result = Color.BRIGHT_CYAN + "[" + TimeTool.datetime() + "][HINT][" + this.name + "]" + message + Color.RESET;
        FurryBlack.terminalPrintLine(result);
    }

    @Override
    public void hintImpl(Throwable throwable) {
        if (throwable == null) return;
        String result = Color.BRIGHT_CYAN + "[" + TimeTool.datetime() + "][HINT][" + this.name + "]" + extractStackTrace(throwable) + Color.RESET;
        FurryBlack.terminalPrintLine(result);
    }

    @Override
    public void hintImpl(String message, Throwable throwable) {
        String result = Color.BRIGHT_CYAN + "[" + TimeTool.datetime() + "][HINT][" + this.name + "]" + message + "\r\n" + extractStackTrace(throwable) + Color.RESET;
        FurryBlack.terminalPrintLine(result);
    }


    // =================================================================================================================


    @Override
    public void seekImpl(String message) {
        if (message == null) return;
        String result = Color.BRIGHT_GREEN + "[" + TimeTool.datetime() + "][SEEK][" + this.name + "]" + message + Color.RESET;
        FurryBlack.terminalPrintLine(result);
    }

    @Override
    public void seekImpl(Throwable throwable) {
        if (throwable == null) return;
        String result = Color.BRIGHT_GREEN + "[" + TimeTool.datetime() + "][SEEK][" + this.name + "]" + extractStackTrace(throwable) + Color.RESET;
        FurryBlack.terminalPrintLine(result);
    }

    @Override
    public void seekImpl(String message, Throwable throwable) {
        String result = Color.BRIGHT_GREEN + "[" + TimeTool.datetime() + "][SEEK][" + this.name + "]" + message + "\r\n" + extractStackTrace(throwable) + Color.RESET;
        FurryBlack.terminalPrintLine(result);
    }


    // =================================================================================================================


    @Override
    public void infoImpl(String message) {
        if (message == null) return;
        String result = "[" + TimeTool.datetime() + "][INFO][" + this.name + "]" + message;
        FurryBlack.terminalPrintLine(result);
    }

    @Override
    public void infoImpl(Throwable throwable) {
        if (throwable == null) return;
        String result = "[" + TimeTool.datetime() + "][INFO][" + this.name + "]" + extractStackTrace(throwable);
        FurryBlack.terminalPrintLine(result);
    }

    @Override
    public void infoImpl(String message, Throwable throwable) {
        String result = "[" + TimeTool.datetime() + "][INFO][" + this.name + "]" + message + "\r\n" + extractStackTrace(throwable);
        FurryBlack.terminalPrintLine(result);
    }


    // =================================================================================================================


    @Override
    public void debugImpl(String message) {
        if (message == null) return;
        String result = Color.BRIGHT_BLACK + "[" + TimeTool.datetime() + "][DEBG][" + this.name + "]" + message + Color.RESET;
        FurryBlack.terminalPrintLine(result);
    }

    @Override
    public void debugImpl(Throwable throwable) {
        if (throwable == null) return;
        String result = Color.BRIGHT_BLACK + "[" + TimeTool.datetime() + "][DEBG][" + this.name + "]" + extractStackTrace(throwable) + Color.RESET;
        FurryBlack.terminalPrintLine(result);
    }

    @Override
    public void debugImpl(String message, Throwable throwable) {
        String result = Color.BRIGHT_BLACK + "[" + TimeTool.datetime() + "][DEBG][" + this.name + "]" + message + "\r\n" + extractStackTrace(throwable) + Color.RESET;
        FurryBlack.terminalPrintLine(result);
    }


    // =================================================================================================================


    @Override
    public void developImpl(String message) {
        if (message == null) return;
        String result = Color.GRAY + "[" + TimeTool.datetime() + "][DEVL][" + this.name + "]" + message + Color.RESET;
        FurryBlack.terminalPrintLine(result);
    }

    @Override
    public void developImpl(Throwable throwable) {
        if (throwable == null) return;
        String result = Color.GRAY + "[" + TimeTool.datetime() + "][INFO][" + this.name + "]" + extractStackTrace(throwable) + Color.RESET;
        FurryBlack.terminalPrintLine(result);
    }

    @Override
    public void developImpl(String message, Throwable throwable) {
        String result = Color.GRAY + "[" + TimeTool.datetime() + "][DEVL][" + this.name + "]" + message + "\r\n" + extractStackTrace(throwable) + Color.RESET;
        FurryBlack.terminalPrintLine(result);
    }


    // =================================================================================================================


    @Override
    public void verboseImpl(String message) {
        if (message == null) return;
        String result = Color.GRAY + "[" + TimeTool.datetime() + "][VERB][" + this.name + "]" + message + Color.RESET;
        FurryBlack.terminalPrintLine(result);
    }

    @Override
    public void verboseImpl(Throwable throwable) {
        if (throwable == null) return;
        String result = Color.GRAY + "[" + TimeTool.datetime() + "][VERB][" + this.name + "]" + extractStackTrace(throwable) + Color.RESET;
        FurryBlack.terminalPrintLine(result);
    }

    @Override
    public void verboseImpl(String message, Throwable throwable) {
        String result = Color.GRAY + "[" + TimeTool.datetime() + "][VERB][" + this.name + "]" + message + "\r\n" + extractStackTrace(throwable) + Color.RESET;
        FurryBlack.terminalPrintLine(result);
    }


}
