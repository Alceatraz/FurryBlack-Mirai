/*
 * Copyright (C) 2021 Alceatraz @ BlackTechStudio
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms parse the BTS Anti-Commercial & GNU Affero General.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty parse
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * BTS Anti-Commercial & GNU Affero General Public License for more details.
 *
 * You should have received a copy parse the BTS Anti-Commercial & GNU Affero
 * General Public License along with this program in README or LICENSE.
 */

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
import org.jline.builtins.Completers.TreeCompleter;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.ParsedLine;
import org.jline.reader.UserInterruptException;
import org.jline.reader.impl.completer.AggregateCompleter;
import org.jline.reader.impl.completer.ArgumentCompleter;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.widget.AutopairWidgets;
import studio.blacktech.furryblack.core.enhance.DigestTool;
import studio.blacktech.furryblack.core.enhance.TimeTool;
import studio.blacktech.furryblackplus.common.Comment;
import studio.blacktech.furryblackplus.core.common.enhance.FileEnhance;
import studio.blacktech.furryblackplus.core.common.enhance.LockEnhance.Latch;
import studio.blacktech.furryblackplus.core.common.enhance.StringEnhance.LineBuilder;
import studio.blacktech.furryblackplus.core.common.logger.LoggerXConfig;
import studio.blacktech.furryblackplus.core.common.logger.LoggerXFactory;
import studio.blacktech.furryblackplus.core.common.logger.base.LoggerX;
import studio.blacktech.furryblackplus.core.exception.CoreException;
import studio.blacktech.furryblackplus.core.exception.system.FirstBootException;
import studio.blacktech.furryblackplus.core.exception.system.InvalidConfigException;
import studio.blacktech.furryblackplus.core.exception.system.TerminalException;
import studio.blacktech.furryblackplus.core.handler.EventHandlerChecker;
import studio.blacktech.furryblackplus.core.handler.EventHandlerExecutor;
import studio.blacktech.furryblackplus.core.handler.EventHandlerFilter;
import studio.blacktech.furryblackplus.core.handler.EventHandlerMonitor;
import studio.blacktech.furryblackplus.core.handler.EventHandlerRunner;
import studio.blacktech.furryblackplus.core.handler.annotation.Checker;
import studio.blacktech.furryblackplus.core.handler.annotation.Executor;
import studio.blacktech.furryblackplus.core.handler.annotation.Filter;
import studio.blacktech.furryblackplus.core.handler.annotation.Monitor;
import studio.blacktech.furryblackplus.core.handler.annotation.Runner;
import studio.blacktech.furryblackplus.core.handler.common.Command;
import studio.blacktech.furryblackplus.core.schema.Plugin;
import studio.blacktech.furryblackplus.core.schema.Schema;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.lang.management.ManagementFactory;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.TimeZone;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static org.jline.builtins.Completers.TreeCompleter.node;
import static studio.blacktech.furryblackplus.core.common.enhance.DataEnhance.parseInt;
import static studio.blacktech.furryblackplus.core.common.enhance.DataEnhance.parseLong;
import static studio.blacktech.furryblackplus.core.common.enhance.StringEnhance.toHumanBytes;
import static studio.blacktech.furryblackplus.core.common.logger.base.LoggerX.Color.BLUE;
import static studio.blacktech.furryblackplus.core.common.logger.base.LoggerX.Color.BRIGHT_BLACK;
import static studio.blacktech.furryblackplus.core.common.logger.base.LoggerX.Color.BRIGHT_BLUE;
import static studio.blacktech.furryblackplus.core.common.logger.base.LoggerX.Color.BRIGHT_CYAN;
import static studio.blacktech.furryblackplus.core.common.logger.base.LoggerX.Color.BRIGHT_GREEN;
import static studio.blacktech.furryblackplus.core.common.logger.base.LoggerX.Color.BRIGHT_MAGENTA;
import static studio.blacktech.furryblackplus.core.common.logger.base.LoggerX.Color.BRIGHT_RED;
import static studio.blacktech.furryblackplus.core.common.logger.base.LoggerX.Color.BRIGHT_WHITE;
import static studio.blacktech.furryblackplus.core.common.logger.base.LoggerX.Color.BRIGHT_YELLOW;
import static studio.blacktech.furryblackplus.core.common.logger.base.LoggerX.Color.CYAN;
import static studio.blacktech.furryblackplus.core.common.logger.base.LoggerX.Color.GRAY;
import static studio.blacktech.furryblackplus.core.common.logger.base.LoggerX.Color.GREEN;
import static studio.blacktech.furryblackplus.core.common.logger.base.LoggerX.Color.MAGENTA;
import static studio.blacktech.furryblackplus.core.common.logger.base.LoggerX.Color.RED;
import static studio.blacktech.furryblackplus.core.common.logger.base.LoggerX.Color.RESET;
import static studio.blacktech.furryblackplus.core.common.logger.base.LoggerX.Color.WHITE;
import static studio.blacktech.furryblackplus.core.common.logger.base.LoggerX.Color.YELLOW;

// 🔫 🧦 ❌ ✔️ ⭕ 🚧 🀄

@Comment(
  value = "FurryBlack Plus Framework - based on Mirai",
  usage = {
    "电子白熊会梦到仿生老黑吗",
    "Alceatraz Warprays @ BlackTechStudio",
    "个人主页 https://www.blacktech.studio",
    "项目地址 https://github.com/Alceatraz/FurryBlack-Mirai",
    "插件地址 https://github.com/Alceatraz/FurryBlack-Mirai-Extensions",
  },
  attention = {
    "!!!本项目并非使用纯AGPLv3协议，请认真阅读LICENSE!!!"
  }
)
public final class FurryBlack {

  //= ==================================================================================================================
  //
  //  常量信息
  //
  //= ==================================================================================================================

  @Comment("框架版本") public static final String APP_VERSION = "3.0.0";

  private static final String[] ARGS_DEBUG = {"debug"};
  private static final String[] ARGS_UNSAFE = {"unsafe"};
  private static final String[] ARGS_UPGRADE = {"upgrade"};
  private static final String[] ARGS_NO_LOGIN = {"no", "login"};
  private static final String[] ARGS_NO_JLINE = {"no", "jline"};
  private static final String[] ARGS_NO_CONSOLE = {"no", "console"};
  private static final String[] ARGS_FORCE_EXIT = {"force", "exit"};
  private static final String[] ARGS_LOGGER_LEVEL = {"logger", "level"};
  private static final String[] ARGS_LOGGER_PROVIDER = {"logger", "provider"};

  private static final String[] CONF_DEVICE_TYPE = {"device", "type"};
  private static final String[] CONF_DEVICE_INFO = {"device", "info"};
  private static final String[] CONF_ACCOUNT_AUTH = {"account", "auth"};
  private static final String[] CONF_ACCOUNT_USERNAME = {"account", "username"};
  private static final String[] CONF_ACCOUNT_PASSWORD = {"account", "password"};
  private static final String[] CONF_COMMAND_REGEX = {"command", "prefix"};
  private static final String[] CONF_THREADS_MONITOR = {"threads", "monitor"};
  private static final String[] CONF_THREADS_SCHEDULE = {"threads", "schedule"};

  //= ==================================================================================================================
  //
  //  静态信息
  //
  //= ==================================================================================================================

  @Comment("换行符") public static final String CRLF = "\r\n";
  @Comment("换行符") public static final String LINE;

  @Comment("系统核心数量") public static final int CPU_CORES;
  @Comment("系统启动时间") public static final long BOOT_TIME;

