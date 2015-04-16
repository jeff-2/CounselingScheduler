package dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import generator.TestDataGenerator;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bean.ClinicianPreferencesBean;

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
		ClinicianPreferencesBean preferences = new ClinicianPreferencesBean(0, 1, 2, 3, 5, 10);
		clinicianPreferencesDAO.insert(preferences);
		
		ClinicianPreferencesBean actual = clinicianPreferencesDAO.loadClinicianPreferences(preferences.getClinicianID());
		assertEquals(preferences, actual);
	}
	
	@Test(expected=SQLException.class)
	public void testInsertDuplicateClinicianPreferences() throws Exception {
		ClinicianPreferencesBean preferences = new ClinicianPreferencesBean(0, 1, 2, 3, 5, 10);
		clinicianPreferencesDAO.insert(preferences);
		clinicianPreferencesDAO.insert(preferences);
	}
	
	@Test
	public void testLoadClinicianPreferencesEmpty() throws Exception {
		ClinicianPreferencesBean actual = clinicianPreferencesDAO.loadClinicianPreferences(0);
		assertNull(actual);
	}
	
	@Test
	public void testUpdateClinicianPreferences() throws Exception {
		ClinicianPreferencesBean preferences = new ClinicianPreferencesBean(0, 1, 2, 3, 5, 10);
		clinicianPreferencesDAO.insert(preferences);
		
		ClinicianPreferencesBean expected = new ClinicianPreferencesBean(0, 3, 2, 1, 6, 8);
		clinicianPreferencesDAO.update(expected);
		ClinicianPreferencesBean actual = clinicianPreferencesDAO.loadClinicianPreferences(preferences.getClinicianID());
		assertEquals(expected, actual);
	}
	
	@Test
	public void testDeleteValidClinician() throws Exception {
		ClinicianPreferencesBean preferences = new ClinicianPreferencesBean(0, 1, 2, 3, 7, 15);
		clinicianPreferencesDAO.insert(preferences);
		
		clinicianPreferencesDAO.delete(preferences.getClinicianID());
		ClinicianPreferencesBean actual = clinicianPreferencesDAO.loadClinicianPreferences(preferences.getClinicianID());
		assertNull(actual);
	}
}
