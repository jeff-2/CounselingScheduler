package dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import bean.ScheduleBean;
import bean.SessionBean;

/**
 * The ScheduleDAO is an interface with the SQL database that allows for the insertion and recovery 
 * of a schedule by storing the relevant fields in the appropriate tables.
 * 
 * @author ramusa2, jmfoste2
 *
 */
public class ScheduleDAO {
	
	/**
	 * DAO for accessing semester calendar info
	 */
	private final CalendarDAO calDAO;
	
	/**
	 * DAO for accessing session info
	 */
	private final SessionsDAO sessionsDAO;
	
	
	public ScheduleDAO(Connection conn) {
		calDAO = new CalendarDAO(conn);
		sessionsDAO = new SessionsDAO(conn);
	}
	
	/**
	 * Load a schedule from the database
	 *
	 * @return the loaded schedule (as a ScheduleBean object)
	 * @throws SQLException the SQL exception
	 */
	public ScheduleBean loadSchedule() throws SQLException {
		ScheduleBean schedule = new ScheduleBean(calDAO.loadCalendar());
		List<SessionBean> sessions = sessionsDAO.loadSessions();
		for (SessionBean session : sessions) {
			schedule.addSession(session);
		}
		return schedule;
	}

	/**
	 * Writes a schedule's sessions to the database
	 *
	 * @return the loaded schedule (as a ScheduleBean object)
	 * @throws SQLException the SQL exception
	 */
	public void saveSchedule(ScheduleBean schedule) throws SQLException {
		sessionsDAO.clearSessions();
		for(SessionBean session : schedule.getAllSessions()) {
			sessionsDAO.insertSession(session);
		}
	}
}
