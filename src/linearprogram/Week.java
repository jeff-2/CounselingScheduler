package linearprogram;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import bean.CalendarBean;
import bean.IAWeektype;

public class Week implements Comparable<Week> {

	private static HashMap<Week, Week> weekCache;
	private static HashMap<Date, Week> dayToWeekCache;

	final Date start, end;
	final IAWeektype type;
	final int orderInSemester;

	private Week(Date s, Date e, int orderInSemester) {
		this.start = s;
		this.end = e;
		this.orderInSemester = orderInSemester;
		this.type = getWeekType(orderInSemester);
	}
	
	public static IAWeektype getWeekType(int orderInSemester) {
		return (orderInSemester % 2 == 0) ? IAWeektype.A : IAWeektype.B;
	}

	public synchronized static Week getWeek(Date day, CalendarBean calendar) {
		if(dayToWeekCache == null) {
			buildCache(calendar);
		}
		return dayToWeekCache.get(day);
	}
	
	public static List<Week> getSemesterWeeks(CalendarBean calendarBean) {
		buildCache(calendarBean);
		List<Week> weeks = new ArrayList<Week>();
		weeks.addAll(weekCache.keySet());
		Collections.sort(weeks);
		return weeks;
	}

	private static void buildCache(CalendarBean calendarBean) {
		if(dayToWeekCache == null
				|| weekCache == null) {
			dayToWeekCache = new HashMap<Date, Week>();
			weekCache = new HashMap<Week, Week>();
		}		
		Date prevMonday = calendarBean.getStartDate();
		Date currentDate = calendarBean.getStartDate();
		Date end = calendarBean.getEndDate();
		Calendar calendar =  Calendar.getInstance();
		calendar.setTime(currentDate);
		int numWeeks = 0;
		while(currentDate.before(end)) {
			ArrayList<Date> days = new ArrayList<Date>();
			while(calendar.get(Calendar.DAY_OF_WEEK) != 6) {
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
			if(temp != null) {
				week = temp;
			}
			weekCache.put(week,  week);
			for(Date d : days) {
				dayToWeekCache.put(d, week);
			}
			numWeeks++;
			calendar.add(Calendar.DAY_OF_WEEK, 1);
			currentDate = calendar.getTime();
			prevMonday = currentDate;
		}
	}

	public int hashCode() {
		return this.start.hashCode();
	}

	@Override
	public boolean equals(Object oth) {
		if(!(oth instanceof Week)) {
			return this == oth;
		}
		Week other = (Week) oth;
		return this.start.equals(other.start)
				&& this.end.equals(other.end)
				&& this.orderInSemester == other.orderInSemester
				&& this.type.equals(other.type);
	}

	@Override
	public int compareTo(Week o) {
		//return (int) Math.signum(o.start.getTime() - this.start.getTime());
		return this.start.compareTo(o.start);
	}
	
	public String toString() {
		return "week"+orderInSemester;
	}
}