package dao;
import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bean.CalendarBean;
import dao.CalendarDAO;
import dao.ConnectionFactory;
import generator.TestDataGenerator;

/**
 * @author nbeltr2
 * @author dtli2
 */
public class CalendarDAOTest {
	private CalendarDAO calendarDAO;
	private SimpleDateFormat format;
	private Connection con;
	private TestDataGenerator gen;
	
	@Before
	public void setUp() throws SQLException {
		con = ConnectionFactory.getInstance();
		calendarDAO = new CalendarDAO(con);
		format = new SimpleDateFormat("MM/dd/yyyy");
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
		calendar.setSemester(2);
		calendar.setStartDate(format.parse("01/01/1990"));
		calendar.setEndDate(format.parse("01/01/1991"));
		calendar.setIaMinHours(35);
		calendar.setEcMinHours(44);
		calendarDAO.insertCalendar(calendar);
		
		CalendarBean output = calendarDAO.loadCalendar();
		assertEquals(calendar.getId(), output.getId());
		assertEquals(calendar.getSemester(), output.getSemester());
		assertEquals(new java.sql.Date(calendar.getStartDate().getTime()), output.getStartDate());
		assertEquals(new java.sql.Date(calendar.getEndDate().getTime()), output.getEndDate());
		assertEquals(calendar.getIaMinHours(), output.getIaMinHours());
		assertEquals(calendar.getEcMinHours(), output.getEcMinHours());

	}
	
	private void generateCalendarData() throws SQLException {
		Statement stmt = con.createStatement();
		
		stmt.execute("INSERT INTO Calendar (id, startDate, endDate, iaMinHours,"
				+ "ecMinHours, term) VALUES(0, '2000-01-02', '2000-01-03', 5, 6, 0),"
				+ "(1, '2000-02-02', '2000-02-03', 7, 8, 1)");
		
	}
}
