package forms;

import java.util.Date;

/**
 * Represents days when a Clinician will be away from the Counseling Center.
 * 
 * @author Yusheng Hou and Kevin Lim
 */
public class TimeAwayPlan {
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
	public TimeAwayPlan(String description, Date start, Date end)
	{
		this.description = description;
		this.startDate = start;
		this.endDate = end;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String planDescription) {
		this.description = planDescription;
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
	
}
