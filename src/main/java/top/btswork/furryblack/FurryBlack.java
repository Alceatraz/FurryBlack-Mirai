package top.btswork.furryblack;

import kotlin.sequences.Sequence;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.Mirai;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.ContactList;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.contact.OtherClientInfo;
import net.mamoe.mirai.contact.Stranger;
import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.data.FriendInfo;
import net.mamoe.mirai.data.MemberInfo;
import net.mamoe.mirai.data.StrangerInfo;
import net.mamoe.mirai.data.UserProfile;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.Listener;
import net.mamoe.mirai.event.events.BotInvitedJoinGroupRequestEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MemberJoinEvent;
import net.mamoe.mirai.event.events.MemberLeaveEvent;
import net.mamoe.mirai.event.events.NewFriendRequestEvent;
import net.mamoe.mirai.event.events.UserMessageEvent;
import net.mamoe.mirai.message.action.Nudge;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.FlashImage;
import net.mamoe.mirai.message.data.ForwardMessage;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageSource;
import net.mamoe.mirai.message.data.PlainText;
import org.jline.builtins.Completers.TreeCompleter;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.ParsedLine;
import org.jline.reader.impl.completer.AggregateCompleter;
import org.jline.reader.impl.completer.ArgumentCompleter;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.widget.AutopairWidgets;
import top.btswork.furryblack.core.common.annotation.Comment;
import top.btswork.furryblack.core.common.enhance.DataEnhance;
import top.btswork.furryblack.core.common.enhance.FileEnhance;
import top.btswork.furryblack.core.common.enhance.LockEnhance;
import top.btswork.furryblack.core.common.enhance.StringEnhance;
import top.btswork.furryblack.core.common.enhance.TimeEnhance;
import top.btswork.furryblack.core.config.Argument;
import top.btswork.furryblack.core.config.Configuration;
import top.btswork.furryblack.core.exception.KernelException;
import top.btswork.furryblack.core.exception.schema.SchemaException;
import top.btswork.furryblack.core.exception.system.TerminalException;
import top.btswork.furryblack.core.handler.EventHandlerChecker;
import top.btswork.furryblack.core.handler.EventHandlerExecutor;
import top.btswork.furryblack.core.handler.EventHandlerFilter;
import top.btswork.furryblack.core.handler.EventHandlerMonitor;
import top.btswork.furryblack.core.handler.EventHandlerRunner;
import top.btswork.furryblack.core.handler.annotation.AnnotationEnhance;
import top.btswork.furryblack.core.handler.annotation.Checker;
import top.btswork.furryblack.core.handler.annotation.Executor;
import top.btswork.furryblack.core.handler.annotation.Filter;
import top.btswork.furryblack.core.handler.annotation.Monitor;
import top.btswork.furryblack.core.handler.annotation.Runner;
import top.btswork.furryblack.core.handler.common.AbstractEventHandler;
import top.btswork.furryblack.core.handler.common.Command;
import top.btswork.furryblack.core.logging.LoggerX;
import top.btswork.furryblack.core.logging.LoggerXFactory;
import top.btswork.furryblack.core.logging.Slf4jLoggerX;
import top.btswork.furryblack.core.logging.annotation.LoggerXConfig;
import top.btswork.furryblack.core.logging.enums.LoggerXLevel;
import top.mrxiaom.overflow.BotBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.lang.management.ManagementFactory;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.nio.file.StandardOpenOption.READ;
import static org.jline.builtins.Completers.TreeCompleter.node;
import static top.btswork.furryblack.core.common.enhance.StringEnhance.toHumanBytes;
import static top.btswork.furryblack.core.common.enhance.StringEnhance.toHumanHashCode;
import static top.btswork.furryblack.core.handler.annotation.AnnotationEnhance.printAnnotation;
import static top.btswork.furryblack.core.logging.enums.LoggerXColor.BLACK;
import static top.btswork.furryblack.core.logging.enums.LoggerXColor.BLUE;
import static top.btswork.furryblack.core.logging.enums.LoggerXColor.BOLD_BLACK;
import static top.btswork.furryblack.core.logging.enums.LoggerXColor.BOLD_BLUE;
import static top.btswork.furryblack.core.logging.enums.LoggerXColor.BOLD_BRIGHT_BLACK;
import static top.btswork.furryblack.core.logging.enums.LoggerXColor.BOLD_BRIGHT_BLUE;
import static top.btswork.furryblack.core.logging.enums.LoggerXColor.BOLD_BRIGHT_CYAN;
import static top.btswork.furryblack.core.logging.enums.LoggerXColor.BOLD_BRIGHT_GREEN;
import static top.btswork.furryblack.core.logging.enums.LoggerXColor.BOLD_BRIGHT_MAGENTA;
import static top.btswork.furryblack.core.logging.enums.LoggerXColor.BOLD_BRIGHT_RED;
import static top.btswork.furryblack.core.logging.enums.LoggerXColor.BOLD_BRIGHT_WHITE;
import static top.btswork.furryblack.core.logging.enums.LoggerXColor.BOLD_BRIGHT_YELLOW;
import static top.btswork.furryblack.core.logging.enums.LoggerXColor.BOLD_CYAN;
import static top.btswork.furryblack.core.logging.enums.LoggerXColor.BOLD_GREEN;
import static top.btswork.furryblack.core.logging.enums.LoggerXColor.BOLD_MAGENTA;
import static top.btswork.furryblack.core.logging.enums.LoggerXColor.BOLD_RED;
import static top.btswork.furryblack.core.logging.enums.LoggerXColor.BOLD_WHITE;
import static top.btswork.furryblack.core.logging.enums.LoggerXColor.BOLD_YELLOW;
import static top.btswork.furryblack.core.logging.enums.LoggerXColor.BRIGHT_BLACK;
import static top.btswork.furryblack.core.logging.enums.LoggerXColor.BRIGHT_BLUE;
import static top.btswork.furryblack.core.logging.enums.LoggerXColor.BRIGHT_CYAN;
import static top.btswork.furryblack.core.logging.enums.LoggerXColor.BRIGHT_GREEN;
import static top.btswork.furryblack.core.logging.enums.LoggerXColor.BRIGHT_MAGENTA;
import static top.btswork.furryblack.core.logging.enums.LoggerXColor.BRIGHT_RED;
import static top.btswork.furryblack.core.logging.enums.LoggerXColor.BRIGHT_WHITE;
import static top.btswork.furryblack.core.logging.enums.LoggerXColor.BRIGHT_YELLOW;
import static top.btswork.furryblack.core.logging.enums.LoggerXColor.CYAN;
import static top.btswork.furryblack.core.logging.enums.LoggerXColor.GREEN;
import static top.btswork.furryblack.core.logging.enums.LoggerXColor.MAGENTA;
import static top.btswork.furryblack.core.logging.enums.LoggerXColor.RED;
import static top.btswork.furryblack.core.logging.enums.LoggerXColor.RESET;
import static top.btswork.furryblack.core.logging.enums.LoggerXColor.WHITE;
import static top.btswork.furryblack.core.logging.enums.LoggerXColor.YELLOW;

@SuppressWarnings("ALL") @Comment(
  value = "FurryBlack - Mirai",
  usage = {
    "A Mirai wrapper QQ-Bot framework make with love and 🧦",
    "电子白熊会梦到仿生老黑吗",
    "Alceatraz Warprays @ BlackTechStudio",
    "个人主页 https://www.btswork.top",
    "项目地址 https://github.com/Alceatraz/FurryBlack-Mirai",
    "插件地址 https://github.com/Alceatraz/FurryBlack-Mirai-Extensions",
  },
  attention = {
    "!!!本项目并非使用纯AGPLv3协议, 请认真阅读LICENSE!!!"
  }
)
public class FurryBlack {

  //= ==================================================================================================================
  //
  //  公共常量
  //
  //= ==================================================================================================================

  public static final String APP_VERSION = "4.0.0";
  public static final String MIRAI_VERSION = "2.16.0";
  public static final String OVERFLOW_VERSION = "1.0.8";

  public static final String LINE = System.lineSeparator();

  public static final int CPU_CORES = Runtime.getRuntime().availableProcessors();
  public static final long BOOT_TIME = ManagementFactory.getRuntimeMXBean().getStartTime();

  public static final String USER_COUNTRY = System.getProperty("user.country").toLowerCase();
  public static final String USER_LANGUAGE = System.getProperty("user.language").toLowerCase();
  public static final String USER_VARIANT = System.getProperty("user.variant").toLowerCase();

  public static final String USER_TIMEZONE = TimeZone.getDefault().getDisplayName();

  public static final String OS_ARCH = System.getProperty("os.arch").toLowerCase();
  public static final String OS_NAME = System.getProperty("os.name").toLowerCase();
  public static final String OS_VERSION = System.getProperty("os.version").toLowerCase();

  //= ==================================================================================================================
  //
  //  框架常量
  //
  //= ==================================================================================================================

  private static final Path FOLDER_ROOT = Paths.get(System.getProperty("user.dir"));
  private static final Path FOLDER_CONFIG = FileEnhance.get(FOLDER_ROOT, "config");
  private static final Path FOLDER_PLUGIN = FileEnhance.get(FOLDER_ROOT, "plugin");
  private static final Path FOLDER_DEPEND = FileEnhance.get(FOLDER_ROOT, "depend");
  private static final Path FOLDER_MODULE = FileEnhance.get(FOLDER_ROOT, "module");
  private static final Path FOLDER_LOGGER = FileEnhance.get(FOLDER_ROOT, "logger");
  private static final Path FILE_APPLICATION_CONFIG = FileEnhance.get(FOLDER_CONFIG, "application.properties");

  private static Path FILE_LOGGER;

  private static final String CONTENT_INIT;
  private static final String CONTENT_DONE;
  private static final String CONTENT_INFO;
  private static final String CONTENT_HELP;
  private static final String CONTENT_COLOR;
  private static final String CONTENT_DEFAULT_CONFIG;

  private static final LockEnhance.Latch LATCH = new LockEnhance.Latch();
  private static final DateTimeFormatter FORMATTER = TimeEnhance.pattern("yyyy-MM-dd HH-mm-ss");

  static {

    String fileEncoding = System.getProperty("file.encoding");
    String stdoutEncoding = System.getProperty("stdout.encoding");
    String stderrEncoding = System.getProperty("stderr.encoding");

    if (!"UTF-8".equals(fileEncoding)) {
      String message = "[KERNEL][BOOTING] JVM file.encoding not UTF-8 but " + fileEncoding + ". Use java --Dfile.encoding=UTF-8";
      System.out.println(message);
      throw new KernelException(message);
    }

    if (!"UTF-8".equals(stdoutEncoding)) {
      String message = "[KERNEL][BOOTING] JVM stdoutEncoding not UTF-8 but " + fileEncoding + ". Use java --Dstdout.encoding=UTF-8";
      System.out.println(message);
      throw new KernelException(message);
    }

    if (!"UTF-8".equals(stderrEncoding)) {
      String message = "[KERNEL][BOOTING] JVM stderrEncoding not UTF-8 but " + fileEncoding + ". Use java --Dstderr.encoding=UTF-8";
      System.out.println(message);
      throw new KernelException(message);
    }

// @formatter:off

//= ====================================================================================================================
//= ====================================================================================================================
//= ====================================================================================================================

CONTENT_INIT =

BOLD_BRIGHT_CYAN +
"※ FurryBlack 安装模式 ===========================================================" + RESET + LINE + LINE +

"A Mirai wrapper framework make with love and 🧦" + LINE +
"Create by: Alceatraz Warprays @ BlackTechStudio" + LINE + LINE +

BOLD_BRIGHT_CYAN +
"#===============================================================================" + RESET

;

CONTENT_DONE =

BOLD_BRIGHT_CYAN +
"# ==============================================================================" + RESET + LINE + LINE +

"文件展开完成, 请退出后修改配置文件 config/application.properties" + LINE +

BOLD_BRIGHT_RED +
"完成后务必删除--init或--install参数,否则配置文件将会被覆盖!" + RESET + LINE + LINE +

BOLD_BRIGHT_CYAN +
"※ FurryBlack 安装完成 ===========================================================" + RESET

;

//= ====================================================================================================================
//= ====================================================================================================================
//= ====================================================================================================================

CONTENT_INFO =

BOLD_BRIGHT_CYAN +
"※ FurryBlack 版本信息 ===========================================================" + RESET + LINE + LINE +

"A Mirai wrapper framework make with love and 🧦" + LINE +
"Create by: Alceatraz Warprays @ BlackTechStudio" + LINE + LINE +

"框架版本 " + APP_VERSION + LINE +
"内核版本 " + MIRAI_VERSION + LINE +
"实现版本 " + OVERFLOW_VERSION + LINE + LINE +
"内核源码 https://github.com/mamoe/mirai" + LINE +
"框架源码 https://github.com/Alceatraz/FurryBlack-Mirai" + LINE +
"示例插件 https://github.com/Alceatraz/FurryBlack-Mirai-Extensions" + LINE + LINE +

BOLD_BRIGHT_CYAN +
"# ==============================================================================" + RESET

;

//= ====================================================================================================================
//= ====================================================================================================================
//= ====================================================================================================================

CONTENT_HELP =

BOLD_BRIGHT_CYAN +
"※ FurryBlack 版本信息 ===========================================================" + RESET + LINE +
"A Mirai wrapper framework make with love and 🧦" + LINE +
"Create by: Alceatraz Warprays @ BlackTechStudio" + LINE +
"框架版本 FurryBlack ---------- " + APP_VERSION + LINE +
"协议版本 Miria-API ----------- " + MIRAI_VERSION + LINE +
"内核版本 Overflow ------------ " + OVERFLOW_VERSION + LINE +
"内核源码 https://github.com/mamoe/mirai" + LINE +
"内核源码 https://github.com/MrXiaoM/Overflow" + LINE +
"框架源码 https://github.com/Alceatraz/FurryBlack-Mirai" + LINE +
"示例插件 https://github.com/Alceatraz/FurryBlack-Mirai-Extensions" + LINE +

BOLD_BRIGHT_CYAN +
"※ FurryBlack 安装模式 ===========================================================" + RESET + LINE +
"--install --------------------------- 展开文件并生成默认配置" + LINE +

BOLD_BRIGHT_CYAN +
"※ FurryBlack 交互模式 ===========================================================" + RESET + LINE +
"--help ------------------------------ 显示帮助" + LINE +
"--info ------------------------------ 显示版本" + LINE +
"--color ----------------------------- 显示颜色" + LINE +

BOLD_BRIGHT_CYAN +
"※ FurryBlack 内核参数 ===========================================================" + RESET + LINE +
"内核配置早于配置文件加载, 所以内核参数必须以环境变量形式提供" + LINE +
"KERNEL_CONSOLE_PROVIDER ------------ 指定终端类型 none/stdin/jline" + LINE +
"KERNEL_LOGGING_LEVEL --------------- 指定日志等级 CLOSE/ERROR/INFO/WARN/DEBUG/TRACE" + LINE +
"KERNEL_LOGGING_PREFIX -------------- 指定日志等级 指定按类前缀不同等级配置文件路径*" + LINE +
"KERNEL_LOGGING_FULLNAME ------------ 指定日志格式 是否显示完整类名" + LINE +
"KERNEL_LOGGING_PROVIDER ------------ 指定日志后端 内置以下三种" + LINE +
"top.btswork.furryblack.core.logging.backend.NullLogger" + LINE +
"top.btswork.furryblack.core.logging.backend.PrintLoggerX" + LINE +
"top.btswork.furryblack.core.logging.backend.WriterLoggerX" + LINE +
"* 前缀和对应的日志等级保存为 properties 格式, 比如 io.netty=ERROR" + LINE +

BOLD_BRIGHT_CYAN +
"※ FurryBlack 终端功能 ============================================================" + RESET + LINE +
RED +
"⚠ 控制台任何操作都属于底层操作可以直接对框架进行不安全和非法的操作" + RESET + LINE +
"安全: 设计如此, 不会导致异常或者不可预测的结果" + LINE +
"风险: 功能设计上是安全操作, 但是具体被操作对象可能导致错误" + LINE +
"危险: 没有安全性检查的操作, 可能会让功能严重异常导致被迫重启或损坏模块的数据存档" + LINE +
"高危: 后果完全未知的危险操作, 或者正常流程中不应该如此操作但是控制台仍然可以强制执行" + LINE +

BOLD_BRIGHT_CYAN +
"※ 框架内核 ======================================================================" + RESET + LINE +
"help -------------------------------- (安全) 显示本帮助信息" + LINE +
"system status ----------------------- (安全) 显示系统运行状态" + LINE +
"system stacks ----------------------- (安全) 显示所有运行中的线程" + LINE +
"system debug [enable|disable] ------- (安全) 切换DEBUG开关状态" + LINE +
"system power-off -------------------- (安全) 正常关闭系统 直接执行" + LINE +
"system rapid-stop ------------------- (危险) 快速关闭系统 直接执行" + LINE +
"system force-exit ------------------- (高危) 直接杀死系统 二次确认" + LINE +

BOLD_BRIGHT_CYAN +
"※ 插件系统 ======================================================================" + RESET + LINE +
"schema event [enable|disable] ------- (安全) 启用消息事件处理 正常响应消息" + LINE +
"schema ------------------------------ (安全) 显示插件机制注册状态" + LINE +
"schema plugin ----------------------- (安全) 列出所有插件" + LINE +
"schema module ----------------------- (安全) 列出所有模块" + LINE +
"schema module init ------------------ (风险) 执行模块预载流程 无视状态直接执行" + LINE +
"schema module boot ------------------ (风险) 执行模块启动流程 无视状态直接执行" + LINE +
"schema module shut ------------------ (风险) 执行模块关闭流程 无视状态直接执行" + LINE +
"schema module reboot ---------------- (风险) 执行模块重启流程 无视状态直接执行" + LINE +
"schema module unload ---------------- (风险) 彻底卸载模块实例 无视状态直接执行" + LINE +
YELLOW +
"* Runner可能会被依赖, 底层操作框架不检查依赖, 有可能导致关联模块崩溃" + RESET + LINE +

BOLD_BRIGHT_CYAN +
"※ 昵称系统 ======================================================================" + RESET + LINE +
"nickname list ----------------------- (安全) 列出昵称" + LINE +
"nickname clean ---------------------- (安全) 清空昵称" + LINE +
"nickname append --------------------- (安全) 加载且合并昵称" + LINE +
"nickname reload --------------------- (安全) 清空且加载昵称" + LINE +

BOLD_BRIGHT_CYAN +
"# ==============================================================================" + RESET

;

//= ====================================================================================================================
//= ====================================================================================================================
//= ====================================================================================================================

CONTENT_COLOR =

"# ===========================================================================================================" + LINE +
"NO COLOR ---------------- THE QUICK BROWN FOX JUMP OVER A LAZY DOG | the quick brown fox jump over a lazy dog" + LINE +
"# ===========================================================================================================" + LINE +
BLACK               + "BLACK ------------------- THE QUICK BROWN FOX JUMP OVER A LAZY DOG | the quick brown fox jump over a lazy dog" + RESET + LINE +
RED                 + "RED --------------------- THE QUICK BROWN FOX JUMP OVER A LAZY DOG | the quick brown fox jump over a lazy dog" + RESET + LINE +
GREEN               + "GREEN ------------------- THE QUICK BROWN FOX JUMP OVER A LAZY DOG | the quick brown fox jump over a lazy dog" + RESET + LINE +
YELLOW              + "YELLOW ------------------ THE QUICK BROWN FOX JUMP OVER A LAZY DOG | the quick brown fox jump over a lazy dog" + RESET + LINE +
BLUE                + "BLUE -------------------- THE QUICK BROWN FOX JUMP OVER A LAZY DOG | the quick brown fox jump over a lazy dog" + RESET + LINE +
MAGENTA             + "MAGENTA ----------------- THE QUICK BROWN FOX JUMP OVER A LAZY DOG | the quick brown fox jump over a lazy dog" + RESET + LINE +
CYAN                + "CYAN -------------------- THE QUICK BROWN FOX JUMP OVER A LAZY DOG | the quick brown fox jump over a lazy dog" + RESET + LINE +
WHITE               + "WHITE ------------------- THE QUICK BROWN FOX JUMP OVER A LAZY DOG | the quick brown fox jump over a lazy dog" + RESET + LINE +
BRIGHT_BLACK        + "BRIGHT_BLACK ------------ THE QUICK BROWN FOX JUMP OVER A LAZY DOG | the quick brown fox jump over a lazy dog" + RESET + LINE +
BRIGHT_RED          + "BRIGHT_RED -------------- THE QUICK BROWN FOX JUMP OVER A LAZY DOG | the quick brown fox jump over a lazy dog" + RESET + LINE +
BRIGHT_GREEN        + "BRIGHT_GREEN ------------ THE QUICK BROWN FOX JUMP OVER A LAZY DOG | the quick brown fox jump over a lazy dog" + RESET + LINE +
BRIGHT_YELLOW       + "BRIGHT_YELLOW ----------- THE QUICK BROWN FOX JUMP OVER A LAZY DOG | the quick brown fox jump over a lazy dog" + RESET + LINE +
BRIGHT_BLUE         + "BRIGHT_BLUE ------------- THE QUICK BROWN FOX JUMP OVER A LAZY DOG | the quick brown fox jump over a lazy dog" + RESET + LINE +
BRIGHT_MAGENTA      + "BRIGHT_MAGENTA ---------- THE QUICK BROWN FOX JUMP OVER A LAZY DOG | the quick brown fox jump over a lazy dog" + RESET + LINE +
BRIGHT_CYAN         + "BRIGHT_CYAN ------------- THE QUICK BROWN FOX JUMP OVER A LAZY DOG | the quick brown fox jump over a lazy dog" + RESET + LINE +
BRIGHT_WHITE        + "BRIGHT_WHITE ------------ THE QUICK BROWN FOX JUMP OVER A LAZY DOG | the quick brown fox jump over a lazy dog" + RESET + LINE +
"# ===========================================================================================================" + LINE +
BOLD_BLACK          + "BOLD_BLACK -------------- THE QUICK BROWN FOX JUMP OVER A LAZY DOG | the quick brown fox jump over a lazy dog" + RESET + LINE +
BOLD_RED            + "BOLD_RED ---------------- THE QUICK BROWN FOX JUMP OVER A LAZY DOG | the quick brown fox jump over a lazy dog" + RESET + LINE +
BOLD_GREEN          + "BOLD_GREEN -------------- THE QUICK BROWN FOX JUMP OVER A LAZY DOG | the quick brown fox jump over a lazy dog" + RESET + LINE +
BOLD_YELLOW         + "BOLD_YELLOW ------------- THE QUICK BROWN FOX JUMP OVER A LAZY DOG | the quick brown fox jump over a lazy dog" + RESET + LINE +
BOLD_BLUE           + "BOLD_BLUE --------------- THE QUICK BROWN FOX JUMP OVER A LAZY DOG | the quick brown fox jump over a lazy dog" + RESET + LINE +
BOLD_MAGENTA        + "BOLD_MAGENTA ------------ THE QUICK BROWN FOX JUMP OVER A LAZY DOG | the quick brown fox jump over a lazy dog" + RESET + LINE +
BOLD_CYAN           + "BOLD_CYAN --------------- THE QUICK BROWN FOX JUMP OVER A LAZY DOG | the quick brown fox jump over a lazy dog" + RESET + LINE +
BOLD_WHITE          + "BOLD_WHITE -------------- THE QUICK BROWN FOX JUMP OVER A LAZY DOG | the quick brown fox jump over a lazy dog" + RESET + LINE +
BOLD_BRIGHT_BLACK   + "BOLD_BRIGHT_BLACK ------- THE QUICK BROWN FOX JUMP OVER A LAZY DOG | the quick brown fox jump over a lazy dog" + RESET + LINE +
BOLD_BRIGHT_RED     + "BOLD_BRIGHT_RED --------- THE QUICK BROWN FOX JUMP OVER A LAZY DOG | the quick brown fox jump over a lazy dog" + RESET + LINE +
BOLD_BRIGHT_GREEN   + "BOLD_BRIGHT_GREEN ------- THE QUICK BROWN FOX JUMP OVER A LAZY DOG | the quick brown fox jump over a lazy dog" + RESET + LINE +
BOLD_BRIGHT_YELLOW  + "BOLD_BRIGHT_YELLOW ------ THE QUICK BROWN FOX JUMP OVER A LAZY DOG | the quick brown fox jump over a lazy dog" + RESET + LINE +
BOLD_BRIGHT_BLUE    + "BOLD_BRIGHT_BLUE -------- THE QUICK BROWN FOX JUMP OVER A LAZY DOG | the quick brown fox jump over a lazy dog" + RESET + LINE +
BOLD_BRIGHT_MAGENTA + "BOLD_BRIGHT_MAGENTA ----- THE QUICK BROWN FOX JUMP OVER A LAZY DOG | the quick brown fox jump over a lazy dog" + RESET + LINE +
BOLD_BRIGHT_CYAN    + "BOLD_BRIGHT_CYAN -------- THE QUICK BROWN FOX JUMP OVER A LAZY DOG | the quick brown fox jump over a lazy dog" + RESET + LINE +
BOLD_BRIGHT_WHITE   + "BOLD_BRIGHT_WHITE ------- THE QUICK BROWN FOX JUMP OVER A LAZY DOG | the quick brown fox jump over a lazy dog" + RESET + LINE +
"# ===========================================================================================================" + LINE +
BOLD_BRIGHT_RED     + "FATAL / BOLD_BRIGHT_RED --------- [2000-00-00 00:00:00][FurryBlack] The Quick Brown Fox Jump Over A Lazy Dog" + RESET + LINE +
BOLD_RED            + "ERROR / BOLD_RED ---------------- [2000-00-00 00:00:00][FurryBlack] The Quick Brown Fox Jump Over A Lazy Dog" + RESET + LINE +
BOLD_BRIGHT_YELLOW  + "WARN  / BOLD_BRIGHT_YELLOW ------ [2000-00-00 00:00:00][FurryBlack] The Quick Brown Fox Jump Over A Lazy Dog" + RESET + LINE +
BRIGHT_CYAN         + "HINT  / BRIGHT_CYAN ------------- [2000-00-00 00:00:00][FurryBlack] The Quick Brown Fox Jump Over A Lazy Dog" + RESET + LINE +
BRIGHT_GREEN        + "SEEK  / BRIGHT_GREEN ------------ [2000-00-00 00:00:00][FurryBlack] The Quick Brown Fox Jump Over A Lazy Dog" + RESET + LINE +
"INFO  / BRIGHT_RED -------------- [2000-00-00 00:00:00][FurryBlack] The Quick Brown Fox Jump Over A Lazy Dog" + LINE +
BRIGHT_BLACK        + "DEBUG / BRIGHT_BLACK ------------ [2000-00-00 00:00:00][FurryBlack] The Quick Brown Fox Jump Over A Lazy Dog" + RESET + LINE +
BLACK               + "TRACE / BLACK ------------------- [2000-00-00 00:00:00][FurryBlack] The Quick Brown Fox Jump Over A Lazy Dog" + RESET + LINE +
"# ==========================================================================================================="

;

//= ====================================================================================================================
//= ====================================================================================================================
//= ====================================================================================================================

CONTENT_DEFAULT_CONFIG =

"""
#==========================================================
# 此 properties 文件使用 UTF-8 模式读取 故支持中文字符
#==========================================================
# none/stdin/jline
# kernel.console.provider=jline
# ERROR WARN INFO DEBUG TRACE EVERY
# kernel.logging.level=INFO
# config/logging-prefix.properties
# KERNEL_LOGGING_PREFIX=
# top.btswork.furryblack.core.logging.backend.NullLoggerX
# top.btswork.furryblack.core.logging.backend.PrintLoggerX
# top.btswork.furryblack.core.logging.backend.WriterLoggerX
# kernel.logging.provider=
# true/false
# kernel.logging.fullname=false
#==========================================================
# 系统配置
# system.debug=false
# system.debug.halt=false
# system.debug.unsafe=false
# system.debug.nologin=false
#==========================================================
# 登录配置
# positive reversed
# system.onebot.mode=positive
# ---------------------------------------------------------
# system.onebot.token=
# ---------------------------------------------------------
# ws://localhost:6099
# system.onebot.positive.server=
# ---------------------------------------------------------
# 6099
# system.onebot.reversed.listen=
#==========================================================
# 性能配置
# thread.monitor.size=
# thread.monitor.size.max=
#==========================================================
# 功能配置
# /[a-zA-Z0-9]{2,16}
# module.regex=
#==========================================================
"""

;

//= ====================================================================================================================
//= ====================================================================================================================
//= ====================================================================================================================

// @formatter:on

  }

