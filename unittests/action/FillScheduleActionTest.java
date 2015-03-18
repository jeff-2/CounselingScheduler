package action;

import static org.junit.Assert.assertEquals;
import generator.TestDataGenerator;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bean.ClinicianBean;
import bean.ClinicianPref;
import bean.ClinicianPreferencesBean;
import bean.CommitmentBean;
import bean.SessionBean;
import bean.SessionType;
import bean.TimeAwayBean;
import dao.ClinicianDAO;
import dao.ClinicianPreferencesDAO;
import dao.CommitmentsDAO;
import dao.ConnectionFactory;
import dao.SessionsDAO;
import dao.TimeAwayDAO;

/**
 * 
 * @author dtli2, lim92
 *
 */
public class FillScheduleActionTest {
	
	private ClinicianPreferencesAction action;
	private Connection conn;
	private List<SessionBean> sessions;
	private ClinicianPreferencesBean preferences;
	private ClinicianPreferencesDAO clinicianPreferencesDAO;
	private TestDataGenerator gen;
	
	@Before
	public void setUp() throws Exception {
		conn = ConnectionFactory.getInstance();
		gen = new TestDataGenerator(conn);
		gen.clearTables();
		gen.generateStandardDataset();
		GenerateUnfilledScheduleAction action = new GenerateUnfilledScheduleAction(conn);
		action.generateUnfilledSchedule();
	}
	
	@Test
	public void iaSessionsHaveMultipleCliniciansTest() throws Exception{
		FillScheduleAction fillScheduleAction = new FillScheduleAction(conn);
		fillScheduleAction.fillSchedule();
		
		SessionsDAO sessionsDAO = new SessionsDAO(conn);
		List<SessionBean> allSessions = sessionsDAO.loadSessions();
		int numOfNotThreeOrFive = 0;
		for (SessionBean sb : allSessions) {
			if (sb.getType().equals(SessionType.IA)) {
				int numClinicians = sessionsDAO.loadSessionClinicians(sb.getID()).size();
				if (!(numClinicians == 3 || numClinicians == 5)) {
					numOfNotThreeOrFive++;
				}
			}
		}
		assertEquals(0, numOfNotThreeOrFive);
	}
	
	@Test
	public void ecSessionsOneToOneTest() throws Exception{
		FillScheduleAction fillScheduleAction = new FillScheduleAction(conn);
		fillScheduleAction.fillSchedule();
		
		SessionsDAO sessionsDAO = new SessionsDAO(conn);
		List<SessionBean> allSessions = sessionsDAO.loadSessions();
		for (SessionBean sb : allSessions) {
			if (sb.getType().equals(SessionType.EC)) {
				assertEquals(1, sessionsDAO.loadSessionClinicians(sb.getID()).size());
			}
		}
			
	}
	
	@After
	public void tearDown() throws Exception {
		gen.clearTables();
	}
}
