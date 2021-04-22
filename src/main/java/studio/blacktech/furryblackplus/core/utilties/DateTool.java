package studio.blacktech.furryblackplus.core.utilties;


import studio.blacktech.furryblackplus.core.annotation.Api;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


@Api("日期工具类")
public final class DateTool {


    private DateTool() { }


    @Api("格式化日期")
    public static String date() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }

    @Api("格式化日期")
    public static String date(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    @Api("格式化日期")
    public static String date(long timestamp) {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date(timestamp));
    }

    @Api("格式化时间")
    public static String time() {
        return new SimpleDateFormat("HH:mm:ss").format(new Date());
    }

    @Api("格式化时间")
    public static String time(Date date) {
        return new SimpleDateFormat("HH:mm:ss").format(date);
    }

    @Api("格式化时间")
    public static String time(long timestamp) {
        return new SimpleDateFormat("HH:mm:ss").format(new Date(timestamp));
    }

    @Api("格式化日期时间")
    public static String datetime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    @Api("格式化日期时间")
    public static String datetime(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }

    @Api("格式化日期时间")
    public static String datetime(long timestamp) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(timestamp));
    }

    // ================================================================

    @Api("按照指定格式格式化")
    public static String formatTime(String format) {
        return new SimpleDateFormat(format).format(new Date());
    }

    @Api("按照指定格式格式化")
    public static String formatTime(String format, Date date) {
        return new SimpleDateFormat(format).format(date);
    }

    @Api("按照指定格式格式化")
    public static String formatTime(String format, long timestamp) {
        return new SimpleDateFormat(format).format(new Date(timestamp));
    }

    @Api("按照指定格式格式化")
    public static String formatTime(String format, TimeZone timezone) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        formatter.setTimeZone(timezone);
        return formatter.format(new Date());
    }

    @Api("按照指定格式格式化")
    public static String formatTime(String format, TimeZone timezone, Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        formatter.setTimeZone(timezone);
        return formatter.format(date);
    }

    @Api("按照指定格式格式化")
    public static String formatTime(String format, TimeZone timezone, long timestamp) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        formatter.setTimeZone(timezone);
        return formatter.format(new Date(timestamp));
    }

    // ================================================================

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

    // ================================================================

    @Api("获取明天")
    public static Date getNextDate() {
        TimeZone timeZone = TimeZone.getDefault();
        return getNextDate(timeZone);
    }

    @Api("获取明天")
    public static Date getNextDate(TimeZone timeZone) {
        Calendar calendar = Calendar.getInstance(timeZone);
        int day = calendar.get(Calendar.DATE) + 1;
        calendar.set(Calendar.DATE, day);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.AM_PM, Calendar.AM);
        return calendar.getTime();
    }

}
