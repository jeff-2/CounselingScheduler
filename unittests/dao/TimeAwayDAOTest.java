package dao;

import static org.junit.Assert.assertEquals;
import generator.TestDataGenerator;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import validator.DateRangeValidator;
import bean.TimeAwayBean;

/**
 * 
 * @author jmfoste2, lim92
 *
 */
public class TimeAwayDAOTest {

    private TimeAwayDAO timeAwayDAO;
    private Connection conn;
    private TestDataGenerator gen;

    @Before
    public void setUp() throws Exception {
	conn = ConnectionFactory.getInstance();
	timeAwayDAO = new TimeAwayDAO(conn);
	gen = new TestDataGenerator(conn);
	gen.clearTimeAwayTable();
    }

    @After
    public void tearDown() throws Exception {
	gen.clearTimeAwayTable();
    }

    @Test
    public void testInsertValidTimeAway() throws Exception {
	TimeAwayBean expected = new TimeAwayBean(0, "description",
		DateRangeValidator.parseDate("3/5/2015"),
		DateRangeValidator.parseDate("3/27/2015"));
	timeAwayDAO.insert(expected);

	List<TimeAwayBean> actualTimeAway = timeAwayDAO.loadTimeAway(expected
		.getClinicianID());
	List<TimeAwayBean> expectedTimeAway = new ArrayList<TimeAwayBean>();
	expectedTimeAway.add(expected);

	assertEquals(expectedTimeAway, actualTimeAway);
    }

    @Test
    public void testLoadTimeAway() throws Exception {
	TimeAwayBean timeAwayOne = new TimeAwayBean(0, "desc",
		DateRangeValidator.parseDate("1/5/2015"),
		DateRangeValidator.parseDate("1/30/2015"));
	TimeAwayBean timeAwayTwo = new TimeAwayBean(0, "other desc",
		DateRangeValidator.parseDate("4/5/2015"),
		DateRangeValidator.parseDate("4/5/2015"));
	timeAwayDAO.insert(timeAwayOne);
	timeAwayDAO.insert(timeAwayTwo);

	List<TimeAwayBean> actual = timeAwayDAO.loadTimeAway(timeAwayOne
		.getClinicianID());
	List<TimeAwayBean> expected = new ArrayList<TimeAwayBean>();
	expected.add(timeAwayOne);
	expected.add(timeAwayTwo);
	assertEquals(expected, actual);
    }

    @Test
    public void testLoadTimeAwayEmpty() throws Exception {
	List<TimeAwayBean> actual = timeAwayDAO.loadTimeAway(0);
	List<TimeAwayBean> expected = new ArrayList<TimeAwayBean>();
	assertEquals(expected, actual);
    }

    @Test
    public void testDeleteTimeAway() throws Exception {
	TimeAwayBean timeAwayBean = new TimeAwayBean(0, "desc",
		DateRangeValidator.parseDate("1/5/2015"),
		DateRangeValidator.parseDate("1/7/2015"));
	timeAwayDAO.insert(timeAwayBean);
	timeAwayDAO.delete(timeAwayBean.getClinicianID());
	List<TimeAwayBean> actual = timeAwayDAO.loadTimeAway(timeAwayBean
		.getClinicianID());
	List<TimeAwayBean> expected = new ArrayList<TimeAwayBean>();
	assertEquals(expected, actual);
    }
}
