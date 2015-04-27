package dao;

import generator.TestDataGenerator;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

/**
 * The Class DBUtils provides utilities for interacting with the database.
 */
public class DBUtils {
	
	/** The Constant CONFIG_FILEPATH. */
	private static final String CONFIG_FILEPATH = "db_connection_config.properties";
	
	/**
	 * Clear all tables except clinicians.
	 *
	 * @throws SQLException the SQL exception
	 */
	public static void clearAllTablesExceptClinicians() throws SQLException {
		TestDataGenerator generator = new TestDataGenerator(ConnectionFactory.getInstance());
		generator.clearCalendarTable();
		generator.clearClinicianPreferencesTable();
		generator.clearCommitmentsTable();
		generator.clearTimeAwayTable();
		generator.clearHolidayTable();
		generator.clearSessionCliniciansTable();
		generator.clearSessionsTable();
	}
	
	/**
	 * Load connection config from the default config file. Returns connection url
	 * specified by connection config file.
	 *
	 * @return the string
	 * @throws ConnectionConfigException the connection config exception
	 */
	public static String loadConnectionConfig() throws ConnectionConfigException {
		return loadConnectionConfig(CONFIG_FILEPATH);
	}
	
	/**
	 * Load connection config from the specified config file. Returns connection url
	 * specified by connection config file.
	 *
	 * @param filePath the file path
	 * @return the string
	 * @throws ConnectionConfigException the connection config exception
	 */
	public static String loadConnectionConfig(String filePath) throws ConnectionConfigException {
		StringBuilder connectionUrl = new StringBuilder();
		Properties properties = new Properties();
		FileInputStream in = null;
		try {
			in = new FileInputStream(filePath);
			properties.load(in);
		} catch (IOException e) {
			throw new ConnectionConfigException("Failed to load from database connection configuration file: " + filePath);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		String url = properties.getProperty("url");
		if (url == null || url.isEmpty()) {
			throw new ConnectionConfigException("No URL is specified in the database connection configuration file");
		}
		connectionUrl.append(url).append(';');
		String database = properties.getProperty("database");
		if (database == null || database.isEmpty()) {
			throw new ConnectionConfigException("No database name is specified in the database connection configuration file");
		}
		connectionUrl.append("databaseName=").append(database).append(';');
		String user = properties.getProperty("user");
		if (user == null || user.isEmpty()) {
			throw new ConnectionConfigException("No username is specified in the database connection configuration file");
		}
		connectionUrl.append("user=").append(user).append(';');
		String password = properties.getProperty("password");
		if (password == null || password.isEmpty()) {
			throw new ConnectionConfigException("No password is specified in the database connection configuration file");
		}
		connectionUrl.append("password=").append(password).append(';');
		return connectionUrl.toString();
	}
}
