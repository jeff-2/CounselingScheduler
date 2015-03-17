package dao;

import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bean.HolidayBean;
import dao.ConnectionFactory;
import dao.HolidayDAO;
import generator.TestDataGenerator;

/**
 * @author nbeltr2
 * @author dtli2
 */
public class HolidayDAOTest {
	 
	private HolidayDAO holidayDAO;
	private SimpleDateFormat format;
	private Connection con;
	private TestDataGenerator gen;
	
	@Before
	public void setUp() throws SQLException {
		con = ConnectionFactory.getInstance();
		holidayDAO = new HolidayDAO(con);
		format = new SimpleDateFormat("MM/dd/yyyy");
		gen = new TestDataGenerator(con);
		gen.clearHolidayTable();
	}
	
	@After
	public void tearDown() throws SQLException {
		gen.clearHolidayTable();
	}


	@Test 
	public void testInsertHoliday() throws ParseException, SQLException {
		HolidayBean holiday = new HolidayBean();
		holiday.setStartDate(format.parse("02/01/1990"));
		holiday.setEndDate(format.parse("02/02/1990"));
		holiday.setName("TestName");
		holidayDAO.insertHoliday(holiday, 0, 0);
		
		HolidayBean output = getFirstHolidayTableRow();
		assertEquals(new java.sql.Date(holiday.getStartDate().getTime()), output.getStartDate());
		assertEquals(new java.sql.Date(holiday.getEndDate().getTime()), output.getEndDate());
		assertEquals(holiday.getName(), output.getName());

	}
	
	private HolidayBean getFirstHolidayTableRow() throws SQLException {
		Statement stmt = con.createStatement();
		
		stmt.execute("Select * From Holiday");
		
		ResultSet res = stmt.getResultSet();
		res.next();
		
		HolidayBean holiday = new HolidayBean();
		holiday.setName(res.getString("name"));
		holiday.setStartDate(res.getDate("startDate"));
		holiday.setEndDate(res.getDate("endDate"));

		return holiday;
	}

}
