package gui.clinician;

import generator.TestDataGenerator;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.uispec4j.Button;
import org.uispec4j.CheckBox;
import org.uispec4j.ComboBox;
import org.uispec4j.ListBox;
import org.uispec4j.TextBox;
import org.uispec4j.Trigger;
import org.uispec4j.UISpecTestCase;
import org.uispec4j.Window;
import org.uispec4j.interception.MainClassAdapter;
import org.uispec4j.interception.WindowHandler;
import org.uispec4j.interception.WindowInterceptor;

import runner.ClinicianFormRunner;
import validator.DateRangeValidator;
import bean.CalendarBean;
import bean.ClinicianBean;
import bean.ClinicianPreferencesBean;
import bean.CommitmentBean;
import bean.Semester;
import bean.TimeAwayBean;
import dao.CalendarDAO;
import dao.ClinicianDAO;
import dao.ClinicianPreferencesDAO;
import dao.CommitmentsDAO;
import dao.ConnectionFactory;
import dao.TimeAwayDAO;

/**
 * 
 * @author jmfoste2, lim92
 *
 */
public class ClinicianFormTest extends UISpecTestCase {

	private Connection conn;
	private List<CommitmentBean> commitments;
	private ClinicianPreferencesBean preferences;
	private List<TimeAwayBean> timeAway;
	private ClinicianPreferencesDAO clinicianPreferencesDAO;
	private CommitmentsDAO commitmentsDAO;
	private TimeAwayDAO timeAwayDao;
	private TestDataGenerator gen;
	
	@Before
	protected void setUp() throws Exception {
		super.setUp();
		setAdapter(new MainClassAdapter(ClinicianFormRunner.class, new String[0]));
		
		conn = ConnectionFactory.getInstance();
		gen = new TestDataGenerator(conn);
		gen.clearTables();
		
		CalendarDAO calendarDAO = new CalendarDAO(conn);
		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
		CalendarBean calendar = new CalendarBean();
		calendar.setId(0);
		calendar.setSemester(Semester.Spring);
		calendar.setStartDate(format.parse("3/25/2015"));
		calendar.setEndDate(format.parse("4/27/2015"));
		calendar.setIaMinHours(35);
		calendar.setEcMinHours(44);
		calendarDAO.insertCalendar(calendar);
		
		clinicianPreferencesDAO = new ClinicianPreferencesDAO(conn);
		commitmentsDAO = new CommitmentsDAO(conn);
		timeAwayDao = new TimeAwayDAO(conn);
		
		ClinicianDAO clinicianDAO = new ClinicianDAO(conn);
		clinicianDAO.insert(new ClinicianBean(0, "Jeff"));
		
		preferences = new ClinicianPreferencesBean(0, 1, 2, 3, 35, 44);
		commitments = new ArrayList<CommitmentBean>();
		commitments.add(new CommitmentBean(0, 8, 9, DateRangeValidator.parseDate("3/25/2015"), "desc"));
		commitments.add(new CommitmentBean(0, 8, 9, DateRangeValidator.parseDate("4/1/2015"), "desc"));
		commitments.add(new CommitmentBean(0, 8, 9, DateRangeValidator.parseDate("4/8/2015"), "desc"));
		commitments.add(new CommitmentBean(0, 8, 9, DateRangeValidator.parseDate("4/15/2015"), "desc"));
		commitments.add(new CommitmentBean(0, 8, 9, DateRangeValidator.parseDate("4/22/2015"), "desc"));
		commitments.add(new CommitmentBean(0, 10, 11, DateRangeValidator.parseDate("3/30/2015"), "other desc"));
		commitments.add(new CommitmentBean(0, 10, 11, DateRangeValidator.parseDate("4/6/2015"), "other desc"));
		commitments.add(new CommitmentBean(0, 10, 11, DateRangeValidator.parseDate("4/13/2015"), "other desc"));
		commitments.add(new CommitmentBean(0, 10, 11, DateRangeValidator.parseDate("4/20/2015"), "other desc"));
		commitments.add(new CommitmentBean(0, 10, 11, DateRangeValidator.parseDate("4/27/2015"), "other desc"));
		
		timeAway = new ArrayList<TimeAwayBean>();
		timeAway.add(new TimeAwayBean(0, "some desc", DateRangeValidator.parseDate("1/3/1970"), DateRangeValidator.parseDate("1/12/1970")));
		timeAway.add(new TimeAwayBean(0, "some other desc", DateRangeValidator.parseDate("1/3/1970"), DateRangeValidator.parseDate("1/6/1970")));
	}
	
