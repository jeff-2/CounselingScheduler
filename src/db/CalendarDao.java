package db;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import forms.Calendar;

public class CalendarDao {
	public void insertCalendar(Calendar calendar) throws SQLException {
		String connectionUrl = "jdbc:sqlserver://localhost;" +
				   "databaseName=CounselingScheduler;user=sa;password=w5Q[7S2_u2/\\+8Ds;";
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
	
	public static int getNextAvailableId() throws SQLException {
		String connectionUrl = "jdbc:sqlserver://localhost;" +
				   "databaseName=CounselingScheduler;user=sa;password=w5Q[7S2_u2/\\+8Ds;";
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
