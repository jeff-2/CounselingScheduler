package validator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import bean.DateRange;

/**
 * The Class DateRangeValidator.
 * 
 * @author jmfoste2, lim92
 */
public class DateRangeValidator {
	private static final SimpleDateFormat dateFormat;
	private static final SimpleDateFormat longDateFormat;

	static {
		dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		dateFormat.setLenient(false);
		longDateFormat = new SimpleDateFormat("EEEEEEEEE, MMMMMMMM d");
	}

	/**
	 * Formats the date as a string in month/day/year format
	 * 
	 * @param date
	 * @return date formatted as string
	 */
	public static String formatDate(Date date) {
		return dateFormat.format(date);
	}

	/**
	 * Formats the date as a string in Day Of Week, Month, Date
	 * 
	 * @param date
	 * @return date formatted as string
	 */
	public static String formatDateLong(Date date) {
		return longDateFormat.format(date);
	}

	/**
	 * Parses the date. Ensures it is in the proper date format MM/dd/yyyy with
	 * a year between 1800 and 10000.
	 *
	 * @param date
	 *            the date
	 * @return the date
	 * @throws ParseException
	 *             the parse exception
	 */
	public static Date parseDate(String date) throws ParseException {
		Date d;
		try {
			d = dateFormat.parse(date);
		} catch (ParseException e) {
			throw new ParseException(
					"The date "
							+ date
							+ " must be a valid date of the form mm/dd/yyyy. For example March 3, 1994 should be entered as 3/3/1994.",
					0);
		}
		if (d.before(new SimpleDateFormat("MM/dd/yyyy").parse("01/01/1800"))) {
			throw new ParseException(
					"The year must be a year of the form yyyy, where yyyy is at least 1800",
					0);
		}
		if (d.after(new SimpleDateFormat("MM/dd/yyyy").parse("12/31/9999"))) {
			throw new ParseException(
					"The year must be a year of the form yyyy, where yyyy is less than 10000",
					0);
		}
		return d;
	}

	/**
	 * Check for valid date range.
	 *
	 * @param startDate
	 *            the start date
	 * @param endDate
	 *            the end date
	 * @throws InvalidDateRangeException
	 *             the invalid date range exception
	 */
	public static DateRange validate(String startDate, String endDate)
			throws InvalidDateRangeException {
		Date lower, upper;
		try {
			lower = parseDate(startDate);
			upper = parseDate(endDate);
			if (lower.after(upper))
				throw new InvalidDateRangeException(
						"The start date must be before the end date.");
		} catch (ParseException e) {
			throw new InvalidDateRangeException(e.getMessage());
		}
		return new DateRange(lower, upper);
	}
}
