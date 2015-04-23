package bean;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import linearprogram.ScheduleProgram;

import dao.CalendarDAO;
import dao.ClinicianDAO;
import dao.ClinicianPreferencesDAO;
import dao.CommitmentsDAO;
import dao.ConnectionFactory;
import dao.HolidayDAO;
import dao.SessionsDAO;
import dao.TimeAwayDAO;

public class Schedule {

	private List<HashMap<SessionBean, Clinician>> ec;
	private List<HashMap<SessionBean, List<Clinician>>> ia;
	private HashMap<ClinicianBean, List<ClinicianWeekBean>> x;
	
	private CalendarBean calendar;
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
		CalendarDAO calendarDAO = new CalendarDAO(conn);
		
		List<ClinicianBean> clinicianBeans = clinicianDAO.loadClinicians();
		clinicians = new ArrayList<Clinician>();
		
		
		for(ClinicianBean cb : clinicianBeans) {
			int clinicianID = cb.getClinicianID();
			clinicians.add(new Clinician(cb,
					clinicianPreferencesDAO.loadClinicianPreferences(clinicianID),
					commitmentsDAO.loadCommitments(clinicianID),
					timeAwayDAO.loadTimeAway(clinicianID)));
		}
		
		setHolidays(holidayDAO.loadHolidays());
		setSessions(sessionsDAO.loadSessions());
		this.calendar = calendarDAO.loadCalendar();
	}
	
	public static Schedule loadScheduleFromDB() throws SQLException {
		Schedule schedule = new Schedule(ConnectionFactory.getInstance());
		schedule.initialLoader();
		return schedule;
	}
	
	public static Schedule loadScheduleFromDBAndAssignClinicians() throws SQLException {
		Schedule schedule = loadScheduleFromDB();
		ScheduleProgram.assignClinicians(schedule);
		return schedule;
	}

	public List<HolidayBean> getHolidays() {
		return holidays;
	}

	public void setHolidays(List<HolidayBean> holidays) {
		this.holidays = holidays;
	}

	public List<SessionBean> getSessions() {
		return sessions;
	}

	public void setSessions(List<SessionBean> sessions) {
		this.sessions = sessions;
	}

	public CalendarBean getCalendar() {
		return calendar;
	}

	public List<Clinician> getClinicians() {
		return clinicians;
	}
}
