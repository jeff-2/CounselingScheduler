package runner;

import generator.TestDataGenerator;
import gui.admin.AdminApplication;

import java.sql.SQLException;
import java.text.ParseException;

import javax.swing.UIManager;

/**
 * The Class AdminApplicationRunner runs the AdminApplication with the database
 * populated with default test data.
 */
public class AdminApplicationRunner {

	/**
	 * Runs the admin application with test data.
	 *
	 * @param args
	 *            the arguments
	 * @throws ParseException, SQLException
	 */
	public static void main(String[] args) throws ParseException, SQLException {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.out
					.println("Can't set system look and feel. Using default.");
		}
		TestDataGenerator.overwriteAndFillDemoData();
		new AdminApplication();
	}
}
