package generator;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import validator.DateRangeValidator;
import action.GenerateUnfilledScheduleAction;
import bean.CalendarBean;
import bean.ClinicianBean;
import bean.ClinicianPreferencesBean;
import bean.CommitmentBean;
import bean.HolidayBean;
import bean.Semester;
import bean.TimeAwayBean;
import dao.CalendarDAO;
import dao.ClinicianDAO;
import dao.ClinicianPreferencesDAO;
import dao.CommitmentsDAO;
import dao.ConnectionFactory;
import dao.HolidayDAO;
import dao.TimeAwayDAO;

/**
 * The Class TestDataGenerator provides functionality to clear different tables
 * in the database and provide standard test data.
 */
public class TestDataGenerator {

	/** The conn. */
	private Connection conn;

	/**
	 * Instantiates a new test data generator.
	 *
	 * @param con
	 *            the connection to the database
	 */
	public TestDataGenerator(Connection con) {
		this.conn = con;
	}

	/**
	 * The main method clears the tables and fills with standard data.
	 *
	 * @param args
	 *            the arguments
	 * @throws SQLException
	 *             the SQL exception
	 * @throws ParseException
	 *             the parse exception
	 */
	public static void main(String[] args) throws SQLException, ParseException {
		TestDataGenerator gen = new TestDataGenerator(
				ConnectionFactory.getInstance());
		gen.clearTables();
		gen.generateStandardDataset();
	}

	/**
	 * Clear all the tables.
	 *
	 * @throws SQLException
	 *             the SQL exception
	 */
	public void clearTables() throws SQLException {
		clearCalendarTable();
		clearHolidayTable();
		clearCliniciansTable();
		clearClinicianPreferencesTable();
		clearCommitmentsTable();
		clearTimeAwayTable();
		clearSessionsTable();
		clearSessionCliniciansTable();
	}

	/**
	 * Clear calendar table.
	 *
	 * @throws SQLException
	 *             the SQL exception
	 */
	public void clearCalendarTable() throws SQLException {
		Statement stmt = conn.createStatement();
		stmt.execute("DELETE FROM Calendar");
		stmt.close();
	}

	/**
	 * Clear holiday table.
	 *
	 * @throws SQLException
	 *             the SQL exception
	 */
	public void clearHolidayTable() throws SQLException {
		Statement stmt = conn.createStatement();
		stmt.execute("DELETE FROM Holiday");
		stmt.close();
	}

	/**
	 * Clear clinicians table.
	 *
	 * @throws SQLException
	 *             the SQL exception
	 */
	public void clearCliniciansTable() throws SQLException {
		Statement stmt = conn.createStatement();
		stmt.execute("DELETE FROM Clinicians");
		stmt.close();
	}

	/**
	 * Clear clinician preferences table.
	 *
	 * @throws SQLException
	 *             the SQL exception
	 */
	public void clearClinicianPreferencesTable() throws SQLException {
		Statement stmt = conn.createStatement();
		stmt.execute("DELETE FROM ClinicianPreferences");
		stmt.close();
	}

	/**
	 * Clear commitments table.
	 *
	 * @throws SQLException
	 *             the SQL exception
	 */
	public void clearCommitmentsTable() throws SQLException {
		Statement stmt = conn.createStatement();
		stmt.execute("DELETE FROM Commitments");
		stmt.close();
	}

	/**
	 * Clear time away table.
	 *
	 * @throws SQLException
	 *             the SQL exception
	 */
	public void clearTimeAwayTable() throws SQLException {
		Statement stmt = conn.createStatement();
		stmt.execute("DELETE FROM TimeAway");
		stmt.close();
	}

	/**
	 * Clear sessions table.
	 *
	 * @throws SQLException
	 *             the SQL exception
	 */
	public void clearSessionsTable() throws SQLException {
		Statement stmt = conn.createStatement();
		stmt.execute("DELETE FROM Sessions");
		stmt.close();
	}

	/**
	 * Clear session clinicians table.
	 *
	 * @throws SQLException
	 *             the SQL exception
	 */
	public void clearSessionCliniciansTable() throws SQLException {
		Statement stmt = conn.createStatement();
		stmt.execute("DELETE FROM SessionClinicians");
		stmt.close();
	}

