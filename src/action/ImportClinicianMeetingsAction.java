package action;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import bean.CommitmentBean;
import bean.OperatingHours;
import bean.Weekday;
import dao.ClinicianDAO;
import dao.CommitmentsDAO;

public class ImportClinicianMeetingsAction {
	
	private CommitmentsDAO commitmentsDAO;
	private ClinicianDAO clinicianDAO;
	private Connection conn;
	private File excelFile;
	
	public ImportClinicianMeetingsAction(Connection conn, File excelFile) {
		this.conn = conn;
		this.excelFile = excelFile;
		commitmentsDAO = new CommitmentsDAO(conn);
		clinicianDAO = new ClinicianDAO(conn);
	}

	public List<CommitmentBean> getMeetings() {
		try {
			FileInputStream file = new FileInputStream(excelFile);
			XSSFWorkbook workbook = new XSSFWorkbook(file);
			XSSFSheet sheet = workbook.getSheetAt(0);
			Iterator<Row> rowIterator = sheet.iterator();
			List<String> titles = new ArrayList<String>();
			if (rowIterator.hasNext()) {
				Row titleRow = rowIterator.next();
				Iterator<Cell> cellIterator = titleRow.cellIterator();
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					titles.add(cell.getStringCellValue());
					System.out.println(cell.getStringCellValue());
				}
			} else {
				return null;
			}
			
			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				Iterator<Cell> cellIterator = row.cellIterator();
				int index = 0;
				Map<String, Object> excelRow = new HashMap<String, Object>();
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					if (titles.get(index).equals("Start Date")) {
						excelRow.put(titles.get(index), cell.getDateCellValue());
					} else {
						excelRow.put(titles.get(index), cell.getStringCellValue());
					}
					index++;
				}
				// TODO: get end date
				List<Date> meetingDates = getMeetingDates(excelRow, new Date());
				String startTime = (String)excelRow.get("Start Time");
				int sTime = parseTime(startTime);
				if (sTime == -1) {
					handleInvalidExcelCell(startTime, "Start Time");
				}
				
				String endTime = (String)excelRow.get("End Time");
				int eTime = parseTime(endTime);
				if (eTime == -1) {
					handleInvalidExcelCell(endTime, "End Time");
				}
				
				String meetingName = (String)excelRow.get("Meeting");
				if (meetingName == null || meetingName.isEmpty()) {
					handleInvalidExcelCell(meetingName, "Meeting");
				}
				
				String staffMembers = (String)excelRow.get("Staff Members");
				if (staffMembers == null || staffMembers.isEmpty()) {
					handleInvalidExcelCell(staffMembers, "Staff Members");
				}
				
