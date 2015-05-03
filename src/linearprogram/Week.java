package linearprogram;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import bean.CalendarBean;
import bean.IAWeektype;

/**
 * Caches the available days of a semester into weeks
 */
public class Week implements Comparable<Week> {
	private static HashMap<Week, Week> weekCache;
	private static HashMap<Date, Week> dayToWeekCache;

	private Date start, end;
	private IAWeektype type;
	private int orderInSemester;

	private Week(Date s, Date e, int orderInSemester) {
		this.start = s;
		this.end = e;
		this.orderInSemester = orderInSemester;
		this.type = getWeekType(orderInSemester);
	}

	private static IAWeektype getWeekType(int orderInSemester) {
		return (orderInSemester % 2 == 0) ? IAWeektype.A : IAWeektype.B;
	}

	/**
	 * Gets the week type
	 * 
	 * @return type of week A or B
	 */
	public IAWeektype getWeektype() {
		return type;
	}

	/**
	 * Gets the week based on the day and calendar given. Generates cache of
	 * days to week if not yet generated.
	 * 
	 * @param day
	 * 			the day as a Date
	 * @param calendar
	 * 			the calendar
	 * @return Week
	 */
	public synchronized static Week getWeek(Date day, CalendarBean calendar) {
		if (dayToWeekCache == null) {
			buildCache(calendar);
		}
		return dayToWeekCache.get(day);
	}

	/**
	 * Gets the list of weeks in a semester
	 * 
	 * @param calendarBean
	 * 			the calendarBean
	 * @return list of weeks for the semester
	 */
	public static List<Week> getSemesterWeeks(CalendarBean calendarBean) {
		buildCache(calendarBean);
		List<Week> weeks = new ArrayList<Week>();
		weeks.addAll(weekCache.keySet());
		Collections.sort(weeks);
		return weeks;
	}

	private static void buildCache(CalendarBean calendarBean) {
		if (dayToWeekCache == null || weekCache == null) {
			dayToWeekCache = new HashMap<Date, Week>();
			weekCache = new HashMap<Week, Week>();
		}
		Date prevMonday = calendarBean.getStartDate();
		Date currentDate = calendarBean.getStartDate();
		Date end = calendarBean.getEndDate();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(currentDate);
		int numWeeks = 0;
		while (currentDate.before(end)) {
			ArrayList<Date> days = new ArrayList<Date>();
			while (calendar.get(Calendar.DAY_OF_WEEK) != 6) {
				days.add(currentDate);
				calendar.add(Calendar.DAY_OF_WEEK, 1);
				currentDate = calendar.getTime();
			}
			days.add(currentDate);
			calendar.add(Calendar.DAY_OF_WEEK, 1);
			currentDate = calendar.getTime();
			// CurrentDate is now Monday
			Week week = new Week(prevMonday, currentDate, numWeeks);
			Week temp = weekCache.get(week);
			if (temp != null) {
				week = temp;
			}
			weekCache.put(week, week);
			for (Date d : days) {
				dayToWeekCache.put(d, week);
			}
			numWeeks++;
			calendar.add(Calendar.DAY_OF_WEEK, 1);
			currentDate = calendar.getTime();
			prevMonday = currentDate;
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return this.start.hashCode();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object oth) {
		if (!(oth instanceof Week)) {
			return this == oth;
		}
		Week other = (Week) oth;
		return this.start.equals(other.start) && this.end.equals(other.end)
				&& this.orderInSemester == other.orderInSemester
				&& this.type.equals(other.type);
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Week o) {
		return this.start.compareTo(o.start);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "week" + orderInSemester;
	}
}