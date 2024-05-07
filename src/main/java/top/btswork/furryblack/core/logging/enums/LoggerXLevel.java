package top.btswork.furryblack.core.logging.enums;

import java.util.Locale;

public enum LoggerXLevel {

  EVERYTHING(0),
  TRACE(10),
  DEBUG(20),
  INFO(30),
  WARN(40),
  ERROR(50),
  CLOSE(Integer.MAX_VALUE),
  ;

  public final int level;

  LoggerXLevel(int level) {
    this.level = level;
  }

  public boolean isEnable(LoggerXLevel targetLevel) {
    return level >= targetLevel.level;
  }

  public static LoggerXLevel of(String name) {
    return switch (name.toUpperCase(Locale.ROOT)) {
      case "EVERYTHING" -> EVERYTHING;
      case "TRACE" -> TRACE;
      case "DEBUG" -> DEBUG;
      case "INFO" -> INFO;
      case "WARN" -> WARN;
      case "ERROR" -> ERROR;
      case "CLOSE" -> CLOSE;
      default -> null;
    };
  }

}
