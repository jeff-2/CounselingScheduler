package action;

import static org.junit.Assert.assertEquals;
import generator.TestDataGenerator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bean.ClinicianPreferencesBean;
import bean.CommitmentBean;
import bean.TimeAwayBean;
import dao.ClinicianPreferencesDAO;
import dao.CommitmentsDAO;
import dao.ConnectionFactory;
import dao.TimeAwayDAO;

/**
 * 
 * @author jmfoste2, lim92
 *
 */
public class ClinicianPreferencesActionTest {

	private ClinicianPreferencesAction action;
	private Connection conn;
	private List<CommitmentBean> commitments;
	private ClinicianPreferencesBean preferences;
	private List<TimeAwayBean> timeAway;
	private ClinicianPreferencesDAO clinicianPreferencesDAO;
	private CommitmentsDAO commitmentsDAO;
	private TimeAwayDAO timeAwayDAO;
	private TestDataGenerator gen;
	
	@Before
	public void setUp() throws Exception {
		conn = ConnectionFactory.getInstance();
		
		gen = new TestDataGenerator(conn);
		gen.clearTables();
		
		clinicianPreferencesDAO = new ClinicianPreferencesDAO(conn);
		commitmentsDAO = new CommitmentsDAO(conn);
		timeAwayDAO = new TimeAwayDAO(conn);
		
		PreparedStatement stmt = conn.prepareStatement("INSERT INTO Clinicians (id, name) VALUES (?, ?)");
		stmt.setInt(1, 0);
		stmt.setString(2, "Jeff");
		stmt.execute();
		stmt.close();
		
		preferences = new ClinicianPreferencesBean(0, 1, 2, 3);
		commitments = new ArrayList<CommitmentBean>();
		commitments.add(new CommitmentBean(0, 8, "Wednesday", "desc"));
		commitments.add(new CommitmentBean(0, 10, "Monday", "other desc"));
		
		timeAway = new ArrayList<TimeAwayBean>();
		timeAway.add(new TimeAwayBean(0, "some desc", new Date(1000000l), new Date(12512500000l)));
		timeAway.add(new TimeAwayBean(0, "some other desc", new Date(5l), new Date(1555555l)));
		
		action = new ClinicianPreferencesAction(preferences, commitments, timeAway, conn);
	}
	
	@After
	public void tearDown() throws Exception {
		gen.clearTables();
	}
	
	@Test
	public void testUpdatePreferences() throws Exception {
		
		action.insertPreferences();
		
		ClinicianPreferencesBean prefs = new ClinicianPreferencesBean(0, 3, 2, 1);
		List<CommitmentBean> cmts = new ArrayList<CommitmentBean>();
		cmts.add(new CommitmentBean(0, 11, "Tuesday", "apple"));
		cmts.add(new CommitmentBean(0, 14, "Thursday", "pear"));
		
		List<TimeAwayBean> tsAway = new ArrayList<TimeAwayBean>();
		tsAway.add(new TimeAwayBean(0, "orange", new Date(1000l), new Date(1251000l)));
		
		ClinicianPreferencesAction a = new ClinicianPreferencesAction(prefs, cmts, tsAway, conn);
		a.updatePreferences();
		
		ClinicianPreferencesBean actualPreferences = clinicianPreferencesDAO.loadClinicianPreferences(0);
		List<CommitmentBean> actualCommitments = commitmentsDAO.loadCommitments(0);
		List<TimeAwayBean> actualTimeAway = timeAwayDAO.loadTimeAway(0);
		
		assertEquals(prefs, actualPreferences);
		assertEquals(cmts, actualCommitments);
		assertEquals(tsAway, actualTimeAway);
	}
	
	@Test
	public void testInsertPreferences() throws Exception {
		action.insertPreferences();
		
		ClinicianPreferencesBean actualPreferences = clinicianPreferencesDAO.loadClinicianPreferences(0);
		List<CommitmentBean> actualCommitments = commitmentsDAO.loadCommitments(0);
		List<TimeAwayBean> actualTimeAway = timeAwayDAO.loadTimeAway(0);
		
		assertEquals(preferences, actualPreferences);
		assertEquals(commitments, actualCommitments);
		assertEquals(timeAway, actualTimeAway);
	}
}