  //= ==================================================================================================================
  //
  //  框架变量
  //
  //= ==================================================================================================================

  private static LoggerX logger;

  private static FurryBlackConfig CONFIG;

  private static Terminal TERMINAL;
  private static Dispatcher DISPATCHER;

  private static Bot BOT;
  private static Schema SCHEMA;
  private static Nickname NICKNAME;

  private static String MESSAGE_INFO;
  private static String MESSAGE_EULA;
  private static String MESSAGE_HELP;
  private static String MESSAGE_LIST_USERS;
  private static String MESSAGE_LIST_GROUP;

  private static ThreadPoolExecutor MONITOR_PROCESS;
  private static ScheduledThreadPoolExecutor SCHEDULE_SERVICE;

  //= =================================================================================================================

  private static volatile boolean STATE_BOOTING = true;
  private static volatile boolean STATE_ACCEPT_EVENT = false;

  private static volatile boolean STATE_SHUTDOWN_DROP = false;

  private static volatile boolean STATE_SYSTEM_DEBUG = false;
  private static volatile boolean STATE_SYSTEM_DEBUG_HALT = false;
  private static volatile boolean STATE_SYSTEM_DEBUG_UNSAFE = false;

  //= ==================================================================================================================
  //=
  //= 实例控制
  //=
  //= ==================================================================================================================

  private FurryBlack() {}

  //= ==================================================================================================================
  //=
  //= 启动入口
  //=
  //= ==================================================================================================================

  public static void main(String[] args) {

    System.out.println(">> FURRYBLACK BOOTSTRAP STARTING ......");

    for (String arg : args) {
      System.out.println("> " + arg);
    }

    System.out.println(">> FURRYBLACK FRAMEWORK KERNEL HANDOVER");

    //= ================================================================================================================
    //=
    //=
    //= 交互模式
    //=
    //=
    //= ================================================================================================================

    Argument argument = Argument.parse(args);

    if (argument.hasOption("init") || argument.hasOption("install")) {

      System.out.println(CONTENT_INIT);

      System.out.println("框架工作目录 " + FOLDER_ROOT);

      System.out.println("框架配置目录 " + FOLDER_PLUGIN);
      String ensureFolderConfig = FileEnhance.ensureFolderSafe(FOLDER_CONFIG);
      KernelException.check("初始化框架配置目录失败 -> ", ensureFolderConfig);

      System.out.println("插件扫描目录 " + FOLDER_PLUGIN);
      String ensureFolderPlugin = FileEnhance.ensureFolderSafe(FOLDER_PLUGIN);
      KernelException.check("初始化插件扫描目录失败 -> ", ensureFolderPlugin);

      System.out.println("插件依赖目录 " + FOLDER_DEPEND);
      String ensureFolderDepend = FileEnhance.ensureFolderSafe(FOLDER_DEPEND);
      KernelException.check("初始化插件依赖目录失败 -> ", ensureFolderDepend);

      System.out.println("模块数据目录 " + FOLDER_MODULE);
      String ensureFolderModule = FileEnhance.ensureFolderSafe(FOLDER_MODULE);
      KernelException.check("初始化模块数据目录失败 -> ", ensureFolderModule);

      System.out.println("框架日志目录 " + FOLDER_LOGGER);
      String ensureFolderLogger = FileEnhance.ensureFolderSafe(FOLDER_LOGGER);
      KernelException.check("初始化框架日志目录失败 -> ", ensureFolderLogger);

      System.out.println("框架配置文件 " + FILE_APPLICATION_CONFIG);
      String ensureConfigApplication = FileEnhance.ensureFileSafe(FILE_APPLICATION_CONFIG);
      KernelException.check("初始化框架配置文件失败 -> ", ensureConfigApplication);

      FileEnhance.write(FILE_APPLICATION_CONFIG, CONTENT_DEFAULT_CONFIG);

      System.out.println(CONTENT_DONE);
      System.out.println();

      return;

    } else {

      // 显示 信息
      if (argument.hasOption("info")) {
        System.out.println(CONTENT_INFO);
        System.out.println();
        return;
      }

      // 显示 帮助
      if (argument.hasOption("help")) {
        System.out.println(CONTENT_HELP);
        System.out.println();
        return;
      }

      // 显示 颜色
      if (argument.hasOption("color")) {
        System.out.println(CONTENT_COLOR);
        System.out.println();
        return;
      }

    }

    //= ================================================================================================================
    //=
    //=
    //= 正式模式
    //=
    //=
    //= ================================================================================================================

    System.out.println("[KERNEL][BOOTING] >> Initialization FurryBlack v" + APP_VERSION);

    System.out.println("[KERNEL][BOOTING]检测运行环境");

    System.out.println("[KERNEL][BOOTING]启动时间 " + TimeEnhance.datetime(BOOT_TIME));
    System.out.println("[KERNEL][BOOTING]物理线程 " + CPU_CORES);
    System.out.println("[KERNEL][BOOTING]系统架构 " + OS_ARCH);
    System.out.println("[KERNEL][BOOTING]系统类型 " + OS_NAME);
    System.out.println("[KERNEL][BOOTING]系统版本 " + OS_VERSION);

    System.out.println("[KERNEL][BOOTING]系统区域 " + USER_COUNTRY);
    System.out.println("[KERNEL][BOOTING]系统语言 " + USER_LANGUAGE);
    System.out.println("[KERNEL][BOOTING]语言变种 " + USER_VARIANT);
    System.out.println("[KERNEL][BOOTING]系统时区 " + USER_TIMEZONE);

    //= ================================================================================================================
    //= 文件子系统
    //= ================================================================================================================

    String ensureFolderConfig = FileEnhance.ensureFolderSafe(FOLDER_CONFIG);
    String ensureFolderPlugin = FileEnhance.ensureFolderSafe(FOLDER_PLUGIN);
    String ensureFolderDepend = FileEnhance.ensureFolderSafe(FOLDER_DEPEND);
    String ensureFolderModule = FileEnhance.ensureFolderSafe(FOLDER_MODULE);
    String ensureFolderLogger = FileEnhance.ensureFolderSafe(FOLDER_LOGGER);

    KernelException.check("初始化配置目录失败 -> ", ensureFolderConfig);
    KernelException.check("初始化插件目录失败 -> ", ensureFolderPlugin);
    KernelException.check("初始化依赖目录失败 -> ", ensureFolderDepend);
    KernelException.check("初始化数据目录失败 -> ", ensureFolderModule);
    KernelException.check("初始化日志目录失败 -> ", ensureFolderLogger);

    System.out.println("[KERNEL][STORAGE]应用工作目录 " + FOLDER_ROOT);
    System.out.println("[KERNEL][STORAGE]核心配置目录 " + FOLDER_CONFIG);
    System.out.println("[KERNEL][STORAGE]插件扫描目录 " + FOLDER_PLUGIN);
    System.out.println("[KERNEL][STORAGE]模块依赖目录 " + FOLDER_DEPEND);
    System.out.println("[KERNEL][STORAGE]模块数据目录 " + FOLDER_MODULE);
    System.out.println("[KERNEL][STORAGE]核心日志目录 " + FOLDER_LOGGER);

    //= ================================================================================================================
    //= 配置子系统
    //= ================================================================================================================

    if (Files.notExists(FILE_APPLICATION_CONFIG)) {

      try {
        FileEnhance.ensureFile(FILE_APPLICATION_CONFIG);
        FileEnhance.write(FILE_APPLICATION_CONFIG, CONTENT_DEFAULT_CONFIG);
      } catch (Exception exception) {
        throw new KernelException("[KERNEL][BOOTING]Create default application.properties failed", exception);
      }

      System.out.println("[KERNEL][BOOTING]错误：未能找到配置文件 - 创建默认配置文件 application.properties");

      throw new KernelException("[KERNEL][BOOTING]Application configuration file not found - application.properties");

    }

    Properties properties = new Properties();

    System.out.println("[KERNEL][SETTING]加载配置文件");

    try (
      InputStream inputStream = Files.newInputStream(FILE_APPLICATION_CONFIG, READ);
      Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)
    ) {
      properties.load(reader);
    } catch (IOException exception) {
      throw new KernelException("[KERNEL][SETTING]读取配置文件失败 -> " + FILE_APPLICATION_CONFIG, exception);
    }

    System.out.println("[KERNEL][SETTING]解析配置文件");

    Configuration configuration = Configuration.getInstance();

    for (Map.Entry<Object, Object> entry : properties.entrySet()) {
      if (entry.getKey() == null) continue;
      var key = entry.getKey().toString();
      var val = DataEnhance.valueOf(entry.getValue());
      System.out.println("[KERNEL][SETTING]CONF > " + key + " -> " + val);
      String[] keys = key.split("\\.");
      configuration.append(keys, val);
    }

    for (String key : argument.getOptions()) {
      System.out.println("[KERNEL][SETTING]ARGS > " + key);
      String[] keys = key.split("-");
      configuration.append(keys);
    }

    for (Map.Entry<String, String> entry : argument.getParametersEntrySet()) {
      var key = entry.getKey();
      var val = entry.getValue();
      if ("onebot-token".equals(key)) {
        System.out.println("[KERNEL][SETTING]出于安全原因使用参数方式传入 --onebot-token 不被支持");
        throw new KernelException("[KERNEL][SETTING] For security reason The agument --onebot-token is forbidden");
      }
      System.out.println("[KERNEL][SETTING]ARGS > " + key + " -> " + val);
      String[] keys = key.split("-");
      configuration.append(keys, val);
    }

    CONFIG = FurryBlackConfig.from(configuration);

    if (CONFIG.systemDebug != null && CONFIG.systemDebug) {
      STATE_SYSTEM_DEBUG = true;
      System.out.println("[KERNEL][SETTING] 调试模式/调试模式 - system.debug = ENABLED");
    }

    if (CONFIG.systemDebugHalt != null && CONFIG.systemDebugHalt) {
      STATE_SYSTEM_DEBUG_HALT = true;
      System.out.println("[KERNEL][SETTING] 调试模式/强制退出 - system.debug.halt = ENABLED");
    }

    if (CONFIG.systemDebugUnsafe != null && CONFIG.systemDebugUnsafe) {
      STATE_SYSTEM_DEBUG_UNSAFE = true;
      System.out.println("[KERNEL][SETTING] 调试模式/内部接口 -  system.debug.unsafe = ENABLED");
    }

    if (CONFIG.systemDebugNologin != null && CONFIG.systemDebugNologin) {
      System.out.println("[KERNEL][SETTING] 调试模式/内部接口 -  system.debug.nologin = ENABLED");
    }

    //= ================================================================================================================
    //= 终端子系统
    //= ================================================================================================================

    switch (CONFIG.kernelConsoleProvider) {

      case null:
        TERMINAL = JlineTerminal.getInstance();
        System.out.println("[KERNEL][CONSOLE]终端模式 - 默认终端 -> jline");
        break;

      case "stdin":
        TERMINAL = StdinTerminal.getInstance();
        System.out.println("[KERNEL][CONSOLE]终端模式 - 兼容终端 -> stdin");
        break;

      case "jline":
        TERMINAL = JlineTerminal.getInstance();
        System.out.println("[KERNEL][CONSOLE]终端模式 - 交互终端 -> jline");
        break;

      case "none":
        TERMINAL = NoConsoleTerminal.getInstance();
        System.out.println("[KERNEL][CONSOLE]终端模式 - 关闭终端 -> no-input");
        break;

      default:
        throw new KernelException("[KERNEL][BOOTING] Console prover invalid " + CONFIG.kernelConsoleProvider + " none/stdin/jline");

    }

    //= ================================================================================================================
    //= 日志子系统
    //= ================================================================================================================

    //= ========================================================================
    //= 日志后端

    if (CONFIG.kernelLoggingProvider == null || CONFIG.kernelLoggingProvider.isBlank()) {

      System.out.println("[KERNEL][LOGGING]日志后端 - 默认后端 -> WriterLoggerX");

    } else {

      System.out.println("[KERNEL][LOGGING]日志后端 - 日志后端 -> " + CONFIG.kernelLoggingProvider);

      Class<?> clazz;
      try {
        clazz = Class.forName(CONFIG.kernelLoggingProvider);
      } catch (Exception exception) {
        throw new KernelException("[KERNEL][LOGGING]日志后端 - 尝试加载日志后端失败, 加载失败 -> " + CONFIG.kernelLoggingProvider, exception);
      }

      Class<? extends LoggerX> loggerClazz;
      if (LoggerX.class.isAssignableFrom(clazz)) {
        loggerClazz = (Class<? extends LoggerX>) clazz;
      } else {
        throw new KernelException("[KERNEL][LOGGING]日志后端 - 尝试加载日志后端失败, 未继承 LoggerX -> " + CONFIG.kernelLoggingProvider);
      }

      if (!loggerClazz.isAnnotationPresent(LoggerXConfig.class)) {
        throw new KernelException("[KERNEL][LOGGING]日志后端 - 尝试加载日志后端失败, 未添加 LoggerXConfig 注解 -> " + CONFIG.kernelLoggingProvider);
      }

      LoggerXFactory.setDefault(loggerClazz);

    }

    System.out.println("[KERNEL][LOGGING]日志后端 - " + LoggerXFactory.getDefault().getSimpleName());

    //= ========================================================================
    //= 日志前缀

    Path kernelLoggingPrefix = null;
    boolean defaultKernelLoggingPrefix = false;

    if (CONFIG.kernelLoggingPrefix == null) {
      System.out.println("[KERNEL][LOGGING]日志前缀 - 用户设置关闭");
    } else if (CONFIG.kernelLoggingPrefix.isBlank()) {
      System.out.println("[KERNEL][LOGGING]日志前缀 - 尝试默认配置 -> config/logging-prefix.properties");
      kernelLoggingPrefix = Paths.get("config/logging-prefix.properties");
      defaultKernelLoggingPrefix = true;
    } else {
      System.out.println("[KERNEL][LOGGING]日志前缀 - 尝试加载配置 -> " + CONFIG.kernelLoggingPrefix);
      kernelLoggingPrefix = Paths.get(CONFIG.kernelLoggingPrefix);
    }

    if (kernelLoggingPrefix == null) {} else if (Files.exists(kernelLoggingPrefix)) {
      List<String> lines = FileEnhance.readLine(kernelLoggingPrefix).stream()
        .map(it -> it.trim())
        .filter(it -> !it.isBlank())
        .filter(it -> !it.startsWith("#"))
        .toList();
      if (lines.isEmpty()) {
        System.out.println("[KERNEL][LOGGING]日志前缀 - 自动关闭前缀功能 指定配置文件无有效内容 -> " + CONFIG.kernelLoggingPrefix);
      } else {
        int prefixCount = 0;
        for (String line : lines) {
          String[] split = line.split("=");
          if (split.length == 2) {
            var key = split[0];
            var val = split[1];
            LoggerXLevel level = LoggerXLevel.of(val);
            if (level == null) {
              System.out.println("[KERNEL][LOGGING]日志前缀 - 跳过无效配置内容 " + line);
              continue;
            }
            FurryBlack.terminalPrintln("[KERNEL][LOGGING]LOAD > " + String.format("%5s", val) + " - " + key);
            LoggerXFactory.injectPrefix(key, level);
            prefixCount++;
          } else {
            System.out.println("[KERNEL][LOGGING]日志前缀 - 跳过无效配置内容 " + line);
          }
        }
        if (prefixCount > 0) {
          LoggerXFactory.setEnablePrefix(true);
          System.out.println("[KERNEL][LOGGING]日志前缀 - 前缀配置功能开启 共加载" + prefixCount + "条规则");
        } else {
          System.out.println("[KERNEL][LOGGING]日志前缀 - 自动关闭前缀功能 无有效配置内容");
        }
      }
    } else {
      if (defaultKernelLoggingPrefix) {
        System.out.println("[KERNEL][LOGGING]日志前缀 - 自动关闭前缀功能 默认配置不存在");
      } else {
        System.out.println("[KERNEL][LOGGING]日志前缀 - 自动关闭前缀功能 指定文件不存在 -> " + CONFIG.kernelLoggingPrefix);
      }
    }

    //= ========================================================================
    //= 日志等级

    if (CONFIG.kernelLoggingLevel != null) {

      LoggerXLevel loggerXLevel = LoggerXLevel.of(CONFIG.kernelLoggingLevel);

      if (loggerXLevel == null) {
        System.out.println("[KERNEL][LOGGING]日志级别 - 输入值无效 -> " + CONFIG.kernelLoggingLevel + ", 可用日志级别为:");
        System.out.println("[KERNEL][LOGGING] - ERROR");
        System.out.println("[KERNEL][LOGGING] - WARN");
        System.out.println("[KERNEL][LOGGING] - INFO");
        System.out.println("[KERNEL][LOGGING] - DEBUG");
        System.out.println("[KERNEL][LOGGING] - TRACE");
        throw new KernelException("[KERNEL][BOOTING] Logger level invalid -> " + CONFIG.kernelLoggingLevel);
      }

      LoggerXFactory.setLevel(loggerXLevel);

    }

    System.out.println("[KERNEL][LOGGING]日志等级 - " + LoggerXFactory.getLevel());

    //= ========================================================================
    //= 日志全名

    if (CONFIG.kernelLoggingFullname) {
      LoggerXFactory.setEnableFullName(true);
    }

    System.out.println("[KERNEL][LOGGING]日志全名 - " + (LoggerXFactory.isEnableFullName() ? "开启" : "关闭"));

    //= ========================================================================
    //= 日志全写

    if (CONFIG.kernelLoggingWriteall) {
      LoggerXFactory.setEnableWriteAll(true);
    }

    System.out.println("[KERNEL][LOGGING]日志全写 - " + (LoggerXFactory.isEnableFullName() ? "开启" : "关闭"));

    //= ========================================================================
    //= 创建日志

    if (LoggerXFactory.needLoggerFile()) {

      String name = FORMATTER.format(Instant.ofEpochMilli(BOOT_TIME)) + ".txt";
      FILE_LOGGER = FileEnhance.get(FOLDER_LOGGER, name);
      KernelException.check("[KERNEL][LOGGING]日志后端 - 日志文件初始化失败 -> ", FileEnhance.ensureFileSafe(FILE_LOGGER));

      try {
        LoggerXFactory.initLoggerFile(FILE_LOGGER);
      } catch (NoSuchMethodException | IllegalAccessException exception) {
        throw new KernelException("[KERNEL][LOGGING]日志后端 - 标记为需要日志文件的后端必须实现public void init(Path)方法 -> " + FILE_LOGGER, exception);
      } catch (InvocationTargetException exception) {
        throw new KernelException("[KERNEL][LOGGING]日志后端 - 后端执行public void init(Path)方法时发生异常 -> " + FILE_LOGGER, exception);
      }

      System.out.println("[KERNEL][LOGGING]日志文件 - 日志路径 -> " + name);

    }

    //= ================================================================================================================
    //= 控制台接管
    //= ================================================================================================================

    System.out.println("[KERNEL][BOOTING]内核初始化完成");
    System.out.println("[KERNEL][BOOTING]内核接管控制台");

    logger = LoggerXFactory.getLogger("SYSTEM");

    logger.hint(">> FURRYBLACK 内核接管成功");

    //= ================================================================================================================
    //= 模板消息子系统
    //= ================================================================================================================

    logger.hint("加载内置消息");

    {

      Path FILE_EULA = FileEnhance.get(FOLDER_CONFIG, "message_eula.txt");
      Path FILE_INFO = FileEnhance.get(FOLDER_CONFIG, "message_info.txt");
      Path FILE_HELP = FileEnhance.get(FOLDER_CONFIG, "message_help.txt");

      logger.info("加载 EULA 文件 -> " + FILE_EULA);

      MESSAGE_EULA = FileEnhance.read(FILE_EULA)
        .replace("APP_VERSION", APP_VERSION)
        .replace("MIRAI_VERSION", MIRAI_VERSION)
        .replace("OVERFLOW_VERSION", OVERFLOW_VERSION);

      logger.info("加载 INFO 文件 -> " + FILE_INFO);

      MESSAGE_INFO = FileEnhance.read(FILE_INFO)
        .replace("APP_VERSION", APP_VERSION)
        .replace("MIRAI_VERSION", MIRAI_VERSION)
        .replace("OVERFLOW_VERSION", OVERFLOW_VERSION);

      logger.info("加载 HELP 文件 -> " + FILE_HELP);

      MESSAGE_HELP = FileEnhance.read(FILE_HELP)
        .replace("APP_VERSION", APP_VERSION)
        .replace("MIRAI_VERSION", MIRAI_VERSION)
        .replace("OVERFLOW_VERSION", OVERFLOW_VERSION);

    }

    //= ================================================================================================================
    //= 昵称子系统
    //= ================================================================================================================

    NICKNAME = Nickname.getInstance();

    logger.hint("加载常用昵称");

    NICKNAME.cleanNickname();
    NICKNAME.appendNickname();

    //= ================================================================================================================
    //= 机器人子系统
    //= ================================================================================================================

    logger.info("初始化机器人");

    BotBuilder botBuilder = switch (CONFIG.onebotMode) {
      case POSITIVE -> BotBuilder.positive(CONFIG.onebotPositiveServer);
      case REVERSED -> BotBuilder.reversed(CONFIG.onebotReversedListen);
    };

    botBuilder.overrideLogger(new Slf4jLoggerX("ONEBOT"));

    //= ========================================================================
    //= 订阅客户端事件

    logger.info("订阅客户端事件");

    Pattern pattern = Pattern.compile(CONFIG.moduleRegex);

