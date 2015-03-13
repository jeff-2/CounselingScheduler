package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import bean.HolidayBean;

/**
 * Handles interactions with the database to access the Holiday table.
 */
public class HolidayDAO extends DAO {
	
	public HolidayDAO(Connection conn) {
		super(conn);
	}

	/**
	 * Inserts holiday object into the database.
	 *
	 * @param holiday the Holiday to add
	 * @param calendarId the id of the Calendar that the holiday is associated with
	 * @param id the id of the holiday
	 * @throws SQLException the SQL exception
	 */
	public void insertHoliday(HolidayBean holiday, int calendarId, int id) throws SQLException {

		Connection con = getConnection();
		
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
