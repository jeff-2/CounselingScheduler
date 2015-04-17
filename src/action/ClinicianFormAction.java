package action;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.swing.ListModel;

import bean.ClinicianPreferencesBean;
import bean.CommitmentBean;
import bean.TimeAwayBean;
import dao.ClinicianPreferencesDAO;
import dao.CommitmentsDAO;
import dao.TimeAwayDAO;

public class ClinicianFormAction {
	
	private Connection conn;
	private ClinicianPreferencesBean preferences;
	private List<List<CommitmentBean>> commitments;
	private ListModel<TimeAwayBean> timesAway;
	private ClinicianPreferencesDAO clinicianPreferencesDao;
	private TimeAwayDAO timeAwayDao;
	private CommitmentsDAO commitmentDao;
	
	public ClinicianFormAction(Connection conn, ClinicianPreferencesBean preferences, List<List<CommitmentBean>> commitments, ListModel<TimeAwayBean> timesAway) {
		this.conn = conn;
		this.preferences = preferences;
		this.commitments = commitments;
		this.timesAway = timesAway;
		clinicianPreferencesDao = new ClinicianPreferencesDAO(conn);
		timeAwayDao = new TimeAwayDAO(conn);
		commitmentDao = new CommitmentsDAO(conn);
	}
	
	/**
	 * Validates field in clinician form and submits clinician preferences and other entered data.
	 *
	 * @throws SQLException the SQL exception
	 */
	public void submit(boolean isUpdate) throws SQLException, InvalidFormDataException {
		
		if (isUpdate) {
			updatePreferences();
		} else {
			insertPreferences();
		}
	}
	
	/**
	 * Returns true if submitting this form will overwrite prior clinician preferences
	 * @return boolean indicating if submission will result in overwriting prior preferences
	 * @throws SQLException
	 */
	public boolean willOverwritePreferences() throws SQLException {
		ClinicianPreferencesDAO clinicianPreferencesDAO = new ClinicianPreferencesDAO(conn);
		ClinicianPreferencesBean existingPreferences = clinicianPreferencesDAO.loadClinicianPreferences(preferences.getClinicianID());
		return existingPreferences != null;
	}
	
	/**
	 * Update all the clinician preferences for the particular clinician id.
	 *
	 * @throws SQLException the SQL exception
	 */
	private void updatePreferences() throws SQLException {
		
		clinicianPreferencesDao.update(preferences);
		int clinicianID = preferences.getClinicianID();
		
		commitmentDao.delete(clinicianID);
		
		for (List<CommitmentBean> commitmentList : commitments) {
			for (CommitmentBean commitment : commitmentList) {
				commitment.setClinicianID(clinicianID);
				commitmentDao.insert(commitment);
			}
		}
		
		timeAwayDao.delete(clinicianID);

		for (int i = 0; i < timesAway.getSize(); i++) {
			TimeAwayBean timeAway = timesAway.getElementAt(i);
			timeAway.setClinicianID(clinicianID);
			timeAwayDao.insert(timeAway);
		}
	}
	
	/**
	 * Insert all the clinician preferences for a particular clinician id.
	 *
	 * @throws SQLException the SQL exception
	 */
	private void insertPreferences() throws SQLException {
		
		clinicianPreferencesDao.insert(preferences);
		int clinicianID = preferences.getClinicianID();
		
		for (List<CommitmentBean> commitmentList : commitments) {
			for (CommitmentBean commitment : commitmentList) {
				commitment.setClinicianID(clinicianID);
				commitmentDao.insert(commitment);
			}
		}
		
		for (int i = 0; i < timesAway.getSize(); i++) {
			TimeAwayBean timeAway = timesAway.getElementAt(i);
			timeAway.setClinicianID(clinicianID);
			timeAwayDao.insert(timeAway);
		}
	}
}
