package bean;

import java.util.Date;

/**
 * Stores a day in the EC calendar
 * 
 * @author ramusa2, lim92
 *
 */
public class ECScheduleDayBean {
	
	/**
	 * The date of this day
	 */
	private final Date date;
	
	/**
	 * The name of this weekday
	 */
	private final String dayName;
	
	/**
	 * If day is a holiday, the HolidayBean object storing this information; null otherwise
	 */
	private HolidayBean holiday;
	
	/**
	 * An array storing the names of the clinicians working this day's sessions
	 */
	private final String[] clinicians;
	
	private int[] sessionIDs;
	
	/**
	 * Default constructor
	 */
	public ECScheduleDayBean(Date myDate, String myDayName) {
		date = myDate;
		dayName = myDayName;
		holiday = null;
		clinicians = new String[3];
		sessionIDs = new int[3];
	}
	
	/**
	 * Returns this day's Date object
	 */
	public Date date() {
		return date;
	}
	
	/**
	 * Returns this day's name
	 */
	public String dayName() {
		return dayName;
	}
	
	/**
	 * Returns true if this day starts a month
	 */
	@SuppressWarnings("deprecation")
	public boolean startsMonth() {
		return this.date.getDate() == 1;
	}
	
	/**
	 * Adds a clinicians for this day's EC sessions (0 = 8am, 1 = noon, 2 = 4pm)
	 */
	public void addClinician(String name, int timeslot, int id) {
		int mappedTimeslot = -1;
		if(timeslot == 8) {
			mappedTimeslot = 0;
		}
		if(timeslot == 12) {
			mappedTimeslot = 1;
		}
		if(timeslot == 16) {
			mappedTimeslot = 2;
		}
		if(mappedTimeslot >= 0) {
			timeslot = mappedTimeslot;
			if(timeslot >= 0 && timeslot <3) {
				clinicians[timeslot] = name;
				sessionIDs[timeslot] = id;
			}
		}
	}
	
	/**
	 * Gets a clinicians for this day's EC sessions (0 = 8am, 1 = noon, 2 = 4pm)
	 */
	public String[] getClinicians() {
		return clinicians;
	}
	
	/**
	 * Sets this day's holiday
	 */
	public void setHoliday(HolidayBean holidayBean) {
		holiday = holidayBean;
	}
	
	/**
	 * Returns this day's holiday
	 */
	public HolidayBean getHoliday() {
		return holiday;
	}
	
	/**
	 * Returns true if this day is a holiday
	 */
	public boolean isHoliday() {
		return holiday != null;
	}

	@SuppressWarnings("deprecation")
	public String headerString() {
		return this.dayName+" "+this.date.getDate();
	}

	public int[] getSessionIDs() {
		return sessionIDs;
	}

	public void setSessionIDs(int[] sessionIDs) {
		this.sessionIDs = sessionIDs;
	}

}
