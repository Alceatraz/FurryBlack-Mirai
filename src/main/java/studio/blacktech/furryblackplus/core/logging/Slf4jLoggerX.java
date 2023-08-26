package studio.blacktech.furryblackplus.core.logging;

import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.event.Level;
import org.slf4j.spi.LoggingEventBuilder;

import java.util.Objects;

public class Slf4jLoggerX implements Logger {

  private final LoggerX logger;

  public Slf4jLoggerX(String name) {
    logger = LoggerXFactory.getLogger(name);
  }

  @Override
  public String getName() {
    return logger.getName();
  }

  //= ==========================================================================
  //= ERROR

  @Override
  public boolean isErrorEnabled() {
    return logger.isErrorEnabled(logger.fullName);
  }

  @Override
  public boolean isWarnEnabled() {
    return logger.isWarnEnabled(logger.fullName);
  }

  @Override
  public boolean isInfoEnabled() {
    return logger.isInfoEnabled(logger.fullName);
  }

  @Override
  public boolean isDebugEnabled() {
    return logger.isDebugEnabled(logger.fullName);
  }

  @Override
  public boolean isTraceEnabled() {
    return logger.isTraceEnabled(logger.fullName);
  }

  @Override
  public boolean isErrorEnabled(Marker marker) {
    return logger.isErrorEnabled(logger.fullName);
  }

  @Override
  public boolean isWarnEnabled(Marker marker) {
    return logger.isWarnEnabled(logger.fullName);
  }

  @Override
  public boolean isInfoEnabled(Marker marker) {
    return logger.isInfoEnabled(logger.fullName);
  }

  @Override
  public boolean isDebugEnabled(Marker marker) {
    return logger.isDebugEnabled(logger.fullName);
  }

  @Override
  public boolean isTraceEnabled(Marker marker) {
    return logger.isTraceEnabled(logger.fullName);
  }

  //= ==========================================================================
  //= 2.0

  @Override
  public boolean isEnabledForLevel(Level level) {
    return Logger.super.isEnabledForLevel(level);
  }

  @Override
  public LoggingEventBuilder atLevel(Level level) {
    return Logger.super.atLevel(level);
  }

  @Override
  public LoggingEventBuilder atError() {
    return Logger.super.atError();
  }

  @Override
  public LoggingEventBuilder atWarn() {
    return Logger.super.atWarn();
  }

  @Override
  public LoggingEventBuilder atInfo() {
    return Logger.super.atInfo();
  }

  @Override
  public LoggingEventBuilder atDebug() {
    return Logger.super.atDebug();
  }

  @Override
  public LoggingEventBuilder atTrace() {
    return Logger.super.atTrace();
  }

  @Override
  public LoggingEventBuilder makeLoggingEventBuilder(Level level) {
    return Logger.super.makeLoggingEventBuilder(level);
  }

  //= ==========================================================================
  //= 1.0

  @Override
  public void error(String message) {
    logger.errorImpl(message);
  }

  @Override
  public void error(String message, Throwable throwable) {
    logger.errorImpl(message, throwable);
  }

  @Override
  public void error(String messagePattern, Object... objects) {
    logger.errorImpl(messagePattern, objects);
  }

  @Override
  public void error(String messagePattern, Object object1) {
    logger.errorImpl(messagePattern, object1);
  }

  @Override
  public void error(String messagePattern, Object object1, Object object2) {
    logger.errorImpl(messagePattern, object1, object2);
  }

  @Override
  public void error(Marker marker, String message) {
    if (marker == null) {
      logger.errorImpl(message);
    } else {
      logger.errorImpl("[" + Objects.requireNonNull(marker) + "]" + message);
    }
  }

  @Override
  public void error(Marker marker, String message, Throwable throwable) {
    if (marker == null) {
      logger.errorImpl(message, throwable);
    } else {
      logger.errorImpl("[" + Objects.requireNonNull(marker) + "]" + message, throwable);
    }
  }

  @Override
  public void error(Marker marker, String messagePattern, Object... objects) {
    if (marker == null) {
      logger.errorImpl(messagePattern, objects);
    } else {
      logger.errorImpl("[" + Objects.requireNonNull(marker) + "]" + messagePattern, objects);
    }
  }

