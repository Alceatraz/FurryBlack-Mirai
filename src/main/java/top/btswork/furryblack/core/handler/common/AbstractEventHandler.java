package top.btswork.furryblack.core.handler.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.btswork.furryblack.FurryBlack;
import top.btswork.furryblack.core.common.enhance.FileEnhance;
import top.btswork.furryblack.core.common.enhance.TimeEnhance;
import top.btswork.furryblack.core.exception.CoreException;
import top.btswork.furryblack.core.exception.moduels.BootException;
import top.btswork.furryblack.core.exception.moduels.InitException;
import top.btswork.furryblack.core.exception.moduels.ModuleException;
import top.btswork.furryblack.core.exception.moduels.ShutException;
import top.btswork.furryblack.core.common.annotation.Comment;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;

@Comment("基础模块类")
public abstract class AbstractEventHandler {

  protected final Logger logger = LoggerFactory.getLogger(getClass());
  //  protected final LoggerX logger = LoggerXFactory.getLogger(getClass());

  private volatile boolean internalInitLock;

  private URLClassLoader exclusiveClassLoader;

  //= ==================================================================================================================

  @Comment("模块开关")
  private volatile boolean enable = true;
  @Comment("模块状态")
  private volatile boolean status = false;

  @Comment("插件名字")
  protected String pluginName;
  @Comment("模块名字")
  protected String moduleName;

  @Comment("插件目录")
  protected Path FOLDER_ROOT;
  @Comment("配置目录")
  protected Path FOLDER_CONF;
  @Comment("数据目录")
  protected Path FOLDER_DATA;
  @Comment("日志目录")
  protected Path FOLDER_LOGS;

  @Comment("配置文件")
  protected Path FILE_CONFIG;

  @Comment("配置对象")
  protected Properties CONFIG;
  @Comment("全新配置")
  protected boolean NEW_CONFIG;

  //= ==================================================================================================================
  //= 生命周期
  //= ==================================================================================================================

  //= ==========================================================================
  //= 实例化

  public final void internalInit(String pluginName, String moduleName, URLClassLoader exclusiveClassLoader) {
    StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
    StackTraceElement schemaClazz = stackTrace[2];
    if (internalInitLock) {
      CoreException coreException = new CoreException("IllegalAccess - Invoke internalInit, And here is caller stack trace");
      coreException.setStackTrace(stackTrace);
      throw coreException;
    }
    if (!"makeModule".equals(schemaClazz.getMethodName())) {
      CoreException coreException = new CoreException("IllegalAccess - Invoke internalInit, And here is caller stack trace");
      coreException.setStackTrace(stackTrace);
      throw coreException;
    }
    if (!"top.btswork.furryblack.FurryBlack$Schema".equals(schemaClazz.getClassName())) {
      CoreException coreException = new CoreException("IllegalAccess - Invoke internalInit, And here is caller stack trace");
      coreException.setStackTrace(stackTrace);
      throw coreException;
    }
    this.internalInitLock = true;
    this.pluginName = pluginName;
    this.moduleName = moduleName;
    this.exclusiveClassLoader = exclusiveClassLoader;
    FOLDER_ROOT = FileEnhance.get(FurryBlack.getFolderModule(), moduleName);
    FOLDER_CONF = FileEnhance.get(FOLDER_ROOT, "conf");
    FOLDER_DATA = FileEnhance.get(FOLDER_ROOT, "data");
    FOLDER_LOGS = FileEnhance.get(FOLDER_ROOT, "logs");
    FILE_CONFIG = FileEnhance.get(FOLDER_ROOT, "config.properties");
    CONFIG = new Properties();
  }

  //= ==========================================================================
  //= 初始化

  public final void initWrapper() throws CoreException {
    ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
    try {
      Thread.currentThread().setContextClassLoader(exclusiveClassLoader);
      init();
    } catch (ModuleException exception) {
      throw new CoreException(exception);
    } finally {
      Thread.currentThread().setContextClassLoader(contextClassLoader);
    }
  }

  //= ==========================================================================
  //= 启动

  public final void bootWrapper() throws CoreException {
    if (!enable) return;
    ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
    try {
      Thread.currentThread().setContextClassLoader(exclusiveClassLoader);
      boot();
    } catch (ModuleException exception) {
      throw new CoreException(exception);
    } finally {
      Thread.currentThread().setContextClassLoader(contextClassLoader);
    }
    status = true;
  }

  //= ==========================================================================
  //= 关闭