  @Comment("原始系统时区") public static final ZoneId SYSTEM_ZONEID;
  @Comment("原始系统偏差") public static final ZoneOffset SYSTEM_OFFSET;

  private static final String CONTENT_INFO;
  private static final String CONTENT_HELP;
  private static final String CONTENT_COLOR;

  private static final String DEFAULT_CONFIG;

  private static final String CONSOLE_PROMPT;

  static {

    //= ================================================================================================================
    //= 跳过语言设置

    // -D user.country=zh
    // -D user.language=CN
    if (System.getenv("FURRYBLACK_LOCALE_SKIP") == null) {
      System.err.println("Env FURRYBLACK_LOCALE_SKIP not set, Setting JVM local to Locale.SIMPLIFIED_CHINESE");
      Locale.setDefault(Locale.SIMPLIFIED_CHINESE);
    }

    //= ================================================================================================================
    //= 跳过时间设置

    // -D user.timezone=Asia/Shanghai
    if (System.getenv("FURRYBLACK_TIMEZONE_SKIP") == null) {
      System.err.println("Env FURRYBLACK_TIMEZONE_SKIP not set, Setting JVM timezone to Asia/Shanghai");
      TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
    }

    //= ================================================================================================================
    //= 系统信息

    LINE = System.lineSeparator();

    BOOT_TIME = ManagementFactory.getRuntimeMXBean().getStartTime();
    CPU_CORES = Runtime.getRuntime().availableProcessors();

    SYSTEM_ZONEID = ZoneId.systemDefault();
    SYSTEM_OFFSET = ZoneOffset.systemDefault().getRules().getOffset(LocalDateTime.now());

    //= ================================================================================================================
    //= 框架信息

    CONTENT_INFO =

      // @formatter:off

      YELLOW + "FurryBlackPlus Mirai - ver " + APP_VERSION + RESET + LINE + """
      A Mirai wrapper QQ-Bot framework make with love and \uD83E\uDDE6
      电子白熊会梦到仿生老黑吗
      By - Alceatraz Warprays @ BlackTechStudio
      项目地址 https://github.com/Alceatraz/FurryBlack-Mirai
      插件地址 https://github.com/Alceatraz/FurryBlack-Mirai-Extensions
      个人主页 https://www.blacktech.studio"""

     // @formatter:on

    ;

    CONTENT_HELP =

      // @formatter:off

      YELLOW + "FurryBlackPlus Mirai - ver " + APP_VERSION + RESET + LINE +
      BRIGHT_CYAN + "# FurryBlackPlus 启动参数 ===========================" + RESET + LINE +
      "--debug       使用DEBUG模式启动" + LINE +
      "--unsafe      允许一些危险的调用" + LINE +
      "--no-login    使用离线模式，仅用于基础调试，功能基本都不可用" + LINE +
      "--no-console  不使用控制台，唯一正常关闭方式是使用进程信号" + LINE +
      "--no-jline    不使用jline控制台，使用BufferedReader" + LINE +
      "--force-exit  关闭流程执行后，强制结束JVM(halt)" + LINE +

      BRIGHT_CYAN + "# FurryBlackPlus 系统参数 ===========================" + RESET + LINE +
      "furryblack.logger.level 日志等级" + LINE +

      BRIGHT_CYAN + "# FurryBlackPlus 控制台  ===========================" + RESET + LINE +
      RED + "⚠ 控制台任何操作都属于底层操作可以直接对框架进行不安全和非法的操作" + RESET + LINE +
      "安全：设计如此，不会导致异常或者不可预测的结果" + LINE +
      "风险：功能设计上是安全操作，但是具体被操作对象可能导致错误" + LINE +
      "危险：没有安全性检查的操作，可能会让功能严重异常导致被迫重启或损坏模块的数据存档" + LINE +
      "高危：后果完全未知的危险操作，或者正常流程中不应该如此操作但是控制台仍然可以强制执行" + LINE +

      GREEN + "# 系统管理 ==========================================" + RESET + LINE +
      "level (安全) 修改控制台日志打印等级，日志不受影响(可能导致漏掉ERR/WARN信息)" + LINE +
      "stat  (安全) 查看性能状态" + LINE +
      "stop  (安全) 正常退出，完整执行关闭流程，等待模块结束，等待线程池结束，等待所有线程" + LINE +
      "drop  (高危) 强制退出，不等待插件关闭完成，不等待线程池结束，且最终强制结束JVM(halt)" + LINE +
      "kill  (高危) 命令执行后直接强制结束JVM(halt)，不会进行任何关闭操作" + LINE +

      GREEN + "# 功能管理 ==========================================" + RESET + LINE +
      "enable  (安全) 启用消息事件处理 正常响应消息" + LINE +
      "disable (安全) 停用消息事件处理 无视任何消息" + LINE +

      GREEN + "# 好友相关 ==========================================" + RESET + LINE +
      "list users   (安全) 列出好友" + LINE +
      "list group   (安全) 列出群组" + LINE +
      "list <group> (安全) 列出成员" + LINE +

      GREEN + "# 昵称相关 ==========================================" + RESET + LINE +
      "nickname list (安全) 列出昵称" + LINE +
      "nickname clean (安全) 清空昵称" + LINE +
      "nickname append (安全) 加载且合并昵称" + LINE +
      "nickname reload (安全) 清空且加载昵称" + LINE +

      GREEN + "# 发送消息 ==========================================" + RESET + LINE +
      "send users <users> <消息>  (安全) 向好友发送消息" + LINE +
      "send group <group> <消息>  (安全) 向群聊发送消息" + LINE +
      "send <group> <user> <消息> (安全) 向群聊发送AT消息" + LINE +

      GREEN + "# 模型管理 ==========================================" + RESET + LINE +
      "schema (安全) 详细显示插件和模块" + LINE +

      GREEN + "# 插件管理 ==========================================" + RESET + LINE +
      "plugin (安全) 列出插件" + LINE +

      GREEN + "# 模块管理 ==========================================" + RESET + LINE +
      "module (安全) 列出模块" + LINE +

      GREEN + "※ Runner可能会被依赖，底层操作框架不检查依赖，有可能导致关联模块崩溃" + RESET + LINE +
      "module unload <名称> (风险) 卸载指定模块(执行 shut + 从处理链中移除)" + LINE +
      "module reboot <名称> (风险) 重启指定模块(执行 shut + init + boot)" + LINE +
      "module shut   <名称> (风险) 关闭指定模块(执行 shut)" + LINE +
      "module init   <名称> (风险) 预载指定模块(执行 init)" + LINE +
      "module boot   <名称> (风险) 启动指定模块(执行 boot)" + LINE +

      GREEN + "# 调试功能 ==========================================" + RESET + LINE +
      "debug [enable|disable] (风险) DEBUG开关，打印DEBUG输出和控制某些功能，插件如果不遵守标准开发可能会导致崩溃"

      // @formatter:on

    ;

    CONTENT_COLOR =

      // @formatter:off

      RED            + "RED -------------- THE QUICK BROWN FOX JUMP OVER A LAZY DOG | the quick brown fox jump over a lazy dog" + RESET + LINE +
      GREEN          + "GREEN ------------ THE QUICK BROWN FOX JUMP OVER A LAZY DOG | the quick brown fox jump over a lazy dog" + RESET + LINE +
      YELLOW         + "YELLOW ----------- THE QUICK BROWN FOX JUMP OVER A LAZY DOG | the quick brown fox jump over a lazy dog" + RESET + LINE +
      BLUE           + "BLUE ------------- THE QUICK BROWN FOX JUMP OVER A LAZY DOG | the quick brown fox jump over a lazy dog" + RESET + LINE +
      MAGENTA        + "MAGENTA ---------- THE QUICK BROWN FOX JUMP OVER A LAZY DOG | the quick brown fox jump over a lazy dog" + RESET + LINE +
      CYAN           + "CYAN ------------- THE QUICK BROWN FOX JUMP OVER A LAZY DOG | the quick brown fox jump over a lazy dog" + RESET + LINE +
      BRIGHT_RED     + "BRIGHT_RED ------- THE QUICK BROWN FOX JUMP OVER A LAZY DOG | the quick brown fox jump over a lazy dog" + RESET + LINE +
      BRIGHT_GREEN   + "BRIGHT_GREEN ----- THE QUICK BROWN FOX JUMP OVER A LAZY DOG | the quick brown fox jump over a lazy dog" + RESET + LINE +
      BRIGHT_YELLOW  + "BRIGHT_YELLOW ---- THE QUICK BROWN FOX JUMP OVER A LAZY DOG | the quick brown fox jump over a lazy dog" + RESET + LINE +
      BRIGHT_BLUE    + "BRIGHT_BLUE ------ THE QUICK BROWN FOX JUMP OVER A LAZY DOG | the quick brown fox jump over a lazy dog" + RESET + LINE +
      BRIGHT_MAGENTA + "BRIGHT_MAGENTA --- THE QUICK BROWN FOX JUMP OVER A LAZY DOG | the quick brown fox jump over a lazy dog" + RESET + LINE +
      BRIGHT_CYAN    + "BRIGHT_CYAN ------ THE QUICK BROWN FOX JUMP OVER A LAZY DOG | the quick brown fox jump over a lazy dog" + RESET + LINE +
      WHITE          + "WHITE ------------ THE QUICK BROWN FOX JUMP OVER A LAZY DOG | the quick brown fox jump over a lazy dog" + RESET + LINE +
      GRAY           + "GRAY ------------- THE QUICK BROWN FOX JUMP OVER A LAZY DOG | the quick brown fox jump over a lazy dog" + RESET + LINE +
      BRIGHT_BLACK   + "BRIGHT_BLACK ----- THE QUICK BROWN FOX JUMP OVER A LAZY DOG | the quick brown fox jump over a lazy dog" + RESET + LINE +
      BRIGHT_WHITE   + "BRIGHT_WHITE ----- THE QUICK BROWN FOX JUMP OVER A LAZY DOG | the quick brown fox jump over a lazy dog" + RESET

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

      .replaceAll("CONF_ACCOUNT_AUTH",     String.join(".", CONF_ACCOUNT_AUTH))
      .replaceAll("CONF_ACCOUNT_USERNAME", String.join(".", CONF_ACCOUNT_USERNAME))
      .replaceAll("CONF_ACCOUNT_PASSWORD", String.join(".", CONF_ACCOUNT_PASSWORD))
      .replaceAll("CONF_DEVICE_TYPE",      String.join(".", CONF_DEVICE_TYPE))
      .replaceAll("CONF_DEVICE_INFO",      String.join(".", CONF_DEVICE_INFO))
      .replaceAll("CONF_COMMAND_REGEX",    String.join(".", CONF_COMMAND_REGEX))
      .replaceAll("CONF_THREADS_MONITOR",  String.join(".", CONF_THREADS_MONITOR))
      .replaceAll("CONF_THREADS_SCHEDULE", String.join(".", CONF_THREADS_SCHEDULE))

      // @formatter:on

    ;

    CONSOLE_PROMPT = "[console]$ ";

  }

