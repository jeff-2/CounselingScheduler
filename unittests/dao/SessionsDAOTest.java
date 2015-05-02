package dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import generator.TestDataGenerator;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import validator.DateRangeValidator;
import bean.ClinicianBean;
import bean.IAWeektype;
import bean.Semester;
import bean.SessionBean;
import bean.SessionType;
import bean.Weekday;

public class SessionsDAOTest {

    private SessionsDAO sessionsDAO;
    private Connection conn;
    private TestDataGenerator gen;
    private List<Integer> clinicians;

    @Before
    public void setUp() throws Exception {
	conn = ConnectionFactory.getInstance();
	sessionsDAO = new SessionsDAO(conn);
	gen = new TestDataGenerator(conn);
	gen.clearTables();

	clinicians = new ArrayList<Integer>();
	clinicians.add(0);
	clinicians.add(1);

	ClinicianDAO clinicianDAO = new ClinicianDAO(conn);
	clinicianDAO.insert(new ClinicianBean(0, "Jeff"));
	clinicianDAO.insert(new ClinicianBean(1, "Ryan"));
    }

    @After
    public void tearDown() throws Exception {
	gen.clearTables();
    }

    @Test
    public void testInsertValidSession() throws Exception {

	SessionBean session = new SessionBean(0, 8, 1, Weekday.Wednesday,
		DateRangeValidator.parseDate("2/15/2015"), SessionType.IA,
		clinicians, Semester.Fall, IAWeektype.A);
	sessionsDAO.insertSession(session);

	List<SessionBean> actualSessions = sessionsDAO.loadSessions();
	List<SessionBean> expectedSessions = new ArrayList<SessionBean>();
	expectedSessions.add(session);

	assertEquals(expectedSessions, actualSessions);
    }

    @Test
    public void testLoadSessionsEmpty() throws Exception {
	List<SessionBean> expectedSessions = new ArrayList<SessionBean>();
	List<SessionBean> actualSessions = sessionsDAO.loadSessions();
	assertEquals(expectedSessions, actualSessions);
    }

    @Test
    public void testLoadSessions() throws Exception {
	SessionBean session = new SessionBean(0, 8, 1, Weekday.Wednesday,
		DateRangeValidator.parseDate("3/18/2015"), SessionType.IA,
		clinicians, Semester.Fall, IAWeektype.A);
	List<Integer> otherClinicians = new ArrayList<Integer>();
	otherClinicians.add(1);
	SessionBean otherSession = new SessionBean(1, 10, 1, Weekday.Monday,
		DateRangeValidator.parseDate("3/16/2015"), SessionType.EC,
		otherClinicians, Semester.Fall, IAWeektype.A);
	sessionsDAO.insertSession(session);
	sessionsDAO.insertSession(otherSession);

	List<SessionBean> expectedSessions = new ArrayList<SessionBean>();
	expectedSessions.add(session);
	expectedSessions.add(otherSession);

	List<SessionBean> actualSessions = sessionsDAO.loadSessions();
	assertEquals(expectedSessions, actualSessions);
    }

    @Test
    public void testDeleteValidSession() throws Exception {
	SessionBean session = new SessionBean(0, 8, 1, Weekday.Wednesday,
		DateRangeValidator.parseDate("3/18/2015"), SessionType.IA,
		clinicians, Semester.Fall, IAWeektype.A);
	List<Integer> otherClinicians = new ArrayList<Integer>();
	otherClinicians.add(1);
	SessionBean otherSession = new SessionBean(1, 10, 1, Weekday.Monday,
		DateRangeValidator.parseDate("3/16/2015"), SessionType.EC,
		otherClinicians, Semester.Fall, IAWeektype.A);
	sessionsDAO.insertSession(session);
	sessionsDAO.insertSession(otherSession);
	sessionsDAO.deleteSession(session);

	List<SessionBean> actualSessions = sessionsDAO.loadSessions();
	List<SessionBean> expectedSessions = new ArrayList<SessionBean>();
	expectedSessions.add(otherSession);
	assertEquals(expectedSessions, actualSessions);
    }

