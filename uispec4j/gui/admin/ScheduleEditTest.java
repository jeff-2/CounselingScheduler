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
import org.uispec4j.RadioButton;
import org.uispec4j.Trigger;
import org.uispec4j.UISpecTestCase;
import org.uispec4j.Window;
import org.uispec4j.interception.MainClassAdapter;
import org.uispec4j.interception.PopupMenuInterceptor;
import org.uispec4j.interception.WindowHandler;
import org.uispec4j.interception.WindowInterceptor;

import runner.AdminApplicationRunner;
import bean.IAWeektype;
import bean.Utility;
import dao.ClinicianDAO;
import dao.ConnectionFactory;

/**
 * Tests schedule edits
 * @author Yusheng, Denise
 *
 */
public class ScheduleEditTest extends UISpecTestCase {

	private TestDataGenerator gen;
	private Connection con;

	/* (non-Javadoc)
	 * @see org.uispec4j.UISpecTestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		setAdapter(new MainClassAdapter(AdminApplicationRunner.class, new String[0]));
		con = ConnectionFactory.getInstance();
		gen = new TestDataGenerator(con);
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
		gen.clearTables();
	}
	
	public void testFirstECSelection() throws SQLException {
		testEditECSchedule("0");
	}
	
	public void testSecondECSelection() throws SQLException {
		testEditECSchedule("1");
	}
	
	public void testThirdECSelection() throws SQLException {
		testEditECSchedule("2");
	}
	
	public void testRemoveClinicianIAScheduleWeekA() {
		testRemoveClinicianIAScheduleWeektype(IAWeektype.A);
	}
	
	public void testRemoveClinicianIAScheduleWeekB() {
		testRemoveClinicianIAScheduleWeektype(IAWeektype.B);
	}
	
	public void testAddClinicianIAScheduleWeekA() throws SQLException {
		testAddClinicianIAScheduleWeektype(IAWeektype.A);
	}
	
	public void testAddClinicianIAScheduleWeekB() throws SQLException {
		testAddClinicianIAScheduleWeektype(IAWeektype.B);
	}
	
	public void testAddClinicianIAScheduleWeektype(IAWeektype weekType) throws SQLException {
		Window iaSchedule = navigateScheduleWindow("IA");
		
		ListBox jList = iaSchedule.getListBox("JList" + weekType + "0");
		ClinicianDAO dao = new ClinicianDAO(ConnectionFactory.getInstance());
		Vector<String> clinicianNames = dao.loadClinicianNames();
		@SuppressWarnings("unchecked")
		JList<String> list = (JList<String>)jList.getAwtComponent();
		DefaultListModel<String> model = (DefaultListModel<String>)list.getModel();
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
		.init(PopupMenuInterceptor
				.run(jList.triggerRightClick(0))
			 	.getSubMenu("Add Clinician")
			 	.triggerClick())
		.process(new WindowHandler() {
			public Trigger process(Window window) {
				ComboBox box = window.getComboBox();
				box.select(c);
				return window.getButton("OK").triggerClick();
			}
		})
	    .run();
		assertTrue(jList.contains(c));
	}

	public void testRemoveClinicianIAScheduleWeektype(IAWeektype weekType) {
		Window iaSchedule = navigateScheduleWindow("IA");
		
		ListBox jList = iaSchedule.getListBox("JList" + weekType + "0");
		@SuppressWarnings("unchecked")
		JList<String> list = (JList<String>)jList.getAwtComponent();
		DefaultListModel<String> model = (DefaultListModel<String>)list.getModel();
		List<String> l = Utility.toStringList(model);
		String clinicianZero = l.get(0);
		
		PopupMenuInterceptor
		 	.run(jList.triggerRightClick(0))
		 	.getSubMenu("Remove Clinician")
		 	.click();
		assertFalse(jList.contains(clinicianZero));
	}
	
	/**
	 * Go to the a schedule window
	 * @return
	 */
	public Window navigateScheduleWindow(String scheduleType) {
		Window window = this.getMainWindow();
		assertEquals("Select Admin Task", window.getTitle());
		
		RadioButton generateSchedule = window.getRadioButton("Generate schedule");
		generateSchedule.click();
		Button runTask = window.getButton("Run task");
		runTask.click();
		
		RadioButton edit = window.getRadioButton("Edit/print " + scheduleType + " schedule");
		edit.click();
		Window schedule = WindowInterceptor.run(runTask.triggerClick());
		assertEquals("View "+ scheduleType + " Schedule", schedule.getTitle());
		return schedule;
	}

	/**
	 * Tests whether selecting new name in the EC schedule works
	 * @throws SQLException
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
	 * Tests whether reseting the EC schedule after making unsaved edits reverts back to original version
	 * @throws SQLException
	 */
	public void testResetECSchedule() throws SQLException {
		
		Window ecSchedule = navigateScheduleWindow("EC");
		
		ComboBox cb[] = new ComboBox[3];
		int selected[] = new int[3];
		
		for (int i = 0; i < cb.length; i++) {
			cb[i] = ecSchedule.getComboBox("" + i);
			selected[i] = cb[i].getAwtComponent().getSelectedIndex();
			cb[i].select("Alice");
		}
		
		Button reset = ecSchedule.getButton("Reset");
		reset.click();
		
		for (int i = 0; i < cb.length; i++) {
			cb[i] = ecSchedule.getComboBox("" + i);
			assertEquals(selected[i], cb[i].getAwtComponent().getSelectedIndex());
		}
	}

	/**
	 * Tests whether reseting the IA schedule after making unsaved edits reverts back to the original version
	 * @throws SQLException
	 */
	public void testResetIASchedule() throws SQLException {
		Window iaSchedule = navigateScheduleWindow("IA");
		
		ListBox jList = iaSchedule.getListBox("JList" + IAWeektype.A + "0");
		@SuppressWarnings("unchecked")
		JList<String> list = (JList<String>)jList.getAwtComponent();
		DefaultListModel<String> model = (DefaultListModel<String>)list.getModel();
		List<String> l = Utility.toStringList(model);
		String clinicianZero = l.get(0);
		
		PopupMenuInterceptor
		 	.run(jList.triggerRightClick(0))
		 	.getSubMenu("Remove Clinician")
		 	.click();
		assertFalse(jList.contains(clinicianZero));
		
		Button reset = iaSchedule.getButton("Reset");
		reset.click();
		jList = iaSchedule.getListBox("JList" + IAWeektype.A + "0");
		assertTrue(jList.contains(clinicianZero));
	}
}
