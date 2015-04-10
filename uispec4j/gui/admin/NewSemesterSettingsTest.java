package gui.admin;

import generator.TestDataGenerator;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.uispec4j.Button;
import org.uispec4j.ListBox;
import org.uispec4j.TextBox;
import org.uispec4j.UISpecTestCase;
import org.uispec4j.Window;
import org.uispec4j.interception.FileChooserHandler;
import org.uispec4j.interception.MainClassAdapter;
import org.uispec4j.interception.WindowInterceptor;

import runner.NewSemesterSettingsRunner;
import validator.DateRangeValidator;
import action.ImportClinicianMeetingsActionTest;
import bean.CalendarBean;
import bean.ClinicianBean;
import bean.CommitmentBean;
import bean.HolidayBean;
import bean.Semester;
import dao.CalendarDAO;
import dao.ClinicianDAO;
import dao.CommitmentsDAO;
import dao.ConnectionFactory;
import dao.HolidayDAO;

/**
 * The Class NewSemesterSettingsTest.
 * 
 * @author jmfoste2
 * @author nbeltr2
 */
public class NewSemesterSettingsTest extends UISpecTestCase {
	
	private Connection con;
	private TestDataGenerator gen;
	private HolidayDAO holidayDAO;
	private CalendarDAO calendarDAO;
	private CommitmentsDAO commitmentsDAO;

	/* (non-Javadoc)
	 * @see org.uispec4j.UISpecTestCase#setUp()
	 */
	protected void setUp() throws Exception {
		setAdapter(new MainClassAdapter(NewSemesterSettingsRunner.class, new String[0]));
		con = ConnectionFactory.getInstance();
		ImportClinicianMeetingsActionTest.generateExcelFile(new Object[][] {{"Meeting", "Duration", "Start Time", "End Time", "Staff Members", "Start Date", "Frequency", "Days", "Dates", "Location"},
				{"Other Meeting", "60 minutes", "10:30am", "11:30am", "Jeff, [Nathan], Bill", DateRangeValidator.parseDate("4/20/2015"), "Weekly", "Monday", null, "Room C"}});
		gen = new TestDataGenerator(con);
		calendarDAO = new CalendarDAO(con);
		holidayDAO = new HolidayDAO(con);
		commitmentsDAO = new CommitmentsDAO(con);
		gen.clearTables();
		ClinicianDAO clinicianDAO = new ClinicianDAO(con);
		clinicianDAO.insert(new ClinicianBean(0, "Jeff"));
		clinicianDAO.insert(new ClinicianBean(1, "Ryan"));
		clinicianDAO.insert(new ClinicianBean(2, "Nathan"));
		clearHolidayTable();
	}
	
