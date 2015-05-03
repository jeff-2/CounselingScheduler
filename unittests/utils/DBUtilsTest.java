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

/**
 * The Class DBUtilsTest tests the functionality of DBUtils.
 */
public class DBUtilsTest {

    /** The config file. */
    private File configFile;

    /**
     * Sets the test up.
     *
     * @throws SQLException the SQL exception
     */
    @Before
    public void setUp() throws SQLException {
	configFile = new File("tmp_config_file.properties");
    }

    /**
     * Tear down.
     *
     * @throws SQLException the SQL exception
     */
    @After
    public void tearDown() throws SQLException {
	configFile.delete();
    }

    /**
     * Test config file with missing url.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testInvalidConfigFileMissingUrl() throws IOException {
	try {
	    writeLines(
		    configFile,
		    Arrays.asList(new String[] {
			    "database=CounselingScheduler", "user=admin",
			    "password=admin" }));
	    DBUtils.loadConnectionConfig(configFile.getAbsolutePath());
	    fail();
	} catch (ConnectionConfigException e) {
	    assertEquals(
		    "No URL is specified in the database connection configuration file",
		    e.getMessage());
	}
    }

    /**
     * Test config file missing database name.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testInvalidConfigFileMissingDatabase() throws IOException {
	try {
	    writeLines(
		    configFile,
		    Arrays.asList(new String[] {
			    "url=jdbc:sqlserver://localhost", "user=admin",
			    "password=admin" }));
	    DBUtils.loadConnectionConfig(configFile.getAbsolutePath());
	    fail();
	} catch (ConnectionConfigException e) {
	    assertEquals(
		    "No database name is specified in the database connection configuration file",
		    e.getMessage());
	}
    }

    /**
     * Test config file with missing user.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testInvalidConfigFileMissingUser() throws IOException {
	try {
	    writeLines(
		    configFile,
		    Arrays.asList(new String[] {
			    "url=jdbc:sqlserver://localhost",
			    "database=CounselingScheduler", "password=admin" }));
	    DBUtils.loadConnectionConfig(configFile.getAbsolutePath());
	    fail();
	} catch (ConnectionConfigException e) {
	    assertEquals(
		    "No username is specified in the database connection configuration file",
		    e.getMessage());
	}
    }

    /**
     * Test config file with missing password.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testInvalidConfigFileMissingPassword() throws IOException {
	try {
	    writeLines(
		    configFile,
		    Arrays.asList(new String[] {
			    "url=jdbc:sqlserver://localhost",
			    "database=CounselingScheduler", "user=admin" }));
	    DBUtils.loadConnectionConfig(configFile.getAbsolutePath());
	    fail();
	} catch (ConnectionConfigException e) {
	    assertEquals(
		    "No password is specified in the database connection configuration file",
		    e.getMessage());
	}
    }

    /**
     * Test missing config file.
     */
    @Test
    public void testMissingConfigFile() {
	try {
	    DBUtils.loadConnectionConfig(configFile.getAbsolutePath());
	    fail();
	} catch (ConnectionConfigException e) {
	    assertEquals(
		    "Failed to load from database connection configuration file: "
			    + configFile.getAbsolutePath(), e.getMessage());
	}
    }

    /**
     * Test valid config file.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws ConnectionConfigException the connection config exception
     */
    @Test
    public void testValidConfigFile() throws IOException,
	    ConnectionConfigException {
	writeLines(
		configFile,
		Arrays.asList(new String[] { "url=jdbc:sqlserver://localhost",
			"database=CounselingScheduler", "user=admin",
			"password=admin" }));
	String connectionUrl = DBUtils.loadConnectionConfig(configFile
		.getAbsolutePath());
	assertEquals(
		"jdbc:sqlserver://localhost;databaseName=CounselingScheduler;user=admin;password=admin;",
		connectionUrl);
    }

    /**
     * Writes each string in data as a separate line into the specified file.
     *
     * @param file the file
     * @param data the data
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void writeLines(File file, List<String> data) throws IOException {
	FileOutputStream out = new FileOutputStream(file);
	for (String line : data) {
	    out.write(line.getBytes());
	    out.write('\n');
	}
	out.close();
    }
}
