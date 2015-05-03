package action;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import generator.TestDataGenerator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import validator.DateRangeValidator;
import bean.ClinicianBean;
import bean.CommitmentBean;
import dao.ClinicianDAO;
import dao.CommitmentsDAO;
import dao.ConnectionFactory;

/**
 * The Class ImportClinicianMeetingsActionTest tests the functionality of
 * ImportClinicianMeetingsAction.
 */
public class ImportClinicianMeetingsActionTest {

	/** The commitments dao. */
	private CommitmentsDAO commitmentsDAO;

	/** The clinician dao. */
	private ClinicianDAO clinicianDAO;

	/** The conn. */
	private Connection conn;

	/** The gen. */
	private TestDataGenerator gen;

	/** The action. */
	private ImportClinicianMeetingsAction action;

	/** The test data. */
	private Object[][] testData;

	/** The start date. */
	private Date startDate;

	/** The end date. */
	private Date endDate;

	/** The jeff, nathan, and ryan id. */
	private int jeffID, nathanID, ryanID;

	/**
	 * Sets the test up.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Before
	public void setUp() throws Exception {
		conn = ConnectionFactory.getInstance();
		gen = new TestDataGenerator(conn);
		gen.clearTables();
		commitmentsDAO = new CommitmentsDAO(conn);
		clinicianDAO = new ClinicianDAO(conn);
		clinicianDAO.insert(new ClinicianBean(0, "Jeff"));
		clinicianDAO.insert(new ClinicianBean(1, "Ryan"));
		clinicianDAO.insert(new ClinicianBean(2, "Nathan"));

		jeffID = clinicianDAO.getClinicianID("Jeff");
		nathanID = clinicianDAO.getClinicianID("Nathan");
		ryanID = clinicianDAO.getClinicianID("Ryan");

		startDate = DateRangeValidator.parseDate("1/25/2015");
		endDate = DateRangeValidator.parseDate("3/9/2015");
	}

	/**
	 * Test importing an empty meetings file.
	 *
	 * @throws SQLException
	 *             the SQL exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	public void testImportEmptyFile() throws SQLException, IOException {
		testData = new Object[0][0];
		try {
			action = new ImportClinicianMeetingsAction(conn,
					generateExcelFile(testData));
			action.insertImportedMeetings(endDate);
			fail();
		} catch (InvalidExcelFormatException e) {
			assertEquals("Excel document contains no data", e.getMessage());
		}
	}

	/**
	 * Tests an excel file with an invalid start time is caught.
	 *
	 * @throws SQLException
	 *             the SQL exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	public void testCatchInvalidStartTime() throws SQLException, IOException {
		testData = new Object[][] {
				{ "Meeting", "Duration", "Start Time", "End Time",
						"Staff Members", "Start Date", "Frequency", "Days",
						"Dates", "Location" },
				{ "Admin Team Meeting", "90 minutes", "8:00", "10:00am",
						"[Jeff], (Ryan), John", startDate, "Monthly",
						"1st Friday,    3rd Tuesday", null, "Room 212" } };
		try {
			action = new ImportClinicianMeetingsAction(conn,
					generateExcelFile(testData));
			action.insertImportedMeetings(endDate);
			fail();
		} catch (InvalidExcelFormatException e) {
			assertEquals(
					"Data under column 'Start Time' is of an invalid format '8:00'",
					e.getMessage());
		}
	}

	/**
	 * Tests an excel file with an invalid end time is caught.
	 *
	 * @throws SQLException
	 *             the SQL exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	public void testCatchInvalidEndTime() throws SQLException, IOException {
		testData = new Object[][] {
				{ "Meeting", "Duration", "Start Time", "End Time",
						"Staff Members", "Start Date", "Frequency", "Days",
						"Dates", "Location" },
				{ "Admin Team Meeting", "90 minutes", "8:30am", "10",
						"[Jeff], (Ryan), John", startDate, "Monthly",
						"1st Friday,    3rd Tuesday", null, "Room 212" } };
		try {
			action = new ImportClinicianMeetingsAction(conn,
					generateExcelFile(testData));
			action.insertImportedMeetings(endDate);
			fail();
		} catch (InvalidExcelFormatException e) {
			assertEquals(
					"Data under column 'End Time' is of an invalid format '10'",
					e.getMessage());
		}
	}

	/**
	 * Tests an excel file with an invalid meeting name is caught.
	 *
	 * @throws SQLException
	 *             the SQL exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	public void testCatchInvalidMeetingName() throws SQLException, IOException {
		testData = new Object[][] {
				{ "Meeting", "Duration", "Start Time", "End Time",
						"Staff Members", "Start Date", "Frequency", "Days",
						"Dates", "Location" },
				{ null, "90 minutes", "8:30am", "10:00am",
						"[Jeff], (Ryan), John", startDate, "Monthly",
						"1st Friday,    3rd Tuesday", null, "Room 212" } };
		try {
			action = new ImportClinicianMeetingsAction(conn,
					generateExcelFile(testData));
			action.insertImportedMeetings(endDate);
			fail();
		} catch (InvalidExcelFormatException e) {
			assertEquals(
					"Data under column 'Meeting' is of an invalid format 'Entry was not specified'",
					e.getMessage());
		}
	}

	/**
	 * Tests an excel file with invalid staff members are caught.
	 *
	 * @throws SQLException
	 *             the SQL exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	public void testCatchInvalidStaffMembers() throws SQLException, IOException {
		testData = new Object[][] {
				{ "Meeting", "Duration", "Start Time", "End Time",
						"Staff Members", "Start Date", "Frequency", "Days",
						"Dates", "Location" },
				{ "Admin Team Meeting", "90 minutes", "8:30am", "10:00am",
						null, startDate, "Monthly",
						"1st Friday,    3rd Tuesday", null, "Room 212" } };
		try {
			action = new ImportClinicianMeetingsAction(conn,
					generateExcelFile(testData));
			action.insertImportedMeetings(endDate);
			fail();
		} catch (InvalidExcelFormatException e) {
			assertEquals(
					"Data under column 'Staff Members' is of an invalid format 'Entry was not specified'",
					e.getMessage());
		}
	}

	/**
	 * Tests an excel file with an invalid frequency is caught.
	 *
	 * @throws SQLException
	 *             the SQL exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	public void testCatchInvalidFrequency() throws SQLException, IOException {
		testData = new Object[][] {
				{ "Meeting", "Duration", "Start Time", "End Time",
						"Staff Members", "Start Date", "Frequency", "Days",
						"Dates", "Location" },
				{ "Admin Team Meeting", "90 minutes", "8:30am", "10:00am",
						"[Jeff], (Ryan), John", startDate, "Daily",
						"1st Friday,    3rd Tuesday", null, "Room 212" } };
		try {
			action = new ImportClinicianMeetingsAction(conn,
					generateExcelFile(testData));
			action.insertImportedMeetings(endDate);
			fail();
		} catch (InvalidExcelFormatException e) {
			assertEquals(
					"Data under column 'Frequency' is of an invalid format 'Daily'",
					e.getMessage());
		}
	}

	/**
	 * Tests an excel file with invalid days is caught.
	 *
	 * @throws SQLException
	 *             the SQL exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	public void testCatchInvalidDays() throws SQLException, IOException {
		testData = new Object[][] {
				{ "Meeting", "Duration", "Start Time", "End Time",
						"Staff Members", "Start Date", "Frequency", "Days",
						"Dates", "Location" },
				{ "Admin Team Meeting", "90 minutes", "8:30am", "10:00am",
						"[Jeff], (Ryan), John", startDate, "Monthly",
						"Last Friday", null, "Room 212" } };
		try {
			action = new ImportClinicianMeetingsAction(conn,
					generateExcelFile(testData));
			action.insertImportedMeetings(endDate);
			fail();
		} catch (InvalidExcelFormatException e) {
			assertEquals(
					"Data under column 'Days' is of an invalid format 'LastFriday'",
					e.getMessage());
		}
	}

	/**
	 * Tests an excel file that has a monthly frequency with a particular day of
	 * the month.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testImportMonthlyDays() throws Exception {
		testData = new Object[][] {
				{ "Meeting", "Duration", "Start Time", "End Time",
						"Staff Members", "Start Date", "Frequency", "Days",
						"Dates", "Location" },
				{ "Admin Team Meeting", "90 minutes", "8:30am", "10:00am",
						"[Jeff], (Ryan), John", startDate, "Monthly",
						"1st Friday,    3rd Tuesday", null, "Room 212" } };
		action = new ImportClinicianMeetingsAction(conn,
				generateExcelFile(testData));
		action.insertImportedMeetings(endDate);
		List<CommitmentBean> commitments = new ArrayList<CommitmentBean>();
		List<CommitmentBean> tmp = commitmentsDAO.loadCommitments(jeffID);
		for (CommitmentBean commitment : tmp) {
			commitments.add(commitment);
		}
		tmp = commitmentsDAO.loadCommitments(ryanID);
		for (CommitmentBean commitment : tmp) {
			commitments.add(commitment);
		}

		List<CommitmentBean> expectedCommitments = new ArrayList<CommitmentBean>();
		expectedCommitments
				.add(new CommitmentBean(jeffID, 8, 10, DateRangeValidator
						.parseDate("2/6/2015"), "Admin Team Meeting"));
		expectedCommitments
				.add(new CommitmentBean(jeffID, 8, 10, DateRangeValidator
						.parseDate("3/6/2015"), "Admin Team Meeting"));
		expectedCommitments
				.add(new CommitmentBean(jeffID, 8, 10, DateRangeValidator
						.parseDate("2/17/2015"), "Admin Team Meeting"));
		expectedCommitments
				.add(new CommitmentBean(ryanID, 8, 10, DateRangeValidator
						.parseDate("2/6/2015"), "Admin Team Meeting"));
		expectedCommitments
				.add(new CommitmentBean(ryanID, 8, 10, DateRangeValidator
						.parseDate("3/6/2015"), "Admin Team Meeting"));
		expectedCommitments
				.add(new CommitmentBean(ryanID, 8, 10, DateRangeValidator
						.parseDate("2/17/2015"), "Admin Team Meeting"));
		assertEquals(commitments, expectedCommitments);
	}

	/**
	 * Tests an excel file with a biweekly frequency.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testImportBiweekly() throws Exception {
		testData = new Object[][] {
				{ "Meeting", "Duration", "Start Time", "End Time",
						"Staff Members", "Start Date", "Frequency", "Days",
						"Dates", "Location" },
				{ "Meeting", "60 minutes", "11:00am", "12:00pm",
						"[Nathan], [Ryan]", startDate, "Biweekly", "Thursday",
						null, "Room 1404" } };
		action = new ImportClinicianMeetingsAction(conn,
				generateExcelFile(testData));
		action.insertImportedMeetings(endDate);
		List<CommitmentBean> commitments = new ArrayList<CommitmentBean>();
		List<CommitmentBean> tmp = commitmentsDAO.loadCommitments(jeffID);
		tmp = commitmentsDAO.loadCommitments(nathanID);
		for (CommitmentBean commitment : tmp) {
			commitments.add(commitment);
		}
		tmp = commitmentsDAO.loadCommitments(ryanID);
		for (CommitmentBean commitment : tmp) {
			commitments.add(commitment);
		}

		List<CommitmentBean> expectedCommitments = new ArrayList<CommitmentBean>();
		expectedCommitments.add(new CommitmentBean(nathanID, 11, 12,
				DateRangeValidator.parseDate("1/29/2015"), "Meeting"));
		expectedCommitments.add(new CommitmentBean(nathanID, 11, 12,
				DateRangeValidator.parseDate("2/12/2015"), "Meeting"));
		expectedCommitments.add(new CommitmentBean(nathanID, 11, 12,
				DateRangeValidator.parseDate("2/26/2015"), "Meeting"));
		expectedCommitments.add(new CommitmentBean(ryanID, 11, 12,
				DateRangeValidator.parseDate("1/29/2015"), "Meeting"));
		expectedCommitments.add(new CommitmentBean(ryanID, 11, 12,
				DateRangeValidator.parseDate("2/12/2015"), "Meeting"));
		expectedCommitments.add(new CommitmentBean(ryanID, 11, 12,
				DateRangeValidator.parseDate("2/26/2015"), "Meeting"));
		assertEquals(commitments, expectedCommitments);
	}

	/**
	 * Tests an excel file with monthly frequency and dates specified.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testImportMonthlyDates() throws Exception {
		testData = new Object[][] {
				{ "Meeting", "Duration", "Start Time", "End Time",
						"Staff Members", "Start Date", "Frequency", "Days",
						"Dates", "Location" },
				{ "Staff Meeting", "120 minutes", "10:00am", "11:00am", "ALL",
						startDate, "Monthly", "Wednesday",
						"2/4, 3/4, 4/1, 5/9", "Room 0207" } };
		action = new ImportClinicianMeetingsAction(conn,
				generateExcelFile(testData));
		action.insertImportedMeetings(DateRangeValidator.parseDate("5/20/2015"));
		List<CommitmentBean> commitments = new ArrayList<CommitmentBean>();
		List<CommitmentBean> tmp = commitmentsDAO.loadCommitments(jeffID);
		for (CommitmentBean commitment : tmp) {
			commitments.add(commitment);
		}
		tmp = commitmentsDAO.loadCommitments(nathanID);
		for (CommitmentBean commitment : tmp) {
			commitments.add(commitment);
		}
		tmp = commitmentsDAO.loadCommitments(ryanID);
		for (CommitmentBean commitment : tmp) {
			commitments.add(commitment);
		}

		List<CommitmentBean> expectedCommitments = new ArrayList<CommitmentBean>();
		expectedCommitments.add(new CommitmentBean(jeffID, 10, 11,
				DateRangeValidator.parseDate("2/4/2015"), "Staff Meeting"));
		expectedCommitments.add(new CommitmentBean(jeffID, 10, 11,
				DateRangeValidator.parseDate("3/4/2015"), "Staff Meeting"));
		expectedCommitments.add(new CommitmentBean(jeffID, 10, 11,
				DateRangeValidator.parseDate("4/1/2015"), "Staff Meeting"));
		expectedCommitments.add(new CommitmentBean(jeffID, 10, 11,
				DateRangeValidator.parseDate("5/9/2015"), "Staff Meeting"));
		expectedCommitments.add(new CommitmentBean(nathanID, 10, 11,
				DateRangeValidator.parseDate("2/4/2015"), "Staff Meeting"));
		expectedCommitments.add(new CommitmentBean(nathanID, 10, 11,
				DateRangeValidator.parseDate("3/4/2015"), "Staff Meeting"));
		expectedCommitments.add(new CommitmentBean(nathanID, 10, 11,
				DateRangeValidator.parseDate("4/1/2015"), "Staff Meeting"));
		expectedCommitments.add(new CommitmentBean(nathanID, 10, 11,
				DateRangeValidator.parseDate("5/9/2015"), "Staff Meeting"));
		expectedCommitments.add(new CommitmentBean(ryanID, 10, 11,
				DateRangeValidator.parseDate("2/4/2015"), "Staff Meeting"));
		expectedCommitments.add(new CommitmentBean(ryanID, 10, 11,
				DateRangeValidator.parseDate("3/4/2015"), "Staff Meeting"));
		expectedCommitments.add(new CommitmentBean(ryanID, 10, 11,
				DateRangeValidator.parseDate("4/1/2015"), "Staff Meeting"));
		expectedCommitments.add(new CommitmentBean(ryanID, 10, 11,
				DateRangeValidator.parseDate("5/9/2015"), "Staff Meeting"));
		assertEquals(commitments, expectedCommitments);
	}

	/**
	 * Tests an excel file with a weekly frequency.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testImportWeekly() throws Exception {
		testData = new Object[][] {
				{ "Meeting", "Duration", "Start Time", "End Time",
						"Staff Members", "Start Date", "Frequency", "Days",
						"Dates", "Location" },
				{ "Other Meeting", "60 minutes", "10:30am", "11:30am",
						"Jeff, [Nathan], Bill", startDate, "Weekly", "Monday",
						null, "Room C" } };
		action = new ImportClinicianMeetingsAction(conn,
				generateExcelFile(testData));
		action.insertImportedMeetings(endDate);
		List<CommitmentBean> commitments = new ArrayList<CommitmentBean>();
		List<CommitmentBean> tmp = commitmentsDAO.loadCommitments(jeffID);
		for (CommitmentBean commitment : tmp) {
			commitments.add(commitment);
		}
		tmp = commitmentsDAO.loadCommitments(nathanID);
		for (CommitmentBean commitment : tmp) {
			commitments.add(commitment);
		}
		List<CommitmentBean> expectedCommitments = new ArrayList<CommitmentBean>();
		
		expectedCommitments.add(new CommitmentBean(jeffID, 10, 12,
				DateRangeValidator.parseDate("1/26/2015"), "Other Meeting"));
		expectedCommitments.add(new CommitmentBean(jeffID, 10, 12,
				DateRangeValidator.parseDate("2/2/2015"), "Other Meeting"));
		expectedCommitments.add(new CommitmentBean(jeffID, 10, 12,
				DateRangeValidator.parseDate("2/9/2015"), "Other Meeting"));
		expectedCommitments.add(new CommitmentBean(jeffID, 10, 12,
				DateRangeValidator.parseDate("2/16/2015"), "Other Meeting"));
		expectedCommitments.add(new CommitmentBean(jeffID, 10, 12,
				DateRangeValidator.parseDate("2/23/2015"), "Other Meeting"));
		expectedCommitments.add(new CommitmentBean(jeffID, 10, 12,
				DateRangeValidator.parseDate("3/2/2015"), "Other Meeting"));
		expectedCommitments.add(new CommitmentBean(jeffID, 10, 12,
				DateRangeValidator.parseDate("3/9/2015"), "Other Meeting"));

		expectedCommitments.add(new CommitmentBean(nathanID, 10, 12,
				DateRangeValidator.parseDate("1/26/2015"), "Other Meeting"));
		expectedCommitments.add(new CommitmentBean(nathanID, 10, 12,
				DateRangeValidator.parseDate("2/2/2015"), "Other Meeting"));
		expectedCommitments.add(new CommitmentBean(nathanID, 10, 12,
				DateRangeValidator.parseDate("2/9/2015"), "Other Meeting"));
		expectedCommitments.add(new CommitmentBean(nathanID, 10, 12,
				DateRangeValidator.parseDate("2/16/2015"), "Other Meeting"));
		expectedCommitments.add(new CommitmentBean(nathanID, 10, 12,
				DateRangeValidator.parseDate("2/23/2015"), "Other Meeting"));
		expectedCommitments.add(new CommitmentBean(nathanID, 10, 12,
				DateRangeValidator.parseDate("3/2/2015"), "Other Meeting"));
		expectedCommitments.add(new CommitmentBean(nathanID, 10, 12,
				DateRangeValidator.parseDate("3/9/2015"), "Other Meeting"));

		assertEquals(commitments, expectedCommitments);
	}

	/**
	 * Tear down. Clears the tables and deletes the file created as
	 * a result of the testing.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@After
	public void tearDown() throws Exception {
		File f = new File("tmpFile.xlsx");
		f.delete();
		gen.clearTables();
	}

	/**
	 * Generate excel file from the given grid of test data provided.
	 *
	 * @param testData
	 *            the test data
	 * @return the file
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static File generateExcelFile(Object[][] testData)
			throws IOException {
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("tmpFile");

		for (int i = 0; i < testData.length; i++) {
			Row row = sheet.createRow(i);
			Object[] data = testData[i];
			int j = 0;
			for (Object cellItem : data) {
				Cell cell = row.createCell(j++);
				if (cellItem instanceof Date) {
					CellStyle cellStyle = workbook.createCellStyle();
					cellStyle.setDataFormat(workbook.createDataFormat()
							.getFormat("d-mmm"));
					cell.setCellValue((Date) cellItem);
					cell.setCellStyle(cellStyle);
				} else {
					cell.setCellValue((String) cellItem);
				}
			}
		}

		FileOutputStream out = new FileOutputStream("tmpFile.xlsx");
		workbook.write(out);
		out.close();
		workbook.close();
		return new File("tmpFile.xlsx");
	}
}
