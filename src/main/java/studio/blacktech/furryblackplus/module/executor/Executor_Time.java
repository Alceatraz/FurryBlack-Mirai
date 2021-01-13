package studio.blacktech.furryblackplus.module.executor;

import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.GroupTempMessageEvent;
import net.mamoe.mirai.message.data.At;
import studio.blacktech.furryblackplus.system.annotation.ComponentHandlerExecutor;
import studio.blacktech.furryblackplus.system.command.Command;
import studio.blacktech.furryblackplus.system.exception.BotException;
import studio.blacktech.furryblackplus.system.handler.EventHandlerExecutor;
import studio.blacktech.furryblackplus.system.logger.LoggerX;

import java.io.File;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;


@ComponentHandlerExecutor(
        name = "环球时间",
        description = "现在都几点了，为什么那个孩子还在水群",
        privacy = {
                "无"
        },
        command = "time",
        usage = {
                "/time - 查看世界时间"
        }
)
public class Executor_Time extends EventHandlerExecutor {


    public Executor_Time(ExecutorInfo INFO) {
        super(INFO);
    }


    private static final TimeZone zone_00 = TimeZone.getTimeZone("UTC");
    private static final TimeZone zone_CN = TimeZone.getTimeZone("Asia/Shanghai");


    private Integer hour;
    private String cache;
    private long current;

    private Map<String, TimeZone> TIME_ZONE;


    @Override
    public void init() throws BotException {

        initAppFolder();
        initConfFolder();

        TIME_ZONE = new LinkedHashMap<>();

        File FILE_TIMEZONE = initConfFile("timezone.txt");

        for (String line : readFile(FILE_TIMEZONE)) {

            if (!line.contains(":")) {
                logger.warning("配置无效 " + line);
                continue;
            }

            String[] temp = line.split(":");

            if (temp.length != 2) {
                logger.warning("配置无效 " + line);
                continue;
            }

            TimeZone timeZone = TimeZone.getTimeZone(temp[1]);

            if (!temp[1].equals("GMT") && timeZone.getID().equals("GMT")) logger.warning("配置无效 TimeZone将不可识别的区域转换为GMT " + line);

            TIME_ZONE.put(temp[0], timeZone);

            logger.seek("添加时区 " + temp[0] + " -> " + timeZone.getDisplayName());
        }

    }

    @Override
    public void boot() throws BotException {

    }

    @Override
    public void shut() throws BotException {

    }


    @Override
    public void handleTempMessage(GroupTempMessageEvent event, Command command) {
        event.getSender().sendMessage(getTime());
    }


    @Override
    public void handleFriendMessage(FriendMessageEvent event, Command command) {
        event.getSender().sendMessage(getTime());
    }


    @Override
    public void handleGroupMessage(GroupMessageEvent event, Command command) {
        event.getGroup().sendMessage(new At(event.getSender().getId()).plus("\r\n" + getTime()));
    }


    private String getTime() {

        int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

        if (hour == null || hour != currentHour) {
            hour = currentHour;
            current = System.currentTimeMillis();

            StringBuilder builder = new StringBuilder();

            builder.append("世界协调时(UTC) ").append(LoggerX.formatTime("yyyy-MM-dd HH:mm", zone_00)).append("\r\n");

            for (Map.Entry<String, TimeZone> entry : TIME_ZONE.entrySet()) {
                TimeZone value = entry.getValue();
                builder.append(entry.getKey()).append(" ").append(LoggerX.formatTime("HH:mm", value)).append(format(value)).append("\r\n");
            }

            builder.append("亚洲中国(UTC+8) ").append(LoggerX.formatTime("HH:mm", zone_CN));

            cache = builder.toString();
        }

        return cache;

    }


    /**
     * 这个算法非常牛逼 而且我不打算解释
     */
    private String format(TimeZone timezone) {


        boolean isEnableDST = false;
        boolean isDisableDST = false;


        StringBuilder builder = new StringBuilder();

        Calendar today = Calendar.getInstance(timezone);
        Calendar begin = Calendar.getInstance(timezone);

        begin.set(Calendar.MONTH, Calendar.FEBRUARY);
        begin.set(Calendar.DATE, 0);
        begin.set(Calendar.HOUR, 0);
        begin.set(Calendar.MINUTE, 0);
        begin.set(Calendar.SECOND, 0);

        Calendar temp = Calendar.getInstance(timezone);

        temp.setTime(begin.getTime());

        for (long i = temp.getTimeInMillis(); i < current; i = temp.getTimeInMillis()) {
            temp.add(Calendar.DATE, 1);
            long t = temp.getTimeInMillis();
            if (t - i < 86400000) {
                isEnableDST = true;
            } else if (t - i > 86400000) {
                isDisableDST = true;
            }
        }

        if (isEnableDST ^ isDisableDST) builder.append(" 夏令时");

        int TZ_DATE = Integer.parseInt(LoggerX.formatTime("dd", timezone));
        int E8_DATE = Integer.parseInt(LoggerX.formatTime("dd", zone_CN));

        if (E8_DATE - TZ_DATE > 0) {
            builder.append(" 昨天,").append(TZ_DATE).append("日");
        } else if (E8_DATE - TZ_DATE < 0) {
            builder.append(" 明天,").append(TZ_DATE).append("日");
        }

        return builder.toString();

    }


}
