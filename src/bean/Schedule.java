package bean;

import java.sql.Connection;
import java.sql.SQLException;
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
}
