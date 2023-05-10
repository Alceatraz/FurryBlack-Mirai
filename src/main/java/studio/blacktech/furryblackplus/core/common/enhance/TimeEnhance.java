package studio.blacktech.furryblackplus.core.common.enhance;

import studio.blacktech.furryblackplus.FurryBlack;
import studio.blacktech.furryblackplus.core.common.annotation.Comment;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Comment("时间工具")
public class TimeEnhance {

  //= ==========================================================================

  private static final DateTimeFormatter DATETIME = pattern("yyyy-MM-dd HH:mm:ss");
  private static final DateTimeFormatter DATE = pattern("yyyy-MM-dd");
  private static final DateTimeFormatter TIME = pattern("HH:mm:ss");

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
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime next = LocalDateTime.of(
      year == null ? now.getYear() : year == 0 ? 0 : now.getYear() + year,
      month == null ? now.getMonthValue() : month == 0 ? 0 : now.getMonthValue() + month,
      date == null ? now.getDayOfMonth() : date == 0 ? 0 : now.getDayOfMonth() + date,
      hour == null ? now.getHour() : hour == 0 ? 0 : now.getHour() + hour,
      second == null ? now.getSecond() : second == 0 ? 0 : now.getSecond() + second,
      nano == null ? now.getNano() : nano == 0 ? 0 : now.getNano() + nano
    );
    return next.toEpochSecond(FurryBlack.SYSTEM_OFFSET) * 1000;
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
