package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import bean.CalendarBean;
import bean.Semester;

/**
 * Handles interactions with the database to access the Calendar table.
 * 
 * @author nbeltr2
 * @author dtli2
 */
public class CalendarDAO extends DAO {
	
	public CalendarDAO(Connection conn) {
		super(conn);
	}

	/**
	 * Inserts a calendar object into the database.
	 *
	 * @param calendar the Calendar to add
	 * @throws SQLException the SQL exception
	 */
	public void insertCalendar(CalendarBean calendar) throws SQLException {		
		PreparedStatement stmt = connection.prepareStatement("INSERT INTO Calendar (id, startDate, endDate, iaMinHours,"
				+ "ecMinHours, term, meetingFilepath) VALUES(?, ?, ?, ?, ?, ?, ?)");
		
		stmt.setInt(1, calendar.getId());
		stmt.setDate(2, new java.sql.Date(calendar.getStartDate().getTime()));
		stmt.setDate(3, new java.sql.Date(calendar.getEndDate().getTime()));
		stmt.setInt(4,  calendar.getIaMinHours());
		stmt.setInt(5,  calendar.getEcMinHours());
		stmt.setInt(6, calendar.getSemester().ordinal());
		stmt.setString(7, calendar.getMeetingFilepath());
		
		stmt.executeUpdate();
	}

	/**
	 * Inserts a calendar object into the database.
	 *
	 * @param calendar the Calendar to add
	 * @throws SQLException the SQL exception
	 */
	public CalendarBean loadCalendar() throws SQLException {
		Statement stmt = connection.createStatement();
		stmt.execute("Select * From Calendar");
		
		ResultSet res = stmt.getResultSet();
		res.next();
		CalendarBean cal = new CalendarBean();
		cal.setStartDate(res.getDate("startDate"));
		cal.setEndDate(res.getDate("endDate"));
		cal.setSemester(Semester.values()[res.getInt("term")]);
		cal.setIaMinHours(res.getInt("iaMinHours"));
		cal.setEcMinHours(res.getInt("ecMinHours"));
		cal.setId(res.getInt("id"));
		cal.setMeetingFilepath(res.getString("meetingFilepath"));
		return cal;
	}
	
	public boolean calendarExists() throws SQLException {
		Statement stmt = connection.createStatement();
		stmt.execute("Select * From Calendar");
		ResultSet res = stmt.getResultSet();
		return res.next();
	}
	
	/**
	 * Gets the next available id for a row in the table.
	 *
	 * @return int representing the next available id
	 * @throws SQLException the SQL exception
	 */
	public int getNextAvailableId() throws SQLException {
		return getNextID("Calendar");		
	}
}