	/**
	 * Fills the database with static demo data.
	 *
	 * @throws ParseException
	 *             the parse exception
	 * @throws SQLException
	 *             the SQL exception
	 */
	public void generateStandardDataset() throws ParseException, SQLException {
		this.generateStandardClinicianData();
		this.generateStandardClinicianPreferencesData();
		this.generateStandardCommitmentsData();
		this.generatedStandardTimeAwayData();
		this.generateStandardCalendarData();
		this.generateStandardHolidayData();
		this.generateUnfilledSchedule();
	}

	/**
	 * Generate unfilled schedule.
	 *
	 * @throws SQLException
	 *             the SQL exception
	 */
	private void generateUnfilledSchedule() throws SQLException {
		GenerateUnfilledScheduleAction action = new GenerateUnfilledScheduleAction(
				conn);
		action.generateUnfilledSchedule();
	}

	/**
	 * Generate standard clinician data.
	 *
	 * @throws SQLException
	 *             the SQL exception
	 */
	private void generateStandardClinicianData() throws SQLException {
		ClinicianDAO clinicianDAO = new ClinicianDAO(conn);
		clinicianDAO.insert(new ClinicianBean(0, "Jeff"));
		clinicianDAO.insert(new ClinicianBean(1, "Ryan"));
		clinicianDAO.insert(new ClinicianBean(2, "Nathan"));
		clinicianDAO.insert(new ClinicianBean(3, "Kevin"));
		clinicianDAO.insert(new ClinicianBean(4, "Denise"));
		clinicianDAO.insert(new ClinicianBean(5, "Yusheng"));
		clinicianDAO.insert(new ClinicianBean(6, "Alice"));
		clinicianDAO.insert(new ClinicianBean(7, "Bob"));
		clinicianDAO.insert(new ClinicianBean(8, "Charlie"));
		clinicianDAO.insert(new ClinicianBean(9, "Dennis"));
		clinicianDAO.insert(new ClinicianBean(10, "Eric"));
		clinicianDAO.insert(new ClinicianBean(11, "Frank"));
		clinicianDAO.insert(new ClinicianBean(12, "Gary"));
		clinicianDAO.insert(new ClinicianBean(13, "Horatio"));
		clinicianDAO.insert(new ClinicianBean(14, "Igor"));
		clinicianDAO.insert(new ClinicianBean(15, "James"));
		clinicianDAO.insert(new ClinicianBean(16, "Kong"));
		clinicianDAO.insert(new ClinicianBean(17, "Linda"));
		clinicianDAO.insert(new ClinicianBean(18, "Maurice"));
		clinicianDAO.insert(new ClinicianBean(19, "Nymphadora"));
		clinicianDAO.insert(new ClinicianBean(20, "Olivia"));
		clinicianDAO.insert(new ClinicianBean(21, "Pam"));
		clinicianDAO.insert(new ClinicianBean(22, "Quincy"));
		clinicianDAO.insert(new ClinicianBean(23, "Rachel"));
	}

