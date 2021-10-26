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

package studio.blacktech.furryblackplus.core;


import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.Mirai;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.ContactList;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.contact.Stranger;
import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.Listener;
import net.mamoe.mirai.event.events.BotInvitedJoinGroupRequestEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MemberJoinEvent;
import net.mamoe.mirai.event.events.MemberLeaveEvent;
import net.mamoe.mirai.event.events.NewFriendRequestEvent;
import net.mamoe.mirai.event.events.UserMessageEvent;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.utils.BotConfiguration;
import studio.blacktech.furryblackplus.FurryBlack;
import studio.blacktech.furryblackplus.common.Api;
import studio.blacktech.furryblackplus.core.common.digest.SHA256;
import studio.blacktech.furryblackplus.core.common.exception.moduels.boot.BootException;
import studio.blacktech.furryblackplus.core.common.exception.moduels.load.FirstBootException;
import studio.blacktech.furryblackplus.core.common.exception.moduels.load.MisConfigException;
import studio.blacktech.furryblackplus.core.common.logger.LoggerXFactory;
import studio.blacktech.furryblackplus.core.common.logger.base.LoggerX;
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
import studio.blacktech.furryblackplus.core.handler.common.BasicModuleUtilities;
import studio.blacktech.furryblackplus.core.handler.common.Command;
import studio.blacktech.furryblackplus.core.schema.Plugin;
import studio.blacktech.furryblackplus.core.schema.Schema;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;


@Api("系统核心路由")
public final class Systemd extends BasicModuleUtilities {


    // ==========================================================================================================================================================
    //
    // 配置项名称
    //
    // ==========================================================================================================================================================


    private static final String CONF_ACCOUNT_ID = "account.id";
    private static final String CONF_ACCOUNT_PW = "account.pw";

    private static final String CONF_THREADS_MONITOR = "threads.monitor";
    private static final String CONF_THREADS_SCHEDULE = "threads.schedule";

    private static final String CONF_BOT_DEVICE_TYPE = "bot.device.type";
    private static final String CONF_BOT_DEVICE_INFO = "bot.device.info";

    private static final String CONF_BOT_COMMAND_PREFIX = "bot.command.prefix";

    private static final String CONF_NET_HEARTBEAT_PERIOD = "net.heartbeat.period";
    private static final String CONF_NET_HEARTBEAT_TIMEOUT = "net.heartbeat.timeout";

    private static final String CONF_NET_RECONNECT_RETRY = "net.reconnect.retry";


    // @formatter:off


    private static final String DEFAULT_CONFIG =

    "# =====================================\n" +
    "# 账号设置\n" +
    "# =====================================\n" +
    "# 填写QQ账号(必填)\n" +
    CONF_ACCOUNT_ID + "=00000000\n" +
    "# 填写QQ密码(必填)\n" +
    CONF_ACCOUNT_PW + "=0000000\n" +
    "# =====================================\n" +
    "# 功能设置\n" +
    "# =====================================\n" +
    "# 监听器线程池\n" +
    CONF_THREADS_MONITOR+ "=4\n"+
    "# 异步任务线程池\n" +
    CONF_THREADS_SCHEDULE+ "=4\n"+
    "# 命令识别前缀\n" +
    CONF_BOT_COMMAND_PREFIX + "=\"/\"\n" +
    "# =====================================\n" +
    "# 设备设置\n" +
    "# =====================================\n" +
    "# 设备类型 PAD/PHONE/WATCH\n" +
    CONF_BOT_DEVICE_TYPE + "=PHONE\n" +
    "# 设备信息文件\n" +
    CONF_BOT_DEVICE_INFO + "=device.info\n" +
    "# =====================================\n" +
    "# 网络设置\n" +
    "# =====================================\n" +
    "# 心跳周期\n" +
    CONF_NET_HEARTBEAT_PERIOD + "=60000\n" +
    "# 心跳超时\n" +
    CONF_NET_HEARTBEAT_TIMEOUT + "=5000\n" +
    "# 重连次数\n" +
    CONF_NET_RECONNECT_RETRY + "=10\n"


    ;


    // @formatter:on


    // ==========================================================================================================================================================
    //
    // 私有对象
    //
    // ==========================================================================================================================================================


    private final Lock lock = new ReentrantLock();
    private final Condition condition = this.lock.newCondition();

    private final File FOLDER_CONFIG;
    private final File FOLDER_PLUGIN;

    private static volatile boolean INSTANCE_LOCK;


    //


    private Bot bot;

    private char COMMAND_PREFIX = '/';
    private Pattern COMMAND_PATTERN;

    private Map<Long, String> NICKNAME_GLOBAL;
    private Map<Long, Map<Long, String>> NICKNAME_GROUPS;

