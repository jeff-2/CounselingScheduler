package action;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import utils.Logger;
import bean.CalendarBean;
import bean.Clinician;
import bean.Schedule;
import bean.Semester;
import bean.SessionBean;
import dao.CalendarDAO;
import dao.SessionsDAO;

/**
 * Class that validates a generated schedule
 */
public class ValidateScheduleAction {
	
	protected Connection conn;
	private SessionsDAO sessionDAO;
	private CalendarDAO calendarDAO;
	private Semester currentSemester;
	private int currentYear;

	/**
	 * Instantiates a new validate schedule action.
	 *
	 * @param connection the connection
	 */
	public ValidateScheduleAction(Connection connection) {
		conn = connection;
		sessionDAO = new SessionsDAO(conn);
		calendarDAO = new CalendarDAO(conn);
	}
	
	/**
	 * Validates the schedule. Logs  any error messages. 
	 *
	 * @throws SQLException the SQL exception
	 */
	public void validateSchedule() throws SQLException {
		CalendarBean calendarBean = calendarDAO.loadCalendar();
		currentSemester = calendarBean.getSemester();
		currentYear = calendarBean.getYear();
		
		validateIASessions();
		validateECSessions();
		validateWeeklyECSessionConstraint();
		validateDailyIASessionConstraint();
		validateAlternatingIAFridayConstraintViolation();
		validateNoonECConstraintViolation();
	}

	/**
	 * Checks whether the IA schedule conflicts with the clinicians' regular commitments
	 * @param sch a schedule
	 * @return set of clinicians with those conflict
	 */
	private Set<Clinician> validateIAScheduleConflicts(Schedule sch) {
		//TODO validateIAScheduleConflicts
		return new HashSet<>();
	}
	
	/**
	 * Checks whether the EC schedule conflicts with the clinicians' regular commitments as well as times away
	 * @param sch a schedule
	 * @return set of clinicians with those conflict
	 */
	private Set<Clinician> validateECScheduleConflicts(Schedule sch) {
		//TODO validateECScheduleConflicts
		return new HashSet<>();
	}

	/**
	 * Checks whether a clinician is assigned to a noon EC session and an 1:00 IA session on the same day
	 * @param sch a schedule
	 * @return set of clinicians with those conflict
	 */
	private Set<Clinician> validateSameDayNoonECIAConflicts(Schedule sch) {
		// TODO validateSameDayNoonECIAConflicts
		Set<Clinician> returnSet = new HashSet<>();
		Set<Clinician> duplicates = new HashSet<>();
		
		for (int week = 1; week <= sch.getNumberOfWeeks(); week++) {
			for (int day = 1; day <= 5; day++) {
				
				duplicates.clear();
				// Find all clinicians assigned to 1:00 IA sessions for this day
				for (Clinician cl: sch.getIAClinician(week % 2 == 1, day, 13)) {
					duplicates.add(cl);
				}

				// Check whether the clinician assigned to the noon EC sessions is also assigned to the IA session 
				Clinician cl = sch.getECClinician(week, day, 12);
				if (cl != null && duplicates.contains(cl)) {
					returnSet.add(cl);
				}
			}
		}
		
		return returnSet;
	}
	
	/**
	 * Checks whether a clinician is assigned a meeting after 5:00 and a 8:00 EC the next morning
	 * @param sch a schedule
	 * @return set of clinicians with those conflict
	 */
	private Set<Clinician> validateAfternoonMeetingMorningECConflicts(Schedule sch) {
		// TODO validateAfternoonTherapyMorningECConflicts
		return new HashSet<>();
	}

	/**
	 * Checks whether a clinician is assigned to a 4:00 EC session and an 8:00 meeting the next morning
	 * @param sch a schedule
	 * @return set of clinicians with those conflict
	 */
	private Set<Clinician> validateMorningMeetingAfternoonECConflicts(Schedule sch) {
		// TODO validateNoonECOneIAConflicts
		return new HashSet<>();
	}

	/**
	 * Checks whether the majority of EC sessions assigned to a clinician is for their preferred time
	 * @param sch a schedule
	 * @return set of clinicians with with less than a majority of preferred EC times
	 */
	private Set<Clinician> validateECAssignmentMeetsPreference(Schedule sch) {
		// TODO validateECAssignmentMeetsPreference
		return new HashSet<>();
	}

	/**
	 * Checks whether all clinicians have been assigned between one less than and one more than the average number of 4:00 EC sessions
	 * @param sch a schedule
	 * @return set of clinicians with too little or too many 4:00 EC sessions
	 */
	private Set<Clinician> validateEvenlyDistributeECSessions(Schedule sch) {
		// TODO validateEvenlyDistributeECSessions
		return new HashSet<>();
	}