	@After
	public void tearDown() throws Exception {
		super.tearDown();
		gen.clearTables();
	}

	/**
	 * Tests whether the clear button clears the field
	 * @throws Exception 
	 */
	public void testClearField() throws Exception {
		Window window = this.getMainWindow();
		
		enterData("Biweekly", true);
		TextBox nameField = window.getTextBox("nameField");
		nameField.setText("Random Text");
		TextBox timeAwayName = window.getTextBox("timeAwayName");
		timeAwayName.setText("some desc");
		TextBox timeAwayStartDate = window.getTextBox("timeAwayStartDate");
		timeAwayStartDate.setText("1/3/1970");
		TextBox timeAwayEndDate = window.getTextBox("timeAwayEndDate");
		timeAwayEndDate.setText("1/12/1970");
		TextBox commitmentDescription = window.getTextBox("commitmentDescription");
		commitmentDescription.setText("desc");
		ComboBox startTimeBox = window.getComboBox("startTimeBox");
		startTimeBox.select("9:00 am");
		ComboBox endTimeBox = window.getComboBox("endTimeBox");
		endTimeBox.select("10:00 am");
		ComboBox daysOfWeekBox = window.getComboBox("daysOfWeekBox");
		daysOfWeekBox.select("Wednesday");
		ComboBox frequencyBox = window.getComboBox("frequencyBox");
		frequencyBox.select("Monthly");
		ComboBox morningRankBox = window.getComboBox("morningRankBox");
		morningRankBox.select("1");
		ComboBox noonRankBox = window.getComboBox("noonRankBox");
		noonRankBox.select("2");
		ComboBox afternoonRankBox = window.getComboBox("afternoonRankBox");
		afternoonRankBox.select("3");
		
		Button clearButton = window.getButton("Clear");
		clearButton.click();
		assertEquals("", nameField.getText());
		assertEquals("", timeAwayName.getText());
		assertEquals("", timeAwayStartDate.getText());
		assertEquals("", timeAwayEndDate.getText());
		assertEquals("", commitmentDescription.getText());
		daysOfWeekBox.selectionEquals("Monday");
		startTimeBox.selectionEquals("8:00 am");
		endTimeBox.selectionEquals("8:00 am");
		daysOfWeekBox.selectionEquals("Monday");
		frequencyBox.selectionEquals("Weekly");
		morningRankBox.selectionEquals("1");
		noonRankBox.selectionEquals("1");
		afternoonRankBox.selectionEquals("1");
		ListBox timeAway = window.getListBox("timeAway");
		timeAway.contentEquals("");
		ListBox commitments = window.getListBox("commitments");
		commitments.contentEquals("");
	}
	
	public void testInsertClinicianFormExternalB() throws Exception {
		insertData("Monthly", true);
		ClinicianPreferencesBean actualPreferences = clinicianPreferencesDAO.loadClinicianPreferences(0);
		List<CommitmentBean> actualCommitments = commitmentsDAO.loadCommitments(0);
		List<TimeAwayBean> actualTimeAway = timeAwayDao.loadTimeAway(0);
		
		List<CommitmentBean> expectedCommitments = new ArrayList<CommitmentBean>();
		expectedCommitments.add(new CommitmentBean(0, 7, 10, DateRangeValidator.parseDate("4/1/2015"), "desc"));
		expectedCommitments.add(new CommitmentBean(0, 9, 12, DateRangeValidator.parseDate("4/6/2015"), "other desc"));
		
		assertEquals(preferences, actualPreferences);
		assertEquals(expectedCommitments, actualCommitments);
		assertEquals(timeAway, actualTimeAway);
	}
	
