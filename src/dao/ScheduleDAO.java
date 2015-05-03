package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import bean.SessionNameBean;
import bean.Weekday;

/**
 * The ScheduleDAO is an interface with the SQL database that allows for the
 * insertion and recovery of a schedule by storing the relevant fields in the
 * appropriate tables.
 * 
 * @author ramusa2, jmfoste2
 *
 */
public class ScheduleDAO extends DAO {

    /**
     * Instantiates a new schedule dao.
     *
     * @param conn the conn
     */
    public ScheduleDAO(Connection conn) {
	super(conn);
    }

    /**
     * Gets all the assigned EC sessions from the database.
     *
     * @return List of SessionNameBean
     * @throws SQLException the SQL exception
     */
    private List<SessionNameBean> loadAllECSessions() throws SQLException {
	PreparedStatement stmt = connection
		.prepareStatement("SELECT Sessions.startTime, Sessions.weekday, Sessions.sDate, Sessions.weektype, Clinicians.name, Sessions.id"
			+ " FROM SessionClinicians SessionClinicians"
			+ " INNER JOIN Sessions Sessions"
			+ " ON SessionClinicians.sessionID=Sessions.id"
			+ " INNER JOIN Clinicians Clinicians"
			+ " ON SessionClinicians.clinicianID=Clinicians.id"
			+ " WHERE sType=1");
	stmt.execute();
	ResultSet results = stmt.getResultSet();
	List<SessionNameBean> ECSessions = new ArrayList<SessionNameBean>();
	while (results.next()) {
	    ECSessions.add(new SessionNameBean(results.getString("name"),
		    results.getInt("startTime"), Weekday.valueOf(results
			    .getString("weekday")), results.getDate("sDate"),
		    results.getInt("weektype"), results.getInt("id")));
	}
	stmt.close();
	return ECSessions;
    }

    /**
     * Gets a week's worth (A or B) of assigned IA Sessions.
     *
     * @param weekType the week type
     * @return the list
     * @throws SQLException the SQL exception
     */
    private List<SessionNameBean> loadAllIASessions(int weekType)
	    throws SQLException {
	PreparedStatement stmt = connection
		.prepareStatement("SELECT Sessions.startTime, Sessions.weekday, Sessions.sDate, Sessions.weektype, Clinicians.name, Sessions.id"
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
	while (results.next()) {
	    IASessions.add(new SessionNameBean(results.getString("name"),
		    results.getInt("startTime"), Weekday.valueOf(results
			    .getString("weekday")), results.getDate("sDate"),
		    results.getInt("weektype"), results.getInt("id")));
	}
	stmt.close();
	return IASessions;
    }

    /**
     * Wrapper method for loading the assigned clinicians to sessions.
     *
     * @param scheduleType the schedule type
     * @return the list
     * @throws SQLException the SQL exception
     */
    public List<SessionNameBean> loadScheduleType(int scheduleType)
	    throws SQLException {
	List<SessionNameBean> schedule = null;
	if (scheduleType == 0) { // week A of IA sessions
	    schedule = loadIAScheduleType(0);
	} else if (scheduleType == 1) { // week B of IA sessions
	    schedule = loadIAScheduleType(0);
	} else if (scheduleType == 2) { // All weeks of EC sessions
	    schedule = loadAllECSessions();
	}
	return schedule;
    }

    /**
     * Helper method to load a specific type of IA schedule.
     *
     * @param scheduleType the schedule type
     * @return the list
     * @throws SQLException the SQL exception
     */
    private List<SessionNameBean> loadIAScheduleType(int scheduleType)
	    throws SQLException {
	List<SessionNameBean> temp = loadAllIASessions(scheduleType);
	if (temp.isEmpty()) {
	    return temp;
	}
	int i = 0;
	Weekday firstDay = temp.get(i).getDayOfWeek();
	while (temp.get(i).getDayOfWeek().equals(firstDay)) {
	    i++;
	}
	while (!temp.get(i).getDayOfWeek().equals(firstDay)) {
	    i++;
	}
	List<SessionNameBean> schedule = temp.subList(0, i);
	return schedule;
    }
}
