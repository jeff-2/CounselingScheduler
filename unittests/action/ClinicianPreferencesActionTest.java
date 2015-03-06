package action;

import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import db.ClinicianPreferencesDao;
import db.CommitmentsDao;
import db.ConnectionFactory;
import db.TimeAwayDao;
import forms.ClinicianPreferences;
import forms.Commitment;
import forms.TimeAway;

/**
 * 
 * @author jmfoste2, lim92
 *
 */
public class ClinicianPreferencesActionTest {

	private ClinicianPreferencesAction action;
	private Connection conn;
	private List<Commitment> commitments;
	private ClinicianPreferences preferences;
	private List<TimeAway> timeAway;
	private ClinicianPreferencesDao clinicianPreferencesDao;
	private CommitmentsDao commitmentsDao;
	private TimeAwayDao timeAwayDao;
	
	@Before
	public void setUp() throws Exception {
		conn = ConnectionFactory.getInstance();
		
		clearCliniciansTable();
		clearClinicianPreferencesTable();
		clearCommitmentsTable();
		clearTimeAwayTable();
		
		clinicianPreferencesDao = new ClinicianPreferencesDao(conn);
		commitmentsDao = new CommitmentsDao(conn);
		timeAwayDao = new TimeAwayDao(conn);
		
		PreparedStatement stmt = conn.prepareStatement("INSERT INTO Clinicians (id, name) VALUES (?, ?)");
		stmt.setInt(1, 0);
		stmt.setString(2, "Jeff");
		stmt.execute();
		stmt.close();
		
		preferences = new ClinicianPreferences(0, 1, 2, 3);
		commitments = new ArrayList<Commitment>();
		commitments.add(new Commitment(0, 8, "Wednesday", "desc"));
		commitments.add(new Commitment(0, 10, "Monday", "other desc"));
		
		timeAway = new ArrayList<TimeAway>();
		timeAway.add(new TimeAway(0, "some desc", new Date(1000000l), new Date(12512500000l)));
		timeAway.add(new TimeAway(0, "some other desc", new Date(5l), new Date(1555555l)));
		
		action = new ClinicianPreferencesAction(preferences, commitments, timeAway, conn);
	}
	
	@After
	public void tearDown() throws Exception {
		clearCliniciansTable();
		clearClinicianPreferencesTable();
		clearCommitmentsTable();
		clearTimeAwayTable();
	}
	
	private void clearCliniciansTable() throws Exception {
		PreparedStatement stmt = conn.prepareStatement("DELETE FROM Clinicians");
		stmt.execute();
		stmt.close();
	}
	
	private void clearClinicianPreferencesTable() throws Exception {
		PreparedStatement stmt = conn.prepareStatement("DELETE FROM ClinicianPreferences");
		stmt.execute();
		stmt.close();
	}
	
	private void clearCommitmentsTable() throws Exception {
		PreparedStatement stmt = conn.prepareStatement("DELETE FROM Commitments");
		stmt.execute();
		stmt.close();
	}
	
	private void clearTimeAwayTable() throws Exception {
		PreparedStatement stmt = conn.prepareStatement("DELETE FROM TimeAway");
		stmt.execute();
		stmt.close();
	}
	
	@Test
	public void testUpdatePreferences() throws Exception {
		
		action.insertPreferences();
		
		ClinicianPreferences prefs = new ClinicianPreferences(0, 3, 2, 1);
		List<Commitment> cmts = new ArrayList<Commitment>();
		cmts.add(new Commitment(0, 11, "Tuesday", "apple"));
		cmts.add(new Commitment(0, 14, "Thursday", "pear"));
		
		List<TimeAway> tsAway = new ArrayList<TimeAway>();
		tsAway.add(new TimeAway(0, "orange", new Date(1000l), new Date(1251000l)));
		
		ClinicianPreferencesAction a = new ClinicianPreferencesAction(prefs, cmts, tsAway, conn);
		a.updatePreferences();
		
		ClinicianPreferences actualPreferences = clinicianPreferencesDao.loadClinicianPreferences(0);
		List<Commitment> actualCommitments = commitmentsDao.loadCommitments(0);
		List<TimeAway> actualTimeAway = timeAwayDao.loadTimeAway(0);
		
		assertEquals(prefs, actualPreferences);
		assertEquals(cmts, actualCommitments);
		assertEquals(tsAway, actualTimeAway);
	}
	
	@Test
	public void testInsertPreferences() throws Exception {
		action.insertPreferences();
		
		ClinicianPreferences actualPreferences = clinicianPreferencesDao.loadClinicianPreferences(0);
		List<Commitment> actualCommitments = commitmentsDao.loadCommitments(0);
		List<TimeAway> actualTimeAway = timeAwayDao.loadTimeAway(0);
		
		assertEquals(preferences, actualPreferences);
		assertEquals(commitments, actualCommitments);
		assertEquals(timeAway, actualTimeAway);
	}
}