  @Override
  public void error(Marker marker, String messagePattern, Object object1) {
    if (marker == null) {
      logger.errorImpl(messagePattern, object1);
    } else {
      logger.errorImpl("[" + Objects.requireNonNull(marker) + "]" + messagePattern, object1);
    }
  }

  @Override
  public void error(Marker marker, String messagePattern, Object object1, Object object2) {
    if (marker == null) {
      logger.errorImpl(messagePattern, object1, object2);
    } else {
      logger.errorImpl("[" + Objects.requireNonNull(marker) + "]" + messagePattern, object1, object2);
    }
  }

  @Override
  public void warn(String message) {
    logger.warnImpl(message);
  }

  @Override
  public void warn(String message, Throwable throwable) {
    logger.warnImpl(message, throwable);
  }

  @Override
  public void warn(String messagePattern, Object... objects) {
    logger.warnImpl(messagePattern, objects);
  }

  @Override
  public void warn(String messagePattern, Object object1) {
    logger.warnImpl(messagePattern, object1);
  }

  @Override
  public void warn(String messagePattern, Object object1, Object object2) {
    logger.warnImpl(messagePattern, object1, object2);
  }

  @Override
  public void warn(Marker marker, String message) {
    if (marker == null) {
      logger.warnImpl(message);
    } else {
      logger.warnImpl("[" + Objects.requireNonNull(marker) + "]" + message);
    }
  }

  @Override
  public void warn(Marker marker, String message, Throwable throwable) {
    if (marker == null) {
      logger.warnImpl(message, throwable);
    } else {
      logger.warnImpl("[" + Objects.requireNonNull(marker) + "]" + message, throwable);
    }
  }

  @Override
  public void warn(Marker marker, String messagePattern, Object... objects) {
    if (marker == null) {
      logger.warnImpl(messagePattern, objects);
    } else {
      logger.warnImpl("[" + Objects.requireNonNull(marker) + "]" + messagePattern, objects);
    }
  }

  @Override
  public void warn(Marker marker, String messagePattern, Object object1) {
    if (marker == null) {
      logger.warnImpl(messagePattern, object1);
    } else {
      logger.warnImpl("[" + Objects.requireNonNull(marker) + "]" + messagePattern, object1);
    }
  }

  @Override
  public void warn(Marker marker, String messagePattern, Object object1, Object object2) {
    if (marker == null) {
      logger.warnImpl(messagePattern, object1, object2);
    } else {
      logger.warnImpl("[" + Objects.requireNonNull(marker) + "]" + messagePattern, object1, object2);
    }
  }

  @Override
  public void info(String message) {
    logger.infoImpl(message);
  }

  @Override
  public void info(String message, Throwable throwable) {
    logger.infoImpl(message, throwable);
  }

  @Override
  public void info(String messagePattern, Object... objects) {
    logger.infoImpl(messagePattern, objects);
  }

  @Override
  public void info(String messagePattern, Object object1) {
    logger.infoImpl(messagePattern, object1);
  }

  @Override
  public void info(String messagePattern, Object object1, Object object2) {
    logger.infoImpl(messagePattern, object1, object2);
  }

  @Override
  public void info(Marker marker, String message) {
    if (marker == null) {
      logger.infoImpl(message);
    } else {
      logger.infoImpl("[" + Objects.requireNonNull(marker) + "]" + message);
    }
  }

  @Override
  public void info(Marker marker, String message, Throwable throwable) {
    if (marker == null) {
      logger.infoImpl(message, throwable);
    } else {
      logger.infoImpl("[" + Objects.requireNonNull(marker) + "]" + message, throwable);
    }
  }

  @Override
  public void info(Marker marker, String messagePattern, Object... objects) {
    if (marker == null) {
      logger.infoImpl(messagePattern, objects);
    } else {
      logger.infoImpl("[" + Objects.requireNonNull(marker) + "]" + messagePattern, objects);
    }
  }

  @Override
  public void info(Marker marker, String messagePattern, Object object1) {
    if (marker == null) {
      logger.infoImpl(messagePattern, object1);
    } else {
      logger.infoImpl("[" + Objects.requireNonNull(marker) + "]" + messagePattern, object1);
    }
  }

