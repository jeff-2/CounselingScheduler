package gui;

import org.uispec4j.Button;
import org.uispec4j.TextBox;
import org.uispec4j.UISpecTestCase;
import org.uispec4j.Window;
import org.uispec4j.interception.MainClassAdapter;

import runner.ClinicianRunner;

public class InputFormPanelTest extends UISpecTestCase {

	protected void setUp() throws Exception {
		setAdapter(new MainClassAdapter(ClinicianRunner.class, new String[0]));
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
}
