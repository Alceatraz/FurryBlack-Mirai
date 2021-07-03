package studio.blacktech.furryblackplus.core.utilties;

import studio.blacktech.furryblackplus.Driver;
import studio.blacktech.furryblackplus.core.annotation.Api;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;


@Api("时间工具")
public class TimeTool {


    private TimeTool() { }


    // ==================================================================================================


    /**
     * 抽取后的逻辑不易读
     */
    @Api("格式化时间间隔")
    public static String duration(long time) {

        long ms = time;
        long dd = ms / 86400000;
        ms = ms % 86400000;
        long hh = ms / 3600000;
        ms = ms % 3600000;
        long mm = ms / 60000;
        ms = ms % 60000;
        long ss = ms / 1000;
        ms = ms % 1000;
        StringBuilder builder = new StringBuilder();

        boolean contains = false;

        if (dd > 0) {
            contains = true;
            builder.append(dd);
            builder.append(" - ");
        }

        if (hh > 0) {
            contains = true;
            if (builder.length() == 0) {
                builder.append(hh);
            } else {
                builder.append(String.format("%02d", hh));
            }
            builder.append(":");
        } else if (contains) {
            builder.append("00:");
        }

        if (mm > 0) {
            contains = true;
            if (builder.length() == 0) {
                builder.append(mm);
            } else {
                builder.append(String.format("%02d", mm));
            }
            builder.append(":");
        } else if (contains) {
            builder.append("00:");
        }

        if (ss > 0) {
            contains = true;
            if (builder.length() == 0) {
                builder.append(ss);
            } else {
                builder.append(String.format("%02d", ss));
            }
        } else if (contains) {
            builder.append("00");
        }

        if (ms > 0) {
            contains = true;
            builder.append(".");
            if (builder.length() == 0) {
                builder.append(ms);
            } else {
                builder.append(String.format("%03d", ms));
            }
        }

        if (contains) {
            return builder.toString();
        } else {
            return "0.000";
        }
    }


    // ==================================================================================================


    @Api(value = "以系统时间偏移计算是否是今天", attention = "建议只用于lastModify判断")
    public static boolean isToday(long timeStamp) {
        int currentDay = LocalDate.now().getDayOfYear();
        int targetDay = LocalDate.ofInstant(Instant.ofEpochMilli(timeStamp), Driver.SYSTEM_OFFSET).getDayOfYear();
        return currentDay == targetDay;
    }


    @Api("获取休眠到明天的毫秒数")
    public static long nextDayDuration() {
        Instant currentTime = LocalDateTime.now().toInstant(ZoneOffset.UTC);
        Instant targetTime = LocalDate.now().plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);
        return targetTime.toEpochMilli() - currentTime.toEpochMilli();
    }


    // ==================================================================================================


}
