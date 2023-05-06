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

package studio.blacktech.furryblackplus.core.common.logger.base;

import net.mamoe.mirai.utils.MiraiLogger;
import studio.blacktech.furryblackplus.common.Comment;
import studio.blacktech.furryblackplus.core.common.logger.LoggerXFactory;
import studio.blacktech.furryblackplus.core.common.logger.support.NullLogger;
import studio.blacktech.furryblackplus.core.common.logger.support.PrintLogger;
import studio.blacktech.furryblackplus.core.common.logger.support.WriteLogger;

import java.io.File;
import java.util.HashMap;

@SuppressWarnings("unused")

@Comment(
  value = "基础日志工具类 请使用工厂方法创建实例",
  relativeClass = {
    LoggerXFactory.class,
    NullLogger.class,
    PrintLogger.class,
    WriteLogger.class,
  }
)
public abstract class LoggerX implements MiraiLogger {

  private static Level level = Level.EVERYTHING;

  public static void initLoggerFile(File file) {
    throw new UnsupportedOperationException("LoggerX后端必须重写此方法");
  }

  //= ==================================================================================================================
  //= 内部实现

  protected final String name;

  public LoggerX(String name) {
    this.name = name;
  }

  public LoggerX(Class<?> clazz) {
    this(clazz.getSimpleName());
  }

  //= ==================================================================================================================
  //= 提供包装

  //= ==========================================================================

  public final void bypass(String message) {
    bypassImpl(message);
  }

  public final void bypass(Throwable throwable) {
    bypassImpl(throwable);
  }

  public final void bypass(String message, Throwable throwable) {
    bypassImpl(message, throwable);
  }

  //= ==========================================================================

  public final void fatal(String message) {
    if (Level.FATAL.shouldPrint(level)) fatalImpl(message);
  }

  public final void fatal(Throwable throwable) {
    if (Level.FATAL.shouldPrint(level)) fatalImpl(throwable);
  }

  public final void fatal(String message, Throwable throwable) {
    if (Level.FATAL.shouldPrint(level)) fatalImpl(message, throwable);
  }

  //= ==========================================================================

  @Override
  public void error(String message) {
    if (Level.ERROR.shouldPrint(level)) errorImpl(message);
  }

  @Override
  public void error(Throwable throwable) {
    if (Level.ERROR.shouldPrint(level)) errorImpl(throwable);
  }

  @Override
  public void error(String message, Throwable throwable) {
    if (Level.ERROR.shouldPrint(level)) errorImpl(message, throwable);
  }

  //= ==========================================================================

  public final void warning(String message) {
    if (Level.WARN.shouldPrint(level)) warnImpl(message);
  }

  public final void warning(Throwable throwable) {
    if (Level.WARN.shouldPrint(level)) warnImpl(throwable);
  }

  public final void warning(String message, Throwable throwable) {
    if (Level.WARN.shouldPrint(level)) warnImpl(message, throwable);
  }

  //= ==========================================================================

  public final void hint(String message) {
    if (Level.HINT.shouldPrint(level)) hintImpl(message);
  }

  public final void hint(Throwable throwable) {
    if (Level.ERROR.shouldPrint(level)) hintImpl(throwable);
  }

  public final void hint(String message, Throwable throwable) {
    if (Level.ERROR.shouldPrint(level)) hintImpl(message, throwable);
  }

  //= ==========================================================================

  public final void seek(String message) {
    if (Level.ERROR.shouldPrint(level)) seekImpl(message);
  }

  public final void seek(Throwable throwable) {
    if (Level.ERROR.shouldPrint(level)) seekImpl(throwable);
  }

  public final void seek(String message, Throwable throwable) {
    if (Level.ERROR.shouldPrint(level)) seekImpl(message, throwable);
  }

  //= ==========================================================================

  public final void info(String message) {
    if (Level.ERROR.shouldPrint(level)) infoImpl(message);
  }

  public final void info(Throwable throwable) {
    if (Level.ERROR.shouldPrint(level)) infoImpl(throwable);
  }

  public final void info(String message, Throwable throwable) {
    if (Level.ERROR.shouldPrint(level)) infoImpl(message, throwable);
  }

  //= ==========================================================================

  public final void debug(String message) {
    if (Level.DEBUG.shouldPrint(level)) debugImpl(message);
  }

  public final void debug(Throwable throwable) {
    if (Level.DEBUG.shouldPrint(level)) debugImpl(throwable);
  }

  public final void debug(String message, Throwable throwable) {
    if (Level.DEBUG.shouldPrint(level)) debugImpl(message, throwable);
  }

  //= ==========================================================================

  public final void develop(String message) {
    if (Level.DEVELOP.shouldPrint(level)) developImpl(message);
  }

  public final void develop(Throwable throwable) {
    if (Level.DEVELOP.shouldPrint(level)) developImpl(throwable);
  }

  public final void develop(String message, Throwable throwable) {
    if (Level.DEVELOP.shouldPrint(level)) developImpl(message, throwable);
  }

  //= ==========================================================================

  public final void verbose(String message) {
    if (Level.VERBOSE.shouldPrint(level)) verboseImpl(message);
  }