  //= ==================================================================================================================
  //
  //  框架常量
  //
  //= ==================================================================================================================

  private static final Latch LATCH = new Latch();

  //= ==================================================================================================================
  //
  //  框架变量
  //
  //= ==================================================================================================================

  private static String NAMESPACE; // 命名空间

  private static volatile boolean EVENT_ENABLE;

  private static volatile boolean SHUTDOWN_HALT;
  private static volatile boolean SHUTDOWN_DROP;

  private static volatile LoggerX.Level LEVEL;

  private static FurryBlackKernelConfig kernelConfig;
  private static FurryBlackSystemConfig systemConfig;

  private static LoggerX logger;
  private static Terminal terminal;

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

  public static void main(String[] args) throws InterruptedException {

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

    System.out.println("[FurryBlack][BOOT]FurryBlackMirai - " + APP_VERSION + " " + TimeTool.datetime(BOOT_TIME));

    //= ================================================================================================================
    //=
    //= 内核系统
    //=
    //= ================================================================================================================

    FurryBlackArgument argument = FurryBlackArgument.parse(args);

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

    System.out.println("[FurryBlack][ARGS] 选项 -> " + argument.options.size());

    for (String it : argument.options) {
      System.out.println("[FurryBlack][ARGS]    " + it);
    }

    System.out.println("[FurryBlack][ARGS] 参数 -> " + argument.parameters.size());

    for (Map.Entry<String, String> entry : argument.parameters.entrySet()) {
      String k = entry.getKey();
      String v = entry.getValue();
      System.out.println("[FurryBlack][ARGS]    " + k + "=" + v);
    }

    //= ========================================================================
    //= 内核参数

    kernelConfig = FurryBlackKernelConfig.getInstance(argument);

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

    if (kernelConfig.upgrade) {
      System.out.println("[FurryBlack][ARGS]协议补丁 - 启用升级");
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

      LEVEL = LoggerX.Level.getByName(kernelConfig.level);

      if (LEVEL == null) {
        System.out.println("[FurryBlack][ARGS]日志级别 - 输入值无效 -> " + kernelConfig.level + ", 可用日志级别为:");
        System.out.println("[FurryBlack][ARGS] - MUTE");
        System.out.println("[FurryBlack][ARGS] - FATAL");
        System.out.println("[FurryBlack][ARGS] - ERROR");
        System.out.println("[FurryBlack][ARGS] - WARN");
        System.out.println("[FurryBlack][ARGS] - HINT");
        System.out.println("[FurryBlack][ARGS] - SEEK");
        System.out.println("[FurryBlack][ARGS] - INFO");
        System.out.println("[FurryBlack][ARGS] - DEBUG");
        System.out.println("[FurryBlack][ARGS] - VERBOSE");
        System.out.println("[FurryBlack][ARGS] - DEVELOP");
        System.out.println("[FurryBlack][ARGS] - EVERYTHING");
        throw new CoreException("[FurryBlack][FATAL] Logger level invalid -> " + kernelConfig.level);
      } else {
        System.out.println("[FurryBlack][ARGS]日志级别 - " + LEVEL.name());
      }

    }

    //= ========================================================================
    //= 日志后端

    if (kernelConfig.provider != null) {
      boolean result = LoggerXFactory.setDefault(kernelConfig.provider);
      if (result) {
        System.out.println("[FurryBlack][ARGS]日志后端 - " + LoggerXFactory.getDefault());
      } else {
        System.out.println("[FurryBlack][ARGS]日志后端 - 指定后端尚未注册 -> " + kernelConfig.provider + ", 已注册日志后端为:");
        LoggerXFactory.getProviders().forEach((k, v) -> System.out.println("[FurryBlack][ARGS] - " + k + "/" + v));
        System.out.println("[FurryBlack][ARGS]日志后端 - 尝试加载日志后端 -> " + kernelConfig.provider);
        Class<?> clazz;
        Class<? extends LoggerX> loggerClazz;
        try {
          clazz = Class.forName(kernelConfig.provider);
        } catch (Exception exception) {
          throw new CoreException("[FurryBlack][ARGS]日志后端 - 尝试加载日志后端失败, 加载类失败 -> " + kernelConfig.provider, exception);
        }
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
        String registerProvider = LoggerXFactory.registerProvider(loggerClazz);
        CoreException.check("尝试加载日志后端失败, 注册日志后端失败 -> ", registerProvider);
        LoggerXFactory.setDefault(loggerClazz);
        System.out.println("[FurryBlack][ARGS]日志后端 - 加载日志后端成功 -> " + LoggerXFactory.getDefault());
      }
    }

    System.out.println("[FurryBlack][INIT]内核配置初始化完成");

    //= ================================================================================================================
    //= 终端子系统
    //= ================================================================================================================

    if (kernelConfig.noConsole) {
      terminal = new NoConsoleTerminal();
    } else {
      if (kernelConfig.noJline) {
        terminal = new StdinTerminal();
      } else {
        terminal = new JlineTerminal();
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
    FurryBlack.println("[FurryBlack][INIT]日志级别 " + LoggerX.getLevel().name());

    //= ========================================================================
    //= 初始化日志

    if (LoggerXFactory.needLoggerFile()) {

      String name = TimeTool.format("yyyy-MM-dd HH-mm-ss", BOOT_TIME) + ".txt";
      Path loggerFile = FileEnhance.get(FOLDER_LOGGER, name);
      CoreException.check("日志文件初始化失败 -> ", FileEnhance.ensureFileSafe(loggerFile));

      try {
        LoggerXFactory.initLoggerFile(loggerFile.toFile());
      } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException exception) {
        throw new CoreException("日志后端初始化失败 -> " + loggerFile, exception);
      }

      FurryBlack.println("[FurryBlack][INIT]日志文件 " + name);
    }

    logger = LoggerXFactory.newLogger(FurryBlack.class);

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
    logger.info("内核配置/日志级别 " + LoggerX.getLevel().name());

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

    if (kernelConfig.upgrade) {
      logger.info("内核配置/协议补丁 - 启用升级");
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
          logger.warning("丢弃无效配置 " + k + "=" + v);
          continue;
        }
        if (v == null || k.toString().isBlank()) {
          logger.warning("丢弃无效配置 " + k + "=" + v);
          continue;
        }
        argument.properties.put(k, v);
      }

    } else {
      logger.info("跳过配置文件");
    }