  @Override
  public void info(Marker marker, String messagePattern, Object object1, Object object2) {
    if (marker == null) {
      logger.infoImpl(messagePattern, object1, object2);
    } else {
      logger.infoImpl("[" + Objects.requireNonNull(marker) + "]" + messagePattern, object1, object2);
    }
  }

  @Override
  public void debug(String message) {
    logger.debugImpl(message);
  }

  @Override
  public void debug(String message, Throwable throwable) {
    logger.debugImpl(message, throwable);
  }

  @Override
  public void debug(String messagePattern, Object... objects) {
    logger.debugImpl(messagePattern, objects);
  }

  @Override
  public void debug(String messagePattern, Object object1) {
    logger.debugImpl(messagePattern, object1);
  }

  @Override
  public void debug(String messagePattern, Object object1, Object object2) {
    logger.debugImpl(messagePattern, object1, object2);
  }

  @Override
  public void debug(Marker marker, String message) {
    if (marker == null) {
      logger.debugImpl(message);
    } else {
      logger.debugImpl("[" + Objects.requireNonNull(marker) + "]" + message);
    }
  }

  @Override
  public void debug(Marker marker, String message, Throwable throwable) {
    if (marker == null) {
      logger.debugImpl(message, throwable);
    } else {
      logger.debugImpl("[" + Objects.requireNonNull(marker) + "]" + message, throwable);
    }
  }

  @Override
  public void debug(Marker marker, String messagePattern, Object... objects) {
    if (marker == null) {
      logger.debugImpl(messagePattern, objects);
    } else {
      logger.debugImpl("[" + Objects.requireNonNull(marker) + "]" + messagePattern, objects);
    }
  }

  @Override
  public void debug(Marker marker, String messagePattern, Object object1) {
    if (marker == null) {
      logger.debugImpl(messagePattern, object1);
    } else {
      logger.debugImpl("[" + Objects.requireNonNull(marker) + "]" + messagePattern, object1);
    }
  }

  @Override
  public void debug(Marker marker, String messagePattern, Object object1, Object object2) {
    if (marker == null) {
      logger.debugImpl(messagePattern, object1, object2);
    } else {
      logger.debugImpl("[" + Objects.requireNonNull(marker) + "]" + messagePattern, object1, object2);
    }
  }

  @Override
  public void trace(String message) {
    logger.traceImpl(message);
  }

  @Override
  public void trace(String message, Throwable throwable) {
    logger.traceImpl(message, throwable);
  }

  @Override
  public void trace(String messagePattern, Object... objects) {
    logger.traceImpl(messagePattern, objects);
  }

  @Override
  public void trace(String messagePattern, Object object1) {
    logger.traceImpl(messagePattern, object1);
  }

  @Override
  public void trace(String messagePattern, Object object1, Object object2) {
    logger.traceImpl(messagePattern, object1, object2);
  }

  @Override
  public void trace(Marker marker, String message) {
    if (marker == null) {
      logger.traceImpl(message);
    } else {
      logger.traceImpl("[" + Objects.requireNonNull(marker) + "]" + message);
    }
  }

  @Override
  public void trace(Marker marker, String message, Throwable throwable) {
    if (marker == null) {
      logger.traceImpl(message, throwable);
    } else {
      logger.traceImpl("[" + Objects.requireNonNull(marker) + "]" + message, throwable);
    }
  }

  @Override
  public void trace(Marker marker, String messagePattern, Object... objects) {
    if (marker == null) {
      logger.traceImpl(messagePattern, objects);
    } else {
      logger.traceImpl("[" + Objects.requireNonNull(marker) + "]" + messagePattern, objects);
    }
  }

  @Override
  public void trace(Marker marker, String messagePattern, Object object1) {
    if (marker == null) {
      logger.traceImpl(messagePattern, object1);
    } else {
      logger.traceImpl("[" + Objects.requireNonNull(marker) + "]" + messagePattern, object1);
    }
  }

  @Override
  public void trace(Marker marker, String messagePattern, Object object1, Object object2) {
    if (marker == null) {
      logger.traceImpl(messagePattern, object1, object2);
    } else {
      logger.traceImpl("[" + Objects.requireNonNull(marker) + "]" + messagePattern, object1, object2);
    }
  }

}
