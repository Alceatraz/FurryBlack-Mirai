/*
 * Copyright (C) 2021 Alceatraz @ BlackTechStudio
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the BTS Anti Commercial & GNU Affero General.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * BTS Anti Commercial & GNU Affero General Public License for more details.
 *
 * You should have received a copy of the BTS Anti Commercial & GNU Affero
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
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.contact.Stranger;
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
import studio.blacktech.furryblackplus.Driver;
import studio.blacktech.furryblackplus.common.Api;
import studio.blacktech.furryblackplus.core.define.Command;
import studio.blacktech.furryblackplus.core.define.annotation.Checker;
import studio.blacktech.furryblackplus.core.define.annotation.Executor;
import studio.blacktech.furryblackplus.core.define.annotation.Filter;
import studio.blacktech.furryblackplus.core.define.annotation.Monitor;
import studio.blacktech.furryblackplus.core.define.annotation.Runner;
import studio.blacktech.furryblackplus.core.define.moduel.EventHandlerChecker;
import studio.blacktech.furryblackplus.core.define.moduel.EventHandlerExecutor;
import studio.blacktech.furryblackplus.core.define.moduel.EventHandlerFilter;
import studio.blacktech.furryblackplus.core.define.moduel.EventHandlerMonitor;
import studio.blacktech.furryblackplus.core.define.moduel.EventHandlerRunner;
import studio.blacktech.furryblackplus.core.define.schema.Plugin;
import studio.blacktech.furryblackplus.core.define.schema.Schema;
import studio.blacktech.furryblackplus.core.exception.moduels.boot.BootException;
import studio.blacktech.furryblackplus.core.exception.moduels.load.FirstBootException;
import studio.blacktech.furryblackplus.core.exception.moduels.load.MisConfigException;
import studio.blacktech.furryblackplus.core.utilties.common.HashTool;
import studio.blacktech.furryblackplus.core.utilties.logger.LoggerX;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;


@Api("系统核心路由")
public final class Systemd {


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


    private final LoggerX logger = new LoggerX(this.getClass());

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
                System.exit(0);
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


        // ==========================================================================================================================
        // 初始化配置文件


        this.logger.info("初始化配置文件");


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
        // 读取配置

        this.logger.hint("初始化命令过滤器");

        String prefix = config.getProperty(CONF_BOT_COMMAND_PREFIX);

        if (prefix == null || prefix.isEmpty() || prefix.isBlank() || prefix.length() != 1) {
            this.logger.warning("指定的命令前缀不可用 将自动设置为默认值: /");
        } else {
            this.COMMAND_PREFIX = prefix.charAt(0);
        }

        String regex = "^" + this.COMMAND_PREFIX + "[a-z]{3,8}";

        this.logger.seek("识别前缀 " + this.COMMAND_PREFIX);
        this.logger.info("识别正则 " + regex);

        this.COMMAND_PATTERN = Pattern.compile(regex);

        // ==========================================================================================================================
        // 读取模板


        this.logger.hint("初始化内置消息");

        File FILE_EULA = Paths.get(Driver.getConfigFolder(), "message_eula.txt").toFile();
        File FILE_INFO = Paths.get(Driver.getConfigFolder(), "message_info.txt").toFile();
        File FILE_HELP = Paths.get(Driver.getConfigFolder(), "message_help.txt").toFile();

        this.logger.info("初始化eula");
        this.MESSAGE_EULA = this.readFile(FILE_EULA);

        this.logger.info("初始化info");
        this.MESSAGE_INFO = this.readFile(FILE_INFO);

        this.logger.info("初始化help");
        this.MESSAGE_HELP = this.readFile(FILE_HELP);

        this.MESSAGE_EULA = this.MESSAGE_EULA.replaceAll("\\$\\{VERSION}", Driver.APP_VERSION);
        this.MESSAGE_INFO = this.MESSAGE_INFO.replaceAll("\\$\\{VERSION}", Driver.APP_VERSION);
        this.MESSAGE_HELP = this.MESSAGE_HELP.replaceAll("\\$\\{VERSION}", Driver.APP_VERSION);

        String SHA_EULA = HashTool.SHA256(this.MESSAGE_EULA);
        String SHA_INFO = HashTool.SHA256(this.MESSAGE_INFO);

        this.MESSAGE_EULA = this.MESSAGE_EULA + "\r\nSHA-256: " + SHA_EULA;
        this.MESSAGE_INFO = this.MESSAGE_INFO + "\r\nSHA-256: " + SHA_INFO;

        this.logger.info("EULA Digest " + SHA_EULA);
        this.logger.info("INFO Digest " + SHA_INFO);


        // ==========================================================================================================================
        // 加载常用昵称


        this.NICKNAME_GLOBAL = new HashMap<>();
        this.NICKNAME_GROUPS = new HashMap<>();


        File commonNick = this.initFile(Paths.get(Driver.getConfigFolder(), "nickname.txt").toFile());

        try (
            FileReader fileReader = new FileReader(commonNick);
            BufferedReader bufferedReader = new BufferedReader(fileReader)
        ) {

            String line;

            while ((line = bufferedReader.readLine()) != null) {

                if (!line.contains(":")) {
                    this.logger.warning("配置无效 " + line);
                    continue;
                }

                String[] temp1 = line.split(":");

                if (temp1.length != 2) {
                    this.logger.warning("配置无效 " + line);
                    continue;
                }

                if (!temp1[0].contains("\\.")) {
                    this.logger.warning("配置无效 " + line);
                    continue;
                }

                String nick = temp1[1].trim();

                String[] temp2 = temp1[0].split("\\.");

                long groupId;
                long userId;

                try {
                    userId = Long.parseLong(temp2[1]);
                } catch (NumberFormatException exception) {
                    this.logger.warning("配置无效 " + line);
                    continue;
                }

                if (temp2[0].equals("\\*")) {

                    this.NICKNAME_GLOBAL.put(userId, nick);

                    this.logger.seek("添加全局昵称 " + userId, nick);

                } else {

                    try {
                        groupId = Long.parseLong(temp2[0]);
                    } catch (NumberFormatException exception) {
                        this.logger.warning("配置无效 " + line);
                        continue;
                    }

                    Map<Long, String> groupNicks;

                    if (this.NICKNAME_GROUPS.containsKey(groupId)) {
                        groupNicks = this.NICKNAME_GROUPS.get(groupId);
                    } else {
                        this.NICKNAME_GROUPS.put(groupId, groupNicks = new HashMap<>());
                    }

                    groupNicks.put(userId, nick);

                    this.logger.seek("添加群内昵称 " + groupId + "." + userId, nick);
                }


            }

        } catch (Exception exception) {
            throw new BootException("昵称映射表读取失败", exception);
        }


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

        if (Driver.isDebug()) {
            this.logger.seek("QQ密码 " + ACCOUNT_PW);
            this.logger.warning("关闭调试模式以给此条日志打码");
        } else {
            String shadow_ACCOUNT_PW = ACCOUNT_PW.charAt(0) + "*".repeat(length - 1);
            this.logger.seek("QQ密码 " + shadow_ACCOUNT_PW);
        }

        // ==========================================================================================================================
        // 读取设备配置


        // 设备类型


        String DEVICE_TYPE = config.getProperty(CONF_BOT_DEVICE_TYPE);


        switch (DEVICE_TYPE) {

            case "PAD":
            case "537062409":
                configuration.setProtocol(BotConfiguration.MiraiProtocol.ANDROID_PAD);
                this.logger.seek("设备模式 " + DEVICE_TYPE + " 安卓平板");
                break;

            case "PHONE":
            case "537062845":
                configuration.setProtocol(BotConfiguration.MiraiProtocol.ANDROID_PHONE);
                this.logger.seek("设备模式 " + DEVICE_TYPE + " 安卓手机");
                break;

            case "WATCH":
            case "537061176":
                configuration.setProtocol(BotConfiguration.MiraiProtocol.ANDROID_WATCH);
                this.logger.seek("设备模式 " + DEVICE_TYPE + " 安卓手表");
                break;

            default:
                this.logger.error("设备模式配置错误");
                throw new MisConfigException(CONF_BOT_DEVICE_TYPE + "必须是填 PAD PHONE WATCH 之一 大写无符号");

        }


        // 设备信息


        String DEVICE_INFO = config.getProperty(CONF_BOT_DEVICE_INFO);

        File deviceInfo = Paths.get(Driver.getConfigFolder(), DEVICE_INFO).toFile();

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


        long NET_HEARTBEAT_PERIOD = this.parseLong(config.getProperty(CONF_NET_HEARTBEAT_PERIOD));
        long NET_HEARTBEAT_TIMEOUT = this.parseLong(config.getProperty(CONF_NET_HEARTBEAT_TIMEOUT));

        this.logger.seek("心跳间隔 " + NET_HEARTBEAT_PERIOD);
        this.logger.seek("心跳超时 " + NET_HEARTBEAT_TIMEOUT);

        configuration.setHeartbeatPeriodMillis(NET_HEARTBEAT_PERIOD);
        configuration.setHeartbeatTimeoutMillis(NET_HEARTBEAT_TIMEOUT);


        // 重连参数


        int NET_RECONNECT_RETRY = this.parseInteger(config.getProperty(CONF_NET_RECONNECT_RETRY));

        this.logger.seek("重连次数 " + NET_RECONNECT_RETRY);

        configuration.setReconnectionRetryTimes(NET_RECONNECT_RETRY);


        // 传入日志


        configuration.setBotLoggerSupplier(i -> new LoggerX("MiraiBot"));
        configuration.setNetworkLoggerSupplier(i -> new LoggerX("MiraiNet"));


        // ==========================================================================================================================
        // 创建机器人


        this.logger.info("初始化机器人");
        this.bot = BotFactory.INSTANCE.newBot(ACCOUNT_QQ, ACCOUNT_PW, configuration);

        this.logger.info("机器人类型 " + this.bot.getClass().getName());


        // ==========================================================================================================================
        //
        // 插件功能
        //
        // ==========================================================================================================================


        this.schema = new Schema(this.FOLDER_PLUGIN);


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

        this.generateListMessage();

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


        if (Driver.isNoLogin()) {
            this.logger.warning("指定了--no-login参数 跳过登录");
        } else {
            this.logger.hint("登录");
            this.bot.login();
        }


        // ==========================================================================================================================
        // 启动线程池


        int monitorPoolSize = this.parseInteger(config.getProperty(CONF_THREADS_SCHEDULE));
        this.logger.seek("监听线程池配置 " + monitorPoolSize);

        this.MONITOR_PROCESS = (ThreadPoolExecutor) Executors.newFixedThreadPool(monitorPoolSize);

        //

        int schedulePoolSize = this.parseInteger(config.getProperty(CONF_THREADS_SCHEDULE));
        this.logger.seek("异步线程池配置 " + schedulePoolSize);

        this.EXECUTOR_SERVICE = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(schedulePoolSize);


        // ==========================================================================================================================
        // 启动模块


        this.schema.boot();


        // ==========================================================================================================================
        // 列出所有好友和群组


        if (!Driver.isNoLogin()) {

            this.logger.seek("机器人账号 " + this.bot.getId());
            this.logger.seek("机器人昵称 " + this.bot.getNick());
            this.logger.seek("机器人头像 " + this.bot.getAvatarUrl());

            this.logger.hint("所有好友");
            this.bot.getFriends().forEach(item -> this.logger.info(Driver.getFormattedNickName(item)));

            this.logger.hint("所有群组");
            this.bot.getGroups().forEach(item -> this.logger.info(Driver.getGroupInfo(item)));

        }


    }


    // ==========================================================================================================================================================
    //
    // 关闭
    //
    // ==========================================================================================================================================================


    public void shut() {

        // ==========================================================================================================================
        // 关闭模块

        boolean shut;

        try {
            shut = this.schema.shut();
        } catch (Exception exception) {
            shut = false;
            this.logger.error("关闭模块系统发生异常", exception);
        }

        if (!shut) {
            this.schema.verboseStatus();
        }

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


        this.logger.hint("关闭线程池");


        if (Driver.isShutModeDrop()) {

            this.logger.info("强制关闭监听器线程池");
            this.MONITOR_PROCESS.shutdownNow();

        } else {

            this.logger.info("关闭监听器线程池");
            this.MONITOR_PROCESS.shutdown();

            try {
                this.logger.info("等待监听器线程池关闭");
                //noinspection ResultOfMethodCallIgnored
                this.MONITOR_PROCESS.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
                this.logger.info("监听器线程池已关闭");
            } catch (InterruptedException exception) {
                this.logger.error("等待关闭监听器线程池错误", exception);
                this.MONITOR_PROCESS.shutdownNow();
            }
        }


        if (Driver.isShutModeDrop()) {

            this.logger.info("强制关闭异步任务线程池");
            this.EXECUTOR_SERVICE.shutdownNow();

        } else {

            this.logger.info("关闭异步任务线程池");
            this.EXECUTOR_SERVICE.shutdown();

            try {
                this.logger.info("等待异步任务线程池关闭");
                //noinspection ResultOfMethodCallIgnored
                this.EXECUTOR_SERVICE.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
            } catch (InterruptedException exception) {
                this.logger.error("等待关闭异步任务线程池错误", exception);
                exception.printStackTrace();
            }
        }


        // ==========================================================================================================================


        this.logger.hint("关闭机器人");

        this.logger.info("通知机器人关闭");

        if (Driver.isNoLogin()) {
            this.logger.warning("调试模式 不需要关闭机器人");
        } else {
            if (Driver.isShutModeDrop()) {
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

        if (!Driver.isEnable()) return;

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

                    case "help":
                        if (command.hasCommandBody()) {
                            String segment = command.getParameterSegment(0);
                            EventHandlerExecutor executor = this.schema.getExecutorUsersPool().get(segment);
                            if (executor == null) {
                                Driver.sendMessage(event, "没有此命令");
                            } else {
                                Driver.sendMessage(event, executor.getHelp());
                            }
                        } else {
                            Driver.sendMessage(event, this.MESSAGE_HELP);
                        }
                        break;

                    case "list":
                        Driver.sendMessage(event, this.MESSAGE_LIST_USERS);
                        break;

                    case "info":
                        Driver.sendMessage(event, this.MESSAGE_INFO);
                        break;

                    case "eula":
                        Driver.sendMessage(event, this.MESSAGE_EULA);
                        break;

                    default:

                        EventHandlerExecutor executor = this.schema.getExecutorUsersPool().get(commandName);

                        if (executor == null) {
                            Driver.sendMessage(event, "没有此命令");
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

                        break;
                }
            }

        } catch (Exception exception) {
            this.logger.dump(event, exception);
        }
    }


    public void handleGroupMessage(GroupMessageEvent event) {

        if (!Driver.isEnable()) return;

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

                    case "help":
                        if (command.hasCommandBody()) {
                            String segment = command.getParameterSegment(0);
                            EventHandlerExecutor executor = this.schema.getExecutorGroupPool().get(segment);
                            if (executor == null) {
                                Driver.sendMessage(event, "没有此命令");
                            } else {
                                try {
                                    Driver.sendMessage(event, executor.getHelp());
                                } catch (Exception exception) {
                                    Driver.sendMessage(event, "帮助信息发送至私聊失败 请允许临时会话权限");
                                }
                            }
                        } else {
                            try {
                                event.getSender().sendMessage(this.MESSAGE_HELP);
                            } catch (Exception exception) {
                                Driver.sendMessage(event, "帮助信息发送至私聊失败 请允许临时会话权限");
                            }
                        }
                        break;

                    case "list":
                        try {
                            event.getSender().sendMessage(this.MESSAGE_LIST_GROUP);
                        } catch (Exception exception) {
                            Driver.sendMessage(event, "可用命令发送至私聊失败 请允许临时会话权限");
                        }
                        break;

                    case "info":
                        try {
                            event.getSender().sendMessage(this.MESSAGE_INFO);
                        } catch (Exception exception) {
                            Driver.sendMessage(event, "关于发送至私聊失败 请允许临时会话权限");
                        }
                        break;

                    case "eula":
                        try {
                            event.getSender().sendMessage(this.MESSAGE_EULA);
                        } catch (Exception exception) {
                            Driver.sendMessage(event, "EULA发送至私聊失败 请允许临时会话权限");
                        }
                        break;

                    default:

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

                        break;
                }
            }

        } catch (Exception exception) {
            this.logger.dump(event, exception);
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


    private int parseInteger(String temp) throws MisConfigException {
        try {
            return Integer.parseInt(temp);
        } catch (Exception exception) {
            throw new MisConfigException("配置解析错误 " + temp, exception);
        }
    }


    private long parseLong(String temp) throws MisConfigException {
        try {
            return Long.parseLong(temp);
        } catch (Exception exception) {
            throw new MisConfigException("配置解析错误 " + temp, exception);
        }
    }


    private File initFile(File file) throws BootException {
        try {
            if (file.createNewFile()) this.logger.hint("创建新的文件 " + file.getAbsolutePath());
        } catch (IOException exception) {
            throw new BootException("文件创建失败 " + file.getAbsolutePath(), exception);
        }

        if (!file.exists()) throw new BootException("文件不存在 " + file.getAbsolutePath());
        if (!file.canRead()) throw new BootException("文件无权读取 " + file.getAbsolutePath());
        return file;
    }


    private String readFile(File file) throws BootException {

        this.initFile(file);

        try (
            FileReader fileReader = new FileReader(file, StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(fileReader)
        ) {

            String temp;
            StringBuilder builder = new StringBuilder();
            while ((temp = bufferedReader.readLine()) != null) builder.append(temp).append("\r\n");
            return builder.toString();

        } catch (FileNotFoundException exception) {
            throw new BootException("文件不存在 " + file.getAbsolutePath(), exception);
        } catch (IOException exception) {
            throw new BootException("文件读取失败 " + file.getAbsolutePath(), exception);
        }
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

    public Set<String> listAllPluginName() {
        return this.schema.listAllPluginName();
    }


    public void importPlugin(String name) {
        this.schema.importPlugin(name);
    }

    public void unloadPlugin(String name) {
        this.schema.unloadPlugin(name);
    }

    public void reloadPlugin(String name) {
        this.schema.reloadPlugin(name);
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

    public void reloadModule(String name) {
        this.schema.reloadModule(name);
    }

    public void schemaVerbose() {
        this.schema.verboseStatus();
    }


    public <T extends EventHandlerRunner> T getRunner(Class<T> clazz) {
        return this.schema.getRunner(clazz);
    }


    // ==========================================================================================================================================================
    //
    // BOT相关封装
    //
    // ==========================================================================================================================================================


    @Api("提交异步任务")
    public Future<?> submit(Runnable runnable) {
        return this.EXECUTOR_SERVICE.submit(runnable);
    }

    @Api("提交异步任务")
    public <T> Future<?> submit(Runnable runnable, T t) {
        return this.EXECUTOR_SERVICE.submit(runnable, t);
    }

    @Api("提交异步任务")
    public Future<?> submit(Callable<?> callable) {
        return this.EXECUTOR_SERVICE.submit(callable);
    }

    @Api("提交定时任务")
    public ScheduledFuture<?> schedule(Runnable runnable, long time, TimeUnit timeUnit) {
        return this.EXECUTOR_SERVICE.schedule(runnable, time, timeUnit);
    }

    @Api("提交定时任务")
    public ScheduledFuture<?> schedule(Callable<?> callable, long delay, TimeUnit unit) {
        return this.EXECUTOR_SERVICE.schedule(callable, delay, unit);
    }

    @Api("提交定时任务")
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable runnable, long initialDelay, long period, TimeUnit unit) {
        return this.EXECUTOR_SERVICE.scheduleAtFixedRate(runnable, initialDelay, period, unit);
    }

    @Api("提交定时任务")
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable runnable, long initialDelay, long delay, TimeUnit unit) {
        return this.EXECUTOR_SERVICE.scheduleWithFixedDelay(runnable, initialDelay, delay, unit);
    }

    // =========================================================================


    @Api("获取BOT自身QQ号")
    public long getBotID() {
        return this.bot.getId();
    }

    @Api("列出所有好友")
    public ContactList<Friend> getFriends() {
        return this.bot.getFriends();
    }

    @Api("列出所有群组")
    public ContactList<Group> getGroups() {
        return this.bot.getGroups();
    }

    @Api("根据ID获取陌生人")
    public Stranger getStranger(long id) {
        return this.bot.getStranger(id);
    }

    @Api("根据ID获取陌生人")
    public Stranger getStrangerOrFail(long id) {
        return this.bot.getStrangerOrFail(id);
    }

    @Api("根据ID获取好友")
    public Friend getFriend(long id) {
        return this.bot.getFriend(id);
    }

    @Api("根据ID获取好友")
    public Friend getFriendOrFail(long id) {
        return this.bot.getFriendOrFail(id);
    }

    @Api("根据ID获取群组")
    public Group getGroup(long id) {
        return this.bot.getGroup(id);
    }

    @Api("根据ID获取群组")
    public Group getGroupOrFail(long id) {
        return this.bot.getGroupOrFail(id);
    }

    @Api("发送消息的核心方法")
    public void sendMessage(Contact contact, Message message) {
        contact.sendMessage(message);
    }

    @Api("获取预设昵称")
    public String getMappedNickName(long groupId, long userId) {
        if (this.NICKNAME_GROUPS.containsKey(groupId)) {
            Map<Long, String> groupNicks = this.NICKNAME_GROUPS.get(groupId);
            if (groupNicks.containsKey(userId)) {
                return groupNicks.get(userId);
            }
        }
        if (this.NICKNAME_GLOBAL.containsKey(userId)) {
            return this.NICKNAME_GLOBAL.get(userId);
        }
        NormalMember member = this.bot.getGroupOrFail(groupId).getOrFail(userId);
        String nameCard = member.getNameCard();
        if (nameCard.isBlank()) {
            return Mirai.getInstance().queryProfile(this.bot, userId).getNickname();
        } else {
            return nameCard;
        }
    }

    @Api("获取预设昵称")
    public String getMappedNickName(GroupMessageEvent event) {
        long groupId = event.getGroup().getId();
        long userId = event.getSender().getId();
        if (this.NICKNAME_GROUPS.containsKey(groupId)) {
            Map<Long, String> groupNicks = this.NICKNAME_GROUPS.get(groupId);
            if (groupNicks.containsKey(userId)) {
                return groupNicks.get(userId);
            }
        }
        if (this.NICKNAME_GLOBAL.containsKey(userId)) {
            return this.NICKNAME_GLOBAL.get(userId);
        }
        String nameCard = event.getSender().getNameCard();
        if (nameCard.isBlank()) {
            return event.getSender().getNick();
        } else {
            return nameCard;
        }
    }


    // =========================================================================


    public Bot getBot() {
        return this.bot;
    }


}