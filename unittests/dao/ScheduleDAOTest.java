package dao;

import generator.TestDataGenerator;

import java.sql.Connection;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bean.SessionNameBean;
import action.FillScheduleAction;
import action.GenerateUnfilledScheduleAction;

/**
 * A JUnit test for the ScheduleDAO class
 * 
 * @author ramusa2, lim92
 *
 */
public class ScheduleDAOTest {
	
	private Connection conn;
	private TestDataGenerator gen;
	
	/*@Before
	public void setUp() throws Exception {
		conn = ConnectionFactory.getInstance();
		gen = new TestDataGenerator(conn);
		gen.clearTables();
		gen.generateStandardDataset();
		GenerateUnfilledScheduleAction action = new GenerateUnfilledScheduleAction(conn);
		action.generateUnfilledSchedule();
		FillScheduleAction fillScheduleAction = new FillScheduleAction(conn);
		fillScheduleAction.fillSchedule();
	}

	@After
	public void tearDown() throws Exception {
		gen.clearTables();
	}
	
	@Test
	public void testLoadIASessionsForWeekA() throws Exception {
		ScheduleDAO scheduleDAO = new ScheduleDAO(conn);
		List<SessionNameBean> IASessionsWeekA = scheduleDAO.loadScheduleType(0);
		System.out.println("This is week A");
		for(SessionNameBean b : IASessionsWeekA) {
			System.out.println(b.getClinicianName() + " " + b.getDayOfWeek());
		}
	}
	
	@Test
	public void testLoadIASessionsForWeekB() throws Exception {
		ScheduleDAO scheduleDAO = new ScheduleDAO(conn);
		List<SessionNameBean> IASessionsWeekB = scheduleDAO.loadScheduleType(1);
		System.out.println("This is week B");
		for(SessionNameBean b : IASessionsWeekB) {
			System.out.println(b.getClinicianName() + " " + b.getDayOfWeek());
		}
	}
	
	@Test
	public void testLoadECSessions() throws Exception {
		ScheduleDAO scheduleDAO = new ScheduleDAO(conn);
		List<SessionNameBean> ECSessions = scheduleDAO.loadScheduleType(2);
		System.out.println("These are the EC Sessions");
		for(SessionNameBean b : ECSessions) {
			System.out.println(b.getClinicianName() + " " + b.getDate());
		}
	}*/
}
