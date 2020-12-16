package studio.blacktech.furryblackplus.module;

import kotlinx.serialization.json.Json;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactoryJvm;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.Events;
import net.mamoe.mirai.event.Listener;
import net.mamoe.mirai.event.ListenerHost;
import net.mamoe.mirai.event.events.BotOfflineEvent;
import net.mamoe.mirai.message.FriendMessageEvent;
import net.mamoe.mirai.message.GroupMessageEvent;
import net.mamoe.mirai.message.TempMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.utils.BotConfiguration;
import net.mamoe.mirai.utils.SystemDeviceInfoKt;
import studio.blacktech.furryblackplus.Driver;
import studio.blacktech.furryblackplus.module.executor.Executor_Acon;
import studio.blacktech.furryblackplus.module.executor.Executor_Chou;
import studio.blacktech.furryblackplus.module.executor.Executor_Dice;
import studio.blacktech.furryblackplus.module.executor.Executor_Jrrp;
import studio.blacktech.furryblackplus.module.executor.Executor_Roll;
import studio.blacktech.furryblackplus.module.executor.Executor_Roulette;
import studio.blacktech.furryblackplus.module.executor.Executor_Zhan;
import studio.blacktech.furryblackplus.module.filter.Filter_UserDeny;
import studio.blacktech.furryblackplus.system.annotation.ComponentHandlerExecutor;
import studio.blacktech.furryblackplus.system.annotation.ComponentHandlerFilter;
import studio.blacktech.furryblackplus.system.command.FriendCommand;
import studio.blacktech.furryblackplus.system.command.GroupCommand;
import studio.blacktech.furryblackplus.system.command.TempCommand;
import studio.blacktech.furryblackplus.system.common.exception.BotException;
import studio.blacktech.furryblackplus.system.common.exception.initlization.FirstBootException;
import studio.blacktech.furryblackplus.system.common.exception.initlization.InitException;
import studio.blacktech.furryblackplus.system.common.exception.initlization.InitLockedException;
import studio.blacktech.furryblackplus.system.common.exception.initlization.MisConfigException;
import studio.blacktech.furryblackplus.system.common.logger.LoggerX;
import studio.blacktech.furryblackplus.system.handler.EventHandlerExecutor;
import studio.blacktech.furryblackplus.system.handler.EventHandlerFilter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class Systemd implements ListenerHost {


    // ==========================================================================================================================================================
    //
    // 配置项名称
    //
    // ==========================================================================================================================================================


    private static final String CONF_ACCOUNT_ID = "account.id";
    private static final String CONF_ACCOUNT_PW = "account.pw";

    private static final String CONF_BOT_DEVICE_TYPE = "bot.device.type";
    private static final String CONF_BOT_DEVICE_INFO = "bot.device.info";

    private static final String CONF_NET_HEARTBEAT_PERIOD = "net.heartbeat.period";
    private static final String CONF_NET_HEARTBEAT_TIMEOUT = "net.heartbeat.timeout";

    private static final String CONF_NET_RECONNECT_RETRY = "net.reconnect.retry";
    private static final String CONF_NET_RECONNECT_DELAY = "net.reconnect.delay";
    private static final String CONF_NET_RECONNECT_PERIOD = "net.reconnect.period";


    //


    // @formatter:off


    private static final String DEFAULT_CONFIG =


            "# =====================================\n" +
            "#\n" +
            "# FurryBlack Plus 主配置文件\n" +
            "#\n" +
            "# =====================================\n" +
            "\n" +
            "\n" +
            "# =====================================\n" +
            "# 账号设置\n" +
            "# =====================================\n" +
            "\n" +
            "\n" +
            "# 填写QQ账号(必填)\n" +
            CONF_ACCOUNT_ID + "=00000000\n" +
            "\n" +
            "# 填写QQ密码(必填)\n" +
            CONF_ACCOUNT_PW + "=0000000\n" +
            "\n" +
            "\n" +
            "# =====================================\n" +
            "# 设备设置\n" +
            "# =====================================\n" +
            "\n" +
            "\n" +
            "# 设备类型 PAD/PHONE/WATCH\n" +
            CONF_BOT_DEVICE_TYPE + "=PHONE\n" +
            "\n" +
            "# 设备信息文件\n" +
            CONF_BOT_DEVICE_INFO + "=device.info\n" +
            "\n" +
            "\n" +
            "# =====================================\n" +
            "# 网络设置\n" +
            "# =====================================\n" +
            "\n" +
            "\n" +
            "# 心跳周期\n" +
            CONF_NET_HEARTBEAT_PERIOD + "=60000\n" +
            "\n" +
            "# 心跳超时\n" +
            CONF_NET_HEARTBEAT_TIMEOUT + "=5000\n" +
            "\n" +
            "# 重连次数\n" +
            CONF_NET_RECONNECT_RETRY + "=2147483647\n" +
            "\n" +
            "# 重连等待\n" +
            CONF_NET_RECONNECT_DELAY + "=5000\n" +
            "\n" +
            "# 重连周期\n" +
            CONF_NET_RECONNECT_PERIOD + "=5000";


    // @formatter:on


    // ==========================================================================================================================================================
    //
    // 私有变量
    //
    // ==========================================================================================================================================================


    private static LoggerX logger;


    private final Lock blockLock = new ReentrantLock(true);
    private final Condition blockCondition = blockLock.newCondition();

    // ==========================================================================================================================================================
    //
    // 私有变量
    //
    // ==========================================================================================================================================================


    private Bot bot;


    private static Properties CONFIG;

    private long ACCOUNT_QQ;
    private String ACCOUNT_PW;

    private Thread await;


    // ==========================================================================================================================
    // 对象控制


    private static volatile boolean INSTANCE_LOCK = false;


    public Systemd() throws BotException {

        synchronized (Systemd.class) {
            if (INSTANCE_LOCK) throw new InitLockedException();
            INSTANCE_LOCK = true;
        }


    }


    // ==========================================================================================================================================================
    //
    // 初始化
    //
    // ==========================================================================================================================================================


    public void init(File FILE_CONFIG) throws BotException {


        logger = new LoggerX(this.getClass());


        // ==========================================================================================================================
        // 扫描执行器


        logger.seek("开始模块扫描");


        @SuppressWarnings("unchecked")
        Class<? extends EventHandlerFilter>[] FILTERS = new Class[]{
                Filter_UserDeny.class
        };

        @SuppressWarnings("unchecked")
        Class<? extends EventHandlerExecutor>[] EXECUTORS = new Class[]{
                Executor_Acon.class,
                Executor_Chou.class,
                Executor_Dice.class,
                Executor_Jrrp.class,
                Executor_Roll.class,
                Executor_Zhan.class,
                Executor_Roulette.class
        };


        for (Class<? extends EventHandlerFilter> item : FILTERS) {
            try {
                ComponentHandlerFilter annotation = item.getAnnotation(ComponentHandlerFilter.class);
                EventHandlerFilter.FilterInfo info = new EventHandlerFilter.FilterInfo(
                        annotation.name(),
                        annotation.description(),
                        annotation.privacy()
                );
                logger.seek("注册过滤器 " + item.getName());
                EventHandlerFilter instance = item.getConstructor(EventHandlerFilter.FilterInfo.class).newInstance(info);
                instance.init();
                EVENT_HANDLER_FILTER.add(instance);
            } catch (Exception exception) {
                throw new BotException("过滤器初始化失败 " + item.getName(), exception);
            }
        }


        for (Class<? extends EventHandlerExecutor> item : EXECUTORS) {
            try {
                ComponentHandlerExecutor annotation = item.getAnnotation(ComponentHandlerExecutor.class);
                EventHandlerExecutor.ExecutorInfo info = new EventHandlerExecutor.ExecutorInfo(
                        annotation.name(),
                        annotation.description(),
                        annotation.privacy(),
                        annotation.command(),
                        annotation.usage()
                );
                logger.seek("注册执行器 " + info.COMMAND + " - " + item.getName());
                EventHandlerExecutor instance = item.getConstructor(EventHandlerExecutor.ExecutorInfo.class).newInstance(info);
                instance.init();
                EVENT_HANDLER_EXECUTOR.put(instance.INFO.COMMAND, instance);
            } catch (Exception exception) {
                throw new BotException("执行器初始化失败 " + item.getName(), exception);
            }
        }


        // ==========================================================================================================================
        // 启动阻塞


        await = new Thread(() -> {
            blockLock.lock();
            try {
                blockCondition.await();
            } catch (InterruptedException ignore) {
                bot.close(null);
            }
            blockLock.unlock();
        });
        await.setContextClassLoader(getClass().getClassLoader());
        await.setDaemon(false);


        // ==========================================================================================================================
        // 读取配置


        if (!FILE_CONFIG.exists()) {

            try {
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


        if (!FILE_CONFIG.isFile()) throw new InitException("配置文件不是文件 -> " + FILE_CONFIG.getAbsolutePath());
        if (!FILE_CONFIG.canRead()) throw new InitException("配置文件无权读取 -> " + FILE_CONFIG.getAbsolutePath());


        CONFIG = new Properties();


        try {
            CONFIG.load(new FileInputStream(FILE_CONFIG));
        } catch (IOException exception) {
            logger.error("核心配置文件读取错误 即将关闭 " + FILE_CONFIG.getAbsolutePath());
            throw new BotException("核心配置文件读取错误 " + FILE_CONFIG.getAbsolutePath(), exception);
        }


        // 创建bot


        logger.seek("机器人初始化");

        BotConfiguration configuration = extractBotConfig();

        logger.seek("机器人实例化");

        bot = BotFactoryJvm.newBot(ACCOUNT_QQ, ACCOUNT_PW, configuration);

        logger.seek("机器人类型 " + bot.getClass().getName());


        // ==========================================================================================================================
        // 注册消息路由

        Events.registerEvents(bot, this);


    }


    // ==========================================================================================================================================================
    //
    // 启动
    //
    // ==========================================================================================================================================================


    public void boot() throws BotException {


        // ==========================================================================================================================
        // 登录QQ


        logger.info("开始登录");

        bot.login();


        // ==========================================================================================================================
        // 列出所有好友和群组


        logger.info("启动过滤器");


        for (EventHandlerFilter instance : EVENT_HANDLER_FILTER) {
            try {
                instance.boot();
            } catch (Exception exception) {
                throw new BotException("过滤器启动失败 " + instance.getClass().getName(), exception);
            }
        }


        logger.info("启动执行器");


        for (Map.Entry<String, EventHandlerExecutor> entry : EVENT_HANDLER_EXECUTOR.entrySet()) {
            EventHandlerExecutor instance = entry.getValue();
            try {
                instance.boot();
            } catch (Exception exception) {
                throw new BotException("执行器启动失败 " + instance.getClass().getName(), exception);
            }
        }


        // ==========================================================================================================================
        // 列出所有好友和群组


        bot.getFriends().forEach(item -> logger.seek("F " + item.getNick() + "(" + item.getId() + ")"));
        bot.getGroups().forEach(item -> logger.seek("G " + item.getName() + "(" + item.getId() + ") " + item.getMembers().size() + " -> " + item.getOwner().getNameCard() + "(" + item.getOwner().getId() + ")"));


        // ==========================================================================================================================
        // 等待结束

        await.start();

    }


    // ==========================================================================================================================================================
    //
    // 关闭
    //
    // ==========================================================================================================================================================


    public void shut() {

        if (await != null) await.interrupt();

        EVENT_HANDLER_EXECUTOR.forEach((k, v) -> {
            try {
                v.shut();
            } catch (BotException exception) {
                logger.error("关闭插件" + k + "发生异常", exception);
            }
        });

    }


    // ==========================================================================================================================================================
    //
    // 工具
    //
    // ==========================================================================================================================================================


    private long extractLong(String name) throws MisConfigException {
        String temp = CONFIG.getProperty(name);
        long result;
        try {
            result = Long.parseLong(temp);
        } catch (Exception exception) {
            throw new MisConfigException("配置解析错误 " + temp, exception);
        }
        return result;
    }


    // ==========================================================================================================================================================
    //
    // 监听器
    //
    // ==========================================================================================================================================================


    /**
     * 掉线时直接摧毁并重连
     *
     * @param event 掉线事件
     */
    // @EventHandler
    public void handleOffline(BotOfflineEvent event) {

        if (!Driver.isEnable()) return;

        if (event instanceof BotOfflineEvent.Dropped) {
            Throwable cause = ((BotOfflineEvent.Dropped) event).getCause();
            logger.hint("掉线事件" + event.getClass().getSimpleName(), cause);
        } else {
            logger.hint("掉线事件" + event.getClass().getSimpleName());
        }


        try {

            Thread.sleep(10000L);

            logger.seek("机器人初始化");
            BotConfiguration configuration = extractBotConfig();

            logger.seek("机器人实例化");
            bot = BotFactoryJvm.newBot(ACCOUNT_QQ, ACCOUNT_PW, configuration);

            logger.hint("重新登录");
            bot.login();

        } catch (Exception exception) {
            logger.error("重新登录失败", exception);
            System.exit(1);
        }


    }


    // ==========================================================================================================================
    // 过滤器


    private final List<EventHandlerFilter> EVENT_HANDLER_FILTER = new LinkedList<>();


    @EventHandler(priority = Listener.EventPriority.NORMAL, concurrency = Listener.ConcurrencyKind.CONCURRENT)
    public void handleTempMessageFilter(TempMessageEvent event) {
        for (EventHandlerFilter eventHandlerFilter : EVENT_HANDLER_FILTER) {
            if (eventHandlerFilter.handleTempMessage(event)) {
                logger.hint("临时消息被拦截 " + event.getSender().getId() + " ->" + event.getMessage());
                event.cancel();
                break;
            }
        }
    }


    @EventHandler(priority = Listener.EventPriority.NORMAL, concurrency = Listener.ConcurrencyKind.CONCURRENT)
    public void handleFriendMessageFilter(FriendMessageEvent event) {
        for (EventHandlerFilter eventHandlerFilter : EVENT_HANDLER_FILTER) {
            if (eventHandlerFilter.handleFriendMessage(event)) {
                logger.hint("好友消息被拦截 " + event.getSender().getId() + " ->" + event.getMessage());
                event.cancel();
                break;
            }
        }
    }


    @EventHandler(priority = Listener.EventPriority.NORMAL, concurrency = Listener.ConcurrencyKind.CONCURRENT)
    public void handleGroupMessageFilter(GroupMessageEvent event) {
        for (EventHandlerFilter eventHandlerFilter : EVENT_HANDLER_FILTER) {
            if (eventHandlerFilter.handleGroupMessage(event)) {
                logger.hint("群组消息被拦截 " + event.getSender().getId() + " ->" + event.getMessage());
                event.cancel();
                break;
            }
        }
    }


    // ==========================================================================================================================
    // 执行器


    private final Map<String, EventHandlerExecutor> EVENT_HANDLER_EXECUTOR = new LinkedHashMap<>();


    @EventHandler(priority = Listener.EventPriority.MONITOR, concurrency = Listener.ConcurrencyKind.CONCURRENT)
    public void handleTempMessageExecutor(TempMessageEvent event) {
        try {

            TempCommand message = new TempCommand(event.getSender(), event.getMessage());

            if (message.isCommand()) {

                switch (message.getCommandName()) {

                    case "?":
                    case "help":
                        event.getSender().sendMessage("\uD83D\uDEA7 暂不可用"); // 🚧 路障 施工中
                        break;

                    case "list":
                        StringBuilder builder = new StringBuilder();
                        EVENT_HANDLER_EXECUTOR.forEach((k, v) -> {
                            builder.append(v.INFO.COMMAND);
                            builder.append(" ");
                            builder.append(v.INFO.NAME);
                            builder.append(" ");
                            builder.append(v.INFO.DESCRIPTION);
                            builder.append("\r\n");
                        });
                        event.getSender().sendMessage(builder.toString());
                        break;

                    default:
                        if (EVENT_HANDLER_EXECUTOR.containsKey(message.getCommandName())) EVENT_HANDLER_EXECUTOR.get(message.getCommandName()).handleTempMessage(message);
                }
            }

        } catch (Exception exception) {
            logger.dump(event, exception);
        }
    }


    @EventHandler(priority = Listener.EventPriority.MONITOR, concurrency = Listener.ConcurrencyKind.CONCURRENT)
    public void handleFriendMessageExecutor(FriendMessageEvent event) {
        try {

            FriendCommand message = new FriendCommand(event.getSender(), event.getMessage());

            if (message.isCommand()) {

                switch (message.getCommandName()) {

                    case "?":
                    case "help":
                        event.getSender().sendMessage("\uD83D\uDEA7 暂不可用"); // 🚧 路障 施工中
                        break;

                    case "list":
                        StringBuilder builder = new StringBuilder();
                        EVENT_HANDLER_EXECUTOR.forEach((k, v) -> {
                            builder.append(v.INFO.COMMAND);
                            builder.append(" ");
                            builder.append(v.INFO.NAME);
                            builder.append(" ");
                            builder.append(v.INFO.DESCRIPTION);
                            builder.append("\r\n");
                        });
                        event.getSender().sendMessage(builder.toString());
                        break;

                    default:
                        if (EVENT_HANDLER_EXECUTOR.containsKey(message.getCommandName())) EVENT_HANDLER_EXECUTOR.get(message.getCommandName()).handleFriendMessage(message);
                }
            }

        } catch (Exception exception) {
            logger.dump(event, exception);
        }
    }


    @EventHandler(priority = Listener.EventPriority.MONITOR, concurrency = Listener.ConcurrencyKind.CONCURRENT)
    public void handleGroupMessageExecutor(GroupMessageEvent event) {
        try {

            GroupCommand message = new GroupCommand(event.getGroup(), event.getSender(), event.getMessage());

            if (message.isCommand()) {

                switch (message.getCommandName()) {

                    case "?":
                    case "help":
                        event.getGroup().sendMessage("\uD83D\uDEA7 暂不可用"); // 🚧 路障 施工中
                        break;

                    case "list":
                        StringBuilder builder = new StringBuilder();
                        EVENT_HANDLER_EXECUTOR.forEach((k, v) -> {
                            builder.append(v.INFO.COMMAND);
                            builder.append(" ");
                            builder.append(v.INFO.NAME);
                            builder.append(" ");
                            builder.append(v.INFO.DESCRIPTION);
                            builder.append("\r\n");
                        });
                        event.getGroup().sendMessage(new At(message.getSender()).plus("\r\n").plus(builder.toString()));
                        break;

                    default:
                        if (EVENT_HANDLER_EXECUTOR.containsKey(message.getCommandName())) EVENT_HANDLER_EXECUTOR.get(message.getCommandName()).handleGroupMessage(message);

                }
            }

        } catch (Exception exception) {
            logger.dump(event, exception);
        }

    }


    //    Face faceHandCannon = new Face(169); // QQ表情 左轮手枪
    //    Face faceMic = new Face(140); // QQ表情 麦克风
    // 🔫
    // 🧦
    // ❌
    // ✔️
    // ⭕
    // 🚧
    // 🀄


    private BotConfiguration extractBotConfig() throws MisConfigException {


        BotConfiguration configuration = new BotConfiguration();


        // ==========================================================================================================================
        // 读取账号配置


        ACCOUNT_QQ = extractLong(CONF_ACCOUNT_ID);

        logger.hint("QQ账号 " + ACCOUNT_QQ);


        ACCOUNT_PW = CONFIG.getProperty(CONF_ACCOUNT_PW);

        int length = ACCOUNT_PW.length();
        String shadow_ACCOUNT_PW = ACCOUNT_PW.charAt(0) + "*".repeat(length - 2) + ACCOUNT_PW.charAt(length - 1);

        logger.hint("QQ密码 " + shadow_ACCOUNT_PW);


        // ==========================================================================================================================
        // 读取机器人配置


        // 设备类型


        String DEVICE_TYPE = CONFIG.getProperty(CONF_BOT_DEVICE_TYPE);


        switch (DEVICE_TYPE) {

            case "PAD":
            case "537062409":
                configuration.setProtocol(BotConfiguration.MiraiProtocol.ANDROID_PAD);
                logger.hint("设备模式 " + DEVICE_TYPE + " 安卓平板");
                break;

            case "PHONE":
            case "537062845":
                configuration.setProtocol(BotConfiguration.MiraiProtocol.ANDROID_PHONE);
                logger.hint("设备模式 " + DEVICE_TYPE + " 安卓手机");
                break;

            case "WATCH":
            case "537061176":
                configuration.setProtocol(BotConfiguration.MiraiProtocol.ANDROID_WATCH);
                logger.hint("设备模式 " + DEVICE_TYPE + " 安卓手表");
                break;

            default:
                logger.error("设备模式配置错误");
                throw new MisConfigException(CONF_BOT_DEVICE_TYPE + "必须是填 PAD PHONE WATCH 之一 大写无符号");

        }


        // 设备信息


        String temp_DEVICE_INFO = CONFIG.getProperty(CONF_BOT_DEVICE_INFO);

        File DEVICE_INFO = Paths.get(Driver.getConfigFolder(), temp_DEVICE_INFO).toFile();

        if (DEVICE_INFO.exists()) {

            if (!DEVICE_INFO.isFile()) {
                String temp = "设备信息配置错误 指定的路径不是文件 " + DEVICE_INFO.getAbsolutePath();
                logger.error(temp);
                throw new MisConfigException(temp);
            }

            if (!DEVICE_INFO.canRead()) {
                String temp = "设备信息配置错误 指定的文件无权读取 " + DEVICE_INFO.getAbsolutePath();
                logger.error(temp);
                throw new MisConfigException(temp);
            }

            logger.hint("设备信息 " + DEVICE_INFO.getName());

        } else {

            logger.hint("设备信息不存在 将由Mirai生成");

        }


        configuration.setDeviceInfo(context -> SystemDeviceInfoKt.loadAsDeviceInfo(DEVICE_INFO, Json.Default, context));


        // 心跳参数


        long NET_HEARTBEAT_PERIOD = extractLong(CONF_NET_HEARTBEAT_PERIOD);
        long NET_HEARTBEAT_TIMEOUT = extractLong(CONF_NET_HEARTBEAT_TIMEOUT);

        logger.hint("心跳间隔 " + NET_HEARTBEAT_PERIOD);
        logger.hint("心跳超时 " + NET_HEARTBEAT_TIMEOUT);

        configuration.setHeartbeatPeriodMillis(NET_HEARTBEAT_PERIOD);
        configuration.setHeartbeatTimeoutMillis(NET_HEARTBEAT_TIMEOUT);


        // 重连参数


        long NET_RECONNECT_RETRY = extractLong(CONF_NET_RECONNECT_RETRY);
        long NET_RECONNECT_DELAY = extractLong(CONF_NET_RECONNECT_DELAY);
        long NET_RECONNECT_PERIOD = extractLong(CONF_NET_RECONNECT_PERIOD);

        int RECONNECT_TIME;

        if (NET_RECONNECT_RETRY > Integer.MAX_VALUE) {
            logger.warning(CONF_NET_RECONNECT_RETRY + " 最大可接受值为 " + Integer.MAX_VALUE);
            RECONNECT_TIME = Integer.MAX_VALUE;
        } else if (NET_RECONNECT_RETRY < -1) {
            RECONNECT_TIME = Integer.MAX_VALUE;
        } else {
            RECONNECT_TIME = Long.valueOf(NET_RECONNECT_RETRY).intValue();
        }


        logger.hint("重连间隔 " + NET_RECONNECT_PERIOD);
        logger.hint("重连延迟 " + NET_RECONNECT_DELAY);
        logger.hint("重连次数 " + RECONNECT_TIME);


        configuration.setReconnectPeriodMillis(NET_RECONNECT_PERIOD);
        configuration.setFirstReconnectDelayMillis(NET_RECONNECT_DELAY);
        configuration.setReconnectionRetryTimes(RECONNECT_TIME);


        // 传入日志


        configuration.setBotLoggerSupplier(bot -> new LoggerX("MiraiBot"));
        configuration.setNetworkLoggerSupplier(bot -> new LoggerX("MiraiNet"));


        return configuration;
    }


}


