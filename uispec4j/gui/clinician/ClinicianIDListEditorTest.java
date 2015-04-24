package gui.clinician;

import generator.TestDataGenerator;
import gui.admin.AdminApplication;

import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.uispec4j.Button;
import org.uispec4j.ComboBox;
import org.uispec4j.Key;
import org.uispec4j.ListBox;
import org.uispec4j.Panel;
import org.uispec4j.TextBox;
import org.uispec4j.Trigger;
import org.uispec4j.UISpecTestCase;
import org.uispec4j.Window;
import org.uispec4j.interception.MainClassAdapter;
import org.uispec4j.interception.WindowHandler;
import org.uispec4j.interception.WindowInterceptor;

import bean.ClinicianBean;
import bean.ClinicianPreferencesBean;
import bean.CommitmentBean;
import bean.TimeAwayBean;
import dao.ClinicianDAO;
import dao.ClinicianPreferencesDAO;
import dao.CommitmentsDAO;
import dao.ConnectionFactory;
import dao.TimeAwayDAO;

public class ClinicianIDListEditorTest extends UISpecTestCase {

	private Connection conn;
	private ClinicianDAO clinicianDAO;
	private TestDataGenerator gen;
	private ClinicianBean beanA, beanB;
	
	@Before
	protected void setUp() throws Exception {
		super.setUp();
		setAdapter(new MainClassAdapter(AdminApplication.class, new String[0]));
		
		conn = ConnectionFactory.getInstance();
		gen = new TestDataGenerator(conn);
		gen.clearTables();
		clinicianDAO = new ClinicianDAO(conn);
		
		beanA = new ClinicianBean(0, "Jeff");
		beanB = new ClinicianBean(1, "Ryan");
	}
	
	@After
	public void tearDown() throws Exception {
		super.tearDown();
		gen.clearTables();
	}
	
	private void typeText(TextBox inputBox, String text) {
		for (char c : text.toCharArray()) {
			if (Character.isUpperCase(c)) {
				inputBox.typeKey(Key.shift(Key.get(Character.toLowerCase(c))));
			} else {
				inputBox.typeKey(Key.get(c));
			}
		}
	}
	
	@Test
	public void testRejectDuplicateClinicians() throws Exception {
		Window window = this.getMainWindow();
		Panel editorPanel = window.getPanel("ClinicianIDListEditor");
		TextBox newFullnameField = editorPanel.getTextBox("newFullnameField");
		typeText(newFullnameField, beanA.getName());
		
		Button addButton = editorPanel.getButton("addButton");
		addButton.click();

		ListBox cliniciansBox = editorPanel.getListBox();
		cliniciansBox.contentEquals(beanA.getName());
		
		List<ClinicianBean> expectedClinicians = new ArrayList<ClinicianBean>();
		expectedClinicians.add(beanA);
		
		List<ClinicianBean> clinicians = clinicianDAO.loadClinicians();
		assertEquals(clinicians, expectedClinicians);
		
		newFullnameField.setText("");
		typeText(newFullnameField, beanA.getName());
		
		WindowInterceptor
		.init(addButton.triggerClick())
		.process(new WindowHandler() {
			public Trigger process(Window window) {
				assertEquals(window.getTitle(), "Adding duplicate clinician");
				return window.getButton("OK").triggerClick();
			}
		})
		.run();
		
		clinicians = clinicianDAO.loadClinicians();
		assertEquals(clinicians, expectedClinicians);
	}
	
	@Test
	public void testAddClinicians() throws Exception {
		Window window = this.getMainWindow();
		TextBox newFullnameField = window.getTextBox("newFullnameField");
		typeText(newFullnameField, beanA.getName());
		
		Button addButton = window.getButton("addButton");
		addButton.click();

		ListBox cliniciansBox = window.getListBox();
		cliniciansBox.contentEquals(beanA.getName());
		
		List<ClinicianBean> expectedClinicians = new ArrayList<ClinicianBean>();
		expectedClinicians.add(beanA);
		
		List<ClinicianBean> clinicians = clinicianDAO.loadClinicians();
		assertEquals(clinicians, expectedClinicians);
		
		newFullnameField.setText("");
		typeText(newFullnameField, beanB.getName());
		addButton.click();
		cliniciansBox.contentEquals(beanA.getName(), beanB.getName());
		
		expectedClinicians.add(beanB);
		
		clinicians = clinicianDAO.loadClinicians();
		assertEquals(clinicians, expectedClinicians);
	}
	
