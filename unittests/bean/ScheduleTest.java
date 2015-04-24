package bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import generator.TestDataGenerator;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import action.GenerateUnfilledScheduleAction;
import dao.ClinicianPreferencesDAO;
import dao.CommitmentsDAO;
import dao.ConnectionFactory;
import dao.TimeAwayDAO;

public class ScheduleTest {

	
	private Connection conn;
	private TestDataGenerator gen;
		
	@Before
	public void setUp() throws Exception {
		conn = ConnectionFactory.getInstance();
		gen = new TestDataGenerator(conn);
		gen.clearTables();
		gen.generateStandardDataset();
	}
	
	@Test
	public void testLoadScheduleFromDatabase() throws SQLException {
		Schedule schedule = Schedule.loadScheduleFromDB();
		assert(schedule.getCalendar() != null);
		assert(schedule.getClinicians() != null);
		assert(schedule.getCommitment(schedule.getClinicians().get(0)) != null);
		assert(schedule.getHolidays() != null);
		assert(schedule.getNumberOfWeeks() == 16);
	}
	
	@Test
	public void testLoadMaps() throws SQLException {
		Schedule schedule = Schedule.loadScheduleFromDBAndAssignClinicians();
		assertEquals(schedule.getECScheduleMap().size(), 16);
		assertEquals(schedule.getIAScheduleMap().size(), 2);
		assertEquals(schedule.getMapOfCliniciansToSessions().size(), schedule.getClinicians().size());
	}
	
	@Test
	public void testDidAssignment() throws SQLException {
		Schedule schedule = Schedule.loadScheduleFromDBAndAssignClinicians();
		for (SessionBean sb : schedule.getSessions()) {
			assert(sb.getClinicians().size() != 0);
		}
	}
	
	@Test
	public void testSessionNameBeanListFills() throws SQLException {
		Schedule schedule = Schedule.loadScheduleFromDBAndAssignClinicians();
		assert(schedule.getECSessions().size() != 0);
		assert(schedule.getIASessionsA().size() != 0);
		assert(schedule.getIASessionsB().size() != 0);
	}
	
	@Test
	public void testEditEC() throws SQLException {
		Schedule schedule = Schedule.loadScheduleFromDBAndAssignClinicians();
		//System.out.println()
	}
	
	@After
	public void tearDown() throws Exception {
		gen.clearTables();
	}
}
