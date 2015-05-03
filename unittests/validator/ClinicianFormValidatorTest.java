package validator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import generator.TestDataGenerator;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import action.InvalidFormDataException;
import bean.CalendarBean;
import bean.ClinicianBean;
import bean.ClinicianPreferencesBean;
import bean.CommitmentBean;
import bean.DateRange;
import bean.Semester;
import bean.TimeAwayBean;
import dao.CalendarDAO;
import dao.ClinicianDAO;
import dao.ConnectionFactory;

/**
 * The Class ClinicianFormValidatorTest tests the ClinicianFormValidator functionality.
 */
public class ClinicianFormValidatorTest {

    /** The gen. */
    private TestDataGenerator gen;
    
    /** The calendar. */
    private CalendarBean calendar;

    /**
     * Sets the test up.
     *
     * @throws Exception the exception
     */
    @Before
    public void setUp() throws Exception {
	Connection conn = ConnectionFactory.getInstance();
	gen = new TestDataGenerator(conn);
	gen.clearTables();
	ClinicianDAO clinicianDAO = new ClinicianDAO(conn);
	clinicianDAO.insert(new ClinicianBean(0, "Bill"));
	CalendarDAO calendarDAO = new CalendarDAO(conn);
	calendar = new CalendarBean();
	calendar.setId(0);
	calendar.setSemester(Semester.Fall);
	calendar.setStartDate(DateRangeValidator.parseDate("3/23/2015"));
	calendar.setEndDate(DateRangeValidator.parseDate("5/10/2015"));
	calendar.setIaMinHours(35);
	calendar.setEcMinHours(44);
	calendarDAO.insertCalendar(calendar);
    }

    /**
     * Clean up.
     *
     * @throws Exception the exception
     */
    @After
    public void cleanUp() throws Exception {
	gen.clearTables();
    }

    /**
     * Test validation of a valid time away.
     *
     * @throws ParseException the parse exception
     * @throws InvalidFormDataException the invalid form data exception
     */
    @Test
    public void testValidateValidTimeAway() throws ParseException,
	    InvalidFormDataException {
	TimeAwayBean actual = ClinicianFormValidator.validateTimeAway("Bill",
		"1/25/2015", "1/27/2015");
	TimeAwayBean expected = new TimeAwayBean(-1, "Bill",
		DateRangeValidator.parseDate("1/25/2015"),
		DateRangeValidator.parseDate("1/27/2015"));
	assertEquals(expected, actual);
    }

    /**
     * Test validation of time away with empty name.
     */
    @Test
    public void testValidateInvalidTimeAwayEmptyName() {
	try {
	    ClinicianFormValidator.validateTimeAway("", "1/25/2015",
		    "1/27/2015");
	    fail();
	} catch (InvalidFormDataException e) {
	    assertEquals("You must enter in a description", e.getMessage());
	    assertEquals("Adding invalid time away description", e.getContext());
	}
    }

    /**
     * Test validation of time away with an invalid date.
     */
    @Test
    public void testValidateInvalidTimeAwayInvalidDate() {
	try {
	    ClinicianFormValidator.validateTimeAway("Bill", "15/25/2015",
		    "1/27/2015");
	    fail();
	} catch (InvalidFormDataException e) {
	    assertEquals(
		    "Cannot add the time away to the list. "
			    + "The date 15/25/2015 must be a valid date of the form mm/dd/yyyy. For example March 3, 1994 should be entered as 3/3/1994.",
		    e.getMessage());
	    assertEquals("Adding invalid time away", e.getContext());
	}
    }

    /**
     * Test validation of valid preferences as a non admin.
     *
     * @throws InvalidFormDataException the invalid form data exception
     * @throws SQLException the SQL exception
     */
    @Test
    public void testValidateValidPreferencesNonAdmin()
	    throws InvalidFormDataException, SQLException {
	ClinicianPreferencesBean expected = new ClinicianPreferencesBean(0, 3,
		1, 2, 35, 44);
	ClinicianPreferencesBean actual = ClinicianFormValidator
		.validatePreferences("Bill", 3, 1, 2, null, null, false);
	assertEquals(expected, actual);
    }

    /**
     * Test validation on preferences as an admin.
     *
     * @throws InvalidFormDataException the invalid form data exception
     * @throws SQLException the SQL exception
     */
    @Test
    public void testValidateValidPreferencesAdmin()
	    throws InvalidFormDataException, SQLException {
	ClinicianPreferencesBean expected = new ClinicianPreferencesBean(0, 3,
		1, 2, 15, 7);
	ClinicianPreferencesBean actual = ClinicianFormValidator
		.validatePreferences("Bill", 3, 1, 2, "15", "7", true);
	assertEquals(expected, actual);
    }

    /**
     * Test validation on preferences with an invalid name.
     *
     * @throws SQLException the SQL exception
     */
    @Test
    public void testValidateInvalidPreferencesInvalidName() throws SQLException {
	try {
	    ClinicianFormValidator.validatePreferences("Charles", 3, 1, 2,
		    null, null, false);
	    fail();
	} catch (InvalidFormDataException e) {
	    assertEquals("You must enter in a valid clinician name",
		    e.getMessage());
	    assertEquals("Adding invalid clinician name", e.getContext());
	}
    }

