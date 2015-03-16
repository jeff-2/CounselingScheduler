package generator;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import bean.CalendarBean;
import bean.HolidayBean;
import dao.CalendarDAO;
import dao.ConnectionFactory;
import dao.HolidayDAO;

public class TestDataGenerator {
	
	private Connection conn;
	
	public TestDataGenerator(Connection con) {
		this.conn = con;
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
		this.generateStandardCalendarData();
		this.generateStandardHolidayData();
		// Add other methods here
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
