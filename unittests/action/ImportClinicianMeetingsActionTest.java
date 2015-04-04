package action;

import generator.TestDataGenerator;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bean.CommitmentBean;
import dao.ConnectionFactory;

public class ImportClinicianMeetingsActionTest {
	
	private Connection conn;
	private TestDataGenerator gen;
	private ImportClinicianMeetingsAction action;
	private Object[][] testData = {{"Meeting", "Duration", "Start Time", "End Time", "Staff Members", "Start Date", "Frequency", "Days", "Dates", "Location"},
			{"Admin Team Meeting", "90 minutes", "8:30am", "10:00am", "[Arnold], (Weathersby), Zhang", new Date(1421971200000l), "Monthly", "1st Friday, 3rd Tuesday", null, "Room 212"}};
	
	@Before
	public void setUp() throws Exception {
		conn = ConnectionFactory.getInstance();
		gen = new TestDataGenerator(conn);
		gen.clearTables();
		action = new ImportClinicianMeetingsAction(conn, generateExcelFile());
	}
	
	@Test
	public void testGetMeetings() {
		List<CommitmentBean> meetings = action.getMeetings();
	}
	
	@After
	public void tearDown() throws Exception {
		gen.clearTables();
	}
	
	private File generateExcelFile() {
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("tmpFile");
		
		CreationHelper createHelper = workbook.getCreationHelper();
		
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
		
		try {
			FileOutputStream out = new FileOutputStream("tmpFile.xlsx");
			workbook.write(out);
			out.close();
		} catch (Exception e) {}
		return new File("tmpFile.xlsx");
	}
}
