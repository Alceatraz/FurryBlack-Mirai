/*
 * Copyright (C) 2021 Alceatraz @ BlackTechStudio
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the BTS Anti-Commercial & GNU Affero General.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * BTS Anti-Commercial & GNU Affero General Public License for more details.
 *
 * You should have received a copy of the BTS Anti-Commercial & GNU Affero
 * General Public License along with this program in README or LICENSE.
 */


package studio.blacktech.furryblackplus;


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
import net.mamoe.mirai.event.events.GroupMessageEvent;
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
import org.jline.reader.UserInterruptException;
import org.jline.reader.impl.completer.AggregateCompleter;
import org.jline.reader.impl.completer.ArgumentCompleter;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.widget.AutopairWidgets;
import studio.blacktech.furryblackplus.common.Api;
import studio.blacktech.furryblackplus.core.Systemd;
import studio.blacktech.furryblackplus.core.common.exception.BotException;
import studio.blacktech.furryblackplus.core.common.exception.console.ConsoleException;
import studio.blacktech.furryblackplus.core.common.exception.moduels.boot.BootException;
import studio.blacktech.furryblackplus.core.common.logger.LoggerXFactory;
import studio.blacktech.furryblackplus.core.common.logger.base.LoggerX;
import studio.blacktech.furryblackplus.core.common.logger.base.LoggerX.Color;
import studio.blacktech.furryblackplus.core.common.logger.support.WriteLogger;
import studio.blacktech.furryblackplus.core.common.time.TimeTool;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static org.jline.builtins.Completers.TreeCompleter.node;


// 🔫 🧦 ❌ ✔️ ⭕ 🚧 🀄


/**
 * FurryBlack Plus Framework - based on Mirai
 * !!!本项目并非使用纯AGPLv3协议，请认真阅读LICENSE!!!
 *
 *
 * 电子白熊会梦到仿生老黑吗
 *
 *
 * 项目地址 https://github.com/Alceatraz/FurryBlack-Mirai
 * 插件地址 https://github.com/Alceatraz/FurryBlack-Mirai-Extensions
 *
 * 个人主页 https://www.blacktech.studio
 *
 * @author Alceatraz Warprays @ BlackTechStudio
 */


@SuppressWarnings("unused")
@Api(
    value = "FurryBlack Plus Framework - based on Mirai",
    usage = {
        "电子白熊会梦到仿生老黑吗",
        "项目地址 https://github.com/Alceatraz/FurryBlack-Mirai",
        "插件地址 https://github.com/Alceatraz/FurryBlack-Mirai-Extensions",
        "个人主页 https://www.blacktech.studio",
        "@author Alceatraz Warprays @ BlackTechStudio",
        "@Api注解可以让你即使阅读反编译后的代码也能看到注释"
    },
    attention = {
        "!!!本项目并非使用纯AGPLv3协议，请认真阅读LICENSE!!!"
    }
)
public final class FurryBlack {


    // =================================================================================================================
    //
    // 版本信息
    //
    // =================================================================================================================


    public static final String APP_VERSION = "2.1.11";


    // =================================================================================================================
    //
    // 公共常量
    //
    // =================================================================================================================


    @Api("系统启动时间") private static final long BOOT_TIME;

    @Api("原始系统时区") public static final ZoneId SYSTEM_ZONEID;
    @Api("原始系统偏差") public static final ZoneOffset SYSTEM_OFFSET;

    @Api("系统换行符") public static final String LINE;


    // =================================================================================================================
    //
    // 私有常量
    //
    // =================================================================================================================

    private static final String CONTENT_INFO;
    private static final String CONTENT_HELP;

    private static final String CONTENT_COLOR;

    private static final String CONSOLE_PROMPT;


    // =================================================================================================================
    //
    // 常量语句块
    //
    // =================================================================================================================


