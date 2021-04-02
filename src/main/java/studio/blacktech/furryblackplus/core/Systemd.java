package studio.blacktech.furryblackplus.core;


import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.Mirai;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.ContactList;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Stranger;
import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.BotInvitedJoinGroupRequestEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.NewFriendRequestEvent;
import net.mamoe.mirai.event.events.UserMessageEvent;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.utils.BotConfiguration;
import org.reflections.Reflections;
import org.reflections.ReflectionsException;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ConfigurationBuilder;
import studio.blacktech.furryblackplus.Driver;
import studio.blacktech.furryblackplus.core.annotation.Api;
import studio.blacktech.furryblackplus.core.annotation.Executor;
import studio.blacktech.furryblackplus.core.annotation.Filter;
import studio.blacktech.furryblackplus.core.annotation.Monitor;
import studio.blacktech.furryblackplus.core.annotation.Runner;
import studio.blacktech.furryblackplus.core.exception.BotException;
import studio.blacktech.furryblackplus.core.exception.initlization.FirstBootException;
import studio.blacktech.furryblackplus.core.exception.initlization.InitException;
import studio.blacktech.furryblackplus.core.exception.initlization.InitLockedException;
import studio.blacktech.furryblackplus.core.exception.initlization.MisConfigException;
import studio.blacktech.furryblackplus.core.interfaces.AbstractEventHandler;
import studio.blacktech.furryblackplus.core.interfaces.EventHandlerExecutor;
import studio.blacktech.furryblackplus.core.interfaces.EventHandlerFilter;
import studio.blacktech.furryblackplus.core.interfaces.EventHandlerMonitor;
import studio.blacktech.furryblackplus.core.interfaces.EventHandlerRunner;
import studio.blacktech.furryblackplus.core.utilties.Command;
import studio.blacktech.furryblackplus.core.utilties.HashTool;
import studio.blacktech.furryblackplus.core.utilties.LoggerX;

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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Api("系统核心路由")
public final class Systemd {


    // ==========================================================================================================================================================
    //
    // 配置项名称
    //
    // ==========================================================================================================================================================


    private static final String CONF_ACCOUNT_ID = "account.id";
    private static final String CONF_ACCOUNT_PW = "account.pw";

    private static final String CONF_CPU_THREADS = "cpu.threads";

    private static final String CONF_BOT_DEVICE_TYPE = "bot.device.type";
    private static final String CONF_BOT_DEVICE_INFO = "bot.device.info";

    private static final String CONF_BOT_COMMAND_PREFIX = "bot.command.prefix";
    private static final String CONF_BOT_PACKAGE_PREFIX = "bot.package.prefix";


    private static final String CONF_NET_HEARTBEAT_PERIOD = "net.heartbeat.period";
    private static final String CONF_NET_HEARTBEAT_TIMEOUT = "net.heartbeat.timeout";

    private static final String CONF_NET_RECONNECT_RETRY = "net.reconnect.retry";
    private static final String CONF_NET_RECONNECT_DELAY = "net.reconnect.delay";
    private static final String CONF_NET_RECONNECT_PERIOD = "net.reconnect.period";


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
    CONF_CPU_THREADS+ "=4\n"+
    "# 命令识别前缀\n" +
    CONF_BOT_COMMAND_PREFIX + "=\"/\"\n" +
    "# 插件扫描路径\n" +
    CONF_BOT_PACKAGE_PREFIX + "=studio.blacktech.furryblackplus.extensions\n" +
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
    CONF_NET_RECONNECT_RETRY + "=10\n" +
    "# 重连等待\n" +
    CONF_NET_RECONNECT_DELAY + "=5000\n" +
    "# 重连周期\n" +
    CONF_NET_RECONNECT_PERIOD + "=5000";


    // @formatter:on


    // ==========================================================================================================================================================
    //
    // 私有对象
    //
    // ==========================================================================================================================================================


    private final LoggerX logger = new LoggerX(this.getClass());

    private Bot bot;

    private char COMMAND_PREFIX = '/';
    private Pattern COMMAND_PATTERN;


    private Map<Long, String> NICKNAME;


    private String MESSAGE_INFO;
    private String MESSAGE_EULA;
    private String MESSAGE_HELP;
    private String MESSAGE_LIST_USERS;
    private String MESSAGE_LIST_GROUP;

    private ThreadPoolExecutor MONITOR_SERVICE;
    private ScheduledExecutorService SCHEDULERS_POOL;

    private Map<String, AbstractEventHandler> MODULES; // 所有模块及注册名

    private Map<String, EventHandlerRunner> EVENT_RUNNER;
    private Map<String, EventHandlerMonitor> EVENT_MONITOR;

    private List<EventHandlerMonitor> EVENT_MONITOR_USERS; // 私聊过滤器注册
    private List<EventHandlerMonitor> EVENT_MONITOR_GROUP; // 群聊过滤器注册

    private Map<String, EventHandlerFilter> EVENT_FILTER;

    private List<EventHandlerFilter> EVENT_FILTER_USERS; // 私聊过滤器注册
    private List<EventHandlerFilter> EVENT_FILTER_GROUP; // 群聊过滤器注册

    private Map<String, EventHandlerExecutor> EVENT_EXECUTOR;

    private Map<String, EventHandlerExecutor> EVENT_EXECUTOR_USERS; // 私聊执行器注册
    private Map<String, EventHandlerExecutor> EVENT_EXECUTOR_GROUP; // 群聊执行器注册


    // ==========================================================================================================================
    // 对象控制
    // 🔫 🧦 ❌ ✔️ ⭕ 🚧 🀄

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


