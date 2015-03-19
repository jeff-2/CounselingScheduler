package action;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import generator.TestDataGenerator;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import utils.Logger;
import bean.ClinicianBean;
import bean.ClinicianPreferencesBean;
import bean.Semester;
import bean.SessionBean;
import bean.SessionType;
import bean.Weekday;
import dao.ClinicianDAO;
import dao.ClinicianPreferencesDAO;
import dao.ConnectionFactory;
import dao.SessionsDAO;



public class ValidateScheduleActionTest {

	private String logFileDir = "tempLogs/";
	private ValidateScheduleAction validateAction;
	private Connection conn;
	private TestDataGenerator gen; 
	
	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

	@Before
	public void setUp() throws SQLException, ParseException {
	    System.setOut(new PrintStream(outContent));
		Logger.setDebugStatus(true);
		
	    conn = ConnectionFactory.getInstance();
	    gen = new TestDataGenerator(conn);
	    gen.clearTables();
		gen.generateStandardDataset();
		
        GenerateUnfilledScheduleAction generateAction = new GenerateUnfilledScheduleAction(conn);
		generateAction.generateUnfilledSchedule();
		
		validateAction = new ValidateScheduleAction(conn);
		
	}

	@After
	public void tearDown() throws SQLException {
	    System.setOut(null);
	    File logFile = new File(Logger.getLogDir() + Logger.getLogFileName());
	    logFile.delete();
	    File dir = new File(logFileDir);
	    dir.delete();
	    Logger.setDebugStatus(false);
	    
	    gen.clearTables();
	}
	
	@Test
	public void testValidSchedule() throws SQLException, ParseException {
		gen.clearTables();
		generateValidSchedule();
		
		FillScheduleAction fillScheduleAction = new FillScheduleAction(conn);
		fillScheduleAction.fillSchedule();
		
		validateAction.validateSchedule();
		assertEquals("", outContent.toString());
	}
	
	@Test
	public void testInvalidSchedule() throws SQLException, ParseException {
		generateInvalidData();
		validateAction.validateSchedule();
		assertNotEquals("", outContent.toString());
	}
	
	private void generateInvalidData() throws SQLException, ParseException {
		SessionsDAO sessionsDAO = new SessionsDAO(conn);
		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
		sessionsDAO.insertSession(new SessionBean(10000, 11, 1, Weekday.Monday, format.parse("03/14/2015"), 
				SessionType.IA, Arrays.asList(0), Semester.Fall.ordinal(), 1)); 
		sessionsDAO.insertSession(new SessionBean(101000, 13, 1, Weekday.Monday, format.parse("03/14/2015"), 
				SessionType.IA, Arrays.asList(0), Semester.Fall.ordinal(), 1)); 
	}
	
	private void generateValidSchedule() throws ParseException, SQLException {
		gen.generateEmptySemesterDataset();
		
		ClinicianDAO clinicianDAO = new ClinicianDAO(conn);
		clinicianDAO.insert(new ClinicianBean(0, "Jeff"));
		clinicianDAO.insert(new ClinicianBean(1, "Ryan"));
		
		ClinicianPreferencesDAO clinicianPreferencesDAO = new ClinicianPreferencesDAO(conn);
		clinicianPreferencesDAO.insert(new ClinicianPreferencesBean(0, 2, 1, 3));
		clinicianPreferencesDAO.insert(new ClinicianPreferencesBean(1, 1, 2, 3));
		
		FillScheduleAction fillScheduleAction = new FillScheduleAction(conn);
		fillScheduleAction.fillSchedule();
	}

}
