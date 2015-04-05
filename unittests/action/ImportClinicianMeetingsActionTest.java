package action;

import static org.junit.Assert.assertEquals;
import generator.TestDataGenerator;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
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

public class ImportClinicianMeetingsActionTest {
	
	private CommitmentsDAO commitmentsDAO;
	private ClinicianDAO clinicianDAO;
	private Connection conn;
	private TestDataGenerator gen;
	private ImportClinicianMeetingsAction action;
	private Object[][] testData;
	private Date startDate;
	private Date endDate;
	private int jeffID, nathanID, ryanID;

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
	
	@Test
	public void testImportMonthlyDays() throws Exception {
		testData = new Object[][] {{"Meeting", "Duration", "Start Time", "End Time", "Staff Members", "Start Date", "Frequency", "Days", "Dates", "Location"},
				{"Admin Team Meeting", "90 minutes", "8:30am", "10:00am", "[Jeff], (Ryan), John", startDate, "Monthly", "1st Friday,    3rd Tuesday", null, "Room 212"}};
		action = new ImportClinicianMeetingsAction(conn, generateExcelFile());
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
		expectedCommitments.add(new CommitmentBean(jeffID, 8, 10, DateRangeValidator.parseDate("2/6/2015"), "Admin Team Meeting"));
		expectedCommitments.add(new CommitmentBean(jeffID, 8, 10, DateRangeValidator.parseDate("3/6/2015"), "Admin Team Meeting"));
		expectedCommitments.add(new CommitmentBean(jeffID, 8, 10, DateRangeValidator.parseDate("2/17/2015"), "Admin Team Meeting"));
		expectedCommitments.add(new CommitmentBean(ryanID, 8, 10, DateRangeValidator.parseDate("2/6/2015"), "Admin Team Meeting"));
		expectedCommitments.add(new CommitmentBean(ryanID, 8, 10, DateRangeValidator.parseDate("3/6/2015"), "Admin Team Meeting"));
		expectedCommitments.add(new CommitmentBean(ryanID, 8, 10, DateRangeValidator.parseDate("2/17/2015"), "Admin Team Meeting"));
		assertEquals(commitments, expectedCommitments);
	}
	
	@Test
	public void testImportBiweekly() throws Exception {
		testData = new Object[][] {{"Meeting", "Duration", "Start Time", "End Time", "Staff Members", "Start Date", "Frequency", "Days", "Dates", "Location"},
				{"Meeting", "60 minutes", "11:00am", "12:00pm", "[Nathan], [Ryan]", startDate, "Biweekly", "Thursday", null, "Room 1404"}};
		action = new ImportClinicianMeetingsAction(conn, generateExcelFile());
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
		expectedCommitments.add(new CommitmentBean(nathanID, 11, 12, DateRangeValidator.parseDate("1/29/2015"), "Meeting"));
		expectedCommitments.add(new CommitmentBean(nathanID, 11, 12, DateRangeValidator.parseDate("2/12/2015"), "Meeting"));
		expectedCommitments.add(new CommitmentBean(nathanID, 11, 12, DateRangeValidator.parseDate("2/26/2015"), "Meeting"));
		expectedCommitments.add(new CommitmentBean(ryanID, 11, 12, DateRangeValidator.parseDate("1/29/2015"), "Meeting"));
		expectedCommitments.add(new CommitmentBean(ryanID, 11, 12, DateRangeValidator.parseDate("2/12/2015"), "Meeting"));
		expectedCommitments.add(new CommitmentBean(ryanID, 11, 12, DateRangeValidator.parseDate("2/26/2015"), "Meeting"));
		assertEquals(commitments, expectedCommitments);
	}
	
