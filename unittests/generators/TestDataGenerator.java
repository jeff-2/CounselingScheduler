package generators;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class TestDataGenerator {
	
	private Connection conn;
	
	public TestDataGenerator(Connection con) {
		this.conn = con;
	}
	
	public void clearTables() throws SQLException {
		clearCalendarTable();
		clearHolidayTable();
		clearCliniciansTable();
		clearClinicianPreferencesTable();
		clearCommitmentsTable();
		clearTimeAwayTable();
	}
	
	public void clearCalendarTable() throws SQLException {
		Statement stmt = conn.createStatement();
		stmt.execute("DELETE FROM Calendar");
		stmt.close();
	}
	
	public void clearHolidayTable() throws SQLException {
		Statement stmt = conn.createStatement();
		stmt.execute("DELETE FROM Holiday");
		stmt.close();
	}
	
	public void clearCliniciansTable() throws SQLException {
		PreparedStatement stmt = conn.prepareStatement("DELETE FROM Clinicians");
		stmt.execute();
		stmt.close();
	}
	
	public void clearClinicianPreferencesTable() throws SQLException {
		PreparedStatement stmt = conn.prepareStatement("DELETE FROM ClinicianPreferences");
		stmt.execute();
		stmt.close();
	}
	
	public void clearCommitmentsTable() throws SQLException {
		PreparedStatement stmt = conn.prepareStatement("DELETE FROM Commitments");
		stmt.execute();
		stmt.close();
	}
	
	public void clearTimeAwayTable() throws SQLException {
		PreparedStatement stmt = conn.prepareStatement("DELETE FROM TimeAway");
		stmt.execute();
		stmt.close();
	}
}
