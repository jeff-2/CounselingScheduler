package generator;

import java.sql.Connection;
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
		Statement stmt = conn.createStatement();
		stmt.execute("DELETE FROM Clinicians");
		stmt.close();
	}
	
	public void clearClinicianPreferencesTable() throws SQLException {
		Statement stmt = conn.createStatement();
		stmt.execute("DELETE FROM ClinicianPreferences");
		stmt.close();
	}
	
	public void clearCommitmentsTable() throws SQLException {
		Statement stmt = conn.createStatement();
		stmt.execute("DELETE FROM Commitments");
		stmt.close();
	}
	
	public void clearTimeAwayTable() throws SQLException {
		Statement stmt = conn.createStatement();
		stmt.execute("DELETE FROM TimeAway");
		stmt.close();
	}
}
