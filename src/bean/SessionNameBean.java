package bean;

import java.util.Date;
import java.util.List;

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
	
	public SessionNameBean(String name, int sTime, String sDay, Date sDate, int sWeekType) {
		clinicianName = name;
		startTime = sTime;
		dayOfWeek = sDay;
		date = sDate;
		weekType = sWeekType;
		
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
	
	
}
