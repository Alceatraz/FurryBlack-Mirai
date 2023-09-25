package studio.blacktech.furryblackplus;

import kotlin.sequences.Sequence;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.Mirai;
import net.mamoe.mirai.auth.BotAuthorization;
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
import net.mamoe.mirai.utils.BotConfiguration;
import net.mamoe.mirai.utils.MiraiLogger;
import org.jetbrains.annotations.Nullable;
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
import studio.blacktech.furryblackplus.core.common.annotation.Comment;
import studio.blacktech.furryblackplus.core.common.enhance.FileEnhance;
import studio.blacktech.furryblackplus.core.common.enhance.LockEnhance;
import studio.blacktech.furryblackplus.core.common.enhance.StringEnhance;
import studio.blacktech.furryblackplus.core.common.enhance.TimeEnhance;
import studio.blacktech.furryblackplus.core.exception.CoreException;
import studio.blacktech.furryblackplus.core.exception.schema.SchemaException;
import studio.blacktech.furryblackplus.core.exception.system.FirstBootException;
import studio.blacktech.furryblackplus.core.exception.system.InvalidConfigException;
import studio.blacktech.furryblackplus.core.exception.system.TerminalException;
import studio.blacktech.furryblackplus.core.handler.EventHandlerChecker;
import studio.blacktech.furryblackplus.core.handler.EventHandlerExecutor;
import studio.blacktech.furryblackplus.core.handler.EventHandlerFilter;
import studio.blacktech.furryblackplus.core.handler.EventHandlerMonitor;
import studio.blacktech.furryblackplus.core.handler.EventHandlerRunner;
import studio.blacktech.furryblackplus.core.handler.annotation.AnnotationEnhance;
import studio.blacktech.furryblackplus.core.handler.annotation.Checker;
import studio.blacktech.furryblackplus.core.handler.annotation.Executor;
import studio.blacktech.furryblackplus.core.handler.annotation.Filter;
import studio.blacktech.furryblackplus.core.handler.annotation.Monitor;
import studio.blacktech.furryblackplus.core.handler.annotation.Runner;
import studio.blacktech.furryblackplus.core.handler.common.AbstractEventHandler;
import studio.blacktech.furryblackplus.core.handler.common.Command;
import studio.blacktech.furryblackplus.core.logging.LoggerX;
import studio.blacktech.furryblackplus.core.logging.LoggerXFactory;
import studio.blacktech.furryblackplus.core.logging.annotation.LoggerXConfig;
import studio.blacktech.furryblackplus.core.logging.enums.LoggerXLevel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.lang.management.ManagementFactory;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.jline.builtins.Completers.TreeCompleter.node;
import static studio.blacktech.furryblackplus.core.common.enhance.DataEnhance.parseInt;
import static studio.blacktech.furryblackplus.core.common.enhance.DataEnhance.parseLong;
import static studio.blacktech.furryblackplus.core.common.enhance.StringEnhance.toHumanBytes;
import static studio.blacktech.furryblackplus.core.common.enhance.StringEnhance.toHumanHashCode;
import static studio.blacktech.furryblackplus.core.handler.annotation.AnnotationEnhance.printAnnotation;
import static studio.blacktech.furryblackplus.core.logging.enums.LoggerXColor.BLACK;
import static studio.blacktech.furryblackplus.core.logging.enums.LoggerXColor.BLUE;
import static studio.blacktech.furryblackplus.core.logging.enums.LoggerXColor.BOLD_BLACK;
import static studio.blacktech.furryblackplus.core.logging.enums.LoggerXColor.BOLD_BLUE;
import static studio.blacktech.furryblackplus.core.logging.enums.LoggerXColor.BOLD_BRIGHT_BLACK;
import static studio.blacktech.furryblackplus.core.logging.enums.LoggerXColor.BOLD_BRIGHT_BLUE;
import static studio.blacktech.furryblackplus.core.logging.enums.LoggerXColor.BOLD_BRIGHT_CYAN;
import static studio.blacktech.furryblackplus.core.logging.enums.LoggerXColor.BOLD_BRIGHT_GREEN;
import static studio.blacktech.furryblackplus.core.logging.enums.LoggerXColor.BOLD_BRIGHT_MAGENTA;
import static studio.blacktech.furryblackplus.core.logging.enums.LoggerXColor.BOLD_BRIGHT_RED;
import static studio.blacktech.furryblackplus.core.logging.enums.LoggerXColor.BOLD_BRIGHT_WHITE;
import static studio.blacktech.furryblackplus.core.logging.enums.LoggerXColor.BOLD_BRIGHT_YELLOW;
import static studio.blacktech.furryblackplus.core.logging.enums.LoggerXColor.BOLD_CYAN;
import static studio.blacktech.furryblackplus.core.logging.enums.LoggerXColor.BOLD_GREEN;
import static studio.blacktech.furryblackplus.core.logging.enums.LoggerXColor.BOLD_MAGENTA;
import static studio.blacktech.furryblackplus.core.logging.enums.LoggerXColor.BOLD_RED;
import static studio.blacktech.furryblackplus.core.logging.enums.LoggerXColor.BOLD_WHITE;
import static studio.blacktech.furryblackplus.core.logging.enums.LoggerXColor.BOLD_YELLOW;
import static studio.blacktech.furryblackplus.core.logging.enums.LoggerXColor.BRIGHT_BLACK;
import static studio.blacktech.furryblackplus.core.logging.enums.LoggerXColor.BRIGHT_BLUE;
import static studio.blacktech.furryblackplus.core.logging.enums.LoggerXColor.BRIGHT_CYAN;
import static studio.blacktech.furryblackplus.core.logging.enums.LoggerXColor.BRIGHT_GREEN;
import static studio.blacktech.furryblackplus.core.logging.enums.LoggerXColor.BRIGHT_MAGENTA;
import static studio.blacktech.furryblackplus.core.logging.enums.LoggerXColor.BRIGHT_RED;
import static studio.blacktech.furryblackplus.core.logging.enums.LoggerXColor.BRIGHT_WHITE;
import static studio.blacktech.furryblackplus.core.logging.enums.LoggerXColor.BRIGHT_YELLOW;
import static studio.blacktech.furryblackplus.core.logging.enums.LoggerXColor.CYAN;
import static studio.blacktech.furryblackplus.core.logging.enums.LoggerXColor.GREEN;
import static studio.blacktech.furryblackplus.core.logging.enums.LoggerXColor.MAGENTA;
import static studio.blacktech.furryblackplus.core.logging.enums.LoggerXColor.RED;
import static studio.blacktech.furryblackplus.core.logging.enums.LoggerXColor.RESET;
import static studio.blacktech.furryblackplus.core.logging.enums.LoggerXColor.WHITE;
import static studio.blacktech.furryblackplus.core.logging.enums.LoggerXColor.YELLOW;

@Comment(
  value = "FurryBlack - Mirai",
  usage = {
    "A Mirai wrapper QQ-Bot framework make with love and 🧦",
    "电子白熊会梦到仿生老黑吗",
    "Alceatraz Warprays @ BlackTechStudio",
    "个人主页 https://www.blacktech.studio",
    "项目地址 https://github.com/Alceatraz/FurryBlack-Mirai",
    "插件地址 https://github.com/Alceatraz/FurryBlack-Mirai-Extensions",
  },
  attention = {
    "!!!本项目并非使用纯AGPLv3协议, 请认真阅读LICENSE!!!"
  }
)
public class FurryBlack {

  //= ==================================================================================================================
  //=
  //= 静态数据
  //=
  //= ==================================================================================================================

  public static final String APP_VERSION = "3.0.4";
  public static final String MIRAI_VERSION = "2.15.0";

  //= ==========================================================================

  private static final String[] ARGS_DEBUG = {"debug"};
  private static final String[] ARGS_UNSAFE = {"unsafe"};
  private static final String[] ARGS_NO_LOGIN = {"no", "login"};
  private static final String[] ARGS_NO_JLINE = {"no", "jline"};
  private static final String[] ARGS_FULL_NAME = {"full", "name"};
  private static final String[] ARGS_NO_CONSOLE = {"no", "console"};
  private static final String[] ARGS_FORCE_EXIT = {"force", "exit"};
  private static final String[] ARGS_LOGGER_LEVEL = {"logger", "level"};
  private static final String[] ARGS_LOGGER_PREFIX = {"logger", "prefix"};
  private static final String[] ARGS_LOGGER_PROVIDER = {"logger", "provider"};

  private static final String[] CONF_DEVICE_TYPE = {"device", "type"};
  private static final String[] CONF_DEVICE_INFO = {"device", "info"};
  private static final String[] CONF_ACCOUNT_AUTH = {"account", "auth"};
  private static final String[] CONF_ACCOUNT_USERNAME = {"account", "username"};
  private static final String[] CONF_ACCOUNT_PASSWORD = {"account", "password"};
  private static final String[] CONF_COMMAND_REGEX = {"command", "regex"};
  private static final String[] CONF_THREADS_MONITOR = {"threads", "monitor"};
  private static final String[] CONF_THREADS_SCHEDULE = {"threads", "schedule"};
  private static final String[] CONF_UPGRADE_PROTOCOLS = {"upgrade", "protocols"};

  //= ==========================================================================

  private static final DateTimeFormatter FORMATTER;

  //= ==========================================================================

  @Comment("QQ用换行符") public static final String CRLF = "\r\n";
  @Comment("系统换行符") public static final String LINE;

  public static final int CPU_CORES;
  public static final long BOOT_TIME;

  public static final String CONTENT_INFO;
  public static final String CONTENT_HELP;
  public static final String CONTENT_COLOR;
  public static final String DEFAULT_CONFIG;

  //= ==================================================================================================================

