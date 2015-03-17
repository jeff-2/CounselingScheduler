package dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bean.ClinicianPreferencesBean;
import dao.ClinicianPreferencesDAO;
import dao.ConnectionFactory;
import generator.TestDataGenerator;

/**
 * 
 * @author jmfoste2, lim92
 *
 */
public class ClinicianPreferencesDAOTest {
	
	private ClinicianPreferencesDAO clinicianPreferencesDAO;
	private Connection conn;
	private TestDataGenerator gen;
	
	@Before
	public void setUp() throws Exception {
		conn = ConnectionFactory.getInstance();
		clinicianPreferencesDAO = new ClinicianPreferencesDAO(conn);
		gen = new TestDataGenerator(conn);
		gen.clearClinicianPreferencesTable();
	}
	
	@After
	public void tearDown() throws Exception {
		gen.clearClinicianPreferencesTable();
	}
	
	@Test
	public void testInsertValidClinicianPreferences() throws Exception {
		int clinicianID = 0;
		ClinicianPreferencesBean preferences = new ClinicianPreferencesBean(clinicianID, 1, 2, 3);
		clinicianPreferencesDAO.insert(preferences);
		
		PreparedStatement stmt = conn.prepareStatement("SELECT morningRank, noonRank, afternoonRank FROM ClinicianPreferences WHERE id = ?");
		stmt.setInt(1, clinicianID);
		stmt.execute();
		ResultSet results = stmt.getResultSet();
		results.next();
		int morningRank = results.getInt("morningRank");
		int noonRank = results.getInt("noonRank");
		int afternoonRank = results.getInt("afternoonRank");
		stmt.close();
		
		assertEquals(preferences, new ClinicianPreferencesBean(clinicianID, morningRank, noonRank, afternoonRank));
	}
	
	@Test(expected=SQLException.class)
	public void testInsertDuplicateClinicianPreferences() throws Exception {
		ClinicianPreferencesBean preferences = new ClinicianPreferencesBean(0, 1, 2, 3);
		clinicianPreferencesDAO.insert(preferences);
		clinicianPreferencesDAO.insert(preferences);
	}
	
	@Test
	public void testLoadClinicianPreferences() throws Exception {
		PreparedStatement stmt = conn.prepareStatement("INSERT INTO ClinicianPreferences (id, morningRank, noonRank, afternoonRank) VALUES (?, ?, ?, ?), (?, ?, ?, ?)");
		stmt.setInt(1, 0);
		stmt.setInt(2, 1);
		stmt.setInt(3, 2);
		stmt.setInt(4, 3);
		stmt.setInt(5, 1);
		stmt.setInt(6, 3);
		stmt.setInt(7, 1);
		stmt.setInt(8, 2);
		
		stmt.execute();
		stmt.close();
		
		ClinicianPreferencesBean actual = clinicianPreferencesDAO.loadClinicianPreferences(0);
		ClinicianPreferencesBean expected = new ClinicianPreferencesBean(0, 1, 2, 3);
		assertEquals(actual, expected);
	}
	
	@Test
	public void testLoadClinicianPreferencesEmpty() throws Exception {
		ClinicianPreferencesBean actual = clinicianPreferencesDAO.loadClinicianPreferences(0);
		assertNull(actual);
	}
	
	@Test
	public void testUpdateClinicianPreferences() throws Exception {
		PreparedStatement stmt = conn.prepareStatement("INSERT INTO ClinicianPreferences (id, morningRank, noonRank, afternoonRank) VALUES (?, ?, ?, ?)");
		stmt.setInt(1, 0);
		stmt.setInt(2, 1);
		stmt.setInt(3, 2);
		stmt.setInt(4, 3);
		stmt.execute();
		stmt.close();
		
		ClinicianPreferencesBean expected = new ClinicianPreferencesBean(0, 3, 2, 1);
		
		clinicianPreferencesDAO.update(expected);
		stmt = conn.prepareStatement("SELECT morningRank, noonRank, afternoonRank FROM ClinicianPreferences WHERE id = ?");
		stmt.setInt(1, 0);
		stmt.execute();
		ResultSet results = stmt.getResultSet();
		results.next();
		int morningRank = results.getInt("morningRank");
		int noonRank = results.getInt("noonRank");
		int afternoonRank = results.getInt("afternoonRank");
		
		stmt.close();
		assertEquals(expected, new ClinicianPreferencesBean(0, morningRank, noonRank, afternoonRank));
	}
	
	@Test
	public void testDeleteValidClinician() throws Exception {
		int clinicianID = 0;
		PreparedStatement stmt = conn.prepareStatement("INSERT INTO ClinicianPreferences (id, morningRank, noonRank, afternoonRank) VALUES (?, ?, ?, ?)");
		stmt.setInt(1, clinicianID);
		stmt.setInt(2, 1);
		stmt.setInt(3, 2);
		stmt.setInt(4, 3);
		stmt.execute();
		stmt.close();
		
		clinicianPreferencesDAO.delete(clinicianID);
		stmt = conn.prepareStatement("SELECT morningRank, noonRank, afternoonRank FROM ClinicianPreferences WHERE id = ?");
		stmt.setInt(1, clinicianID);
		stmt.execute();
		ResultSet results = stmt.getResultSet();
		assertFalse(results.next());
		stmt.close();
	}
}