    private String MESSAGE_INFO;
    private String MESSAGE_EULA;
    private String MESSAGE_HELP;
    private String MESSAGE_LIST_USERS;
    private String MESSAGE_LIST_GROUP;

    private ThreadPoolExecutor MONITOR_PROCESS;
    private ScheduledThreadPoolExecutor EXECUTOR_SERVICE;

    private Schema schema;

    private Listener<UserMessageEvent> userMessageEventListener;
    private Listener<GroupMessageEvent> groupMessageEventListener;
    private Listener<MemberJoinEvent> memberJoinEventListener;
    private Listener<MemberLeaveEvent> memberLeaveEventListener;
    private Listener<NewFriendRequestEvent> newFriendRequestEventListener;
    private Listener<BotInvitedJoinGroupRequestEvent> botInvitedJoinGroupRequestEventListener;


    // ==========================================================================================================================
    // 对象控制


    public Systemd(File folderConfig, File folderPlugin) {
        synchronized (Systemd.class) {
            if (INSTANCE_LOCK) {
                BootException bootException = new BootException("Systemd init-lock is on, Invoker stack trace append as suppressed");
                RuntimeException runtimeException = new RuntimeException();
                StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
                runtimeException.setStackTrace(stackTrace);
                bootException.addSuppressed(runtimeException);
                throw bootException;
            }
            INSTANCE_LOCK = true;
            this.FOLDER_CONFIG = folderConfig;
            this.FOLDER_PLUGIN = folderPlugin;
        }
    }


    // ==========================================================================================================================================================
    //
    // 初始化
    //
    // ==========================================================================================================================================================


