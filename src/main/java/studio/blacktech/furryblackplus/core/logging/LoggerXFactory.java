package studio.blacktech.furryblackplus.core.logging;

import studio.blacktech.furryblackplus.core.logging.annotation.LoggerXConfig;
import studio.blacktech.furryblackplus.core.logging.enums.LoggerXLevel;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.List;

public class LoggerXFactory {

  private static Class<? extends LoggerX> DEFAULT_LOGGER = DefaultLoggerX.class;

  //= ==================================================================================================================
  //= 配置系统

  public static void setDefault(Class<? extends LoggerX> provider) {
    DEFAULT_LOGGER = provider;
  }

  public static String getDefault() {
    return DEFAULT_LOGGER.getSimpleName();
  }

  public static LoggerXLevel getLevel() {
    return LoggerX.getLevel();
  }

  public static void setLevel(LoggerXLevel level) {
    LoggerX.setLevel(level);
  }

  //= ==================================================================================================================
  //= 前缀系统

  public static void injectPrefix(String prefix,LoggerXLevel level) {
    LoggerX.injectPrefix(prefix,level);
  }

  //= ==================================================================================================================
  //=  初始化

  public static boolean needLoggerFile() {
    return DEFAULT_LOGGER.getAnnotation(LoggerXConfig.class).needLoggerFile();
  }

  public static void initLoggerFile(Path path) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    DEFAULT_LOGGER
      .getDeclaredMethod("init", Path.class)
      .invoke(null, path);
  }

  //= ==================================================================================================================
  //= 实例化

  public static LoggerX getLogger(Class<?> clazz) {
    try {
      return DEFAULT_LOGGER.getConstructor(Class.class).newInstance(clazz);
    } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException exception) {
      throw new RuntimeException(exception);
    }
  }

  public static LoggerX getLogger(String simpleName) {
    try {
      return DEFAULT_LOGGER.getConstructor(String.class).newInstance(simpleName);
    } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException exception) {
      throw new RuntimeException(exception);
    }
  }

  //= ==================================================================================================================

}
