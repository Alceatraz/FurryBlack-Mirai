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
import net.mamoe.mirai.data.UserProfile;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.Listener;
import net.mamoe.mirai.event.events.BotInvitedJoinGroupRequestEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MemberJoinEvent;
import net.mamoe.mirai.event.events.MemberLeaveEvent;
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
import studio.blacktech.furryblackplus.core.exception.initlization.BootException;
import studio.blacktech.furryblackplus.core.exception.initlization.FirstBootException;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
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

    private static final String CONF_THREADS_MONITOR = "threads.monitor";
    private static final String CONF_THREADS_SCHEDULE = "threads.schedule";

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
    CONF_THREADS_MONITOR+ "=4\n"+
    "# 异步任务线程池\n" +
    CONF_THREADS_SCHEDULE+ "=4\n"+
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

    private final File FOLDER_CONFIG;


    private static volatile boolean INSTANCE_LOCK = false;


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

    private Listener<UserMessageEvent> userMessageEventListener;
    private Listener<GroupMessageEvent> groupMessageEventListener;
    private Listener<MemberJoinEvent> memberJoinEventListener;
    private Listener<MemberLeaveEvent> memberLeaveEventListener;
    private Listener<NewFriendRequestEvent> newFriendRequestEventListener;
    private Listener<BotInvitedJoinGroupRequestEvent> botInvitedJoinGroupRequestEventListener;


    // ==========================================================================================================================
    // 对象控制
    // 🔫 🧦 ❌ ✔️ ⭕ 🚧 🀄


    public Systemd(File folder) {
        synchronized (Systemd.class) {
            if (INSTANCE_LOCK) System.exit(0);
            INSTANCE_LOCK = true;
            FOLDER_CONFIG = folder;
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


        logger.info("初始化配置文件");


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


        // ==========================================================================================================================
        // 加载配置

        logger.info("加载配置文件");


        Properties config = new Properties();

        try (FileInputStream inStream = new FileInputStream(FILE_CONFIG)) {
            config.load(inStream);
        } catch (IOException exception) {
            logger.error("核心配置文件读取错误 即将关闭 " + FILE_CONFIG.getAbsolutePath());
            throw new BootException("核心配置文件读取错误 " + FILE_CONFIG.getAbsolutePath(), exception);
        }


        // ==========================================================================================================================
        // 读取配置

        logger.hint("初始化命令过滤器");

        String prefix = config.getProperty(CONF_BOT_COMMAND_PREFIX);

        if (prefix == null || prefix.isEmpty() || prefix.isBlank() || prefix.length() != 1) {
            logger.warning("指定的命令前缀不可用 将自动设置为默认值: /");
        } else {
            COMMAND_PREFIX = prefix.charAt(0);
        }

        String regex = "^" + COMMAND_PREFIX + "[a-z]{3,8}";

        logger.seek("识别前缀 " + COMMAND_PREFIX);
        logger.info("识别正则 " + regex);

        COMMAND_PATTERN = Pattern.compile(regex);

        // ==========================================================================================================================
        // 读取模板


        logger.hint("初始化内置消息");

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


        NICKNAME_GLOBAL = new HashMap<>();
        NICKNAME_GROUPS = new HashMap<>();


        File commonNick = initFile(Paths.get(Driver.getConfigFolder(), "nickname.txt").toFile());

        try (
            FileReader fileReader = new FileReader(commonNick);
            BufferedReader bufferedReader = new BufferedReader(fileReader)
        ) {

            // 1234.1234:nick

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

                if (!temp1[0].contains("\\.")) {
                    logger.warning("配置无效 " + line);
                    continue;
                }

                String nick = temp1[1].trim();

                String[] temp2 = temp1[0].split("\\.");

                long groupId;
                long userId;

                try {
                    userId = Long.parseLong(temp2[1]);
                } catch (NumberFormatException exception) {
                    logger.warning("配置无效 " + line);
                    continue;
                }

                if (temp2[0].equals("\\*")) {

                    NICKNAME_GLOBAL.put(userId, nick);

                    logger.seek("添加全局昵称 " + userId, nick);

                } else {

                    try {
                        groupId = Long.parseLong(temp2[0]);
                    } catch (NumberFormatException exception) {
                        logger.warning("配置无效 " + line);
                        continue;
                    }

                    Map<Long, String> groupNicks;

                    if (NICKNAME_GROUPS.containsKey(groupId)) {
                        groupNicks = NICKNAME_GROUPS.get(groupId);
                    } else {
                        NICKNAME_GROUPS.put(groupId, groupNicks = new HashMap<>());
                    }

                    groupNicks.put(userId, nick);

                    logger.seek("添加群内昵称 " + groupId + "." + userId, nick);
                }


            }

        } catch (Exception exception) {
            throw new BootException("昵称映射表读取失败", exception);
        }


        // ==========================================================================================================================
        // 读取机器人配置


        logger.hint("加载机器人配置");

        BotConfiguration configuration = new BotConfiguration();

        File cacheFolder = Paths.get(FOLDER_CONFIG.getAbsolutePath(), "cache").toFile();

        configuration.setCacheDir(cacheFolder);


        // ==========================================================================================================================
        // 读取账号配置


        String accountConfig = config.getProperty(CONF_ACCOUNT_ID);
        long ACCOUNT_QQ = parseLong(accountConfig);

        logger.seek("QQ账号 " + ACCOUNT_QQ);

        String ACCOUNT_PW = config.getProperty(CONF_ACCOUNT_PW);

        int length = ACCOUNT_PW.length();

        if (ACCOUNT_PW.charAt(0) == '\"' && ACCOUNT_PW.charAt(length - 1) == '\"') {
            ACCOUNT_PW = ACCOUNT_PW.substring(1, length - 1);
        }

        length = accountConfig.length();

        if (Driver.isDebug()) {
            logger.seek("QQ密码 " + ACCOUNT_PW);
            logger.warning("关闭调试模式以给此条日志打码");
        } else {
            String shadow_ACCOUNT_PW = ACCOUNT_PW.charAt(0) + "*".repeat(length - 1);
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


        logger.info("初始化机器人");
        bot = BotFactory.INSTANCE.newBot(ACCOUNT_QQ, ACCOUNT_PW, configuration);


        logger.info("机器人类型 " + bot.getClass().getName());


        // ==========================================================================================================================
        // 注册模块


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

        // ==========================================================================================================================
        // 扫描模块

        logger.hint("扫描所有插件");

        String RAW_PACKAGE_PREFIX = config.getProperty(CONF_BOT_PACKAGE_PREFIX);

        logger.seek("扫描路径配置 " + RAW_PACKAGE_PREFIX);

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


        logger.info("开始模块扫描");

        configurationBuilder.addScanners(new SubTypesScanner());
        Reflections reflections = new Reflections(configurationBuilder);


        // ==========================================================================================================================
        // 分析扫描结果


        logger.info("分析扫描结果");


        List<Class<? extends EventHandlerRunner>> runnerList = new LinkedList<>();
        List<Class<? extends EventHandlerMonitor>> monitorList = new LinkedList<>();
        List<Class<? extends EventHandlerFilter>> filterList = new LinkedList<>();
        List<Class<? extends EventHandlerExecutor>> executorList = new LinkedList<>();


        Map<String, Class<? extends AbstractEventHandler>> modules = new HashMap<>();
        Map<String, Class<? extends EventHandlerExecutor>> commands = new HashMap<>();


        // ==========================================================================================================================
        // 分析定时器扫描结果


        try {

            Set<Class<? extends EventHandlerRunner>> runners = reflections.getSubTypesOf(EventHandlerRunner.class);

            for (Class<? extends EventHandlerRunner> clazz : runners) {
                if (clazz.isAnnotationPresent(Runner.class)) {
                    String artificial = clazz.getAnnotation(Runner.class).artificial();
                    if (modules.containsKey(artificial)) {
                        throw new BootException("注册定时器" + clazz.getName() + "失败" + artificial + "已注册为" + modules.get(artificial).getName());
                    }
                    modules.put(artificial, clazz);
                    runnerList.add(clazz);
                } else {
                    logger.warning("发现无注解定时器 " + clazz.getName());
                }
            }

            runnerList.sort((o1, o2) -> {
                Runner o1Annotation = o1.getAnnotation(Runner.class);
                Runner o2Annotation = o2.getAnnotation(Runner.class);
                return o1Annotation.priority() - o2Annotation.priority();
            });

            logger.hint("扫描到以下定时器");
            runnerList.forEach(item -> logger.info(item.getAnnotation(Runner.class).priority() + " - " + item.getName()));

        } catch (Exception exception) {
            if (exception instanceof ReflectionsException && "Scanner SubTypesScanner was not configured".equalsIgnoreCase(exception.getMessage())) {
                logger.info("没有扫描到任何定时器");
            } else {
                logger.warning("扫描定时器时发生异常", exception);
            }
        }


        // ==========================================================================================================================
        // 分析监听器扫描结果


        try {

            Set<Class<? extends EventHandlerMonitor>> monitors = reflections.getSubTypesOf(EventHandlerMonitor.class);

            for (Class<? extends EventHandlerMonitor> clazz : monitors) {
                if (clazz.isAnnotationPresent(Monitor.class)) {
                    Monitor annotation = clazz.getAnnotation(Monitor.class);
                    if (!annotation.users() && !annotation.group()) {
                        logger.warning("发现无用监听器 " + clazz.getName());
                        continue;
                    }
                    String artificial = annotation.artificial();
                    if (modules.containsKey(artificial)) {
                        throw new BootException("注册监听器" + clazz.getName() + "失败" + artificial + "模块已注册为" + modules.get(artificial).getName());
                    }
                    modules.put(artificial, clazz);
                    monitorList.add(clazz);
                } else {
                    logger.warning("发现无注解监听器 " + clazz.getName());
                }
            }

            monitorList.sort((o1, o2) -> {
                Monitor o1Annotation = o1.getAnnotation(Monitor.class);
                Monitor o2Annotation = o2.getAnnotation(Monitor.class);
                return o1Annotation.priority() - o2Annotation.priority();
            });

            logger.hint("扫描到以下监听器");
            monitorList.forEach(item -> logger.info(item.getAnnotation(Monitor.class).priority() + " - " + item.getName()));

        } catch (Exception exception) {
            if (exception instanceof ReflectionsException && "Scanner SubTypesScanner was not configured".equalsIgnoreCase(exception.getMessage())) {
                logger.info("没有扫描到任何监听器");
            } else {
                logger.warning("扫描监听器时发生异常", exception);
            }
        }


        // ==========================================================================================================================
        // 分析过滤器扫描结果


        try {

            Set<Class<? extends EventHandlerFilter>> filters = reflections.getSubTypesOf(EventHandlerFilter.class);

            for (Class<? extends EventHandlerFilter> clazz : filters) {
                if (clazz.isAnnotationPresent(Filter.class)) {
                    Filter annotation = clazz.getAnnotation(Filter.class);
                    if (!annotation.users() && !annotation.group()) {
                        logger.warning("发现无用过滤器 " + clazz.getName());
                        continue;
                    }
                    String artificial = annotation.artificial();
                    if (modules.containsKey(artificial)) {
                        throw new BootException("注册过滤器" + clazz.getName() + "失败" + artificial + "模块已注册为" + modules.get(artificial).getName());
                    }
                    modules.put(artificial, clazz);
                    filterList.add(clazz);
                } else {
                    logger.warning("发现无注解过滤器 " + clazz.getName());
                }
            }

            filterList.sort((o1, o2) -> {
                Filter o1Annotation = o1.getAnnotation(Filter.class);
                Filter o2Annotation = o2.getAnnotation(Filter.class);
                return o1Annotation.priority() - o2Annotation.priority();
            });

            logger.hint("扫描到以下过滤器");
            filterList.forEach(item -> logger.info(item.getAnnotation(Filter.class).priority() + " - " + item.getName()));

        } catch (Exception exception) {
            if (exception instanceof ReflectionsException && "Scanner SubTypesScanner was not configured".equalsIgnoreCase(exception.getMessage())) {
                logger.info("没有扫描到任何过滤器");
            } else {
                logger.warning("扫描过滤器时发生异常", exception);
            }
        }


        // ==========================================================================================================================
        // 分析执行器扫描结果


        try {

            Set<Class<? extends EventHandlerExecutor>> executors = reflections.getSubTypesOf(EventHandlerExecutor.class);

            for (Class<? extends EventHandlerExecutor> clazz : executors) {
                if (clazz.isAnnotationPresent(Executor.class)) {
                    Executor annotation = clazz.getAnnotation(Executor.class);
                    String command = annotation.command();
                    String artificial = annotation.artificial();
                    if (modules.containsKey(artificial)) {
                        throw new BootException("注册执行器" + clazz.getName() + "失败" + artificial + "模块已注册为" + modules.get(artificial).getName());
                    }
                    if (commands.containsKey(command)) {
                        throw new BootException("注册执行器" + clazz.getName() + "失败 " + command + "命令已注册为" + commands.get(command).getName());
                    }
                    modules.put(artificial, clazz);
                    commands.put(command, clazz);
                    executorList.add(clazz);
                } else {
                    logger.warning("发现无注解执行器 " + clazz.getName());
                }

            }

            logger.hint("扫描到以下执行器");
            executorList.forEach(item -> logger.info(item.getName()));

        } catch (Exception exception) {
            if (exception instanceof ReflectionsException && "Scanner SubTypesScanner was not configured".equalsIgnoreCase(exception.getMessage())) {
                logger.info("没有扫描到任何执行器");
            } else {
                logger.warning("扫描执行器时发生异常", exception);
            }
        }


        // ==========================================================================================================================


        if (runnerList.size() + monitorList.size() + filterList.size() + executorList.size() == 0) {
            logger.warning("没有扫描到任何模块 请检查扫描路径");
        }


        // ==========================================================================================================================
        // 注册模块


        logger.info("注册模块实例");


        // ==========================================================================================================================
        // 注册定时器


        logger.hint("注册定时器");
        for (Class<? extends EventHandlerRunner> clazz : runnerList) {
            Runner annotation = clazz.getAnnotation(Runner.class);
            EventHandlerRunner.RunnerInfo info = new EventHandlerRunner.RunnerInfo(annotation);
            try {
                EventHandlerRunner instance = clazz.getConstructor(EventHandlerRunner.RunnerInfo.class).newInstance(info);
                logger.info("注册定时器 " + annotation.priority() + " - " + info.ARTIFICIAL + " > " + clazz.getName());
                MODULES.put(info.ARTIFICIAL, instance);
                EVENT_RUNNER.put(info.ARTIFICIAL, instance);
            } catch (Exception exception) {
                throw new BootException("定时器注册失败 " + clazz.getName(), exception);
            }
        }


        // ==========================================================================================================================
        // 注册监听器


        logger.hint("注册监听器");
        for (Class<? extends EventHandlerMonitor> clazz : monitorList) {
            Monitor annotation = clazz.getAnnotation(Monitor.class);
            EventHandlerMonitor.MonitorInfo info = new EventHandlerMonitor.MonitorInfo(annotation);
            try {
                EventHandlerMonitor instance = clazz.getConstructor(EventHandlerMonitor.MonitorInfo.class).newInstance(info);
                logger.info("注册监听器 " + annotation.priority() + " - " + info.ARTIFICIAL + " > " + clazz.getName());
                MODULES.put(info.ARTIFICIAL, instance);
                EVENT_MONITOR.put(info.ARTIFICIAL, instance);
                if (annotation.users()) EVENT_MONITOR_USERS.add(instance);
                if (annotation.group()) EVENT_MONITOR_GROUP.add(instance);
            } catch (Exception exception) {
                throw new BootException("监听器注册失败 " + clazz.getName(), exception);
            }
        }


        // ==========================================================================================================================
        // 注册过滤器


        logger.hint("注册过滤器");
        for (Class<? extends EventHandlerFilter> clazz : filterList) {
            try {
                Filter annotation = clazz.getAnnotation(Filter.class);
                EventHandlerFilter.FilterInfo info = new EventHandlerFilter.FilterInfo(annotation);
                EventHandlerFilter instance = clazz.getConstructor(EventHandlerFilter.FilterInfo.class).newInstance(info);
                logger.info("注册过滤器 " + annotation.priority() + " - " + info.ARTIFICIAL + " > " + clazz.getName());
                MODULES.put(info.ARTIFICIAL, instance);
                EVENT_FILTER.put(info.ARTIFICIAL, instance);
                if (annotation.users()) EVENT_FILTER_USERS.add(instance);
                if (annotation.group()) EVENT_FILTER_GROUP.add(instance);
            } catch (Exception exception) {
                throw new BootException("过滤器注册失败 " + clazz.getName(), exception);
            }
        }


        // ==========================================================================================================================
        // 初始化执行器


        logger.hint("注册执行器");

        for (Class<? extends EventHandlerExecutor> clazz : executorList) {
            Executor annotation = clazz.getAnnotation(Executor.class);
            EventHandlerExecutor.ExecutorInfo info = new EventHandlerExecutor.ExecutorInfo(annotation);
            try {
                EventHandlerExecutor instance = clazz.getConstructor(EventHandlerExecutor.ExecutorInfo.class).newInstance(info);
                logger.info("注册监听器 " + info.COMMAND + " - " + info.ARTIFICIAL + " > " + clazz.getName());
                MODULES.put(info.ARTIFICIAL, instance);
                EVENT_EXECUTOR.put(info.ARTIFICIAL, instance);
                if (annotation.users()) EVENT_EXECUTOR_USERS.put(info.COMMAND, instance);
                if (annotation.group()) EVENT_EXECUTOR_GROUP.put(info.COMMAND, instance);
            } catch (Exception exception) {
                throw new BootException("执行器初始化失败 " + clazz.getName(), exception);
            }
        }


        logger.info("组装用户list消息");
        MESSAGE_LIST_USERS = generateListMessage(EVENT_EXECUTOR_USERS.entrySet());


        logger.info("组装群组list消息");
        MESSAGE_LIST_GROUP = generateListMessage(EVENT_EXECUTOR_GROUP.entrySet());


        // ==========================================================================================================================
        // 加载模块


        logger.hint("加载定时器");

        for (Map.Entry<String, EventHandlerRunner> entry : EVENT_RUNNER.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            try {
                entry.getValue().load();
                logger.info("加载定时器成功 " + k + " -> " + v.getClass().getName());
            } catch (Exception exception) {
                throw new BootException("加载定时器失败 " + v.getClass().getName(), exception);
            }
        }


        logger.hint("加载监听器");

        for (Map.Entry<String, EventHandlerMonitor> entry : EVENT_MONITOR.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            try {
                entry.getValue().load();
                logger.info("加载监听器成功 " + k + " -> " + v.getClass().getName());
            } catch (Exception exception) {
                throw new BootException("加载监听器失败 " + v.getClass().getName(), exception);
            }
        }


        logger.hint("加载过滤器");

        for (Map.Entry<String, EventHandlerFilter> entry : EVENT_FILTER.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            try {
                entry.getValue().load();
                logger.info("启动过滤器成功 " + k + " -> " + v.getClass().getName());
            } catch (Exception exception) {
                throw new BootException("加载过滤器失败 " + v.getClass().getName(), exception);
            }
        }


        logger.hint("加载执行器");

        for (Map.Entry<String, EventHandlerExecutor> entry : EVENT_EXECUTOR.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            try {
                entry.getValue().load();
                logger.info("加载执行器成功 " + k + " -> " + v.getClass().getName());
            } catch (Exception exception) {
                throw new BootException("加载执行器失败 " + v.getClass().getName(), exception);
            }
        }


        // =============================================================================================================
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


        // ==========================================================================================================================
        // 登录QQ


        if (Driver.isNoLogin()) {
            logger.warning("指定了--no-login参数 跳过登录");
        } else {
            logger.hint("登录");
            bot.login();
        }


        // ==========================================================================================================================
        // 启动线程池


        int monitorPoolSize = parseInteger(config.getProperty(CONF_THREADS_SCHEDULE));
        logger.seek("监听线程池配置 " + monitorPoolSize);

        MONITOR_PROCESS = (ThreadPoolExecutor) Executors.newFixedThreadPool(monitorPoolSize);

        //

        int schedulePoolSize = parseInteger(config.getProperty(CONF_THREADS_SCHEDULE));
        logger.seek("异步线程池配置 " + schedulePoolSize);

        EXECUTOR_SERVICE = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(schedulePoolSize);


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
                throw new BootException("启动定时器失败 " + v.getClass().getName(), exception);
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
                throw new BootException("启动监听器失败 " + v.getClass().getName(), exception);
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
                throw new BootException("启动过滤器失败 " + v.getClass().getName(), exception);
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
                throw new BootException("启动执行器失败 " + v.getClass().getName(), exception);
            }
        }


        // ==========================================================================================================================
        // 列出所有好友和群组


        if (!Driver.isNoLogin()) {

            logger.seek("机器人账号 " + bot.getId());
            logger.seek("机器人昵称 " + bot.getNick());
            logger.seek("机器人头像 " + bot.getAvatarUrl());

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


    public void shut() {


        userMessageEventListener.complete();
        groupMessageEventListener.complete();
        memberJoinEventListener.complete();
        memberLeaveEventListener.complete();
        newFriendRequestEventListener.complete();
        botInvitedJoinGroupRequestEventListener.complete();


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
        ArrayList<Map.Entry<String, EventHandlerFilter>> reverseFilter = new ArrayList<>(EVENT_FILTER.entrySet());
        Collections.reverse(reverseFilter);
        for (Map.Entry<String, EventHandlerFilter> entry : reverseFilter) {
            try {
                entry.getValue().shut();
                logger.info("关闭过滤器成功 " + entry.getKey() + " -> " + entry.getValue().getClass().getName());
            } catch (Exception exception) {
                logger.error("关闭过滤器失败 " + entry.getValue().getClass().getName(), exception);
            }
        }

        logger.hint("关闭监听器");
        ArrayList<Map.Entry<String, EventHandlerMonitor>> reverseMonitor = new ArrayList<>(EVENT_MONITOR.entrySet());
        Collections.reverse(reverseMonitor);
        for (Map.Entry<String, EventHandlerMonitor> entry : reverseMonitor) {
            try {
                entry.getValue().shut();
                logger.info("关闭监听器成功 " + entry.getKey() + " -> " + entry.getValue().getClass().getName());
            } catch (Exception exception) {
                logger.error("关闭定时器失败 " + entry.getValue().getClass().getName(), exception);
            }
        }

        logger.hint("关闭定时器");
        ArrayList<Map.Entry<String, EventHandlerRunner>> reverseRunner = new ArrayList<>(EVENT_RUNNER.entrySet());
        Collections.reverse(reverseRunner);
        for (Map.Entry<String, EventHandlerRunner> entry : reverseRunner) {
            try {
                entry.getValue().shut();
                logger.info("关闭定时器成功 " + entry.getKey() + " -> " + entry.getValue().getClass().getName());
            } catch (Exception exception) {
                logger.error("关闭定时器失败 " + entry.getValue().getClass().getName(), exception);
            }
        }

        if (Driver.isShutModeDrop()) {
            logger.info("强制关闭监听器线程池");
            MONITOR_PROCESS.shutdownNow();
        } else {
            logger.info("关闭监听器线程池");
            MONITOR_PROCESS.shutdown();
            try {
                logger.info("等待监听器线程池关闭");
                //noinspection ResultOfMethodCallIgnored
                MONITOR_PROCESS.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
                logger.info("监听器线程池已关闭");
            } catch (InterruptedException exception) {
                logger.error("等待关闭监听器线程池错误", exception);
                MONITOR_PROCESS.shutdownNow();
            }
        }

        if (Driver.isShutModeDrop()) {
            logger.info("强制关闭异步任务线程池");
            EXECUTOR_SERVICE.shutdownNow();
        } else {
            logger.info("关闭异步任务线程池");
            EXECUTOR_SERVICE.shutdown();
            try {
                logger.info("等待异步任务线程池关闭");
                //noinspection ResultOfMethodCallIgnored
                EXECUTOR_SERVICE.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
            } catch (InterruptedException exception) {
                logger.error("等待关闭异步任务线程池错误", exception);
                exception.printStackTrace();
            }
        }

        logger.info("等待Mirai关闭");

        bot.close();
        bot.join();

    }


    // ==========================================================================================================================================================
    //
    // 监听器
    //
    // ==========================================================================================================================================================


    private void handleUsersMessage(UserMessageEvent event) {

        if (!Driver.isEnable()) return;

        try {

            MONITOR_PROCESS.submit(() -> EVENT_MONITOR_USERS.forEach(item -> item.handleUsersMessage(event)));

            if (EVENT_FILTER_USERS.parallelStream().anyMatch(item -> item.handleUsersMessage(event))) return;

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

            MONITOR_PROCESS.submit(() -> EVENT_MONITOR_GROUP.forEach(item -> item.handleGroupMessage(event)));

            if (EVENT_FILTER_GROUP.parallelStream().anyMatch(item -> item.handleGroupMessage(event))) return;

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


    // ==========================================================================================================================================================
    //
    // 工具
    //
    // ==========================================================================================================================================================


    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();


    public void await() {
        try {
            lock.lock();
            try {
                condition.await();
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }
        } finally {
            lock.unlock();
        }
    }


    public void signal() {
        try {
            lock.lock();
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
            if (file.createNewFile()) logger.hint("创建新的文件 " + file.getAbsolutePath());
        } catch (IOException exception) {
            throw new BootException("文件创建失败 " + file.getAbsolutePath(), exception);
        }

        if (!file.exists()) throw new BootException("文件不存在 " + file.getAbsolutePath());
        if (!file.canRead()) throw new BootException("文件无权读取 " + file.getAbsolutePath());
        return file;
    }


    private String readFile(File file) throws BootException {

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
            throw new BootException("文件不存在 " + file.getAbsolutePath(), exception);
        } catch (IOException exception) {
            throw new BootException("文件读取失败 " + file.getAbsolutePath(), exception);
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


    @Api("列出模块")
    public Set<String> listAllPlugin() {
        return MODULES.keySet();
    }


    @Api("按名称重载模块")
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
            instance.load();
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


    @Api("提交异步任务")
    public Future<?> submit(Runnable runnable) {
        return EXECUTOR_SERVICE.submit(runnable);
    }

    @Api("提交异步任务")
    public <T> Future<?> submit(Runnable runnable, T t) {
        return EXECUTOR_SERVICE.submit(runnable, t);
    }

    @Api("提交异步任务")
    public Future<?> submit(Callable<?> callable) {
        return EXECUTOR_SERVICE.submit(callable);
    }

    @Api("提交定时任务")
    public ScheduledFuture<?> schedule(Runnable runnable, long time, TimeUnit timeUnit) {
        return EXECUTOR_SERVICE.schedule(runnable, time, timeUnit);
    }

    @Api("提交定时任务")
    public ScheduledFuture<?> schedule(Callable<?> callable, long delay, TimeUnit unit) {
        return EXECUTOR_SERVICE.schedule(callable, delay, unit);
    }

    @Api("提交定时任务")
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable runnable, long initialDelay, long period, TimeUnit unit) {
        return EXECUTOR_SERVICE.scheduleAtFixedRate(runnable, initialDelay, period, unit);
    }

    @Api("提交定时任务")
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable runnable, long initialDelay, long delay, TimeUnit unit) {
        return EXECUTOR_SERVICE.scheduleWithFixedDelay(runnable, initialDelay, delay, unit);
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

    @Api("获取用户名片")
    public UserProfile getUserProfile(long user) {
        return Mirai.getInstance().queryProfile(bot, user);
    }

    @Api("获取预设昵称")
    public String getMappedNickName(long groupId, long userId) {
        if (NICKNAME_GROUPS.containsKey(groupId)) {
            Map<Long, String> groupNicks = NICKNAME_GROUPS.get(groupId);
            if (groupNicks.containsKey(userId)) {
                return groupNicks.get(userId);
            }
        }
        if (NICKNAME_GLOBAL.containsKey(userId)) {
            return NICKNAME_GLOBAL.get(userId);
        }
        NormalMember member = bot.getGroupOrFail(groupId).getOrFail(userId);
        String nameCard = member.getNameCard();
        if (nameCard.isBlank()) {
            return getUserProfile(userId).getNickname();
        } else {
            return nameCard;
        }
    }

    @Api("获取预设昵称")
    public String getMappedNickName(GroupMessageEvent event) {
        long groupId = event.getGroup().getId();
        long userId = event.getSender().getId();
        if (NICKNAME_GROUPS.containsKey(groupId)) {
            Map<Long, String> groupNicks = NICKNAME_GROUPS.get(groupId);
            if (groupNicks.containsKey(userId)) {
                return groupNicks.get(userId);
            }
        }
        if (NICKNAME_GLOBAL.containsKey(userId)) {
            return NICKNAME_GLOBAL.get(userId);
        }
        String nameCard = event.getSender().getNameCard();
        if (nameCard.isBlank()) {
            return event.getSender().getNick();
        } else {
            return nameCard;
        }
    }

}