package generator;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import bean.CalendarBean;
import bean.ClinicianBean;
import bean.ClinicianPreferencesBean;
import bean.CommitmentBean;
import bean.HolidayBean;
import bean.TimeAwayBean;
import dao.CalendarDAO;
import dao.ClinicianDAO;
import dao.ClinicianPreferencesDAO;
import dao.CommitmentsDAO;
import dao.ConnectionFactory;
import dao.HolidayDAO;
import dao.TimeAwayDAO;

public class TestDataGenerator {
	
	private Connection conn;
	
	public TestDataGenerator(Connection con) {
		this.conn = con;
	}
	
	public static void main(String [] args) throws SQLException, ParseException {
		TestDataGenerator gen = new TestDataGenerator(ConnectionFactory.getInstance());
		gen.clearTables();
		gen.generateStandardDataset();
	}
	
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
	
	public void clearCalendarTable() throws SQLException {
		Statement stmt = conn.createStatement();
		stmt.execute("DELETE FROM Calendar");
		stmt.close();
	}
	
	public void clearHolidayTable() throws SQLException {
		Statement stmt = conn.createStatement();
		stmt.execute("DELETE FROM Holiday");
		stmt.close();
	}
	
	public void clearCliniciansTable() throws SQLException {
		Statement stmt = conn.createStatement();
		stmt.execute("DELETE FROM Clinicians");
		stmt.close();
	}
	
	public void clearClinicianPreferencesTable() throws SQLException {
		Statement stmt = conn.createStatement();
		stmt.execute("DELETE FROM ClinicianPreferences");
		stmt.close();
	}
	
	public void clearCommitmentsTable() throws SQLException {
		Statement stmt = conn.createStatement();
		stmt.execute("DELETE FROM Commitments");
		stmt.close();
	}
	
	public void clearTimeAwayTable() throws SQLException {
		Statement stmt = conn.createStatement();
		stmt.execute("DELETE FROM TimeAway");
		stmt.close();
	}
	
	public void clearSessionsTable() throws SQLException {
		Statement stmt = conn.createStatement();
		stmt.execute("DELETE FROM Sessions");
		stmt.close();
	}
	
	public void clearSessionCliniciansTable() throws SQLException {
		Statement stmt = conn.createStatement();
		stmt.execute("DELETE FROM SessionClinicians");
		stmt.close();
	}

	/** 
	 * Fills the database with static demo data
	 * @throws ParseException
	 * @throws SQLException
	 */
	public void generateStandardDataset() throws ParseException, SQLException {
		this.generateStandardClinicianData();
		this.generateStandardClinicianPreferencesData();
		this.generateStandardCommitmentsData();
		this.generatedStandardTimeAwayData();
		this.generateStandardCalendarData();
		this.generateStandardHolidayData();
		// Add other methods here
	}
	
	private void generateStandardClinicianData() throws SQLException {
		ClinicianDAO clinicianDAO = new ClinicianDAO(conn);
		clinicianDAO.insert(new ClinicianBean(0, "Jeff"));
		clinicianDAO.insert(new ClinicianBean(1, "Ryan"));
		clinicianDAO.insert(new ClinicianBean(2, "Nathan"));
		clinicianDAO.insert(new ClinicianBean(3, "Kevin"));
		clinicianDAO.insert(new ClinicianBean(4, "Denise"));
		clinicianDAO.insert(new ClinicianBean(5, "Yusheng"));
		clinicianDAO.insert(new ClinicianBean(6, "A"));
		clinicianDAO.insert(new ClinicianBean(7, "B"));
		clinicianDAO.insert(new ClinicianBean(8, "C"));
		clinicianDAO.insert(new ClinicianBean(9, "D"));
		clinicianDAO.insert(new ClinicianBean(10, "E"));
		clinicianDAO.insert(new ClinicianBean(11, "F"));
		clinicianDAO.insert(new ClinicianBean(12, "G"));
		clinicianDAO.insert(new ClinicianBean(13, "H"));
		clinicianDAO.insert(new ClinicianBean(14, "I"));
		clinicianDAO.insert(new ClinicianBean(15, "J"));
		clinicianDAO.insert(new ClinicianBean(16, "K"));
		clinicianDAO.insert(new ClinicianBean(17, "L"));
		clinicianDAO.insert(new ClinicianBean(18, "M"));
		clinicianDAO.insert(new ClinicianBean(19, "N"));
		clinicianDAO.insert(new ClinicianBean(20, "O"));
		clinicianDAO.insert(new ClinicianBean(21, "P"));
		clinicianDAO.insert(new ClinicianBean(22, "Q"));
		clinicianDAO.insert(new ClinicianBean(23, "R"));
	}
	
