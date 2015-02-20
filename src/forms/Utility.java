package forms;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Contains commonly used static methods
 * @author Yusheng Hou and Kevin Lim
 *
 */
public final class Utility {
	private static SimpleDateFormat dateFormat;
	
	static {
		dateFormat = new SimpleDateFormat("EEEE, MMMM d, Y");
	}
	
	/**
	 * Formats the date as a string in "Dayofweek, month date, year" format
	 * @param date
	 * @return date formatted as string
	 */
	public static String formatDate(Date date) {
		return dateFormat.format(date);
	}
}
