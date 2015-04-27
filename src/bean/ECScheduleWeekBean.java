package bean;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import dao.CalendarDAO;
import dao.ConnectionFactory;
import dao.HolidayDAO;

/**
 * Stores the information for one week in the EC schedule
 * @author ramusa2, lim92
 *
 */
public class ECScheduleWeekBean implements Comparable<ECScheduleWeekBean> {

	/**
	 * EC timeslots
	 */
	public static final String[] timeslots = new String[]{"8:00", "Noon", "4:00"};

	/**
	 * List of the days for this week
	 */
	private ArrayList<ECScheduleDayBean> days;

	/**
	 * Private map from dates to index in week
	 */
	private HashMap<Date, Integer> dateMap;

	/**
	 * Default constructor
	 */
	private ECScheduleWeekBean(Date mondayDate) {
		days = new ArrayList<ECScheduleDayBean>();
		dateMap = new HashMap<Date, Integer>();
		Calendar c = Calendar.getInstance();
		c.setTime(mondayDate);
		for(int d = 0; d<5; d++) {
			Date date = c.getTime();
			dateMap.put(date,  d);
			String dayName = Weekday.dayName(date);
			days.add(new ECScheduleDayBean(date, dayName));
			c.add(Calendar.DATE, 1);
		}
	}

	/**
	 * Add a holiday to this week
	 */
	public void addHoliday(HolidayBean holiday) {
		Date date = holiday.getStartDate();
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		while(!date.after(holiday.getEndDate())) {
			Integer d = dateMap.get(date);
			if(d != null) {
				this.days.get(d).setHoliday(holiday);
			}
			c.add(Calendar.DATE, 1);
			date = c.getTime();
		}
	}

	/**
	 * Add a clinician to a day in this week
	 */
	public void addClinician(Date date, int timeslot, String clinician, int id) {
		Integer d = dateMap.get(date);
		if(d != null) {
			this.days.get(d).addClinician(clinician, timeslot, id);
		}
	}

	/**
	 * Returns this week's days (with data)
	 */
	public ArrayList<ECScheduleDayBean> days() {
		return days;
	}

	/**
	 * Returns this week's month abbreviation
	 */
	public String monthAbbrev() {
		SimpleDateFormat format = new SimpleDateFormat("MMM");
		return format.format(days.get(0).date());
	}

	@Override
	public int compareTo(ECScheduleWeekBean o) {
		return this.days.get(0).date().compareTo(o.days.get(0).date());
	}

	/**
	 * Factory method
	 * @throws SQLException 
	 */
	public static ArrayList<ECScheduleWeekBean> getECScheduleWeekBeans(Schedule schedule) throws SQLException {
		CalendarBean cal = new CalendarDAO(ConnectionFactory.getInstance()).loadCalendar();
		Calendar c = Calendar.getInstance();
		// Build weeks
		c.setFirstDayOfWeek(Calendar.MONDAY);
		HashMap<Date, ECScheduleWeekBean> weekMap = new HashMap<Date, ECScheduleWeekBean>();
		Date curDate = cal.getStartDate();
		curDate = getMondayDate(curDate);
		while(!curDate.after(cal.getEndDate())) {
			weekMap.put(curDate, new ECScheduleWeekBean(curDate));
			curDate = incrementDateByDays(curDate, 7);
		}
		// Add holidays
		addHolidays(weekMap, new HolidayDAO(ConnectionFactory.getInstance()));
		
		// Clinician sessions
		List<SessionNameBean> sessions = schedule.getECSessions();
		for(SessionNameBean bean : sessions) {
			Date date = bean.getDate();
			getWeek(date, weekMap).addClinician(date, bean.getStartTime(), bean.getClinicianName(), bean.getSessionID());
		}		

		// Sort weeks
		ArrayList<ECScheduleWeekBean> weeks = new ArrayList<ECScheduleWeekBean>();
		weeks.addAll(weekMap.values());
		Collections.sort(weeks);	
		
		return weeks;		
	}

	private static void addHolidays(HashMap<Date, ECScheduleWeekBean> weekMap,
			HolidayDAO dao) throws SQLException {
		for(HolidayBean bean : dao.loadHolidays()) {
			Date curDate = bean.getStartDate();
			while(!curDate.after(bean.getEndDate())) {
				getWeek(curDate, weekMap);
				curDate = incrementDateByDays(curDate, 7);
			}
		}
	}

	private static Date incrementDateByDays(Date date, int numDays) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DATE, numDays);
		return c.getTime();
	}

	private static ECScheduleWeekBean getWeek(Date date, HashMap<Date, ECScheduleWeekBean> weekMap) {
		return weekMap.get(getMondayDate(date));
	}

	private static Date getMondayDate(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		SimpleDateFormat format = new SimpleDateFormat("EEEEEEEEE");
		while(!format.format(date).equals("Monday")) {
			c.add(Calendar.DATE, -1);
			date = c.getTime();
		}
		return date;
	}

	private  ArrayList<ArrayList<String>> arrangeCellContent(boolean isName) {
		ArrayList<ArrayList<String>> entries = new ArrayList<ArrayList<String>>();
		for(int row = 0; row <4; row++) {
			entries.add(new ArrayList<String>());
		}

		String month = this.monthAbbrev();		
		String[] rowLabels = new String[]{month, "8:00", "Noon", "4:00"};
		entries.get(0).add(month);
		for(int d=0; d<5; d++) {
			entries.get(0).add(days.get(d).headerString());
		}
		for(int t=1; t<4; t++) {
			entries.get(t).add(rowLabels[t]);
			for(int d=0; d<5; d++) {
				ECScheduleDayBean dayBean = days.get(d);
				String name = "";
				if(dayBean.isHoliday()) {
					if(t==1) {
						name = dayBean.getHoliday().getName();
					}
				}
				else {
					if (isName) {
						name = dayBean.getClinicians()[t-1];
					}
					else {
						name = Integer.toString(dayBean.getSessionIDs()[t-1]);
					}
				}
				entries.get(t).add(name);
			}
		}
		
		return entries;
		
	}
	
	/**
	 * Returns two dimensional array representing the text to go into the calendar view of this week's schedule.
	 * @return
	 */
	public ArrayList<ArrayList<String>> getCellContent() {
		return arrangeCellContent(true);
	}

	/**
	 * Returns two dimensional array contaning the session ID corresponding to each session in the schedule
	 * @return
	 */
	public ArrayList<ArrayList<String>> getCellIDs() {
		return arrangeCellContent(false);
	}

}
