package runner;

import javax.swing.UIManager;

import gui.clinician.ClinicianForm;

/**
 * 
 * @author Yusheng Hou and Kevin Lim
 *
 */
public class ClinicianFormRunner {

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(
					UIManager.getSystemLookAndFeelClassName());
		}
		catch(Exception e) {
			System.out.println("Can't set system look and feel. Using default.");
		}
		new ClinicianForm();
	}

}
