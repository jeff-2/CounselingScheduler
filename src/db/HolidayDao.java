package db;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import forms.Holiday;

public class HolidayDao {
	public void insertHoliday(Holiday holiday, int calendarId, int id) throws SQLException {
		String connectionUrl = "jdbc:sqlserver://localhost;" +
				   "databaseName=CounselingScheduler;user=admin;password=admin;";
		Connection con = DriverManager.getConnection(connectionUrl);
		
		PreparedStatement stmt = con.prepareStatement("INSERT INTO Holiday(id, calendarId, "
				+ "name, startDate, endDate) VALUES (?, ?, ?, ?, ?)");
		
		
		stmt.setInt(1, id);
		stmt.setInt(2, calendarId);
		stmt.setString(3, holiday.getName());
		stmt.setDate(4, new java.sql.Date(holiday.getStartDate().getTime()));
		stmt.setDate(5, new java.sql.Date(holiday.getEndDate().getTime()));
		
		stmt.executeUpdate();
		
	}
	
	
}
