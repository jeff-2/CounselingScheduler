package gui.clinician;

import generator.TestDataGenerator;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.uispec4j.Button;
import org.uispec4j.Key;
import org.uispec4j.ListBox;
import org.uispec4j.TextBox;
import org.uispec4j.Trigger;
import org.uispec4j.UISpecTestCase;
import org.uispec4j.Window;
import org.uispec4j.interception.MainClassAdapter;
import org.uispec4j.interception.WindowHandler;
import org.uispec4j.interception.WindowInterceptor;

import bean.ClinicianBean;
import runner.ClinicianIDListRunner;
import dao.ClinicianDAO;
import dao.ConnectionFactory;

public class ClinicianIDListEditorTest extends UISpecTestCase {

	private Connection conn;
	private ClinicianDAO clinicianDAO;
	private TestDataGenerator gen;
	private ClinicianBean beanA, beanB;
	
	@Before
	protected void setUp() throws Exception {
		super.setUp();
		setAdapter(new MainClassAdapter(ClinicianIDListRunner.class, new String[0]));
		
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
}
