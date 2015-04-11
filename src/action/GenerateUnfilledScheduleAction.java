package action;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import dao.CalendarDAO;
import dao.HolidayDAO;
import dao.SessionsDAO;
import bean.CalendarBean;
import bean.HolidayBean;
import bean.SessionBean;
import bean.SessionType;
import bean.Weekday;

/**
 * Abstract class for assigning clinicians to slots in an unfilled schedule
 * @author ramusa2
 *
 */
public class GenerateUnfilledScheduleAction {

	protected Connection conn;
	
	public GenerateUnfilledScheduleAction(Connection connection) {
		conn = connection;
	}

	/**
	 * Generates an unfilled schedule and (over)writes it to the database
	 */
	public void generateUnfilledSchedule() throws SQLException {
		CalendarDAO calDAO = new CalendarDAO(this.conn);
		CalendarBean calBean = calDAO.loadCalendar();
		SessionsDAO sessionsDAO = new SessionsDAO(this.conn);
		sessionsDAO.clearSessions();
		ArrayList<Date> workDays = this.getWorkDays(calBean);
		
		// TODO: actually reflect admin prefs (but works for now/development)
		int[] ecSlots = new int[]{8, 12, 16};
		int[] iaSlots = new int[]{11, 13, 14, 15};
		int ecClinicianMin = 1;
		int iaClinicianMin = 3;
		int weektype = 0;
		Calendar cal = Calendar.getInstance();
		for (Date d : workDays) {
			
			Weekday day = Weekday.getWeekday(d);
			for (int e : ecSlots) {
				SessionBean session = new SessionBean(sessionsDAO.getNextSessionID(), e, ecClinicianMin, day, d, SessionType.EC, new ArrayList<Integer>(), calBean.getSemester(), weektype);
				sessionsDAO.insertSession(session);
			}
			for (int i : iaSlots) {
				SessionBean session = new SessionBean(sessionsDAO.getNextSessionID(), i, iaClinicianMin, day, d, SessionType.IA, new ArrayList<Integer>(), calBean.getSemester(), weektype);
				sessionsDAO.insertSession(session);
			}
			
			int prevWeek = cal.get(Calendar.WEEK_OF_YEAR);
			cal.setTime(d);
			int week = cal.get(Calendar.WEEK_OF_YEAR);
			if (week > prevWeek) {
				weektype = 1 - weektype;
			}
		}
	}

	/**
	 * Returns the list of workdays for this semester
	 */
	private ArrayList<Date> getWorkDays(CalendarBean calendarInfo) throws SQLException {
		HashSet<Date> holidays = this.getHolidays();
		ArrayList<Date> dates = new ArrayList<Date>();
		Date start = calendarInfo.getStartDate();
		Date end = calendarInfo.getEndDate();
		ArrayList<Date> possibleDays = getDateRange(start, end);
		for (Date day : possibleDays) {
		    if (Weekday.isWeekday(day)) {
		    	if (holidays.isEmpty() || !holidays.contains(day)) {
		    		dates.add(day);
		    	}
		    }
		}
		return dates;
	}

	/**
	 * Return all holidays for this semester
	 */
	private HashSet<Date> getHolidays() throws SQLException {
		HashSet<Date> dates = new HashSet<Date>();
		HolidayDAO holidayDAO = new HolidayDAO(this.conn);
		List<HolidayBean> holidays = holidayDAO.loadHolidays();
		for (HolidayBean holiday : holidays) {
			Date start = holiday.getStartDate();
			Date end = holiday.getEndDate();
			for(Date d : getDateRange(start, end)) {
				dates.add(d);
			}
		}
		return dates;
	}
	
	/**
	 * Given start and end Dates, return a list of intervening Dates
	 */
	private static final ArrayList<Date> getDateRange(Date start, Date end) {
		ArrayList<Date> dates = new ArrayList<Date>();
		Calendar calStart = Calendar.getInstance();
		calStart.setTime(start);
		Calendar calEnd = Calendar.getInstance();
		calEnd.setTime(end);
		while (!calStart.after(calEnd)) {
		    Date day = calStart.getTime();
		    dates.add(day);
		    calStart.add(Calendar.DATE, 1);
		}
		return dates;
	}
}