  static {

    //= ================================================================================================================
    //= 系统信息

    LINE = System.lineSeparator();

    BOOT_TIME = ManagementFactory.getRuntimeMXBean().getStartTime();
    CPU_CORES = Runtime.getRuntime().availableProcessors();

    FORMATTER = TimeEnhance.pattern("yyyy-MM-dd HH-mm-ss");

    //= ================================================================================================================
    //= 框架信息

    CONTENT_INFO =

      // @formatter:off

BOLD_BRIGHT_CYAN +
"※ FurryBlack 版本信息 ===========================================================" + RESET + LINE + LINE +

"A Mirai wrapper framework make with love and 🧦" + LINE +
"Create by: Alceatraz Warprays @ BlackTechStudio" + LINE + LINE +

"框架版本 " + APP_VERSION + LINE +
"内核版本 " + MIRAI_VERSION + LINE + LINE +
"内核源码 https://github.com/mamoe/mirai" + LINE +
"框架源码 https://github.com/Alceatraz/FurryBlack-Mirai" + LINE +
"示例插件 https://github.com/Alceatraz/FurryBlack-Mirai-Extensions" + LINE + LINE +

BOLD_BRIGHT_CYAN +
"# ==============================================================================" + RESET

      // @formatter:on

    ;

    CONTENT_HELP =

      // @formatter:off

BOLD_BRIGHT_CYAN +
"※ FurryBlack 版本信息 ===========================================================" + RESET + LINE +
"A Mirai wrapper framework make with love and 🧦" + LINE +
"Create by: Alceatraz Warprays @ BlackTechStudio" + LINE +
"框架版本 " + APP_VERSION + LINE +
"内核版本 " + MIRAI_VERSION + LINE +
"内核源码 https://github.com/mamoe/mirai" + LINE +
"框架源码 https://github.com/Alceatraz/FurryBlack-Mirai" + LINE +
"示例插件 https://github.com/Alceatraz/FurryBlack-Mirai-Extensions" + LINE +

BOLD_BRIGHT_CYAN +
"※ FurryBlack 交互模式 ===========================================================" + RESET + LINE +
"--help ------------------------------ 显示帮助" + LINE +
"--info ------------------------------ 显示版本" + LINE +
"--color ----------------------------- 显示颜色" + LINE +
"* 交互模式是模仿unix软件的信息显示功能, 执行后退出" + LINE +

BOLD_BRIGHT_CYAN +
"※ FurryBlack 启动参数 ===========================================================" + RESET + LINE +
"--debug ----------------------------- 选项 启动DEBUG模式*" + LINE +
"--unsafe ---------------------------- 选项 允许一些正常模式下禁止的调用" + LINE +
"--no-login -------------------------- 选项 跳过客户端登录,大部分功能不可用" + LINE +
"--no-jline -------------------------- 选项 不使用jline终端" + LINE +
"--no-console ------------------------ 选项 不使用终端" + LINE +
"--force-exit ------------------------ 选项 关闭后将强退JVM" + LINE +
"--full-name ------------------------- 选项 日志显示完成类名" + LINE +
"--logger-level ---------------------- 参数 设置默认日志级别*" + LINE +
"--logger-prefix --------------------- 参数 使用指定的日志级别配置*" + LINE +
"--logger-provider ------------------- 参数 使用指定类名的日志实现后端*" + LINE +
YELLOW +
"* 可在启动后通过终端修改,参数的目的是启动初始化阶段即应用" + LINE +
"* 选项: 键存在即可, 参数: 必须是键值对 例如 --logger-level MUTE" + RESET + LINE +

BOLD_BRIGHT_CYAN +
"※ FurryBlack 参数传递 ===========================================================" + RESET + LINE +
"例如 foo bar 参数 可由三种方式传递" + LINE +
"环境变量 export FOO_BAR -------------- 转换为大写 下划线拼接" + LINE +
"系统配置 -Dfoo.bar ------------------- 转换为小写 英句号拼接" + LINE +
"程序参数 --foo-bar ------------------- 转换为小写 中横线拼接" + LINE +
"配置文件 foo.bar --------------------- 转换为小写 英句号拼接" + LINE +

BOLD_BRIGHT_CYAN +
"※ FurryBlack 命名空间 ===========================================================" + RESET + LINE +
"程序参数 --namespace xxx 可将参数传递时的所有键添加前缀, 例如:" + LINE +
"环境变量 export XXX_FOO_BAR ---------- 转换为大写 下划线拼接" + LINE +
"系统配置 -Dxxx.foo.bar --------------- 转换为小写 中横线拼接" + LINE +
"程序参数 --xxx-foo-bar --------------- 转换为小写 英句号拼接" + LINE +
"配置文件 foo.bar --------------------- 转换为小写 英句号拼接" + LINE +
YELLOW +
"* 配置文件内名称不受命名空间影响" + RESET + LINE +

BOLD_BRIGHT_CYAN +
"※ FurryBlack 覆盖参数 ===========================================================" + RESET + LINE +
"参数优先级 环境变量 > 系统配置 > 程序参数 > 配置文件 " + LINE +
"account.auth ----------------------- 认证模式 PASSWD/QRCODE" + LINE +
"account.username ------------------- 账号, 必填" + LINE +
"account.password* ------------------ 密码, QRCODE模式不填" + LINE +
"device.type ------------------------ 设备类型, IPAD/MACOS/PAD/PHONE/WATCH" + LINE +
"device.info ------------------------ 设备信息, 使用Aoki生成" + LINE +
"command.prefix --------------------- 命令识别正则" + LINE +
"threads.monitor -------------------- 监听器线程池" + LINE +
"threads.schedule ------------------- 定时器线程池" + LINE +
"upgrade.protocols ------------------ 升级协议版本" + LINE +
YELLOW +
"* 为了避免有人把密码写在命令行导致所有人都能在task里看见, 密码不从系统配置或程序参数读取" + LINE +
"* 如若执意要如此使用, 需要使用unsafe配置项, 解锁安全限制后使用, 强烈反对使用因其极度危险" + RESET + LINE +

BOLD_BRIGHT_CYAN +
"※ FurryBlack 控制台  ============================================================" + RESET + LINE +
RED +
"⚠ 控制台任何操作都属于底层操作可以直接对框架进行不安全和非法的操作" + RESET + LINE +
"安全: 设计如此, 不会导致异常或者不可预测的结果" + LINE +
"风险: 功能设计上是安全操作, 但是具体被操作对象可能导致错误" + LINE +
"危险: 没有安全性检查的操作, 可能会让功能严重异常导致被迫重启或损坏模块的数据存档" + LINE +
"高危: 后果完全未知的危险操作, 或者正常流程中不应该如此操作但是控制台仍然可以强制执行" + LINE +

BOLD_BRIGHT_CYAN +
"※ 框架内核 ======================================================================" + RESET + LINE +
"? ----------------------------------- (安全) 显示本帮助信息" + LINE +
"help -------------------------------- (安全) 显示本帮助信息" + LINE +
"gc ---------------------------------- (安全) 显示系统运行状态" + LINE +
"status ------------------------------ (安全) 显示系统运行状态" + LINE +
"system status ----------------------- (安全) 显示系统运行状态" + LINE +
"system stacks ----------------------- (安全) 打印所有运行中的线程" + LINE +
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

      // @formatter:on

    ;

    CONTENT_COLOR =

      // @formatter:off

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

      // @formatter:on

    ;

    DEFAULT_CONFIG =

      // @formatter:off

"""
#===============================================================================
# 账号配置
#===============================================================================
# 认证模式 PASSWD/QRCODE
CONF_ACCOUNT_AUTH=PASSWD
# 账号
CONF_ACCOUNT_USERNAME=0000
# 密码
CONF_ACCOUNT_PASSWORD=0000
#===============================================================================
# 设备设置
#===============================================================================
# 设备类型 IPAD/MACOS/PAD/PHONE/WATCH
CONF_DEVICE_TYPE=IPAD
# 设备信息 需要使用Aoki生成
CONF_DEVICE_INFO=device.json
#===============================================================================
# 设备设置
#===============================================================================
# 命令识别正则
CONF_COMMAND_REGEX=/[a-zA-Z0-9]{2,16}
# 监听器线程池
CONF_THREADS_MONITOR=0
# 定时器线程池
CONF_THREADS_SCHEDULE=0
#===============================================================================
"""

      // @formatter:on

  .replaceAll("CONF_ACCOUNT_AUTH", String.join(".", CONF_ACCOUNT_AUTH))
  .replaceAll("CONF_ACCOUNT_USERNAME", String.join(".", CONF_ACCOUNT_USERNAME))
  .replaceAll("CONF_ACCOUNT_PASSWORD", String.join(".", CONF_ACCOUNT_PASSWORD))
  .replaceAll("CONF_DEVICE_TYPE", String.join(".", CONF_DEVICE_TYPE))
  .replaceAll("CONF_DEVICE_INFO", String.join(".", CONF_DEVICE_INFO))
  .replaceAll("CONF_COMMAND_REGEX", String.join(".", CONF_COMMAND_REGEX))
  .replaceAll("CONF_THREADS_MONITOR", String.join(".", CONF_THREADS_MONITOR))
  .replaceAll("CONF_THREADS_SCHEDULE", String.join(".", CONF_THREADS_SCHEDULE))

    ;

  }

  //= ==================================================================================================================
  //=
  //= 实例控制
  //=
  //= ==================================================================================================================

  private FurryBlack() {}

  //= ==================================================================================================================
  //
  //  框架常量
  //
  //= ==================================================================================================================

  private static final LockEnhance.Latch LATCH = new LockEnhance.Latch();

  //= ==================================================================================================================
  //
  //  框架变量
  //
  //= ==================================================================================================================

  private static String NAMESPACE; // 命名空间

  private static volatile boolean EVENT_ENABLE;

  private static volatile boolean KERNEL_DEBUG;
  private static volatile boolean SHUTDOWN_HALT;
  private static volatile boolean SHUTDOWN_DROP;
  private static volatile boolean SHUTDOWN_KILL;

  private static KernelConfig kernelConfig;
  private static SystemConfig systemConfig;

  private static LoggerX logger;
  private static Terminal terminal;
  private static Dispatcher dispatcher;

  private static Bot bot;
  private static Schema schema;
  private static Nickname nickname;

  private static Path FOLDER_ROOT;
  private static Path FOLDER_CONFIG;
  private static Path FOLDER_PLUGIN;
  private static Path FOLDER_DEPEND;
  private static Path FOLDER_MODULE;
  private static Path FOLDER_LOGGER;

  private static String MESSAGE_INFO;
  private static String MESSAGE_EULA;
  private static String MESSAGE_HELP;
  private static String MESSAGE_LIST_USERS;
  private static String MESSAGE_LIST_GROUP;

  private static ThreadPoolExecutor MONITOR_PROCESS;
  private static ScheduledThreadPoolExecutor SCHEDULE_SERVICE;

  //= ==================================================================================================================
  //=
  //= 启动入口
  //=
  //= ==================================================================================================================

