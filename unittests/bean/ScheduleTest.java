package bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import generator.TestDataGenerator;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import linearprogram.Week;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import dao.ConnectionFactory;

/**
 * The Class ScheduleTest tests the functionality of Schedule.
 */
public class ScheduleTest {

    /** The conn. */
    private Connection conn;
    
    /** The gen. */
    private TestDataGenerator gen;

    /**
     * Sets the test up.
     *
     * @throws Exception the exception
     */
    @Before
    public void setUp() throws Exception {
	conn = ConnectionFactory.getInstance();
	gen = new TestDataGenerator(conn);
	gen.clearTables();
	gen.generateStandardDataset();
    }

    /**
     * Test loading schedule from database.
     *
     * @throws SQLException the SQL exception
     */
    @Test
    public void testLoadScheduleFromDatabase() throws SQLException {
	Schedule schedule = Schedule.loadScheduleFromDB();
	assertTrue(schedule.getCalendar() != null);
	assertTrue(schedule.getClinicians() != null);
	assertTrue(schedule.getCommitment(schedule.getClinicians().get(0)) != null);
	assertTrue(schedule.getHolidays() != null);
	assertTrue(schedule.getNumberOfWeeks() == 16);
    }

    /**
     * Test loading of maps.
     *
     * @throws SQLException the SQL exception
     */
    @Test
    public void testLoadMaps() throws SQLException {
	Schedule schedule = Schedule.loadScheduleFromDBAndAssignClinicians();
	assertEquals(schedule.getECScheduleMap().size(), 16);
	assertEquals(schedule.getIAScheduleMap().size(), 2);
	assertEquals(schedule.getMapOfCliniciansToSessions().size(), schedule
		.getClinicians().size());
    }

    /**
     * Test assignment of clinicians to schedule is done.
     *
     * @throws SQLException the SQL exception
     */
    @Test
    public void testDidAssignment() throws SQLException {
	Schedule schedule = Schedule.loadScheduleFromDBAndAssignClinicians();
	for (SessionBean sb : schedule.getSessions()) {
	    assert (sb.getClinicians().size() != 0);
	}
    }

    /**
     * Test session name bean list fills.
     *
     * @throws SQLException the SQL exception
     */
    @Test
    public void testSessionNameBeanListFills() throws SQLException {
	Schedule schedule = Schedule.loadScheduleFromDBAndAssignClinicians();
	assertTrue(schedule.getECSessions().size() != 0);
	assertTrue(schedule.getIASessionsA().size() != 0);
	assertTrue(schedule.getIASessionsB().size() != 0);
    }

    /**
     * Test edit ec schedule.
     *
     * @throws SQLException the SQL exception
     * @throws ParseException the parse exception
     */
    @Test
    public void testEditEC() throws SQLException, ParseException {
	Schedule schedule = Schedule.loadScheduleFromDBAndAssignClinicians();

	Date d = schedule.getCalendar().getStartDate();
	int day = Weekday.getIndexOfDay(Weekday.dayName(d));
	schedule.editEC(d, 8, "Nymphadora");
	assertTrue(schedule
		.getECClinician(
			schedule.getWeeks().indexOf(
				Week.getWeek(d, schedule.getCalendar())), day,
			8).getClinicianBean().getName().equals("Nymphadora"));

	schedule.editEC(d, 8, "Alice");
	assertTrue(schedule
		.getECClinician(
			schedule.getWeeks().indexOf(
				Week.getWeek(d, schedule.getCalendar())), day,
			8).getClinicianBean().getName().equals("Alice"));
    }

    /**
     * Test removing an ia clinician.
     *
     * @throws SQLException the SQL exception
     */
    @Test
    public void removeIA() throws SQLException {
	Schedule schedule = Schedule.loadScheduleFromDBAndAssignClinicians();
	List<Clinician> iaClinicianList = schedule.getIAClinician(true, 0, 13);
	String clinicianName = iaClinicianList.get(0).getClinicianBean()
		.getName();

	assertTrue(searchForClinician(iaClinicianList, clinicianName));
	schedule.removeIAClinician(true, 0, 13, clinicianName);
	assertTrue(!searchForClinician(iaClinicianList, clinicianName));
    }

    /**
     * Tests adding an ia clinician.
     *
     * @throws SQLException the SQL exception
     */
    @Test
    public void addIA() throws SQLException {
	Schedule schedule = Schedule.loadScheduleFromDBAndAssignClinicians();
	List<Clinician> iaClinicianList = schedule.getIAClinician(false, 1, 11);
	String clinicianName = "Igor";

	assertTrue(!searchForClinician(iaClinicianList, clinicianName));
	schedule.addIAClinician(false, 1, 11, clinicianName);
	assertTrue(searchForClinician(iaClinicianList, clinicianName));
    }

    /**
     * Helper method to look through the clincian IA assignments for a
     * particular day.
     *
     * @param clist            List of clinicians assigned to a day session
     * @param clinicianName the clinician name
     * @return true if clinician is assigned for that day, false otherwise
     */
    private boolean searchForClinician(List<Clinician> clist,
	    String clinicianName) {
	boolean foundClinician = false;
	for (Clinician c : clist) {
	    String cName = c.getClinicianBean().getName();
	    if (cName.equals(clinicianName)) {
		foundClinician = true;
	    }
	}
	return foundClinician;
    }

    /**
     * Tear down.
     *
     * @throws Exception the exception
     */
    @After
    public void tearDown() throws Exception {
	gen.clearTables();
    }
}