    public void boot() throws BootException {


        this.logger.hint("启动核心配置");


        // ==========================================================================================================================
        // 初始化配置文件


        this.logger.info("检查配置文件");


        File FILE_CONFIG = Paths.get(this.FOLDER_CONFIG.getAbsolutePath(), "application.properties").toFile();


        if (!FILE_CONFIG.exists()) {
            try {
                //noinspection ResultOfMethodCallIgnored
                FILE_CONFIG.createNewFile();
            } catch (IOException exception) {
                throw new FirstBootException("配置文件创建失败 -> " + FILE_CONFIG.getAbsolutePath(), exception);
            }

            if (!FILE_CONFIG.canWrite()) throw new FirstBootException("配置文件无权写入 -> " + FILE_CONFIG.getAbsolutePath());

            try (FileWriter writer = new FileWriter(FILE_CONFIG, StandardCharsets.UTF_8, false)) {
                writer.write(DEFAULT_CONFIG);
                writer.flush();
            } catch (IOException exception) {
                throw new FirstBootException("默认配置文件写入失败 -> " + FILE_CONFIG.getAbsolutePath(), exception);
            }

            this.logger.warning("检测到初次启动 需要填写必要的配置 即将关闭");
            throw new FirstBootException("检测到初次启动 需要填写必要的配置 -> " + FILE_CONFIG.getAbsolutePath());
        }

        if (!FILE_CONFIG.isFile()) throw new BootException("配置文件不是文件 -> " + FILE_CONFIG.getAbsolutePath());
        if (!FILE_CONFIG.canRead()) throw new BootException("配置文件无权读取 -> " + FILE_CONFIG.getAbsolutePath());


        // ==========================================================================================================================
        // 加载配置

        this.logger.info("加载配置文件");


        Properties config = new Properties();

        try (FileInputStream inStream = new FileInputStream(FILE_CONFIG)) {
            config.load(inStream);
        } catch (IOException exception) {
            this.logger.error("核心配置文件读取错误 即将关闭 " + FILE_CONFIG.getAbsolutePath());
            throw new BootException("核心配置文件读取错误 " + FILE_CONFIG.getAbsolutePath(), exception);
        }


        // ==========================================================================================================================


        this.logger.hint("配置核心系统");


        // ==========================================================================================================================
        // 读取配置

        this.logger.info("加载命令前缀配置");


        String prefix = config.getProperty(CONF_BOT_COMMAND_PREFIX);

        if (prefix == null || prefix.isEmpty() || prefix.isBlank() || prefix.length() != 1) {
            this.logger.warning("指定的命令前缀不可用 将自动设置为默认值: /");
        } else {
            this.COMMAND_PREFIX = prefix.charAt(0);
        }

        String regex = "^" + this.COMMAND_PREFIX + "[a-zA-Z0-9]{2,16}";

        this.logger.seek("识别前缀 " + this.COMMAND_PREFIX);
        this.logger.info("识别正则 " + regex);

        this.COMMAND_PATTERN = Pattern.compile(regex);

        // ==========================================================================================================================
        // 读取模板


        this.logger.hint("加载内置消息");

        File FILE_EULA = Paths.get(FurryBlack.getConfigFolder(), "message_eula.txt").toFile();
        File FILE_INFO = Paths.get(FurryBlack.getConfigFolder(), "message_info.txt").toFile();
        File FILE_HELP = Paths.get(FurryBlack.getConfigFolder(), "message_help.txt").toFile();

        this.logger.info("加载eula");
        this.MESSAGE_EULA = this.readFileContent(FILE_EULA);

        this.logger.info("加载info");
        this.MESSAGE_INFO = this.readFileContent(FILE_INFO);

        this.logger.info("加载help");
        this.MESSAGE_HELP = this.readFileContent(FILE_HELP);

        this.MESSAGE_EULA = this.MESSAGE_EULA.replaceAll("\\$\\{VERSION}", FurryBlack.APP_VERSION);
        this.MESSAGE_INFO = this.MESSAGE_INFO.replaceAll("\\$\\{VERSION}", FurryBlack.APP_VERSION);
        this.MESSAGE_HELP = this.MESSAGE_HELP.replaceAll("\\$\\{VERSION}", FurryBlack.APP_VERSION);

        String SHA_EULA = SHA256.getInstance().digest(this.MESSAGE_EULA);
        String SHA_INFO = SHA256.getInstance().digest(this.MESSAGE_INFO);

        this.MESSAGE_EULA = this.MESSAGE_EULA + "\r\nSHA-256: " + SHA_EULA;
        this.MESSAGE_INFO = this.MESSAGE_INFO + "\r\nSHA-256: " + SHA_INFO;

        this.logger.seek("签名EULA -> " + SHA_EULA);
        this.logger.seek("签名INFO -> " + SHA_INFO);


        // ==========================================================================================================================
        // 加载常用昵称


        this.logger.hint("加载常用昵称");

        this.NICKNAME_GLOBAL = new ConcurrentHashMap<>();
        this.NICKNAME_GROUPS = new ConcurrentHashMap<>();

        this.appendNickname();


        // ==========================================================================================================================
        // 读取机器人配置


        this.logger.hint("加载机器人配置");

        BotConfiguration configuration = new BotConfiguration();

        File cacheFolder = Paths.get(this.FOLDER_CONFIG.getAbsolutePath(), "cache").toFile();

        configuration.setCacheDir(cacheFolder);
        configuration.enableContactCache();


        // ==========================================================================================================================
        // 读取账号配置


        String accountConfig = config.getProperty(CONF_ACCOUNT_ID);
        long ACCOUNT_QQ = this.parseLong(accountConfig);

        this.logger.seek("QQ账号 " + ACCOUNT_QQ);

        String ACCOUNT_PW = config.getProperty(CONF_ACCOUNT_PW);

        int length = ACCOUNT_PW.length();

        if (ACCOUNT_PW.charAt(0) == '\"' && ACCOUNT_PW.charAt(length - 1) == '\"') {
            ACCOUNT_PW = ACCOUNT_PW.substring(1, length - 1);
        }

        length = accountConfig.length();

        if (FurryBlack.isDebug()) {
            this.logger.seek("QQ密码 " + ACCOUNT_PW);
            this.logger.warning("！！！！！！！！！！！！！！！！！！");
            this.logger.warning("！调试模式开启时会在日志中记录密码！");
            this.logger.warning("！关闭调试模式以给日志密码信息打码!");
            this.logger.warning("！！！！！！！！！！！！！！！！！！");
        } else {
            String shadow_ACCOUNT_PW = ACCOUNT_PW.charAt(0) + "*".repeat(length - 1);
            this.logger.seek("QQ密码 " + shadow_ACCOUNT_PW);
        }

        // ==========================================================================================================================
        // 读取设备配置


        // 设备类型


        String DEVICE_TYPE = config.getProperty(CONF_BOT_DEVICE_TYPE);


        switch (DEVICE_TYPE) {
            case "PAD", "537062409" -> {
                configuration.setProtocol(BotConfiguration.MiraiProtocol.ANDROID_PAD);
                this.logger.seek("设备模式 " + DEVICE_TYPE + " 安卓平板");
            }
            case "PHONE", "537062845" -> {
                configuration.setProtocol(BotConfiguration.MiraiProtocol.ANDROID_PHONE);
                this.logger.seek("设备模式 " + DEVICE_TYPE + " 安卓手机");
            }
            case "WATCH", "537061176" -> {
                configuration.setProtocol(BotConfiguration.MiraiProtocol.ANDROID_WATCH);
                this.logger.seek("设备模式 " + DEVICE_TYPE + " 安卓手表");
            }
            default -> {
                this.logger.error("设备模式配置错误");
                throw new MisConfigException(CONF_BOT_DEVICE_TYPE + "必须是填 PAD PHONE WATCH 之一 大写无符号");
            }
        }


        // 设备信息


        String DEVICE_INFO = config.getProperty(CONF_BOT_DEVICE_INFO);

        File deviceInfo = Paths.get(FurryBlack.getConfigFolder(), DEVICE_INFO).toFile();

        if (deviceInfo.exists()) {

            if (!deviceInfo.isFile()) {
                String temp = "设备信息配置错误 指定的路径不是文件 " + deviceInfo.getAbsolutePath();
                this.logger.error(temp);
                throw new MisConfigException(temp);
            }

            if (!deviceInfo.canRead()) {
                String temp = "设备信息配置错误 指定的文件无权读取 " + deviceInfo.getAbsolutePath();
                this.logger.error(temp);
                throw new MisConfigException(temp);
            }

            this.logger.seek("设备信息 " + deviceInfo.getName());

        } else {

            this.logger.seek("设备信息不存在 将由Mirai生成");

        }


        configuration.fileBasedDeviceInfo(deviceInfo.getAbsolutePath());


        // ==========================================================================================================================
        // 读取网络配置


        // 心跳参数

        Long NET_HEARTBEAT_PERIOD = this.parseLongOrNull(config.getProperty(CONF_NET_HEARTBEAT_PERIOD));
        if (NET_HEARTBEAT_PERIOD != null) {
            this.logger.seek("心跳间隔 " + NET_HEARTBEAT_PERIOD);
            configuration.setHeartbeatPeriodMillis(NET_HEARTBEAT_PERIOD);
        }

        Long NET_HEARTBEAT_TIMEOUT = this.parseLongOrNull(config.getProperty(CONF_NET_HEARTBEAT_TIMEOUT));
        if (NET_HEARTBEAT_TIMEOUT != null) {
            this.logger.seek("心跳超时 " + NET_HEARTBEAT_TIMEOUT);
            configuration.setHeartbeatTimeoutMillis(NET_HEARTBEAT_TIMEOUT);
        }


        // 重连参数

        Integer NET_RECONNECT_RETRY = this.parseIntegerOrNull(config.getProperty(CONF_NET_RECONNECT_RETRY));
        if (NET_RECONNECT_RETRY != null) {
            this.logger.seek("重连次数 " + NET_RECONNECT_RETRY);
            configuration.setReconnectionRetryTimes(NET_RECONNECT_RETRY);
        }


        // 传入日志

        configuration.setBotLoggerSupplier(i -> LoggerXFactory.newLogger("MiraiBot"));
        configuration.setNetworkLoggerSupplier(i -> LoggerXFactory.newLogger("MiraiNet"));


        // ==========================================================================================================================
        // 创建机器人


        this.logger.hint("创建机器人");
        this.bot = BotFactory.INSTANCE.newBot(ACCOUNT_QQ, ACCOUNT_PW, configuration);

        this.logger.info("机器人类型 " + this.bot.getClass().getName());


        // ==========================================================================================================================
        //
        // 插件功能
        //
        // ==========================================================================================================================


        this.schema = new Schema(this.FOLDER_PLUGIN);


        // ==========================================================================================================================
        // 扫描插件


        this.schema.find();


        // ==========================================================================================================================
        // 扫描模块


        this.schema.scan();


        // ==========================================================================================================================
        // 注册模块


        this.schema.load();


        // ==========================================================================================================================
        // 创建模块


        this.schema.make();


        // ==========================================================================================================================
        // 注册完成


        this.logger.hint("生成模板消息");


        // ==========================================================================================================================
        // 执行初始化方法


        this.schema.init();


        // =============================================================================================================
        // 注册事件监听


        this.logger.hint("注册机器人事件监听");


        this.userMessageEventListener = GlobalEventChannel.INSTANCE.subscribeAlways(UserMessageEvent.class, this::handleUsersMessage);
        this.groupMessageEventListener = GlobalEventChannel.INSTANCE.subscribeAlways(GroupMessageEvent.class, this::handleGroupMessage);


        this.newFriendRequestEventListener = GlobalEventChannel.INSTANCE.subscribeAlways(NewFriendRequestEvent.class, event -> {
            this.logger.hint("BOT被添加好友 " + event.getFromNick() + "(" + event.getFromId() + ")");
            event.accept();
        });

        this.botInvitedJoinGroupRequestEventListener = GlobalEventChannel.INSTANCE.subscribeAlways(BotInvitedJoinGroupRequestEvent.class, event -> {
            this.logger.hint("BOT被邀请入群 " + event.getGroupName() + "(" + event.getGroupId() + ") 邀请人 " + event.getInvitorNick() + "(" + event.getInvitorId() + ")");
            event.accept();
        });


        this.memberJoinEventListener = GlobalEventChannel.INSTANCE.subscribeAlways(MemberJoinEvent.class, event -> {
            String user = event.getUser().getNick() + "(" + event.getUser().getId() + ")";
            if (event instanceof MemberJoinEvent.Active) {
                this.logger.hint("用户申请加群 " + user + " → " + event.getGroup().getName() + "(" + event.getGroupId() + ")");
            } else if (event instanceof MemberJoinEvent.Invite) {
                this.logger.hint("用户受邀进群 " + user + " → " + event.getGroup().getName() + "(" + event.getGroupId() + ")");
            }
        });

        this.memberLeaveEventListener = GlobalEventChannel.INSTANCE.subscribeAlways(MemberLeaveEvent.class, event -> {
            String user = event.getUser().getNick() + "(" + event.getUser().getId() + ")";
            if (event instanceof MemberLeaveEvent.Quit) {
                this.logger.hint("用户主动退群 " + user + " → " + event.getGroup().getName() + "(" + event.getGroupId() + ")");
            } else if (event instanceof MemberLeaveEvent.Kick) {
                this.logger.hint("用户被踢出群 " + user + " → " + event.getGroup().getName() + "(" + event.getGroupId() + ")");
            }
        });


        // ==========================================================================================================================
        // 登录QQ


        if (FurryBlack.isNoLogin()) {
            this.logger.warning("指定了--no-login参数 跳过登录");
        } else {
            this.logger.hint("登录");
            this.bot.login();
        }


        // ==========================================================================================================================
        // 启动线程池


        int monitorPoolSize = this.parseInteger(config.getProperty(CONF_THREADS_SCHEDULE));
        this.logger.seek("监听线程池配置 " + monitorPoolSize);

        this.MONITOR_PROCESS = new ThreadPoolExecutor(monitorPoolSize, monitorPoolSize, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());

        //

        int schedulePoolSize = this.parseInteger(config.getProperty(CONF_THREADS_SCHEDULE));
        this.logger.seek("异步线程池配置 " + schedulePoolSize);

        this.EXECUTOR_SERVICE = new ScheduledThreadPoolExecutor(schedulePoolSize);


        // ==========================================================================================================================
        // 启动模块


        this.schema.boot();


        // ==========================================================================================================================
        // 列出所有好友和群组


        if (!FurryBlack.isNoLogin()) {

            this.logger.seek("机器人账号 " + this.bot.getId());
            this.logger.seek("机器人昵称 " + this.bot.getNick());
            this.logger.seek("机器人头像 " + this.bot.getAvatarUrl());

            this.logger.hint("所有好友");
            this.bot.getFriends().forEach(item -> this.logger.info(FurryBlack.getFormattedNickName(item)));

            this.logger.hint("所有群组");
            this.bot.getGroups().forEach(item -> this.logger.info(FurryBlack.getGroupInfo(item)));

        }


        // ==========================================================================================================================
        // 生成模板消息


        this.generateListMessage();


    }


