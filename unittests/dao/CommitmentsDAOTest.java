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

import bean.CommitmentBean;
import dao.CommitmentsDAO;
import dao.ConnectionFactory;
import generator.TestDataGenerator;

/**
 * 
 * @author jmfoste2, lim92
 *
 */ 
public class CommitmentsDAOTest {

	private CommitmentsDAO commitmentsDAO;
	private Connection conn;
	private TestDataGenerator gen;
	
	@Before
	public void setUp() throws Exception {
		conn = ConnectionFactory.getInstance();
		commitmentsDAO = new CommitmentsDAO(conn);
		gen = new TestDataGenerator(conn);
		gen.clearCommitmentsTable();
	}
	
	@After
	public void tearDown() throws Exception {
		gen.clearCommitmentsTable();
	}
	
	@Test
	public void testInsertValidCommitment() throws Exception {
		int clinicianID = 1;
		CommitmentBean expected = new CommitmentBean(clinicianID, 4, "Wednesday", "Description");
		commitmentsDAO.insert(expected);
		
		PreparedStatement stmt = conn.prepareStatement("SELECT hour, day, description FROM Commitments WHERE id = ?");
		stmt.setInt(1, clinicianID);
		stmt.execute();
		ResultSet results = stmt.getResultSet();
		results.next();
		int hour = results.getInt("hour");
		String day = results.getString("day");
		String description = results.getString("description");
		
		
		assertEquals(expected, new CommitmentBean(clinicianID, hour, day, description));
		stmt.close();
	}
	
	@Test
	public void testLoadCommitments() throws Exception {
		int clinicianID = 1;
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
		
		List<CommitmentBean> actual = commitmentsDAO.loadCommitments(clinicianID);
		List<CommitmentBean> expected = new ArrayList<CommitmentBean>();
		expected.add(new CommitmentBean(clinicianID, 8, "Friday", "desc"));
		expected.add(new CommitmentBean(clinicianID, 9, "Wednesday", "other desc"));
		assertEquals(actual, expected);
	}
	
	@Test
	public void testLoadCommitmentsEmpty() throws Exception {
		List<CommitmentBean> actual = commitmentsDAO.loadCommitments(1);
		List<CommitmentBean> expected = new ArrayList<CommitmentBean>();
		assertEquals(actual, expected);
	}
	
	@Test
	public void testDeleteCommitment() throws Exception {
		int clinicianID = 1;
		PreparedStatement stmt = conn.prepareStatement("INSERT INTO Commitments (id, hour, day, description) VALUES (?, ?, ?, ?)");
		stmt.setInt(1, clinicianID);
		stmt.setInt(2, 8);
		stmt.setString(3, "Friday");
		stmt.setString(4, "desc");
		stmt.execute();
		stmt.close();
		
		commitmentsDAO.delete(clinicianID);
		
		stmt = conn.prepareStatement("SELECT hour, day, description FROM Commitments WHERE id = ?");
		stmt.setInt(1, clinicianID);
		stmt.execute();
		ResultSet results = stmt.getResultSet();
		assertFalse(results.next());
		stmt.close();
	}
}
