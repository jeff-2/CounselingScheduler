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
	
	public static String dayName(Date d) {
		return format.format(d);
	}
	
	/**
	 * Returns true if the name of a weekday is contained in the given string
	 * @param value
	 * @return
	 */
	public static boolean isContainedIn(String value) {
		for (Weekday w : Weekday.values()) {
			if (value.contains(w.name())) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns true if the provided value is equal to the name of a weekday
	 * @param value
	 * @return
	 */
	public static boolean contains(String value) {
		for (Weekday w : Weekday.values()) {
			if (w.name().equals(value)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Converts this ordinal to the int constant used with calendar to represent weekdays
	 * @return
	 */
	public int toCalendarWeekday() {
		return ordinal() + 2;
	}
}
