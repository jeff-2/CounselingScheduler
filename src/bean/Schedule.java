package bean;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dao.ClinicianDAO;
import dao.ClinicianPreferencesDAO;
import dao.CommitmentsDAO;
import dao.HolidayDAO;
import dao.SessionsDAO;
import dao.TimeAwayDAO;

public class Schedule {

	private List<HashMap<SessionBean, Clinician>> ec;
	private List<HashMap<SessionBean, List<Clinician>>> ia;
	private HashMap<ClinicianBean, List<ClinicianWeekBean>> x;
	
	private List<Clinician> clinicians;
	private List<HolidayBean> holidays;
	private List<SessionBean> sessions;
	
	protected Connection conn;
	
	public Schedule(Connection conn) {
		this.conn = conn;
	}
	
	private void initialLoader() throws SQLException {
		ClinicianPreferencesDAO clinicianPreferencesDAO = new ClinicianPreferencesDAO(conn);
		ClinicianDAO clinicianDAO = new ClinicianDAO(conn);
		CommitmentsDAO commitmentsDAO = new CommitmentsDAO(conn);
		HolidayDAO holidayDAO = new HolidayDAO(conn);
		SessionsDAO sessionsDAO = new SessionsDAO(conn);
		TimeAwayDAO timeAwayDAO = new TimeAwayDAO(conn);
		
		List<ClinicianBean> clinicianBeans = clinicianDAO.loadClinicians();
		
		
		for(ClinicianBean cb : clinicianBeans) {
			int clinicianID = cb.getClinicianID();
			clinicians.add(new Clinician(cb,
					clinicianPreferencesDAO.loadClinicianPreferences(clinicianID),
					commitmentsDAO.loadCommitments(clinicianID),
					timeAwayDAO.loadTimeAway(clinicianID)));
		}
		
		holidays = holidayDAO.loadHolidays();
		sessions = sessionsDAO.loadSessions();
	}

	/**
	 * Returns the number of weeks in this schedule
	 */
	public int getNumberOfWeeks() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * Finds the clinicians assigned to the specified IA session
	 * @param isTypeA
	 * @param day of week
	 * @param hour of IA session
	 * @return list of clinicians assigned to the specified IA session
	 */
	public List<Clinician> getIAClinician(boolean isTypeA, int day, int hour) {
		// TODO Auto-generated method stub
		return new ArrayList<>();
	}

	/**
	 * Finds the clinicians assigned to the specified EC session 
	 * @param week number
	 * @param day of week
	 * @param hour of EC session
	 * @return clinician assigned to the specified EC session
	 */
	public Clinician getECClinician(int week, int day, int hour) {
		// TODO Auto-generated method stub
		return null;
	}
}
