package studio.blacktech.furryblackplus.core.logging;

import studio.blacktech.furryblackplus.core.common.annotation.Comment;
import studio.blacktech.furryblackplus.core.logging.enums.LoggerXColor;
import studio.blacktech.furryblackplus.core.logging.enums.LoggerXLevel;

import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import static studio.blacktech.furryblackplus.FurryBlack.LINE;
import static studio.blacktech.furryblackplus.core.common.enhance.StringEnhance.extractStackTrace;
import static studio.blacktech.furryblackplus.core.logging.enums.LoggerXLevel.DEBUG;
import static studio.blacktech.furryblackplus.core.logging.enums.LoggerXLevel.ERROR;
import static studio.blacktech.furryblackplus.core.logging.enums.LoggerXLevel.INFO;
import static studio.blacktech.furryblackplus.core.logging.enums.LoggerXLevel.TRACE;
import static studio.blacktech.furryblackplus.core.logging.enums.LoggerXLevel.WARN;

@SuppressWarnings("unused")

@Comment(
  value = "基础日志工具类 请使用工厂方法创建实例",
  usage = {
    "TRACE = TRACE + VERBOSE(Mirai)",
    "DEBUG = DEBUG",
    "INFO  = HINT + SEEK + INFO",
    "WARN  = WARN",
    "ERROR = ERROR + FATAL",
    "CLOSE = N/A",
  },
  attention = {
    "Impl转发可以强制要求实现 OperationNotSupportedException 只有在运行时才能发现问题 不调用永远不会发现",
    "SLF4j使用isEnable控制是否打印对应日志,LoggerX实现了一个简易的按包路径设置不同级别的机制",
    "为了节省性能,FurryBlack和Mirai的日志只受DEFAULT_LEVEL控制,忽略包路径设置",
  }
)
public abstract class LoggerX {

  //= ==================================================================================================================
  //=
  //= 框架部分
  //=
  //= ==================================================================================================================

  //= ==========================================================================
  //= 日志颜色

  public static final LoggerXColor COLOR_FATAL = LoggerXColor.BOLD_BRIGHT_RED;
  public static final LoggerXColor COLOR_ERROR = LoggerXColor.BRIGHT_RED;
  public static final LoggerXColor COLOR_WARN = LoggerXColor.BRIGHT_YELLOW;
  public static final LoggerXColor COLOR_HINT = LoggerXColor.BRIGHT_CYAN;
  public static final LoggerXColor COLOR_SEEK = LoggerXColor.BRIGHT_GREEN;
  public static final LoggerXColor COLOR_INFO = LoggerXColor.WHITE;
  public static final LoggerXColor COLOR_DEBUG = LoggerXColor.BRIGHT_BLACK;
  public static final LoggerXColor COLOR_TRACE = LoggerXColor.BRIGHT_BLACK;

  //= ==========================================================================
  //= 日志等级

  private static LoggerXLevel DEFAULT_LEVEL = INFO;

  protected static LoggerXLevel getLevel() {
    return DEFAULT_LEVEL;
  }

  protected static void setLevel(LoggerXLevel level) {
    DEFAULT_LEVEL = Objects.requireNonNull(level, "Can't set logging level to null !");
  }

  //= ==========================================================================
  //= 分组等级

  private static final Node PREFIX = Node.root();

  private static boolean enablePrefix = false;

  protected static void enablePrefix() {
    enablePrefix = true;
  }

  protected static void disablePrefix() {
    enablePrefix = false;
  }

  protected static void loadPrefix(Map<String, LoggerXLevel> prefixes) {
    prefixes.forEach(PREFIX::set);
  }

  private static class Node {

    private final Map<String, Node> nodes = new TreeMap<>();

    private LoggerXLevel level;

    public static Node root() {
      return new Node();
    }

    public LoggerXLevel getLevel() {
      return level == null ? DEFAULT_LEVEL : level;
    }

    private Node add(String[] paths) {
      Node current = this;
      for (String path : paths) {
        current = current.nodes.computeIfAbsent(path, i -> new Node());
      }
      return current;
    }

    private LoggerXLevel get(String[] paths) {
      Node current = this;
      for (String path : paths) {
        Node next = current.nodes.get(path);
        if (next == null) return current.getLevel();
        current = next;
      }
      return current.getLevel();
    }

    public void set(String path, LoggerXLevel level) {
      add(path.split("\\.")).level = level;
    }

    public LoggerXLevel getLevel(String path) {
      return get(path.split("\\."));
    }

  }

  //= ==================================================================================================================
  //=
  //= ==================================================================================================================

  protected static String inject(String pattern, Object... objects) {
    for (Object object : objects) {
      if (object == null) {
        pattern = pattern.replace("{}", "null");
      } else if (object instanceof Throwable throwable) {
        String message = throwable.getMessage();
        pattern = pattern.replace("{}", throwable.getClass().getName() + ":" + message);
      } else {
        try {
          String message = Objects.toString(object);
          pattern = pattern.replace("{}", message);
        } catch (Exception exception) {
          pattern = pattern.replace("{}", "<<LoggerX Exception>>");
          pattern = pattern + LINE + extractStackTrace(exception);
        }
      }
    }
    return pattern;
  }

  //= ==================================================================================================================
  //=
  //= 对象部分
  //=
  //= ==================================================================================================================

  protected final Class<?> clazz;
  protected final String fullName;
  protected final String simpleName;

