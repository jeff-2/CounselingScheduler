package generator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

import dao.ConnectionFactory;

/**
 * The Class DBBuilder provides the functionality to execute the sql queries for
 * creating the database tables and dropping the database tables.
 */
public class DBBuilder {

	/** The Constant DROP_TABLES. */
	private static final String DROP_TABLES = "sql/dropTables.sql";

	/** The Constant CREATE_TABLES. */
	private static final String CREATE_TABLES = "sql/createTables.sql";

	/**
	 * The main method drops all tables and re-creates all the tables.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		try {
			rebuildAll();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Rebuilds all the tables in the database by dropping and recreating them.
	 *
	 * @throws SQLException
	 *             the SQL exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void rebuildAll() throws SQLException, IOException {
		dropTables();
		createTables();
	}

	/**
	 * Drop all tables in the database.
	 *
	 * @throws SQLException
	 *             the SQL exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void dropTables() throws SQLException, IOException {
		executeSQLFile(DROP_TABLES);
	}

	/**
	 * Creates all the tables in the database.
	 *
	 * @throws SQLException
	 *             the SQL exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void createTables() throws SQLException, IOException {
		executeSQLFile(CREATE_TABLES);
	}

	/**
	 * Executes the given sql file.
	 *
	 * @param path
	 *            the path
	 * @throws SQLException
	 *             the SQL exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void executeSQLFile(String path) throws SQLException,
			IOException {
		byte[] file = Files.readAllBytes(Paths.get(path));
		String fileContents = new String(file);
		List<String> queries = Arrays.asList(fileContents.split(";"));
		Connection conn = ConnectionFactory.getInstance();
		Statement stmt = conn.createStatement();
		for (String query : queries) {
			stmt.execute(query);
		}
		stmt.close();
	}
}
