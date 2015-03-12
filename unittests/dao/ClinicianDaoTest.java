package dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import generators.TestDataGenerator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import db.Clinician;
import db.ClinicianDao;
import db.ConnectionFactory;

/**
 * 
 * @author jmfoste2, lim92
 *
 */
public class ClinicianDaoTest {
	
	private ClinicianDao clinicianDao;
	private Connection conn;
	private TestDataGenerator gen;
	
	@Before
	public void setUp() throws Exception {
		conn = ConnectionFactory.getInstance();
		clinicianDao = new ClinicianDao(conn);
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
		Clinician clinician = new Clinician(clinicianID, "Jeff");
		clinicianDao.insert(clinician);
		PreparedStatement stmt = conn.prepareStatement("SELECT id, name FROM Clinicians WHERE id = ?");
		stmt.setInt(1, clinicianID);
		stmt.execute();
		ResultSet results = stmt.getResultSet();
		results.next();
		String name = results.getString("name");
		int id = results.getInt("id");
		stmt.close();
		assertEquals(clinician, new Clinician(id, name));
	}
	
	@Test(expected=SQLException.class)
	public void testInsertDuplicateClinician() throws Exception {
		Clinician clinician = new Clinician(1, "Jeff");
		clinicianDao.insert(clinician);
		clinicianDao.insert(clinician);
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
		
		List<Clinician> actual = clinicianDao.loadClinicians();
		List<Clinician> expected = new ArrayList<Clinician>();
		expected.add(new Clinician(0, "Jeff"));
		expected.add(new Clinician(1, "Kevin"));
		assertEquals(actual, expected);
	}
	
	@Test
	public void testLoadCliniciansEmpty() throws Exception {
		List<Clinician> actual = clinicianDao.loadClinicians();
		List<Clinician> expected = new ArrayList<Clinician>();
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

		clinicianDao.delete(clinicianID);
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
		
		int actualID = clinicianDao.getClinicianID(clinicianName);
		assertEquals(clinicianID, actualID);
	}
	
	@Test
	public void testGetInvalidClinicianID() throws Exception {
		int actualID = clinicianDao.getClinicianID("Jeff");
		assertEquals(actualID, -1);
	}
}
