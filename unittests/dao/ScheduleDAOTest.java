package dao;

import static org.junit.Assert.assertEquals;
import generator.TestDataGenerator;

import java.sql.Connection;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import action.FillScheduleAction;
import action.GenerateUnfilledScheduleAction;
import bean.SessionNameBean;

/**
 * A JUnit test for the ScheduleDAO class
 * 
 * @author ramusa2, lim92
 *
 */
public class ScheduleDAOTest {

    private Connection conn;
    private TestDataGenerator gen;
    private ScheduleDAO scheduleDAO;

    @Before
    public void setUp() throws Exception {
	conn = ConnectionFactory.getInstance();
	gen = new TestDataGenerator(conn);
	gen.clearTables();
	gen.generateStandardDataset();
	scheduleDAO = new ScheduleDAO(conn);
	GenerateUnfilledScheduleAction action = new GenerateUnfilledScheduleAction(
		conn);
	action.generateUnfilledSchedule();
	FillScheduleAction fillScheduleAction = new FillScheduleAction(conn);
	fillScheduleAction.fillSchedule();
    }

    @After
    public void tearDown() throws Exception {
	gen.clearTables();
    }

    @Test
    public void testLoadIASessionsForWeekAEmpty() throws Exception {
	gen.clearTables();
	List<SessionNameBean> ECSessions = scheduleDAO.loadScheduleType(0);
	assertEquals(0, ECSessions.size());
    }

    @Test
    public void testLoadIASessionsForWeekBEmpty() throws Exception {
	gen.clearTables();
	List<SessionNameBean> ECSessions = scheduleDAO.loadScheduleType(1);
	assertEquals(0, ECSessions.size());
    }

    @Test
    public void testLoadECSessionsEmpty() throws Exception {
	gen.clearTables();
	List<SessionNameBean> ECSessions = scheduleDAO.loadScheduleType(2);
	assertEquals(0, ECSessions.size());
    }

    @Test
    public void testLoadIASessionsForWeekA() throws Exception {
	List<SessionNameBean> IASessionsWeekA = scheduleDAO.loadScheduleType(0);
	assertEquals(80, IASessionsWeekA.size());
    }

    @Test
    public void testLoadIASessionsForWeekB() throws Exception {
	List<SessionNameBean> IASessionsWeekB = scheduleDAO.loadScheduleType(1);
	assertEquals(80, IASessionsWeekB.size());
    }

    @Test
    public void testLoadECSessions() throws Exception {
	List<SessionNameBean> ECSessions = scheduleDAO.loadScheduleType(2);
	assertEquals(210, ECSessions.size());
    }
}
