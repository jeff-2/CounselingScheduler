package dao;
import static org.junit.Assert.assertEquals;
import generator.TestDataGenerator;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import validator.DateRangeValidator;
import bean.CalendarBean;
import bean.Semester;

/**
 * @author nbeltr2
 * @author dtli2
 */ 
public class CalendarDAOTest {
	private CalendarDAO calendarDAO;
	private Connection con;
	private TestDataGenerator gen;
	
	@Before
	public void setUp() throws SQLException {
		con = ConnectionFactory.getInstance();
		calendarDAO = new CalendarDAO(con);
		gen = new TestDataGenerator(con);
		gen.clearCalendarTable();
	}
	
	@After
	public void tearDown() throws SQLException {
		gen.clearCalendarTable();
	}

	@Test
	public void testNoCalendarsYet() throws SQLException {
		assertEquals(calendarDAO.getNextAvailableId(), 0);
	}
	
	@Test
	public void testGetNextAvailableId() throws SQLException {
		generateCalendarData();
		assertEquals(calendarDAO.getNextAvailableId(), 2);
	}
	
	@Test 
	public void testInsertCalendar() throws ParseException, SQLException {
		CalendarBean calendar = new CalendarBean();
		calendar.setId(0);
		calendar.setSemester(Semester.Winter);
		calendar.setStartDate(DateRangeValidator.parseDate("01/01/1990"));
		calendar.setEndDate(DateRangeValidator.parseDate("01/01/1991"));
		calendar.setIaMinHours(35);
		calendar.setEcMinHours(44);
		calendarDAO.insertCalendar(calendar);
		
		CalendarBean output = calendarDAO.loadCalendar();
		assertEquals(calendar, output);
	}
	
	private void generateCalendarData() throws SQLException {
		Statement stmt = con.createStatement();
		
		stmt.execute("INSERT INTO Calendar (id, startDate, endDate, iaMinHours,"
				+ "ecMinHours, term) VALUES(0, '2000-01-02', '2000-01-03', 5, 6, 0),"
				+ "(1, '2000-02-02', '2000-02-03', 7, 8, 0)");
		
	}
}
