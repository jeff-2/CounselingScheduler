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

import db.HolidayDao;
import forms.Holiday;

/**
 * @author nbeltr2
 * @author dtli2
 */
public class HolidayDaoTest {
	private HolidayDao holidayDao;
	private SimpleDateFormat format;
	
	@Before
	public void setUp() throws SQLException {
		holidayDao = new HolidayDao();
		format = new SimpleDateFormat("MM/dd/yyyy");
		clearHolidayTable();
	}
	
	@After
	public void tearDown() throws SQLException {
		clearHolidayTable();
	}


	@Test 
	public void testInsertHoliday() throws ParseException, SQLException {
		Holiday holiday = new Holiday();
		holiday.setStartDate(format.parse("02/01/1990"));
		holiday.setEndDate(format.parse("02/02/1990"));
		holiday.setName("TestName");
		holidayDao.insertHoliday(holiday, 0, 0);
		
		Holiday output = getFirstHolidayTableRow();
		assertEquals(new java.sql.Date(holiday.getStartDate().getTime()), output.getStartDate());
		assertEquals(new java.sql.Date(holiday.getEndDate().getTime()), output.getEndDate());
		assertEquals(holiday.getName(), output.getName());

	}
	
	private void clearHolidayTable() throws SQLException {
		String connectionUrl = "jdbc:sqlserver://localhost;" +
				   "databaseName=CounselingScheduler;user=admin;password=admin;";
		Connection con = DriverManager.getConnection(connectionUrl);
		Statement stmt = con.createStatement();
		
		stmt.execute("DELETE FROM Holiday");
	}
	
	private Holiday getFirstHolidayTableRow() throws SQLException {
		String connectionUrl = "jdbc:sqlserver://localhost;" +
				   "databaseName=CounselingScheduler;user=admin;password=admin;";
		Connection con = DriverManager.getConnection(connectionUrl);
		Statement stmt = con.createStatement();
		
		stmt.execute("Select * From Holiday");
		
		ResultSet res = stmt.getResultSet();
		res.next();
		
		Holiday holiday = new Holiday();
		holiday.setName(res.getString("name"));
		holiday.setStartDate(res.getDate("startDate"));
		holiday.setEndDate(res.getDate("endDate"));

		return holiday;
	}

}
