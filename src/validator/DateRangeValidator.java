package validator;

import gui.InvalidDateRangeException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The Class DateRangeValidator.
 * 
 * @author jmfoste2 lim92
 * 
 */
public class DateRangeValidator {
	
	/**
	 * Parses the date. Ensures it is in the proper date format MM/dd/yyyy with a year 
	 * between 1800 and 10000. 
	 *
	 * @param date the date
	 * @return the date
	 * @throws ParseException the parse exception
	 */
	private static Date parseDate(String date) throws ParseException {
		Date d;
		try {
			SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
			format.setLenient(false);
			d = format.parse(date);
		} catch (ParseException e) {
			throw new ParseException("The date " + date + " must be a valid date of the form mm/dd/yyyy. For example March 3, 1994 should be entered as 3/3/1994.", 0);
		}
		if (d.before(new SimpleDateFormat("MM/dd/yyyy").parse("01/01/1800"))) {
			throw new ParseException("The year must be a year of the form yyyy, where yyyy is at least 1800", 0);
		}
		if (d.after(new SimpleDateFormat("MM/dd/yyyy").parse("12/31/9999"))) {
			throw new ParseException("The year must be a year of the form yyyy, where yyyy is less than 10000", 0);
		}
		
		return d;
	}

	/**
	 * Check for valid date range.
	 *
	 * @param startDate the start date
	 * @param endDate the end date
	 * @throws InvalidDateRangeException the invalid date range exception
	 */
	public static void validate(String startDate, String endDate) throws InvalidDateRangeException {
		try {
			Date lower = parseDate(startDate);
			Date upper = parseDate(endDate);
			if (lower.after(upper))
				throw new InvalidDateRangeException("The start date must be before the end date.");
		} catch (ParseException e) {
			throw new InvalidDateRangeException(e.getMessage());
		}
	}
}
