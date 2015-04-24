package bean;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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

/**
 * Contains all information pertaining to a schedule. This class is used first
 * to pull all the information from the database for the assignment algorithm.
 * Then, runs the assignment algorithm and fills 3 HashMaps containing the 
 * assignments organized by week, session, session type, and clinician. The
 * contents of these maps can be used for any post-assignment processes.
 * 
 * @author dtli2, lim92, ramusa2
 *
 */
public class Schedule {

	/**
	 * Each list element of ec represents a single week. The HashMap for each
	 * week maps EC sessions to their assigned clinician.
	 */
	private List<HashMap<SessionBean, Clinician>> ec;
	
	/**
	 * Each list element of ia represents a single week. THe HashMap for each
	 * week maps IA sessions to a list of their assigned clinicians.
	 */
	private List<HashMap<SessionBean, List<Clinician>>> ia;
	
	/**
	 * Each clinician maps to a list containing all the sessions that clinician
	 * is assigned to. This list is organized by week, via the ClinicianWeekBean
	 * class.
	 */
	private HashMap<Clinician, List<ClinicianWeekBean>> sessionsByClinician;
	
	private CalendarBean calendar;
	private List<HolidayBean> holidays;
	private List<SessionBean> sessions;
	private List<Week> weeks;
	
	private List<SessionNameBean> ecSessions;
	private List<SessionNameBean> iaSessionsA;
	private List<SessionNameBean> iaSessionsB;
	
	/**
	 * Maps clinician IDs to instances of the Clinician class.
	 */
	private HashMap<Integer, Clinician> clinicians;
	
	protected Connection conn;
	
	public Schedule(Connection conn) {
		this.conn = conn;
	}
	
	/**
	 * Pulls everything from the database needed to run the scheduling algorithm,
	 * including the list of holidays, pre-assignment session slots, properties of
	 * the calendar, the list of all clinicians, and each clinician's preferences,
	 * commitments, and time away.
	 * 
	 * @throws SQLException
	 */
	private void initialLoader() throws SQLException {
		ClinicianPreferencesDAO clinicianPreferencesDAO = new ClinicianPreferencesDAO(conn);
		ClinicianDAO clinicianDAO = new ClinicianDAO(conn);
		CommitmentsDAO commitmentsDAO = new CommitmentsDAO(conn);
		HolidayDAO holidayDAO = new HolidayDAO(conn);
		SessionsDAO sessionsDAO = new SessionsDAO(conn);
		TimeAwayDAO timeAwayDAO = new TimeAwayDAO(conn);
		CalendarDAO calendarDAO = new CalendarDAO(conn);
		
		List<ClinicianBean> clinicianBeans = clinicianDAO.loadClinicians();
		clinicians = new HashMap<Integer, Clinician>();
		ec = new ArrayList<HashMap<SessionBean, Clinician>>();
		ia = new ArrayList<HashMap<SessionBean, List<Clinician>>>();
		
		for (ClinicianBean cb : clinicianBeans) {
			int clinicianID = cb.getClinicianID();
			clinicians.put(cb.getClinicianID(), new Clinician(cb,
					clinicianPreferencesDAO.loadClinicianPreferences(clinicianID),
					commitmentsDAO.loadCommitments(clinicianID),
					timeAwayDAO.loadTimeAway(clinicianID)));
		}
		
		holidays = holidayDAO.loadHolidays();
		sessions = sessionsDAO.loadSessions();
		this.calendar = calendarDAO.loadCalendar();
		
		weeks = Week.getSemesterWeeks(calendar);
		ecSessions = new ArrayList<SessionNameBean>();
		iaSessionsA = new ArrayList<SessionNameBean>();
		iaSessionsB = new ArrayList<SessionNameBean>();
		sessionsByClinician = new HashMap<Clinician, List<ClinicianWeekBean>>();
	}

	/**
	 * Returns the number of weeks in this schedule
	 */
	public int getNumberOfWeeks() {
		// initialLoader needs to be called first
		return weeks.size();
	}