    public void init(File FOLDER_CONFIG) throws InitException {


        // ==========================================================================================================================
        // 初始化配置文件


        logger.hint("初始化Systemd配置文件");


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

        if (!FILE_CONFIG.isFile()) throw new InitException("配置文件不是文件 -> " + FILE_CONFIG.getAbsolutePath());
        if (!FILE_CONFIG.canRead()) throw new InitException("配置文件无权读取 -> " + FILE_CONFIG.getAbsolutePath());


        // ==========================================================================================================================
        // 加载配置


        Properties config = new Properties();

        try (FileInputStream inStream = new FileInputStream(FILE_CONFIG)) {
            config.load(inStream);
        } catch (IOException exception) {
            logger.error("核心配置文件读取错误 即将关闭 " + FILE_CONFIG.getAbsolutePath());
            throw new InitException("核心配置文件读取错误 " + FILE_CONFIG.getAbsolutePath(), exception);
        }


        // ==========================================================================================================================
        // 读取配置


        String prefix = config.getProperty(CONF_BOT_COMMAND_PREFIX);

        if (prefix == null || prefix.isEmpty() || prefix.isBlank()) {

            logger.warning("指定的命令前缀不可用 将自动设置为默认值: /");

        } else {

            int length = prefix.length();

            switch (length) {
                case 1:
                    COMMAND_PREFIX = prefix.charAt(0);
                    break;
                case 3:
                    COMMAND_PREFIX = prefix.charAt(1);
                    break;
                default:
                    logger.warning("指定的命令前缀不可用 将自动设置为默认值: /");

            }

            logger.seek("命令前缀 " + COMMAND_PREFIX);

            COMMAND_PATTERN = Pattern.compile("^" + COMMAND_PREFIX + "[a-z]{3,8}");

        }


        // ==========================================================================================================================
        // 读取模板


        logger.hint("初始化预生成消息");

        File FILE_EULA = Paths.get(Driver.getConfigFolder(), "message_eula.txt").toFile();
        File FILE_INFO = Paths.get(Driver.getConfigFolder(), "message_info.txt").toFile();
        File FILE_HELP = Paths.get(Driver.getConfigFolder(), "message_help.txt").toFile();

        logger.info("初始化eula");
        MESSAGE_EULA = readFile(FILE_EULA);

        logger.info("初始化info");
        MESSAGE_INFO = readFile(FILE_INFO);

        logger.info("初始化help");
        MESSAGE_HELP = readFile(FILE_HELP);

        MESSAGE_EULA = MESSAGE_EULA.replaceAll("\\$\\{VERSION}", Driver.getAppVersion());
        MESSAGE_INFO = MESSAGE_INFO.replaceAll("\\$\\{VERSION}", Driver.getAppVersion());
        MESSAGE_HELP = MESSAGE_HELP.replaceAll("\\$\\{VERSION}", Driver.getAppVersion());

        String SHA_EULA = HashTool.SHA256(MESSAGE_EULA);
        String SHA_INFO = HashTool.SHA256(MESSAGE_INFO);

        MESSAGE_EULA = MESSAGE_EULA + "\r\nSHA-256: " + SHA_EULA;
        MESSAGE_INFO = MESSAGE_INFO + "\r\nSHA-256: " + SHA_INFO;

        logger.info("EULA Digest " + SHA_EULA);
        logger.info("INFO Digest " + SHA_INFO);


        // ==========================================================================================================================
        // 加载常用昵称


        NICKNAME = new HashMap<>();


        File commonNick = initFile(Paths.get(Driver.getConfigFolder(), "nickname.txt").toFile());

        try (
            FileReader fileReader = new FileReader(commonNick);
            BufferedReader bufferedReader = new BufferedReader(fileReader)
        ) {

            String line;
            while ((line = bufferedReader.readLine()) != null) {

                if (!line.contains(":")) {
                    logger.warning("配置无效 " + line);
                    continue;
                }

                String[] temp1 = line.split(":");

                if (temp1.length != 2) {
                    logger.warning("配置无效 " + line);
                    continue;
                }

                long userID = Long.parseLong(temp1[0]);
                NICKNAME.put(userID, temp1[1].trim());

            }

        } catch (Exception exception) {
            throw new InitException("昵称映射表读取失败", exception);
        }


        // ==========================================================================================================================
        // 读取机器人配置


        logger.hint("加载机器人配置");
        BotConfiguration configuration = new BotConfiguration();

        File cacheFolder = Paths.get(FOLDER_CONFIG.getAbsolutePath(), "cache").toFile();

        configuration.setCacheDir(cacheFolder);


        // ==========================================================================================================================
        // 读取账号配置


        long ACCOUNT_QQ = parseLong(config.getProperty(CONF_ACCOUNT_ID));

        logger.seek("QQ账号 " + ACCOUNT_QQ);

        String ACCOUNT_PW = config.getProperty(CONF_ACCOUNT_PW);

        int length = ACCOUNT_PW.length();

        if (ACCOUNT_PW.charAt(0) == '\"' && ACCOUNT_PW.charAt(length - 1) == '\"') {
            ACCOUNT_PW = ACCOUNT_PW.substring(1, length - 1);
        }

        length = ACCOUNT_PW.length();

        if (Driver.isDebug()) {
            logger.warning("QQ密码 " + ACCOUNT_PW + "关闭调试模式以给此条日志打码");
        } else {
            String shadow_ACCOUNT_PW = ACCOUNT_PW.charAt(0) + "*".repeat(length - 2) + ACCOUNT_PW.charAt(length - 1);
            logger.seek("QQ密码 " + shadow_ACCOUNT_PW);
        }

        // ==========================================================================================================================
        // 读取设备配置


        // 设备类型


        String DEVICE_TYPE = config.getProperty(CONF_BOT_DEVICE_TYPE);


        switch (DEVICE_TYPE) {

            case "PAD":
            case "537062409":
                configuration.setProtocol(BotConfiguration.MiraiProtocol.ANDROID_PAD);
                logger.seek("设备模式 " + DEVICE_TYPE + " 安卓平板");
                break;

            case "PHONE":
            case "537062845":
                configuration.setProtocol(BotConfiguration.MiraiProtocol.ANDROID_PHONE);
                logger.seek("设备模式 " + DEVICE_TYPE + " 安卓手机");
                break;

            case "WATCH":
            case "537061176":
                configuration.setProtocol(BotConfiguration.MiraiProtocol.ANDROID_WATCH);
                logger.seek("设备模式 " + DEVICE_TYPE + " 安卓手表");
                break;

            default:
                logger.error("设备模式配置错误");
                throw new MisConfigException(CONF_BOT_DEVICE_TYPE + "必须是填 PAD PHONE WATCH 之一 大写无符号");

        }


        // 设备信息


        String DEVICE_INFO = config.getProperty(CONF_BOT_DEVICE_INFO);

        File deviceInfo = Paths.get(Driver.getConfigFolder(), DEVICE_INFO).toFile();

        if (deviceInfo.exists()) {

            if (!deviceInfo.isFile()) {
                String temp = "设备信息配置错误 指定的路径不是文件 " + deviceInfo.getAbsolutePath();
                logger.error(temp);
                throw new MisConfigException(temp);
            }

            if (!deviceInfo.canRead()) {
                String temp = "设备信息配置错误 指定的文件无权读取 " + deviceInfo.getAbsolutePath();
                logger.error(temp);
                throw new MisConfigException(temp);
            }

            logger.seek("设备信息 " + deviceInfo.getName());

        } else {

            logger.seek("设备信息不存在 将由Mirai生成");

        }

        configuration.fileBasedDeviceInfo(deviceInfo.getAbsolutePath());


        // ==========================================================================================================================
        // 读取网络配置


        // 心跳参数


        long NET_HEARTBEAT_PERIOD = parseLong(config.getProperty(CONF_NET_HEARTBEAT_PERIOD));
        long NET_HEARTBEAT_TIMEOUT = parseLong(config.getProperty(CONF_NET_HEARTBEAT_TIMEOUT));

        logger.seek("心跳间隔 " + NET_HEARTBEAT_PERIOD);
        logger.seek("心跳超时 " + NET_HEARTBEAT_TIMEOUT);

        configuration.setHeartbeatPeriodMillis(NET_HEARTBEAT_PERIOD);
        configuration.setHeartbeatTimeoutMillis(NET_HEARTBEAT_TIMEOUT);


        // 重连参数


        long NET_RECONNECT_DELAY = parseLong(config.getProperty(CONF_NET_RECONNECT_DELAY));
        long NET_RECONNECT_PERIOD = parseLong(config.getProperty(CONF_NET_RECONNECT_PERIOD));
        int NET_RECONNECT_RETRY = parseInteger(config.getProperty(CONF_NET_RECONNECT_RETRY));

        logger.seek("重连间隔 " + NET_RECONNECT_PERIOD);
        logger.seek("重连延迟 " + NET_RECONNECT_DELAY);
        logger.seek("重连次数 " + NET_RECONNECT_RETRY);

        configuration.setReconnectPeriodMillis(NET_RECONNECT_PERIOD);
        configuration.setReconnectionRetryTimes(NET_RECONNECT_RETRY);
        configuration.setFirstReconnectDelayMillis(NET_RECONNECT_DELAY);


        // 传入日志


        configuration.setBotLoggerSupplier(botInstance -> new LoggerX("MiraiBot"));
        configuration.setNetworkLoggerSupplier(botInstance -> new LoggerX("MiraiNet"));


        // ==========================================================================================================================
        // 创建机器人


        logger.hint("初始化机器人");
        bot = BotFactory.INSTANCE.newBot(ACCOUNT_QQ, ACCOUNT_PW, configuration);


        logger.info("机器人类型 " + bot.getClass().getName());


        // ==========================================================================================================================
        // 初始化注册


        MODULES = new LinkedHashMap<>();

        EVENT_RUNNER = new LinkedHashMap<>();

        EVENT_MONITOR = new LinkedHashMap<>();
        EVENT_MONITOR_USERS = new LinkedList<>();
        EVENT_MONITOR_GROUP = new LinkedList<>();

        EVENT_FILTER = new LinkedHashMap<>();
        EVENT_FILTER_USERS = new LinkedList<>();
        EVENT_FILTER_GROUP = new LinkedList<>();

        EVENT_EXECUTOR = new LinkedHashMap<>();
        EVENT_EXECUTOR_USERS = new LinkedHashMap<>();
        EVENT_EXECUTOR_GROUP = new LinkedHashMap<>();

        int poolSize = parseInteger(config.getProperty(CONF_CPU_THREADS));

        logger.seek("监听器线程池设置为" + poolSize + "线程");

        MONITOR_SERVICE = (ThreadPoolExecutor) Executors.newFixedThreadPool(poolSize);
        SCHEDULERS_POOL = Executors.newScheduledThreadPool(poolSize);

        // ==========================================================================================================================
        // 扫描模块


        logger.hint("开始模块扫描");

        String RAW_PACKAGE_PREFIX = config.getProperty(CONF_BOT_PACKAGE_PREFIX);

        logger.seek("模块扫描路径 " + RAW_PACKAGE_PREFIX);

        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();

        if (RAW_PACKAGE_PREFIX.indexOf(";") > 0) {
            String[] packages = RAW_PACKAGE_PREFIX.split(";");
            for (String packagePath : packages) {
                String trim = packagePath.trim();
                configurationBuilder.forPackages(trim);
                logger.info("添加扫描路径 " + trim);
            }
        } else {
            String trim = RAW_PACKAGE_PREFIX.trim();
            configurationBuilder.forPackages(trim);
            logger.info("添加扫描路径 " + trim);
        }

        configurationBuilder.addScanners(new SubTypesScanner());
        Reflections reflections = new Reflections(configurationBuilder);

        // ==========================================================================================================================
        // 分析扫描结果

        Set<Class<? extends EventHandlerRunner>> RUNNERS = new HashSet<>();
        Set<Class<? extends EventHandlerMonitor>> MONITORS = new HashSet<>();
        Set<Class<? extends EventHandlerFilter>> FILTERS = new HashSet<>();
        Set<Class<? extends EventHandlerExecutor>> EXECUTORS = new HashSet<>();

        try {
            RUNNERS = reflections.getSubTypesOf(EventHandlerRunner.class);
            logger.hint("扫描到以下定时器");
            RUNNERS.forEach(item -> logger.info(item.getName()));
        } catch (Exception exception) {
            if (exception instanceof ReflectionsException && "Scanner SubTypesScanner was not configured".equalsIgnoreCase(exception.getMessage())) {
                logger.info("没有扫描到任何定时器");
            } else {
                logger.warning("扫描定时器时发生异常", exception);
            }
        }

        try {
            MONITORS = reflections.getSubTypesOf(EventHandlerMonitor.class);
            logger.hint("扫描到以下监视器");
            MONITORS.forEach(item -> logger.info(item.getName()));
        } catch (Exception exception) {
            if (exception instanceof ReflectionsException && "Scanner SubTypesScanner was not configured".equalsIgnoreCase(exception.getMessage())) {
                logger.info("没有扫描到任何监视器");
            } else {
                logger.warning("扫描监视器时发生异常", exception);
            }
        }

        try {
            FILTERS = reflections.getSubTypesOf(EventHandlerFilter.class);
            logger.hint("扫描到以下过滤器");
            FILTERS.forEach(item -> logger.info(item.getName()));
        } catch (Exception exception) {
            if (exception instanceof ReflectionsException && "Scanner SubTypesScanner was not configured".equalsIgnoreCase(exception.getMessage())) {
                logger.info("没有扫描到任何过滤器");
            } else {
                logger.warning("扫描过滤器时发生异常", exception);
            }
        }

        try {
            EXECUTORS = reflections.getSubTypesOf(EventHandlerExecutor.class);
            logger.hint("扫描到以下执行器");
            EXECUTORS.forEach(item -> logger.info(item.getName()));
        } catch (Exception exception) {
            if (exception instanceof ReflectionsException && "Scanner SubTypesScanner was not configured".equalsIgnoreCase(exception.getMessage())) {
                logger.info("没有扫描到任何执行器");
            } else {
                logger.warning("扫描执行器时发生异常", exception);
            }
        }


        if (RUNNERS.size() + MONITORS.size() + FILTERS.size() + EXECUTORS.size() == 0) {
            logger.warning("没有扫描到任何模块 请检查扫描路径");
        }


        // ==========================================================================================================================
        // 注册定时器


        logger.hint("初始化定时器");
        for (Class<? extends EventHandlerRunner> clazz : RUNNERS) {
            try {
                if (!clazz.isAnnotationPresent(Runner.class)) {
                    logger.warning("发现无注解定时器 " + clazz.getName());
                    continue;
                }
                Runner annotation = clazz.getAnnotation(Runner.class);
                EventHandlerRunner.RunnerInfo info = new EventHandlerRunner.RunnerInfo(
                    annotation.name(),
                    annotation.artificial(),
                    annotation.description(),
                    annotation.privacy()
                );
                if (MODULES.containsKey(info.ARTIFICIAL)) {
                    AbstractEventHandler handler = MODULES.get(info.ARTIFICIAL);
                    throw new InitException("注册监听器失败 " + clazz.getName() + " 同名已存在 -> " + handler.getClass().getName());
                }
                EventHandlerRunner instance = clazz.getConstructor(EventHandlerRunner.RunnerInfo.class).newInstance(info);
                instance.init();
                logger.info("注册定时器 " + info.ARTIFICIAL + " - " + clazz.getName());
                MODULES.put(info.ARTIFICIAL, instance);
                EVENT_RUNNER.put(info.ARTIFICIAL, instance);
            } catch (Exception exception) {
                throw new InitException("定时器初始化失败 " + clazz.getName(), exception);
            }
        }


        // ==========================================================================================================================
        // 注册监听器


        logger.hint("初始化监听器");
        for (Class<? extends EventHandlerMonitor> clazz : MONITORS) {
            try {
                if (!clazz.isAnnotationPresent(Monitor.class)) {
                    logger.warning("发现无注解监听器 " + clazz.getName());
                    continue;
                }
                Monitor annotation = clazz.getAnnotation(Monitor.class);
                if (!annotation.users() && !annotation.group()) {
                    logger.warning("发现无用监听器 " + clazz.getName());
                    continue;
                }
                EventHandlerMonitor.MonitorInfo info = new EventHandlerMonitor.MonitorInfo(
                    annotation.name(),
                    annotation.artificial(),
                    annotation.description(),
                    annotation.privacy()
                );
                if (MODULES.containsKey(info.ARTIFICIAL)) {
                    AbstractEventHandler handler = MODULES.get(info.ARTIFICIAL);
                    throw new InitException("注册监听器失败 " + clazz.getName() + " 同名已存在 -> " + handler.getClass().getName());
                }
                EventHandlerMonitor instance = clazz.getConstructor(EventHandlerMonitor.MonitorInfo.class).newInstance(info);
                instance.init();
                logger.info("注册定时器 " + info.ARTIFICIAL + " - " + clazz.getName());
                MODULES.put(info.ARTIFICIAL, instance);
                EVENT_MONITOR.put(info.ARTIFICIAL, instance);
                if (annotation.users()) EVENT_MONITOR_USERS.add(instance);
                if (annotation.group()) EVENT_MONITOR_GROUP.add(instance);
            } catch (Exception exception) {
                throw new InitException("监听器初始化失败 " + clazz.getName(), exception);
            }
        }


        // ==========================================================================================================================
        // 注册过滤器


        logger.hint("初始化过滤器链");
        for (Class<? extends EventHandlerFilter> clazz : FILTERS) {
            try {
                if (!clazz.isAnnotationPresent(Filter.class)) {
                    logger.warning("发现无注解过滤器 " + clazz.getName());
                    continue;
                }
                Filter annotation = clazz.getAnnotation(Filter.class);
                if (!annotation.users() && !annotation.group()) {
                    logger.warning("发现无用过滤器 " + clazz.getName());
                    continue;
                }
                EventHandlerFilter.FilterInfo info = new EventHandlerFilter.FilterInfo(
                    annotation.name(),
                    annotation.artificial(),
                    annotation.description(),
                    annotation.privacy()
                );
                if (MODULES.containsKey(info.ARTIFICIAL)) {
                    AbstractEventHandler handler = MODULES.get(info.ARTIFICIAL);
                    throw new InitException("注册过滤器失败 " + clazz.getName() + " 同名已存在 -> " + handler.getClass().getName());
                }
                EventHandlerFilter instance = clazz.getConstructor(EventHandlerFilter.FilterInfo.class).newInstance(info);
                instance.init();
                logger.info("注册过滤器 " + info.ARTIFICIAL + " - " + clazz.getName());
                MODULES.put(info.ARTIFICIAL, instance);
                EVENT_FILTER.put(info.ARTIFICIAL, instance);
                if (annotation.users()) EVENT_FILTER_USERS.add(instance);
                if (annotation.group()) EVENT_FILTER_GROUP.add(instance);
            } catch (Exception exception) {
                throw new InitException("过滤器初始化失败 " + clazz.getName(), exception);
            }
        }


        // ==========================================================================================================================
        // 初始化执行器


        logger.hint("初始化执行器链");

        for (Class<? extends EventHandlerExecutor> clazz : EXECUTORS) {
            try {
                if (!clazz.isAnnotationPresent(Executor.class)) {
                    logger.warning("发现无注解执行器 " + clazz.getName());
                    continue;
                }
                Executor annotation = clazz.getAnnotation(Executor.class);
                if (!annotation.users() && !annotation.group()) {
                    logger.warning("发现无用执行器 " + clazz.getName());
                    continue;
                }
                EventHandlerExecutor.ExecutorInfo info = new EventHandlerExecutor.ExecutorInfo(
                    annotation.name(),
                    annotation.artificial(),
                    annotation.description(),
                    annotation.privacy(),
                    annotation.command(),
                    annotation.usage()
                );
                if (MODULES.containsKey(info.ARTIFICIAL)) {
                    AbstractEventHandler handler = MODULES.get(info.ARTIFICIAL);
                    throw new InitException("注册过滤器失败 " + clazz.getName() + " 同名已存在 -> " + handler.getClass().getName());
                }
                if (EVENT_EXECUTOR.containsKey(info.ARTIFICIAL)) {
                    EventHandlerExecutor handler = EVENT_EXECUTOR.get(info.ARTIFICIAL);
                    throw new InitException("注册执行器失败 " + info.COMMAND + " " + clazz.getName() + " 命令已存在 -> " + handler.getClass().getName());
                }
                EventHandlerExecutor instance = clazz.getConstructor(EventHandlerExecutor.ExecutorInfo.class).newInstance(info);
                instance.init();
                logger.info("注册入执行链 " + info.ARTIFICIAL + " - " + info.COMMAND + " > " + clazz.getName());
                MODULES.put(info.ARTIFICIAL, instance);
                EVENT_EXECUTOR.put(info.ARTIFICIAL, instance);
                if (annotation.users()) EVENT_EXECUTOR_USERS.put(info.COMMAND, instance);
                if (annotation.group()) EVENT_EXECUTOR_GROUP.put(info.COMMAND, instance);
            } catch (Exception exception) {
                throw new InitException("执行器初始化失败 " + clazz.getName(), exception);
            }
        }


        logger.info("组装用户list消息");
        MESSAGE_LIST_USERS = generateListMessage(EVENT_EXECUTOR_USERS.entrySet());


        logger.info("组装群组list消息");
        MESSAGE_LIST_GROUP = generateListMessage(EVENT_EXECUTOR_GROUP.entrySet());


        // =============================================================================================================
        // 注册消息路由


        GlobalEventChannel.INSTANCE.subscribeAlways(UserMessageEvent.class, this::handleUsersMessage);
        GlobalEventChannel.INSTANCE.subscribeAlways(GroupMessageEvent.class, this::handleGroupMessage);
        GlobalEventChannel.INSTANCE.subscribeAlways(NewFriendRequestEvent.class, this::handleFriendRequest);
        GlobalEventChannel.INSTANCE.subscribeAlways(BotInvitedJoinGroupRequestEvent.class, this::handleInvitedRequest);


    }


