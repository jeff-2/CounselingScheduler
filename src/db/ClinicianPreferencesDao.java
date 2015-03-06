package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import forms.ClinicianPreferences;

/**
 * The Class ClinicianPreferencesDao handles interactions with the database to access the ClinicianPreferences table.
 * 
 * @author jmfoste2, lim92
 */
public class ClinicianPreferencesDao extends Dao {
	
	/**
	 * Instantiates a new clinician preferences dao.
	 *
	 * @param conn the conn
	 */
	public ClinicianPreferencesDao(Connection conn) {
		super(conn);
	}

	/**
	 * Insert a clinicians preferences into the database.
	 *
	 * @param preferences the preferences
	 * @throws SQLException the SQL exception
	 */
	public void insert(ClinicianPreferences preferences) throws SQLException {
		PreparedStatement stmt = getConnection().prepareStatement("INSERT INTO ClinicianPreferences (id, morningRank, noonRank, afternoonRank) VALUES (?, ?, ?, ?)");
		stmt.setInt(1, preferences.getClinicianID());
		stmt.setInt(2, preferences.getMorningRank());
		stmt.setInt(3, preferences.getNoonRank());
		stmt.setInt(4, preferences.getAfternoonRank());
		stmt.execute();
		stmt.close();
	}
	
	/**
	 * Loads all clinician preferences from the database.
	 *
	 * @param clinicianID the clinician id
	 * @return the clinician preferences
	 * @throws SQLException the SQL exception
	 */
	public ClinicianPreferences loadClinicianPreferences(int clinicianID) throws SQLException {
		PreparedStatement stmt = getConnection().prepareStatement("SELECT morningRank, noonRank, afternoonRank FROM ClinicianPreferences WHERE id = ?");
		stmt.setInt(1, clinicianID);
		stmt.execute();
		ResultSet results = stmt.getResultSet();
		if (!results.next()) {
			stmt.close();
			return null;
		}
		int morningRank = results.getInt("morningRank");
		int noonRank = results.getInt("noonRank");
		int afternoonRank = results.getInt("afternoonRank");
		ClinicianPreferences preferences = new ClinicianPreferences(clinicianID, morningRank, noonRank, afternoonRank);
		stmt.close();
		
		return preferences;
	}
	
	/**
	 * Update the preferences for a particular clinician in the database.
	 *
	 * @param preferences the preferences
	 * @throws SQLException the SQL exception
	 */
	public void update(ClinicianPreferences preferences) throws SQLException {
		PreparedStatement stmt = getConnection().prepareStatement("UPDATE ClinicianPreferences SET morningRank = ?, noonRank = ?, afternoonRank = ? WHERE id = ?");
		stmt.setInt(1, preferences.getMorningRank());
		stmt.setInt(2, preferences.getNoonRank());
		stmt.setInt(3, preferences.getAfternoonRank());
		stmt.setInt(4, preferences.getClinicianID());
		stmt.executeUpdate();
		stmt.close();
	}
	
	/**
	 * Delete a particular clinician's preferences from the database.
	 *
	 * @param clinicianID the clinician id
	 * @throws SQLException the SQL exception
	 */
	public void delete(int clinicianID) throws SQLException {
		PreparedStatement stmt = getConnection().prepareStatement("DELETE FROM ClinicianPreferences WHERE id = ?");
		stmt.setInt(1, clinicianID);
		stmt.execute();
		stmt.close();
	}
}