	/**
	 * Generate standard clinician preferences data.
	 *
	 * @throws SQLException
	 *             the SQL exception
	 */
	private void generateStandardClinicianPreferencesData() throws SQLException {
		ClinicianPreferencesDAO clinicianPreferencesDAO = new ClinicianPreferencesDAO(
				conn);
		clinicianPreferencesDAO.insert(new ClinicianPreferencesBean(0, 2, 1, 3,
				35, 44));
		clinicianPreferencesDAO.insert(new ClinicianPreferencesBean(1, 1, 2, 3,
				35, 44));
		clinicianPreferencesDAO.insert(new ClinicianPreferencesBean(2, 3, 2, 1,
				35, 44));
		clinicianPreferencesDAO.insert(new ClinicianPreferencesBean(3, 1, 2, 3,
				35, 44));
		clinicianPreferencesDAO.insert(new ClinicianPreferencesBean(4, 2, 1, 3,
				35, 44));
		clinicianPreferencesDAO.insert(new ClinicianPreferencesBean(5, 1, 3, 2,
				35, 44));
		clinicianPreferencesDAO.insert(new ClinicianPreferencesBean(6, 2, 3, 1,
				35, 44));
		clinicianPreferencesDAO.insert(new ClinicianPreferencesBean(7, 2, 1, 3,
				35, 44));
		clinicianPreferencesDAO.insert(new ClinicianPreferencesBean(8, 1, 2, 3,
				35, 44));
		clinicianPreferencesDAO.insert(new ClinicianPreferencesBean(9, 1, 2, 3,
				35, 44));
		clinicianPreferencesDAO.insert(new ClinicianPreferencesBean(10, 2, 3,
				1, 35, 44));
		clinicianPreferencesDAO.insert(new ClinicianPreferencesBean(11, 1, 3,
				2, 35, 44));
		clinicianPreferencesDAO.insert(new ClinicianPreferencesBean(12, 1, 3,
				2, 35, 44));
		clinicianPreferencesDAO.insert(new ClinicianPreferencesBean(13, 3, 2,
				1, 35, 44));
		clinicianPreferencesDAO.insert(new ClinicianPreferencesBean(14, 3, 2,
				1, 35, 44));
		clinicianPreferencesDAO.insert(new ClinicianPreferencesBean(15, 1, 2,
				3, 35, 44));
		clinicianPreferencesDAO.insert(new ClinicianPreferencesBean(16, 2, 1,
				3, 35, 44));
		clinicianPreferencesDAO.insert(new ClinicianPreferencesBean(17, 1, 2,
				3, 35, 44));
		clinicianPreferencesDAO.insert(new ClinicianPreferencesBean(18, 2, 1,
				3, 35, 44));
		clinicianPreferencesDAO.insert(new ClinicianPreferencesBean(19, 1, 3,
				2, 35, 44));
		clinicianPreferencesDAO.insert(new ClinicianPreferencesBean(20, 3, 2,
				1, 35, 44));
		clinicianPreferencesDAO.insert(new ClinicianPreferencesBean(21, 1, 2,
				3, 35, 44));
		clinicianPreferencesDAO.insert(new ClinicianPreferencesBean(22, 2, 1,
				3, 35, 44));
		clinicianPreferencesDAO.insert(new ClinicianPreferencesBean(23, 1, 2,
				3, 35, 44));
	}

	/**
	 * Generate standard commitments data.
	 *
	 * @throws SQLException
	 *             the SQL exception
	 * @throws ParseException
	 *             the parse exception
	 */
	private void generateStandardCommitmentsData() throws SQLException,
			ParseException {
		CommitmentsDAO commitmentsDAO = new CommitmentsDAO(conn);
		commitmentsDAO.insert(new CommitmentBean(0, 8, 9, DateRangeValidator
				.parseDate("4/1/2015"), "Doctor's appointment"));
		commitmentsDAO.insert(new CommitmentBean(0, 8, 9, DateRangeValidator
				.parseDate("4/8/2015"), "Doctor's appointment"));
		commitmentsDAO.insert(new CommitmentBean(0, 11, 12, DateRangeValidator
				.parseDate("4/3/2015"), "Staff meeting"));
		commitmentsDAO.insert(new CommitmentBean(1, 14, 15, DateRangeValidator
				.parseDate("3/30/2015"), "Staff meeting"));
		commitmentsDAO.insert(new CommitmentBean(2, 10, 11, DateRangeValidator
				.parseDate("3/30/2015"), "Dropping off child at school"));
		commitmentsDAO.insert(new CommitmentBean(2, 11, 12, DateRangeValidator
				.parseDate("4/15/2015"), "Doctor's appointment"));
		commitmentsDAO.insert(new CommitmentBean(3, 10, 11, DateRangeValidator
				.parseDate("4/10/2015"), "Senior staff meeting"));
		commitmentsDAO.insert(new CommitmentBean(4, 11, 12, DateRangeValidator
				.parseDate("4/9/2015"), "Some commitment"));
		commitmentsDAO.insert(new CommitmentBean(5, 15, 16, DateRangeValidator
				.parseDate("4/7/2015"), "Manager meeting"));
	}

