package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import bean.SessionNameBean;

/**
 * The ScheduleDAO is an interface with the SQL database that allows for the insertion and recovery 
 * of a schedule by storing the relevant fields in the appropriate tables.
 * 
 * @author ramusa2, jmfoste2
 *
 */
public class ScheduleDAO extends DAO {
	
	public ScheduleDAO(Connection conn) {
		super(conn);
	}
	
	/**
	 * Gets all the assigned EC sessions from the database
	 * 
	 * @return List of SessionNameBean
	 * @throws SQLException
	 */
	private List<SessionNameBean> loadAllECSessions() throws SQLException {
		PreparedStatement stmt = connection.prepareStatement(
				"SELECT Sessions.startTime, Sessions.weekday, Sessions.sDate, Sessions.weektype, Clinicians.name, Sessions.id"
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
					results.getInt("weektype"),
					results.getInt("id")));
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
		PreparedStatement stmt = connection.prepareStatement(
				"SELECT Sessions.startTime, Sessions.weekday, Sessions.sDate, Sessions.weektype, Clinicians.name, Sessions.id"
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
					results.getInt("weektype"),
					results.getInt("id")));
		}
		stmt.close();
		return IASessions;
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
			schedule = loadIAScheduleType(0);
		}
		else if(scheduleType == 1) { //week B of IA sessions
			schedule = loadIAScheduleType(0);
		}
		else if(scheduleType == 2) { //All weeks of EC sessions
			schedule = loadAllECSessions();
		}
		return schedule;
	}
	
	/**
	 * Helper method to load a specific type of IA schedule
	 * 
	 * @param 0 refers to week A of IA sessions, 1 refers to week B of IA sessions 
	 * @return
	 * @throws SQLException
	 */
	private List<SessionNameBean> loadIAScheduleType(int scheduleType) throws SQLException {
		List<SessionNameBean> temp = loadAllIASessions(scheduleType);
		if (temp.isEmpty()) {
			return temp;
		}
		int i=0;
		String firstDay = temp.get(i).getDayOfWeek();
		while(temp.get(i).getDayOfWeek().equals(firstDay)) {
			i++;
		}
		while(!temp.get(i).getDayOfWeek().equals(firstDay)) {
			i++;
		}
		List<SessionNameBean> schedule = temp.subList(0, i);
		return schedule;
	}
}
