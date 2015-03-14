package runner;

import gui.admin.NewSemesterSettings;

import javax.swing.UIManager;

/**
 * Creates an instance of a NewSemesterSettings which sets up a JFrame and runs.
 * 
 * @author jmfoste2
 * @author nbeltr2
 */
public class NewSemesterSettingsRunner {
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(
					UIManager.getSystemLookAndFeelClassName());
		}
		catch(Exception e) {
			System.out.println("Can't set system look and feel. Using default.");
		}
		new NewSemesterSettings();
	}
}
