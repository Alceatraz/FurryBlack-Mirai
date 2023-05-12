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

import static studio.blacktech.furryblackplus.core.common.enhance.StringEnhance.extractStackTrace;
import static studio.blacktech.furryblackplus.core.common.logger.base.LoggerX.Color.RESET;

@SuppressWarnings("unused")

@LoggerXConfig
public final class PrintLogger extends LoggerX {

  public PrintLogger(String name) {
    super(name);
  }

  public PrintLogger(Class<?> clazz) {
    super(clazz);
  }

  //= ==================================================================================================================

  @Override
  public void fatalImpl(String message) {
    if (message == null) return;
    String result = FATAL + "[" + TimeEnhance.datetime() + "][FATAL][" + name + "]" + message + RESET;
    FurryBlack.println(result);
  }

  @Override
  public void fatalImpl(Throwable throwable) {
    if (throwable == null) return;
    String result = FATAL + "[" + TimeEnhance.datetime() + "][FATAL][" + name + "]" + extractStackTrace(throwable) + RESET;
    FurryBlack.println(result);
  }

  @Override
  public void fatalImpl(String message, Throwable throwable) {
    String result = FATAL + "[" + TimeEnhance.datetime() + "][FATAL][" + name + "]" + message + "\n" + extractStackTrace(throwable) + RESET;
    FurryBlack.println(result);
  }

  //= ==================================================================================================================

  @Override
  public void errorImpl(String message) {
    if (message == null) return;
    String result = ERROR + "[" + TimeEnhance.datetime() + "][ERROR][" + name + "]" + message + RESET;
    FurryBlack.println(result);
  }

  @Override
  public void errorImpl(Throwable throwable) {
    if (throwable == null) return;
    String result = ERROR + "[" + TimeEnhance.datetime() + "][ERROR][" + name + "]" + extractStackTrace(throwable) + RESET;
    FurryBlack.println(result);
  }

  @Override
  public void errorImpl(String message, Throwable throwable) {
    String result = ERROR + "[" + TimeEnhance.datetime() + "][ERROR][" + name + "]" + message + "\n" + extractStackTrace(throwable) + RESET;
    FurryBlack.println(result);
  }

  //= ==================================================================================================================

  @Override
  public void warnImpl(String message) {
    if (message == null) return;
    String result = WARN + "[" + TimeEnhance.datetime() + "][WARN][" + name + "]" + message + RESET;
    FurryBlack.println(result);
  }

  @Override
  public void warnImpl(Throwable throwable) {
    if (throwable == null) return;
    String result = WARN + "[" + TimeEnhance.datetime() + "][WARN][" + name + "]" + extractStackTrace(throwable) + RESET;
    FurryBlack.println(result);
  }

  @Override
  public void warnImpl(String message, Throwable throwable) {
    String result = WARN + "[" + TimeEnhance.datetime() + "][WARN][" + name + "]" + message + "\n" + extractStackTrace(throwable) + RESET;
    FurryBlack.println(result);
  }

  //= ==================================================================================================================

  @Override
  public void hintImpl(String message) {
    if (message == null) return;
    String result = HINT + "[" + TimeEnhance.datetime() + "][HINT][" + name + "]" + message + RESET;
    FurryBlack.println(result);
  }

  @Override
  public void hintImpl(Throwable throwable) {
    if (throwable == null) return;
    String result = HINT + "[" + TimeEnhance.datetime() + "][HINT][" + name + "]" + extractStackTrace(throwable) + RESET;
    FurryBlack.println(result);
  }

  @Override
  public void hintImpl(String message, Throwable throwable) {
    String result = HINT + "[" + TimeEnhance.datetime() + "][HINT][" + name + "]" + message + "\n" + extractStackTrace(throwable) + RESET;
    FurryBlack.println(result);
  }

  //= ==================================================================================================================

  @Override
  public void seekImpl(String message) {
    if (message == null) return;
    String result = SEEK + "[" + TimeEnhance.datetime() + "][SEEK][" + name + "]" + message + RESET;
    FurryBlack.println(result);
  }

  @Override
  public void seekImpl(Throwable throwable) {
    if (throwable == null) return;
    String result = SEEK + "[" + TimeEnhance.datetime() + "][SEEK][" + name + "]" + extractStackTrace(throwable) + RESET;
    FurryBlack.println(result);
  }

  @Override
  public void seekImpl(String message, Throwable throwable) {
    String result = SEEK + "[" + TimeEnhance.datetime() + "][SEEK][" + name + "]" + message + "\n" + extractStackTrace(throwable) + RESET;
    FurryBlack.println(result);
  }

  //= ==================================================================================================================

  @Override
  public void infoImpl(String message) {
    if (message == null) return;
    String result = INFO + "[" + TimeEnhance.datetime() + "][INFO][" + name + "]" + message + RESET;
    FurryBlack.println(result);
  }

  @Override
  public void infoImpl(Throwable throwable) {
    if (throwable == null) return;
    String result = INFO + "[" + TimeEnhance.datetime() + "][INFO][" + name + "]" + extractStackTrace(throwable) + RESET;
    FurryBlack.println(result);
  }

  @Override
  public void infoImpl(String message, Throwable throwable) {
    String result = INFO + "[" + TimeEnhance.datetime() + "][INFO][" + name + "]" + message + "\n" + extractStackTrace(throwable) + RESET;
    FurryBlack.println(result);
  }

  //= ==================================================================================================================

  @Override
  public void debugImpl(String message) {
    if (message == null) return;
    String result = DEBUG + "[" + TimeEnhance.datetime() + "][DEBUG][" + name + "]" + message + RESET;
    FurryBlack.println(result);
  }

  @Override
  public void debugImpl(Throwable throwable) {
    if (throwable == null) return;
    String result = DEBUG + "[" + TimeEnhance.datetime() + "][DEBUG][" + name + "]" + extractStackTrace(throwable) + RESET;
    FurryBlack.println(result);
  }

  @Override
  public void debugImpl(String message, Throwable throwable) {
    String result = DEBUG + "[" + TimeEnhance.datetime() + "][DEBUG][" + name + "]" + message + "\n" + extractStackTrace(throwable) + RESET;
    FurryBlack.println(result);
  }

  //= ==================================================================================================================

  @Override
  public void traceImpl(String message) {
    if (message == null) return;
    String result = TRACE + "[" + TimeEnhance.datetime() + "][TRACE][" + name + "]" + message + RESET;
    FurryBlack.println(result);
  }

  @Override
  public void traceImpl(Throwable throwable) {
    if (throwable == null) return;
    String result = TRACE + "[" + TimeEnhance.datetime() + "][TRACE][" + name + "]" + extractStackTrace(throwable) + RESET;
    FurryBlack.println(result);
  }

  @Override
  public void traceImpl(String message, Throwable throwable) {
    String result = TRACE + "[" + TimeEnhance.datetime() + "][TRACE][" + name + "]" + message + "\n" + extractStackTrace(throwable) + RESET;
    FurryBlack.println(result);
  }

}
