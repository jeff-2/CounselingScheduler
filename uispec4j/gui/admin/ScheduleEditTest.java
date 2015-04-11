package gui.admin;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.uispec4j.Button;
import org.uispec4j.ComboBox;
import org.uispec4j.ListBox;
import org.uispec4j.Mouse;
import org.uispec4j.RadioButton;
import org.uispec4j.Trigger;
import org.uispec4j.UISpecTestCase;
import org.uispec4j.Window;
import org.uispec4j.interception.MainClassAdapter;
import org.uispec4j.interception.WindowInterceptor;

import runner.AdminApplicationRunner;
import runner.NewSemesterSettingsRunner;
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
	
	public void testFourthECSelection() throws SQLException {
		editECSchedule("3");
	}
	
	public void testFifthECSelection() throws SQLException {
		editECSchedule("4");
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
