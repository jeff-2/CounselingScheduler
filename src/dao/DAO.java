package dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * The Class DAO handles storage of database connection as well as method to get
 * next id in database tables.
 */
public class DAO {

	/** The connection. */
	protected Connection connection;

	/**
	 * Instantiates a new dao.
	 *
	 * @param conn
	 *            the conn
	 */
	public DAO(Connection conn) {
		connection = conn;
	}

	/**
	 * Utility method to get the next id in a table. Precondition: the table has
	 * a column called 'id'.
	 *
	 * @param table
	 *            the name of the table to query
	 * @return the next ID
	 * @throws SQLException
	 *             the SQL exception
	 */
	protected int getNextID(String table) throws SQLException {
		Statement stmt = connection.createStatement();

		stmt.execute("SELECT COUNT(*) AS count FROM " + table);
		ResultSet res = stmt.getResultSet();
		res.next();
		if (res.getInt("count") == 0) {
			return 0;
		}

		stmt.execute("SELECT MAX(id) AS max FROM " + table);
		res = stmt.getResultSet();
		res.next();
		return res.getInt("max") + 1;
	}
}