    // ==========================================================================================================================================================
    //
    // 关闭
    //
    // ==========================================================================================================================================================


    public void shut() {

        // ==========================================================================================================================
        // 关闭监听

        this.logger.hint("结束监听通道");

        this.logger.info("结束私聊监听通道");
        this.userMessageEventListener.complete();

        this.logger.info("结束群聊监听通道");
        this.groupMessageEventListener.complete();

        this.logger.info("结束成员进群监听通道");
        this.memberJoinEventListener.complete();

        this.logger.info("结束成员离群监听通道");
        this.memberLeaveEventListener.complete();

        this.logger.info("结束好友添加监听通道");
        this.newFriendRequestEventListener.complete();

        this.logger.info("结束邀请加群监听通道");
        this.botInvitedJoinGroupRequestEventListener.complete();


        // ==========================================================================================================================
        // 关闭模块

        try {
            this.schema.shut();
        } catch (Exception exception) {
            this.logger.error("关闭插件模型发生异常", exception);
        }

        // ==========================================================================================================================
        // 关闭线程池


        this.logger.hint("关闭线程池");


        // =====================================================================


        if (FurryBlack.isShutModeDrop()) {
            this.logger.warning("丢弃监听任务线程池");
            Thread thread = new Thread(() -> this.MONITOR_PROCESS.shutdownNow());
            thread.setDaemon(true);
            thread.start();
        } else {
            this.logger.info("关闭监听任务线程池");
            this.MONITOR_PROCESS.shutdown();
            try {
                //noinspection ResultOfMethodCallIgnored
                this.MONITOR_PROCESS.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            } catch (InterruptedException exception) {
                this.logger.error("等待关闭监听任务线程池被中断", exception);
            }
            this.logger.info("监听任务线程池关闭");
        }


        // =====================================================================


        if (FurryBlack.isShutModeDrop()) {
            this.logger.warning("丢弃定时任务线程池");
            Thread thread = new Thread(() -> this.EXECUTOR_SERVICE.shutdownNow());
            thread.setDaemon(true);
            thread.start();
        } else {
            this.logger.info("关闭定时任务线程池");
            this.EXECUTOR_SERVICE.shutdown();
            try {
                //noinspection ResultOfMethodCallIgnored
                this.EXECUTOR_SERVICE.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            } catch (InterruptedException exception) {
                this.logger.error("等待关闭定时任务线程池被中断", exception);
            }
            this.logger.info("定时任务线程池关闭");
        }


        // ==========================================================================================================================
        // 关闭Mirai-Bot


        this.logger.hint("关闭机器人");


        // =====================================================================


        this.logger.info("通知机器人关闭");

        if (FurryBlack.isNoLogin()) {
            this.logger.warning("调试模式 不需要关闭机器人");
        } else {
            if (FurryBlack.isShutModeDrop()) {
                this.bot.close(null);
            } else {
                this.bot.closeAndJoin(null);
            }
        }

        this.logger.info("机器人已关闭");

    }


