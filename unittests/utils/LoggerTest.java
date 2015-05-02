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

    private String logFileDir = "tempLogs/";
    private String logFilePrefix = "temp";
    private String logFileName = "";

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @Before
    public void setUp() {
	System.setOut(new PrintStream(outContent));
    }

    @Test
    public void testPreSetup() {
	assertEquals(Logger.logFileIsOpen(), false);
	logFileName = Logger.getLogFileName();
	assertEquals(logFileName.isEmpty(), true);
	assertEquals(Logger.getLogStatus(), false);
	assertEquals(Logger.getDebugStatus(), false);
    }

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

    @Test
    public void testClose() throws Exception {
	Logger.closeFileForLogging();
	assertEquals(Logger.logFileIsOpen(), false);
	assertEquals(Logger.getLogStatus(), false);
    }

    @After
    public void cleanUp() {
	System.setOut(null);
	File logFile = new File(logFileName);
	logFile.delete();
	File dir = new File(logFileDir);
	dir.delete();
    }
}