/*
 * Copyright (C) 2021 Alceatraz @ BlackTechStudio
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the BTS Anti-Commercial & GNU Affero General
 * Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * BTS Anti-Commercial & GNU Affero General Public License for more details.
 *
 * You should have received a copy of the BTS Anti-Commercial & GNU Affero
 * General Public License along with this program.
 *
 */


package studio.blacktech.furryblackplus.core.common.time;

import studio.blacktech.furryblackplus.FurryBlack;
import studio.blacktech.furryblackplus.common.Api;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;


@Api("时间工具")
public class TimeTool {


    private static final DateTimeFormatter DATETIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault());
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm:ss").withZone(ZoneId.systemDefault());


    private TimeTool() {}


    // ==================================================================================================


    @Api("格式化日期")
    public static String date() {
        return DATE.format(Instant.now());
    }

    @Api("格式化日期")
    public static String date(long timeStamp) {
        return DATE.format(Instant.ofEpochMilli(timeStamp));
    }

    @Api("格式化日期")
    public static String date(Instant instant) {
        return DATE.format(instant);
    }

    @Api("格式化日期")
    public static String date(LocalDateTime localDateTime) {
        return DATE.format(localDateTime);
    }


    @Api("格式化时间")
    public static String time() {
        return TIME.format(Instant.now());
    }

    @Api("格式化时间")
    public static String time(long timeStamp) {
        return TIME.format(Instant.ofEpochMilli(timeStamp));
    }

    @Api("格式化时间")
    public static String time(Instant instant) {
        return TIME.format(instant);
    }

    @Api("格式化时间")
    public static String time(LocalDateTime localDateTime) {
        return TIME.format(localDateTime);
    }


    @Api("格式化日期时间")
    public static String datetime() {
        return DATETIME.format(Instant.now());
    }

    @Api("格式化日期时间")
    public static String datetime(long timeStamp) {
        return DATETIME.format(Instant.ofEpochMilli(timeStamp));
    }

    @Api("格式化日期时间")
    public static String datetime(Instant instant) {
        return DATETIME.format(instant);
    }

    @Api("格式化日期时间")
    public static String datetime(LocalDateTime localDateTime) {
        return DATETIME.format(localDateTime);
    }


    @Api("自定义格式化")
    public static String format(String pattern) {
        return DateTimeFormatter.ofPattern(pattern).withZone(ZoneId.systemDefault()).format(Instant.now());
    }

    @Api("自定义格式化")
    public static String format(String pattern, ZoneId zone) {
        return DateTimeFormatter.ofPattern(pattern).withZone(zone).format(Instant.now());
    }

    @Api("自定义格式化")
    public static String format(String pattern, Instant instant) {
        return DateTimeFormatter.ofPattern(pattern).withZone(ZoneId.systemDefault()).format(instant);
    }

    @Api("自定义格式化")
    public static String format(String pattern, ZoneId zone, Instant instant) {
        return DateTimeFormatter.ofPattern(pattern).withZone(zone).format(instant);
    }

    @Api("自定义格式化")
    public static String format(String pattern, long timeStamp) {
        return DateTimeFormatter.ofPattern(pattern).withZone(ZoneId.systemDefault()).format(Instant.ofEpochMilli(timeStamp));
    }

    @Api("自定义格式化")
    public static String format(String pattern, ZoneId zone, long timeStamp) {
        return DateTimeFormatter.ofPattern(pattern).withZone(zone).format(Instant.ofEpochMilli(timeStamp));
    }


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
        int targetDay = LocalDate.ofInstant(Instant.ofEpochMilli(timeStamp), FurryBlack.SYSTEM_OFFSET).getDayOfYear();
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