    // ==========================================================================================================================================================
    //
    // 启动
    //
    // ==========================================================================================================================================================


    public void boot() throws BotException {


        // ==========================================================================================================================
        // 登录QQ


        if (Driver.isDryRun()) {
            logger.warning("指定了--dry-run参数 跳过真实登录");
        } else {
            logger.hint("开始登录");
            bot.login();
        }


        // ==========================================================================================================================
        // 启动模块


        logger.hint("启动定时器");
        for (Map.Entry<String, EventHandlerRunner> entry : EVENT_RUNNER.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            try {
                entry.getValue().boot();
                logger.info("启动定时器成功 " + k + " -> " + v.getClass().getName());
            } catch (Exception exception) {
                throw new BotException("启动定时器失败 " + v.getClass().getName(), exception);
            }
        }


        logger.hint("启动监听器");
        for (Map.Entry<String, EventHandlerMonitor> entry : EVENT_MONITOR.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            try {
                entry.getValue().boot();
                logger.info("启动监听器成功 " + k + " -> " + v.getClass().getName());
            } catch (Exception exception) {
                throw new BotException("启动监听器失败 " + v.getClass().getName(), exception);
            }
        }


        logger.hint("启动过滤器");
        for (Map.Entry<String, EventHandlerFilter> entry : EVENT_FILTER.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            try {
                entry.getValue().boot();
                logger.info("启动过滤器成功 " + k + " -> " + v.getClass().getName());
            } catch (Exception exception) {
                throw new BotException("启动过滤器失败 " + v.getClass().getName(), exception);
            }
        }


        logger.hint("启动执行器");
        for (Map.Entry<String, EventHandlerExecutor> entry : EVENT_EXECUTOR.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            try {
                entry.getValue().boot();
                logger.info("启动执行器成功 " + k + " -> " + v.getClass().getName());
            } catch (Exception exception) {
                throw new BotException("启动执行器失败 " + v.getClass().getName(), exception);
            }
        }


        // ==========================================================================================================================
        // 列出所有好友和群组


        if (!Driver.isDryRun()) {

            logger.hint("机器人昵称 " + bot.getNick());

            logger.hint("所有好友");
            bot.getFriends().forEach(item -> logger.info(Driver.getFormattedNickName(item)));

            logger.hint("所有群组");
            bot.getGroups().forEach(item -> logger.info(Driver.getGroupInfo(item)));

        }

    }


