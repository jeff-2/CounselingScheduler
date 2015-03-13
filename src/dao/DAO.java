package dao;

import java.sql.Connection;

public class DAO {
	
	private Connection connection;
	
	public DAO(Connection conn) {
		connection = conn;
	}
	
	protected Connection getConnection() {
		return connection;
	}
}
