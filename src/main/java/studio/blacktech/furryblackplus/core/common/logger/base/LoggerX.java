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
import org.jetbrains.annotations.Nullable;
import studio.blacktech.furryblackplus.core.common.annotation.Comment;
import studio.blacktech.furryblackplus.core.common.logger.LoggerXFactory;
import studio.blacktech.furryblackplus.core.common.logger.support.NullLogger;
import studio.blacktech.furryblackplus.core.common.logger.support.PrintLogger;
import studio.blacktech.furryblackplus.core.common.logger.support.WriteLogger;

import java.io.File;
import java.util.HashMap;
import java.util.Objects;

@SuppressWarnings("unused")

@Comment(
  value = "基础日志工具类 请使用工厂方法创建实例",
  attention = {
    "TRACE = TRACE + VERBOSE(Mirai)",
    "DEBUG = DEBUG",
    "INFO  = HINT + SEEK + INFO",
    "WARN  = WARN",
    "ERROR = ERROR + FATAL",
    "CLOSE = N/A",
  },
  relativeClass = {
    LoggerXFactory.class,
    NullLogger.class,
    PrintLogger.class,
    WriteLogger.class,
  }
)
public abstract class LoggerX implements MiraiLogger {

  private static Level level = Level.TRACE;

  public static final Color FATAL = Color.BOLD_BRIGHT_RED;
  public static final Color ERROR = Color.BOLD_RED;
  public static final Color WARN = Color.BOLD_BRIGHT_YELLOW;
  public static final Color HINT = Color.BRIGHT_CYAN;
  public static final Color SEEK = Color.BRIGHT_GREEN;
  public static final Color INFO = Color.WHITE;
  public static final Color DEBUG = Color.BRIGHT_BLACK;
  public static final Color TRACE = Color.BLACK;

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

  //= ==========================================================================

  public boolean isTraceEnabled() {
    return Level.TRACE.shouldPrint(level);
  }

  public boolean isDebugEnabled() {
    return Level.DEBUG.shouldPrint(level);
  }

  public boolean isInfoEnabled() {
    return Level.INFO.shouldPrint(level);
  }

  public boolean isWarnEnabled() {
    return Level.WARN.shouldPrint(level);
  }

  public boolean isErrorEnabled() {
    return Level.ERROR.shouldPrint(level);
  }

  //= ==================================================================================================================
  //= 提供包装

  //= ==========================================================================

  public final void fatal(String message) {
    if (Level.ERROR.shouldPrint(level)) fatalImpl(message);
  }

  public final void fatal(Throwable throwable) {
    if (Level.ERROR.shouldPrint(level)) fatalImpl(throwable);
  }

  public final void fatal(String message, Throwable throwable) {
    if (Level.ERROR.shouldPrint(level)) fatalImpl(message, throwable);
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
    if (Level.INFO.shouldPrint(level)) hintImpl(message);
  }

  public final void hint(Throwable throwable) {
    if (Level.INFO.shouldPrint(level)) hintImpl(throwable);
  }

  public final void hint(String message, Throwable throwable) {
    if (Level.INFO.shouldPrint(level)) hintImpl(message, throwable);
  }

  //= ==========================================================================

  public final void seek(String message) {
    if (Level.INFO.shouldPrint(level)) seekImpl(message);
  }

  public final void seek(Throwable throwable) {
    if (Level.INFO.shouldPrint(level)) seekImpl(throwable);
  }

  public final void seek(String message, Throwable throwable) {
    if (Level.INFO.shouldPrint(level)) seekImpl(message, throwable);
  }

  //= ==========================================================================

  public final void info(String message) {
    if (Level.INFO.shouldPrint(level)) infoImpl(message);
  }

  public final void info(Throwable throwable) {
    if (Level.INFO.shouldPrint(level)) infoImpl(throwable);
  }

