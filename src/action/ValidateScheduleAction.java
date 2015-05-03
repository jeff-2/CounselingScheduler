package action;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import utils.DateUtils;
import bean.Clinician;
import bean.ClinicianPreferencesBean;
import bean.CommitmentBean;
import bean.Schedule;
import bean.TimeAwayBean;

/**
 * Class that validates a generated schedule.
 */
public class ValidateScheduleAction {

    /** The ec hours. */
    private int[] ecHours;
    
    /** The ia hours. */
    private int[] iaHours;

    /**
     * Instantiates a new validate schedule action.
     */
    public ValidateScheduleAction() {
	ecHours = new int[] { 8, 12, 16 };
	iaHours = new int[] { 11, 13, 14, 15 };
    }

    /**
     * Validate schedules and returns list of the clinicians which break constraints.
     *
     * @param sch the sch
     * @return the sets the clinicians which conflict with constraints
     */
    public Set<Clinician> validateSchedule(Schedule sch) {
	Set<Clinician> resultSet = new HashSet<Clinician>();
	resultSet.addAll(validateIAScheduleConflicts(sch));
	resultSet.addAll(validateECScheduleConflicts(sch));
	resultSet.addAll(validateSameDayNoonECIAConflicts(sch));
	resultSet.addAll(validateAfternoonMeetingMorningECConflicts(sch));
	resultSet.addAll(validateMorningMeetingAfternoonECConflicts(sch));
	resultSet.addAll(validateECAssignmentMeetsPreference(sch));
	resultSet.addAll(validateEvenlyDistributeECSessions(sch));
	resultSet.addAll(validateOneECPerWeek(sch));
	resultSet.addAll(validateOneIAPerDay(sch));
	return resultSet;
    }

    /**
     * Checks whether the IA schedule conflicts with the clinicians' regular
     * commitments.
     *
     * @param sch            a schedule
     * @return set of clinicians with those conflict
     */
    private Set<Clinician> validateIAScheduleConflicts(Schedule sch) {
	Set<Clinician> retset = new HashSet<>();
	List<Clinician> clinicians = sch.getClinicians();
	Date startdate = sch.getCalendar().getStartDate();
	Calendar cal = Calendar.getInstance();
	cal.setTime(startdate);
	int startweek = cal.get(Calendar.WEEK_OF_YEAR);

	for (Clinician cl : clinicians) {
	    List<CommitmentBean> commitments = cl.getCommitmentBeans();

	    // Checks whether commitment with IA session
	    for (CommitmentBean cmt : commitments) {
		Date date = cmt.getDate();
		int weekday = DateUtils.getDayOfWeek(date) - 1;
		cal.setTime(date);
		int week = cal.get(Calendar.WEEK_OF_YEAR);
		int weekdiff = week - startweek;
		boolean withinSemester = weekdiff >= 0
			&& weekdiff < sch.getNumberOfWeeks();
		boolean weekdayConflicts = weekday >= 0 && weekday <= 4;
		if (withinSemester && weekdayConflicts) {
		    if (cmt.getStartHour() <= 11
			    && cmt.getEndHour() >= 12
			    && sch.getIAClinician(weekdiff % 2 == 0, weekday,
				    11).contains(cl)) {
			retset.add(cl);
		    }
		    if (cmt.getStartHour() <= 13
			    && cmt.getEndHour() >= 14
			    && sch.getIAClinician(weekdiff % 2 == 0, weekday,
				    13).contains(cl)) {
			retset.add(cl);
		    }
		    if (cmt.getStartHour() <= 14
			    && cmt.getEndHour() >= 15
			    && sch.getIAClinician(weekdiff % 2 == 0, weekday,
				    14).contains(cl)) {
			retset.add(cl);
		    }
		    if (cmt.getStartHour() <= 15
			    && cmt.getEndHour() >= 16
			    && sch.getIAClinician(weekdiff % 2 == 0, weekday,
				    15).contains(cl)) {
			retset.add(cl);
		    }
		}
	    }
	}

	return retset;
    }