    Listener<UserMessageEvent> userMessageEventListener = GlobalEventChannel.INSTANCE.subscribeAlways(UserMessageEvent.class, event -> {

      if (!STATE_ACCEPT_EVENT) return;

      try {

        for (EventHandlerFilter it : SCHEMA.getFilterUsersChain()) {
          if (!it.isEnable()) continue;
          if (it.handleUsersMessageWrapper(event)) return;
        }

        MONITOR_PROCESS.submit(() -> {
          for (EventHandlerMonitor it : SCHEMA.getMonitorUsersChain()) {
            if (!it.isEnable()) continue;
            it.handleUsersMessageWrapper(event);
          }
        });

        String content = event.getMessage().contentToString();

        if (pattern.matcher(content).find()) {

          Command command = new Command(content.substring(1));
          String commandName = command.getCommandName();

          switch (commandName) {

            case "info" -> FurryBlack.sendMessage(event, MESSAGE_INFO);
            case "eula" -> FurryBlack.sendMessage(event, MESSAGE_EULA);
            case "list" -> FurryBlack.sendMessage(event, MESSAGE_LIST_USERS);

            case "help" -> {
              if (command.hasCommandBody()) {
                String segment = command.getParameterSegment(0);
                EventHandlerExecutor executor = SCHEMA.getExecutorUsersPool().get(segment);
                if (executor == null) {
                  FurryBlack.sendMessage(event, "没有此命令");
                } else {
                  FurryBlack.sendMessage(event, executor.getHelp());
                }
              } else {
                FurryBlack.sendMessage(event, MESSAGE_HELP);
              }
            }

            default -> {
              EventHandlerExecutor executor = SCHEMA.getExecutorUsersPool().get(commandName);
              if (executor == null) return;
              if (!executor.isEnable()) return;
              for (EventHandlerChecker checker : SCHEMA.getGlobalCheckerUsersPool()) {
                if (!checker.isEnable()) continue;
                if (checker.handleUsersMessageWrapper(event, command)) return;
              }
              List<EventHandlerChecker> commandCheckerUsersPool = SCHEMA.getCommandCheckerUsersPool(commandName);
              if (commandCheckerUsersPool != null) {
                for (EventHandlerChecker checker : commandCheckerUsersPool) {
                  if (!checker.isEnable()) continue;
                  if (checker.handleUsersMessageWrapper(event, command)) return;
                }
              }
              executor.handleUsersMessageWrapper(event, command);
            }
          }
        }

      } catch (Exception exception) {
        logger.warn("处理私聊消息异常", exception);
      }
    });

    Listener<GroupMessageEvent> groupMessageEventListener = GlobalEventChannel.INSTANCE.subscribeAlways(GroupMessageEvent.class, event -> {

      if (!STATE_ACCEPT_EVENT) return;

      try {

        for (EventHandlerFilter it : SCHEMA.getFilterGroupChain()) {
          if (!it.isEnable()) continue;
          if (it.handleGroupMessageWrapper(event)) return;
        }

        MONITOR_PROCESS.submit(() -> {
          for (EventHandlerMonitor it : SCHEMA.getMonitorGroupChain()) {
            if (!it.isEnable()) continue;
            it.handleGroupMessageWrapper(event);
          }
        });

        String content = event.getMessage().contentToString();

        if (pattern.matcher(content).find()) {

          Command command = new Command(content.substring(1));
          String commandName = command.getCommandName();

          switch (commandName) {

            case "help" -> {
              if (command.hasCommandBody()) {
                String segment = command.getParameterSegment(0);
                EventHandlerExecutor executor = SCHEMA.getExecutorGroupPool().get(segment);
                if (executor == null) {
                  FurryBlack.sendMessage(event, "没有此命令");
                } else {
                  try {
                    FurryBlack.sendMessage(event, executor.getHelp());
                  } catch (Exception exception) {
                    FurryBlack.sendMessage(event, "帮助信息发送至私聊失败 请允许临时会话权限");
                  }
                }
              } else {
                try {
                  event.getSender().sendMessage(MESSAGE_HELP);
                } catch (Exception exception) {
                  FurryBlack.sendMessage(event, "帮助信息发送至私聊失败 请允许临时会话权限");
                }
              }
            }

            case "list" -> {
              try {
                event.getSender().sendMessage(MESSAGE_LIST_GROUP);
              } catch (Exception exception) {
                FurryBlack.sendMessage(event, "可用命令发送至私聊失败 请允许临时会话权限");
              }
            }

            case "info" -> {
              try {
                event.getSender().sendMessage(MESSAGE_INFO);
              } catch (Exception exception) {
                FurryBlack.sendMessage(event, "关于发送至私聊失败 请允许临时会话权限");
              }
            }

            case "eula" -> {
              try {
                event.getSender().sendMessage(MESSAGE_EULA);
              } catch (Exception exception) {
                FurryBlack.sendMessage(event, "EULA发送至私聊失败 请允许临时会话权限");
              }
            }

            default -> {
              EventHandlerExecutor executor = SCHEMA.getExecutorGroupPool().get(commandName);
              if (executor == null) return;
              if (!executor.isEnable()) return;
              for (EventHandlerChecker checker : SCHEMA.getGlobalCheckerGroupPool()) {
                if (!checker.isEnable()) continue;
                if (checker.handleGroupMessageWrapper(event, command)) return;
              }
              List<EventHandlerChecker> commandCheckerGroupPool = SCHEMA.getCommandCheckerGroupPool(commandName);
              if (commandCheckerGroupPool != null) {
                for (EventHandlerChecker checker : commandCheckerGroupPool) {
                  if (!checker.isEnable()) continue;
                  if (checker.handleGroupMessageWrapper(event, command)) return;
                }
              }
              executor.handleGroupMessageWrapper(event, command);
            }
          }
        }

      } catch (Exception exception) {
        logger.warn("处理群聊消息异常", exception);
      }
    });

    Listener<NewFriendRequestEvent> newFriendRequestEventListener = GlobalEventChannel.INSTANCE.subscribeAlways(NewFriendRequestEvent.class, event -> {
      logger.hint("BOT被添加好友 " + event.getFromNick() + "(" + event.getFromId() + ")");
      event.accept();
    });

    Listener<BotInvitedJoinGroupRequestEvent> botInvitedJoinGroupRequestEventListener = GlobalEventChannel.INSTANCE.subscribeAlways(BotInvitedJoinGroupRequestEvent.class, event -> {
      logger.hint("BOT被邀请入群 " + event.getGroupName() + "(" + event.getGroupId() + ") 邀请人 " + event.getInvitorNick() + "(" + event.getInvitorId() + ")");
      event.accept();
    });

    Listener<MemberJoinEvent> memberJoinEventListener = GlobalEventChannel.INSTANCE.subscribeAlways(MemberJoinEvent.class, event -> {
      String user = event.getUser().getNick() + "(" + event.getUser().getId() + ")";
      if (event instanceof MemberJoinEvent.Active) {
        logger.hint("用户申请加群 " + user + " → " + event.getGroup().getName() + "(" + event.getGroupId() + ")");
      } else if (event instanceof MemberJoinEvent.Invite) {
        logger.hint("用户受邀进群 " + user + " → " + event.getGroup().getName() + "(" + event.getGroupId() + ")");
      }
    });

    Listener<MemberLeaveEvent> memberLeaveEventListener = GlobalEventChannel.INSTANCE.subscribeAlways(MemberLeaveEvent.class, event -> {
      String user = event.getUser().getNick() + "(" + event.getUser().getId() + ")";
      if (event instanceof MemberLeaveEvent.Quit) {
        logger.hint("用户主动退群 " + user + " → " + event.getGroup().getName() + "(" + event.getGroupId() + ")");
      } else if (event instanceof MemberLeaveEvent.Kick) {
        logger.hint("用户被踢出群 " + user + " → " + event.getGroup().getName() + "(" + event.getGroupId() + ")");
      }
    });

    //= ================================================================================================================
    //= 插件子系统
    //= ================================================================================================================

    SCHEMA = new Schema(FOLDER_PLUGIN);

    //= ========================================================================
    // 扫描插件

    SCHEMA.scanPlugin();

    //= ========================================================================
    // 扫描模块

    SCHEMA.scanModule();

    //= ========================================================================
    // 注册模块

    SCHEMA.loadModule();

    //= ========================================================================
    // 创建模块

    SCHEMA.makeModule();

    //= ========================================================================
    // 执行初始化方法

    SCHEMA.initModule();

    //= ================================================================================================================
    //= 机器人子系统
    //= ================================================================================================================

    //= ========================================================================
    //= 登录机器人

    if (CONFIG.systemDebugNologin) {
      logger.warn("跳过登录");
    } else {
      logger.hint("框架登录");
      BOT = botBuilder.connect();
      if (BOT == null) {
        logger.fatal("登录失败");
        throw new KernelException("[KERNELE][BOOTING]登录失败");
      }
    }

    //= ================================================================================================================
    //= 插件子系统
    //= ================================================================================================================

    //= ========================================================================
    //= 启动线程池

    logger.hint("启动线程池");

    logger.info("启动监听器线程池");

    MONITOR_PROCESS = new ThreadPoolExecutor(
      CONFIG.threadMonitorSize,
      CONFIG.threadMonitorSizeMax,
      0L,
      TimeUnit.MILLISECONDS,
      new LinkedBlockingQueue<>()
    );

    logger.info("启动定时器线程池");

    SCHEDULE_SERVICE = new ScheduledThreadPoolExecutor(
      CPU_CORES,
      Executors.defaultThreadFactory(),
      (runnable, executor) -> {
        throw new KernelException("添加计划任务到线程池失败  " + runnable.toString() + " -> " + executor.toString());
      }
    );

    //= ========================================================================
    //= 启动模块

    SCHEMA.bootModule();

    //= ========================================================================
    //= 注册钩子

