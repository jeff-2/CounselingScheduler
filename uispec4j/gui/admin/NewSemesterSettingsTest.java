package gui.admin;

import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;

import org.uispec4j.Button;
import org.uispec4j.ListBox;
import org.uispec4j.TextBox;
import org.uispec4j.UISpecTestCase;
import org.uispec4j.Window;
import org.uispec4j.interception.MainClassAdapter;

import db.CalendarDao;
import db.HolidayDao;
import forms.Calendar;
import forms.Holiday;

/**
 * The Class NewSemesterSettingsTest.
 * 
 * @author jmfoste2
 * @author nbeltr2
 */
public class NewSemesterSettingsTest extends UISpecTestCase {
	
	private HolidayDao holidayDao;
	private CalendarDao calendarDao;
	private SimpleDateFormat format;

	/* (non-Javadoc)
	 * @see org.uispec4j.UISpecTestCase#setUp()
	 */
	protected void setUp() throws Exception {
		setAdapter(new MainClassAdapter(NewSemesterSettingsRunner.class, new String[0]));
		holidayDao = new HolidayDao();
		format = new SimpleDateFormat("MM/dd/yyyy");
		clearHolidayTable();
		clearCalendarTable();
	}
	
	protected void tearDown() throws SQLException {
		clearHolidayTable();
		clearCalendarTable();
	}
	
	/**
	 * Adds a holiday.
	 */
	public void addHoliday() {
		Window window = this.getMainWindow();
		TextBox startDate = window.getTextBox("startHolidayText");
		startDate.setText("3/5/2015");
		TextBox endDate = window.getTextBox("endHolidayText");
		endDate.setText("3/12/2015");
		TextBox holidayName = window.getTextBox("holidayNameText");
		holidayName.setText("Spring Break");
		Button addHolidayButton = window.getButton("addHolidayButton");
		addHolidayButton.click();
	}
	
	/**
	 * Test add holiday.
	 */
	public void testAddHoliday() {
		Window window = this.getMainWindow();
		addHoliday();
		ListBox holidays = window.getListBox();
		assertTrue(holidays.contentEquals("Spring Break 3/5/2015-3/12/2015"));
	}
	
	/**
	 * Test remove holiday.
	 */
	public void testRemoveHoliday() {
		Window window = this.getMainWindow();
		addHoliday();
		ListBox holidays = window.getListBox();
		holidays.select("Spring Break 3/5/2015-3/12/2015");
		
		Button removeHolidayButton = window.getButton("removeHolidayButton");
		removeHolidayButton.click();
		assertTrue(holidays.isEmpty());
	}
	
	public void testAddValidCalendar() throws SQLException {
		Window window = this.getMainWindow();
		
		// Add start and end date
		TextBox startDate = window.getTextBox("startDateText");
		TextBox endDate = window.getTextBox("endDateText");
		startDate.setText("3/1/2015");
		endDate.setText("3/20/2015");
		
		// Add Holiday
		TextBox holidayStartDate = window.getTextBox("startHolidayText");
		holidayStartDate.setText("3/5/2015");
		TextBox holidayEndDate = window.getTextBox("endHolidayText");
		holidayEndDate.setText("3/12/2015");
		TextBox holidayName = window.getTextBox("holidayNameText");
		holidayName.setText("Spring Break");
		Button addHolidayButton = window.getButton("addHolidayButton");
		addHolidayButton.click();
		
		// Add IA and EC clinician hours
		TextBox ecHours = window.getTextBox("ECHoursText");
		TextBox iaHours = window.getTextBox("IAHoursText");
		ecHours.setText("15");
		iaHours.setText("33");
		
		//Submit calendar
		Button submitButton = window.getButton("submitButton");
		submitButton.click();
		
		assertEquals(1, countRowsInHolidayTable());
		assertEquals(1, countRowsInCalendarTable());
	}
	
	public void testInvalidSemesterDates() throws SQLException {
		Window window = this.getMainWindow();
		
		// Add start and end date
		TextBox startDate = window.getTextBox("startDateText");
		TextBox endDate = window.getTextBox("endDateText");
		startDate.setText("3/1/2015");
		endDate.setText("3/20/2014");
		
		// Add IA and EC clinician hours
		TextBox ecHours = window.getTextBox("ECHoursText");
		TextBox iaHours = window.getTextBox("IAHoursText");
		ecHours.setText("15");
		iaHours.setText("33");
		
		//Submit calendar
		Button submitButton = window.getButton("submitButton");
		submitButton.click();
		
		assertEquals(0, countRowsInCalendarTable());
	}
	
	public void testInvalidHolidayDates() {
		Window window = this.getMainWindow();
		
		// Add Holiday
		TextBox holidayStartDate = window.getTextBox("startHolidayText");
		holidayStartDate.setText("3/5/2015");
		TextBox holidayEndDate = window.getTextBox("endHolidayText");
		holidayEndDate.setText("3/12/2014");
		TextBox holidayName = window.getTextBox("holidayNameText");
		holidayName.setText("Spring Break");
		Button addHolidayButton = window.getButton("addHolidayButton");
		addHolidayButton.click();
		
		ListBox holidays = window.getListBox();
		assertTrue(holidays.isEmpty());
	}
	
	private int countRowsInHolidayTable() throws SQLException {
		String connectionUrl = "jdbc:sqlserver://localhost;" +
				   "databaseName=CounselingScheduler;user=sa;password=w5Q[7S2_u2/\\+8Ds;";
		Connection con = DriverManager.getConnection(connectionUrl);
		Statement stmt = con.createStatement();
		
		stmt.execute("SELECT COUNT(*) AS count FROM Holiday");
		ResultSet res = stmt.getResultSet();
		res.next();
		return res.getInt("count");
	}
	
	private int countRowsInCalendarTable() throws SQLException {
		String connectionUrl = "jdbc:sqlserver://localhost;" +
				   "databaseName=CounselingScheduler;user=sa;password=w5Q[7S2_u2/\\+8Ds;";
		Connection con = DriverManager.getConnection(connectionUrl);
		Statement stmt = con.createStatement();
		
		stmt.execute("SELECT COUNT(*) AS count FROM Calendar");
		ResultSet res = stmt.getResultSet();
		res.next();
		return res.getInt("count");
	}
	
	private void clearHolidayTable() throws SQLException {
		String connectionUrl = "jdbc:sqlserver://localhost;" +
				   "databaseName=CounselingScheduler;user=sa;password=w5Q[7S2_u2/\\+8Ds;";
		Connection con = DriverManager.getConnection(connectionUrl);
		Statement stmt = con.createStatement();
		
		stmt.execute("DELETE FROM Holiday");
	}
	
	private void clearCalendarTable() throws SQLException {
		String connectionUrl = "jdbc:sqlserver://localhost;" +
				   "databaseName=CounselingScheduler;user=sa;password=w5Q[7S2_u2/\\+8Ds;";
		Connection con = DriverManager.getConnection(connectionUrl);
		Statement stmt = con.createStatement();
		
		stmt.execute("DELETE FROM Calendar");
	}
}