  public final void verbose(Throwable throwable) {
    if (Level.VERBOSE.shouldPrint(level)) verboseImpl(throwable);
  }

  public final void verbose(String message, Throwable throwable) {
    if (Level.VERBOSE.shouldPrint(level)) verboseImpl(message, throwable);
  }

  //= ==================================================================================================================
  // 提供接口

  protected abstract void bypassImpl(String message);

  protected abstract void bypassImpl(Throwable throwable);

  protected abstract void bypassImpl(String message, Throwable throwable);

  protected abstract void fatalImpl(String message);

  protected abstract void fatalImpl(Throwable throwable);

  protected abstract void fatalImpl(String message, Throwable throwable);

  protected abstract void errorImpl(String message);

  protected abstract void errorImpl(Throwable throwable);

  protected abstract void errorImpl(String message, Throwable throwable);

  protected abstract void warnImpl(String message);

  protected abstract void warnImpl(Throwable throwable);

  protected abstract void warnImpl(String message, Throwable throwable);

  protected abstract void hintImpl(String message);

  protected abstract void hintImpl(Throwable throwable);

  protected abstract void hintImpl(String message, Throwable throwable);

  protected abstract void seekImpl(String message);

  protected abstract void seekImpl(Throwable throwable);

  protected abstract void seekImpl(String message, Throwable throwable);

  protected abstract void infoImpl(String message);

  protected abstract void infoImpl(Throwable throwable);

  protected abstract void infoImpl(String message, Throwable throwable);

  protected abstract void debugImpl(String message);

  protected abstract void debugImpl(Throwable throwable);

  protected abstract void debugImpl(String message, Throwable throwable);

  protected abstract void developImpl(String message);

  protected abstract void developImpl(Throwable throwable);

  protected abstract void developImpl(String message, Throwable throwable);

  protected abstract void verboseImpl(String message);

  protected abstract void verboseImpl(Throwable throwable);

  protected abstract void verboseImpl(String message, Throwable throwable);

  //= ==================================================================================================================

  public static Level getLevel() {
    return level;
  }

  public static void setLevel(Level level) {
    if (level == null) {
      throw new IllegalArgumentException("Can't set logging level to null !");
    }
    LoggerX.level = level;
  }

  public static boolean setLevel(String level) {
    Level byName = Level.getByName(level);
    if (byName == null) {
      return false;
    }
    LoggerX.level = byName;
    return true;
  }

  //= ==================================================================================================================

  public enum Level {

    MUTE(0, "MUTE"),
    FATAL(1, "FATAL"),
    ERROR(2, "ERROR"),
    WARN(3, "WARN"),
    HINT(4, "HINT"),
    SEEK(5, "SEEK"),
    INFO(6, "INFO"),
    DEBUG(7, "DEBUG"),
    VERBOSE(8, "VERBOSE"),
    DEVELOP(9, "DEVELOP"),
    EVERYTHING(Integer.MAX_VALUE, "ALL"),

    ;

    private static final HashMap<String, Level> LOOKUP;

    static {
      LOOKUP = new HashMap<>();
      LOOKUP.put("mute", Level.MUTE);
      LOOKUP.put("fatal", Level.FATAL);
      LOOKUP.put("error", Level.ERROR);
      LOOKUP.put("warn", Level.WARN);
      LOOKUP.put("hint", Level.HINT);
      LOOKUP.put("seek", Level.SEEK);
      LOOKUP.put("info", Level.INFO);
      LOOKUP.put("debug", Level.DEBUG);
      LOOKUP.put("verbose", Level.VERBOSE);
      LOOKUP.put("develop", Level.DEVELOP);
      LOOKUP.put("everything", Level.EVERYTHING);
    }

    public static Level getByName(String name) {
      return LOOKUP.get(name.toLowerCase());
    }

    private final int level;
    private final String name;

    Level(int level, String name) {
      this.level = level;
      this.name = name;
    }

    public int getLevel() {
      return level;
    }

    public String getName() {
      return name;
    }

    public boolean shouldPrint(Level target) {
      return level <= target.level;
    }

  }

  public enum Color {

    RESET("\u001b[0m"),

    WHITE("\u001b[30m"),
    RED("\u001b[31m"),
    GREEN("\u001b[32m"),
    YELLOW("\u001b[33m"),
    BLUE("\u001b[34m"),
    MAGENTA("\u001b[35m"),
    CYAN("\u001b[36m"),
    GRAY("\u001b[90m"),
    BRIGHT_BLACK("\u001b[90m"),
    BRIGHT_RED("\u001b[91m"),
    BRIGHT_GREEN("\u001b[92m"),
    BRIGHT_YELLOW("\u001b[93m"),
    BRIGHT_BLUE("\u001b[94m"),
    BRIGHT_MAGENTA("\u001b[95m"),
    BRIGHT_CYAN("\u001b[96m"),
    BRIGHT_WHITE("\u001b[97m"),

    ;

    private final String value;

    Color(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return value;
    }
  }

  //= ==================================================================================================================
  // MiraLogger必备继承

  @Override
  public String getIdentity() {
    return name;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

}
