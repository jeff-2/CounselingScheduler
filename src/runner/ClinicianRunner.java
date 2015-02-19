package runner;

import javax.swing.JFrame;

import gui.InputFormPanel;

/**
 * 
 * @author Yusheng Hou and Kevin Lim
 *
 */
public class ClinicianRunner {

	public static void main(String[] args) {
		JFrame frame = new JFrame("Input Form");
		frame.add(new InputFormPanel());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(500, 500);
		frame.setVisible(true);
	}

}
