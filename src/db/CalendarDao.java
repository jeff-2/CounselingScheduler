package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import forms.Calendar;

/**
 * Handles interactions with the database to access the Calendar table.
 * 
 * @author nbeltr2
 * @author dtli2
 */
public class CalendarDao {
	
	/**
	 * Inserts a calendar object into the database.
	 *
	 * @param calendar the Calendar to add
	 * @throws SQLException the SQL exception
	 */
	public void insertCalendar(Calendar calendar) throws SQLException {
		String connectionUrl = "jdbc:sqlserver://localhost;" +
				   "databaseName=CounselingScheduler;user=admin;password=admin;";
		Connection con = DriverManager.getConnection(connectionUrl);
		
		PreparedStatement stmt = con.prepareStatement("INSERT INTO Calendar (id, startDate, endDate, iaMinHours,"
				+ "ecMinHours, term) VALUES(?, ?, ?, ?, ?, ?)");
		
		stmt.setInt(1, calendar.getId());
		stmt.setDate(2, new java.sql.Date(calendar.getStartDate().getTime()));
		stmt.setDate(3, new java.sql.Date(calendar.getEndDate().getTime()));
		stmt.setInt(4,  calendar.getIaMinHours());
		stmt.setInt(5,  calendar.getEcMinHours());
		stmt.setInt(6, calendar.getSemester());
		
		stmt.executeUpdate();
	}
	
	/**
	 * Gets the next available id for a row in the table.
	 *
	 * @return int representing the next available id
	 * @throws SQLException the SQL exception
	 */
	public static int getNextAvailableId() throws SQLException {
		String connectionUrl = "jdbc:sqlserver://localhost;" +
				   "databaseName=CounselingScheduler;user=admin;password=admin;";
		Connection con = DriverManager.getConnection(connectionUrl);
		Statement stmt = con.createStatement();
		
		stmt.execute("SELECT COUNT(*) AS count FROM Calendar");
		ResultSet res = stmt.getResultSet();
		res.next();
		if(res.getInt("count") == 0) {
			return 0;
		}
				
		stmt.execute("SELECT MAX(id) AS max FROM Calendar");
		res = stmt.getResultSet();
		res.next();
		return res.getInt("max") + 1;
		
	}
}