    static {

        BOOT_TIME = System.currentTimeMillis();

        SYSTEM_ZONEID = ZoneId.systemDefault();
        SYSTEM_OFFSET = ZoneOffset.systemDefault().getRules().getOffset(LocalDateTime.now());

        // =====================================================================

        System.setProperty("mirai.no-desktop", "");

        Locale.setDefault(Locale.SIMPLIFIED_CHINESE);
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));

        Thread.currentThread().setName("furryblack-main");

        // =====================================================================

        CONSOLE_PROMPT = "[console]$ ";

        LINE = System.lineSeparator();

        CONTENT_COLOR =

            // @formatter:off

            Color.RED +            "RED ------------ THE QUICK BROWN FOX JUMP OVER A LAZY DOG - the quick brown fox jump over a lazy dog" + Color.RESET + LINE +
            Color.GREEN +          "GREEN ---------- THE QUICK BROWN FOX JUMP OVER A LAZY DOG - the quick brown fox jump over a lazy dog" + Color.RESET + LINE +
            Color.YELLOW +         "YELLOW --------- THE QUICK BROWN FOX JUMP OVER A LAZY DOG - the quick brown fox jump over a lazy dog" + Color.RESET + LINE +
            Color.BLUE +           "BLUE ----------- THE QUICK BROWN FOX JUMP OVER A LAZY DOG - the quick brown fox jump over a lazy dog" + Color.RESET + LINE +
            Color.MAGENTA +        "MAGENTA -------- THE QUICK BROWN FOX JUMP OVER A LAZY DOG - the quick brown fox jump over a lazy dog" + Color.RESET + LINE +
            Color.CYAN +           "CYAN ----------- THE QUICK BROWN FOX JUMP OVER A LAZY DOG - the quick brown fox jump over a lazy dog" + Color.RESET + LINE +
            Color.BRIGHT_RED +     "BRIGHT_RED ----- THE QUICK BROWN FOX JUMP OVER A LAZY DOG - the quick brown fox jump over a lazy dog" + Color.RESET + LINE +
            Color.BRIGHT_GREEN +   "BRIGHT_GREEN --- THE QUICK BROWN FOX JUMP OVER A LAZY DOG - the quick brown fox jump over a lazy dog" + Color.RESET + LINE +
            Color.BRIGHT_YELLOW +  "BRIGHT_YELLOW -- THE QUICK BROWN FOX JUMP OVER A LAZY DOG - the quick brown fox jump over a lazy dog" + Color.RESET + LINE +
            Color.BRIGHT_BLUE +    "BRIGHT_BLUE ---- THE QUICK BROWN FOX JUMP OVER A LAZY DOG - the quick brown fox jump over a lazy dog" + Color.RESET + LINE +
            Color.BRIGHT_MAGENTA + "BRIGHT_MAGENTA - THE QUICK BROWN FOX JUMP OVER A LAZY DOG - the quick brown fox jump over a lazy dog" + Color.RESET + LINE +
            Color.BRIGHT_CYAN +    "BRIGHT_CYAN ---- THE QUICK BROWN FOX JUMP OVER A LAZY DOG - the quick brown fox jump over a lazy dog" + Color.RESET + LINE +
            Color.WHITE +          "WHITE ---------- THE QUICK BROWN FOX JUMP OVER A LAZY DOG - the quick brown fox jump over a lazy dog" + Color.RESET + LINE +
            Color.GRAY +           "GRAY ----------- THE QUICK BROWN FOX JUMP OVER A LAZY DOG - the quick brown fox jump over a lazy dog" + Color.RESET + LINE +
            Color.BRIGHT_BLACK +   "BRIGHT_BLACK --- THE QUICK BROWN FOX JUMP OVER A LAZY DOG - the quick brown fox jump over a lazy dog" + Color.RESET + LINE +
            Color.BRIGHT_WHITE +   "BRIGHT_WHITE --- THE QUICK BROWN FOX JUMP OVER A LAZY DOG - the quick brown fox jump over a lazy dog" + Color.RESET

            // @formatter:on

        ;

        CONTENT_INFO =

            // @formatter:off

            Color.YELLOW + "FurryBlackPlus Mirai - ver " + APP_VERSION + Color.RESET + LINE +
            """
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

            Color.YELLOW + "FurryBlackPlus Mirai - ver " + APP_VERSION + Color.RESET + LINE +
            Color.BRIGHT_CYAN + "# FurryBlackPlus 启动参数 ===========================" + Color.RESET + LINE +
            "--debug       使用DEBUG模式启动" + LINE +
            "--unsafe      允许一些危险的调用" + LINE +
            "--no-login    使用离线模式，仅用于基础调试，功能基本都不可用" + LINE +
            "--no-console  不使用控制台，唯一正常关闭方式是使用进程信号" + LINE +
            "--no-jline    不使用jline控制台，使用BufferedReader" + LINE +
            "--force-exit  关闭流程执行后，强制结束JVM(halt)" + LINE +

            Color.BRIGHT_CYAN + "# FurryBlackPlus 系统参数 ===========================" + Color.RESET + LINE +
            "furryblack.logger.level 日志等级" + LINE +

            Color.BRIGHT_CYAN + "# FurryBlackPlus 控制台  ===========================" + Color.RESET + LINE +
            Color.RED + "⚠ 控制台任何操作都属于底层操作可以直接对框架进行不安全和非法的操作" + Color.RESET + LINE +
            "安全：设计如此，不会导致异常或者不可预测的结果" + LINE +
            "风险：功能设计上是安全操作，但是具体被操作对象可能导致错误" + LINE +
            "危险：没有安全性检查的操作，可能会让功能严重异常导致被迫重启或损坏模块的数据存档" + LINE +
            "高危：后果完全未知的危险操作，或者正常流程中不应该如此操作但是控制台仍然可以强制执行" + LINE +

            Color.GREEN + "# 系统管理 ==========================================" + Color.RESET + LINE +
            "level (安全) 修改控制台日志打印等级，日志不受影响(可能导致漏掉ERR/WARN信息)" + LINE +
            "stat  (安全) 查看性能状态" + LINE +
            "stop  (安全) 正常退出，完整执行关闭流程，等待模块结束，等待线程池结束，等待所有线程" + LINE +
            "drop  (高危) 强制退出，不等待插件关闭完成，不等待线程池结束，且最终强制结束JVM(halt)" + LINE +
            "kill  (高危) 命令执行后直接强制结束JVM(halt)，不会进行任何关闭操作" + LINE +

            Color.GREEN + "# 功能管理 ==========================================" + Color.RESET + LINE +
            "enable  (安全) 启用消息事件处理 正常响应消息" + LINE +
            "disable (安全) 停用消息事件处理 无视任何消息" + LINE +

            Color.GREEN + "# 好友相关 ==========================================" + Color.RESET + LINE +
            "list users   (安全) 列出好友" + LINE +
            "list group   (安全) 列出群组" + LINE +
            "list <group> (安全) 列出成员" + LINE +

            Color.GREEN + "# 昵称相关 ==========================================" + Color.RESET + LINE +
            "nickname list (安全) 列出昵称" + LINE +
            "nickname clean (安全) 清空昵称" + LINE +
            "nickname append (安全) 加载且合并昵称" + LINE +
            "nickname reload (安全) 清空且加载昵称" + LINE +

            Color.GREEN + "# 发送消息 ==========================================" + Color.RESET + LINE +
            "send users <users> <消息>  (安全) 向好友发送消息" + LINE +
            "send group <group> <消息>  (安全) 向群聊发送消息" + LINE +
            "send <group> <user> <消息> (安全) 向群聊发送AT消息" + LINE +

            Color.GREEN + "# 模型管理 ==========================================" + Color.RESET + LINE +
            "schema (安全) 详细显示插件和模块" + LINE +

            Color.GREEN + "# 插件管理 ==========================================" + Color.RESET + LINE +
            "plugin (安全) 列出插件" + LINE +

            Color.GREEN + "# 模块管理 ==========================================" + Color.RESET + LINE +
            "module (安全) 列出模块" + LINE +

            Color.GREEN + "※ Runner可能会被依赖，底层操作框架不检查依赖，有可能导致关联模块崩溃" + Color.RESET + LINE +
            "module unload <名称> (风险) 卸载指定模块(执行 shut + 从处理链中移除)" + LINE +
            "module reboot <名称> (风险) 重启指定模块(执行 shut + init + boot)" + LINE +
            "module shut   <名称> (风险) 关闭指定模块(执行 shut)" + LINE +
            "module init   <名称> (风险) 预载指定模块(执行 init)" + LINE +
            "module boot   <名称> (风险) 启动指定模块(执行 boot)" + LINE +

            Color.GREEN + "# 调试功能 ==========================================" + Color.RESET + LINE +
            "debug [enable|disable] (风险) DEBUG开关，打印DEBUG输出和控制某些功能，插件如果不遵守标准开发可能会导致崩溃"

        // @formatter:on

        ;

    }


    // =================================================================================================================
    //
    // 私有变量
    //
    // =================================================================================================================


    private static LoggerX logger;

    private static Systemd systemd;

    private static volatile boolean debug;

    private static volatile boolean enable;

    private static boolean unsafe;

    private static boolean noLogin;
    private static boolean noJline;

    private static boolean shutModeHalt;
    private static boolean shutModeDrop;

    private static Terminal terminal;

    private static File FOLDER_ROOT;
    private static File FOLDER_CONFIG;
    private static File FOLDER_PLUGIN;
    private static File FOLDER_DEPEND;
    private static File FOLDER_MODULE;
    private static File FOLDER_LOGGER;


    // =================================================================================================================
    //
    // 启动入口
    //
    // =================================================================================================================


    public static void main(String[] args) {

        System.out.println("[FurryBlack][MAIN]FurryBlackPlus Mirai - ver " + APP_VERSION + " " + TimeTool.datetime(BOOT_TIME));

        // =====================================================================
        // 初始化命令行参数

        List<String> parameters = Arrays.asList(args);

        boolean drtRun = false;

        // =====================================================================
        // color 模式
        if (parameters.contains("--color")) {
            System.out.println(CONTENT_COLOR);
            System.out.println();
            drtRun = true;
        }

        // =====================================================================
        // info 模式
        if (parameters.contains("--info")) {
            System.out.println(CONTENT_INFO);
            System.out.println();
            drtRun = true;
        }

        // =====================================================================
        // help 模式
        if (parameters.contains("--help")) {
            System.out.println(CONTENT_HELP);
            System.out.println();
            drtRun = true;
        }

        if (drtRun) return;

        // =====================================================================
        // debug 模式
        debug = parameters.contains("--debug");
        if (debug) {
            System.out.println("[FurryBlack][ARGS]调试模式");
        } else {
            System.out.println("[FurryBlack][ARGS]生产模式");
        }

        // =====================================================================
        // unsafe 模式
        unsafe = parameters.contains("--unsafe");
        if (unsafe) {
            System.out.println("[FurryBlack][ARGS]宽松模式");
        } else {
            System.out.println("[FurryBlack][ARGS]严格模式");
        }

        // =====================================================================
        // 控制台设置
        boolean noConsole = parameters.contains("--no-console");

        if (noConsole) {
            System.out.println("[FurryBlack][ARGS]关闭控制台");
        } else {

            // =====================================================================
            // jLine 设置
            noJline = parameters.contains("--no-jline");
            if (noJline) {
                System.out.println("[FurryBlack][ARGS]精简控制台");
            } else {
                System.out.println("[FurryBlack][ARGS]完整控制台");
            }

        }

        // =====================================================================
        // Dry Run 模式
        noLogin = parameters.contains("--no-login");
        if (noLogin) {
            System.out.println("[FurryBlack][ARGS]模拟运行模式");
        } else {
            System.out.println("[FurryBlack][ARGS]真实运行模式");
        }

        // =====================================================================
        // 退出模式 设置
        shutModeHalt = parameters.contains("--force-exit");
        if (shutModeHalt) {
            System.out.println("[FurryBlack][ARGS]使用强制退出");
        } else {
            System.out.println("[FurryBlack][ARGS]使用正常退出");
        }

        // =====================================================================
        // 日志级别 设置
        String level = System.getProperty("furryblack.logger.level");
        if (level != null) {
            if (LoggerX.setLevel(level)) {
                System.out.println("[FurryBlack][PROP]不存在此目标日志级别" + level + ", 可用值为 MUTE FATAL ERROR WARN HINT SEEK INFO DEBUG VERBOSE DEVELOP");
            } else {
                System.out.println("[FurryBlack][PROP]目标日志级别" + level);
            }
        }


        // =====================================================================
        // 日志模式 设置

        String provider = System.getProperty("furryblack.logger.provider");
        if (provider != null) {
            LoggerXFactory.setDefault(provider);
        }

        System.out.println("[FurryBlack][ARGS]使用日志模块 " + LoggerXFactory.getDefault());


        // =====================================================================
        // 初始化目录


        String userDir = System.getProperty("user.dir");

        FOLDER_ROOT = Paths.get(userDir).toFile();

        FOLDER_CONFIG = Paths.get(userDir, "config").toFile();
        FOLDER_PLUGIN = Paths.get(userDir, "plugin").toFile();
        FOLDER_DEPEND = Paths.get(userDir, "depend").toFile();
        FOLDER_MODULE = Paths.get(userDir, "module").toFile();
        FOLDER_LOGGER = Paths.get(userDir, "logger").toFile();

        File loggerFile = Paths.get(FOLDER_LOGGER.getAbsolutePath(), TimeTool.format("yyyy_MM_dd_HH_mm_ss", BOOT_TIME) + ".txt").toFile();

        if (!FOLDER_CONFIG.exists() && !FOLDER_CONFIG.mkdirs()) throw new BootException("无法创建文件夹 " + FOLDER_CONFIG.getAbsolutePath());
        if (!FOLDER_PLUGIN.exists() && !FOLDER_PLUGIN.mkdirs()) throw new BootException("无法创建文件夹 " + FOLDER_PLUGIN.getAbsolutePath());
        if (!FOLDER_DEPEND.exists() && !FOLDER_DEPEND.mkdirs()) throw new BootException("无法创建文件夹 " + FOLDER_DEPEND.getAbsolutePath());
        if (!FOLDER_MODULE.exists() && !FOLDER_MODULE.mkdirs()) throw new BootException("无法创建文件夹 " + FOLDER_MODULE.getAbsolutePath());
        if (!FOLDER_LOGGER.exists() && !FOLDER_LOGGER.mkdirs()) throw new BootException("无法创建文件夹 " + FOLDER_LOGGER.getAbsolutePath());

        if (!FOLDER_CONFIG.isDirectory()) throw new BootException("文件夹被文件占位 " + FOLDER_CONFIG.getAbsolutePath());
        if (!FOLDER_PLUGIN.isDirectory()) throw new BootException("文件夹被文件占位 " + FOLDER_PLUGIN.getAbsolutePath());
        if (!FOLDER_DEPEND.isDirectory()) throw new BootException("文件夹被文件占位 " + FOLDER_DEPEND.getAbsolutePath());
        if (!FOLDER_MODULE.isDirectory()) throw new BootException("文件夹被文件占位 " + FOLDER_MODULE.getAbsolutePath());
        if (!FOLDER_LOGGER.isDirectory()) throw new BootException("文件夹被文件占位 " + FOLDER_LOGGER.getAbsolutePath());

        if (provider == null || "studio.blacktech.furryblackplus.core.common.logger.support.WriteLogger".equals(provider)) {
            try {
                if (!loggerFile.createNewFile()) throw new BootException("日志文件创建失败 " + loggerFile.getAbsolutePath());
            } catch (IOException exception) {
                throw new BootException("创建日志文件失败", exception);
            }
            if (!loggerFile.exists()) throw new BootException("日志文件不存在 " + loggerFile.getAbsolutePath());
            if (!loggerFile.canWrite()) throw new BootException("日志文件没有写权限 " + loggerFile.getAbsolutePath());
            WriteLogger.setFile(loggerFile);
        }

        System.out.println("[FurryBlack][INIT]日志系统初始化完成");

        logger = LoggerXFactory.newLogger(FurryBlack.class);

        // =====================================================================

        terminal = Terminal.getInstance();

        FurryBlack.terminalPrintLine("[FurryBlack][INIT]终端系统初始化完成");

        logger.info("应用工作目录 " + FOLDER_ROOT.getAbsolutePath());
        logger.info("插件扫描目录 " + FOLDER_PLUGIN.getAbsolutePath());
        logger.info("模块依赖目录 " + FOLDER_DEPEND.getAbsolutePath());
        logger.info("模块数据目录 " + FOLDER_MODULE.getAbsolutePath());
        logger.info("核心日志目录 " + FOLDER_LOGGER.getAbsolutePath());
        logger.info("当前日志文件 " + loggerFile.getAbsolutePath());

        systemd = new Systemd(FOLDER_CONFIG, FOLDER_PLUGIN);


        // =====================================================================

        try {
            systemd.boot();
        } catch (Exception exception) {
            throw new BootException("[FurryBlack][MAIN]FATAL -> Systemd bootModule failed.", exception);
        }

        // =====================================================================

        Thread mainThread = Thread.currentThread();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            systemd.signal();
            try {
                mainThread.join();
            } catch (InterruptedException exception) {
                System.out.println("[FurryBlack][EXIT]FATAL -> Shutdown hook interrupted, Shutdown process not finished.");
                exception.printStackTrace();
            }
            System.out.println("[FurryBlack][EXIT]FurryBlackPlus normally closed, Bye.");
            if (shutModeHalt) {
                System.out.println("[FurryBlack][EXIT]FurryBlackPlus normally close with halt, Execute halt now.");
                Runtime.getRuntime().halt(1);
            }
        }));

        // =====================================================================

        logger.hint("系统启动完成 耗时" + TimeTool.duration(System.currentTimeMillis() - BOOT_TIME));

        if (!debug) {
            LoggerX.setLevel(level);
        }

        // =====================================================================

        if (!noConsole) {
            Thread consoleThread = new Thread(FurryBlack::console);
            consoleThread.setName("furryblack-terminal");
            consoleThread.setDaemon(true);
            consoleThread.start();
            terminal.updateCompleter();
        }

        // =====================================================================

        enable = true;

        // =====================================================================

        systemd.await();

        // =====================================================================

        enable = false;

        LoggerX.setLevel(LoggerX.Level.VERBOSE);

        // =====================================================================

        try {
            systemd.shut();
        } catch (Exception exception) {
            throw new BootException("[FurryBlack][MAIN]FATAL -> Systemd shut failed.", exception);
        }

        if (shutModeDrop) {
            System.out.println("[FurryBlack][DROP]Shutdown mode drop, Invoke JVM halt now, Hope nothing broken.");
            Runtime.getRuntime().halt(1);
        }
    }


    // =================================================================================================================
    //
    // 控制台
    //
    // =================================================================================================================


    private static void console() {

        console:
        while (true) {
            try {

                String temp = terminal.readLine();
                if (temp == null || temp.isBlank()) continue;
                Command command = new Command(temp.trim());
                switch (command.getCommandName()) {

                    // =================================================================================================================

                    case "?":
                    case "help":
                        FurryBlack.terminalPrintLine(CONTENT_HELP);
                        break;

                    // =========================================================

                    case "info":
                        FurryBlack.terminalPrintLine(CONTENT_INFO);
                        break;

                    // =================================================================================================

                    case "halt":
                    case "kill":
                        System.out.println("[FurryBlack][KILL]Invoke JVM halt now, Good luck.");
                        Runtime.getRuntime().halt(1);
                        break console;

                    case "drop":
                        shutModeDrop = true;

                    case "stop":
                    case "quit":
                    case "exit":
                        Runtime.getRuntime().exit(0);
                        break console;

                    // =================================================================================================================

                    case "enable":
                        enable = true;
                        FurryBlack.terminalPrintLine("启动事件响应");
                        break;

                    // =========================================================

                    case "disable":
                        enable = false;
                        FurryBlack.terminalPrintLine("关闭事件响应");
                        break;

                    // =========================================================

                    case "gc":
                    case "stat":
                    case "stats":
                    case "status":
                        long totalMemory = Runtime.getRuntime().totalMemory() / 1024;
                        long freeMemory = Runtime.getRuntime().freeMemory() / 1024;
                        long maxMemory = Runtime.getRuntime().maxMemory() / 1024;
                        FurryBlack.terminalPrintLine(

                            // @formatter:off

                            "调试模式: " + (debug ? "启用" : "关闭") + LINE +
                            "严格模式: " + (unsafe ? "启用" : "关闭") + LINE +
                            "关闭模式: " + (shutModeHalt ? "强制" : "正常") + LINE +
                            "消息事件: " + (enable ? "启用" : "关闭") + LINE +
                            "运行时间: " + TimeTool.duration(System.currentTimeMillis() - BOOT_TIME) + LINE +
                            "内存占用: " + (totalMemory - freeMemory) + "KB/" + totalMemory + "KB/" + maxMemory + "KB(" + maxMemory / 1024 + "MB)"

                            // @formatter:on

                        );
                        break;

                    // =========================================================

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
                            builder.append(k.getId());
                            builder.append(" ");
                            builder.append(k.getState());
                            builder.append(" (");
                            builder.append(k.getName());
                            builder.append(") ");
                            builder.append(k.getPriority());
                            builder.append(" [");
                            builder.append(k.getThreadGroup().getName());
                            builder.append("]");
                            builder.append(LINE);
                            for (StackTraceElement element : v) {
                                builder.append("    ");
                                builder.append(element.getClassName());
                                builder.append(":");
                                builder.append(element.getMethodName());
                                builder.append("(");
                                builder.append(element.getLineNumber());
                                builder.append(")");
                                builder.append(LINE);
                            }
                            FurryBlack.terminalPrintLine(builder);

                        }
                        break;

                    // =================================================================================================

                    case "debug":
                        if (command.getParameterLength() == 1) {
                            switch (command.getParameterSegment(0)) {
                                case "enable" -> {
                                    debug = true;
                                    FurryBlack.terminalPrintLine("DEBUG模式启动");
                                }
                                case "disable" -> {
                                    debug = false;
                                    FurryBlack.terminalPrintLine("DEBUG模式关闭");
                                }
                            }
                        } else {
                            FurryBlack.terminalPrintLine(debug ? "DEBUG已开启" : "DEBUG已关闭");
                        }
                        break;

                    // =================================================================================================================

                    case "color":
                        FurryBlack.terminalPrintLine(CONTENT_COLOR);
                        break;

                    // =========================================================

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

                    // =================================================================================================================

                    case "schema":
                        FurryBlack.terminalPrintLine(systemd.schemaVerbose());
                        break;

                    // =========================================================

                    case "plugin":
                        for (Map.Entry<String, Plugin> pluginEntry : systemd.getAllPlugin()) {

                            var pluginName = pluginEntry.getKey();
                            var pluginItem = pluginEntry.getValue();

                            FurryBlack.terminalPrintLine(Color.BRIGHT_CYAN + pluginName + " " + pluginItem.getModules().size() + Color.RESET);

                            Map<Runner, Class<? extends EventHandlerRunner>> runnerClassMap = pluginItem.getRunnerClassMap();
                            FurryBlack.terminalPrintLine(Color.GREEN + ">> 定时器 " + runnerClassMap.size() + Color.RESET);
                            for (Map.Entry<Runner, Class<? extends EventHandlerRunner>> classEntry : runnerClassMap.entrySet()) {
                                var moduleName = classEntry.getKey();
                                var moduleItem = classEntry.getValue();
                                FurryBlack.terminalPrintLine(moduleName.value() + '[' + moduleName.priority() + "] -> " + moduleItem.getName());
                            }

                            Map<Filter, Class<? extends EventHandlerFilter>> filterClassMap = pluginItem.getFilterClassMap();
                            FurryBlack.terminalPrintLine(Color.GREEN + ">> 过滤器 " + filterClassMap.size() + Color.RESET);
                            for (Map.Entry<Filter, Class<? extends EventHandlerFilter>> classEntry : filterClassMap.entrySet()) {
                                var moduleName = classEntry.getKey();
                                var moduleItem = classEntry.getValue();
                                FurryBlack.terminalPrintLine(moduleName.value() + '[' + moduleName.priority() + "] -> " + moduleItem.getName());
                            }

                            Map<Monitor, Class<? extends EventHandlerMonitor>> monitorClassMap = pluginItem.getMonitorClassMap();
                            FurryBlack.terminalPrintLine(Color.GREEN + ">> 监听器 " + monitorClassMap.size() + Color.RESET);
                            for (Map.Entry<Monitor, Class<? extends EventHandlerMonitor>> classEntry : monitorClassMap.entrySet()) {
                                var moduleName = classEntry.getKey();
                                var moduleItem = classEntry.getValue();
                                FurryBlack.terminalPrintLine(moduleName.value() + '[' + moduleName.priority() + "] -> " + moduleItem.getName());
                            }

                            Map<Checker, Class<? extends EventHandlerChecker>> checkerClassMap = pluginItem.getCheckerClassMap();
                            FurryBlack.terminalPrintLine(Color.GREEN + ">> 检查器 " + checkerClassMap.size() + Color.RESET);
                            for (Map.Entry<Checker, Class<? extends EventHandlerChecker>> classEntry : checkerClassMap.entrySet()) {
                                var moduleName = classEntry.getKey();
                                var moduleItem = classEntry.getValue();
                                FurryBlack.terminalPrintLine(moduleName.value() + '[' + moduleName.priority() + "](" + moduleName.command() + ") -> " + moduleItem.getName());
                            }

                            Map<Executor, Class<? extends EventHandlerExecutor>> executorClassMap = pluginItem.getExecutorClassMap();
                            FurryBlack.terminalPrintLine(Color.GREEN + ">> 执行器 " + executorClassMap.size() + Color.RESET);
                            for (Map.Entry<Executor, Class<? extends EventHandlerExecutor>> classEntry : executorClassMap.entrySet()) {
                                var moduleName = classEntry.getKey();
                                var moduleItem = classEntry.getValue();
                                FurryBlack.terminalPrintLine(moduleName.value() + '(' + moduleName.command() + ") -> " + moduleItem.getName());
                            }
                        }
                        break;

                    // =========================================================

                    case "module":

                        switch (command.getParameterLength()) {

                            case 2:

                                switch (command.getParameterSegment(0)) {
                                    // module shut <plugin>
                                    case "shut" -> systemd.shutModule(command.getParameterSegment(1));

                                    // module initModule <plugin>
                                    case "initModule" -> systemd.initModule(command.getParameterSegment(1));

                                    // module bootModule <plugin>
                                    case "bootModule" -> systemd.bootModule(command.getParameterSegment(1));

                                    // module reboot <plugin>
                                    case "reboot" -> systemd.rebootModule(command.getParameterSegment(1));

                                    // module unload <plugin>
                                    case "unload" -> {
                                        systemd.unloadModule(command.getParameterSegment(1));
                                        terminal.updateCompleter();
                                    }

                                }
                                break;

                            case 0:
                                Map<Runner, Boolean> listAllRunner = systemd.listAllRunner();
                                FurryBlack.terminalPrintLine(Color.BRIGHT_CYAN + ">> 定时器 " + listAllRunner.size() + Color.RESET);
                                for (Map.Entry<Runner, Boolean> entry : listAllRunner.entrySet()) {
                                    FurryBlack.terminalPrintLine((entry.getValue() ? "开 " : "关 ") + entry.getKey().value());
                                }
                                Map<Filter, Boolean> listAllFilter = systemd.listAllFilter();
                                FurryBlack.terminalPrintLine(Color.BRIGHT_CYAN + ">> 过滤器 " + listAllFilter.size() + Color.RESET);
                                for (Map.Entry<Filter, Boolean> entry : listAllFilter.entrySet()) {
                                    FurryBlack.terminalPrintLine((entry.getValue() ? "开 " : "关 ") + entry.getKey().value() + "{" + (entry.getKey().users() ? "U" : "") + (entry.getKey().group() ? "G" : "") + "}");
                                }
                                Map<Monitor, Boolean> listAllMonitor = systemd.listAllMonitor();
                                FurryBlack.terminalPrintLine(Color.BRIGHT_CYAN + ">> 监听器 " + listAllMonitor.size() + Color.RESET);
                                for (Map.Entry<Monitor, Boolean> entry : listAllMonitor.entrySet()) {
                                    FurryBlack.terminalPrintLine((entry.getValue() ? "开 " : "关 ") + entry.getKey().value() + "{" + (entry.getKey().users() ? "U" : "") + (entry.getKey().group() ? "G" : "") + "}");
                                }
                                Map<Checker, Boolean> listAllChecker = systemd.listAllChecker();
                                FurryBlack.terminalPrintLine(Color.BRIGHT_CYAN + ">> 检查器 " + listAllChecker.size() + Color.RESET);
                                for (Map.Entry<Checker, Boolean> entry : listAllChecker.entrySet()) {
                                    FurryBlack.terminalPrintLine((entry.getValue() ? "开 " : "关 ") + entry.getKey().value() + "[" + entry.getKey().command() + "]" + "{" + (entry.getKey().users() ? "U" : "") + (entry.getKey().group() ? "G" : "") + "}");
                                }
                                Map<Executor, Boolean> listAllExecutor = systemd.listAllExecutor();
                                FurryBlack.terminalPrintLine(Color.BRIGHT_CYAN + ">> 执行器 " + listAllExecutor.size() + Color.RESET);
                                for (Map.Entry<Executor, Boolean> entry : listAllExecutor.entrySet()) {
                                    FurryBlack.terminalPrintLine((entry.getValue() ? "开 " : "关 ") + entry.getKey().value() + "[" + entry.getKey().command() + "]{" + (entry.getKey().users() ? "U" : "") + (entry.getKey().group() ? "G" : "") + "}");
                                }
                                List<Checker> globalUsersChecker = systemd.listGlobalUsersChecker();
                                FurryBlack.terminalPrintLine(Color.BRIGHT_CYAN + ">> 全局私聊检查器 " + globalUsersChecker.size() + Color.RESET);
                                for (Checker annotation : globalUsersChecker) {
                                    FurryBlack.terminalPrintLine(annotation.value());
                                }
                                List<Checker> globalGroupChecker = systemd.listGlobalGroupChecker();
                                FurryBlack.terminalPrintLine(Color.BRIGHT_CYAN + ">> 全局群聊检查器 " + globalGroupChecker.size() + Color.RESET);
                                for (Checker annotation : globalGroupChecker) {
                                    FurryBlack.terminalPrintLine("  " + annotation.value());
                                }
                                Map<String, List<Checker>> listCommandUsersChecker = systemd.listCommandUsersChecker();
                                FurryBlack.terminalPrintLine(Color.BRIGHT_CYAN + ">> 有限私聊检查器 " + listCommandUsersChecker.size() + Color.RESET);
                                for (Map.Entry<String, List<Checker>> entry : listCommandUsersChecker.entrySet()) {
                                    FurryBlack.terminalPrintLine(entry.getKey() + " " + entry.getValue().size());
                                    for (Checker item : entry.getValue()) {
                                        FurryBlack.terminalPrintLine("  " + item.value());
                                    }
                                }
                                Map<String, List<Checker>> listCommandGroupChecker = systemd.listCommandGroupChecker();
                                FurryBlack.terminalPrintLine(Color.BRIGHT_CYAN + ">> 有限群聊检查器 " + listCommandGroupChecker.size() + Color.RESET);
                                for (Map.Entry<String, List<Checker>> entry : listCommandGroupChecker.entrySet()) {
                                    FurryBlack.terminalPrintLine(entry.getKey() + " " + entry.getValue().size());
                                    for (Checker item : entry.getValue()) {
                                        FurryBlack.terminalPrintLine("  " + item.value());
                                    }
                                }
                                FurryBlack.terminalPrintLine(Color.BRIGHT_CYAN + ">> 私聊命令列表" + Color.RESET);
                                FurryBlack.terminalPrintLine(systemd.getMessageListUsers());
                                FurryBlack.terminalPrintLine(Color.BRIGHT_CYAN + ">> 群聊命令列表" + Color.RESET);
                                FurryBlack.terminalPrintLine(systemd.getMessageListGroup());
                                break;
                        }
                        break;

                    // =================================================================================================================

                    case "nickname":

                        if (!command.hasCommandBody()) break;

                        switch (command.getParameterSegment(0)) {

                            case "list" -> {
                                FurryBlack.terminalPrintLine(Color.BRIGHT_CYAN + "全局昵称" + Color.RESET);
                                for (Map.Entry<Long, String> entry : systemd.getNicknameGlobal().entrySet()) {
                                    FurryBlack.terminalPrintLine(entry.getKey() + ":" + entry.getValue());
                                }
                                FurryBlack.terminalPrintLine(Color.BRIGHT_CYAN + "群内昵称" + Color.RESET);
                                for (Map.Entry<Long, Map<Long, String>> groupsEntry : systemd.getNicknameGroups().entrySet()) {
                                    FurryBlack.terminalPrintLine("> " + groupsEntry.getKey());
                                    for (Map.Entry<Long, String> nicknameEntry : groupsEntry.getValue().entrySet()) {
                                        FurryBlack.terminalPrintLine(nicknameEntry.getKey() + ":" + nicknameEntry.getValue());
                                    }
                                }
                            }

                            case "clean" -> {
                                systemd.cleanNickname();
                                FurryBlack.terminalPrintLine("昵称已清空");
                            }

                            case "append" -> {
                                systemd.appendNickname();
                                FurryBlack.terminalPrintLine("昵称已续加");
                            }

                            case "reload" -> {
                                systemd.cleanNickname();
                                systemd.appendNickname();
                                FurryBlack.terminalPrintLine("昵称已重载");
                            }

                            case "export" -> {
                                File file = Paths.get(FOLDER_CONFIG.getAbsolutePath(), "export-nickname-" + TimeTool.format("yyyy-MM-dd HH-mm-ss") + ".txt").toFile();
                                //noinspection ResultOfMethodCallIgnored
                                file.createNewFile();
                                try (FileOutputStream fileOutputStream = new FileOutputStream(file);
                                     OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream)
                                ) {
                                    ContactList<Friend> friends = getFriends();
                                    outputStreamWriter.write("# 好友 " + friends.size() + LINE);
                                    for (Friend friend : friends) {
                                        outputStreamWriter.write("*." + friend.getId() + ":" + friend.getNick());
                                        outputStreamWriter.write(LINE);
                                    }
                                    ContactList<Group> groups = getGroups();
                                    outputStreamWriter.write("# 群组 " + groups.size() + LINE);
                                    for (Group group : groups) {
                                        outputStreamWriter.write(LINE);
                                        long groupId = group.getId();
                                        outputStreamWriter.write("# " + group.getName() + " " + group.getOwner().getId() + LINE);
                                        for (NormalMember member : group.getMembers()) {
                                            String nameCard = member.getNameCard();
                                            if (nameCard.isEmpty()) {
                                                outputStreamWriter.write(groupId + "." + member.getId() + ":" + member.getNick());
                                            } else {
                                                outputStreamWriter.write(groupId + "." + member.getId() + ":" + member.getNick() + "[" + nameCard + "]");
                                            }
                                            outputStreamWriter.write(LINE);
                                        }
                                    }
                                }
                                FurryBlack.terminalPrintLine("昵称已导出 -> " + file.getAbsolutePath());
                            }
                        }
                        break;

                    // =================================================================================================

                    case "list":

                        if (!command.hasCommandBody()) break;

                        switch (command.getParameterSegment(0)) {

                            case "u", "usr", "user", "users", "f", "fri", "friend", "friends" -> {
                                List<Friend> friends = FurryBlack.getFriends().stream().filter(item -> item.getId() != systemd.getBotID()).toList();
                                if (friends.size() == 0) {
                                    FurryBlack.terminalPrintLine("你没有朋友");
                                    break;
                                }
                                friends.stream()
                                       .map(FurryBlack::getFormattedNickName)
                                       .forEach(FurryBlack::terminalPrintLine);
                            }

                            case "g", "grp", "group", "groups" -> {
                                ContactList<Group> groups = FurryBlack.getGroups();
                                if (groups.size() == 0) {
                                    FurryBlack.terminalPrintLine("你没有群组");
                                    break;
                                }
                                groups.stream()
                                      .map(item -> item.getName() + "(" + item.getId() + ") " + item.getMembers().size() + "人")
                                      .forEach(FurryBlack::terminalPrintLine);
                            }

                            default -> {
                                long group;
                                try {
                                    group = Long.parseLong(command.getParameterSegment(0));
                                } catch (Exception exception) {
                                    FurryBlack.terminalPrintLine("命令发生异常 省略group需要指定群号");
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
                                              FurryBlack.terminalPrintLine(builder);
                                          });
                            }
                        }
                        break;

                    // =========================================================

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


    // =================================================================================================================


    private abstract static sealed class Terminal permits SimpleTerminal, JlineTerminal {

        public static Terminal getInstance() {
            if (noJline) {
                return new SimpleTerminal();
            } else {
                return new JlineTerminal();
            }
        }

        String readLine() {
            return this.readLineImpl();
        }

        void print(String message) {
            this.printImpl(message);
        }

        void printLine(String message) {
            this.printLineImpl(message);
        }

        void updateCompleter() {
            this.updateCompleterImpl();
        }

        protected abstract String readLineImpl();

        protected abstract void printImpl(String message);

        protected abstract void printLineImpl(String message);

        protected abstract void updateCompleterImpl();

    }


    // =========================================================================


    private static final class SimpleTerminal extends Terminal {

        private final BufferedReader bufferedReader;
        private final OutputStreamWriter outputStreamWriter;

        public SimpleTerminal() {
            InputStreamReader inputStreamReader = new InputStreamReader(System.in, StandardCharsets.UTF_8);
            this.bufferedReader = new BufferedReader(inputStreamReader);
            this.outputStreamWriter = new OutputStreamWriter(System.out, StandardCharsets.UTF_8);
        }

        @Override
        protected String readLineImpl() {
            this.printImpl(FurryBlack.CONSOLE_PROMPT);
            try {
                return this.bufferedReader.readLine();
            } catch (IOException exception) {
                throw new ConsoleException(exception);
            }
        }

        @Override
        protected synchronized void printImpl(String message) {
            try {
                this.outputStreamWriter.write(message);
                this.outputStreamWriter.flush();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }

        @Override
        protected void printLineImpl(String message) {
            this.printImpl(message + LINE);
        }

        @Override
        protected void updateCompleterImpl() {
            // Do nothing
        }
    }


    // =========================================================================


    private static final class JlineTerminal extends Terminal {

        private final LineReader jlineReader;
        private final CompleterDelegate completerDelegate;

        public JlineTerminal() {
            this.completerDelegate = new CompleterDelegate();
            this.jlineReader = LineReaderBuilder.builder().completer(this.completerDelegate).build();
            AutopairWidgets autopairWidgets = new AutopairWidgets(this.jlineReader);
            autopairWidgets.enable();
        }

        @Override
        protected String readLineImpl() {
            return this.jlineReader.readLine(FurryBlack.CONSOLE_PROMPT);
        }

        @Override
        protected synchronized void printImpl(String message) {
            this.jlineReader.printAbove(message);
        }

        @Override
        protected void printLineImpl(String message) {
            this.printImpl(message + LINE);
        }

        @Override
        protected void updateCompleterImpl() {
            this.completerDelegate.update();
        }

        public static class CompleterDelegate implements Completer {

            private Completer completer;

            public CompleterDelegate() {
                this.completer = new AggregateCompleter(
                    new ArgumentCompleter(new StringsCompleter("help", "kill", "drop", "stop", "stat", "enable", "disable", "schema", "color")),
                    new ArgumentCompleter(new StringsCompleter("list", "send"), new StringsCompleter("users", "group")),
                    new TreeCompleter(node("level", node("MUTE", "FATAL", "ERROR", "WARN", "HINT", "SEEK", "INFO", "DEBUG", "VERBOSE", "DEVELOP", "EVERYTHING"))),
                    new TreeCompleter(node("nickname", node("list", "clean", "reload", "append", "export"))),
                    new TreeCompleter(node("debug", node("enable", "disable"))),
                    new TreeCompleter(node("plugin")),
                    new TreeCompleter(node(
                        "module",
                        node("initModule", "bootModule", "shut", "reboot", "unload")
                    ))
                );
            }

            @Override
            public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
                this.completer.complete(reader, line, candidates);
            }

            public void update() {
                this.completer = new AggregateCompleter(
                    new ArgumentCompleter(new StringsCompleter("help", "kill", "drop", "stop", "stat", "enable", "disable", "schema", "color")),
                    new ArgumentCompleter(new StringsCompleter("list", "send"), new StringsCompleter("users", "group")),
                    new TreeCompleter(node("level", node("MUTE", "FATAL", "ERROR", "WARN", "HINT", "SEEK", "INFO", "DEBUG", "VERBOSE", "DEVELOP", "EVERYTHING"))),
                    new TreeCompleter(node("nickname", node("list", "clean", "reload", "append", "export"))),
                    new TreeCompleter(node("debug", node("enable", "disable"))),
                    new TreeCompleter(node("plugin")),
                    new TreeCompleter(node(
                        "module",
                        node("initModule", "bootModule", "shut", "reboot", "unload",
                             node(new StringsCompleter(systemd.listAllModule().keySet()))
                        )
                    ))
                );
            }
        }

    }


    // =================================================================================================================
    //
    // Runtime相关
    //
    // =================================================================================================================


    @Api("获取启动时间戳")
    public static long getBootTime() {
        return BOOT_TIME;
    }

    @Api("是否正在监听消息")
    public static boolean isEnable() {
        return enable;
    }

    @Api("否是真的登录账号")
    public static boolean isNoLogin() {
        return noLogin;
    }

    @Api("否是进入调试模式")
    public static boolean isDebug() {
        return debug;
    }

    @Api("是否进入抛弃模式")
    public static boolean isShutModeDrop() {
        return shutModeDrop;
    }

    @Api("获取运行目录 - 不是插件私有目录")
    public static String getRootFolder() {
        return FOLDER_ROOT.getAbsolutePath();
    }

    @Api("获取核心配置目录 - 不是插件私有目录")
    public static String getConfigFolder() {
        return FOLDER_CONFIG.getAbsolutePath();
    }

    @Api("获取插件核心目录 - 不是插件私有目录")
    public static String getPluginFolder() {
        return FOLDER_PLUGIN.getAbsolutePath();
    }

    @Api("获取插件依赖目录 - 不是插件私有目录")
    public static String getDependFolder() {
        return FOLDER_DEPEND.getAbsolutePath();
    }

    @Api("获取模块数据目录 - 不是插件私有目录")
    public static String getModuleFolder() {
        return FOLDER_MODULE.getAbsolutePath();
    }

    @Api("获取核心日志目录 - 不是插件私有目录")
    public static String getLoggerFolder() {
        return FOLDER_LOGGER.getAbsolutePath();
    }

    @Api("获取模块实例")
    public static <T extends EventHandlerRunner> T getRunner(Class<T> clazz) {
        return systemd.getRunner(clazz);
    }

    @Api("提交异步任务")
    public static Future<?> submit(Runnable runnable) {
        return systemd.submit(runnable);
    }

    @Api("提交异步任务")
    public static <T> Future<?> submit(Runnable runnable, T t) {
        return systemd.submit(runnable, t);
    }

    @Api("提交异步任务")
    public static Future<?> submit(Callable<?> callable) {
        return systemd.submit(callable);
    }

    @Api("提交定时任务")
    public static ScheduledFuture<?> schedule(Runnable runnable, long time, TimeUnit timeUnit) {
        return systemd.schedule(runnable, time, timeUnit);
    }

    @Api("提交定时任务")
    public static ScheduledFuture<?> schedule(Callable<?> callable, long delay, TimeUnit unit) {
        return systemd.schedule(callable, delay, unit);
    }

    @Api("提交等间隔定时任务")
    public static ScheduledFuture<?> scheduleAtFixedRate(Runnable runnable, long initialDelay, long period, TimeUnit unit) {
        return systemd.scheduleAtFixedRate(runnable, initialDelay, period, unit);
    }

    @Api("提交等延迟定时任务")
    public static ScheduledFuture<?> scheduleWithFixedDelay(Runnable runnable, long initialDelay, long delay, TimeUnit unit) {
        return systemd.scheduleWithFixedDelay(runnable, initialDelay, delay, unit);
    }

    @Api("提交明天开始的等间隔定时任务")
    public static ScheduledFuture<?> scheduleAtNextDayFixedRate(Runnable runnable, long period, TimeUnit unit) {
        return systemd.scheduleAtFixedRate(runnable, TimeTool.nextDayDuration(), period, unit);
    }

    @Api("提交明天开始的等延迟定时任务")
    public static ScheduledFuture<?> scheduleWithNextDayFixedDelay(Runnable runnable, long delay, TimeUnit unit) {
        return systemd.scheduleWithFixedDelay(runnable, TimeTool.nextDayDuration(), delay, unit);
    }


    // =================================================================================================================


    @Api("在终端打印消息")
    public static void terminalPrint(Object message) {
        if (message == null) return;
        terminal.print(message.toString());
    }


    @Api("在终端打印消息")
    public static void terminalPrintLine(Object message) {
        if (message == null) return;
        terminal.printLine(message.toString());
    }


    // =================================================================================================================
    //
    // Mirai转发 - 为了系统安全Bot不允许直接获取 需要对Mirai的方法进行转发
    //
    // =================================================================================================================

    //= ========================================================================
    //= 来自  IMirai.kt

    @Api("转发Mirai")
    public static List<ForwardMessage.Node> downloadForwardMessage(String resourceId) {
        return Mirai.getInstance().downloadForwardMessage(systemd.getBot(), resourceId);
    }

    @Api("转发Mirai")
    public static MessageChain downloadLongMessage(String resourceId) {
        return Mirai.getInstance().downloadLongMessage(systemd.getBot(), resourceId);
    }

    @Api("转发Mirai")
    public static List<OtherClientInfo> getOnlineOtherClientsList(boolean mayIncludeSelf) {
        return Mirai.getInstance().getOnlineOtherClientsList(systemd.getBot(), mayIncludeSelf);
    }

    @Api("转发Mirai")
    public static long getUin() {
        return Mirai.getInstance().getUin(systemd.getBot());
    }

    @Api("转发Mirai")
    public static String queryImageUrl(Image image) {
        return Mirai.getInstance().queryImageUrl(systemd.getBot(), image);
    }

    @Api("转发Mirai")
    public static UserProfile queryProfile(long id) {
        return Mirai.getInstance().queryProfile(systemd.getBot(), id);
    }

    @Api("转发Mirai")
    public static void recallMessage(MessageSource messageSource) {
        Mirai.getInstance().recallMessage(systemd.getBot(), messageSource);
    }

    @Api("转发Mirai")
    public static void sendNudge(Nudge nudge, Contact contact) {
        Mirai.getInstance().sendNudge(systemd.getBot(), nudge, contact);
    }

    //= ========================================================================
    //= 来自 LowLevelApiAccessor.kt

    @Api("转发Mirai")
    public static void getGroupVoiceDownloadUrl(byte[] md5, long groupId, long dstUin) {
        Mirai.getInstance().getGroupVoiceDownloadUrl(systemd.getBot(), md5, groupId, dstUin);
    }

    @Api("转发Mirai")
    public static Sequence<Long> getRawGroupList() {
        return Mirai.getInstance().getRawGroupList(systemd.getBot());
    }

    @Api("转发Mirai")
    public static Sequence<MemberInfo> getRawGroupMemberList(long groupUin, long groupCode, long ownerId) {
        return Mirai.getInstance().getRawGroupMemberList(systemd.getBot(), groupUin, groupCode, ownerId);
    }

    @Api("转发Mirai")
    public static void muteAnonymousMember(String anonymousId, String anonymousNick, long groupId, int seconds) {
        Mirai.getInstance().muteAnonymousMember(systemd.getBot(), anonymousId, anonymousNick, groupId, seconds);
    }

    @Api("转发Mirai")
    public static Friend newFriend(FriendInfo friendInfo) {
        return Mirai.getInstance().newFriend(systemd.getBot(), friendInfo);
    }

    @Api("转发Mirai")
    public static Stranger newStranger(StrangerInfo strangerInfo) {
        return Mirai.getInstance().newStranger(systemd.getBot(), strangerInfo);
    }

    @Api("转发Mirai")
    public static boolean recallFriendMessageRaw(long targetId, int[] messagesIds, int[] messageInternalIds, int time) {
        return Mirai.getInstance().recallFriendMessageRaw(systemd.getBot(), targetId, messagesIds, messageInternalIds, time);
    }

    @Api("转发Mirai")
    public static boolean recallGroupMessageRaw(long groupCode, int[] messagesIds, int[] messageInternalIds) {
        return Mirai.getInstance().recallGroupMessageRaw(systemd.getBot(), groupCode, messagesIds, messageInternalIds);
    }

    @Api("转发Mirai")
    public static boolean recallGroupTempMessageRaw(long groupUin, long targetId, int[] messagesIds, int[] messageInternalIds, int time) {
        return Mirai.getInstance().recallGroupTempMessageRaw(systemd.getBot(), groupUin, targetId, messagesIds, messageInternalIds, time);
    }

    @Api("转发Mirai")
    public static void refreshKeys() {
        Mirai.getInstance().refreshKeys(systemd.getBot());
    }

    @Api("转发Mirai")
    public static void solveBotInvitedJoinGroupRequestEvent(long eventId, long invitorId, long groupId, boolean accept) {
        Mirai.getInstance().solveBotInvitedJoinGroupRequestEvent(systemd.getBot(), eventId, invitorId, groupId, accept);
    }

    @Api("转发Mirai")
    public static void solveMemberJoinRequestEvent(long eventId, long fromId, String fromNick, long groupId, boolean accept, boolean blackList, String message) {
        Mirai.getInstance().solveMemberJoinRequestEvent(systemd.getBot(), eventId, fromId, fromNick, groupId, accept, blackList, message);
    }

    @Api("转发Mirai")
    public static void solveNewFriendRequestEvent(long eventId, long fromId, String fromNick, boolean accept, boolean blackList) {
        Mirai.getInstance().solveNewFriendRequestEvent(systemd.getBot(), eventId, fromId, fromNick, accept, blackList);
    }

    // =================================================================================================================
    //
    // Bot相关
    //
    // =================================================================================================================


    @Api("获取用户昵称")
    public static String getNickName(long user) {
        return queryProfile(user).getNickname();
    }

    @Api("获取用户格式化名")
    public static String getFormattedNickName(User user) {
        return user.getNick() + "(" + user.getId() + ")";
    }

    @Api("获取用户格式化名")
    public static String getFormattedNickName(long user) {
        return getNickName(user) + "(" + user + ")";
    }

    @Api("获取用户昵称")
    public static String getUsersMappedNickName(User user) {
        return systemd.getUsersMappedNickName(user);
    }

    @Api("获取用户昵称")
    public static String getUsersMappedNickName(long userId) {
        return systemd.getUsersMappedNickName(userId);
    }

    @Api("获取预设昵称")
    public static String getMappedNickName(GroupMessageEvent event) {
        return FurryBlack.getMemberMappedNickName(event.getSender());
    }

    @Api("获取预设昵称")
    public static String getMemberMappedNickName(Member member) {
        return systemd.getMemberMappedNickName(member);
    }

    @Api("获取预设昵称")
    public static String getMappedNickName(long groupId, long userId) {
        return systemd.getMemberMappedNickName(groupId, userId);
    }

    @Api("格式化群组信息")
    public static String getGroupInfo(Group group) {
        return group.getName() + "(" + group.getId() + ") " + group.getMembers().size() + " -> " + group.getOwner().getNameCard() + "(" + group.getOwner().getId() + ")";
    }

    @Api("获取BOT自身QQ号")
    public static long getBotID() {
        return systemd.getBotID();
    }

    @Api("列出所有好友")
    public static ContactList<Friend> getFriends() {
        return systemd.getFriends();
    }

    @Api("列出所有群组")
    public static ContactList<Group> getGroups() {
        return systemd.getGroups();
    }

    @Api("根据ID获取陌生人")
    public static Stranger getStranger(long id) {
        return systemd.getStranger(id);
    }

    @Api("根据ID获取陌生人")
    public static Stranger getStrangerOrFail(long id) {
        return systemd.getStrangerOrFail(id);
    }

    @Api("根据ID获取好友")
    public static Friend getFriend(long id) {
        return systemd.getFriend(id);
    }

    @Api("根据ID获取好友")
    public static Friend getFriendOrFail(long id) {
        return systemd.getFriendOrFail(id);
    }

    @Api("根据ID获取群组")
    public static Group getGroup(long id) {
        return systemd.getGroup(id);
    }

    @Api("根据ID获取群组")
    public static Group getGroupOrFail(long id) {
        return systemd.getGroupOrFail(id);
    }

    @Api("根据ID获取成员")
    public static NormalMember getMemberOrFail(long group, long member) {
        return getGroupOrFail(group).getOrFail(member);
    }

    @Api("获取图片的URL")
    public static String getImageURL(Image image) {
        return queryImageUrl(image);
    }

    @Api("获取图片的URL")
    public static String getImageURL(FlashImage flashImage) {
        return queryImageUrl(flashImage.getImage());
    }

    // == Systemd仅转发原生方法 Driver负责二次封装

    private static void sendContactMessage(Contact contact, Message message) {
        systemd.sendMessage(contact, message);
    }

    // ====

    @Api("发送私聊消息")
    public static void sendMessage(User user, Message message) {
        sendContactMessage(user, message);
    }

    @Api("发送私聊消息")
    public static void sendMessage(User user, String message) {
        sendMessage(user, new PlainText(message));
    }

    @Api("发送私聊消息")
    public static void sendMessage(UserMessageEvent event, Message message) {
        sendMessage(event.getSender(), message);
    }

    @Api("发送私聊消息")
    public static void sendMessage(UserMessageEvent event, String message) {
        sendMessage(event, new PlainText(message));
    }

    @Api("发送私聊消息")
    public static void sendUserMessage(long id, Message message) {
        User user = getFriend(id);
        if (user == null) user = getStrangerOrFail(id);
        sendMessage(user, message);
    }

    @Api("发送私聊消息")
    public static void sendUserMessage(long id, String message) {
        sendUserMessage(id, new PlainText(message));
    }

    // ====

    @Api("发送群组消息")
    public static void sendMessage(Group group, Message message) {
        sendContactMessage(group, message);
    }

    @Api("发送群组消息")
    public static void sendMessage(Group group, String message) {
        sendMessage(group, new PlainText(message));
    }

    @Api("发送群组消息")
    public static void sendMessage(GroupMessageEvent event, Message message) {
        sendMessage(event.getGroup(), message);
    }

    @Api("发送群组消息")
    public static void sendMessage(GroupMessageEvent event, String message) {
        sendMessage(event, new PlainText(message));
    }

    @Api("发送群组消息")
    public static void sendGroupMessage(long group, Message message) {
        sendMessage(getGroupOrFail(group), message);
    }

    @Api("发送群组消息")
    public static void sendGroupMessage(long group, String message) {
        sendGroupMessage(group, new PlainText(message));
    }

    // ====

    @Api("发送群组消息")
    public static void sendAtMessage(Group group, Member member, Message message) {
        sendMessage(group, new At(member.getId()).plus(message));
    }

    @Api("发送群组消息")
    public static void sendAtMessage(Group group, Member member, String message) {
        sendAtMessage(group, member, new PlainText(message));
    }

    @Api("发送群组消息")
    public static void sendAtMessage(GroupMessageEvent event, Message message) {
        sendAtMessage(event.getGroup(), event.getSender(), message);
    }

    @Api("发送群组消息")
    public static void sendAtMessage(GroupMessageEvent event, String message) {
        sendAtMessage(event, new PlainText(message));
    }

    @Api("发送群组消息")
    public static void sendAtMessage(long group, long member, Message message) {
        Group groupOrFail = getGroupOrFail(group);
        Member memberOrFail = groupOrFail.getOrFail(member);
        sendAtMessage(groupOrFail, memberOrFail, message);
    }

    @Api("发送群组消息")
    public static void sendAtMessage(long group, long member, String message) {
        Group groupOrFail = getGroupOrFail(group);
        Member memberOrFail = groupOrFail.getOrFail(member);
        sendAtMessage(groupOrFail, memberOrFail, new PlainText(message));
    }

    @Api("获取Mirai机器人实例 只有--unsafe模式下可以使用")
    public static Bot getBot() {
        if (unsafe) {
            return systemd.getBot();
        } else {
            logger.warning("获取机器人实例禁止 只有在unsafe模式下可用");
            for (StackTraceElement stackTraceElement : Thread.currentThread().getStackTrace()) {
                System.out.println(stackTraceElement);
            }
            throw new BotException("Get Mirai-BOT instance only allowed when --unsafe present!");
        }
    }


}
