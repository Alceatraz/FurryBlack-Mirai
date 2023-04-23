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

import studio.blacktech.furryblack.core.enhance.Enhance;
import studio.blacktech.furryblack.core.enhance.TimeTool;
import studio.blacktech.furryblackplus.FurryBlack;
import studio.blacktech.furryblackplus.core.common.exception.moduels.load.LockedException;
import studio.blacktech.furryblackplus.core.common.logger.base.LoggerX;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@SuppressWarnings("unused")

public final class WriteLogger extends LoggerX {

  private static boolean lock;

  private static File logger;

  //= ==================================================================================================================

  public WriteLogger(String name) {
    super(name);
  }

  public WriteLogger(Class<?> clazz) {
    super(clazz);
  }

  //= ==================================================================================================================

  public static void init(File logger) {
    if (WriteLogger.lock) {
      throw new LockedException();
    }
    WriteLogger.lock = true;
    WriteLogger.logger = logger;
  }

  private static synchronized void handle(Color color, String message) {
    if (color == null) {
      FurryBlack.println(message);
    } else {
      FurryBlack.println(color + message + Color.RESET);
    }
    try (FileWriter writer = new FileWriter(logger, StandardCharsets.UTF_8, true)) {
      writer.append(message);
      writer.append("\n");
      writer.flush();
    } catch (IOException exception) {
      System.err.println("LoggerX WriteLogger - writer log failed \n" + Enhance.extractStackTrace(exception));
    }
  }

  //= ==================================================================================================================

  @Override
  public void bypassImpl(String message) {
    if (message == null) return;
    String result = "[" + TimeTool.datetime() + "][BYPASS][" + name + "]" + message;
    WriteLogger.handle(null, result);
  }

  @Override
  public void bypassImpl(Throwable throwable) {
    if (throwable == null) return;
    String result = "[" + TimeTool.datetime() + "][BYPASS][" + name + "]" + Enhance.extractStackTrace(throwable);
    WriteLogger.handle(null, result);
  }

  @Override
  public void bypassImpl(String message, Throwable throwable) {
    String result = "[" + TimeTool.datetime() + "][BYPASS][" + name + "]" + message + "\n" + Enhance.extractStackTrace(throwable);
    WriteLogger.handle(null, result);
  }

  //= ==================================================================================================================

  @Override
  public void fatalImpl(String message) {
    if (message == null) return;
    String result = "[" + TimeTool.datetime() + "][FATAL][" + name + "]" + message;
    WriteLogger.handle(Color.BRIGHT_MAGENTA, result);
  }

  @Override
  public void fatalImpl(Throwable throwable) {
    if (throwable == null) return;
    String result = "[" + TimeTool.datetime() + "][FATAL][" + name + "]" + Enhance.extractStackTrace(throwable);
    WriteLogger.handle(Color.BRIGHT_MAGENTA, result);
  }

  @Override
  public void fatalImpl(String message, Throwable throwable) {
    String result = "[" + TimeTool.datetime() + "][FATAL][" + name + "]" + message + "\n" + Enhance.extractStackTrace(throwable);
    WriteLogger.handle(Color.BRIGHT_MAGENTA, result);
  }

  //= ==================================================================================================================

  @Override
  public void errorImpl(String message) {
    if (message == null) return;
    String result = "[" + TimeTool.datetime() + "][ERROR][" + name + "]" + message;
    WriteLogger.handle(Color.BRIGHT_RED, result);
  }

  @Override
  public void errorImpl(Throwable throwable) {
    if (throwable == null) return;
    String result = "[" + TimeTool.datetime() + "][ERROR][" + name + "]" + Enhance.extractStackTrace(throwable);
    WriteLogger.handle(Color.BRIGHT_RED, result);
  }

  @Override
  public void errorImpl(String message, Throwable throwable) {
    String result = "[" + TimeTool.datetime() + "][ERROR][" + name + "]" + message + "\n" + Enhance.extractStackTrace(throwable);
    WriteLogger.handle(Color.BRIGHT_RED, result);
  }

  //= ==================================================================================================================

  @Override
  public void warnImpl(String message) {
    if (message == null) return;
    String result = "[" + TimeTool.datetime() + "][WARN][" + name + "]" + message;
    WriteLogger.handle(Color.BRIGHT_YELLOW, result);
  }

  @Override
  public void warnImpl(Throwable throwable) {
    if (throwable == null) return;
    String result = "[" + TimeTool.datetime() + "][WARN][" + name + "]" + Enhance.extractStackTrace(throwable);
    WriteLogger.handle(Color.BRIGHT_YELLOW, result);
  }

  @Override
  public void warnImpl(String message, Throwable throwable) {
    String result = "[" + TimeTool.datetime() + "][WARN][" + name + "]" + message + "\n" + Enhance.extractStackTrace(throwable);
    WriteLogger.handle(Color.BRIGHT_YELLOW, result);
  }

  //= ==================================================================================================================