  public final void shutWrapper() throws CoreException {
    if (!enable || !status) return;
    status = false;
    ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
    try {
      Thread.currentThread().setContextClassLoader(exclusiveClassLoader);
      shut();
    } catch (ModuleException exception) {
      throw new CoreException(exception);
    } finally {
      Thread.currentThread().setContextClassLoader(contextClassLoader);
    }
  }

  public final void executeWrapper(FurryBlack.ModuleCommand command) {
    ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
    try {
      Thread.currentThread().setContextClassLoader(exclusiveClassLoader);
      String message = execute(command);
      if (message != null) logger.error("命令执行失败 {} -> {}", command.toString(), message);
    } catch (ModuleException exception) {
      logger.error("命令执行失败 -> {}", command.toString(), exception);
    } catch (Exception exception) {
      throw new CoreException(exception);
    } finally {
      Thread.currentThread().setContextClassLoader(contextClassLoader);
    }
  }

  //= ==========================================================================
  //= 接口

  @Comment("生命周期 预载时")
  protected abstract void init() throws InitException;

  @Comment("生命周期 启动时")
  protected abstract void boot() throws BootException;

  @Comment("生命周期 关闭时")
  protected abstract void shut() throws ShutException;

  @Comment("辅助功能 执行命令")
  protected String execute(FurryBlack.ModuleCommand consoleCommand) throws ModuleException {
    return null;
  }

  //= ==================================================================================================================
  //= 公共API
  //= ==================================================================================================================

  //= ==========================================================================
  //= 模块

  @Comment("查询模块的工作状态")
  public boolean isReady() {
    return status;
  }

  @Comment("查询模块的启用状态")
  public final boolean isEnable() {
    return enable;
  }

  @Comment("改变模块的开关状态 启用")
  public final void setEnable() {
    this.enable = true;
  }

  @Comment("改变模块的开关状态 禁用")
  public final void setDisable() {
    this.enable = false;
  }

  @Comment("获取本插件的名字")
  public final String getPluginName() {
    return pluginName;
  }

  @Comment("获取本模块的名字")
  public final String getModuleName() {
    return moduleName;
  }

  //= ==========================================================================
  //= 文件

  @Comment("初始化插件根目录")
  protected final void ensureRootFolder() {
    FileEnhance.ensureFolder(FOLDER_ROOT);
  }

  @Comment("初始化插件配置目录")
  protected final void ensureConfFolder() {
    FileEnhance.ensureFolder(FOLDER_CONF);
  }

  @Comment("初始化插件数据目录")
  protected final void ensureDataFolder() {
    FileEnhance.ensureFolder(FOLDER_DATA);
  }

  @Comment("初始化插件日志目录")
  protected final void ensureLogsFolder() {
    FileEnhance.ensureFolder(FOLDER_LOGS);
  }

  //= ==========================================================================

  @Comment("获取目录下的文件")
  protected final Path getFile(String... name) {
    return FileEnhance.get(FOLDER_ROOT, name);
  }

  @Comment("获取配置目录下的文件")
  protected final Path getConfFile(String... name) {
    return FileEnhance.get(FOLDER_CONF, name);
  }

  @Comment("获取数据目录下的文件")
  protected final Path getDataFile(String... name) {
    return FileEnhance.get(FOLDER_DATA, name);
  }

  @Comment("获取日志目录下的文件")
  protected final Path getLogsFile(String... name) {
    return FileEnhance.get(FOLDER_LOGS, name);
  }

  @Comment("初始化目录下的文件")
  protected final Path ensureFile(String... name) {
    return FileEnhance.ensureFile(FOLDER_ROOT, name);
  }

  @Comment("初始化配置目录下的文件")
  protected final Path ensureConfFile(String... name) {
    return FileEnhance.ensureFile(FOLDER_CONF, name);
  }

  @Comment("初始化数据目录下的文件")
  protected final Path ensureDataFile(String... name) {
    return FileEnhance.ensureFile(FOLDER_DATA, name);
  }

  @Comment("初始化日志目录下的文件")
  protected final Path ensureLogsFile(String... name) {
    return FileEnhance.ensureFile(FOLDER_LOGS, name);
  }

  //= ==========================================================================

  @Comment("初始化配置目录下的目录")
  protected final Path ensureConfFolder(String... name) {
    return FileEnhance.ensureFolder(FOLDER_CONF, name);
  }

  @Comment("初始化数据目录下的目录")
  protected final Path ensureDataFolder(String... name) {
    return FileEnhance.ensureFolder(FOLDER_DATA, name);
  }

  @Comment("初始化日志目录下的目录")
  protected final Path ensureLogsFolder(String... name) {
    return FileEnhance.ensureFolder(FOLDER_LOGS, name);
  }

