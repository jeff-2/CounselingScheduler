package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import forms.TimeAway;

public class TimeAwayDao extends Dao {
	
	public TimeAwayDao(Connection conn) {
		super(conn);
	}

	public void insert(TimeAway timeAway) throws SQLException {
		PreparedStatement stmt = getConnection().prepareStatement("INSERT INTO TimeAway (id, startDate, endDate, description) VALUES (?, ?, ?, ?)");
		stmt.setInt(1, timeAway.getClinicianID());
		stmt.setDate(2, new java.sql.Date(timeAway.getStartDate().getTime()));
		stmt.setDate(3, new java.sql.Date(timeAway.getEndDate().getTime()));
		stmt.setString(4, timeAway.getDescription());
		stmt.execute();
		stmt.close();
	}
	
	public List<TimeAway> loadTimeAway(int clinicianID) throws SQLException {
		PreparedStatement stmt = getConnection().prepareStatement("SELECT startDate, endDate, description FROM TimeAway WHERE id = ?");
		stmt.setInt(1, clinicianID);
		stmt.execute();
		ResultSet results = stmt.getResultSet();
		
		List<TimeAway> timesAway = new ArrayList<TimeAway>();
		while (results.next()) {
			timesAway.add(loadTimeAway(clinicianID, results));
		}
		stmt.close();
		
		return timesAway;
	}
	
	private TimeAway loadTimeAway(int clinicianID, ResultSet results) throws SQLException {
		Date startDate = new Date(results.getDate("startDate").getTime());
		Date endDate = new Date(results.getDate("endDate").getTime());
		String description = results.getString("description");
		return new TimeAway(clinicianID, description, endDate, startDate);
	}
	
	public void update(TimeAway timeAway) throws SQLException {
		PreparedStatement stmt = getConnection().prepareStatement("UPDATE TimeAway SET startDate = ?, endDate = ?, description = ? WHERE id = ?");
		stmt.setDate(1, new java.sql.Date(timeAway.getStartDate().getTime()));
		stmt.setDate(2, new java.sql.Date(timeAway.getEndDate().getTime()));
		stmt.setString(3, timeAway.getDescription());
		stmt.setInt(4, timeAway.getClinicianID());
		stmt.executeUpdate();
		stmt.close();
	}
	
	public void delete(int clinicianID) throws SQLException {
		PreparedStatement stmt = getConnection().prepareStatement("DELETE FROM TimeAway WHERE id = ?");
		stmt.setInt(1, clinicianID);
		stmt.execute();
		stmt.close();
	}
}
