package studio.blacktech.furryblackplus.system;

import kotlinx.serialization.json.Json;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactoryJvm;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.Events;
import net.mamoe.mirai.event.Listener;
import net.mamoe.mirai.event.ListenerHost;
import net.mamoe.mirai.message.FriendMessageEvent;
import net.mamoe.mirai.message.GroupMessageEvent;
import net.mamoe.mirai.message.TempMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.utils.BotConfiguration;
import net.mamoe.mirai.utils.SystemDeviceInfoKt;
import studio.blacktech.furryblackplus.Driver;
import studio.blacktech.furryblackplus.module.command.*;
import studio.blacktech.furryblackplus.system.command.GroupCommand;
import studio.blacktech.furryblackplus.system.common.exception.BotException;
import studio.blacktech.furryblackplus.system.common.exception.initlization.FirstBootException;
import studio.blacktech.furryblackplus.system.common.exception.initlization.InitException;
import studio.blacktech.furryblackplus.system.common.exception.initlization.InitLockedException;
import studio.blacktech.furryblackplus.system.common.exception.initlization.MisConfigException;
import studio.blacktech.furryblackplus.system.common.logger.LoggerX;
import studio.blacktech.furryblackplus.system.module.ModuleExecutor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class Systemd implements ListenerHost {


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


    //


    private static LoggerX logger;


    private static Properties CONFIG;


    // ==========================================================================================================================


    private Bot bot;


    private final Lock blockLock = new ReentrantLock(true);
    private final Condition blockCondition = blockLock.newCondition();


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


    // ==========================================================================================================================
    // 周期函数


    public void init(File FILE_CONFIG) throws BotException {


        logger = new LoggerX(this.getClass());


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


        // ==========================================================================================================================
        // 读取账号配置


        long ACCOUNT_QQ = extractLong(CONF_ACCOUNT_ID);


        logger.hint("QQ账号 " + ACCOUNT_QQ);


        String ACCOUNT_PW = CONFIG.getProperty(CONF_ACCOUNT_PW);

        int length = ACCOUNT_PW.length();

        String shadow_ACCOUNT_PW = ACCOUNT_PW.charAt(0) + "*".repeat(length - 2) + ACCOUNT_PW.charAt(length - 1);

        logger.hint("QQ密码 " + shadow_ACCOUNT_PW);


        // ==========================================================================================================================
        // 读取机器人配置


        BotConfiguration configuration = new BotConfiguration();


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


        // 创建bot


        logger.seek("机器人初始化");

        bot = BotFactoryJvm.newBot(ACCOUNT_QQ, ACCOUNT_PW, configuration);

        logger.seek("机器人类型 " + bot.getClass().getName());


        // ==========================================================================================================================
        // 注册消息路由


        Events.registerEvents(bot, this);

        Class<? extends ModuleExecutor>[] MODULES = new Class[]{
                Module_Acon.class,
                Module_Chou.class,
                Module_Dice.class,
                Module_Jrrp.class,
                Module_Roll.class,
                Module_Zhan.class,
                Module_Roulette.class
        };


        for (Class<? extends ModuleExecutor> item : MODULES) {

            try {
                ModuleExecutor instance = item.getConstructor().newInstance();
                instance.init();
                COMMAND_PROVIDER.put(instance.INFO.COMMAND_NAME, instance);
                logger.seek("注册执行器 " + instance.INFO.MODULE_ARTIFACT_NAME + " -> " + instance.INFO.COMMAND_NAME);
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }

        }


    }


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


    public void boot() {


        // ==========================================================================================================================
        // 登录QQ


        logger.info("开始登录");

        bot.login();

        logger.info("登录完成");


        // ==========================================================================================================================
        // 列出所有好友和群组


        logger.seek("> 列出所有好友");
        bot.getFriends().forEach(item -> logger.seek("  " + item.getNick() + "(" + item.getId() + ")"));
        logger.seek("> 列出所有群组");
        bot.getGroups().forEach(item -> logger.seek("  " + item.getName() + "(" + item.getId() + ") " + item.getMembers().size() + " -> " + item.getOwner().getNameCard() + "(" + item.getOwner().getId() + ")"));


        COMMAND_PROVIDER.forEach(

                (k, v) -> {
                    try {
                        v.boot();
                    } catch (BotException exception) {
                        logger.error("插件启动失败 " + v.INFO.MODULE_ARTIFACT_NAME, exception);
                        System.exit(1);
                    }
                }

        );


        // ==========================================================================================================================
        // 等待结束


        block();


    }


    public void shut() {

        await.interrupt();

        COMMAND_PROVIDER.forEach((k, v) -> {
            try {
                v.shut();
            } catch (BotException exception) {
                logger.error("关闭插件" + k + "发生异常", exception);
            }
        });

    }


    // ==========================================================================================================================
    // 注册后 Mirai 将会调用


    private final Map<String, ModuleExecutor> COMMAND_PROVIDER = new LinkedHashMap<>();


    @EventHandler(priority = Listener.EventPriority.MONITOR, concurrency = Listener.ConcurrencyKind.CONCURRENT)
    public void handleTempMessage(TempMessageEvent event) {

    }


    @EventHandler(priority = Listener.EventPriority.MONITOR, concurrency = Listener.ConcurrencyKind.CONCURRENT)
    public void handleFriendMessage(FriendMessageEvent event) {

    }


    private static final List<Long> BLACKLIST = Arrays.asList(
            2410587830L,
            412815735L
    );

    @EventHandler(priority = Listener.EventPriority.MONITOR, concurrency = Listener.ConcurrencyKind.CONCURRENT)
    public void handleGroupMessage(GroupMessageEvent event) {

        if (BLACKLIST.contains(event.getSender().getId())) return;

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
                        COMMAND_PROVIDER.forEach((k, v) -> {
                            builder.append(v.INFO.COMMAND_NAME);
                            builder.append(" ");
                            builder.append(v.INFO.MODULE_FRIENDLY_NAME);
                            builder.append(" ");
                            builder.append(v.INFO.MODULE_DESCRIPTION);
                            builder.append("\r\n");
                        });


                        event.getGroup().sendMessage(new At(message.getSender()).plus(builder.toString()));
                        break;


                    default:
                        if (COMMAND_PROVIDER.containsKey(message.getCommandName())) {
                            COMMAND_PROVIDER.get(message.getCommandName()).handleGroupMessage(message);
                        }
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


    // ==========================================================================================================================
    // 无限等待 Mirai和JCQ线程模型不同


    private void block() {
        await.start();
    }


}

