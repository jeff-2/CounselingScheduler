package linearprogram;

import static org.junit.Assert.assertTrue;
import generator.TestDataGenerator;

import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import utils.Logger;
import bean.Clinician;
import bean.ClinicianWeekBean;
import bean.Schedule;
import bean.SessionBean;
import bean.SessionType;
import bean.Weekday;

/**
 * The Class ScheduleProgramTest tests the functionality of the ScheduleProgram.
 */
public class ScheduleProgramTest {

	/** The schedule. */
	private Schedule schedule;

	/**
	 * Sets the test up.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Before
	public void setUp() throws Exception {
		TestDataGenerator.overwriteAndFillDemoData();
		schedule = Schedule.loadScheduleFromDBAndAssignClinicians();
	}

	/**
	 * Tear down.
	 *
	 * @throws SQLException
	 *             the SQL exception
	 */
	@After
	public void tearDown() throws SQLException {
		System.setOut(null);
		File logFile = new File(Logger.getLogDir() + Logger.getLogFileName());
		logFile.delete();
		File dir = new File(Logger.getLogDir());
		dir.delete();
		Logger.setDebugStatus(false);
	}

	/**
	 * Test that all ec sessions are filled.
	 *
	 * @throws SQLException
	 *             the SQL exception
	 */
	@Test
	public void testAllECSessionsFilled() throws SQLException {
		boolean allFilled = true;
		for (SessionBean session : schedule.getSessions()) {
			if (session.getType() == SessionType.EC) {
				allFilled = allFilled && session.getClinicians().size() == 1;
			}
		}
		assertTrue(allFilled);
	}

	/**
	 * Test that all ia sessions are filled.
	 *
	 * @throws SQLException
	 *             the SQL exception
	 */
	@Test
	public void testAllIASessionsFilled() throws SQLException {
		boolean allFilled = true;
		for (SessionBean session : schedule.getSessions()) {
			if (session.getType() == SessionType.EC) {
				allFilled = allFilled
						&& session.getClinicians().size() >= session
								.getDuration();
			}
		}
		assertTrue(allFilled);
	}

	/**
	 * Test that each clinician has no more than one ec per week.
	 *
	 * @throws SQLException
	 *             the SQL exception
	 */
	@Test
	public void eachClinicianHasNoMoreThanOneECPerWeek() throws SQLException {
		boolean passed = true;
		HashMap<Clinician, List<ClinicianWeekBean>> map = schedule
				.getMapOfCliniciansToSessions();
		for (Clinician clinician : map.keySet()) {
			for (ClinicianWeekBean wb : map.get(clinician)) {
				int numECs = 0;
				for (SessionBean session : wb.getSessions()) {
					if (session.getType() == SessionType.EC) {
						numECs++;
					}
				}
				passed = passed && numECs <= 1;
			}
		}
		assertTrue(passed);
	}

	/**
	 * Test that each clinician has no more than one ia per day.
	 *
	 * @throws SQLException
	 *             the SQL exception
	 */
	@Test
	public void eachClinicianHasNoMoreThanOneIAPerDay() throws SQLException {
		boolean passed = true;
		HashMap<Clinician, List<ClinicianWeekBean>> map = schedule
				.getMapOfCliniciansToSessions();
		for (Clinician clinician : map.keySet()) {
			for (ClinicianWeekBean wb : map.get(clinician)) {
				HashMap<Weekday, Integer> dayMap = new HashMap<Weekday, Integer>();
				for (SessionBean session : wb.getSessions()) {
					if (session.getType() == SessionType.IA) {
						Integer dayIAs = dayMap.get(session.getDayOfWeek());
						if (dayIAs == null) {
							dayIAs = 0;
						}
						dayMap.put(session.getDayOfWeek(), dayIAs + 1);
					}
				}
				for (Weekday day : dayMap.keySet()) {
					passed = passed && dayMap.get(day) <= 1;
				}
			}
		}
		assertTrue(passed);
	}

	/**
	 * Test that each clinician has no time conflicts.
	 *
	 * @throws SQLException
	 *             the SQL exception
	 */
	@Test
	public void eachClinicianHasNoTimeConflicts() throws SQLException {
		boolean passed = true;
		HashMap<Clinician, List<ClinicianWeekBean>> map = schedule
				.getMapOfCliniciansToSessions();
		for (Clinician clinician : map.keySet()) {
			for (ClinicianWeekBean wb : map.get(clinician)) {
				for (SessionBean session : wb.getSessions()) {
					passed = passed && clinician.canCover(session);
				}
			}
		}
		assertTrue(passed);
	}
}
