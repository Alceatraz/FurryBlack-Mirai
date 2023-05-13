/*
 * Copyright (C) 2021 Alceatraz @ BlackTechStudio
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms from the BTS Anti-Commercial & GNU Affero General
 * Public License as published by the Free Software Foundation, either
 * version 3 from the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty from
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * BTS Anti-Commercial & GNU Affero General Public License for more details.
 *
 * You should have received a copy from the BTS Anti-Commercial & GNU Affero
 * General Public License along with this program.
 *
 */

package studio.blacktech.furryblackplus.core.common.logger.support;

import studio.blacktech.furryblackplus.FurryBlack;
import studio.blacktech.furryblackplus.core.common.enhance.TimeEnhance;
import studio.blacktech.furryblackplus.core.common.logger.LoggerXConfig;
import studio.blacktech.furryblackplus.core.common.logger.base.LoggerX;
import studio.blacktech.furryblackplus.core.exception.CoreException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static studio.blacktech.furryblackplus.core.common.enhance.StringEnhance.extractStackTrace;
import static studio.blacktech.furryblackplus.core.common.logger.base.LoggerX.Color.RESET;

@SuppressWarnings("unused")

@LoggerXConfig(needLoggerFile = true)
public final class WriteLogger extends LoggerX {

  private static boolean lock;

  private static File logger;

  //= ==================================================================================================================

  public WriteLogger(String simple) {
    super(simple);
  }

  public WriteLogger(Class<?> clazz) {
    super(clazz);
  }

  //= ==================================================================================================================

  public static void initLoggerFile(File file) {
    if (lock) {
      throw new CoreException("Do not init twice");
    }
    lock = true;
    logger = file;
  }

  private static synchronized void handle(Color color, String message) {
    if (color == null) {
      FurryBlack.println(message);
    } else {
      FurryBlack.println(color + message + RESET);
    }
    try (FileWriter writer = new FileWriter(logger, StandardCharsets.UTF_8, true)) {
      writer.append(message);
      writer.append("\n");
      writer.flush();
    } catch (IOException exception) {
      System.err.println("LoggerX WriteLogger - writer log failed \n" + extractStackTrace(exception));
    }
  }

  //= ==================================================================================================================

  @Override
  public void fatalImpl(String message) {
    if (message == null) return;
    String result = "[" + TimeEnhance.datetime() + "][FATAL][" + name + "]" + message;
    WriteLogger.handle(FATAL, result);
  }

  @Override
  public void fatalImpl(Throwable throwable) {
    if (throwable == null) return;
    String result = "[" + TimeEnhance.datetime() + "][FATAL][" + name + "]" + extractStackTrace(throwable);
    WriteLogger.handle(FATAL, result);
  }

  @Override
  public void fatalImpl(String message, Throwable throwable) {
    String result = "[" + TimeEnhance.datetime() + "][FATAL][" + name + "]" + message + "\n" + extractStackTrace(throwable);
    WriteLogger.handle(FATAL, result);
  }

  //= ==================================================================================================================

  @Override
  public void errorImpl(String message) {
    if (message == null) return;
    String result = "[" + TimeEnhance.datetime() + "][ERROR][" + name + "]" + message;
    WriteLogger.handle(ERROR, result);
  }

  @Override
  public void errorImpl(Throwable throwable) {
    if (throwable == null) return;
    String result = "[" + TimeEnhance.datetime() + "][ERROR][" + name + "]" + extractStackTrace(throwable);
    WriteLogger.handle(ERROR, result);
  }

  @Override
  public void errorImpl(String message, Throwable throwable) {
    String result = "[" + TimeEnhance.datetime() + "][ERROR][" + name + "]" + message + "\n" + extractStackTrace(throwable);
    WriteLogger.handle(ERROR, result);
  }

  //= ==================================================================================================================

  @Override
  public void warnImpl(String message) {
    if (message == null) return;
    String result = "[" + TimeEnhance.datetime() + "][WARN][" + name + "]" + message;
    WriteLogger.handle(WARN, result);
  }

  @Override
  public void warnImpl(Throwable throwable) {
    if (throwable == null) return;
    String result = "[" + TimeEnhance.datetime() + "][WARN][" + name + "]" + extractStackTrace(throwable);
    WriteLogger.handle(WARN, result);
  }

