package bean;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import linearprogram.ScheduleProgram;
import linearprogram.Week;
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
	private HashMap<Clinician, List<ClinicianWeekBean>> x;
	
	private CalendarBean calendar;
	private List<HolidayBean> holidays;
	private List<SessionBean> sessions;	
	private HashMap<Integer, Clinician> clinicians;
	
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
		
		ec = new ArrayList<HashMap<SessionBean, Clinician>>();
		ia = new ArrayList<HashMap<SessionBean, List<Clinician>>>();
		
		List<ClinicianBean> clinicianBeans = clinicianDAO.loadClinicians();
		
		for(ClinicianBean cb : clinicianBeans) {
			int clinicianID = cb.getClinicianID();
			clinicians.put(cb.getClinicianID(), new Clinician(cb,
					clinicianPreferencesDAO.loadClinicianPreferences(clinicianID),
					commitmentsDAO.loadCommitments(clinicianID),
					timeAwayDAO.loadTimeAway(clinicianID)));
		}
		
		setHolidays(holidayDAO.loadHolidays());
		setSessions(sessionsDAO.loadSessions());
		this.calendar = calendarDAO.loadCalendar();
	}
	
	public Schedule loadScheduleFromDB() throws SQLException {
		Schedule schedule = new Schedule(ConnectionFactory.getInstance());
		schedule.initialLoader();
		return schedule;
	}
	
	public Schedule loadScheduleFromDBAndAssignClinicians() throws SQLException {
		Schedule schedule = loadScheduleFromDB();
		ScheduleProgram.assignClinicians(schedule);
		
		List<Week> weeks = Week.getSemesterWeeks(calendar);
		
		// Initial fill of ia and ec
		for (int i = 0; i < weeks.size(); i++) {
			ec.add(new HashMap<SessionBean, Clinician>());
			ia.add(new HashMap<SessionBean, List<Clinician>>());
		}
		
		// Initial fill of x
		for (Clinician c: clinicians.values()) {
			List<ClinicianWeekBean> weekBeans = new ArrayList<ClinicianWeekBean>();
			for (int i = 0; i < weeks.size(); i++) {
				weekBeans.add(new ClinicianWeekBean());
			}
			x.put(c, weekBeans);
		}

		// The important part
		for (SessionBean sb : sessions) {
			List<Integer> clinicianIDs = sb.getClinicians();
			int weekNum = weeks.indexOf(Week.getWeek(sb.getDate(), calendar));
			// Handle ia/ec
			if (sb.getType() == SessionType.IA) {
				// This is an IA session.
				List<Clinician> assigned = new ArrayList<Clinician>();
				for (Integer id : clinicianIDs) {
					assigned.add(clinicians.get(id));
				}
				ia.get(weekNum).put(sb, assigned);
			} else {
				// This is an EC session.
				ec.get(weekNum).put(sb, clinicians.get(clinicianIDs.get(0)));
			}
			// Handle x
			for (Integer id : clinicianIDs) {
				x.get(clinicians.get(id)).get(weekNum).addSession(sb);
			}
		}
		
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
		Collection<Clinician> c = clinicians.values();
		if (c instanceof List) {
			return (List<Clinician>) c;
		}
		return new ArrayList<Clinician>(c);
	}
}
