package bean;

import java.util.Date;

import utils.DateUtils;

/**
 * Stores a day in the EC calendar.
 *
 * @author ramusa2, lim92
 */
public class ECScheduleDayBean {

	/** The date of this day. */
	private final Date date;

	/** The name of this weekday. */
	private final String dayName;

	/**
	 * If day is a holiday, the HolidayBean object storing this information;
	 * null otherwise.
	 */
	private HolidayBean holiday;

	/**
	 * An array storing the names of the clinicians working this day's sessions.
	 */
	private final String[] clinicians;

	/** The session i ds. */
	private int[] sessionIDs;

	/**
	 * Default constructor.
	 *
	 * @param myDate
	 *            the my date
	 * @param myDayName
	 *            the my day name
	 */
	public ECScheduleDayBean(Date myDate, String myDayName) {
		date = myDate;
		dayName = myDayName;
		holiday = null;
		clinicians = new String[3];
		sessionIDs = new int[3];
	}

	/**
	 * Returns this day's Date object.
	 *
	 * @return the date
	 */
	public Date date() {
		return date;
	}

	/**
	 * Returns this day's name.
	 *
	 * @return the string
	 */
	public String dayName() {
		return dayName;
	}

	/**
	 * Returns true if this day starts a month.
	 *
	 * @return true, if successful
	 */
	public boolean startsMonth() {
		return DateUtils.getDayOfMonth(this.date) == 1;
	}

	/**
	 * Adds a clinicians for this day's EC sessions (0 = 8am, 1 = noon, 2 =
	 * 4pm).
	 *
	 * @param name
	 *            the name
	 * @param timeslot
	 *            the timeslot
	 * @param id
	 *            the id
	 */
	public void addClinician(String name, int timeslot, int id) {
		int mappedTimeslot = -1;
		if (timeslot == 8) {
			mappedTimeslot = 0;
		}
		if (timeslot == 12) {
			mappedTimeslot = 1;
		}
		if (timeslot == 16) {
			mappedTimeslot = 2;
		}
		if (mappedTimeslot >= 0) {
			timeslot = mappedTimeslot;
			if (timeslot >= 0 && timeslot < 3) {
				clinicians[timeslot] = name;
				sessionIDs[timeslot] = id;
			}
		}
	}

	/**
	 * Gets a clinicians for this day's EC sessions (0 = 8am, 1 = noon, 2 =
	 * 4pm).
	 *
	 * @return the clinicians
	 */
	public String[] getClinicians() {
		return clinicians;
	}

	/**
	 * Sets this day's holiday.
	 *
	 * @param holidayBean
	 *            the new holiday
	 */
	public void setHoliday(HolidayBean holidayBean) {
		holiday = holidayBean;
	}

	/**
	 * Returns this day's holiday.
	 *
	 * @return the holiday
	 */
	public HolidayBean getHoliday() {
		return holiday;
	}

	/**
	 * Returns true if this day is a holiday.
	 *
	 * @return true, if is holiday
	 */
	public boolean isHoliday() {
		return holiday != null;
	}

	/**
	 * Header string.
	 *
	 * @return the string
	 */
	public String headerString() {
		return this.dayName + " " + DateUtils.getDayOfMonth(this.date);
	}

	/**
	 * Gets the session ids.
	 *
	 * @return the session ids
	 */
	public int[] getSessionIDs() {
		return sessionIDs;
	}

	/**
	 * Sets the session ids.
	 *
	 * @param sessionIDs
	 *            the new session ids
	 */
	public void setSessionIDs(int[] sessionIDs) {
		this.sessionIDs = sessionIDs;
	}

}
