package bean;

import java.util.Date;
import java.util.List;

/**
 * The Class SessionBean.
 * 
 * @author jmfoste2, ramusa2
 */
public class SessionBean {
	
	/** The id. */
	private int id;
	
	/** The start time. */
	private int startTime;
	
	/** The duration. */
	private int duration;
	
	/** The day of week. */
	private Weekday dayOfWeek;
	
	/** The date. */
	private Date date;
	
	/** The type. */
	private SessionType type;
	
	/** The clinicians. */
	private List<Integer> clinicians;
	
	/**
	 * Instantiates a new session bean.
	 *
	 * @param sID the s id
	 * @param sTime the s time
	 * @param sDur the s dur
	 * @param sDay the s day
	 * @param sDate the s date
	 * @param sType the s type
	 * @param sClinicians the s clinicians
	 */
	public SessionBean(int sID, int sTime, int sDur,  Weekday sDay, Date sDate, SessionType sType, List<Integer> sClinicians) {
		id = sID;
		startTime = sTime;
		duration = sDur;
		dayOfWeek = sDay;
		date = sDate;
		type = sType;
		clinicians = sClinicians;
	}
	
	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public int getID() {
		return id;
	}
	
	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setID(int id) {
		this.id = id;
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
	 * @param startTime the new start time
	 */
	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}
	
	/**
	 * Gets the duration.
	 *
	 * @return the duration
	 */
	public int getDuration() {
		return duration;
	}
	
	/**
	 * Sets the duration.
	 *
	 * @param duration the new duration
	 */
	public void setDuration(int duration) {
		this.duration = duration;
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
	 * @param dayOfWeek the new day of week
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
	 * @param date the new date
	 */
	public void setDate(Date date) {
		this.date = date;
	}
	
	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public SessionType getType() {
		return type;
	}
	
	/**
	 * Sets the type.
	 *
	 * @param type the new type
	 */
	public void setType(SessionType type) {
		this.type = type;
	}
	
	/**
	 * Gets the clinicians.
	 *
	 * @return the clinicians
	 */
	public List<Integer> getClinicians() {
		return clinicians;
	}
	
	/**
	 * Sets the clinicians.
	 *
	 * @param clinicians the new clinicians
	 */
	public void setClinicians(List<Integer> clinicians) {
		this.clinicians = clinicians;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other == null) {
			return false;
		}
		if (!(other instanceof SessionBean)) {
			return false;
		}
		
		SessionBean session = (SessionBean)other;
		
		return this.id == session.id && this.startTime == session.startTime && this.duration == session.duration 
				&& this.dayOfWeek.equals(session.dayOfWeek) && this.date.equals(session.date) 
				&& this.type == session.type && this.clinicians.equals(session.clinicians);
	}
	
}
