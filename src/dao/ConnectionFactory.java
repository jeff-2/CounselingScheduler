package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * A factory for creating Connection objects.
 */
public class ConnectionFactory {

	/** The conn. */
	private static Connection conn = null;

	/**
	 * Gets the single instance of Connection.
	 *
	 * @return single instance of Connection
	 * @throws SQLException
	 *             the SQL exception
	 */
	public static Connection getInstance() throws SQLException {
		if (conn == null) {
			try {
				conn = DriverManager.getConnection(DBUtils
						.loadConnectionConfig());
			} catch (ConnectionConfigException e) {
				throw new SQLException(
						"Unable to setup database connection because "
								+ e.getMessage());
			}
		}
		return conn;
	}
}
