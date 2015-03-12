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

import db.ClinicianPreferencesDao;
import db.ConnectionFactory;
import forms.ClinicianPreferences;
import generators.TestDataGenerator;

/**
 * 
 * @author jmfoste2, lim92
 *
 */
public class ClinicianPreferencesDaoTest {
	
	private ClinicianPreferencesDao clinicianPreferencesDao;
	private Connection conn;
	private TestDataGenerator gen;
	
	@Before
	public void setUp() throws Exception {
		conn = ConnectionFactory.getInstance();
		clinicianPreferencesDao = new ClinicianPreferencesDao(conn);
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
		ClinicianPreferences preferences = new ClinicianPreferences(clinicianID, 1, 2, 3);
		clinicianPreferencesDao.insert(preferences);
		
		PreparedStatement stmt = conn.prepareStatement("SELECT morningRank, noonRank, afternoonRank FROM ClinicianPreferences WHERE id = ?");
		stmt.setInt(1, clinicianID);
		stmt.execute();
		ResultSet results = stmt.getResultSet();
		results.next();
		int morningRank = results.getInt("morningRank");
		int noonRank = results.getInt("noonRank");
		int afternoonRank = results.getInt("afternoonRank");
		stmt.close();
		
		assertEquals(preferences, new ClinicianPreferences(clinicianID, morningRank, noonRank, afternoonRank));
	}
	
	@Test(expected=SQLException.class)
	public void testInsertDuplicateClinicianPreferences() throws Exception {
		ClinicianPreferences preferences = new ClinicianPreferences(0, 1, 2, 3);
		clinicianPreferencesDao.insert(preferences);
		clinicianPreferencesDao.insert(preferences);
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
		
		ClinicianPreferences actual = clinicianPreferencesDao.loadClinicianPreferences(0);
		ClinicianPreferences expected = new ClinicianPreferences(0, 1, 2, 3);
		assertEquals(actual, expected);
	}
	
	@Test
	public void testLoadClinicianPreferencesEmpty() throws Exception {
		ClinicianPreferences actual = clinicianPreferencesDao.loadClinicianPreferences(0);
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
		
		ClinicianPreferences expected = new ClinicianPreferences(0, 3, 2, 1);
		
		clinicianPreferencesDao.update(expected);
		stmt = conn.prepareStatement("SELECT morningRank, noonRank, afternoonRank FROM ClinicianPreferences WHERE id = ?");
		stmt.setInt(1, 0);
		stmt.execute();
		ResultSet results = stmt.getResultSet();
		results.next();
		int morningRank = results.getInt("morningRank");
		int noonRank = results.getInt("noonRank");
		int afternoonRank = results.getInt("afternoonRank");
		
		stmt.close();
		assertEquals(expected, new ClinicianPreferences(0, morningRank, noonRank, afternoonRank));
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
		
		clinicianPreferencesDao.delete(clinicianID);
		stmt = conn.prepareStatement("SELECT morningRank, noonRank, afternoonRank FROM ClinicianPreferences WHERE id = ?");
		stmt.setInt(1, clinicianID);
		stmt.execute();
		ResultSet results = stmt.getResultSet();
		assertFalse(results.next());
		stmt.close();
	}
}
