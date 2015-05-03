package gui.admin;

import generator.TestDataGenerator;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JList;

import org.uispec4j.Button;
import org.uispec4j.ComboBox;
import org.uispec4j.ListBox;
import org.uispec4j.MenuBar;
import org.uispec4j.MenuItem;
import org.uispec4j.Panel;
import org.uispec4j.TabGroup;
import org.uispec4j.Trigger;
import org.uispec4j.UISpecTestCase;
import org.uispec4j.Window;
import org.uispec4j.interception.MainClassAdapter;
import org.uispec4j.interception.PopupMenuInterceptor;
import org.uispec4j.interception.WindowHandler;
import org.uispec4j.interception.WindowInterceptor;

import runner.AdminApplicationRunner;
import utils.Utility;
import bean.IAWeektype;
import dao.ClinicianDAO;
import dao.ConnectionFactory;

/**
 * The Class ScheduleEditTest tests the edit functionality for both IA and EC
 * schedules.
 * 
 * @author Yusheng, Denise
 *
 */
public class ScheduleEditTest extends UISpecTestCase {

	/** The gen. */
	private TestDataGenerator gen;

	/** The con. */
	private Connection con;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.uispec4j.UISpecTestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		setAdapter(new MainClassAdapter(AdminApplicationRunner.class,
				new String[0]));
		con = ConnectionFactory.getInstance();
		gen = new TestDataGenerator(con);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.uispec4j.UISpecTestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		gen.clearTables();
	}

	/**
	 * Test first EC selection.
	 *
	 * @throws SQLException
	 *             the SQL exception
	 */
	public void testFirstECSelection() throws SQLException {
		testEditECScheduleCatchWindow("0");
	}

	/**
	 * Test second EC selection.
	 *
	 * @throws SQLException
	 *             the SQL exception
	 */
	public void testSecondECSelection() throws SQLException {
		testEditECScheduleCatchWindow("1");
	}

	/**
	 * Test third EC selection.
	 *
	 * @throws SQLException
	 *             the SQL exception
	 */
	public void testThirdECSelection() throws SQLException {
		testEditECScheduleCatchWindow("2");
	}

	/**
	 * Test remove clinician IA schedule week a.
	 */
	public void testRemoveClinicianIAScheduleWeekA() {
		testRemoveClinicianIAScheduleWeektype(IAWeektype.A);
	}

	/**
	 * Test remove clinician IA schedule week b.
	 */
	public void testRemoveClinicianIAScheduleWeekB() {
		testRemoveClinicianIAScheduleWeektype(IAWeektype.B);
	}

	/**
	 * Test add clinician IA schedule week a.
	 *
	 * @throws SQLException
	 *             the SQL exception
	 */
	public void testAddClinicianIAScheduleWeekA() throws SQLException {
		testAddClinicianIAScheduleWeektype(IAWeektype.A);
	}

	/**
	 * Test add clinician IA schedule week b.
	 *
	 * @throws SQLException
	 *             the SQL exception
	 */
	public void testAddClinicianIAScheduleWeekB() throws SQLException {
		testAddClinicianIAScheduleWeektype(IAWeektype.B);
	}

	/**
	 * Test add clinician IA schedule weektype.
	 *
	 * @param weekType
	 *            the week type
	 * @throws SQLException
	 *             the SQL exception
	 */
	public void testAddClinicianIAScheduleWeektype(IAWeektype weekType)
			throws SQLException {
		Window iaSchedule = navigateScheduleWindow("IA");

		ListBox jList = iaSchedule.getListBox(IAWeektype.A + "-0-" + "11");
		ClinicianDAO dao = new ClinicianDAO(ConnectionFactory.getInstance());
		Vector<String> clinicianNames = dao.loadClinicianNames();
		@SuppressWarnings("unchecked")
		JList<String> list = (JList<String>) jList.getAwtComponent();
		DefaultListModel<String> model = (DefaultListModel<String>) list
				.getModel();
		List<String> l = Utility.toStringList(model);
		String clinician = null;
		for (String name : clinicianNames) {
			if (!l.contains(name)) {
				clinician = name;
				break;
			}
		}
		final String c = clinician;

		WindowInterceptor
				.init(PopupMenuInterceptor.run(jList.triggerRightClick(0))
						.getSubMenu("Add Clinician").triggerClick())
				.process(new WindowHandler() {
					public Trigger process(Window window) {
						ComboBox box = window.getComboBox();
						box.select(c);
						return window.getButton("OK").triggerClick();
					}
				}).run();
		assertTrue(jList.contains(c));
	}

	/**
	 * Test remove clinician IA schedule weektype.
	 *
	 * @param weekType
	 *            the week type
	 */
	public void testRemoveClinicianIAScheduleWeektype(IAWeektype weekType) {
		Window iaSchedule = navigateScheduleWindow("IA");

		ListBox jList = iaSchedule.getListBox(IAWeektype.A + "-0-" + "11");
		@SuppressWarnings("unchecked")
		JList<String> list = (JList<String>) jList.getAwtComponent();
		DefaultListModel<String> model = (DefaultListModel<String>) list
				.getModel();
		List<String> l = Utility.toStringList(model);
		String clinicianZero = l.get(0);

		PopupMenuInterceptor.run(jList.triggerRightClick(0))
				.getSubMenu("Remove Clinician").click();
		assertFalse(jList.contains(clinicianZero));
	}

	/**
	 * Navigate to the schedule window.
	 *
	 * @param scheduleType
	 *            the schedule type
	 * @return the window
	 */
	public Window navigateScheduleWindow(String scheduleType) {
		Window window = this.getMainWindow();

		MenuBar menuBar = window.getMenuBar();
		MenuItem menu = menuBar.getMenu("File");
		MenuItem generate = menu.getSubMenu("Generate Schedule");
		generate.click();

		TabGroup tabbedPane = window.getTabGroup("tabbedPane");
		if (scheduleType.equals("IA")) {
			tabbedPane.selectTab("IA Schedule");
		} else if (scheduleType.equals("EC")) {
			tabbedPane.selectTab("EC Schedule");
		}

		return window;
	}

	/**
	 * Test edit EC schedule and catch the constraints broken notification
	 * window.
	 *
	 * @param boxnum
	 *            the boxnum
	 */
	public void testEditECScheduleCatchWindow(final String boxnum) {
		final Window ecSchedule = navigateScheduleWindow("EC");

		WindowInterceptor.init(new Trigger() {
			public void run() throws Exception {
				ComboBox cb = ecSchedule.getComboBox(boxnum);
				cb.select("Alice");
				cb.selectionEquals("Alice");
			}
		}).process(new WindowHandler() {
			public Trigger process(Window errorMessage) {
				return errorMessage.getButton("OK").triggerClick();
			}
		}).run();
	}

	/**
	 * Tests whether selecting new name in the EC schedule works.
	 *
	 * @param boxnum
	 *            the boxnum
	 * @throws SQLException
	 *             the SQL exception
	 */
	public void testEditECSchedule(String boxnum) throws SQLException {
		Window ecSchedule = navigateScheduleWindow("EC");

		ComboBox cb = ecSchedule.getComboBox(boxnum);
		cb.select("Alice");
		cb.selectionEquals("Alice");
		cb.select("Yusheng");
		cb.selectionEquals("Yusheng");
	}

	/**
	 * Tests whether reseting the EC schedule after making unsaved edits reverts
	 * back to original version.
	 *
	 * @throws SQLException
	 *             the SQL exception
	 */
	public void testResetECSchedule() throws SQLException {

		final Window ecSchedule = navigateScheduleWindow("EC");
		Panel ecPanel = ecSchedule.getPanel("ECScheduleFrame");

		ComboBox cb[] = new ComboBox[3];
		int selected[] = new int[3];

		for (int i = 0; i < cb.length; i++) {
			cb[i] = ecSchedule.getComboBox("" + i);
			selected[i] = cb[i].getAwtComponent().getSelectedIndex();
			final ComboBox current = cb[i];
			WindowInterceptor.init(new Trigger() {
				public void run() throws Exception {
					current.select("Alice");
				}
			}).process(new WindowHandler() {
				public Trigger process(Window errorMessage) {
					return errorMessage.getButton("OK").triggerClick();
				}
			}).run();
		}

		Button reset = ecPanel.getButton("Reset");
		reset.click();

		for (int i = 0; i < cb.length; i++) {
			cb[i] = ecSchedule.getComboBox("" + i);
			assertEquals(selected[i], cb[i].getAwtComponent()
					.getSelectedIndex());
		}
	}

	/**
	 * Tests whether reseting the IA schedule after making unsaved edits reverts
	 * back to the original version.
	 *
	 * @throws SQLException
	 *             the SQL exception
	 */
	public void testResetIASchedule() throws SQLException {
		Window iaSchedule = navigateScheduleWindow("IA");
		Panel iaPanel = iaSchedule.getPanel("IAScheduleFrame");

		ListBox jList = iaSchedule.getListBox(IAWeektype.A + "-0-" + "11");
		@SuppressWarnings("unchecked")
		JList<String> list = (JList<String>) jList.getAwtComponent();
		DefaultListModel<String> model = (DefaultListModel<String>) list
				.getModel();
		List<String> l = Utility.toStringList(model);
		String clinicianZero = l.get(0);

		PopupMenuInterceptor.run(jList.triggerRightClick(0))
				.getSubMenu("Remove Clinician").click();
		assertFalse(jList.contains(clinicianZero));

		Button reset = iaPanel.getButton("Reset");
		reset.click();
		jList = iaSchedule.getListBox(IAWeektype.A + "-0-" + "11");
		assertTrue(jList.contains(clinicianZero));
	}
}
