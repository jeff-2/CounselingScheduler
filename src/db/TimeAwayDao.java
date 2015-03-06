package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import forms.TimeAway;

/**
 * The Class TimeAwayDao handles the interaction with the database for the table TimeAway.
 * 
 * @author jmfoste2, lim92
 */
public class TimeAwayDao extends Dao {
	
	/**
	 * Instantiates a new time away dao.
	 *
	 * @param conn the conn
	 */
	public TimeAwayDao(Connection conn) {
		super(conn);
	}

	/**
	 * Insert a time away for a particular clinician into the database.
	 *
	 * @param timeAway the time away
	 * @throws SQLException the SQL exception
	 */
	public void insert(TimeAway timeAway) throws SQLException {
		PreparedStatement stmt = getConnection().prepareStatement("INSERT INTO TimeAway (id, startDate, endDate, description) VALUES (?, ?, ?, ?)");
		stmt.setInt(1, timeAway.getClinicianID());
		stmt.setLong(2, timeAway.getStartDate().getTime());
		stmt.setLong(3, timeAway.getEndDate().getTime());
		stmt.setString(4, timeAway.getDescription());
		stmt.execute();
		stmt.close();
	}
	
	/**
	 * Load all the time away for a particular clinician from the database.
	 *
	 * @param clinicianID the clinician id
	 * @return the list
	 * @throws SQLException the SQL exception
	 */
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
	
	/**
	 * Load the time away for a particular clinician id from a result set
	 *
	 * @param clinicianID the clinician id
	 * @param results the results
	 * @return the time away
	 * @throws SQLException the SQL exception
	 */
	private TimeAway loadTimeAway(int clinicianID, ResultSet results) throws SQLException {
		Date startDate = new Date(results.getLong("startDate"));
		Date endDate = new Date(results.getLong("endDate"));
		String description = results.getString("description");
		return new TimeAway(clinicianID, description, startDate, endDate);
	}

	
	/**
	 * Deletes all the time away from the database for a particular clinician id.
	 *
	 * @param clinicianID the clinician id
	 * @throws SQLException the SQL exception
	 */
	public void delete(int clinicianID) throws SQLException {
		PreparedStatement stmt = getConnection().prepareStatement("DELETE FROM TimeAway WHERE id = ?");
		stmt.setInt(1, clinicianID);
		stmt.execute();
		stmt.close();
	}
}
