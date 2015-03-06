package action;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import db.ClinicianPreferencesDao;
import db.CommitmentsDao;
import db.TimeAwayDao;
import forms.ClinicianPreferences;
import forms.Commitment;
import forms.TimeAway;

/**
 * The Class ClinicianPreferencesAction which handles the validation and interaction with the daos.
 * 
 * @author jmfoste2, lim92
 */
public class ClinicianPreferencesAction {
	
	/** The preferences. */
	private ClinicianPreferences preferences;
	
	/** The commitments. */
	private List<Commitment> commitments;
	
	/** The times away. */
	private List<TimeAway> timesAway;
	
	/** The clinician preferences dao. */
	private ClinicianPreferencesDao clinicianPreferencesDao;
	
	/** The time away dao. */
	private TimeAwayDao timeAwayDao;
	
	/** The commitment dao. */
	private CommitmentsDao commitmentDao;
	
	/**
	 * Instantiates a new clinician preferences action.
	 *
	 * @param prefs the prefs
	 * @param cmts the cmts
	 * @param tsAway the ts away
	 */
	public ClinicianPreferencesAction(ClinicianPreferences prefs, List<Commitment> cmts, List<TimeAway> tsAway, Connection conn) {
		preferences = prefs;
		commitments = cmts;
		timesAway = tsAway;
		clinicianPreferencesDao = new ClinicianPreferencesDao(conn);
		timeAwayDao = new TimeAwayDao(conn);
		commitmentDao = new CommitmentsDao(conn);
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
		
		for (Commitment commitment : commitments) {
			commitment.setClinicianID(clinicianID);
			commitmentDao.insert(commitment);
		}
		
		timeAwayDao.delete(clinicianID);

		for (TimeAway timeAway : timesAway) {
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
		
		for (Commitment commitment : commitments) {
			commitment.setClinicianID(clinicianID);
			commitmentDao.insert(commitment);
		}
		
		for (TimeAway timeAway : timesAway) {
			timeAway.setClinicianID(clinicianID);
			timeAwayDao.insert(timeAway);
		}
	}
}