    // ==========================================================================================================================================================
    //
    // 关闭
    //
    // ==========================================================================================================================================================


    /**
     * 即使发生异常也应该继续执行下一个
     */
    public void shut() {


        // ==========================================================================================================================
        // 关闭模块

        logger.hint("关闭执行器");
        for (Map.Entry<String, EventHandlerExecutor> entry : EVENT_EXECUTOR.entrySet()) {
            try {
                entry.getValue().shut();
                logger.info("关闭执行器成功 " + entry.getKey() + " -> " + entry.getValue().getClass().getName());
            } catch (Exception exception) {
                logger.error("关闭执行器失败 " + entry.getValue().getClass().getName(), exception);
            }
        }

        logger.hint("关闭过滤器");
        for (Map.Entry<String, EventHandlerFilter> entry : EVENT_FILTER.entrySet()) {
            try {
                entry.getValue().shut();
                logger.info("关闭过滤器成功 " + entry.getKey() + " -> " + entry.getValue().getClass().getName());
            } catch (Exception exception) {
                logger.error("关闭过滤器失败 " + entry.getValue().getClass().getName(), exception);
            }
        }

        logger.hint("关闭监听器");
        for (Map.Entry<String, EventHandlerMonitor> entry : EVENT_MONITOR.entrySet()) {
            try {
                entry.getValue().shut();
                logger.info("关闭监听器成功 " + entry.getKey() + " -> " + entry.getValue().getClass().getName());
            } catch (Exception exception) {
                logger.error("关闭定时器失败 " + entry.getValue().getClass().getName(), exception);
            }
        }

        logger.hint("关闭定时器");
        for (Map.Entry<String, EventHandlerRunner> entry : EVENT_RUNNER.entrySet()) {
            try {
                entry.getValue().shut();
                logger.info("关闭定时器成功 " + entry.getKey() + " -> " + entry.getValue().getClass().getName());
            } catch (Exception exception) {
                logger.error("关闭定时器失败 " + entry.getValue().getClass().getName(), exception);
            }
        }

        logger.hint("关闭监听器工作线程");
        try {
            MONITOR_SERVICE.shutdown();
            boolean res = MONITOR_SERVICE.awaitTermination(3600, TimeUnit.SECONDS);
            if (res) {
                logger.info("线程池正常退出");
            } else {
                logger.info("线程池超时退出");
            }
        } catch (InterruptedException exception) {
            logger.error("线程池关闭异常 强制关闭", exception);
            MONITOR_SERVICE.shutdownNow();
        }

        logger.hint("关闭计划任务线程池");
        try {
            SCHEDULERS_POOL.shutdown();
            boolean res = SCHEDULERS_POOL.awaitTermination(3600, TimeUnit.SECONDS);
            if (res) {
                logger.info("线程池正常退出");
            } else {
                logger.info("线程池超时退出");
            }
        } catch (InterruptedException exception) {
            logger.error("线程池关闭异常 强制关闭", exception);
            SCHEDULERS_POOL.shutdownNow();
        }

    }


