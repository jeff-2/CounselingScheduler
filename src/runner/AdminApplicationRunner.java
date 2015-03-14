package runner;

import gui.admin.AdminTaskSelector;

import javax.swing.UIManager;

import utils.Logger;

/**
 * Launcher for the admin application (currently bare-bones)
 * 
 * @author ramusa2, jmfoste2
 *
 */
public class AdminApplicationRunner {
	
	// TODO: should we create a single connection object, or recreate one for each subroutine?

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(
					UIManager.getSystemLookAndFeelClassName());
		}
		catch(Exception e) {
			System.out.println("Can't set system look and feel. Using default.");
		}
		// Set up logger
		Logger.initialize("admin");
		Logger.logln("Started admin application.");
		
		// TODO: Read in configuration parameters from file
		// e.g. SQLDB IP address/port info, log file location, etc.
		
		// Start admin's task selector GUI
		new AdminTaskSelector();
	}

}