    /**
     * Checks whether the EC schedule conflicts with the clinicians' regular
     * commitments as well as times away.
     *
     * @param sch            a schedule
     * @return set of clinicians with those conflict
     */
    public Set<Clinician> validateECScheduleConflicts(Schedule sch) {
	Set<Clinician> retset = new HashSet<>();
	List<Clinician> clinicians = sch.getClinicians();
	Date startdate = sch.getCalendar().getStartDate();
	Calendar cal = Calendar.getInstance();
	cal.setTime(startdate);
	int startweek = cal.get(Calendar.WEEK_OF_YEAR);

	for (Clinician cl : clinicians) {
	    List<CommitmentBean> commitments = cl.getCommitmentBeans();
	    List<TimeAwayBean> timesAway = cl.getTimeAwayBeans();

	    // Checks whether commitment with EC session
	    for (CommitmentBean cmt : commitments) {
		Date date = cmt.getDate();
		int weekday = DateUtils.getDayOfWeek(date) - 1;
		cal.setTime(date);
		int week = cal.get(Calendar.WEEK_OF_YEAR);
		int weekdiff = week - startweek;
		boolean withinSemester = weekdiff >= 0
			&& weekdiff < sch.getNumberOfWeeks();
		boolean weekdayConflicts = weekday >= 0 && weekday <= 4;
		if (withinSemester && weekdayConflicts) {
		    if (cmt.getStartHour() <= 8
			    && cmt.getEndHour() >= 9
			    && cl.equals(sch.getECClinician(weekdiff, weekday,
				    8))) {
			retset.add(cl);
		    }
		    if (cmt.getStartHour() <= 12
			    && cmt.getEndHour() >= 13
			    && cl.equals(sch.getECClinician(weekdiff, weekday,
				    12))) {
			retset.add(cl);
		    }
		    if (cmt.getStartHour() <= 16
			    && cmt.getEndHour() >= 17
			    && cl.equals(sch.getECClinician(weekdiff, weekday,
				    16))) {
			retset.add(cl);
		    }
		}
	    }

	    // Checks whether time away conflicts with EC sessions
	    for (TimeAwayBean tm : timesAway) {
		Calendar start = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		start.setTime(tm.getStartDate());
		end.setTime(tm.getEndDate());

		while (!start.after(end)) {
		    Date date = start.getTime();
		    int weekday = DateUtils.getDayOfWeek(date) - 1;
		    int week = start.get(Calendar.WEEK_OF_YEAR);
		    int weekdiff = week - startweek;
		    boolean withinSemester = weekdiff >= 0
			    && weekdiff < sch.getNumberOfWeeks();
		    boolean timeConflicts = weekday >= 0 && weekday <= 4;
		    if (withinSemester
			    && timeConflicts
			    && (cl.equals(sch.getECClinician(weekdiff, weekday,
				    8))
				    || cl.equals(sch.getECClinician(weekdiff,
					    weekday, 12)) || cl.equals(sch
				    .getECClinician(weekdiff, weekday, 16)))) {
			retset.add(cl);
		    }
		    start.add(Calendar.DATE, 1);
		}
	    }
	}

	return retset;
    }

    /**
     * Checks whether a clinician is assigned to a noon EC session and an 1:00
     * IA session on the same day.
     *
     * @param sch            a schedule
     * @return set of clinicians with those conflict
     */
    public Set<Clinician> validateSameDayNoonECIAConflicts(Schedule sch) {
	Set<Clinician> returnSet = new HashSet<>();
	Set<Clinician> duplicates = new HashSet<>();

	for (int week = 0; week < sch.getNumberOfWeeks(); week++) {
	    for (int day = 0; day < 5; day++) {

		duplicates.clear();
		// Find all clinicians assigned to 1:00 IA sessions for this day
		for (Clinician cl : sch.getIAClinician(week % 2 != 0, day, 13)) {
		    duplicates.add(cl);
		}

		// Check whether the clinician assigned to the noon EC sessions
		// is also assigned to the IA session
		Clinician cl = sch.getECClinician(week, day, 12);
		if (cl != null && duplicates.contains(cl)) {
		    returnSet.add(cl);
		}
	    }
	}

	return returnSet;
    }

