package studio.blacktech.furryblackplus;


import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.ContactList;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.contact.Stranger;
import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.data.UserProfile;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.UserMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.FlashImage;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.PlainText;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.reader.impl.completer.AggregateCompleter;
import org.jline.reader.impl.completer.ArgumentCompleter;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.widget.AutopairWidgets;
import studio.blacktech.furryblackplus.common.Api;
import studio.blacktech.furryblackplus.core.Systemd;
import studio.blacktech.furryblackplus.core.define.Command;
import studio.blacktech.furryblackplus.core.define.moduel.EventHandlerRunner;
import studio.blacktech.furryblackplus.core.exception.console.ConsoleException;
import studio.blacktech.furryblackplus.core.exception.moduels.boot.BootException;
import studio.blacktech.furryblackplus.core.utilties.common.TimeTool;
import studio.blacktech.furryblackplus.core.utilties.logger.LoggerX;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

// 🔫 🧦 ❌ ✔️ ⭕ 🚧 🀄

/**
 * FurryBlack Plus Framework
 *
 * 本项目并非使用纯AGPLv3协议，请认真阅读LICENSE
 *
 * @author Alceatraz Warprays alceatraz@blacktech.studio
 * @see Driver 为启动类main方法所在地，初始化日志和目录系统，提供控制台
 * @see Systemd 是整个系统的内核所在
 */
@Api("项目开源地址https://github.com/Alceatraz/FurryBlack-Mirai")
public final class Driver {


    @Api("原始系统时区") public static final ZoneId SYSTEM_ZONEID;
    @Api("原始系统偏差") public static final ZoneOffset SYSTEM_OFFSET;