  public static void main(String[] args) {

    //= ================================================================================================================
    //=
    //=
    //= 交互模式
    //=
    //=
    //= ================================================================================================================

    boolean dryRun = false;

    List<String> arguments = List.of(args);

    // 显示 信息
    if (arguments.contains("--info")) {
      System.out.println(CONTENT_INFO);
      System.out.println();
      dryRun = true;
    }

    // 显示 帮助
    if (arguments.contains("--help")) {
      System.out.println(CONTENT_HELP);
      System.out.println();
      dryRun = true;
    }

    // 显示 颜色
    if (arguments.contains("--color")) {
      System.out.println(CONTENT_COLOR);
      System.out.println();
      dryRun = true;
    }

    if (dryRun) return;

    //= ================================================================================================================
    //=
    //=
    //= 正式模式
    //=
    //=
    //= ================================================================================================================

    System.out.println("[FurryBlack][BOOT]FurryBlackMirai - " + APP_VERSION + " " + TimeEnhance.datetime(BOOT_TIME));

    //= ================================================================================================================
    //= 跳过语言设置

    // -D user.country=zh
    // -D user.language=CN
    if (System.getenv("FURRYBLACK_LOCALE_SKIP") == null) {
      if (System.getenv("FURRYBLACK_MUTE") == null) {
        System.err.println("Env FURRYBLACK_LOCALE_SKIP not set, Setting JVM local to Locale.SIMPLIFIED_CHINESE");
      }
      Locale.setDefault(Locale.SIMPLIFIED_CHINESE);
    }

    //= ================================================================================================================
    //= 跳过时间设置

    // -D user.timezone=Asia/Shanghai
    if (System.getenv("FURRYBLACK_TIMEZONE_SKIP") == null) {
      if (System.getenv("FURRYBLACK_MUTE") == null) {
        System.err.println("Env FURRYBLACK_TIMEZONE_SKIP not set, Setting JVM timezone to Asia/Shanghai");
      }
      TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
    }

    //= ================================================================================================================
    //=
    //= 内核系统
    //=
    //= ================================================================================================================

    Argument argument = Argument.parse(args);

    //= ========================================================================
    //= 命名空间

    NAMESPACE = argument.getKernelParameter("namespace");

    if (NAMESPACE == null || NAMESPACE.isBlank()) {
      System.out.println("[FurryBlack][ARGS]默认命名空间");
    } else {
      System.out.println("[FurryBlack][ARGS]设置命名空间 " + NAMESPACE);
    }

    //= ========================================================================
    //= 打印参数

    System.out.println("[FurryBlack][ARGS] 选项 -> " + argument.optionSize());

    for (String it : argument.cloneOptions()) {
      System.out.println("[FurryBlack][ARGS]    " + it);
    }

    System.out.println("[FurryBlack][ARGS] 参数 -> " + argument.parameterSize());

    for (Map.Entry<String, String> entry : argument.cloneParameters().entrySet()) {
      String k = entry.getKey();
      String v = entry.getValue();
      System.out.println("[FurryBlack][ARGS]    " + k + "=" + v);
    }

    //= ========================================================================
    //= 内核参数

    kernelConfig = KernelConfig.getInstance(argument);

    if (kernelConfig.debug) {
      System.out.println("[FurryBlack][ARGS]调试开关 - 调试模式");
    } else {
      System.out.println("[FurryBlack][ARGS]调试开关 - 生产模式");
    }

    if (kernelConfig.unsafe) {
      System.out.println("[FurryBlack][ARGS]安全策略 - 宽松策略");
    } else {
      System.out.println("[FurryBlack][ARGS]安全策略 - 标准策略");
    }

    if (kernelConfig.noLogin) {
      System.out.println("[FurryBlack][ARGS]登录模式 - 跳过登录");
    } else {
      System.out.println("[FurryBlack][ARGS]登录模式 - 真实登录");
    }

    if (kernelConfig.noConsole) {
      System.out.println("[FurryBlack][ARGS]终端模式 - 关闭终端");
    } else {
      if (kernelConfig.noJline) {
        System.out.println("[FurryBlack][ARGS]终端模式 - 精简终端");
      } else {
        System.out.println("[FurryBlack][ARGS]终端模式 - 完整终端");
      }
    }

    if (kernelConfig.forceExit) {
      System.out.println("[FurryBlack][ARGS]关闭策略 - 强制退出");
    } else {
      System.out.println("[FurryBlack][ARGS]关闭策略 - 正常退出");
    }

    //= ========================================================================
    //= 日志等级

    if (kernelConfig.level != null) {

      LoggerXLevel level = LoggerXLevel.of(kernelConfig.level);

      if (level == null) {
        System.out.println("[FurryBlack][ARGS]日志级别 - 输入值无效 -> " + kernelConfig.level + ", 可用日志级别为:");
        System.out.println("[FurryBlack][ARGS] - CLOSE");
        System.out.println("[FurryBlack][ARGS] - ERROR");
        System.out.println("[FurryBlack][ARGS] - WARN");
        System.out.println("[FurryBlack][ARGS] - INFO");
        System.out.println("[FurryBlack][ARGS] - DEBUG");
        System.out.println("[FurryBlack][ARGS] - TRACE");
        throw new CoreException("[FurryBlack][FATAL] Logger level invalid -> " + kernelConfig.level);
      } else {
        System.out.println("[FurryBlack][ARGS]日志级别 - " + kernelConfig.level);
      }

      LoggerXFactory.setLevel(level);

    }

    //= ========================================================================
    //= 日志全名

    if (kernelConfig.fullname) {
      LoggerXFactory.setEnableFullName(true);
    }

    System.out.println("[FurryBlack][ARGS]日志全名 - " + (kernelConfig.fullname ? "开启" : "关闭"));

    //= ========================================================================
    //= 日志前缀

    if (kernelConfig.prefix != null) {

      System.out.println("[FurryBlack][ARGS]日志前缀 - 尝试加载前缀配置 -> " + kernelConfig.prefix);

      Path path = Paths.get(kernelConfig.prefix);
      List<String> lines = FileEnhance.readLine(path);

      if (lines.isEmpty()) {
        System.out.println("[FurryBlack][ARGS]日志前缀 - 前缀配置为空 切换至默认模式");
      } else {
        for (String line : lines) {
          String[] split = line.split("=");
          var k = split[0];
          var v = split[1];
          LoggerXLevel of = LoggerXLevel.of(v);
          LoggerXFactory.injectPrefix(k, of);
          System.out.println("[FurryBlack][ARGS]日志前缀 - 加载 " + v + " " + k);
        }
        LoggerXFactory.setEnablePrefix(true);
      }
    }

    //= ========================================================================
    //= 日志后端

    if (kernelConfig.provider != null) {

      System.out.println("[FurryBlack][ARGS]日志后端 - 尝试加载日志后端 -> " + kernelConfig.provider);

      Class<?> clazz;
      try {
        clazz = Class.forName(kernelConfig.provider);
      } catch (Exception exception) {
        throw new CoreException("[FurryBlack][ARGS]日志后端 - 尝试加载日志后端失败, 加载类失败 -> " + kernelConfig.provider, exception);
      }

      Class<? extends LoggerX> loggerClazz;
      if (LoggerX.class.isAssignableFrom(clazz)) {
        @SuppressWarnings("unchecked")
        Class<? extends LoggerX> tempForSuppress = (Class<? extends LoggerX>) clazz;
        loggerClazz = tempForSuppress;
      } else {
        throw new CoreException("[FurryBlack][ARGS]日志后端 - 尝试加载日志后端失败, 指定的类未继承 LoggerX -> " + kernelConfig.provider);
      }

      if (!loggerClazz.isAnnotationPresent(LoggerXConfig.class)) {
        throw new CoreException("[FurryBlack][ARGS]日志后端 - 尝试加载日志后端失败, 指定的类未添加 LoggerXConfig 注解 -> " + kernelConfig.provider);
      }

      LoggerXFactory.setDefault(loggerClazz);

    }

    System.out.println("[FurryBlack][ARGS]日志后端 - " + LoggerXFactory.getDefault());

    System.out.println("[FurryBlack][INIT]内核配置初始化完成");

    //= ================================================================================================================
    //= 终端子系统
    //= ================================================================================================================

    if (kernelConfig.noConsole) {
      terminal = NoConsoleTerminal.getInstance();
    } else {
      if (kernelConfig.noJline) {
        terminal = StdinTerminal.getInstance();
      } else {
        terminal = JlineTerminal.getInstance();
      }
    }

    FurryBlack.println("[FurryBlack][INIT]终端系统初始化完成");

    //= ================================================================================================================
    //= 文件子系统
    //= ================================================================================================================

    //= ========================================================================
    //= 初始化目录

    FOLDER_ROOT = Paths.get(System.getProperty("user.dir"));

    FOLDER_CONFIG = FileEnhance.get(FOLDER_ROOT, "config");
    FOLDER_PLUGIN = FileEnhance.get(FOLDER_ROOT, "plugin");
    FOLDER_DEPEND = FileEnhance.get(FOLDER_ROOT, "depend");
    FOLDER_MODULE = FileEnhance.get(FOLDER_ROOT, "module");
    FOLDER_LOGGER = FileEnhance.get(FOLDER_ROOT, "logger");

    String ensureFolderConfig = FileEnhance.ensureFolderSafe(FOLDER_CONFIG);
    String ensureFolderPlugin = FileEnhance.ensureFolderSafe(FOLDER_PLUGIN);
    String ensureFolderDepend = FileEnhance.ensureFolderSafe(FOLDER_DEPEND);
    String ensureFolderModule = FileEnhance.ensureFolderSafe(FOLDER_MODULE);
    String ensureFolderLogger = FileEnhance.ensureFolderSafe(FOLDER_LOGGER);

    CoreException.check("初始化配置目录失败 -> ", ensureFolderConfig);
    CoreException.check("初始化插件目录失败 -> ", ensureFolderPlugin);
    CoreException.check("初始化依赖目录失败 -> ", ensureFolderDepend);
    CoreException.check("初始化数据目录失败 -> ", ensureFolderModule);
    CoreException.check("初始化日志目录失败 -> ", ensureFolderLogger);

    FurryBlack.println("[FurryBlack][INIT]应用工作目录 " + FOLDER_ROOT);
    FurryBlack.println("[FurryBlack][INIT]插件扫描目录 " + FOLDER_PLUGIN);
    FurryBlack.println("[FurryBlack][INIT]模块依赖目录 " + FOLDER_DEPEND);
    FurryBlack.println("[FurryBlack][INIT]模块数据目录 " + FOLDER_MODULE);
    FurryBlack.println("[FurryBlack][INIT]核心日志目录 " + FOLDER_LOGGER);

    FurryBlack.println("[FurryBlack][INIT]日志后端 " + LoggerXFactory.getDefault());
    FurryBlack.println("[FurryBlack][INIT]日志级别 " + LoggerXFactory.getLevel().name());

    //= ========================================================================
    //= 初始化日志

    if (LoggerXFactory.needLoggerFile()) {

      String name = FORMATTER.format(Instant.ofEpochMilli(BOOT_TIME)) + ".txt";
      Path loggerFile = FileEnhance.get(FOLDER_LOGGER, name);
      CoreException.check("日志文件初始化失败 -> ", FileEnhance.ensureFileSafe(loggerFile));

      try {
        LoggerXFactory.initLoggerFile(loggerFile);
      } catch (NoSuchMethodException | IllegalAccessException exception) {
        throw new CoreException("日志后端初始化失败 标记为需要日志文件的后端必须实现public void init(Path)方法 -> " + loggerFile, exception);
      } catch (InvocationTargetException exception) {
        throw new CoreException("日志后端初始化失败 后端执行public void init(Path)方法时发生异常 -> " + loggerFile, exception);
      }

      FurryBlack.println("[FurryBlack][INIT]日志文件 " + name);
    }

    logger = LoggerXFactory.getLogger("System");

    FurryBlack.println("[FurryBlack][INIT]日志系统初始化完成");

    //= ========================================================================
    //= 终端接管

    logger.hint("日志系统接管 " + terminal.getClass().getSimpleName() + " -> " + logger.getClass().getSimpleName());

    logger.info("系统状态/应用工作目录 " + FOLDER_ROOT);
    logger.info("系统状态/插件扫描目录 " + FOLDER_PLUGIN);
    logger.info("系统状态/模块依赖目录 " + FOLDER_DEPEND);
    logger.info("系统状态/模块数据目录 " + FOLDER_MODULE);
    logger.info("系统状态/核心日志目录 " + FOLDER_LOGGER);

    logger.info("内核配置/日志后端 " + LoggerXFactory.getDefault());
    logger.info("内核配置/日志级别 " + LoggerXFactory.getLevel().name());

    if (kernelConfig.debug) {
      logger.info("内核配置/调试开关 - 调试模式");
    } else {
      logger.info("内核配置/调试开关 - 生产模式");
    }

    if (kernelConfig.unsafe) {
      logger.info("内核配置/安全策略 - 宽松策略");
    } else {
      logger.info("内核配置/安全策略 - 标准策略");
    }

    if (kernelConfig.noLogin) {
      logger.info("内核配置/登录模式 - 跳过登录");
    } else {
      logger.info("内核配置/登录模式 - 真实登录");
    }

    if (kernelConfig.noConsole) {
      logger.info("内核配置/终端模式 - 关闭终端");
    } else {
      if (kernelConfig.noJline) {
        logger.info("内核配置/终端模式 - 精简终端");
      } else {
        logger.info("内核配置/终端模式 - 完整终端");
      }
    }

    if (kernelConfig.forceExit) {
      logger.info("内核配置/关闭策略 - 强制退出");
    } else {
      logger.info("内核配置/关闭策略 - 正常退出");
    }

    //= ========================================================================
    //= 赋值

    KERNEL_DEBUG = kernelConfig.debug;
    SHUTDOWN_HALT = kernelConfig.forceExit;

    //= ================================================================================================================
    //=
    //= 框架系统
    //=
    //= ================================================================================================================

    logger.hint("初始化系统核心");

    //= ================================================================================================================
    //= 应用配置
    //= ================================================================================================================

    //= ========================================================================
    //= 加载配置文件

    Path FILE_CONFIG = FileEnhance.get(FOLDER_CONFIG, "application.properties");

    logger.info("检查配置文件");

    if (Files.exists(FILE_CONFIG)) {

      Properties properties = new Properties();

      logger.info("加载配置文件");

      try (
        InputStream inputStream = Files.newInputStream(FILE_CONFIG);
        Reader reader = new InputStreamReader(inputStream)
      ) {
        properties.load(reader);
      } catch (IOException exception) {
        throw new CoreException("读取配置文件失败 -> " + FILE_CONFIG, exception);
      }

      logger.info("合并配置文件");

      for (Map.Entry<Object, Object> entry : properties.entrySet()) {
        var k = entry.getKey();
        var v = entry.getValue();
        if (k == null || k.toString().isBlank()) {
          logger.warn("丢弃无效配置 " + k + "=" + v);
          continue;
        }
        if (v == null || k.toString().isBlank()) {
          logger.warn("丢弃无效配置 " + k + "=" + v);
          continue;
        }
        argument.append(k, v);
      }

    } else {
      logger.info("跳过配置文件");
    }

    //= ========================================================================
    //= 检查配置项目

    logger.info("检查配置项目");

    try {

      systemConfig = SystemConfig.getInstance(argument);

    } catch (FirstBootException exception) {

      logger.fatal("必要配置项目缺失, 写入默认配置文件 -> " + FILE_CONFIG);

      try {
        Files.writeString(FILE_CONFIG, DEFAULT_CONFIG);
      } catch (IOException ioException) {
        logger.fatal("写入默认配置文件失败", ioException);
        CoreException CoreException = new CoreException(ioException);
        CoreException.addSuppressed(exception);
        throw CoreException;

      }

      throw exception;

    }

    //= ================================================================================================================
    //= 模板消息子系统
    //= ================================================================================================================

    logger.hint("加载内置消息");

    {

      Path FILE_EULA = FileEnhance.get(FOLDER_CONFIG, "message_eula.txt");
      Path FILE_INFO = FileEnhance.get(FOLDER_CONFIG, "message_info.txt");
      Path FILE_HELP = FileEnhance.get(FOLDER_CONFIG, "message_help.txt");

      MESSAGE_EULA = FileEnhance.read(FILE_EULA).replace("\\$VERSION", APP_VERSION);
      MESSAGE_INFO = FileEnhance.read(FILE_INFO).replace("\\$VERSION", APP_VERSION);
      MESSAGE_HELP = FileEnhance.read(FILE_HELP).replace("\\$VERSION", APP_VERSION);

    }

    //= ================================================================================================================
    //= 昵称子系统
    //= ================================================================================================================

    nickname = Nickname.getInstance();

    logger.hint("加载常用昵称");

    nickname.cleanNickname();
    nickname.appendNickname();

    //= ================================================================================================================
    //= 机器人子系统
    //= ================================================================================================================

    //= ========================================================================
    //= 升级客户端协议

    if (systemConfig.upgradeProtocols != null) {

      logger.hint("升级协议版本");

      System.setProperty("xyz.cssxsh.mirai.tool.KFCFactory.config", "config/qsign.json");
      System.setProperty("xyz.cssxsh.mirai.tool.FixProtocolVersion.folder", "config/protocol");

      Class<?> clazz;
      try {
        clazz = Class.forName("xyz.cssxsh.mirai.tool.FixProtocolVersion");
      } catch (ClassNotFoundException exception) {
        throw new CoreException("[UPGRADE/PROTOCOL] Load class failure", exception);
      }

      Method methodLoad;
      try {
        methodLoad = clazz.getMethod("load", BotConfiguration.MiraiProtocol.class);
      } catch (NoSuchMethodException exception) {
        throw new CoreException("[UPGRADE/PROTOCOL] Invoke method failure", exception);
      }

      for (BotConfiguration.MiraiProtocol miraiProtocol : systemConfig.upgradeProtocols) {
        logger.info("升级协议 -> " + miraiProtocol.name());
        try {
          methodLoad.invoke(null, miraiProtocol);
        } catch (IllegalAccessException | InvocationTargetException exception) {
          throw new CoreException("[UPGRADE/PROTOCOL] Invoke method failure", exception);
        }
      }

      Method methodInfo;
      try {
        methodInfo = clazz.getMethod("info");
      } catch (NoSuchMethodException exception) {
        throw new CoreException("[UPGRADE/PROTOCOL] Load method failure", exception);
      }

      logger.info("当前协议版本");

      Object invoke;
      try {
        invoke = methodInfo.invoke(null);
      } catch (IllegalAccessException | InvocationTargetException | ClassCastException exception) {
        throw new CoreException("[UPGRADE/PROTOCOL] Invoke method failure", exception);
      }

      try {
        if (invoke instanceof Map<?, ?> map) {
          @SuppressWarnings("unchecked")
          Map<BotConfiguration.MiraiProtocol, String> info = (Map<BotConfiguration.MiraiProtocol, String>) map;
          info.forEach((k, v) -> logger.info(v));
        }
      } catch (Exception exception) {
        throw new CoreException("[UPGRADE/PROTOCOL] Invoke method failure", exception);
      }
    }

    //= ========================================================================

    logger.hint("初始化机器人");

    //= ========================================================================
    //= 加载客户端配置

    logger.info("加载客户端配置");

    BotConfiguration botConfiguration = new BotConfiguration();

    botConfiguration.setLoginCacheEnabled(true);
    botConfiguration.enableContactCache();
    botConfiguration.setCacheDir(FileEnhance.get(FOLDER_CONFIG, "cache").toFile());
    botConfiguration.setProtocol(systemConfig.deviceType.toMiraiProtocol());
    botConfiguration.loadDeviceInfoJson(systemConfig.deviceInfo);
    botConfiguration.setBotLoggerSupplier(i -> new MiraiLoggerX("MiraiBot"));
    botConfiguration.setNetworkLoggerSupplier(i -> new MiraiLoggerX("MiraiNet"));

    //= ========================================================================
    //= 加载客户端认证

    logger.info("加载客户端认证");

    BotAuthorization authorization = switch (systemConfig.authMod) {
      case QRCODE -> BotAuthorization.byQRCode();
      case PASSWD -> BotAuthorization.byPassword(systemConfig.password);
    };

    //= ========================================================================
    //= 创建机器人实例

    logger.info("创建机器人实例");

    bot = BotFactory.INSTANCE.newBot(systemConfig.username, authorization, botConfiguration);

    //= ========================================================================
    //= 订阅客户端事件

    logger.info("订阅客户端事件");

    Listener<UserMessageEvent> userMessageEventListener = GlobalEventChannel.INSTANCE.subscribeAlways(UserMessageEvent.class, event -> {

      if (!EVENT_ENABLE) return;

      try {

        for (EventHandlerFilter eventHandlerFilter : schema.getFilterUsersChain()) {
          if (eventHandlerFilter.handleUsersMessageWrapper(event)) return;
        }

        MONITOR_PROCESS.submit(() -> {
          for (EventHandlerMonitor item : schema.getMonitorUsersChain()) {
            item.handleUsersMessageWrapper(event);
          }
        });

        String content = event.getMessage().contentToString();

        if (systemConfig.commandRegex.matcher(content).find()) {

          Command command = new Command(content.substring(1));
          String commandName = command.getCommandName();

          switch (commandName) {

            case "info" -> FurryBlack.sendMessage(event, MESSAGE_INFO);
            case "eula" -> FurryBlack.sendMessage(event, MESSAGE_EULA);
            case "list" -> FurryBlack.sendMessage(event, MESSAGE_LIST_USERS);

            case "help" -> {
              if (command.hasCommandBody()) {
                String segment = command.getParameterSegment(0);
                EventHandlerExecutor executor = schema.getExecutorUsersPool().get(segment);
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
              EventHandlerExecutor executor = schema.getExecutorUsersPool().get(commandName);
              if (executor == null) {
                FurryBlack.sendMessage(event, "没有此命令");
                return;
              }
              for (EventHandlerChecker checker : schema.getGlobalCheckerUsersPool()) {
                if (checker.handleUsersMessageWrapper(event, command)) return;
              }
              List<EventHandlerChecker> commandCheckerUsersPool = schema.getCommandCheckerUsersPool(commandName);
              if (commandCheckerUsersPool != null) {
                for (EventHandlerChecker checker : commandCheckerUsersPool) {
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

      if (!EVENT_ENABLE) return;

      try {

        for (EventHandlerFilter eventHandlerFilter : schema.getFilterGroupChain()) {
          if (eventHandlerFilter.handleGroupMessageWrapper(event)) return;
        }

        MONITOR_PROCESS.submit(() -> {
          for (EventHandlerMonitor item : schema.getMonitorGroupChain()) {
            item.handleGroupMessageWrapper(event);
          }
        });

        String content = event.getMessage().contentToString();

        if (systemConfig.commandRegex.matcher(content).find()) {

          Command command = new Command(content.substring(1));
          String commandName = command.getCommandName();

          switch (commandName) {

            case "help" -> {
              if (command.hasCommandBody()) {
                String segment = command.getParameterSegment(0);
                EventHandlerExecutor executor = schema.getExecutorGroupPool().get(segment);
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
              EventHandlerExecutor executor = schema.getExecutorGroupPool().get(commandName);
              if (executor == null) {
                return;
              }
              for (EventHandlerChecker checker : schema.getGlobalCheckerGroupPool()) {
                if (checker.handleGroupMessageWrapper(event, command))
                  return;
              }
              List<EventHandlerChecker> commandCheckerGroupPool = schema.getCommandCheckerGroupPool(commandName);
              if (commandCheckerGroupPool != null) {
                for (EventHandlerChecker checker : commandCheckerGroupPool) {
                  if (checker.handleGroupMessageWrapper(event, command))
                    return;
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

    schema = new Schema(FOLDER_PLUGIN);

    //= ========================================================================
    // 扫描插件

    schema.scanPlugin();

    //= ========================================================================
    // 扫描模块

    schema.scanModule();

    //= ========================================================================
    // 注册模块

    schema.loadModule();

    //= ========================================================================
    // 创建模块

    schema.makeModule();

    //= ========================================================================
    // 执行初始化方法

    schema.initModule();

    //= ================================================================================================================
    //= 机器人子系统
    //= ================================================================================================================

    //= ========================================================================
    //= 登录机器人

    if (kernelConfig.noLogin) {
      logger.warn("指定了--no-login参数 跳过登录");
    } else {
      logger.hint("登录机器人");
      bot.login();
    }

    //= ================================================================================================================
    //= 插件子系统
    //= ================================================================================================================

    //= ========================================================================
    //= 启动线程池

    logger.seek("启动线程池");

    logger.info("启动监听器线程池");

    MONITOR_PROCESS = new ThreadPoolExecutor(
      systemConfig.monitorThreads,
      systemConfig.monitorThreads,
      0L,
      TimeUnit.MILLISECONDS,
      new LinkedBlockingQueue<>()
    );

    logger.info("启动定时器线程池");

    SCHEDULE_SERVICE = new ScheduledThreadPoolExecutor(
      systemConfig.scheduleThreads,
      Executors.defaultThreadFactory(),
      (runnable, executor) -> {
        throw new CoreException("添加计划任务到线程池失败  " + runnable.toString() + " -> " + executor.toString());
      }
    );

    //= ========================================================================
    //= 启动模块

    schema.bootModule();

    //= ========================================================================
    //= 注册钩子

    Thread currentThread = Thread.currentThread();

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {

      LATCH.signal();

      try {
        currentThread.join();
      } catch (InterruptedException exception) {
        FurryBlack.println("[FurryBlack][EXIT]FATAL -> Shutdown hook interrupted, Shutdown process not finished.");
        exception.printStackTrace();
      }

      FurryBlack.println("[FurryBlack][EXIT]FurryBlack normally closed, Bye.");

      if (SHUTDOWN_HALT) {
        FurryBlack.println("[FurryBlack][EXIT]FurryBlack normally close with halt, Execute halt now.");
        Runtime.getRuntime().halt(1);
      } else if (SHUTDOWN_DROP) {
        FurryBlack.println("[FurryBlack][EXIT]FurryBlack normally close with drop, Execute halt now.");
        Runtime.getRuntime().halt(1);
      }
    }));

    //= ================================================================================================================
    //= 机器人子系统
    //= ================================================================================================================

    //= ========================================================================
    //= 列出所有好友和群组

    if (!kernelConfig.noLogin) {

      logger.seek("机器人账号 " + bot.getId());
      logger.seek("机器人昵称 " + bot.getNick());
      logger.seek("机器人头像 " + bot.getAvatarUrl());

      logger.hint("所有好友");
      bot.getFriends().forEach(item -> logger.info(FurryBlack.getFormattedNickName(item)));

      logger.hint("所有群组");
      bot.getGroups().forEach(item -> logger.info(FurryBlack.getGroupInfo(item)));

    }

    //= ================================================================================================================
    //= 插件子系统
    //= ================================================================================================================

    //= ========================================================================
    //= 生成模板消息

    logger.hint("生成模板消息");

    logger.info("组装用户list消息");
    MESSAGE_LIST_USERS = schema.generateUsersExecutorList();
    logger.info("组装群组list消息");
    MESSAGE_LIST_GROUP = schema.generateGroupExecutorList();

    //= ================================================================================================================
    //= 控制台子系统
    //= ================================================================================================================

    dispatcher = new Dispatcher();

    //= ========================================================================

    dispatcher.registerFunction()
      .command("info")
      .function(it -> FurryBlack.println(CONTENT_INFO));

    //= ========================================================================

    dispatcher.registerFunction()
      .command("help")
      .command("?")
      .function(it -> FurryBlack.println(CONTENT_HELP));

    //= ========================================================================

    dispatcher.registerFunction()
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

        FurryBlack.println(

          // @formatter:off

          "命名空间: " + (NAMESPACE == null || NAMESPACE.isBlank() ? "无" : NAMESPACE ) + LINE +
          "调试开关: " + (kernelConfig.debug ? "调试模式" : "生产模式") + LINE +
          "安全策略: " + (kernelConfig.unsafe ? "宽松策略" : "标准策略") + LINE +
          "终端模式: " + (kernelConfig.noJline ? "精简终端" : "完整终端") + LINE +
          "登录模式: " + (kernelConfig.noLogin ? "跳过登录" : "真实登录") + LINE +
          "关闭策略: " + (SHUTDOWN_HALT ? "强制退出" : "正常退出") + LINE +
          "消息事件: " + (EVENT_ENABLE ? "正常监听" : "忽略消息") + LINE +
          "核心数量: " + Runtime.getRuntime().availableProcessors() + LINE +
          "最大内存: " + maxMemoryH + "/" + maxMemory + LINE +
          "已用内存: " + useMemoryH + "/" + useMemory + LINE +
          "空闲内存: " + freeMemoryH + "/" + freeMemory + LINE +
          "分配内存: " + totalMemoryH + "/" + totalMemory + LINE +
          "运行时间: " + TimeEnhance.duration(System.currentTimeMillis() - BOOT_TIME)

          // @formatter:on

        );

      });

    dispatcher.registerFunction()
      .command("system", "stacks")
      .function(it -> {

        Map<Thread, StackTraceElement[]> stackTraces = Thread.getAllStackTraces();

        ArrayList<Map.Entry<Thread, StackTraceElement[]>> entries = new ArrayList<>(stackTraces.entrySet());

        entries.sort((o1, o2) -> {
          if (o1 == o2) return 0;
          Thread o1Key = o1.getKey();
          Thread o2Key = o2.getKey();
          return (int) (o1Key.getId() - o2Key.getId());
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
          builder.append(k.getId()).append(" ").append(k.getState());
          builder.append(" (").append(k.getName()).append(") ").append(k.getPriority());
          builder.append(" [").append(k.getThreadGroup().getName()).append("]").append(LINE);
          for (StackTraceElement element : v) {
            builder.append("    ").append(element.getClassName()).append(":").append(element.getMethodName()).append("(").append(element.getLineNumber()).append(")").append(LINE);
          }
          FurryBlack.println(builder);
        }
      });

    //= ========================================================================

    dispatcher.registerExclusive()
      .command("system", "debug")
      .function(it -> {
        if (it == null) {
          FurryBlack.println("DEBUG模式 -> " + (KERNEL_DEBUG ? "已开启" : "已关闭"));
        } else {
          switch (it.getOrEmpty(0).toLowerCase()) {
            case "enable" -> {
              kernelConfig.debug = true;
              FurryBlack.println("DEBUG模式: 启动");
            }
            case "disable" -> {
              kernelConfig.debug = false;
              FurryBlack.println("DEBUG模式: 关闭");
            }
            default -> FurryBlack.println("USAGE: system debug enable|disable");
          }
        }
      });

    //= ========================================================================

    dispatcher.registerFunction()
      .command("system", "power-off")
      .command("exit")
      .command("quit")
      .command("stop")
      .function(it -> {
        FurryBlack.println(YELLOW + "CONSOLE invoke -> shutdown" + RESET);
        Runtime.getRuntime().exit(0);
      });

    //= ========================================================================

    dispatcher.registerFunction()
      .command("system", "rapid-stop")
      .function(it -> {
        SHUTDOWN_DROP = true;
        FurryBlack.println(RED + "⚠ !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ⚠" + RESET);
        FurryBlack.println(RED + "⚠ WARNING WARNING WARNING WARNING WARNING ⚠" + RESET);
        FurryBlack.println(RED + "⚠                                         ⚠" + RESET);
        FurryBlack.println(RED + "⚠   This command will skip all waiting    ⚠" + RESET);
        FurryBlack.println(RED + "⚠     It is not good for your health      ⚠" + RESET);
        FurryBlack.println(RED + "⚠       Wish we can see you again         ⚠" + RESET);
        FurryBlack.println(RED + "⚠                                         ⚠" + RESET);
        FurryBlack.println(RED + "⚠ WARNING WARNING WARNING WARNING WARNING ⚠" + RESET);
        FurryBlack.println(RED + "⚠ !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ⚠" + RESET);
        Runtime.getRuntime().exit(0);
      });

    //= ========================================================================

    dispatcher.registerFunction()
      .command("kill")
      .function(command -> {
        FurryBlack.println(RED + "💀 !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! 💀" + RESET);
        FurryBlack.println(RED + "💀 FATAL FATAL FATAL FATAL FATAL FATAL 💀" + RESET);
        FurryBlack.println(RED + "💀                                     💀" + RESET);
        FurryBlack.println(RED + "💀        Directly halt invoking       💀" + RESET);
        FurryBlack.println(RED + "💀       There is no turning back      💀" + RESET);
        FurryBlack.println(RED + "💀      JVM will be termination now    💀" + RESET);
        FurryBlack.println(RED + "💀                                     💀" + RESET);
        FurryBlack.println(RED + "💀 FATAL FATAL FATAL FATAL FATAL FATAL 💀" + RESET);
        FurryBlack.println(RED + "💀 !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! 💀" + RESET);
        FurryBlack.println(RED + "[FurryBlack][FATAL] Invoke -> Runtime.getRuntime().halt(1)" + RESET);
        Runtime.getRuntime().halt(1);
      });

    //= ========================================================================

    dispatcher.registerFunction()
      .command("system", "force-exit")
      .function(command -> {
        if (SHUTDOWN_KILL) {
          FurryBlack.println(RED + "💀 !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! 💀" + RESET);
          FurryBlack.println(RED + "💀 FATAL FATAL FATAL FATAL FATAL FATAL 💀" + RESET);
          FurryBlack.println(RED + "💀                                     💀" + RESET);
          FurryBlack.println(RED + "💀         Intention confirmed         💀" + RESET);
          FurryBlack.println(RED + "💀       There is no turning back      💀" + RESET);
          FurryBlack.println(RED + "💀      JVM will be termination now    💀" + RESET);
          FurryBlack.println(RED + "💀                                     💀" + RESET);
          FurryBlack.println(RED + "💀 FATAL FATAL FATAL FATAL FATAL FATAL 💀" + RESET);
          FurryBlack.println(RED + "💀 !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! 💀" + RESET);
          FurryBlack.println(RED + "[FurryBlack][FATAL] Invoke -> Runtime.getRuntime().halt(1)" + RESET);
          Runtime.getRuntime().halt(1);
        } else {
          logger.fatal(RED + "⚠ !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ⚠" + RESET);
          logger.fatal(RED + "⚠ WARNING WARNING WARNING WARNING WARNING ⚠" + RESET);
          logger.fatal(RED + "⚠                                         ⚠" + RESET);
          logger.fatal(RED + "⚠   This command will kill JVM directly   ⚠" + RESET);
          logger.fatal(RED + "⚠   Input it again to confirm intention   ⚠" + RESET);
          logger.fatal(RED + "⚠                                         ⚠" + RESET);
          logger.fatal(RED + "⚠ WARNING WARNING WARNING WARNING WARNING ⚠" + RESET);
          logger.fatal(RED + "⚠ !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ⚠" + RESET);
          SHUTDOWN_KILL = true;
        }
      });

    //= ========================================================================

    dispatcher.registerFunction()
      .command("logger", "color")
      .command("color")
      .function(it -> FurryBlack.println(CONTENT_COLOR));

    //= ========================================================================

    dispatcher.registerExclusive()
      .command("logger", "level")
      .function(it -> {
        if (it == null) {
          FurryBlack.println("当前日志级别 -> " + LoggerXFactory.getLevel());
        } else {

          LoggerXLevel of = LoggerXLevel.of(it.getOrEmpty(0));

          if (of == null) {
            FurryBlack.println("日志级别不存在 -> " + it.getOrEmpty(0));
            FurryBlack.println(

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
            FurryBlack.println("日志级别修改为 -> " + LoggerXFactory.getLevel());
          }
        }
      });

    //= ========================================================================

    dispatcher.registerExclusive()
      .command("logger", "verbose", "name")
      .function(it -> {
        if (it == null) {
          FurryBlack.println("当前详细名称 -> " + LoggerXFactory.isEnableFullName());
        } else {
          if (it.getBooleanOrFalse(0)) {
            LoggerXFactory.setEnableFullName(true);
            FurryBlack.println("设置详细名称为 -> 开启");
          } else {
            LoggerXFactory.setEnableFullName(false);
            FurryBlack.println("设置详细名称为 -> 关闭");
          }
        }
      });

    //= ========================================================================

    dispatcher.registerFunction()
      .command("schema")
      .function(it -> FurryBlack.println(schema.verboseStatus()));

    //= ========================================================================

    dispatcher.registerExclusive()
      .command("schema", "event")
      .function(it -> {
        if (it == null) {
          FurryBlack.println("SCHEMA模式, 是否响应消息事件 -> " + (KERNEL_DEBUG ? "已开启" : "已关闭"));
        } else {
          switch (it.getOrEmpty(0).toLowerCase()) {
            case "enable" -> {
              kernelConfig.debug = true;
              FurryBlack.println("SCHEMA模式: 启动");
            }
            case "disable" -> {
              kernelConfig.debug = false;
              FurryBlack.println("SCHEMA模式: 关闭");
            }
            default -> FurryBlack.println("USAGE: system debug enable|disable");
          }
        }
      });

    //= ========================================================================

    dispatcher.registerExclusive()
      .command("schema", "plugin")
      .function(it -> {

        StringEnhance.LineBuilder builder = new StringEnhance.LineBuilder();

        for (Map.Entry<String, Schema.Plugin> pluginEntry : schema.getAllPlugin()) {

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

        FurryBlack.println(builder);

      });

    //= ========================================================================

    dispatcher.registerExclusive()
      .command("schema", "module")
      .function(it -> {

        if (it == null) {

          StringEnhance.LineBuilder builder = new StringEnhance.LineBuilder();

          Map<Runner, Boolean> listRunner = schema.listRunner();
          builder.append(BRIGHT_CYAN + ">> 定时器 " + listRunner.size() + RESET);
          for (Map.Entry<Runner, Boolean> entry : listRunner.entrySet()) {
            builder.append((entry.getValue() ? "开 " : "关 ") + entry.getKey().value());
          }

          Map<Filter, Boolean> listFilter = schema.listFilter();
          builder.append(BRIGHT_CYAN + ">> 过滤器 " + listFilter.size() + RESET);
          for (Map.Entry<Filter, Boolean> entry : listFilter.entrySet()) {
            builder.append((entry.getValue() ? "开 " : "关 ") + entry.getKey().value() + "{" + (entry.getKey().users() ? "U" : "") + (entry.getKey().group() ? "G" : "") + "}");
          }

          Map<Monitor, Boolean> listMonitor = schema.listMonitor();
          builder.append(BRIGHT_CYAN + ">> 监听器 " + listMonitor.size() + RESET);
          for (Map.Entry<Monitor, Boolean> entry : listMonitor.entrySet()) {
            builder.append((entry.getValue() ? "开 " : "关 ") + entry.getKey().value() + "{" + (entry.getKey().users() ? "U" : "") + (entry.getKey().group() ? "G" : "") + "}");
          }

          Map<Checker, Boolean> listChecker = schema.listChecker();
          builder.append(BRIGHT_CYAN + ">> 检查器 " + listChecker.size() + RESET);
          for (Map.Entry<Checker, Boolean> entry : listChecker.entrySet()) {
            builder.append((entry.getValue() ? "开 " : "关 ") + entry.getKey().value() + "[" + entry.getKey().command() + "]" + "{" + (entry.getKey().users() ? "U" : "") + (entry.getKey().group() ? "G" : "") + "}");
          }

          Map<Executor, Boolean> listExecutor = schema.listExecutor();
          builder.append(BRIGHT_CYAN + ">> 执行器 " + listExecutor.size() + RESET);
          for (Map.Entry<Executor, Boolean> entry : listExecutor.entrySet()) {
            builder.append((entry.getValue() ? "开 " : "关 ") + entry.getKey().value() + "[" + entry.getKey().command() + "]{" + (entry.getKey().users() ? "U" : "") + (entry.getKey().group() ? "G" : "") + "}");
          }

          List<Checker> globalUsersChecker = schema.listGlobalUsersChecker();
          builder.append(BRIGHT_CYAN + ">> 全局私聊检查器 " + globalUsersChecker.size() + RESET);
          for (Checker annotation : globalUsersChecker) {
            builder.append(annotation.value());
          }

          List<Checker> globalGroupChecker = schema.listGlobalGroupChecker();
          builder.append(BRIGHT_CYAN + ">> 全局群聊检查器 " + globalGroupChecker.size() + RESET);
          for (Checker annotation : globalGroupChecker) {
            builder.append("  " + annotation.value());
          }

          Map<String, List<Checker>> listCommandUsersChecker = schema.listCommandsUsersChecker();
          builder.append(BRIGHT_CYAN + ">> 有限私聊检查器 " + listCommandUsersChecker.size() + RESET);
          for (Map.Entry<String, List<Checker>> entry : listCommandUsersChecker.entrySet()) {
            builder.append(entry.getKey() + " " + entry.getValue().size());
            for (Checker item : entry.getValue()) {
              builder.append("  " + item.value());
            }
          }

          Map<String, List<Checker>> listCommandGroupChecker = schema.listCommandsGroupChecker();
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

          FurryBlack.println(builder);

        } else {

          String type = it.getOrNull(0);
          String name = it.getOrNull(1);

          if (type == null || name == null) {
            FurryBlack.println("USAGE: schema module init|boot|shut|reboot|unload <name>");
            return;
          }

          switch (type) {
            case "init" -> schema.initModule(name);
            case "boot" -> schema.bootModule(name);
            case "shut" -> schema.shutModule(name);
            case "reboot" -> schema.rebootModule(name);
            case "unload" -> schema.unloadModule(name);
            default -> FurryBlack.println("USAGE: schema module init|boot|shut|reboot|unload <name>");
          }
        }
      });

    //= ========================================================================

    dispatcher.registerFunction()
      .command("nickname")
      .function(it -> FurryBlack.println("USAGE: nickname list|load|clean|reload|export"));

    //= ========================================================================

    dispatcher.registerExclusive()
      .command("nickname", "list")
      .function(it -> {
        FurryBlack.println(BRIGHT_CYAN + "全局昵称 " + nickname.getNicknameGlobal().size() + RESET);
        for (Map.Entry<Long, String> entry : nickname.getNicknameGlobal().entrySet()) {
          FurryBlack.println(entry.getKey() + ":" + entry.getValue());
        }
        FurryBlack.println(BRIGHT_CYAN + "群内昵称 " + nickname.getNicknameGroups().size() + RESET);
        for (Map.Entry<Long, Map<Long, String>> groupsEntry : nickname.getNicknameGroups().entrySet()) {
          FurryBlack.println("> " + groupsEntry.getKey());
          for (Map.Entry<Long, String> nicknameEntry : groupsEntry.getValue().entrySet()) {
            FurryBlack.println(nicknameEntry.getKey() + ":" + nicknameEntry.getValue());
          }
        }
      });

    //= ========================================================================

    dispatcher.registerExclusive()
      .command("nickname", "clean")
      .function(it -> {
        nickname.cleanNickname();
        FurryBlack.println("昵称已清空");
      });

    //= ========================================================================

    dispatcher.registerExclusive()
      .command("nickname", "append")
      .function(it -> {
        nickname.appendNickname();
        FurryBlack.println("昵称已续加");
      });

    //= ========================================================================

    dispatcher.registerExclusive()
      .command("nickname", "reload")
      .function(it -> {
        nickname.cleanNickname();
        nickname.appendNickname();
        FurryBlack.println("昵称已重载");
      });

    //= ========================================================================

    dispatcher.registerExclusive()
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
        FurryBlack.println("昵称已导出 -> " + path);
      });

    //= ========================================================================

    terminal.updateCompleter();

    //= ========================================================================

    Thread consoleThread = new Thread(() -> {
      while (true) {
        String readLine = terminal.readLine();
        if (readLine == null || readLine.isBlank()) {
          continue;
        }
        readLine = readLine.trim();
        try {
          boolean exist = dispatcher.execute(readLine);
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

    //= ========================================================================
    //= 安全模式

    kernelConfig.unsafe = false;

    //= ========================================================================
    //= 启动订阅

    EVENT_ENABLE = true;

    //= ========================================================================
    //= 启动完成

    logger.hint("系统启动完成 耗时" + TimeEnhance.duration(System.currentTimeMillis() - BOOT_TIME));

    //= ========================================================================
    //= 正常工作

    LATCH.await();

    //= ================================================================================================================
    //= ================================================================================================================
    //= ================================================================================================================
    //= ================================================================================================================
    //= ================================================================================================================
    //= ================================================================================================================
    //= ================================================================================================================
    //= ================================================================================================================

    //= ========================================================================
    //= 关闭事件响应

    EVENT_ENABLE = false;

    //= ================================================================================================================
    //= 机器人子系统
    //= ================================================================================================================

    //= ========================================================================
    //= 取消消息订阅

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

    //= ================================================================================================================
    //= 插件子系统
    //= ================================================================================================================

    //= ========================================================================
    //= 关闭模块

    try {
      schema.shutModule();
    } catch (Exception exception) {
      logger.error("关闭插件模型发生异常", exception);
    }

    //= ========================================================================
    //= 关闭线程池

    logger.hint("关闭线程池");

    CompletableFuture<Void> monitorShutdown = CompletableFuture.runAsync(() -> {
      if (SHUTDOWN_DROP) {
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
      if (SHUTDOWN_DROP) {
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

    //= ================================================================================================================
    //= 机器人子系统
    //= ================================================================================================================

    logger.hint("关闭机器人");

    //= ========================================================================
    //= 关闭机器人

    logger.info("通知机器人关闭");

    if (kernelConfig.noLogin) {
      logger.warn("调试模式 不需要关闭机器人");
    } else {
      if (SHUTDOWN_DROP) {
        bot.close(null);
      } else {
        logger.info("机器人关闭中");
        bot.closeAndJoin(null);
      }
    }

    logger.info("机器人已关闭");

  }

  //= ==================================================================================================================
  //=
  //= MiraiLogger
  //=
  //= ==================================================================================================================

  private static class MiraiLoggerX implements MiraiLogger {

    private final LoggerX logger;

    protected MiraiLoggerX(String name) {
      this.logger = LoggerXFactory.getLogger(name);
    }

    @Override public String getIdentity() {
      return logger.getName();
    }

    @Override public boolean isEnabled() {
      return true;
    }

    @Override public boolean isErrorEnabled() {
      return logger.isErrorEnabled();
    }

    @Override public boolean isWarningEnabled() {
      return logger.isWarnEnabled();
    }

    @Override public boolean isInfoEnabled() {
      return logger.isInfoEnabled();
    }

    @Override public boolean isDebugEnabled() {
      return logger.isDebugEnabled();
    }

    @Override public boolean isVerboseEnabled() {
      return logger.isTraceEnabled();
    }

    @Override public void error(String message) {
      if (message == null) return;
      logger.error(message);
    }

    @Override public void error(Throwable throwable) {
      if (throwable == null) return;
      logger.error(StringEnhance.extractStackTrace(throwable));
    }

    @Override public void error(String message, Throwable throwable) {
      if (throwable == null) error(message);
      if (message == null) error(throwable);
      logger.error(message, throwable);
    }

    @Override public void warning(String message) {
      if (message == null) return;
      logger.warn(message);
    }

    @Override public void warning(Throwable throwable) {
      if (throwable == null) return;
      logger.warn(StringEnhance.extractStackTrace(throwable));
    }

    @Override public void warning(String message, Throwable throwable) {
      if (throwable == null) warning(message);
      if (message == null) warning(throwable);
      logger.warn(message, throwable);
    }

    @Override public void info(String message) {
      if (message == null) return;
      logger.info(message);
    }

    @Override public void info(Throwable throwable) {
      if (throwable == null) return;
      logger.info(StringEnhance.extractStackTrace(throwable));
    }

    @Override public void info(String message, Throwable throwable) {
      if (throwable == null) info(message);
      if (message == null) info(throwable);
      logger.info(message, throwable);
    }

    @Override public void debug(String message) {
      if (message == null) return;
      logger.error(message);

    }

    @Override public void debug(Throwable throwable) {
      if (throwable == null) return;
      logger.debug(StringEnhance.extractStackTrace(throwable));
    }

    @Override public void debug(String message, Throwable throwable) {
      if (throwable == null) debug(message);
      if (message == null) debug(throwable);
      logger.debug(message, throwable);
    }

    @Override public void verbose(String message) {
      if (message == null) return;
      logger.trace(message);
    }

    @Override public void verbose(Throwable throwable) {
      if (throwable == null) return;
      logger.trace(StringEnhance.extractStackTrace(throwable));
    }

    @Override public void verbose(String message, Throwable throwable) {
      if (throwable == null) verbose(message);
      if (message == null) verbose(throwable);
      logger.trace(message, throwable);
    }

  }

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
      System.out.println(message + LINE);
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
      printImpl(CONSOLE_PROMPT);
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
        exception.printStackTrace();
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
      if (kernelConfig.noJline) {
        completerDelegate = null;
        reader = null;
      } else {
        completerDelegate = new CompleterDelegate();
        reader = LineReaderBuilder.builder().completer(completerDelegate).build();
        AutopairWidgets autopairWidgets = new AutopairWidgets(reader);
        autopairWidgets.enable();
      }
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

        return new AggregateCompleter(

          // system dump
          // system status
          // system power-off
          // system rapid-stop
          // system force-exit
          new TreeCompleter(node("system", node("status", "dump", "power-off", "rapid-stop", "force-exit"))),

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
          new TreeCompleter(node("schema", node("module", node("init", "boot", "shut", "reboot", "unload", node(new StringsCompleter(schema.listModuleName())))))),

          // nickname list
          // nickname clean
          // nickname append
          // nickname reload
          // nickname export
          new TreeCompleter(node("nickname", node("list", "clean", "append", "reload", "export"))),

          // logger level xxx
          new TreeCompleter(node("logger", node("level", node("TRACE", "DEBUG", "INFO", "SEEK", "HINT", "WARN", "ERROR", "FATAL", "CLOSE")))),

          // logger verbose name
          // logger verbose slf4j
          new TreeCompleter(node("logger", node("verbose", node("name", node("true", "false"))))),

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

    public static ConsoleCommand of(String command) {
      return new ConsoleCommand(command);
    }

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
              if (builder.length() == 0) {
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

    public String[] copy() {
      String[] copy = new String[args.length];
      System.arraycopy(args, 0, copy, 0, args.length);
      return copy;
    }

    public ConsoleSubCommand subCommand(int i) {
      if (i > args.length) {
        throw new IllegalArgumentException("Too long");
      }
      String[] copy = new String[args.length - i];
      System.arraycopy(args, i, copy, 0, copy.length);
      return new ConsoleSubCommand(copy);
    }

    public int length() {
      return args.length;
    }

    @Override
    public String toString() {
      StringBuilder builder = new StringBuilder();
      for (String arg : args) {
        if (arg.contains(" ")) {
          builder.append("'").append(arg).append("'");
        } else {
          builder.append(arg);
        }
        builder.append(" ");
      }
      builder.setLength(builder.length() - 1);
      return builder.toString();
    }
  }

  @SuppressWarnings("unused")
  private record ConsoleSubCommand(String[] args) {

    public String getOrNull(int i) {
      return i < args.length ? args[i] : null;
    }

    public String getOrEmpty(int i) {
      return i < args.length ? args[i] : "";
    }

    public boolean getBooleanOrTrue(int i) {
      if (i < args.length) {
        return Boolean.parseBoolean(args[i]);
      } else {
        return true;
      }
    }

    public boolean getBooleanOrFalse(int i) {
      if (i < args.length) {
        return Boolean.parseBoolean(args[i]);
      } else {
        return false;
      }
    }

    @Override
    public String toString() {
      StringBuilder builder = new StringBuilder();
      for (String arg : args) {
        if (arg.contains(" ")) {
          builder.append("'").append(arg).append("'");
        } else {
          builder.append(arg);
        }
        builder.append(" ");
      }
      builder.setLength(builder.length() - 1);
      return builder.toString();
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
        if (dispatcher.tree.isAdopted()) {
          throw new IllegalArgumentException("Can't register this command -> " + String.join(".", command));
        }
        commands.add(command);
        return this;
      }

      public void function(Consumer<ConsoleSubCommand> function) {
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
        if (dispatcher.tree.isAdopted()) {
          throw new IllegalArgumentException("Can't register this command -> " + String.join(".", command));
        }
        commands.add(command);
        return this;
      }

      public void function(Consumer<ConsoleSubCommand> function) {
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
    public Consumer<ConsoleSubCommand> function;

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

    public synchronized void registerFunction(List<String[]> commands, Consumer<ConsoleSubCommand> function) {
      for (String[] command : commands) {
        if (isAdopted(command)) {
          throw new RuntimeException("Command already registered -> " + String.join(" ", command));
        }
      }
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

    public synchronized void registerExclusive(List<String[]> commands, Consumer<ConsoleSubCommand> function) {
      for (String[] command : commands) {
        if (isAdopted(command)) {
          throw new RuntimeException("Command already registered -> " + String.join(" ", command));
        }
      }
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
      String[] args = consoleCommand.copy();
      for (String arg : args) {
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
        ConsoleSubCommand subCommand = consoleCommand.subCommand(node.depth);
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
  //= 插件子系统
  //=
  //= ==================================================================================================================

  //= ==================================================================================================================
  //= 插件系统

  private static final class Schema {

    private final LoggerX logger = LoggerXFactory.getLogger("Schema");

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

    public Schema(Path folder) {

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
        return (T) collect.get(0);
      } else {
        return null;
      }
    }

    //= ========================================================================
    //= 生成信息

    public String generateUsersExecutorList() {
      if (EXECUTOR_USERS_POOL.size() == 0) {
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
      if (EXECUTOR_GROUP_POOL.size() == 0) {
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

      if (listFiles.size() == 0) {
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

        logger.info("模块冲突检查 -> " + pluginName);

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

        logger.info("冲突检查通过 -> " + pluginName);

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
        logger.info("预载定时器" + annotation.value() + "[" + annotation.priority() + "] -> " + instance.getClass().getName());
        try {
          instance.initWrapper();
        } catch (Exception exception) {
          throw new SchemaException("预载定时器失败 " + MODULE_PLUGIN_RELATION.get(annotation.value()) + ":" + annotation.value() + " -> " + instance.getClass().getName(), exception);
        }
      }

      logger.hint("预载过滤器");

      for (Filter annotation : SORTED_FILTER) {
        EventHandlerFilter instance = COMPONENT_FILTER_INSTANCE.get(annotation);
        logger.info("预载过滤器" + annotation.value() + "[" + annotation.priority() + "] -> " + instance.getClass().getName());
        try {
          instance.initWrapper();
        } catch (Exception exception) {
          throw new SchemaException("预载过滤器失败 " + MODULE_PLUGIN_RELATION.get(annotation.value()) + ":" + annotation.value() + " -> " + instance.getClass().getName(), exception);
        }
      }

      logger.hint("预载监听器");

      for (Monitor annotation : SORTED_MONITOR) {
        EventHandlerMonitor instance = COMPONENT_MONITOR_INSTANCE.get(annotation);
        logger.info("预载监听器" + annotation.value() + "[" + annotation.priority() + "] -> " + instance.getClass().getName());
        try {
          instance.initWrapper();
        } catch (Exception exception) {
          throw new SchemaException("预载监听器失败 " + MODULE_PLUGIN_RELATION.get(annotation.value()) + ":" + annotation.value() + " -> " + instance.getClass().getName(), exception);
        }
      }

      logger.hint("预载检查器");

      for (Checker annotation : SORTED_CHECKER) {
        EventHandlerChecker instance = COMPONENT_CHECKER_INSTANCE.get(annotation);
        logger.info("预载检查器" + annotation.value() + "[" + annotation.priority() + "] -> " + instance.getClass().getName());
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
        logger.info("预载执行器" + annotation.value() + "[" + annotation.command() + "] -> " + instance.getClass().getName());
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
        EventHandlerRunner clazz = COMPONENT_RUNNER_INSTANCE.get(annotation);
        logger.info("启动定时器" + annotation.value() + "[" + annotation.priority() + "] -> " + clazz.getClass().getName());
        try {
          clazz.bootWrapper();
        } catch (Exception exception) {
          throw new SchemaException("启动定时器失败 " + MODULE_PLUGIN_RELATION.get(annotation.value()) + ":" + annotation.value() + " -> " + clazz.getClass().getName(), exception);
        }
      }

      logger.hint("启动过滤器");

      for (Filter annotation : SORTED_FILTER) {
        EventHandlerFilter clazz = COMPONENT_FILTER_INSTANCE.get(annotation);
        logger.info("启动过滤器" + annotation.value() + "[" + annotation.priority() + "] -> " + clazz.getClass().getName());
        try {
          clazz.bootWrapper();
        } catch (Exception exception) {
          throw new SchemaException("启动过滤器失败 " + MODULE_PLUGIN_RELATION.get(annotation.value()) + ":" + annotation.value() + " -> " + clazz.getClass().getName(), exception);
        }
      }

      logger.hint("启动监听器");

      for (Monitor annotation : SORTED_MONITOR) {
        EventHandlerMonitor clazz = COMPONENT_MONITOR_INSTANCE.get(annotation);
        logger.info("启动监听器" + annotation.value() + "[" + annotation.priority() + "] -> " + clazz.getClass().getName());
        try {
          clazz.bootWrapper();
        } catch (Exception exception) {
          throw new SchemaException("启动监听器失败 " + MODULE_PLUGIN_RELATION.get(annotation.value()) + ":" + annotation.value() + " -> " + clazz.getClass().getName(), exception);
        }
      }

      logger.hint("启动检查器");

      for (Checker annotation : SORTED_CHECKER) {
        EventHandlerChecker clazz = COMPONENT_CHECKER_INSTANCE.get(annotation);
        logger.info("启动检查器" + annotation.value() + "[" + annotation.priority() + "] -> " + clazz.getClass().getName());
        try {
          clazz.bootWrapper();
        } catch (Exception exception) {
          throw new SchemaException("启动检查器失败 " + MODULE_PLUGIN_RELATION.get(annotation.value()) + ":" + annotation.value() + " -> " + clazz.getClass().getName(), exception);
        }
      }

      logger.hint("启动执行器");

      for (Map.Entry<Executor, EventHandlerExecutor> entry : COMPONENT_EXECUTOR_INSTANCE.entrySet()) {
        Executor annotation = entry.getKey();
        EventHandlerExecutor clazz = entry.getValue();
        logger.info("启动执行器" + annotation.value() + "[" + annotation.command() + "] -> " + clazz.getClass().getName());
        try {
          clazz.bootWrapper();
        } catch (Exception exception) {
          throw new SchemaException("启动执行器失败 " + MODULE_PLUGIN_RELATION.get(annotation.value()) + ":" + annotation.value() + " -> " + clazz.getClass().getName(), exception);
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
        try {
          if (SHUTDOWN_DROP) {
            logger.info("丢弃执行器" + annotation.value() + "[" + annotation.command() + "] -> " + instance.getClass().getName());
            Thread thread = new Thread(instance::shutWrapper);
            thread.setDaemon(true);
            thread.start();
          } else {
            logger.info("关闭执行器" + annotation.value() + "[" + annotation.command() + "] -> " + instance.getClass().getName());
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
        try {
          if (SHUTDOWN_DROP) {
            logger.info("丢弃检查器" + annotation.value() + "[" + annotation.command() + "/" + annotation.priority() + "] -> " + instance.getClass().getName());
            Thread thread = new Thread(instance::shutWrapper);
            thread.setDaemon(true);
            thread.start();
          } else {
            logger.info("关闭检查器" + annotation.value() + "[" + annotation.command() + "/" + annotation.priority() + "] -> " + instance.getClass().getName());
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
        try {
          if (SHUTDOWN_DROP) {
            logger.info("丢弃检查器" + annotation.value() + "[" + annotation.priority() + "] -> " + instance.getClass().getName());
            Thread thread = new Thread(instance::shutWrapper);
            thread.setDaemon(true);
            thread.start();
          } else {
            logger.info("关闭检查器" + annotation.value() + "[" + annotation.priority() + "] -> " + instance.getClass().getName());
            instance.shutWrapper();
          }
        } catch (Exception exception) {
          logger.warn("关闭检查器异常" + annotation.value() + "[" + annotation.priority() + "] -> " + instance.getClass().getName(), exception);
        }
      }

      logger.hint("关闭过滤器");

      List<Filter> filters = new ArrayList<>(SORTED_FILTER);
      Collections.reverse(filters);
      for (Filter annotation : filters) {
        EventHandlerFilter instance = COMPONENT_FILTER_INSTANCE.get(annotation);
        try {
          if (SHUTDOWN_DROP) {
            logger.info("丢弃过滤器" + annotation.value() + "[" + annotation.priority() + "] -> " + instance.getClass().getName());
            Thread thread = new Thread(instance::shutWrapper);
            thread.setDaemon(true);
            thread.start();
          } else {
            logger.info("关闭过滤器" + annotation.value() + "[" + annotation.priority() + "] -> " + instance.getClass().getName());
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
        try {
          if (SHUTDOWN_DROP) {
            logger.info("丢弃定时器" + annotation.value() + "[" + annotation.priority() + "] -> " + instance.getClass().getName());
            Thread thread = new Thread(instance::shutWrapper);
            thread.setDaemon(true);
            thread.start();
          } else {
            logger.info("关闭定时器" + annotation.value() + "[" + annotation.priority() + "] -> " + instance.getClass().getName());
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

      if (!modules.containsKey(name)) {
        return null;
      }

      for (Map.Entry<Runner, EventHandlerRunner> entry : COMPONENT_RUNNER_INSTANCE.entrySet()) {
        if (entry.getKey().value().equals(name)) {
          return entry.getValue();
        }
      }

      for (Map.Entry<Filter, EventHandlerFilter> entry : COMPONENT_FILTER_INSTANCE.entrySet()) {
        if (entry.getKey().value().equals(name)) {
          return entry.getValue();
        }
      }

      for (Map.Entry<Monitor, EventHandlerMonitor> entry : COMPONENT_MONITOR_INSTANCE.entrySet()) {
        if (entry.getKey().value().equals(name)) {
          return entry.getValue();
        }
      }

      for (Map.Entry<Checker, EventHandlerChecker> entry : COMPONENT_CHECKER_INSTANCE.entrySet()) {
        if (entry.getKey().value().equals(name)) {
          return entry.getValue();
        }
      }

      for (Map.Entry<Executor, EventHandlerExecutor> entry : COMPONENT_EXECUTOR_INSTANCE.entrySet()) {
        if (entry.getKey().value().equals(name)) {
          return entry.getValue();
        }
      }

      return null;
    }

    //= ========================================================================
    //=  预载模块

    public void initModule(String name) {
      AbstractEventHandler moduleInstance = getModuleInstanceEnsure(name);
      if (moduleInstance == null)
        return;
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
      if (moduleInstance == null)
        return;
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
      if (moduleInstance == null)
        return;
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
      if (moduleInstance == null)
        return;
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
      instance.setEnable(false);
      instance.shutWrapper();
      logger.info("定时器已卸载 -> " + printAnnotation(annotation));
    }

    private void unloadModule(Filter annotation) {
      EventHandlerFilter instance = COMPONENT_FILTER_INSTANCE.remove(annotation);
      instance.setEnable(false);
      if (annotation.users())
        FILTER_USERS_CHAIN.remove(instance);
      if (annotation.group())
        FILTER_GROUP_CHAIN.remove(instance);
      instance.shutWrapper();
      logger.info("过滤器已卸载 -> " + printAnnotation(annotation));
    }

    private void unloadModule(Monitor annotation) {
      EventHandlerMonitor instance = COMPONENT_MONITOR_INSTANCE.remove(annotation);
      instance.setEnable(false);
      if (annotation.users())
        MONITOR_USERS_CHAIN.remove(instance);
      if (annotation.group())
        MONITOR_GROUP_CHAIN.remove(instance);
      instance.shutWrapper();
      logger.info("监听器已卸载 -> " + printAnnotation(annotation));
    }

    private void unloadModule(Checker annotation) {
      EventHandlerChecker instance = COMPONENT_CHECKER_INSTANCE.remove(annotation);
      instance.setEnable(false);
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
      instance.setEnable(false);
      if (annotation.users())
        EXECUTOR_USERS_POOL.remove(annotation.command());
      if (annotation.group())
        EXECUTOR_GROUP_POOL.remove(annotation.command());
      COMMAND_EXECUTOR_RELATION.remove(annotation.command());
      instance.shutWrapper();
      logger.info("执行器已卸载 -> " + printAnnotation(annotation));
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
        throw new CoreException("读取昵称配置文件失败 -> " + path, exception);
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
      return global.getOrDefault(userId, Mirai.getInstance().queryProfile(bot, userId).getNickname());
    }

    private String getMemberMappedNickName(Member member) {
      Map<Long, String> groupMap = groups.get(member.getGroup().getId());
      if (groupMap != null) {
        String nickName = groupMap.get(member.getId());
        if (nickName != null)
          return nickName;
      }
      String nickName = global.get(member.getId());
      if (nickName != null)
        return nickName;
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
        if (nickName != null)
          return nickName;
      }
      String nickName = global.get(userId);
      if (nickName != null)
        return nickName;
      Member member = bot.getGroupOrFail(groupId).getOrFail(userId);
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
  //= 配置子系统
  //=
  //= ==================================================================================================================

  //= ==================================================================================================================
  //= 参数模块
  //= ==================================================================================================================

  private static class Argument {

    private final Properties properties;
    private final LinkedList<String> options;
    private final LinkedHashMap<String, String> parameters;

    //= ========================================================================
    //= 名称转换

    /**
     * a,b,c -> a-b-c for args --a-b-c xxx
     */
    public static String toArgumentName(String... name) {
      String join = String.join("-", name);
      if (NAMESPACE == null)
        return join;
      return NAMESPACE + "-" + join;
    }

    /**
     * a,b,c -> a.b.c for system property -Da.b.c=xxx
     */
    public static String toPropertyName(String... name) {
      String join = String.join(".", name);
      if (NAMESPACE == null)
        return join;
      return NAMESPACE + "." + join;
    }

    /**
     * a,b,c -> A_B_C for envs export A_B_C=xxx
     */
    public static String toEnvironmentName(String... name) {
      String join = String.join("_", name);
      if (NAMESPACE == null)
        return join;
      return (NAMESPACE + "_" + join).toUpperCase();
    }

    /**
     * a,b,c -> a.b.c for property a.b.c=xxx no namespace
     */
    public static String toConfigName(String... name) {
      return String.join(".", name);
    }

    //= ========================================================================

    public static Argument parse(String[] arguments) {

      Argument instance = new Argument();
      int length = arguments.length;
      for (int i = 0; i < length; i++) {
        String argument = arguments[i].trim();
        if (argument.startsWith("--")) {
          if (i + 1 == length) {
            instance.options.add(argument.substring(2));
          } else {
            String next = arguments[i + 1];
            if (next.startsWith("--")) {
              instance.options.add(argument.substring(2));
            } else {
              instance.parameters.put(argument.substring(2), next);
              i++;
            }
          }
        } else {
          instance.options.add(argument);
        }
      }
      return instance;
    }

    //= ========================================================================

    private Argument() {
      options = new LinkedList<>();
      parameters = new LinkedHashMap<>();
      properties = new Properties();
    }

    //= ========================================================================

    public int optionSize() {
      return options.size();
    }

    public int parameterSize() {
      return parameters.size();
    }

    public LinkedList<String> cloneOptions() {
      return new LinkedList<>(options);
    }

    public LinkedHashMap<String, String> cloneParameters() {
      return new LinkedHashMap<>(parameters);
    }

    public void append(Object key, Object value) {
      properties.put(key.toString(), String.valueOf(value));
    }

    //= ========================================================================

    @Comment("环境变量 > 系统配置 > 程序参数 > 配置文件")
    public boolean checkKernelOption(String... name) {
      if (System.getenv(toEnvironmentName(name)) != null) return true;
      if (System.getProperty(toPropertyName(name)) != null) return true;
      return options.contains(toArgumentName(name));
    }

    @Nullable
    @Comment("环境变量 > 系统配置 > 程序参数 > 配置文件")
    public String getKernelParameter(String... name) {
      String value = System.getenv(toEnvironmentName(name));
      if (value != null) return value;
      value = System.getProperty(toPropertyName(name));
      if (value != null) return value;
      value = parameters.get(toArgumentName(name));
      if (value != null) return value;
      return null;
    }

    //= ========================================================================

    @Comment("环境变量 > 系统配置 > 程序参数 > 配置文件")
    public boolean checkSystemOption(String... name) {
      if (System.getenv(toEnvironmentName(name)) != null) return true;
      if (System.getProperty(toPropertyName(name)) != null) return true;
      if (options.contains(toArgumentName(name))) return true;
      return properties.getProperty(toConfigName(name)) != null;
    }

    @Nullable
    @Comment("环境变量 > 系统配置 > 程序参数 > 配置文件")
    public String getSystemParameter(String... name) {
      String value = System.getenv(toEnvironmentName(name));
      if (value != null) return value;
      value = System.getProperty(toPropertyName(name));
      if (value != null) return value;
      value = parameters.get(toArgumentName(name)); if (value != null) return value;
      value = properties.getProperty(toConfigName(name));
      if (value != null) return value;
      return null;
    }

    @Comment("环境变量 > unsafe(系统配置) > unsafe(程序参数) > 配置文件")
    public boolean checkSystemOptionSafe(String... name) {
      if (System.getenv(toEnvironmentName(name)) != null) return true;
      if (kernelConfig.unsafe) {
        if (System.getProperty(toPropertyName(name)) != null) {
          logger.warn("WARNING WARNING WARNING WARNING WARNING WARNING");
          logger.warn("从系统属性加载私密配置非常危险, 强烈建议不要使用此配置方式");
          logger.warn("WARNING WARNING WARNING WARNING WARNING WARNING");
          return true;
        }
        if (options.contains(toArgumentName(name))) {
          logger.warn("WARNING WARNING WARNING WARNING WARNING WARNING");
          logger.warn("从程序参数加载私密配置非常危险, 强烈建议不要使用此配置方式");
          logger.warn("WARNING WARNING WARNING WARNING WARNING WARNING");
          return true;
        }
      }
      return properties.getProperty(toConfigName(name)) != null;
    }

    @Nullable
    @Comment("环境变量 > unsafe(系统配置) > unsafe(程序参数) > 配置文件")
    public String getSystemParameterSafe(String... name) {
      String value = System.getenv(toEnvironmentName(name));
      if (value != null) return value;
      if (kernelConfig.unsafe) {
        value = System.getProperty(toPropertyName(name));
        if (value != null) {
          logger.warn("WARNING WARNING WARNING WARNING WARNING WARNING");
          logger.warn("从系统属性加载私密配置非常危险, 强烈建议不要使用此配置方式");
          logger.warn("WARNING WARNING WARNING WARNING WARNING WARNING");
          return value;
        }
        value = parameters.get(toArgumentName(name));
        if (value != null) {
          logger.warn("WARNING WARNING WARNING WARNING WARNING WARNING");
          logger.warn("从程序参数加载私密配置非常危险, 强烈建议不要使用此配置方式");
          logger.warn("WARNING WARNING WARNING WARNING WARNING WARNING");
          return value;
        }
      }
      value = properties.getProperty(toConfigName(name));
      if (value != null) return value;
      return null;
    }

  }

  //= ==================================================================================================================
  //= 内核参数
  //= ==================================================================================================================

  private static class KernelConfig {

    private volatile boolean debug;
    private volatile boolean unsafe;
    private boolean noLogin;
    private boolean noJline;
    private boolean fullname;
    private boolean noConsole;
    private boolean forceExit;

    private String level;
    private String prefix;
    private String provider;

    public static KernelConfig getInstance(Argument argument) {

      KernelConfig config = new KernelConfig();

      config.debug = argument.checkKernelOption(ARGS_DEBUG);
      config.unsafe = argument.checkKernelOption(ARGS_UNSAFE);
      config.noLogin = argument.checkKernelOption(ARGS_NO_LOGIN);
      config.noJline = argument.checkKernelOption(ARGS_NO_JLINE);
      config.fullname = argument.checkKernelOption(ARGS_FULL_NAME);
      config.noConsole = argument.checkKernelOption(ARGS_NO_CONSOLE);
      config.forceExit = argument.checkKernelOption(ARGS_FORCE_EXIT);

      config.level = argument.getKernelParameter(ARGS_LOGGER_LEVEL);
      config.prefix = argument.getKernelParameter(ARGS_LOGGER_PREFIX);
      config.provider = argument.getKernelParameter(ARGS_LOGGER_PROVIDER);

      return config;
    }

    private KernelConfig() {}
  }

  //= ==================================================================================================================
  //= 系统参数
  //= ==================================================================================================================

  private static class SystemConfig {

    private static final LoggerX logger = LoggerXFactory.getLogger("Config");

    AuthMode authMod;
    long username;
    String password;
    DeviceType deviceType;
    String deviceInfo;
    Pattern commandRegex;
    Integer monitorThreads;
    Integer scheduleThreads;
    Set<BotConfiguration.MiraiProtocol> upgradeProtocols;

    static SystemConfig getInstance(Argument argument) {

      SystemConfig config = new SystemConfig();

      //= ======================================================================

      String authMod = argument.getSystemParameter(CONF_ACCOUNT_AUTH);
      if (authMod == null) {
        logger.info("认证模式 -> 使用默认值");
      } else {
        config.authMod = AuthMode.of(authMod);
        logger.seek("认证模式 -> " + config.authMod);
      }

      //= ======================================================================

      String username = argument.getSystemParameter(CONF_ACCOUNT_USERNAME);
      FirstBootException.require(username, CONF_ACCOUNT_USERNAME);
      logger.seek("登录账号 -> " + username);
      config.username = parseLong(username, () -> new InvalidConfigException("账号配置有误 -> " + username));

      //= ======================================================================

      if (config.authMod == AuthMode.PASSWD) {
        String password = argument.getSystemParameterSafe(CONF_ACCOUNT_PASSWORD);
        FirstBootException.require(password, CONF_ACCOUNT_PASSWORD);
        config.password = password;
        if (kernelConfig.debug) {
          logger.warn("！！！！！！！！！！！！！！！！");
          logger.warn("调试模式开启时会在日志中记录密码");
          logger.warn("！！！！！！！！！！！！！！！！");
          logger.seek("登录密码 -> " + password);
          logger.warn("！！！！！！！！！！！！！！！！");
          logger.warn("调试模式开启时会在日志中记录密码");
          logger.warn("！！！！！！！！！！！！！！！！");
        } else {
          logger.seek("登录密码 -> " + "*".repeat(username.length()));
        }
      }

      //= ======================================================================

      String deviceType = argument.getSystemParameter(CONF_DEVICE_TYPE);
      if (deviceType == null) {
        if (config.authMod == AuthMode.QRCODE) {
          config.deviceType = DeviceType.WATCH;
          logger.info("设备类型 -> 使用默认值 WATCH");
        } else {
          config.deviceType = DeviceType.PHONE;
          logger.info("设备类型 -> 使用默认值 PHONE");
        }
      } else {
        config.deviceType = DeviceType.of(deviceType);
        if (config.authMod == AuthMode.QRCODE && config.deviceType != DeviceType.WATCH && config.deviceType != DeviceType.MACOS) {
          throw new InvalidConfigException("配置无效 - 扫码认证必须使用 WATCH/MACOS 协议");
        }
        logger.seek("设备类型 -> " + config.deviceType);
      }

      //= ======================================================================

      String deviceInfo = argument.getSystemParameter(CONF_DEVICE_INFO);
      if (deviceInfo == null) {
        logger.info("设备信息 -> 使用默认值 device.json");
      } else {
        if (!deviceInfo.matches("^[a-zA-Z0-9.]*$")) {
          throw new InvalidConfigException("配置无效 - 设备信息文件名不合法 必须满足 ^[a-zA-Z0-9.]*$");
        }
        logger.seek("设备信息 -> " + deviceInfo);
      }
      Path deviceInfoPath = FileEnhance.get(FOLDER_CONFIG, deviceInfo == null ? "device.json" : deviceInfo);

      if (Files.notExists(deviceInfoPath)) {
        throw new FirstBootException("配置无效 - 设备信息文件不存在 -> " + deviceInfoPath);
      }

      if (!Files.isRegularFile(deviceInfoPath)) {
        throw new FirstBootException("配置无效 - 设备信息不是平文件 -> " + deviceInfoPath);
      }

      try {
        config.deviceInfo = Files.readString(deviceInfoPath);
      } catch (IOException exception) {
        throw new CoreException("配置无效 - 设备信息文件无法读取 -> " + deviceInfoPath, exception);
      }

      //= ======================================================================

      String commandRegex = argument.getSystemParameter(CONF_COMMAND_REGEX);
      if (commandRegex == null) {
        config.commandRegex = Pattern.compile("^/[a-zA-Z0-9]{2,16}");
        logger.info("命令正则 -> 使用默认值 ^/[a-zA-Z0-9]{2,16}");
      } else {
        config.commandRegex = Pattern.compile(commandRegex);
        logger.seek("命令正则 -> " + commandRegex);
      }

      //= ======================================================================

      String monitorThreads = argument.getSystemParameter(CONF_THREADS_MONITOR);
      if (monitorThreads == null) {
        config.monitorThreads = CPU_CORES;
        logger.info("监听器池 -> 使用系统值 " + CPU_CORES);
      } else {
        config.monitorThreads = parseInt(monitorThreads, () -> new InvalidConfigException("监听器池配置有误 -> " + monitorThreads));
        if (config.monitorThreads <= 0) {
          config.monitorThreads = CPU_CORES;
          logger.seek("监听器池 -> 使用自动值 " + config.monitorThreads);
        } else {
          logger.seek("监听器池 -> " + config.monitorThreads);
        }
      }

      //= ======================================================================

      String scheduleThreads = argument.getSystemParameter(CONF_THREADS_SCHEDULE);
      if (scheduleThreads == null) {
        config.scheduleThreads = CPU_CORES;
        logger.info("定时器池 -> 使用系统值 " + CPU_CORES);
      } else {
        config.scheduleThreads = parseInt(scheduleThreads, () -> new InvalidConfigException("定时器池配置有误 -> " + scheduleThreads));
        if (config.scheduleThreads <= 0) {
          config.scheduleThreads = CPU_CORES;
          logger.seek("定时器池 -> 使用自动值 " + config.monitorThreads);
        } else {
          logger.seek("定时器池 -> " + config.scheduleThreads);
        }
      }

      //= ======================================================================

      String upgradeProtocols = argument.getSystemParameter(CONF_UPGRADE_PROTOCOLS);

      if (upgradeProtocols == null || upgradeProtocols.isBlank()) {
        config.upgradeProtocols = null;
      } else {
        String[] strings = upgradeProtocols.split(",");
        config.upgradeProtocols = Arrays.stream(strings)
          .map(DeviceType::of)
          .map(DeviceType::toMiraiProtocol)
          .collect(Collectors.toUnmodifiableSet());
      }

      //= ======================================================================

      return config;
    }

    private SystemConfig() {}

  }

  //= ==================================================================================================================
  //= 认证模式
  //= ==================================================================================================================

  private enum AuthMode {

    PASSWD,
    QRCODE,
    ;

    public static AuthMode of(String value) {
      return switch (value.toLowerCase()) {
        case "code", "qrcode", "scancode" -> QRCODE;
        case "pass", "passwd", "password" -> PASSWD;
        default -> throw new InvalidConfigException("ERROR: No such AuthMode -> " + value);
      };
    }
  }

  //= ==================================================================================================================
  //= 设备类型
  //= ==================================================================================================================

  private enum DeviceType {

    PAD,
    PHONE,
    WATCH,
    IPAD,
    MACOS,
    ;

    public static DeviceType of(String value) {
      return switch (value.toLowerCase()) {
        case "pad", "android_pad" -> PAD;
        case "phone", "android_phone" -> PHONE;
        case "watch", "android_watch" -> WATCH;
        case "ipad" -> IPAD;
        case "macos" -> MACOS;
        default -> throw new InvalidConfigException("ERROR: No such DeviceType -> " + value);
      };
    }

    public BotConfiguration.MiraiProtocol toMiraiProtocol() {
      return switch (this) {
        case PAD -> BotConfiguration.MiraiProtocol.ANDROID_PAD;
        case PHONE -> BotConfiguration.MiraiProtocol.ANDROID_PHONE;
        case WATCH -> BotConfiguration.MiraiProtocol.ANDROID_WATCH;
        case IPAD -> BotConfiguration.MiraiProtocol.IPAD;
        case MACOS -> BotConfiguration.MiraiProtocol.MACOS;
      };
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
    terminal.print(message.toString());
  }

  @Comment("在终端打印消息")
  public static void println(Object message) {
    if (message == null) return;
    terminal.println(message.toString());
  }

  //= ==========================================================================
  //= 框架状态

  @Comment("框架运行状态")
  public static boolean isDebug() {
    return kernelConfig.debug;
  }

  @Comment("框架运行状态")
  public static boolean isNoJline() {
    return kernelConfig.noJline;
  }

  @Comment("框架运行状态")
  public static boolean isNoConsole() {
    return kernelConfig.noConsole;
  }

  @Comment("框架运行状态")
  public static boolean isShutdownHalt() {
    return SHUTDOWN_HALT;
  }

  @Comment("框架运行状态")
  public static boolean isShutModeDrop() {
    return SHUTDOWN_DROP;
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
    return schema.getRunner(clazz);
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
    return nickname.getUsersMappedNickName(user);
  }

  @Comment("获取用户昵称")
  public static String getUsersMappedNickName(long userId) {
    return nickname.getUsersMappedNickName(userId);
  }

  @Comment("获取预设昵称")
  public static String getMappedNickName(GroupMessageEvent event) {
    return nickname.getMemberMappedNickName(event.getSender());
  }

  @Comment("获取预设昵称")
  public static String getMemberMappedNickName(Member member) {
    return nickname.getMemberMappedNickName(member);
  }

  @Comment("获取预设昵称")
  public static String getMappedNickName(long groupId, long userId) {
    return nickname.getMemberMappedNickName(groupId, userId);
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
    if (kernelConfig.unsafe) {
      return bot;
    } else {
      logger.warn("获取机器人实例禁止 只有在unsafe模式下可用");
      for (StackTraceElement stackTraceElement : Thread.currentThread().getStackTrace()) {
        System.out.println(stackTraceElement);
      }
      throw new CoreException("Get Mirai-BOT instance only allowed when --unsafe present! And only allowed before booted.");
    }
  }

  @Comment("格式化群组信息")
  public static String getGroupInfo(Group group) {
    return group.getName() + "(" + group.getId() + ") " + group.getMembers().size() + " -> " + group.getOwner().getNameCard() + "(" + group.getOwner().getId() + ")";
  }

  @Comment("获取BOT自身QQ号")
  public static long getBotID() {
    return bot.getId();
  }

  @Comment("列出所有好友")
  public static ContactList<Friend> getFriends() {
    return bot.getFriends();
  }

  @Comment("列出所有群组")
  public static ContactList<Group> getGroups() {
    return bot.getGroups();
  }

  @Comment("根据ID获取陌生人")
  public static Stranger getStranger(long id) {
    return bot.getStranger(id);
  }

  @Comment("根据ID获取陌生人")
  public static Stranger getStrangerOrFail(long id) {
    return bot.getStrangerOrFail(id);
  }

  @Comment("根据ID获取好友")
  public static Friend getFriend(long id) {
    return bot.getFriend(id);
  }

  @Comment("根据ID获取好友")
  public static Friend getFriendOrFail(long id) {
    return bot.getFriendOrFail(id);
  }

  @Comment("根据ID获取群组")
  public static Group getGroup(long id) {
    return bot.getGroup(id);
  }

  @Comment("根据ID获取群组")
  public static Group getGroupOrFail(long id) {
    return bot.getGroupOrFail(id);
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
    return Mirai.getInstance().downloadForwardMessage(bot, resourceId);
  }

  @Comment("转发Mirai")
  public static MessageChain downloadLongMessage(String resourceId) {
    return Mirai.getInstance().downloadLongMessage(bot, resourceId);
  }

  @Comment("转发Mirai")
  public static List<OtherClientInfo> getOnlineOtherClientsList(boolean mayIncludeSelf) {
    return Mirai.getInstance().getOnlineOtherClientsList(bot, mayIncludeSelf);
  }

  @Comment("转发Mirai")
  public static long getUin() {
    return Mirai.getInstance().getUin(bot);
  }

  @Comment("转发Mirai")
  public static String queryImageUrl(Image image) {
    return Mirai.getInstance().queryImageUrl(bot, image);
  }

  @Comment("转发Mirai")
  public static UserProfile queryProfile(long id) {
    return Mirai.getInstance().queryProfile(bot, id);
  }

  @Comment("转发Mirai")
  public static void recallMessage(MessageSource messageSource) {
    Mirai.getInstance().recallMessage(bot, messageSource);
  }

  @Comment("转发Mirai")
  public static void sendNudge(Nudge nudge, Contact contact) {
    Mirai.getInstance().sendNudge(bot, nudge, contact);
  }

  //= ========================================================================
  //= 来自 LowLevelApiAccessor.kt

  @Comment("转发Mirai")
  public static void getGroupVoiceDownloadUrl(byte[] md5, long groupId, long dstUin) {
    Mirai.getInstance().getGroupVoiceDownloadUrl(bot, md5, groupId, dstUin);
  }

  @Comment("转发Mirai")
  public static Sequence<Long> getRawGroupList() {
    return Mirai.getInstance().getRawGroupList(bot);
  }

  @Comment("转发Mirai")
  public static Sequence<MemberInfo> getRawGroupMemberList(long groupUin, long groupCode, long ownerId) {
    return Mirai.getInstance().getRawGroupMemberList(bot, groupUin, groupCode, ownerId);
  }

  @Comment("转发Mirai")
  public static void muteAnonymousMember(String anonymousId, String anonymousNick, long groupId, int seconds) {
    Mirai.getInstance().muteAnonymousMember(bot, anonymousId, anonymousNick, groupId, seconds);
  }

  @Comment("转发Mirai")
  public static Friend newFriend(FriendInfo friendInfo) {
    return Mirai.getInstance().newFriend(bot, friendInfo);
  }

  @Comment("转发Mirai")
  public static Stranger newStranger(StrangerInfo strangerInfo) {
    return Mirai.getInstance().newStranger(bot, strangerInfo);
  }

  @Comment("转发Mirai")
  public static boolean recallFriendMessageRaw(long targetId, int[] messagesIds, int[] messageInternalIds, int time) {
    return Mirai.getInstance().recallFriendMessageRaw(bot, targetId, messagesIds, messageInternalIds, time);
  }

  @Comment("转发Mirai")
  public static boolean recallGroupMessageRaw(long groupCode, int[] messagesIds, int[] messageInternalIds) {
    return Mirai.getInstance().recallGroupMessageRaw(bot, groupCode, messagesIds, messageInternalIds);
  }

  @Comment("转发Mirai")
  public static boolean recallGroupTempMessageRaw(long groupUin, long targetId, int[] messagesIds, int[] messageInternalIds, int time) {
    return Mirai.getInstance().recallGroupTempMessageRaw(bot, groupUin, targetId, messagesIds, messageInternalIds, time);
  }

  @Comment("转发Mirai")
  public static void refreshKeys() {
    Mirai.getInstance().refreshKeys(bot);
  }

  @Comment("转发Mirai")
  public static void solveBotInvitedJoinGroupRequestEvent(long eventId, long invitorId, long groupId, boolean accept) {
    Mirai.getInstance().solveBotInvitedJoinGroupRequestEvent(bot, eventId, invitorId, groupId, accept);
  }

  @Comment("转发Mirai")
  public static void solveMemberJoinRequestEvent(long eventId, long fromId, String fromNick, long groupId, boolean accept, boolean blackList, String message) {
    Mirai.getInstance().solveMemberJoinRequestEvent(bot, eventId, fromId, fromNick, groupId, accept, blackList, message);
  }

  @Comment("转发Mirai")
  public static void solveNewFriendRequestEvent(long eventId, long fromId, String fromNick, boolean accept, boolean blackList) {
    Mirai.getInstance().solveNewFriendRequestEvent(bot, eventId, fromId, fromNick, accept, blackList);
  }

}
