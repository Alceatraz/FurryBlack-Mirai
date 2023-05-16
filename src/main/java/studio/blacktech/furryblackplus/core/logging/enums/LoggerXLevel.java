package studio.blacktech.furryblackplus.core.logging.enums;

public enum LoggerXLevel {

  CLOSE(0),
  ERROR(1),
  WARN(2),
  INFO(3),
  DEBUG(4),
  TRACE(5),

  ;

  public final int level;

  LoggerXLevel(int level) {
    this.level = level;
  }

  public boolean isEnable(LoggerXLevel level) {
    return this.level <= level.level;
  }

  public static LoggerXLevel of(String name) {
    return switch (name) {
      case "close", "CLOSE" -> CLOSE;
      case "error", "ERROR" -> ERROR;
      case "warn", "WARN" -> WARN;
      case "info", "INFO" -> INFO;
      case "debug", "DEBUG" -> DEBUG;
      case "trace", "TRACE" -> TRACE;
      default -> null;
    };
  }

}
