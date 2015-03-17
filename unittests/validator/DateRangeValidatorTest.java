package validator;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

/**
 * 
 * @author jmfoste2, lim92
 *
 */
public class DateRangeValidatorTest {

	@Test(expected=ParseException.class)
	public void testParseInvalidYearLow() throws Exception {
		String date = "1/5/1700";
		DateRangeValidator.parseDate(date);
	}
	
	@Test(expected=ParseException.class)
	public void testParseInvalidYearHigh() throws Exception {
		String date = "2/7/10005";
		DateRangeValidator.parseDate(date);
	}
	
	@Test(expected=ParseException.class)
	public void testParseInvalidDate() throws Exception {
		String date = "18/5/2000";
		DateRangeValidator.parseDate(date);
	}
	
	@Test
	public void testParseValidDate() throws Exception {
		String date = "1/5/2000";
		Date d = DateRangeValidator.parseDate(date);
		assertEquals(d, new SimpleDateFormat("MM/dd/yyyy").parse(date));
	}
	
	@Test
	public void testValidateValidDateRange() throws Exception {
		String start = "3/5/2015";
		String end = "3/15/2015";
		DateRangeValidator.validate(start, end);
	}
	
	@Test(expected=InvalidDateRangeException.class)
	public void testValidateInvalidDateRange() throws Exception {
		String start = "1/1/2000";
		String end = "2/4/1985";
		DateRangeValidator.validate(start, end);
	}
}
