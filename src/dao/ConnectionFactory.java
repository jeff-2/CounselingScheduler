package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
	
	private static final String connectionUrl = "jdbc:sqlserver://localhost;databaseName=CounselingScheduler;user=admin;password=admin;";
	
	private static Connection conn = null;
	
	public static Connection getInstance() throws SQLException {
		if (conn == null) {
			conn = DriverManager.getConnection(connectionUrl);
		}
		return conn;
	}
}