    /**
     * Checks whether a clinician is assigned a meeting after 5:00 and a 8:00 EC
     * the next morning.
     *
     * @param sch            a schedule
     * @return set of clinicians with those conflict
     */
    public Set<Clinician> validateAfternoonMeetingMorningECConflicts(
	    Schedule sch) {
	Set<Clinician> retset = new HashSet<>();
	List<Clinician> clinicians = sch.getClinicians();
	Date startdate = sch.getCalendar().getStartDate();
	Calendar cal = Calendar.getInstance();
	cal.setTime(startdate);
	int startweek = cal.get(Calendar.WEEK_OF_YEAR);

	for (Clinician cl : clinicians) {
	    List<CommitmentBean> commitments = cl.getCommitmentBeans();
	    List<TimeAwayBean> timesAway = cl.getTimeAwayBeans();

	    // Checks whether commitment conflicts with next day's morning EC
	    // session
	    for (CommitmentBean cmt : commitments) {
		Date date = cmt.getDate();
		int weekday = DateUtils.getDayOfWeek(date) - 1;
		int hour = cmt.getEndHour();
		cal.setTime(date);
		int week = cal.get(Calendar.WEEK_OF_YEAR);
		int weekdiff = week - startweek;
		boolean withinSemester = weekdiff >= 0
			&& weekdiff < sch.getNumberOfWeeks();
		boolean timeConflicts = weekday >= -1 && weekday <= 4
			&& hour >= 6;
		if (withinSemester
			&& timeConflicts
			&& cl.equals(sch.getECClinician(weekdiff, weekday + 1,
				8))) {
		    retset.add(cl);
		}
	    }

	    // Checks whether time away conflicts with next day's morning EC
	    // session
	    for (TimeAwayBean tm : timesAway) {
		Calendar start = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		start.setTime(tm.getStartDate());
		end.setTime(tm.getEndDate());

		while (!start.after(end)) {
		    Date date = start.getTime();
		    int weekday = DateUtils.getDayOfWeek(date) - 1;
		    int week = start.get(Calendar.WEEK_OF_YEAR);
		    int weekdiff = week - startweek;
		    boolean withinSemester = weekdiff >= 0
			    && weekdiff < sch.getNumberOfWeeks();
		    boolean timeConflicts = weekday >= -1 && weekday <= 4;
		    if (withinSemester
			    && timeConflicts
			    && cl.equals(sch.getECClinician(weekdiff,
				    weekday + 1, 8))) {
			retset.add(cl);
		    }
		    start.add(Calendar.DATE, 1);
		}
	    }
	}

	return retset;
    }

    /**
     * Checks whether a clinician is assigned to a 4:00 EC session and an 8:00
     * meeting the next morning.
     *
     * @param sch            a schedule
     * @return set of clinicians with those conflict
     */
    public Set<Clinician> validateMorningMeetingAfternoonECConflicts(
	    Schedule sch) {
	Set<Clinician> retset = new HashSet<>();
	List<Clinician> clinicians = sch.getClinicians();
	Date startdate = sch.getCalendar().getStartDate();
	Calendar cal = Calendar.getInstance();
	cal.setTime(startdate);
	int startweek = cal.get(Calendar.WEEK_OF_YEAR);

	for (Clinician cl : clinicians) {
	    List<CommitmentBean> commitments = cl.getCommitmentBeans();
	    List<TimeAwayBean> timesAway = cl.getTimeAwayBeans();

	    // Checks whether commitment conflicts with previous day's afternoon
	    // EC session
	    for (CommitmentBean cmt : commitments) {
		Date date = cmt.getDate();
		int weekday = DateUtils.getDayOfWeek(date) - 2;
		int hour = cmt.getStartHour();
		cal.setTime(date);
		int week = cal.get(Calendar.WEEK_OF_YEAR);
		int weekdiff = week - startweek;
		boolean withinSemester = weekdiff >= 0
			&& weekdiff < sch.getNumberOfWeeks();
		boolean timeConflicts = weekday >= 1 && weekday <= 5
			&& hour <= 8;
		if (withinSemester
			&& timeConflicts
			&& cl.equals(sch.getECClinician(weekdiff, weekday - 1,
				16))) {
		    retset.add(cl);
		}
	    }

	    // Checks whether time away conflicts with previous day's afternoon
	    // EC session
	    for (TimeAwayBean tm : timesAway) {
		Calendar start = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		start.setTime(tm.getStartDate());
		end.setTime(tm.getEndDate());

		while (!start.after(end)) {
		    Date date = start.getTime();
		    int weekday = DateUtils.getDayOfWeek(date) - 1;
		    int week = start.get(Calendar.WEEK_OF_YEAR);
		    int weekdiff = week - startweek;
		    boolean withinSemester = weekdiff >= 0
			    && weekdiff < sch.getNumberOfWeeks();
		    boolean timeConflicts = weekday >= 1 && weekday <= 5;
		    if (withinSemester
			    && timeConflicts
			    && cl.equals(sch.getECClinician(weekdiff,
				    weekday - 1, 16))) {
			retset.add(cl);
		    }
		    start.add(Calendar.DATE, 1);
		}
	    }
	}

	return retset;
    }

