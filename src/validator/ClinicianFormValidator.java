package validator;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import action.ImportClinicianMeetingsAction;
import action.InvalidFormDataException;
import bean.CalendarBean;
import bean.ClinicianPreferencesBean;
import bean.CommitmentBean;
import bean.DateRange;
import bean.TimeAwayBean;
import bean.Utility;
import dao.CalendarDAO;
import dao.ClinicianDAO;
import dao.ConnectionFactory;

/**
 * The Class ClinicianFormValidator handles the validation of the ClinicianForm input.
 */
public class ClinicianFormValidator {
	
	/**
	 * Validates the input for a given time away.
	 *
	 * @param name the time away description
	 * @param startDate the time away start date
	 * @param endDate the time away end date
	 * @return the time away bean
	 * @throws InvalidFormDataException the invalid form data exception
	 */
	public static TimeAwayBean validateTimeAway(String name, String startDate, String endDate) throws InvalidFormDataException {
		if (name.isEmpty()) {
			throw new InvalidFormDataException("You must enter in a description", "Adding invalid time away description");
		}
		
		DateRange dateRange;
		try {
			dateRange = DateRangeValidator.validate(startDate, endDate);
		} catch (InvalidDateRangeException e) {
			throw new InvalidFormDataException("Cannot add the time away to the list. " + e.getMessage(), "Adding invalid time away");
		}
		return new TimeAwayBean(-1, name, dateRange.getStartDate(), dateRange.getEndDate());
	}
	
	/**
	 * Validates the input for a given commitment.
	 *
	 * @param dateRange the date range for the semester start/end dates
	 * @param description the description of the commitment
	 * @param startTime the start time of the commitment
	 * @param endTime the end time of the commitment
	 * @param dayOfWeek the day of week of the commitment
	 * @param frequency the frequency of the commitment
	 * @param isExternal whether the commitment is external and requires travel
	 * @return the list of commitments generated from the given input
	 * @throws InvalidFormDataException the invalid form data exception
	 */
	public static List<CommitmentBean> validateCommitment(DateRange dateRange, String description, String startTime, String endTime, String dayOfWeek, String frequency, boolean isExternal) throws InvalidFormDataException {
		if (description.isEmpty()) {
			throw new InvalidFormDataException("You must enter in a description", "Adding invalid commitment description");
		}
		
		String sTime = startTime.replaceAll(" ", "");
		String eTime = endTime.replaceAll(" ", "");
		
		int startHour = Utility.parseTime(sTime, false);
		int endHour = Utility.parseTime(eTime, true);
		if (startHour >= endHour || sTime.equals(eTime)) {
			throw new InvalidFormDataException("The start time must be before the end time of the commitment", "Adding invalid time range for commitment");
		}
		if (startTime.contains(":00") && isExternal) {
			startHour--;
		}
		if (endTime.contains(":00") && isExternal) {
			endHour++;
		}
		List<Date> meetingDates;
		if (frequency.equals("Weekly")) {
			meetingDates = ImportClinicianMeetingsAction.getMeetingDatesWeekly(dateRange.getStartDate(), dateRange.getEndDate(), 7, dayOfWeek);
		} else if (frequency.equals("Biweekly")) {
			meetingDates = ImportClinicianMeetingsAction.getMeetingDatesWeekly(dateRange.getStartDate(), dateRange.getEndDate(), 14, dayOfWeek);
		} else {
			meetingDates = ImportClinicianMeetingsAction.getMeetingDatesMonthly(dateRange.getStartDate(), dateRange.getEndDate(), 1, dayOfWeek);
		}

		List<CommitmentBean> list = new ArrayList<CommitmentBean>();
		for (Date meetingDate : meetingDates) {
			list.add(new CommitmentBean(-1, startHour, endHour, meetingDate, description));
		}
		return list;
	}
	
	/**
	 * Validate the input for clinician preferences.
	 *
	 * @param clinicianName the clinician name
	 * @param morningRank the morning rank preference
	 * @param noonRank the noon rank preference
	 * @param afternoonRank the afternoon rank preference
	 * @param myIAHours the my ia hours preference
	 * @param myECHours the my ec hours preference
	 * @param isAdmin whether it is an admin (in which case ia/ec hours are displayed)
	 * @return the clinician preferences bean
	 * @throws InvalidFormDataException the invalid form data exception
	 * @throws SQLException the SQL exception
	 */
	public static ClinicianPreferencesBean validatePreferences(String clinicianName, int morningRank, int noonRank, int afternoonRank, String myIAHours, String myECHours, boolean isAdmin) throws InvalidFormDataException, SQLException {

		Connection conn = ConnectionFactory.getInstance();
		ClinicianDAO clinicianDAO = new ClinicianDAO(conn);
		int	clinicianID = clinicianDAO.getClinicianID(clinicianName);
		if (clinicianID == -1) {
			throw new InvalidFormDataException("You must enter in a valid clinician name", "Adding invalid clinician name");
		}

		
		if (morningRank == noonRank || afternoonRank == morningRank || noonRank == afternoonRank) {
			throw new InvalidFormDataException("You must enter unique ranks for each time preference", "Adding clinician ec preferences");
		}
		
		int iaNumHours, ecNumHours;
		if (isAdmin) {
			iaNumHours = Utility.parseInt(myIAHours, "assigned IA hours", "Adding clinician ia preferences");
			ecNumHours = Utility.parseInt(myECHours, "assigned EC hours", "Adding clinician ec preferences");
		} else {
			// load values from calendar
			CalendarDAO calendarDAO = new CalendarDAO(conn);
			CalendarBean calendar = calendarDAO.loadCalendar();
			iaNumHours = calendar.getIaMinHours();
			ecNumHours = calendar.getEcMinHours();
		}
		return new ClinicianPreferencesBean(clinicianID, morningRank, noonRank, afternoonRank, iaNumHours, ecNumHours);
	}
}