	private void generateStandardClinicianPreferencesData() throws SQLException {
		ClinicianPreferencesDAO clinicianPreferencesDAO = new ClinicianPreferencesDAO(conn);
		clinicianPreferencesDAO.insert(new ClinicianPreferencesBean(0, 2, 1, 3));
		clinicianPreferencesDAO.insert(new ClinicianPreferencesBean(1, 1, 2, 3));
		clinicianPreferencesDAO.insert(new ClinicianPreferencesBean(2, 3, 2, 1));
		clinicianPreferencesDAO.insert(new ClinicianPreferencesBean(3, 1, 2, 3));
		clinicianPreferencesDAO.insert(new ClinicianPreferencesBean(4, 2, 1, 3));
		clinicianPreferencesDAO.insert(new ClinicianPreferencesBean(5, 1, 3, 2));
		clinicianPreferencesDAO.insert(new ClinicianPreferencesBean(6, 2, 3, 1));
		clinicianPreferencesDAO.insert(new ClinicianPreferencesBean(7, 2, 1, 3));
		clinicianPreferencesDAO.insert(new ClinicianPreferencesBean(8, 1, 2, 3));
		clinicianPreferencesDAO.insert(new ClinicianPreferencesBean(9, 1, 2, 3));
		clinicianPreferencesDAO.insert(new ClinicianPreferencesBean(10, 2, 3, 1));
		clinicianPreferencesDAO.insert(new ClinicianPreferencesBean(11, 1, 3, 2));
		clinicianPreferencesDAO.insert(new ClinicianPreferencesBean(12, 1, 3, 2));
		clinicianPreferencesDAO.insert(new ClinicianPreferencesBean(13, 3, 2, 1));
		clinicianPreferencesDAO.insert(new ClinicianPreferencesBean(14, 3, 2, 1));
		clinicianPreferencesDAO.insert(new ClinicianPreferencesBean(15, 1, 2, 3));
		clinicianPreferencesDAO.insert(new ClinicianPreferencesBean(16, 2, 1, 3));
		clinicianPreferencesDAO.insert(new ClinicianPreferencesBean(17, 1, 2, 3));
		clinicianPreferencesDAO.insert(new ClinicianPreferencesBean(18, 2, 1, 3));
		clinicianPreferencesDAO.insert(new ClinicianPreferencesBean(19, 1, 3, 2));
		clinicianPreferencesDAO.insert(new ClinicianPreferencesBean(20, 3, 2, 1));
		clinicianPreferencesDAO.insert(new ClinicianPreferencesBean(21, 1, 2, 3));
		clinicianPreferencesDAO.insert(new ClinicianPreferencesBean(22, 2, 1, 3));
		clinicianPreferencesDAO.insert(new ClinicianPreferencesBean(23, 1, 2, 3));
	}
	
