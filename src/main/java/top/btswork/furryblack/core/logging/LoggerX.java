package top.btswork.furryblack.core.logging;

import top.btswork.furryblack.core.common.annotation.Comment;
import top.btswork.furryblack.core.logging.enums.LoggerXColor;
import top.btswork.furryblack.core.logging.enums.LoggerXLevel;

import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import static top.btswork.furryblack.core.logging.enums.LoggerXLevel.DEBUG;
import static top.btswork.furryblack.core.logging.enums.LoggerXLevel.ERROR;
import static top.btswork.furryblack.core.logging.enums.LoggerXLevel.INFO;
import static top.btswork.furryblack.core.logging.enums.LoggerXLevel.TRACE;
import static top.btswork.furryblack.core.logging.enums.LoggerXLevel.WARN;

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
  public static final LoggerXColor COLOR_SEEK = LoggerXColor.GREEN;
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
    DEFAULT_LEVEL = Objects.requireNonNull(level, "Can't addPrefix logging level to null!");
  }

  //= ==========================================================================
  //= 分组等级

  private static final Node PREFIX = Node.root();
  private static final Map<String, LoggerXLevel> CACHES = new ConcurrentHashMap<>();

  private static boolean enablePrefix = false;
  private static boolean enableFullName = false;

  //= ==========================================================================
  //= 功能开关

  protected static boolean isEnablePrefix() {
    return enablePrefix;
  }

  protected static boolean isEnableFullName() {
    return enableFullName;
  }

  protected static void setEnablePrefix(boolean enablePrefix) {
    LoggerX.enablePrefix = enablePrefix;
  }

  protected static void setEnableFullName(boolean enableFullName) {
    LoggerX.enableFullName = enableFullName;
  }

  protected static void flushPrefixCache() {
    CACHES.clear();
  }

  protected static Map<String, LoggerXLevel> listPrefix() {
    TreeMap<String, LoggerXLevel> temp = new TreeMap<>(String::compareTo);
    temp.putAll(PREFIX.listPrefix());
    return temp;
  }

  protected static Map<String, LoggerXLevel> listPrefixCache() {
    TreeMap<String, LoggerXLevel> temp = new TreeMap<>(String::compareTo);
    temp.putAll(CACHES);
    return temp;
  }

  protected static LoggerXLevel testPrefix(String path) {
    return PREFIX.getLevel(path);
  }

  protected static void setPrefix(String path, LoggerXLevel level) {
    PREFIX.addPrefix(path, level);
  }

  protected static void delPrefix(String path) {
    PREFIX.delLevel(path);
  }

  //= ==========================================================================
  //= 功能开关

  private static class Node {

    private final Map<String, Node> nodes = new TreeMap<>();

    private LoggerXLevel level;

    public static Node root() {
      return new Node();
    }

    public LoggerXLevel getLevel() {
      return level;
    }

    private Node add(String[] paths) {
      Node current = this;
      for (String path : paths) {
        current = current.nodes.computeIfAbsent(path, i -> new Node());
      }
      return current;
    }

    private void del(String[] paths) {
      Node current = this;
      for (String path : paths) {
        current = current.nodes.computeIfAbsent(path, i -> new Node());
      }
      current.level = null;
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

    public void delLevel(String path) {
      del(path.split("\\."));
    }

    public void addPrefix(String path, LoggerXLevel level) {
      add(path.split("\\.")).level = level;
    }

    public LoggerXLevel getLevel(String path) {
      LoggerXLevel level = CACHES.computeIfAbsent(path, i -> get(i.split("\\.")));
      return level == null ? DEFAULT_LEVEL : level;
    }

    public Map<String, LoggerXLevel> listPrefix() {
      Map<String, LoggerXLevel> map = new TreeMap<>();
      recursiveNodes("", map);
      return map;
    }

    private void recursiveNodes(String prefix, Map<String, LoggerXLevel> map) {
      if (nodes.isEmpty()) {
        if (prefix.isBlank()) {
          map.put(".", DEFAULT_LEVEL);
        } else {
          map.put(prefix, level);
        }
      } else {
        for (Map.Entry<String, Node> entry : nodes.entrySet()) {
          entry.getValue().recursiveNodes((prefix.isBlank() ? "" : prefix + ".") + entry.getKey(), map);
        }
      }
    }

  }

  //= ==========================================================================
  //= 级别开关

  public final boolean isErrorEnabled() {
    if (enablePrefix) {
      return ERROR.isEnable(PREFIX.getLevel(fullName));
    } else {
      return ERROR.isEnable(DEFAULT_LEVEL);
    }
  }

  public final boolean isWarnEnabled() {
    if (enablePrefix) {
      return WARN.isEnable(PREFIX.getLevel(fullName));
    } else {
      return WARN.isEnable(DEFAULT_LEVEL);
    }
  }

  public final boolean isInfoEnabled() {
    if (enablePrefix) {
      return INFO.isEnable(PREFIX.getLevel(fullName));
    } else {
      return INFO.isEnable(DEFAULT_LEVEL);
    }
  }

  public final boolean isDebugEnabled() {
    if (enablePrefix) {
      return DEBUG.isEnable(PREFIX.getLevel(fullName));
    } else {
      return DEBUG.isEnable(DEFAULT_LEVEL);
    }
  }

  public final boolean isTraceEnabled() {
    if (enablePrefix) {
      return TRACE.isEnable(PREFIX.getLevel(fullName));
    } else {
      return TRACE.isEnable(DEFAULT_LEVEL);
    }
  }

  //= ==========================================================================
  //= 外部级别

  public final boolean isErrorEnabled(String name) {
    if (enablePrefix) {
      return ERROR.isEnable(PREFIX.getLevel(name));
    } else {
      return ERROR.isEnable(DEFAULT_LEVEL);
    }
  }

  public final boolean isWarnEnabled(String name) {
    if (enablePrefix) {
      return WARN.isEnable(PREFIX.getLevel(name));
    } else {
      return WARN.isEnable(DEFAULT_LEVEL);
    }
  }

  public final boolean isInfoEnabled(String name) {
    if (enablePrefix) {
      return INFO.isEnable(PREFIX.getLevel(name));
    } else {
      return INFO.isEnable(DEFAULT_LEVEL);
    }
  }

  public final boolean isDebugEnabled(String name) {
    if (enablePrefix) {
      return DEBUG.isEnable(PREFIX.getLevel(name));
    } else {
      return DEBUG.isEnable(DEFAULT_LEVEL);
    }
  }

  public final boolean isTraceEnabled(String name) {
    if (enablePrefix) {
      return TRACE.isEnable(PREFIX.getLevel(name));
    } else {
      return TRACE.isEnable(DEFAULT_LEVEL);
    }
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

  protected LoggerX(String name) {
    clazz = null;
    fullName = name;
    String[] split = name.split("\\.");
    simpleName = split[split.length - 1];
  }

  public Class<?> getClazz() {
    return clazz;
  }

  public String getName() {
    return enableFullName ? fullName : simpleName;
  }

  public String getFullName() {
    return fullName;
  }

  public String getSimpleName() {
    return simpleName;
  }

  //= ==========================================================================
  //= 转发

  public final void fatal(String message) {
    fatalImpl(message);
  }

  public final void fatal(Throwable throwable) {
    fatalImpl(throwable);
  }

  public final void fatal(String message, Throwable throwable) {
    fatalImpl(message, throwable);
  }

  public final void fatal(String messagePattern, Object... objects) {
    fatalImpl(messagePattern, objects);
  }

  //

  public final void error(String message) {
    errorImpl(message);
  }

  public final void error(Throwable throwable) {
    errorImpl(throwable);
  }

  public final void error(String message, Throwable throwable) {
    errorImpl(message, throwable);
  }

  public final void error(String messagePattern, Object... objects) {
    errorImpl(messagePattern, objects);
  }

  //

  public final void warn(String message) {
    warnImpl(message);
  }

  public final void warn(Throwable throwable) {
    warnImpl(throwable);
  }

  public final void warn(String message, Throwable throwable) {
    warnImpl(message, throwable);
  }

  public final void warn(String messagePattern, Object... objects) {
    warnImpl(messagePattern, objects);
  }

  //

  public final void hint(String message) {
    hintImpl(message);
  }

  public final void hint(Throwable throwable) {
    hintImpl(throwable);
  }

  public final void hint(String message, Throwable throwable) {
    hintImpl(message, throwable);
  }

  public final void hint(String messagePattern, Object... objects) {
    hintImpl(messagePattern, objects);
  }

  //

  public final void seek(String message) {
    seekImpl(message);
  }

  public final void seek(Throwable throwable) {
    seekImpl(throwable);
  }

  public final void seek(String message, Throwable throwable) {
    seekImpl(message, throwable);
  }

  public final void seek(String messagePattern, Object... objects) {
    seekImpl(messagePattern, objects);
  }

  //

  public final void info(String message) {
    infoImpl(message);
  }

  public final void info(Throwable throwable) {
    infoImpl(throwable);
  }

  public final void info(String message, Throwable throwable) {
    infoImpl(message, throwable);
  }

  public final void info(String messagePattern, Object... objects) {
    infoImpl(messagePattern, objects);
  }

  //

  public final void debug(String message) {
    debugImpl(message);
  }

  public final void debug(Throwable throwable) {
    debugImpl(throwable);
  }

  public final void debug(String message, Throwable throwable) {
    debugImpl(message, throwable);
  }

  public final void debug(String messagePattern, Object... objects) {
    debugImpl(messagePattern, objects);
  }

  //

  public final void trace(String message) {
    traceImpl(message);
  }

  public final void trace(Throwable throwable) {
    traceImpl(throwable);
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

  protected abstract void fatalImpl(Throwable throwable);

  protected abstract void fatalImpl(String message, Throwable throwable);

  protected abstract void fatalImpl(String messagePattern, Object... objects);

  //

  protected abstract void errorImpl(String message);

  protected abstract void errorImpl(Throwable throwable);

  protected abstract void errorImpl(String message, Throwable throwable);

  protected abstract void errorImpl(String messagePattern, Object... objects);

  //

  protected abstract void warnImpl(String message);

  protected abstract void warnImpl(Throwable throwable);

  protected abstract void warnImpl(String message, Throwable throwable);

  protected abstract void warnImpl(String messagePattern, Object... objects);

  //

  protected abstract void hintImpl(String message);

  protected abstract void hintImpl(Throwable throwable);

  protected abstract void hintImpl(String message, Throwable throwable);

  protected abstract void hintImpl(String messagePattern, Object... objects);

  //

  protected abstract void infoImpl(String message);

  protected abstract void infoImpl(Throwable throwable);

  protected abstract void infoImpl(String message, Throwable throwable);

  protected abstract void infoImpl(String messagePattern, Object... objects);

  //

  protected abstract void seekImpl(String message);

  protected abstract void seekImpl(Throwable throwable);

  protected abstract void seekImpl(String message, Throwable throwable);

  protected abstract void seekImpl(String messagePattern, Object... objects);

  //

  protected abstract void debugImpl(String message);

  protected abstract void debugImpl(Throwable throwable);

  protected abstract void debugImpl(String message, Throwable throwable);

  protected abstract void debugImpl(String messagePattern, Object... objects);

  //

  protected abstract void traceImpl(String message);

  protected abstract void traceImpl(Throwable throwable);

  protected abstract void traceImpl(String message, Throwable throwable);

  protected abstract void traceImpl(String messagePattern, Object... objects);

}
