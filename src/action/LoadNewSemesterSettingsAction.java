package action;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import bean.CalendarBean;
import bean.HolidayBean;
import dao.CalendarDAO;
import dao.HolidayDAO;

/**
 * The Class LoadNewSemesterSettingsAction handles loading New Semester Settings
 * data from the database for display in the AdminApplication.
 */
public class LoadNewSemesterSettingsAction {

	/** The calendar dao. */
	private CalendarDAO calendarDAO;

	/** The holiday dao. */
	private HolidayDAO holidayDAO;

	/**
	 * Instantiates a new load new semester settings action.
	 *
	 * @param conn
	 *            the conn
	 */
	public LoadNewSemesterSettingsAction(Connection conn) {
		calendarDAO = new CalendarDAO(conn);
		holidayDAO = new HolidayDAO(conn);
	}

	/**
	 * Load holidays.
	 *
	 * @return the list
	 * @throws SQLException
	 *             the SQL exception
	 */
	public List<HolidayBean> loadHolidays() throws SQLException {
		return holidayDAO.loadHolidays();
	}

	/**
	 * Load calendar.
	 *
	 * @return the calendar bean
	 * @throws SQLException
	 *             the SQL exception
	 */
	public CalendarBean loadCalendar() throws SQLException {
		return calendarDAO.loadCalendar();
	}
}
