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
 * The Class ClinicianPreferencesDAOTest tests the ClinicianPreferencesDAO.
 *
 * @author jmfoste2, lim92
 */
public class ClinicianPreferencesDAOTest {

    /** The clinician preferences dao. */
    private ClinicianPreferencesDAO clinicianPreferencesDAO;
    
    /** The conn. */
    private Connection conn;
    
    /** The gen. */
    private TestDataGenerator gen;

    /**
     * Sets the test up.
     *
     * @throws Exception the exception
     */
    @Before
    public void setUp() throws Exception {
	conn = ConnectionFactory.getInstance();
	clinicianPreferencesDAO = new ClinicianPreferencesDAO(conn);
	gen = new TestDataGenerator(conn);
	gen.clearClinicianPreferencesTable();
    }

    /**
     * Tear down.
     *
     * @throws Exception the exception
     */
    @After
    public void tearDown() throws Exception {
	gen.clearClinicianPreferencesTable();
    }

    /**
     * Test insert valid clinician preferences into the database.
     *
     * @throws Exception the exception
     */
    @Test
    public void testInsertValidClinicianPreferences() throws Exception {
	ClinicianPreferencesBean preferences = new ClinicianPreferencesBean(0,
		1, 2, 3, 5, 10);
	clinicianPreferencesDAO.insert(preferences);

	ClinicianPreferencesBean actual = clinicianPreferencesDAO
		.loadClinicianPreferences(preferences.getClinicianID());
	assertEquals(preferences, actual);
    }

    /**
     * Test insertion of duplicate clinician preferences.
     *
     * @throws Exception the exception
     */
    @Test(expected = SQLException.class)
    public void testInsertDuplicateClinicianPreferences() throws Exception {
	ClinicianPreferencesBean preferences = new ClinicianPreferencesBean(0,
		1, 2, 3, 5, 10);
	clinicianPreferencesDAO.insert(preferences);
	clinicianPreferencesDAO.insert(preferences);
    }

    /**
     * Test loading clinician preferences when there are none.
     *
     * @throws Exception the exception
     */
    @Test
    public void testLoadClinicianPreferencesEmpty() throws Exception {
	ClinicianPreferencesBean actual = clinicianPreferencesDAO
		.loadClinicianPreferences(0);
	assertNull(actual);
    }

    /**
     * Test update clinician preferences.
     *
     * @throws Exception the exception
     */
    @Test
    public void testUpdateClinicianPreferences() throws Exception {
	ClinicianPreferencesBean preferences = new ClinicianPreferencesBean(0,
		1, 2, 3, 5, 10);
	clinicianPreferencesDAO.insert(preferences);

	ClinicianPreferencesBean expected = new ClinicianPreferencesBean(0, 3,
		2, 1, 6, 8);
	clinicianPreferencesDAO.update(expected);
	ClinicianPreferencesBean actual = clinicianPreferencesDAO
		.loadClinicianPreferences(preferences.getClinicianID());
	assertEquals(expected, actual);
    }

    /**
     * Test deletion of valid clinician preferences.
     *
     * @throws Exception the exception
     */
    @Test
    public void testDeleteValidClinician() throws Exception {
	ClinicianPreferencesBean preferences = new ClinicianPreferencesBean(0,
		1, 2, 3, 7, 15);
	clinicianPreferencesDAO.insert(preferences);

	clinicianPreferencesDAO.delete(preferences.getClinicianID());
	ClinicianPreferencesBean actual = clinicianPreferencesDAO
		.loadClinicianPreferences(preferences.getClinicianID());
	assertNull(actual);
    }
}