  @Override
  public void hintImpl(String message) {
    if (message == null) return;
    String result = "[" + TimeTool.datetime() + "][HINT][" + name + "]" + message;
    WriteLogger.handle(Color.BRIGHT_CYAN, result);
  }

  @Override
  public void hintImpl(Throwable throwable) {
    if (throwable == null) return;
    String result = "[" + TimeTool.datetime() + "][HINT][" + name + "]" + Enhance.extractStackTrace(throwable);
    WriteLogger.handle(Color.BRIGHT_CYAN, result);
  }

  @Override
  public void hintImpl(String message, Throwable throwable) {
    String result = "[" + TimeTool.datetime() + "][HINT][" + name + "]" + message + "\n" + Enhance.extractStackTrace(throwable);
    WriteLogger.handle(Color.BRIGHT_CYAN, result);
  }

  //= ==================================================================================================================

  @Override
  public void seekImpl(String message) {
    if (message == null) return;
    String result = "[" + TimeTool.datetime() + "][SEEK][" + name + "]" + message;
    WriteLogger.handle(Color.BRIGHT_GREEN, result);
  }

  @Override
  public void seekImpl(Throwable throwable) {
    if (throwable == null) return;
    String result = "[" + TimeTool.datetime() + "][SEEK][" + name + "]" + Enhance.extractStackTrace(throwable);
    WriteLogger.handle(Color.BRIGHT_GREEN, result);
  }

  @Override
  public void seekImpl(String message, Throwable throwable) {
    String result = "[" + TimeTool.datetime() + "][SEEK][" + name + "]" + message + "\n" + Enhance.extractStackTrace(throwable);
    WriteLogger.handle(Color.BRIGHT_GREEN, result);
  }

  //= ==================================================================================================================

  @Override
  public void infoImpl(String message) {
    if (message == null) return;
    String result = "[" + TimeTool.datetime() + "][INFO][" + name + "]" + message;
    WriteLogger.handle(null, result);
  }

  @Override
  public void infoImpl(Throwable throwable) {
    if (throwable == null) return;
    String result = "[" + TimeTool.datetime() + "][INFO][" + name + "]" + Enhance.extractStackTrace(throwable);
    WriteLogger.handle(null, result);
  }

  @Override
  public void infoImpl(String message, Throwable throwable) {
    String result = "[" + TimeTool.datetime() + "][INFO][" + name + "]" + message + "\n" + Enhance.extractStackTrace(throwable);
    WriteLogger.handle(null, result);
  }

  //= ==================================================================================================================

  @Override
  public void debugImpl(String message) {
    if (message == null) return;
    String result = "[" + TimeTool.datetime() + "][DEBUG][" + name + "]" + message;
    WriteLogger.handle(Color.BRIGHT_BLACK, result);
  }

  @Override
  public void debugImpl(Throwable throwable) {
    if (throwable == null) return;
    String result = "[" + TimeTool.datetime() + "][DEBUG][" + name + "]" + Enhance.extractStackTrace(throwable);
    WriteLogger.handle(Color.BRIGHT_BLACK, result);
  }

  @Override
  public void debugImpl(String message, Throwable throwable) {
    String result = "[" + TimeTool.datetime() + "][DEBUG][" + name + "]" + message + "\n" + Enhance.extractStackTrace(throwable);
    WriteLogger.handle(Color.BRIGHT_BLACK, result);
  }

  //= ==================================================================================================================

  @Override
  public void developImpl(String message) {
    if (message == null) return;
    String result = "[" + TimeTool.datetime() + "][DEVEL][" + name + "]" + message;
    WriteLogger.handle(Color.GRAY, result);
  }

  @Override
  public void developImpl(Throwable throwable) {
    if (throwable == null) return;
    String result = "[" + TimeTool.datetime() + "][DEVEL][" + name + "]" + Enhance.extractStackTrace(throwable);
    WriteLogger.handle(Color.GRAY, result);
  }

  @Override
  public void developImpl(String message, Throwable throwable) {
    String result = "[" + TimeTool.datetime() + "][DEVEL][" + name + "]" + message + "\n" + Enhance.extractStackTrace(throwable);
    WriteLogger.handle(Color.GRAY, result);
  }

  //= ==================================================================================================================

  @Override
  public void verboseImpl(String message) {
    if (message == null) return;
    String result = "[" + TimeTool.datetime() + "][VERBOSE][" + name + "]" + message;
    WriteLogger.handle(Color.GRAY, result);
  }

  @Override
  public void verboseImpl(Throwable throwable) {
    if (throwable == null) return;
    String result = "[" + TimeTool.datetime() + "][VERBOSE][" + name + "]" + Enhance.extractStackTrace(throwable);
    WriteLogger.handle(Color.GRAY, result);
  }

  @Override
  public void verboseImpl(String message, Throwable throwable) {
    String result = "[" + TimeTool.datetime() + "][VERBOSE][" + name + "]" + message + "\n" + Enhance.extractStackTrace(throwable);
    WriteLogger.handle(Color.GRAY, result);
  }

}
