package bean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

/**
 * A ScheduleBean represent a schedule as a list of SessionBeans (for ECs and IAs, alternating weeks)
 * and interfaces with the SQL database through the ScheduleDAO class.
 * 
 * @author ramusa2, jmfoste2
 *
 */
public class ScheduleBean {
	
	/**
	 * CalendarBean with basic semester info
	 */
	private CalendarBean calendarInfo;
	
	// TODO: scheduling crew, feel free to add extra data structures here
	//		 and/or use a better one (e.g. HashMap, etc.)
	/**
	 * List of daily sessions for a single week
	 */
	private HashMap<Date, ArrayList<SessionBean>> sessions;
	
	/**
	 * Default constructor for empty schedule
	 */
	public ScheduleBean(CalendarBean calInfo) {
		calendarInfo = calInfo;
		sessions = new HashMap<Date, ArrayList<SessionBean>>();
	}
	
	/**
	 * Returns the calendar info for this schedule
	 */
	public CalendarBean getCalendarInfo() {
		return this.calendarInfo;
	}
	
	/**
	 * Adds a empty SessionBean slot to the calendar, to be filled by the scheduling algorithm
	 */
	public void addEmptySessionBeanSlot(SessionBean emptySession) {
		// TODO: fill in this method and data type (if you want to add a raw type.date.timeslot rather than a Sessions object)
		Date d = emptySession.getDate();
		ArrayList<SessionBean> temp = this.sessions.get(d);
		if(temp == null) {
			temp = new ArrayList<SessionBean>();
			this.sessions.put(d, temp);
		}
		temp.add(emptySession);
		// TODO: make sure we don't overlap, or something
	}
	
	/**
	 * Adds a session to the schedule; this session may or may not already be filled
	 * with the appropriate set of clinicians.
	 */
	public void addSession(SessionBean session) {
		// TODO: fill in this method and data type (make sure to avoid collisions)
	}

	/**
	 * Returns a collection of all of the SessionBeans in this schedule
	 */
	public Collection<SessionBean> getAllSessions() {
		ArrayList<SessionBean> allSessions = new ArrayList<SessionBean>();
		for(Date d : sessions.keySet()) {
			allSessions.addAll(sessions.get(d));
		}
		return allSessions;
	}
}
