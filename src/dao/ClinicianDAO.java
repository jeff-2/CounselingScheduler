package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import bean.ClinicianBean;

/**
 * The Class ClinicianDao handles the interaction with the database for
 * accessing data in the Clinicians table.
 * 
 * @author jmfoste2, lim92
 */
public class ClinicianDAO extends DAO {

    /**
     * Instantiates a new clinician dao.
     *
     * @param conn
     *            the conn
     */
    public ClinicianDAO(Connection conn) {
	super(conn);
    }

    /**
     * Insert a clinician into the database.
     *
     * @param clinician
     *            the clinician
     * @throws SQLException
     *             the SQL exception
     */
    public void insert(ClinicianBean clinician) throws SQLException {
	PreparedStatement stmt = connection
		.prepareStatement("INSERT INTO Clinicians (id, name) VALUES (?, ?)");
	stmt.setInt(1, clinician.getClinicianID());
	stmt.setString(2, clinician.getName());
	stmt.execute();
	stmt.close();
    }

    /**
     * Load all clinicians from database.
     *
     * @return the list
     * @throws SQLException
     *             the SQL exception
     */
    public List<ClinicianBean> loadClinicians() throws SQLException {
	PreparedStatement stmt = connection
		.prepareStatement("SELECT * FROM Clinicians");
	stmt.execute();
	ResultSet results = stmt.getResultSet();

	List<ClinicianBean> clinicians = new ArrayList<ClinicianBean>();
	while (results.next()) {
	    clinicians.add(loadClinician(results));
	}
	stmt.close();

	return clinicians;
    }

    /**
     * Loads the list of clinician names
     * 
     * @return list of clinician names
     * @throws SQLException
     */
    public Vector<String> loadClinicianNames() throws SQLException {
	List<ClinicianBean> clinicians = loadClinicians();
	Vector<String> names = new Vector<>(clinicians.size());

	for (ClinicianBean cb : clinicians) {
	    names.add(cb.getName());
	}

	return names;
    }

    /**
     * Load a single clinician from a ResultSet.
     *
     * @param results
     *            the results
     * @return the clinician
     * @throws SQLException
     *             the SQL exception
     */
    private ClinicianBean loadClinician(ResultSet results) throws SQLException {
	int clinicianID = results.getInt("id");
	String clinicianName = results.getString("name");
	return new ClinicianBean(clinicianID, clinicianName);
    }

    /**
     * Delete a clinician from the database.
     *
     * @param clinicianID
     *            the clinician id
     * @throws SQLException
     *             the SQL exception
     */
    public void delete(int clinicianID) throws SQLException {
	PreparedStatement stmt = connection
		.prepareStatement("DELETE FROM Clinicians WHERE id = ?");
	stmt.setInt(1, clinicianID);
	stmt.execute();
	stmt.close();
    }

    /**
     * Gets the clinician id associated with a particular name.
     *
     * @param name
     *            the name
     * @return the clinician id
     * @throws SQLException
     *             the SQL exception
     */
    public int getClinicianID(String name) throws SQLException {
	PreparedStatement stmt = connection
		.prepareStatement("SELECT id FROM Clinicians WHERE name = ?");
	stmt.setString(1, name);
	stmt.execute();
	ResultSet results = stmt.getResultSet();
	if (!results.next()) {
	    stmt.close();
	    return -1;
	}

	int clinicianID = results.getInt("id");
	stmt.close();
	return clinicianID;
    }

    /**
     * Gets the next possible clinician id.
     * 
     * @throws SQLException
     *             the SQL exception
     */
    public int getNextClinicianID() throws SQLException {
	return getNextID("Clinicians");
    }
}