	public void testInsertClinicianFormExternalA() throws Exception {
		insertData("Biweekly", true);
		
		ClinicianPreferencesBean actualPreferences = clinicianPreferencesDAO.loadClinicianPreferences(0);
		List<CommitmentBean> actualCommitments = commitmentsDAO.loadCommitments(0);
		List<TimeAwayBean> actualTimeAway = timeAwayDao.loadTimeAway(0);
		
		List<CommitmentBean> expectedCommitments = new ArrayList<CommitmentBean>();
		expectedCommitments.add(new CommitmentBean(0, 7, 10, DateRangeValidator.parseDate("3/25/2015"), "desc"));
		expectedCommitments.add(new CommitmentBean(0, 7, 10, DateRangeValidator.parseDate("4/8/2015"), "desc"));
		expectedCommitments.add(new CommitmentBean(0, 7, 10, DateRangeValidator.parseDate("4/22/2015"), "desc"));
		expectedCommitments.add(new CommitmentBean(0, 9, 12, DateRangeValidator.parseDate("3/30/2015"), "other desc"));
		expectedCommitments.add(new CommitmentBean(0, 9, 12, DateRangeValidator.parseDate("4/13/2015"), "other desc"));
		expectedCommitments.add(new CommitmentBean(0, 9, 12, DateRangeValidator.parseDate("4/27/2015"), "other desc"));
		
		assertEquals(preferences, actualPreferences);
		assertEquals(expectedCommitments, actualCommitments);
		assertEquals(timeAway, actualTimeAway);
	}
	
	public void testInsertClinicianFormBiweekly() throws Exception {
		insertData("Biweekly", false);
		
		ClinicianPreferencesBean actualPreferences = clinicianPreferencesDAO.loadClinicianPreferences(0);
		List<CommitmentBean> actualCommitments = commitmentsDAO.loadCommitments(0);
		List<TimeAwayBean> actualTimeAway = timeAwayDao.loadTimeAway(0);
		
		List<CommitmentBean> expectedCommitments = new ArrayList<CommitmentBean>();
		expectedCommitments.add(new CommitmentBean(0, 8, 9, DateRangeValidator.parseDate("3/25/2015"), "desc"));
		expectedCommitments.add(new CommitmentBean(0, 8, 9, DateRangeValidator.parseDate("4/8/2015"), "desc"));
		expectedCommitments.add(new CommitmentBean(0, 8, 9, DateRangeValidator.parseDate("4/22/2015"), "desc"));
		expectedCommitments.add(new CommitmentBean(0, 10, 11, DateRangeValidator.parseDate("3/30/2015"), "other desc"));
		expectedCommitments.add(new CommitmentBean(0, 10, 11, DateRangeValidator.parseDate("4/13/2015"), "other desc"));
		expectedCommitments.add(new CommitmentBean(0, 10, 11, DateRangeValidator.parseDate("4/27/2015"), "other desc"));
		
		assertEquals(preferences, actualPreferences);
		assertEquals(expectedCommitments, actualCommitments);
		assertEquals(timeAway, actualTimeAway);
	}
	
	public void testInsertClinicianFormMonthly() throws Exception {
		insertData("Monthly", false);
		
		ClinicianPreferencesBean actualPreferences = clinicianPreferencesDAO.loadClinicianPreferences(0);
		List<CommitmentBean> actualCommitments = commitmentsDAO.loadCommitments(0);
		List<TimeAwayBean> actualTimeAway = timeAwayDao.loadTimeAway(0);
		
		List<CommitmentBean> expectedCommitments = new ArrayList<CommitmentBean>();
		expectedCommitments.add(new CommitmentBean(0, 8, 9, DateRangeValidator.parseDate("4/1/2015"), "desc"));
		expectedCommitments.add(new CommitmentBean(0, 10, 11, DateRangeValidator.parseDate("4/6/2015"), "other desc"));
		
		assertEquals(preferences, actualPreferences);
		assertEquals(expectedCommitments, actualCommitments);
		assertEquals(timeAway, actualTimeAway);
	}
	