    // ==========================================================================================================================================================
    //
    // 监听器
    //
    // ==========================================================================================================================================================


    private void handleUsersMessage(UserMessageEvent event) {

        if (!FurryBlack.isEnable()) return;

        try {

            for (EventHandlerFilter eventHandlerFilter : this.schema.getFilterUsersChain()) {
                if (eventHandlerFilter.handleUsersMessageWrapper(event)) return;
            }

            this.MONITOR_PROCESS.submit(() -> {
                for (EventHandlerMonitor item : this.schema.getMonitorUsersChain()) {
                    item.handleUsersMessageWrapper(event);
                }
            });

            String content = event.getMessage().contentToString();

            if (this.isCommand(content)) {

                Command command = new Command(content.substring(1));

                String commandName = command.getCommandName();

                switch (commandName) {

                    case "help" -> {
                        if (command.hasCommandBody()) {
                            String segment = command.getParameterSegment(0);
                            EventHandlerExecutor executor = this.schema.getExecutorUsersPool().get(segment);
                            if (executor == null) {
                                FurryBlack.sendMessage(event, "没有此命令");
                            } else {
                                FurryBlack.sendMessage(event, executor.getHelp());
                            }
                        } else {
                            FurryBlack.sendMessage(event, this.MESSAGE_HELP);
                        }
                    }

                    case "list" -> FurryBlack.sendMessage(event, this.MESSAGE_LIST_USERS);
                    case "info" -> FurryBlack.sendMessage(event, this.MESSAGE_INFO);
                    case "eula" -> FurryBlack.sendMessage(event, this.MESSAGE_EULA);

                    default -> {
                        EventHandlerExecutor executor = this.schema.getExecutorUsersPool().get(commandName);
                        if (executor == null) {
                            FurryBlack.sendMessage(event, "没有此命令");
                            return;
                        }
                        for (EventHandlerChecker checker : this.schema.getGlobalCheckerUsersPool()) {
                            if (checker.handleUsersMessageWrapper(event, command)) return;
                        }
                        List<EventHandlerChecker> commandCheckerUsersPool = this.schema.getCommandCheckerUsersPool(commandName);
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
            this.logger.warning(LoggerX.dumpMessage(event, exception));
        }
    }


    public void handleGroupMessage(GroupMessageEvent event) {

        if (!FurryBlack.isEnable()) return;

        try {

            for (EventHandlerFilter eventHandlerFilter : this.schema.getFilterGroupChain()) {
                if (eventHandlerFilter.handleGroupMessageWrapper(event)) return;
            }

            this.MONITOR_PROCESS.submit(() -> {
                for (EventHandlerMonitor item : this.schema.getMonitorGroupChain()) {
                    item.handleGroupMessageWrapper(event);
                }
            });

            String content = event.getMessage().contentToString();

            if (this.isCommand(content)) {

                Command command = new Command(content.substring(1));

                String commandName = command.getCommandName();

                switch (commandName) {

                    case "help" -> {
                        if (command.hasCommandBody()) {
                            String segment = command.getParameterSegment(0);
                            EventHandlerExecutor executor = this.schema.getExecutorGroupPool().get(segment);
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
                                event.getSender().sendMessage(this.MESSAGE_HELP);
                            } catch (Exception exception) {
                                FurryBlack.sendMessage(event, "帮助信息发送至私聊失败 请允许临时会话权限");
                            }
                        }
                    }

                    case "list" -> {
                        try {
                            event.getSender().sendMessage(this.MESSAGE_LIST_GROUP);
                        } catch (Exception exception) {
                            FurryBlack.sendMessage(event, "可用命令发送至私聊失败 请允许临时会话权限");
                        }
                    }

                    case "info" -> {
                        try {
                            event.getSender().sendMessage(this.MESSAGE_INFO);
                        } catch (Exception exception) {
                            FurryBlack.sendMessage(event, "关于发送至私聊失败 请允许临时会话权限");
                        }
                    }

                    case "eula" -> {
                        try {
                            event.getSender().sendMessage(this.MESSAGE_EULA);
                        } catch (Exception exception) {
                            FurryBlack.sendMessage(event, "EULA发送至私聊失败 请允许临时会话权限");
                        }
                    }

                    default -> {
                        EventHandlerExecutor executor = this.schema.getExecutorGroupPool().get(commandName);
                        if (executor == null) {
                            return;
                        }
                        for (EventHandlerChecker checker : this.schema.getGlobalCheckerGroupPool()) {
                            if (checker.handleGroupMessageWrapper(event, command)) return;
                        }
                        List<EventHandlerChecker> commandCheckerGroupPool = this.schema.getCommandCheckerGroupPool(commandName);
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
            this.logger.warning(LoggerX.dumpMessage(event, exception));
        }

    }


    // ==========================================================================================================================================================
    //
    // 工具
    //
    // ==========================================================================================================================================================


    public void await() {
        try {
            this.lock.lock();
            try {
                this.condition.await();
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }
        } finally {
            this.lock.unlock();
        }
    }


    public void signal() {
        try {
            this.lock.lock();
            this.condition.signal();
        } finally {
            this.lock.unlock();
        }
    }


    private boolean isCommand(String content) {
        if (content.length() < 3) return false;
        if (content.charAt(0) != this.COMMAND_PREFIX) return false;
        return this.COMMAND_PATTERN.matcher(content).find();
    }


    public void generateListMessage() {
        this.logger.info("组装用户list消息");
        this.MESSAGE_LIST_USERS = this.schema.generateUsersExecutorList();
        this.logger.info("组装群组list消息");
        this.MESSAGE_LIST_GROUP = this.schema.generateGroupExecutorList();
    }

    public String getMessageListUsers() {
        return this.MESSAGE_LIST_USERS;
    }

    public String getMessageListGroup() {
        return this.MESSAGE_LIST_GROUP;
    }


    // ==========================================================================================================================================================
    //
    // 模块相关
    //
    // ==========================================================================================================================================================


    // =========================================================================
    // 插件操作


    public Set<Map.Entry<String, Plugin>> getAllPlugin() {
        return this.schema.getAllPlugin();
    }

    public Map<String, Boolean> listAllModule() {
        return this.schema.listAllModule();
    }

    public Map<Runner, Boolean> listAllRunner() {
        return this.schema.listAllRunner();
    }

    public Map<Filter, Boolean> listAllFilter() {
        return this.schema.listAllFilter();
    }

    public Map<Monitor, Boolean> listAllMonitor() {
        return this.schema.listAllMonitor();
    }

    public Map<Checker, Boolean> listAllChecker() {
        return this.schema.listAllChecker();
    }

    public List<Checker> listGlobalUsersChecker() {
        return this.schema.listGlobalUsersChecker();
    }

    public List<Checker> listGlobalGroupChecker() {
        return this.schema.listGlobalGroupChecker();
    }

    public Map<String, List<Checker>> listCommandUsersChecker() {
        return this.schema.listCommandsUsersChecker();
    }

    public Map<String, List<Checker>> listCommandGroupChecker() {
        return this.schema.listCommandsGroupChecker();
    }

    public Map<Executor, Boolean> listAllExecutor() {
        return this.schema.listAllExecutor();
    }


    public void shutModule(String name) {
        this.schema.shutModule(name);
    }

    public void initModule(String name) {
        this.schema.initModule(name);
    }

    public void bootModule(String name) {
        this.schema.bootModule(name);
    }

    public void rebootModule(String name) {
        this.schema.rebootModule(name);
    }

    public void unloadModule(String name) {
        this.schema.unloadModule(name);
    }

    public String schemaVerbose() {
        return this.schema.verboseStatus();
    }

    public <T extends EventHandlerRunner> T getRunner(Class<T> clazz) {
        return this.schema.getRunner(clazz);
    }


    // ==========================================================================================================================================================
    //
    // BOT相关封装
    //
    // ==========================================================================================================================================================


    public Future<?> submit(Runnable runnable) {
        return this.EXECUTOR_SERVICE.submit(runnable);
    }

    public <T> Future<?> submit(Runnable runnable, T t) {
        return this.EXECUTOR_SERVICE.submit(runnable, t);
    }

    public Future<?> submit(Callable<?> callable) {
        return this.EXECUTOR_SERVICE.submit(callable);
    }

    public ScheduledFuture<?> schedule(Runnable runnable, long time, TimeUnit timeUnit) {
        return this.EXECUTOR_SERVICE.schedule(runnable, time, timeUnit);
    }

    public ScheduledFuture<?> schedule(Callable<?> callable, long delay, TimeUnit unit) {
        return this.EXECUTOR_SERVICE.schedule(callable, delay, unit);
    }

    public ScheduledFuture<?> scheduleAtFixedRate(Runnable runnable, long initialDelay, long period, TimeUnit unit) {
        return this.EXECUTOR_SERVICE.scheduleAtFixedRate(runnable, initialDelay, period, unit);
    }

    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable runnable, long initialDelay, long delay, TimeUnit unit) {
        return this.EXECUTOR_SERVICE.scheduleWithFixedDelay(runnable, initialDelay, delay, unit);
    }


    // =========================================================================


    public long getBotID() {
        return this.bot.getId();
    }

    public ContactList<Friend> getFriends() {
        return this.bot.getFriends();
    }

    public ContactList<Group> getGroups() {
        return this.bot.getGroups();
    }

    public Stranger getStranger(long id) {
        return this.bot.getStranger(id);
    }

    public Stranger getStrangerOrFail(long id) {
        return this.bot.getStrangerOrFail(id);
    }

    public Friend getFriend(long id) {
        return this.bot.getFriend(id);
    }

    public Friend getFriendOrFail(long id) {
        return this.bot.getFriendOrFail(id);
    }

    public Group getGroup(long id) {
        return this.bot.getGroup(id);
    }

    public Group getGroupOrFail(long id) {
        return this.bot.getGroupOrFail(id);
    }

    public void sendMessage(Contact contact, Message message) {
        contact.sendMessage(message);
    }

    public void cleanNickname() {
        this.NICKNAME_GLOBAL.clear();
        this.NICKNAME_GROUPS.clear();
    }

    public void appendNickname() {
        File nicknameFile = this.initFile(Paths.get(FurryBlack.getConfigFolder(), "nickname.txt"));
        List<String> nicknames = this.readFile(nicknameFile);
        for (String line : nicknames) {
            String temp = line.trim();
            int indexOfDot = temp.indexOf(".");
            int indexOfColon = temp.indexOf(":");
            if (indexOfDot < 0) {
                this.logger.warning("配置无效" + line);
                continue;
            }
            if (indexOfColon < 0) {
                this.logger.warning("配置无效" + line);
                continue;
            }
            String group = line.substring(0, indexOfDot);
            String user = line.substring(indexOfDot + 1, indexOfColon);
            String nickname = line.substring(indexOfColon + 1);
            long userId = Long.parseLong(user);
            if ("*".equals(group)) {
                this.NICKNAME_GLOBAL.put(userId, nickname);
                this.logger.seek("全局 " + userId + " -> " + nickname);
            } else {
                long groupId = Long.parseLong(group);
                Map<Long, String> groupNicks = this.NICKNAME_GROUPS.computeIfAbsent(groupId, k -> new ConcurrentHashMap<>());
                groupNicks.put(userId, nickname);
                this.logger.seek("群内 " + groupId + "." + userId + " -> " + nickname);
            }
        }
    }

    public Map<Long, String> getNicknameGlobal() {
        return this.NICKNAME_GLOBAL;
    }

    public Map<Long, Map<Long, String>> getNicknameGroups() {
        return this.NICKNAME_GROUPS;
    }

    public String getUsersMappedNickName(User user) {
        return this.NICKNAME_GLOBAL.getOrDefault(user.getId(), user.getNick());
    }

    public String getUsersMappedNickName(long userId) {
        return this.NICKNAME_GLOBAL.getOrDefault(userId, Mirai.getInstance().queryProfile(this.bot, userId).getNickname());
    }

    public String getMemberMappedNickName(Member member) {
        Map<Long, String> groupMap = this.NICKNAME_GROUPS.get(member.getGroup().getId());
        if (groupMap != null) {
            String nickName = groupMap.get(member.getId());
            if (nickName != null) {
                return nickName;
            }
        }
        String nickName = this.NICKNAME_GLOBAL.get(member.getId());
        if (nickName != null) {
            return nickName;
        }
        String nameCard = member.getNameCard();
        if (nameCard.isBlank()) {
            return member.getNick();
        } else {
            return nameCard;
        }
    }

    public String getMemberMappedNickName(long groupId, long userId) {
        Map<Long, String> groupMap = this.NICKNAME_GROUPS.get(groupId);
        if (groupMap != null) {
            String nickName = groupMap.get(userId);
            if (nickName != null) {
                return nickName;
            }
        }
        String nickName = this.NICKNAME_GLOBAL.get(userId);
        if (nickName != null) {
            return nickName;
        }
        Member member = this.bot.getGroupOrFail(groupId).getOrFail(userId);
        String nameCard = member.getNameCard();
        if (nameCard.isBlank()) {
            return member.getNick();
        } else {
            return nameCard;
        }
    }

    // =========================================================================

    public Bot getBot() {
        return this.bot;
    }

}