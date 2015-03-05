package forms;

import java.util.Date;

public class TimeAway {
	
	private int clinicianID;
	private String description;
	private Date startDate;
	private Date endDate;
	
	/**
	 * Creates a time when Clinician will be away from the Counseling Center
	 * 
	 * @param description of event
	 * @param start date
	 * @param end date
	 */
	public TimeAway(int id, String desc, Date start, Date end) {
		clinicianID = id;
		description = desc;
		startDate = start;
		endDate = end;
	}
	
	public int getClinicianID() {
		return clinicianID;
	}
	
	public void setClinicianID(int id) {
		clinicianID = id;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String desc) {
		description = desc;
	}
	
	public Date getStartDate() {
		return startDate;
	}
	
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	public Date getEndDate() {
		return endDate;
	}
	
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	public String toString() {
		return description + ": " + Utility.formatDate(startDate) + "-" + Utility.formatDate(endDate);
	}
}