    //= ========================================================================
    //= 检查配置项目

    logger.info("检查配置项目");

    try {

      systemConfig = FurryBlackSystemConfig.getInstance(argument);

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

      String SHA256_EULA = DigestTool.sha256(MESSAGE_EULA);
      String SHA256_INFO = DigestTool.sha256(MESSAGE_INFO);
      String SHA256_HELP = DigestTool.sha256(MESSAGE_INFO);

      logger.info("SHA-256 EULA -> " + SHA256_EULA);
      logger.info("SHA-256 INFO -> " + SHA256_INFO);
      logger.info("SHA-256 HELP -> " + SHA256_HELP);

      MESSAGE_EULA = MESSAGE_EULA + "\r\nSHA-256: " + SHA256_EULA;
      MESSAGE_INFO = MESSAGE_INFO + "\r\nSHA-256: " + SHA256_INFO;
      MESSAGE_HELP = MESSAGE_HELP + "\r\nSHA-256: " + SHA256_HELP;

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

    logger.hint("初始化机器人");

    //= ========================================================================
    //= 升级客户端协议

    if (kernelConfig.upgrade) {

      logger.info("升级客户端协议");

      Class<?> clazz;
      try {
        clazz = Class.forName("xyz.cssxsh.mirai.tool.FixProtocolVersion");
      } catch (ClassNotFoundException exception) {
        throw new CoreException("[UPGRADE/PROTOCOL] Load class failure", exception);
      }

      Method methodUpdate;
      try {
        methodUpdate = clazz.getMethod("update");
      } catch (NoSuchMethodException exception) {
        throw new CoreException("[UPGRADE/PROTOCOL] Load method failure", exception);
      }

      try {
        methodUpdate.invoke(null);
      } catch (IllegalAccessException | InvocationTargetException exception) {
        throw new CoreException("[UPGRADE/PROTOCOL] Invoke method failure", exception);
      }

      Method methodInfo;
      try {
        methodInfo = clazz.getMethod("info");
      } catch (NoSuchMethodException exception) {
        throw new CoreException("[UPGRADE/PROTOCOL] Load method failure", exception);
      }

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
          info.forEach((k, v) -> logger.info(v + " -> " + k.name()));
        }
      } catch (Exception exception) {
        throw new CoreException("[UPGRADE/PROTOCOL] Invoke method failure", exception);
      }
    }

    //= ========================================================================
    //= 加载客户端配置

    logger.info("加载客户端配置");

    BotConfiguration botConfiguration = new BotConfiguration();

    botConfiguration.setLoginCacheEnabled(true);
    botConfiguration.enableContactCache();
    botConfiguration.setCacheDir(FileEnhance.get(FOLDER_CONFIG, "cache").toFile());
    botConfiguration.setProtocol(systemConfig.deviceType.toMiraiProtocol());
    botConfiguration.loadDeviceInfoJson(systemConfig.deviceInfo);
    botConfiguration.setBotLoggerSupplier(i -> LoggerXFactory.newLogger("MiraiBot"));
    botConfiguration.setNetworkLoggerSupplier(i -> LoggerXFactory.newLogger("MiraiNet"));

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

    Listener<UserMessageEvent> userMessageEventListener = GlobalEventChannel.INSTANCE.subscribeAlways(UserMessageEvent.class, FurryBlack::handleUsersMessage);
    Listener<GroupMessageEvent> groupMessageEventListener = GlobalEventChannel.INSTANCE.subscribeAlways(GroupMessageEvent.class, FurryBlack::handleGroupMessage);

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
      logger.warning("指定了--no-login参数 跳过登录");
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
      FurryBlack.println("[FurryBlack][EXIT]FurryBlackPlus normally closed, Bye.");
      if (isShutdownHalt()) {
        FurryBlack.println("[FurryBlack][EXIT]FurryBlackPlus normally close with halt, Execute halt now.");
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
    //= 启动完成
    //= ================================================================================================================

    logger.hint("系统启动完成 耗时" + TimeTool.duration(System.currentTimeMillis() - BOOT_TIME));

    //= ========================================================================
    //= 启动完成 修改日志界别到设定值

    if (!isDebug() && LEVEL != null) {
      LoggerX.setLevel(LEVEL);
    }

    //= ========================================================================
    //= 启动终端输入功能

    Thread consoleThread = new Thread(FurryBlack::console);
    consoleThread.setName("furryblack-terminal");
    consoleThread.setDaemon(true);
    consoleThread.start();

    terminal.updateCompleter();

    //= ========================================================================
    //= 启动事件响应

    EVENT_ENABLE = true;

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

    //= ========================================================================
    //= 开始关闭 修改日志界别到设定值

    LoggerX.setLevel(LoggerX.Level.VERBOSE);

    //= ========================================================================
    //= 特殊关闭模式

    if (isShutModeDrop()) {
      System.out.println("[FurryBlack][DROP]Shutdown mode drop, Invoke JVM halt now, Hope nothing broken.");
      Runtime.getRuntime().halt(1);
    }

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
      if (FurryBlack.isShutModeDrop()) {
        logger.warning("丢弃监听任务线程池");
        MONITOR_PROCESS.shutdownNow();
      } else {
        logger.info("关闭监听任务线程池");
        MONITOR_PROCESS.shutdown();
        try {
          boolean termination = MONITOR_PROCESS.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
          if (!termination) logger.warning("监听任务线程池关闭超时");
        } catch (InterruptedException exception) {
          logger.error("等待关闭监听任务线程池被中断", exception);
        }
        logger.info("监听任务线程池关闭");
      }
    });

    CompletableFuture<Void> scheduleShutdown = CompletableFuture.runAsync(() -> {
      if (FurryBlack.isShutModeDrop()) {
        logger.warning("丢弃定时任务线程池");
        SCHEDULE_SERVICE.shutdownNow();
      } else {
        logger.info("关闭定时任务线程池");
        SCHEDULE_SERVICE.shutdown();
        try {
          boolean termination = SCHEDULE_SERVICE.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
          if (!termination) logger.warning("定时任务线程池关闭超时");
        } catch (InterruptedException exception) {
          logger.error("等待关闭定时任务线程池被中断", exception);
        }
        logger.info("定时任务线程池关闭");
      }
    });

    try {
      CompletableFuture.allOf(monitorShutdown, scheduleShutdown).get();
    } catch (ExecutionException exception) {
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
      logger.warning("调试模式 不需要关闭机器人");
    } else {
      if (FurryBlack.isShutModeDrop()) {
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
  //=  监听器
  //=
  //= ==================================================================================================================

  //= ==========================================================================
  //= 用户消息

  private static void handleUsersMessage(UserMessageEvent event) {

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

          case "list" -> FurryBlack.sendMessage(event, MESSAGE_LIST_USERS);
          case "info" -> FurryBlack.sendMessage(event, MESSAGE_INFO);
          case "eula" -> FurryBlack.sendMessage(event, MESSAGE_EULA);

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
      logger.warning("处理私聊消息异常", exception);
    }
  }

  //= ==========================================================================
  //= 群组消息

  public static void handleGroupMessage(GroupMessageEvent event) {

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
              if (checker.handleGroupMessageWrapper(event, command)) return;
            }
            List<EventHandlerChecker> commandCheckerGroupPool = schema.getCommandCheckerGroupPool(commandName);
            if (commandCheckerGroupPool != null) {
              for (EventHandlerChecker checker : commandCheckerGroupPool) {
                if (checker.handleGroupMessageWrapper(event, command)) return;
              }
            }
            executor.handleGroupMessageWrapper(event, command);
          }
        }
      }

    } catch (Exception exception) {
      logger.warning("处理群聊消息异常", exception);
    }

  }

  //= ==================================================================================================================
  //
  //
  //  昵称系统
  //
  //
  //= ==================================================================================================================

  private static class Nickname {

    private static final LoggerX logger = LoggerXFactory.newLogger(Nickname.class);

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
          logger.warning("配置无效 " + line);
          continue;
        } if (indexOfColon < 0) {
          logger.warning("配置无效 " + line);
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
  //
  //
  //  终端系统
  //
  //
  //= ==================================================================================================================

  //= ==========================================================================
  //= 控制台终端

  private abstract static sealed class Terminal permits JlineTerminal, NoConsoleTerminal, StdinTerminal {

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

  private static final class NoConsoleTerminal extends FurryBlack.Terminal {

    private final OutputStreamWriter writer;

    public NoConsoleTerminal() {
      writer = new OutputStreamWriter(System.out, StandardCharsets.UTF_8);
    }

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
  //= StdinTerminal

  private static final class StdinTerminal extends FurryBlack.Terminal {

    private final BufferedReader reader;
    private final OutputStreamWriter writer;

    public StdinTerminal() {
      InputStreamReader inputStreamReader = new InputStreamReader(System.in, StandardCharsets.UTF_8);
      reader = new BufferedReader(inputStreamReader);
      writer = new OutputStreamWriter(System.out, StandardCharsets.UTF_8);
    }

    @Override
    protected String readLineImpl() {
      printImpl(FurryBlack.CONSOLE_PROMPT);
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

  private static final class JlineTerminal extends FurryBlack.Terminal {

    private final LineReader reader;
    private final CompleterDelegate completerDelegate;

    public JlineTerminal() {
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
      return reader.readLine(FurryBlack.CONSOLE_PROMPT);
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

    public static class CompleterDelegate implements Completer {

      private Completer completer;

      public CompleterDelegate() {
        completer = buildCompleter();
      }

      @Override
      public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
        completer.complete(reader, line, candidates);
      }

      public void update() {
        completer = buildCompleter("", "");
      }

      private AggregateCompleter buildCompleter(String... modules) {
        return new AggregateCompleter(
          new ArgumentCompleter(new StringsCompleter("help", "kill", "drop", "stop", "gc", "stack", "enable", "disable", "schema", "color")),
          new ArgumentCompleter(new StringsCompleter("list", "send"), new StringsCompleter("users", "group")),
          new TreeCompleter(node("level", node("MUTE", "FATAL", "ERROR", "WARN", "HINT", "SEEK", "INFO", "DEBUG", "VERBOSE", "DEVELOP", "EVERYTHING"))),
          new TreeCompleter(node("nickname", node("list", "clean", "reload", "append", "export"))),
          new TreeCompleter(node("debug", node("enable", "disable"))),
          new TreeCompleter(node("plugin")),
          new TreeCompleter(node("module", node("initModule", "bootModule", "shut", "reboot", "unload", node(new StringsCompleter(modules)))))
        );
      }

    }

  }

  //= ==================================================================================================================
  //
  //
  //  控制台系统
  //
  //
  //= ==================================================================================================================

  private static void console() {

    console:
    while (true) {

      try {

        String temp = terminal.readLine();
        if (temp == null || temp.isBlank()) continue;

        Command command = new Command(temp.trim());

        switch (command.getCommandName()) {

          //= ==========================================================================================================

          case "?":
          case "help":
            FurryBlack.println(CONTENT_HELP);
            break;

          //= ==========================================================================================================

          case "info":
            FurryBlack.println(CONTENT_INFO);
            break;

          //= ==========================================================================================================

          case "halt":
            if (command.getParameterLength() == 1) {
              SHUTDOWN_HALT = Boolean.parseBoolean(command.getParameterSegment(0));
              if (SHUTDOWN_HALT) {
                FurryBlack.println("启动强制退出");
              } else {
                FurryBlack.println("关闭强制退出");
              }
            } else {
              FurryBlack.println("Usage: halt enable/disable");
            }
            break;

          //= ==========================================================================================================

          case "drop":
            SHUTDOWN_DROP = true;

          case "stop":
          case "quit":
          case "exit":
            Runtime.getRuntime().exit(0);
            break console;

          //= ==========================================================================================================

          case "kill":
            System.out.println("[FurryBlack][KILL]Invoke JVM halt now, Good luck.");
            Runtime.getRuntime().halt(1);
            break console;

          //= ==========================================================================================================

          case "enable":
            EVENT_ENABLE = true;
            FurryBlack.println("启动事件响应");
            break;

          //= ==========================================================================================================

          case "disable":
            EVENT_ENABLE = false;
            FurryBlack.println("关闭事件响应");
            break;

          //= ==========================================================================================================

          case "gc":
          case "stat":
          case "stats":
          case "status":
            long totalMemory = Runtime.getRuntime().totalMemory();
            long freeMemory = Runtime.getRuntime().freeMemory();
            long maxMemory = Runtime.getRuntime().maxMemory();
            long useMemory = totalMemory - freeMemory;

            String totalMemoryH = toHumanBytes(totalMemory);
            String freeMemoryH = toHumanBytes(freeMemory);
            String maxMemoryH = toHumanBytes(maxMemory);
            String useMemoryH = toHumanBytes(useMemory);

            // @formatter:off

            FurryBlack.println(

              "命名空间: " + NAMESPACE + LINE +
              "调试开关: " + (kernelConfig.debug ? "调试模式" : "生产模式") + LINE +
              "安全策略: " + (kernelConfig.unsafe ? "宽松策略" : "标准策略") + LINE +
              "协议补丁: " + (kernelConfig.upgrade ? "启用升级" : "原生模式") + LINE +
              "终端模式: " + (kernelConfig.noJline ? "精简终端" : "完整终端") + LINE +
              "登录模式: " + (kernelConfig.noLogin ? "跳过登录" : "真实登录") + LINE +
              "关闭策略: " + (SHUTDOWN_HALT ? "强制退出" : "正常退出") + LINE +
              "消息事件: " + (EVENT_ENABLE ? "正常监听" : "忽略消息") + LINE +
              "核心数量: " + Runtime.getRuntime().availableProcessors() + LINE +
              "最大内存: " + maxMemoryH + "/" + maxMemory + LINE +
              "已用内存: " + useMemoryH + "/" + useMemory + LINE +
              "空闲内存: " + freeMemoryH + "/" + freeMemory + LINE +
              "分配内存: " + totalMemoryH + "/" + totalMemory + LINE +
              "运行时间: " + TimeTool.duration(System.currentTimeMillis() - BOOT_TIME)

            );

            // @formatter:on

            break;

          //= ==========================================================================================================

          case "stack":

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
              builder.append(" (").append(k.getName());
              builder.append(") ").append(k.getPriority());
              builder.append(" [").append(k.getThreadGroup().getName());
              builder.append("]");
              builder.append(LINE);
              for (StackTraceElement element : v) {
                String format = String.format("    %s:%s(%s)", element.getClassName(), element.getMethodName(), element.getLineNumber());
                builder.append(format).append(LINE);
              }
              FurryBlack.println(builder);

            }
            break;

          //= ==========================================================================================================

          case "debug":
            if (command.getParameterLength() == 1) {
              switch (command.getParameterSegment(0)) {
                case "enable" -> {
                  kernelConfig.debug = true;
                  FurryBlack.println("DEBUG模式启动");
                }
                case "disable" -> {
                  kernelConfig.debug = false;
                  FurryBlack.println("DEBUG模式关闭");
                }
              }
            } else {
              FurryBlack.println(kernelConfig.debug ? "DEBUG已开启" : "DEBUG已关闭");
            }
            break;

          //= ==========================================================================================================

          case "color":
            FurryBlack.println(CONTENT_COLOR);
            break;

          //= ==========================================================================================================

          case "level":
            if (command.hasCommandBody()) {
              String level = command.getParameterSegment(0);
              if (LoggerX.setLevel(level)) {
                logger.bypass("日志级别调整为 " + level);
              } else {
                logger.bypass("修改日志级别失败：不存在此级别，可用值为 MUTE FATAL ERROR WARN HINT SEEK INFO DEBUG VERBOSE DEVELOP EVERYTHING");
              }
            } else {
              logger.bypass("可用值为 MUTE ERROR WARN HINT SEEK INFO DEBUG VERBOSE EVERYTHING");
              logger.fatal("The quick brown fox jump over a lazy dog");
              logger.error("The quick brown fox jump over a lazy dog");
              logger.warning("The quick brown fox jump over a lazy dog");
              logger.hint("The quick brown fox jump over a lazy dog");
              logger.seek("The quick brown fox jump over a lazy dog");
              logger.info("The quick brown fox jump over a lazy dog");
              logger.debug("The quick brown fox jump over a lazy dog");
              logger.verbose("The quick brown fox jump over a lazy dog");
              logger.develop("The quick brown fox jump over a lazy dog");
            }
            break;

          //= ==========================================================================================================

          case "schema":
            FurryBlack.println(schema.verboseStatus());
            break;

          //= ==========================================================================================================

          case "plugin":

            for (Map.Entry<String, Plugin> pluginEntry : schema.getAllPlugin()) {

              var pluginName = pluginEntry.getKey();
              var pluginItem = pluginEntry.getValue();

              FurryBlack.println(BRIGHT_CYAN + pluginName + " " + pluginItem.getModules().size() + RESET);

              Map<Runner, Class<? extends EventHandlerRunner>> runnerClassMap = pluginItem.getRunnerClassMap();
              FurryBlack.println(GREEN + ">> 定时器 " + runnerClassMap.size() + RESET);
              for (Map.Entry<Runner, Class<? extends EventHandlerRunner>> classEntry : runnerClassMap.entrySet()) {
                var moduleName = classEntry.getKey();
                var moduleItem = classEntry.getValue();
                FurryBlack.println(moduleName.value() + '[' + moduleName.priority() + "] -> " + moduleItem.getName());
              }

              Map<Filter, Class<? extends EventHandlerFilter>> filterClassMap = pluginItem.getFilterClassMap();
              FurryBlack.println(GREEN + ">> 过滤器 " + filterClassMap.size() + RESET);
              for (Map.Entry<Filter, Class<? extends EventHandlerFilter>> classEntry : filterClassMap.entrySet()) {
                var moduleName = classEntry.getKey();
                var moduleItem = classEntry.getValue();
                FurryBlack.println(moduleName.value() + '[' + moduleName.priority() + "] -> " + moduleItem.getName());
              }

              Map<Monitor, Class<? extends EventHandlerMonitor>> monitorClassMap = pluginItem.getMonitorClassMap();
              FurryBlack.println(GREEN + ">> 监听器 " + monitorClassMap.size() + RESET);
              for (Map.Entry<Monitor, Class<? extends EventHandlerMonitor>> classEntry : monitorClassMap.entrySet()) {
                var moduleName = classEntry.getKey();
                var moduleItem = classEntry.getValue();
                FurryBlack.println(moduleName.value() + '[' + moduleName.priority() + "] -> " + moduleItem.getName());
              }

              Map<Checker, Class<? extends EventHandlerChecker>> checkerClassMap = pluginItem.getCheckerClassMap();
              FurryBlack.println(GREEN + ">> 检查器 " + checkerClassMap.size() + RESET);
              for (Map.Entry<Checker, Class<? extends EventHandlerChecker>> classEntry : checkerClassMap.entrySet()) {
                var moduleName = classEntry.getKey();
                var moduleItem = classEntry.getValue();
                FurryBlack.println(moduleName.value() + '[' + moduleName.priority() + "](" + moduleName.command() + ") -> " + moduleItem.getName());
              }

              Map<Executor, Class<? extends EventHandlerExecutor>> executorClassMap = pluginItem.getExecutorClassMap();
              FurryBlack.println(GREEN + ">> 执行器 " + executorClassMap.size() + RESET);
              for (Map.Entry<Executor, Class<? extends EventHandlerExecutor>> classEntry : executorClassMap.entrySet()) {
                var moduleName = classEntry.getKey();
                var moduleItem = classEntry.getValue();
                FurryBlack.println(moduleName.value() + '(' + moduleName.command() + ") -> " + moduleItem.getName());
              }
            }

            MESSAGE_LIST_USERS = schema.generateUsersExecutorList();
            MESSAGE_LIST_GROUP = schema.generateGroupExecutorList();

            break;

          //= ==================================================================================================================

          case "module":

            switch (command.getParameterLength()) {

              case 2 -> {

                switch (command.getParameterSegment(0)) {
                  // module shut <plugin>
                  case "shut" -> schema.shutModule(command.getParameterSegment(1));

                  // module initModule <plugin>
                  case "initModule" -> schema.initModule(command.getParameterSegment(1));

                  // module bootModule <plugin>
                  case "bootModule" -> schema.bootModule(command.getParameterSegment(1));

                  // module reboot <plugin>
                  case "reboot" -> schema.rebootModule(command.getParameterSegment(1));

                  // module unload <plugin>
                  case "unload" -> {
                    schema.unloadModule(command.getParameterSegment(1));
                    terminal.updateCompleter();
                  }

                }
              }

              case 0 -> {

                LineBuilder builder = new LineBuilder();

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

              }
            }

            MESSAGE_LIST_USERS = schema.generateUsersExecutorList();
            MESSAGE_LIST_GROUP = schema.generateGroupExecutorList();

            break;

          //= ==================================================================================================================

          case "nickname":

            if (!command.hasCommandBody()) break;

            switch (command.getParameterSegment(0)) {

              case "list" -> {
                FurryBlack.println(BRIGHT_CYAN + "全局昵称" + RESET);
                for (Map.Entry<Long, String> entry : nickname.getNicknameGlobal().entrySet()) {
                  FurryBlack.println(entry.getKey() + ":" + entry.getValue());
                }
                FurryBlack.println(BRIGHT_CYAN + "群内昵称" + RESET);
                for (Map.Entry<Long, Map<Long, String>> groupsEntry : nickname.getNicknameGroups().entrySet()) {
                  FurryBlack.println("> " + groupsEntry.getKey());
                  for (Map.Entry<Long, String> nicknameEntry : groupsEntry.getValue().entrySet()) {
                    FurryBlack.println(nicknameEntry.getKey() + ":" + nicknameEntry.getValue());
                  }
                }
              }

              case "clean" -> {
                nickname.cleanNickname();
                FurryBlack.println("昵称已清空");
              }

              case "append" -> {
                nickname.appendNickname();
                FurryBlack.println("昵称已续加");
              }

              case "reload" -> {
                nickname.cleanNickname();
                nickname.appendNickname();
                FurryBlack.println("昵称已重载");
              }

              case "export" -> {
                Path path = FileEnhance.get(FOLDER_CONFIG, "export-" + TimeTool.format("yyyy-MM-dd HH-mm-ss") + ".txt");
                LineBuilder builder = new LineBuilder();
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
                Files.writeString(path, builder.toString());
                FurryBlack.println("昵称已导出 -> " + path);
              }
            }
            break;

          //= ==================================================================================================================

          case "list":

            if (!command.hasCommandBody()) break;

            switch (command.getParameterSegment(0)) {

              case "u", "usr", "user", "users", "f", "fri", "friend", "friends" -> {
                List<Friend> friends = FurryBlack.getFriends().stream().filter(item -> item.getId() != getBotID()).toList();
                if (friends.size() == 0) {
                  FurryBlack.println("你没有朋友");
                  break;
                }
                friends.stream()
                  .map(FurryBlack::getFormattedNickName)
                  .forEach(FurryBlack::println);
              }

              case "g", "grp", "group", "groups" -> {
                ContactList<Group> groups = FurryBlack.getGroups();
                if (groups.size() == 0) {
                  FurryBlack.println("你没有群组");
                  break;
                }
                groups.stream()
                  .map(item -> item.getName() + "(" + item.getId() + ") " + item.getMembers().size() + "人")
                  .forEach(FurryBlack::println);
              }

              default -> {
                long group;
                try {
                  group = Long.parseLong(command.getParameterSegment(0));
                } catch (Exception exception) {
                  FurryBlack.println("命令发生异常 省略group需要指定群号");
                  break;
                }
                FurryBlack.getGroup(group).getMembers().stream()
                  .sorted((_$1, _$2) -> _$2.getPermission().getLevel() - _$1.getPermission().getLevel())
                  .forEach(item -> {
                    StringBuilder builder = new StringBuilder();
                    builder.append(item.getNameCard());
                    builder.append(" - ");
                    builder.append(FurryBlack.getFormattedNickName(item));
                    switch (item.getPermission().getLevel()) {
                      case 2 -> builder.append(" 群主");
                      case 1 -> builder.append(" 管理");
                    }
                    FurryBlack.println(builder);
                  });
              }
            }
            break;

          //= ==================================================================================================================

          case "send":

            if (!command.hasCommandBody()) break;

            switch (command.getParameterSegment(0)) {

              case "u":
              case "usr":
              case "user":
              case "users":
              case "f":
              case "fri":
              case "friend":
              case "friends":
                long user = Long.parseLong(command.getParameterSegment(1));
                FurryBlack.sendUserMessage(user, command.join(2));
                break;

              case "g":
              case "grp":
              case "group":
              case "groups":
                long group = Long.parseLong(command.getParameterSegment(1));
                FurryBlack.sendGroupMessage(group, command.join(2));
                break;

              default:
                group = Long.parseLong(command.getParameterSegment(0));
                user = Long.parseLong(command.getParameterSegment(1));
                FurryBlack.sendAtMessage(group, user, command.join(2));
            }
            break;

        }

      } catch (UserInterruptException exception) {
        return;
      } catch (Exception exception) {
        logger.error("命令导致了异常", exception);
      }
    }

  }

  //= ==================================================================================================================
  //
  //
  //  公共API
  //
  //
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
  public static ScheduledFuture<?> schedule(Runnable runnable, long time, TimeUnit timeUnit) {
    return SCHEDULE_SERVICE.schedule(runnable, time, timeUnit);
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

  @Comment("提交明天开始的等间隔定时任务")
  public static ScheduledFuture<?> scheduleAtNextDayFixedRate(Runnable runnable, long period, TimeUnit unit) {
    return SCHEDULE_SERVICE.scheduleAtFixedRate(runnable, TimeTool.timeToTomorrow(), period, unit);
  }

  @Comment("提交明天开始的等延迟定时任务")
  public static ScheduledFuture<?> scheduleWithNextDayFixedDelay(Runnable runnable, long delay, TimeUnit unit) {
    return SCHEDULE_SERVICE.scheduleWithFixedDelay(runnable, TimeTool.timeToTomorrow(), delay, unit);
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

  @Comment("获取Mirai机器人实例 只有--unsafe模式下可以使用")
  public static Bot getBot() {
    if (kernelConfig.unsafe) {
      return bot;
    } else {
      logger.warning("获取机器人实例禁止 只有在unsafe模式下可用");
      for (StackTraceElement stackTraceElement : Thread.currentThread().getStackTrace()) {
        System.out.println(stackTraceElement);
      }
      throw new CoreException("Get Mirai-BOT instance only allowed when --unsafe present!");
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

  //= ==================================================================================================================
  //=
  //=
  //= 配置管理
  //=
  //=
  //= ==================================================================================================================

  //= ==================================================================================================================
  //= 名称转换

  /**
   * a,b,c -> a-b-c for args --a-b-c xxx
   */
  public static String toArgumentName(String... name) {
    String join = String.join("-", name);
    if (NAMESPACE == null) return join;
    return NAMESPACE + "-" + join;
  }

  /**
   * a,b,c -> a.b.c for system property -Da.b.c=xxx
   */
  public static String toPropertyName(String... name) {
    String join = String.join(".", name);
    if (NAMESPACE == null) return join;
    return NAMESPACE + "." + join;
  }

  /**
   * a,b,c -> A_B_C for envs export A_B_C=xxx
   */
  public static String toEnvironmentName(String... name) {
    String join = String.join("_", name);
    if (NAMESPACE == null) return join;
    return (NAMESPACE + "_" + join).toUpperCase();
  }

  /**
   * a,b,c -> a.b.c for property a.b.c=xxx no namespace
   */
  public static String toConfigName(String... name) {
    return String.join(".", name);
  }

  //= ==================================================================================================================
  //= 配置存储

  private static class FurryBlackArgument {

    private final Properties properties;
    private final LinkedList<String> options;
    private final LinkedHashMap<String, String> parameters;

    //= ========================================================================

    private static FurryBlackArgument parse(String[] arguments) {

      FurryBlackArgument instance = new FurryBlackArgument();
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

    private FurryBlackArgument() {
      options = new LinkedList<>();
      parameters = new LinkedHashMap<>();
      properties = new Properties();
    }

    //= ========================================================================

    @Comment(value = "查询内核选项", attention = {
      "环境变量 > 系统属性 > 程序参数",
    })
    private boolean checkKernelOption(String... name) {
      if (System.getenv(toEnvironmentName(name)) != null) return true;
      if (System.getProperty(toPropertyName(name)) != null) return true;
      return options.contains(toArgumentName(name));
    }

    @Comment(value = "查询内核参数", attention = {
      "环境变量 > 系统属性 > 程序参数",
    })
    private String getKernelParameter(String... name) {
      String value = System.getenv(toEnvironmentName(name));
      if (value != null) return value;
      value = System.getProperty(toPropertyName(name));
      if (value != null) return value;
      value = parameters.get(toArgumentName(name));
      if (value != null) return value;
      return null;
    }

    //= ========================================================================

    @Comment(value = "查询框架选项", attention = {
      "环境变量 > 系统属性 > 程序参数 > 配置文件",
    })
    private boolean checkSystemOption(String... name) {
      if (System.getenv(toEnvironmentName(name)) != null) return true;
      if (System.getProperty(toPropertyName(name)) != null) return true;
      if (options.contains(toArgumentName(name))) return true;
      return properties.getProperty(toConfigName(name)) != null;
    }

    @Comment(value = "查询框架参数", attention = {
      "环境变量 > 系统属性 > 程序参数 > 配置文件",
    })
    private String getSystemParameter(String... name) {
      String value = System.getenv(toEnvironmentName(name));
      if (value != null) return value;
      value = System.getProperty(toPropertyName(name));
      if (value != null) return value;
      value = parameters.get(toArgumentName(name));
      if (value != null) return value;
      value = properties.getProperty(toConfigName(name));
      if (value != null) return value;
      return null;
    }

    @Comment(value = "查询框架选项", attention = {
      "环境变量 > 程序参数 > 配置文件",
      "不读取系统配置,避免有人把密码写在命令行,导致谁都能看",
    })
    private boolean checkSystemOptionSafe(String... name) {
      if (System.getenv(toEnvironmentName(name)) != null) return true;
      if (options.contains(toArgumentName(name))) return true;
      return properties.getProperty(toConfigName(name)) != null;
    }

    @Comment(value = "查询框架参数", attention = {
      "环境变量 > 程序参数 > 配置文件",
      "不读取系统配置,避免有人把密码写在命令行,导致谁都能看",
    })
    private String getSystemParameterSafe(String... name) {
      String value = System.getenv(toEnvironmentName(name));
      if (value != null) return value;
      value = parameters.get(toArgumentName(name));
      if (value != null) return value;
      value = properties.getProperty(toConfigName(name));
      if (value != null) return value;
      return null;
    }

  }

  //= ==================================================================================================================
  //= 内核配置

  private static class FurryBlackKernelConfig {

    volatile boolean debug;
    volatile boolean unsafe;
    boolean upgrade;
    boolean noLogin;
    boolean noJline;
    boolean noConsole;
    boolean forceExit;

    String level;
    private String provider;

    private static FurryBlackKernelConfig getInstance(FurryBlackArgument argument) {

      FurryBlackKernelConfig config = new FurryBlackKernelConfig();

      config.debug = argument.checkKernelOption(ARGS_DEBUG);
      config.unsafe = argument.checkKernelOption(ARGS_UNSAFE);
      config.upgrade = argument.checkKernelOption(ARGS_UPGRADE);
      config.noLogin = argument.checkKernelOption(ARGS_NO_LOGIN);
      config.noJline = argument.checkKernelOption(ARGS_NO_JLINE);
      config.noConsole = argument.checkKernelOption(ARGS_NO_CONSOLE);
      config.forceExit = argument.checkKernelOption(ARGS_FORCE_EXIT);

      config.level = argument.getKernelParameter(ARGS_LOGGER_LEVEL);
      config.provider = argument.getKernelParameter(ARGS_LOGGER_PROVIDER);

      return config;
    }

    private FurryBlackKernelConfig() {

    }
  }

  //= ==================================================================================================================
  //= 框架配置

  private static class FurryBlackSystemConfig {

    private static final LoggerX logger = LoggerXFactory.newLogger(FurryBlackSystemConfig.class);

    private AuthMode authMod;
    private long username;
    private String password;
    private DeviceType deviceType;
    private String deviceInfo;
    private Pattern commandRegex;
    private Integer monitorThreads;
    private Integer scheduleThreads;

    public static FurryBlackSystemConfig getInstance(FurryBlackArgument argument) {

      FurryBlackSystemConfig config = new FurryBlackSystemConfig();

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
          logger.warning("！！！！！！！！！！！！！！！！");
          logger.warning("调试模式开启时会在日志中记录密码");
          logger.warning("！！！！！！！！！！！！！！！！");
          logger.seek("登录密码 -> " + password);
          logger.warning("！！！！！！！！！！！！！！！！");
          logger.warning("调试模式开启时会在日志中记录密码");
          logger.warning("！！！！！！！！！！！！！！！！");
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

      return config;
    }

    private FurryBlackSystemConfig() {

    }

    //= ========================================================================

    private enum AuthMode {

      PASSWD,
      QRCODE,
      ;

      private static AuthMode of(String value) {
        return switch (value.toLowerCase()) {
          case "code", "qrcode", "scancode" -> QRCODE;
          case "pass", "passwd", "password" -> PASSWD;
          default -> throw new InvalidConfigException("ERROR: No such AuthMode -> " + value);
        };
      }
    }

    //= ========================================================================

    private enum DeviceType {

      PAD,
      PHONE,
      WATCH,
      IPAD,
      MACOS,
      ;

      private static DeviceType of(String value) {
        return switch (value.toLowerCase()) {
          case "pad", "android_pad" -> PAD;
          case "phone", "android_phone" -> PHONE;
          case "watch", "android_watch" -> WATCH;
          case "ipad" -> IPAD;
          case "macos" -> MACOS;
          default -> throw new InvalidConfigException("ERROR: No such DeviceType -> " + value);
        };
      }

      private BotConfiguration.MiraiProtocol toMiraiProtocol() {
        return switch (this) {
          case PAD -> BotConfiguration.MiraiProtocol.ANDROID_PAD;
          case PHONE -> BotConfiguration.MiraiProtocol.ANDROID_PHONE;
          case WATCH -> BotConfiguration.MiraiProtocol.ANDROID_WATCH;
          case IPAD -> BotConfiguration.MiraiProtocol.IPAD;
          case MACOS -> BotConfiguration.MiraiProtocol.MACOS;
        };
      }
    }

  }

  //= ==================================================================================================================
  //=
  //=
  //= 辅助功能
  //=
  //=
  //= ==================================================================================================================

}