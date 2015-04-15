package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bean.Semester;
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
		PreparedStatement stmt = connection.prepareStatement("INSERT INTO Sessions (id, startTime, duration, weekday, sDate, sType, semester, weektype) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
		stmt.setInt(1, session.getID());
		stmt.setInt(2, session.getStartTime());
		stmt.setInt(3, session.getDuration());
		stmt.setString(4, session.getDayOfWeek().toString());
		stmt.setDate(5, new java.sql.Date(session.getDate().getTime()));
		stmt.setInt(6, session.getType().ordinal());
		stmt.setInt(7, session.getSemester().ordinal());
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
		Statement stmt = connection.createStatement();
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
		PreparedStatement stmt = connection.prepareStatement("DELETE FROM Sessions WHERE id = ?");
		stmt.setInt(1, session.getID());
		stmt.execute();
		stmt.close();
		stmt = connection.prepareStatement("DELETE FROM SessionClinicians WHERE sessionID = ?");
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
		Semester semester = Semester.values()[results.getInt("semester")]; 
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
		PreparedStatement stmt = connection.prepareStatement("INSERT INTO SessionClinicians (sessionID, clinicianID) VALUES (?, ?)");
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
		PreparedStatement stmt = connection.prepareStatement("SELECT clinicianID FROM Sessions JOIN SessionClinicians ON Sessions.id = SessionClinicians.sessionID WHERE SessionClinicians.sessionID = ?");
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
		return getNextID("Sessions");
	}

	/**
	 * Deletes all sessions from the database
	 */
	public void clearSessions() throws SQLException {
		PreparedStatement stmt = connection.prepareStatement("DELETE FROM Sessions");
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
	public List<SessionBean> getInvalidECSessions(int semester, int year) throws SQLException {
		PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Sessions WHERE (startTime != ? AND startTime != ? AND startTime != ?)"
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
	 * Finds IA sessions for the current semester whose session does not start at 11am, 1pm, 2pm, or 3pm
	 * 
	 * @param semester 
	 * @param year
	 * @return List of IA sessions with invalid hours
	 * @throws SQLException
	 */
	public List<SessionBean> getInvalidIASessions(Semester semester, int year) throws SQLException {
		PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Sessions WHERE "
				+ "(startTime != ? AND startTime != ? AND startTime != ? AND startTime != ?)"
				+ "AND sType = ? AND semester = ? AND YEAR(sDate) = ?");
		stmt.setInt(1, 11);
		stmt.setInt(2, 13);
		stmt.setInt(3, 14);
		stmt.setInt(4, 15);
		stmt.setInt(5, SessionType.IA.ordinal());
		stmt.setInt(6, semester.ordinal());
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
	
	/**
	 * Checks the constraint that no clinician can be scheduled for more than 1 EC session per week
	 * 
	 * @param semester
	 * @param year
	 * @return List of error messages of clinicians that violate the constraint and when it was violated
	 * @throws SQLException
	 */
	public List<String> getWeeklyECSessionConstraintViolation(Semester semester, int year) throws SQLException {
		PreparedStatement stmt = connection.prepareStatement(""
				+ "SELECT weektype, C.id id "
				+ "FROM Sessions S INNER JOIN SessionClinicians SC "
				+ "ON S.id = SC.sessionID INNER JOIN Clinicians C "
				+ "ON SC.clinicianID = C.id "
				+ "WHERE semester = ? AND year(sDate) = ? AND sType = ? "
				+ "GROUP BY weektype, C.id "
				+ "HAVING COUNT(C.id) > 1 ");
		stmt.setInt(1, semester.ordinal());
		stmt.setInt(2, year);
		stmt.setInt(3, SessionType.EC.ordinal());
		stmt.execute();
		ResultSet results = stmt.getResultSet();
		List<String> errors = new ArrayList<>();
		while (results.next()) {
			errors.add("Clinician number " + results.getInt("id") + " is assigned more than 1 EC session in week " + results.getInt("weektype"));
		}
		stmt.close();
		return errors;
	}
	
	/**
	 * Checks the constraint that no clinician can be scheduled for more than 1 IA session per day
	 * 
	 * @param semester
	 * @param year
	 * @return List of error messages of clinicians that violate the constraint and when it was violated
	 * @throws SQLException
	 */
	public List<String> getDailyIASessionConstraintViolation(Semester semester, int year) throws SQLException {
		PreparedStatement stmt = connection.prepareStatement(""
				+ "SELECT weektype, weekday, C.id id "
				+ "FROM Sessions S INNER JOIN SessionClinicians SC "
				+ "ON S.id = SC.sessionID INNER JOIN Clinicians C "
				+ "ON SC.clinicianID = C.id "
				+ "WHERE semester = ? AND year(sDate) = ? AND sType = ? "
				+ "GROUP BY weektype, weekday, C.id "
				+ "HAVING COUNT(C.id) > 1 ");
		stmt.setInt(1, semester.ordinal());
		stmt.setInt(2, year);
		stmt.setInt(3, SessionType.IA.ordinal());
		stmt.execute();
		ResultSet results = stmt.getResultSet();
		List<String> errors = new ArrayList<>();
		while (results.next()) {
			errors.add("Clinician number " + results.getInt("id") + " is assigned more than 1 IA session on " + results.getString("weekday") + " of week " + results.getInt("weektype"));
		}
		stmt.close();
		return errors;
	}
	
	/**
	 * Checks the schedule constraint that clinicians who receive a 3pm Friday IA 
	 * session in the Fall do not receive one in the Spring, and vice versa
	 * 
	 * @param semester
	 * @param year
	 * @return List of error messages of clinicians that violate the constraint and when it was violated
	 * @throws SQLException
	 */
	public List<String> getAlternatingIAFridayConstraintViolation(Semester semester, int year) throws SQLException {
		PreparedStatement stmt = connection.prepareStatement(""
				+ "SELECT SC1.clinicianID id "
				+ "FROM Sessions S1 INNER JOIN SessionClinicians SC1 ON S1.id = SC1.sessionID "
				+ "INNER JOIN SessionClinicians SC2 ON SC1.clinicianID = SC2.clinicianID "
				+ "INNER JOIN Sessions S2 ON SC2.sessionID = S2.id "
				+ "WHERE S1.semester = ? AND year(S1.sDate) = ? AND S1.sType = ? "
				+ "AND S2.semester = ? AND year(S2.sDate) = ? AND S2.sType = ? "
				+ "AND S1.weekday = 'Friday' AND S2.weekday = 'Friday' "
				+ "AND S1.startTime = 15 AND S2.startTime = 15 ");
		stmt.setInt(1, semester.ordinal());
		stmt.setInt(2, year);
		stmt.setInt(3, SessionType.IA.ordinal());
		if (semester.equals(Semester.Fall)) {
			stmt.setInt(4, Semester.Spring.ordinal());
			stmt.setInt(5, year);
			stmt.setInt(6, SessionType.IA.ordinal());
		}
		else if (semester.equals(Semester.Spring)) {
			stmt.setInt(4, Semester.Fall.ordinal());
			stmt.setInt(5, year - 1);
			stmt.setInt(6, SessionType.IA.ordinal());
		}
		else {
			stmt.close();
			new ArrayList<String>();
		}
		
		stmt.execute();
		ResultSet results = stmt.getResultSet();
		List<String> errors = new ArrayList<>();
		while (results.next()) {
			errors.add("Clinician number " + results.getInt("id") + " is assigned to a 3 PM Friday IA session in both this semester and last semester.");
		}
		stmt.close();
		return errors;
	}
	
	/**
	 * Checks the schedule constraint that if a clinician is scheduled for a noon EC session,
	 * they are unavailable for a 1pm IA session that same day 
	 * 
	 * @param semester
	 * @param year
	 * @return List of error messages of clinicians that violate the constraint and when it was violated
	 * @throws SQLException
	 */
	public List<String> getNoonECConstraintViolation(Semester semester, int year) throws SQLException {
		PreparedStatement stmt = connection.prepareStatement(""
				+ "SELECT SC1.clinicianID id, S1.sDate as sDate "
				+ "FROM Sessions S1 INNER JOIN SessionClinicians SC1 ON S1.id = SC1.sessionID "
				+ "INNER JOIN SessionClinicians SC2 ON SC1.clinicianID = SC2.clinicianID "
				+ "INNER JOIN Sessions S2 ON SC2.sessionID = S2.id "
				+ "WHERE S1.semester = ? AND year(S1.sDate) = ? AND S1.sType = ? "
				+ "AND S2.semester = ? AND year(S2.sDate) = ? AND S2.sType = ? "
				+ "AND S1.startTime = 12 AND S2.startTime = 13 "
				+ "AND S1.weektype % 2 = S2.weektype % 2 "
				+ "AND S1.weekday = S2.weekday ");
		stmt.setInt(1, semester.ordinal());
		stmt.setInt(2, year);
		stmt.setInt(3, SessionType.EC.ordinal());
		stmt.setInt(4, semester.ordinal());
		stmt.setInt(5, year);
		stmt.setInt(6, SessionType.IA.ordinal());
		
		stmt.execute();
		ResultSet results = stmt.getResultSet();
		List<String> errors = new ArrayList<>();
		while (results.next()) {
			errors.add("Clinician number " + results.getInt("id") + " is assigned to a 12 PM EC session and a 1 PM IA session on " + results.getDate("sDate")+ '.');
		}
		stmt.close();
		return errors;
	}
	
}
