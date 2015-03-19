package action;

import static org.junit.Assert.assertEquals;
import generator.TestDataGenerator;

import java.sql.Connection;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bean.SessionBean;
import bean.SessionType;
import dao.ConnectionFactory;
import dao.SessionsDAO;

/**
 * 
 * @author dtli2, lim92
 *
 */
public class FillScheduleActionTest {
	
	private Connection conn;
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
