package db;

import java.sql.Connection;

public class Dao {
	
	private Connection connection;
	
	public Dao(Connection conn) {
		connection = conn;
	}
	
	protected Connection getConnection() {
		return connection;
	}
}
