package studio.blacktech.furryblackplus.core.logging;

import studio.blacktech.furryblackplus.core.logging.annotation.LoggerXConfig;
import studio.blacktech.furryblackplus.core.logging.enums.LoggerXLevel;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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

  public static void enablePrefix() {
    LoggerX.enablePrefix();
  }

  public static void injectPrefix(List<String> lines) {
    Map<String, LoggerXLevel> temp = new TreeMap<>();
    for (String line : lines) {
      String[] split = line.split("=");
      var k = split[0];
      var v = split[1];
      temp.put(k, LoggerXLevel.of(v));
    }
    LoggerX.loadPrefix(temp);
  }

  //= ==================================================================================================================
  //=  初始化

  public static boolean needLoggerFile() {
    return DEFAULT_LOGGER.getAnnotation(LoggerXConfig.class).needLoggerFile();
  }

  public static void initLoggerFile(File file) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    Method initLoggerFile = DEFAULT_LOGGER.getDeclaredMethod("initLoggerFile", File.class);
    initLoggerFile.invoke(null, file);
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
