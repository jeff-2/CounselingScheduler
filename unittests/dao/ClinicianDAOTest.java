package dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import generator.TestDataGenerator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bean.ClinicianBean;
import dao.ClinicianDAO;
import dao.ConnectionFactory;

/**
 * 
 * @author jmfoste2, lim92
 *
 */ 
public class ClinicianDAOTest {
	
	private ClinicianDAO clinicianDAO;
	private Connection conn;
	private TestDataGenerator gen;
	
	@Before
	public void setUp() throws Exception {
		conn = ConnectionFactory.getInstance();
		clinicianDAO = new ClinicianDAO(conn);
		gen = new TestDataGenerator(conn);
		gen.clearCliniciansTable();
	}
	
	@After
	public void tearDown() throws Exception {
		gen.clearCliniciansTable();
	}
	
	@Test
	public void testInsertValidClinician() throws Exception {
		int clinicianID = 1;
		ClinicianBean clinician = new ClinicianBean(clinicianID, "Jeff");
		clinicianDAO.insert(clinician);
		PreparedStatement stmt = conn.prepareStatement("SELECT id, name FROM Clinicians WHERE id = ?");
		stmt.setInt(1, clinicianID);
		stmt.execute();
		ResultSet results = stmt.getResultSet();
		results.next();
		String name = results.getString("name");
		int id = results.getInt("id");
		stmt.close();
		assertEquals(clinician, new ClinicianBean(id, name));
	}
	
	@Test(expected=SQLException.class)
	public void testInsertDuplicateClinician() throws Exception {
		ClinicianBean clinician = new ClinicianBean(1, "Jeff");
		clinicianDAO.insert(clinician);
		clinicianDAO.insert(clinician);
	}
	
	@Test
	public void testLoadClinicians() throws Exception {
		PreparedStatement stmt = conn.prepareStatement("INSERT INTO Clinicians (id, name) VALUES (?, ?), (?, ?)");
		stmt.setInt(1, 0);
		stmt.setString(2, "Jeff");
		stmt.setInt(3, 1);
		stmt.setString(4, "Kevin");
		stmt.execute();
		stmt.close();
		
		List<ClinicianBean> actual = clinicianDAO.loadClinicians();
		List<ClinicianBean> expected = new ArrayList<ClinicianBean>();
		expected.add(new ClinicianBean(0, "Jeff"));
		expected.add(new ClinicianBean(1, "Kevin"));
		assertEquals(actual, expected);
		assertEquals(clinicianDAO.getNextClinicianID(), 2);
	}
	
	@Test
	public void testLoadCliniciansEmpty() throws Exception {
		List<ClinicianBean> actual = clinicianDAO.loadClinicians();
		List<ClinicianBean> expected = new ArrayList<ClinicianBean>();
		assertEquals(actual, expected);
	}
	
	@Test
	public void testDeleteValidClinician() throws Exception {
		int clinicianID = 0;
		PreparedStatement stmt = conn.prepareStatement("INSERT INTO Clinicians (id, name) VALUES (?, ?)");
		stmt.setInt(1, clinicianID);
		stmt.setString(2, "Jeff");
		stmt.execute();
		stmt.close();

		clinicianDAO.delete(clinicianID);
		stmt = conn.prepareStatement("SELECT id, name FROM Clinicians WHERE id = ?");
		stmt.setInt(1, clinicianID);
		stmt.execute();
		ResultSet results = stmt.getResultSet();
		assertFalse(results.next());
		stmt.close();
	}
	
	@Test
	public void testGetValidClinicianID() throws Exception {
		int clinicianID = 0;
		String clinicianName = "Jeff";
		PreparedStatement stmt = conn.prepareStatement("INSERT INTO Clinicians (id, name) VALUES (?, ?)");
		stmt.setInt(1, clinicianID);
		stmt.setString(2, clinicianName);
		stmt.execute();
		stmt.close();
		
		int actualID = clinicianDAO.getClinicianID(clinicianName);
		assertEquals(clinicianID, actualID);
	}
	
	@Test
	public void testGetInvalidClinicianID() throws Exception {
		int actualID = clinicianDAO.getClinicianID("Jeff");
		assertEquals(actualID, -1);
	}
}