    @Test
    public void testInvalidECSessions() throws ParseException, SQLException {
	SessionsDAO sessionsDAO = new SessionsDAO(conn);
	sessionsDAO.insertSession(new SessionBean(0, 20, 1, Weekday.Monday,
		DateRangeValidator.parseDate("03/17/2015"), SessionType.EC,
		new ArrayList<Integer>(), Semester.Fall, IAWeektype.A));

	List<SessionBean> invalidSessions = sessionsDAO.getInvalidECSessions(
		Semester.Fall.ordinal(), 2015);
	assertNotEquals(0, invalidSessions.size());
    }

    @Test
    public void testInvalidIASessions() throws ParseException, SQLException {
	SessionsDAO sessionsDAO = new SessionsDAO(conn);
	sessionsDAO.insertSession(new SessionBean(1, 16, 1, Weekday.Monday,
		DateRangeValidator.parseDate("03/17/2015"), SessionType.IA,
		Arrays.asList(0), Semester.Fall, IAWeektype.A));

	List<SessionBean> invalidSessions = sessionsDAO.getInvalidIASessions(
		Semester.Fall, 2015);
	assertNotEquals(0, invalidSessions.size());
    }

    @Test
    public void testWeeklyECSessionConstraintViolation() throws SQLException,
	    ParseException {
	SessionsDAO sessionsDAO = new SessionsDAO(conn);
	sessionsDAO.insertSession(new SessionBean(0, 8, 1, Weekday.Monday,
		DateRangeValidator.parseDate("03/16/2015"), SessionType.EC,
		Arrays.asList(0), Semester.Spring, IAWeektype.B));
	sessionsDAO.insertSession(new SessionBean(1, 16, 1, Weekday.Wednesday,
		DateRangeValidator.parseDate("03/18/2015"), SessionType.EC,
		Arrays.asList(0), Semester.Spring, IAWeektype.B));

	List<String> invalidSessions = sessionsDAO
		.getWeeklyECSessionConstraintViolation(Semester.Spring, 2015);
	assertEquals(1, invalidSessions.size());
    }

    @Test
    public void testDailyIASessionConstraintViolation() throws SQLException,
	    ParseException {
	SessionsDAO sessionsDAO = new SessionsDAO(conn);
	sessionsDAO.insertSession(new SessionBean(0, 11, 1, Weekday.Monday,
		DateRangeValidator.parseDate("03/16/2015"), SessionType.IA,
		Arrays.asList(0), Semester.Spring, IAWeektype.B));
	sessionsDAO.insertSession(new SessionBean(1, 15, 1, Weekday.Monday,
		DateRangeValidator.parseDate("03/16/2015"), SessionType.IA,
		Arrays.asList(0), Semester.Spring, IAWeektype.B));

	List<String> invalidSessions = sessionsDAO
		.getDailyIASessionConstraintViolation(Semester.Spring, 2015);
	assertEquals(1, invalidSessions.size());
    }

    @Test
    public void testAlternatingIAFridayConstraintViolation()
	    throws SQLException, ParseException {
	SessionsDAO sessionsDAO = new SessionsDAO(conn);
	sessionsDAO.insertSession(new SessionBean(0, 15, 1, Weekday.Friday,
		DateRangeValidator.parseDate("03/16/2015"), SessionType.IA,
		Arrays.asList(0), Semester.Fall, IAWeektype.B));
	sessionsDAO.insertSession(new SessionBean(1, 15, 1, Weekday.Friday,
		DateRangeValidator.parseDate("03/16/2015"), SessionType.IA,
		Arrays.asList(0), Semester.Spring, IAWeektype.B));

	List<String> invalidSessions = sessionsDAO
		.getAlternatingIAFridayConstraintViolation(Semester.Fall, 2015);
	assertEquals(1, invalidSessions.size());
    }

    @Test
    public void testNoonECConstraintViolation() throws SQLException,
	    ParseException {
	SessionsDAO sessionsDAO = new SessionsDAO(conn);
	sessionsDAO.insertSession(new SessionBean(0, 12, 1, Weekday.Friday,
		DateRangeValidator.parseDate("03/16/2015"), SessionType.EC,
		Arrays.asList(0), Semester.Fall, IAWeektype.B));
	sessionsDAO.insertSession(new SessionBean(1, 13, 1, Weekday.Friday,
		DateRangeValidator.parseDate("03/16/2015"), SessionType.IA,
		Arrays.asList(0), Semester.Fall, IAWeektype.B));

	List<String> invalidSessions = sessionsDAO
		.getNoonECConstraintViolation(Semester.Fall, 2015);
	assertEquals(1, invalidSessions.size());
    }
}
