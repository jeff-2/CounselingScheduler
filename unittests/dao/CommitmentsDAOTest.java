package dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import generator.TestDataGenerator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import validator.DateRangeValidator;
import bean.CommitmentBean;

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
		CommitmentBean expected = new CommitmentBean(clinicianID, 4, 5, DateRangeValidator.parseDate("4/1/2015"), "Description");
		commitmentsDAO.insert(expected);
		
		PreparedStatement stmt = conn.prepareStatement("SELECT startHour, endHour, commitmentDate, description FROM Commitments WHERE id = ?");
		stmt.setInt(1, clinicianID);
		stmt.execute();
		ResultSet results = stmt.getResultSet();
		results.next();
		int startHour = results.getInt("startHour");
		int endHour = results.getInt("endHour");
		Date date = results.getDate("commitmentDate");
		String description = results.getString("description");
		
		
		assertEquals(expected, new CommitmentBean(clinicianID, startHour, endHour, date, description));
		stmt.close();
	}
	
	@Test
	public void testLoadCommitments() throws Exception {
		int clinicianID = 1;
		PreparedStatement stmt = conn.prepareStatement("INSERT INTO Commitments (id, startHour, endHour, commitmentDate, description) VALUES (?, ?, ?, ?, ?), (?, ?, ?, ?, ?)");
		stmt.setInt(1, clinicianID);
		stmt.setInt(2, 8);
		stmt.setInt(3, 9);
		stmt.setDate(4, new java.sql.Date(DateRangeValidator.parseDate("4/3/2015").getTime()));
		stmt.setString(5, "desc");
		stmt.setInt(6, clinicianID);
		stmt.setInt(7, 9);
		stmt.setInt(8, 10);
		stmt.setDate(9, new java.sql.Date(DateRangeValidator.parseDate("4/1/2015").getTime()));
		stmt.setString(10, "other desc");
		stmt.execute();
		stmt.close();
		
		List<CommitmentBean> actual = commitmentsDAO.loadCommitments(clinicianID);
		List<CommitmentBean> expected = new ArrayList<CommitmentBean>();
		expected.add(new CommitmentBean(clinicianID, 8, 9, DateRangeValidator.parseDate("4/3/2015"), "desc"));
		expected.add(new CommitmentBean(clinicianID, 9, 10, DateRangeValidator.parseDate("4/1/2015"), "other desc"));
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
		PreparedStatement stmt = conn.prepareStatement("INSERT INTO Commitments (id, startHour, endHour, commitmentDate, description) VALUES (?, ?, ?, ?, ?)");
		stmt.setInt(1, clinicianID);
		stmt.setInt(2, 8);
		stmt.setInt(3, 9);
		stmt.setDate(4, new java.sql.Date(DateRangeValidator.parseDate("4/3/2015").getTime()));
		stmt.setString(5, "desc");
		stmt.execute();
		stmt.close();
		
		commitmentsDAO.delete(clinicianID);
		
		stmt = conn.prepareStatement("SELECT startHour, endHour, commitmentDate, description FROM Commitments WHERE id = ?");
		stmt.setInt(1, clinicianID);
		stmt.execute();
		ResultSet results = stmt.getResultSet();
		assertFalse(results.next());
		stmt.close();
	}
}
