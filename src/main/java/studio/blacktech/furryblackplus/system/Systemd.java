package studio.blacktech.furryblackplus.system;


import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.event.ConcurrencyKind;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.EventPriority;
import net.mamoe.mirai.event.ListenerHost;
import net.mamoe.mirai.event.events.BotInvitedJoinGroupRequestEvent;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.GroupTempMessageEvent;
import net.mamoe.mirai.event.events.NewFriendRequestEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.utils.BotConfiguration;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ConfigurationBuilder;
import studio.blacktech.furryblackplus.Driver;
import studio.blacktech.furryblackplus.bridge.MiraiBridge;
import studio.blacktech.furryblackplus.system.annotation.ComponentHandlerExecutor;
import studio.blacktech.furryblackplus.system.annotation.ComponentHandlerFilter;
import studio.blacktech.furryblackplus.system.command.Command;
import studio.blacktech.furryblackplus.system.exception.BotException;
import studio.blacktech.furryblackplus.system.exception.initlization.FirstBootException;
import studio.blacktech.furryblackplus.system.exception.initlization.InitException;
import studio.blacktech.furryblackplus.system.exception.initlization.InitLockedException;
import studio.blacktech.furryblackplus.system.exception.initlization.MisConfigException;
import studio.blacktech.furryblackplus.system.handler.AbstractEventHandler;
import studio.blacktech.furryblackplus.system.handler.EventHandlerExecutor;
import studio.blacktech.furryblackplus.system.handler.EventHandlerFilter;
import studio.blacktech.furryblackplus.system.logger.LoggerX;
import studio.blacktech.furryblackplus.system.utilties.HashTool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


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

    private static final String CONF_BOT_COMMAND_PREFIX = "bot.command.prefix";

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
    CONF_NET_RECONNECT_RETRY + "=2147483647\n" +
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


    private final Lock lock = new ReentrantLock(true);
    private final Condition condition = lock.newCondition();


    private Bot bot;

    private Thread thread;

    private char COMMAND_PREFIX = '/';
    private Pattern COMMAND_PATTERN;


    // =========================================================================
    // 预生成消息


    private String MESSAGE_INFO;
    private String MESSAGE_EULA;
    private String MESSAGE_HELP;
    private String MESSAGE_LIST_USERS;
    private String MESSAGE_LIST_GROUP;


    // =========================================================================
    // 模块注册


    private Map<String, AbstractEventHandler> EVENT_HANDLER; // 所有模块及注册名

    private List<EventHandlerFilter> EVENT_HANDLER_FILTER_USERS; // 私聊过滤器注册
    private List<EventHandlerFilter> EVENT_HANDLER_FILTER_GROUP; // 群聊过滤器注册

    private Map<String, EventHandlerExecutor> EVENT_HANDLER_EXECUTOR_USERS; // 私聊执行器注册
    private Map<String, EventHandlerExecutor> EVENT_HANDLER_EXECUTOR_GROUP; // 群聊执行器注册


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


        // ==========================================================================================================================
        // 初始化配置文件


        logger.hint("初始化Systemd配置文件");


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

        try {
            config.load(new FileInputStream(FILE_CONFIG));
        } catch (IOException exception) {
            logger.error("核心配置文件读取错误 即将关闭 " + FILE_CONFIG.getAbsolutePath());
            throw new BotException("核心配置文件读取错误 " + FILE_CONFIG.getAbsolutePath(), exception);
        }


        // ==========================================================================================================================
        // 读取配置


        String prefix = config.getProperty(CONF_BOT_COMMAND_PREFIX);

        if (prefix == null) {

            logger.warning("指定的命令前缀不可用 将自动设置为默认值");

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
                    logger.warning("指定的命令前缀不可用 将自动设置为默认值");

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
        // 读取机器人配置


        logger.hint("加载机器人配置");
        BotConfiguration configuration = new BotConfiguration();


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

        String shadow_ACCOUNT_PW = ACCOUNT_PW.charAt(0) + "*".repeat(length - 2) + ACCOUNT_PW.charAt(length - 1);

        logger.seek("QQ密码 " + shadow_ACCOUNT_PW);


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


        String temp_DEVICE_INFO = config.getProperty(CONF_BOT_DEVICE_INFO);

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

            logger.seek("设备信息 " + DEVICE_INFO.getName());

        } else {

            logger.seek("设备信息不存在 将由Mirai生成");

        }

        configuration.fileBasedDeviceInfo(DEVICE_INFO.getAbsolutePath());

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


        long NET_RECONNECT_RETRY = parseLong(config.getProperty(CONF_NET_RECONNECT_RETRY));
        long NET_RECONNECT_DELAY = parseLong(config.getProperty(CONF_NET_RECONNECT_DELAY));
        long NET_RECONNECT_PERIOD = parseLong(config.getProperty(CONF_NET_RECONNECT_PERIOD));

        int RECONNECT_TIME;

        if (NET_RECONNECT_RETRY > Integer.MAX_VALUE) {
            logger.warning(CONF_NET_RECONNECT_RETRY + " 最大可接受值为 " + Integer.MAX_VALUE);
            RECONNECT_TIME = Integer.MAX_VALUE;
        } else if (NET_RECONNECT_RETRY < -1) {
            RECONNECT_TIME = Integer.MAX_VALUE;
        } else {
            RECONNECT_TIME = Long.valueOf(NET_RECONNECT_RETRY).intValue();
        }


        logger.seek("重连间隔 " + NET_RECONNECT_PERIOD);
        logger.seek("重连延迟 " + NET_RECONNECT_DELAY);
        logger.seek("重连次数 " + RECONNECT_TIME);


        configuration.setReconnectPeriodMillis(NET_RECONNECT_PERIOD);
        configuration.setFirstReconnectDelayMillis(NET_RECONNECT_DELAY);
        configuration.setReconnectionRetryTimes(RECONNECT_TIME);


        // 传入日志


        configuration.setBotLoggerSupplier(bot -> new LoggerX("MiraiBot"));
        configuration.setNetworkLoggerSupplier(bot -> new LoggerX("MiraiNet"));


        // ==========================================================================================================================
        // 创建机器人


        logger.hint("初始化机器人");
        bot = BotFactory.INSTANCE.newBot(ACCOUNT_QQ, ACCOUNT_PW, configuration);


        logger.info("机器人类型 " + bot.getClass().getName());


        // ==========================================================================================================================
        // 初始化注册


        EVENT_HANDLER = new LinkedHashMap<>();

        EVENT_HANDLER_FILTER_USERS = new LinkedList<>();
        EVENT_HANDLER_FILTER_GROUP = new LinkedList<>();

        EVENT_HANDLER_EXECUTOR_USERS = new LinkedHashMap<>();
        EVENT_HANDLER_EXECUTOR_GROUP = new LinkedHashMap<>();


        // ==========================================================================================================================
        // 扫描模块


        logger.hint("开始模块扫描");


        Reflections reflections = new Reflections(
                new ConfigurationBuilder()
                        .forPackages("studio.blacktech.furryblackplus.module")
                        .addScanners(new SubTypesScanner())
        );


        // ==========================================================================================================================
        // 分析扫描结果


        Set<Class<? extends EventHandlerFilter>> FILTERS = reflections.getSubTypesOf(EventHandlerFilter.class);
        logger.hint("扫描到以下过滤器");
        FILTERS.forEach(item -> logger.info(item.getName()));


        Set<Class<? extends EventHandlerExecutor>> EXECUTORS = reflections.getSubTypesOf(EventHandlerExecutor.class);
        logger.hint("扫描到以下执行器");
        EXECUTORS.forEach(item -> logger.info(item.getName()));


        // ==========================================================================================================================
        // 注册过滤器


        logger.hint("初始化过滤器链");
        for (Class<? extends EventHandlerFilter> item : FILTERS) {
            try {
                if (!item.isAnnotationPresent(ComponentHandlerFilter.class)) {
                    logger.warning("发现无注解过滤器 " + item.getName());
                    continue;
                }
                ComponentHandlerFilter annotation = item.getAnnotation(ComponentHandlerFilter.class);
                if (!annotation.users() && !annotation.group()) {
                    logger.warning("发现无用过滤器 " + item.getName());
                    continue;
                }
                EventHandlerFilter.FilterInfo info = new EventHandlerFilter.FilterInfo(
                        annotation.name(),
                        annotation.description(),
                        annotation.privacy()
                );
                EventHandlerFilter instance = item.getConstructor(EventHandlerFilter.FilterInfo.class).newInstance(info);
                instance.init();
                logger.info("注册入过滤链 " + info.NAME + " - " + item.getName());
                EVENT_HANDLER.put(annotation.artificial(), instance);
                if (annotation.users()) EVENT_HANDLER_FILTER_USERS.add(instance);
                if (annotation.group()) EVENT_HANDLER_FILTER_GROUP.add(instance);
            } catch (Exception exception) {
                throw new BotException("过滤器初始化失败 " + item.getName(), exception);
            }
        }


        // ==========================================================================================================================
        // 初始化执行器


        logger.hint("初始化执行器链");
        for (Class<? extends EventHandlerExecutor> item : EXECUTORS) {
            try {
                if (!item.isAnnotationPresent(ComponentHandlerExecutor.class)) {
                    logger.warning("发现无注解执行器 " + item.getName());
                    continue;
                }
                ComponentHandlerExecutor annotation = item.getAnnotation(ComponentHandlerExecutor.class);
                if (!annotation.users() && !annotation.group()) {
                    logger.warning("发现无用执行器 " + item.getName());
                    continue;
                }
                EventHandlerExecutor.ExecutorInfo info = new EventHandlerExecutor.ExecutorInfo(
                        annotation.name(),
                        annotation.description(),
                        annotation.privacy(),
                        annotation.command(),
                        annotation.usage()
                );
                EventHandlerExecutor instance = item.getConstructor(EventHandlerExecutor.ExecutorInfo.class).newInstance(info);
                instance.init();
                logger.info("注册入执行链 " + info.COMMAND + " - " + item.getName());
                EVENT_HANDLER.put(annotation.command(), instance);
                if (annotation.users()) EVENT_HANDLER_EXECUTOR_USERS.put(instance.INFO.COMMAND, instance);
                if (annotation.group()) EVENT_HANDLER_EXECUTOR_GROUP.put(instance.INFO.COMMAND, instance);
            } catch (Exception exception) {
                throw new BotException("执行器初始化失败 " + item.getName(), exception);
            }
        }


        logger.info("组装用户list消息");
        MESSAGE_LIST_USERS = generateListMessage(EVENT_HANDLER_EXECUTOR_USERS.entrySet());


        logger.info("组装群组list消息");
        MESSAGE_LIST_GROUP = generateListMessage(EVENT_HANDLER_EXECUTOR_GROUP.entrySet());


        // ==========================================================================================================================
        // 注册消息路由

        bot.getEventChannel().registerListenerHost(this);


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
            logger.hint("跳过登录");
        } else {
            logger.hint("开始登录");
            bot.login();
        }


        // ==========================================================================================================================
        // 启动模块


        logger.hint("启动模块");

        for (Map.Entry<String, AbstractEventHandler> entry : EVENT_HANDLER.entrySet()) {
            try {
                entry.getValue().boot();
                logger.info("启动模块 " + entry.getKey() + " -> " + entry.getValue().getClass().getName());
            } catch (Exception exception) {
                throw new BotException("过滤器启动失败 " + entry.getValue().getClass().getName(), exception);
            }
        }


        // ==========================================================================================================================
        // 列出所有好友和群组


        if (!Driver.isDryRun()) {

            logger.hint("机器人昵称 " + bot.getNick());

            logger.hint("所有好友");
            bot.getFriends().forEach(item -> logger.info(item.getNick() + "(" + item.getId() + ")"));

            logger.hint("所有群组");
            bot.getGroups().forEach(item -> logger.info(item.getName() + "(" + item.getId() + ") " + item.getMembers().size() + " -> " + item.getOwner().getNameCard() + "(" + item.getOwner().getId() + ")"));

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


        logger.hint("关闭模块");


        for (Map.Entry<String, AbstractEventHandler> entry : EVENT_HANDLER.entrySet()) {
            try {
                entry.getValue().shut();
                logger.info("关闭模块 " + entry.getKey() + " -> " + entry.getValue().getClass().getName());
            } catch (Exception exception) {
                logger.error("关闭失败 " + entry.getValue().getClass().getName(), exception);
            }
        }


        // ==========================================================================================================================
        // 关闭阻塞


        logger.hint("关闭机器人");

        MiraiBridge.close(bot);
        MiraiBridge.join(bot);

    }


    // ==========================================================================================================================================================
    //
    // 工具
    //
    // ==========================================================================================================================================================


    private long parseLong(String temp) throws BotException {
        try {
            return Long.parseLong(temp);
        } catch (Exception exception) {
            throw new MisConfigException("配置解析错误 " + temp, exception);
        }
    }


    private String readFile(File file) throws BotException {

        if (!file.exists()) {
            try {
                //noinspection ResultOfMethodCallIgnored
                file.createNewFile();
                logger.hint("创建新的文件 " + file.getAbsolutePath());
            } catch (IOException exception) {
                throw new BotException("文件创建失败 " + file.getAbsolutePath(), exception);
            }
        }

        if (!file.exists()) throw new BotException("文件不存在 " + file.getAbsolutePath());
        if (!file.canRead()) throw new BotException("文件无权读取 " + file.getAbsolutePath());

        try (
                FileReader fileReader = new FileReader(file, StandardCharsets.UTF_8);
                BufferedReader bufferedReader = new BufferedReader(fileReader)
        ) {

            String temp;
            StringBuilder builder = new StringBuilder();
            while ((temp = bufferedReader.readLine()) != null) builder.append(temp).append("\r\n");
            return builder.toString();

        } catch (FileNotFoundException exception) {
            throw new BotException("文件不存在 " + file.getAbsolutePath(), exception);
        } catch (IOException exception) {
            throw new BotException("文件读取失败 " + file.getAbsolutePath(), exception);
        }
    }


    private String generateListMessage(Set<Map.Entry<String, EventHandlerExecutor>> entrySet) {

        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, EventHandlerExecutor> entry : entrySet) {
            var k = entry.getKey();
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
    // 监听器
    //
    // ==========================================================================================================================================================


    @EventHandler
    public void handleBotInvitedJoinGroupRequestEvent(BotInvitedJoinGroupRequestEvent event) {
        logger.hint("BOT被邀请入群 " + event.getGroupName() + "(" + event.getGroupId() + ") 邀请人 " + event.getInvitorNick() + "(" + event.getInvitorId() + ")");
        event.accept();
    }


    @EventHandler
    public void handleNewFriendRequestEvent(NewFriendRequestEvent event) {
        logger.hint("BOT被添加好友 " + event.getFromNick() + "(" + event.getFromId() + ")");
        event.accept();
    }


    // ==========================================================================================================================
    // MessageEvent居然不支持cancel
    // 消息串


    private boolean isCommand(String content) {
        if (content.length() < 3) return false;
        if (content.charAt(0) != COMMAND_PREFIX) return false;
        return COMMAND_PATTERN.matcher(content).find();
    }


    @EventHandler(
            priority = EventPriority.MONITOR,
            concurrency = ConcurrencyKind.CONCURRENT
    )
    public void handleTempMessageExecutor(GroupTempMessageEvent event) {

        if (!Driver.isEnable()) return;

        String content = event.getMessage().contentToString();

        try {

            if (EVENT_HANDLER_FILTER_USERS.stream().anyMatch(item -> item.handleTempMessage(event, content))) {
                logger.hint("临时消息被拦截 " + event.getSender().getId() + " ->" + event.getMessage());
                return;
            }

            if (isCommand(content)) {

                Command command = new Command(content.substring(1));

                switch (command.getCommandName()) {

                    case "?":
                    case "help":
                        // 🚧 路障 施工中
                        // event.getSender().sendMessage("\uD83D\uDEA7 暂不可用");
                        if (command.hasCommandBody()) {
                            if (EVENT_HANDLER_EXECUTOR_USERS.containsKey(command.getParameterSegment(0))) {
                                EventHandlerExecutor executor = EVENT_HANDLER_EXECUTOR_USERS.get(command.getParameterSegment(0));
                                event.getSender().sendMessage(executor.INFO.HELP);
                            }
                        } else {
                            event.getSender().sendMessage(MESSAGE_HELP);
                        }
                        break;

                    case "list":
                        event.getSender().sendMessage(MESSAGE_LIST_USERS);
                        break;

                    case "info":
                        event.getSender().sendMessage(MESSAGE_INFO);
                        break;

                    case "eula":
                        event.getSender().sendMessage(MESSAGE_EULA);
                        break;

                    default:
                        if (EVENT_HANDLER_EXECUTOR_USERS.containsKey(command.getCommandName())) {
                            EVENT_HANDLER_EXECUTOR_USERS.get(command.getCommandName()).handleTempMessage(event, command);
                        }
                }
            }

        } catch (Exception exception) {
            logger.dump(event, exception);
        }
    }


    @EventHandler(
            priority = EventPriority.MONITOR,
            concurrency = ConcurrencyKind.CONCURRENT
    )
    public void handleFriendMessageExecutor(FriendMessageEvent event) {

        if (!Driver.isEnable()) return;

        String content = event.getMessage().contentToString();

        try {

            if (EVENT_HANDLER_FILTER_USERS.stream().anyMatch(item -> item.handleFriendMessage(event, content))) {
                logger.hint("好友消息被拦截 " + event.getSender().getId() + " ->" + event.getMessage());
                return;
            }


            if (isCommand(content)) {

                Command command = new Command(content.substring(1));

                switch (command.getCommandName()) {

                    case "?":
                    case "help":
                        // 🚧 路障 施工中
                        // event.getSender().sendMessage("\uD83D\uDEA7 暂不可用");
                        if (command.hasCommandBody()) {
                            if (EVENT_HANDLER_EXECUTOR_USERS.containsKey(command.getParameterSegment(0))) {
                                EventHandlerExecutor executor = EVENT_HANDLER_EXECUTOR_USERS.get(command.getParameterSegment(0));
                                event.getSender().sendMessage(executor.INFO.HELP);
                            }
                        } else {
                            event.getSender().sendMessage(MESSAGE_HELP);
                        }
                        break;

                    case "list":
                        event.getSender().sendMessage(MESSAGE_LIST_USERS);
                        break;

                    case "info":
                        event.getSender().sendMessage(MESSAGE_INFO);
                        break;

                    case "eula":
                        event.getSender().sendMessage(MESSAGE_EULA);
                        break;

                    default:
                        if (EVENT_HANDLER_EXECUTOR_USERS.containsKey(command.getCommandName())) {
                            EVENT_HANDLER_EXECUTOR_USERS.get(command.getCommandName()).handleFriendMessage(event, command);
                        }
                }
            }

        } catch (Exception exception) {
            logger.dump(event, exception);
        }
    }


    @EventHandler(
            priority = EventPriority.MONITOR,
            concurrency = ConcurrencyKind.CONCURRENT
    )
    public void handleGroupMessageExecutor(GroupMessageEvent event) {

        if (!Driver.isEnable()) return;

        String content = event.getMessage().contentToString();

        try {

            if (EVENT_HANDLER_FILTER_GROUP.stream().anyMatch(item -> item.handleGroupMessage(event, content))) {
                logger.hint("群组消息被拦截 " + event.getSender().getId() + " ->" + event.getMessage());
                return;
            }

            if (isCommand(content)) {

                Command command = new Command(content.substring(1));

                switch (command.getCommandName()) {

                    case "?":
                    case "help":
                        // 🚧 路障 施工中
                        // event.getGroup().sendMessage("\uD83D\uDEA7 暂不可用");
                        if (command.hasCommandBody()) {
                            if (EVENT_HANDLER_EXECUTOR_GROUP.containsKey(command.getParameterSegment(0))) {
                                EventHandlerExecutor executor = EVENT_HANDLER_EXECUTOR_GROUP.get(command.getParameterSegment(0));
                                try {
                                    event.getSender().sendMessage(executor.INFO.HELP);
                                } catch (Exception exception) {
                                    At at = new At(event.getSender().getId());
                                    event.getGroup().sendMessage(at.plus("帮助信息发送至私聊失败 请允许临时会话权限"));
                                }
                            }
                        } else {
                            try {
                                event.getSender().sendMessage(MESSAGE_HELP);
                            } catch (Exception exception) {
                                At at = new At(event.getSender().getId());
                                event.getGroup().sendMessage(at.plus("帮助信息发送至私聊失败 请允许临时会话权限"));
                            }
                        }
                        break;

                    case "list":
                        try {
                            event.getSender().sendMessage(MESSAGE_LIST_GROUP);
                        } catch (Exception exception) {
                            At at = new At(event.getSender().getId());
                            event.getGroup().sendMessage(at.plus("可用命令发送至私聊失败 请允许临时会话权限"));
                        }
                        break;

                    case "info":
                        try {
                            event.getSender().sendMessage(MESSAGE_INFO);
                        } catch (Exception exception) {
                            At at = new At(event.getSender().getId());
                            event.getGroup().sendMessage(at.plus("关于发送至私聊失败 请允许临时会话权限"));
                        }
                        break;

                    case "eula":
                        try {
                            event.getSender().sendMessage(MESSAGE_EULA);
                        } catch (Exception exception) {
                            At at = new At(event.getSender().getId());
                            event.getGroup().sendMessage(at.plus("EULA发送至私聊失败 请允许临时会话权限"));
                        }
                        break;

                    default:
                        if (EVENT_HANDLER_EXECUTOR_GROUP.containsKey(command.getCommandName())) {
                            EVENT_HANDLER_EXECUTOR_GROUP.get(command.getCommandName()).handleGroupMessage(event, command);
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


    // ==========================================================================================================================================================
    //
    // 模块相关
    //
    // ==========================================================================================================================================================


    public List<String> listAllPlugin() {
        return EVENT_HANDLER.keySet().stream().collect(Collectors.toUnmodifiableList());
    }


    public void reloadPlugin(String name) {
        if (!EVENT_HANDLER.containsKey(name)) {
            logger.warning("不存在此模块 -> " + name);
            return;
        }
        AbstractEventHandler instance = EVENT_HANDLER.get(name);
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

    // ==========================================================================================================================================================
    //
    // BOT相关封装
    //
    // ==========================================================================================================================================================


    public long getBotID() {
        return bot.getId();
    }


    public Friend getFriend(long id) {
        return bot.getFriend(id);
    }


    public Group getGroup(long id) {
        return bot.getGroup(id);
    }


    public Member getGroupMember(long group, long member) {
        return bot.getGroupOrFail(group).getOrFail(member);
    }


    public void sendGroupMessage(Group group, Member member, String message) {
        sendGroupMessage(group.getId(), member.getId(), message);
    }


    public void sendGroupMessage(long group, long member, String message) {
        bot.getGroupOrFail(group).getOrFail(member).sendMessage(new At(member).plus(message));
    }

}



