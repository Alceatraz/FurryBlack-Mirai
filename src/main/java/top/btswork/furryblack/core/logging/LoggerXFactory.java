package top.btswork.furryblack.core.logging;

import top.btswork.furryblack.core.logging.annotation.LoggerXConfig;
import top.btswork.furryblack.core.logging.backend.WritterLoggerX;
import top.btswork.furryblack.core.logging.enums.LoggerXLevel;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.Map;

public class LoggerXFactory {

  private static Class<? extends LoggerX> DEFAULT_LOGGER = WritterLoggerX.class;

  //= ==================================================================================================================
  //= 配置系统

  public static LoggerXLevel getLevel() {
    return LoggerX.getLevel();
  }

  public static void setLevel(LoggerXLevel level) {
    LoggerX.setLevel(level);
  }

  public static String getDefault() {
    return DEFAULT_LOGGER.getSimpleName();
  }

  public static void setDefault(Class<? extends LoggerX> provider) {
    DEFAULT_LOGGER = provider;
  }

  public static void flushPrefixCache() {
    LoggerX.flushPrefixCache();
  }

  public static Map<String, LoggerXLevel> listPrefix() {
    return LoggerX.listPrefix();
  }

  public static LoggerXLevel testPrefix(String node) {
    return LoggerX.testPrefix(node);
  }

  public static void setPrefix(String node, LoggerXLevel level) {
    LoggerX.setPrefix(node, level);
  }

  public static void delPrefix(String node) {
    LoggerX.delPrefix(node);
  }

  //= ==================================================================================================================
  //= 功能开关

  public static boolean isEnablePrefix() {
    return LoggerX.isEnablePrefix();
  }

  public static boolean isEnableFullName() {
    return LoggerX.isEnableFullName();
  }

  public static void setEnablePrefix(boolean value) {
    LoggerX.setEnablePrefix(value);
  }

  public static void setEnableFullName(boolean value) {
    LoggerX.setEnableFullName(value);
  }

  //= ==================================================================================================================
  //= 前缀系统

  public static void injectPrefix(String prefix, LoggerXLevel level) {
    LoggerX.setPrefix(prefix, level);
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
