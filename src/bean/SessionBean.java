package bean;

import java.util.Date;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class SessionBean handles storage of data associated with a particular
 * session.
 * 
 * @author jmfoste2, ramusa2
 */
public class SessionBean implements Comparable<SessionBean> {

    /** The id. */
    private int id;

    /** The start time. */
    private int startTime;

    /** The duration. */
    private int duration;

    /** The day of week. */
    private Weekday dayOfWeek;

    /** The date. */
    private Date date;

    /** The type. */
    private SessionType type;

    /** The clinicians. */
    private List<Integer> clinicians;

    /** The semester. */
    private Semester semester;

    /** The type of week A/B. */
    private IAWeektype weekType;

    /**
     * Instantiates a new session bean.
     *
     * @param sID
     *            the s id
     * @param sTime
     *            the s time
     * @param sDur
     *            the s dur
     * @param sDay
     *            the s day
     * @param sDate
     *            the s date
     * @param sType
     *            the s type
     * @param sClinicians
     *            the s clinicians
     * @param sSemester
     *            the s semester
     * @param sWeekType
     *            the s week type
     */
    public SessionBean(int sID, int sTime, int sDur, Weekday sDay, Date sDate,
	    SessionType sType, List<Integer> sClinicians, Semester sSemester,
	    IAWeektype sWeekType) {
	id = sID;
	startTime = sTime;
	duration = sDur;
	dayOfWeek = sDay;
	date = sDate;
	type = sType;
	clinicians = sClinicians;
	semester = sSemester;
	weekType = sWeekType;
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
     * Sets the id.
     *
     * @param id
     *            the new id
     */
    public void setID(int id) {
	this.id = id;
    }

    /**
     * Gets the start time.
     *
     * @return the start time
     */
    public int getStartTime() {
	return startTime;
    }

    /**
     * Sets the start time.
     *
     * @param startTime
     *            the new start time
     */
    public void setStartTime(int startTime) {
	this.startTime = startTime;
    }

    /**
     * Gets the duration.
     *
     * @return the duration
     */
    public int getDuration() {
	return duration;
    }

    /**
     * Sets the duration.
     *
     * @param duration
     *            the new duration
     */
    public void setDuration(int duration) {
	this.duration = duration;
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
     * @param dayOfWeek
     *            the new day of week
     */
    public void setDayOfWeek(Weekday dayOfWeek) {
	this.dayOfWeek = dayOfWeek;
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
     * @param date
     *            the new date
     */
    public void setDate(Date date) {
	this.date = date;
    }

    /**
     * Gets the type.
     *
     * @return the type
     */
    public SessionType getType() {
	return type;
    }

    /**
     * Sets the type.
     *
     * @param type
     *            the new type
     */
    public void setType(SessionType type) {
	this.type = type;
    }

    /**
     * Gets the clinicians.
     *
     * @return the clinicians
     */
    public List<Integer> getClinicians() {
	return clinicians;
    }

    /**
     * Sets the clinicians.
     *
     * @param clinicians
     *            the new clinicians
     */
    public void setClinicians(List<Integer> clinicians) {
	this.clinicians = clinicians;
    }

    /**
     * Adds the clinician.
     *
     * @param clinician
     *            the clinician
     */
    public void addClinician(Clinician clinician) {
	this.clinicians.add(clinician.getClinicianBean().getClinicianID());
    }

    /**
     * Removes the clinician.
     *
     * @param clinician
     *            the clinician
     * @return true, if successful
     */
    public boolean removeClinician(Clinician clinician) {
	if (this.clinicians.contains(clinician.getClinicianBean()
		.getClinicianID())) {
	    this.clinicians.remove(new Integer(clinician.getClinicianBean()
		    .getClinicianID()));
	    return true;
	}
	return false;
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
     * @param semester
     *            the new semester
     */
    public void setSemester(Semester semester) {
	this.semester = semester;
    }

    /**
     * Gets the week type.
     *
     * @return the week type
     */
    public IAWeektype getWeekType() {
	return weekType;
    }

    /**
     * Sets the week type.
     *
     * @param weekType
     *            the new week type
     */
    public void setWeekType(IAWeektype weekType) {
	this.weekType = weekType;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
	return this.id;
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
	if (!(other instanceof SessionBean)) {
	    return false;
	}

	SessionBean session = (SessionBean) other;

	return this.id == session.id && this.startTime == session.startTime
		&& this.duration == session.duration
		&& this.dayOfWeek.equals(session.dayOfWeek)
		&& this.date.equals(session.date) && this.type == session.type
		&& this.clinicians.equals(session.clinicians);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(SessionBean o) {
	int res = (this.date.before(o.date)) ? -100
		: (this.date.after(o.date) ? 100 : 0);
	res += (int) Math.signum(this.startTime - o.startTime);
	return res;
    }

    /**
     * Gets the variable string to represent the session bean.
     *
     * @return the variable string
     */
    public String getVariableString() {
	if (this.type == SessionType.EC) {
	    return id + "_" + dayOfWeek + "_" + startTime + "_" + type;
	} else {
	    return dayOfWeek + "_" + startTime + "_" + type + "_" + weekType;
	}
    }

}