    static {

        System.setProperty("mirai.no-desktop", "");

        SYSTEM_ZONEID = ZoneId.systemDefault();
        SYSTEM_OFFSET = ZoneOffset.systemDefault().getRules().getOffset(LocalDateTime.now());

        Locale.setDefault(Locale.SIMPLIFIED_CHINESE);
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));

    }


    public static final String APP_VERSION = "0.8.0-SNAPSHOT";


    // ==========================================================================================================================================================
    //
    // 私有变量
    //
    // ==========================================================================================================================================================


    private static final long BOOT_TIME = System.currentTimeMillis();

    private static final LoggerX logger = new LoggerX(Driver.class);

    private static Systemd systemd;


    private static volatile boolean debug;

    private static volatile boolean enable;

    private static volatile boolean noLogin;
    private static volatile boolean noJline;

    private static volatile boolean shutModeExit;
    private static volatile boolean shutModeDrop;
    private static volatile boolean shutBySignal = true;

    private static final AtomicReference<String> prompt = new AtomicReference<>("");

    private static Thread consoleThread;

    private static File FOLDER_ROOT;
    private static File FOLDER_CONFIG;
    private static File FOLDER_MODULE;
    private static File FOLDER_PLUGIN;
    private static File FOLDER_LOGGER;


    // ==========================================================================================================================================================
    //
    // 启动入口
    //
    // ==========================================================================================================================================================


    public static void main(String[] args) {

        System.out.println("[FurryBlack][MAIN]FurryBlackPlus Mirai - ver " + APP_VERSION + " " + LoggerX.datetime(BOOT_TIME));

        // =====================================================================
        // 初始化命令行参数

        List<String> parameters = Arrays.asList(args);

        // =====================================================================
        // help 模式
        if (parameters.contains("--help")) {
            printHelp();
            return;
        }

        // =====================================================================
        // debug 模式
        debug = parameters.contains("--debug");
        if (debug) {
            System.out.println("[FurryBlack][ARGS]调试模式");
        } else {
            System.out.println("[FurryBlack][ARGS]生产模式");
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
        // 退出模式 设置
        shutModeExit = parameters.contains("--force-exit");
        if (shutModeExit) {
            System.out.println("[FurryBlack][ARGS]使用强制退出");
        } else {
            System.out.println("[FurryBlack][ARGS]使用正常退出");
        }


        // =====================================================================
        // 日志级别 设置
        String level = System.getProperty("furryblack.logger.level");
        if (level != null) {
            if (LoggerX.LEVELS.contains(level)) {
                System.out.println("[FurryBlack][PROP]目标日志级别" + level);
            } else {
                System.out.println("[FurryBlack][PROP]不存在此目标日志级别" + level + ", 可用值为 MUTE ERROR WARN HINT SEEK INFO DEBUG VERBOSE");
            }
        }

        // =====================================================================
        // 初始化目录

        try {

            System.out.println("[FurryBlack][INIT]初始化路径");

            String userDir = System.getProperty("user.dir");

            FOLDER_ROOT = Paths.get(userDir).toFile();

            FOLDER_CONFIG = Paths.get(userDir, "config").toFile();
            FOLDER_MODULE = Paths.get(userDir, "module").toFile();
            FOLDER_PLUGIN = Paths.get(userDir, "plugin").toFile();
            FOLDER_LOGGER = Paths.get(userDir, "logger").toFile();

            File loggerFile = Paths.get(FOLDER_LOGGER.getAbsolutePath(), LoggerX.format("yyyy_MM_dd_HH_mm_ss", BOOT_TIME) + ".txt").toFile();

            System.out.println("[FurryBlack][INIT]初始化目录");

            if (!FOLDER_CONFIG.exists() && !FOLDER_CONFIG.mkdirs()) throw new BootException("无法创建文件夹 " + FOLDER_CONFIG.getAbsolutePath());
            if (!FOLDER_MODULE.exists() && !FOLDER_MODULE.mkdirs()) throw new BootException("无法创建文件夹 " + FOLDER_MODULE.getAbsolutePath());
            if (!FOLDER_PLUGIN.exists() && !FOLDER_PLUGIN.mkdirs()) throw new BootException("无法创建文件夹 " + FOLDER_PLUGIN.getAbsolutePath());
            if (!FOLDER_LOGGER.exists() && !FOLDER_LOGGER.mkdirs()) throw new BootException("无法创建文件夹 " + FOLDER_LOGGER.getAbsolutePath());

            System.out.println("[FurryBlack][INIT]初始化检查");

            if (!FOLDER_CONFIG.isDirectory()) throw new BootException("文件夹被文件占位 " + FOLDER_CONFIG.getAbsolutePath());
            if (!FOLDER_MODULE.isDirectory()) throw new BootException("文件夹被文件占位 " + FOLDER_MODULE.getAbsolutePath());
            if (!FOLDER_PLUGIN.isDirectory()) throw new BootException("文件夹被文件占位 " + FOLDER_PLUGIN.getAbsolutePath());
            if (!FOLDER_LOGGER.isDirectory()) throw new BootException("文件夹被文件占位 " + FOLDER_LOGGER.getAbsolutePath());

            System.out.println("[FurryBlack][INIT]创建日志文件");

            if (!loggerFile.createNewFile()) throw new BootException("日志文件创建失败 " + loggerFile.getAbsolutePath());
            if (!loggerFile.exists()) throw new BootException("日志文件不存在 " + loggerFile.getAbsolutePath());
            if (!loggerFile.canWrite()) throw new BootException("日志文件没有写权限 " + loggerFile.getAbsolutePath());

            LoggerX.init(loggerFile);

            System.out.println("[FurryBlack][INIT]日志系统初始化完成");

            logger.info("应用工作目录 " + FOLDER_ROOT.getAbsolutePath());
            logger.info("插件扫描目录 " + FOLDER_PLUGIN.getAbsolutePath());
            logger.info("模块数据目录 " + FOLDER_MODULE.getAbsolutePath());
            logger.info("核心日志目录 " + FOLDER_LOGGER.getAbsolutePath());
            logger.info("当前日志文件 " + loggerFile.getAbsolutePath());

            systemd = new Systemd(FOLDER_CONFIG, FOLDER_PLUGIN);

        } catch (Exception exception) {
            throw new BootException("[FurryBlack][FATAL]核心系统初始化发生异常 终止启动", exception);
        }


        // =====================================================================

        logger.hint("启动机器人...");
        try {
            systemd.boot();
        } catch (Exception exception) {
            logger.error("启动路由系统发生异常 终止启动", exception);
            System.exit(-1);
        }
        logger.hint("机器人已启动");

        // =====================================================================

        logger.info("注册关闭回调");

        Thread mainThread = Thread.currentThread();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (!shutBySignal) return;
            consoleThread.interrupt();
            systemd.signal();
            try {
                mainThread.join();
            } catch (InterruptedException exception) {
                System.out.println("[FurryBlack][MAIN]FurryBlackPlus shutdown hook interrupted!");
                exception.printStackTrace();
            }
        }));

        // =====================================================================

        if (!noConsole) {
            logger.info("启动终端线程");
            consoleThread = new Thread(Driver::console);
            consoleThread.setDaemon(true);
            consoleThread.start();
        }


        // =====================================================================

        logger.hint("系统启动完成 耗时" + TimeTool.duration(System.currentTimeMillis() - BOOT_TIME));
        if (!debug) {
            LoggerX.setPrintLevel(level);
        }

        enable = true;

        // =====================================================================

        systemd.await();

        // =====================================================================

        enable = false;

        LoggerX.setPrintLevel(LoggerX.LEVEL.ALL);

        // =====================================================================

        try {
            systemd.shut();
        } catch (Exception exception) {
            logger.error("关闭路由系统关闭异常", exception);
        }


        if (shutModeExit || shutModeDrop) {
            System.out.println("[FurryBlack][MAIN]FurryBlackPlus force exit, Bye.");
            System.exit(0);
        } else {
            System.out.println("[FurryBlack][MAIN]FurryBlackPlus closed, Bye.");
        }

    }


    public static void printHelp() {

        System.out.println(LoggerX.Color.LIGHT_CYAN + "# FurryBlackPlus 启动参数 ===========================" + LoggerX.Color.RESET);
        System.out.println("--debug       使用DEBUG模式启动");
        System.out.println("--no-login    使用离线模式，仅用于基础调试，功能基本都不可用");
        System.out.println("--no-console  不使用控制台，唯一正常关闭方式是使用进程信号");
        System.out.println("--no-jline    不使用jline控制台，使用BufferedReader");
        System.out.println("--force-exit  关闭流程执行后，强制结束System.exit(0)");

        System.out.println(LoggerX.Color.LIGHT_CYAN + "# FurryBlackPlus 系统参数 ===========================" + LoggerX.Color.RESET);
        System.out.println("furryblack.logger.level 日志等级");

        System.out.println(LoggerX.Color.LIGHT_CYAN + "# FurryBlackPlus 控制台  ===========================" + LoggerX.Color.RESET);
        System.out.println(LoggerX.Color.RED + "⚠ 控制台任何操作都属于底层操作可以直接对框架进行不安全和非法的操作" + LoggerX.Color.RESET);
        System.out.println("安全：设计如此，不会导致异常或者不可预测的结果");
        System.out.println("风险：功能设计上是安全操作，但是具体被操作对象可能导致错误");
        System.out.println("危险：没有安全性检查的操作，可能会让功能严重异常导致被迫重启或损坏模块的数据存档");
        System.out.println("高危：后果完全未知的危险操作，或者正常流程中不应该如此操作但是控制台仍然可以强制执行");

        System.out.println(LoggerX.Color.EMERALD_GREEN + "# 系统管理 ==========================================" + LoggerX.Color.RESET);
        System.out.println("level (安全) 修改控制台日志打印等级，日志不受影响(可能导致漏掉ERR/WARN信息)");
        System.out.println("stat  (安全) 查看性能状态");
        System.out.println("stop  (安全) 正常退出，完整执行关闭流程，等待模块结束，等待线程池结束，等待所有线程");
        System.out.println("drop  (高危) 强制退出，不等待插件关闭完成，不等待线程池结束，且最终杀死JVM");
        System.out.println("kill  (高危) 命令执行后直接杀死JVM，不会进行任何关闭操作");

        System.out.println(LoggerX.Color.EMERALD_GREEN + "# 功能管理 ==========================================" + LoggerX.Color.RESET);
        System.out.println("enable  (安全) 启用消息事件处理 正常响应消息");
        System.out.println("disable (安全) 停用消息事件处理 无视任何消息");

        System.out.println(LoggerX.Color.EMERALD_GREEN + "# 好友相关 ==========================================" + LoggerX.Color.RESET);
        System.out.println("list users   (安全) 列出好友");
        System.out.println("list group   (安全) 列出群组");
        System.out.println("list <group> (安全) 列出成员");

        System.out.println(LoggerX.Color.EMERALD_GREEN + "# 发送消息 ==========================================" + LoggerX.Color.RESET);
        System.out.println("send users <users> <消息>  (安全) 向好友发送消息");
        System.out.println("send group <group> <消息>  (安全) 向群聊发送消息");
        System.out.println("send <group> <user> <消息> (安全) 向群聊发送AT消息");

        System.out.println(LoggerX.Color.EMERALD_GREEN + "# 模型管理 ==========================================" + LoggerX.Color.RESET);
        System.out.println("schema (安全) 详细显示插件和模块");

        System.out.println(LoggerX.Color.EMERALD_GREEN + "# 插件管理 ==========================================" + LoggerX.Color.RESET);
        System.out.println("plugin (安全) 列出插件");
        System.out.println("plugin unload (安全) 卸载所有插件，关闭顺序等效于shut");
        System.out.println("plugin import <路径> (风险) 加载某个插件，如果加载被异常打断，无法回滚至加载前的状态，只能重启");
        System.out.println("plugin unload <名称> (危险) 卸载某个插件，因为插件之间Runner有可能相互依赖，有可能导致意外或者抛出异常");
        System.out.println("plugin reload <名称> (危险) 重载某个插件，插件将被卸载，再加载同文件路径插件包，不检查模块增减和版本，可能崩溃");

        System.out.println(LoggerX.Color.EMERALD_GREEN + "# 模块管理 ==========================================" + LoggerX.Color.RESET);
        System.out.println("module (安全) 列出模块");
        System.out.println("※ Runner可能会被依赖，底层操作框架不检查依赖，有可能导致关联模块崩溃");
        System.out.println("module reboot <名称> (风险) 重启指定模块(执行 shut + init + boot)");
        System.out.println("module shut   <名称> (风险) 关闭指定模块(执行 shut)");
        System.out.println("module init   <名称> (风险) 预载指定模块(执行 init)");
        System.out.println("module shut   <名称> (风险) 启动指定模块(执行 boot)");
        System.out.println("※ Runner可能会被依赖，框架无法检查此操作，相关的模块仍然使用旧实例，需要重启相关模块(重新执行getRunner)");
        System.out.println("module unload <名称> (危险) 卸载指定模块实例，但保留扫描结果");
        System.out.println("module reload <名称> (危险) 重启 + 重新实例化执行模块");

        System.out.println(LoggerX.Color.EMERALD_GREEN + "# 调试功能 ==========================================" + LoggerX.Color.RESET);
        System.out.println("debug [enable|disable] (风险) DEBUG开关，打印DEBUG输出和控制某些功能，插件如果不遵守标准开发可能会导致崩溃");
    }


    // ==========================================================================================================================================================
    //
    //
    //
    // ==========================================================================================================================================================


    @SuppressWarnings("SwitchStatementWithTooFewBranches")
    private static void console() {


        Console console = noJline ? new ReaderConsole() : new JLineConsole();

        new Thread(() -> {
            try {
                Thread.sleep(10L);
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }
            prompt.set("[console]$ ");
        }).start();

        console:
        while (true) {
            try {
                String temp = console.readLine(prompt.get());
                if (temp == null || temp.isEmpty() || temp.isBlank()) continue;
                Command command = new Command(temp.trim());
                switch (command.getCommandName()) {


                    // =========================================================


                    case "kill":
                        System.out.println("[FurryBlack] Kill the JVM");
                        System.exit(-1);

                    case "drop":
                        shutModeDrop = true;

                    case "stop":
                    case "quit":
                    case "exit":
                        shutBySignal = false;
                        prompt.set("");
                        systemd.signal();
                        break console;


                    // =========================================================


                    case "?":
                    case "help":
                        printHelp();
                        break;


                    // =========================================================


                    case "info":
                        System.out.println("FurryBlack-Plus");
                        System.out.println("A Mirai wrapper QQ-Bot framework make with love and \uD83E\uDDE6");
                        System.out.println("Mr.Black is a  with a white bear furry visualize");
                        System.out.println("Create by Alceatraz Warprays @ Black Tech Studio");
                        System.out.println("https://www.blacktech.studio");
                        break;


                    // =========================================================


                    case "debug":

                        switch (command.getParameterLength()) {

                            case 1:

                                switch (command.getParameterSegment(0)) {

                                    case "enable":
                                        debug = true;
                                        System.out.println("DEBUG模式启动");
                                        break;

                                    case "disable":
                                        debug = false;
                                        System.out.println("DEBUG模式关闭");
                                        break;

                                    default:
                                        System.out.println(debug ? "DEBUG已开启" : "DEBUG已关闭");
                                        break;

                                }
                                break;

                            default:
                                System.out.println(debug ? "DEBUG已开启" : "DEBUG已关闭");
                                break;

                        }

                        break;


                    // =========================================================


                    case "enable":
                        enable = true;
                        System.out.println("启动事件响应");
                        break;

                    case "disable":
                        enable = false;
                        System.out.println("关闭事件响应");
                        break;


                    // =========================================================


                    // plugin
                    case "plugin":

                        switch (command.getParameterLength()) {

                            case 2:

                                switch (command.getParameterSegment(0)) {

                                    // plugin load <file-name>
                                    case "import":
                                        systemd.importPlugin(command.getParameterSegment(1));
                                        systemd.generateListMessage();
                                        break;

                                    // plugin unload <plugin>
                                    case "unload":
                                        systemd.unloadPlugin(command.getParameterSegment(1));
                                        systemd.generateListMessage();
                                        break;

                                    // plugin reload <plugin>
                                    case "reload":
                                        systemd.reloadPlugin(command.getParameterSegment(1));
                                        systemd.generateListMessage();
                                        break;

                                    default:
                                        System.out.println("plugin <import|unload|reload> <name|path>");
                                        break;

                                }
                                break;

                            case 1:

                                switch (command.getParameterSegment(0)) {

                                    // plugin unload
                                    case "unload":
                                        for (String s : systemd.listAllPlugin()) {
                                            systemd.unloadPlugin(s);
                                        }
                                        systemd.generateListMessage();
                                        break;

                                    default:
                                        System.out.println("plugin unload");

                                }
                                break;

                            // plugin
                            case 0:
                                systemd.listAllPlugin().forEach(System.out::println);


                        }
                        break;


                    // =========================================================


                    // module
                    case "module":

                        switch (command.getParameterLength()) {

                            case 2:

                                switch (command.getParameterSegment(0)) {

                                    // module shut <plugin>
                                    case "shut":
                                        systemd.shutModule(command.getParameterSegment(1));
                                        break;

                                    // module init <plugin>
                                    case "init":
                                        systemd.initModule(command.getParameterSegment(1));
                                        break;

                                    // module boot <plugin>
                                    case "boot":
                                        systemd.bootModule(command.getParameterSegment(1));
                                        break;

                                    // module reboot <plugin>
                                    case "reboot":
                                        systemd.rebootModule(command.getParameterSegment(1));
                                        break;

                                    // module unload <plugin>
                                    case "unload":
                                        systemd.unloadModule(command.getParameterSegment(1));
                                        break;

                                    // module reload <plugin>
                                    case "reload":
                                        systemd.reloadModule(command.getParameterSegment(1));
                                        break;

                                }
                                break;


                            // module
                            case 0:

                                Map<String, Boolean> listAllRunner = systemd.listAllRunner();
                                System.out.println(">> 定时器 " + listAllRunner.size());
                                for (Map.Entry<String, Boolean> entry : listAllRunner.entrySet()) {
                                    System.out.println((entry.getValue() ? "√ " : "  ") + entry.getKey());
                                }

                                Map<String, Boolean> listAllFilter = systemd.listAllFilter();
                                System.out.println(">> 过滤器 " + listAllFilter.size());
                                for (Map.Entry<String, Boolean> entry : listAllFilter.entrySet()) {
                                    System.out.println((entry.getValue() ? "√ " : "  ") + entry.getKey());
                                }

                                Map<String, Boolean> listAllMonitor = systemd.listAllMonitor();
                                System.out.println(">> 监听器 " + listAllMonitor.size());
                                for (Map.Entry<String, Boolean> entry : listAllMonitor.entrySet()) {
                                    System.out.println((entry.getValue() ? "√ " : "  ") + entry.getKey());
                                }

                                Map<String, Boolean> listAllExecutor = systemd.listAllExecutor();
                                System.out.println(">> 执行器 " + listAllExecutor.size());
                                for (Map.Entry<String, Boolean> entry : listAllExecutor.entrySet()) {
                                    System.out.println((entry.getValue() ? "√ " : "  ") + entry.getKey());
                                }

                                System.out.println(">> 私聊命令列表");
                                System.out.println(systemd.getMessageListUsers());

                                System.out.println(">> 群聊命令列表");
                                System.out.println(systemd.getMessageListGroup());

                                break;
                        }
                        break;


                    // =========================================================


                    case "gc":
                    case "stat":
                    case "stats":
                    case "status":
                        long totalMemory = Runtime.getRuntime().totalMemory() / 1024;
                        long freeMemory = Runtime.getRuntime().freeMemory() / 1024;
                        long maxMemory = Runtime.getRuntime().maxMemory() / 1024;
                        System.out.println("调试模式: " + (debug ? "启用" : "关闭"));
                        System.out.println("关闭模式: " + (shutModeExit ? "强制" : "正常"));
                        System.out.println("消息事件: " + (enable ? "启用" : "关闭"));
                        System.out.println("运行时间: " + TimeTool.duration(System.currentTimeMillis() - BOOT_TIME));
                        System.out.println("内存占用: " + (totalMemory - freeMemory) + "KB/" + totalMemory + "KB/" + maxMemory + "KB(" + maxMemory / 1024 + "MB)");
                        break;

                    case "level":
                        if (command.hasCommandBody()) {
                            String level = command.getParameterSegment(0);
                            if (LoggerX.setPrintLevel(level)) {
                                logger.bypass("日志级别调整为 " + level);
                            } else {
                                logger.bypass("修改日志级别失败：不存在此级别，可用值为 MUTE ERROR WARN HINT SEEK INFO DEBUG VERBOSE");
                            }
                        } else {
                            logger.bypass("可用值为 MUTE ERROR WARN HINT SEEK INFO DEBUG VERBOSE ALL");
                            logger.error("[EXCE]错误 红色 ERROR");
                            logger.warning("[WARN]警告 黄色 WARN");
                            logger.hint("[HINT]提示 青色 HINT");
                            logger.seek("[SEEK]配置 绿色 SEEK");
                            logger.info("[INFO]信息 白色 INFO");
                            logger.debug("[DEBG]调试 灰色 DEBUG");
                            logger.verbose("[VERB]详情 灰色 VERBOSE");
                        }
                        break;


                    // =========================================================


                    case "list":
                        if (!command.hasCommandBody()) continue console;
                        switch (command.getParameterSegment(0)) {

                            case "u":
                            case "usr":
                            case "user":
                            case "users":
                            case "f":
                            case "fri":
                            case "friend":
                            case "friends":
                                List<Friend> friends = Driver.getFriends().stream().filter(item -> item.getId() != systemd.getBotID()).collect(Collectors.toList());
                                if (friends.size() == 0) {
                                    System.out.println("你没有朋友");
                                    break;
                                }
                                friends.stream()
                                    .map(Driver::getFormattedNickName)
                                    .forEach(System.out::println);
                                break;

                            case "g":
                            case "grp":
                            case "group":
                            case "groups":
                                ContactList<Group> groups = Driver.getGroups();
                                if (groups.size() == 0) {
                                    System.out.println("你没有群组");
                                    break;
                                }
                                groups.stream()
                                    .map(item -> item.getName() + "(" + item.getId() + ") " + item.getMembers().size() + "人")
                                    .forEach(System.out::println);
                                break;

                            default:
                                long group;
                                try {
                                    group = Long.parseLong(command.getParameterSegment(0));
                                } catch (Exception exception) {
                                    System.out.println("命令发生异常 省略group需要指定群号");
                                    break;
                                }
                                Driver.getGroup(group).getMembers().stream()
                                    .sorted((_$1, _$2) -> _$2.getPermission().getLevel() - _$1.getPermission().getLevel())
                                    .forEach(item -> {
                                        StringBuilder builder = new StringBuilder();
                                        builder.append(item.getNameCard());
                                        builder.append(" - ");
                                        builder.append(Driver.getFormattedNickName(item));
                                        switch (item.getPermission().getLevel()) {
                                            case 2:
                                                builder.append(" 群主");
                                                break;
                                            case 1:
                                                builder.append(" 管理");
                                                break;
                                            default:

                                        }
                                        System.out.println(builder);
                                    });
                        }
                        break;


                    // =========================================================


                    case "send":
                        if (command.getParameterLength() < 1) continue;
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
                                Driver.sendUserMessage(user, command.join(2));
                                break;

                            case "g":
                            case "grp":
                            case "group":
                            case "groups":
                                long group = Long.parseLong(command.getParameterSegment(1));
                                Driver.sendGroupMessage(group, command.join(2));
                                break;

                            default:
                                group = Long.parseLong(command.getParameterSegment(0));
                                user = Long.parseLong(command.getParameterSegment(1));
                                Driver.sendAtMessage(group, user, command.join(2));
                        }
                        break;


                    // =========================================================


                    case "schema":
                        systemd.schemaDebug();
                        break;


                    default:
                        System.out.println("没有此命令");
                        break;
                }

            } catch (UserInterruptException exception) {
                return;
            } catch (Exception exception) {
                logger.error("命令导致了异常", exception);
            }
        }
    }


    // ==========================================================================================================================================================
    //
    // 控制台
    //
    // ==========================================================================================================================================================


    public interface Console {
        String readLine(String prompt);
    }


    public static class JLineConsole implements Console {

        private final LineReader jlineReader;

        public JLineConsole() {
            this.jlineReader = LineReaderBuilder.builder().completer(new AggregateCompleter(
                new ArgumentCompleter(new StringsCompleter("?", "help", "kill", "drop", "stop", "enable", "disable", "stat", "level", "schema")),
                new ArgumentCompleter(
                    new StringsCompleter("debug"),
                    new StringsCompleter("enable", "disable")
                ),
                new ArgumentCompleter(
                    new StringsCompleter("list", "send"),
                    new StringsCompleter("users", "group")
                ),
                new ArgumentCompleter(
                    new StringsCompleter("plugin"),
                    new StringsCompleter("unload", "reload"),
                    new StringsCompleter(systemd.listAllPlugin())
                ),
                new ArgumentCompleter(
                    new StringsCompleter("plugin"),
                    new StringsCompleter("import", "unload")
                ),
                new ArgumentCompleter(
                    new StringsCompleter("module"),
                    new StringsCompleter("init", "boot", "shut", "reboot", "unload", "reload"),
                    new StringsCompleter(systemd.listAllModule().keySet())
                )
            )).build();
            AutopairWidgets autopairWidgets = new AutopairWidgets(this.jlineReader);
            autopairWidgets.enable();
        }

        @Override
        public String readLine(String prompt) {
            return this.jlineReader.readLine(prompt);
        }

    }


    public static class ReaderConsole implements Console {

        private final BufferedReader bufferedReader;

        public ReaderConsole() {
            this.bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        }

        @Override
        public String readLine(String prompt) {
            try {
                return this.bufferedReader.readLine();
            } catch (IOException exception) {
                throw new ConsoleException(exception);
            }
        }
    }


    // ==========================================================================================================================================================
    //
    // Runtime相关
    //
    // ==========================================================================================================================================================


    @Api("获取启动时间戳")
    public static long getBootTime() {
        return BOOT_TIME;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
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

    @Api("获取配置目录 - 不是插件私有目录")
    public static String getConfigFolder() {
        return FOLDER_CONFIG.getAbsolutePath();
    }

    @Api("获取数据目录 - 不是插件私有目录")
    public static String getModuleFolder() {
        return FOLDER_MODULE.getAbsolutePath();
    }

    @Api("获取插件目录 - 不是插件私有目录")
    public static String getPluginFolder() {
        return FOLDER_PLUGIN.getAbsolutePath();
    }

    @Api("获取日志目录 - 不是插件私有目录")
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

    // ==========================================================================================================================================================
    //
    // Bot相关
    //
    // ==========================================================================================================================================================

    @Api("获取用户信息")
    public static UserProfile getUserProfile(long user) {
        return systemd.getUserProfile(user);
    }

    @Api("获取用户昵称")
    public static String getNickName(long user) {
        return getUserProfile(user).getNickname();
    }

    @Api("获取用户格式化名")
    public static String getFormattedNickName(User user) {
        return user.getNick() + "(" + user.getId() + ")";
    }

    @Api("获取用户格式化名")
    public static String getFormattedNickName(long user) {
        return getNickName(user) + "(" + user + ")";
    }

    @Api("获取预设昵称")
    public static String getMappedNickName(GroupMessageEvent event) {
        return systemd.getMappedNickName(event);
    }

    @Api("获取预设昵称")
    public static String getMappedNickName(long groupId, long userId) {
        return systemd.getMappedNickName(groupId, userId);
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
        return systemd.getImageURL(image);
    }

    @Api("获取图片的URL")
    public static String getImageURL(FlashImage flashImage) {
        return getImageURL(flashImage.getImage());
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
}
