package bean;

import java.util.Calendar;
import java.util.Date;

import validator.DateRangeValidator;

/**
 * The Class TimeAway handles the storage related to time away for a particular
 * clinician id.
 * 
 * @author jmfoste2, lim92
 */
public class TimeAwayBean {

    /** The clinician id. */
    private int clinicianID;

    /** The description. */
    private String description;

    /** The start date. */
    private Date startDate;

    /** The end date. */
    private Date endDate;

    /**
     * Creates a time when Clinician will be away from the Counseling Center.
     *
     * @param id
     *            the id
     * @param desc
     *            the desc
     * @param start
     *            date
     * @param end
     *            date
     */
    public TimeAwayBean(int id, String desc, Date start, Date end) {
	clinicianID = id;
	description = desc;
	startDate = start;
	endDate = end;
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
     * @param id
     *            the new clinician id
     */
    public void setClinicianID(int id) {
	clinicianID = id;
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
     * @param desc
     *            the new description
     */
    public void setDescription(String desc) {
	description = desc;
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
	Calendar calendar = Calendar.getInstance();
	calendar.setTime(startDate);
	int yearStart = calendar.get(Calendar.YEAR);
	calendar.setTime(endDate);
	int yearEnd = calendar.get(Calendar.YEAR);
	return description + ": "
		+ DateRangeValidator.formatDateLong(startDate) + ", "
		+ yearStart + " to "
		+ DateRangeValidator.formatDateLong(endDate) + ", " + yearEnd;
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
	if (!(other instanceof TimeAwayBean)) {
	    return false;
	}
	TimeAwayBean timeAway = (TimeAwayBean) other;
	return clinicianID == timeAway.clinicianID
		&& description.equals(timeAway.description)
		&& startDate.equals(timeAway.startDate)
		&& endDate.equals(timeAway.endDate);
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
