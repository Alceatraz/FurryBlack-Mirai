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

package top.btswork.furryblack.core.logging.backend;

import top.btswork.furryblack.FurryBlack;
import top.btswork.furryblack.core.common.enhance.FileEnhance;
import top.btswork.furryblack.core.common.enhance.TimeEnhance;
import top.btswork.furryblack.core.logging.LoggerX;
import top.btswork.furryblack.core.logging.annotation.LoggerXConfig;
import top.btswork.furryblack.core.logging.backend.wrapper.PlaceholdLoggerX;

import java.nio.file.Path;

import static top.btswork.furryblack.FurryBlack.LINE;
import static top.btswork.furryblack.core.logging.enums.LoggerXColor.RESET;

@SuppressWarnings("unused")

@LoggerXConfig(needLoggerFile = true)
public final class WriterLoggerX extends PlaceholdLoggerX {

  private static Path path;
  private static volatile boolean lock = false;

  public WriterLoggerX(String simple) {
    super(simple);
  }

  public WriterLoggerX(Class<?> clazz) {
    super(clazz);
  }

  //= ==================================================================================================================

  public static void init(Path path) {
    if (lock) return;
    lock = true;
    WriterLoggerX.path = path;
  }

  //= ==================================================================================================================

  @Override
  public void fatalImpl(String message) {
    if (message == null) return;
    String content = "[" + TimeEnhance.datetime() + "][FATAL][" + getName() + "]" + message;
    if (isEnableWriteAll() || isErrorEnabled()) FileEnhance.append(path, content + LINE);
    if (isErrorEnabled()) FurryBlack.terminalPrintln(LoggerX.COLOR_FATAL + content + RESET);
  }

  @Override
  public void errorImpl(String message) {
    if (message == null) return;
    String content = "[" + TimeEnhance.datetime() + "][ERROR][" + getName() + "]" + message;
    if (isEnableWriteAll() || isErrorEnabled()) FileEnhance.append(path, content + LINE);
    if (isErrorEnabled()) FurryBlack.terminalPrintln(LoggerX.COLOR_ERROR + content + RESET);
  }

  @Override
  public void warnImpl(String message) {
    if (message == null) return;
    String content = "[" + TimeEnhance.datetime() + "][WARN][" + getName() + "]" + message;
    if (isEnableWriteAll() || isWarnEnabled()) FileEnhance.append(path, content + LINE);
    if (isWarnEnabled()) FurryBlack.terminalPrintln(LoggerX.COLOR_WARN + content + RESET);
  }

  @Override
  public void hintImpl(String message) {
    if (message == null) return;
    String content = "[" + TimeEnhance.datetime() + "][HINT][" + getName() + "]" + message;
    if (isEnableWriteAll() || isInfoEnabled()) FileEnhance.append(path, content + LINE);
    if (isInfoEnabled()) FurryBlack.terminalPrintln(LoggerX.COLOR_HINT + content + RESET);
  }

  @Override
  public void seekImpl(String message) {
    if (message == null) return;
    String content = "[" + TimeEnhance.datetime() + "][SEEK][" + getName() + "]" + message;
    if (isEnableWriteAll() || isInfoEnabled()) FileEnhance.append(path, content + LINE);
    if (isInfoEnabled()) FurryBlack.terminalPrintln(LoggerX.COLOR_SEEK + content + RESET);
  }

  @Override
  public void infoImpl(String message) {
    if (message == null) return;
    String content = "[" + TimeEnhance.datetime() + "][INFO][" + getName() + "]" + message;
    if (isEnableWriteAll() || isInfoEnabled()) FileEnhance.append(path, content + LINE);
    if (isInfoEnabled()) FurryBlack.terminalPrintln(LoggerX.COLOR_INFO + content + RESET);
  }

  @Override
  public void debugImpl(String message) {
    if (message == null) return;
    String content = "[" + TimeEnhance.datetime() + "][DEBUG][" + getName() + "]" + message;
    if (isEnableWriteAll() || isDebugEnabled()) FileEnhance.append(path, content + LINE);
    if (isDebugEnabled()) FurryBlack.terminalPrintln(LoggerX.COLOR_DEBUG + content + RESET);
  }

  @Override
  public void traceImpl(String message) {
    if (message == null) return;
    String content = "[" + TimeEnhance.datetime() + "][TRACE][" + getName() + "]" + message;
    if (isEnableWriteAll() || isTraceEnabled()) FileEnhance.append(path, content + LINE);
    if (isTraceEnabled()) FurryBlack.terminalPrintln(LoggerX.COLOR_TRACE + content + RESET);
  }

  @Override protected void printImpl(String message) {
    FileEnhance.append(path, message);
    FurryBlack.terminalPrint(message);
  }

  @Override protected void printlnImpl(String message) {
    FileEnhance.append(path, message + LINE);
    FurryBlack.terminalPrintln(message);
  }
}
