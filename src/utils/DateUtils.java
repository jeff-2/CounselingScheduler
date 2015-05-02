package utils;

import java.util.Calendar;
import java.util.Date;

public class DateUtils {
    public static int getDay(Date date) {
	Calendar cal = Calendar.getInstance();
	cal.setTimeInMillis(date.getTime());
	return cal.get(Calendar.DAY_OF_WEEK);
    }

    public static int getDate(Date date) {
	Calendar cal = Calendar.getInstance();
	cal.setTime(date);
	return cal.get(Calendar.DAY_OF_MONTH);
    }

}
