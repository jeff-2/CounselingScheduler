package dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import db.CommitmentsDao;
import db.ConnectionFactory;
import forms.Commitment;
import generators.TestDataGenerator;

/**
 * 
 * @author jmfoste2, lim92
 *
 */
public class CommitmentsDaoTest {

	private CommitmentsDao commitmentsDao;
	private Connection conn;
	private TestDataGenerator gen;
	
	@Before
	public void setUp() throws Exception {
		conn = ConnectionFactory.getInstance();
		commitmentsDao = new CommitmentsDao(conn);
		gen = new TestDataGenerator(conn);
		gen.clearCommitmentsTable();
	}
	
	@After
	public void tearDown() throws Exception {
		gen.clearCommitmentsTable();
	}
	
	@Test
	public void testInsertValidCommitment() throws Exception {
		int clinicianID = 0;
		Commitment expected = new Commitment(clinicianID, 4, "Wednesday", "Description");
		commitmentsDao.insert(expected);
		
		PreparedStatement stmt = conn.prepareStatement("SELECT hour, day, description FROM Commitments WHERE id = ?");
		stmt.setInt(1, clinicianID);
		stmt.execute();
		ResultSet results = stmt.getResultSet();
		results.next();
		int hour = results.getInt("hour");
		String day = results.getString("day");
		String description = results.getString("description");
		
		
		assertEquals(expected, new Commitment(clinicianID, hour, day, description));
		stmt.close();
	}
	
	@Test
	public void testLoadCommitments() throws Exception {
		int clinicianID = 0;
		PreparedStatement stmt = conn.prepareStatement("INSERT INTO Commitments (id, hour, day, description) VALUES (?, ?, ?, ?), (?, ?, ?, ?)");
		stmt.setInt(1, clinicianID);
		stmt.setInt(2, 8);
		stmt.setString(3, "Friday");
		stmt.setString(4, "desc");
		stmt.setInt(5, clinicianID);
		stmt.setInt(6, 9);
		stmt.setString(7, "Wednesday");
		stmt.setString(8, "other desc");
		stmt.execute();
		stmt.close();
		
		List<Commitment> actual = commitmentsDao.loadCommitments(clinicianID);
		List<Commitment> expected = new ArrayList<Commitment>();
		expected.add(new Commitment(clinicianID, 8, "Friday", "desc"));
		expected.add(new Commitment(clinicianID, 9, "Wednesday", "other desc"));
		assertEquals(actual, expected);
	}
	
	@Test
	public void testLoadCommitmentsEmpty() throws Exception {
		List<Commitment> actual = commitmentsDao.loadCommitments(0);
		List<Commitment> expected = new ArrayList<Commitment>();
		assertEquals(actual, expected);
	}
	
	@Test
	public void testDeleteCommitment() throws Exception {
		int clinicianID = 0;
		PreparedStatement stmt = conn.prepareStatement("INSERT INTO Commitments (id, hour, day, description) VALUES (?, ?, ?, ?)");
		stmt.setInt(1, clinicianID);
		stmt.setInt(2, 8);
		stmt.setString(3, "Friday");
		stmt.setString(4, "desc");
		stmt.execute();
		stmt.close();
		
		commitmentsDao.delete(clinicianID);
		
		stmt = conn.prepareStatement("SELECT hour, day, description FROM Commitments WHERE id = ?");
		stmt.setInt(1, clinicianID);
		stmt.execute();
		ResultSet results = stmt.getResultSet();
		assertFalse(results.next());
		stmt.close();
	}
}
