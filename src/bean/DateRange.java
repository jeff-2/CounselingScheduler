package bean;

import java.util.Date;

/**
 * The Class DateRange is used to track a range of dates [start, end].
 */
public class DateRange {
	
	/** The start date. */
	private Date startDate;
	
	/** The end date. */
	private Date endDate;
	
	/**
	 * Instantiates a new date range.
	 *
	 * @param start the start
	 * @param end the end
	 */
	public DateRange(Date start, Date end) {
		startDate = start;
		endDate = end;
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
	 * @param start the new start date
	 */
	public void setStartDate(Date start) {
		startDate = start;
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
	 * @param end the new end date
	 */
	public void setEndDate(Date end) {
		endDate = end;
	}
}
