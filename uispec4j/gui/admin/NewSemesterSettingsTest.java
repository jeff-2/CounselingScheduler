package gui.admin;

import org.uispec4j.Button;
import org.uispec4j.ListBox;
import org.uispec4j.TextBox;
import org.uispec4j.UISpecTestCase;
import org.uispec4j.Window;
import org.uispec4j.interception.MainClassAdapter;

/**
 * The Class NewSemesterSettingsTest.
 * 
 * @author jmfoste2
 * @author nbeltr2
 */
public class NewSemesterSettingsTest extends UISpecTestCase {
	
	
	/* (non-Javadoc)
	 * @see org.uispec4j.UISpecTestCase#setUp()
	 */
	protected void setUp() throws Exception {
		setAdapter(new MainClassAdapter(NewSemesterSettingsRunner.class, new String[0]));
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
}
