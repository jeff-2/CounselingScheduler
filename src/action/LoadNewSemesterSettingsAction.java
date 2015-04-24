package action;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import bean.CalendarBean;
import bean.HolidayBean;
import dao.CalendarDAO;
import dao.HolidayDAO;

public class LoadNewSemesterSettingsAction {

	private CalendarDAO calendarDAO;
	private HolidayDAO holidayDAO;
	
	public LoadNewSemesterSettingsAction(Connection conn) {
		calendarDAO = new CalendarDAO(conn);
		holidayDAO = new HolidayDAO(conn);
	}
	
	public List<HolidayBean> loadHolidays() throws SQLException {
		return holidayDAO.loadHolidays();
	}
	
	public CalendarBean loadCalendar() throws SQLException {
		return calendarDAO.loadCalendar();
	}
}