	/**
	 * Finds the clinicians assigned to the specified IA session
	 * @param isTypeA
	 * @param day of week
	 * @param hour of IA session
	 * @return list of clinicians assigned to the specified IA session
	 */
	public List<Clinician> getIAClinician(boolean isTypeA, int day, int hour) {
		int weekNum = isTypeA ? 0 : 1;
		for (SessionBean sb : ia.get(weekNum).keySet()) {
			if (sb.getDayOfWeek().ordinal() == day && sb.getStartTime() == hour) {
				return ia.get(weekNum).get(sb);
			}
		}
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
		HashMap<SessionBean, Clinician> specifiedWeek = ec.get(week);
		for (SessionBean sb : specifiedWeek.keySet()) {
			// instead of using ordinal maybe pass in Weekday type?
			if (sb.getDayOfWeek().ordinal() == day && sb.getStartTime() == hour) {
				return specifiedWeek.get(sb);
			}
		}
		return null;
	}
	
	/**
	 * Finds the Commitments of the specified clinician
	 * @param specified clinician
	 * @return ??
	 */
	public List<CommitmentBean> getCommitment(Clinician c) {
		int id = c.getClinicianBean().getClinicianID();
		if (clinicians.get(id) == null) {
			return null;
		}
		return clinicians.get(id).getCommitmentBeans();
	}
	
	/**
	 * Finds the vacation times of the specified clinician
	 * @param specified clinician
	 * @return list of TimeAwayBean
	 */
	public List<TimeAwayBean> getTimeAway(Clinician c) {
		int id = c.getClinicianBean().getClinicianID();
		if (clinicians.get(id) == null) {
			return null;
		}
		return clinicians.get(id).getTimeAwayBeans();
	}
	
	public static Schedule loadScheduleFromDB() throws SQLException {
		Schedule schedule = new Schedule(ConnectionFactory.getInstance());
		schedule.initialLoader();
		return schedule;
	}
	
	/**
	 * Runs the scheduling algorithm (assigning clinicians) and fills the
	 * 3 schedule mappings (ec, ia, sessionsByClinician) based on the
	 * assignments given by ScheduleProgram, which adds the assignments to
	 * this "sessions" List.
	 * 
	 * @return instance of Schedule class containing everything.
	 * @throws SQLException
	 */
	public static Schedule loadScheduleFromDBAndAssignClinicians() throws SQLException {
		Schedule schedule = loadScheduleFromDB();
		ScheduleProgram.assignClinicians(schedule);
		
		// Initial fill of ia and ec
		for (int i = 0; i < schedule.weeks.size(); i++) {
			schedule.ec.add(new HashMap<SessionBean, Clinician>());
		}
		schedule.ia.add(new HashMap<SessionBean, List<Clinician>>());
		schedule.ia.add(new HashMap<SessionBean, List<Clinician>>());
		
		// Initial fill of x
		for (Clinician c: schedule.clinicians.values()) {
			List<ClinicianWeekBean> weekBeans = new ArrayList<ClinicianWeekBean>();
			for (int i = 0; i < schedule.weeks.size(); i++) {
				weekBeans.add(new ClinicianWeekBean());
			}
			schedule.sessionsByClinician.put(c, weekBeans);
		}
		

		for (SessionBean sb : schedule.sessions) {
			// Fills the 3 maps
			List<Integer> clinicianIDs = sb.getClinicians();
			int weekNum = schedule.weeks.indexOf(Week.getWeek(sb.getDate(), schedule.calendar));
			// Handle ia/ec
			if (sb.getType() == SessionType.IA) {
				// This is an IA session.
				List<Clinician> assigned = new ArrayList<Clinician>();
				for (Integer id : clinicianIDs) {
					assigned.add(schedule.clinicians.get(id));
				}
				if (assigned.size() > 0) {
					boolean found = false;
					for (SessionBean s : schedule.ia.get(weekNum % 2).keySet()) {
						if (s.getDayOfWeek().equals(sb.getDayOfWeek()) && s.getStartTime() == sb.getStartTime()) {
							found = true;
							schedule.ia.get(weekNum % 2).put(s, assigned);
						}
					}
					if (!found) {
						schedule.ia.get(weekNum%2).put(sb, assigned);
					}
				}
			} else {
				// This is an EC session.
				schedule.ec.get(weekNum).put(sb, schedule.clinicians.get(clinicianIDs.get(0)));
			}
			// Fill sessionsByClinician and the ec SessionNameBean lists
			for (Integer id : clinicianIDs) {
				Clinician c = schedule.clinicians.get(id);
				ClinicianBean cb = c.getClinicianBean();
				schedule.sessionsByClinician.get(c).get(weekNum).addSession(sb);
				if (sb.getType() == SessionType.EC) {
					schedule.ecSessions.add(new SessionNameBean(
							cb.getName(),
							sb.getStartTime(),
							sb.getDayOfWeek(),
							sb.getDate(),
							sb.getWeekType() == IAWeektype.A ? 0 : 1,
							sb.getID()));
				}
			}
		}
		
		schedule.fillIASessionNameBeans();
		
		return schedule;
	}
	