    Thread currentThread = Thread.currentThread();

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {

      LATCH.signal();

      if (STATE_SHUTDOWN_DROP) {
        logger.println("[FurryBlack][EXIT]FurryBlack normally close with drop, Execute drop now.");
        System.exit(1);
      }

      try {
        currentThread.join();
      } catch (InterruptedException exception) {
        logger.println("[FurryBlack][EXIT]Shutdown hook interrupted -> " + exception.getMessage());
      }

      logger.println("[FurryBlack][EXIT]FurryBlack normally closed, Bye.");

      if (CONFIG.systemDebugHalt) {
        logger.println("[FurryBlack][EXIT]FurryBlack normally close with halt, Execute halt now.");
        Runtime.getRuntime().halt(1);
      }

    }));

    //= ================================================================================================================
    //= 机器人子系统
    //= ================================================================================================================

    //= ========================================================================
    //= 列出所有好友和群组

    if (!CONFIG.systemDebugNologin) {

      logger.seek("机器人账号 " + BOT.getId());
      logger.seek("机器人昵称 " + BOT.getNick());
      logger.seek("机器人头像 " + BOT.getAvatarUrl());

      logger.hint("所有好友");
      BOT.getFriends().forEach(item -> logger.info(FurryBlack.getFormattedNickName(item)));

      logger.hint("所有群组");
      BOT.getGroups().forEach(item -> logger.info(FurryBlack.getGroupInfo(item)));

    }

    //= ================================================================================================================
    //= 插件子系统
    //= ================================================================================================================

    //= ========================================================================
    //= 生成模板消息

    logger.hint("生成模板消息");

    logger.info("组装用户list消息");
    MESSAGE_LIST_USERS = SCHEMA.generateUsersExecutorList();

    logger.info("组装群组list消息");
    MESSAGE_LIST_GROUP = SCHEMA.generateGroupExecutorList();

    //= ================================================================================================================
    //= 控制台子系统
    //= ================================================================================================================

    DISPATCHER = new Dispatcher();

    //= ========================================================================

    DISPATCHER.registerFunction()
      .command("info")
      .function(it -> {
        logger.println(CONTENT_INFO);
      });

    //= ========================================================================

    DISPATCHER.registerFunction()
      .command("help")
      .command("?")
      .function(it -> {
        logger.println(CONTENT_HELP);
      });

    //= ========================================================================

    DISPATCHER.registerFunction()
      .command("system", "status")
      .command("status")
      .command("gc")
      .function(it -> {

        long totalMemory = Runtime.getRuntime().totalMemory();
        long freeMemory = Runtime.getRuntime().freeMemory();
        long maxMemory = Runtime.getRuntime().maxMemory();
        long useMemory = totalMemory - freeMemory;

        String totalMemoryH = toHumanBytes(totalMemory);
        String freeMemoryH = toHumanBytes(freeMemory);
        String maxMemoryH = toHumanBytes(maxMemory);
        String useMemoryH = toHumanBytes(useMemory);

        StringBuilder builder = new StringBuilder(256);

        builder.append("运行环境/物理线程 - " + CPU_CORES).append(LINE);
        builder.append("运行环境/系统架构 - " + OS_ARCH).append(LINE);
        builder.append("运行环境/系统类型 - " + OS_NAME).append(LINE);
        builder.append("运行环境/系统版本 - " + OS_VERSION).append(LINE);

        builder.append("运行环境/系统区域 - " + USER_COUNTRY).append(LINE);
        builder.append("运行环境/系统语言 - " + USER_LANGUAGE).append(LINE);
        builder.append("运行环境/语言变种 - " + USER_VARIANT).append(LINE);
        builder.append("运行环境/系统时区 - " + USER_TIMEZONE).append(LINE);

        builder.append("运行状态/工作目录 - " + FOLDER_ROOT).append(LINE);
        builder.append("运行状态/插件目录 - " + FOLDER_PLUGIN).append(LINE);
        builder.append("运行状态/依赖目录 - " + FOLDER_DEPEND).append(LINE);
        builder.append("运行状态/数据目录 - " + FOLDER_MODULE).append(LINE);
        builder.append("运行状态/日志目录 - " + FOLDER_LOGGER).append(LINE);
        builder.append("运行状态/日志文件 - " + FILE_LOGGER).append(LINE);

        builder.append("运行状态/启动时间 - " + TimeEnhance.datetime(BOOT_TIME)).append(LINE);
        builder.append("运行状态/运行时间 - " + TimeEnhance.duration(System.currentTimeMillis() - BOOT_TIME)).append(LINE);

        builder.append("运行状态/最大内存 - " + maxMemoryH + "/" + maxMemory).append(LINE);
        builder.append("运行状态/已用内存 - " + useMemoryH + "/" + useMemory).append(LINE);
        builder.append("运行状态/空闲内存 - " + freeMemoryH + "/" + freeMemory).append(LINE);
        builder.append("运行状态/分配内存 - " + totalMemoryH + "/" + totalMemory).append(LINE);

        builder.append("内核状态/日志后端 - " + LoggerXFactory.getDefault().getSimpleName()).append(LINE);
        builder.append("内核状态/日志级别 - " + LoggerXFactory.getLevel().name()).append(LINE);
        builder.append("内核状态/日志前缀 - " + (LoggerXFactory.isEnablePrefix() ? "开启" : "关闭")).append(LINE);
        builder.append("内核状态/日志落盘 - " + (LoggerXFactory.needLoggerFile() ? "开启" : "关闭")).append(LINE);
        builder.append("内核状态/日志全名 - " + (LoggerXFactory.isEnableFullName() ? "开启" : "关闭")).append(LINE);
        builder.append("内核状态/日志全写 - " + (LoggerXFactory.isEnableWriteAll() ? "开启" : "关闭")).append(LINE);

        builder.append("调试模式/调试模式 - " + (STATE_SYSTEM_DEBUG ? "开启" : "关闭")).append(LINE);
        builder.append("调试模式/强制退出 - " + (STATE_SYSTEM_DEBUG_HALT ? "开启" : "关闭")).append(LINE);
        builder.append("调试模式/内部接口 - " + (STATE_SYSTEM_DEBUG_UNSAFE ? "开启" : "关闭")).append(LINE);
        builder.append("调试模式/跳过登录 - " + (CONFIG.systemDebugNologin ? "开启" : "关闭")).append(LINE);

        builder.append("事件总线/开启响应 - " + (STATE_ACCEPT_EVENT ? "开启" : "关闭")).append(LINE);

        logger.println(builder.toString());

      });

    DISPATCHER.registerFunction()
      .command("system", "stacks")
      .function(it -> {

        Map<Thread, StackTraceElement[]> stackTraces = Thread.getAllStackTraces();

        ArrayList<Map.Entry<Thread, StackTraceElement[]>> entries = new ArrayList<>(stackTraces.entrySet());

        entries.sort((o1, o2) -> {
          if (o1 == o2) return 0;
          Thread o1Key = o1.getKey();
          Thread o2Key = o2.getKey();
          return (int) (o1Key.threadId() - o2Key.threadId());
        });

        for (Map.Entry<Thread, StackTraceElement[]> entry : entries) {
          var k = entry.getKey();
          var v = entry.getValue();
          StringBuilder builder = new StringBuilder();
          if (k.isDaemon()) {
            builder.append("Daemon-");
          } else {
            builder.append("Thread-");
          }
          builder.append(k.threadId() + " " + k.getState());
          builder.append(" (" + k.getName() + ") " + k.getPriority());
          builder.append(" [" + k.getThreadGroup().getName() + "]").append(LINE);
          for (StackTraceElement element : v) {
            builder.append("    " + element.getClassName() + ":" + element.getMethodName() + "(" + element.getLineNumber() + ")").append(LINE);
          }
          logger.println(builder.toString());
        }
      });

    //= ========================================================================

    DISPATCHER.registerExclusive()
      .command("system", "debug")
      .function(it -> {
        if (it == null) {
          logger.println("DEBUG模式 -> " + (STATE_SYSTEM_DEBUG ? "已开启" : "已关闭"));
        } else {
          switch (it.getString(0, "")) {
            case "enable" -> {
              STATE_SYSTEM_DEBUG = true;
              logger.println("[SYSTEM][RUNTIME] DEBUG模式: 启动");
            }
            case "disable" -> {
              STATE_SYSTEM_DEBUG = false;
              logger.println("[SYSTEM][RUNTIME] DEBUG模式: 关闭");
            }
            default -> logger.println("USAGE: system debug enable|disable");
          }
        }
      });

    //= ========================================================================

    DISPATCHER.registerFunction()
      .command("system", "power-off")
      .command("exit")
      .command("quit")
      .command("stop")
      .function(it -> {
        logger.println(YELLOW + "CONSOLE invoke -> shutdown" + RESET);
        Runtime.getRuntime().exit(0);
      });

    //= ========================================================================

    DISPATCHER.registerFunction()
      .command("system", "rapid-stop")
      .function(it -> {
        STATE_SYSTEM_DEBUG_HALT = true;
        logger.println(RED + """
          ⚠ !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ⚠
          ⚠ WARNING WARNING WARNING WARNING WARNING ⚠
          ⚠                                         ⚠
          ⚠   This command will skip all waiting    ⚠
          ⚠     It is not good for your health      ⚠
          ⚠       Wish we can see you again         ⚠
          ⚠                                         ⚠
          ⚠ WARNING WARNING WARNING WARNING WARNING ⚠
          ⚠ !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ⚠
          """);
        Runtime.getRuntime().exit(0);
      });

    //= ========================================================================

    DISPATCHER.registerFunction()
      .command("system", "force-exit")
      .function(command -> {
        if (STATE_SHUTDOWN_DROP) {
          logger.println(RED + """
            💀 !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! 💀
            💀 FATAL FATAL FATAL FATAL FATAL FATAL 💀
            💀                                     💀
            💀         Intention confirmed         💀
            💀       There is no turning back      💀
            💀      JVM will be termination now    💀
            💀                                     💀
            💀 FATAL FATAL FATAL FATAL FATAL FATAL 💀
            💀 !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! 💀
            """ + GREEN +
            ">>>> Invoke Now -> Runtime.getRuntime().halt(1)" + RESET
          );
          logger.error(">> CONSOLE confirm force-exite");
          Runtime.getRuntime().halt(1);
        } else {
          STATE_SHUTDOWN_DROP = true;
          logger.println(RED + """
            ⚠ !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ⚠
            ⚠ WARNING WARNING WARNING WARNING WARNING ⚠
            ⚠                                         ⚠
            ⚠   This command will kill JVM directly   ⚠
            ⚠   Input it again to confirm intention   ⚠
            ⚠                                         ⚠
            ⚠ WARNING WARNING WARNING WARNING WARNING ⚠
            ⚠ !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ⚠
            """);
          logger.error(">> CONSOLE require force-exite");
        }
      });

    //= ========================================================================

    DISPATCHER.registerFunction()
      .command("kill")
      .function(command -> {
        logger.println(RED + """
          💀 !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! 💀
          💀 FATAL FATAL FATAL FATAL FATAL FATAL 💀
          💀                                     💀
          💀        Directly kill invoking       💀
          💀       There is no turning back      💀
          💀      JVM will be termination now    💀
          💀                                     💀
          💀 FATAL FATAL FATAL FATAL FATAL FATAL 💀
          💀 !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! 💀
          """ + GREEN +
          ">>>> Runtime.getRuntime().halt(1)" + RESET
        );
        logger.error(">> CONSOLE invoke kill");
        Runtime.getRuntime().halt(1);
      });

    //= ========================================================================

    DISPATCHER.registerFunction()
      .command("logger", "color")
      .command("color")
      .function(it ->
        logger.println(CONTENT_COLOR)
      );

    //= ========================================================================

    DISPATCHER.registerExclusive()
      .command("logger", "level")
      .function(it -> {

        if (it == null) {
          logger.println("当前日志级别 -> " + LoggerXFactory.getLevel());
        } else {
          String target = it.getString(0, "IMPOSSIBLE");
          LoggerXLevel of = LoggerXLevel.of(target);
          if (of == null) {
            logger.println("日志级别不可用 -> " + target);
            logger.println(

              // @formatter:off

              "可用日志级别为: " + LINE +
              "TRACE = TRACE "  + LINE +
              "DEBUG = DEBUG "  + LINE +
              "INFO  = HINT + SEEK + INFO"  + LINE +
              "WARN  = WARN"   + LINE +
              "ERROR = ERROR + FATAL"  + LINE +
              "CLOSE = N/A"

              // @formatter:on

            );
          } else {
            LoggerXFactory.setLevel(of);
            logger.println("日志级别修改为 -> " + LoggerXFactory.getLevel());
          }
        }
      });

    //= ========================================================================

    DISPATCHER.registerExclusive()
      .command("logger", "fullname")
      .function(it -> {
        if (it == null) {
          logger.println("当前详细名称 -> " + LoggerXFactory.isEnableFullName());
        } else {
          if (it.getBoolean(0, false)) {
            LoggerXFactory.setEnableFullName(true);
            logger.println("设置详细名称为 -> 开启");
          } else {
            LoggerXFactory.setEnableFullName(false);
            logger.println("设置详细名称为 -> 关闭");
          }
        }
      });

    //= ========================================================================

    DISPATCHER.registerExclusive()
      .command("logger", "prefix")
      .function(it -> {
        if (it == null) {
          Map<String, LoggerXLevel> prefix = LoggerXFactory.listPrefix();
          logger.println("当前级别前缀 -> " + prefix.size());
          for (Map.Entry<String, LoggerXLevel> entry : prefix.entrySet()) {
            logger.println(String.format("%5s", entry.getValue()) + " " + entry.getKey());
          }
        } else {
          String code = it.getString(0, null);
          String path = it.getString(1, null);
          if (code == null || path == null) {
            logger.println("前缀格式无效 -> 输入为空");
          } else {
            switch (code) {
              case "test" -> logger.println(path + " -> " + LoggerXFactory.testPrefix(path));
              case "cache" -> LoggerXFactory.listPrefixCache().forEach((k, v) -> logger.println(k + " " + v.name()));
              case "flush" -> LoggerXFactory.flushPrefixCache();
              case "delete", "remove" -> LoggerXFactory.delPrefix(path);
              default -> {
                LoggerXLevel of = LoggerXLevel.of(code);
                LoggerXFactory.setPrefix(path, of);
              }
            }
          }
        }
      });

    //= ========================================================================

    DISPATCHER.registerFunction()
      .command("schema")
      .function(it -> logger.println(SCHEMA.verboseStatus()));

    //= ========================================================================

    DISPATCHER.registerExclusive()
      .command("schema", "event")
      .function(it -> {
        if (it == null) {
          logger.println("SCHEMA模式, 是否响应消息事件 -> " + (STATE_ACCEPT_EVENT ? "开启" : "关闭"));
        } else {
          switch (it.getString(0, "IMPOSSIBLE")) {
            case "enable" -> {
              STATE_ACCEPT_EVENT = true;
              logger.println("SCHEMA模式: 启动");
            }
            case "disable" -> {
              STATE_ACCEPT_EVENT = false;
              logger.println("SCHEMA模式: 关闭");
            }
            default -> logger.println("USAGE: system debug enable|disable");
          }
        }
      });

    //= ========================================================================

    DISPATCHER.registerExclusive()
      .command("schema", "plugin")
      .function(it -> {

        StringEnhance.LineBuilder builder = new StringEnhance.LineBuilder();

        for (Map.Entry<String, Schema.Plugin> pluginEntry : SCHEMA.getAllPlugin()) {

          var pluginName = pluginEntry.getKey();
          var pluginItem = pluginEntry.getValue();

          builder.append(BRIGHT_CYAN + pluginName + " " + pluginItem.getModules().size() + RESET);

          Map<Runner, Class<? extends EventHandlerRunner>> runnerClassMap = pluginItem.getRunnerClassMap();
          builder.append(GREEN + ">> 定时器 " + runnerClassMap.size() + RESET);
          for (Map.Entry<Runner, Class<? extends EventHandlerRunner>> classEntry : runnerClassMap.entrySet()) {
            var moduleName = classEntry.getKey();
            var moduleItem = classEntry.getValue();
            builder.append(moduleName.value() + '[' + moduleName.priority() + "] -> " + moduleItem.getName());
          }

          Map<Filter, Class<? extends EventHandlerFilter>> filterClassMap = pluginItem.getFilterClassMap();
          builder.append(GREEN + ">> 过滤器 " + filterClassMap.size() + RESET);
          for (Map.Entry<Filter, Class<? extends EventHandlerFilter>> classEntry : filterClassMap.entrySet()) {
            var moduleName = classEntry.getKey();
            var moduleItem = classEntry.getValue();
            builder.append(moduleName.value() + '[' + moduleName.priority() + "] -> " + moduleItem.getName());
          }

          Map<Monitor, Class<? extends EventHandlerMonitor>> monitorClassMap = pluginItem.getMonitorClassMap();
          builder.append(GREEN + ">> 监听器 " + monitorClassMap.size() + RESET);
          for (Map.Entry<Monitor, Class<? extends EventHandlerMonitor>> classEntry : monitorClassMap.entrySet()) {
            var moduleName = classEntry.getKey();
            var moduleItem = classEntry.getValue();
            builder.append(moduleName.value() + '[' + moduleName.priority() + "] -> " + moduleItem.getName());
          }

          Map<Checker, Class<? extends EventHandlerChecker>> checkerClassMap = pluginItem.getCheckerClassMap();
          builder.append(GREEN + ">> 检查器 " + checkerClassMap.size() + RESET);
          for (Map.Entry<Checker, Class<? extends EventHandlerChecker>> classEntry : checkerClassMap.entrySet()) {
            var moduleName = classEntry.getKey();
            var moduleItem = classEntry.getValue();
            builder.append(moduleName.value() + '[' + moduleName.priority() + "](" + moduleName.command() + ") -> " + moduleItem.getName());
          }

          Map<Executor, Class<? extends EventHandlerExecutor>> executorClassMap = pluginItem.getExecutorClassMap();
          builder.append(GREEN + ">> 执行器 " + executorClassMap.size() + RESET);
          for (Map.Entry<Executor, Class<? extends EventHandlerExecutor>> classEntry : executorClassMap.entrySet()) {
            var moduleName = classEntry.getKey();
            var moduleItem = classEntry.getValue();
            builder.append(moduleName.value() + '(' + moduleName.command() + ") -> " + moduleItem.getName());
          }
        }

        logger.println(builder.toString());

      });

    //= ========================================================================

    DISPATCHER.registerExclusive()
      .command("schema", "module")
      .function(it -> {

        if (it == null) {
          StringEnhance.LineBuilder builder = new StringEnhance.LineBuilder();
          Map<Runner, Boolean> listRunner = SCHEMA.listRunner();
          builder.append(BRIGHT_CYAN + ">> 定时器 " + listRunner.size() + RESET);
          for (Map.Entry<Runner, Boolean> entry : listRunner.entrySet()) {
            builder.append((entry.getValue() ? "开 " : "关 ") + entry.getKey().value());
          }
          Map<Filter, Boolean> listFilter = SCHEMA.listFilter();
          builder.append(BRIGHT_CYAN + ">> 过滤器 " + listFilter.size() + RESET);
          for (Map.Entry<Filter, Boolean> entry : listFilter.entrySet()) {
            builder.append((entry.getValue() ? "开 " : "关 ") + entry.getKey().value() + "{" + (entry.getKey().users() ? "U" : "") + (entry.getKey().group() ? "G" : "") + "}");
          }
          Map<Monitor, Boolean> listMonitor = SCHEMA.listMonitor();
          builder.append(BRIGHT_CYAN + ">> 监听器 " + listMonitor.size() + RESET);
          for (Map.Entry<Monitor, Boolean> entry : listMonitor.entrySet()) {
            builder.append((entry.getValue() ? "开 " : "关 ") + entry.getKey().value() + "{" + (entry.getKey().users() ? "U" : "") + (entry.getKey().group() ? "G" : "") + "}");
          }
          Map<Checker, Boolean> listChecker = SCHEMA.listChecker();
          builder.append(BRIGHT_CYAN + ">> 检查器 " + listChecker.size() + RESET);
          for (Map.Entry<Checker, Boolean> entry : listChecker.entrySet()) {
            builder.append((entry.getValue() ? "开 " : "关 ") + entry.getKey().value() + "[" + entry.getKey().command() + "]" + "{" + (entry.getKey().users() ? "U" : "") + (entry.getKey().group() ? "G" : "") + "}");
          }
          Map<Executor, Boolean> listExecutor = SCHEMA.listExecutor();
          builder.append(BRIGHT_CYAN + ">> 执行器 " + listExecutor.size() + RESET);
          for (Map.Entry<Executor, Boolean> entry : listExecutor.entrySet()) {
            builder.append((entry.getValue() ? "开 " : "关 ") + entry.getKey().value() + "[" + entry.getKey().command() + "]{" + (entry.getKey().users() ? "U" : "") + (entry.getKey().group() ? "G" : "") + "}");
          }
          List<Checker> globalUsersChecker = SCHEMA.listGlobalUsersChecker();
          builder.append(BRIGHT_CYAN + ">> 全局私聊检查器 " + globalUsersChecker.size() + RESET);
          for (Checker annotation : globalUsersChecker) {
            builder.append(annotation.value());
          }
          List<Checker> globalGroupChecker = SCHEMA.listGlobalGroupChecker();
          builder.append(BRIGHT_CYAN + ">> 全局群聊检查器 " + globalGroupChecker.size() + RESET);
          for (Checker annotation : globalGroupChecker) {
            builder.append("  " + annotation.value());
          }
          Map<String, List<Checker>> listCommandUsersChecker = SCHEMA.listCommandsUsersChecker();
          builder.append(BRIGHT_CYAN + ">> 有限私聊检查器 " + listCommandUsersChecker.size() + RESET);
          for (Map.Entry<String, List<Checker>> entry : listCommandUsersChecker.entrySet()) {
            builder.append(entry.getKey() + " " + entry.getValue().size());
            for (Checker item : entry.getValue()) {
              builder.append("  " + item.value());
            }
          }
          Map<String, List<Checker>> listCommandGroupChecker = SCHEMA.listCommandsGroupChecker();
          builder.append(BRIGHT_CYAN + ">> 有限群聊检查器 " + listCommandGroupChecker.size() + RESET);
          for (Map.Entry<String, List<Checker>> entry : listCommandGroupChecker.entrySet()) {
            builder.append(entry.getKey() + " " + entry.getValue().size());
            for (Checker item : entry.getValue()) {
              builder.append("  " + item.value());
            }
          }

          builder.append(BRIGHT_CYAN + ">> 私聊命令列表" + RESET);
          builder.append(MESSAGE_LIST_USERS);
          builder.append(BRIGHT_CYAN + ">> 群聊命令列表" + RESET);
          builder.append(MESSAGE_LIST_GROUP);

          logger.println(builder.toString());

        } else {

          String type = it.getString(0, null);
          String name = it.getString(1, null);

          if (type == null || name == null) {
            logger.println("USAGE: schema module init|boot|shut|reboot|unload|execute <name>");
            return;
          }

          switch (type) {
            case "init" -> SCHEMA.initModule(name);
            case "boot" -> SCHEMA.bootModule(name);
            case "shut" -> SCHEMA.shutModule(name);
            case "reboot" -> SCHEMA.rebootModule(name);
            case "unload" -> SCHEMA.unloadModule(name);
            case "execute" -> SCHEMA.executeModule(name, it.toModuleCommand(2));
            default -> logger.println("USAGE: schema module init|boot|shut|reboot|unload|execute <name>");
          }
        }
      });

    //= ========================================================================

    DISPATCHER.registerExclusive()
      .command("exec")
      .command("execute")
      .function(it -> {
        String string = it.getString(0, null);
        if (string == null) {
          logger.println("USAGE: exec|execute <name> xxx xxx xxx ...");
          return;
        }
        boolean code = SCHEMA.executeModule(string, it.toModuleCommand(1));
        if (!code) {
          logger.println("ERROR: 指定模块不存在 -> " + string);
        }
      });

    //= ========================================================================

    DISPATCHER.registerFunction()
      .command("nickname")
      .function(it -> logger.println("USAGE: nickname list|load|clean|reload|export"));

    //= ========================================================================

    DISPATCHER.registerExclusive()
      .command("nickname", "list")
      .function(it -> {
        logger.println(BRIGHT_CYAN + "全局昵称 " + NICKNAME.getNicknameGlobal().size() + RESET);
        for (Map.Entry<Long, String> entry : NICKNAME.getNicknameGlobal().entrySet()) {
          logger.println(entry.getKey() + ":" + entry.getValue());
        }
        logger.println(BRIGHT_CYAN + "群内昵称 " + NICKNAME.getNicknameGroups().size() + RESET);
        for (Map.Entry<Long, Map<Long, String>> groupsEntry : NICKNAME.getNicknameGroups().entrySet()) {
          logger.println("> " + groupsEntry.getKey());
          for (Map.Entry<Long, String> nicknameEntry : groupsEntry.getValue().entrySet()) {
            logger.println(nicknameEntry.getKey() + ":" + nicknameEntry.getValue());
          }
        }
      });

    //= ========================================================================

    DISPATCHER.registerExclusive()
      .command("nickname", "clean")
      .function(it -> {
        NICKNAME.cleanNickname();
        logger.println("昵称已清空");
      });

    //= ========================================================================

    DISPATCHER.registerExclusive()
      .command("nickname", "append")
      .function(it -> {
        NICKNAME.appendNickname();
        logger.println("昵称已续加");
      });

    //= ========================================================================

    DISPATCHER.registerExclusive()
      .command("nickname", "reload")
      .function(it -> {
        NICKNAME.cleanNickname();
        NICKNAME.appendNickname();
        logger.println("昵称已重载");
      });

    //= ========================================================================

    DISPATCHER.registerExclusive()
      .command("nickname", "export")
      .function(it -> {
        Path path = FileEnhance.get(FOLDER_CONFIG, "export-" + FORMATTER.format(Instant.now()) + ".txt");
        StringEnhance.LineBuilder builder = new StringEnhance.LineBuilder();
        ContactList<Friend> friends = getFriends();
        builder.append("# 好友 ", friends.size());
        for (Friend friend : friends) {
          builder.append("*.", friend.getId(), ":", friend.getNick());
        }
        ContactList<Group> groups = getGroups();
        builder.append("# 群组 ", groups.size());
        for (Group group : groups) {
          long groupId = group.getId();
          builder.append("# ", group.getName(), " ", group.getOwner().getId());
          for (NormalMember member : group.getMembers()) {
            String nameCard = member.getNameCard();
            if (nameCard.isEmpty()) {
              builder.append(groupId, ".", member.getId(), ":", member.getNick());
            } else {
              builder.append(groupId, ".", member.getId(), ":", member.getNick(), "[", nameCard, "]");
            }
          }
        }
        FileEnhance.write(path, builder.toString());
        logger.println("昵称已导出 -> " + path);
      });

    //= ========================================================================

    TERMINAL.updateCompleter();

    //= ========================================================================

    Thread consoleThread = new Thread(() -> {
      while (true) {
        String readLine = TERMINAL.readLine();
        if (readLine == null || readLine.isBlank()) {
          continue;
        }
        readLine = readLine.trim();
        try {
          boolean exist = DISPATCHER.execute(readLine);
          if (!exist) {
            logger.error("命令不存在 -> " + readLine);
          }
        } catch (Exception exception) {
          logger.error("执行命令发生错误 -> " + readLine, exception);
        }
      }
    });

    consoleThread.setName("furryblack-terminal");
    consoleThread.setDaemon(true);
    consoleThread.start();

    //= ================================================================================================================
    //= 启动完成
    //= ================================================================================================================

    STATE_BOOTING = false;
    STATE_ACCEPT_EVENT = true;

    logger.hint("系统启动完成 耗时" + TimeEnhance.duration(System.currentTimeMillis() - BOOT_TIME));

    //= ================================================================================================================
    //= 正常工作
    //= ================================================================================================================

    LATCH.await();

    //= ========================================================================
    //= 关闭事件响应

    STATE_ACCEPT_EVENT = false;

    //= ========================================================================
    //= 取消订阅

    logger.hint("结束监听通道");

    logger.info("结束私聊监听通道");
    userMessageEventListener.complete();

    logger.info("结束群聊监听通道");
    groupMessageEventListener.complete();

    logger.info("结束成员进群监听通道");
    memberJoinEventListener.complete();

    logger.info("结束成员离群监听通道");
    memberLeaveEventListener.complete();

    logger.info("结束好友添加监听通道");
    newFriendRequestEventListener.complete();

    logger.info("结束邀请加群监听通道");
    botInvitedJoinGroupRequestEventListener.complete();

    //= ========================================================================
    //= 关闭模块

    try {
      SCHEMA.shutModule();
    } catch (Exception exception) {
      logger.error("关闭插件模型发生异常", exception);
    }

    //= ========================================================================
    //= 关闭线程池

    logger.hint("关闭线程池");

    CompletableFuture<Void> monitorShutdown = CompletableFuture.runAsync(() -> {
      if (STATE_SHUTDOWN_DROP) {
        logger.warn("丢弃监听任务线程池");
        MONITOR_PROCESS.shutdownNow();
      } else {
        logger.info("关闭监听任务线程池");
        MONITOR_PROCESS.shutdown();
        try {
          boolean termination = MONITOR_PROCESS.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
          if (!termination)
            logger.warn("监听任务线程池关闭超时");
        } catch (InterruptedException exception) {
          logger.error("等待关闭监听任务线程池被中断", exception);
        }
        logger.info("监听任务线程池关闭");
      }
    });

    CompletableFuture<Void> scheduleShutdown = CompletableFuture.runAsync(() -> {
      if (STATE_SHUTDOWN_DROP) {
        logger.warn("丢弃定时任务线程池");
        SCHEDULE_SERVICE.shutdownNow();
      } else {
        logger.info("关闭定时任务线程池");
        SCHEDULE_SERVICE.shutdown();
        try {
          boolean termination = SCHEDULE_SERVICE.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
          if (!termination)
            logger.warn("定时任务线程池关闭超时");
        } catch (InterruptedException exception) {
          logger.error("等待关闭定时任务线程池被中断", exception);
        }
        logger.info("定时任务线程池关闭");
      }
    });

    try {
      CompletableFuture.allOf(monitorShutdown, scheduleShutdown).get();
    } catch (InterruptedException | ExecutionException exception) {
      logger.error("等待关闭线程池被中断", exception);
    }

    //= ========================================================================
    //= 关闭机器人

    logger.hint("关闭机器人");

    logger.info("通知机器人关闭");

    if (CONFIG.systemDebugNologin) {
      logger.warn("调试模式 不需要关闭机器人");
    } else {
      BOT.closeAndJoin(null);
    }

    logger.info("机器人已关闭");

  }

  //= MAIN END
  //= ==================================================================================================================
  //= ==================================================================================================================
  //= ==================================================================================================================
  //= ==================================================================================================================
  //= ==================================================================================================================
  //= ==================================================================================================================
  //= ==================================================================================================================
  //= ==================================================================================================================
  //= ==================================================================================================================

  //= ==================================================================================================================
  //=
  //= 终端子系统
  //=
  //= ==================================================================================================================

  //= ==================================================================================================================
  //=  终端系统
  //= ==================================================================================================================

  private abstract sealed static class Terminal permits NoConsoleTerminal, StdinTerminal, JlineTerminal {

    public static final String CONSOLE_PROMPT = "[console]$ ";

    //= ========================================================================
    //= 控制台终端

    String readLine() {
      return readLineImpl();
    }

    void print(String message) {
      printImpl(message);
    }

    void println(String message) {
      printLineImpl(message);
    }

    void updateCompleter() {
      updateCompleterImpl();
    }

    protected abstract String readLineImpl();

    protected abstract void printImpl(String message);

    protected abstract void printLineImpl(String message);

    protected abstract void updateCompleterImpl();

  }

  //= ==========================================================================
  //= NoConsoleTerminal

  private static final class NoConsoleTerminal extends Terminal {

    public static NoConsoleTerminal getInstance() {

      System.setIn(new InputStream() {
        @Override public int read() {return -1;}
      });

      return new NoConsoleTerminal();
    }

    private NoConsoleTerminal() {}

    @Override
    protected String readLineImpl() {
      try {
        Thread.sleep(Long.MAX_VALUE);
      } catch (InterruptedException exception) {
        throw new TerminalException(exception);
      }
      return null;
    }

    @Override
    protected synchronized void printImpl(String message) {
      System.out.print(message);
    }

    @Override
    protected void printLineImpl(String message) {
      FurryBlack.terminalPrintln(message + LINE);
    }

    @Override
    protected void updateCompleterImpl() {

    }
  }

  //= ==========================================================================
  //= StdinTerminal

  private static final class StdinTerminal extends Terminal {

    public static StdinTerminal getInstance() {
      return new StdinTerminal();
    }

    private final BufferedReader reader;
    private final OutputStreamWriter writer;

    private StdinTerminal() {
      InputStreamReader inputStreamReader = new InputStreamReader(System.in, StandardCharsets.UTF_8);
      reader = new BufferedReader(inputStreamReader);
      writer = new OutputStreamWriter(System.out, StandardCharsets.UTF_8);
    }

    @Override
    protected String readLineImpl() {
      try {
        return reader.readLine();
      } catch (IOException exception) {
        throw new TerminalException(exception);
      }
    }

    @Override
    protected synchronized void printImpl(String message) {
      try {
        writer.write(message);
        writer.flush();
      } catch (IOException exception) {
        throw new RuntimeException(exception);
      }
    }

    @Override
    protected void printLineImpl(String message) {
      printImpl(message + LINE);
    }

    @Override
    protected void updateCompleterImpl() {

    }
  }

  //= ==========================================================================
  //= JlineTerminal

  private static final class JlineTerminal extends Terminal {

    public static JlineTerminal getInstance() {
      return new JlineTerminal();
    }

    private final LineReader reader;
    private final CompleterDelegate completerDelegate;

    private JlineTerminal() {
      completerDelegate = new CompleterDelegate();
      reader = LineReaderBuilder.builder().completer(completerDelegate).build();
      AutopairWidgets autopairWidgets = new AutopairWidgets(reader);
      autopairWidgets.enable();
    }

    @Override
    protected String readLineImpl() {
      return reader.readLine(CONSOLE_PROMPT);
    }

    @Override
    protected synchronized void printImpl(String message) {
      reader.printAbove(message);
    }

    @Override
    protected void printLineImpl(String message) {
      printImpl(message + LINE);
    }

    @Override
    protected void updateCompleterImpl() {
      completerDelegate.update();
    }

    private static class CompleterDelegate implements Completer {

      private Completer completer;

      private CompleterDelegate() {}

      @Override
      public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
        completer.complete(reader, line, candidates);
      }

      private void update() {
        completer = buildCompleter();
      }

      private AggregateCompleter buildCompleter() {

        StringsCompleter stringsCompleter = new StringsCompleter(SCHEMA.listModuleName());

        return new AggregateCompleter(

          // system stacks
          // system status
          // system power-off
          // system rapid-stop
          // system force-exit
          new TreeCompleter(node("system", node("status", "stacks", "power-off", "rapid-stop", "force-exit"))),

          // system debug
          // system debug enable|disable
          new TreeCompleter(node("system", node("debug", node("enable", "disable")))),

          // schema plugin
          new TreeCompleter(node("schema", node("plugin"))),

          // schema module
          // schema module init xxx
          // schema module boot xxx
          // schema module shut xxx
          // schema module reboot xxx
          // schema module unload xxx
          // schema module execute xxx xxx xxx xxxx
          new TreeCompleter(node("schema", node("module", node("init", "boot", "shut", "reboot", "unload", "execute", node(stringsCompleter))))),

          // nickname list
          // nickname clean
          // nickname append
          // nickname reload
          // nickname export
          new TreeCompleter(node("nickname", node("list", "clean", "append", "reload", "export"))),

          // logger color
          new TreeCompleter(node("logger", node("color"))),

          // logger level xxx
          new TreeCompleter(node("logger", node("level", node("EVERYTHING", "TRACE", "DEBUG", "INFO", "WARN", "ERROR", "CLOSE")))),

          // logger verbose name
          // logger verbose slf4j
          new TreeCompleter(node("logger", node("verbose", node("true", "false")))),

          // logger prefix
          // logger prefix delete xxx.xxx.xx.x.x
          new TreeCompleter(node("logger", node("prefix", node("test", "cache", "flush", "delete", "remove")))),

          // exec <name> xxx xxx xxx
          // execute <name> xxx xxx xxx
          new TreeCompleter(node("exec", "execute", node(stringsCompleter))),

          // ?
          // help
          // info
          // gc
          // status
          // stop
          new ArgumentCompleter(new StringsCompleter("?", "help", "info", "status", "gc", "stop"))
        );
      }
    }
  }

  //= ==================================================================================================================
  //=
  //= 控制台子系统
  //=
  //= ==================================================================================================================

  //= ================================================================================================================
  //= 命令体
  //= ================================================================================================================

  private static class ConsoleCommand {

    private final String[] args;

    private ConsoleCommand(String[] args) {
      this.args = args;
    }

    private ConsoleCommand(String command) {
      this(parseCommand(command));
    }

    private static String[] parseCommand(String command) {
      char[] chars = command.toCharArray();
      boolean filed = false;
      boolean escape = false;
      List<String> parts = new LinkedList<>();
      StringBuilder builder = new StringBuilder();
      for (char chat : chars) {
        switch (chat) {
          case '\\' -> {
            if (escape) {
              builder.append("\\");
            }
            escape = !escape;
          }
          case '\'' -> {
            if (escape) {
              builder.append('\'');
            } else {
              filed = !filed;
            }
            escape = false;
          }
          case ' ' -> {
            if (filed) {
              builder.append(chat);
            } else {
              if (builder.isEmpty()) {
                continue;
              }
              parts.add(builder.toString());
              builder.setLength(0);
            }
            escape = false;
          }
          default -> {
            builder.append(chat);
            escape = false;
          }
        }
      }
      parts.add(builder.toString());
      return parts.toArray(new String[0]);
    }

    public int length() {
      return args.length;
    }

    public String getString(int i, String defaultValue) {
      return i < args.length ? args[i] : defaultValue;
    }

    public boolean getBoolean(int i, boolean defaultValue) {
      return i < args.length ? Boolean.parseBoolean(args[i]) : defaultValue;
    }

    public ConsoleCommand subCommand(int i) {
      if (i > args.length) {
        throw new IllegalArgumentException("Index out of command boundary");
      }

      String[] copy = Arrays.copyOfRange(args, i, args.length);
      return new ConsoleCommand(copy);
    }

    public ModuleCommand toModuleCommand(int depth) {
      if (depth < args.length) {
        String[] copy = Arrays.copyOfRange(args, depth, args.length);
        return new ModuleCommand(copy);
      } else {
        return ModuleCommand.empty;
      }
    }

  }

  @Comment("用于模块的命令投递")
  public static class ModuleCommand {

    private static final ModuleCommand empty = new ModuleCommand(new String[0], Collections.emptyList(), Collections.emptyMap());

    private final String[] argument;
    private final List<String> options;
    private final Map<String, String> parameters;

    private ModuleCommand(String[] argument, List<String> options, Map<String, String> parameters) {
      this.argument = argument;
      this.options = options;
      this.parameters = parameters;
    }

    private ModuleCommand(String[] args) {

      List<String> argument = new ArrayList<>();
      List<String> options = new ArrayList<>();
      Map<String, String> parameters = new LinkedHashMap<>();

      for (String arg : args) {
        if (arg.startsWith("--")) {
          int i = arg.indexOf("=");
          if (i > 0) {
            parameters.put(arg.substring(2, i), arg.substring(i + 1));
          } else {
            options.add(arg.substring(2));
          }
        } else {
          argument.add(arg);
        }
      }

      this.argument = new String[argument.size()];
      argument.toArray(this.argument);

      this.options = Collections.unmodifiableList(options);
      this.parameters = Collections.unmodifiableMap(parameters);

    }

    @Comment("获取命令")
    public String[] getArgument() {
      return argument;
    }

    @Comment("获取选项")
    public List<String> getOptions() {
      return options;
    }

    @Comment("获取参数")
    public Map<String, String> getParameters() {
      return parameters;
    }

    @Comment("获取命令")
    public String getArgument(int i) {
      if (i > argument.length - 1) {
        return null;
      }
      return argument[i];
    }

    @Comment("获取选项")
    public boolean hasOption(String option) {
      return options.contains(option);
    }

    @Comment("获取参数")
    public String getParameter(String key, String defaultValue) {
      return parameters.getOrDefault(key, defaultValue);
    }

  }

  //= ================================================================================================================
  //= 调度器
  //= ================================================================================================================

  private static class Dispatcher {

    private final Tree tree = new Tree(null, 0);

    public boolean execute(String command) {
      ConsoleCommand consoleCommand = new ConsoleCommand(command);
      return tree.execute(consoleCommand);
    }

    public RegisterFunctionAccessor registerFunction() {
      return new RegisterFunctionAccessor(this);
    }

    public RegisterExclusiveAccessor registerExclusive() {
      return new RegisterExclusiveAccessor(this);
    }

    public static class RegisterFunctionAccessor {

      private final Dispatcher dispatcher;
      private final List<String[]> commands = new LinkedList<>();

      private RegisterFunctionAccessor(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
      }

      public RegisterFunctionAccessor command(String... command) {
        if (dispatcher.tree.isAdopted(command)) {
          throw new IllegalArgumentException("Can't register this command -> " + String.join(".", command));
        }
        commands.add(command);
        return this;
      }

      public void function(Consumer<ConsoleCommand> function) {
        dispatcher.tree.registerFunction(commands, function);
      }
    }

    public static class RegisterExclusiveAccessor {

      private final Dispatcher dispatcher;
      private final List<String[]> commands = new LinkedList<>();

      private RegisterExclusiveAccessor(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
      }

      public RegisterExclusiveAccessor command(String... command) {
        if (dispatcher.tree.isAdopted(command)) {
          throw new IllegalArgumentException("Can't register this command -> " + String.join(".", command));
        }
        commands.add(command);
        return this;
      }

      public void function(Consumer<ConsoleCommand> function) {
        dispatcher.tree.registerExclusive(commands, function);
      }
    }
  }

  //= ================================================================================================================
  //= 存储体
  //= ================================================================================================================

  private static class Tree {

    private final Tree parent;
    private final int depth;
    private final Map<String, Tree> tree = new LinkedHashMap<>();

    public String name;
    public Boolean exclusive = false;
    public Consumer<ConsoleCommand> function;

    public Tree(Tree parent, int depth) {
      this.parent = parent;
      this.depth = depth;
    }

    public boolean isAdopted(String... args) {
      Tree node = this;
      for (String arg : args) {
        Tree next = node.tree.get(arg);
        if (next == null) return false;
        node = next;
      }
      return node.exclusive;
    }

    public synchronized void registerFunction(List<String[]> commands, Consumer<ConsoleCommand> function) {
      for (String[] command : commands) {
        Tree node = this;
        for (String arg : command) {
          Tree next = node.tree.get(arg);
          if (next == null) {
            Tree temp = new Tree(node, node.depth + 1);
            temp.name = arg;
            node.tree.put(arg, temp);
            node = temp;
          } else {
            node = next;
          }
        }
        node.function = function;
        node.exclusive = false;
      }
    }

    public synchronized void registerExclusive(List<String[]> commands, Consumer<ConsoleCommand> function) {
      for (String[] command : commands) {
        Tree node = this;
        for (String arg : command) {
          Tree next = node.tree.get(arg);
          if (next == null) {
            Tree temp = new Tree(node, node.depth + 1);
            temp.name = arg;
            node.tree.put(arg, temp);
            node = temp;
          } else {
            node = next;
          }
        }
        node.function = function;
        node.exclusive = true;
      }
    }

    public boolean execute(ConsoleCommand consoleCommand) {
      Tree node = this;
      for (String arg : consoleCommand.args) {
        Tree next = node.tree.get(arg);
        if (next == null) {
          return false;
        } else {
          node = next;
          if (node.exclusive) {
            break;
          }
        }
      }
      if (node.function == null) {
        return false;
      }
      if (consoleCommand.length() == node.depth) {
        node.function.accept(null);
      } else {
        ConsoleCommand subCommand = consoleCommand.subCommand(node.depth);
        node.function.accept(subCommand);
      }
      return true;
    }

    @Override
    public String toString() {
      if (parent == null) {
        return "";
      } else {
        return parent + "." + name;
      }
    }

  }

  //= ==================================================================================================================
  //=
  //= 配置子系统
  //=
  //= ==================================================================================================================

  private static class FurryBlackConfig {

    public static FurryBlackConfig from(Configuration configuration) {
      return new FurryBlackConfig(configuration);
    }

    private static final String[] kernel_console_provider = {"kernel", "console", "provider"};

    private static final String[] kernel_logging_provider = {"kernel", "logging", "provider"};
    private static final String[] kernel_logging_level = {"kernel", "logging", "level"};
    private static final String[] kernel_logging_prefix = {"kernel", "logging", "prefix"};
    private static final String[] kernel_logging_fullname = {"kernel", "logging", "fullname"};
    private static final String[] kernel_logging_writeall = {"kernel", "logging", "writeall"};

    private static final String[] system_debug = {"system", "debug"};
    private static final String[] system_debug_halt = {"system", "debug", "halt"};
    private static final String[] system_debug_unsafe = {"system", "debug", "unsafe"};
    private static final String[] system_debug_nologin = {"system", "debug ", "nologin"};

    private static final String[] thread_montior_size = {"thread", "monitor", "size"};
    private static final String[] thread_montior_size_max = {"thread", "monitor ", "size", "max"};

    private static final String[] onebot_mode = {"onebot", "mode"};
    private static final String[] onebot_token = {"onebot", "token"};
    private static final String[] onebot_positive_server = {"onebot", "positive", "server"};
    private static final String[] onebot_reversed_listen = {"onebot", "reversed", "listen"};
    private static final String[] module_regex = {"module", "regex"};

    private final Configuration configuration;

    public final String kernelConsoleProvider;

    public final String kernelLoggingProvider;
    public final String kernelLoggingLevel;
    public final String kernelLoggingPrefix;
    public final Boolean kernelLoggingFullname;
    public final Boolean kernelLoggingWriteall;

    public final Boolean systemDebug;
    public final Boolean systemDebugHalt;
    public final Boolean systemDebugUnsafe;
    public final Boolean systemDebugNologin;

    public final Integer threadMonitorSize;
    public final Integer threadMonitorSizeMax;

    public final OnebotMode onebotMode;
    public final String onebotToken;
    public final String onebotPositiveServer;
    public final Integer onebotReversedListen;

    public final String moduleRegex;

    private FurryBlackConfig(Configuration configuration) {

      this.configuration = configuration;

      this.kernelConsoleProvider = get(kernel_console_provider);

      this.kernelLoggingProvider = get(kernel_logging_provider);
      this.kernelLoggingLevel = get(kernel_logging_level);
      this.kernelLoggingPrefix = get(kernel_logging_prefix);
      this.kernelLoggingFullname = get(kernel_logging_fullname, () -> false, DataEnhance::parseBooleanOrNull);
      this.kernelLoggingWriteall = get(kernel_logging_writeall, () -> false, DataEnhance::parseBooleanOrNull);

      this.systemDebug = get(system_debug, () -> false, DataEnhance::parseBooleanOrNull);
      this.systemDebugHalt = get(system_debug_halt, () -> false, DataEnhance::parseBooleanOrNull);
      this.systemDebugUnsafe = get(system_debug_unsafe, () -> false, DataEnhance::parseBooleanOrNull);
      this.systemDebugNologin = get(system_debug_nologin, () -> false, DataEnhance::parseBooleanOrNull);

      this.threadMonitorSize = get(thread_montior_size, () -> CPU_CORES, DataEnhance::parseIntOrNull);
      this.threadMonitorSizeMax = get(thread_montior_size_max, () -> CPU_CORES, DataEnhance::parseIntOrNull);

      this.onebotMode = get(onebot_mode, () -> OnebotMode.POSITIVE, OnebotMode::from);
      this.onebotToken = get(onebot_token);
      this.onebotPositiveServer = get(onebot_positive_server, () -> "ws://localhost:6099");
      this.onebotReversedListen = get(onebot_reversed_listen, () -> 6099, DataEnhance::parseIntOrNull);

      this.moduleRegex = get(module_regex, () -> "/[a-zA-Z0-9]{2,6}");

    }

    private boolean has(String[] keys) {
      String envName = String.join("_", keys).toUpperCase(Locale.ROOT);
      if (System.getenv(envName) != null) return true;
      return configuration.has(keys);
    }

    private String get(String[] keys) {
      return get(keys, () -> null, it -> it);
    }

    private String get(String[] keys, Supplier<String> defaultValue) {
      return get(keys, defaultValue, it -> it);
    }

    private <T> T get(String[] keys, Function<String, T> convertor) {
      return get(keys, () -> null, convertor);
    }

    private <T> T get(String[] keys, Supplier<T> defaultValue, Function<String, T> convertor) {
      String envName = String.join("_", keys).toUpperCase(Locale.ROOT);
      String envValue = System.getenv(envName);
      try {
        if (envValue != null) {
          return convertor.apply(envValue);
        }
        if (configuration.has(keys)) {
          return convertor.apply(configuration.get(keys));
        }
      } catch (Exception ignored) {}
      return defaultValue.get();
    }

    //= ========================================================

    public enum OnebotMode {
      POSITIVE,
      REVERSED,
      ;

      public static OnebotMode from(String value) {
        return value == null ? null : switch (value.toLowerCase()) {
          case "positive" -> POSITIVE;
          case "reversed" -> REVERSED;
          default -> throw new KernelException("[BOOTING][FATAL] OneBot mode invalid " + value + " positive/reversed");
        };
      }
    }

    //= ========================================================

  }

  //= ==================================================================================================================
  //=
  //= 昵称子系统
  //=
  //= ==================================================================================================================

  private static class Nickname {

    private static final LoggerX logger = LoggerXFactory.getLogger("Nickname");

    private final Map<Long, String> global;
    private final Map<Long, Map<Long, String>> groups;

    public static Nickname getInstance() {
      return new Nickname();
    }

    private Nickname() {
      global = new ConcurrentHashMap<>();
      groups = new ConcurrentHashMap<>();
    }

    private void cleanNickname() {
      global.clear();
      groups.clear();
    }

    private void appendNickname() {
      Path path = FileEnhance.get(FOLDER_CONFIG, "nickname.txt");
      List<String> nicknames;
      try {
        nicknames = Files.readAllLines(path, StandardCharsets.UTF_8);
      } catch (IOException exception) {
        throw new KernelException("读取昵称配置文件失败 -> " + path, exception);
      }
      for (String line : nicknames) {
        String temp = line.trim();
        int indexOfDot = temp.indexOf(".");
        int indexOfColon = temp.indexOf(":");
        if (indexOfDot < 0) {
          logger.warn("配置无效 " + line);
          continue;
        }
        if (indexOfColon < 0) {
          logger.warn("配置无效 " + line);
          continue;
        }
        String group = line.substring(0, indexOfDot);
        String user = line.substring(indexOfDot + 1, indexOfColon);
        String nickname = line.substring(indexOfColon + 1);
        long userId = Long.parseLong(user);
        if ("*".equals(group)) {
          global.put(userId, nickname);
          logger.seek("全局 " + userId + " -> " + nickname);
        } else {
          long groupId = Long.parseLong(group);
          Map<Long, String> groupNicks = groups.computeIfAbsent(groupId, k -> new ConcurrentHashMap<>());
          groupNicks.put(userId, nickname);
          logger.seek("群内 " + groupId + "." + userId + " -> " + nickname);
        }
      }
    }

    private Map<Long, String> getNicknameGlobal() {
      return global;
    }

    private Map<Long, Map<Long, String>> getNicknameGroups() {
      return groups;
    }

    private String getUsersMappedNickName(User user) {
      return global.getOrDefault(user.getId(), user.getNick());
    }

    private String getUsersMappedNickName(long userId) {
      return global.getOrDefault(userId, Mirai.getInstance().queryProfile(BOT, userId).getNickname());
    }

    private String getMemberMappedNickName(Member member) {
      Map<Long, String> groupMap = groups.get(member.getGroup().getId());
      if (groupMap != null) {
        String nickName = groupMap.get(member.getId());
        if (nickName != null) return nickName;
      }
      String nickName = global.get(member.getId());
      if (nickName != null) return nickName;
      String nameCard = member.getNameCard();
      if (nameCard.isBlank()) {
        return member.getNick();
      } else {
        return nameCard;
      }
    }

    private String getMemberMappedNickName(long groupId, long userId) {
      Map<Long, String> groupMap = groups.get(groupId);
      if (groupMap != null) {
        String nickName = groupMap.get(userId);
        if (nickName != null) return nickName;
      }
      String nickName = global.get(userId);
      if (nickName != null) return nickName;
      Member member = BOT.getGroupOrFail(groupId).getOrFail(userId);
      String nameCard = member.getNameCard();
      if (nameCard.isBlank()) {
        return member.getNick();
      } else {
        return nameCard;
      }
    }

  }

  //= ==================================================================================================================
  //=
  //= 插件子系统
  //=
  //= ==================================================================================================================

  //= ==================================================================================================================
  //= 插件系统

  private static final class Schema {

    private final LoggerX logger = LoggerXFactory.getLogger("SCHEMA");

    private final Path folder;

    private final Map<String, Plugin> plugins;

    private final Map<String, Class<? extends AbstractEventHandler>> modules;

    private final Map<Runner, Class<? extends EventHandlerRunner>> COMPONENT_RUNNER_CLAZZ;
    private final Map<Filter, Class<? extends EventHandlerFilter>> COMPONENT_FILTER_CLAZZ;
    private final Map<Monitor, Class<? extends EventHandlerMonitor>> COMPONENT_MONITOR_CLAZZ;
    private final Map<Checker, Class<? extends EventHandlerChecker>> COMPONENT_CHECKER_CLAZZ;
    private final NavigableMap<Executor, Class<? extends EventHandlerExecutor>> COMPONENT_EXECUTOR_CLAZZ;

    private final List<Runner> SORTED_RUNNER;
    private final List<Filter> SORTED_FILTER;
    private final List<Monitor> SORTED_MONITOR;
    private final List<Checker> SORTED_CHECKER;

    private final Map<Runner, EventHandlerRunner> COMPONENT_RUNNER_INSTANCE;
    private final Map<Filter, EventHandlerFilter> COMPONENT_FILTER_INSTANCE;
    private final Map<Monitor, EventHandlerMonitor> COMPONENT_MONITOR_INSTANCE;
    private final Map<Checker, EventHandlerChecker> COMPONENT_CHECKER_INSTANCE;
    private final NavigableMap<Executor, EventHandlerExecutor> COMPONENT_EXECUTOR_INSTANCE;

    private final Map<String, Executor> COMMAND_EXECUTOR_RELATION;

    private final Map<String, String> MODULE_PLUGIN_RELATION;

    private final List<EventHandlerFilter> FILTER_USERS_CHAIN;
    private final List<EventHandlerFilter> FILTER_GROUP_CHAIN;

    private final List<EventHandlerMonitor> MONITOR_USERS_CHAIN;
    private final List<EventHandlerMonitor> MONITOR_GROUP_CHAIN;

    private final Map<String, EventHandlerExecutor> EXECUTOR_USERS_POOL;
    private final Map<String, EventHandlerExecutor> EXECUTOR_GROUP_POOL;

    private final List<EventHandlerChecker> GLOBAL_CHECKER_USERS_POOL;
    private final List<EventHandlerChecker> GLOBAL_CHECKER_GROUP_POOL;

    private final Map<String, List<EventHandlerChecker>> COMMAND_CHECKER_USERS_POOL;
    private final Map<String, List<EventHandlerChecker>> COMMAND_CHECKER_GROUP_POOL;

    //= ========================================================================
    //= 构造
    //= ========================================================================

    private Schema(Path folder) {

      this.folder = folder;

      logger.hint("加载插件模型");

      plugins = new HashMap<>();
      modules = new HashMap<>();

      COMPONENT_RUNNER_CLAZZ = new HashMap<>();
      COMPONENT_FILTER_CLAZZ = new HashMap<>();
      COMPONENT_MONITOR_CLAZZ = new HashMap<>();
      COMPONENT_CHECKER_CLAZZ = new HashMap<>();
      COMPONENT_EXECUTOR_CLAZZ = new TreeMap<>(AnnotationEnhance::compare);

      SORTED_RUNNER = new LinkedList<>();
      SORTED_FILTER = new LinkedList<>();
      SORTED_MONITOR = new LinkedList<>();
      SORTED_CHECKER = new LinkedList<>();

      COMPONENT_RUNNER_INSTANCE = new ConcurrentHashMap<>();
      COMPONENT_FILTER_INSTANCE = new ConcurrentHashMap<>();
      COMPONENT_MONITOR_INSTANCE = new ConcurrentHashMap<>();
      COMPONENT_CHECKER_INSTANCE = new ConcurrentHashMap<>();
      COMPONENT_EXECUTOR_INSTANCE = new ConcurrentSkipListMap<>(AnnotationEnhance::compare);

      COMMAND_EXECUTOR_RELATION = new HashMap<>();
      MODULE_PLUGIN_RELATION = new HashMap<>();

      FILTER_USERS_CHAIN = new CopyOnWriteArrayList<>();
      FILTER_GROUP_CHAIN = new CopyOnWriteArrayList<>();

      MONITOR_USERS_CHAIN = new CopyOnWriteArrayList<>();
      MONITOR_GROUP_CHAIN = new CopyOnWriteArrayList<>();

      EXECUTOR_USERS_POOL = new ConcurrentHashMap<>();
      EXECUTOR_GROUP_POOL = new ConcurrentHashMap<>();

      GLOBAL_CHECKER_USERS_POOL = new CopyOnWriteArrayList<>();
      GLOBAL_CHECKER_GROUP_POOL = new CopyOnWriteArrayList<>();

      COMMAND_CHECKER_USERS_POOL = new ConcurrentHashMap<>();
      COMMAND_CHECKER_GROUP_POOL = new ConcurrentHashMap<>();

    }

    //= ========================================================================
    //= 核心功能
    //= ========================================================================

    //= ========================================================================
    //= 反转控制

    @SuppressWarnings("unchecked")
    public <T extends EventHandlerRunner> T getRunner(Class<T> clazz) {
      List<EventHandlerRunner> collect = COMPONENT_RUNNER_INSTANCE.values().stream().filter(clazz::isInstance).toList();
      if (collect.size() == 1) {
        return (T) collect.getFirst();
      } else {
        return null;
      }
    }

    //= ========================================================================
    //= 生成信息

    public String generateUsersExecutorList() {
      if (EXECUTOR_USERS_POOL.isEmpty()) {
        return "没有任何已装载的命令";
      }
      StringBuilder builder = new StringBuilder();
      for (Executor executor : COMPONENT_EXECUTOR_INSTANCE.keySet()) {
        if (!EXECUTOR_USERS_POOL.containsKey(executor.command())) {
          continue;
        }
        builder.append(executor.outline());
        builder.append("[");
        builder.append(executor.command());
        builder.append("]");
        builder.append(executor.description());
        builder.append("\r\n");
      }
      builder.setLength(builder.length() - 2);
      return builder.toString();
    }

    public String generateGroupExecutorList() {
      if (EXECUTOR_GROUP_POOL.isEmpty()) {
        return "没有任何已装载的命令";
      }
      StringBuilder builder = new StringBuilder();
      for (Executor executor : COMPONENT_EXECUTOR_INSTANCE.keySet()) {
        if (!EXECUTOR_GROUP_POOL.containsKey(executor.command())) {
          continue;
        }
        builder.append(executor.outline());
        builder.append("[");
        builder.append(executor.command());
        builder.append("]");
        builder.append(executor.description());
        builder.append("\r\n");
      }
      builder.setLength(builder.length() - 2);
      return builder.toString();
    }

    //= ========================================================================
    //= 处理系统

    public List<EventHandlerFilter> getFilterUsersChain() {
      return FILTER_USERS_CHAIN;
    }

    public List<EventHandlerFilter> getFilterGroupChain() {
      return FILTER_GROUP_CHAIN;
    }

    public List<EventHandlerMonitor> getMonitorUsersChain() {
      return MONITOR_USERS_CHAIN;
    }

    public List<EventHandlerMonitor> getMonitorGroupChain() {
      return MONITOR_GROUP_CHAIN;
    }

    public Map<String, EventHandlerExecutor> getExecutorUsersPool() {
      return EXECUTOR_USERS_POOL;
    }

    public Map<String, EventHandlerExecutor> getExecutorGroupPool() {
      return EXECUTOR_GROUP_POOL;
    }

    public List<EventHandlerChecker> getGlobalCheckerUsersPool() {
      return GLOBAL_CHECKER_USERS_POOL;
    }

    public List<EventHandlerChecker> getGlobalCheckerGroupPool() {
      return GLOBAL_CHECKER_GROUP_POOL;
    }

    public List<EventHandlerChecker> getCommandCheckerUsersPool(String name) {
      return COMMAND_CHECKER_USERS_POOL.get(name);
    }

    public List<EventHandlerChecker> getCommandCheckerGroupPool(String name) {
      return COMMAND_CHECKER_GROUP_POOL.get(name);
    }

    //= ========================================================================
    //= 模块承载
    //= ========================================================================

    //= ========================================================================
    //=  扫描插件

    public void scanPlugin() {

      logger.hint("扫描插件目录");

      List<Path> listFiles;

      try (Stream<Path> stream = Files.list(folder)) {
        listFiles = stream.toList();
      } catch (IOException exception) {
        throw new SchemaException("扫描插件目录失败", exception);
      }

      if (listFiles.isEmpty()) {
        logger.warn("插件目录为空");
        return;
      }

      logger.seek("发现[" + listFiles.size() + "]个文件");

      for (Path path : listFiles) {
        logger.info("尝试加载 -> " + path.getFileName());
        Plugin plugin = Plugin.load(path);
        String name = plugin.getName();
        if (plugins.containsKey(name)) {
          Plugin exist = plugins.get(name);
          throw new SchemaException("发现插件名称冲突 " + plugin.getPath() + "名称" + name + "已被注册" + exist.getPath());
        }
        plugins.put(name, plugin);
      }

      logger.seek("发现[" + plugins.size() + "]个插件");

      for (Plugin plugin : plugins.values()) {
        logger.info(plugin.getPath().getFileName() + " -> " + plugin.getName());
      }
    }

    //= ========================================================================
    //=  扫描模块

    public void scanModule() {
      logger.hint("扫描插件包内容");
      plugins.values().forEach(Plugin::scan);
    }

    //= ========================================================================
    //=  注册模块

    public void loadModule() {

      logger.hint("向插件模型注册模块");

      for (Map.Entry<String, Plugin> pluginEntry : plugins.entrySet()) {

        var pluginName = pluginEntry.getKey();
        var pluginPackage = pluginEntry.getValue();

        logger.seek("尝试注册插件 -> " + pluginName);

        if (pluginPackage.getModules().isEmpty()) {
          logger.warn("插件包内不含任何模块 " + pluginName);
          return;
        }

        logger.seek("模块冲突检查 -> " + pluginName);

        for (Map.Entry<Runner, Class<? extends EventHandlerRunner>> moduleEntry : pluginPackage.getRunnerClassMap().entrySet()) {
          var k = moduleEntry.getKey();
          var v = moduleEntry.getValue();
          if (COMPONENT_RUNNER_CLAZZ.containsKey(k)) {
            Class<? extends AbstractEventHandler> exist = COMPONENT_RUNNER_CLAZZ.get(k);
            throw new SchemaException("发现模块名冲突 " + pluginName + ":" + v.getName() + "与" + COMPONENT_RUNNER_CLAZZ.get(k) + ":" + exist.getName());
          }
        }

        for (Map.Entry<Filter, Class<? extends EventHandlerFilter>> moduleEntry : pluginPackage.getFilterClassMap().entrySet()) {
          var k = moduleEntry.getKey();
          var v = moduleEntry.getValue();
          if (COMPONENT_FILTER_CLAZZ.containsKey(k)) {
            Class<? extends AbstractEventHandler> exist = COMPONENT_FILTER_CLAZZ.get(k);
            throw new SchemaException("发现模块名冲突 " + pluginName + ":" + v.getName() + "与" + COMPONENT_FILTER_CLAZZ.get(k) + ":" + exist.getName());
          }
        }

        for (Map.Entry<Monitor, Class<? extends EventHandlerMonitor>> moduleEntry : pluginPackage.getMonitorClassMap().entrySet()) {
          var k = moduleEntry.getKey();
          var v = moduleEntry.getValue();
          if (COMPONENT_MONITOR_CLAZZ.containsKey(k)) {
            Class<? extends AbstractEventHandler> exist = COMPONENT_MONITOR_CLAZZ.get(k);
            throw new SchemaException("发现模块名冲突 " + pluginName + ":" + v.getName() + "与" + COMPONENT_MONITOR_CLAZZ.get(k) + ":" + exist.getName());
          }
        }

        for (Map.Entry<Checker, Class<? extends EventHandlerChecker>> moduleEntry : pluginPackage.getCheckerClassMap().entrySet()) {
          var k = moduleEntry.getKey();
          var v = moduleEntry.getValue();
          if (COMPONENT_CHECKER_CLAZZ.containsKey(k)) {
            Class<? extends AbstractEventHandler> exist = COMPONENT_CHECKER_CLAZZ.get(k);
            throw new SchemaException("发现模块名冲突 " + pluginName + ":" + v.getName() + "与" + COMPONENT_CHECKER_CLAZZ.get(k) + ":" + exist.getName());
          }
        }

        for (Map.Entry<Executor, Class<? extends EventHandlerExecutor>> moduleEntry : pluginPackage.getExecutorClassMap().entrySet()) {
          var k = moduleEntry.getKey();
          var v = moduleEntry.getValue();
          if (COMPONENT_EXECUTOR_CLAZZ.containsKey(k)) {
            Class<? extends AbstractEventHandler> exist = COMPONENT_EXECUTOR_CLAZZ.get(k);
            throw new SchemaException("发现模块名冲突 " + pluginName + ":" + v.getName() + "与" + COMPONENT_EXECUTOR_CLAZZ.get(k) + ":" + exist.getName());
          }
        }

        for (Map.Entry<Executor, Class<? extends EventHandlerExecutor>> entry : pluginPackage.getExecutorClassMap().entrySet()) {
          var k = entry.getKey();
          var v = entry.getValue();
          String command = k.command();
          if (COMMAND_EXECUTOR_RELATION.containsKey(command)) {
            Executor annotation = COMMAND_EXECUTOR_RELATION.get(command);
            Class<? extends EventHandlerExecutor> exist = COMPONENT_EXECUTOR_CLAZZ.get(annotation);
            String existPluginName = MODULE_PLUGIN_RELATION.get(annotation.value());
            throw new SchemaException("发现命令冲突 " + command + " - " + pluginName + ":" + v.getName() + "已注册为" + existPluginName + ":" + exist.getName());
          }
        }

        logger.seek("冲突检查通过 -> " + pluginName);

        for (Map.Entry<Runner, Class<? extends EventHandlerRunner>> entry : pluginPackage.getRunnerClassMap().entrySet()) {
          var k = entry.getKey();
          var v = entry.getValue();
          String moduleName = k.value();
          modules.put(moduleName, v);
          SORTED_RUNNER.add(k);
          COMPONENT_RUNNER_CLAZZ.put(k, v);
          MODULE_PLUGIN_RELATION.put(moduleName, pluginName);
          logger.info("注册定时器" + pluginName + ":" + moduleName + "[" + k.priority() + "] -> " + v.getName());
        }

        for (Map.Entry<Filter, Class<? extends EventHandlerFilter>> entry : pluginPackage.getFilterClassMap().entrySet()) {
          var k = entry.getKey();
          var v = entry.getValue();
          String moduleName = k.value();
          modules.put(moduleName, v);
          SORTED_FILTER.add(k);
          COMPONENT_FILTER_CLAZZ.put(k, v);
          MODULE_PLUGIN_RELATION.put(moduleName, pluginName);
          logger.info("注册过滤器" + pluginName + ":" + moduleName + "[" + k.priority() + "] -> " + v.getName());
        }

        for (Map.Entry<Monitor, Class<? extends EventHandlerMonitor>> entry : pluginPackage.getMonitorClassMap().entrySet()) {
          var k = entry.getKey();
          var v = entry.getValue();
          String moduleName = k.value();
          modules.put(moduleName, v);
          SORTED_MONITOR.add(k);
          COMPONENT_MONITOR_CLAZZ.put(k, v);
          MODULE_PLUGIN_RELATION.put(moduleName, pluginName);
          logger.info("注册监听器" + pluginName + ":" + moduleName + "[" + k.priority() + "] -> " + v.getName());
        }

        for (Map.Entry<Checker, Class<? extends EventHandlerChecker>> entry : pluginPackage.getCheckerClassMap().entrySet()) {
          var k = entry.getKey();
          var v = entry.getValue();
          String moduleName = k.value();
          modules.put(moduleName, v);
          SORTED_CHECKER.add(k);
          COMPONENT_CHECKER_CLAZZ.put(k, v);
          MODULE_PLUGIN_RELATION.put(moduleName, pluginName);
          logger.info("注册检查器" + pluginName + ":" + moduleName + "[" + k.priority() + "] -> " + v.getName());
        }

        for (Map.Entry<Executor, Class<? extends EventHandlerExecutor>> entry : pluginPackage.getExecutorClassMap().entrySet()) {
          var k = entry.getKey();
          var v = entry.getValue();
          String moduleName = k.value();
          modules.put(moduleName, v);
          COMMAND_EXECUTOR_RELATION.put(k.command(), k);
          COMPONENT_EXECUTOR_CLAZZ.put(k, v);
          MODULE_PLUGIN_RELATION.put(moduleName, pluginName);
          logger.info("注册执行器" + pluginName + ":" + moduleName + "[" + k.command() + "] -> " + v.getName());
        }
      }

      SORTED_RUNNER.sort(AnnotationEnhance::compare);
      SORTED_FILTER.sort(AnnotationEnhance::compare);
      SORTED_MONITOR.sort(AnnotationEnhance::compare);
      SORTED_CHECKER.sort(AnnotationEnhance::compare);

    }

    //= ========================================================================
    //=  创建模块

    public void makeModule() {

      logger.hint("加载定时器 " + COMPONENT_RUNNER_CLAZZ.size());

      for (Runner annotation : SORTED_RUNNER) {
        Class<? extends EventHandlerRunner> clazz = COMPONENT_RUNNER_CLAZZ.get(annotation);
        String moduleName = annotation.value();
        String pluginName = MODULE_PLUGIN_RELATION.get(moduleName);
        Plugin plugin = plugins.get(pluginName);
        URLClassLoader dependClassLoader = plugin.getDependClassLoader();
        logger.info("加载定时器" + pluginName + ":" + moduleName + "[" + annotation.priority() + "] -> " + clazz.getName());
        EventHandlerRunner instance;
        try {
          instance = clazz.getConstructor().newInstance();
          instance.internalInit(pluginName, moduleName, dependClassLoader);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
          throw new SchemaException("加载定时器失败 " + pluginName + ":" + moduleName + "[" + annotation.priority() + "] -> " + clazz.getName());
        }

        COMPONENT_RUNNER_INSTANCE.put(annotation, instance);
      }

      logger.hint("加载过滤器 " + COMPONENT_FILTER_CLAZZ.size());

      for (Filter annotation : SORTED_FILTER) {
        Class<? extends EventHandlerFilter> clazz = COMPONENT_FILTER_CLAZZ.get(annotation);
        String moduleName = annotation.value();
        String pluginName = MODULE_PLUGIN_RELATION.get(moduleName);
        Plugin plugin = plugins.get(pluginName);
        URLClassLoader dependClassLoader = plugin.getDependClassLoader();
        logger.info("加载过滤器" + pluginName + ":" + moduleName + "[" + annotation.priority() + "] -> " + clazz.getName());
        EventHandlerFilter instance;
        try {
          instance = clazz.getConstructor().newInstance();
          instance.internalInit(pluginName, moduleName, dependClassLoader);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
          throw new SchemaException("加载过滤器失败 " + MODULE_PLUGIN_RELATION.get(moduleName) + ":" + moduleName + " " + clazz.getName());
        }
        COMPONENT_FILTER_INSTANCE.put(annotation, instance);
        if (annotation.users()) FILTER_USERS_CHAIN.add(instance);
        if (annotation.group()) FILTER_GROUP_CHAIN.add(instance);
      }

      logger.hint("加载监听器 " + COMPONENT_MONITOR_CLAZZ.size());

      for (Monitor annotation : SORTED_MONITOR) {
        Class<? extends EventHandlerMonitor> clazz = COMPONENT_MONITOR_CLAZZ.get(annotation);
        String moduleName = annotation.value();
        String pluginName = MODULE_PLUGIN_RELATION.get(moduleName);
        Plugin plugin = plugins.get(pluginName);
        URLClassLoader dependClassLoader = plugin.getDependClassLoader();
        logger.info("加载监听器" + pluginName + ":" + moduleName + "[" + annotation.priority() + "] -> " + clazz.getName());
        EventHandlerMonitor instance;
        try {
          instance = clazz.getConstructor().newInstance();
          instance.internalInit(pluginName, moduleName, dependClassLoader);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
          throw new SchemaException("加载监听器失败 " + MODULE_PLUGIN_RELATION.get(moduleName) + ":" + moduleName + " " + clazz.getName());
        }
        COMPONENT_MONITOR_INSTANCE.put(annotation, instance);
        if (annotation.users()) MONITOR_USERS_CHAIN.add(instance);
        if (annotation.group()) MONITOR_GROUP_CHAIN.add(instance);
      }

      logger.hint("加载检查器 " + COMPONENT_CHECKER_CLAZZ.size());

      for (Checker annotation : SORTED_CHECKER) {
        Class<? extends EventHandlerChecker> clazz = COMPONENT_CHECKER_CLAZZ.get(annotation);
        String moduleName = annotation.value();
        String pluginName = MODULE_PLUGIN_RELATION.get(moduleName);
        Plugin plugin = plugins.get(pluginName);
        URLClassLoader dependClassLoader = plugin.getDependClassLoader();
        logger.info("加载检查器" + pluginName + ":" + moduleName + "[" + annotation.priority() + "] -> " + clazz.getName());
        EventHandlerChecker instance;
        try {
          instance = clazz.getConstructor().newInstance();
          instance.internalInit(pluginName, moduleName, dependClassLoader);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
          throw new SchemaException("加载检查器失败 " + MODULE_PLUGIN_RELATION.get(moduleName) + ":" + moduleName + " " + clazz.getName());
        }
        COMPONENT_CHECKER_INSTANCE.put(annotation, instance);
        if (annotation.command().equals("*")) {
          if (annotation.users()) GLOBAL_CHECKER_USERS_POOL.add(instance);
          if (annotation.group()) GLOBAL_CHECKER_GROUP_POOL.add(instance);
        } else {
          if (annotation.users()) {
            List<EventHandlerChecker> checkerList = COMMAND_CHECKER_USERS_POOL.computeIfAbsent(annotation.command(), k1 -> new CopyOnWriteArrayList<>());
            checkerList.add(instance);
            checkerList.sort((o1, o2) -> {
              Checker o1Annotation = o1.getClass().getAnnotation(Checker.class);
              Checker o2Annotation = o2.getClass().getAnnotation(Checker.class);
              return o1Annotation.priority() - o2Annotation.priority();
            });
          }
          if (annotation.group()) {
            List<EventHandlerChecker> checkerList = COMMAND_CHECKER_GROUP_POOL.computeIfAbsent(annotation.command(), k1 -> new CopyOnWriteArrayList<>());
            checkerList.add(instance);
            checkerList.sort((o1, o2) -> {
              Checker o1Annotation = o1.getClass().getAnnotation(Checker.class);
              Checker o2Annotation = o2.getClass().getAnnotation(Checker.class);
              return o1Annotation.priority() - o2Annotation.priority();
            });
          }
        }
      }

      logger.hint("加载执行器 " + COMPONENT_EXECUTOR_CLAZZ.size());

      for (Map.Entry<Executor, Class<? extends EventHandlerExecutor>> entry : COMPONENT_EXECUTOR_CLAZZ.entrySet()) {
        Executor annotation = entry.getKey();
        Class<? extends EventHandlerExecutor> clazz = entry.getValue();
        String moduleName = annotation.value();
        String pluginName = MODULE_PLUGIN_RELATION.get(moduleName);
        Plugin plugin = plugins.get(pluginName);
        URLClassLoader dependClassLoader = plugin.getDependClassLoader();
        logger.info("加载执行器" + pluginName + ":" + moduleName + "[" + annotation.command() + "] -> " + clazz.getName());
        EventHandlerExecutor instance;
        try {
          instance = clazz.getConstructor().newInstance();
          instance.internalInit(pluginName, moduleName, dependClassLoader);
          instance.buildHelp(annotation);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
          throw new SchemaException("加载执行器失败 " + MODULE_PLUGIN_RELATION.get(moduleName) + ":" + moduleName + " " + clazz.getName());
        }
        COMPONENT_EXECUTOR_INSTANCE.put(annotation, instance);
        if (annotation.users()) EXECUTOR_USERS_POOL.put(annotation.command(), instance);
        if (annotation.group()) EXECUTOR_GROUP_POOL.put(annotation.command(), instance);
      }

    }

    //= ========================================================================
    //=  预载模块

    public void initModule() {

      logger.hint("预载定时器");

      for (Runner annotation : SORTED_RUNNER) {
        EventHandlerRunner instance = COMPONENT_RUNNER_INSTANCE.get(annotation);
        logger.seek("预载定时器" + annotation.value() + "[" + annotation.priority() + "] -> " + instance.getClass().getName());
        try {
          instance.initWrapper();
        } catch (Exception exception) {
          throw new SchemaException("预载定时器失败 " + MODULE_PLUGIN_RELATION.get(annotation.value()) + ":" + annotation.value() + " -> " + instance.getClass().getName(), exception);
        }
      }

      logger.hint("预载过滤器");

      for (Filter annotation : SORTED_FILTER) {
        EventHandlerFilter instance = COMPONENT_FILTER_INSTANCE.get(annotation);
        logger.seek("预载过滤器" + annotation.value() + "[" + annotation.priority() + "] -> " + instance.getClass().getName());
        try {
          instance.initWrapper();
        } catch (Exception exception) {
          throw new SchemaException("预载过滤器失败 " + MODULE_PLUGIN_RELATION.get(annotation.value()) + ":" + annotation.value() + " -> " + instance.getClass().getName(), exception);
        }
      }

      logger.hint("预载监听器");

      for (Monitor annotation : SORTED_MONITOR) {
        EventHandlerMonitor instance = COMPONENT_MONITOR_INSTANCE.get(annotation);
        logger.seek("预载监听器" + annotation.value() + "[" + annotation.priority() + "] -> " + instance.getClass().getName());
        try {
          instance.initWrapper();
        } catch (Exception exception) {
          throw new SchemaException("预载监听器失败 " + MODULE_PLUGIN_RELATION.get(annotation.value()) + ":" + annotation.value() + " -> " + instance.getClass().getName(), exception);
        }
      }

      logger.hint("预载检查器");

      for (Checker annotation : SORTED_CHECKER) {
        EventHandlerChecker instance = COMPONENT_CHECKER_INSTANCE.get(annotation);
        logger.seek("预载检查器" + annotation.value() + "[" + annotation.priority() + "] -> " + instance.getClass().getName());
        try {
          instance.initWrapper();
        } catch (Exception exception) {
          throw new SchemaException("预载检查器失败 " + MODULE_PLUGIN_RELATION.get(annotation.value()) + ":" + annotation.value() + " -> " + instance.getClass().getName(), exception);
        }
      }

      logger.hint("预载执行器");

      for (Map.Entry<Executor, EventHandlerExecutor> entry : COMPONENT_EXECUTOR_INSTANCE.entrySet()) {
        Executor annotation = entry.getKey();
        EventHandlerExecutor instance = entry.getValue();
        logger.seek("预载执行器" + annotation.value() + "[" + annotation.command() + "] -> " + instance.getClass().getName());
        try {
          instance.initWrapper();
        } catch (Exception exception) {
          throw new SchemaException("预载执行器失败 " + MODULE_PLUGIN_RELATION.get(annotation.value()) + ":" + annotation.value() + " -> " + instance.getClass().getName(), exception);
        }
      }
    }

    //= ========================================================================
    //=  启动模块

    public void bootModule() {

      logger.hint("启动定时器");

      for (Runner annotation : SORTED_RUNNER) {
        EventHandlerRunner instance = COMPONENT_RUNNER_INSTANCE.get(annotation);
        if (instance.isEnable()) {
          logger.seek("启动定时器" + annotation.value() + "[" + annotation.priority() + "] -> " + instance.getClass().getName());
        } else {
          logger.seek("跳过定时器" + annotation.value() + "[" + annotation.priority() + "] -> " + instance.getClass().getName());
          continue;
        }
        try {
          instance.bootWrapper();
        } catch (Exception exception) {
          throw new SchemaException("启动定时器失败 " + MODULE_PLUGIN_RELATION.get(annotation.value()) + ":" + annotation.value() + " -> " + instance.getClass().getName(), exception);
        }
      }

      logger.hint("启动过滤器");

      for (Filter annotation : SORTED_FILTER) {
        EventHandlerFilter instance = COMPONENT_FILTER_INSTANCE.get(annotation);
        if (instance.isEnable()) {
          logger.seek("启动过滤器" + annotation.value() + "[" + annotation.priority() + "] -> " + instance.getClass().getName());
        } else {
          logger.seek("跳过过滤器" + annotation.value() + "[" + annotation.priority() + "] -> " + instance.getClass().getName());
          continue;
        }
        try {
          instance.bootWrapper();
        } catch (Exception exception) {
          throw new SchemaException("启动过滤器失败 " + MODULE_PLUGIN_RELATION.get(annotation.value()) + ":" + annotation.value() + " -> " + instance.getClass().getName(), exception);
        }
      }

      logger.hint("启动监听器");

      for (Monitor annotation : SORTED_MONITOR) {
        EventHandlerMonitor instance = COMPONENT_MONITOR_INSTANCE.get(annotation);
        if (instance.isEnable()) {
          logger.seek("启动监听器" + annotation.value() + "[" + annotation.priority() + "] -> " + instance.getClass().getName());
        } else {
          logger.seek("跳过监听器" + annotation.value() + "[" + annotation.priority() + "] -> " + instance.getClass().getName());
          continue;
        }
        try {
          instance.bootWrapper();
        } catch (Exception exception) {
          throw new SchemaException("启动监听器失败 " + MODULE_PLUGIN_RELATION.get(annotation.value()) + ":" + annotation.value() + " -> " + instance.getClass().getName(), exception);
        }
      }

      logger.hint("启动检查器");

      for (Checker annotation : SORTED_CHECKER) {
        EventHandlerChecker instance = COMPONENT_CHECKER_INSTANCE.get(annotation);
        if (instance.isEnable()) {
          logger.seek("启动检查器" + annotation.value() + "[" + annotation.priority() + "] -> " + instance.getClass().getName());
        } else {
          logger.seek("启动检查器" + annotation.value() + "[" + annotation.priority() + "] -> " + instance.getClass().getName());
          continue;
        }
        try {
          instance.bootWrapper();
        } catch (Exception exception) {
          throw new SchemaException("启动检查器失败 " + MODULE_PLUGIN_RELATION.get(annotation.value()) + ":" + annotation.value() + " -> " + instance.getClass().getName(), exception);
        }
      }

      logger.hint("启动执行器");

      for (Map.Entry<Executor, EventHandlerExecutor> entry : COMPONENT_EXECUTOR_INSTANCE.entrySet()) {
        Executor annotation = entry.getKey();
        EventHandlerExecutor instance = entry.getValue();
        if (instance.isEnable()) {
          logger.seek("启动执行器" + annotation.value() + "[" + annotation.command() + "] -> " + instance.getClass().getName());
        } else {
          logger.seek("跳过执行器" + annotation.value() + "[" + annotation.command() + "] -> " + instance.getClass().getName());
        }
        try {
          instance.bootWrapper();
        } catch (Exception exception) {
          throw new SchemaException("启动执行器失败 " + MODULE_PLUGIN_RELATION.get(annotation.value()) + ":" + annotation.value() + " -> " + instance.getClass().getName(), exception);
        }
      }
    }

    //= ========================================================================
    //=  关闭模块

    public void shutModule() {

      logger.hint("关闭执行器");

      for (Map.Entry<Executor, EventHandlerExecutor> entry : COMPONENT_EXECUTOR_INSTANCE.entrySet()) {
        Executor annotation = entry.getKey();
        EventHandlerExecutor instance = entry.getValue();
        if (!instance.isEnable()) {
          logger.seek("跳过执行器" + annotation.value() + "[" + annotation.command() + "] -> " + instance.getClass().getName());
          continue;
        }
        try {
          if (STATE_SHUTDOWN_DROP) {
            logger.seek("丢弃执行器" + annotation.value() + "[" + annotation.command() + "] -> " + instance.getClass().getName());
            Thread thread = new Thread(instance::shutWrapper);
            thread.setDaemon(true);
            thread.start();
          } else {
            logger.seek("关闭执行器" + annotation.value() + "[" + annotation.command() + "] -> " + instance.getClass().getName());
            instance.shutWrapper();
          }
        } catch (Exception exception) {
          logger.warn("关闭执行器异常" + annotation.value() + "[" + annotation.command() + "] -> " + instance.getClass().getName(), exception);
        }
      }

      logger.hint("关闭检查器");

      List<Checker> checkers = new ArrayList<>(SORTED_CHECKER);
      Collections.reverse(checkers);
      for (Checker annotation : checkers) {
        EventHandlerChecker instance = COMPONENT_CHECKER_INSTANCE.get(annotation);
        if (!instance.isEnable()) {
          logger.seek("跳过检查器" + annotation.value() + "[" + annotation.command() + "] -> " + instance.getClass().getName());
          continue;
        }
        try {
          if (STATE_SHUTDOWN_DROP) {
            logger.seek("丢弃检查器" + annotation.value() + "[" + annotation.command() + "/" + annotation.priority() + "] -> " + instance.getClass().getName());
            Thread thread = new Thread(instance::shutWrapper);
            thread.setDaemon(true);
            thread.start();
          } else {
            logger.seek("关闭检查器" + annotation.value() + "[" + annotation.command() + "/" + annotation.priority() + "] -> " + instance.getClass().getName());
            instance.shutWrapper();
          }
        } catch (Exception exception) {
          logger.warn("关闭检查器异常" + annotation.value() + "[" + annotation.command() + "/" + annotation.priority() + "] -> " + instance.getClass().getName(), exception);
        }
      }

      logger.hint("关闭监听器");

      List<Monitor> monitors = new ArrayList<>(SORTED_MONITOR);
      Collections.reverse(monitors);
      for (Monitor annotation : monitors) {
        EventHandlerMonitor instance = COMPONENT_MONITOR_INSTANCE.get(annotation);
        if (!instance.isEnable()) {
          logger.seek("跳过监听器" + annotation.value() + "[" + annotation.priority() + "] -> " + instance.getClass().getName());
          continue;
        }
        try {
          if (STATE_SHUTDOWN_DROP) {
            logger.seek("丢弃监听器" + annotation.value() + "[" + annotation.priority() + "] -> " + instance.getClass().getName());
            Thread thread = new Thread(instance::shutWrapper);
            thread.setDaemon(true);
            thread.start();
          } else {
            logger.seek("关闭监听器" + annotation.value() + "[" + annotation.priority() + "] -> " + instance.getClass().getName());
            instance.shutWrapper();
          }
        } catch (Exception exception) {
          logger.warn("关闭检监听异常" + annotation.value() + "[" + annotation.priority() + "] -> " + instance.getClass().getName(), exception);
        }
      }

      logger.hint("关闭过滤器");

      List<Filter> filters = new ArrayList<>(SORTED_FILTER);
      Collections.reverse(filters);
      for (Filter annotation : filters) {
        EventHandlerFilter instance = COMPONENT_FILTER_INSTANCE.get(annotation);
        if (!instance.isEnable()) {
          logger.seek("跳过过滤器" + annotation.value() + "[" + annotation.priority() + "] -> " + instance.getClass().getName());
          continue;
        }
        try {
          if (STATE_SHUTDOWN_DROP) {
            logger.seek("丢弃过滤器" + annotation.value() + "[" + annotation.priority() + "] -> " + instance.getClass().getName());
            Thread thread = new Thread(instance::shutWrapper);
            thread.setDaemon(true);
            thread.start();
          } else {
            logger.seek("关闭过滤器" + annotation.value() + "[" + annotation.priority() + "] -> " + instance.getClass().getName());
            instance.shutWrapper();
          }
        } catch (Exception exception) {
          logger.warn("关闭过滤器异常" + annotation.value() + "[" + annotation.priority() + "] -> " + instance.getClass().getName(), exception);
        }
      }

      logger.hint("关闭定时器");

      List<Runner> runners = new ArrayList<>(SORTED_RUNNER);
      Collections.reverse(runners);
      for (Runner annotation : runners) {
        EventHandlerRunner instance = COMPONENT_RUNNER_INSTANCE.get(annotation);
        if (!instance.isEnable()) {
          logger.seek("跳过定时器" + annotation.value() + "[" + annotation.priority() + "] -> " + instance.getClass().getName());
          continue;
        }
        try {
          if (STATE_SHUTDOWN_DROP) {
            logger.seek("丢弃定时器" + annotation.value() + "[" + annotation.priority() + "] -> " + instance.getClass().getName());
            Thread thread = new Thread(instance::shutWrapper);
            thread.setDaemon(true);
            thread.start();
          } else {
            logger.seek("关闭定时器" + annotation.value() + "[" + annotation.priority() + "] -> " + instance.getClass().getName());
            instance.shutWrapper();
          }
        } catch (Exception exception) {
          logger.warn("关闭定时器异常" + annotation.value() + "[" + annotation.priority() + "] -> " + instance.getClass().getName(), exception);
        }
      }

    }

    //= ========================================================================
    //= 模块管理
    //= ========================================================================

    //= ========================================================================
    //=  查询模块

    public Set<Map.Entry<String, Plugin>> getAllPlugin() {
      return plugins.entrySet();
    }

    public Set<String> listModuleName() {
      return modules.keySet();
    }

    public Map<Runner, Boolean> listRunner() {
      Map<Runner, Boolean> result = new LinkedHashMap<>();
      for (Runner annotation : COMPONENT_RUNNER_CLAZZ.keySet()) {
        result.put(annotation, COMPONENT_RUNNER_INSTANCE.containsKey(annotation));
      }
      return result;
    }

    public Map<Filter, Boolean> listFilter() {
      Map<Filter, Boolean> result = new LinkedHashMap<>();
      for (Filter annotation : COMPONENT_FILTER_CLAZZ.keySet()) {
        result.put(annotation, COMPONENT_FILTER_INSTANCE.containsKey(annotation));
      }
      return result;
    }

    public Map<Monitor, Boolean> listMonitor() {
      Map<Monitor, Boolean> result = new LinkedHashMap<>();
      for (Monitor annotation : COMPONENT_MONITOR_CLAZZ.keySet()) {
        result.put(annotation, COMPONENT_MONITOR_INSTANCE.containsKey(annotation));
      }
      return result;
    }

    public Map<Checker, Boolean> listChecker() {
      Map<Checker, Boolean> result = new LinkedHashMap<>();
      for (Checker annotation : COMPONENT_CHECKER_CLAZZ.keySet()) {
        result.put(annotation, COMPONENT_CHECKER_INSTANCE.containsKey(annotation));
      }
      return result;
    }

    public Map<Executor, Boolean> listExecutor() {
      Map<Executor, Boolean> result = new LinkedHashMap<>();
      for (Executor annotation : COMPONENT_EXECUTOR_CLAZZ.keySet()) {
        result.put(annotation, COMPONENT_EXECUTOR_INSTANCE.containsKey(annotation));
      }
      return result;
    }

    public List<Checker> listGlobalUsersChecker() {
      return GLOBAL_CHECKER_USERS_POOL.stream().map(item -> item.getClass().getAnnotation(Checker.class)).toList();
    }

    public List<Checker> listGlobalGroupChecker() {
      return GLOBAL_CHECKER_GROUP_POOL.stream().map(item -> item.getClass().getAnnotation(Checker.class)).toList();
    }

    public Map<String, List<Checker>> listCommandsUsersChecker() {
      Map<String, List<Checker>> result = new LinkedHashMap<>();
      for (Map.Entry<String, List<EventHandlerChecker>> entry : COMMAND_CHECKER_USERS_POOL.entrySet()) {
        var k = entry.getKey();
        var v = entry.getValue();
        List<Checker> collect = v.stream().map(item -> item.getClass().getAnnotation(Checker.class)).toList();
        result.put(k, collect);
      }
      return result;
    }

    public Map<String, List<Checker>> listCommandsGroupChecker() {
      Map<String, List<Checker>> result = new LinkedHashMap<>();
      for (Map.Entry<String, List<EventHandlerChecker>> entry : COMMAND_CHECKER_GROUP_POOL.entrySet()) {
        var k = entry.getKey();
        var v = entry.getValue();
        List<Checker> collect = v.stream().map(item -> item.getClass().getAnnotation(Checker.class)).toList();
        result.put(k, collect);
      }
      return result;
    }

    //= ========================================================================
    //=  预载模块模板

    private Class<? extends AbstractEventHandler> getModuleClass(String name) {

      if (!modules.containsKey(name)) return null;

      for (Map.Entry<Runner, Class<? extends EventHandlerRunner>> entry : COMPONENT_RUNNER_CLAZZ.entrySet()) {
        if (entry.getKey().value().equals(name)) return entry.getValue();
      }

      for (Map.Entry<Filter, Class<? extends EventHandlerFilter>> entry : COMPONENT_FILTER_CLAZZ.entrySet()) {
        if (entry.getKey().value().equals(name)) return entry.getValue();
      }

      for (Map.Entry<Monitor, Class<? extends EventHandlerMonitor>> entry : COMPONENT_MONITOR_CLAZZ.entrySet()) {
        if (entry.getKey().value().equals(name)) return entry.getValue();

      }

      for (Map.Entry<Checker, Class<? extends EventHandlerChecker>> entry : COMPONENT_CHECKER_CLAZZ.entrySet()) {
        if (entry.getKey().value().equals(name)) return entry.getValue();
      }

      for (Map.Entry<Executor, Class<? extends EventHandlerExecutor>> entry : COMPONENT_EXECUTOR_CLAZZ.entrySet()) {
        if (entry.getKey().value().equals(name)) return entry.getValue();
      }

      return null;
    }

    //= ========================================================================
    //=  获取模块实例

    private AbstractEventHandler getModuleInstanceEnsure(String name) {
      AbstractEventHandler instance = getModuleInstance(name);
      if (instance == null) {
        logger.info("没有找到模块实例 -> " + name + " " + (getModuleClass(name) == null ? "不存在" : "未加载"));
      }
      return instance;
    }

    private AbstractEventHandler getModuleInstance(String name) {

      if (!modules.containsKey(name)) return null;

      for (Map.Entry<Runner, EventHandlerRunner> entry : COMPONENT_RUNNER_INSTANCE.entrySet()) {
        if (entry.getKey().value().equals(name)) return entry.getValue();
      }

      for (Map.Entry<Filter, EventHandlerFilter> entry : COMPONENT_FILTER_INSTANCE.entrySet()) {
        if (entry.getKey().value().equals(name)) return entry.getValue();
      }

      for (Map.Entry<Monitor, EventHandlerMonitor> entry : COMPONENT_MONITOR_INSTANCE.entrySet()) {
        if (entry.getKey().value().equals(name)) return entry.getValue();
      }

      for (Map.Entry<Checker, EventHandlerChecker> entry : COMPONENT_CHECKER_INSTANCE.entrySet()) {
        if (entry.getKey().value().equals(name)) return entry.getValue();
      }

      for (Map.Entry<Executor, EventHandlerExecutor> entry : COMPONENT_EXECUTOR_INSTANCE.entrySet()) {
        if (entry.getKey().value().equals(name)) return entry.getValue();
      }

      return null;
    }

    //= ========================================================================
    //=  预载模块

    public void initModule(String name) {
      AbstractEventHandler moduleInstance = getModuleInstanceEnsure(name);
      if (moduleInstance == null) return;
      String instanceName = moduleInstance.getClass().getName();
      logger.info("预载模块 " + name + " -> " + instanceName);
      try {
        moduleInstance.initWrapper();
      } catch (Exception exception) {
        logger.warn("预载模块发生错误 " + name + " " + instanceName, exception);
      }
    }

    //= ========================================================================
    //=  启动模块

    public void bootModule(String name) {
      AbstractEventHandler moduleInstance = getModuleInstanceEnsure(name);
      if (moduleInstance == null) return;
      String instanceName = moduleInstance.getClass().getName();
      logger.info("启动模块 " + name + " -> " + instanceName);
      try {
        moduleInstance.bootWrapper();
      } catch (Exception exception) {
        logger.warn("启动模块发生错误 " + name + " " + instanceName, exception);
      }
    }

    //= ========================================================================
    //=  关闭模块

    public void shutModule(String name) {
      AbstractEventHandler moduleInstance = getModuleInstanceEnsure(name);
      if (moduleInstance == null) return;
      String instanceName = moduleInstance.getClass().getName();
      logger.info("关闭模块 " + name + " -> " + instanceName);
      try {
        moduleInstance.shutWrapper();
      } catch (Exception exception) {
        logger.warn("关闭模块发生错误 " + name + " " + instanceName, exception);
      }
    }

    //= ========================================================================
    //=  重启模块

    public void rebootModule(String name) {
      AbstractEventHandler moduleInstance = getModuleInstanceEnsure(name);
      if (moduleInstance == null) return;
      String instanceName = moduleInstance.getClass().getName();
      logger.info("重启模块 " + name + " -> " + instanceName);
      try {
        moduleInstance.shutWrapper();
        moduleInstance.initWrapper();
        moduleInstance.bootWrapper();
      } catch (Exception exception) {
        logger.warn("重启模块发生错误 " + name + " " + instanceName, exception);
      }
    }

    //= ========================================================================
    //=  卸载模块

    public void unloadModule(String name) {

      Class<? extends AbstractEventHandler> clazz = modules.get(name);

      if (clazz == null) {
        logger.warn("不存在此名称的模块 -> " + name);
        return;
      }

      for (Map.Entry<Runner, EventHandlerRunner> entry : COMPONENT_RUNNER_INSTANCE.entrySet()) {
        if (entry.getKey().value().equals(name)) {
          unloadModule(entry.getKey());
          return;
        }
      }

      for (Map.Entry<Filter, EventHandlerFilter> entry : COMPONENT_FILTER_INSTANCE.entrySet()) {
        if (entry.getKey().value().equals(name)) {
          unloadModule(entry.getKey());
          return;
        }
      }

      for (Map.Entry<Monitor, EventHandlerMonitor> entry : COMPONENT_MONITOR_INSTANCE.entrySet()) {
        if (entry.getKey().value().equals(name)) {
          unloadModule(entry.getKey());
          return;
        }
      }

      for (Map.Entry<Checker, EventHandlerChecker> entry : COMPONENT_CHECKER_INSTANCE.entrySet()) {
        if (entry.getKey().value().equals(name)) {
          unloadModule(entry.getKey());
          return;
        }
      }

      for (Map.Entry<Executor, EventHandlerExecutor> entry : COMPONENT_EXECUTOR_INSTANCE.entrySet()) {
        if (entry.getKey().value().equals(name)) {
          unloadModule(entry.getKey());
          return;
        }
      }

      logger.warn("此名称的模块未加载 -> " + name);

    }

    private void unloadModule(Runner annotation) {
      EventHandlerRunner instance = COMPONENT_RUNNER_INSTANCE.remove(annotation);
      instance.shutWrapper();
      logger.info("定时器已卸载 -> " + printAnnotation(annotation));
    }

    private void unloadModule(Filter annotation) {
      EventHandlerFilter instance = COMPONENT_FILTER_INSTANCE.remove(annotation);
      if (annotation.users()) FILTER_USERS_CHAIN.remove(instance);
      if (annotation.group()) FILTER_GROUP_CHAIN.remove(instance);
      instance.shutWrapper();
      logger.info("过滤器已卸载 -> " + printAnnotation(annotation));
    }

    private void unloadModule(Monitor annotation) {
      EventHandlerMonitor instance = COMPONENT_MONITOR_INSTANCE.remove(annotation);
      if (annotation.users()) MONITOR_USERS_CHAIN.remove(instance);
      if (annotation.group()) MONITOR_GROUP_CHAIN.remove(instance);
      instance.shutWrapper();
      logger.info("监听器已卸载 -> " + printAnnotation(annotation));
    }

    private void unloadModule(Checker annotation) {
      EventHandlerChecker instance = COMPONENT_CHECKER_INSTANCE.remove(annotation);
      if (annotation.users()) {
        if ("*".equals(annotation.command())) {
          GLOBAL_CHECKER_USERS_POOL.remove(instance);
        } else {
          COMMAND_CHECKER_USERS_POOL.get(annotation.command()).remove(instance);
        }
      }
      if (annotation.group()) {
        if ("*".equals(annotation.command())) {
          GLOBAL_CHECKER_GROUP_POOL.remove(instance);
        } else {
          COMMAND_CHECKER_GROUP_POOL.get(annotation.command()).remove(instance);
        }
      }
      instance.shutWrapper();
      logger.info("检查器已卸载 -> " + printAnnotation(annotation));
    }

    private void unloadModule(Executor annotation) {
      EventHandlerExecutor instance = COMPONENT_EXECUTOR_INSTANCE.remove(annotation);
      if (annotation.users()) EXECUTOR_USERS_POOL.remove(annotation.command());
      if (annotation.group()) EXECUTOR_GROUP_POOL.remove(annotation.command());
      COMMAND_EXECUTOR_RELATION.remove(annotation.command());
      instance.shutWrapper();
      logger.info("执行器已卸载 -> " + printAnnotation(annotation));
    }

    //= ========================================================================

    public boolean executeModule(String name, ModuleCommand command) {
      AbstractEventHandler instance = getModuleInstance(name);
      if (instance == null) {
        return false;
      }
      instance.executeWrapper(command);
      return true;
    }

    //= ========================================================================
    //= 调试信息
    //= ========================================================================

    @SuppressWarnings("DuplicatedCode")
    public String verboseStatus() {

      StringBuilder builder = new StringBuilder();

      builder.append(BRIGHT_MAGENTA).append(">> PLUGINS").append(RESET).append(LINE);

      for (Map.Entry<String, Plugin> entry : plugins.entrySet()) {
        var k = entry.getKey();
        var v = entry.getValue();
        builder
          .append(BRIGHT_CYAN)
          .append(k)
          .append(":")
          .append(toHumanHashCode(v))
          .append(" ")
          .append(v.getPath())
          .append(RESET)
          .append(LINE);
        for (Map.Entry<String, Class<? extends AbstractEventHandler>> classEntry : v.getModules().entrySet()) {
          var classK = classEntry.getKey();
          var classV = classEntry.getValue();
          builder
            .append(classK)
            .append(" -> ")
            .append(classV.getName())
            .append(":")
            .append(toHumanHashCode(classV))
            .append(LINE);
        }
      }

      builder.append(BRIGHT_MAGENTA).append(">> MODULES").append(RESET).append(LINE);

      for (Map.Entry<String, Class<? extends AbstractEventHandler>> entry : modules.entrySet()) {
        var k = entry.getKey();
        var v = entry.getValue();
        builder
          .append(k)
          .append(" -> ")
          .append(v.getName())
          .append(":")
          .append(toHumanHashCode(v))
          .append(LINE);
      }

      builder.append(BRIGHT_MAGENTA).append(">> MODULE_PLUGIN_RELATION").append(RESET).append(LINE);

      for (Map.Entry<String, String> entry : MODULE_PLUGIN_RELATION.entrySet()) {
        var k = entry.getKey();
        var v = entry.getValue();
        builder
          .append(k)
          .append(" -> ")
          .append(v)
          .append(LINE);
      }

      builder.append(BRIGHT_CYAN).append(">> COMPONENT_RUNNER_CLAZZ").append(RESET).append(LINE);

      for (Map.Entry<Runner, Class<? extends EventHandlerRunner>> entry : COMPONENT_RUNNER_CLAZZ.entrySet()) {
        var k = entry.getKey();
        var v = entry.getValue();
        builder
          .append(printAnnotation(k))
          .append(":")
          .append(toHumanHashCode(k))
          .append(" -> ")
          .append(v.getName())
          .append(":")
          .append(toHumanHashCode(v))
          .append(LINE);
      }

      builder.append(BRIGHT_CYAN).append(">> COMPONENT_FILTER_CLAZZ").append(RESET).append(LINE);

      for (Map.Entry<Filter, Class<? extends EventHandlerFilter>> entry : COMPONENT_FILTER_CLAZZ.entrySet()) {
        var k = entry.getKey();
        var v = entry.getValue();
        builder
          .append(printAnnotation(k))
          .append(":")
          .append(toHumanHashCode(k))
          .append(" -> ")
          .append(v.getName())
          .append(":")
          .append(toHumanHashCode(v))
          .append(LINE);
      }

      builder.append(BRIGHT_CYAN).append(">> COMPONENT_MONITOR_CLAZZ").append(RESET).append(LINE);

      for (Map.Entry<Monitor, Class<? extends EventHandlerMonitor>> entry : COMPONENT_MONITOR_CLAZZ.entrySet()) {
        var k = entry.getKey();
        var v = entry.getValue();
        builder
          .append(printAnnotation(k))
          .append(":")
          .append(toHumanHashCode(k))
          .append(" -> ")
          .append(v.getName())
          .append(":")
          .append(toHumanHashCode(v))
          .append(LINE);
      }

      builder.append(BRIGHT_CYAN).append(">> COMPONENT_CHECKER_CLAZZ").append(RESET).append(LINE);

      for (Map.Entry<Checker, Class<? extends EventHandlerChecker>> entry : COMPONENT_CHECKER_CLAZZ.entrySet()) {
        var k = entry.getKey();
        var v = entry.getValue();
        builder
          .append(printAnnotation(k))
          .append(":")
          .append(toHumanHashCode(k))
          .append(" -> ")
          .append(v.getName())
          .append(":")
          .append(toHumanHashCode(v))
          .append(LINE);
      }

      builder.append(BRIGHT_CYAN).append(">> COMPONENT_EXECUTOR_CLAZZ").append(RESET).append(LINE);

      for (Map.Entry<Executor, Class<? extends EventHandlerExecutor>> entry : COMPONENT_EXECUTOR_CLAZZ.entrySet()) {
        var k = entry.getKey();
        var v = entry.getValue();
        builder
          .append(printAnnotation(k))
          .append(":")
          .append(toHumanHashCode(k))
          .append(" -> ")
          .append(v.getName())
          .append(":")
          .append(toHumanHashCode(v))
          .append(LINE);
      }

      builder.append(BRIGHT_CYAN).append(">> SORTED_RUNNER").append(RESET).append(LINE);

      for (Runner entry : SORTED_RUNNER) {
        builder
          .append(printAnnotation(entry))
          .append(":")
          .append(toHumanHashCode(entry))
          .append(LINE);
      }

      builder.append(BRIGHT_CYAN).append(">> SORTED_FILTER").append(RESET).append(LINE);

      for (Filter entry : SORTED_FILTER) {
        builder
          .append(printAnnotation(entry))
          .append(":")
          .append(toHumanHashCode(entry))
          .append(LINE);
      }

      builder.append(BRIGHT_CYAN).append(">> SORTED_MONITOR").append(RESET).append(LINE);

      for (Monitor entry : SORTED_MONITOR) {
        builder
          .append(printAnnotation(entry))
          .append(":")
          .append(toHumanHashCode(entry))
          .append(LINE);
      }

      builder.append(BRIGHT_CYAN).append(">> SORTED_CHECKER").append(RESET).append(LINE);

      for (Checker entry : SORTED_CHECKER) {
        builder
          .append(printAnnotation(entry))
          .append(":")
          .append(toHumanHashCode(entry))
          .append(LINE);
      }

      builder.append(BRIGHT_CYAN).append(">> COMPONENT_RUNNER_INSTANCE").append(RESET).append(LINE);

      for (Map.Entry<Runner, EventHandlerRunner> entry : COMPONENT_RUNNER_INSTANCE.entrySet()) {
        var k = entry.getKey();
        var v = entry.getValue();
        builder
          .append(printAnnotation(k))
          .append(":")
          .append(toHumanHashCode(k))
          .append(" -> ")
          .append(v.getClass().getName())
          .append(":")
          .append(toHumanHashCode(v))
          .append(LINE);
      }

      builder.append(BRIGHT_CYAN).append(">> COMPONENT_FILTER_INSTANCE").append(RESET).append(LINE);

      for (Map.Entry<Filter, EventHandlerFilter> entry : COMPONENT_FILTER_INSTANCE.entrySet()) {
        var k = entry.getKey();
        var v = entry.getValue();
        builder
          .append(printAnnotation(k))
          .append(":")
          .append(toHumanHashCode(k))
          .append(" -> ")
          .append(v.getClass().getName())
          .append(":")
          .append(toHumanHashCode(v))
          .append(LINE);
      }

      builder.append(BRIGHT_CYAN).append(">> COMPONENT_MONITOR_INSTANCE").append(RESET).append(LINE);

      for (Map.Entry<Monitor, EventHandlerMonitor> entry : COMPONENT_MONITOR_INSTANCE.entrySet()) {
        var k = entry.getKey();
        var v = entry.getValue();
        builder
          .append(printAnnotation(k))
          .append(":")
          .append(toHumanHashCode(k))
          .append(" -> ")
          .append(v.getClass().getName())
          .append(":")
          .append(toHumanHashCode(v))
          .append(LINE);
      }

      builder.append(BRIGHT_CYAN).append(">> COMPONENT_CHECKER_INSTANCE").append(RESET).append(LINE);

      for (Map.Entry<Checker, EventHandlerChecker> entry : COMPONENT_CHECKER_INSTANCE.entrySet()) {
        var k = entry.getKey();
        var v = entry.getValue();
        builder
          .append(printAnnotation(k))
          .append(":")
          .append(toHumanHashCode(k))
          .append(" -> ")
          .append(v.getClass().getName())
          .append(":")
          .append(toHumanHashCode(v))
          .append(LINE);
      }

      builder.append(BRIGHT_CYAN).append(">> COMPONENT_EXECUTOR_INSTANCE").append(RESET).append(LINE);

      for (Map.Entry<Executor, EventHandlerExecutor> entry : COMPONENT_EXECUTOR_INSTANCE.entrySet()) {
        var k = entry.getKey();
        var v = entry.getValue();
        builder
          .append(printAnnotation(k))
          .append(":")
          .append(toHumanHashCode(k))
          .append(" -> ")
          .append(v.getClass().getName())
          .append(":")
          .append(toHumanHashCode(v))
          .append(LINE);
      }

      builder.append(BRIGHT_CYAN).append(">> FILTER_USERS_CHAIN").append(RESET).append(LINE);

      for (EventHandlerFilter item : FILTER_USERS_CHAIN) {
        builder
          .append(item.getClass().getName())
          .append(":")
          .append(toHumanHashCode(item))
          .append(LINE);
      }

      builder.append(BRIGHT_CYAN).append(">> FILTER_GROUP_CHAIN").append(RESET).append(LINE);

      for (EventHandlerFilter item : FILTER_GROUP_CHAIN) {
        builder
          .append(item.getClass().getName())
          .append(":")
          .append(toHumanHashCode(item))
          .append(LINE);
      }

      builder.append(BRIGHT_CYAN).append(">> MONITOR_USERS_CHAIN").append(RESET).append(LINE);

      for (EventHandlerMonitor item : MONITOR_USERS_CHAIN) {
        builder
          .append(item.getClass().getName())
          .append(":")
          .append(toHumanHashCode(item))
          .append(LINE);
      }

      builder.append(BRIGHT_CYAN).append(">> MONITOR_GROUP_CHAIN").append(RESET).append(LINE);

      for (EventHandlerMonitor item : MONITOR_GROUP_CHAIN) {
        builder
          .append(item.getClass().getName())
          .append(":")
          .append(toHumanHashCode(item))
          .append(LINE);
      }

      builder.append(BRIGHT_CYAN).append(">> GLOBAL_CHECKER_USERS_POOL").append(RESET).append(LINE);

      for (EventHandlerChecker item : GLOBAL_CHECKER_USERS_POOL) {
        builder
          .append(item.getClass().getName())
          .append(":")
          .append(toHumanHashCode(item))
          .append(LINE);
      }

      builder.append(BRIGHT_CYAN).append(">> GLOBAL_CHECKER_GROUP_POOL").append(RESET).append(LINE);

      for (EventHandlerChecker item : GLOBAL_CHECKER_GROUP_POOL) {
        builder
          .append(item.getClass().getName())
          .append(":")
          .append(toHumanHashCode(item))
          .append(LINE);
      }

      builder.append(BRIGHT_CYAN).append(">> COMMAND_CHECKER_USERS_POOL").append(RESET).append(LINE);

      for (Map.Entry<String, List<EventHandlerChecker>> entry : COMMAND_CHECKER_USERS_POOL.entrySet()) {
        var k = entry.getKey();
        var v = entry.getValue();
        builder
          .append(CYAN)
          .append(k)
          .append(RESET)
          .append(" ")
          .append(v.size())
          .append(LINE);
        for (EventHandlerChecker checker : v) {
          builder
            .append(checker.getClass().getName())
            .append(":")
            .append(toHumanHashCode(checker))
            .append(LINE);
        }
      }

      builder.append(BRIGHT_CYAN).append(">> COMMAND_CHECKER_GROUP_POOL").append(RESET).append(LINE);

      for (Map.Entry<String, List<EventHandlerChecker>> entry : COMMAND_CHECKER_GROUP_POOL.entrySet()) {
        var k = entry.getKey();
        var v = entry.getValue();
        builder
          .append(CYAN)
          .append(k)
          .append(RESET)
          .append(" ")
          .append(v.size())
          .append(LINE);
        for (EventHandlerChecker checker : v) {
          builder
            .append(checker.getClass().getName())
            .append(":")
            .append(toHumanHashCode(checker))
            .append(LINE);
        }
      }

      builder.append(BRIGHT_CYAN).append(">> EXECUTOR_USERS_POOL").append(RESET).append(LINE);

      for (Map.Entry<String, EventHandlerExecutor> entry : EXECUTOR_USERS_POOL.entrySet()) {
        var k = entry.getKey();
        var v = entry.getValue();
        builder
          .append(k)
          .append(" -> ")
          .append(v.getClass().getName())
          .append(":")
          .append(toHumanHashCode(v))
          .append(LINE);
      }

      builder.append(BRIGHT_CYAN).append(">> EXECUTOR_GROUP_POOL").append(RESET).append(LINE);

      for (Map.Entry<String, EventHandlerExecutor> entry : EXECUTOR_GROUP_POOL.entrySet()) {
        var k = entry.getKey();
        var v = entry.getValue();
        builder
          .append(k)
          .append(" -> ")
          .append(v.getClass().getName())
          .append(":")
          .append(toHumanHashCode(v))
          .append(LINE);
      }

      builder.append(BRIGHT_CYAN).append(">> COMMAND_EXECUTOR_RELATION").append(RESET).append(LINE);

      for (Map.Entry<String, Executor> entry : COMMAND_EXECUTOR_RELATION.entrySet()) {
        var k = entry.getKey();
        var v = entry.getValue();
        builder
          .append(CYAN)
          .append(k)
          .append(RESET)
          .append(" -> ")
          .append(v.value())
          .append(":")
          .append(toHumanHashCode(v))
          .append(" {")
          .append(v.users() ? "U" : "")
          .append(v.group() ? "G" : "")
          .append("} ")
          .append(v.outline())
          .append(":")
          .append(v.description())
          .append(LINE);
        for (String temp : v.usage()) {
          builder
            .append(temp)
            .append(LINE);
        }
        for (String temp : v.privacy()) {
          builder
            .append(temp)
            .append(LINE);
        }
      }

      return builder.toString();

    }

    private static final class Plugin {

      private static final Pattern PATTERN = Pattern.compile("^[\\da-z_-]{8,64}$");

      private final LoggerX logger;

      private final Path path;
      private final String name;

      private URLClassLoader dependClassLoader;
      private URLClassLoader pluginClassLoader;

      private Map<String, Class<? extends AbstractEventHandler>> modules;
      private Map<Runner, Class<? extends EventHandlerRunner>> runnerClassMap;
      private Map<Filter, Class<? extends EventHandlerFilter>> filterClassMap;
      private Map<Monitor, Class<? extends EventHandlerMonitor>> monitorClassMap;
      private Map<Checker, Class<? extends EventHandlerChecker>> checkerClassMap;
      private Map<Executor, Class<? extends EventHandlerExecutor>> executorClassMap;

      public static Plugin load(Path path) {

        String name;

        try (JarFile jarFile = new JarFile(path.toFile())) {

          Manifest manifest;
          try {
            manifest = jarFile.getManifest();
          } catch (IOException exception) {
            throw new SchemaException("加载MANIFEST失败 -> " + path, exception);
          }

          Attributes attributes = manifest.getAttributes("FurryBlack-Extension");
          if (attributes == null || attributes.isEmpty()) {
            throw new SchemaException("加载插件失败: MANIFEST不包含FurryBlack-Extension标签组");
          }

          String loaderVersion = attributes.getValue("Loader-Version");

          if (loaderVersion == null) {
            throw new SchemaException("加载插件失败: MANIFEST中FurryBlack-Extension标签组不含Loader-Version");
          }

          if (!"1".equals(loaderVersion)) {
            throw new SchemaException("加载插件失败: 加载器版本不符, 此插件声明其版本为 " + loaderVersion);
          }

          name = attributes.getValue("Extension-Name");

          if (name == null) {
            throw new SchemaException("加载插件失败: MANIFEST中FurryBlack-Extension标签组不含Extension-Name");
          }

          if (!PATTERN.matcher(name).find()) {
            throw new SchemaException("加载插件失败: 插件包名非法, 此插件声明其名称为 " + name);
          }

        } catch (IOException | SchemaException exception) {
          throw new SchemaException(exception);
        }

        Plugin plugin;
        try {
          plugin = new Plugin(path, name);
        } catch (Exception exception) {
          throw new SchemaException(exception);
        }
        return plugin;
      }

      //= ==================================================================================================================

      private Plugin(Path path, String name) {

        this.path = path;
        this.name = name;

        logger = LoggerXFactory.getLogger(name);

      }

      @SuppressWarnings("unchecked")
      public void scan() {

        //= ==================================================================================================================

        Path depend = FileEnhance.get(FOLDER_DEPEND, name);

        //= ==================================================================================================================

        List<URL> tempURL = new LinkedList<>();

        try (JarFile jarFile = new JarFile(path.toFile())) {

          if (Files.exists(depend)) {

            if (!Files.isDirectory(depend)) {
              throw new SchemaException("依赖文件不是目录 -> " + depend);
            }

            List<Path> dependFiles;

            try (Stream<Path> stream = Files.list(depend)) {
              dependFiles = stream.toList();
            } catch (IOException exception) {
              throw new SchemaException("列出依赖文件失败 -> " + depend);
            }

            for (Path dependFile : dependFiles) {
              if (Files.isRegularFile(dependFile)) {
                URL url = dependFile.toUri().toURL();
                tempURL.add(url);
              }
            }
          }

          URL[] urls = tempURL.toArray(new URL[0]);

          logger.seek("加载依赖 -> " + depend + "[" + urls.length + "]");

          dependClassLoader = new URLClassLoader(urls); // Inject with systemClassLoader in default

          URL pluginURL = path.toUri().toURL();

          pluginClassLoader = new URLClassLoader(new URL[]{pluginURL}, dependClassLoader);

          Map<String, Class<? extends EventHandlerExecutor>> commands = new HashMap<>();

          Enumeration<JarEntry> entries = jarFile.entries();

          //= ==================================================================================================================

          modules = new LinkedHashMap<>();
          runnerClassMap = new LinkedHashMap<>();
          filterClassMap = new LinkedHashMap<>();
          monitorClassMap = new LinkedHashMap<>();
          checkerClassMap = new LinkedHashMap<>();
          executorClassMap = new LinkedHashMap<>();

          //= ==================================================================================================================

          while (entries.hasMoreElements()) {

            JarEntry jarEntry = entries.nextElement();

            if (jarEntry.isDirectory()) {
              continue;
            }

            String jarEntryName = jarEntry.getName();

            if (!jarEntryName.endsWith(".class")) {
              continue;
            }

            String className = jarEntryName.substring(0, jarEntryName.length() - 6).replace("/", ".");

            //= ==================================================================================================================

            Class<?> clazz;

            try {
              clazz = Class.forName(className, false, pluginClassLoader);
            } catch (ClassNotFoundException exception) {
              logger.warn("加载类失败 " + name + ":" + className, exception);
              continue;
            }

            if (!AbstractEventHandler.class.isAssignableFrom(clazz)) {
              continue;
            }

            String clazzName = clazz.getName();

            //= ==================================================================================================================

            if (EventHandlerRunner.class.isAssignableFrom(clazz)) {

              if (!clazz.isAnnotationPresent(Runner.class)) {
                logger.warn("发现无注解模块 不予注册 " + name);
                continue;
              }

              Runner annotation = clazz.getAnnotation(Runner.class);

              String moduleName = annotation.value();

              if (modules.containsKey(moduleName)) {
                Class<? extends AbstractEventHandler> exist = modules.get(moduleName);
                logger.warn("发现自冲突 " + clazz.getName() + " " + moduleName + " " + exist.getName());
                logger.warn("不予注册插件 " + name);
                throw new SchemaException("发现垃圾插件 包含自冲突");
              }

              modules.put(moduleName, (Class<? extends AbstractEventHandler>) clazz);
              runnerClassMap.put(annotation, (Class<? extends EventHandlerRunner>) clazz);
              logger.info("定时器 -> " + clazzName);

              continue;

            } else if (EventHandlerFilter.class.isAssignableFrom(clazz)) {

              if (!clazz.isAnnotationPresent(Filter.class)) {
                logger.warn("发现无注解模块 不予注册 " + name);
                continue;
              }

              Filter annotation = clazz.getAnnotation(Filter.class);

              String moduleName = annotation.value();

              if (modules.containsKey(moduleName)) {
                Class<? extends AbstractEventHandler> exist = modules.get(moduleName);
                logger.warn("发现自冲突 " + clazz.getName() + " " + moduleName + " " + exist.getName());
                logger.warn("不予注册插件 " + name);
                throw new SchemaException("发现垃圾插件 包含自冲突");
              }

              if (annotation.users() || annotation.group()) {
                modules.put(moduleName, (Class<? extends AbstractEventHandler>) clazz);
                filterClassMap.put(annotation, (Class<? extends EventHandlerFilter>) clazz);
                logger.info("过滤器 -> " + clazzName);
              } else {
                logger.warn("发现未启用过滤器 " + clazzName);
              }

              continue;

            } else if (EventHandlerMonitor.class.isAssignableFrom(clazz)) {

              if (!clazz.isAnnotationPresent(Monitor.class)) {
                logger.warn("发现无注解模块 不予注册 " + name);
                continue;
              }

              Monitor annotation = clazz.getAnnotation(Monitor.class);

              String moduleName = annotation.value();

              if (modules.containsKey(moduleName)) {
                Class<? extends AbstractEventHandler> exist = modules.get(moduleName);
                logger.warn("发现自冲突 " + clazz.getName() + " " + moduleName + " " + exist.getName());
                logger.warn("不予注册插件 " + name);
                throw new SchemaException("发现垃圾插件 包含自冲突");
              }

              if (annotation.users() || annotation.group()) {
                modules.put(moduleName, (Class<? extends AbstractEventHandler>) clazz);
                monitorClassMap.put(annotation, (Class<? extends EventHandlerMonitor>) clazz);
                logger.info("监视器 -> " + clazzName);
              } else {
                logger.warn("发现未启用监听器 " + clazz.getName());
              }

              continue;

            } else if (EventHandlerChecker.class.isAssignableFrom(clazz)) {

              if (!clazz.isAnnotationPresent(Checker.class)) {
                logger.warn("发现无注解模块 不予注册 " + name);
                continue;
              }

              Checker annotation = clazz.getAnnotation(Checker.class);

              String moduleName = annotation.value();

              if (modules.containsKey(moduleName)) {
                Class<? extends AbstractEventHandler> exist = modules.get(moduleName);
                logger.warn("发现自冲突 " + clazz.getName() + " " + moduleName + " " + exist.getName());
                logger.warn("不予注册插件 " + name);
                throw new SchemaException("发现垃圾插件 包含自冲突");
              }

              if (annotation.users() || annotation.group()) {
                modules.put(moduleName, (Class<? extends AbstractEventHandler>) clazz);
                checkerClassMap.put(annotation, (Class<? extends EventHandlerChecker>) clazz);
                logger.info("检查器 -> " + clazzName);
              } else {
                logger.warn("发现未启用检查器 " + clazz.getName());
              }

              continue;

            } else if (EventHandlerExecutor.class.isAssignableFrom(clazz)) {

              if (!clazz.isAnnotationPresent(Executor.class)) {
                logger.warn("发现无注解模块 不予注册 " + name);
                continue;
              }

              Executor annotation = clazz.getAnnotation(Executor.class);

              String moduleName = annotation.value();

              if (modules.containsKey(moduleName)) {
                Class<? extends AbstractEventHandler> exist = modules.get(moduleName);
                logger.warn("发现自冲突 " + clazz.getName() + " " + moduleName + " " + exist.getName());
                logger.warn("不予注册插件 " + name);
                throw new SchemaException("发现垃圾插件 包含自冲突");
              }

              String command = annotation.command();

              if (commands.containsKey(command)) {
                Class<? extends EventHandlerExecutor> exist = commands.get(command);
                logger.warn("发现自冲突命令 " + command + " " + clazz.getName() + " " + moduleName + " " + exist.getName());
                logger.warn("不予注册插件 " + name);
                throw new SchemaException("发现垃圾插件 包含自冲突");
              }

              if (annotation.users() || annotation.group()) {
                commands.put(command, (Class<? extends EventHandlerExecutor>) clazz);
                modules.put(moduleName, (Class<? extends AbstractEventHandler>) clazz);
                executorClassMap.put(annotation, (Class<? extends EventHandlerExecutor>) clazz);
                logger.info("执行器 -> " + clazzName);
              } else {
                logger.warn("发现未启用执行器 " + clazzName);
              }

              continue;

            }

            logger.warn("不支持自行创建的分支模块 不予注册 " + name + ":" + className);

          }

        } catch (IOException exception) {
          throw new SchemaException(exception);
        }
      }

      public String getName() {
        return name;
      }

      public Path getPath() {
        return path;
      }

      public Map<String, Class<? extends AbstractEventHandler>> getModules() {
        return modules;
      }

      public Map<Runner, Class<? extends EventHandlerRunner>> getRunnerClassMap() {
        return runnerClassMap;
      }

      public Map<Filter, Class<? extends EventHandlerFilter>> getFilterClassMap() {
        return filterClassMap;
      }

      public Map<Monitor, Class<? extends EventHandlerMonitor>> getMonitorClassMap() {
        return monitorClassMap;
      }

      public Map<Checker, Class<? extends EventHandlerChecker>> getCheckerClassMap() {
        return checkerClassMap;
      }

      public Map<Executor, Class<? extends EventHandlerExecutor>> getExecutorClassMap() {
        return executorClassMap;
      }

      public URLClassLoader getDependClassLoader() {
        return dependClassLoader;
      }

      @SuppressWarnings("unused")
      public URLClassLoader getPluginClassLoader() {
        return pluginClassLoader;
      }
    }

  }

  //= ==================================================================================================================
  //=
  //= 公共API
  //=
  //= ==================================================================================================================

  //= ==========================================================================
  //= 打印消息

  @Comment("在终端打印消息")
  public static void terminalPrint(Object message) {
    if (message == null) return;
    TERMINAL.print(message.toString());
  }

  @Comment("在终端打印消息")
  public static void terminalPrintln(Object message) {
    if (message == null) return;
    TERMINAL.println(message.toString());
  }

  //= ==========================================================================
  //= 框架状态

  @Comment("框架运行状态")
  public static boolean isDebug() {
    return STATE_SYSTEM_DEBUG;
  }

  @Comment("框架运行状态")
  public static boolean isShutdownHalt() {
    return STATE_SYSTEM_DEBUG_HALT;
  }

  @Comment("框架运行状态")
  public static boolean isShutModeDrop() {
    return STATE_SHUTDOWN_DROP;
  }

  //= ==========================================================================
  //= 框架相关

  @Comment("框架相关")
  public static Path getFolderRoot() {
    return FOLDER_ROOT;
  }

  @Comment("框架相关")
  public static Path getFolderConfig() {
    return FOLDER_CONFIG;
  }

  @Comment("框架相关")
  public static Path getFolderPlugin() {
    return FOLDER_PLUGIN;
  }

  @Comment("框架相关")
  public static Path getDependFolder() {
    return FOLDER_DEPEND;
  }

  @Comment("框架相关")
  public static Path getFolderDepend() {
    return FOLDER_DEPEND;
  }

  @Comment("框架相关")
  public static Path getFolderModule() {
    return FOLDER_MODULE;
  }

  @Comment("框架相关")
  public static Path getFolderLogger() {
    return FOLDER_LOGGER;
  }

  //= ==========================================================================
  //= 插件子系统

  @Comment("获取模块实例")
  public static <T extends EventHandlerRunner> T getRunner(Class<T> clazz) {
    return SCHEMA.getRunner(clazz);
  }

  //= ==========================================================================
  //= 线程池子系统

  @Comment("提交异步任务")
  public static Future<?> submit(Runnable runnable) {
    return MONITOR_PROCESS.submit(runnable);
  }

  @Comment("提交异步任务")
  public static <T> Future<?> submit(Runnable runnable, T t) {
    return MONITOR_PROCESS.submit(runnable, t);
  }

  @Comment("提交异步任务")
  public static Future<?> submit(Callable<?> callable) {
    return MONITOR_PROCESS.submit(callable);
  }

  @Comment("提交定时任务")
  public static ScheduledFuture<?> schedule(Runnable runnable, long time) {
    return SCHEDULE_SERVICE.schedule(runnable, time, TimeUnit.MILLISECONDS);
  }

  @Comment("提交定时任务")
  public static ScheduledFuture<?> schedule(Callable<?> callable, long delay) {
    return SCHEDULE_SERVICE.schedule(callable, delay, TimeUnit.MILLISECONDS);
  }

  @Comment("提交等间隔定时任务")
  public static ScheduledFuture<?> scheduleAtFixedRate(Runnable runnable, long initialDelay, long period) {
    return SCHEDULE_SERVICE.scheduleAtFixedRate(runnable, initialDelay, period, TimeUnit.MILLISECONDS);
  }

  @Comment("提交等延迟定时任务")
  public static ScheduledFuture<?> scheduleWithFixedDelay(Runnable runnable, long initialDelay, long delay) {
    return SCHEDULE_SERVICE.scheduleWithFixedDelay(runnable, initialDelay, delay, TimeUnit.MILLISECONDS);
  }

  @Comment("提交定时任务")
  public static ScheduledFuture<?> schedule(Runnable runnable, long time, TimeUnit unit) {
    return SCHEDULE_SERVICE.schedule(runnable, time, unit);
  }

  @Comment("提交定时任务")
  public static ScheduledFuture<?> schedule(Callable<?> callable, long delay, TimeUnit unit) {
    return SCHEDULE_SERVICE.schedule(callable, delay, unit);
  }

  @Comment("提交等间隔定时任务")
  public static ScheduledFuture<?> scheduleAtFixedRate(Runnable runnable, long initialDelay, long period, TimeUnit unit) {
    return SCHEDULE_SERVICE.scheduleAtFixedRate(runnable, initialDelay, period, unit);
  }

  @Comment("提交等延迟定时任务")
  public static ScheduledFuture<?> scheduleWithFixedDelay(Runnable runnable, long initialDelay, long delay, TimeUnit unit) {
    return SCHEDULE_SERVICE.scheduleWithFixedDelay(runnable, initialDelay, delay, unit);
  }

  //= ==========================================================================
  //= 昵称子系统

  @Comment("获取用户昵称")
  public static String getNickName(long user) {
    return queryProfile(user).getNickname();
  }

  @Comment("获取用户格式化名")
  public static String getFormattedNickName(User user) {
    return user.getNick() + "(" + user.getId() + ")";
  }

  @Comment("获取用户格式化名")
  public static String getFormattedNickName(long user) {
    return getNickName(user) + "(" + user + ")";
  }

  @Comment("获取用户昵称")
  public static String getUsersMappedNickName(User user) {
    return NICKNAME.getUsersMappedNickName(user);
  }

  @Comment("获取用户昵称")
  public static String getUsersMappedNickName(long userId) {
    return NICKNAME.getUsersMappedNickName(userId);
  }

  @Comment("获取预设昵称")
  public static String getMappedNickName(GroupMessageEvent event) {
    return NICKNAME.getMemberMappedNickName(event.getSender());
  }

  @Comment("获取预设昵称")
  public static String getMemberMappedNickName(Member member) {
    return NICKNAME.getMemberMappedNickName(member);
  }

  @Comment("获取预设昵称")
  public static String getMappedNickName(long groupId, long userId) {
    return NICKNAME.getMemberMappedNickName(groupId, userId);
  }

  //= ==========================================================================

  @Comment("发送私聊消息")
  public static void sendMessage(User user, Message message) {
    Objects.requireNonNull(user).sendMessage(message);
  }

  @Comment("发送私聊消息")
  public static void sendMessage(User user, String message) {
    sendMessage(user, new PlainText(message));
  }

  @Comment("发送私聊消息")
  public static void sendMessage(UserMessageEvent event, Message message) {
    sendMessage(event.getSender(), message);
  }

  @Comment("发送私聊消息")
  public static void sendMessage(UserMessageEvent event, String message) {
    sendMessage(event, new PlainText(message));
  }

  @Comment("发送私聊消息")
  public static void sendUserMessage(long id, Message message) {
    User user = getFriend(id);
    if (user == null) user = getStrangerOrFail(id);
    sendMessage(user, message);
  }

  @Comment("发送私聊消息")
  public static void sendUserMessage(long id, String message) {
    sendUserMessage(id, new PlainText(message));
  }

  //= ==========================================================================

  @Comment("发送群组消息")
  public static void sendMessage(Group group, Message message) {
    Objects.requireNonNull(group).sendMessage(message);
  }

  @Comment("发送群组消息")
  public static void sendMessage(Group group, String message) {
    sendMessage(group, new PlainText(message));
  }

  @Comment("发送群组消息")
  public static void sendMessage(GroupMessageEvent event, Message message) {
    sendMessage(event.getGroup(), message);
  }

  @Comment("发送群组消息")
  public static void sendMessage(GroupMessageEvent event, String message) {
    sendMessage(event, new PlainText(message));
  }

  @Comment("发送群组消息")
  public static void sendGroupMessage(long group, Message message) {
    sendMessage(getGroupOrFail(group), message);
  }

  @Comment("发送群组消息")
  public static void sendGroupMessage(long group, String message) {
    sendGroupMessage(group, new PlainText(message));
  }

  //= ==========================================================================

  @Comment("发送群组消息")
  public static void sendAtMessage(Group group, Member member, Message message) {
    sendMessage(group, new At(member.getId()).plus(message));
  }

  @Comment("发送群组消息")
  public static void sendAtMessage(Group group, Member member, String message) {
    sendAtMessage(group, member, new PlainText(message));
  }

  @Comment("发送群组消息")
  public static void sendAtMessage(GroupMessageEvent event, Message message) {
    sendAtMessage(event.getGroup(), event.getSender(), message);
  }

  @Comment("发送群组消息")
  public static void sendAtMessage(GroupMessageEvent event, String message) {
    sendAtMessage(event, new PlainText(message));
  }

  @Comment("发送群组消息")
  public static void sendAtMessage(long group, long member, Message message) {
    Group groupOrFail = getGroupOrFail(group);
    Member memberOrFail = groupOrFail.getOrFail(member);
    sendAtMessage(groupOrFail, memberOrFail, message);
  }

  @Comment("发送群组消息")
  public static void sendAtMessage(long group, long member, String message) {
    Group groupOrFail = getGroupOrFail(group);
    Member memberOrFail = groupOrFail.getOrFail(member);
    sendAtMessage(groupOrFail, memberOrFail, new PlainText(message));
  }

  //= ==========================================================================
  //= 机器人功能

  @Comment("获取Mirai机器人实例 只有--unsafe模式下可以使用 并且必须在启动完成前调用")
  public static Bot getBot() {
    if (CONFIG.systemDebugUnsafe) {
      if (STATE_BOOTING) return BOT;
      logger.warn("获取机器人实例禁止 并且必须在启动完成前调用");
      for (StackTraceElement stackTraceElement : Thread.currentThread().getStackTrace()) {
        System.out.println(stackTraceElement);
      }
      throw new KernelException("Get Mirai-BOT instance only allowed before booted.");
    } else {
      logger.warn("获取机器人实例禁止 只有在unsafe模式下可用");
      for (StackTraceElement stackTraceElement : Thread.currentThread().getStackTrace()) {
        System.out.println(stackTraceElement);
      }
      throw new KernelException("Get Mirai-BOT instance only allowed unsafe enabled.");
    }
  }

  @Comment("格式化群组信息")
  public static String getGroupInfo(Group group) {
    return group.getName() + "(" + group.getId() + ") " + group.getMembers().size() + " -> " + group.getOwner().getNameCard() + "(" + group.getOwner().getId() + ")";
  }

  @Comment("获取BOT自身QQ号")
  public static long getBotID() {
    return BOT.getId();
  }

  @Comment("列出所有好友")
  public static ContactList<Friend> getFriends() {
    return BOT.getFriends();
  }

  @Comment("列出所有群组")
  public static ContactList<Group> getGroups() {
    return BOT.getGroups();
  }

  @Comment("根据ID获取陌生人")
  public static Stranger getStranger(long id) {
    return BOT.getStranger(id);
  }

  @Comment("根据ID获取陌生人")
  public static Stranger getStrangerOrFail(long id) {
    return BOT.getStrangerOrFail(id);
  }

  @Comment("根据ID获取好友")
  public static Friend getFriend(long id) {
    return BOT.getFriend(id);
  }

  @Comment("根据ID获取好友")
  public static Friend getFriendOrFail(long id) {
    return BOT.getFriendOrFail(id);
  }

  @Comment("根据ID获取群组")
  public static Group getGroup(long id) {
    return BOT.getGroup(id);
  }

  @Comment("根据ID获取群组")
  public static Group getGroupOrFail(long id) {
    return BOT.getGroupOrFail(id);
  }

  @Comment("根据ID获取成员")
  public static NormalMember getMemberOrFail(long group, long member) {
    return getGroupOrFail(group).getOrFail(member);
  }

  @Comment("获取图片的URL")
  public static String getImageURL(Image image) {
    return queryImageUrl(image);
  }

  @Comment("获取图片的URL")
  public static String getImageURL(FlashImage flashImage) {
    return queryImageUrl(flashImage.getImage());
  }

  //= ==========================================================================
  //= 来自 IMirai.kt

  @Comment("转发Mirai")
  public static List<ForwardMessage.Node> downloadForwardMessage(String resourceId) {
    return Mirai.getInstance().downloadForwardMessage(BOT, resourceId);
  }

  @Comment("转发Mirai")
  public static MessageChain downloadLongMessage(String resourceId) {
    return Mirai.getInstance().downloadLongMessage(BOT, resourceId);
  }

  @Comment("转发Mirai")
  public static List<OtherClientInfo> getOnlineOtherClientsList(boolean mayIncludeSelf) {
    return Mirai.getInstance().getOnlineOtherClientsList(BOT, mayIncludeSelf);
  }

  @Comment("转发Mirai")
  public static long getUin() {
    return Mirai.getInstance().getUin(BOT);
  }

  @Comment("转发Mirai")
  public static String queryImageUrl(Image image) {
    return Mirai.getInstance().queryImageUrl(BOT, image);
  }

  @Comment("转发Mirai")
  public static UserProfile queryProfile(long id) {
    return Mirai.getInstance().queryProfile(BOT, id);
  }

  @Comment("转发Mirai")
  public static void recallMessage(MessageSource messageSource) {
    Mirai.getInstance().recallMessage(BOT, messageSource);
  }

  @Comment("转发Mirai")
  public static void sendNudge(Nudge nudge, Contact contact) {
    Mirai.getInstance().sendNudge(BOT, nudge, contact);
  }

  //= ========================================================================
  //= 来自 LowLevelApiAccessor.kt

  @Comment("转发Mirai")
  public static void getGroupVoiceDownloadUrl(byte[] md5, long groupId, long dstUin) {
    Mirai.getInstance().getGroupVoiceDownloadUrl(BOT, md5, groupId, dstUin);
  }

  @Comment("转发Mirai")
  public static Sequence<Long> getRawGroupList() {
    return Mirai.getInstance().getRawGroupList(BOT);
  }

  @Comment("转发Mirai")
  public static Sequence<MemberInfo> getRawGroupMemberList(long groupUin, long groupCode, long ownerId) {
    return Mirai.getInstance().getRawGroupMemberList(BOT, groupUin, groupCode, ownerId);
  }

  @Comment("转发Mirai")
  public static void muteAnonymousMember(String anonymousId, String anonymousNick, long groupId, int seconds) {
    Mirai.getInstance().muteAnonymousMember(BOT, anonymousId, anonymousNick, groupId, seconds);
  }

  @Comment("转发Mirai")
  public static Friend newFriend(FriendInfo friendInfo) {
    return Mirai.getInstance().newFriend(BOT, friendInfo);
  }

  @Comment("转发Mirai")
  public static Stranger newStranger(StrangerInfo strangerInfo) {
    return Mirai.getInstance().newStranger(BOT, strangerInfo);
  }

  @Comment("转发Mirai")
  public static boolean recallFriendMessageRaw(long targetId, int[] messagesIds, int[] messageInternalIds, int time) {
    return Mirai.getInstance().recallFriendMessageRaw(BOT, targetId, messagesIds, messageInternalIds, time);
  }

  @Comment("转发Mirai")
  public static boolean recallGroupMessageRaw(long groupCode, int[] messagesIds, int[] messageInternalIds) {
    return Mirai.getInstance().recallGroupMessageRaw(BOT, groupCode, messagesIds, messageInternalIds);
  }

  @Comment("转发Mirai")
  public static boolean recallGroupTempMessageRaw(long groupUin, long targetId, int[] messagesIds, int[] messageInternalIds, int time) {
    return Mirai.getInstance().recallGroupTempMessageRaw(BOT, groupUin, targetId, messagesIds, messageInternalIds, time);
  }

  @Comment("转发Mirai")
  public static void refreshKeys() {
    Mirai.getInstance().refreshKeys(BOT);
  }

  @Comment("转发Mirai")
  public static void solveBotInvitedJoinGroupRequestEvent(long eventId, long invitorId, long groupId, boolean accept) {
    Mirai.getInstance().solveBotInvitedJoinGroupRequestEvent(BOT, eventId, invitorId, groupId, accept);
  }

  @Comment("转发Mirai")
  public static void solveMemberJoinRequestEvent(long eventId, long fromId, String fromNick, long groupId, boolean accept, boolean blackList, String message) {
    Mirai.getInstance().solveMemberJoinRequestEvent(BOT, eventId, fromId, fromNick, groupId, accept, blackList, message);
  }

  @Comment("转发Mirai")
  public static void solveNewFriendRequestEvent(long eventId, long fromId, String fromNick, boolean accept, boolean blackList) {
    Mirai.getInstance().solveNewFriendRequestEvent(BOT, eventId, fromId, fromNick, accept, blackList);
  }

}
