package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CalendarDao {
	public static int getNextAvailableId() throws SQLException {
		System.out.println("1");
		String connectionUrl = "jdbc:sqlserver://localhost;" +
				   "databaseName=CounselingScheduler;user=sa;password=w5Q[7S2_u2/\\+8Ds;";
		Connection con = DriverManager.getConnection(connectionUrl);
		Statement stmt = con.createStatement();
		
		stmt.execute("SELECT COUNT(*) AS count FROM Calendar");
		ResultSet res = stmt.getResultSet();
		res.next();
		if(res.getInt("count") == 0) {
			return 0;
		}
				
		stmt.execute("SELECT MAX(id) AS max FROM Calendar");
		res = stmt.getResultSet();
		res.next();
		return res.getInt("max") + 1;
		
	}
}
