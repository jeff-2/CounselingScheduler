package validator;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
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
		assertEquals(d, new Date(947052000000l));
	}
}
