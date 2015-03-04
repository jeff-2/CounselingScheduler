package dao;
import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.Before;
import org.junit.Test;

import db.CalendarDao;


public class CalendarDaoTest {
	private CalendarDao calendarDao;
	
	@Before
	public void setUp() throws SQLException {
		calendarDao = new CalendarDao();
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
	
	private void clearCalendarTable() throws SQLException {
		String connectionUrl = "jdbc:sqlserver://localhost;" +
				   "databaseName=CounselingScheduler;user=sa;password=w5Q[7S2_u2/\\+8Ds;";
		Connection con = DriverManager.getConnection(connectionUrl);
		Statement stmt = con.createStatement();
		
		stmt.execute("DELETE FROM Calendar");
	}
	
	private void generateCalendarData() throws SQLException {
		String connectionUrl = "jdbc:sqlserver://localhost;" +
				   "databaseName=CounselingScheduler;user=sa;password=w5Q[7S2_u2/\\+8Ds;";
		Connection con = DriverManager.getConnection(connectionUrl);
		Statement stmt = con.createStatement();
		
		stmt.execute("INSERT INTO Calendar (id, startDate, endDate, iaMinHours,"
				+ "ecMinHours, term) VALUES(0, '2000-01-02', '2000-01-03', 5, 6, 0),"
				+ "(1, '2000-02-02', '2000-02-03', 7, 8, 1)");
		
	}

}
