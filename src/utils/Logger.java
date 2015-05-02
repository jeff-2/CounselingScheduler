package utils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A static class for logging debug statements to file
 * 
 * @author ramusa2, jmfoste2
 *
 */
public abstract class Logger {

    /**
     * Default directory for writing logs
     */
    private static String DEFAULT_DIR_NAME = "logs/";

    /**
     * Name of the log file
     */
    private static String LOG_FILE_NAME;

    /**
     * Writer to the log file
     */
    private static PrintWriter PW;

    /**
     * If true, print log statements to file
     */
    private static boolean LOG;

    /**
     * If true, print log statements to console
     */
    private static boolean DEBUG;

    // Set initial state (both logging and debugging off)
    static {
	Logger.LOG_FILE_NAME = "";
	Logger.PW = null;
	Logger.LOG = false;
	Logger.DEBUG = false;
    }

    /**
     * Sets whether the logger prints messages to the console. Can be called
     * while a log file is active.
     * 
     * @param turnDebugOn
     *            if true, print future statements to console; if false, write
     *            future statements to file only.
     */
    public static void setLogStatus(boolean turnLoggingOn) {
	Logger.LOG = turnLoggingOn;
    }

    /**
     * Sets whether the logger prints messages to the console. Can be called
     * while a log file is active.
     * 
     * @param turnDebugOn
     *            if true, print future statements to console; if false, write
     *            future statements to file only.
     */
    public static void setDebugStatus(boolean turnDebugOn) {
	Logger.DEBUG = turnDebugOn;
    }

    /**
     * Opens a log file at the target address, and turns on logging.
     * 
     * @param filename
     *            address of the target log file
     */
    private static void openFileForLogging(String filename) {
	Logger.closeFileForLogging();
	try {
	    File logDir = new File(Logger.DEFAULT_DIR_NAME);
	    logDir.mkdirs();
	    Logger.PW = new PrintWriter(new File(filename));
	    Logger.LOG_FILE_NAME = filename;
	    Logger.LOG = true;
	} catch (IOException e) {
	    System.out.println("Exception while initializing logfile: "
		    + filename + "\nAborting logging.");
	    if (Logger.PW != null) {
		Logger.PW.close();
	    }
	    Logger.PW = null;
	    Logger.LOG_FILE_NAME = "";
	    Logger.LOG = false;
	    Logger.DEBUG = false;
	}
    }

    /**
     * Closes the current log file and turns off logging.
     */
    public static void closeFileForLogging() {
	if (Logger.PW != null) {
	    Logger.logln("Closing logfile " + Logger.LOG_FILE_NAME);
	    Logger.PW.close();
	    Logger.PW = null;
	    Logger.LOG = false;
	}
    }

    /**
     * Returns true if there is an active log file
     */
    public static boolean logFileIsOpen() {
	return Logger.PW != null;
    }

    /**
     * Initializes a log file in the default directory (logs). The name of the
     * logfile is
     * 
     * @param prefix
     *            log filename prefix
     */
    public static void initialize(String prefix) {
	String filename = Logger.DEFAULT_DIR_NAME;
	if (prefix != null && !prefix.isEmpty()) {
	    filename += prefix + ".";
	}
	filename += Logger.generateTimestamp() + ".txt";
	Logger.openFileForLogging(filename);
    }

    /**
     * Returns String with the current timestamp
     */
    private static String generateTimestamp() {
	return new SimpleDateFormat("yyyyMMddhhmm").format(new Date());
    }

    /**
     * Initializes a log file in the default directory (logs).
     */
    public static void initialize() {
	Logger.initialize("");
    }

    /**
     * Writes a log message to file without starting a new line. If Logger.DEBUG
     * is true, prints the message to the console as well.
     * 
     * @param statement
     *            the message to print
     */
    public static void log(String statement) {
	if (Logger.LOG) {
	    PW.print(statement);
	}
	if (Logger.DEBUG) {
	    System.out.print(statement);
	}
    }

    /**
     * Writes a log message to file and starts a new line If Logger.DEBUG is
     * true, prints the message to the console as well.
     * 
     * @param statement
     *            the message to print
     */
    private static void logln(String statement) {
	Logger.log(statement + "\n");
    }

    /**
     * Returns the name of this current log file
     */
    public static String getLogFileName() {
	return Logger.LOG_FILE_NAME;
    }

    /**
     * Returns the directory the logs are written to
     */
    public static String getLogDir() {
	return Logger.DEFAULT_DIR_NAME;
    }

    /**
     * Sets the log directory
     */
    public static void setLogDir(String dir) {
	if (!dir.endsWith("/")) {
	    dir += "/";
	}
	Logger.closeFileForLogging();
	Logger.DEFAULT_DIR_NAME = dir;
    }

    /**
     * Returns true if the logger is set to write to file
     */
    public static boolean getLogStatus() {
	return Logger.LOG;
    }

    /**
     * Returns true if the logger is set to write to file
     */
    public static boolean getDebugStatus() {
	return Logger.DEBUG;
    }
}
