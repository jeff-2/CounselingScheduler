package dao;

import static org.junit.Assert.assertEquals;
import generator.TestDataGenerator;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bean.ClinicianBean;

/**
 * The Class ClinicianDAOTest tests the functionality of ClinicianDAO.
 *
 * @author jmfoste2, lim92
 */
public class ClinicianDAOTest {

    /** The clinician dao. */
    private ClinicianDAO clinicianDAO;
    
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
	clinicianDAO = new ClinicianDAO(conn);
	gen = new TestDataGenerator(conn);
	gen.clearCliniciansTable();
    }

    /**
     * Tear down.
     *
     * @throws Exception the exception
     */
    @After
    public void tearDown() throws Exception {
	gen.clearCliniciansTable();
    }

    /**
     * Test insert valid clinician into the database.
     *
     * @throws Exception the exception
     */
    @Test
    public void testInsertValidClinician() throws Exception {
	ClinicianBean clinician = new ClinicianBean(0, "Jeff");
	clinicianDAO.insert(clinician);

	List<ClinicianBean> actual = clinicianDAO.loadClinicians();
	List<ClinicianBean> expected = new ArrayList<ClinicianBean>();
	expected.add(clinician);

	assertEquals(expected, actual);
	assertEquals(clinicianDAO.getNextClinicianID(), 1);
    }

    /**
     * Test insert duplicate clinician into the database.
     *
     * @throws Exception the exception
     */
    @Test(expected = SQLException.class)
    public void testInsertDuplicateClinician() throws Exception {
	ClinicianBean clinician = new ClinicianBean(1, "Jeff");
	clinicianDAO.insert(clinician);
	clinicianDAO.insert(clinician);
    }

    /**
     * Test load clinicians from the database.
     *
     * @throws Exception the exception
     */
    @Test
    public void testLoadClinicians() throws Exception {
	ClinicianBean clinicianBeanOne = new ClinicianBean(0, "Jeff");
	ClinicianBean clinicianBeanTwo = new ClinicianBean(1, "Kevin");
	clinicianDAO.insert(clinicianBeanOne);
	clinicianDAO.insert(clinicianBeanTwo);

	List<ClinicianBean> actual = clinicianDAO.loadClinicians();
	List<ClinicianBean> expected = new ArrayList<ClinicianBean>();
	expected.add(clinicianBeanOne);
	expected.add(clinicianBeanTwo);
	assertEquals(expected, actual);
	assertEquals(clinicianDAO.getNextClinicianID(), 2);
    }

    /**
     * Test load clinicians when there are none.
     *
     * @throws Exception the exception
     */
    @Test
    public void testLoadCliniciansEmpty() throws Exception {
	List<ClinicianBean> actual = clinicianDAO.loadClinicians();
	List<ClinicianBean> expected = new ArrayList<ClinicianBean>();
	assertEquals(actual, expected);
    }

    /**
     * Test delete valid clinician from the database.
     *
     * @throws Exception the exception
     */
    @Test
    public void testDeleteValidClinician() throws Exception {
	ClinicianBean expected = new ClinicianBean(0, "Jeff");
	clinicianDAO.insert(expected);
	clinicianDAO.delete(expected.getClinicianID());

	List<ClinicianBean> actualClinicians = clinicianDAO.loadClinicians();
	List<ClinicianBean> expectedClinicians = new ArrayList<ClinicianBean>();
	assertEquals(expectedClinicians, actualClinicians);
    }

    /**
     * Test get valid clinician id from the database.
     *
     * @throws Exception the exception
     */
    @Test
    public void testGetValidClinicianID() throws Exception {
	ClinicianBean expected = new ClinicianBean(0, "Jeff");
	clinicianDAO.insert(expected);

	int actualID = clinicianDAO.getClinicianID(expected.getName());
	assertEquals(expected.getClinicianID(), actualID);
    }

    /**
     * Test get clinician id for a clinician which does not exist in the database.
     *
     * @throws Exception the exception
     */
    @Test
    public void testGetInvalidClinicianID() throws Exception {
	int actualID = clinicianDAO.getClinicianID("Jeff");
	assertEquals(actualID, -1);
    }
}