	@Test
	public void testAddRemoveReadd() throws Exception {
		Window window = this.getMainWindow();
		TextBox newFullnameField = window.getTextBox("newFullnameField");
		typeText(newFullnameField, beanA.getName());
		
		Button addButton = window.getButton("addButton");
		addButton.click();

		ListBox cliniciansBox = window.getListBox();
		cliniciansBox.contentEquals(beanA.getName());
		
		List<ClinicianBean> expectedClinicians = new ArrayList<ClinicianBean>();
		expectedClinicians.add(beanA);
		
		List<ClinicianBean> clinicians = clinicianDAO.loadClinicians();
		assertEquals(clinicians, expectedClinicians);
		
		cliniciansBox.select(beanA.getName());
		Button removeButton = window.getButton("removeButton");
		removeButton.click();
		
		expectedClinicians.remove(beanA);
		
		clinicians = clinicianDAO.loadClinicians();
		assertEquals(clinicians, expectedClinicians);
		
		newFullnameField.setText("");
		typeText(newFullnameField, beanA.getName());
		addButton.click();

		expectedClinicians.add(beanA);
		clinicians = clinicianDAO.loadClinicians();
		assertEquals(clinicians, expectedClinicians);
	}
	
	@Test
	public void testRemoveClinicians() throws Exception {
		Window window = this.getMainWindow();
		TextBox newFullnameField = window.getTextBox("newFullnameField");
		typeText(newFullnameField, beanA.getName());
		
		Button addButton = window.getButton("addButton");
		addButton.click();

		ListBox cliniciansBox = window.getListBox();
		cliniciansBox.contentEquals(beanA.getName());
		
		List<ClinicianBean> expectedClinicians = new ArrayList<ClinicianBean>();
		expectedClinicians.add(beanA);
		
		List<ClinicianBean> clinicians = clinicianDAO.loadClinicians();
		assertEquals(clinicians, expectedClinicians);
		
		newFullnameField.setText("");
		typeText(newFullnameField, beanB.getName());
		addButton.click();
		cliniciansBox.contentEquals(beanA.getName(), beanB.getName());
		
		expectedClinicians.add(beanB);
		
		clinicians = clinicianDAO.loadClinicians();
		assertEquals(clinicians, expectedClinicians);
		
		cliniciansBox.select(beanA.getName());
		Button removeButton = window.getButton("removeButton");
		removeButton.click();
		cliniciansBox.contentEquals(beanB.getName());
		clinicians = clinicianDAO.loadClinicians();
		expectedClinicians.remove(beanA);
		assertEquals(clinicians, expectedClinicians);
		
		cliniciansBox.select(beanB.getName());
		removeButton.click();
		clinicians = clinicianDAO.loadClinicians();
		expectedClinicians.remove(beanB);
		assertEquals(clinicians, expectedClinicians);
	}
	
	public void testLoadClinicianPreferences() throws ParseException, SQLException {
		gen.generateStandardDataset();
		Window window = this.getMainWindow();
		ListBox cliniciansBox = window.getListBox();
		cliniciansBox.select("Denise");
		Button editButton = window.getButton("editButton");
		WindowInterceptor
		.init(editButton.triggerClick())
		.process(new WindowHandler() {
			public Trigger process(final Window window) {
				assertEquals(window.getTitle(), "Clinician Input Form");
				ComboBox morningRankBox = window.getComboBox("morningRankBox");
				morningRankBox.selectionEquals("2");
				ComboBox noonRankBox = window.getComboBox("noonRankBox");
				noonRankBox.selectionEquals("1");
				ComboBox afternoonRankBox = window.getComboBox("afternoonRankBox");
				afternoonRankBox.selectionEquals("3");
				TextBox nameField = window.getTextBox("nameField");
				assertEquals("Denise", nameField.getText());
				TextBox iaHours = window.getTextBox("iaHours");
				assertEquals("35", iaHours.getText());
				TextBox ecHours = window.getTextBox("ecHours");
				assertEquals("44", ecHours.getText());
				ListBox timeAway = window.getListBox("timeAway");
				timeAway.contentEquals("Unspecified: Sunday, March 1, 2015 to Tuesday, March 3, 2015");
				ListBox commitments = window.getListBox("commitments");
				commitments.contentEquals("Meeting: Some commitment on Thursday, April 9, 2015 from 11 to 12");
				return new Trigger() {
					@Override
			        public void run() throws Exception {
						((JFrame)window.getAwtContainer()).dispatchEvent(new WindowEvent((JFrame)window.getAwtContainer(), WindowEvent.WINDOW_CLOSING));
			        }
				};
			}
		})
		.run();
	}
	
