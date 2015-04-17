package runner;

import gui.clinician.ClinicianForm;

import java.sql.SQLException;

import javax.swing.UIManager;

import bean.CalendarBean;
import bean.DateRange;
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
			new ClinicianForm(calendarBean.getSemester(), calendarBean.getYear(), new DateRange(calendarBean.getStartDate(), calendarBean.getEndDate()), false, "");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
