package bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import generator.TestDataGenerator;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.swing.DefaultListModel;

import linearprogram.Week;

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
	public void testEditEC() throws SQLException, ParseException {
		Schedule schedule = Schedule.loadScheduleFromDBAndAssignClinicians();

		Date d = schedule.getCalendar().getStartDate();
		schedule.editEC(d, 8, "Nymphadora");
		assert(schedule.getECClinician(schedule.getWeeks()
				.indexOf(Week.getWeek(d, schedule.getCalendar())), 0, 8).equals("Nymphadora"));

		schedule.editEC(d, 8, "Alice");
		assert(schedule.getECClinician(schedule.getWeeks()
				.indexOf(Week.getWeek(d, schedule.getCalendar())), 0, 8).equals("Alice"));
	}
	
	@Test
	public void removeIA() throws SQLException {
		Schedule schedule = Schedule.loadScheduleFromDBAndAssignClinicians();
		List<Clinician> iaClinicianList = schedule.getIAClinician(true, 0, 13);
		String clinicianName = "Eric";
		
		assert(searchForClinician(iaClinicianList, clinicianName));
		schedule.removeIAClinician(true, 0, 13, clinicianName);
		assert(!searchForClinician(iaClinicianList, clinicianName));
	}
	
	@Test
	public void addIA() throws SQLException {
		Schedule schedule = Schedule.loadScheduleFromDBAndAssignClinicians();
		List<Clinician> iaClinicianList = schedule.getIAClinician(false, 1, 11);		
		String clinicianName = "Igor";
		
		assert(!searchForClinician(iaClinicianList, clinicianName));
		schedule.addIAClinician(false, 1, 11, clinicianName);
		assert(searchForClinician(iaClinicianList, clinicianName));
	}
	
	/**
	 * Helper method to look through the clincian IA assignments for a particular day
	 * @param clist List of clinicians assigned to a day session
	 * @param clinicianName
	 * @return true if clinician is assigned for that day, false otherwise
	 */
	private boolean searchForClinician(List<Clinician> clist, String clinicianName) {
		boolean foundClinician = false;
		for (Clinician c : clist) {
			String cName = c.getClinicianBean().getName();
			if (cName.equals(clinicianName)) {
				foundClinician = true;
			}
		}
		return foundClinician;
	}
	
	@After
	public void tearDown() throws Exception {
		gen.clearTables();
	}
}
