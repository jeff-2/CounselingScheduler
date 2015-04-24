package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
	
	private static Connection conn = null;
	
	public static Connection getInstance() throws SQLException {
		if (conn == null) {
			try {
				conn = DriverManager.getConnection(DBUtils.loadConnectionConfig());
			} catch (ConnectionConfigException e) {
				throw new SQLException("Unable to setup database connection because " + e.getMessage());
			}
		}
		return conn;
	}
}