    // ==========================================================================================================================================================
    //
    // 监听器
    //
    // ==========================================================================================================================================================


    private void handleUsersMessage(UserMessageEvent event) {

        if (!Driver.isEnable()) return;

        try {

            MONITOR_SERVICE.submit(() -> EVENT_MONITOR_USERS.forEach(item -> item.handleUsersMessage(event)));

            if (EVENT_FILTER_USERS.parallelStream().anyMatch(item -> item.handleUsersMessage(event))) {
                logger.hint("用户消息被拦截 " + event.getSender().getId() + " ->" + event.getMessage());
                return;
            }

            String content = event.getMessage().contentToString();

            if (this.isCommand(content)) {

                Command command = new Command(content.substring(1));

                switch (command.getCommandName()) {

                    case "?":
                    case "help":
                        if (command.hasCommandBody()) {
                            if (EVENT_EXECUTOR_USERS.containsKey(command.getParameterSegment(0))) {
                                EventHandlerExecutor executor = EVENT_EXECUTOR_USERS.get(command.getParameterSegment(0));
                                Driver.sendMessage(event, executor.INFO.HELP);
                            }
                        } else {
                            Driver.sendMessage(event, MESSAGE_HELP);
                        }
                        break;

                    case "list":
                        Driver.sendMessage(event, MESSAGE_LIST_USERS);
                        break;

                    case "info":
                        Driver.sendMessage(event, MESSAGE_INFO);
                        break;

                    case "eula":
                        Driver.sendMessage(event, MESSAGE_EULA);
                        break;

                    default:
                        if (EVENT_EXECUTOR_USERS.containsKey(command.getCommandName())) {
                            EVENT_EXECUTOR_USERS.get(command.getCommandName()).handleUsersMessage(event, command);
                        }
                }
            }

        } catch (Exception exception) {
            logger.dump(event, exception);
        }
    }


