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

package studio.blacktech.furryblackplus.core.logging.backend;

import studio.blacktech.furryblackplus.FurryBlack;
import studio.blacktech.furryblackplus.core.common.enhance.FileEnhance;
import studio.blacktech.furryblackplus.core.common.enhance.TimeEnhance;
import studio.blacktech.furryblackplus.core.logging.annotation.LoggerXConfig;
import studio.blacktech.furryblackplus.core.logging.backend.wrapper.PlaceholdLoggerX;

import java.nio.file.Path;

import static studio.blacktech.furryblackplus.FurryBlack.LINE;
import static studio.blacktech.furryblackplus.core.logging.enums.LoggerXColor.RESET;

@SuppressWarnings("unused")

@LoggerXConfig(needLoggerFile = true)
public final class WritterLoggerX extends PlaceholdLoggerX {

  private static Path path;
  private static volatile boolean lock = false;

  public WritterLoggerX(String simple) {
    super(simple);
  }

  public WritterLoggerX(Class<?> clazz) {
    super(clazz);
  }

  //= ==================================================================================================================

  public static void init(Path path) {
    if (lock) return;
    lock = true;
    WritterLoggerX.path = path;
  }

  //= ==================================================================================================================

  @Override
  public void fatalImpl(String message) {
    if (message == null) return;
    String content = "[" + TimeEnhance.datetime() + "][FATAL][" + getName() + "]" + message;
    FileEnhance.append(path, content + LINE);
    if (isErrorEnabled()) FurryBlack.println(COLOR_FATAL + content + RESET);
  }

  @Override
  public void errorImpl(String message) {
    if (message == null) return;
    String content = "[" + TimeEnhance.datetime() + "][ERROR][" + getName() + "]" + message;
    FileEnhance.append(path, content + LINE);
    if (isErrorEnabled()) FurryBlack.println(COLOR_ERROR + content + RESET);
  }

  @Override
  public void warnImpl(String message) {
    if (message == null) return;
    String content = "[" + TimeEnhance.datetime() + "][WARN][" + getName() + "]" + message;
    FileEnhance.append(path, content + LINE);
    if (isWarnEnabled()) FurryBlack.println(COLOR_WARN + content + RESET);
  }

  @Override
  public void hintImpl(String message) {
    if (message == null) return;
    String content = "[" + TimeEnhance.datetime() + "][HINT][" + getName() + "]" + message;
    FileEnhance.append(path, content + LINE);
    if (isInfoEnabled()) FurryBlack.println(COLOR_HINT + content + RESET);
  }

  @Override
  public void seekImpl(String message) {
    if (message == null) return;
    String content = "[" + TimeEnhance.datetime() + "][SEEK][" + getName() + "]" + message;
    FileEnhance.append(path, content + LINE);
    if (isInfoEnabled()) FurryBlack.println(COLOR_SEEK + content + RESET);
  }

  @Override
  public void infoImpl(String message) {
    if (message == null) return;
    String content = "[" + TimeEnhance.datetime() + "][INFO][" + getName() + "]" + message;
    FileEnhance.append(path, content + LINE);
    if (isInfoEnabled()) FurryBlack.println(COLOR_INFO + content + RESET);
  }

  @Override
  public void debugImpl(String message) {
    if (message == null) return;
    String content = "[" + TimeEnhance.datetime() + "][DEBUG][" + getName() + "]" + message;
    FileEnhance.append(path, content + LINE);
    if (isDebugEnabled()) FurryBlack.println(COLOR_DEBUG + content + RESET);
  }

  @Override
  public void traceImpl(String message) {
    if (message == null) return;
    String content = "[" + TimeEnhance.datetime() + "][TRACE][" + getName() + "]" + message;
    FileEnhance.append(path, content + LINE);
    if (isTraceEnabled()) FurryBlack.println(COLOR_TRACE + content + RESET);
  }

}
