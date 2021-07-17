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
import studio.blacktech.furryblackplus.Driver;
import studio.blacktech.furryblackplus.common.Api;
import studio.blacktech.furryblackplus.core.annotation.Component;
import studio.blacktech.furryblackplus.core.define.ModuleWrapper;
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
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
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
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
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

    // CLAZZ用于持有扫描得到的class文件

    private Map<String, ModuleWrapper<? extends AbstractEventHandler>> COMPONENT_CLAZZ;
    private Map<String, ModuleWrapper<? extends EventHandlerRunner>> COMPONENT_RUNNER_CLAZZ;
    private Map<String, ModuleWrapper<? extends EventHandlerFilter>> COMPONENT_FILTER_CLAZZ;
    private Map<String, ModuleWrapper<? extends EventHandlerMonitor>> COMPONENT_MONITOR_CLAZZ;
    private Map<String, ModuleWrapper<? extends EventHandlerExecutor>> COMPONENT_EXECUTOR_CLAZZ;

    // INSTANCE用于持有实例化后的对象，是有序Map，按照优先级排序
    private Map<String, AbstractEventHandler> COMPONENT_INSTANCE;
    private Map<String, EventHandlerRunner> COMPONENT_RUNNER_INSTANCE;
    private Map<String, EventHandlerFilter> COMPONENT_FILTER_INSTANCE;
    private Map<String, EventHandlerMonitor> COMPONENT_MONITOR_INSTANCE;
    private Map<String, EventHandlerExecutor> COMPONENT_EXECUTOR_INSTANCE;

    // CHAIN用于保存分类后的引用
    private LinkedList<EventHandlerMonitor> MONITOR_USERS_CHAIN; // 私聊过滤器注册
    private LinkedList<EventHandlerMonitor> MONITOR_GROUP_CHAIN; // 群聊过滤器注册

    private LinkedList<EventHandlerFilter> FILTER_USERS_CHAIN; // 私聊过滤器注册
    private LinkedList<EventHandlerFilter> FILTER_GROUP_CHAIN; // 群聊过滤器注册

    private Map<String, EventHandlerExecutor> EXECUTOR_USERS_COMMANDS; // 私聊执行器注册
    private Map<String, EventHandlerExecutor> EXECUTOR_GROUP_COMMANDS; // 群聊执行器注册

    private Listener<UserMessageEvent> userMessageEventListener;
    private Listener<GroupMessageEvent> groupMessageEventListener;
    private Listener<MemberJoinEvent> memberJoinEventListener;
    private Listener<MemberLeaveEvent> memberLeaveEventListener;
    private Listener<NewFriendRequestEvent> newFriendRequestEventListener;
    private Listener<BotInvitedJoinGroupRequestEvent> botInvitedJoinGroupRequestEventListener;


    // ==========================================================================================================================
    // 对象控制
    // 🔫 🧦 ❌ ✔️ ⭕ 🚧 🀄


    public Systemd(File folderConfig, File folderPlugin) {
        synchronized (Systemd.class) {
            if (INSTANCE_LOCK) System.exit(0);
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


    @SuppressWarnings("unchecked")
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

            // 1234.1234:nick

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


        configuration.setBotLoggerSupplier(botInstance -> new LoggerX("MiraiBot"));
        configuration.setNetworkLoggerSupplier(botInstance -> new LoggerX("MiraiNet"));


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

        this.COMPONENT_CLAZZ = new HashMap<>();

        this.COMPONENT_RUNNER_CLAZZ = new LinkedHashMap<>();
        this.COMPONENT_FILTER_CLAZZ = new LinkedHashMap<>();
        this.COMPONENT_MONITOR_CLAZZ = new LinkedHashMap<>();
        this.COMPONENT_EXECUTOR_CLAZZ = new LinkedHashMap<>();

        this.COMPONENT_INSTANCE = new HashMap<>();

        this.COMPONENT_RUNNER_INSTANCE = new LinkedHashMap<>();
        this.COMPONENT_FILTER_INSTANCE = new LinkedHashMap<>();
        this.COMPONENT_MONITOR_INSTANCE = new LinkedHashMap<>();
        this.COMPONENT_EXECUTOR_INSTANCE = new HashMap<>();

        this.MONITOR_USERS_CHAIN = new LinkedList<>();
        this.MONITOR_GROUP_CHAIN = new LinkedList<>();

        this.FILTER_USERS_CHAIN = new LinkedList<>();
        this.FILTER_GROUP_CHAIN = new LinkedList<>();

        this.EXECUTOR_USERS_COMMANDS = new HashMap<>();
        this.EXECUTOR_GROUP_COMMANDS = new HashMap<>();


        // ==========================================================================================================================


        List<ModuleWrapper<? extends EventHandlerRunner>> tempComponentRunnerClazz = new LinkedList<>();
        List<ModuleWrapper<? extends EventHandlerFilter>> tempComponentFilterClazz = new LinkedList<>();
        List<ModuleWrapper<? extends EventHandlerMonitor>> tempComponentMonitorClazz = new LinkedList<>();


        this.logger.hint("扫描插件");

        File[] files = this.FOLDER_PLUGIN.listFiles();

        if (files == null) {

            this.logger.warning("没有发现任何插件");

        } else {


            Map<String, ModuleWrapper<? extends EventHandlerExecutor>> executors = new HashMap<>();


            for (File file : files) {

                this.logger.seek("扫描 " + file.getAbsolutePath());

                try (JarFile jarFile = new JarFile(file)) {

                    Enumeration<JarEntry> entries = jarFile.entries();

                    ClassLoader classLoader = URLClassLoader.newInstance(new URL[]{file.toURI().toURL()}, ClassLoader.getSystemClassLoader());

                    while (entries.hasMoreElements()) {

                        String entryName = entries.nextElement().getName();

                        if (!entryName.endsWith(".class")) {
                            continue;
                        }

                        String className = entryName.substring(0, entryName.length() - 6).replace("/", ".");

                        Class<?> clazz;

                        try {
                            clazz = Class.forName(className, true, classLoader);
                        } catch (ClassNotFoundException exception) {
                            this.logger.warning("类加载失败 " + entryName, exception);
                            continue;
                        }


                        if (!clazz.isAnnotationPresent(Component.class)) {
                            continue;
                        }


                        Component annotation = clazz.getAnnotation(Component.class);

                        String fileName = file.getName();
                        String artificial = annotation.artificial();

                        if (this.COMPONENT_CLAZZ.containsKey(artificial)) {
                            ModuleWrapper<? extends AbstractEventHandler> exist = this.COMPONENT_CLAZZ.get(artificial);
                            throw new BootException("发现模块冲突 " + fileName + ":" + clazz.getName() + " 模块名" + artificial + "已被" + exist.getPluginName() + ":" + exist.getClassName() + "注册");
                        }

                        if (EventHandlerRunner.class.isAssignableFrom(clazz)) {
                            Class<? extends EventHandlerRunner> runnerClazz = (Class<? extends EventHandlerRunner>) clazz;
                            ModuleWrapper<? extends EventHandlerRunner> wrapper = new ModuleWrapper<>(fileName, runnerClazz);
                            this.COMPONENT_CLAZZ.put(artificial, wrapper);
                            tempComponentRunnerClazz.add(wrapper);
                            this.logger.info("加载定时器 " + clazz.getName());
                            continue;
                        }

                        if (EventHandlerFilter.class.isAssignableFrom(clazz)) {
                            if (!annotation.users() && !annotation.group()) {
                                this.logger.warning("发现未启用过滤器 " + clazz.getName());
                                continue;
                            }
                            Class<? extends EventHandlerFilter> runnerClazz = (Class<? extends EventHandlerFilter>) clazz;
                            ModuleWrapper<? extends EventHandlerFilter> wrapper = new ModuleWrapper<>(fileName, runnerClazz);
                            this.COMPONENT_CLAZZ.put(artificial, wrapper);
                            tempComponentFilterClazz.add(wrapper);
                            this.logger.info("加载过滤器 " + clazz.getName());
                            continue;
                        }

                        if (EventHandlerMonitor.class.isAssignableFrom(clazz)) {
                            if (!annotation.users() && !annotation.group()) {
                                this.logger.warning("发现未启用监听器 " + clazz.getName());
                                continue;
                            }
                            Class<? extends EventHandlerMonitor> runnerClazz = (Class<? extends EventHandlerMonitor>) clazz;
                            ModuleWrapper<? extends EventHandlerMonitor> wrapper = new ModuleWrapper<>(fileName, runnerClazz);
                            this.COMPONENT_CLAZZ.put(artificial, wrapper);
                            tempComponentMonitorClazz.add(wrapper);
                            this.logger.info("加载监听器 " + clazz.getName());
                            continue;
                        }

                        if (EventHandlerExecutor.class.isAssignableFrom(clazz)) {
                            if (!annotation.users() && !annotation.group()) {
                                this.logger.warning("发现未启用执行器 " + clazz.getName());
                                continue;
                            }
                            String command = annotation.command();
                            if (executors.containsKey(command)) {
                                ModuleWrapper<? extends EventHandlerExecutor> exist = executors.get(command);
                                throw new BootException("执行器命令冲突 " + fileName + "中的" + clazz.getName() + "(" + artificial + ")已被" + exist.getPluginName() + "中的" + exist.getClazz().getName() + "(" + exist.getAnnotation().artificial() + "注册");
                            }
                            Class<? extends EventHandlerExecutor> runnerClazz = (Class<? extends EventHandlerExecutor>) clazz;
                            ModuleWrapper<? extends EventHandlerExecutor> wrapper = new ModuleWrapper<>(fileName, runnerClazz);
                            executors.put(command, wrapper);
                            this.COMPONENT_CLAZZ.put(artificial, wrapper);
                            this.COMPONENT_EXECUTOR_CLAZZ.put(artificial, wrapper);
                            this.logger.info("加载执行器 " + clazz.getName());
                            continue;
                        }

                        this.logger.warning("发现无效注解 " + clazz.getName());

                    }

                } catch (IOException exception) {
                    this.logger.warning("加载插件失败 " + file.getAbsolutePath(), exception);
                }
            }
        }


        // ==========================================================================================================================
        //
        // ==========================================================================================================================


        if (this.COMPONENT_RUNNER_CLAZZ.size() + this.COMPONENT_FILTER_CLAZZ.size() + this.COMPONENT_MONITOR_CLAZZ.size() + this.COMPONENT_EXECUTOR_CLAZZ.size() == 0) {
            this.logger.warning("没有扫描到任何模块");
        }


        // ==========================================================================================================================
        // 排序模块


        tempComponentRunnerClazz.sort(Comparator.comparingInt(o -> o.getAnnotation().priority()));
        for (ModuleWrapper<? extends EventHandlerRunner> wrapper : tempComponentRunnerClazz) {
            this.COMPONENT_RUNNER_CLAZZ.put(wrapper.getAnnotation().artificial(), wrapper);
        }


        tempComponentFilterClazz.sort(Comparator.comparingInt(o -> o.getAnnotation().priority()));
        for (ModuleWrapper<? extends EventHandlerFilter> wrapper : tempComponentFilterClazz) {
            this.COMPONENT_FILTER_CLAZZ.put(wrapper.getAnnotation().artificial(), wrapper);
        }


        tempComponentMonitorClazz.sort(Comparator.comparingInt(o -> o.getAnnotation().priority()));
        for (ModuleWrapper<? extends EventHandlerMonitor> wrapper : tempComponentMonitorClazz) {
            this.COMPONENT_MONITOR_CLAZZ.put(wrapper.getAnnotation().artificial(), wrapper);
        }


        // ==========================================================================================================================
        // 注册模块


        this.logger.hint("注册定时器");

        for (Map.Entry<String, ModuleWrapper<? extends EventHandlerRunner>> entry : this.COMPONENT_RUNNER_CLAZZ.entrySet()) {

            String artificial = entry.getKey();
            ModuleWrapper<? extends EventHandlerRunner> wrapper = entry.getValue();
            Component annotation = wrapper.getAnnotation();

            try {
                EventHandlerRunner instance = wrapper.newInstance();
                this.COMPONENT_INSTANCE.put(artificial, instance);
                this.COMPONENT_RUNNER_INSTANCE.put(artificial, instance);
                this.logger.info("注册定时器 " + annotation.priority() + " - " + artificial + " > " + wrapper.getClassName());
            } catch (Exception exception) {
                throw new BootException("定时器创建失败 " + wrapper.getPluginName() + ":" + wrapper.getClazz().getName(), exception);
            }
        }


        // ==========================================================================================================================
        // 注册过滤器


        this.logger.hint("注册过滤器");

        for (Map.Entry<String, ModuleWrapper<? extends EventHandlerMonitor>> entry : this.COMPONENT_MONITOR_CLAZZ.entrySet()) {

            String artificial = entry.getKey();
            ModuleWrapper<? extends EventHandlerMonitor> wrapper = entry.getValue();
            Component annotation = wrapper.getAnnotation();

            try {
                EventHandlerMonitor instance = wrapper.newInstance();
                this.COMPONENT_INSTANCE.put(artificial, instance);
                this.COMPONENT_MONITOR_INSTANCE.put(artificial, instance);

                if (annotation.users()) this.MONITOR_USERS_CHAIN.add(instance);
                if (annotation.group()) this.MONITOR_GROUP_CHAIN.add(instance);

                this.logger.info("注册监听器 " + annotation.priority() + " - " + artificial + " > " + wrapper.getClassName());
            } catch (Exception exception) {
                throw new BootException("监听器注册失败 " + wrapper.getPluginName() + ":" + wrapper.getClazz().getName(), exception);
            }
        }


        // ==========================================================================================================================
        // 注册监听器


        this.logger.hint("注册监听器");

        for (Map.Entry<String, ModuleWrapper<? extends EventHandlerFilter>> entry : this.COMPONENT_FILTER_CLAZZ.entrySet()) {

            String artificial = entry.getKey();
            ModuleWrapper<? extends EventHandlerFilter> wrapper = entry.getValue();
            Component annotation = wrapper.getAnnotation();

            try {
                EventHandlerFilter instance = wrapper.newInstance();
                this.COMPONENT_INSTANCE.put(artificial, instance);
                this.COMPONENT_FILTER_INSTANCE.put(artificial, instance);

                if (annotation.users()) this.FILTER_USERS_CHAIN.add(instance);
                if (annotation.group()) this.FILTER_GROUP_CHAIN.add(instance);

                this.logger.info("注册过滤器 " + annotation.priority() + " - " + artificial + " > " + wrapper.getClassName());
            } catch (Exception exception) {
                throw new BootException("过滤器创建失败 " + wrapper.getPluginName() + ":" + wrapper.getClazz().getName(), exception);
            }
        }


        // ==========================================================================================================================
        // 注册执行器


        this.logger.hint("注册执行器");

        for (Map.Entry<String, ModuleWrapper<? extends EventHandlerExecutor>> entry : this.COMPONENT_EXECUTOR_CLAZZ.entrySet()) {

            String artificial = entry.getKey();
            ModuleWrapper<? extends EventHandlerExecutor> wrapper = entry.getValue();
            Component annotation = wrapper.getAnnotation();

            try {
                EventHandlerExecutor instance = wrapper.newInstance();
                this.COMPONENT_INSTANCE.put(artificial, instance);
                this.COMPONENT_EXECUTOR_INSTANCE.put(artificial, instance);

                if (annotation.users()) this.EXECUTOR_USERS_COMMANDS.put(annotation.command(), instance);
                if (annotation.group()) this.EXECUTOR_GROUP_COMMANDS.put(annotation.command(), instance);

                this.logger.info("注册执行器 " + annotation.priority() + " - " + artificial + " > " + wrapper.getClassName());
            } catch (Exception exception) {
                throw new BootException("执行器注册失败 " + wrapper.getPluginName() + ":" + wrapper.getClazz().getName(), exception);
            }
        }

        this.logger.hint("生成模板消息");

        // 注册完成
        // ==========================================================================================================================


        this.logger.info("组装用户list消息");
        this.MESSAGE_LIST_USERS = this.generateListMessage(this.EXECUTOR_USERS_COMMANDS.entrySet());


        this.logger.info("组装群组list消息");
        this.MESSAGE_LIST_GROUP = this.generateListMessage(this.EXECUTOR_GROUP_COMMANDS.entrySet());


        // ==========================================================================================================================
        // 执行初始化方法


        this.logger.hint("预载定时器");

        for (Map.Entry<String, EventHandlerRunner> entry : this.COMPONENT_RUNNER_INSTANCE.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            try {
                v.initWrapper();
            } catch (Exception exception) {
                throw new BootException("预载定时器失败 " + v.getClass().getName(), exception);
            }
            this.logger.info("预载定时器 " + this.COMPONENT_RUNNER_CLAZZ.get(k).getAnnotation().artificial() + " -> " + v);
        }


        this.logger.hint("预载监听器");

        for (Map.Entry<String, EventHandlerMonitor> entry : this.COMPONENT_MONITOR_INSTANCE.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            try {
                v.initWrapper();
            } catch (Exception exception) {
                throw new BootException("预载监听器失败 " + v.getClass().getName(), exception);
            }
            this.logger.info("预载监听器 " + this.COMPONENT_MONITOR_CLAZZ.get(k).getAnnotation().artificial() + " -> " + v);
        }


        this.logger.hint("预载过滤器");

        for (Map.Entry<String, EventHandlerFilter> entry : this.COMPONENT_FILTER_INSTANCE.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            try {
                v.initWrapper();
            } catch (Exception exception) {
                throw new BootException("预载过滤器失败 " + v.getClass().getName(), exception);
            }
            this.logger.info("预载过滤器 " + this.COMPONENT_FILTER_CLAZZ.get(k).getAnnotation().artificial() + " -> " + v);
        }


        this.logger.hint("预载执行器");

        for (Map.Entry<String, EventHandlerExecutor> entry : this.COMPONENT_EXECUTOR_INSTANCE.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            try {
                v.initWrapper();
            } catch (Exception exception) {
                throw new BootException("预载执行器失败 " + v.getClass().getName(), exception);
            }
            this.logger.info("预载执行器 " + this.COMPONENT_EXECUTOR_CLAZZ.get(k).getAnnotation().artificial() + " -> " + v);
        }


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


        this.logger.hint("启动定时器");


        for (Map.Entry<String, EventHandlerRunner> entry : this.COMPONENT_RUNNER_INSTANCE.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            try {
                v.bootWrapper();
            } catch (Exception exception) {
                throw new BootException("启动定时器失败 " + v.getClass().getName(), exception);
            }
            this.logger.info("启动定时器 " + this.COMPONENT_RUNNER_CLAZZ.get(k).getAnnotation().artificial() + " -> " + v);
        }


        this.logger.hint("启动过滤器");

        for (Map.Entry<String, EventHandlerFilter> entry : this.COMPONENT_FILTER_INSTANCE.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            try {
                v.bootWrapper();
            } catch (Exception exception) {
                throw new BootException("启动过滤器失败 " + v.getClass().getName(), exception);
            }
            this.logger.info("启动过滤器 " + this.COMPONENT_FILTER_CLAZZ.get(k).getAnnotation().artificial() + " -> " + v);
        }


        this.logger.hint("启动监听器");

        for (Map.Entry<String, EventHandlerMonitor> entry : this.COMPONENT_MONITOR_INSTANCE.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            try {
                v.bootWrapper();
            } catch (Exception exception) {
                throw new BootException("启动监听器失败 " + v.getClass().getName(), exception);
            }
            this.logger.info("启动监听器 " + this.COMPONENT_MONITOR_CLAZZ.get(k).getAnnotation().artificial() + " -> " + v);
        }


        this.logger.hint("启动执行器");

        for (Map.Entry<String, EventHandlerExecutor> entry : this.COMPONENT_EXECUTOR_INSTANCE.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            try {
                v.bootWrapper();
            } catch (Exception exception) {
                throw new BootException("启动执行器失败 " + v.getClass().getName(), exception);
            }
            this.logger.info("启动执行器 " + this.COMPONENT_EXECUTOR_CLAZZ.get(k).getAnnotation().artificial() + " -> " + v);
        }


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
        // 关闭监听


        this.logger.hint("结束监听通道");


        this.logger.info("结束私聊监听通道");
        this.userMessageEventListener.complete();

        this.logger.info("结束群聊监听通道");
        this.groupMessageEventListener.complete();

        this.logger.info("结束成员入群监听通道");
        this.memberJoinEventListener.complete();

        this.logger.info("结束成员离群监听通道");
        this.memberLeaveEventListener.complete();

        this.logger.info("结束好友添加监听通道");
        this.newFriendRequestEventListener.complete();

        this.logger.info("结束邀请加群监听通道");
        this.botInvitedJoinGroupRequestEventListener.complete();


        // ==========================================================================================================================
        // 关闭模块


        this.logger.hint("关闭执行器");

        for (Map.Entry<String, EventHandlerExecutor> entry : this.COMPONENT_EXECUTOR_INSTANCE.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            try {
                v.shutWrapper();
            } catch (Exception exception) {
                this.logger.error("关闭执行器失败 " + v.getClass().getName(), exception);
            }
            this.logger.info("关闭执行器 " + this.COMPONENT_EXECUTOR_CLAZZ.get(k).getAnnotation().artificial() + " -> " + v);
        }


        this.logger.hint("关闭监听器");

        ArrayList<Map.Entry<String, EventHandlerMonitor>> reverseMonitor = new ArrayList<>(this.COMPONENT_MONITOR_INSTANCE.entrySet());
        Collections.reverse(reverseMonitor);

        for (Map.Entry<String, EventHandlerMonitor> entry : reverseMonitor) {
            var k = entry.getKey();
            var v = entry.getValue();
            try {
                v.shutWrapper();
            } catch (Exception exception) {
                this.logger.error("关闭监听器失败 " + v.getClass().getName(), exception);
            }
            this.logger.info("关闭监听器 " + this.COMPONENT_MONITOR_CLAZZ.get(k).getAnnotation().artificial() + " -> " + v);
        }


        this.logger.hint("关闭过滤器");

        ArrayList<Map.Entry<String, EventHandlerFilter>> reverseFilter = new ArrayList<>(this.COMPONENT_FILTER_INSTANCE.entrySet());
        Collections.reverse(reverseFilter);

        for (Map.Entry<String, EventHandlerFilter> entry : reverseFilter) {
            var k = entry.getKey();
            var v = entry.getValue();
            try {
                v.shutWrapper();
            } catch (Exception exception) {
                this.logger.error("关闭过滤器失败 " + v.getClass().getName(), exception);
            }
            this.logger.info("关闭过滤器 " + this.COMPONENT_FILTER_CLAZZ.get(k).getAnnotation().artificial() + " -> " + v);
        }


        this.logger.hint("关闭定时器");

        ArrayList<Map.Entry<String, EventHandlerRunner>> reverseRunner = new ArrayList<>(this.COMPONENT_RUNNER_INSTANCE.entrySet());
        Collections.reverse(reverseRunner);

        for (Map.Entry<String, EventHandlerRunner> entry : reverseRunner) {
            var k = entry.getKey();
            var v = entry.getValue();
            try {
                v.shutWrapper();
            } catch (Exception exception) {
                this.logger.error("关闭定时器失败 " + v.getClass().getName(), exception);
            }
            this.logger.info("关闭定时器 " + this.COMPONENT_RUNNER_CLAZZ.get(k).getAnnotation().artificial() + " -> " + v);
        }


        // ==========================================================================================================================
        // 关闭模块


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


        // ==========================================================================================================================
        // 关闭模块


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
        // 关闭模块


        this.logger.hint("关闭机器人");

        this.logger.info("通知机器人关闭");

        if (Driver.isNoLogin()) {
            this.logger.warning("调试模式 不需要关闭机器人");
        } else {
            this.bot.closeAndJoin(null);
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

            if (this.FILTER_USERS_CHAIN.stream().anyMatch(item -> item.handleUsersMessageWrapper(event))) {
                return;
            }

            this.MONITOR_PROCESS.submit(() -> {
                for (EventHandlerMonitor item : this.MONITOR_USERS_CHAIN) {
                    item.handleUsersMessageWrapper(event);
                }
            });

            String content = event.getMessage().contentToString();

            if (this.isCommand(content)) {

                Command command = new Command(content.substring(1));

                switch (command.getCommandName()) {

                    case "?":
                    case "help":
                        if (command.hasCommandBody()) {
                            if (this.EXECUTOR_USERS_COMMANDS.containsKey(command.getParameterSegment(0))) {
                                EventHandlerExecutor executor = this.EXECUTOR_USERS_COMMANDS.get(command.getParameterSegment(0));
                                Driver.sendMessage(event, executor.getHelpMessage());
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
                        if (this.EXECUTOR_USERS_COMMANDS.containsKey(command.getCommandName())) {
                            this.EXECUTOR_USERS_COMMANDS.get(command.getCommandName()).handleUsersMessageWrapper(event, command);
                        }
                }
            }

        } catch (Exception exception) {
            this.logger.dump(event, exception);
        }
    }


    public void handleGroupMessage(GroupMessageEvent event) {

        if (!Driver.isEnable()) return;

        try {


            if (this.FILTER_GROUP_CHAIN.stream().anyMatch(item -> item.handleGroupMessageWrapper(event))) {
                return;
            }

            this.MONITOR_PROCESS.submit(() -> {
                for (EventHandlerMonitor item : this.MONITOR_GROUP_CHAIN) {
                    item.handleGroupMessageWrapper(event);
                }
            });

            String content = event.getMessage().contentToString();

            if (this.isCommand(content)) {
                Command command = new Command(content.substring(1));
                switch (command.getCommandName()) {
                    case "?":
                    case "help":
                        if (command.hasCommandBody()) {
                            if (this.EXECUTOR_GROUP_COMMANDS.containsKey(command.getParameterSegment(0))) {
                                EventHandlerExecutor executor = this.EXECUTOR_GROUP_COMMANDS.get(command.getParameterSegment(0));
                                try {
                                    Driver.sendMessage(event, executor.getHelpMessage());
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
                        if (this.EXECUTOR_GROUP_COMMANDS.containsKey(command.getCommandName())) {
                            this.EXECUTOR_GROUP_COMMANDS.get(command.getCommandName()).handleGroupMessageWrapper(event, command);
                        }

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


    private String generateListMessage(Set<Map.Entry<String, EventHandlerExecutor>> entrySet) {
        if (entrySet.size() == 0) return "无模块";
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, EventHandlerExecutor> entry : entrySet) {
            var v = entry.getValue();
            builder.append(v.getAnnotation().command());
            builder.append(" ");
            builder.append(v.getAnnotation().name());
            builder.append(" ");
            builder.append(v.getAnnotation().description());
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


    @SuppressWarnings("SameParameterValue")
    private void isCallerDriver(int level) {
        Thread thread = Thread.currentThread();
        StackTraceElement[] stackTrace = thread.getStackTrace();
        int maxLevel = Math.min(level, stackTrace.length);
        for (int i = 0; i < maxLevel; i++) {
            if (stackTrace[level].getClassName().startsWith("studio.blacktech.furryblackplus.Driver")) {
                return;
            }
        }
        this.logger.warning("发生违规调用：");
        for (StackTraceElement stackTraceElement : stackTrace) {
            this.logger.warning("    " + stackTraceElement.toString());
        }
        if (!Driver.isDebug()) {
            this.logger.error("检测到违规调用，系统即将关闭。如果要放行此操作，请使用Debug模式。");
            this.signal();
        }
    }

    @Api("列出所有模块")
    public Set<String> listAllModule() {
        this.isCallerDriver(3);
        return this.COMPONENT_CLAZZ.keySet();
    }


    public void shutModule(String name) {
        this.isCallerDriver(3);
        if (!this.COMPONENT_CLAZZ.containsKey(name)) {
            throw new BotException("没有此模块 -> " + name);
        }
        Component annotation = this.COMPONENT_CLAZZ.get(name).getAnnotation();
        AbstractEventHandler instance = this.COMPONENT_INSTANCE.get(annotation.artificial());
        this.logger.info("停止 " + name);
        instance.shutWrapper();
    }

    public void initModule(String name) {
        this.isCallerDriver(3);
        if (!this.COMPONENT_CLAZZ.containsKey(name)) {
            throw new BotException("没有此模块 -> " + name);
        }
        Component annotation = this.COMPONENT_CLAZZ.get(name).getAnnotation();
        AbstractEventHandler instance = this.COMPONENT_INSTANCE.get(annotation.artificial());
        this.logger.info("加载 " + name);
        instance.initWrapper();
    }

    public void bootModule(String name) {
        this.isCallerDriver(3);
        if (!this.COMPONENT_CLAZZ.containsKey(name)) {
            throw new BotException("没有此模块 -> " + name);
        }
        Component annotation = this.COMPONENT_CLAZZ.get(name).getAnnotation();
        AbstractEventHandler instance = this.COMPONENT_INSTANCE.get(annotation.artificial());
        this.logger.info("启动 " + name);
        instance.bootWrapper();
    }

    public void reInstantizeModule(String name) {

        if (!this.COMPONENT_CLAZZ.containsKey(name)) {
            throw new BotException("没有此模块 -> " + name);
        }

        if (this.COMPONENT_RUNNER_CLAZZ.containsKey(name)) {
            ModuleWrapper<? extends EventHandlerRunner> moduleWrapper = this.COMPONENT_RUNNER_CLAZZ.get(name);
            Component annotation = moduleWrapper.getAnnotation();
            EventHandlerRunner newInstance = moduleWrapper.newInstance();
            EventHandlerRunner oldInstance = this.COMPONENT_RUNNER_INSTANCE.get(annotation.artificial());
            this.logger.info("停止旧实例 " + name + " " + oldInstance.hashCode());
            oldInstance.shutWrapper();
            this.logger.info("加载新实例 " + name + " " + newInstance.hashCode());
            this.COMPONENT_INSTANCE.put(annotation.artificial(), newInstance);
            this.COMPONENT_RUNNER_INSTANCE.put(annotation.artificial(), newInstance);
            this.logger.info("预载新实例 " + name + " " + newInstance.hashCode());
            newInstance.initWrapper();
            this.logger.info("启动新实例 " + name + " " + newInstance.hashCode());
            newInstance.bootWrapper();
            return;
        }

        if (this.COMPONENT_FILTER_CLAZZ.containsKey(name)) {
            ModuleWrapper<? extends EventHandlerFilter> moduleWrapper = this.COMPONENT_FILTER_CLAZZ.get(name);
            Component annotation = moduleWrapper.getAnnotation();
            EventHandlerFilter newInstance = moduleWrapper.newInstance();
            EventHandlerFilter oldInstance = this.COMPONENT_FILTER_INSTANCE.get(annotation.artificial());
            this.logger.info("停止旧实例 " + name + " " + oldInstance.hashCode());
            oldInstance.shutWrapper();
            this.logger.info("加载新实例 " + name + " " + newInstance.hashCode());
            this.COMPONENT_INSTANCE.put(annotation.artificial(), newInstance);
            this.COMPONENT_FILTER_INSTANCE.put(annotation.artificial(), newInstance);
            if (annotation.users()) this.FILTER_USERS_CHAIN.replaceAll(item -> newInstance);
            if (annotation.group()) this.FILTER_GROUP_CHAIN.replaceAll(item -> newInstance);
            this.logger.info("预载新实例 " + name + " " + newInstance.hashCode());
            newInstance.initWrapper();
            this.logger.info("启动新实例 " + name + " " + newInstance.hashCode());
            newInstance.bootWrapper();
            return;
        }

        if (this.COMPONENT_MONITOR_CLAZZ.containsKey(name)) {
            ModuleWrapper<? extends EventHandlerMonitor> moduleWrapper = this.COMPONENT_MONITOR_CLAZZ.get(name);
            Component annotation = moduleWrapper.getAnnotation();
            EventHandlerMonitor newInstance = moduleWrapper.newInstance();
            EventHandlerMonitor oldInstance = this.COMPONENT_MONITOR_INSTANCE.get(annotation.artificial());
            this.logger.info("停止旧实例 " + name + " " + oldInstance.hashCode());
            oldInstance.shutWrapper();
            this.logger.info("加载新实例 " + name + " " + newInstance.hashCode());
            this.COMPONENT_INSTANCE.put(annotation.artificial(), newInstance);
            this.COMPONENT_MONITOR_INSTANCE.put(annotation.artificial(), newInstance);
            if (annotation.users()) this.MONITOR_USERS_CHAIN.replaceAll(item -> newInstance);
            if (annotation.group()) this.MONITOR_GROUP_CHAIN.replaceAll(item -> newInstance);
            this.logger.info("预载新实例 " + name + " " + newInstance.hashCode());
            newInstance.initWrapper();
            this.logger.info("启动新实例 " + name + " " + newInstance.hashCode());
            newInstance.bootWrapper();
            return;
        }


        if (this.COMPONENT_EXECUTOR_CLAZZ.containsKey(name)) {
            ModuleWrapper<? extends EventHandlerExecutor> moduleWrapper = this.COMPONENT_EXECUTOR_CLAZZ.get(name);
            Component annotation = moduleWrapper.getAnnotation();
            EventHandlerExecutor newInstance = moduleWrapper.newInstance();
            EventHandlerExecutor oldInstance = this.COMPONENT_EXECUTOR_INSTANCE.get(annotation.artificial());
            this.logger.info("停止旧实例 " + name + " " + oldInstance.hashCode());
            oldInstance.shutWrapper();
            this.logger.info("加载新实例 " + name + " " + newInstance.hashCode());
            this.COMPONENT_INSTANCE.put(annotation.artificial(), newInstance);
            this.COMPONENT_EXECUTOR_INSTANCE.put(annotation.artificial(), newInstance);
            if (annotation.users()) this.EXECUTOR_USERS_COMMANDS.put(annotation.command(), newInstance);
            if (annotation.group()) this.EXECUTOR_GROUP_COMMANDS.put(annotation.command(), newInstance);
            this.logger.info("预载新实例 " + name + " " + newInstance.hashCode());
            newInstance.initWrapper();
            this.logger.info("启动新实例 " + name + " " + newInstance.hashCode());
            newInstance.bootWrapper();
            return;
        }

        throw new BotException("WTF if something inside CC but not any sub clazz");

    }


    @Api("获取模块实例")
    @SuppressWarnings("unchecked")
    public <T extends EventHandlerRunner> T getRunner(Class<T> clazz) {
        List<EventHandlerRunner> collect = this.COMPONENT_RUNNER_INSTANCE.values().stream().filter(clazz::isInstance).collect(Collectors.toUnmodifiableList());
        if (collect.size() == 1) return (T) collect.get(0);
        throw new IllegalArgumentException("No such runner exist");
    }


    public void debug() {


        System.out.println(">> COMPONENT_CLAZZ");
        for (Map.Entry<String, ModuleWrapper<? extends AbstractEventHandler>> entry : this.COMPONENT_CLAZZ.entrySet()) {
            System.out.println(entry.getKey() + " - " + entry.getValue());
        }

        System.out.println(">> COMPONENT_RUNNER_CLAZZ");
        for (Map.Entry<String, ModuleWrapper<? extends EventHandlerRunner>> entry : this.COMPONENT_RUNNER_CLAZZ.entrySet()) {
            System.out.println(entry.getKey() + " - " + entry.getValue());
        }

        System.out.println(">> COMPONENT_FILTER_CLAZZ");
        for (Map.Entry<String, ModuleWrapper<? extends EventHandlerFilter>> entry : this.COMPONENT_FILTER_CLAZZ.entrySet()) {
            System.out.println(entry.getKey() + " - " + entry.getValue());
        }

        System.out.println(">> COMPONENT_MONITOR_CLAZZ");
        for (Map.Entry<String, ModuleWrapper<? extends EventHandlerMonitor>> entry : this.COMPONENT_MONITOR_CLAZZ.entrySet()) {
            System.out.println(entry.getKey() + " - " + entry.getValue());
        }

        System.out.println(">> COMPONENT_EXECUTOR_CLAZZ");
        for (Map.Entry<String, ModuleWrapper<? extends EventHandlerExecutor>> entry : this.COMPONENT_EXECUTOR_CLAZZ.entrySet()) {
            System.out.println(entry.getKey() + " - " + entry.getValue());
        }

        System.out.println(">> COMPONENT_INSTANCE");
        for (Map.Entry<String, AbstractEventHandler> entry : this.COMPONENT_INSTANCE.entrySet()) {
            System.out.println(entry.getKey() + " - " + entry.getValue() + " " + entry.getValue().hashCode());
        }

        System.out.println(">> COMPONENT_RUNNER_INSTANCE");
        for (Map.Entry<String, EventHandlerRunner> entry : this.COMPONENT_RUNNER_INSTANCE.entrySet()) {
            System.out.println(entry.getKey() + " - " + entry.getValue() + " " + entry.getValue().hashCode());
        }

        System.out.println(">> COMPONENT_FILTER_INSTANCE");
        for (Map.Entry<String, EventHandlerFilter> entry : this.COMPONENT_FILTER_INSTANCE.entrySet()) {
            System.out.println(entry.getKey() + " - " + entry.getValue() + " " + entry.getValue().hashCode());
        }

        System.out.println(">> FILTER_USERS_CHAIN");
        for (EventHandlerFilter entry : this.FILTER_USERS_CHAIN) {
            System.out.println(entry);
        }

        System.out.println(">> FILTER_GROUP_CHAIN");
        for (EventHandlerFilter entry : this.FILTER_GROUP_CHAIN) {
            System.out.println(entry);
        }

        System.out.println(">> COMPONENT_MONITOR_INSTANCE");
        for (Map.Entry<String, EventHandlerMonitor> entry : this.COMPONENT_MONITOR_INSTANCE.entrySet()) {
            System.out.println(entry.getKey() + " - " + entry.getValue() + " " + entry.getValue().hashCode());
        }

        System.out.println(">> MONITOR_USERS_CHAIN");
        for (EventHandlerMonitor entry : this.MONITOR_USERS_CHAIN) {
            System.out.println(entry);
        }

        System.out.println(">> MONITOR_GROUP_CHAIN");
        for (EventHandlerMonitor entry : this.MONITOR_GROUP_CHAIN) {
            System.out.println(entry);
        }

        System.out.println(">> COMPONENT_EXECUTOR_INSTANCE");
        for (Map.Entry<String, EventHandlerExecutor> entry : this.COMPONENT_EXECUTOR_INSTANCE.entrySet()) {
            System.out.println(entry.getKey() + " - " + entry.getValue() + " " + entry.getValue().hashCode());
        }

        System.out.println(">> EXECUTOR_USERS_COMMANDS");
        for (Map.Entry<String, EventHandlerExecutor> entry : this.EXECUTOR_USERS_COMMANDS.entrySet()) {
            System.out.println(entry.getKey() + " - " + entry.getValue());
        }

        System.out.println(">> EXECUTOR_GROUP_COMMANDS");
        for (Map.Entry<String, EventHandlerExecutor> entry : this.EXECUTOR_GROUP_COMMANDS.entrySet()) {
            System.out.println(entry.getKey() + " - " + entry.getValue());
        }

    }


    // ==========================================================================================================================================================


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

    @Api("获取图片的URL")
    public String getImageURL(Image image) {
        return Mirai.getInstance().queryImageUrl(this.bot, image);
    }

    @Api("获取用户名片")
    public UserProfile getUserProfile(long user) {
        return Mirai.getInstance().queryProfile(this.bot, user);
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
            return this.getUserProfile(userId).getNickname();
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

}