    /**
     * Test validation on a preferences with invalid ranks.
     *
     * @throws SQLException the SQL exception
     */
    @Test
    public void testValidateInvalidPreferencesInvalidRanks()
	    throws SQLException {
	try {
	    ClinicianFormValidator.validatePreferences("Bill", 3, 1, 1, null,
		    null, false);
	    fail();
	} catch (InvalidFormDataException e) {
	    assertEquals(
		    "You must enter unique ranks for each time preference",
		    e.getMessage());
	    assertEquals("Adding clinician ec preferences", e.getContext());
	}
    }

    /**
     * Test validation on a valid biweekly commitment that is non external.
     *
     * @throws InvalidFormDataException the invalid form data exception
     * @throws ParseException the parse exception
     */
    @Test
    public void testValidateValidCommitmentBiweeklyNonExternal()
	    throws InvalidFormDataException, ParseException {
	List<CommitmentBean> expected = new ArrayList<CommitmentBean>();
	expected.add(new CommitmentBean(-1, 9, 10, DateRangeValidator
		.parseDate("3/23/2015"), "meeting"));
	expected.add(new CommitmentBean(-1, 9, 10, DateRangeValidator
		.parseDate("4/6/2015"), "meeting"));
	expected.add(new CommitmentBean(-1, 9, 10, DateRangeValidator
		.parseDate("4/20/2015"), "meeting"));
	expected.add(new CommitmentBean(-1, 9, 10, DateRangeValidator
		.parseDate("5/4/2015"), "meeting"));
	List<CommitmentBean> actual = ClinicianFormValidator
		.validateCommitment(new DateRange(calendar.getStartDate(),
			calendar.getEndDate()), "meeting", "9:30am", "10:00am",
			"Monday", "Biweekly", false);
	assertEquals(expected, actual);
    }

    /**
     * Test validation on a valid weekly commitment that is external.
     *
     * @throws InvalidFormDataException the invalid form data exception
     * @throws ParseException the parse exception
     */
    @Test
    public void testValidateValidCommitmentWeeklyExternal()
	    throws InvalidFormDataException, ParseException {
	List<CommitmentBean> expected = new ArrayList<CommitmentBean>();
	expected.add(new CommitmentBean(-1, 9, 11, DateRangeValidator
		.parseDate("3/23/2015"), "meeting"));
	expected.add(new CommitmentBean(-1, 9, 11, DateRangeValidator
		.parseDate("3/30/2015"), "meeting"));
	expected.add(new CommitmentBean(-1, 9, 11, DateRangeValidator
		.parseDate("4/6/2015"), "meeting"));
	expected.add(new CommitmentBean(-1, 9, 11, DateRangeValidator
		.parseDate("4/13/2015"), "meeting"));
	expected.add(new CommitmentBean(-1, 9, 11, DateRangeValidator
		.parseDate("4/20/2015"), "meeting"));
	expected.add(new CommitmentBean(-1, 9, 11, DateRangeValidator
		.parseDate("4/27/2015"), "meeting"));
	expected.add(new CommitmentBean(-1, 9, 11, DateRangeValidator
		.parseDate("5/4/2015"), "meeting"));
	List<CommitmentBean> actual = ClinicianFormValidator
		.validateCommitment(new DateRange(calendar.getStartDate(),
			calendar.getEndDate()), "meeting", "10:00am",
			"10:30am", "Monday", "Weekly", true);
	assertEquals(expected, actual);
    }

    /**
     * Test validation on a valid monthly commitment which is not external.
     *
     * @throws InvalidFormDataException the invalid form data exception
     * @throws ParseException the parse exception
     */
    @Test
    public void testValidateValidCommitmentMonthlyNonExternal()
	    throws InvalidFormDataException, ParseException {
	List<CommitmentBean> expected = new ArrayList<CommitmentBean>();
	expected.add(new CommitmentBean(-1, 10, 11, DateRangeValidator
		.parseDate("4/6/2015"), "meeting"));
	expected.add(new CommitmentBean(-1, 10, 11, DateRangeValidator
		.parseDate("5/4/2015"), "meeting"));
	List<CommitmentBean> actual = ClinicianFormValidator
		.validateCommitment(new DateRange(calendar.getStartDate(),
			calendar.getEndDate()), "meeting", "10:00am",
			"10:30am", "Monday", "Monthly", false);
	assertEquals(expected, actual);
    }

    /**
     * Test validation with invalid commitment times.
     */
    @Test
    public void testValidateInvalidCommitmentInvalidTimes() {
	try {
	    ClinicianFormValidator.validateCommitment(
		    new DateRange(calendar.getStartDate(), calendar
			    .getEndDate()), "meeting", "10:30am", "10:00am",
		    "Monday", "Biweekly", false);
	    fail();
	} catch (InvalidFormDataException e) {
	    assertEquals(
		    "The start time must be before the end time of the commitment",
		    e.getMessage());
	    assertEquals("Adding invalid time range for commitment",
		    e.getContext());
	}
    }

    /**
     * Test validation with a commitment with an invalid description.
     */
    @Test
    public void testValidateInvalidCommitmentInvalidDescription() {
	try {
	    ClinicianFormValidator.validateCommitment(
		    new DateRange(calendar.getStartDate(), calendar
			    .getEndDate()), "", "9:00am", "10:00am", "Monday",
		    "Biweekly", false);
	    fail();
	} catch (InvalidFormDataException e) {
	    assertEquals("You must enter in a description", e.getMessage());
	    assertEquals("Adding invalid commitment description",
		    e.getContext());
	}
    }
}
