package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

	
	/**
	 * Load all the holidays from the database.
	 *
	 * @return the list
	 * @throws SQLException the SQL exception
	 */
	public List<HolidayBean> loadHolidays() throws SQLException {
		PreparedStatement stmt = getConnection().prepareStatement("SELECT * FROM Holiday");
		stmt.execute();
		ResultSet results = stmt.getResultSet();
		
		List<HolidayBean> holidays = new ArrayList<HolidayBean>();
		while (results.next()) {
			holidays.add(loadHoliday(results));
		}
		stmt.close();
		
		return holidays;
	}

	/**
	 * Reads a holiday from the current loadHolidays results pointer
	 * @return the constructed HolidayBean object
	 */
	private HolidayBean loadHoliday(ResultSet res) throws SQLException {
		int id = res.getInt("id");
		//int calendarID = res.getInt("calendarId");
		String name = res.getString("name");
		Date startDate = res.getDate("startDate");
		Date endDate = res.getDate("endDate");
		return new HolidayBean(id, name, startDate, endDate);
	}
	
}
