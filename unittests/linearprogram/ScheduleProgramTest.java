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

public class ScheduleProgramTest {

    private Schedule schedule;

    @Before
    public void setUp() throws Exception {
	TestDataGenerator.overwriteAndFillDemoData();
	schedule = Schedule.loadScheduleFromDBAndAssignClinicians();
    }

    @After
    public void tearDown() throws SQLException {
	System.setOut(null);
	File logFile = new File(Logger.getLogDir() + Logger.getLogFileName());
	logFile.delete();
	File dir = new File(Logger.getLogDir());
	dir.delete();
	Logger.setDebugStatus(false);
    }

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
