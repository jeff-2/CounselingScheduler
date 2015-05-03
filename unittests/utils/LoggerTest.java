package utils;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * A class for testing utils/Logger.java
 * 
 * @author ramusa2, jmfoste2
 *
 */
public class LoggerTest {

    /** The log file dir. */
    private String logFileDir = "tempLogs/";
    
    /** The log file prefix. */
    private String logFilePrefix = "temp";
    
    /** The log file name. */
    private String logFileName = "";

    /** The out content. */
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    /**
     * Sets the test up.
     */
    @Before
    public void setUp() {
	System.setOut(new PrintStream(outContent));
    }

    /**
     * Test pre setup of logging.
     */
    @Test
    public void testPreSetup() {
	assertEquals(Logger.logFileIsOpen(), false);
	logFileName = Logger.getLogFileName();
	assertEquals(logFileName.isEmpty(), true);
	assertEquals(Logger.getLogStatus(), false);
	assertEquals(Logger.getDebugStatus(), false);
    }

    /**
     * Test initialize logging.
     */
    @Test
    public void testInitialize() {
	Logger.setLogDir(logFileDir);
	assertEquals(Logger.getLogDir(), logFileDir);
	Logger.initialize(logFilePrefix);
	assertEquals(Logger.logFileIsOpen(), true);
	logFileName = Logger.getLogFileName();
	assertEquals(logFileName.isEmpty(), false);
	assertEquals(logFileName.contains(logFilePrefix), true);
	assertEquals(Logger.getLogStatus(), true);
	assertEquals(Logger.getDebugStatus(), false);
    }

    /**
     * Test writing to log file.
     */
    @Test
    public void testWriting() {
	Logger.setLogStatus(false);
	assertEquals(Logger.getLogStatus(), false);
	Logger.setDebugStatus(true);
	assertEquals(Logger.getDebugStatus(), true);
	String statement = "hello world";
	Logger.log(statement);
	String console = outContent.toString();
	assertEquals(statement, console);
    }

    /**
     * Test closing the log file.
     *
     * @throws Exception the exception
     */
    @Test
    public void testClose() throws Exception {
	Logger.closeFileForLogging();
	assertEquals(Logger.logFileIsOpen(), false);
	assertEquals(Logger.getLogStatus(), false);
    }

    /**
     * Clean up.
     */
    @After
    public void cleanUp() {
	System.setOut(null);
	File logFile = new File(logFileName);
	logFile.delete();
	File dir = new File(logFileDir);
	dir.delete();
    }
}