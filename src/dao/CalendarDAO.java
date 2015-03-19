package dao;

import java.sql.Connection;
import java.sql.Date;
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
		Connection con = getConnection();
		
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
	 * Inserts a calendar object into the database.
	 *
	 * @param calendar the Calendar to add
	 * @throws SQLException the SQL exception
	 */
	public CalendarBean loadCalendar() throws SQLException {
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		stmt.execute("Select * From Calendar");
		
		ResultSet res = stmt.getResultSet();
		res.next();
		CalendarBean cal = new CalendarBean();
		cal.setId(res.getInt("id"));
		cal.setStartDate(res.getDate("startDate"));
		cal.setEndDate(res.getDate("endDate"));
		cal.setIaMinHours(res.getInt("iaMinHours"));
		cal.setEcMinHours(res.getInt("ecMinHours"));
		cal.setSemester(res.getInt("term"));
		return cal;
	}
	
	/**
	 * Gets the next available id for a row in the table.
	 *
	 * @return int representing the next available id
	 * @throws SQLException the SQL exception
	 */
	public int getNextAvailableId() throws SQLException {
		return DAO.getNextID("Calendar");		
	}

	public Semester getCurrentSemester() throws SQLException {
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		stmt.execute("Select term From Calendar");
		
		ResultSet res = stmt.getResultSet();
		res.next();
		int semester = res.getInt("term");
		return Semester.values()[semester];
	}

	public int getCurrentYear() throws SQLException {
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		stmt.execute("Select YEAR(startDate) AS year From Calendar");
		
		ResultSet res = stmt.getResultSet();
		res.next();
		return res.getInt("year");
	}
}
