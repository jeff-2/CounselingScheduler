package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DAO {
	
	private Connection connection;
	
	public DAO(Connection conn) {
		connection = conn;
	}
	
	protected Connection getConnection() {
		return connection;
	}
	
	/**
	 * Utility method to get the next id in a table. Precondition: the table
	 * has a column called 'id'.
	 * 
	 * @param table the name of the table to query
	 * @return the next ID
	 * @throws SQLException
	 */
	public int getNextID(String table) throws SQLException {
		PreparedStatement stmt = getConnection().prepareStatement("SELECT * FROM "+table);
		stmt.execute();
		ResultSet results = stmt.getResultSet();
		if (!results.next()) {
			stmt.close();
			return 0;
		}

		int curMaxID = 0;
		while(results.next()) {
			int id = results.getInt("id");
			curMaxID = Math.max(curMaxID, id);
		}
		stmt.close();
		return curMaxID + 1;
	}
}
