package action;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
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

import bean.ClinicianBean;
import bean.CommitmentBean;
import bean.OperatingHours;
import bean.Weekday;
import dao.ClinicianDAO;
import dao.CommitmentsDAO;

/**
 * The Class ImportClinicianMeetingsAction handles the importing of a meeting schedule from an excel file.
 * 
 * @author jmfoste2, nbeltr2
 */
public class ImportClinicianMeetingsAction {
	
	private CommitmentsDAO commitmentsDAO;
	private ClinicianDAO clinicianDAO;
	private Connection conn;
	private File excelFile;
	
	/**
	 * Instantiates a new import clinician meetings action.
	 *
	 * @param conn the conn
	 * @param excelFile the excel file
	 */
	public ImportClinicianMeetingsAction(Connection conn, File excelFile) {
		this.conn = conn;
		this.excelFile = excelFile;
		commitmentsDAO = new CommitmentsDAO(conn);
		clinicianDAO = new ClinicianDAO(conn);
	}

	/**
	 * Insert imported meetings into the commitments table for each clinician specified for a meeting.
	 *
	 * @param endDate the end date
	 * @throws SQLException the SQL exception
	 * @throws InvalidExcelFormatException the invalid excel format exception
	 */
	public void insertImportedMeetings(Date endDate) throws SQLException, InvalidExcelFormatException {
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
				}
			} else {
				workbook.close();
				throw new InvalidExcelFormatException("Excel document contains no data");
			}
			for (int i = sheet.getFirstRowNum() + 1; i <= sheet.getLastRowNum(); i++) {
				Map<String, Object> excelRow = new HashMap<String, Object>();
				Row r = sheet.getRow(i);
				for (int j = 0; j < r.getLastCellNum(); j++) {
					Cell cell = r.getCell(j, Row.RETURN_NULL_AND_BLANK);
					if (cell == null) {
						excelRow.put(titles.get(j), new String(""));
					} else if (titles.get(j).equals("Start Date")) {
						excelRow.put(titles.get(j), cell.getDateCellValue());
					} else {
						excelRow.put(titles.get(j), cell.getStringCellValue());
					}
				}
				List<Date> meetingDates = getMeetingDates(excelRow, endDate);
				String startTime = (String)excelRow.get("Start Time");
				int sTime = parseTime(startTime, false);
				if (sTime == -1) {
					workbook.close();
					throw new InvalidExcelFormatException(formatInvalidExcelFormatString(startTime, "Start Time"));
				}
				String endTime = (String)excelRow.get("End Time");
				int eTime = parseTime(endTime, true);
				if (eTime == -1) {
					workbook.close();
					throw new InvalidExcelFormatException(formatInvalidExcelFormatString(endTime, "End Time"));
				}
				String meetingName = (String)excelRow.get("Meeting");
				if (meetingName == null || meetingName.isEmpty()) {
					workbook.close();
					throw new InvalidExcelFormatException(formatInvalidExcelFormatString(meetingName, "Meeting"));
				}
				String staffMembers = (String)excelRow.get("Staff Members");
				if (staffMembers == null || staffMembers.isEmpty()) {
					workbook.close();
					throw new InvalidExcelFormatException(formatInvalidExcelFormatString(staffMembers, "Staff Members"));
				}
				staffMembers = staffMembers.replaceAll(" ", "");
				if (staffMembers.equalsIgnoreCase("ALL")) {
					List<ClinicianBean> clinicians = clinicianDAO.loadClinicians();
					for (Date meetingDate : meetingDates) {
						for (ClinicianBean clinician: clinicians) {
							int clinicianID = clinician.getClinicianID();
							CommitmentBean commitment = new CommitmentBean(clinicianID, sTime, eTime, meetingDate, meetingName);
							commitmentsDAO.insert(commitment);
						}
					}
				} else {
					staffMembers = staffMembers.replaceAll("[\\[()\\]]", "");
					String [] staff = staffMembers.split(",");
					for (Date meetingDate : meetingDates) {
						for (String staffMember : staff) {
							int clinicianID = clinicianDAO.getClinicianID(staffMember);
							if (clinicianID != -1) {
								CommitmentBean commitment = new CommitmentBean(clinicianID, sTime, eTime, meetingDate, meetingName);
								commitmentsDAO.insert(commitment);
							}
						}
					}
				}
			}
			workbook.close();
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Parses the time in the specified string, rounding up to the nearest hour if specified.
	 *
	 * @param timeString the time string to parse
	 * @param roundUp specify whether it should the round up to the next hour
	 * @return the 24 hour integer representation of this time
	 */
	private int parseTime(String timeString, boolean roundUp) {
		if (timeString == null) {
			return -1;
		}
		int colonIndex = timeString.indexOf(":");
		if (colonIndex != -1 && timeString.length() > colonIndex + 3) {
			if (!roundUp || timeString.contains(":00")) {
				timeString = timeString.substring(0, colonIndex) + ":00 " + timeString.substring(colonIndex + 3);
			} else {
				int t = (Integer.parseInt(timeString.substring(0, colonIndex)) + 1);
				timeString = t + ":00 " + timeString.substring(colonIndex + 3);
				// if rounded up and switched from am to pm
				if (t == 12) {
					timeString = timeString.replaceAll("a", "p");
				}
			}
		}
		return OperatingHours.toInt(timeString);
	}
	
	/**
	 * Create formatted string to display error formatting of particular cell.
	 *
	 * @param cellData the cell data
	 * @param cellTitle the cell title
	 * @return the string
	 */
	private String formatInvalidExcelFormatString(String cellData, String cellTitle) {
		if (cellData == null || cellData.isEmpty()) {
			return "Data under column '" + cellTitle + "' is of an invalid format 'Entry was not specified'";
		} else {
			return "Data under column '" + cellTitle + "' is of an invalid format '" + cellData + "'";
		}
	}
	
	/**
	 * Gets the meeting dates when they are scheduled weekly (or biweekly) on a particular day of the week.
	 *
	 * @param startDate the start date
	 * @param endDate the end date
	 * @param daysBetweenMeetings the days between meetings
	 * @param day the day of the week
	 * @return the meeting dates at the specified frequency from start date to end date on the day specified
	 */
	private List<Date> getMeetingDatesWeekly(Date startDate, Date endDate, int daysBetweenMeetings, String day) {
		List<Date> dates = new ArrayList<Date>();
		Calendar calStart = Calendar.getInstance();
		calStart.setTime(startDate);
		Calendar calEnd = Calendar.getInstance();
		calEnd.setTime(endDate);
		
		int dayOfWeek = Weekday.valueOf(day).toCalendarWeekday();
		calStart.set(Calendar.DAY_OF_WEEK, dayOfWeek);
		
		while (!calStart.after(calEnd)) {
		    Date currentDate = calStart.getTime();
		    dates.add(currentDate);
		    calStart.add(Calendar.DATE, daysBetweenMeetings);
		}
		return dates;
	}
	
	/**
	 * Gets the meeting dates when they are scheduled monthly (e.g. 1st tuesday of month).
	 *
	 * @param startDate the start date
	 * @param endDate the end date
	 * @param dayOfMonth the day of month (e.g. 2nd tuesday)
	 * @param day the day of the week
	 * @return the meeting dates monthly for the specified day of the week
	 */
	private List<Date> getMeetingDatesMonthly(Date startDate, Date endDate, int dayOfMonth, String day) {
		List<Date> dates = new ArrayList<Date>();
		Calendar calStart = Calendar.getInstance();
		calStart.setTime(startDate);
		Calendar calEnd = Calendar.getInstance();
		calEnd.setTime(endDate);
		
		int dayOfWeek = Weekday.valueOf(day).toCalendarWeekday();
		calStart.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().getActualMinimum(Calendar.DAY_OF_MONTH));
		if (calStart.getTime().before(startDate)) {
			calStart.add(Calendar.MONTH, 1);
		}
		Date prev = calStart.getTime();
		calStart.set(Calendar.DAY_OF_WEEK, dayOfWeek);
		if (calStart.getTime().before(prev)) {
			calStart.add(Calendar.DATE, 7);
		}
		calStart.add(Calendar.WEEK_OF_MONTH, dayOfMonth - 1);
		
		while (!calStart.after(calEnd)) {
			 Date currentDate = calStart.getTime();
			 dates.add(currentDate);
			 calStart.add(Calendar.MONTH, 1);
			 calStart.set(Calendar.DATE, calStart.getActualMinimum(Calendar.DAY_OF_MONTH));
			 prev = calStart.getTime();
			 calStart.set(Calendar.DAY_OF_WEEK, dayOfWeek);
			 if (calStart.getTime().before(prev)) {
				 calStart.add(Calendar.DATE, 7);
			 }
			 calStart.add(Calendar.WEEK_OF_MONTH, dayOfMonth - 1);
		}

		return dates;
	}
	
	/**
	 * Parses the specified meeting dates which have been specified explicitly in the excel document.
	 *
	 * @param startDate the start date
	 * @param endDate the end date
	 * @param dates the dates
	 * @return the list
	 * @throws InvalidExcelFormatException the invalid excel format exception
	 */
	private List<Date> parseSpecifiedMeetingDates(Date startDate, Date endDate, String dates) throws InvalidExcelFormatException {
		
		List<Date> meetingDates = new ArrayList<Date>();
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd");
		dates = dates.replaceAll(" ", "");
		String []  datesSpecified = dates.split(",");
		for (String dateSpecified : datesSpecified) {
			try {
				Calendar cal = Calendar.getInstance();
				Calendar endCal = Calendar.getInstance();
				endCal.setTime(endDate);
				cal.setTime(formatter.parse(dateSpecified));
				cal.set(Calendar.YEAR, endCal.get(Calendar.YEAR));
				if (cal.getTime().after(endDate) || cal.getTime().before(startDate)) {
					throw new InvalidExcelFormatException(formatInvalidExcelFormatString(dates, "Dates"));
				} else {
					meetingDates.add(cal.getTime());
				}
			} catch (ParseException e) {
				throw new InvalidExcelFormatException(formatInvalidExcelFormatString(dates, "Dates"));
			}
		}
		return meetingDates;
	}
	
	/**
	 * Parses the monthly specifier (e.g. 1st Tuesday) and returns it as an integer in the range [1,4].
	 *
	 * @param daySpecified the day specified
	 * @return the int
	 * @throws InvalidExcelFormatException the invalid excel format exception
	 */
	private int parseMonthlySpecifier(String daySpecified) throws InvalidExcelFormatException {
		// must be of the format {1st|2nd|3rd|4th} Weekday
		int firstIndex = daySpecified.indexOf("1st");
		int secondIndex = daySpecified.indexOf("2nd");
		int thirdIndex = daySpecified.indexOf("3rd");
		int fourthIndex = daySpecified.indexOf("4th");
		if (firstIndex != -1 && (secondIndex != -1 || thirdIndex != -1 || fourthIndex != -1)) {
			throw new InvalidExcelFormatException(formatInvalidExcelFormatString(daySpecified, "Days"));
		} else if (secondIndex != -1 && (firstIndex != -1 || thirdIndex != -1 || fourthIndex != -1)) {
			throw new InvalidExcelFormatException(formatInvalidExcelFormatString(daySpecified, "Days"));
		} else if (thirdIndex != -1 && (firstIndex != -1 || secondIndex != -1 || fourthIndex != -1)) {
			throw new InvalidExcelFormatException(formatInvalidExcelFormatString(daySpecified, "Days"));
		} else if (fourthIndex != -1 && (firstIndex != -1 || secondIndex != -1 || thirdIndex != -1)) {
			throw new InvalidExcelFormatException(formatInvalidExcelFormatString(daySpecified, "Days"));
		} else if (firstIndex == -1 && secondIndex == -1 && thirdIndex == -1 && fourthIndex == -1) {
			throw new InvalidExcelFormatException(formatInvalidExcelFormatString(daySpecified, "Days"));
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
		return dayOfMonth;
	}
	
	
	/**
	 * Parses the days specified in a particular excel row.
	 *
	 * @param startDate the start date
	 * @param endDate the end date
	 * @param days the days
	 * @param frequency the frequency
	 * @return the list of dates specified by the excel row
	 * @throws InvalidExcelFormatException the invalid excel format exception
	 */
	private List<Date> parseDays(Date startDate, Date endDate, String days, String frequency) throws InvalidExcelFormatException {
		
		int daysBetweenMeetings = -1;
		if (frequency.equals("Weekly")) {
			daysBetweenMeetings = 7;
		} else if (frequency.equals("Biweekly")) {
			daysBetweenMeetings = 14;
		}
		List<Date> meetingDates = new ArrayList<Date>();
		days = days.replaceAll(" ", "");
		String[] daysSpecified = days.split(",");
		
		for (String daySpecified : daysSpecified) {
			if (!(daySpecified.contains(Weekday.Monday.name()) || daySpecified.contains(Weekday.Tuesday.name()) || daySpecified.contains(Weekday.Wednesday.name()) || 
					daySpecified.contains(Weekday.Thursday.name()) || daySpecified.contains(Weekday.Friday.name()))) {
				throw new InvalidExcelFormatException(formatInvalidExcelFormatString(daySpecified, "Days"));
			}
			if (!(daySpecified.equals(Weekday.Monday.name()) || daySpecified.equals(Weekday.Tuesday.name()) || daySpecified.equals(Weekday.Wednesday.name()) || 
					daySpecified.equals(Weekday.Thursday.name()) || daySpecified.equals(Weekday.Friday.name()))) {
				
				int dayOfMonth = parseMonthlySpecifier(daySpecified);
				
				// only one is set
				if (daySpecified.length() <= 3) {
					throw new InvalidExcelFormatException(formatInvalidExcelFormatString(daySpecified, "Days"));
				} else {
					String day = daySpecified.substring(3);
					if (!(day.equals(Weekday.Monday.name()) || day.equals(Weekday.Tuesday.name()) || day.equals(Weekday.Wednesday.name()) || 
							day.equals(Weekday.Thursday.name()) || day.equals(Weekday.Friday.name()))) {
						throw new InvalidExcelFormatException(formatInvalidExcelFormatString(daySpecified, "Days"));
					} else {
						if (!frequency.equals("Monthly")) {
							throw new InvalidExcelFormatException(formatInvalidExcelFormatString(frequency + " and " + daySpecified, "Frequency and Days"));
						}
						List<Date> monthlyDates = getMeetingDatesMonthly(startDate, endDate, dayOfMonth, day);
						for (Date date : monthlyDates) {
							meetingDates.add(date);
						}
					}
				}
			} else {
				if (frequency.equals("Monthly")) {
					throw new InvalidExcelFormatException(formatInvalidExcelFormatString(frequency + " and " + daySpecified, "Frequency and Days"));
				}
				List<Date> weeklyDates = getMeetingDatesWeekly(startDate, endDate, daysBetweenMeetings, daySpecified);
				for (Date date : weeklyDates) {
					meetingDates.add(date);
				}
			}
		}
		
		return meetingDates;
	}
	
	/**
	 * Gets the meeting dates from a particular meeting specified in a row of the excel document.
	 *
	 * @param excelRow the excel row
	 * @param endDate the end date
	 * @return the meeting dates
	 * @throws InvalidExcelFormatException the invalid excel format exception
	 */
	private List<Date> getMeetingDates(Map<String, Object> excelRow, Date endDate) throws InvalidExcelFormatException {
		
		Date startDate = (Date)excelRow.get("Start Date");
		if (startDate == null) {
			throw new InvalidExcelFormatException(formatInvalidExcelFormatString(null, "Start Date"));
		}
		
		String dates = (String)excelRow.get("Dates");
		if (dates != null && !dates.isEmpty()) {
			return parseSpecifiedMeetingDates(startDate, endDate, dates);
		}
		
		String frequency = (String)excelRow.get("Frequency");
		if (frequency == null || frequency.isEmpty() || !(frequency.equals("Weekly") || frequency.equals("Biweekly") || frequency.equals("Monthly"))) {
			throw new InvalidExcelFormatException(formatInvalidExcelFormatString(frequency, "Frequency"));
		}
		
		String days = (String)excelRow.get("Days");
		if (days == null || days.isEmpty()) {
			throw new InvalidExcelFormatException(formatInvalidExcelFormatString(days, "Days"));
		}
		return parseDays(startDate, endDate, days, frequency);
	}
}