	/**
	 * Generated standard time away data.
	 *
	 * @throws SQLException
	 *             the SQL exception
	 * @throws ParseException
	 *             the parse exception
	 */
	private void generatedStandardTimeAwayData() throws SQLException,
			ParseException {
		TimeAwayDAO timeAwayDAO = new TimeAwayDAO(conn);
		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
		timeAwayDAO.insert(new TimeAwayBean(0, "Vacation", format
				.parse("2/3/2015"), format.parse("2/12/2015")));
		timeAwayDAO.insert(new TimeAwayBean(1, "Vacation", format
				.parse("1/23/2015"), format.parse("1/27/2015")));
		timeAwayDAO.insert(new TimeAwayBean(2, "Vacation", format
				.parse("3/18/2015"), format.parse("3/22/2015")));
		timeAwayDAO.insert(new TimeAwayBean(3, "Vacation", format
				.parse("5/6/2015"), format.parse("5/10/2015")));
		timeAwayDAO.insert(new TimeAwayBean(4, "Unspecified", format
				.parse("3/1/2015"), format.parse("3/3/2015")));
		timeAwayDAO.insert(new TimeAwayBean(5, "Vacation", format
				.parse("4/17/2015"), format.parse("4/17/2015")));
	}

	/**
	 * Generate standard calendar data.
	 *
	 * @throws ParseException
	 *             the parse exception
	 * @throws SQLException
	 *             the SQL exception
	 */
	public void generateStandardCalendarData() throws ParseException,
			SQLException {
		CalendarDAO calendarDAO = new CalendarDAO(conn);
		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
		CalendarBean calendar = new CalendarBean();
		calendar.setId(0);
		calendar.setSemester(Semester.Spring);
		calendar.setStartDate(format.parse("01/23/2015"));
		calendar.setEndDate(format.parse("5/10/2015"));
		calendar.setIaMinHours(35);
		calendar.setEcMinHours(44);
		calendarDAO.insertCalendar(calendar);
	}

	/**
	 * Generate standard holiday data.
	 *
	 * @throws ParseException
	 *             the parse exception
	 * @throws SQLException
	 *             the SQL exception
	 */
	private void generateStandardHolidayData() throws ParseException,
			SQLException {
		HolidayDAO holidayDAO = new HolidayDAO(conn);
		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
		HolidayBean valentines = new HolidayBean(0, "Valentine's Day",
				format.parse("02/14/2015"), format.parse("02/14/2015"));
		holidayDAO.insertHoliday(valentines, 0, valentines.getID());
		HolidayBean unofficial = new HolidayBean(1, "Unofficial",
				format.parse("03/06/2015"), format.parse("03/06/2015"));
		holidayDAO.insertHoliday(unofficial, 0, unofficial.getID());
		HolidayBean springBreak = new HolidayBean(2, "Spring Break",
				format.parse("03/21/2015"), format.parse("03/29/2015"));
		holidayDAO.insertHoliday(springBreak, 0, springBreak.getID());
	}

	/**
	 * Generate empty semester dataset.
	 *
	 * @throws ParseException
	 *             the parse exception
	 * @throws SQLException
	 *             the SQL exception
	 */
	public void generateEmptySemesterDataset() throws ParseException,
			SQLException {
		// generate a one-weekend calendar
		CalendarDAO calendarDAO = new CalendarDAO(conn);
		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
		CalendarBean calendar = new CalendarBean();
		calendar.setId(0);
		calendar.setSemester(Semester.Spring);
		calendar.setStartDate(format.parse("03/14/2015"));
		calendar.setEndDate(format.parse("03/15/2015"));
		calendar.setIaMinHours(35);
		calendar.setEcMinHours(44);
		calendarDAO.insertCalendar(calendar);
		// Same holidays as before
		this.generateStandardHolidayData();

	}

	/**
	 * Overwrite any current data and fill with demo data.
	 *
	 * @throws SQLException
	 *             the SQL exception
	 * @throws ParseException
	 *             the parse exception
	 */
	public static void overwriteAndFillDemoData() throws SQLException,
			ParseException {
		TestDataGenerator gen = new TestDataGenerator(
				ConnectionFactory.getInstance());
		gen.clearTables();
		gen.generateStandardDataset();
	}
}
