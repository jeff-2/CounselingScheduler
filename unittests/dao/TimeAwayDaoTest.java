package dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import db.ConnectionFactory;
import db.TimeAwayDao;
import forms.TimeAway;
import generators.TestDataGenerator;

/**
 * 
 * @author jmfoste2, lim92
 *
 */
public class TimeAwayDaoTest {
	private TimeAwayDao timeAwayDao;
	private Connection conn;
	private TestDataGenerator gen;
	
	@Before
	public void setUp() throws Exception {
		conn = ConnectionFactory.getInstance();
		timeAwayDao = new TimeAwayDao(conn);
		gen = new TestDataGenerator(conn);
		gen.clearTimeAwayTable();
	}
	
	@After
	public void tearDown() throws Exception {
		gen.clearTimeAwayTable();
	}
	
	@Test
	public void testInsertValidTimeAway() throws Exception {
		int clinicianID = 0;
		TimeAway expected = new TimeAway(clinicianID, "description", new Date(8641231100l), new Date(8640325325200l));
		timeAwayDao.insert(expected);
		
		PreparedStatement stmt = conn.prepareStatement("SELECT description, startDate, endDate FROM TimeAway WHERE id = ?");
		stmt.setInt(1, clinicianID);
		stmt.execute();
		ResultSet results = stmt.getResultSet();
		results.next();
		String description = results.getString("description");
		Date startDate = new Date(results.getLong("startDate"));
		Date endDate = new Date(results.getLong("endDate"));
		
		assertEquals(expected, new TimeAway(clinicianID, description, startDate, endDate));
		stmt.close();
	}
	
	@Test
	public void testLoadTimeAway() throws Exception {
		int clinicianID = 0;
		PreparedStatement stmt = conn.prepareStatement("INSERT INTO TimeAway (id, startDate, endDate, description) VALUES (?, ?, ?, ?), (?, ?, ?, ?)");
		stmt.setInt(1, clinicianID);
		stmt.setLong(2, 150000000l);
		stmt.setLong(3, 2700000000l);
		stmt.setString(4, "desc");
		stmt.setInt(5, clinicianID);
		stmt.setLong(6, 10043630000l);
		stmt.setLong(7, 10000000000000l);
		stmt.setString(8, "other desc");
		stmt.execute();
		stmt.close();
		
		List<TimeAway> actual = timeAwayDao.loadTimeAway(clinicianID);
		List<TimeAway> expected = new ArrayList<TimeAway>();
		expected.add(new TimeAway(clinicianID, "desc", new Date(150000000l), new Date(2700000000l)));
		expected.add(new TimeAway(clinicianID, "other desc", new Date(10043630000l), new Date(10000000000000l)));
		assertEquals(actual, expected);
	}
	
	@Test
	public void testLoadTimeAwayEmpty() throws Exception {
		List<TimeAway> actual = timeAwayDao.loadTimeAway(0);
		List<TimeAway> expected = new ArrayList<TimeAway>();
		assertEquals(actual, expected);
	}
	
	@Test
	public void testDeleteTimeAway() throws Exception {
		int clinicianID = 0;
		PreparedStatement stmt = conn.prepareStatement("INSERT INTO TimeAway (id, startDate, endDate, description) VALUES (?, ?, ?, ?)");
		stmt.setInt(1, clinicianID);
		stmt.setLong(2, 123253252l);
		stmt.setLong(3, 150000000l);
		stmt.setString(4, "desc");
		stmt.execute();
		stmt.close();
		
		timeAwayDao.delete(clinicianID);
		
		stmt = conn.prepareStatement("SELECT startDate, endDate, description FROM TimeAway WHERE id = ?");
		stmt.setInt(1, clinicianID);
		stmt.execute();
		ResultSet results = stmt.getResultSet();
		assertFalse(results.next());
		stmt.close();
	}
}
