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
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.utils.BotConfiguration;
import net.mamoe.mirai.utils.DeviceInfo;
import studio.blacktech.furryblack.core.enhance.MessageDigest;
import studio.blacktech.furryblackplus.FurryBlack;
import studio.blacktech.furryblackplus.common.Api;
import studio.blacktech.furryblackplus.core.common.exception.moduels.boot.BootException;
import studio.blacktech.furryblackplus.core.common.exception.moduels.load.FirstBootException;
import studio.blacktech.furryblackplus.core.common.exception.moduels.load.MisConfigException;
import studio.blacktech.furryblackplus.core.common.logger.LoggerXFactory;
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
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

  //= ==================================================================================================================
  //
  // 配置项名称
  //
  //= ==================================================================================================================

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
    "# QQ账号\n" +
    CONF_ACCOUNT_ID + "=00000000\n" +
    "# QQ密码\n" +
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
    "# 设备类型 IPAD/MACOS/PAD/PHONE/WATCH\n" +
    CONF_BOT_DEVICE_TYPE + "=PHONE\n" +
    "# 设备文件\n" +
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

  //= ==================================================================================================================
  //
  // 私有对象
  //
  //= ==================================================================================================================

  private final Lock lock = new ReentrantLock();
  private final Condition condition = lock.newCondition();

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

  //= ==================================================================================================================
  // 对象控制

  public Systemd(File folderConfig, File folderPlugin) {
    synchronized (Systemd.class) {
      if (INSTANCE_LOCK) {
        BootException bootException = new BootException("Systemd initModule-lock is on, Invoker stack trace append as suppressed");
        RuntimeException runtimeException = new RuntimeException();
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        runtimeException.setStackTrace(stackTrace);
        bootException.addSuppressed(runtimeException);
        throw bootException;
      }
      INSTANCE_LOCK = true;
      FOLDER_CONFIG = folderConfig;
      FOLDER_PLUGIN = folderPlugin;
    }
  }

  //= ==================================================================================================================
  //
  // 初始化
  //
  //= ==================================================================================================================

  public void boot() throws BootException {

    logger.hint("启动核心配置");

    //= ==================================================================================================================
    // 初始化配置文件

    logger.info("检查配置文件");

    File FILE_CONFIG = Paths.get(FOLDER_CONFIG.getAbsolutePath(), "application.properties").toFile();

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

      logger.warning("检测到初次启动 需要填写必要的配置 即将关闭");
      throw new FirstBootException("检测到初次启动 需要填写必要的配置 -> " + FILE_CONFIG.getAbsolutePath());
    }

    if (!FILE_CONFIG.isFile()) throw new BootException("配置文件不是文件 -> " + FILE_CONFIG.getAbsolutePath());
    if (!FILE_CONFIG.canRead()) throw new BootException("配置文件无权读取 -> " + FILE_CONFIG.getAbsolutePath());

    //= ==================================================================================================================
    // 加载配置

    logger.info("加载配置文件");

    Properties config = new Properties();

    try (
      FileInputStream stream = new FileInputStream(FILE_CONFIG);
      InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)
    ) {
      config.load(reader);
    } catch (IOException exception) {
      logger.error("核心配置文件读取错误 即将关闭 " + FILE_CONFIG.getAbsolutePath());
      throw new BootException("核心配置文件读取错误 " + FILE_CONFIG.getAbsolutePath(), exception);
    }

    //= ==================================================================================================================

    logger.hint("配置核心系统");

    //= ==================================================================================================================
    // 读取配置

    logger.info("加载命令前缀配置");

    String prefix = config.getProperty(CONF_BOT_COMMAND_PREFIX);

    if (prefix == null || prefix.isEmpty() || prefix.isBlank() || prefix.length() != 1) {
      logger.warning("指定的命令前缀不可用 将自动设置为默认值: /");
    } else {
      COMMAND_PREFIX = prefix.charAt(0);
    }

    String regex = "^" + COMMAND_PREFIX + "[a-zA-Z\\d]{2,16}";

    logger.seek("识别前缀 " + COMMAND_PREFIX);
    logger.info("识别正则 " + regex);

    COMMAND_PATTERN = Pattern.compile(regex);

    //= ==================================================================================================================
    // 读取模板

    logger.hint("加载内置消息");

    File FILE_EULA = Paths.get(FurryBlack.getConfigFolder(), "message_eula.txt").toFile();
    File FILE_INFO = Paths.get(FurryBlack.getConfigFolder(), "message_info.txt").toFile();
    File FILE_HELP = Paths.get(FurryBlack.getConfigFolder(), "message_help.txt").toFile();

    logger.info("加载eula");
    MESSAGE_EULA = readFileContent(FILE_EULA);

    logger.info("加载info");
    MESSAGE_INFO = readFileContent(FILE_INFO);

    logger.info("加载help");
    MESSAGE_HELP = readFileContent(FILE_HELP);

    MESSAGE_EULA = MESSAGE_EULA.replaceAll("\\$\\{VERSION}", FurryBlack.APP_VERSION);
    MESSAGE_INFO = MESSAGE_INFO.replaceAll("\\$\\{VERSION}", FurryBlack.APP_VERSION);
    MESSAGE_HELP = MESSAGE_HELP.replaceAll("\\$\\{VERSION}", FurryBlack.APP_VERSION);

    String SHA256_EULA = MessageDigest.sha256(MESSAGE_EULA);
    String SHA256_INFO = MessageDigest.sha256(MESSAGE_INFO);
    String SHA384_EULA = MessageDigest.sha384(MESSAGE_EULA);
    String SHA384_INFO = MessageDigest.sha384(MESSAGE_INFO);
    String SHA512_EULA = MessageDigest.sha512(MESSAGE_EULA);
    String SHA512_INFO = MessageDigest.sha512(MESSAGE_INFO);

    MESSAGE_EULA = MESSAGE_EULA + "\r\nSHA-256: " + SHA256_EULA + "\r\nSHA-384: " + SHA384_EULA + "\r\nSHA-256: " + SHA512_EULA;
    MESSAGE_INFO = MESSAGE_INFO + "\r\nSHA-256: " + SHA256_INFO + "\r\nSHA-384: " + SHA384_INFO + "\r\nSHA-256: " + SHA512_INFO;

    logger.seek("哈希EULA -> " + SHA256_EULA);
    logger.seek("哈希INFO -> " + SHA256_INFO);
    logger.seek("哈希EULA -> " + SHA384_EULA);
    logger.seek("哈希INFO -> " + SHA384_INFO);
    logger.seek("哈希EULA -> " + SHA512_EULA);
    logger.seek("哈希INFO -> " + SHA512_INFO);

    //= ==================================================================================================================
    // 加载常用昵称

    logger.hint("加载常用昵称");

    NICKNAME_GLOBAL = new ConcurrentHashMap<>();
    NICKNAME_GROUPS = new ConcurrentHashMap<>();

    appendNickname();

    //= ==================================================================================================================
    // 读取机器人配置

    logger.hint("加载机器人配置");

    BotConfiguration configuration = new BotConfiguration();

    File cacheFolder = Paths.get(FOLDER_CONFIG.getAbsolutePath(), "cache").toFile();

    configuration.setCacheDir(cacheFolder);
    configuration.enableContactCache();

    //= ==================================================================================================================
    // 读取账号配置

    String accountConfig = config.getProperty(CONF_ACCOUNT_ID);
    long ACCOUNT_QQ = parseLong(accountConfig);

    logger.seek("QQ账号 " + ACCOUNT_QQ);

    String ACCOUNT_PW = config.getProperty(CONF_ACCOUNT_PW);

    int length = accountConfig.length();

    if (FurryBlack.isDebug()) {
      logger.seek("QQ密码 " + ACCOUNT_PW);
      logger.warning("！！！！！！！！！！！！！！！！！！");
      logger.warning("！调试模式开启时会在日志中记录密码！");
      logger.warning("！关闭调试模式以给日志密码信息打码!");
      logger.warning("！！！！！！！！！！！！！！！！！！");
    } else {
      String shadow_ACCOUNT_PW = ACCOUNT_PW.charAt(0) + "*".repeat(length - 1);
      logger.seek("QQ密码 " + shadow_ACCOUNT_PW);
    }

    //= ==================================================================================================================
    // 读取设备配置

    // 设备类型

    String DEVICE_TYPE = config.getProperty(CONF_BOT_DEVICE_TYPE);

    BotConfiguration.MiraiProtocol protocol;

    switch (DEVICE_TYPE) {
      case "IPAD" -> {
        protocol = BotConfiguration.MiraiProtocol.IPAD;
        logger.seek("设备模式 " + DEVICE_TYPE + " 苹果平板");
      }
      case "MACOS" -> {
        protocol = BotConfiguration.MiraiProtocol.MACOS;
        logger.seek("设备模式 " + DEVICE_TYPE + " 苹果电脑");
      }
      case "PAD" -> {
        protocol = BotConfiguration.MiraiProtocol.ANDROID_PAD;
        logger.seek("设备模式 " + DEVICE_TYPE + " 安卓平板");
      }
      case "WATCH" -> {
        protocol = BotConfiguration.MiraiProtocol.ANDROID_WATCH;
        logger.seek("设备模式 " + DEVICE_TYPE + " 安卓手表");
      }
      case "PHONE" -> {
        protocol = BotConfiguration.MiraiProtocol.ANDROID_PHONE;
        logger.seek("设备模式 " + DEVICE_TYPE + " 安卓手机");
      }
      default -> throw new IllegalStateException("设备信息配置错误 不存在此类型 -> " + DEVICE_TYPE + ", 必须为 IPAD/MACOS/PAD/PHONE/WATCH 之一");
    }

    configuration.setProtocol(protocol);

    // 设备信息

    String DEVICE_INFO = config.getProperty(CONF_BOT_DEVICE_INFO);

    File deviceInfoFile = Paths.get(FurryBlack.getConfigFolder(), DEVICE_INFO).toFile();

    if (!deviceInfoFile.exists()) {
      String temp = "设备信息配置错误 指定的文件不存在 请使用Aoki生成 " + deviceInfoFile.getAbsolutePath();
      logger.error(temp);
      throw new MisConfigException(temp);
    }

    if (!deviceInfoFile.isFile()) {
      String temp = "设备信息配置错误 指定的路径不是文件 " + deviceInfoFile.getAbsolutePath();
      logger.error(temp);
      throw new MisConfigException(temp);
    }

    if (!deviceInfoFile.canRead()) {
      String temp = "设备信息配置错误 指定的文件无权读取 " + deviceInfoFile.getAbsolutePath();
      logger.error(temp);
      throw new MisConfigException(temp);
    }

    logger.seek("设备信息文件 " + deviceInfoFile.getAbsolutePath());

    DeviceInfo deviceInfo = DeviceInfo.from(deviceInfoFile);

    configuration.setDeviceInfo((i) -> deviceInfo);

    //= ==================================================================================================================
    // 读取网络配置

    // 心跳参数

    Long NET_HEARTBEAT_PERIOD = parseLongOrNull(config.getProperty(CONF_NET_HEARTBEAT_PERIOD));
    if (NET_HEARTBEAT_PERIOD != null) {
      logger.seek("心跳间隔 " + NET_HEARTBEAT_PERIOD);
      configuration.setHeartbeatPeriodMillis(NET_HEARTBEAT_PERIOD);
    }

    Long NET_HEARTBEAT_TIMEOUT = parseLongOrNull(config.getProperty(CONF_NET_HEARTBEAT_TIMEOUT));
    if (NET_HEARTBEAT_TIMEOUT != null) {
      logger.seek("心跳超时 " + NET_HEARTBEAT_TIMEOUT);
      configuration.setHeartbeatTimeoutMillis(NET_HEARTBEAT_TIMEOUT);
    }

    // 重连参数

    Integer NET_RECONNECT_RETRY = parseIntegerOrNull(config.getProperty(CONF_NET_RECONNECT_RETRY));
    if (NET_RECONNECT_RETRY != null) {
      logger.seek("重连次数 " + NET_RECONNECT_RETRY);
      configuration.setReconnectionRetryTimes(NET_RECONNECT_RETRY);
    }

    // 传入日志

    configuration.setBotLoggerSupplier(i -> LoggerXFactory.newLogger("MiraiBot"));
    configuration.setNetworkLoggerSupplier(i -> LoggerXFactory.newLogger("MiraiNet"));

    //= ==================================================================================================================
    // 创建机器人

    // 2.15.0

    //    BotAuthorization authorization;
    //    if (FurryBlack.isQrcode()) {
    //      authorization = BotAuthorization.byQRCode();
    //    } else {
    //      authorization = BotAuthorization.byPassword(ACCOUNT_PW);
    //    }
    //    logger.hint("创建机器人");
    //    bot = BotFactory.INSTANCE.newBot(ACCOUNT_QQ, authorization, configuration);

    logger.hint("创建机器人");
    bot = BotFactory.INSTANCE.newBot(ACCOUNT_QQ, ACCOUNT_PW, configuration);

    //= ==================================================================================================================
    //
    // 插件功能
    //
    //= ==================================================================================================================

    schema = new Schema(FOLDER_PLUGIN);

    //= ==================================================================================================================
    // 扫描插件

    schema.scanPlugin();

    //= ==================================================================================================================
    // 扫描模块

    schema.scanModule();

    //= ==================================================================================================================
    // 注册模块

    schema.loadModule();

    //= ==================================================================================================================
    // 创建模块

    schema.makeModule();

    //= ==================================================================================================================
    // 注册完成

    logger.hint("生成模板消息");

    //= ==================================================================================================================
    // 执行初始化方法

    schema.initModule();

    //= ==================================================================================================================
    // 注册事件监听

    logger.hint("注册机器人事件监听");

    userMessageEventListener = GlobalEventChannel.INSTANCE.subscribeAlways(UserMessageEvent.class, this::handleUsersMessage);
    groupMessageEventListener = GlobalEventChannel.INSTANCE.subscribeAlways(GroupMessageEvent.class, this::handleGroupMessage);

    newFriendRequestEventListener = GlobalEventChannel.INSTANCE.subscribeAlways(NewFriendRequestEvent.class, event -> {
      logger.hint("BOT被添加好友 " + event.getFromNick() + "(" + event.getFromId() + ")");
      event.accept();
    });

    botInvitedJoinGroupRequestEventListener = GlobalEventChannel.INSTANCE.subscribeAlways(BotInvitedJoinGroupRequestEvent.class, event -> {
      logger.hint("BOT被邀请入群 " + event.getGroupName() + "(" + event.getGroupId() + ") 邀请人 " + event.getInvitorNick() + "(" + event.getInvitorId() + ")");
      event.accept();
    });

    memberJoinEventListener = GlobalEventChannel.INSTANCE.subscribeAlways(MemberJoinEvent.class, event -> {
      String user = event.getUser().getNick() + "(" + event.getUser().getId() + ")";
      if (event instanceof MemberJoinEvent.Active) {
        logger.hint("用户申请加群 " + user + " → " + event.getGroup().getName() + "(" + event.getGroupId() + ")");
      } else if (event instanceof MemberJoinEvent.Invite) {
        logger.hint("用户受邀进群 " + user + " → " + event.getGroup().getName() + "(" + event.getGroupId() + ")");
      }
    });

    memberLeaveEventListener = GlobalEventChannel.INSTANCE.subscribeAlways(MemberLeaveEvent.class, event -> {
      String user = event.getUser().getNick() + "(" + event.getUser().getId() + ")";
      if (event instanceof MemberLeaveEvent.Quit) {
        logger.hint("用户主动退群 " + user + " → " + event.getGroup().getName() + "(" + event.getGroupId() + ")");
      } else if (event instanceof MemberLeaveEvent.Kick) {
        logger.hint("用户被踢出群 " + user + " → " + event.getGroup().getName() + "(" + event.getGroupId() + ")");
      }
    });

    //= ==================================================================================================================
    // 升级协议

    if (FurryBlack.isUpdate()) {

      logger.hint("升级机器人协议");

      Class<?> clazz;
      try {
        clazz = Class.forName("xyz.cssxsh.mirai.tool.FixProtocolVersion");
      } catch (ClassNotFoundException exception) {
        throw new BootException("[UPGRADE/PROTOCOL] Load class failure", exception);
      }

      Method methodUpdate;
      try {
        methodUpdate = clazz.getMethod("update");
      } catch (NoSuchMethodException exception) {
        throw new BootException("[UPGRADE/PROTOCOL] Load method failure", exception);
      }

      try {
        methodUpdate.invoke(null);
      } catch (IllegalAccessException | InvocationTargetException exception) {
        throw new BootException("[UPGRADE/PROTOCOL] Invoke method failure", exception);
      }

      Method methodInfo;
      try {
        methodInfo = clazz.getMethod("info");
      } catch (NoSuchMethodException exception) {
        throw new BootException("[UPGRADE/PROTOCOL] Load method failure", exception);
      }

      Object invoke;
      try {
        invoke = methodInfo.invoke(null);
      } catch (IllegalAccessException | InvocationTargetException | ClassCastException exception) {
        throw new BootException("[UPGRADE/PROTOCOL] Invoke method failure", exception);
      }

      try {
        if (invoke instanceof Map<?, ?> map) {
          @SuppressWarnings("unchecked") Map<BotConfiguration.MiraiProtocol, String> info = (Map<BotConfiguration.MiraiProtocol, String>) map;
          info.forEach((k, v) -> logger.info(v + " -> " + k.name()));
        }
      } catch (Exception exception) {
        throw new BootException("[UPGRADE/PROTOCOL] Invoke method failure", exception);
      }

    }

    //= ==================================================================================================================
    // 登录QQ

    if (FurryBlack.isNoLogin()) {
      logger.warning("指定了--no-login参数 跳过登录");
    } else {
      logger.hint("登录机器人");
      bot.login();
    }

    //= ==================================================================================================================
    // 启动线程池

    int monitorPoolSize = parseInteger(config.getProperty(CONF_THREADS_SCHEDULE));
    logger.seek("监听线程池配置 " + monitorPoolSize);

    MONITOR_PROCESS = new ThreadPoolExecutor(monitorPoolSize, monitorPoolSize, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());

    //

    int schedulePoolSize = parseInteger(config.getProperty(CONF_THREADS_SCHEDULE));
    logger.seek("异步线程池配置 " + schedulePoolSize);

    EXECUTOR_SERVICE = new ScheduledThreadPoolExecutor(schedulePoolSize);

    //= ==================================================================================================================
    // 启动模块

    schema.bootModule();

    //= ==================================================================================================================
    // 列出所有好友和群组

    if (!FurryBlack.isNoLogin()) {

      logger.seek("机器人账号 " + bot.getId());
      logger.seek("机器人昵称 " + bot.getNick());
      logger.seek("机器人头像 " + bot.getAvatarUrl());

      logger.hint("所有好友");
      bot.getFriends().forEach(item -> logger.info(FurryBlack.getFormattedNickName(item)));

      logger.hint("所有群组");
      bot.getGroups().forEach(item -> logger.info(FurryBlack.getGroupInfo(item)));

    }

    //= ==================================================================================================================
    // 生成模板消息

    generateListMessage();

  }

  //= ==================================================================================================================
  //
  // 关闭
  //
  //= ==================================================================================================================

  public void shut() {

    //= ==================================================================================================================
    // 关闭监听

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

    //= ==================================================================================================================
    // 关闭模块

    try {
      schema.shutModule();
    } catch (Exception exception) {
      logger.error("关闭插件模型发生异常", exception);
    }

    //= ==================================================================================================================
    // 关闭线程池

    logger.hint("关闭线程池");

    //= ==================================================================================================================

    if (FurryBlack.isShutModeDrop()) {
      logger.warning("丢弃监听任务线程池");
      Thread thread = new Thread(() -> MONITOR_PROCESS.shutdownNow());
      thread.setDaemon(true);
      thread.start();
    } else {
      logger.info("关闭监听任务线程池");
      MONITOR_PROCESS.shutdown();
      try {
        //noinspection ResultOfMethodCallIgnored
        MONITOR_PROCESS.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
      } catch (InterruptedException exception) {
        logger.error("等待关闭监听任务线程池被中断", exception);
      }
      logger.info("监听任务线程池关闭");
    }

    //= ==================================================================================================================

    if (FurryBlack.isShutModeDrop()) {
      logger.warning("丢弃定时任务线程池");
      Thread thread = new Thread(() -> EXECUTOR_SERVICE.shutdownNow());
      thread.setDaemon(true);
      thread.start();
    } else {
      logger.info("关闭定时任务线程池");
      EXECUTOR_SERVICE.shutdown();
      try {
        //noinspection ResultOfMethodCallIgnored
        EXECUTOR_SERVICE.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
      } catch (InterruptedException exception) {
        logger.error("等待关闭定时任务线程池被中断", exception);
      }
      logger.info("定时任务线程池关闭");
    }

    //= ==================================================================================================================
    // 关闭Mirai-Bot

    logger.hint("关闭机器人");

    //= ==================================================================================================================

    logger.info("通知机器人关闭");

    if (FurryBlack.isNoLogin()) {
      logger.warning("调试模式 不需要关闭机器人");
    } else {
      if (FurryBlack.isShutModeDrop()) {
        bot.close(null);
      } else {
        bot.closeAndJoin(null);
      }
    }

    logger.info("机器人已关闭");

  }

  //= ==================================================================================================================
  //
  // 监听器
  //
  //= ==================================================================================================================

  private void handleUsersMessage(UserMessageEvent event) {

    if (!FurryBlack.isEnable()) return;

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

      if (isCommand(content)) {

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
      logger.warning(dumpEventMessage(event), exception);
    }
  }

  public void handleGroupMessage(GroupMessageEvent event) {

    if (!FurryBlack.isEnable()) return;

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

      if (isCommand(content)) {

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
      logger.warning(dumpEventMessage(event), exception);
    }

  }

  //= ==================================================================================================================
  //
  // 工具
  //
  //= ==================================================================================================================

  public void await() {
    lock.lock();
    try {
      condition.await();
    } catch (InterruptedException exception) {
      exception.printStackTrace();
    } finally {
      lock.unlock();
    }
  }

  public void signal() {
    lock.lock();
    try {
      condition.signal();
    } finally {
      lock.unlock();
    }
  }

  private boolean isCommand(String content) {
    if (content.length() < 3) return false;
    if (content.charAt(0) != COMMAND_PREFIX) return false;
    return COMMAND_PATTERN.matcher(content).find();
  }

  private String dumpEventMessage(UserMessageEvent event) {
    long marker = System.currentTimeMillis();
    StringBuilder builder = new StringBuilder();
    builder.append("[DUMP][MARK=");
    builder.append(marker);
    builder.append("]");
    builder.append(event.getSender().getNick());
    builder.append("(");
    builder.append(event.getSender().getId());
    builder.append(") -> ");
    dumpMessageChain(builder, event.getMessage());
    return builder.toString();
  }

  private String dumpEventMessage(GroupMessageEvent event) {
    long marker = System.currentTimeMillis();
    StringBuilder builder = new StringBuilder();
    builder.append("[DUMP][MARK=");
    builder.append(marker);
    builder.append("]");
    builder.append(event.getGroup().getName());
    builder.append("(");
    builder.append(event.getGroup().getId());
    builder.append(") - ");
    builder.append(event.getSender().getNameCard());
    builder.append("/");
    builder.append(event.getSender().getNick());
    builder.append("(");
    builder.append(event.getSender().getId());
    builder.append(") -> ");
    dumpMessageChain(builder, event.getMessage());
    return builder.toString();
  }

  private void dumpMessageChain(StringBuilder builder, MessageChain messageChain) {
    messageChain.forEach(it -> {
      builder.append("[");
      builder.append(it.getClass().getSimpleName());
      builder.append("/");
      builder.append(it.hashCode());
      builder.append("]");
      builder.append(it.contentToString());
      builder.append("\n");
    });
  }

  public void generateListMessage() {
    logger.info("组装用户list消息");
    MESSAGE_LIST_USERS = schema.generateUsersExecutorList();
    logger.info("组装群组list消息");
    MESSAGE_LIST_GROUP = schema.generateGroupExecutorList();
  }

  public String getMessageListUsers() {
    return MESSAGE_LIST_USERS;
  }

  public String getMessageListGroup() {
    return MESSAGE_LIST_GROUP;
  }

  //= ==================================================================================================================
  //
  // 模块相关
  //
  //= ==================================================================================================================

  //= ==================================================================================================================
  // 插件操作

  public Set<Map.Entry<String, Plugin>> getAllPlugin() {
    return schema.getAllPlugin();
  }

  public Map<String, Boolean> listAllModule() {
    return schema.listAllModule();
  }

  public Map<Runner, Boolean> listAllRunner() {
    return schema.listRunner();
  }

  public Map<Filter, Boolean> listAllFilter() {
    return schema.listFilter();
  }

  public Map<Monitor, Boolean> listAllMonitor() {
    return schema.listMonitor();
  }

  public Map<Checker, Boolean> listAllChecker() {
    return schema.listChecker();
  }

  public List<Checker> listGlobalUsersChecker() {
    return schema.listGlobalUsersChecker();
  }

  public List<Checker> listGlobalGroupChecker() {
    return schema.listGlobalGroupChecker();
  }

  public Map<String, List<Checker>> listCommandUsersChecker() {
    return schema.listCommandsUsersChecker();
  }

  public Map<String, List<Checker>> listCommandGroupChecker() {
    return schema.listCommandsGroupChecker();
  }

  public Map<Executor, Boolean> listAllExecutor() {
    return schema.listAllExecutor();
  }

  public void shutModule(String name) {
    schema.shutModule(name);
  }

  public void initModule(String name) {
    schema.initModule(name);
  }

  public void bootModule(String name) {
    schema.bootModule(name);
  }

  public void rebootModule(String name) {
    schema.rebootModule(name);
  }

  public void unloadModule(String name) {
    schema.unloadModule(name);
  }

  public String schemaVerbose() {
    return schema.verboseStatus();
  }

  public <T extends EventHandlerRunner> T getRunner(Class<T> clazz) {
    return schema.getRunner(clazz);
  }

  //= ==================================================================================================================
  //
  // BOT相关封装
  //
  //= ==================================================================================================================

  public Future<?> submit(Runnable runnable) {
    return EXECUTOR_SERVICE.submit(runnable);
  }

  public <T> Future<?> submit(Runnable runnable, T t) {
    return EXECUTOR_SERVICE.submit(runnable, t);
  }

  public Future<?> submit(Callable<?> callable) {
    return EXECUTOR_SERVICE.submit(callable);
  }

  public ScheduledFuture<?> schedule(Runnable runnable, long time, TimeUnit timeUnit) {
    return EXECUTOR_SERVICE.schedule(runnable, time, timeUnit);
  }

  public ScheduledFuture<?> schedule(Callable<?> callable, long delay, TimeUnit unit) {
    return EXECUTOR_SERVICE.schedule(callable, delay, unit);
  }

  public ScheduledFuture<?> scheduleAtFixedRate(Runnable runnable, long initialDelay, long period, TimeUnit unit) {
    return EXECUTOR_SERVICE.scheduleAtFixedRate(runnable, initialDelay, period, unit);
  }

  public ScheduledFuture<?> scheduleWithFixedDelay(Runnable runnable, long initialDelay, long delay, TimeUnit unit) {
    return EXECUTOR_SERVICE.scheduleWithFixedDelay(runnable, initialDelay, delay, unit);
  }

  //= ==================================================================================================================

  public long getBotID() {
    return bot.getId();
  }

  public ContactList<Friend> getFriends() {
    return bot.getFriends();
  }

  public ContactList<Group> getGroups() {
    return bot.getGroups();
  }

  public Stranger getStranger(long id) {
    return bot.getStranger(id);
  }

  public Stranger getStrangerOrFail(long id) {
    return bot.getStrangerOrFail(id);
  }

  public Friend getFriend(long id) {
    return bot.getFriend(id);
  }

  public Friend getFriendOrFail(long id) {
    return bot.getFriendOrFail(id);
  }

  public Group getGroup(long id) {
    return bot.getGroup(id);
  }

  public Group getGroupOrFail(long id) {
    return bot.getGroupOrFail(id);
  }

  public void sendMessage(Contact contact, Message message) {
    contact.sendMessage(message);
  }

  public void cleanNickname() {
    NICKNAME_GLOBAL.clear();
    NICKNAME_GROUPS.clear();
  }

  public void appendNickname() {
    File nicknameFile = initFile(Paths.get(FurryBlack.getConfigFolder(), "nickname.txt"));
    List<String> nicknames = readFile(nicknameFile);
    for (String line : nicknames) {
      String temp = line.trim();
      int indexOfDot = temp.indexOf(".");
      int indexOfColon = temp.indexOf(":");
      if (indexOfDot < 0) {
        logger.warning("配置无效" + line);
        continue;
      }
      if (indexOfColon < 0) {
        logger.warning("配置无效" + line);
        continue;
      }
      String group = line.substring(0, indexOfDot);
      String user = line.substring(indexOfDot + 1, indexOfColon);
      String nickname = line.substring(indexOfColon + 1);
      long userId = Long.parseLong(user);
      if ("*".equals(group)) {
        NICKNAME_GLOBAL.put(userId, nickname);
        logger.seek("全局 " + userId + " -> " + nickname);
      } else {
        long groupId = Long.parseLong(group);
        Map<Long, String> groupNicks = NICKNAME_GROUPS.computeIfAbsent(groupId, k -> new ConcurrentHashMap<>());
        groupNicks.put(userId, nickname);
        logger.seek("群内 " + groupId + "." + userId + " -> " + nickname);
      }
    }
  }

  public Map<Long, String> getNicknameGlobal() {
    return NICKNAME_GLOBAL;
  }

  public Map<Long, Map<Long, String>> getNicknameGroups() {
    return NICKNAME_GROUPS;
  }

  public String getUsersMappedNickName(User user) {
    return NICKNAME_GLOBAL.getOrDefault(user.getId(), user.getNick());
  }

  public String getUsersMappedNickName(long userId) {
    return NICKNAME_GLOBAL.getOrDefault(userId, Mirai.getInstance().queryProfile(bot, userId).getNickname());
  }

  public String getMemberMappedNickName(Member member) {
    Map<Long, String> groupMap = NICKNAME_GROUPS.get(member.getGroup().getId());
    if (groupMap != null) {
      String nickName = groupMap.get(member.getId());
      if (nickName != null) {
        return nickName;
      }
    }
    String nickName = NICKNAME_GLOBAL.get(member.getId());
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
    Map<Long, String> groupMap = NICKNAME_GROUPS.get(groupId);
    if (groupMap != null) {
      String nickName = groupMap.get(userId);
      if (nickName != null) {
        return nickName;
      }
    }
    String nickName = NICKNAME_GLOBAL.get(userId);
    if (nickName != null) {
      return nickName;
    }
    Member member = bot.getGroupOrFail(groupId).getOrFail(userId);
    String nameCard = member.getNameCard();
    if (nameCard.isBlank()) {
      return member.getNick();
    } else {
      return nameCard;
    }
  }

  //= ==================================================================================================================

  public Bot getBot() {
    return bot;
  }
}