	public void testUpdateClinicianPreferences() throws ParseException, SQLException {
		gen.generateStandardDataset();
		Window window = this.getMainWindow();
		ListBox cliniciansBox = window.getListBox();
		cliniciansBox.select("Denise");
		Button editButton = window.getButton("editButton");
		WindowInterceptor
		.init(editButton.triggerClick())
		.process(new WindowHandler() {
			public Trigger process(final Window window) {
				assertEquals(window.getTitle(), "Clinician Input Form");
				ComboBox morningRankBox = window.getComboBox("morningRankBox");
				morningRankBox.select("1");
				ComboBox noonRankBox = window.getComboBox("noonRankBox");
				noonRankBox.select("2");
				ComboBox afternoonRankBox = window.getComboBox("afternoonRankBox");
				afternoonRankBox.select("3");
				TextBox nameField = window.getTextBox("nameField");
				assertEquals("Denise", nameField.getText());
				TextBox iaHours = window.getTextBox("iaHours");
				iaHours.setText("15");
				TextBox ecHours = window.getTextBox("ecHours");
				ecHours.setText("24");
				ListBox timeAway = window.getListBox("timeAway");
				timeAway.select("Unspecified: Sunday, March 1, 2015 to Tuesday, March 3, 2015");
				Button removeTimeAwayButton = window.getButton("removeTimeAwayButton");
				removeTimeAwayButton.click();
				ListBox commitments = window.getListBox("commitments");
				commitments.select("Meeting: Some commitment on Thursday, April 9, 2015 from 11 to 12");
				Button removeCommitmentButton = window.getButton("removeCommitmentButton");
				removeCommitmentButton.click();
				Button submitButton = window.getButton("submitButton");
				WindowInterceptor
				.init(submitButton.triggerClick())
				.process(new WindowHandler() {
					@Override
					public Trigger process(Window arg0) throws Exception {
						assertEquals(arg0.getTitle(), "Update clinician preferences");
						return arg0.getButton("YES").triggerClick();
					}
				})
				.run();
				return new Trigger() {
					@Override
			        public void run() throws Exception {
						((JFrame)window.getAwtContainer()).dispatchEvent(new WindowEvent((JFrame)window.getAwtContainer(), WindowEvent.WINDOW_CLOSING));
			        }
				};
			}
		})
		.run();
		CommitmentsDAO commitmentsDAO = new CommitmentsDAO(conn);
		TimeAwayDAO timeAwayDAO = new TimeAwayDAO(conn);
		ClinicianPreferencesDAO clinicianPreferencesDAO = new ClinicianPreferencesDAO(conn);
		List<CommitmentBean> cmtList = commitmentsDAO.loadCommitments(4);
		List<TimeAwayBean> timesAway = timeAwayDAO.loadTimeAway(4);
		ClinicianPreferencesBean preferences = clinicianPreferencesDAO.loadClinicianPreferences(4);
		List<CommitmentBean> expectedCMTList = new ArrayList<CommitmentBean>();
		List<TimeAwayBean> expectedTimesAway = new ArrayList<TimeAwayBean>();
		ClinicianPreferencesBean expectedPreferences = new ClinicianPreferencesBean(4, 1, 2, 3, 15, 24);
		assertEquals(expectedCMTList, cmtList);
		assertEquals(expectedTimesAway, timesAway);
		assertEquals(expectedPreferences, preferences);
	}
	
