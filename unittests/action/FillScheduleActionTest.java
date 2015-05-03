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
 * The Class FillScheduleActionTest tests the functionality of
 * FillScheduleAction.
 *
 * @author dtli2, lim92
 */
public class FillScheduleActionTest {

	/** The conn. */
	private Connection conn;

	/** The gen. */
	private TestDataGenerator gen;

	/** The sessions dao. */
	private SessionsDAO sessionsDAO;

	/**
	 * Sets the test up.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Before
	public void setUp() throws Exception {
		conn = ConnectionFactory.getInstance();
		gen = new TestDataGenerator(conn);
		gen.clearTables();
		gen.generateStandardDataset();
		sessionsDAO = new SessionsDAO(conn);
		GenerateUnfilledScheduleAction action = new GenerateUnfilledScheduleAction(
				conn);
		action.generateUnfilledSchedule();
		FillScheduleAction fillScheduleAction = new FillScheduleAction(conn);
		fillScheduleAction.fillSchedule();
	}

	/**
	 * Test that ia sessions have multiple clinicians.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void iaSessionsHaveMultipleCliniciansTest() throws Exception {
		List<SessionBean> allSessions = sessionsDAO.loadSessions();
		int numOfNotThreeOrFive = 0;
		for (SessionBean sb : allSessions) {
			if (sb.getType().equals(SessionType.IA)) {
				int numClinicians = sessionsDAO.loadSessionClinicians(
						sb.getID()).size();
				if (!(numClinicians == 3 || numClinicians == 5)) {
					numOfNotThreeOrFive++;
				}
			}
		}
		assertEquals(0, numOfNotThreeOrFive);
	}

	/**
	 * Test that exactly one clinician is assigned to each ce sessions.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void ecSessionsOneToOneTest() throws Exception {
		List<SessionBean> allSessions = sessionsDAO.loadSessions();
		for (SessionBean sb : allSessions) {
			if (sb.getType().equals(SessionType.EC)) {
				assertEquals(1, sessionsDAO.loadSessionClinicians(sb.getID())
						.size());
			}
		}
	}

	/**
	 * Tear down.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@After
	public void tearDown() throws Exception {
		gen.clearTables();
	}
}
