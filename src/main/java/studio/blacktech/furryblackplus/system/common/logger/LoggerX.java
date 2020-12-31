package studio.blacktech.furryblackplus.system.common.logger;

import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.FriendMessageEvent;
import net.mamoe.mirai.message.GroupMessageEvent;
import net.mamoe.mirai.message.MessageEvent;
import net.mamoe.mirai.message.TempMessageEvent;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.SingleMessage;
import net.mamoe.mirai.utils.PlatformLogger;
import studio.blacktech.furryblackplus.system.common.exception.BotException;
import studio.blacktech.furryblackplus.system.common.exception.initlization.InitLockedException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;


public class LoggerX extends PlatformLogger {


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
        if (INIT_LOCK) {
            throw new InitLockedException();
        }
        INIT_LOCK = true;
        FILE_LOGGER = file;
    }


    // ==================================================================================================
    //
    //
    // ==================================================================================================


    @Override
    public void error0(String message) {
        String result = "[" + LoggerX.datetime() + "][ERROR][" + name + "]" + message;
        LoggerX.printStd(Color.RED + result + Color.RESET);
        LoggerX.writeLog(result);
    }


    @Override
    public void error0(String message, Throwable throwable) {
        String result = "[" + LoggerX.datetime() + "][INFO][" + name + "]" + message + "\r\n" + extractTrace(throwable);
        LoggerX.printStd(Color.RED + result + Color.RESET);
        LoggerX.writeLog(result);
    }


    @Override
    public void warning0(String message) {
        String result = "[" + LoggerX.datetime() + "][WARN][" + name + "]" + message;
        LoggerX.printStd(Color.LIGHT_YELLOW + result + Color.RESET);
        LoggerX.writeLog(result);
    }


    @Override
    public void warning0(String message, Throwable throwable) {
        String result = "[" + LoggerX.datetime() + "][WARN][" + name + "]" + message + "\r\n" + extractTrace(throwable);
        LoggerX.printStd(Color.LIGHT_YELLOW + result + Color.RESET);
        LoggerX.writeLog(result);
    }


    public void hint(String message) {
        String result = "[" + LoggerX.datetime() + "][HINT][" + name + "]" + message;
        LoggerX.printStd(Color.LIGHT_CYAN + result + Color.RESET);
        LoggerX.writeLog(result);
    }


    public void hint(String message, Throwable throwable) {
        String result = "[" + LoggerX.datetime() + "][HINT][" + name + "]" + message + "\r\n" + extractTrace(throwable);
        LoggerX.printStd(Color.LIGHT_CYAN + result + Color.RESET);
        LoggerX.writeLog(result);
    }


    public void seek(String message) {
        String result = "[" + LoggerX.datetime() + "][SEEK][" + name + "]" + message;
        LoggerX.printStd(Color.LIGHT_GREEN + result + Color.RESET);
        LoggerX.writeLog(result);
    }


    public void seek(String message, Throwable throwable) {
        String result = "[" + LoggerX.datetime() + "][SEEK][" + name + "]" + message + "\r\n" + extractTrace(throwable);
        LoggerX.printStd(Color.LIGHT_GREEN + result + Color.RESET);
        LoggerX.writeLog(result);
    }


    @Override
    public void info0(String message) {
        String result = "[" + LoggerX.datetime() + "][INFO][" + name + "]" + message;
        LoggerX.printStd(result);
        LoggerX.writeLog(result);
    }


    @Override
    public void info0(String message, Throwable throwable) {
        String result = "[" + LoggerX.datetime() + "][INFO][" + name + "]" + message + "\r\n" + extractTrace(throwable);
        LoggerX.printStd(result);
        LoggerX.writeLog(result);
    }


    @Override
    public void debug0(String message) {
        String result = "[" + LoggerX.datetime() + "][DEBUG][" + name + "]" + message;
        LoggerX.printStd(Color.GRAY + result + Color.RESET);
        LoggerX.writeLog(result);
    }


    @Override
    public void debug0(String message, Throwable throwable) {
        String result = "[" + LoggerX.datetime() + "][DEBUG][" + name + "]" + message + "\r\n" + extractTrace(throwable);
        LoggerX.printStd(Color.GRAY + result + Color.RESET);
        LoggerX.writeLog(result);
    }


    @Override
    public void verbose0(String message) {
        String result = "[" + LoggerX.datetime() + "][VERBOSE][" + name + "]" + message;
        LoggerX.printStd(Color.GRAY + result + Color.RESET);
        LoggerX.writeLog(result);
    }


    @Override
    public void verbose0(String message, Throwable throwable) {
        String result = "[" + LoggerX.datetime() + "][VERBOSE][" + name + "]" + message + "\r\n" + extractTrace(throwable);
        LoggerX.printStd(Color.GRAY + result + Color.RESET);
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
        builder.append(LoggerX.datetime());
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

        } else if (event instanceof TempMessageEvent) {

            builder.append("TempMessageEvent");

            Member sender = ((TempMessageEvent) event).getSender();
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


        LoggerX.printStd(Color.LIGHT_PURPLE + "信息已转储 -> " + TIME_CODE + Color.RESET);
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


    private static synchronized void printStd(String message) {
        System.out.println(message);
    }


    private static synchronized void printErr(String message) {
        System.err.println(message);
    }


    private static synchronized void writeLog(String message) {
        try (FileWriter writer = new FileWriter(FILE_LOGGER, StandardCharsets.UTF_8, true)) {
            writer.append(message);
            writer.append("\r\n");
            writer.flush();
        } catch (IOException ignore) {
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


    // ==================================================================================================
    //
    //
    // ==================================================================================================


    public static String date() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }


    public static String date(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }


    public static String date(long timestamp) {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date(timestamp));
    }


    public static String time() {
        return new SimpleDateFormat("HH:mm:ss").format(new Date());
    }


    public static String time(Date date) {
        return new SimpleDateFormat("HH:mm:ss").format(date);
    }


    public static String time(long timestamp) {
        return new SimpleDateFormat("HH:mm:ss").format(new Date(timestamp));
    }


    public static String datetime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }


    public static String datetime(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }


    public static String datetime(long timestamp) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(timestamp));
    }


    // ================================================================


    public static String formatTime(String format) {
        return new SimpleDateFormat(format).format(new Date());
    }


    public static String formatTime(String format, Date date) {
        return new SimpleDateFormat(format).format(date);
    }


    public static String formatTime(String format, long timestamp) {
        return new SimpleDateFormat(format).format(new Date(timestamp));
    }


    public static String formatTime(String format, TimeZone timezone) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        formatter.setTimeZone(timezone);
        return formatter.format(new Date());
    }


    public static String formatTime(String format, TimeZone timezone, Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        formatter.setTimeZone(timezone);
        return formatter.format(date);
    }


    public static String formatTime(String format, TimeZone timezone, long timestamp) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        formatter.setTimeZone(timezone);
        return formatter.format(new Date(timestamp));
    }


    public static String duration(long time) {
        long ss = time;
        long dd = ss / 86400;
        ss = ss % 86400;
        long hh = ss / 3600;
        ss = ss % 3600;
        long mm = ss / 60;
        ss = ss % 60;
        return dd + " - " + String.format("%02d", hh) + ":" + String.format("%02d", mm) + ":" + String.format("%02d", ss);
    }


    public static String durationMille(long time) {
        long ms = time;
        long dd = ms / 86400000;
        ms = ms % 86400000;
        long hh = ms / 3600000;
        ms = ms % 3600000;
        long mm = ms / 60000;
        ms = ms % 60000;
        long ss = ms / 1000;
        ms = ms % 1000;
        return dd + " - " + hh + ":" + mm + ":" + ss + ":" + String.format("%04d", ms);
    }


}