  protected LoggerX(Class<?> clazz) {
    this.clazz = clazz;
    fullName = clazz.getName();
    simpleName = clazz.getSimpleName();
  }

  protected LoggerX(String simpleName) {
    clazz = null;
    fullName = null;
    this.simpleName = simpleName;
  }

  public Class<?> getClazz() {
    return clazz;
  }

  public String getFullName() {
    return fullName;
  }

  public String getSimpleName() {
    return simpleName;
  }

  public String getSafeName() {
    return clazz == null ? simpleName : fullName;
  }

  public final boolean isErrorEnabled() {
    if (enablePrefix) {
      return PREFIX.getLevel(fullName).isEnable(DEFAULT_LEVEL);
    } else {
      return ERROR.isEnable(DEFAULT_LEVEL);
    }
  }

  public final boolean isWarnEnabled() {
    if (enablePrefix) {
      return PREFIX.getLevel(fullName).isEnable(DEFAULT_LEVEL);
    } else {
      return WARN.isEnable(DEFAULT_LEVEL);
    }
  }

  public final boolean isInfoEnabled() {
    if (enablePrefix) {
      return PREFIX.getLevel(fullName).isEnable(DEFAULT_LEVEL);
    } else {
      return INFO.isEnable(DEFAULT_LEVEL);
    }
  }

  public final boolean isDebugEnabled() {
    if (enablePrefix) {
      return PREFIX.getLevel(fullName).isEnable(DEFAULT_LEVEL);
    } else {
      return DEBUG.isEnable(DEFAULT_LEVEL);
    }
  }

  public final boolean isTraceEnabled() {
    if (enablePrefix) {
      return PREFIX.getLevel(fullName).isEnable(DEFAULT_LEVEL);
    } else {
      return TRACE.isEnable(DEFAULT_LEVEL);
    }
  }

  //= ==========================================================================
  //= 转发

  public final void fatal(String message) {
    fatalImpl(message);
  }

  public final void fatal(String message, Throwable throwable) {
    fatalImpl(message, throwable);
  }

  public final void fatal(String messagePattern, Object... objects) {
    fatalImpl(messagePattern, objects);
  }

  public final void error(String message) {
    errorImpl(message);
  }

  public final void error(String message, Throwable throwable) {
    errorImpl(message, throwable);
  }

  public final void error(String messagePattern, Object... objects) {
    errorImpl(messagePattern, objects);
  }

  public final void warn(String message) {
    warnImpl(message);
  }

  public final void warn(String message, Throwable throwable) {
    warnImpl(message, throwable);
  }

  public final void warn(String messagePattern, Object... objects) {
    warnImpl(messagePattern, objects);
  }

  public final void hint(String message) {
    hintImpl(message);
  }

  public final void hint(String message, Throwable throwable) {
    hintImpl(message, throwable);
  }

  public final void hint(String messagePattern, Object... objects) {
    hintImpl(messagePattern, objects);
  }

  public final void seek(String message) {
    seekImpl(message);
  }

  public final void seek(String message, Throwable throwable) {
    seekImpl(message, throwable);
  }

  public final void seek(String messagePattern, Object... objects) {
    seekImpl(messagePattern, objects);
  }

  public final void info(String message) {
    infoImpl(message);
  }

  public final void info(String message, Throwable throwable) {
    infoImpl(message, throwable);
  }

  public final void info(String messagePattern, Object... objects) {
    infoImpl(messagePattern, objects);
  }

  public final void debug(String message) {
    debugImpl(message);
  }

  public final void debug(String message, Throwable throwable) {
    debugImpl(message, throwable);
  }

  public final void debug(String messagePattern, Object... objects) {
    debugImpl(messagePattern, objects);
  }

  public final void trace(String message) {
    traceImpl(message);
  }

  public final void trace(String message, Throwable throwable) {
    traceImpl(message, throwable);
  }

  public final void trace(String messagePattern, Object... objects) {
    traceImpl(messagePattern, objects);
  }

  //= ==========================================================================
  //= 实现

  protected abstract void fatalImpl(String message);

  protected abstract void fatalImpl(String message, Throwable throwable);

  protected abstract void fatalImpl(String messagePattern, Object... objects);

  protected abstract void errorImpl(String message);

  protected abstract void errorImpl(String message, Throwable throwable);

  protected abstract void errorImpl(String messagePattern, Object... objects);

  protected abstract void warnImpl(String message);

  protected abstract void warnImpl(String message, Throwable throwable);

  protected abstract void warnImpl(String messagePattern, Object... objects);

  protected abstract void hintImpl(String message);

  protected abstract void hintImpl(String message, Throwable throwable);

  protected abstract void hintImpl(String messagePattern, Object... objects);

  protected abstract void infoImpl(String message);

  protected abstract void infoImpl(String message, Throwable throwable);

  protected abstract void infoImpl(String messagePattern, Object... objects);

  protected abstract void seekImpl(String message);

  protected abstract void seekImpl(String message, Throwable throwable);

  protected abstract void seekImpl(String messagePattern, Object... objects);

  protected abstract void debugImpl(String message);

  protected abstract void debugImpl(String message, Throwable throwable);

  protected abstract void debugImpl(String messagePattern, Object... objects);

  protected abstract void traceImpl(String message);

  protected abstract void traceImpl(String message, Throwable throwable);

  protected abstract void traceImpl(String messagePattern, Object... objects);

}
