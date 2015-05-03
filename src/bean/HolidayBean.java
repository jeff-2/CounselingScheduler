package bean;

import java.util.Date;

import validator.DateRangeValidator;

/**
 * Represents a holiday that an administrator adds to the settings for a new
 * semester
 * 
 * @author nbeltr2
 * @author dtli2
 */
public class HolidayBean {

	/** The id. */
	private int id;

	/** The name. */
	private String name;

	/** The start date. */
	private Date startDate;

	/** The end date. */
	private Date endDate;

	/**
	 * Default constructor.
	 */
	public HolidayBean() {
	}

	/**
	 * Full constructor.
	 *
	 * @param index
	 *            the index
	 * @param n
	 *            the n
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 */
	public HolidayBean(int index, String n, Date start, Date end) {
		id = index;
		name = n;
		startDate = start;
		endDate = end;
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
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name
	 *            the new name
	 */
	public void setName(String name) {
		this.name = name;
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
	 * @param startDate
	 *            the new start date
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
	 * @param endDate
	 *            the new end date
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return name + " from " + DateRangeValidator.formatDateLong(startDate)
				+ " to " + DateRangeValidator.formatDateLong(endDate);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}

		if (other == null) {
			return false;
		}

		if (!(other instanceof HolidayBean)) {
			return false;
		}

		HolidayBean holiday = (HolidayBean) other;
		return id == holiday.id && startDate.equals(holiday.startDate)
				&& endDate.equals(holiday.endDate) && name.equals(holiday.name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		throw new UnsupportedOperationException(
				"Hash code is not implemented for this class");
	}
}
