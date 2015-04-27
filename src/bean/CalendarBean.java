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
	
	/** The id. */
	private int id;
	
	/** The start date. */
	private Date startDate;
	
	/** The end date. */
	private Date endDate;
	
	/** The ia min hours. */
	private int iaMinHours;
	
	/** The ec min hours. */
	private int ecMinHours;
	
	/** The semester. */
	private Semester semester;
	
	/** The meeting filepath. */
	private String meetingFilepath;
	
	/**
	 * Instantiates a new calendar bean.
	 */
	public CalendarBean() {
		this.meetingFilepath = "";
	}
	
	/**
	 * Instantiates a new calendar bean.
	 *
	 * @param id the id
	 * @param startDate the start date
	 * @param endDate the end date
	 * @param iaMinHours the ia min hours
	 * @param ecMinHours the ec min hours
	 * @param semester the semester
	 * @param meetingFilepath the meeting filepath
	 */
	public CalendarBean(int id, Date startDate, Date endDate, int iaMinHours, int ecMinHours, Semester semester, String meetingFilepath) {
		this.id = id;
		this.startDate = startDate;
		this.endDate = endDate;
		this.iaMinHours = iaMinHours;
		this.ecMinHours = ecMinHours;
		this.semester = semester;
		this.meetingFilepath = meetingFilepath;
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * Gets the start date.
	 *
	 * @return the start date
	 */
	public Date getStartDate() {
		return startDate;
	}
	
	/**
	 * Sets the start date.
	 *
	 * @param startDate the new start date
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	/**
	 * Gets the end date.
	 *
	 * @return the end date
	 */
	public Date getEndDate() {
		return endDate;
	}
	
	/**
	 * Sets the end date.
	 *
	 * @param endDate the new end date
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	/**
	 * Gets the ia min hours.
	 *
	 * @return the ia min hours
	 */
	public int getIaMinHours() {
		return iaMinHours;
	}
	
	/**
	 * Sets the ia min hours.
	 *
	 * @param iaMinHours the new ia min hours
	 */
	public void setIaMinHours(int iaMinHours) {
		this.iaMinHours = iaMinHours;
	}
	
	/**
	 * Gets the ec min hours.
	 *
	 * @return the ec min hours
	 */
	public int getEcMinHours() {
		return ecMinHours;
	}
	
	/**
	 * Sets the ec min hours.
	 *
	 * @param ecMinHours the new ec min hours
	 */
	public void setEcMinHours(int ecMinHours) {
		this.ecMinHours = ecMinHours;
	}
	
	/**
	 * Gets the semester.
	 *
	 * @return the semester
	 */
	public Semester getSemester() {
		return semester;
	}
	
	/**
	 * Sets the semester.
	 *
	 * @param semester the new semester
	 */
	public void setSemester(Semester semester) {
		this.semester = semester;
	}
	
	/**
	 * Gets the meeting filepath.
	 *
	 * @return the meeting filepath
	 */
	public String getMeetingFilepath() {
		return meetingFilepath;
	}
	
	/**
	 * Sets the meeting filepath.
	 *
	 * @param meetingFilepath the new meeting filepath
	 */
	public void setMeetingFilepath(String meetingFilepath) {
		this.meetingFilepath = meetingFilepath;
	}
	
	/**
	 * Gets the year.
	 *
	 * @return the year
	 */
	public int getYear() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(startDate);
		return calendar.get(Calendar.YEAR);
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
		
		if (!(other instanceof CalendarBean)) {
			return false;
		}
		
		CalendarBean calendar = (CalendarBean)other;
		return id == calendar.id && startDate.equals(calendar.startDate)
				&& endDate.equals(calendar.endDate) && iaMinHours == calendar.iaMinHours
				&& ecMinHours == calendar.ecMinHours && semester == calendar.semester &&
				meetingFilepath.equals(calendar.meetingFilepath);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		throw new UnsupportedOperationException("Hash code is not implemented for this class");
	}
}
