package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bean.SessionBean;
import bean.SessionType;
import bean.Weekday;

/**
 * The Class SessionsDAO.
 * 
 * @author jmfoste2, ramusa2
 */
public class SessionsDAO extends DAO {

	/**
	 * Instantiates a new sessions dao.
	 *
	 * @param conn the conn
	 */
	public SessionsDAO(Connection conn) {
		super(conn);
	}

	/**
	 * Insert given session and its associated clinicians into Sessions and SessionClinicians tables in the database.
	 *
	 * @param session the session
	 * @throws SQLException the SQL exception
	 */
	public void insertSession(SessionBean session) throws SQLException {
		Connection conn = getConnection();
		PreparedStatement stmt = conn.prepareStatement("INSERT INTO Sessions (id, startTime, duration, weekday, sDate, sType, semester, weektype) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
		stmt.setInt(1, session.getID());
		stmt.setInt(2, session.getStartTime());
		stmt.setInt(3, session.getDuration());
		stmt.setString(4, session.getDayOfWeek().toString());
		stmt.setDate(5, new java.sql.Date(session.getDate().getTime()));
		stmt.setInt(6, session.getType().ordinal());
		stmt.setInt(7, session.getSemester());
		stmt.setInt(8, session.getWeekType());
		stmt.execute();
		stmt.close();
		
		insertSessionClinicians(session.getID(), session.getClinicians());
	}
	
	/**
	 * Load all the sessions and their associated clinicians from the Sessions and SessionClincians tables in the database.
	 *
	 * @return the list
	 * @throws SQLException the SQL exception
	 */
	public List<SessionBean> loadSessions() throws SQLException {
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		stmt.execute("SELECT id, startTime, duration, weekday, sDate, sType, semester, weektype FROM Sessions");
		ResultSet results = stmt.getResultSet();
		List<SessionBean> sessions = new ArrayList<SessionBean>();
		while (results.next()) {
			sessions.add(loadSession(results));
		}
		stmt.close();
		return sessions;
	}
	
	/**
	 * Deletes session and its associated clinicians from the Sessions and SessionClincians table in the database.
	 *
	 * @param session the session
	 * @throws SQLException the SQL exception
	 */
	public void deleteSession(SessionBean session) throws SQLException {
		Connection conn = getConnection();
		PreparedStatement stmt = conn.prepareStatement("DELETE FROM Sessions WHERE id = ?");
		stmt.setInt(1, session.getID());
		stmt.execute();
		stmt.close();
		stmt = conn.prepareStatement("DELETE FROM SessionClinicians WHERE sessionID = ?");
		stmt.setInt(1, session.getID());
		stmt.execute();
		stmt.close();
	}
	
	/**
	 * Load session.
	 *
	 * @param results the results
	 * @return the session bean
	 * @throws SQLException the SQL exception
	 */
	private SessionBean loadSession(ResultSet results) throws SQLException {
		int id = results.getInt("id");
		int startTime = results.getInt("startTime");
		int duration = results.getInt("duration");
		String dayOfWeek = results.getString("weekday");
		Date date = results.getDate("sDate");
		int type = results.getInt("sType");
		int semester = results.getInt("semester"); 
		int weekType = results.getInt("weekType");
	
		List<Integer> clinicians = loadSessionClinicians(id);
		
		return new SessionBean(id, startTime, duration, Weekday.valueOf(dayOfWeek), date, SessionType.values()[type], clinicians, semester, weekType);
	}
	
	/**
	 * Inserts the session id, clinician id pairs to the SessionClinicians table in the database.
	 *
	 * @param sessionID the session id
	 * @param clinicians the clinicians
	 * @throws SQLException the SQL exception
	 */
	public void insertSessionClinicians(int sessionID, List<Integer> clinicians) throws SQLException {
		Connection conn = getConnection();
		PreparedStatement stmt = conn.prepareStatement("INSERT INTO SessionClinicians (sessionID, clinicianID) VALUES (?, ?)");
		for (int clinicianID: clinicians) {
			stmt.setInt(1, sessionID);
			stmt.setInt(2, clinicianID);
			stmt.execute();
		}
		stmt.close();
	}
	
	/**
	 * Loads the clinician ids associated with a particular session id.
	 *
	 * @param sessionID the session id
	 * @return the list
	 * @throws SQLException the SQL exception
	 */
	public List<Integer> loadSessionClinicians(int sessionID) throws SQLException {
		Connection conn = getConnection();
		PreparedStatement stmt = conn.prepareStatement("SELECT clinicianID FROM Sessions JOIN SessionClinicians ON Sessions.id = SessionClinicians.sessionID WHERE SessionClinicians.sessionID = ?");
		stmt.setInt(1, sessionID);
		stmt.execute();
		ResultSet results = stmt.getResultSet();
		List<Integer> clinicians = new ArrayList<Integer>();
		while (results.next()) {
			clinicians.add(results.getInt("clinicianID"));
		}
		stmt.close();
		return clinicians;
	}
	
	/**
	 * Gets the next available session id from the Sessions table in the database.
	 *
	 * @return the next session id
	 * @throws SQLException the SQL exception
	 */
	public int getNextSessionID() throws SQLException {
		return DAO.getNextID("Sessions");
	}

	/**
	 * Deletes all sessions from the database
	 */
	public void clearSessions() throws SQLException {
		// TODO: could depend on (what is currently) TestDataGenerator.java
		PreparedStatement stmt = getConnection().prepareStatement("DELETE FROM Sessions");
		stmt.execute();
	}
	
	/**
	 * Finds EC sessions for the current semester whose session does not start at 8am, 12pm, or 4pm
	 * 
	 * @param semester 
	 * @param year
	 * @return List of EC sessions with invalid hours
	 * @throws SQLException
	 */
	public List<SessionBean> getECSessionsWithInvalidHours(int semester, int year) throws SQLException {
		Connection conn = getConnection();
		PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Sessions WHERE (startTime != ? AND startTime != ? AND startTime != ?)"
				+ "AND sType = ? AND semester = ? AND YEAR(sDate) = ?");
		stmt.setInt(1, 8);
		stmt.setInt(2, 12);
		stmt.setInt(3, 16);
		stmt.setInt(4, SessionType.EC.ordinal());
		stmt.setInt(5, semester);
		stmt.setInt(6, year);
		stmt.execute();
		ResultSet results = stmt.getResultSet();
		List<SessionBean> sessions = new ArrayList<SessionBean>();
		while (results.next()) {
			sessions.add(loadSession(results));
		}
		stmt.close();
		return sessions;
	}
	
	/**
	 * Finds IA sessions for the current semester whose session does not start at 
	 * 11am, 1pm, 2pm, or 3pm
	 * 
	 * @param semester 
	 * @param year
	 * @return List of IA sessions with invalid hours
	 * @throws SQLException
	 */
	public List<SessionBean> getICSessionsWithInvalidHours(int semester, int year) throws SQLException {
		Connection conn = getConnection();
		PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Sessions WHERE "
				+ "(startTime != ? AND startTime != ? AND startTime != ? AND startTime != ?)"
				+ "AND sType = ? AND semester = ? AND YEAR(sDate) = ?");
		stmt.setInt(1, 11);
		stmt.setInt(2, 13);
		stmt.setInt(3, 14);
		stmt.setInt(4, 15);
		stmt.setInt(5, SessionType.IA.ordinal());
		stmt.setInt(6, semester);
		stmt.setInt(7, year);
		stmt.execute();
		ResultSet results = stmt.getResultSet();
		List<SessionBean> sessions = new ArrayList<SessionBean>();
		while (results.next()) {
			sessions.add(loadSession(results));
		}
		stmt.close();
		return sessions;
	}
	
}