	public void testUpdateWithMissingIAHours() throws ParseException, SQLException {
		gen.generateStandardDataset();
		Window window = this.getMainWindow();
		ListBox cliniciansBox = window.getListBox();
		cliniciansBox.select("Denise");
		Button editButton = window.getButton("editButton");
		WindowInterceptor
		.init(editButton.triggerClick())
		.process(new WindowHandler() {
			public Trigger process(final Window window) {
				assertEquals(window.getTitle(), "Clinician Input Form");
				TextBox iaHours = window.getTextBox("iaHours");
				iaHours.setText("");
				Button submitButton = window.getButton("submitButton");
				WindowInterceptor
				.init(submitButton.triggerClick())
				.process(new WindowHandler() {
					@Override
					public Trigger process(Window arg0) throws Exception {
						assertEquals(arg0.getTitle(), "Adding clinician ia preferences");
						return arg0.getButton("OK").triggerClick();
					}
				}).run();
				return new Trigger() {
					@Override
			        public void run() throws Exception {
						((JFrame)window.getAwtContainer()).dispatchEvent(new WindowEvent((JFrame)window.getAwtContainer(), WindowEvent.WINDOW_CLOSING));
			        }
				};
			}
		})
		.run();
	}
	
	public void testUpdateWithMissingECHours() throws ParseException, SQLException {
		gen.generateStandardDataset();
		Window window = this.getMainWindow();
		ListBox cliniciansBox = window.getListBox();
		cliniciansBox.select("Denise");
		Button editButton = window.getButton("editButton");
		WindowInterceptor
		.init(editButton.triggerClick())
		.process(new WindowHandler() {
			public Trigger process(final Window window) {
				assertEquals(window.getTitle(), "Clinician Input Form");
				TextBox ecHours = window.getTextBox("ecHours");
				ecHours.setText("");
				Button submitButton = window.getButton("submitButton");
				WindowInterceptor
				.init(submitButton.triggerClick())
				.process(new WindowHandler() {
					@Override
					public Trigger process(Window arg0) throws Exception {
						assertEquals(arg0.getTitle(), "Adding clinician ec preferences");
						return arg0.getButton("OK").triggerClick();
					}
				}).run();
				return new Trigger() {
					@Override
			        public void run() throws Exception {
						((JFrame)window.getAwtContainer()).dispatchEvent(new WindowEvent((JFrame)window.getAwtContainer(), WindowEvent.WINDOW_CLOSING));
			        }
				};
			}
		})
		.run();
	}
	
	public void testLoadEmptyPreferences() throws ParseException, SQLException {
		gen.generateStandardDataset();
		Window window = this.getMainWindow();
		ListBox cliniciansBox = window.getListBox();
		cliniciansBox.select("Eric");
		Button editButton = window.getButton("editButton");
		WindowInterceptor
		.init(editButton.triggerClick())
		.process(new WindowHandler() {
			public Trigger process(final Window window) {
				assertEquals(window.getTitle(), "Clinician Input Form");
				ComboBox morningRankBox = window.getComboBox("morningRankBox");
				morningRankBox.selectionEquals("2");
				ComboBox noonRankBox = window.getComboBox("noonRankBox");
				noonRankBox.selectionEquals("1");
				ComboBox afternoonRankBox = window.getComboBox("afternoonRankBox");
				afternoonRankBox.selectionEquals("3");
				TextBox nameField = window.getTextBox("nameField");
				assertEquals("Eric", nameField.getText());
				TextBox iaHours = window.getTextBox("iaHours");
				assertEquals("35", iaHours.getText());
				TextBox ecHours = window.getTextBox("ecHours");
				assertEquals("44", ecHours.getText());
				ListBox timeAway = window.getListBox("timeAway");
				timeAway.contentEquals("");
				ListBox commitments = window.getListBox("commitments");
				commitments.contentEquals("");
				return new Trigger() {
					@Override
			        public void run() throws Exception {
						((JFrame)window.getAwtContainer()).dispatchEvent(new WindowEvent((JFrame)window.getAwtContainer(), WindowEvent.WINDOW_CLOSING));
			        }
				};
			}
		})
		.run();
	}
}
