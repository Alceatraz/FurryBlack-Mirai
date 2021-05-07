package studio.blacktech.furryblackplus;


import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.ContactList;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.contact.Stranger;
import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.UserMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.FlashImage;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.PlainText;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.widget.AutopairWidgets;
import studio.blacktech.furryblackplus.core.Systemd;
import studio.blacktech.furryblackplus.core.annotation.Api;
import studio.blacktech.furryblackplus.core.exception.initlization.BootException;
import studio.blacktech.furryblackplus.core.interfaces.EventHandlerRunner;
import studio.blacktech.furryblackplus.core.utilties.Command;
import studio.blacktech.furryblackplus.core.utilties.DateTool;
import studio.blacktech.furryblackplus.core.utilties.LoggerX;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


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


    static {

        System.setProperty("mirai.no-desktop", "");

        Locale.setDefault(Locale.SIMPLIFIED_CHINESE);
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));

    }


    public static final String APP_VERSION = "0.6.3";


    // ==========================================================================================================================================================
    //
    // 私有变量
    //
    // ==========================================================================================================================================================


    private static final long BOOT_TIME = System.currentTimeMillis();

    private static final LoggerX logger = new LoggerX(Driver.class);

    private static Systemd systemd;


    private static volatile boolean debug;

    private static volatile boolean noLogin = false;
    private static volatile boolean noJline = false;

    private static volatile boolean enable = false;

    private static volatile boolean shutBySignal = true;
    private static volatile boolean shutModeDrop = false;


    private static Thread consoleThread;

    private static File FOLDER_ROOT;
    private static File FOLDER_CONFIG;
    private static File FOLDER_MODULE;
    private static File FOLDER_LOGGER;


    // ==========================================================================================================================================================
    //
    // 启动入口
    //
    // ==========================================================================================================================================================


    public static void main(String[] args) {

        System.out.println("[FurryBlack][MAIN]FurryBlackPlus Mirai - ver " + APP_VERSION + " " + DateTool.formatTime("yyyy-MM-dd HH:mm:ss", BOOT_TIME));

        // =====================================================================
        // 初始化命令行参数

        List<String> parameters = Arrays.asList(args);

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
            FOLDER_LOGGER = Paths.get(userDir, "logger").toFile();

            File loggerFile = Paths.get(FOLDER_LOGGER.getAbsolutePath(), DateTool.formatTime("yyyy_MM_dd_HH_mm_ss", BOOT_TIME) + ".txt").toFile();

            System.out.println("[FurryBlack][INIT]初始化目录");

            if (!FOLDER_CONFIG.exists() && !FOLDER_CONFIG.mkdirs()) throw new BootException("无法创建文件夹 " + FOLDER_CONFIG.getAbsolutePath());
            if (!FOLDER_MODULE.exists() && !FOLDER_MODULE.mkdirs()) throw new BootException("无法创建文件夹 " + FOLDER_MODULE.getAbsolutePath());
            if (!FOLDER_LOGGER.exists() && !FOLDER_LOGGER.mkdirs()) throw new BootException("无法创建文件夹 " + FOLDER_LOGGER.getAbsolutePath());

            System.out.println("[FurryBlack][INIT]初始化检查");

            if (!FOLDER_CONFIG.isDirectory()) throw new BootException("文件夹被文件占位 " + FOLDER_CONFIG.getAbsolutePath());
            if (!FOLDER_MODULE.isDirectory()) throw new BootException("文件夹被文件占位 " + FOLDER_MODULE.getAbsolutePath());
            if (!FOLDER_LOGGER.isDirectory()) throw new BootException("文件夹被文件占位 " + FOLDER_LOGGER.getAbsolutePath());

            System.out.println("[FurryBlack][INIT]创建日志文件");

            if (!loggerFile.createNewFile()) throw new BootException("日志文件创建失败 " + loggerFile.getAbsolutePath());
            if (!loggerFile.exists()) throw new BootException("日志文件不存在 " + loggerFile.getAbsolutePath());
            if (!loggerFile.canWrite()) throw new BootException("日志文件没有写权限 " + loggerFile.getAbsolutePath());

            LoggerX.init(loggerFile);

            System.out.println("[FurryBlack][INIT]日志系统初始化完成");

            logger.info("应用工作目录 " + FOLDER_ROOT.getAbsolutePath());
            logger.info("核心日志目录 " + FOLDER_LOGGER.getAbsolutePath());
            logger.info("模块数据目录 " + FOLDER_MODULE.getAbsolutePath());
            logger.info("当前日志文件 " + loggerFile.getAbsolutePath());

            systemd = new Systemd(FOLDER_CONFIG);

        } catch (Exception exception) {
            throw new RuntimeException("[FurryBlack][FATAL]核心系统初始化发生异常 终止启动", exception);
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
            logger.info("接收到关闭信号");
            logger.info("关闭控制台");
            consoleThread.interrupt();
            logger.info("关闭机器人");
            systemd.shutBot();
            logger.info("等待主线程");
            try {
                mainThread.join();
            } catch (InterruptedException ignoring) {
                logger.error("关闭信号回调被打断", ignoring);
            }
            logger.info("回调结束");
        }));

        // =====================================================================

        if (!noConsole) {
            logger.info("启动终端线程");
            consoleThread = new Thread(Driver::console);
            consoleThread.start();
        }


        // =====================================================================

        logger.hint("系统启动完成 耗时" + DateTool.duration(System.currentTimeMillis() - BOOT_TIME));
        LoggerX.setPrintLevel(level);

        enable = true;

        // =====================================================================

        systemd.joinBot();

        // =====================================================================

        enable = false;

        LoggerX.setPrintLevel(LoggerX.LEVEL.ALL);
        logger.info("执行关闭流程");


        // =====================================================================

        logger.hint("关闭路由系统");

        try {
            systemd.shut();
        } catch (Exception exception) {
            logger.error("关闭路由系统关闭异常", exception);
        }

        logger.hint("关闭核心系统");

        System.out.println("[FurryBlack][MAIN]FurryBlackPlus closed, Bye.");

    }


    // ==========================================================================================================================================================
    //
    //
    //
    // ==========================================================================================================================================================

    private static boolean firstPrompt = true;

    private static void console() {
        Console console = new Console();
        console:
        while (true) {
            try {
                String temp = console.read("[console]$ ");
                if (temp == null || temp.isEmpty() || temp.isBlank()) continue;
                Command command = new Command(temp);
                switch (command.getCommandName()) {

                    case "drop":
                        shutModeDrop = true;

                    case "stop":
                    case "quit":
                    case "exit":
                        shutBySignal = false;
                        systemd.shutBot();
                        break console;

                    case "?":
                    case "help":
                        System.out.println("exit    退出");
                        System.out.println("list    列出好友和群");
                        System.out.println("enable  开启消息处理");
                        System.out.println("disable 关闭消息处理");
                        System.out.println("module  列出所有模块");
                        System.out.println("reload  重启指定模块");
                        break;

                    case "debug":
                        synchronized (Driver.class) {
                            debug = !debug;
                        }
                        System.out.println(debug ? "Enable DEBUG" : "Disable DEBUG");
                        break;

                    case "module":
                        systemd.listAllPlugin().forEach(System.out::println);
                        break;

                    case "reload":
                        for (String name : command.getParameterSegment()) systemd.reloadPlugin(name);
                        break;

                    case "enable":
                        enable = true;
                        System.out.println("启动事件响应");
                        break;

                    case "disable":
                        enable = false;
                        System.out.println("关闭事件响应");
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
                                        System.out.println(builder.toString());
                                    });
                        }
                        break;

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

                    case "gc":
                    case "stat":
                    case "stats":
                    case "status":
                        long totalMemory = Runtime.getRuntime().totalMemory() / 1024;
                        long freeMemory = Runtime.getRuntime().freeMemory() / 1024;
                        long maxMemory = Runtime.getRuntime().maxMemory() / 1024;
                        System.out.println("消息事件: " + (enable ? "启用" : "关闭"));
                        System.out.println("运行时间: " + DateTool.duration(System.currentTimeMillis() - BOOT_TIME));
                        System.out.println("内存占用: " + (totalMemory - freeMemory) + "KB/" + totalMemory + "KB/" + maxMemory + "KB(" + (maxMemory / 1024) + "MB)");
                        break;


                    default:
                        System.out.println("没有此命令");
                        break;
                }
            } catch (Exception exception) {
                logger.error("命令导致了异常", exception);
            }
        }
    }

    private static class Console {

        private LineReader jlineReader;
        private BufferedReader bufferedReader;

        public Console() {
            if (noJline) {
                bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            } else {
                jlineReader = LineReaderBuilder.builder().build();
                AutopairWidgets autopairWidgets = new AutopairWidgets(jlineReader);
                autopairWidgets.enable();
                assert jlineReader != null;
            }
        }

        public String read(String prompt) {

            if (noJline) {
                if (firstPrompt) {
                    firstPrompt = false;
                } else {
                    System.out.print(prompt);
                }
                try {
                    return bufferedReader.readLine();
                } catch (IOException exception) {
                    return null;
                }
            } else {
                if (firstPrompt) {
                    firstPrompt = false;
                    return jlineReader.readLine("");
                } else {
                    return jlineReader.readLine(prompt);
                }
            }
        }
    }

    // ==========================================================================================================================================================
    //
    // Runtime相关
    //
    // ==========================================================================================================================================================


    @Api("获取版本")
    public static String getAppVersion() {
        return APP_VERSION;
    }

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

    @Api("获取配置目录 - 不是插件私有目录")
    public static String getConfigFolder() {
        return FOLDER_CONFIG.getAbsolutePath();
    }

    @Api("获取数据目录 - 不是插件私有目录")
    public static String getModuleFolder() {
        return FOLDER_MODULE.getAbsolutePath();
    }

    @Api("获取日志目录 - 不是插件私有目录")
    public static String getLoggerFolder() {
        return FOLDER_LOGGER.getAbsolutePath();
    }

    @Api("获取模块实例")
    public static <T extends EventHandlerRunner> T getRunner(Class<T> clazz) {
        return systemd.getRunner(clazz);
    }

    @Api("提交定时任务")
    public static ScheduledFuture<?> schedule(Runnable runnable, long delay, TimeUnit unit) {
        return systemd.schedule(runnable, delay, unit);
    }

    @Api("提交定时任务")
    public static ScheduledFuture<?> scheduleAtFixedRate(Runnable runnable, long initialDelay, long period, TimeUnit unit) {
        return systemd.scheduleAtFixedRate(runnable, initialDelay, period, unit);
    }

    @Api("提交定时任务")
    public static ScheduledFuture<?> scheduleWithFixedDelay(Runnable runnable, long initialDelay, long delay, TimeUnit unit) {
        return systemd.scheduleWithFixedDelay(runnable, initialDelay, delay, unit);
    }

    // ==========================================================================================================================================================
    //
    // Bot相关
    //
    // ==========================================================================================================================================================


    @Api("按照配置的映射表获取ID")
    public static String getNickName(User user) {
        return systemd.getNickName(user);
    }

    @Api("按照配置的映射表获取ID")
    public static String getNickName(long user) {
        return systemd.getNickName(user);
    }

    @Api("按照配置的映射表获取ID")
    public static String getFormattedNickName(User user) {
        return getNickName(user) + "(" + user.getId() + ")";
    }

    @Api("按照配置的映射表获取ID")
    public static String getFormattedNickName(long user) {
        return getNickName(user) + "(" + user + ")";
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
