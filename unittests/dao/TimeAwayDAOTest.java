package dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bean.TimeAwayBean;
import dao.ConnectionFactory;
import dao.TimeAwayDAO;
import generator.TestDataGenerator;

/**
 * 
 * @author jmfoste2, lim92
 *
 */
public class TimeAwayDAOTest {
	
	private TimeAwayDAO timeAwayDAO;
	private Connection conn;
	private TestDataGenerator gen;
	private SimpleDateFormat format;
	
	@Before
	public void setUp() throws Exception {
		conn = ConnectionFactory.getInstance();
		timeAwayDAO = new TimeAwayDAO(conn);
		gen = new TestDataGenerator(conn);
		gen.clearTimeAwayTable();
		format = new SimpleDateFormat("MM/dd/yyyy");
	}
	
	@After
	public void tearDown() throws Exception {
		gen.clearTimeAwayTable();
	}
	
	@Test
	public void testInsertValidTimeAway() throws Exception {
		int clinicianID = 0;
		TimeAwayBean expected = new TimeAwayBean(clinicianID, "description", format.parse("3/5/2015"), format.parse("3/27/2015"));
		timeAwayDAO.insert(expected);
		
		PreparedStatement stmt = conn.prepareStatement("SELECT description, startDate, endDate FROM TimeAway WHERE id = ?");
		stmt.setInt(1, clinicianID);
		stmt.execute();
		ResultSet results = stmt.getResultSet();
		results.next();
		String description = results.getString("description");
		Date startDate = results.getDate("startDate");
		Date endDate = results.getDate("endDate");
		
		assertEquals(expected, new TimeAwayBean(clinicianID, description, startDate, endDate));
		stmt.close();
	}
	
	@Test
	public void testLoadTimeAway() throws Exception {
		int clinicianID = 0;
		PreparedStatement stmt = conn.prepareStatement("INSERT INTO TimeAway (id, startDate, endDate, description) VALUES (?, ?, ?, ?), (?, ?, ?, ?)");
		stmt.setInt(1, clinicianID);
		stmt.setDate(2, new java.sql.Date(format.parse("1/5/2015").getTime()));
		stmt.setDate(3, new java.sql.Date(format.parse("1/30/2015").getTime()));
		stmt.setString(4, "desc");
		stmt.setInt(5, clinicianID);
		stmt.setDate(6, new java.sql.Date(format.parse("4/5/2015").getTime()));
		stmt.setDate(7, new java.sql.Date(format.parse("4/5/2015").getTime()));
		stmt.setString(8, "other desc");
		stmt.execute();
		stmt.close();
		
		List<TimeAwayBean> actual = timeAwayDAO.loadTimeAway(clinicianID);
		List<TimeAwayBean> expected = new ArrayList<TimeAwayBean>();
		expected.add(new TimeAwayBean(clinicianID, "desc", format.parse("1/5/2015"), format.parse("1/30/2015")));
		expected.add(new TimeAwayBean(clinicianID, "other desc", format.parse("4/5/2015"), format.parse("4/5/2015")));
		assertEquals(actual, expected);
	}
	
	@Test
	public void testLoadTimeAwayEmpty() throws Exception {
		List<TimeAwayBean> actual = timeAwayDAO.loadTimeAway(0);
		List<TimeAwayBean> expected = new ArrayList<TimeAwayBean>();
		assertEquals(actual, expected);
	}
	
	@Test
	public void testDeleteTimeAway() throws Exception {
		int clinicianID = 0;
		PreparedStatement stmt = conn.prepareStatement("INSERT INTO TimeAway (id, startDate, endDate, description) VALUES (?, ?, ?, ?)");
		stmt.setInt(1, clinicianID);
		stmt.setDate(2, new java.sql.Date(format.parse("1/5/2015").getTime()));
		stmt.setDate(3, new java.sql.Date(format.parse("1/7/2015").getTime()));
		stmt.setString(4, "desc");
		stmt.execute();
		stmt.close();
		
		timeAwayDAO.delete(clinicianID);
		
		stmt = conn.prepareStatement("SELECT startDate, endDate, description FROM TimeAway WHERE id = ?");
		stmt.setInt(1, clinicianID);
		stmt.execute();
		ResultSet results = stmt.getResultSet();
		assertFalse(results.next());
		stmt.close();
	}
}
