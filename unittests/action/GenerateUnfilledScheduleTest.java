package action;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import generator.TestDataGenerator;

import org.junit.After;
import org.junit.Test;

import bean.CalendarBean;
import bean.SessionBean;
import bean.Weekday;
import dao.CalendarDAO;
import dao.ConnectionFactory;
import dao.SessionsDAO;

/**
 * JUnit test for generating an unfilled schedule
 * 
 * @author ramusa2, jmfoste2
 *
 */
public class GenerateUnfilledScheduleTest {
	
	@Test
	public void testGenerateUnfilledSchedule() throws SQLException, ParseException {
		TestDataGenerator gen = new TestDataGenerator(ConnectionFactory.getInstance());
		gen.clearTables();
		gen.generateStandardDataset();
		GenerateUnfilledScheduleAction action = new GenerateUnfilledScheduleAction(ConnectionFactory.getInstance());
		action.generateUnfilledSchedule();
		SessionsDAO sessionsDAO = new SessionsDAO(ConnectionFactory.getInstance());
		List<SessionBean> sessions = sessionsDAO.loadSessions();
		assertEquals(sessions.size(), 490);
		boolean onlyWeekdays = true;
		for(SessionBean sesh : sessions) {
			onlyWeekdays = onlyWeekdays && Weekday.isWeekday(sesh.getDate());
		}
		assertEquals(onlyWeekdays, true);
		CalendarDAO calendarDAO = new CalendarDAO(ConnectionFactory.getInstance());
		CalendarBean calBean = calendarDAO.loadCalendar();
		Date start = sessions.get(0).getDate();
		assertEquals(!Weekday.isWeekday(calBean.getStartDate()) || start.equals(calBean.getStartDate()), true);
		Date end = sessions.get(sessions.size()-1).getDate();
		assertEquals(!Weekday.isWeekday(calBean.getEndDate()) || end.equals(calBean.getEndDate()), true);
	}
	
	@Test
	public void testGenerateUnfilledEmptySchedule() throws SQLException, ParseException {
		TestDataGenerator gen = new TestDataGenerator(ConnectionFactory.getInstance());
		gen.clearTables();
		gen.generateEmptySemesterDataset();
		GenerateUnfilledScheduleAction action = new GenerateUnfilledScheduleAction(ConnectionFactory.getInstance());
		action.generateUnfilledSchedule();
		SessionsDAO sessionsDAO = new SessionsDAO(ConnectionFactory.getInstance());
		List<SessionBean> sessions = sessionsDAO.loadSessions();
		assertEquals(sessions.size(), 0);
	}

	@After
	public void cleanUp() throws Exception {
		TestDataGenerator gen = new TestDataGenerator(ConnectionFactory.getInstance());
		gen.clearTables();
	}
}
