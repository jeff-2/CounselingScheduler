package action;

import static org.junit.Assert.assertEquals;
import generator.TestDataGenerator;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import validator.DateRangeValidator;
import bean.ClinicianBean;
import bean.ClinicianPreferencesBean;
import bean.CommitmentBean;
import bean.TimeAwayBean;
import dao.ClinicianDAO;
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
		
		ClinicianDAO clinicianDAO = new ClinicianDAO(conn);
		clinicianDAO.insert(new ClinicianBean(0, "Jeff"));
		
		preferences = new ClinicianPreferencesBean(0, 1, 2, 3);
		commitments = new ArrayList<CommitmentBean>();
		commitments.add(new CommitmentBean(0, 8, 9, DateRangeValidator.parseDate("4/1/2015"), "desc"));
		commitments.add(new CommitmentBean(0, 10, 11, DateRangeValidator.parseDate("3/30/2015"), "other desc"));
		
		timeAway = new ArrayList<TimeAwayBean>();
		timeAway.add(new TimeAwayBean(0, "some desc", DateRangeValidator.parseDate("1/5/2015"), DateRangeValidator.parseDate("2/7/2015")));
		timeAway.add(new TimeAwayBean(0, "some other desc", DateRangeValidator.parseDate("2/1/2015"), DateRangeValidator.parseDate("2/1/2015")));
		
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
		cmts.add(new CommitmentBean(0, 11, 12, DateRangeValidator.parseDate("3/31/2015"), "apple"));
		cmts.add(new CommitmentBean(0, 14, 15, DateRangeValidator.parseDate("4/2/2015"), "pear"));
		
		List<TimeAwayBean> tsAway = new ArrayList<TimeAwayBean>();
		tsAway.add(new TimeAwayBean(0, "orange", DateRangeValidator.parseDate("2/2/2015"), DateRangeValidator.parseDate("2/17/2015")));
		
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