  public final void info(String message, Throwable throwable) {
    if (Level.INFO.shouldPrint(level)) infoImpl(message, throwable);
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

  public final void trace(String message) {
    if (Level.TRACE.shouldPrint(level)) traceImpl(message);
  }

  public final void trace(Throwable throwable) {
    if (Level.TRACE.shouldPrint(level)) traceImpl(throwable);
  }

  public final void trace(String message, Throwable throwable) {
    if (Level.TRACE.shouldPrint(level)) traceImpl(message, throwable);
  }

  //= ==========================================================================

  @Override
  public final void verbose(@Nullable String message) {
    if (Level.TRACE.shouldPrint(level)) debugImpl(message);
  }

  @Override
  public final void verbose(@Nullable String message, @Nullable Throwable throwable) {
    if (Level.TRACE.shouldPrint(level)) debugImpl(message, throwable);
  }

  //= ==================================================================================================================
  // 提供接口

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

  protected abstract void traceImpl(String message);

  protected abstract void traceImpl(Throwable throwable);

  protected abstract void traceImpl(String message, Throwable throwable);

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

  public static String process(String pattern, Object... objects) {
    for (Object object : objects) {
      String value = Objects.toString(object);
      pattern = pattern.replace("{}", value);
    }
    return pattern;
  }

  //= ==================================================================================================================

  public enum Level {

    CLOSE(0),
    ERROR(1),
    WARN(2),
    INFO(3),
    DEBUG(4),
    TRACE(5),

    ;

    private static final HashMap<String, Level> LOOKUP;

    static {
      LOOKUP = new HashMap<>();
      LOOKUP.put("close", Level.CLOSE);
      LOOKUP.put("error", Level.ERROR);
      LOOKUP.put("warn", Level.WARN);
      LOOKUP.put("info", Level.INFO);
      LOOKUP.put("debug", Level.DEBUG);
      LOOKUP.put("trace", Level.TRACE);
    }

    public static Level getByName(String name) {
      return LOOKUP.get(name.toLowerCase());
    }

    private final int level;

    Level(int level) {
      this.level = level;
    }

    public int getLevel() {
      return level;
    }

    public boolean shouldPrint(Level level) {
      return this.level <= level.level;
    }

  }

  public enum Color {

    RESET("\u001b[0m"),

    BLACK("\u001b[30m"),
    RED("\u001b[31m"),
    GREEN("\u001b[32m"),
    YELLOW("\u001b[33m"),
    BLUE("\u001b[34m"),
    MAGENTA("\u001b[35m"),
    CYAN("\u001b[36m"),
    WHITE("\u001b[37m"),

    BRIGHT_BLACK("\u001b[90m"),
    BRIGHT_RED("\u001b[91m"),
    BRIGHT_GREEN("\u001b[92m"),
    BRIGHT_YELLOW("\u001b[93m"),
    BRIGHT_BLUE("\u001b[94m"),
    BRIGHT_MAGENTA("\u001b[95m"),
    BRIGHT_CYAN("\u001b[96m"),
    BRIGHT_WHITE("\u001b[97m"),

    BOLD_BLACK("\u001b[1;30m"),
    BOLD_RED("\u001b[1;31m"),
    BOLD_GREEN("\u001b[1;32m"),
    BOLD_YELLOW("\u001b[1;33m"),
    BOLD_BLUE("\u001b[1;34m"),
    BOLD_MAGENTA("\u001b[1;35m"),
    BOLD_CYAN("\u001b[1;36m"),
    BOLD_WHITE("\u001b[1;37m"),

    BOLD_BRIGHT_BLACK("\u001b[1;90m"),
    BOLD_BRIGHT_RED("\u001b[1;91m"),
    BOLD_BRIGHT_GREEN("\u001b[1;92m"),
    BOLD_BRIGHT_YELLOW("\u001b[1;93m"),
    BOLD_BRIGHT_BLUE("\u001b[1;94m"),
    BOLD_BRIGHT_MAGENTA("\u001b[1;95m"),
    BOLD_BRIGHT_CYAN("\u001b[1;96m"),
    BOLD_BRIGHT_WHITE("\u001b[1;97m"),

    BACKGROUND_BLACK("\u001b[40m"),
    BACKGROUND_RED("\u001b[41m"),
    BACKGROUND_GREEN("\u001b[42m"),
    BACKGROUND_YELLOW("\u001b[43m"),
    BACKGROUND_BLUE("\u001b[44m"),
    BACKGROUND_MAGENTA("\u001b[45m"),
    BACKGROUND_CYAN("\u001b[46m"),
    BACKGROUND_WHITE("\u001b[47m"),

    BACKGROUND_BRIGHT_BLACK("\u001b[100m"),
    BACKGROUND_BRIGHT_RED("\u001b[101m"),
    BACKGROUND_BRIGHT_GREEN("\u001b[102m"),
    BACKGROUND_BRIGHT_YELLOW("\u001b[103m"),
    BACKGROUND_BRIGHT_BLUE("\u001b[104m"),
    BACKGROUND_BRIGHT_MAGENTA("\u001b[105m"),
    BACKGROUND_BRIGHT_CYAN("\u001b[106m"),
    BACKGROUND_BRIGHT_WHITE("\u001b[107m"),

    ;

    public final String code;

    Color(String code) {
      this.code = code;
    }

    @Override
    public String toString() {
      return code;
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