  @Override
  public void warnImpl(String message, Throwable throwable) {
    String result = "[" + TimeEnhance.datetime() + "][WARN][" + name + "]" + message + "\n" + extractStackTrace(throwable);
    WriteLogger.handle(WARN, result);
  }

  //= ==================================================================================================================

  @Override
  public void hintImpl(String message) {
    if (message == null) return;
    String result = "[" + TimeEnhance.datetime() + "][HINT][" + name + "]" + message;
    WriteLogger.handle(HINT, result);
  }

  @Override
  public void hintImpl(Throwable throwable) {
    if (throwable == null) return;
    String result = "[" + TimeEnhance.datetime() + "][HINT][" + name + "]" + extractStackTrace(throwable);
    WriteLogger.handle(HINT, result);
  }

  @Override
  public void hintImpl(String message, Throwable throwable) {
    String result = "[" + TimeEnhance.datetime() + "][HINT][" + name + "]" + message + "\n" + extractStackTrace(throwable);
    WriteLogger.handle(HINT, result);
  }

  //= ==================================================================================================================

  @Override
  public void seekImpl(String message) {
    if (message == null) return;
    String result = "[" + TimeEnhance.datetime() + "][SEEK][" + name + "]" + message;
    WriteLogger.handle(SEEK, result);
  }

  @Override
  public void seekImpl(Throwable throwable) {
    if (throwable == null) return;
    String result = "[" + TimeEnhance.datetime() + "][SEEK][" + name + "]" + extractStackTrace(throwable);
    WriteLogger.handle(SEEK, result);
  }

  @Override
  public void seekImpl(String message, Throwable throwable) {
    String result = "[" + TimeEnhance.datetime() + "][SEEK][" + name + "]" + message + "\n" + extractStackTrace(throwable);
    WriteLogger.handle(SEEK, result);
  }

  //= ==================================================================================================================

  @Override
  public void infoImpl(String message) {
    if (message == null) return;
    String result = "[" + TimeEnhance.datetime() + "][INFO][" + name + "]" + message;
    WriteLogger.handle(null, result);
  }

  @Override
  public void infoImpl(Throwable throwable) {
    if (throwable == null) return;
    String result = "[" + TimeEnhance.datetime() + "][INFO][" + name + "]" + extractStackTrace(throwable);
    WriteLogger.handle(INFO, result);
  }

  @Override
  public void infoImpl(String message, Throwable throwable) {
    String result = "[" + TimeEnhance.datetime() + "][INFO][" + name + "]" + message + "\n" + extractStackTrace(throwable);
    WriteLogger.handle(INFO, result);
  }

  //= ==================================================================================================================

  @Override
  public void debugImpl(String message) {
    if (message == null) return;
    String result = "[" + TimeEnhance.datetime() + "][DEBUG][" + name + "]" + message;
    WriteLogger.handle(DEBUG, result);
  }

  @Override
  public void debugImpl(Throwable throwable) {
    if (throwable == null) return;
    String result = "[" + TimeEnhance.datetime() + "][DEBUG][" + name + "]" + extractStackTrace(throwable);
    WriteLogger.handle(DEBUG, result);
  }

  @Override
  public void debugImpl(String message, Throwable throwable) {
    String result = "[" + TimeEnhance.datetime() + "][DEBUG][" + name + "]" + message + "\n" + extractStackTrace(throwable);
    WriteLogger.handle(DEBUG, result);
  }

  //= ==================================================================================================================

  @Override
  public void traceImpl(String message) {
    if (message == null) return;
    String result = "[" + TimeEnhance.datetime() + "][TRACE][" + name + "]" + message;
    WriteLogger.handle(TRACE, result);
  }

  @Override
  public void traceImpl(Throwable throwable) {
    if (throwable == null) return;
    String result = "[" + TimeEnhance.datetime() + "][TRACE][" + name + "]" + extractStackTrace(throwable);
    WriteLogger.handle(TRACE, result);
  }

  @Override
  public void traceImpl(String message, Throwable throwable) {
    String result = "[" + TimeEnhance.datetime() + "][TRACE][" + name + "]" + message + "\n" + extractStackTrace(throwable);
    WriteLogger.handle(TRACE, result);
  }

}