	private void fillIASessionNameBeans() {
		for (SessionBean sb : ia.get(0).keySet()) {
			for (int id : sb.getClinicians()) {
				iaSessionsA.add(new SessionNameBean(
						clinicians.get(id).getClinicianBean().getName(),
						sb.getStartTime(),
						sb.getDayOfWeek(),
						sb.getDate(),
						0,
						sb.getID()));
			}
		}
		
		for (SessionBean sb : ia.get(1).keySet()) {
			for (int id : sb.getClinicians()) {
				iaSessionsB.add(new SessionNameBean(
						clinicians.get(id).getClinicianBean().getName(),
						sb.getStartTime(),
						sb.getDayOfWeek(),
						sb.getDate(),
						1,
						sb.getID()));
			}
		}
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

	public List<HashMap<SessionBean, Clinician>> getECScheduleMap() {
		return ec;
	}

	public List<HashMap<SessionBean, List<Clinician>>> getIAScheduleMap() {
		return ia;
	}

	public HashMap<Clinician, List<ClinicianWeekBean>> getMapOfCliniciansToSessions() {
		return sessionsByClinician;
	}
	
	public List<SessionNameBean> getECSessions() {
		return ecSessions;
	}

	public List<SessionNameBean> getIASessionsA() {
		return iaSessionsA;
	}
	
	public List<SessionNameBean> getIASessionsB() {
		return iaSessionsB;
	}

	/**
	 * Replaces the current clinician assignment for the given EC session with
	 * the specified clinician.
	 * 
	 * @param d Date of the EC session
	 * @param time Start time of the EC session
	 * @param clinicianName Name of the Clinician we should assign the session to
	 */
	public void editEC(Date d, int time, String clinicianName) {
		Clinician c = nameToClinician(clinicianName);
		
		int weekNum = weeks.indexOf(Week.getWeek(d, calendar));
		for (SessionBean sb : ec.get(weekNum).keySet()) {
			if (sb.getDate().equals(d) && sb.getStartTime() == time) {
				ec.get(weekNum).put(sb, c);
				break;
			}
		}
	}
	
	/**
	 * Adds the specified clinician to the specified IA session.
	 * 
	 * @param d Date of the IA session
	 * @param time Start time of the IA session
	 * @param clinicianName Name of the Clinician we should assigne the session to
	 */
	public void addIAClinician(Date d, int time, String clinicianName) {
		Clinician c = nameToClinician(clinicianName);
		
		int weekNum = weeks.indexOf(Week.getWeek(d, calendar));
		for (SessionBean sb : ia.get(weekNum).keySet()) {
			if (sb.getDate().equals(d) && sb.getStartTime() == time) {
				ia.get(weekNum).get(sb).add(c);
				break;
			}
		}
	}
	
	/**
	 * Removes the specified clinician from the specified session. Returns
	 * whether the remove was successful (Returns false if the clinician was
	 * not in the list of assigned clinicians for the specified session, or
	 * if the specified session does not exist).
	 * 
	 * @param d Date of the IA session
	 * @param time Start time fo the IA session
	 * @param clinicianName Name of the specified Clinician
	 * @return boolean - whether the remove was successful
	 */
	public boolean removeIAClinician(Date d, int time, String clinicianName) {
		Clinician c = nameToClinician(clinicianName);
		
		int weekNum = weeks.indexOf(Week.getWeek(d, calendar));
		for (SessionBean sb : ia.get(weekNum).keySet()) {
			if (sb.getDate().equals(d) && sb.getStartTime() == time) {
				if (ia.get(weekNum).get(sb).contains(c)) {
					ia.get(weekNum).get(sb).remove(c);
					return true;
				} else {
					return false;
				}
			}
		}		
		return false;
	}
	
	/**
	 * Helper method to get instance of Clinician class from the clinician's name
	 * 
	 * @param name Name of clinician
	 * @return Clinician object assosiated with clinician name
	 */
	private Clinician nameToClinician(String name) {
		for (Clinician c : clinicians.values()) {
			if (c.getClinicianBean().getName().equals(name)) {
				return c;
			}
		}
		return null;
	}
}
