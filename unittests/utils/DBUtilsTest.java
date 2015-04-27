package utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import dao.ConnectionConfigException;
import dao.DBUtils;

public class DBUtilsTest {
	
	private File configFile;
	
	@Before
	public void setUp() throws SQLException {
		configFile = new File("tmp_config_file.properties");
	}
	
	@After
	public void tearDown() throws SQLException {
		configFile.delete();
	}
	
	@Test
	public void testInvalidConfigFileMissingUrl() throws IOException {
		try {
			writeLines(configFile, Arrays.asList(new String [] {
					"database=CounselingScheduler", "user=admin", "password=admin"
			}));
			DBUtils.loadConnectionConfig(configFile.getAbsolutePath());
			fail();
		} catch (ConnectionConfigException e) {
			assertEquals("No URL is specified in the database connection configuration file", e.getMessage());
		}
	}
	
	@Test
	public void testInvalidConfigFileMissingDatabase() throws IOException {
		try {
			writeLines(configFile, Arrays.asList(new String [] { "url=jdbc:sqlserver://localhost",
					"user=admin", "password=admin"
			}));
			DBUtils.loadConnectionConfig(configFile.getAbsolutePath());
			fail();
		} catch (ConnectionConfigException e) {
			assertEquals("No database name is specified in the database connection configuration file", e.getMessage());
		}
	}
	
	@Test
	public void testInvalidConfigFileMissingUser() throws IOException {
		try {
			writeLines(configFile, Arrays.asList(new String [] { "url=jdbc:sqlserver://localhost",
					"database=CounselingScheduler", "password=admin"
			}));
			DBUtils.loadConnectionConfig(configFile.getAbsolutePath());
			fail();
		} catch (ConnectionConfigException e) {
			assertEquals("No username is specified in the database connection configuration file", e.getMessage());
		}
	}
	
	@Test
	public void testInvalidConfigFileMissingPassword() throws IOException {
		try {
			writeLines(configFile, Arrays.asList(new String [] { "url=jdbc:sqlserver://localhost",
					"database=CounselingScheduler", "user=admin"
			}));
			DBUtils.loadConnectionConfig(configFile.getAbsolutePath());
			fail();
		} catch (ConnectionConfigException e) {
			assertEquals("No password is specified in the database connection configuration file", e.getMessage());
		}
	}
	
	@Test
	public void testMissingConfigFile() {
		try {
			DBUtils.loadConnectionConfig(configFile.getAbsolutePath());
			fail();
		} catch (ConnectionConfigException e) {
			assertEquals("Failed to load from database connection configuration file: " + configFile.getAbsolutePath(), e.getMessage());
		}
	}
	
	@Test
	public void testValidConfigFile() throws IOException, ConnectionConfigException {
		writeLines(configFile, Arrays.asList(new String [] { "url=jdbc:sqlserver://localhost",
				"database=CounselingScheduler", "user=admin", "password=admin"
		}));
		String connectionUrl = DBUtils.loadConnectionConfig(configFile.getAbsolutePath());
		assertEquals("jdbc:sqlserver://localhost;databaseName=CounselingScheduler;user=admin;password=admin;", connectionUrl);
	}
	
	private void writeLines(File file, List<String> data) throws IOException {
		FileOutputStream out = new FileOutputStream(file);
		for (String line : data) {
			out.write(line.getBytes());
			out.write('\n');
		}
		out.close();
	}
}