    /**
     * Checks whether the majority of EC sessions assigned to a clinician is for
     * their preferred time.
     *
     * @param sch            a schedule
     * @return set of clinicians with with less than a majority of preferred EC
     *         times
     */
    private Set<Clinician> validateECAssignmentMeetsPreference(Schedule sch) {
	List<Clinician> clinicians = sch.getClinicians();
	Set<Clinician> retSet = new HashSet<>();
	Map<Clinician, Integer> prefEC = new HashMap<>();
	Map<Clinician, Integer> allEC = new HashMap<>();
	ClinicianPreferencesBean pref;

	for (Clinician cl : clinicians) {
	    prefEC.put(cl, 0);
	    allEC.put(cl, 0);
	}

	for (int week = 0; week < sch.getNumberOfWeeks(); week++) {
	    for (int day = 0; day < 5; day++) {
		for (int ec : ecHours) {
		    Clinician cl = sch.getECClinician(week, day, ec);
		    if (cl != null) {
			pref = cl.getClinicianPreferencesBean();

			// calculate start hour from preferences format for
			// preferred time
			int prefHour = pref.getRanking(1) * 4 + 8;
			if (ec == prefHour) {
			    prefEC.put(cl, prefEC.get(cl) + 1);
			}
			allEC.put(cl, allEC.get(cl) + 1);
		    }
		}
	    }
	}

	for (Clinician cl : clinicians) {
	    int prefAssigned = prefEC.get(cl);
	    int totalAssigned = allEC.get(cl);
	    if (prefAssigned < (totalAssigned + 1) / 2) {
		retSet.add(cl);
	    }
	}

	return retSet;
    }

    /**
     * Checks whether all clinicians have been assigned between one less than
     * and one more than the average number of 4:00 EC sessions.
     *
     * @param sch            a schedule
     * @return set of clinicians with too little or too many 4:00 EC sessions
     */
    private Set<Clinician> validateEvenlyDistributeECSessions(Schedule sch) {
	List<Clinician> clinicians = sch.getClinicians();
	Set<Clinician> retSet = new HashSet<>();
	Map<Clinician, Integer> fourEC = new HashMap<>();

	for (Clinician cl : clinicians) {
	    fourEC.put(cl, 0);
	}

	for (int week = 0; week < sch.getNumberOfWeeks(); week++) {
	    for (int day = 0; day < 5; day++) {
		Clinician cl = sch.getECClinician(week, day, 16);
		if (cl != null)
		    fourEC.put(cl, fourEC.get(cl) + 1);
	    }
	}

	for (Clinician cl : clinicians) {
	    ClinicianPreferencesBean pref = cl.getClinicianPreferencesBean();
	    int expectedNumberSessions = (pref.getECHours() + 1) / 3;
	    if (Math.abs(fourEC.get(cl) - expectedNumberSessions) > 1) {
		retSet.add(cl);
	    }
	}

	return retSet;
    }

    /**
     * Checks whether clinicians are assigned to at most 1 IA session per day.
     *
     * @param sch            a schedule
     * @return set of clinicians assigned to more than 1 IA session per day
     */
    public Set<Clinician> validateOneIAPerDay(Schedule sch) {
	Set<Clinician> returnSet = new HashSet<>();
	Set<Clinician> duplicatesWeekA = new HashSet<>();
	Set<Clinician> duplicatesWeekB = new HashSet<>();

	for (int day = 0; day < 5; day++) {
	    duplicatesWeekA.clear();
	    duplicatesWeekB.clear();
	    for (int hour : iaHours) {
		// Check whether there is more than one IA session that a
		// clinician is assigned to this day
		for (Clinician cl : sch.getIAClinician(true, day, hour)) {
		    if (!duplicatesWeekA.add(cl)) {
			returnSet.add(cl);
		    }
		}

		for (Clinician cl : sch.getIAClinician(false, day, hour)) {
		    if (!duplicatesWeekB.add(cl)) {
			returnSet.add(cl);
		    }
		}
	    }
	}

	return returnSet;
    }

    /**
     * Checks whether clinicians are assigned to at most 1 EC session per week.
     *
     * @param sch            a schedule
     * @return set of clinicians assigned to more than 1 EC session per week
     */
    public Set<Clinician> validateOneECPerWeek(Schedule sch) {
	Set<Clinician> returnSet = new HashSet<>();
	Set<Clinician> duplicates = new HashSet<>();

	for (int week = 0; week < sch.getNumberOfWeeks(); week++) {

	    duplicates.clear();
	    for (int day = 0; day < 5; day++) {
		for (int hour : ecHours) {

		    // Check whether there is more than one EC session that a
		    // clinician is assigned to this week
		    Clinician cl = sch.getECClinician(week, day, hour);
		    if (cl == null) {
			continue;
		    }
		    if (duplicates.contains(cl)) {
			returnSet.add(cl);
		    } else {
			duplicates.add(cl);
		    }
		}
	    }
	}

	return returnSet;
    }
}
