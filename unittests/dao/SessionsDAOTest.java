package dao;

import static org.junit.Assert.assertEquals;
import generator.TestDataGenerator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bean.SessionBean;
import bean.SessionType;
import bean.Weekday;

public class SessionsDAOTest {
	
	private SessionsDAO sessionsDAO;
	private Connection conn;
	private TestDataGenerator gen;
	private List<Integer> clinicians;
	private SimpleDateFormat format;
	
	@Before
	public void setUp() throws Exception {
		conn = ConnectionFactory.getInstance();
		sessionsDAO = new SessionsDAO(conn);
		gen = new TestDataGenerator(conn);
		gen.clearTables();
		
		format = new SimpleDateFormat("MM/dd/yyyy");
		

		clinicians = new ArrayList<Integer>();
		clinicians.add(0);
		clinicians.add(1);
		
		PreparedStatement stmt = conn.prepareStatement("INSERT INTO Clinicians (id, name) VALUES (?, ?), (?, ?)");
		stmt.setInt(1, 0);
		stmt.setString(2, "Jeff");
		stmt.setInt(3, 1);
		stmt.setString(4, "Ryan");
		stmt.execute();
		stmt.close();
	}
	
	@After
	public void tearDown() throws Exception {
		gen.clearTables();
	}
	
	@Test
	public void testInsertValidSession() throws Exception {
		
		SessionBean session = new SessionBean(0, 8, 1, Weekday.Wednesday, format.parse("2/15/2015"), SessionType.IA, clinicians);
		sessionsDAO.insertSession(session);
		
		List<Integer> actualClinicians = new ArrayList<Integer>();
		
		PreparedStatement stmt = conn.prepareStatement("SELECT id, startTime, duration, weekday, sDate, sType FROM Sessions");
		stmt.execute();
		ResultSet results = stmt.getResultSet();
		results.next();
		int id = results.getInt("id");
		int startTime = results.getInt("startTime");
		int duration = results.getInt("duration");
		String weekday = results.getString("weekday");
		Date date = results.getDate("sDate");
		int type = results.getInt("sType");
		stmt.close();
		
		stmt = conn.prepareStatement("SELECT clinicianID FROM Sessions JOIN SessionClinicians ON Sessions.id = SessionClinicians.sessionID WHERE SessionClinicians.sessionID = ?");
		stmt.setInt(1, 0);
		stmt.execute();
		results = stmt.getResultSet();
		results.next();
		actualClinicians.add(results.getInt("clinicianID"));
		results.next();
		actualClinicians.add(results.getInt("clinicianID"));
		stmt.close();
		
		SessionBean actualSession = new SessionBean(id, startTime, duration, Weekday.valueOf(weekday), date, SessionType.values()[type], actualClinicians);
		assertEquals(session, actualSession);
	}
	
	@Test
	public void testLoadSessionsEmpty() throws Exception {
		List<SessionBean> expectedSessions = new ArrayList<SessionBean>();
		List<SessionBean> actualSessions = sessionsDAO.loadSessions();
		assertEquals(expectedSessions, actualSessions);
	}
	
