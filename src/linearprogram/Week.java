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
		this.type = (orderInSemester % 2 == 0) ? IAWeektype.A : IAWeektype.B;
	}

	public synchronized static Week getWeek(Date day, CalendarBean calendar) {
		if(dayToWeekCache != null) {
			Week cached = dayToWeekCache.get(day);
			if(cached != null) {
				return cached;
			}
			else {
				Week newWeek = getWeekFromDate(day, calendar.getStartDate());
				Week temp = weekCache.get(newWeek);
				if(temp != null) {
					dayToWeekCache.put(day, temp);
					return temp;
				}
				weekCache.put(newWeek, newWeek);
				dayToWeekCache.put(day, newWeek);
				return newWeek;
			}
		}
		else {
			buildCache(calendar);
			return getWeekFromDate(day, calendar.getStartDate());
		}
	}
	
	public static List<Week> getSemesterWeeks(CalendarBean calendarBean) {
		buildCache(calendarBean);
		List<Week> weeks = new ArrayList<Week>();
		weeks.addAll(weekCache.keySet());
		Collections.sort(weeks);
		return weeks;
	}

	private static void buildCache(CalendarBean calendarBean) {
		dayToWeekCache = new HashMap<Date, Week>();
		weekCache = new HashMap<Week, Week>();
		Date calStart = calendarBean.getStartDate();
		Date cur = calStart;
		Date end = calendarBean.getEndDate();
		Calendar calendar =  Calendar.getInstance();
		calendar.setTime(cur);
		while(cur.before(end)) {
			Week week = getWeekFromDate(cur, calStart);
			dayToWeekCache.put(cur, week);
			if(cur.getDay() == 1) {
				while(cur.getDay() != Calendar.MONDAY) {
					calendar.add(Calendar.DAY_OF_WEEK, 1);
					cur = calendar.getTime();
					dayToWeekCache.put(cur, week);
				}
			}
			else {
				calendar.add(Calendar.DAY_OF_WEEK, 1);
				cur = calendar.getTime();
			}
		}
	}

	private static Week getWeekFromDate(Date day, Date calStart) {
		Week cached = dayToWeekCache.get(day);
		if(cached != null) {
			return cached;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(calStart);
		while(calendar.getTime().getDay() == 6 || calendar.getTime().getDay() == 0) {
			// Go ahead to Monday if Sa/Su
			calendar.add(Calendar.DAY_OF_WEEK, 1);
		}
		while(calendar.getTime().getDay() > 1 && calendar.getTime().getDay() < 6) {
			// Go back to Monday if T/W/Th/F
			calendar.add(Calendar.DAY_OF_WEEK, -1);
		}
		Date calStartMonday = calendar.getTime();
		return getWeek(calStartMonday, day);
	}

	private static Week getWeek(Date calStartMonday, Date day) {
		Calendar calendar = Calendar.getInstance();
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		calendar.setTime(day);
		int daysGone = 0;
		while(calendar.getTime().after(calStartMonday)
				&& calendar.getTime().getDay() != calendar.getFirstDayOfWeek()) {
			calendar.add(Calendar.DAY_OF_MONTH, -1);
			daysGone++;
		}
		Date start = calendar.getTime();
		calendar.setTime(start);
		calendar.add(Calendar.DAY_OF_MONTH, 7);
		Date end = calendar.getTime();
		Week week = new Week(start, end, daysGone / 7);
		Week cached = weekCache.get(week);
		if(cached == null) {
			return cached;
		}
		weekCache.put(week, week);
		return week;
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
		return (int) Math.signum(this.start.getTime() - o.start.getTime());
	}
}