  //= ==========================================================================

  @Comment("读取文件")
  protected final String read(Path path) {
    return FileEnhance.read(path);
  }

  @Comment("读取文件")
  protected final List<String> readLine(Path path) {
    return readLine(path, false);
  }

  @Comment("读取文件")
  protected final List<String> readLine(Path path, boolean keepComment) {
    List<String> strings = FileEnhance.readLine(path);
    return keepComment ? strings : removeComment(strings);
  }

  //= ==========================================================================

  @Comment("读取根目录下的文件")
  protected final String read(String name) {
    return FileEnhance.read(FOLDER_ROOT, name);
  }

  @Comment("读取配置目录下的文件")
  protected final String readConf(String name) {
    return FileEnhance.read(FOLDER_CONF, name);
  }

  @Comment("读取数据目录下的文件")
  protected final String readData(String name) {
    return FileEnhance.read(FOLDER_DATA, name);
  }

  @Comment("读取日志目录下的文件")
  protected final String readLogs(String name) {
    return FileEnhance.read(FOLDER_LOGS, name);
  }

  //= ==========================================================================

  @Comment("读取根目录下的文件")
  protected final List<String> readLine(String name) {
    return readLine(name, false);
  }

  @Comment("读取日志目录下的文件")
  protected final List<String> readConfLine(String name) {
    return readConfLine(name, false);
  }

  @Comment("读取数据目录下的文件")
  protected final List<String> readDataLine(String name) {
    return readConfLine(name, false);
  }

  @Comment("读取日志目录下的文件")
  protected final List<String> readLogsLine(String name) {
    return readDataLine(name, false);
  }

  //= ==========================================================================

  @Comment("读取根目录下的文件")
  protected final List<String> readLine(String name, boolean keepComment) {
    List<String> strings = FileEnhance.readLine(FOLDER_ROOT, name);
    return keepComment ? strings : removeComment(strings);
  }

  @Comment("读取日志目录下的文件")
  protected final List<String> readConfLine(String name, boolean keepComment) {
    List<String> strings = FileEnhance.readLine(FOLDER_CONF, name);
    return keepComment ? strings : removeComment(strings);
  }

  @Comment("读取数据目录下的文件")
  protected final List<String> readDataLine(String name, boolean keepComment) {
    List<String> strings = FileEnhance.readLine(FOLDER_DATA, name);
    return keepComment ? strings : removeComment(strings);
  }

  @Comment("读取日志目录下的文件")
  protected final List<String> readLogsLine(String name, boolean keepComment) {
    List<String> strings = FileEnhance.readLine(FOLDER_LOGS, name);
    return keepComment ? strings : removeComment(strings);
  }

  private List<String> removeComment(List<String> lines) {
    return lines.stream()
             .filter(it -> !it.isBlank())
             .filter(it -> !it.startsWith("#"))
             .map(it -> {
               int index = it.indexOf("#");
               return index > 0 ? it.substring(0, index).trim() : it;
             }).toList();
  }

  //= ==========================================================================

  @Comment("写入根目录下的文件")
  protected final void write(Path path, String content) {
    FileEnhance.write(path, content);
  }

  @Comment("写入根目录下的文件")
  protected final void write(Path path, List<String> content) {
    FileEnhance.write(path, content);
  }

  @Comment("写入根目录下的文件")
  protected final void write(String name, String content) {
    FileEnhance.write(FOLDER_ROOT, name, content);
  }

  @Comment("写入日志目录下的文件")
  protected final void writeConf(String name, String content) {
    FileEnhance.write(FOLDER_CONF, name, content);
  }

  @Comment("写入日志目录下的文件")
  protected final void writeData(String name, String content) {
    FileEnhance.write(FOLDER_DATA, name, content);
  }

  @Comment("写入日志目录下的文件")
  protected final void writeLogs(String name, String content) {
    FileEnhance.write(FOLDER_LOGS, name, content);
  }

  @Comment("写入根目录下的文件")
  protected final void write(String name, List<String> content) {
    FileEnhance.write(FOLDER_ROOT, name, content);
  }

  @Comment("写入日志目录下的文件")
  protected final void writeConf(String name, List<String> content) {
    FileEnhance.write(FOLDER_CONF, name, content);
  }

  @Comment("写入日志目录下的文件")
  protected final void writeData(String name, List<String> content) {
    FileEnhance.write(FOLDER_DATA, name, content);
  }

  @Comment("写入日志目录下的文件")
  protected final void writeLogs(String name, List<String> content) {
    FileEnhance.write(FOLDER_LOGS, name, content);
  }

