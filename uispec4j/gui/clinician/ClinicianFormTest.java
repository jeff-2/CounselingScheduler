package gui.clinician;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.uispec4j.Button;
import org.uispec4j.ComboBox;
import org.uispec4j.TextBox;
import org.uispec4j.Trigger;
import org.uispec4j.UISpecTestCase;
import org.uispec4j.Window;
import org.uispec4j.interception.MainClassAdapter;
import org.uispec4j.interception.WindowHandler;
import org.uispec4j.interception.WindowInterceptor;

import bean.ClinicianPreferencesBean;
import bean.CommitmentBean;
import bean.TimeAwayBean;
import dao.ClinicianPreferencesDAO;
import dao.CommitmentsDAO;
import dao.ConnectionFactory;
import dao.TimeAwayDAO;
import runner.ClinicianFormRunner;
import validator.DateRangeValidator;

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
	private ClinicianPreferencesDAO clinicianPreferencesDao;
	private CommitmentsDAO commitmentsDao;
	private TimeAwayDAO timeAwayDao;
	
	@Before
	protected void setUp() throws Exception {
		setAdapter(new MainClassAdapter(ClinicianFormRunner.class, new String[0]));
		
		conn = ConnectionFactory.getInstance();
		
		clearCliniciansTable();
		clearClinicianPreferencesTable();
		clearCommitmentsTable();
		clearTimeAwayTable();
		
		clinicianPreferencesDao = new ClinicianPreferencesDAO(conn);
		commitmentsDao = new CommitmentsDAO(conn);
		timeAwayDao = new TimeAwayDAO(conn);
		
		PreparedStatement stmt = conn.prepareStatement("INSERT INTO Clinicians (id, name) VALUES (?, ?)");
		stmt.setInt(1, 0);
		stmt.setString(2, "Jeff");
		stmt.execute();
		stmt.close();
		
		preferences = new ClinicianPreferencesBean(0, 1, 2, 3);
		commitments = new ArrayList<CommitmentBean>();
		commitments.add(new CommitmentBean(0, 8, "Wednesday", "desc"));
		commitments.add(new CommitmentBean(0, 10, "Monday", "other desc"));
		
		timeAway = new ArrayList<TimeAwayBean>();
		timeAway.add(new TimeAwayBean(0, "some desc", DateRangeValidator.parseDate("1/3/1970"), DateRangeValidator.parseDate("1/12/1970")));
		timeAway.add(new TimeAwayBean(0, "some other desc", DateRangeValidator.parseDate("1/3/1970"), DateRangeValidator.parseDate("1/6/1970")));
	}
	
	private void clearCliniciansTable() throws Exception {
		PreparedStatement stmt = conn.prepareStatement("DELETE FROM Clinicians");
		stmt.execute();
		stmt.close();
	}
	
	private void clearClinicianPreferencesTable() throws Exception {
		PreparedStatement stmt = conn.prepareStatement("DELETE FROM ClinicianPreferences");
		stmt.execute();
		stmt.close();
	}
	
	private void clearCommitmentsTable() throws Exception {
		PreparedStatement stmt = conn.prepareStatement("DELETE FROM Commitments");
		stmt.execute();
		stmt.close();
	}
	
	private void clearTimeAwayTable() throws Exception {
		PreparedStatement stmt = conn.prepareStatement("DELETE FROM TimeAway");
		stmt.execute();
		stmt.close();
	}
	
	@After
	public void tearDown() throws Exception {
		clearCliniciansTable();
		clearClinicianPreferencesTable();
		clearCommitmentsTable();
		clearTimeAwayTable();
	}

	/**
	 * Tests whether the clear button clears the field
	 */
	public void testClearField() {
		Window window = this.getMainWindow();
		TextBox nameField = window.getTextBox("nameField");
		nameField.setText("Random Text");
		Button clearButton = window.getButton("Clear");
		clearButton.click();
		assertEquals("", nameField.getText());
	}
	
	public void testInsertClinicianForm() throws Exception {
		insertData();
		
		ClinicianPreferencesBean actualPreferences = clinicianPreferencesDao.loadClinicianPreferences(0);
		List<CommitmentBean> actualCommitments = commitmentsDao.loadCommitments(0);
		List<TimeAwayBean> actualTimeAway = timeAwayDao.loadTimeAway(0);
		
		assertEquals(preferences, actualPreferences);
		assertEquals(commitments, actualCommitments);
		assertEquals(timeAway, actualTimeAway);
	}
	
	private void insertData() throws Exception {
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
		
		ComboBox operatingHoursBox = window.getComboBox("operatingHoursBox");
		operatingHoursBox.select("8 am");
		
		ComboBox daysOfWeekBox = window.getComboBox("daysOfWeekBox");
		daysOfWeekBox.select("Wednesday");
		
		Button addCommitmentButton = window.getButton("addCommitmentButton");
		addCommitmentButton.click();
		
		commitmentDescription.setText("other desc");
		operatingHoursBox.select("10 am");
		daysOfWeekBox.select("Monday");
		addCommitmentButton.click();
		
		ComboBox morningRankBox = window.getComboBox("morningRankBox");
		morningRankBox.select("1");
		
		ComboBox noonRankBox = window.getComboBox("noonRankBox");
		noonRankBox.select("2");
		
		ComboBox afternoonRankBox = window.getComboBox("afternoonRankBox");
		afternoonRankBox.select("3");
		
		Button submitButton = window.getButton("submitButton");
		submitButton.click();
	}
	
	public void testCancelUpdateClinicianForm() throws Exception {
		insertData();
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
		
		ClinicianPreferencesBean actualPreferences = clinicianPreferencesDao.loadClinicianPreferences(0);
		List<CommitmentBean> actualCommitments = commitmentsDao.loadCommitments(0);
		List<TimeAwayBean> actualTimeAway = timeAwayDao.loadTimeAway(0);
		
		assertEquals(preferences, actualPreferences);
		assertEquals(commitments, actualCommitments);
		assertEquals(timeAway, actualTimeAway);
	}
	
	public void testUpdateClinicianForm() throws Exception {
		insertData();
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
		
		ClinicianPreferencesBean actualPreferences = clinicianPreferencesDao.loadClinicianPreferences(0);
		List<CommitmentBean> actualCommitments = commitmentsDao.loadCommitments(0);
		List<TimeAwayBean> actualTimeAway = timeAwayDao.loadTimeAway(0);
		
		ClinicianPreferencesBean otherPreferences = new ClinicianPreferencesBean(0, 3, 1, 2);
		List<CommitmentBean> otherCommitments = new ArrayList<CommitmentBean>();
		otherCommitments.add(new CommitmentBean(0, 15, "Wednesday", "pear"));
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
		
		ComboBox operatingHoursBox = window.getComboBox("operatingHoursBox");
		operatingHoursBox.select("3 pm");
		
		ComboBox daysOfWeekBox = window.getComboBox("daysOfWeekBox");
		daysOfWeekBox.select("Wednesday");
		
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
