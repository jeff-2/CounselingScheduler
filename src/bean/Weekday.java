package bean;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 * @author jmfoste2, lim92
 *
 */
public enum Weekday {
	Monday, Tuesday, Wednesday, Thursday, Friday;

	private static final SimpleDateFormat format = new SimpleDateFormat("EEEEEEEEE");
	
	public static Weekday getWeekday(Date d) {
		if(!Weekday.isWeekday(d)) {
			throw new IllegalArgumentException("Weekdays are not Saturday/Sunday");
		}
		return Weekday.valueOf(format.format(d));
	}

	public static boolean isWeekday(Date d) {
		String day =  format.format(d).trim();
		return !(day.equals("Saturday") || day.equals("Sunday"));
	}
}