	protected void tearDown() throws SQLException {
		gen.clearTables();
		File f = new File("tmpFile.xlsx");
		f.delete();
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
	
	public void testImportMeetingSchedule() throws SQLException, ParseException {
		Window window = this.getMainWindow();
		
		// Add start and end date
		TextBox startDate = window.getTextBox("startDateText");
		TextBox endDate = window.getTextBox("endDateText");
		startDate.setText("1/20/2015");
		endDate.setText("5/25/2015");
		
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
		
		String filePath = new File("tmpFile.xlsx").getAbsolutePath();
		Button importMeetingsButton = window.getButton("importMeetingsButton");
		WindowInterceptor
			.init(importMeetingsButton.triggerClick())
			.process(FileChooserHandler.init()
			.titleEquals("Select Meeting Schedule")
			.assertAcceptsFilesOnly()
			.select(filePath))
		.run();
		
		//Submit calendar
		Button submitButton = window.getButton("submitButton");
		submitButton.click();
		
		List<HolidayBean> holidays = holidayDAO.loadHolidays();
		CalendarBean calendar = calendarDAO.loadCalendar();
		List<CommitmentBean> commitments = new ArrayList<CommitmentBean>();
		commitments.addAll(commitmentsDAO.loadCommitments(0));
		commitments.addAll(commitmentsDAO.loadCommitments(1));
		commitments.addAll(commitmentsDAO.loadCommitments(2));
		List<CommitmentBean> expectedCommitments = new ArrayList<CommitmentBean>();
		expectedCommitments.add(new CommitmentBean(0, 10, 12, DateRangeValidator.parseDate("4/20/2015"), "Other Meeting"));
		expectedCommitments.add(new CommitmentBean(0, 10, 12, DateRangeValidator.parseDate("4/27/2015"), "Other Meeting"));
		expectedCommitments.add(new CommitmentBean(0, 10, 12, DateRangeValidator.parseDate("5/4/2015"), "Other Meeting"));
		expectedCommitments.add(new CommitmentBean(0, 10, 12, DateRangeValidator.parseDate("5/11/2015"), "Other Meeting"));
		expectedCommitments.add(new CommitmentBean(0, 10, 12, DateRangeValidator.parseDate("5/18/2015"), "Other Meeting"));
		expectedCommitments.add(new CommitmentBean(0, 10, 12, DateRangeValidator.parseDate("5/25/2015"), "Other Meeting"));
		expectedCommitments.add(new CommitmentBean(2, 10, 12, DateRangeValidator.parseDate("4/20/2015"), "Other Meeting"));
		expectedCommitments.add(new CommitmentBean(2, 10, 12, DateRangeValidator.parseDate("4/27/2015"), "Other Meeting"));
		expectedCommitments.add(new CommitmentBean(2, 10, 12, DateRangeValidator.parseDate("5/4/2015"), "Other Meeting"));
		expectedCommitments.add(new CommitmentBean(2, 10, 12, DateRangeValidator.parseDate("5/11/2015"), "Other Meeting"));
		expectedCommitments.add(new CommitmentBean(2, 10, 12, DateRangeValidator.parseDate("5/18/2015"), "Other Meeting"));
		expectedCommitments.add(new CommitmentBean(2, 10, 12, DateRangeValidator.parseDate("5/25/2015"), "Other Meeting"));
		List<HolidayBean> expectedHolidays = new ArrayList<HolidayBean>();
		expectedHolidays.add(new HolidayBean(0, "Spring Break", DateRangeValidator.parseDate("3/5/2015"), DateRangeValidator.parseDate("3/12/2015")));
		CalendarBean expectedCalendar = new CalendarBean(0, DateRangeValidator.parseDate("1/20/2015"), DateRangeValidator.parseDate("5/25/2015"), 33, 15, Semester.Spring.ordinal());
		assertEquals(expectedCommitments, commitments);
		assertEquals(expectedHolidays, holidays);
		assertEquals(expectedCalendar, calendar);
	}
	
	public void testSelectMeetingSchedule() throws IOException {
		String filePath = new File("tmpFile.xlsx").getAbsolutePath();
		Window window = this.getMainWindow();
		Button importMeetingsButton = window.getButton("importMeetingsButton");
		WindowInterceptor
			.init(importMeetingsButton.triggerClick())
			.process(FileChooserHandler.init()
			.titleEquals("Select Meeting Schedule")
			.assertAcceptsFilesOnly()
			.select(filePath))
		.run();
		TextBox filePathBox = window.getTextBox("excelFilenameLabel");
		assertEquals(filePath, filePathBox.getText());
	}
	
	public void testRemoveMeetingSchedule() {
		String filePath = new File("tmpFile.xlsx").getAbsolutePath();
		Window window = this.getMainWindow();
		Button importMeetingsButton = window.getButton("importMeetingsButton");
		WindowInterceptor
			.init(importMeetingsButton.triggerClick())
			.process(FileChooserHandler.init()
			.titleEquals("Select Meeting Schedule")
			.assertAcceptsFilesOnly()
			.select(filePath))
		.run();
		Button removeMeetingsButton = window.getButton("removeMeetingsButton");
		removeMeetingsButton.click();
		TextBox filePathBox = window.getTextBox("excelFilenameLabel");
		assertEquals("No File Selected", filePathBox.getText());
	}
	
	private int countRowsInHolidayTable() throws SQLException {
		Statement stmt = con.createStatement();
		
		stmt.execute("SELECT COUNT(*) AS count FROM Holiday");
		ResultSet res = stmt.getResultSet();
		res.next();
		return res.getInt("count");
	}
	
	private int countRowsInCalendarTable() throws SQLException {
		Statement stmt = con.createStatement();
		
		stmt.execute("SELECT COUNT(*) AS count FROM Calendar");
		ResultSet res = stmt.getResultSet();
		res.next();
		return res.getInt("count");
	}
	
	private void clearHolidayTable() throws SQLException {
		Statement stmt = con.createStatement();
		
		stmt.execute("DELETE FROM Holiday");
	}
	
	private void clearCalendarTable() throws SQLException {
		Statement stmt = con.createStatement();
		
		stmt.execute("DELETE FROM Calendar");
	}
}
