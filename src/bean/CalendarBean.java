package bean;

import java.util.Calendar;
import java.util.Date;

/**
 * Represents the calendar parameters that an administrator creates
 * when entering setting for a new semester
 *  
 * @author nbeltr2
 * @author dtli2
 */
public class CalendarBean {
	private int id;
	private Date startDate;
	private Date endDate;
	private int iaMinHours;
	private int ecMinHours;
	private Semester semester;
	
	public CalendarBean() {}
	
	public CalendarBean(int id, Date startDate, Date endDate, int iaMinHours, int ecMinHours, Semester semester) {
		this.id = id;
		this.startDate = startDate;
		this.endDate = endDate;
		this.iaMinHours = iaMinHours;
		this.ecMinHours = ecMinHours;
		this.semester = semester;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
	public int getIaMinHours() {
		return iaMinHours;
	}
	public void setIaMinHours(int iaMinHours) {
		this.iaMinHours = iaMinHours;
	}
	public int getEcMinHours() {
		return ecMinHours;
	}
	public void setEcMinHours(int ecMinHours) {
		this.ecMinHours = ecMinHours;
	}
	public Semester getSemester() {
		return semester;
	}
	public void setSemester(Semester semester) {
		this.semester = semester;
	}
	
	public int getYear() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(startDate);
		return calendar.get(Calendar.YEAR);
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		
		if (other == null) {
			return false;
		}
		
		if (!(other instanceof CalendarBean)) {
			return false;
		}
		
		CalendarBean calendar = (CalendarBean)other;
		return id == calendar.id && startDate.equals(calendar.startDate)
				&& endDate.equals(calendar.endDate) && iaMinHours == calendar.iaMinHours
				&& ecMinHours == calendar.ecMinHours && semester == calendar.semester;
	}
}
