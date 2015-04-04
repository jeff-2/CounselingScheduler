package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bean.CalendarBean;
import bean.HolidayBean;
import bean.ScheduleBean;
import bean.SessionBean;
import bean.SessionNameBean;
import bean.Weekday;

/**
 * The ScheduleDAO is an interface with the SQL database that allows for the insertion and recovery 
 * of a schedule by storing the relevant fields in the appropriate tables.
 * 
 * @author ramusa2, jmfoste2
 *
 */
public class ScheduleDAO extends DAO{
	
	/**
	 * DAO for accessing semester calendar info
	 */
	private final CalendarDAO calDAO;
	
	/**
	 * DAO for accessing session info
	 */
	private final SessionsDAO sessionsDAO;
	
	private final HolidayDAO holidayDAO;
	
	public ScheduleDAO(Connection conn) {
		super(conn);
		calDAO = new CalendarDAO(conn);
		sessionsDAO = new SessionsDAO(conn);
		holidayDAO = new HolidayDAO(conn);
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
	
	/**
	 * Gets all the assigned EC sessions from the database
	 * 
	 * @return List of SessionNameBean
	 * @throws SQLException
	 */
	private List<SessionNameBean> loadAllECSessions() throws SQLException {
		Connection conn = getConnection();
		PreparedStatement stmt = conn.prepareStatement(
				"SELECT Sessions.startTime, Sessions.weekday, Sessions.sDate, Sessions.weektype, Clinicians.name"
				+ " FROM SessionClinicians SessionClinicians"
				+ " INNER JOIN Sessions Sessions"
				+ " ON SessionClinicians.sessionID=Sessions.id"
				+ " INNER JOIN Clinicians Clinicians"
				+ " ON SessionClinicians.clinicianID=Clinicians.id"
				+ " WHERE sType=1");
		stmt.execute();
		ResultSet results = stmt.getResultSet();
		List<SessionNameBean> ECSessions = new ArrayList<SessionNameBean>();
		while(results.next()) {
			ECSessions.add(new SessionNameBean (
					results.getString("name"),
					results.getInt("startTime"),
					results.getString("weekday"),
					results.getDate("sDate"),
					results.getInt("weektype")));
		}
		stmt.close();
		return ECSessions;
	}
	
	/**
	 * Gets a week's worth (A or B) of assigned IA Sessions
	 * 
	 * @param 0 gives week A, 1 gives week B
	 * @return
	 * @throws SQLException
	 */
	private List<SessionNameBean> loadAllIASessions(int weekType) throws SQLException {
		Connection conn = getConnection();
		PreparedStatement stmt = conn.prepareStatement(
				"SELECT Sessions.startTime, Sessions.weekday, Sessions.sDate, Sessions.weektype, Clinicians.name"
				+ " FROM SessionClinicians SessionClinicians"
				+ " INNER JOIN Sessions Sessions"
				+ " ON SessionClinicians.sessionID=Sessions.id"
				+ " INNER JOIN Clinicians Clinicians"
				+ " ON SessionClinicians.clinicianID=Clinicians.id"
				+ " WHERE sType=0 AND weekType=?"
				+ " ORDER BY sDate ASC");
		stmt.setInt(1, weekType);
		stmt.execute();
		ResultSet results = stmt.getResultSet();
		List<SessionNameBean> IASessions = new ArrayList<SessionNameBean>();
		while(results.next()) {
			IASessions.add(new SessionNameBean (
					results.getString("name"),
					results.getInt("startTime"),
					results.getString("weekday"),
					results.getDate("sDate"),
					results.getInt("weektype")));
		}
		stmt.close();
		return IASessions;
	}
	
	/**
	 * Gets the holidays in the current semester
	 * 
	 * @return List of holidayBeans
	 * @throws SQLException
	 */
	public List<HolidayBean> getHolidays() throws SQLException {
		return holidayDAO.loadHolidays();
	}
	
	/**
	 * Wrapper method for loading the assigned clinicians to sessions
	 * 
	 * @param 0 refers to week A of IA sessions, 1 refers to week B of IA sessions, and 2 refers to EC sessions
	 * @return
	 * @throws SQLException
	 */
	public List<SessionNameBean> loadScheduleType(int scheduleType) throws SQLException {
		List<SessionNameBean> schedule = null;
		if(scheduleType == 0) { //week A of IA sessions
			List<SessionNameBean> temp = loadAllIASessions(0);
			int i=0;
			String firstDay = temp.get(i).getDayOfWeek();
			while(temp.get(i).getDayOfWeek().equals(firstDay)) {
				i++;
			}
			while(!temp.get(i).getDayOfWeek().equals(firstDay)) {
				i++;
			}
			schedule = temp.subList(0, i);
		}
		else if(scheduleType == 1) { //week B of IA sessions
			List<SessionNameBean> temp = loadAllIASessions(1);
			int i=0;
			String firstDay = temp.get(i).getDayOfWeek();
			while(temp.get(i).getDayOfWeek().equals(firstDay)) {
				i++;
			}
			while(!temp.get(i).getDayOfWeek().equals(firstDay)) {
				i++;
			}
			schedule = temp.subList(0, i);
		}
		else if(scheduleType == 2) { //All weeks of EC sessions
			schedule = loadAllECSessions();
			System.out.println("\n\nNumber of EC Sessions: " + schedule.size());
		}
		return schedule;
	}
	
	public CalendarBean getCalendarBean() throws SQLException {
		return calDAO.loadCalendar();
	}
}
