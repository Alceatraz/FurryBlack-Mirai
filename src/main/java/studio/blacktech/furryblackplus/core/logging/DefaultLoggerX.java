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

package studio.blacktech.furryblackplus.core.logging;

import studio.blacktech.furryblackplus.FurryBlack;
import studio.blacktech.furryblackplus.core.common.enhance.FileEnhance;
import studio.blacktech.furryblackplus.core.common.enhance.TimeEnhance;
import studio.blacktech.furryblackplus.core.logging.annotation.LoggerXConfig;

import java.io.File;
import java.nio.file.Path;

import static studio.blacktech.furryblackplus.FurryBlack.LINE;
import static studio.blacktech.furryblackplus.core.logging.enums.LoggerXColor.RESET;

@SuppressWarnings("unused")

@LoggerXConfig(needLoggerFile = true)
public final class DefaultLoggerX extends WrappedLoggerX {

  private static Path path;

  public DefaultLoggerX(String simple) {
    super(simple);
  }

  public DefaultLoggerX(Class<?> clazz) {
    super(clazz);
  }

  //= ==================================================================================================================

  @Override
  public void initLoggerFile(File file) {
    path = file.toPath();
  }

  //= ==================================================================================================================

  @Override
  public void fatalImpl(String message) {
    if (message == null) return;
    String content = "[" + TimeEnhance.datetime() + "][FATAL][" + simpleName + "]" + message;
    FurryBlack.println(COLOR_FATAL + content + RESET);
    FileEnhance.append(path, content + LINE);
  }

  @Override
  public void errorImpl(String message) {
    if (message == null) return;
    String content = "[" + TimeEnhance.datetime() + "][ERROR][" + simpleName + "]" + message;
    FurryBlack.println(COLOR_ERROR + content + RESET);
    FileEnhance.append(path, content + LINE);
  }

  @Override
  public void warnImpl(String message) {
    if (message == null) return;
    String content = "[" + TimeEnhance.datetime() + "][WARN][" + simpleName + "]" + message;
    FurryBlack.println(COLOR_WARN + content + RESET);
    FileEnhance.append(path, content + LINE);
  }

  @Override
  public void hintImpl(String message) {
    if (message == null) return;
    String content = "[" + TimeEnhance.datetime() + "][HINT][" + simpleName + "]" + message;
    FurryBlack.println(COLOR_HINT + content + RESET);
    FileEnhance.append(path, content + LINE);
  }

  @Override
  public void seekImpl(String message) {
    if (message == null) return;
    String content = "[" + TimeEnhance.datetime() + "][SEEK][" + simpleName + "]" + message;
    FurryBlack.println(COLOR_SEEK + content + RESET);
    FileEnhance.append(path, content + LINE);
  }

  @Override
  public void infoImpl(String message) {
    if (message == null) return;
    String content = "[" + TimeEnhance.datetime() + "][INFO][" + simpleName + "]" + message;
    FurryBlack.println(COLOR_INFO + content + RESET);
    FileEnhance.append(path, content + LINE);
  }

  @Override
  public void debugImpl(String message) {
    if (message == null) return;
    String content = "[" + TimeEnhance.datetime() + "][DEBUG][" + simpleName + "]" + message;
    FurryBlack.println(COLOR_DEBUG + content + RESET);
    FileEnhance.append(path, content + LINE);
  }

  @Override
  public void traceImpl(String message) {
    if (message == null) return;
    String content = "[" + TimeEnhance.datetime() + "][TRACE][" + simpleName + "]" + message;
    FurryBlack.println(COLOR_TRACE + content + RESET);
    FileEnhance.append(path, content + LINE);
  }

}
