package bean;

import java.util.Date;

/**
 * The Class Commitment handles the storage of data for a particular clinician's commitment.
 * 
 * @author jmfoste2, lim92
 */
public class CommitmentBean {
	
	/** The clinician id. */
	private int clinicianID;
	
	/** The start hour. */
	private int startHour;
	
	/** The end hour. */
	private int endHour;
	
	/** The date. */
	private Date date;
	
	/** The description. */
	private String description;
	
	/**
	 * Creates a weekly event when a clinician is unavailable.
	 *
	 * @param id the id
	 * @param sHour the s hour
	 * @param eHour the e hour
	 * @param d the d
	 * @param desc the desc
	 */
	public CommitmentBean(int id, int sHour, int eHour, Date d, String desc) {
		clinicianID = id;
		startHour = sHour;
		endHour = eHour;
		date = d;
		description = desc;
	}
	
	/**
	 * Gets the clinician id.
	 *
	 * @return the clinician id
	 */
	public int getClinicianID() {
		return clinicianID;
	}
	
	/**
	 * Sets the clinician id.
	 *
	 * @param id the new clinician id
	 */
	public void setClinicianID(int id) {
		clinicianID = id;
	}
	
	/**
	 * Gets the start hour.
	 *
	 * @return the start hour
	 */
	public int getStartHour() {
		return startHour;
	}
	
	/**
	 * Sets the start hour.
	 *
	 * @param hour the new start hour
	 */
	public void setStartHour(int hour) {
		startHour = hour;
	}
	
	/**
	 * Gets the end hour.
	 *
	 * @return the end hour
	 */
	public int getEndHour() {
		return endHour;
	}
	
	/**
	 * Sets the end hour.
	 *
	 * @param hour the new end hour
	 */
	public void setEndHour(int hour) {
		endHour = hour;
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
	 * @param d the new date
	 */
	public void setDate(Date d) {
		date = d;
	}
	
	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Sets the description.
	 *
	 * @param desc the new description
	 */
	public void setDescription(String desc) {
		description = desc;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return clinicianID + " " + OperatingHours.toString(startHour) + "-" + OperatingHours.toString(endHour) + " " +  date + " " + description;
		//return OperatingHours.toString(startHour) + " " + Weekday.dayName(date) + " " + description;
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
		if (!(other instanceof CommitmentBean)) {
			return false;
		}
		CommitmentBean commitment = (CommitmentBean)other;
		return clinicianID == commitment.clinicianID && startHour == commitment.startHour
				&& endHour == commitment.endHour && date.equals(commitment.date)
				&& description.equals(commitment.description);
	}
}