    public void handleGroupMessage(GroupMessageEvent event) {

        if (!Driver.isEnable()) return;

        try {

            MONITOR_SERVICE.submit(() -> EVENT_MONITOR_GROUP.forEach(item -> item.handleGroupMessage(event)));

            if (EVENT_FILTER_GROUP.stream().anyMatch(item -> item.handleGroupMessage(event))) {
                logger.hint("群组消息被拦截 " + event.getSender().getId() + " ->" + event.getMessage());
                return;
            }

            String content = event.getMessage().contentToString();

            if (this.isCommand(content)) {
                Command command = new Command(content.substring(1));
                switch (command.getCommandName()) {
                    case "?":
                    case "help":
                        if (command.hasCommandBody()) {
                            if (EVENT_EXECUTOR_GROUP.containsKey(command.getParameterSegment(0))) {
                                EventHandlerExecutor executor = EVENT_EXECUTOR_GROUP.get(command.getParameterSegment(0));
                                try {
                                    Driver.sendMessage(event, executor.INFO.HELP);
                                } catch (Exception exception) {
                                    Driver.sendMessage(event, "帮助信息发送至私聊失败 请允许临时会话权限");
                                }
                            }
                        } else {
                            try {
                                event.getSender().sendMessage(MESSAGE_HELP);
                            } catch (Exception exception) {
                                Driver.sendMessage(event, "帮助信息发送至私聊失败 请允许临时会话权限");
                            }
                        }
                        break;

                    case "list":
                        try {
                            event.getSender().sendMessage(MESSAGE_LIST_GROUP);
                        } catch (Exception exception) {
                            Driver.sendMessage(event, "可用命令发送至私聊失败 请允许临时会话权限");
                        }
                        break;

                    case "info":
                        try {
                            event.getSender().sendMessage(MESSAGE_INFO);
                        } catch (Exception exception) {
                            Driver.sendMessage(event, "关于发送至私聊失败 请允许临时会话权限");
                        }
                        break;

                    case "eula":
                        try {
                            event.getSender().sendMessage(MESSAGE_EULA);
                        } catch (Exception exception) {
                            Driver.sendMessage(event, "EULA发送至私聊失败 请允许临时会话权限");
                        }
                        break;

                    default:
                        if (EVENT_EXECUTOR_GROUP.containsKey(command.getCommandName())) {
                            EVENT_EXECUTOR_GROUP.get(command.getCommandName()).handleGroupMessage(event, command);
                        }

                }
            }

        } catch (Exception exception) {
            logger.dump(event, exception);
        }

    }


