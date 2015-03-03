package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;


public class JDBCTest {
	
	public static void main(String [] args) throws Exception {
		
		String connectionUrl = "jdbc:sqlserver://localhost;" +
		   "databaseName=dbNameHere;user=usernameHere;password=passwordHere;";
		Connection con = DriverManager.getConnection(connectionUrl);
		Statement stmt = con.createStatement();
		// Test with simple table named Test with columns 'name' (varchar) and 'id' (int) 
		stmt.execute("SELECT * FROM Test");
		ResultSet res = stmt.getResultSet();
		res.next();
		System.out.println("name:" + res.getString("name"));
		System.out.println("id:" + res.getInt("id"));
	}
}
