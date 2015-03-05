package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import forms.Commitment;

public class CommitmentsDao extends Dao {
	
	public CommitmentsDao(Connection conn) {
		super(conn);
	}

	public void insert(Commitment commitment) throws SQLException {
		PreparedStatement stmt = getConnection().prepareStatement("INSERT INTO Commitments (id, hour, day, description) VALUES (?, ?, ?, ?)");
		stmt.setInt(1, commitment.getClinicianID());
		stmt.setInt(2, commitment.getHourOfDay());
		stmt.setString(3, commitment.getDayOfWeek());
		stmt.setString(4, commitment.getDescription());
		stmt.execute();
		stmt.close();
	}
	
	public List<Commitment> loadCommitments(int clinicianID) throws SQLException {
		PreparedStatement stmt = getConnection().prepareStatement("SELECT hour, day, description FROM Commitments WHERE id = ?");
		stmt.setInt(1, clinicianID);
		stmt.execute();
		ResultSet results = stmt.getResultSet();
		
		List<Commitment> commitments = new ArrayList<Commitment>();
		while (results.next()) {
			commitments.add(loadCommitment(clinicianID, results));
		}
		stmt.close();
		
		return commitments;
	}
	
	private Commitment loadCommitment(int clinicianID, ResultSet results) throws SQLException {
		int hour = results.getInt("hour");
		String day = results.getString("day");
		String description = results.getString("description");
		return new Commitment(clinicianID, hour, day, description);
	}
	
	public void update(Commitment commitment) throws SQLException {
		PreparedStatement stmt = getConnection().prepareStatement("UPDATE Commitments SET hour = ?, day = ?, description = ? WHERE id = ?");
		stmt.setInt(1, commitment.getHourOfDay());
		stmt.setString(2, commitment.getDayOfWeek());
		stmt.setString(3, commitment.getDescription());
		stmt.setInt(4, commitment.getClinicianID());
		stmt.executeUpdate();
		stmt.close();
	}
	
	public void delete(int clinicianID) throws SQLException {
		PreparedStatement stmt = getConnection().prepareStatement("DELETE FROM Commitments WHERE id = ?");
		stmt.setInt(1, clinicianID);
		stmt.execute();
		stmt.close();
	}
}
