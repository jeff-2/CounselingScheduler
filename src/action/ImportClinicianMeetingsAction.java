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

import javax.swing.JOptionPane;
import javax.swing.JPanel;

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
	
	/** The commitments dao. */
	private CommitmentsDAO commitmentsDAO;
	
	/** The clinician dao. */
	private ClinicianDAO clinicianDAO;
	
	/** The conn. */
	private Connection conn;
	
	/** The excel file. */
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
	 */
	public void insertImportedMeetings(Date endDate) {
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
				return;
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
					handleInvalidExcelCell(startTime, "Start Time");
					workbook.close();
					return;
				}
				String endTime = (String)excelRow.get("End Time");
				int eTime = parseTime(endTime, true);
				if (eTime == -1) {
					handleInvalidExcelCell(endTime, "End Time");
					workbook.close();
					return;
				}
				String meetingName = (String)excelRow.get("Meeting");
				if (meetingName == null || meetingName.isEmpty()) {
					handleInvalidExcelCell(meetingName, "Meeting");
					workbook.close();
					return;
				}
				String staffMembers = (String)excelRow.get("Staff Members");
				if (staffMembers == null || staffMembers.isEmpty()) {
					handleInvalidExcelCell(staffMembers, "Staff Members");
					workbook.close();
					return;
				}
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
					String [] staff = staffMembers.split(", ");
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
		} catch (Exception e){
			
		}
	}
	
	/**
	 * Parses the time in the specified string, rounding up to the nearest hour if specified.
	 *
	 * @param timeString the time string
	 * @param roundUp the round up
	 * @return the int
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
				if (t == 12) {
					timeString = timeString.replaceAll("a", "p");
				}
			}
		}
		int time = OperatingHours.toInt(timeString);
		return time;
	}
	
	/**
	 * Handle invalid excel cell by providing error dialog.
	 *
	 * @param cellData the cell data
	 * @param cellTitle the cell title
	 */
	private void handleInvalidExcelCell(String cellData, String cellTitle) {
		JOptionPane.showMessageDialog(new JPanel(),
				"Data under column " + cellTitle + " is of an invalid format:" + cellData,
				"Invalid Format",
				JOptionPane.ERROR_MESSAGE);
	}
	
	/**
	 * Gets the meeting dates when they are scheduled weekly (or biweekly).
	 *
	 * @param startDate the start date
	 * @param endDate the end date
	 * @param daysBetweenMeetings the days between meetings
	 * @param day the day
	 * @return the meeting dates weekly
	 */
	private List<Date> getMeetingDatesWeekly(Date startDate, Date endDate, int daysBetweenMeetings, String day) {
		// for start date to end date
		List<Date> dates = new ArrayList<Date>();
		Calendar calStart = Calendar.getInstance();
		calStart.setTime(startDate);
		Calendar calEnd = Calendar.getInstance();
		calEnd.setTime(endDate);
		
		int dayOfWeek = Weekday.valueOf(day).ordinal() + 2;
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
	 * @param dayOfMonth the day of month
	 * @param day the day
	 * @return the meeting dates monthly
	 */
	private List<Date> getMeetingDatesMonthly(Date startDate, Date endDate, int dayOfMonth, String day) {
		List<Date> dates = new ArrayList<Date>();
		Calendar calStart = Calendar.getInstance();
		calStart.setTime(startDate);
		Calendar calEnd = Calendar.getInstance();
		calEnd.setTime(endDate);
		
		int dayOfWeek = Weekday.valueOf(day).ordinal() + 2;
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
	 * Gets the meeting dates from a particular meeting specified in a row of the excel document.
	 *
	 * @param excelRow the excel row
	 * @param endDate the end date
	 * @return the meeting dates
	 */
	private List<Date> getMeetingDates(Map<String, Object> excelRow, Date endDate) {
		
		List<Date> meetingDates = new ArrayList<Date>();
		
		Date startDate = (Date)excelRow.get("Start Date");
		if (startDate == null) {
			handleInvalidExcelCell(null, "Start Date");
			return null;
		}
		
		// Check if dates specified for us...
		String dates = (String)excelRow.get("Dates");
		if (dates != null && !dates.isEmpty()) {
			// ensure all specified dates are valid
			SimpleDateFormat formatter = new SimpleDateFormat("MM/dd");
			String []  datesSpecified = dates.split(", ");
			for (String dateSpecified : datesSpecified) {
				try {
					Calendar cal = Calendar.getInstance();
					Calendar endCal = Calendar.getInstance();
					endCal.setTime(endDate);
					cal.setTime(formatter.parse(dateSpecified));
					cal.set(Calendar.YEAR, endCal.get(Calendar.YEAR));
					if (cal.getTime().after(endDate) || cal.getTime().before(startDate)) {
						handleInvalidExcelCell(dates, "Dates");
					} else {
						meetingDates.add(cal.getTime());
					}
				} catch (ParseException e) {
					handleInvalidExcelCell(dates, "Dates");
					return null;
				}
			}
			return meetingDates;
		}
		
		String frequency = (String)excelRow.get("Frequency");
		if (frequency == null || frequency.isEmpty()) {
			handleInvalidExcelCell(frequency, "Frequency");
			return null;
		}
		if (!(frequency.equals("Weekly") || frequency.equals("Biweekly") || frequency.equals("Monthly"))) {
			handleInvalidExcelCell(frequency, "Frequency");
			return null;
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
			return null;
		}
		String[] daysSpecified = days.split(", ");
		
		for (String daySpecified : daysSpecified) {
			if (!(daySpecified.contains(Weekday.Monday.name()) || daySpecified.contains(Weekday.Tuesday.name()) || daySpecified.contains(Weekday.Wednesday.name()) || 
					daySpecified.contains(Weekday.Thursday.name()) || daySpecified.contains(Weekday.Friday.name()))) {
				handleInvalidExcelCell(daySpecified, "Days");
				return null;
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
					return null;
				} else if (secondIndex != -1 && (firstIndex != -1 || thirdIndex != -1 || fourthIndex != -1)) {
					handleInvalidExcelCell(daySpecified, "Days");
					return null;
				} else if (thirdIndex != -1 && (firstIndex != -1 || secondIndex != -1 || fourthIndex != -1)) {
					handleInvalidExcelCell(daySpecified, "Days");
					return null;
				} else if (fourthIndex != -1 && (firstIndex != -1 || secondIndex != -1 || thirdIndex != -1)) {
					handleInvalidExcelCell(daySpecified, "Days");
					return null;
				} else if (firstIndex == -1 && secondIndex == -1 && thirdIndex == -1 && fourthIndex == -1) {
					handleInvalidExcelCell(daySpecified, "Days");
					return null;
				} else {
					// only one is set
					if (daySpecified.length() <= 4) {
						handleInvalidExcelCell(daySpecified, "Days");
						return null;
					} else {
						String day = daySpecified.substring(4);
						if (!(day.equals(Weekday.Monday.name()) || day.equals(Weekday.Tuesday.name()) || day.equals(Weekday.Wednesday.name()) || 
								day.equals(Weekday.Thursday.name()) || day.equals(Weekday.Friday.name()))) {
							handleInvalidExcelCell(daySpecified, "Days");
							return null;
						} else {
							if (!frequency.equals("Monthly")) {
								handleInvalidExcelCell(frequency + " and " + daySpecified, "Frequency and Days");
								return null;
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
						}
					}
				}
				
			} else {
				if (frequency.equals("Monthly")) {
					handleInvalidExcelCell(frequency + " and " + daySpecified, "Frequency and Days");
					return null;
				}
				List<Date> weeklyDates = getMeetingDatesWeekly(startDate, endDate, daysBetweenMeetings, daySpecified);
				for (Date date : weeklyDates) {
					meetingDates.add(date);
				}
			}
		}
		
		return meetingDates;
	}
}
