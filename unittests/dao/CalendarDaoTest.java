package dao;
import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import db.CalendarDao;
import forms.Calendar;


public class CalendarDaoTest {
	private CalendarDao calendarDao;
	private SimpleDateFormat format;
	
	@Before
	public void setUp() throws SQLException {
		calendarDao = new CalendarDao();
		format = new SimpleDateFormat("MM/dd/yyyy");
		clearCalendarTable();
	}
	
	@After
	public void tearDown() throws SQLException {
		clearCalendarTable();
	}

	@Test
	public void testNoCalendarsYet() throws SQLException {
		assertEquals(calendarDao.getNextAvailableId(), 0);
	}
	
	@Test
	public void testGetNextAvailableId() throws SQLException {
		generateCalendarData();
		assertEquals(calendarDao.getNextAvailableId(), 2);
	}
	
	@Test 
	public void testInsertCalendar() throws ParseException, SQLException {
		Calendar calendar = new Calendar();
		calendar.setId(0);
		calendar.setSemester(2);
		calendar.setStartDate(format.parse("01/01/1990"));
		calendar.setEndDate(format.parse("01/01/1991"));
		calendar.setIaMinHours(35);
		calendar.setEcMinHours(44);
		calendarDao.insertCalendar(calendar);
		
		Calendar output = getFirstCalendarTableRow();
		assertEquals(calendar.getId(), output.getId());
		assertEquals(calendar.getSemester(), output.getSemester());
		assertEquals(new java.sql.Date(calendar.getStartDate().getTime()), output.getStartDate());
		assertEquals(new java.sql.Date(calendar.getEndDate().getTime()), output.getEndDate());
		assertEquals(calendar.getIaMinHours(), output.getIaMinHours());
		assertEquals(calendar.getEcMinHours(), output.getEcMinHours());

	}
	
	private void clearCalendarTable() throws SQLException {
		String connectionUrl = "jdbc:sqlserver://localhost;" +
				   "databaseName=CounselingScheduler;user=admin;password=admin;";
		Connection con = DriverManager.getConnection(connectionUrl);
		Statement stmt = con.createStatement();
		
		stmt.execute("DELETE FROM Calendar");
	}
	
	private void generateCalendarData() throws SQLException {
		String connectionUrl = "jdbc:sqlserver://localhost;" +
				   "databaseName=CounselingScheduler;user=admin;password=admin;";
		Connection con = DriverManager.getConnection(connectionUrl);
		Statement stmt = con.createStatement();
		
		stmt.execute("INSERT INTO Calendar (id, startDate, endDate, iaMinHours,"
				+ "ecMinHours, term) VALUES(0, '2000-01-02', '2000-01-03', 5, 6, 0),"
				+ "(1, '2000-02-02', '2000-02-03', 7, 8, 1)");
		
	}
	
	private Calendar getFirstCalendarTableRow() throws SQLException {
		String connectionUrl = "jdbc:sqlserver://localhost;" +
				   "databaseName=CounselingScheduler;user=admin;password=admin;";
		Connection con = DriverManager.getConnection(connectionUrl);
		Statement stmt = con.createStatement();
		
		stmt.execute("Select * From Calendar");
		
		ResultSet res = stmt.getResultSet();
		res.next();
		
		Calendar cal = new Calendar();
		cal.setId(res.getInt("id"));
		cal.setStartDate(res.getDate("startDate"));
		cal.setEndDate(res.getDate("endDate"));
		cal.setIaMinHours(res.getInt("iaMinHours"));
		cal.setEcMinHours(res.getInt("ecMinHours"));
		cal.setSemester(res.getInt("term"));

		return cal;
	}

}
