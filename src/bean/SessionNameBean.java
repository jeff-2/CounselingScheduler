package bean;

import java.util.Date;

/**
 * The Class SessionNameBean provides storage of session info for a particular
 * clinician.
 */
public class SessionNameBean {

	/** The clinician name. */
	private String clinicianName;

	/** The start time. */
	private int startTime;

	/** The day of week. */
	private Weekday dayOfWeek;

	/** The date. */
	private Date date;

	/** The type of week A/B. */
	private int weekType;

	/** The session id. */
	private int sessionID;

	/**
	 * Instantiates a new session name bean.
	 *
	 * @param name
	 *            the name
	 * @param sTime
	 *            the s time
	 * @param sDay
	 *            the s day
	 * @param sDate
	 *            the s date
	 * @param sWeekType
	 *            the s week type
	 * @param sSessionID
	 *            the s session id
	 */
	public SessionNameBean(String name, int sTime, Weekday sDay, Date sDate,
			int sWeekType, int sSessionID) {
		clinicianName = name;
		startTime = sTime;
		dayOfWeek = sDay;
		date = sDate;
		weekType = sWeekType;
		sessionID = sSessionID;
	}

	/**
	 * Gets the clinician name.
	 *
	 * @return the clinician name
	 */
	public String getClinicianName() {
		return clinicianName;
	}

	/**
	 * Sets the clinician name.
	 *
	 * @param clinicianName
	 *            the new clinician name
	 */
	public void setClinicianName(String clinicianName) {
		this.clinicianName = clinicianName;
	}

	/**
	 * Gets the start time.
	 *
	 * @return the start time
	 */
	public int getStartTime() {
		return startTime;
	}

	/**
	 * Sets the start time.
	 *
	 * @param startTime
	 *            the new start time
	 */
	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}

	/**
	 * Gets the day of week.
	 *
	 * @return the day of week
	 */
	public Weekday getDayOfWeek() {
		return dayOfWeek;
	}

	/**
	 * Sets the day of week.
	 *
	 * @param dayOfWeek
	 *            the new day of week
	 */
	public void setDayOfWeek(Weekday dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	/**
	 * Gets the date.
	 *
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * Sets the date.
	 *
	 * @param date
	 *            the new date
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * Gets the week type.
	 *
	 * @return the week type
	 */
	public int getWeekType() {
		return weekType;
	}

	/**
	 * Sets the week type.
	 *
	 * @param weekType
	 *            the new week type
	 */
	public void setWeekType(int weekType) {
		this.weekType = weekType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "Clinician: " + clinicianName + " startTime: " + startTime
				+ " startDay: " + dayOfWeek + " startDate: " + date
				+ " weekType: " + weekType;
	}

	/**
	 * Gets the session id.
	 *
	 * @return the session id
	 */
	public int getSessionID() {
		return sessionID;
	}

	/**
	 * Sets the session id.
	 *
	 * @param sessionID
	 *            the new session id
	 */
	public void setSessionID(int sessionID) {
		this.sessionID = sessionID;
	}

}
