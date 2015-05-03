package runner;

import gui.clinician.ClinicianForm;

import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import bean.CalendarBean;
import bean.DateRange;
import dao.CalendarDAO;
import dao.ConnectionFactory;

/**
 * The Class ClinicianFormRunner runs the ClinicianForm application.
 *
 * @author Yusheng Hou and Kevin Lim
 */
public class ClinicianFormRunner {

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.out
					.println("Can't set system look and feel. Using default.");
		}
		CalendarBean calendarBean;
		try {
			CalendarDAO calendarDAO = new CalendarDAO(
					ConnectionFactory.getInstance());
			calendarBean = calendarDAO.loadCalendar();
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane
					.showMessageDialog(
							null,
							"Clinician Preferences cannot be set without existing semester settings",
							"Error Opening Clinician Preferences",
							JOptionPane.ERROR_MESSAGE);
			return;
		}
		JFrame frame = new JFrame("Clinician Input Form");
		ClinicianForm form = new ClinicianForm(calendarBean.getSemester(),
				calendarBean.getYear(),
				new DateRange(calendarBean.getStartDate(), calendarBean
						.getEndDate()), false, "");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(form);
		frame.pack();
		frame.setVisible(true);
	}
}