	public void testInsertClinicianFormWeekly() throws Exception {
		insertData("Weekly", false);
		
		ClinicianPreferencesBean actualPreferences = clinicianPreferencesDAO.loadClinicianPreferences(0);
		List<CommitmentBean> actualCommitments = commitmentsDAO.loadCommitments(0);
		List<TimeAwayBean> actualTimeAway = timeAwayDao.loadTimeAway(0);
		
		assertEquals(preferences, actualPreferences);
		assertEquals(commitments, actualCommitments);
		assertEquals(timeAway, actualTimeAway);
	}
	
	private void enterData(String frequency, boolean isExternal) throws Exception {
		Window window = this.getMainWindow();
		TextBox nameField = window.getTextBox("nameField");
		nameField.setText("Jeff");
		
		TextBox timeAwayName = window.getTextBox("timeAwayName");
		timeAwayName.setText("some desc");
		
		TextBox timeAwayStartDate = window.getTextBox("timeAwayStartDate");
		timeAwayStartDate.setText("1/3/1970");
		
		TextBox timeAwayEndDate = window.getTextBox("timeAwayEndDate");
		timeAwayEndDate.setText("1/12/1970");
		
		Button addTimeAwayButton = window.getButton("addTimeAwayButton");
		addTimeAwayButton.click();
		
		timeAwayName.setText("some other desc");
		timeAwayStartDate.setText("1/3/1970");
		timeAwayEndDate.setText("1/6/1970");
		addTimeAwayButton.click();
		
		TextBox commitmentDescription = window.getTextBox("commitmentDescription");
		commitmentDescription.setText("desc");
		
		ComboBox startTimeBox = window.getComboBox("startTimeBox");
		startTimeBox.select("8:00 am");
		
		ComboBox endTimeBox = window.getComboBox("endTimeBox");
		endTimeBox.select("9:00 am");
		
		ComboBox daysOfWeekBox = window.getComboBox("daysOfWeekBox");
		daysOfWeekBox.select("Wednesday");
		
		ComboBox frequencyBox = window.getComboBox("frequencyBox");
		frequencyBox.select(frequency);
		
		if (isExternal) {
			CheckBox checkBox = window.getCheckBox();
			checkBox.select();
		}
		
		Button addCommitmentButton = window.getButton("addCommitmentButton");
		addCommitmentButton.click();
		
		commitmentDescription.setText("other desc");
		startTimeBox.select("10:00 am");
		endTimeBox.select("11:00 am");
		daysOfWeekBox.select("Monday");
		frequencyBox.select(frequency);
		
		if (isExternal) {
			CheckBox checkBox = window.getCheckBox();
			checkBox.select();
		}
		addCommitmentButton.click();
		
		ComboBox morningRankBox = window.getComboBox("morningRankBox");
		morningRankBox.select("1");
		
		ComboBox noonRankBox = window.getComboBox("noonRankBox");
		noonRankBox.select("2");
		
		ComboBox afternoonRankBox = window.getComboBox("afternoonRankBox");
		afternoonRankBox.select("3");
	}
	
	private void insertData(String frequency, boolean isExternal) throws Exception {
		enterData(frequency, isExternal);
		
		Window window = this.getMainWindow();
		Button submitButton = window.getButton("submitButton");
		submitButton.click();
	}
	
	public void testCancelUpdateClinicianForm() throws Exception {
		insertData("Weekly", false);
		Window window = this.getMainWindow();
		Button submitButton = window.getButton("submitButton");
		
		WindowInterceptor
			.init(submitButton.triggerClick())
			.process(new WindowHandler() {
			public Trigger process(Window window) {
				assertEquals(window.getTitle(), "Update clinician preferences");
				return window.getButton("NO").triggerClick();
			}
		})
		.run();
		
		ClinicianPreferencesBean actualPreferences = clinicianPreferencesDAO.loadClinicianPreferences(0);
		List<CommitmentBean> actualCommitments = commitmentsDAO.loadCommitments(0);
		List<TimeAwayBean> actualTimeAway = timeAwayDao.loadTimeAway(0);
		
		assertEquals(preferences, actualPreferences);
		assertEquals(commitments, actualCommitments);
		assertEquals(timeAway, actualTimeAway);
	}
	
