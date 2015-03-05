package runner;

import javax.swing.JFrame;
import javax.swing.UIManager;

import gui.InputFormPanel;

/**
 * 
 * @author Yusheng Hou and Kevin Lim
 *
 */
public class ClinicianRunner {

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(
					UIManager.getSystemLookAndFeelClassName());
		}
		catch(Exception e) {
			System.out.println("Can't set system look and feel. Using default.");
		}
		JFrame frame = new JFrame("Input Form");
		frame.add(new InputFormPanel());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(600, 600);
		frame.setVisible(true);
	}

}
