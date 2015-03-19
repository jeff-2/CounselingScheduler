package action;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import utils.Logger;
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
		currentSemester = calendarDAO.getCurrentSemester();
		currentYear = calendarDAO.getCurrentYear();
		
		validateIASessions();
		validateECSessions();
		validateWeeklyECSessionConstraint();
		validateDailyIASessionConstraint();
		validateAlternatingIAFridayConstraintViolation();
		validateNoonECConstraintViolation();
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
