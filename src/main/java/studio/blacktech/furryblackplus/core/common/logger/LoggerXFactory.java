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

package studio.blacktech.furryblackplus.core.common.logger;

import studio.blacktech.furryblackplus.core.common.annotation.Comment;
import studio.blacktech.furryblackplus.core.common.logger.base.LoggerX;
import studio.blacktech.furryblackplus.core.common.logger.support.NullLogger;
import studio.blacktech.furryblackplus.core.common.logger.support.PrintLogger;
import studio.blacktech.furryblackplus.core.common.logger.support.WriteLogger;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

@Comment("日志工厂")
public final class LoggerXFactory {

  //= ==================================================================================================================

  private static Class<? extends LoggerX> defaultLogger;
  private static final Map<String, Class<? extends LoggerX>> registry;

  //= ==================================================================================================================

  static {
    registry = new ConcurrentSkipListMap<>();
    LoggerXFactory.setDefault(WriteLogger.class);
    LoggerXFactory.registerProvider(NullLogger.class);
    LoggerXFactory.registerProvider(PrintLogger.class);
    LoggerXFactory.registerProvider(WriteLogger.class);
  }

  //= ==================================================================================================================

  public static String registerProvider(Class<? extends LoggerX> clazz) {
    if (!clazz.isAnnotationPresent(LoggerXConfig.class)) {
      return "未添加 LoggerXConfig 注解";
    }
    String name = clazz.getSimpleName().toLowerCase();
    Class<? extends LoggerX> exist = registry.get(name);
    if (exist != null) return "名称已被占用 " + name + " -> " + exist.getName();
    registry.put(name, clazz);
    return null;
  }

  public static Map<String, String> getProviders() {
    Map<String, String> result = new LinkedHashMap<>();
    for (Map.Entry<String, Class<? extends LoggerX>> entry : registry.entrySet()) {
      var k = entry.getKey();
      var v = entry.getValue().getName();
      result.put(k, v);
    }
    return result;
  }

  //= ==================================================================================================================

  public static String getDefault() {
    return defaultLogger.getSimpleName();
  }

  public static void setDefault(Class<? extends LoggerX> provider) {
    defaultLogger = provider;
  }

  public static boolean setDefault(String provider) {
    Class<? extends LoggerX> clazz = registry.get(provider);
    if (clazz == null) return false;
    defaultLogger = clazz;
    return true;
  }

  public static boolean needLoggerFile() {
    LoggerXConfig annotation = defaultLogger.getAnnotation(LoggerXConfig.class);
    return annotation.needLoggerFile();
  }

  public static void initLoggerFile(File file) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    Method initLoggerFile = defaultLogger.getDeclaredMethod("initLoggerFile", File.class);
    initLoggerFile.invoke(null, file);
  }

  //= ==================================================================================================================

  public static LoggerX newLogger(Class<?> clazz) {
    if (defaultLogger == null) {
      return new NullLogger(clazz);
    }
    LoggerX loggerX;
    try {
      loggerX = defaultLogger.getConstructor(Class.class).newInstance(clazz);
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException exception) {
      throw new RuntimeException(exception);
    }
    return loggerX;
  }

  public static LoggerX newLogger(String name) {
    if (defaultLogger == null) {
      return new NullLogger(name);
    }
    LoggerX loggerX;
    try {
      loggerX = defaultLogger.getConstructor(String.class).newInstance(name);
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException exception) {
      throw new RuntimeException(exception);
    }
    return loggerX;
  }

  //= ==================================================================================================================

}
