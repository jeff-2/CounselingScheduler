package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClinicianDao extends Dao {

	public ClinicianDao(Connection conn) {
		super(conn);
	}
	
	public void insert(Clinician clinician) throws SQLException {
		PreparedStatement stmt = getConnection().prepareStatement("INSERT INTO Clinicians (id, name) VALUES (?, ?)");
		stmt.setInt(1, clinician.getClinicianID());
		stmt.setString(2, clinician.getName());
		stmt.execute();
		stmt.close();
	}
	
	public List<Clinician> loadClinicians() throws SQLException {
		PreparedStatement stmt = getConnection().prepareStatement("SELECT * FROM Clinicians");
		stmt.execute();
		ResultSet results = stmt.getResultSet();
		
		List<Clinician> clinicians = new ArrayList<Clinician>();
		while (results.next()) {
			clinicians.add(loadClinician(results));
		}
		stmt.close();
		
		return clinicians;
	}
	
	private Clinician loadClinician(ResultSet results) throws SQLException {
		int clinicianID = results.getInt("id");
		String clinicianName = results.getString("name");
		return new Clinician(clinicianID, clinicianName);
	}
	
	public void delete(int clinicianID) throws SQLException {
		PreparedStatement stmt = getConnection().prepareStatement("DELETE FROM Clinician WHERE id = ?");
		stmt.setInt(1, clinicianID);
		stmt.execute();
		stmt.close();
	}
}