	/**
	 * Checks whether clinicians are assigned to at most 1 IA session per day
	 * @param sch a schedule
	 * @return set of clinicians assigned to more than 1 IA session per day
	 */
	private Set<Clinician> validateOneIAPerDay(Schedule sch) {
		//TODO validateOneIAPerDay
		Set<Clinician> returnSet = new HashSet<>();
		Set<Clinician> duplicates = new HashSet<>();
		int iaHours[] = new int[]{11, 13, 14, 15};
		
		for (boolean isTypeA = true; isTypeA; isTypeA = !isTypeA) {
			for (int day = 1; day <= 5; day++) {
				
				duplicates.clear();
				for (int hour: iaHours) {
					
					// Check whether there is more than one IA session that a clinician is assigned to this day
					for (Clinician cl: sch.getIAClinician(isTypeA, day, hour)) {
						if (duplicates.contains(cl)) {
							returnSet.add(cl);
						}
						else {
							duplicates.add(cl);
						}
					}
				}
			}
		}
		
		return returnSet;
	}
	
	/**
	 * Checks whether clinicians are assigned to at most 1 EC session per week
	 * @param sch a schedule
	 * @return set of clinicians assigned to more than 1 EC session per week
	 */
	private Set<Clinician> validateOneECPerWeek(Schedule sch) {
		//TODO validateOneECPerWeek
		Set<Clinician> returnSet = new HashSet<>();
		Set<Clinician> duplicates = new HashSet<>();
		int ecHours[] = new int[]{8, 12, 16};
		
		for (int week = 1; week <= sch.getNumberOfWeeks(); week++) {
			
			duplicates.clear();
			for (int day = 1; day <= 5; day++) {
				for (int hour: ecHours) {
					
					// Check whether there is more than one EC session that a clinician is assigned to this week 
					Clinician cl = sch.getECClinician(week, day, hour);
					if (cl == null) {
						continue;
					}
					if (duplicates.contains(cl)) {
						returnSet.add(cl);
					}
					else {
						duplicates.add(cl);
					}
				}
			}
		}
		
		return returnSet;
	}

	/**
	 * Validate ia sessions.
	 *
	 * @throws SQLException the SQL exception
	 */
	private void validateIASessions() throws SQLException {
		List<SessionBean> invalidSessions = sessionDAO.getInvalidIASessions(currentSemester, currentYear);
		if(invalidSessions.isEmpty()) {
			return;
		}
		for(SessionBean session : invalidSessions) {
			String error = "The IA session occurring on " + session.getDate() +
					" has an invalid start time of " + session.getStartTime();
			Logger.logln(error);
		}
	}

	/**
	 * Validate ec sessions.
	 *
	 * @throws SQLException the SQL exception
	 */
	private void validateECSessions() throws SQLException {
		List<SessionBean> invalidSessions = sessionDAO.getInvalidECSessions(currentSemester.ordinal(), currentYear);
		if(invalidSessions.isEmpty()) {
			return;
		}
		for(SessionBean session : invalidSessions) {
			String error = "The EC session occurring on " + session.getDate() +
					" has an invalid start time of " + session.getStartTime();
			Logger.logln(error);
		}
	}

	/**
	 * Validate weekly ec session constraint.
	 *
	 * @throws SQLException the SQL exception
	 */
	private void validateWeeklyECSessionConstraint() throws SQLException {
		List<String> errors = sessionDAO.getWeeklyECSessionConstraintViolation(currentSemester, currentYear);
		if(errors.isEmpty()) {
			return;
		}
		for(String error : errors) {
			Logger.logln(error);
		}
		
	}

	/**
	 * Validate daily ia session constraint.
	 *
	 * @throws SQLException the SQL exception
	 */
	private void validateDailyIASessionConstraint() throws SQLException {
		List<String> errors = sessionDAO.getDailyIASessionConstraintViolation(currentSemester, currentYear);
		if(errors.isEmpty()) {
			return;
		}
		for(String error : errors) {
			Logger.logln(error);
		}
	}

	/**
	 * Validate alternating ia friday constraint violation.
	 *
	 * @throws SQLException the SQL exception
	 */
	private void validateAlternatingIAFridayConstraintViolation() throws SQLException {
		List<String> errors = sessionDAO.getAlternatingIAFridayConstraintViolation(currentSemester, currentYear);
		if(errors.isEmpty()) {
			return;
		}
		for(String error : errors) {
			Logger.logln(error);
		}
	}

	/**
	 * Validate noon ec constraint violation.
	 *
	 * @throws SQLException the SQL exception
	 */
	private void validateNoonECConstraintViolation() throws SQLException {
		List<String> errors = sessionDAO.getNoonECConstraintViolation(currentSemester, currentYear);
		if(errors.isEmpty()) {
			return;
		}
		for(String error : errors) {
			Logger.logln(error);
		}
		
	}

}
