package studio.blacktech.furryblackplus;

import org.junit.jupiter.api.Test;
import studio.blacktech.furryblackplus.system.common.logger.LoggerX;

import java.util.Calendar;
import java.util.TimeZone;

public class TimeTest {


    private static final TimeZone zone_US = TimeZone.getTimeZone("America/Los_Angeles");
    private static final TimeZone zone_00 = TimeZone.getTimeZone("UTC");
    private static final TimeZone zone_UK = TimeZone.getTimeZone("Europe/London");
    private static final TimeZone zone_SE = TimeZone.getTimeZone("Europe/Stockholm");
    private static final TimeZone zone_FR = TimeZone.getTimeZone("Europe/Paris");
    private static final TimeZone zone_CN = TimeZone.getTimeZone("Asia/Shanghai");


    private Integer hour;
    private String cache;
    private long current;


    @Test
    void test() {
        System.out.println(getTime());
    }


    private String getTime() {

        int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

        if (hour == null || hour != currentHour) {
            hour = currentHour;
            current = System.currentTimeMillis();
            cache =

                    // @formatter:off

                    "世界协调时(UTC) " + LoggerX.formatTime("yyyy-MM-dd HH:mm", zone_00) + "\r\n" +
                            "美国西部(UTC-8) " + LoggerX.formatTime("HH:mm", zone_US) + format(zone_US) + "\r\n" +
                            "欧洲英国(UTC+0) " + LoggerX.formatTime("HH:mm", zone_UK) + format(zone_UK) + "\r\n" +
                            "欧洲瑞典(UTC+1) " + LoggerX.formatTime("HH:mm", zone_SE) + format(zone_SE) + "\r\n" +
                            "欧洲法国(UTC+1) " + LoggerX.formatTime("HH:mm", zone_FR) + format(zone_FR) + "\r\n" +
                            "亚洲中国(UTC+8) " + LoggerX.formatTime("HH:mm", zone_CN);

            // @formatter:off

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

        begin.set(Calendar.MONTH,Calendar.FEBRUARY);
        begin.set(Calendar.DATE,0);
        begin.set(Calendar.HOUR,0);
        begin.set(Calendar.MINUTE,0);
        begin.set(Calendar.SECOND,0);

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