	private void generateStandardCommitmentsData() throws SQLException {
		CommitmentsDAO commitmentsDAO = new CommitmentsDAO(conn);
		commitmentsDAO.insert(new CommitmentBean(0, 8, "Wednesday", "Doctor's appointment"));
		commitmentsDAO.insert(new CommitmentBean(0, 11, "Friday", "Staff meeting"));
		commitmentsDAO.insert(new CommitmentBean(1, 14, "Monday", "Staff meeting"));
		commitmentsDAO.insert(new CommitmentBean(2, 10, "Monday", "Dropping off child at school"));
		commitmentsDAO.insert(new CommitmentBean(2, 11, "Wednesday", "Doctor's appointment"));
		commitmentsDAO.insert(new CommitmentBean(3, 10, "Friday", "Senior staff meeting"));
		commitmentsDAO.insert(new CommitmentBean(4, 11, "Thursday", "Some commitment"));
		commitmentsDAO.insert(new CommitmentBean(5, 15, "Tuesday", "Manager meeting"));
	}
	
	private void generatedStandardTimeAwayData() throws SQLException, ParseException {
		TimeAwayDAO timeAwayDAO = new TimeAwayDAO(conn);
		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
		timeAwayDAO.insert(new TimeAwayBean(0, "Vacation", format.parse("2/3/2015"), format.parse("2/12/2015")));
		timeAwayDAO.insert(new TimeAwayBean(1, "Vacation", format.parse("1/23/2015"), format.parse("1/27/2015")));
		timeAwayDAO.insert(new TimeAwayBean(2, "Vacation", format.parse("3/18/2015"), format.parse("3/22/2015")));
		timeAwayDAO.insert(new TimeAwayBean(3, "Vacation", format.parse("5/6/2015"), format.parse("5/10/2015")));
		timeAwayDAO.insert(new TimeAwayBean(4, "Unspecified", format.parse("3/1/2015"), format.parse("3/3/2015")));
		timeAwayDAO.insert(new TimeAwayBean(5, "Vacation", format.parse("4/17/2015"), format.parse("4/17/2015")));
	}

	private void generateStandardCalendarData() throws ParseException, SQLException {
		CalendarDAO calendarDAO = new CalendarDAO(conn);
		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
		CalendarBean calendar = new CalendarBean();
		calendar.setId(0);
		calendar.setSemester(2);
		calendar.setStartDate(format.parse("01/23/2015"));
		calendar.setEndDate(format.parse("5/10/2015"));
		calendar.setIaMinHours(35);
		calendar.setEcMinHours(44);
		calendarDAO.insertCalendar(calendar);
	}

	private void generateStandardHolidayData() throws ParseException, SQLException {
		HolidayDAO holidayDAO = new HolidayDAO(conn);
		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
		HolidayBean valentines = new HolidayBean(0, "Valentine's Day", format.parse("02/14/2015"), format.parse("02/14/2015"));
		holidayDAO.insertHoliday(valentines, 0, valentines.getID());
		HolidayBean unofficial = new HolidayBean(1, "Unofficial", format.parse("03/06/2015"), format.parse("03/06/2015"));
		holidayDAO.insertHoliday(unofficial, 0, unofficial.getID());
		HolidayBean springBreak = new HolidayBean(2, "Spring Break", format.parse("03/21/2015"), format.parse("03/29/2015"));
		holidayDAO.insertHoliday(springBreak, 0, springBreak.getID());
	}

	public void generateEmptySemesterDataset() throws ParseException, SQLException {
		// generate a one-weekend calendar
		CalendarDAO calendarDAO = new CalendarDAO(conn);
		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
		CalendarBean calendar = new CalendarBean();
		calendar.setId(0);
		calendar.setSemester(2);
		calendar.setStartDate(format.parse("03/14/2015"));
		calendar.setEndDate(format.parse("03/15/2015"));
		calendar.setIaMinHours(35);
		calendar.setEcMinHours(44);
		calendarDAO.insertCalendar(calendar);
		// Same holidays as before
		this.generateStandardHolidayData();
		
	}

	public static void overwriteAndFillDemoData() throws SQLException, ParseException {
		TestDataGenerator gen = new TestDataGenerator(ConnectionFactory.getInstance());
		gen.clearTables();
		gen.generateStandardDataset();		
	}
}
