package dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
	protected static final int getNextID(String table) throws SQLException {
		Connection con = ConnectionFactory.getInstance();
		Statement stmt = con.createStatement();
		
		stmt.execute("SELECT COUNT(*) AS count FROM "+table);
		ResultSet res = stmt.getResultSet();
		res.next();
		if(res.getInt("count") == 0) {
			return 0;
		}
				
		stmt.execute("SELECT MAX(id) AS max FROM "+table);
		res = stmt.getResultSet();
		res.next();
		return res.getInt("max") + 1;
	}
}
