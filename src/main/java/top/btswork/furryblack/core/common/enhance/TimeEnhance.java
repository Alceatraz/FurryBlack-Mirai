package top.btswork.furryblack.core.common.enhance;

import top.btswork.furryblack.core.common.annotation.Comment;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@SuppressWarnings("unused")

@Comment("时间工具")
public class TimeEnhance {

  public static final ZoneId SYSTEM_ZONEID;
  public static final ZoneOffset SYSTEM_OFFSET;

  public static final long DURATION_HOUR;
  public static final long DURATION_DAY;
  public static final long DURATION_WEEK;

  private static final DateTimeFormatter DATETIME;
  private static final DateTimeFormatter DATE;
  private static final DateTimeFormatter TIME;

  static {

    DURATION_HOUR = 1000 * 3600;
    DURATION_DAY = 1000 * 3600 * 24;
    DURATION_WEEK = 1000 * 3600 * 24 * 7;

    DATETIME = pattern("yyyy-MM-dd HH:mm:ss");
    TIME = pattern("HH:mm:ss");
    DATE = pattern("yyyy-MM-dd");

    SYSTEM_ZONEID = ZoneId.systemDefault();
    SYSTEM_OFFSET = ZoneOffset.systemDefault().getRules().getOffset(LocalDateTime.now());
  }

  //= ==========================================================================

  public static DateTimeFormatter pattern(String pattern) {
    return DateTimeFormatter.ofPattern(pattern).withZone(ZoneId.systemDefault());
  }

  public static DateTimeFormatter pattern(String pattern, ZoneId zone) {
    return DateTimeFormatter.ofPattern(pattern).withZone(zone);
  }

  //= ==========================================================================

  private static Instant to(long epoch) {
    return Instant.ofEpochMilli(epoch);
  }

  //= ==========================================================================

  public static String datetime() {
    return DATETIME.format(Instant.now());
  }

  public static String datetime(long epoch) {
    return DATETIME.format(Instant.ofEpochMilli(epoch));
  }

  public static String date() {
    return DATE.format(Instant.now());
  }

  public static String date(long epoch) {
    return DATE.format(Instant.ofEpochMilli(epoch));
  }

  public static String time() {
    return TIME.format(Instant.now());
  }

  public static String time(long epoch) {
    return TIME.format(Instant.ofEpochMilli(epoch));
  }

  //= ==========================================================================

  public static long toNextSecond() {
    return plus(null, null, null, null, null, 1, 0);
  }

  public static long toNextMinute() {
    return plus(null, null, null, null, 1, 0, 0);
  }

  public static long toNextHour() {
    return plus(null, null, null, 1, 0, 0, 0);
  }

  public static long toNextDay() {
    return plus(null, null, 1, 0, 0, 0, 0);
  }

  public static long toNextMonth() {
    return plus(null, 1, 0, 0, 0, 0, 0);
  }

  public static long toNextYear() {
    return plus(1, 0, 0, 0, 0, 0, 0);
  }

  public static long plus(Integer year, Integer month, Integer date, Integer hour, Integer minute, Integer second, Integer nano) {
    LocalDateTime value = LocalDateTime.now();
    if (nano != null) value = nano == 0 ? value.withNano(0) : value.plusNanos(nano);
    if (second != null) value = second == 0 ? value.withSecond(0) : value.plusSeconds(second);
    if (minute != null) value = minute == 0 ? value.withMinute(0) : value.plusMinutes(minute);
    if (hour != null) value = hour == 0 ? value.withHour(0) : value.plusHours(hour);
    if (date != null) value = date == 0 ? value.withDayOfMonth(1) : value.plusDays(date);
    if (month != null) value = month == 0 ? value.withMonth(1) : value.plusMonths(month);
    if (year != null) value = year == 0 ? value.withYear(0) : value.plusYears(year);
    return value.toEpochSecond(TimeEnhance.SYSTEM_OFFSET) * 1000;
  }

  //= ==========================================================================

  public static void safeDelay(long time) {
    try {
      Thread.sleep(time);
    } catch (InterruptedException exception) {
      throw new RuntimeException(exception);
    }
  }

  //= ==========================================================================

  public static String duration(long duration) {

    var ms = duration;

    long dd = ms / 86400000;
    ms %= 86400000;
    long hh = ms / 3600000;
    ms %= 3600000;
    long mm = ms / 60000;
    ms %= 60000;
    long ss = ms / 1000;
    ms %= 1000;

    StringBuilder builder = new StringBuilder();

    if (dd > 0) {
      builder.append(dd);
      builder.append("-");
      builder.append(String.format("%02d", hh));
      builder.append(":");
      builder.append(String.format("%02d", mm));
      builder.append(":");
      builder.append(String.format("%02d", ss));
      builder.append(".");
      builder.append(String.format("%03d", ms));
    } else if (hh > 0) {
      builder.append(hh);
      builder.append(":");
      builder.append(String.format("%02d", mm));
      builder.append(":");
      builder.append(String.format("%02d", ss));
      builder.append(".");
      builder.append(String.format("%03d", ms));
    } else if (mm > 0) {
      builder.append("00:");
      builder.append(String.format("%02d", mm));
      builder.append(":");
      builder.append(String.format("%02d", ss));
      builder.append(".");
      builder.append(String.format("%03d", ms));
    } else if (ss > 0) {
      builder.append("00:00:");
      builder.append(String.format("%02d", ss));
      builder.append(".");
      builder.append(String.format("%03d", ms));
    } else if (ms > 0) {
      builder.append("0.");
      builder.append(String.format("%03d", ms));
    } else {
      builder.append("0.000");
    }

    return builder.toString();

  }

}
