package utils;

import java.util.Calendar;
import java.util.Date;

/**
 * The Class DateUtils provides utilities for working which dates.
 */
public class DateUtils {

	/**
	 * Gets the day of the week.
	 *
	 * @param date
	 *            the date
	 * @return the day of the week
	 */
	public static int getDayOfWeek(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(date.getTime());
		return cal.get(Calendar.DAY_OF_WEEK);
	}

	/**
	 * Gets the day of month.
	 *
	 * @param date
	 *            the date
	 * @return the day of the month
	 */
	public static int getDayOfMonth(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.DAY_OF_MONTH);
	}
}