	@Test
	public void testLoadSessions() throws Exception {
		PreparedStatement stmt = conn.prepareStatement("INSERT INTO Sessions (id, startTime, duration, weekday, sDate, sType) VALUES (?, ?, ?, ?, ?, ?), (?, ?, ?, ?, ?, ?)");
		stmt.setInt(1, 0);
		stmt.setInt(2, 8);
		stmt.setInt(3, 1);
		stmt.setString(4, Weekday.Wednesday.toString());
		stmt.setDate(5, new java.sql.Date(format.parse("3/18/2015").getTime()));
		stmt.setInt(6, SessionType.IA.ordinal());
		stmt.setInt(7, 1);
		stmt.setInt(8, 10);
		stmt.setInt(9, 1);
		stmt.setString(10, Weekday.Monday.toString());
		stmt.setDate(11, new java.sql.Date(format.parse("3/16/2015").getTime()));
		stmt.setInt(12, SessionType.EC.ordinal());
		stmt.execute();
		stmt.close();
		
		stmt = conn.prepareStatement("INSERT INTO SessionClinicians (sessionID, clinicianID) VALUES (?, ?), (?, ?), (?, ?)");
		stmt.setInt(1, 0);
		stmt.setInt(2, 0);
		stmt.setInt(3, 0);
		stmt.setInt(4, 1);
		stmt.setInt(5, 1);
		stmt.setInt(6, 1);
		stmt.execute();
		stmt.close();
		
		SessionBean session = new SessionBean(0, 8, 1, Weekday.Wednesday, format.parse("3/18/2015"), SessionType.IA, clinicians);
		
		List<Integer> otherClinicians = new ArrayList<Integer>();
		otherClinicians.add(1);
		SessionBean otherSession = new SessionBean(1, 10, 1, Weekday.Monday, format.parse("3/16/2015"), SessionType.EC, otherClinicians);
		
		List<SessionBean> sessions = new ArrayList<SessionBean>();
		sessions.add(session);
		sessions.add(otherSession);
		
		List<SessionBean> actualSessions = sessionsDAO.loadSessions();
		assertEquals(sessions, actualSessions);
	}
	
	@Test
	public void testDeleteValidSession() throws Exception {
		PreparedStatement stmt = conn.prepareStatement("INSERT INTO Sessions (id, startTime, duration, weekday, sDate, sType) VALUES (?, ?, ?, ?, ?, ?), (?, ?, ?, ?, ?, ?)");
		stmt.setInt(1, 0);
		stmt.setInt(2, 8);
		stmt.setInt(3, 1);
		stmt.setString(4, Weekday.Wednesday.toString());
		stmt.setDate(5, new java.sql.Date(format.parse("3/18/2015").getTime()));
		stmt.setInt(6, SessionType.IA.ordinal());
		stmt.setInt(7, 1);
		stmt.setInt(8, 10);
		stmt.setInt(9, 1);
		stmt.setString(10, Weekday.Monday.toString());
		stmt.setDate(11, new java.sql.Date(format.parse("3/16/2015").getTime()));
		stmt.setInt(12, SessionType.EC.ordinal());
		stmt.execute();
		stmt.close();
		
		stmt = conn.prepareStatement("INSERT INTO SessionClinicians (sessionID, clinicianID) VALUES (?, ?), (?, ?), (?, ?)");
		stmt.setInt(1, 0);
		stmt.setInt(2, 0);
		stmt.setInt(3, 0);
		stmt.setInt(4, 1);
		stmt.setInt(5, 1);
		stmt.setInt(6, 1);
		stmt.execute();
		stmt.close();
		
		SessionBean session = new SessionBean(0, 8, 1, Weekday.Wednesday, format.parse("3/18/2015"), SessionType.IA, clinicians);
		
		List<Integer> otherClinicians = new ArrayList<Integer>();
		otherClinicians.add(1);
		SessionBean otherSession = new SessionBean(1, 10, 1, Weekday.Monday, format.parse("3/16/2015"), SessionType.EC, otherClinicians);
		
		sessionsDAO.deleteSession(session);
		
		stmt = conn.prepareStatement("SELECT id, startTime, duration, weekday, sDate, sType FROM Sessions");
		stmt.execute();
		ResultSet results = stmt.getResultSet();
		results.next();
		int id = results.getInt("id");
		int startTime = results.getInt("startTime");
		int duration = results.getInt("duration");
		String weekday = results.getString("weekday");
		Date date = results.getDate("sDate");
		int type = results.getInt("sType");
		stmt.close();
		
		List<Integer> actualClinicians = new ArrayList<Integer>();
		
		stmt = conn.prepareStatement("SELECT clinicianID FROM Sessions JOIN SessionClinicians ON Sessions.id = SessionClinicians.sessionID WHERE SessionClinicians.sessionID = ?");
		stmt.setInt(1, 1);
		stmt.execute();
		results = stmt.getResultSet();
		results.next();
		actualClinicians.add(results.getInt("clinicianID"));
		stmt.close();
		
		SessionBean actualSession = new SessionBean(id, startTime, duration, Weekday.valueOf(weekday), date, SessionType.values()[type], actualClinicians);
		assertEquals(otherSession, actualSession);
	}
}