    private void handleFriendRequest(NewFriendRequestEvent event) {
        logger.hint("BOT被添加好友 " + event.getFromNick() + "(" + event.getFromId() + ")");
        event.accept();
    }


    private void handleInvitedRequest(BotInvitedJoinGroupRequestEvent event) {
        logger.hint("BOT被邀请入群 " + event.getGroupName() + "(" + event.getGroupId() + ") 邀请人 " + event.getInvitorNick() + "(" + event.getInvitorId() + ")");
        event.accept();
    }


    // ==========================================================================================================================================================
    //
    // 工具
    //
    // ==========================================================================================================================================================


    private boolean isCommand(String content) {
        if (content.length() < 3) return false;
        if (content.charAt(0) != COMMAND_PREFIX) return false;
        return COMMAND_PATTERN.matcher(content).find();
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


    private File initFile(File file) throws InitException {
        if (!file.exists()) {
            try {
                //noinspection ResultOfMethodCallIgnored
                file.createNewFile();
                logger.hint("创建新的文件 " + file.getAbsolutePath());
            } catch (IOException exception) {
                throw new InitException("文件创建失败 " + file.getAbsolutePath(), exception);
            }
        }
        if (!file.exists()) throw new InitException("文件不存在 " + file.getAbsolutePath());
        if (!file.canRead()) throw new InitException("文件无权读取 " + file.getAbsolutePath());
        return file;
    }


    private String readFile(File file) throws InitException {

        initFile(file);

        try (
            FileReader fileReader = new FileReader(file, StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(fileReader)
        ) {

            String temp;
            StringBuilder builder = new StringBuilder();
            while ((temp = bufferedReader.readLine()) != null) builder.append(temp).append("\r\n");
            return builder.toString();

        } catch (FileNotFoundException exception) {
            throw new InitException("文件不存在 " + file.getAbsolutePath(), exception);
        } catch (IOException exception) {
            throw new InitException("文件读取失败 " + file.getAbsolutePath(), exception);
        }
    }


    private String generateListMessage(Set<Map.Entry<String, EventHandlerExecutor>> entrySet) {
        if (entrySet.size() == 0) return "无插件";
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, EventHandlerExecutor> entry : entrySet) {
            var v = entry.getValue();
            builder.append(v.INFO.COMMAND);
            builder.append(" ");
            builder.append(v.INFO.NAME);
            builder.append(" ");
            builder.append(v.INFO.DESCRIPTION);
            builder.append("\r\n");
        }
        builder.setLength(builder.length() - 2);
        return builder.toString();
    }


    // ==========================================================================================================================================================
    //
    // 模块相关
    //
    // ==========================================================================================================================================================


    public Set<String> listAllPlugin() {
        return MODULES.keySet();
    }


    @Api
    public void reloadPlugin(String name) {
        if (!MODULES.containsKey(name)) {
            logger.warning("不存在此模块 -> " + name);
            return;
        }
        AbstractEventHandler instance = MODULES.get(name);
        try {
            logger.info("停止 " + name);
            instance.shut();
            logger.info("加载 " + name);
            instance.init();
            logger.info("启动 " + name);
            instance.boot();
        } catch (BotException exception) {
            logger.warning("重载模块发生错误 -> " + name, exception);
        }
    }


    @Api("获取模块实例")
    @SuppressWarnings("unchecked")
    public <T extends EventHandlerRunner> T getRunner(Class<T> clazz) {
        List<EventHandlerRunner> collect = EVENT_RUNNER.values().stream().filter(clazz::isInstance).collect(Collectors.toUnmodifiableList());
        if (collect.size() == 1) return (T) collect.get(0);
        throw new IllegalArgumentException("No such runner exist");
    }


    // ==========================================================================================================================================================
    //
    // BOT相关封装
    //
    // ==========================================================================================================================================================


    @Api("以Mirai阻塞")
    public void joinBot() {
        bot.join();
    }

    @Api("关闭Bot")
    public void shutBot() {
        bot.close();
    }

    @Api("提交定时任务")
    public ScheduledFuture<?> schedule(Runnable runnable, long delay, TimeUnit unit) {
        return SCHEDULERS_POOL.schedule(runnable, delay, unit);
    }

    @Api("提交定时任务")
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable runnable, long initialDelay, long period, TimeUnit unit) {
        return SCHEDULERS_POOL.scheduleAtFixedRate(runnable, initialDelay, period, unit);
    }

