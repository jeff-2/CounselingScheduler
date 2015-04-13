package bean;

import java.util.Date;

public class SessionNameBean {
	
	/** The clinician name */
	private String clinicianName;
	
	/** The start time. */
	private int startTime;
	
	/** The day of week. */
	private String dayOfWeek;
	
	/** The date. */
	private Date date;
	
	/**  The type of week A/B. */
	private int weekType;
	
	private int sessionID;
	
	public SessionNameBean(String name, int sTime, String sDay, Date sDate, int sWeekType, int sSessionID) {
		clinicianName = name;
		startTime = sTime;
		dayOfWeek = sDay;
		date = sDate;
		weekType = sWeekType;
		sessionID = sSessionID;
		
	}

	public String getClinicianName() {
		return clinicianName;
	}

	public void setClinicianName(String clinicianName) {
		this.clinicianName = clinicianName;
	}

	public int getStartTime() {
		return startTime;
	}

	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}

	public String getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(String dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getWeekType() {
		return weekType;
	}

	public void setWeekType(int weekType) {
		this.weekType = weekType;
	}
	
	public String toString() {
		return "Clinician: " + clinicianName + " startTime: " + startTime + " startDay: " + dayOfWeek + " startDate: " + date + " weekType: " + weekType;
	}

	public int getSessionID() {
		return sessionID;
	}

	public void setSessionID(int sessionID) {
		this.sessionID = sessionID;
	}
	
}
