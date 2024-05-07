package top.btswork.furryblack.core.logging.enums;

public enum LoggerXColor {

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

  LoggerXColor(String code) {
    this.code = code;
  }

  @Override
  public String toString() {
    return code;
  }
}