package runner;

import java.sql.SQLException;

import gui.clinician.ClinicianForm;

import javax.swing.UIManager;

import bean.CalendarBean;
import dao.CalendarDAO;
import dao.ConnectionFactory;

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
		try {
			CalendarDAO calendarDAO = new CalendarDAO(ConnectionFactory.getInstance());
			CalendarBean calendarBean = calendarDAO.loadCalendar();
			new ClinicianForm(calendarBean.getSemester(), calendarBean.getYear(), calendarBean.getStartDate(), calendarBean.getEndDate());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