    @Api("提交定时任务")
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable runnable, long initialDelay, long delay, TimeUnit unit) {
        return SCHEDULERS_POOL.scheduleWithFixedDelay(runnable, initialDelay, delay, unit);
    }

    // =========================================================================


    @Api("获取BOT自身QQ号")
    public long getBotID() {
        return bot.getId();
    }

    @Api("列出所有好友")
    public ContactList<Friend> getFriends() {
        return bot.getFriends();
    }

    @Api("列出所有群组")
    public ContactList<Group> getGroups() {
        return bot.getGroups();
    }

    @Api("根据ID获取陌生人")
    public Stranger getStranger(long id) {
        return bot.getStranger(id);
    }

    @Api("根据ID获取陌生人")
    public Stranger getStrangerOrFail(long id) {
        return bot.getStrangerOrFail(id);
    }

    @Api("根据ID获取好友")
    public Friend getFriend(long id) {
        return bot.getFriend(id);
    }

    @Api("根据ID获取好友")
    public Friend getFriendOrFail(long id) {
        return bot.getFriendOrFail(id);
    }

    @Api("根据ID获取群组")
    public Group getGroup(long id) {
        return bot.getGroup(id);
    }

    @Api("根据ID获取群组")
    public Group getGroupOrFail(long id) {
        return bot.getGroupOrFail(id);
    }

    @Api("发送消息的核心方法")
    public void sendMessage(Contact contact, Message message) {
        contact.sendMessage(message);
    }

    @Api("获取图片的URL")
    public String getImageURL(Image image) {
        return Mirai.getInstance().queryImageUrl(bot, image);
    }

    @Api("按照配置的映射表获取ID")
    public String getUserProfile(long user) {
        return Mirai.getInstance().queryProfile(bot, user).getNickname();
    }

    @Api("按照配置的映射表获取ID")
    public String getNickName(User user) {
        if (NICKNAME.containsKey(user.getId())) {
            return NICKNAME.get(user.getId());
        } else {
            return user.getNick(); // User包含了nick强行使用重载方法是一种浪费
        }
    }

    @Api("按照配置的映射表获取ID")
    public String getNickName(long user) {
        if (NICKNAME.containsKey(user)) {
            return NICKNAME.get(user);
        } else {
            return Mirai.getInstance().queryProfile(bot, user).getNickname();
        }
    }

}