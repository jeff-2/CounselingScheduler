package bean;

/**
 * The Class Commitment handles the storage of data for a particular clinician's commitment.
 * 
 * @author jmfoste2, lim92
 */
public class CommitmentBean {
	
	/** The clinician id. */
	private int clinicianID;
	
	/** The hour of day. */
	private int hourOfDay;
	
	/** The day of week. */
	private Weekday dayOfWeek;
	
	/** The description. */
	private String description;
	
	/**
	 * Creates a weekly event when a clinician is unavailable.
	 *
	 * @param id the id
	 * @param hour in 24-hour format
	 * @param day of week, use Calendar constants
	 * @param desc the desc
	 */
	public CommitmentBean(int id, int hour, Weekday day, String desc) {
		clinicianID = id;
		hourOfDay = hour;
		dayOfWeek = day;
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
	 * Gets the hour of day.
	 *
	 * @return the hour of day
	 */
	public int getHourOfDay() {
		return hourOfDay;
	}
	
	/**
	 * Sets the hour of day.
	 *
	 * @param hour the new hour of day
	 */
	public void setHourOfDay(int hour) {
		hourOfDay = hour;
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
	 * @param day the new day of week
	 */
	public void setDayOfWeek(Weekday day) {
		dayOfWeek = day;
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

		return OperatingHours.toString(hourOfDay) + " " + dayOfWeek + " " + description;
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
		return clinicianID == commitment.clinicianID && hourOfDay == commitment.hourOfDay 
				&& dayOfWeek.equals(commitment.dayOfWeek) && description.equals(commitment.description);
	}
}