  @Comment("续写根目录下的文件")
  protected final void append(Path path, String content) {
    FileEnhance.append(path, content);
  }

  //= ==========================================================================

  @Comment("续写根目录下的文件")
  protected final void append(Path path, List<String> content) {
    FileEnhance.append(path, content);
  }

  @Comment("续写根目录下的文件")
  protected final void append(String name, String content) {
    FileEnhance.append(FOLDER_ROOT, name, content);
  }

  @Comment("续写日志目录下的文件")
  protected final void appendConf(String name, String content) {
    FileEnhance.append(FOLDER_CONF, name, content);
  }

  @Comment("续写日志目录下的文件")
  protected final void appendData(String name, String content) {
    FileEnhance.append(FOLDER_DATA, name, content);
  }

  @Comment("续写日志目录下的文件")
  protected final void appendLogs(String name, String content) {
    FileEnhance.append(FOLDER_LOGS, name, content);
  }

  @Comment("续写根目录下的文件")
  protected final void append(String name, List<String> content) {
    FileEnhance.append(FOLDER_ROOT, name, content);
  }

  @Comment("续写日志目录下的文件")
  protected final void appendConf(String name, List<String> content) {
    FileEnhance.append(FOLDER_CONF, name, content);
  }

  @Comment("续写日志目录下的文件")
  protected final void appendData(String name, List<String> content) {
    FileEnhance.append(FOLDER_DATA, name, content);
  }

  @Comment("续写日志目录下的文件")
  protected final void appendLogs(String name, List<String> content) {
    FileEnhance.append(FOLDER_LOGS, name, content);
  }

  //= ==========================================================================
  //= 配置

  @Comment("初始化默认配置文件")
  protected final void initConfig() {
    if (Files.notExists(FILE_CONFIG)) {
      logger.info("创建新配置文件 {}", FILE_CONFIG);
      try {
        FileEnhance.ensureFile(FILE_CONFIG);
      } catch (Exception exception) {
        throw new RuntimeException("初始化配置错误", exception);
      }
      NEW_CONFIG = true;
    }
  }

  @Comment("加载默认配置文件")
  protected final void loadConfig() {
    try (
      InputStream stream = Files.newInputStream(FILE_CONFIG);
      Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)
    ) {
      CONFIG.load(reader);
    } catch (IOException exception) {
      throw new RuntimeException("加载配置错误", exception);
    }
  }

  @Comment("保存默认配置文件")
  protected final void saveConfig() {
    try (
      OutputStream stream = Files.newOutputStream(FILE_CONFIG);
      Writer writer = new OutputStreamWriter(stream, StandardCharsets.UTF_8)
    ) {
      CONFIG.store(writer, "Saved by FurryBlack at " + TimeEnhance.datetime());
      writer.flush();
    } catch (IOException exception) {
      throw new RuntimeException("保存配置错误", exception);
    }
  }

  //= ==========================================================================

  @Comment("从配置加载")
  protected final byte getProperty(String name, byte defaultValue) {
    String value = CONFIG.getProperty(name);
    if (value == null) return defaultValue;
    try {
      return Byte.parseByte(value);
    } catch (Exception exception) {
      return defaultValue;
    }
  }

  @Comment("从配置加载")
  protected final short getProperty(String name, short defaultValue) {
    String value = CONFIG.getProperty(name);
    if (value == null) return defaultValue;
    try {
      return Short.parseShort(value);
    } catch (Exception exception) {
      return defaultValue;
    }
  }

  @Comment("从配置加载")
  protected final int getProperty(String name, int defaultValue) {
    String value = CONFIG.getProperty(name);
    if (value == null) return defaultValue;
    try {
      return Integer.parseInt(value);
    } catch (Exception exception) {
      return defaultValue;
    }
  }

  @Comment("从配置加载")
  protected final long getProperty(String name, long defaultValue) {
    String value = CONFIG.getProperty(name);
    if (value == null) return defaultValue;
    try {
      return Long.parseLong(value);
    } catch (Exception exception) {
      return defaultValue;
    }
  }

  @Comment("从配置加载")
  protected final boolean getProperty(String name, boolean defaultValue) {
    String value = CONFIG.getProperty(name);
    if (value == null) return defaultValue;
    try {
      return Boolean.parseBoolean(value);
    } catch (Exception exception) {
      return defaultValue;
    }
  }

  @Comment("从配置加载")
  protected final String getProperty(String name, String defaultValue) {
    return CONFIG.getProperty(name, defaultValue);
  }

  //= ==========================================================================

}
