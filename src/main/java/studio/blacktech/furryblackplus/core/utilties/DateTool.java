package studio.blacktech.furryblackplus.core.utilties;


import studio.blacktech.furryblackplus.core.annotation.Api;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


@Api("日期工具类")
public final class DateTool {

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

    public static Date getNextDate() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DATE) + 1;
        calendar.set(Calendar.DATE, day);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }


}
