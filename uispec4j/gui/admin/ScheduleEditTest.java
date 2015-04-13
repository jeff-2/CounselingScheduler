package gui.admin;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
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
import bean.Utility;
import dao.ClinicianDAO;
import dao.ConnectionFactory;

/**
 * Tests schedule edits
 * @author Yusheng, Denise
 *
 */
public class ScheduleEditTest extends UISpecTestCase {

	private Connection con;

	/* (non-Javadoc)
	 * @see org.uispec4j.UISpecTestCase#setUp()
	 */
	protected void setUp() throws Exception {
		setAdapter(new MainClassAdapter(AdminApplicationRunner.class, new String[0]));
		con = ConnectionFactory.getInstance();
		clearHolidayTable();
		clearCalendarTable();
	}
	
	protected void tearDown() throws SQLException {
		clearHolidayTable();
		clearCalendarTable();
	}
	
	public void testFirstECSelection() throws SQLException {
		editECSchedule("0");
	}
	
	public void testSecondECSelection() throws SQLException {
		editECSchedule("1");
	}
	
	public void testThirdECSelection() throws SQLException {
		editECSchedule("2");
	}
	
	public void testRemoveClinicianIAScheduleWeekA() {
		testRemoveClinicianIAScheduleWeektype("A");
	}
	
	public void testRemoveClinicianIAScheduleWeekB() {
		testRemoveClinicianIAScheduleWeektype("B");
	}
	
	public void testAddClinicianIAScheduleWeekA() throws SQLException {
		testAddClinicianIAScheduleWeektypeQ("A");
	}
	
	public void testAddClinicianIAScheduleWeekB() throws SQLException {
		testAddClinicianIAScheduleWeektypeQ("B");
	}
	
	public void testAddClinicianIAScheduleWeektypeQ(String weekType) throws SQLException {
		Window window = this.getMainWindow();
		assertEquals("Select Admin Task", window.getTitle());
		
		RadioButton generateSchedule = window.getRadioButton("Generate schedule");
		generateSchedule.click();
		Button runTask = window.getButton("Run task");
		runTask.click();
		
		RadioButton editIA = window.getRadioButton("Edit/print IA schedule");
		editIA.click();
		Window iaSchedule = WindowInterceptor.run(runTask.triggerClick());
		assertEquals("View IA Schedule", iaSchedule.getTitle());
		
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
	
	public void testRemoveClinicianIAScheduleWeektype(String weekType) {
		Window window = this.getMainWindow();
		assertEquals("Select Admin Task", window.getTitle());
		
		RadioButton generateSchedule = window.getRadioButton("Generate schedule");
		generateSchedule.click();
		Button runTask = window.getButton("Run task");
		runTask.click();
		
		RadioButton editIA = window.getRadioButton("Edit/print IA schedule");
		editIA.click();
		Window iaSchedule = WindowInterceptor.run(runTask.triggerClick());
		assertEquals("View IA Schedule", iaSchedule.getTitle());
		
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
	 * Tests whether selecting new name in the EC schedule works
	 * @throws SQLException
	 */
	public void editECSchedule(String boxnum) throws SQLException {
		Window window = this.getMainWindow();
		assertEquals("Select Admin Task", window.getTitle());
		
		RadioButton generateSchedule = window.getRadioButton("Generate schedule");
		generateSchedule.click();
		Button runTask = window.getButton("Run task");
		runTask.click();
		
		RadioButton editEC = window.getRadioButton("Edit/print EC schedule");
		editEC.click();
		Window ecSchedule = WindowInterceptor.run(runTask.triggerClick());
		assertEquals("View EC Schedule", ecSchedule.getTitle());
		
		ComboBox cb = ecSchedule.getComboBox(boxnum);
		cb.select("Alice");
		cb.selectionEquals("Alice");
		cb.select("Yusheng");
		cb.selectionEquals("Yusheng");

		ComboBox cb1 = ecSchedule.getComboBox(boxnum);
		cb.select("Yusheng");
		cb.selectionEquals("Yusheng");
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