	public void testUpdateClinicianForm() throws Exception {
		insertData("Weekly", false);
		Window window = this.getMainWindow();
		Button submitButton = window.getButton("submitButton");
		submitButton.click();
		
		Button clearButton = window.getButton("Clear");
		clearButton.click();
		
		loadOtherData();
		
		WindowInterceptor
			.init(submitButton.triggerClick())
			.process(new WindowHandler() {
			public Trigger process(Window window) {
				assertEquals(window.getTitle(), "Update clinician preferences");
				return window.getButton("YES").triggerClick();
			}
		})
		.run();
		
		ClinicianPreferencesBean actualPreferences = clinicianPreferencesDAO.loadClinicianPreferences(0);
		List<CommitmentBean> actualCommitments = commitmentsDAO.loadCommitments(0);
		List<TimeAwayBean> actualTimeAway = timeAwayDao.loadTimeAway(0);
		
		ClinicianPreferencesBean otherPreferences = new ClinicianPreferencesBean(0, 3, 1, 2, 35, 44);
		List<CommitmentBean> otherCommitments = new ArrayList<CommitmentBean>();
		otherCommitments.add(new CommitmentBean(0, 15, 16, DateRangeValidator.parseDate("3/25/2015"), "pear"));
		otherCommitments.add(new CommitmentBean(0, 15, 16, DateRangeValidator.parseDate("4/1/2015"), "pear"));
		otherCommitments.add(new CommitmentBean(0, 15, 16, DateRangeValidator.parseDate("4/8/2015"), "pear"));
		otherCommitments.add(new CommitmentBean(0, 15, 16, DateRangeValidator.parseDate("4/15/2015"), "pear"));
		otherCommitments.add(new CommitmentBean(0, 15, 16, DateRangeValidator.parseDate("4/22/2015"), "pear"));
		List<TimeAwayBean> otherTimeAway = new ArrayList<TimeAwayBean>();
		otherTimeAway.add(new TimeAwayBean(0, "a desc", DateRangeValidator.parseDate("1/3/2000"), DateRangeValidator.parseDate("1/12/2000")));
		
		assertEquals(otherPreferences, actualPreferences);
		assertEquals(otherCommitments, actualCommitments);
		assertEquals(otherTimeAway, actualTimeAway);
	}
	
	private void loadOtherData() throws Exception {
		Window window = this.getMainWindow();
		TextBox nameField = window.getTextBox("nameField");
		nameField.setText("Jeff");
		
		TextBox timeAwayName = window.getTextBox("timeAwayName");
		timeAwayName.setText("a desc");
		
		TextBox timeAwayStartDate = window.getTextBox("timeAwayStartDate");
		timeAwayStartDate.setText("1/3/2000");
		
		TextBox timeAwayEndDate = window.getTextBox("timeAwayEndDate");
		timeAwayEndDate.setText("1/12/2000");
		
		Button addTimeAwayButton = window.getButton("addTimeAwayButton");
		addTimeAwayButton.click();
		
		TextBox commitmentDescription = window.getTextBox("commitmentDescription");
		commitmentDescription.setText("pear");
		
		ComboBox startTimeBox = window.getComboBox("startTimeBox");
		startTimeBox.select("3:00 pm");
		
		ComboBox endTimeBox = window.getComboBox("endTimeBox");
		endTimeBox.select("4:00 pm");
		
		ComboBox daysOfWeekBox = window.getComboBox("daysOfWeekBox");
		daysOfWeekBox.select("Wednesday");
		
		ComboBox frequencyBox = window.getComboBox("frequencyBox");
		frequencyBox.select("Weekly");
		
		Button addCommitmentButton = window.getButton("addCommitmentButton");
		addCommitmentButton.click();
		
		ComboBox morningRankBox = window.getComboBox("morningRankBox");
		morningRankBox.select("3");
		
		ComboBox noonRankBox = window.getComboBox("noonRankBox");
		noonRankBox.select("1");
		
		ComboBox afternoonRankBox = window.getComboBox("afternoonRankBox");
		afternoonRankBox.select("2");
		
		Button submitButton = window.getButton("submitButton");
		submitButton.click();
	}
}
