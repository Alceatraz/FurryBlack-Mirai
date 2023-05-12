package studio.blacktech.furryblackplus.core.common.logger.slf4j;

import org.slf4j.Logger;
import org.slf4j.Marker;
import studio.blacktech.furryblackplus.core.common.logger.LoggerXFactory;
import studio.blacktech.furryblackplus.core.common.logger.base.LoggerX;

import java.util.Objects;

public class Slf4jLoggerX implements Logger {

  private final LoggerX logger;

  public Slf4jLoggerX(String name) {
    logger = LoggerXFactory.newLogger(name);
  }

  public Slf4jLoggerX(LoggerX logger) {
    this.logger = logger;
  }

  @Override public String getName() {
    return logger.getIdentity();
  }

  @Override public boolean isTraceEnabled() {
    return logger.isTraceEnabled();
  }

  @Override public void trace(String message) {
    logger.trace(message);
  }

  @Override public void trace(String message, Object object) {
    logger.trace(message.replace("{}", Objects.toString(object)));
  }

  @Override public void trace(String message, Object object, Object object1) {
    logger.trace(message.replace("{}", Objects.toString(object)).replace("{}", Objects.toString(object1)));
  }

  @Override public void trace(String message, Object... objects) {
    logger.trace(LoggerX.process(message, objects));
  }

  @Override public void trace(String message, Throwable throwable) {
    logger.trace(message, throwable);
  }

  @Override public boolean isTraceEnabled(Marker marker) {
    return logger.isTraceEnabled();
  }

  @Override public void trace(Marker marker, String message) {
    logger.trace(message);
  }

  @Override public void trace(Marker marker, String message, Object object) {
    logger.trace(message.replace("{}", Objects.toString(object)));
  }

  @Override public void trace(Marker marker, String message, Object object, Object object1) {
    logger.trace(message.replace("{}", Objects.toString(object)).replace("{}", Objects.toString(object1)));
  }

  @Override public void trace(Marker marker, String message, Object... objects) {
    logger.trace(LoggerX.process(message, objects));
  }

  @Override public void trace(Marker marker, String message, Throwable throwable) {
    logger.trace(message, throwable);
  }

  @Override public boolean isDebugEnabled() {
    return logger.isDebugEnabled();
  }

  @Override public void debug(String message) {
    logger.debug(message);
  }

  @Override public void debug(String message, Object object) {
    logger.debug(message.replace("{}", Objects.toString(object)));
  }

  @Override public void debug(String message, Object object, Object object1) {
    logger.debug(message.replace("{}", Objects.toString(object)).replace("{}", Objects.toString(object1)));
  }

  @Override public void debug(String message, Object... objects) {
    logger.debug(LoggerX.process(message, objects));
  }

  @Override public void debug(String message, Throwable throwable) {
    logger.trace(message, throwable);
  }

  @Override public boolean isDebugEnabled(Marker marker) {
    return logger.isDebugEnabled();
  }

  @Override public void debug(Marker marker, String message) {
    logger.debug(message);
  }

  @Override public void debug(Marker marker, String message, Object object) {
    logger.debug(message.replace("{}", Objects.toString(object)));
  }

  @Override public void debug(Marker marker, String message, Object object, Object object1) {
    logger.debug(message.replace("{}", Objects.toString(object)).replace("{}", Objects.toString(object1)));
  }

  @Override public void debug(Marker marker, String message, Object... objects) {
    logger.debug(LoggerX.process(message, objects));
  }

  @Override public void debug(Marker marker, String message, Throwable throwable) {
    logger.debug(message, throwable);
  }

  @Override public boolean isInfoEnabled() {
    return logger.isInfoEnabled();
  }

  @Override public void info(String message) {
    logger.info(message);
  }

  @Override public void info(String message, Object object) {
    logger.info(message.replace("{}", Objects.toString(object)));
  }

  @Override public void info(String message, Object object, Object object1) {
    logger.info(message.replace("{}", Objects.toString(object)).replace("{}", Objects.toString(object1)));
  }

  @Override public void info(String message, Object... objects) {
    logger.info(LoggerX.process(message, objects));
  }

  @Override public void info(String message, Throwable throwable) {
    logger.info(message, throwable);
  }

  @Override public boolean isInfoEnabled(Marker marker) {
    return logger.isInfoEnabled();
  }

  @Override public void info(Marker marker, String message) {
    logger.info(message);
  }

  @Override public void info(Marker marker, String message, Object object) {
    logger.info(message.replace("{}", Objects.toString(object)));
  }

  @Override public void info(Marker marker, String message, Object object, Object object1) {
    logger.info(message.replace("{}", Objects.toString(object)).replace("{}", Objects.toString(object1)));
  }

  @Override public void info(Marker marker, String message, Object... objects) {
    logger.info(LoggerX.process(message, objects));
  }

  @Override public void info(Marker marker, String message, Throwable throwable) {
    logger.info(message, throwable);
  }

  @Override public boolean isWarnEnabled() {
    return logger.isWarnEnabled();
  }

  @Override public void warn(String message) {
    logger.warning(message);
  }

  @Override public void warn(String message, Object object) {
    logger.warning(message.replace("{}", Objects.toString(object)));
  }

  @Override public void warn(String message, Object... objects) {
    logger.warning(LoggerX.process(message, objects));
  }

  @Override public void warn(String message, Object object, Object object1) {
    logger.warning(message.replace("{}", Objects.toString(object)).replace("{}", Objects.toString(object1)));
  }

  @Override public void warn(String message, Throwable throwable) {
    logger.warning(message, throwable);
  }

  @Override public boolean isWarnEnabled(Marker marker) {
    return logger.isWarnEnabled();
  }

  @Override public void warn(Marker marker, String message) {
    logger.warning(message);
  }

  @Override public void warn(Marker marker, String message, Object object) {
    logger.warning(message.replace("{}", Objects.toString(object)));
  }

  @Override public void warn(Marker marker, String message, Object object, Object object1) {
    logger.warning(message.replace("{}", Objects.toString(object)).replace("{}", Objects.toString(object1)));
  }

  @Override public void warn(Marker marker, String message, Object... objects) {
    logger.warning(LoggerX.process(message, objects));
  }

  @Override public void warn(Marker marker, String message, Throwable throwable) {
    logger.warning(message, throwable);
  }

  @Override public boolean isErrorEnabled() {
    return logger.isErrorEnabled();
  }

  @Override public void error(String message) {
    logger.error(message);
  }

  @Override public void error(String message, Object object) {
    logger.error(message.replace("{}", Objects.toString(object)));
  }

  @Override public void error(String message, Object object, Object object1) {
    logger.error(message.replace("{}", Objects.toString(object)).replace("{}", Objects.toString(object)));
  }

  @Override public void error(String message, Object... objects) {
    logger.error(LoggerX.process(message, objects));
  }

  @Override public void error(String message, Throwable throwable) {
    logger.error(message, throwable);
  }

  @Override public boolean isErrorEnabled(Marker marker) {
    return logger.isErrorEnabled();
  }

  @Override public void error(Marker marker, String message) {
    logger.error(message);
  }

  @Override public void error(Marker marker, String message, Object object) {
    logger.error(message.replace("{}", Objects.toString(object)));
  }

  @Override public void error(Marker marker, String message, Object object, Object object1) {
    logger.error(message.replace("{}", Objects.toString(object)).replace("{}", Objects.toString(object)));
  }

  @Override public void error(Marker marker, String message, Object... objects) {
    logger.error(LoggerX.process(message, objects));
  }

  @Override public void error(Marker marker, String message, Throwable throwable) {
    logger.error(message, throwable);
  }
}