				String [] staff = staffMembers.split(", ");
				for (Date meetingDate : meetingDates) {
					System.out.println(meetingDate);
					for (String staffMember : staff) {
						int clinicianID = clinicianDAO.getClinicianID(staffMember);
						if (clinicianID != -1) {
							// add this to database
						}
					}
				}
				System.out.println("");
			}
			file.close();
		} catch (Exception e){}
		
		return null;
	}
	
	private int parseTime(String timeString) {
		if (timeString == null) {
			return -1;
		}
		int colonIndex = timeString.indexOf(":");
		if (colonIndex != -1 && timeString.length() > colonIndex + 3) {
			timeString = timeString.substring(0, colonIndex) + ":00 " + timeString.substring(colonIndex + 3);
		}
		int time = OperatingHours.toInt(timeString);
		return time;
	}
	
	private void handleInvalidExcelCell(String cellData, String cellTitle) {
		// do something
		System.out.println("Invalid input: " + cellData + " for title " + cellTitle);
	}
	
	private List<Date> getMeetingDatesWeekly(Date startDate, Date endDate, int daysBetweenMeetings, String day) {
		// for start date to end date
		List<Date> dates = new ArrayList<Date>();
		Calendar calStart = Calendar.getInstance();
		calStart.setTime(startDate);
		Calendar calEnd = Calendar.getInstance();
		calEnd.setTime(endDate);
		
		int dayOfWeek = Weekday.valueOf(day).ordinal() + 1;
		calStart.set(Calendar.DAY_OF_WEEK, dayOfWeek);
		
		while (!calStart.after(calEnd)) {
		    Date currentDate = calStart.getTime();
		    dates.add(currentDate);
		    calStart.add(Calendar.DATE, daysBetweenMeetings);
		}
		return dates;
	}
	
	private List<Date> getMeetingDatesMonthly(Date startDate, Date endDate, int dayOfMonth, String day) {
		List<Date> dates = new ArrayList<Date>();
		Calendar calStart = Calendar.getInstance();
		calStart.setTime(startDate);
		Calendar calEnd = Calendar.getInstance();
		calEnd.setTime(endDate);
		
		int dayOfWeek = Weekday.valueOf(day).ordinal() + 1;
		calStart.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().getActualMinimum(Calendar.DAY_OF_MONTH));
		calStart.set(Calendar.DAY_OF_WEEK, dayOfWeek);
		calStart.add(Calendar.WEEK_OF_MONTH, dayOfMonth - 1);
		
		while (!calStart.after(calEnd)) {
			 Date currentDate = calStart.getTime();
			 dates.add(currentDate);
			 calStart.add(Calendar.MONTH, 1);
			 calStart.set(Calendar.DATE, calStart.getActualMinimum(Calendar.DAY_OF_MONTH));
			 calStart.set(Calendar.DAY_OF_WEEK, dayOfWeek);
			 calStart.add(Calendar.WEEK_OF_MONTH, dayOfMonth - 1);
		}

		return dates;
	}
	
	private List<Date> getMeetingDates(Map<String, Object> excelRow, Date endDate) {
		
		List<Date> meetingDates = new ArrayList<Date>();
		
		// Check if dates specified for us...
		String dates = (String)excelRow.get("Dates");
		if (dates != null && !dates.isEmpty()) {
			// ensure all specified dates are valid
			SimpleDateFormat formatter = new SimpleDateFormat("MM/dd");
			String []  datesSpecified = dates.split(", ");
			for (String dateSpecified : datesSpecified) {
				try {
					meetingDates.add(formatter.parse(dateSpecified));
				} catch (ParseException e) {
					// error
				}
			}
			return meetingDates;
		}
		
		Date startDate = (Date)excelRow.get("Start Date");
		if (startDate == null) {
			handleInvalidExcelCell(startDate.toString(), "Start Date");
		}
		
		String frequency = (String)excelRow.get("Frequency");
		if (frequency == null || frequency.isEmpty()) {
			handleInvalidExcelCell(frequency, "Frequency");
		}
		if (!(frequency.equals("Weekly") || frequency.equals("Biweekly") || frequency.equals("Monthly"))) {
			handleInvalidExcelCell(frequency, "Frequency");
		}
		
		int daysBetweenMeetings = -1;
		if (frequency.equals("Weekly")) {
			daysBetweenMeetings = 7;
		} else if (frequency.equals("Biweekly")) {
			daysBetweenMeetings = 14;
		}
		
		String days = (String)excelRow.get("Days");
		if (days == null || days.isEmpty()) {
			handleInvalidExcelCell(days, "Days");
		}
		String[] daysSpecified = days.split(", ");
		
		for (String daySpecified : daysSpecified) {
			if (!(daySpecified.contains(Weekday.Monday.name()) || daySpecified.contains(Weekday.Tuesday.name()) || daySpecified.contains(Weekday.Wednesday.name()) || 
					daySpecified.contains(Weekday.Thursday.name()) || daySpecified.contains(Weekday.Friday.name()))) {
				handleInvalidExcelCell(daySpecified, "Days");
			}
			if (!(daySpecified.equals(Weekday.Monday.name()) || daySpecified.equals(Weekday.Tuesday.name()) || daySpecified.equals(Weekday.Wednesday.name()) || 
					daySpecified.equals(Weekday.Thursday.name()) || daySpecified.equals(Weekday.Friday.name()))) {
				
				// must be of the format {1st|2nd|3rd|4th} Weekday
				int firstIndex = daySpecified.indexOf("1st ");
				int secondIndex = daySpecified.indexOf("2nd ");
				int thirdIndex = daySpecified.indexOf("3rd ");
				int fourthIndex = daySpecified.indexOf("4th ");
				if (firstIndex != -1 && (secondIndex != -1 || thirdIndex != -1 || fourthIndex != -1)) {
					handleInvalidExcelCell(daySpecified, "Days");
				} else if (secondIndex != -1 && (firstIndex != -1 || thirdIndex != -1 || fourthIndex != -1)) {
					handleInvalidExcelCell(daySpecified, "Days");
				} else if (thirdIndex != -1 && (firstIndex != -1 || secondIndex != -1 || fourthIndex != -1)) {
					handleInvalidExcelCell(daySpecified, "Days");
				} else if (fourthIndex != -1 && (firstIndex != -1 || secondIndex != -1 || thirdIndex != -1)) {
					handleInvalidExcelCell(daySpecified, "Days");
				} else if (firstIndex == -1 && secondIndex == -1 && thirdIndex == -1 && fourthIndex == -1) {
					handleInvalidExcelCell(daySpecified, "Days");
				} else {
					// only one is set
					if (daySpecified.length() <= 4) {
						handleInvalidExcelCell(daySpecified, "Days");
					} else {
						String day = daySpecified.substring(4);
						if (!(day.equals(Weekday.Monday.name()) || day.equals(Weekday.Tuesday.name()) || day.equals(Weekday.Wednesday.name()) || 
								day.equals(Weekday.Thursday.name()) || day.equals(Weekday.Friday.name()))) {
							handleInvalidExcelCell(daySpecified, "Days");
						} else {
							if (!frequency.equals("Monthly")) {
								handleInvalidExcelCell(frequency, "Frequency");
								handleInvalidExcelCell(daySpecified, "Days");
							}
							int dayOfMonth = -1;
							if (firstIndex != -1) {
								dayOfMonth = 1;
							} else if (secondIndex != -1) {
								dayOfMonth = 2;
							} else if (thirdIndex != -1) {
								dayOfMonth = 3;
							} else if (fourthIndex != -1) {
								dayOfMonth = 4;
							}
							List<Date> monthlyDates = getMeetingDatesMonthly(startDate, endDate, dayOfMonth, day);
							for (Date date : monthlyDates) {
								meetingDates.add(date);
							}
							System.out.println(daySpecified);
						}
					}
				}
				
			} else {
				// just Weekday
				if (frequency.equals("Monthly")) {
					handleInvalidExcelCell(frequency, "Frequency");
					handleInvalidExcelCell(daySpecified, "Days");
				}
				List<Date> weeklyDates = getMeetingDatesWeekly(startDate, endDate, daysBetweenMeetings, daySpecified);
				for (Date date : weeklyDates) {
					meetingDates.add(date);
				}
				System.out.println(daySpecified);
			}
		}
		
		return meetingDates;
	}
}
