package action;

import static org.junit.Assert.assertEquals;
import generator.TestDataGenerator;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import validator.DateRangeValidator;
import bean.ClinicianPreferencesBean;
import bean.CommitmentBean;
import bean.TimeAwayBean;
import dao.ConnectionFactory;

/**
 * The Class ClinicianLoadPreferencesActionTest test the functionality of ClinicianLoadPreferencesAction.
 */
public class ClinicianLoadPreferencesActionTest {

    /** The conn. */
    private Connection conn;
    
    /** The gen. */
    private TestDataGenerator gen;
    
    /** The action. */
    private ClinicianLoadPreferencesAction action;

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
	action = new ClinicianLoadPreferencesAction(conn, "Jeff");
    }

    /**
     * Clean up.
     *
     * @throws Exception the exception
     */
    @After
    public void cleanUp() throws Exception {
	gen.clearTables();
    }

    /**
     * Test load times away.
     *
     * @throws SQLException the SQL exception
     * @throws ParseException the parse exception
     */
    @Test
    public void testLoadTimesAway() throws SQLException, ParseException {
	List<TimeAwayBean> expected = new ArrayList<TimeAwayBean>();
	expected.add(new TimeAwayBean(0, "Vacation", DateRangeValidator
		.parseDate("2/3/2015"), DateRangeValidator
		.parseDate("2/12/2015")));
	List<TimeAwayBean> actual = action.loadTimesAway();
	assertEquals(expected, actual);
    }

    /**
     * Test load clinician preferences.
     *
     * @throws SQLException the SQL exception
     */
    @Test
    public void testLoadClinicianPreferences() throws SQLException {
	ClinicianPreferencesBean expected = new ClinicianPreferencesBean(0, 2,
		1, 3, 35, 44);
	ClinicianPreferencesBean actual = action.loadClinicianPreferences();
	assertEquals(expected, actual);
    }

    /**
     * Test load commitments.
     *
     * @throws SQLException the SQL exception
     * @throws ParseException the parse exception
     */
    @Test
    public void testLoadCommitments() throws SQLException, ParseException {
	List<List<CommitmentBean>> expected = new ArrayList<List<CommitmentBean>>();
	List<CommitmentBean> listOne = new ArrayList<CommitmentBean>();
	listOne.add(new CommitmentBean(0, 8, 9, DateRangeValidator
		.parseDate("4/1/2015"), "Doctor's appointment"));
	listOne.add(new CommitmentBean(0, 8, 9, DateRangeValidator
		.parseDate("4/8/2015"), "Doctor's appointment"));
	Comparator<CommitmentBean> dateComparator = new Comparator<CommitmentBean>() {
	    @Override
	    public int compare(CommitmentBean o1, CommitmentBean o2) {
		return o1.getDate().compareTo(o2.getDate());
	    }
	};
	Collections.sort(listOne, dateComparator);
	List<CommitmentBean> listTwo = new ArrayList<CommitmentBean>();
	listTwo.add(new CommitmentBean(0, 11, 12, DateRangeValidator
		.parseDate("4/3/2015"), "Staff meeting"));
	expected.add(listTwo);
	expected.add(listOne);
	Comparator<List<CommitmentBean>> listComparator = new Comparator<List<CommitmentBean>>() {
	    @Override
	    public int compare(List<CommitmentBean> o1, List<CommitmentBean> o2) {
		return o1.get(0).getDescription()
			.compareTo(o2.get(0).getDescription());
	    }
	};
	Collections.sort(expected, listComparator);
	List<List<CommitmentBean>> actual = action.loadCommitments();
	Collections.sort(actual, listComparator);
	assertEquals(expected, actual);
    }

    /**
     * Test load commitment descriptions.
     *
     * @throws SQLException the SQL exception
     */
    @Test
    public void testLoadCommitmentDescriptions() throws SQLException {
	List<String> expected = new ArrayList<String>();
	expected.add("Doctor's appointment: Weekly on Wednesday from 8:00 am to 9:00 am");
	expected.add("Staff meeting: Friday, April 3, 2015 from 11:00 am to 12:00 pm");
	Collections.sort(expected);
	List<List<CommitmentBean>> commitmentList = action.loadCommitments();
	List<String> actual = action.loadCommitmentDescriptions(commitmentList);
	Collections.sort(actual);
	assertEquals(expected, actual);
    }
}
