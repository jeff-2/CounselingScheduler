package action;

import static org.junit.Assert.assertEquals;
import generator.TestDataGenerator;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bean.CalendarBean;
import bean.SessionBean;
import bean.Weekday;
import dao.CalendarDAO;
import dao.ConnectionFactory;
import dao.SessionsDAO;

/**
 * JUnit test for generating an unfilled schedule.
 *
 * @author ramusa2, jmfoste2
 */
public class GenerateUnfilledScheduleTest {

	/** The conn. */
	private Connection conn;

	/** The gen. */
	private TestDataGenerator gen;

	/** The sessions dao. */
	private SessionsDAO sessionsDAO;

	/** The action. */
	private GenerateUnfilledScheduleAction action;

	/**
	 * Sets the tests up by getting the database connection and
	 * clearing the tables.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Before
	public void setUp() throws Exception {
		conn = ConnectionFactory.getInstance();
		sessionsDAO = new SessionsDAO(conn);
		gen = new TestDataGenerator(conn);
		gen.clearTables();
		action = new GenerateUnfilledScheduleAction(conn);
	}

	/**
	 * Test generate unfilled schedule.
	 *
	 * @throws SQLException
	 *             the SQL exception
	 * @throws ParseException
	 *             the parse exception
	 */
	@Test
	public void testGenerateUnfilledSchedule() throws SQLException,
			ParseException {
		gen.generateStandardDataset();
		action.generateUnfilledSchedule();
		List<SessionBean> sessions = sessionsDAO.loadSessions();
		assertEquals(sessions.size(), 490);
		boolean onlyWeekdays = true;
		for (SessionBean sesh : sessions) {
			onlyWeekdays = onlyWeekdays && Weekday.isWeekday(sesh.getDate());
		}
		assertEquals(onlyWeekdays, true);
		CalendarDAO calendarDAO = new CalendarDAO(
				ConnectionFactory.getInstance());
		CalendarBean calBean = calendarDAO.loadCalendar();
		Date start = sessions.get(0).getDate();
		assertEquals(
				!Weekday.isWeekday(calBean.getStartDate())
						|| start.equals(calBean.getStartDate()), true);
		Date end = sessions.get(sessions.size() - 1).getDate();
		assertEquals(
				!Weekday.isWeekday(calBean.getEndDate())
						|| end.equals(calBean.getEndDate()), true);
	}

	/**
	 * Test generate unfilled empty schedule.
	 *
	 * @throws SQLException
	 *             the SQL exception
	 * @throws ParseException
	 *             the parse exception
	 */
	@Test
	public void testGenerateUnfilledEmptySchedule() throws SQLException,
			ParseException {
		gen.generateEmptySemesterDataset();
		action.generateUnfilledSchedule();
		List<SessionBean> sessions = sessionsDAO.loadSessions();
		assertEquals(sessions.size(), 0);
	}

	/**
	 * Clean up. Clears the database tables.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@After
	public void cleanUp() throws Exception {
		gen.clearTables();
	}
}
