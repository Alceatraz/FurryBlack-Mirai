package studio.blacktech.furryblackplus.core.utilties;

import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.GroupTempMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.SingleMessage;
import net.mamoe.mirai.utils.PlatformLogger;
import studio.blacktech.furryblackplus.core.annotation.Api;
import studio.blacktech.furryblackplus.core.exception.BotException;
import studio.blacktech.furryblackplus.core.exception.initlization.BootLockedException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


@SuppressWarnings("unused")

@Api("日志工具 为了兼容Mirai继承了PlatformLogger 同时添加了新的级别 seek hint")
public final class LoggerX extends PlatformLogger {

    public static final List<String> LEVELS = Arrays.asList("MUTE", "ERROR", "WARN", "HINT", "SEEK", "INFO", "DEBUG", "VERBOSE", "ALL");


    private static boolean INIT_LOCK = false;
    private static File FILE_LOGGER;
    private final String name;

    public LoggerX(String name) {
        super(name);
        this.name = name;
    }

    public LoggerX(Class<?> clazz) {
        super(clazz.getSimpleName());
        this.name = clazz.getSimpleName();
    }

    public static void init(File file) throws BotException {
        if (INIT_LOCK) throw new BootLockedException();
        INIT_LOCK = true;
        FILE_LOGGER = file;
    }


    public enum LEVEL {
        MUTE(0),
        ERROR(1),
        WARN(2),
        HINT(3),
        SEEK(4),
        INFO(5),
        DEBUG(6),
        VERBOSE(7),
        ALL(Integer.MAX_VALUE);

        private final int level;

        LEVEL(int level) {
            this.level = level;
        }

        public int getLevel() {
            return level;
        }

    }

    public static boolean setPrintLevel(String level) {
        LEVEL loggerLevel;
        try {
            loggerLevel = LEVEL.valueOf(level);
        } catch (Exception exception) {
            return false;
        }
        setPrintLevel(loggerLevel);
        return true;
    }

    public static void setPrintLevel(LEVEL level) {
        int i = level.getLevel();
        PRINT_ERROR = 0 < i;
        PRINT_WARN = 1 < i;
        PRINT_HINT = 2 < i;
        PRINT_SEEK = 3 < i;
        PRINT_INFO = 4 < i;
        PRINT_DEBUG = 5 < i;
        PRINT_VERBOSE = 6 < i;
    }


    private static boolean PRINT_ERROR = true;
    private static boolean PRINT_WARN = true;
    private static boolean PRINT_HINT = true;
    private static boolean PRINT_SEEK = true;
    private static boolean PRINT_INFO = true;
    private static boolean PRINT_DEBUG = true;
    private static boolean PRINT_VERBOSE = true;

    // ==================================================================================================
    //
    //
    // ==================================================================================================


    public void bypass(String message) {
        String result = "[" + DateTool.datetime() + "][BYPS][" + name + "]" + message;
        System.out.println(result);
        LoggerX.writeLog(result);
    }

    public void bypass(String message, Throwable throwable) {
        String result = "[" + DateTool.datetime() + "][BYPS][" + name + "]" + message + "\r\n" + extractTrace(throwable);
        System.out.println(result);
        LoggerX.writeLog(result);
    }


    @Override
    public void error0(String message) {
        String result = "[" + DateTool.datetime() + "][EXCE][" + name + "]" + message;
        if (PRINT_ERROR) System.out.println(Color.RED + result + Color.RESET);
        LoggerX.writeLog(result);
    }

    @Override
    public void error0(String message, Throwable throwable) {
        String result = "[" + DateTool.datetime() + "][EXCE][" + name + "]" + message + "\r\n" + extractTrace(throwable);
        if (PRINT_ERROR) System.out.println(Color.RED + result + Color.RESET);
        LoggerX.writeLog(result);
    }


    @Override
    public void warning0(String message) {
        String result = "[" + DateTool.datetime() + "][WARN][" + name + "]" + message;
        if (PRINT_WARN) System.out.println(Color.LIGHT_YELLOW + result + Color.RESET);
        LoggerX.writeLog(result);
    }

    @Override
    public void warning0(String message, Throwable throwable) {
        String result = "[" + DateTool.datetime() + "][WARN][" + name + "]" + message + "\r\n" + extractTrace(throwable);
        if (PRINT_WARN) System.out.println(Color.LIGHT_YELLOW + result + Color.RESET);
        LoggerX.writeLog(result);
    }


    public void hint(String message) {
        String result = "[" + DateTool.datetime() + "][HINT][" + name + "]" + message;
        if (PRINT_HINT) System.out.println(Color.LIGHT_CYAN + result + Color.RESET);
        LoggerX.writeLog(result);
    }

    public void hint(String message, Throwable throwable) {
        String result = "[" + DateTool.datetime() + "][HINT][" + name + "]" + message + "\r\n" + extractTrace(throwable);
        if (PRINT_HINT) System.out.println(Color.LIGHT_CYAN + result + Color.RESET);
        LoggerX.writeLog(result);
    }


    public void seek(String message) {
        String result = "[" + DateTool.datetime() + "][SEEK][" + name + "]" + message;
        if (PRINT_SEEK) System.out.println(Color.LIGHT_GREEN + result + Color.RESET);
        LoggerX.writeLog(result);
    }

    public String seek(String message, String value) {
        String result = "[" + DateTool.datetime() + "][SEEK][" + name + "]" + message + " `" + value + "`";
        if (PRINT_SEEK) System.out.println(Color.LIGHT_GREEN + result + Color.RESET);
        LoggerX.writeLog(result);
        return value;
    }

