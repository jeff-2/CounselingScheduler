package action;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import bean.ClinicianPreferencesBean;
import bean.CommitmentBean;
import bean.TimeAwayBean;
import dao.ClinicianPreferencesDAO;
import dao.CommitmentsDAO;
import dao.TimeAwayDAO;

/**
 * The Class ClinicianPreferencesAction which handles the validation and interaction with the daos.
 * 
 * @author jmfoste2, lim92
 */
public class ClinicianPreferencesAction {
	
	private ClinicianPreferencesBean preferences;
	private List<CommitmentBean> commitments;
	private List<TimeAwayBean> timesAway;
	private ClinicianPreferencesDAO clinicianPreferencesDao;
	private TimeAwayDAO timeAwayDao;
	private CommitmentsDAO commitmentDao;
	
	/**
	 * Instantiates a new clinician preferences action.
	 *
	 * @param prefs the clinician preferences
	 * @param cmts the list of commitments
	 * @param tsAway the list of time away
	 * @param conn the connection
	 */
	public ClinicianPreferencesAction(ClinicianPreferencesBean prefs, List<CommitmentBean> cmts, List<TimeAwayBean> tsAway, Connection conn) {
		preferences = prefs;
		commitments = cmts;
		timesAway = tsAway;
		clinicianPreferencesDao = new ClinicianPreferencesDAO(conn);
		timeAwayDao = new TimeAwayDAO(conn);
		commitmentDao = new CommitmentsDAO(conn);
	}
	
	/**
	 * Update all the clinician preferences for the particular clinician id.
	 *
	 * @throws SQLException the SQL exception
	 */
	public void updatePreferences() throws SQLException {
		
		clinicianPreferencesDao.update(preferences);
		int clinicianID = preferences.getClinicianID();
		
		commitmentDao.delete(clinicianID);
		
		for (CommitmentBean commitment : commitments) {
			commitment.setClinicianID(clinicianID);
			commitmentDao.insert(commitment);
		}
		
		timeAwayDao.delete(clinicianID);

		for (TimeAwayBean timeAway : timesAway) {
			timeAway.setClinicianID(clinicianID);
			timeAwayDao.insert(timeAway);
		}
	}
	
	/**
	 * Insert all the clinician preferences for a particular clinician id.
	 *
	 * @throws SQLException the SQL exception
	 */
	public void insertPreferences() throws SQLException {
		
		clinicianPreferencesDao.insert(preferences);
		int clinicianID = preferences.getClinicianID();
		
		for (CommitmentBean commitment : commitments) {
			commitment.setClinicianID(clinicianID);
			commitmentDao.insert(commitment);
		}
		
		for (TimeAwayBean timeAway : timesAway) {
			timeAway.setClinicianID(clinicianID);
			timeAwayDao.insert(timeAway);
		}
	}
}
