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
import net.mamoe.mirai.data.GroupActiveData;
import net.mamoe.mirai.data.GroupHonorListData;
import net.mamoe.mirai.data.GroupHonorType;
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
import java.nio.charset.StandardCharsets;
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
 * 项目地址 <url>https://github.com/Alceatraz/FurryBlack-Mirai</url>
 * 插件地址 <url>https://github.com/Alceatraz/FurryBlack-Mirai-Extensions</url>
 *
 * 个人主页 <url>https://www.blacktech.studio</url>
 *
 * @author Alceatraz Warprays @ BlackTechStudio
 */


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


    // ==========================================================================================================================================================
    //
    // 版本信息
    //
    // ==========================================================================================================================================================


    public static final String APP_VERSION = "2.0.4";


    // ==========================================================================================================================================================
    //
    // 系统信息
    //
    // ==========================================================================================================================================================


    @Api("系统启动时间") private static final long BOOT_TIME = System.currentTimeMillis();

    @Api("原始系统时区") public static final ZoneId SYSTEM_ZONEID;
    @Api("原始系统偏差") public static final ZoneOffset SYSTEM_OFFSET;


    static {

        System.setProperty("mirai.no-desktop", "");

        SYSTEM_ZONEID = ZoneId.systemDefault();
        SYSTEM_OFFSET = ZoneOffset.systemDefault().getRules().getOffset(LocalDateTime.now());

        Locale.setDefault(Locale.SIMPLIFIED_CHINESE);
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));

    }


    // ==========================================================================================================================================================
    //
    // 私有变量
    //
    // ==========================================================================================================================================================


    private static LoggerX logger;

    private static Systemd systemd;
    private static JLineConsole.CompleterDelegate completerDelegate;

    private static volatile boolean debug;

    private static volatile boolean enable;

    private static boolean unsafe;

    private static boolean noLogin;
    private static boolean noJline;

    private static boolean shutModeExit;
    private static boolean shutModeDrop;
    private static boolean shutBySignal = true;

    private static final AtomicReference<String> prompt = new AtomicReference<>("");

    private static Thread consoleThread;

    private static File FOLDER_ROOT;
    private static File FOLDER_CONFIG;
    private static File FOLDER_PLUGIN;
    private static File FOLDER_DEPEND;
    private static File FOLDER_MODULE;
    private static File FOLDER_LOGGER;


    // ==========================================================================================================================================================
    //
    // 启动入口
    //
    // ==========================================================================================================================================================


    public static void main(String[] args) {

        System.out.println("[FurryBlack][MAIN]FurryBlackPlus Mirai - ver " + APP_VERSION + " " + TimeTool.datetime(BOOT_TIME));

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


        logger.hint("环境检查完毕");

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
            logger.fatal("启动路由系统发生异常 终止启动", exception);
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
            consoleThread = new Thread(FurryBlack::console);
            consoleThread.setDaemon(true);
            consoleThread.start();
        }


        // =====================================================================

        logger.hint("系统启动完成 耗时" + TimeTool.duration(System.currentTimeMillis() - BOOT_TIME));
        if (!debug) {
            LoggerX.setLevel(level);
        }

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
            logger.fatal("关闭路由系统关闭异常", exception);
        }

        if (shutModeExit || shutModeDrop) {
            System.out.println("[FurryBlack][MAIN]FurryBlackPlus force exit, Bye.");
            System.exit(0);
        } else {
            System.out.println("[FurryBlack][MAIN]FurryBlackPlus closed, Bye.");
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


    // ==========================================================================================================================================================
    //
    // Mirai转发 - 为了系统安全Bot不允许直接获取 需要对Mirai的方法进行转发
    //
    // ==========================================================================================================================================================


    @Api("转发Mirai")
    public static List<ForwardMessage.Node> downloadForwardMessage(String resourceId) {
        return Mirai.getInstance().downloadForwardMessage(systemd.getBot(), resourceId);
    }

    @Api("转发Mirai")
    public static MessageChain downloadLongMessage(String resourceId) {
        return Mirai.getInstance().downloadLongMessage(systemd.getBot(), resourceId);
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
    public static List<OtherClientInfo> getOnlineOtherClientsList(boolean mayIncludeSelf) {
        return Mirai.getInstance().getOnlineOtherClientsList(systemd.getBot(), mayIncludeSelf);
    }

    @Api("转发Mirai")
    public static void recallMessage(MessageSource messageSource) {
        Mirai.getInstance().recallMessage(systemd.getBot(), messageSource);
    }

    @Api("转发Mirai")
    public static void sendNudge(Nudge nudge, Contact contact) {
        Mirai.getInstance().sendNudge(systemd.getBot(), nudge, contact);
    }

    @Api("转发Mirai")
    public static void getGroupVoiceDownloadUrl(byte[] md5, long groupId, long dstUin) {
        Mirai.getInstance().getGroupVoiceDownloadUrl(systemd.getBot(), md5, groupId, dstUin);
    }

    @Api("转发Mirai")
    public static void muteAnonymousMember(String anonymousId, String anonymousNick, long groupId, int seconds) {
        Mirai.getInstance().muteAnonymousMember(systemd.getBot(), anonymousId, anonymousNick, groupId, seconds);
    }

    @Api("转发Mirai")
    public static UserProfile getUserProfile(long user) {
        return Mirai.getInstance().queryProfile(systemd.getBot(), user);
    }

    @Api("转发Mirai")
    public static GroupActiveData getRawGroupActiveData(long groupId, int page) {
        return Mirai.getInstance().getRawGroupActiveData(systemd.getBot(), groupId, page);
    }

    @Api("转发Mirai")
    public static GroupHonorListData getRawGroupHonorListData(long groupId, GroupHonorType type) {
        return Mirai.getInstance().getRawGroupHonorListData(systemd.getBot(), groupId, type);
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
    public static Friend getRawGroupMemberList(FriendInfo friendInfo) {
        return Mirai.getInstance().newFriend(systemd.getBot(), friendInfo);
    }

    @Api("转发Mirai")
    public static Stranger getRawGroupMemberList(StrangerInfo strangerInfo) {
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


    // ==========================================================================================================================================================
    //
    // Bot相关
    //
    // ==========================================================================================================================================================


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


    // ==========================================================================================================================================================
    //
    // 控制台
    //
    // ==========================================================================================================================================================


    @SuppressWarnings("SwitchStatementWithTooFewBranches")
    private static void console() {

        completerDelegate = new JLineConsole.CompleterDelegate();
        completerDelegate.update();

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

                    case "color":

                        // @formatter:off

                        System.out.println(Color.RED +            "RED ------------ Quick brown fox jump over the lazy dog" + Color.RESET);
                        System.out.println(Color.GREEN +          "GREEN ---------- Quick brown fox jump over the lazy dog" + Color.RESET);
                        System.out.println(Color.YELLOW +         "YELLOW --------- Quick brown fox jump over the lazy dog" + Color.RESET);
                        System.out.println(Color.BLUE +           "BLUE ----------- Quick brown fox jump over the lazy dog" + Color.RESET);
                        System.out.println(Color.MAGENTA +        "MAGENTA -------- Quick brown fox jump over the lazy dog" + Color.RESET);
                        System.out.println(Color.CYAN +           "CYAN ----------- Quick brown fox jump over the lazy dog" + Color.RESET);
                        System.out.println(Color.BRIGHT_RED +     "BRIGHT_RED ----- Quick brown fox jump over the lazy dog" + Color.RESET);
                        System.out.println(Color.BRIGHT_GREEN +   "BRIGHT_GREEN --- Quick brown fox jump over the lazy dog" + Color.RESET);
                        System.out.println(Color.BRIGHT_YELLOW +  "BRIGHT_YELLOW -- Quick brown fox jump over the lazy dog" + Color.RESET);
                        System.out.println(Color.BRIGHT_BLUE +    "BRIGHT_BLUE ---- Quick brown fox jump over the lazy dog" + Color.RESET);
                        System.out.println(Color.BRIGHT_MAGENTA + "BRIGHT_MAGENTA - Quick brown fox jump over the lazy dog" + Color.RESET);
                        System.out.println(Color.BRIGHT_CYAN +    "BRIGHT_CYAN ---- Quick brown fox jump over the lazy dog" + Color.RESET);
                        System.out.println(Color.WHITE +          "WHITE ---------- Quick brown fox jump over the lazy dog" + Color.RESET);
                        System.out.println(Color.GRAY +           "GRAY ----------- Quick brown fox jump over the lazy dog" + Color.RESET);
                        System.out.println(Color.BRIGHT_BLACK +   "BRIGHT_BLACK --- Quick brown fox jump over the lazy dog" + Color.RESET);
                        System.out.println(Color.BRIGHT_WHITE +   "BRIGHT_WHITE --- Quick brown fox jump over the lazy dog" + Color.RESET);

                        // @formatter:on

                        break;

                    // =========================================================


                    case "info":

                        System.out.println("""
                            FurryBlack-Plus
                            A Mirai wrapper QQ-Bot framework make with love and \\uD83E\\uDDE6
                            Mr.Black is a housekeeper with a white bear furry visualize
                            Create by Alceatraz Warprays @ Black Tech Studio
                            https://www.blacktech.studio""");

                        break;


                    // =========================================================


                    case "debug":

                        switch (command.getParameterLength()) {

                            case 1:

                                switch (command.getParameterSegment(0)) {
                                    case "enable" -> {
                                        debug = true;
                                        System.out.println("DEBUG模式启动");
                                    }
                                    case "disable" -> {
                                        debug = false;
                                        System.out.println("DEBUG模式关闭");
                                    }
                                    default -> System.out.println(debug ? "DEBUG已开启" : "DEBUG已关闭");
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

                            // plugin
                            case 0:
                                for (Map.Entry<String, Plugin> pluginEntry : systemd.getAllPlugin()) {

                                    var pluginName = pluginEntry.getKey();
                                    var pluginItem = pluginEntry.getValue();

                                    System.out.println(Color.BRIGHT_CYAN + pluginName + " " + pluginItem.getModules().size() + Color.RESET);

                                    Map<Runner, Class<? extends EventHandlerRunner>> runnerClassMap = pluginItem.getRunnerClassMap();
                                    System.out.println(Color.GREEN + ">> Runner " + runnerClassMap.size() + Color.RESET);
                                    for (Map.Entry<Runner, Class<? extends EventHandlerRunner>> classEntry : runnerClassMap.entrySet()) {
                                        var moduleName = classEntry.getKey();
                                        var moduleItem = classEntry.getValue();
                                        System.out.println(moduleName.value() + '[' + moduleName.priority() + "] -> " + moduleItem.getName());
                                    }

                                    Map<Filter, Class<? extends EventHandlerFilter>> filterClassMap = pluginItem.getFilterClassMap();
                                    System.out.println(Color.GREEN + ">> Filter " + filterClassMap.size() + Color.RESET);
                                    for (Map.Entry<Filter, Class<? extends EventHandlerFilter>> classEntry : filterClassMap.entrySet()) {
                                        var moduleName = classEntry.getKey();
                                        var moduleItem = classEntry.getValue();
                                        System.out.println(moduleName.value() + '[' + moduleName.priority() + "] -> " + moduleItem.getName());
                                    }

                                    Map<Monitor, Class<? extends EventHandlerMonitor>> monitorClassMap = pluginItem.getMonitorClassMap();
                                    System.out.println(Color.GREEN + ">> Monitor " + monitorClassMap.size() + Color.RESET);
                                    for (Map.Entry<Monitor, Class<? extends EventHandlerMonitor>> classEntry : monitorClassMap.entrySet()) {
                                        var moduleName = classEntry.getKey();
                                        var moduleItem = classEntry.getValue();
                                        System.out.println(moduleName.value() + '[' + moduleName.priority() + "] -> " + moduleItem.getName());
                                    }

                                    Map<Checker, Class<? extends EventHandlerChecker>> checkerClassMap = pluginItem.getCheckerClassMap();
                                    System.out.println(Color.GREEN + ">> Checker " + checkerClassMap.size() + Color.RESET);
                                    for (Map.Entry<Checker, Class<? extends EventHandlerChecker>> classEntry : checkerClassMap.entrySet()) {
                                        var moduleName = classEntry.getKey();
                                        var moduleItem = classEntry.getValue();
                                        System.out.println(moduleName.value() + '[' + moduleName.priority() + "](" + moduleName.command() + ") -> " + moduleItem.getName());
                                    }

                                    Map<Executor, Class<? extends EventHandlerExecutor>> executorClassMap = pluginItem.getExecutorClassMap();
                                    System.out.println(Color.GREEN + ">> Executor " + executorClassMap.size() + Color.RESET);
                                    for (Map.Entry<Executor, Class<? extends EventHandlerExecutor>> classEntry : executorClassMap.entrySet()) {
                                        var moduleName = classEntry.getKey();
                                        var moduleItem = classEntry.getValue();
                                        System.out.println(moduleName.value() + '(' + moduleName.command() + ") -> " + moduleItem.getName());
                                    }

                                }
                                break;

                        }
                        break;


                    // =========================================================


                    // module
                    case "module":

                        switch (command.getParameterLength()) {

                            case 2:

                                switch (command.getParameterSegment(0)) {

                                    // module shut <plugin>
                                    case "shut" -> systemd.shutModule(command.getParameterSegment(1));


                                    // module init <plugin>
                                    case "init" -> systemd.initModule(command.getParameterSegment(1));


                                    // module boot <plugin>
                                    case "boot" -> systemd.bootModule(command.getParameterSegment(1));


                                    // module reboot <plugin>
                                    case "reboot" -> systemd.rebootModule(command.getParameterSegment(1));

                                }
                                break;


                            // module
                            case 0:

                                Map<Runner, Boolean> listAllRunner = systemd.listAllRunner();
                                System.out.println(Color.BRIGHT_CYAN + ">> 定时器 " + listAllRunner.size() + Color.RESET);
                                for (Map.Entry<Runner, Boolean> entry : listAllRunner.entrySet()) {
                                    System.out.println((entry.getValue() ? "√ " : "   ") + entry.getKey().value());
                                }

                                Map<Filter, Boolean> listAllFilter = systemd.listAllFilter();
                                System.out.println(Color.BRIGHT_CYAN + ">> 过滤器 " + listAllFilter.size() + Color.RESET);
                                for (Map.Entry<Filter, Boolean> entry : listAllFilter.entrySet()) {
                                    System.out.println((entry.getValue() ? "√ " : "   ") + entry.getKey().value());
                                }

                                Map<Monitor, Boolean> listAllMonitor = systemd.listAllMonitor();
                                System.out.println(Color.BRIGHT_CYAN + ">> 监听器 " + listAllMonitor.size() + Color.RESET);
                                for (Map.Entry<Monitor, Boolean> entry : listAllMonitor.entrySet()) {
                                    System.out.println((entry.getValue() ? "√ " : "   ") + entry.getKey().value());
                                }

                                Map<Checker, Boolean> listAllChecker = systemd.listAllChecker();
                                System.out.println(Color.BRIGHT_CYAN + ">> 检查器 " + listAllChecker.size() + Color.RESET);
                                for (Map.Entry<Checker, Boolean> entry : listAllChecker.entrySet()) {
                                    System.out.println((entry.getValue() ? "√ " : "   ") + entry.getKey().value() + "[" + entry.getKey().command() + "]");
                                }

                                Map<Executor, Boolean> listAllExecutor = systemd.listAllExecutor();
                                System.out.println(Color.BRIGHT_CYAN + ">> 执行器 " + listAllExecutor.size() + Color.RESET);
                                for (Map.Entry<Executor, Boolean> entry : listAllExecutor.entrySet()) {
                                    System.out.println((entry.getValue() ? "√ " : "   ") + entry.getKey().value() + "[" + entry.getKey().command() + "]{" + (entry.getKey().users() ? "U" : "") + (entry.getKey().group() ? "G" : "") + "}");
                                }

                                List<Checker> globalUsersChecker = systemd.listGlobalUsersChecker();
                                System.out.println(Color.BRIGHT_CYAN + ">> 全局私聊检查器 " + globalUsersChecker.size() + Color.RESET);
                                for (Checker annotation : globalUsersChecker) {
                                    System.out.println(annotation.value());
                                }

                                Map<String, List<Checker>> listCommandUsersChecker = systemd.listCommandUsersChecker();
                                System.out.println(Color.BRIGHT_CYAN + ">> 指定私聊检查器 " + listCommandUsersChecker.size() + Color.RESET);
                                for (Map.Entry<String, List<Checker>> entry : listCommandUsersChecker.entrySet()) {
                                    System.out.println(entry.getKey() + " " + entry.getValue().size());
                                    for (Checker item : entry.getValue()) {
                                        System.out.println("  " + item.value());
                                    }
                                }

                                List<Checker> globalGroupChecker = systemd.listGlobalGroupChecker();
                                System.out.println(Color.BRIGHT_CYAN + ">> 全局群聊检查器 " + globalGroupChecker.size() + Color.RESET);
                                for (Checker annotation : globalGroupChecker) {
                                    System.out.println("  " + annotation.value());
                                }

                                Map<String, List<Checker>> listCommandGroupChecker = systemd.listCommandGroupChecker();
                                System.out.println(Color.BRIGHT_CYAN + ">> 指定群聊检查器 " + listCommandGroupChecker.size() + Color.RESET);
                                for (Map.Entry<String, List<Checker>> entry : listCommandGroupChecker.entrySet()) {
                                    System.out.println(entry.getKey() + " " + entry.getValue().size());
                                    for (Checker item : entry.getValue()) {
                                        System.out.println("  " + item.value());
                                    }
                                }

                                System.out.println(Color.BRIGHT_CYAN + ">> 私聊命令列表" + Color.RESET);
                                System.out.println(systemd.getMessageListUsers());

                                System.out.println(Color.BRIGHT_CYAN + ">> 群聊命令列表" + Color.RESET);
                                System.out.println(systemd.getMessageListGroup());

                                break;
                        }
                        break;


                    // =========================================================


                    case "schema":
                        systemd.schemaVerbose();
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
                            if (LoggerX.setLevel(level)) {
                                logger.bypass("日志级别调整为 " + level);
                            } else {
                                logger.bypass("修改日志级别失败：不存在此级别，可用值为 MUTE FATAL ERROR WARN HINT SEEK INFO DEBUG VERBOSE DEVELOP");
                            }
                        } else {
                            logger.bypass("可用值为 MUTE ERROR WARN HINT SEEK INFO DEBUG VERBOSE ALL");
                            logger.fatal("[FATAL]致命 红色 FATAL");
                            logger.error("[ERROR]错误 亮黄 ERROR");
                            logger.warning("[WARN]警告 黄色 WARN");
                            logger.hint("[HINT]提示 青色 HINT");
                            logger.seek("[SEEK]配置 绿色 SEEK");
                            logger.info("[INFO]信息 白色 INFO");
                            logger.debug("[DEBG]调试 灰色 DEBUG");
                            logger.verbose("[VERB]详情 灰色 VERBOSE");
                        }
                        break;


                    // =========================================================


                    case "nickname":

                        if (!command.hasCommandBody()) continue console;

                        switch (command.getParameterSegment(0)) {

                            case "list":
                                System.out.println(Color.BRIGHT_CYAN + "全局昵称" + Color.RESET);
                                for (Map.Entry<Long, String> entry : systemd.getNicknameGlobal().entrySet()) {
                                    System.out.println(entry.getKey() + ":" + entry.getValue());
                                }
                                System.out.println(Color.BRIGHT_CYAN + "群内昵称" + Color.RESET);
                                for (Map.Entry<Long, Map<Long, String>> groupsEntry : systemd.getNicknameGroups().entrySet()) {
                                    System.out.println("> " + groupsEntry.getKey());
                                    for (Map.Entry<Long, String> nicknameEntry : groupsEntry.getValue().entrySet()) {
                                        System.out.println(nicknameEntry.getKey() + ":" + nicknameEntry.getValue());
                                    }
                                }
                                break;

                            case "clean":
                                systemd.cleanNickname();
                                break;

                            case "reload":
                                systemd.cleanNickname();

                            case "append":
                                systemd.appendNickname();
                                break;

                            case "export":
                                List<String> list = systemd.exportNickname();
                                File file = Paths.get(FOLDER_CONFIG.getAbsolutePath(), "export-nickname-" + TimeTool.format("yyyy-MM-dd HH-mm-ss") + ".txt").toFile();
                                //noinspection ResultOfMethodCallIgnored
                                file.createNewFile();
                                try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                                    for (String line : list) {
                                        byte[] bytes = line.getBytes(StandardCharsets.UTF_8);
                                        fileOutputStream.write(bytes);
                                        fileOutputStream.write("\n".getBytes(StandardCharsets.UTF_8));
                                    }
                                }
                                break;
                        }
                        break;


                    case "list":
                        if (!command.hasCommandBody()) continue console;
                        switch (command.getParameterSegment(0)) {
                            case "u", "usr", "user", "users", "f", "fri", "friend", "friends" -> {
                                List<Friend> friends = FurryBlack.getFriends().stream().filter(item -> item.getId() != systemd.getBotID()).collect(Collectors.toList());
                                if (friends.size() == 0) {
                                    System.out.println("你没有朋友");
                                    break;
                                }
                                friends.stream()
                                    .map(FurryBlack::getFormattedNickName)
                                    .forEach(System.out::println);
                            }
                            case "g", "grp", "group", "groups" -> {
                                ContactList<Group> groups = FurryBlack.getGroups();
                                if (groups.size() == 0) {
                                    System.out.println("你没有群组");
                                    break;
                                }
                                groups.stream()
                                    .map(item -> item.getName() + "(" + item.getId() + ") " + item.getMembers().size() + "人")
                                    .forEach(System.out::println);
                            }
                            default -> {
                                long group;
                                try {
                                    group = Long.parseLong(command.getParameterSegment(0));
                                } catch (Exception exception) {
                                    System.out.println("命令发生异常 省略group需要指定群号");
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
                                            default -> {
                                            }
                                        }
                                        System.out.println(builder);
                                    });
                            }
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


                    // =========================================================


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


    public interface Console {
        String readLine(String prompt);
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


    public static class JLineConsole implements Console {

        private final LineReader jlineReader;

        public JLineConsole() {
            this.jlineReader = LineReaderBuilder.builder().completer(completerDelegate).build();
            AutopairWidgets autopairWidgets = new AutopairWidgets(this.jlineReader);
            autopairWidgets.enable();
        }

        @Override
        public String readLine(String prompt) {
            return this.jlineReader.readLine(prompt);
        }

        public static class CompleterDelegate implements Completer {

            private Completer completer;

            @Override
            public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
                this.completer.complete(reader, line, candidates);
            }

            public void update() {
                this.completer = new AggregateCompleter(
                    new ArgumentCompleter(new StringsCompleter("help", "kill", "drop", "stop", "stat", "enable", "disable", "schema", "color")),
                    new ArgumentCompleter(new StringsCompleter("list", "send"), new StringsCompleter("users", "group")),
                    new TreeCompleter(node("level", node("MUTE", "ERROR", "WARN", "HINT", "SEEK", "INFO", "DEBUG", "VERBOSE", "ALL"))),
                    new TreeCompleter(node("nickname", node("list", "clean", "reload", "append", "export"))),
                    new TreeCompleter(node("debug", node("enable", "disable"))),
                    new TreeCompleter(node("plugin")),
                    new TreeCompleter(node("module",
                        node("init", "boot", "shut", "reboot",
                            node(new StringsCompleter(systemd.listAllModule().keySet()))
                        )
                    ))
                );
            }
        }

    }


    public static void printHelp() {

        System.out.println(

            // @formatter:off

            Color.BRIGHT_CYAN + "# FurryBlackPlus 启动参数 ===========================" + Color.RESET + "\n" +
            "--debug       使用DEBUG模式启动\n" +
            "--unsafe      允许一些危险的调用\n" +
            "--no-login    使用离线模式，仅用于基础调试，功能基本都不可用\n" +
            "--no-console  不使用控制台，唯一正常关闭方式是使用进程信号\n" +
            "--no-jline    不使用jline控制台，使用BufferedReader\n" +
            "--force-exit  关闭流程执行后，强制结束System.exit(0)\n" +

            Color.BRIGHT_CYAN + "# FurryBlackPlus 系统参数 ===========================" + Color.RESET + "\n" +
            "furryblack.logger.level 日志等级\n" +

            Color.BRIGHT_CYAN + "# FurryBlackPlus 控制台  ===========================" + Color.RESET + "\n" +
            Color.RED + "⚠ 控制台任何操作都属于底层操作可以直接对框架进行不安全和非法的操作" + Color.RESET + "\n" +
            "安全：设计如此，不会导致异常或者不可预测的结果\n" +
            "风险：功能设计上是安全操作，但是具体被操作对象可能导致错误\n" +
            "危险：没有安全性检查的操作，可能会让功能严重异常导致被迫重启或损坏模块的数据存档\n" +
            "高危：后果完全未知的危险操作，或者正常流程中不应该如此操作但是控制台仍然可以强制执行\n" +

            Color.GREEN + "# 系统管理 ==========================================" + Color.RESET + "\n" +
            "level (安全) 修改控制台日志打印等级，日志不受影响(可能导致漏掉ERR/WARN信息)\n" +
            "stat  (安全) 查看性能状态\n" +
            "stop  (安全) 正常退出，完整执行关闭流程，等待模块结束，等待线程池结束，等待所有线程\n" +
            "drop  (高危) 强制退出，不等待插件关闭完成，不等待线程池结束，且最终杀死JVM\n" +
            "kill  (高危) 命令执行后直接杀死JVM，不会进行任何关闭操作\n" +

            Color.GREEN + "# 功能管理 ==========================================" + Color.RESET + "\n" +
            "enable  (安全) 启用消息事件处理 正常响应消息\n" +
            "disable (安全) 停用消息事件处理 无视任何消息\n" +

            Color.GREEN + "# 好友相关 ==========================================" + Color.RESET + "\n" +
            "list users   (安全) 列出好友\n" +
            "list group   (安全) 列出群组\n" +
            "list <group> (安全) 列出成员\n" +

            Color.GREEN + "# 昵称相关 ==========================================" + Color.RESET + "\n" +
            "nickname list (安全) 列出昵称\n" +
            "nickname clean (安全) 清空昵称\n" +
            "nickname append (安全) 加载且合并昵称\n" +
            "nickname reload (安全) 清空且加载昵称\n" +

            Color.GREEN + "# 发送消息 ==========================================" + Color.RESET + "\n" +
            "send users <users> <消息>  (安全) 向好友发送消息\n" +
            "send group <group> <消息>  (安全) 向群聊发送消息\n" +
            "send <group> <user> <消息> (安全) 向群聊发送AT消息\n" +

            Color.GREEN + "# 模型管理 ==========================================" + Color.RESET + "\n" +
            "schema (安全) 详细显示插件和模块\n" +

            Color.GREEN + "# 插件管理 ==========================================" + Color.RESET + "\n" +
            "plugin (安全) 列出插件\n" +

            Color.GREEN + "# 模块管理 ==========================================" + Color.RESET + "\n" +
            "module (安全) 列出模块\n" +
            Color.GREEN + "※ Runner可能会被依赖，底层操作框架不检查依赖，有可能导致关联模块崩溃" + Color.RESET + "\n" +
            "module reboot <名称> (风险) 重启指定模块(执行 shut + init + boot)\n" +
            "module shut   <名称> (风险) 关闭指定模块(执行 shut)\n" +
            "module init   <名称> (风险) 预载指定模块(执行 init)\n" +
            "module shut   <名称> (风险) 启动指定模块(执行 boot)\n" +

            Color.GREEN + "# 调试功能 ==========================================" + Color.RESET + "\n" +
            "debug [enable|disable] (风险) DEBUG开关，打印DEBUG输出和控制某些功能，插件如果不遵守标准开发可能会导致崩溃\n"

            // @formatter:on

        );
    }
}
