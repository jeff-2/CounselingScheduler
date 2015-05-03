package action;

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
 * The Class ClinicianFormActionTest tests the functionality of
 * ClinicianFormAction.
 *
 * @author jmfoste2, lim92
 */
public class ClinicianFormActionTest {

	/** The action. */
	private ClinicianFormAction action;

	/** The conn. */
	private Connection conn;

	/** The commitments. */
	private List<List<CommitmentBean>> commitments;

	/** The preferences. */
	private ClinicianPreferencesBean preferences;

	/** The time away. */
	private DefaultListModel<TimeAwayBean> timeAway;

	/** The clinician preferences dao. */
	private ClinicianPreferencesDAO clinicianPreferencesDAO;

	/** The commitments dao. */
	private CommitmentsDAO commitmentsDAO;

	/** The time away dao. */
	private TimeAwayDAO timeAwayDAO;

	/** The gen. */
	private TestDataGenerator gen;

	/**
	 * Sets the test up.
	 *
	 * @throws Exception
	 *             the exception
	 */
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

		preferences = new ClinicianPreferencesBean(0, 1, 2, 3, 5, 10);
		commitments = new ArrayList<List<CommitmentBean>>();
		List<CommitmentBean> listOne = new ArrayList<CommitmentBean>();
		listOne.add(new CommitmentBean(0, 8, 9, DateRangeValidator
				.parseDate("4/1/2015"), "desc"));
		List<CommitmentBean> listTwo = new ArrayList<CommitmentBean>();
		listTwo.add(new CommitmentBean(0, 10, 11, DateRangeValidator
				.parseDate("3/30/2015"), "other desc"));
		commitments.add(listOne);
		commitments.add(listTwo);

		timeAway = new DefaultListModel<TimeAwayBean>();
		timeAway.addElement(new TimeAwayBean(0, "some desc", DateRangeValidator
				.parseDate("1/5/2015"), DateRangeValidator
				.parseDate("2/7/2015")));
		timeAway.addElement(new TimeAwayBean(0, "some other desc",
				DateRangeValidator.parseDate("2/1/2015"), DateRangeValidator
						.parseDate("2/1/2015")));

		action = new ClinicianFormAction(conn, preferences, commitments,
				timeAway);
	}

	/**
	 * Tear down.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@After
	public void tearDown() throws Exception {
		gen.clearTables();
	}

	/**
	 * Test whether preferences will be overwritten with no current preferences.
	 *
	 * @throws SQLException
	 *             the SQL exception
	 */
	@Test
	public void testCheckWillOverwritePreferencesWithNoPreferences()
			throws SQLException {
		assertFalse(action.willOverwritePreferences());
	}

	/**
	 * Test whether preferences will be overwritten preferences when preferences
	 * already exist.
	 *
	 * @throws SQLException
	 *             the SQL exception
	 * @throws InvalidFormDataException
	 *             the invalid form data exception
	 */
	@Test
	public void testCheckWillOverwritePreferencesWithPreferences()
			throws SQLException, InvalidFormDataException {
		action.submit(false);
		assertTrue(action.willOverwritePreferences());
	}

	/**
	 * Test submit preferences and overwrite existing preferences.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testSubmitAndOverwrite() throws Exception {

		action.submit(false);

		ClinicianPreferencesBean prefs = new ClinicianPreferencesBean(0, 3, 2,
				1, 5, 10);
		List<List<CommitmentBean>> cmts = new ArrayList<List<CommitmentBean>>();
		List<CommitmentBean> listOne = new ArrayList<CommitmentBean>();
		listOne.add(new CommitmentBean(0, 11, 12, DateRangeValidator
				.parseDate("3/31/2015"), "apple"));
		List<CommitmentBean> listTwo = new ArrayList<CommitmentBean>();
		listTwo.add(new CommitmentBean(0, 14, 15, DateRangeValidator
				.parseDate("4/2/2015"), "pear"));
		cmts.add(listOne);
		cmts.add(listTwo);

		DefaultListModel<TimeAwayBean> tsAway = new DefaultListModel<TimeAwayBean>();
		tsAway.addElement(new TimeAwayBean(0, "orange", DateRangeValidator
				.parseDate("2/2/2015"), DateRangeValidator
				.parseDate("2/17/2015")));

		ClinicianFormAction actionTwo = new ClinicianFormAction(conn, prefs,
				cmts, tsAway);
		actionTwo.submit(true);

		ClinicianPreferencesBean actualPreferences = clinicianPreferencesDAO
				.loadClinicianPreferences(0);
		List<CommitmentBean> actualCommitments = commitmentsDAO
				.loadCommitments(0);
		List<TimeAwayBean> actualTimeAway = timeAwayDAO.loadTimeAway(0);

		List<CommitmentBean> expectedCommitments = new ArrayList<CommitmentBean>();
		for (List<CommitmentBean> commitmentList : cmts) {
			expectedCommitments.addAll(commitmentList);
		}
		List<TimeAwayBean> expectedTimesAway = new ArrayList<TimeAwayBean>();
		for (int i = 0; i < tsAway.size(); i++) {
			expectedTimesAway.add(tsAway.get(i));
		}

		assertEquals(prefs, actualPreferences);
		assertEquals(expectedCommitments, actualCommitments);
		assertEquals(expectedTimesAway, actualTimeAway);
	}

	/**
	 * Test submit preferences with no pre-existing preferences.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testSubmit() throws Exception {
		action.submit(false);

		ClinicianPreferencesBean actualPreferences = clinicianPreferencesDAO
				.loadClinicianPreferences(0);
		List<CommitmentBean> actualCommitments = commitmentsDAO
				.loadCommitments(0);
		List<TimeAwayBean> actualTimeAway = timeAwayDAO.loadTimeAway(0);

		List<CommitmentBean> expectedCommitments = new ArrayList<CommitmentBean>();
		for (List<CommitmentBean> commitmentList : commitments) {
			expectedCommitments.addAll(commitmentList);
		}
		List<TimeAwayBean> expectedTimesAway = new ArrayList<TimeAwayBean>();
		for (int i = 0; i < timeAway.size(); i++) {
			expectedTimesAway.add(timeAway.get(i));
		}

		assertEquals(preferences, actualPreferences);
		assertEquals(expectedCommitments, actualCommitments);
		assertEquals(expectedTimesAway, actualTimeAway);
	}
}