	@Test
	public void testImportMonthlyDates() throws Exception {
		testData = new Object[][] {{"Meeting", "Duration", "Start Time", "End Time", "Staff Members", "Start Date", "Frequency", "Days", "Dates", "Location"},
				{"Staff Meeting", "120 minutes", "10:00am", "11:00am", "ALL", startDate, "Monthly", "Wednesday", "2/4, 3/4, 4/1, 5/9", "Room 0207"}};
		action = new ImportClinicianMeetingsAction(conn, generateExcelFile());
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
		expectedCommitments.add(new CommitmentBean(jeffID, 10, 11, DateRangeValidator.parseDate("2/4/2015"), "Staff Meeting"));
		expectedCommitments.add(new CommitmentBean(jeffID, 10, 11, DateRangeValidator.parseDate("3/4/2015"), "Staff Meeting"));
		expectedCommitments.add(new CommitmentBean(jeffID, 10, 11, DateRangeValidator.parseDate("4/1/2015"), "Staff Meeting"));
		expectedCommitments.add(new CommitmentBean(jeffID, 10, 11, DateRangeValidator.parseDate("5/9/2015"), "Staff Meeting"));
		expectedCommitments.add(new CommitmentBean(nathanID, 10, 11, DateRangeValidator.parseDate("2/4/2015"), "Staff Meeting"));
		expectedCommitments.add(new CommitmentBean(nathanID, 10, 11, DateRangeValidator.parseDate("3/4/2015"), "Staff Meeting"));
		expectedCommitments.add(new CommitmentBean(nathanID, 10, 11, DateRangeValidator.parseDate("4/1/2015"), "Staff Meeting"));
		expectedCommitments.add(new CommitmentBean(nathanID, 10, 11, DateRangeValidator.parseDate("5/9/2015"), "Staff Meeting"));
		expectedCommitments.add(new CommitmentBean(ryanID, 10, 11, DateRangeValidator.parseDate("2/4/2015"), "Staff Meeting"));
		expectedCommitments.add(new CommitmentBean(ryanID, 10, 11, DateRangeValidator.parseDate("3/4/2015"), "Staff Meeting"));
		expectedCommitments.add(new CommitmentBean(ryanID, 10, 11, DateRangeValidator.parseDate("4/1/2015"), "Staff Meeting"));
		expectedCommitments.add(new CommitmentBean(ryanID, 10, 11, DateRangeValidator.parseDate("5/9/2015"), "Staff Meeting"));
		assertEquals(commitments, expectedCommitments);
	}
	
	@Test
	public void testImportWeekly() throws Exception {
		testData = new Object[][] {{"Meeting", "Duration", "Start Time", "End Time", "Staff Members", "Start Date", "Frequency", "Days", "Dates", "Location"},
				{"Other Meeting", "60 minutes", "10:30am", "11:30am", "Jeff, [Nathan], Bill", startDate, "Weekly", "Monday", null, "Room C"}};
		action = new ImportClinicianMeetingsAction(conn, generateExcelFile());
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
		expectedCommitments.add(new CommitmentBean(jeffID, 10, 12, DateRangeValidator.parseDate("1/26/2015"), "Other Meeting"));
		expectedCommitments.add(new CommitmentBean(jeffID, 10, 12, DateRangeValidator.parseDate("2/2/2015"), "Other Meeting"));
		expectedCommitments.add(new CommitmentBean(jeffID, 10, 12, DateRangeValidator.parseDate("2/9/2015"), "Other Meeting"));
		expectedCommitments.add(new CommitmentBean(jeffID, 10, 12, DateRangeValidator.parseDate("2/16/2015"), "Other Meeting"));
		expectedCommitments.add(new CommitmentBean(jeffID, 10, 12, DateRangeValidator.parseDate("2/23/2015"), "Other Meeting"));
		expectedCommitments.add(new CommitmentBean(jeffID, 10, 12, DateRangeValidator.parseDate("3/2/2015"), "Other Meeting"));
		expectedCommitments.add(new CommitmentBean(jeffID, 10, 12, DateRangeValidator.parseDate("3/9/2015"), "Other Meeting"));
		
		expectedCommitments.add(new CommitmentBean(nathanID, 10, 12, DateRangeValidator.parseDate("1/26/2015"), "Other Meeting"));
		expectedCommitments.add(new CommitmentBean(nathanID, 10, 12, DateRangeValidator.parseDate("2/2/2015"), "Other Meeting"));
		expectedCommitments.add(new CommitmentBean(nathanID, 10, 12, DateRangeValidator.parseDate("2/9/2015"), "Other Meeting"));
		expectedCommitments.add(new CommitmentBean(nathanID, 10, 12, DateRangeValidator.parseDate("2/16/2015"), "Other Meeting"));
		expectedCommitments.add(new CommitmentBean(nathanID, 10, 12, DateRangeValidator.parseDate("2/23/2015"), "Other Meeting"));
		expectedCommitments.add(new CommitmentBean(nathanID, 10, 12, DateRangeValidator.parseDate("3/2/2015"), "Other Meeting"));
		expectedCommitments.add(new CommitmentBean(nathanID, 10, 12, DateRangeValidator.parseDate("3/9/2015"), "Other Meeting"));

		assertEquals(commitments, expectedCommitments);
	}
	
	@After
	public void tearDown() throws Exception {
		File f = new File("tmpFile.xlsx");
		f.delete();
		gen.clearTables();
	}
	
	private File generateExcelFile() throws Exception {
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
					cellStyle.setDataFormat(workbook.createDataFormat().getFormat("d-mmm"));
					cell.setCellValue((Date)cellItem);
					cell.setCellStyle(cellStyle);
				} else {
					cell.setCellValue((String)cellItem);
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
