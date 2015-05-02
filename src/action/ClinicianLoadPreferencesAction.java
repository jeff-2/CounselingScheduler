package action;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import validator.DateRangeValidator;
import bean.ClinicianPreferencesBean;
import bean.Commitment;
import bean.CommitmentBean;
import bean.TimeAwayBean;
import bean.Weekday;
import dao.ClinicianDAO;
import dao.ClinicianPreferencesDAO;
import dao.CommitmentsDAO;
import dao.TimeAwayDAO;

/**
 * The Class ClinicianLoadPreferencesAction handles loading the preferences for
 * a particular clinician from the database so they can be used to populate the
 * ClinicianForm.
 */
public class ClinicianLoadPreferencesAction {

    private ClinicianDAO clinicianDAO;
    private CommitmentsDAO commitmentsDAO;
    private TimeAwayDAO timeAwayDAO;
    private ClinicianPreferencesDAO clinicianPreferencesDAO;
    private int clinicianID;

    /**
     * Instantiates a new clinician load preferences action.
     *
     * @param conn
     *            the conn
     * @param clinicianName
     *            the clinician name
     * @throws SQLException
     *             the SQL exception
     */
    public ClinicianLoadPreferencesAction(Connection conn, String clinicianName)
	    throws SQLException {
	clinicianDAO = new ClinicianDAO(conn);
	commitmentsDAO = new CommitmentsDAO(conn);
	timeAwayDAO = new TimeAwayDAO(conn);
	clinicianPreferencesDAO = new ClinicianPreferencesDAO(conn);
	clinicianID = clinicianDAO.getClinicianID(clinicianName);
    }

    /**
     * Load the list of times away for this clinician.
     *
     * @return the list of times away
     * @throws SQLException
     *             the SQL exception
     */
    public List<TimeAwayBean> loadTimesAway() throws SQLException {
	return timeAwayDAO.loadTimeAway(clinicianID);
    }

    /**
     * Loads the preferences for this clinician.
     *
     * @return the clinician preferences bean
     * @throws SQLException
     *             the SQL exception
     */
    public ClinicianPreferencesBean loadClinicianPreferences()
	    throws SQLException {
	return clinicianPreferencesDAO.loadClinicianPreferences(clinicianID);
    }

    /**
     * Given a list of commitment beans, groups together all commitment beans
     * referring to the same commitment. Each sublist is sorted in order of
     * increasing date.
     *
     * @return the commitment list
     * @throws SQLException
     *             the SQL exception
     */
    public List<List<CommitmentBean>> loadCommitments() throws SQLException {
	List<CommitmentBean> commitments = commitmentsDAO
		.loadCommitments(clinicianID);
	Map<Commitment, List<CommitmentBean>> map = new HashMap<Commitment, List<CommitmentBean>>();
	for (CommitmentBean commitment : commitments) {
	    Date date = commitment.getDate();
	    Calendar calendar = Calendar.getInstance();
	    calendar.setTime(date);
	    int day = calendar.get(Calendar.DAY_OF_WEEK);
	    Weekday dayOfWeek = Weekday.values()[day - 2];
	    Commitment current = new Commitment(commitment.getClinicianID(),
		    commitment.getStartHour(), commitment.getEndHour(),
		    commitment.getDescription(), dayOfWeek);
	    if (map.containsKey(current)) {
		List<CommitmentBean> list = map.get(current);
		list.add(commitment);
	    } else {
		List<CommitmentBean> list = new ArrayList<CommitmentBean>();
		list.add(commitment);
		map.put(current, list);
	    }
	}
	List<List<CommitmentBean>> commitmentList = new ArrayList<List<CommitmentBean>>();
	for (List<CommitmentBean> list : map.values()) {
	    Collections.sort(list, new Comparator<CommitmentBean>() {
		@Override
		public int compare(CommitmentBean arg0, CommitmentBean arg1) {
		    return arg0.getDate().compareTo(arg1.getDate());
		}
	    });
	    commitmentList.add(list);
	}
	return commitmentList;
    }

    /**
     * For each commitment list in the 2d list of commitmentbeans, creates a
     * string which is representative of that commitment and meaningful for
     * display to the user.
     *
     * @param commitmentList
     *            the commitment list
     * @return the commitment strings
     */
    public List<String> loadCommitmentDescriptions(
	    List<List<CommitmentBean>> commitmentList) {
	List<String> commitmentStrings = new ArrayList<String>();
	for (List<CommitmentBean> list : commitmentList) {
	    CommitmentBean b1 = list.get(0);

	    String frequency = null;
	    if (list.size() >= 2) {
		CommitmentBean b2 = list.get(1);
		long diff = b2.getDate().getTime() - b1.getDate().getTime();
		long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
		if (days == 7) {
		    frequency = "Weekly";
		} else if (days == 14) {
		    frequency = "Biweekly";
		} else {
		    frequency = "Monthly";
		}
	    }
	    Date date = b1.getDate();
	    Calendar calendar = Calendar.getInstance();
	    calendar.setTime(date);
	    int day = calendar.get(Calendar.DAY_OF_WEEK);
	    Weekday dayOfWeek = Weekday.values()[day - 2];

	    String commitmentString;
	    if (frequency != null) {
		commitmentString = "Meeting: " + b1.getDescription() + " "
			+ frequency + " on " + dayOfWeek + " from "
			+ b1.getStartHour() + " to " + b1.getEndHour();
	    } else {
		int year = calendar.get(Calendar.YEAR);
		commitmentString = "Meeting: " + b1.getDescription() + " on "
			+ DateRangeValidator.formatDateLong(date) + ", " + year
			+ " from " + b1.getStartHour() + " to "
			+ b1.getEndHour();
	    }
	    commitmentStrings.add(commitmentString);
	    continue;
	}

	return commitmentStrings;
    }
}
