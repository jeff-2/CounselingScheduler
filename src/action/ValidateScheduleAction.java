package action;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import utils.Logger;
import bean.Semester;
import bean.SessionBean;
import dao.CalendarDAO;
import dao.SessionsDAO;

public class ValidateScheduleAction {
	
	protected Connection conn;
	private SessionsDAO sessionDAO;
	private CalendarDAO calendarDAO;
	private Semester currentSemester;
	private int currentYear;

	public ValidateScheduleAction(Connection connection) {
		conn = connection;
		sessionDAO = new SessionsDAO(conn);
		calendarDAO = new CalendarDAO(conn);
	}
	
	public void validateSchedule() throws SQLException {
		Logger.logln("Validating schedule.");
		
		currentSemester = calendarDAO.getCurrentSemester();
		currentYear = calendarDAO.getCurrentYear();
		
		validateIASessions();
		validateECSessions();
		validateWeeklyECSessionConstraint();
		validateDailyIASessionConstraint();
		validateAlternatingIAFridayConstraintViolation();
		validateNoonECConstraintViolation();
		
		Logger.logln("Schedule validation complete.");
	}

	private void validateIASessions() throws SQLException {
		List<SessionBean> invalidSessions = sessionDAO.getInvalidIASessions(currentSemester, currentYear);
		if(invalidSessions.size() == 0) {
			return;
		}
		for(SessionBean session : invalidSessions) {
			String error = "The IA session occurring on " + session.getDate() +
					" has an invalid start time of " + session.getStartTime();
			Logger.logln(error);
		}
	}

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

	private void validateWeeklyECSessionConstraint() throws SQLException {
		List<String> errors = sessionDAO.getWeeklyECSessionConstraintViolation(currentSemester, currentYear);
		if(errors.isEmpty()) {
			return;
		}
		for(String error : errors) {
			Logger.logln(error);
		}
		
	}

	private void validateDailyIASessionConstraint() throws SQLException {
		List<String> errors = sessionDAO.getDailyIASessionConstraintViolation(currentSemester, currentYear);
		if(errors.isEmpty()) {
			return;
		}
		for(String error : errors) {
			Logger.logln(error);
		}
	}

	private void validateAlternatingIAFridayConstraintViolation() throws SQLException {
		List<String> errors = sessionDAO.getAlternatingIAFridayConstraintViolation(currentSemester, currentYear);
		if(errors.isEmpty()) {
			return;
		}
		for(String error : errors) {
			Logger.logln(error);
		}
	}

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
