package studio.blacktech.furryblackplus.core.logging.adapter;

import net.mamoe.mirai.utils.MiraiLogger;
import studio.blacktech.furryblackplus.core.logging.LoggerX;
import studio.blacktech.furryblackplus.core.logging.LoggerXFactory;

public class MiraiLoggerX implements MiraiLogger {

  private final LoggerX logger;

  public MiraiLoggerX(Class<?> clazz, String customNane) {
    if (customNane == null) {
      logger = LoggerXFactory.getLogger(clazz);
    } else {
      logger = LoggerXFactory.getLogger(customNane);
    }
  }

  @Override
  public String getIdentity() {
    return logger.getName();
  }

  //= ==========================================================================

  @Override
  public boolean isEnabled() {
    return true;
  }

  //= ==========================================================================

  @Override
  public boolean isDebugEnabled() {
    return logger.isDebugEnabled();
  }

  @Override
  public boolean isErrorEnabled() {
    return logger.isErrorEnabled();
  }

  @Override
  public boolean isWarningEnabled() {
    return logger.isWarnEnabled();
  }

  @Override
  public boolean isInfoEnabled() {
    return logger.isInfoEnabled();
  }

  @Override
  public boolean isVerboseEnabled() {
    return logger.isDebugEnabled();
  }

  //= ==========================================================================

  @Override
  public void error(String s) {
    logger.error(s);
  }

  @Override
  public void error(Throwable e) {
    logger.error(null, e);
  }

  @Override
  public void error(String s, Throwable throwable) {
    logger.error(s, throwable);
  }

  @Override
  public void warning(String s) {
    logger.warn(s);
  }

  @Override
  public void warning(Throwable e) {
    logger.warn(null, e);
  }

  @Override
  public void warning(String s, Throwable throwable) {
    logger.warn(s, throwable);
  }

  @Override
  public void info(String s) {
    logger.info(s);
  }

  @Override
  public void info(Throwable e) {
    logger.info(null, e);
  }

  @Override
  public void info(String s, Throwable throwable) {
    logger.info(s, throwable);
  }

  @Override
  public void verbose(String s) {
    logger.debug(s);
  }

  @Override
  public void verbose(Throwable e) {
    logger.debug(null, e);
  }

  @Override
  public void verbose(String s, Throwable throwable) {
    logger.debug(s);
  }

  @Override
  public void debug(String s) {
    logger.trace(s);
  }

  @Override
  public void debug(Throwable e) {
    logger.trace(null, e);
  }

  @Override
  public void debug(String s, Throwable throwable) {
    logger.trace(s, throwable);
  }
}
