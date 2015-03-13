package runner;

import gui.admin.ClinicianIDListEditor;

import javax.swing.UIManager;

/**
 * Tester class to create a simple clinician ID list GUI frame.
 * 
 * @author ramusa2
 * @author dtli2
 */
public class ClinicianIDListRunner {

	/**
	 * Create an empty GUI frame 
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(
					UIManager.getSystemLookAndFeelClassName());
		}
		catch(Exception e) {
			System.out.println("Can't set system look and feel. Using default.");
		}
		ClinicianIDListEditor editor = new ClinicianIDListEditor();
	}

}