    public void seek(String message, Throwable throwable) {
        String result = "[" + DateTool.datetime() + "][SEEK][" + name + "]" + message + "\r\n" + extractTrace(throwable);
        if (PRINT_SEEK) System.out.println(Color.LIGHT_GREEN + result + Color.RESET);
        LoggerX.writeLog(result);
    }


    @Override
    public void info0(String message) {
        String result = "[" + DateTool.datetime() + "][INFO][" + name + "]" + message;
        if (PRINT_INFO) System.out.println(result);
        LoggerX.writeLog(result);
    }

    @Override
    public void info0(String message, Throwable throwable) {
        String result = "[" + DateTool.datetime() + "][INFO][" + name + "]" + message + "\r\n" + extractTrace(throwable);
        if (PRINT_INFO) System.out.println(result);
        LoggerX.writeLog(result);
    }


    @Override
    public void debug0(String message) {
        String result = "[" + DateTool.datetime() + "][DEBG][" + name + "]" + message;
        if (PRINT_DEBUG) System.out.println(Color.GRAY + result + Color.RESET);
        LoggerX.writeLog(result);
    }

    @Override
    public void debug0(String message, Throwable throwable) {
        String result = "[" + DateTool.datetime() + "][DEBG][" + name + "]" + message + "\r\n" + extractTrace(throwable);
        if (PRINT_DEBUG) System.out.println(Color.GRAY + result + Color.RESET);
        LoggerX.writeLog(result);
    }


    @Override
    public void verbose0(String message) {
        String result = "[" + DateTool.datetime() + "][VERB][" + name + "]" + message;
        if (PRINT_VERBOSE) System.out.println(Color.GRAY + result + Color.RESET);
        LoggerX.writeLog(result);
    }

    @Override
    public void verbose0(String message, Throwable throwable) {
        String result = "[" + DateTool.datetime() + "][VERB][" + name + "]" + message + "\r\n" + extractTrace(throwable);
        if (PRINT_VERBOSE) System.out.println(Color.GRAY + result + Color.RESET);
        LoggerX.writeLog(result);
    }


    // ==================================================================================================
    //
    //
    // ==================================================================================================


    public void dump(MessageEvent event, Throwable throwable) {

        long TIME_CODE = System.currentTimeMillis();

        StringBuilder builder = new StringBuilder();

        builder.append("[");
        builder.append(DateTool.datetime());
        builder.append("][DUMP][");
        builder.append(name);
        builder.append("]");

        if (event instanceof GroupMessageEvent) {

            builder.append("GroupMessageEvent ");

            Group group = ((GroupMessageEvent) event).getGroup();
            builder.append(group.getName());
            builder.append("(");
            builder.append(group.getId());
            builder.append(") -> ");

            Member sender = (Member) event.getSender();
            builder.append(sender.getNick());
            builder.append("[");
            builder.append(sender.getNameCard());
            builder.append("](");
            builder.append(sender.getId());
            builder.append(") -> ");

        } else if (event instanceof FriendMessageEvent) {

            builder.append("FriendMessageEvent");

            Friend sender = (Friend) event.getSender();
            builder.append(sender.getNick());
            builder.append("(");
            builder.append(sender.getId());
            builder.append(") -> ");

        } else if (event instanceof GroupTempMessageEvent) {

            builder.append("TempMessageEvent");

            Member sender = ((GroupTempMessageEvent) event).getSender();
            builder.append(sender.getNick());
            builder.append("(");
            builder.append(sender.getId());
            builder.append(") -> ");

        } else {
            builder.append(event.getClass());
        }


        MessageChain messages = event.getMessage();

        builder.append(messages.size());

        for (int i = 0; i < messages.size(); i++) {
            SingleMessage message = messages.get(i);
            builder.append(i);
            builder.append(" ");
            builder.append(message.getClass().getSimpleName());
            builder.append(" ");
            builder.append(message.toString());
            builder.append("\r\n");
        }

        builder.append(extractTrace(throwable));

        String result = builder.toString();

        System.out.println(Color.LIGHT_PURPLE + "信息已转储 -> " + TIME_CODE + Color.RESET);
        LoggerX.writeLog(TIME_CODE + "\r\n" + result);

    }


    // ==================================================================================================
    //
    //
    // ==================================================================================================


    public static String extractTrace(Throwable throwable) {
        if (throwable == null) return "没有异常";
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        throwable.printStackTrace(printWriter);
        return stringWriter.toString();

    }

    private static synchronized void writeLog(String message) {
        try (FileWriter writer = new FileWriter(FILE_LOGGER, StandardCharsets.UTF_8, true)) {
            writer.append(message);
            writer.append("\r\n");
            writer.flush();
        } catch (IOException exception) {
            System.err.println("Writer log failed \n" + extractTrace(exception));
        }
    }


    // ==================================================================================================
    //
    //
    // ==================================================================================================


    public static String unicode(String raw) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < raw.length(); i++) {
            builder.append("\\u");
            builder.append(String.format("%1$4s", Integer.toHexString(raw.charAt(i) & 0xffff)).replace(" ", "0"));
        }
        return builder.toString();
    }


    public static String[] unicodeid(String raw) {
        List<String> tmp = new LinkedList<>();
        for (int i = 0; i < raw.length(); i++) tmp.add(Integer.toHexString(raw.charAt(i) & 0xffff));
        String[] res = new String[tmp.size()];
        tmp.toArray(res);
        return res;
    }

}
