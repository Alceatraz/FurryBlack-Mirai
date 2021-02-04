package studio.blacktech.furryblackplus.core.utilties;


import java.util.Calendar;


public class DateTool {


    public static long durationDay() {
        return 86400000L;
    }


    public static long getNextDate() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DATE) + 1;
        calendar.set(Calendar.DATE, day);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime().getTime() - System.currentTimeMillis();
    }


}
