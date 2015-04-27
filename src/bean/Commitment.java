package bean;

/**
 * The Class Commitment is used in order to aggregate a set of commitments with different
 * dates that all refer to the same commitment.
 */
public class Commitment {
	
	/** The id. */
	private int id;
	
	/** The start hour. */
	private int startHour;
	
	/** The end hour. */
	private int endHour;
	
	/** The description. */
	private String description;
	
	/** The day of week. */
	private Weekday dayOfWeek;
	
	/**
	 * Instantiates a new commitment.
	 *
	 * @param id the id
	 * @param sHour the s hour
	 * @param eHour the e hour
	 * @param desc the desc
	 * @param day the day
	 */
	public Commitment(int id, int sHour, int eHour, String desc, Weekday day) {
		this.id = id;
		startHour = sHour;
		endHour = eHour;
		description = desc;
		dayOfWeek = day;
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
	 * @param startHour the new start hour
	 */
	public void setStartHour(int startHour) {
		this.startHour = startHour;
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
	 * @param endHour the new end hour
	 */
	public void setEndHour(int endHour) {
		this.endHour = endHour;
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
	 * @param description the new description
	 */
	public void setDescription(String description) {
		this.description = description;
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
		
		if (!(other instanceof Commitment)) {
			return false;
		}
		
		Commitment commitment = (Commitment) other;
		return id == commitment.id && startHour == commitment.startHour && endHour == commitment.endHour
				&& description.equals(commitment.description) && dayOfWeek.equals(commitment.dayOfWeek);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int hashCode = 31;
		hashCode = hashCode * 31 + id;
		hashCode = hashCode * 31 + startHour;
		hashCode = hashCode * 31 + endHour;
		hashCode = hashCode * 31 + description.hashCode();
		hashCode = hashCode * 31 + dayOfWeek.hashCode();
		return hashCode;
	}
}
