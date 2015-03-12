package generators;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

import db.ConnectionFactory;

public class DBBuilder {
	
	private static final String DROP_TABLES = "sql/dropTables.sql";
	private static final String CREATE_TABLES = "sql/createTables.sql";

	public static void main(String[] args) throws Exception {
		rebuildAll();
	}

	public static void rebuildAll() throws SQLException, IOException {
		dropTables();
		createTables();
	}

	public static void dropTables() throws SQLException, IOException {
		executeSQLFile(DROP_TABLES);
	}

	public static void createTables() throws SQLException, IOException {
		executeSQLFile(CREATE_TABLES);
	}

	public static void executeSQLFile(String path) throws SQLException, IOException {
		byte [] file = Files.readAllBytes(Paths.get(path));
		String fileContents = new String(file);
		List<String> queries = Arrays.asList(fileContents.split(";"));
		Connection conn = ConnectionFactory.getInstance();
		for (String query : queries) {
			Statement stmt = conn.createStatement();
			stmt.execute(query);
			stmt.close();
		}
	}
}
