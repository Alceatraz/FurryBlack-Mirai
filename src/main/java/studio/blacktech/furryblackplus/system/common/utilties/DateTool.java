package studio.blacktech.furryblackplus.system.common.utilties;

import java.util.Calendar;
import java.util.Date;

public class DateTool {


    public static Date getNextDate() {
        Calendar instance = Calendar.getInstance();
        int day = instance.get(Calendar.DATE) + 1;
        instance.set(Calendar.DATE, day);
        instance.set(Calendar.HOUR, 0);
        instance.set(Calendar.MINUTE, 0);
        instance.set(Calendar.SECOND, 0);
        return instance.getTime();
    }
}
