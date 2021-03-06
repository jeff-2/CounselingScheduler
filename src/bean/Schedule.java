package bean;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
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

	/** The calendar. */
	private CalendarBean calendar;

	/** The holidays. */
	private List<HolidayBean> holidays;

	/** The sessions. */
	private List<SessionBean> sessions;

	/** The weeks. */
	private List<Week> weeks;

	/** The ec sessions. */
	private List<SessionNameBean> ecSessions;

	/** The ia sessions a. */
	private List<SessionNameBean> iaSessionsA;

	/** The ia sessions b. */
	private List<SessionNameBean> iaSessionsB;

	/**
	 * Maps clinician IDs to instances of the Clinician class.
	 */
	private HashMap<Integer, Clinician> clinicians;

	/** The conn. */
	private Connection conn;

	/**
	 * Instantiates a new schedule.
	 *
	 * @param conn
	 *            the conn
	 */
	public Schedule(Connection conn) {
		this.conn = conn;
	}

	/**
	 * Pulls everything from the database needed to run the scheduling
	 * algorithm, including the list of holidays, pre-assignment session slots,
	 * properties of the calendar, the list of all clinicians, and each
	 * clinician's preferences, commitments, and time away.
	 *
	 * @throws SQLException
	 *             the SQL exception
	 */
	private void initialLoader() throws SQLException {
		ClinicianPreferencesDAO clinicianPreferencesDAO = new ClinicianPreferencesDAO(
				conn);
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
			clinicians.put(
					cb.getClinicianID(),
					new Clinician(cb, clinicianPreferencesDAO
							.loadClinicianPreferences(clinicianID),
							commitmentsDAO.loadCommitments(clinicianID),
							timeAwayDAO.loadTimeAway(clinicianID)));
		}

		holidays = holidayDAO.loadHolidays();
		sessions = sessionsDAO.loadSessions();

		// if no calendar in db, use default calendar corresponding to next
		// upcoming semester
		try {
			calendar = calendarDAO.loadCalendar();
		} catch (SQLException e) {
			Calendar cal = Calendar.getInstance();
			calendar = new CalendarBean();
			calendar.setEcMinHours(10);
			calendar.setIaMinHours(10);
			calendar.setId(calendarDAO.getNextAvailableId());
			Semester semester = Semester.getSemesterStartingClosestTo(cal
					.getTime());
			calendar.setSemester(semester);
			calendar.setStartDate(semester.getStartDate());
			calendar.setEndDate(semester.getEndDate());
			calendarDAO.insertCalendar(calendar);
		}

		weeks = Week.getSemesterWeeks(calendar);
		ecSessions = new ArrayList<SessionNameBean>();
		iaSessionsA = new ArrayList<SessionNameBean>();
		iaSessionsB = new ArrayList<SessionNameBean>();
		sessionsByClinician = new HashMap<Clinician, List<ClinicianWeekBean>>();
	}

	/**
	 * Returns the number of weeks in this schedule. Must be called
	 * before the initialLoader
	 *
	 * @return the number of weeks
	 */
	public int getNumberOfWeeks() {
		return weeks.size();
	}

	/**
	 * Returns the list of weeks in the schedule.
	 *
	 * @return the weeks
	 */
	public List<Week> getWeeks() {
		return weeks;
	}

	/**
	 * Finds the clinicians assigned to the specified IA session.
	 *
	 * @param isTypeA
	 *            the is type a
	 * @param day
	 *            of week
	 * @param hour
	 *            of IA session
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
	 * Finds the clinicians assigned to the specified EC session.
	 *
	 * @param week
	 *            number
	 * @param day
	 *            of week
	 * @param hour
	 *            of EC session
	 * @return clinician assigned to the specified EC session
	 */
	public Clinician getECClinician(int week, int day, int hour) {
		HashMap<SessionBean, Clinician> specifiedWeek = ec.get(week);
		for (SessionBean sb : specifiedWeek.keySet()) {
			if (sb.getDayOfWeek().ordinal() == day && sb.getStartTime() == hour) {
				return specifiedWeek.get(sb);
			}
		}
		return null;
	}

	/**
	 * Finds the Commitments of the specified clinician.
	 *
	 * @param c
	 *            the c
	 * @return list of commitmentBean
	 */
	public List<CommitmentBean> getCommitment(Clinician c) {
		int id = c.getClinicianBean().getClinicianID();
		if (clinicians.get(id) == null) {
			return null;
		}
		return clinicians.get(id).getCommitmentBeans();
	}

	/**
	 * Finds the vacation times of the specified clinician.
	 *
	 * @param c
	 *            the c
	 * @return list of TimeAwayBean
	 */
	public List<TimeAwayBean> getTimeAway(Clinician c) {
		int id = c.getClinicianBean().getClinicianID();
		if (clinicians.get(id) == null) {
			return null;
		}
		return clinicians.get(id).getTimeAwayBeans();
	}

	/**
	 * Load schedule from db.
	 *
	 * @return the schedule
	 * @throws SQLException
	 *             the SQL exception
	 */
	public static Schedule loadScheduleFromDB() throws SQLException {
		Schedule schedule = new Schedule(ConnectionFactory.getInstance());
		schedule.initialLoader();
		return schedule;
	}

	/**
	 * Runs the scheduling algorithm (assigning clinicians) and fills the 3
	 * schedule mappings (ec, ia, sessionsByClinician) based on the assignments
	 * given by ScheduleProgram, which adds the assignments to this "sessions"
	 * List.
	 *
	 * @return instance of Schedule class containing everything.
	 * @throws SQLException
	 *             the SQL exception
	 */
	public static Schedule loadScheduleFromDBAndAssignClinicians()
			throws SQLException {
		Schedule schedule = loadScheduleFromDB();
		ScheduleProgram.assignClinicians(schedule);

		// Initial fill of ia and ec
		for (int i = 0; i < schedule.weeks.size(); i++) {
			schedule.ec.add(new HashMap<SessionBean, Clinician>());
		}
		schedule.ia.add(new HashMap<SessionBean, List<Clinician>>());
		schedule.ia.add(new HashMap<SessionBean, List<Clinician>>());

		// Initial fill of x
		for (Clinician c : schedule.clinicians.values()) {
			List<ClinicianWeekBean> weekBeans = new ArrayList<ClinicianWeekBean>();
			for (int i = 0; i < schedule.weeks.size(); i++) {
				weekBeans.add(new ClinicianWeekBean());
			}
			schedule.sessionsByClinician.put(c, weekBeans);
		}

		for (SessionBean sb : schedule.sessions) {
			// Fills the 3 maps
			List<Integer> clinicianIDs = sb.getClinicians();
			int weekNum = schedule.weeks.indexOf(Week.getWeek(sb.getDate(),
					schedule.calendar));
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
						if (s.getDayOfWeek().equals(sb.getDayOfWeek())
								&& s.getStartTime() == sb.getStartTime()) {
							found = true;
							schedule.ia.get(weekNum % 2).put(s, assigned);
						}
					}
					if (!found) {
						schedule.ia.get(weekNum % 2).put(sb, assigned);
					}
				}
			} else { // This is an EC session.
				schedule.ec.get(weekNum).put(sb,
						schedule.clinicians.get(clinicianIDs.get(0)));
			}
			// Fill sessionsByClinician and the ec SessionNameBean lists
			for (Integer id : clinicianIDs) {
				Clinician c = schedule.clinicians.get(id);
				ClinicianBean cb = c.getClinicianBean();
				schedule.sessionsByClinician.get(c).get(weekNum).addSession(sb);
				if (sb.getType() == SessionType.EC) {
					schedule.ecSessions.add(new SessionNameBean(cb.getName(),
							sb.getStartTime(), sb.getDayOfWeek(), sb.getDate(),
							sb.getWeekType() == IAWeektype.A ? 0 : 1, sb
									.getID()));
				}
			}
		}
		schedule.fillIASessionNameBeans();
		return schedule;
	}

	/**
	 * Fill ia session name beans.
	 */
	private void fillIASessionNameBeans() {
		for (SessionBean sb : ia.get(0).keySet()) {
			for (int id : sb.getClinicians()) {
				iaSessionsA.add(new SessionNameBean(clinicians.get(id)
						.getClinicianBean().getName(), sb.getStartTime(), sb
						.getDayOfWeek(), sb.getDate(), 0, sb.getID()));
			}
		}

		for (SessionBean sb : ia.get(1).keySet()) {
			for (int id : sb.getClinicians()) {
				iaSessionsB.add(new SessionNameBean(clinicians.get(id)
						.getClinicianBean().getName(), sb.getStartTime(), sb
						.getDayOfWeek(), sb.getDate(), 1, sb.getID()));
			}
		}
	}

	/**
	 * Gets the holidays.
	 *
	 * @return the holidays
	 */
	public List<HolidayBean> getHolidays() {
		return holidays;
	}

	/**
	 * Sets the holidays.
	 *
	 * @param holidays
	 *            the new holidays
	 */
	public void setHolidays(List<HolidayBean> holidays) {
		this.holidays = holidays;
	}

	/**
	 * Gets the sessions.
	 *
	 * @return the sessions
	 */
	public List<SessionBean> getSessions() {
		return sessions;
	}

	/**
	 * Sets the sessions.
	 *
	 * @param sessions
	 *            the new sessions
	 */
	public void setSessions(List<SessionBean> sessions) {
		this.sessions = sessions;
	}

	/**
	 * Gets the calendar.
	 *
	 * @return the calendar
	 */
	public CalendarBean getCalendar() {
		return calendar;
	}

	/**
	 * Gets the clinicians.
	 *
	 * @return the clinicians
	 */
	public List<Clinician> getClinicians() {
		Collection<Clinician> c = clinicians.values();
		if (c instanceof List) {
			return (List<Clinician>) c;
		}
		return new ArrayList<Clinician>(c);
	}

	/**
	 * Gets the EC schedule map.
	 *
	 * @return the EC schedule map
	 */
	public List<HashMap<SessionBean, Clinician>> getECScheduleMap() {
		return ec;
	}

	/**
	 * Gets the IA schedule map.
	 *
	 * @return the IA schedule map
	 */
	public List<HashMap<SessionBean, List<Clinician>>> getIAScheduleMap() {
		return ia;
	}

	/**
	 * Gets the map of clinicians to sessions.
	 *
	 * @return the map of clinicians to sessions
	 */
	public HashMap<Clinician, List<ClinicianWeekBean>> getMapOfCliniciansToSessions() {
		return sessionsByClinician;
	}

	/**
	 * Gets the EC sessions.
	 *
	 * @return the EC sessions
	 */
	public List<SessionNameBean> getECSessions() {
		return ecSessions;
	}

	/**
	 * Gets the IA sessions a.
	 *
	 * @return the IA sessions a
	 */
	public List<SessionNameBean> getIASessionsA() {
		return iaSessionsA;
	}

	/**
	 * Gets the IA sessions b.
	 *
	 * @return the IA sessions b
	 */
	public List<SessionNameBean> getIASessionsB() {
		return iaSessionsB;
	}

	/**
	 * Replaces the current clinician assignment for the given EC session with
	 * the specified clinician.
	 * 
	 * @param d
	 *            Date of the EC session
	 * @param time
	 *            Start time of the EC session
	 * @param clinicianName
	 *            Name of the Clinician we should assign the session to
	 */
	public void editEC(Date d, int time, String clinicianName) {
		Clinician c = nameToClinician(clinicianName);
		Calendar cal = Calendar.getInstance();
		cal.setTime(calendar.getStartDate());
		int startWeek = cal.get(Calendar.WEEK_OF_YEAR);
		cal.setTime(d);
		int curWeek = cal.get(Calendar.WEEK_OF_YEAR);

		int weekNum = curWeek - startWeek;

		// Update map
		for (SessionBean sb : ec.get(weekNum).keySet()) {
			if (sb.getDate().equals(d) && sb.getStartTime() == time
					&& sb.getType() == SessionType.EC) {
				ec.get(weekNum).put(sb, c);
				break;
			}
		}

		// Update SessionNameBean list
		for (SessionNameBean sb : ecSessions) {
			if (sb.getDate().equals(d) && sb.getStartTime() == time) {
				sb.setClinicianName(clinicianName);
				break;
			}
		}

		// Update sessions
		for (SessionBean sb : sessions) {
			if (sb.getDate().equals(d) && sb.getStartTime() == time
					&& sb.getType() == SessionType.EC) {
				List<Integer> cAL = new ArrayList<Integer>();
				cAL.add(c.getClinicianBean().getClinicianID());
				sb.setClinicians(cAL);
				break;
			}
		}
	}

	/**
	 * Adds the specified clinician to the specified IA session.
	 * 
	 * @param isTypeA
	 *            is the week type A or B (true means it's type A)
	 * @param day
	 *            integer representation of the day of the week of the IA
	 *            session
	 * @param time
	 *            Start time of the IA session
	 * @param clinicianName
	 *            Name of the Clinician we should assigne the session to
	 */
	public void addIAClinician(boolean isTypeA, int day, int time,
			String clinicianName) {
		Clinician c = nameToClinician(clinicianName);
		int weekNum = isTypeA ? 0 : 1;

		// Edit map
		for (SessionBean sb : ia.get(weekNum).keySet()) {
			if (Weekday.getWeekday(sb.getDate()).ordinal() == day
					&& sb.getStartTime() == time
					&& sb.getType() == SessionType.IA) {
				sb.addClinician(c);
				ia.get(weekNum).get(sb).add(c);
				break;
			}
		}

		// Edit SessionNameBean list
		if (weekNum == 0) {
			for (int i = 0; i < iaSessionsA.size(); i++) {
				if (iaSessionsA.get(i).getDayOfWeek().ordinal() == day
						&& iaSessionsA.get(i).getStartTime() == time) {
					iaSessionsA.add(
							i + 1,
							new SessionNameBean(clinicianName, iaSessionsA.get(
									i).getStartTime(), iaSessionsA.get(i)
									.getDayOfWeek(), iaSessionsA.get(i)
									.getDate(), 0, iaSessionsA.get(i)
									.getSessionID()));
					break;
				}
			}
		} else {
			for (int i = 0; i < iaSessionsB.size(); i++) {
				if (iaSessionsB.get(i).getDayOfWeek().ordinal() == day
						&& iaSessionsB.get(i).getStartTime() == time) {
					iaSessionsB.add(
							i + 1,
							new SessionNameBean(clinicianName, iaSessionsB.get(
									i).getStartTime(), iaSessionsB.get(i)
									.getDayOfWeek(), iaSessionsB.get(i)
									.getDate(), 1, iaSessionsB.get(i)
									.getSessionID()));
					break;
				}
			}
		}

		// Update sessions
		for (SessionBean sb : sessions) {
			if (sb.getDayOfWeek().ordinal() == day && sb.getStartTime() == time
					&& sb.getType() == SessionType.IA) {
				sb.addClinician(c);
			}
		}
	}

	/**
	 * Removes the specified clinician from the specified session. Returns
	 * whether the remove was successful (Returns false if the clinician was not
	 * in the list of assigned clinicians for the specified session, or if the
	 * specified session does not exist).
	 * 
	 * @param isTypeA
	 *            true if type A, false if type B
	 * @param day
	 *            integer representation of day of week of the IA session
	 * @param time
	 *            Start time fo the IA session
	 * @param clinicianName
	 *            Name of the specified Clinician
	 * @return boolean - whether the remove was successful
	 */
	public boolean removeIAClinician(boolean isTypeA, int day, int time,
			String clinicianName) {
		Clinician c = nameToClinician(clinicianName);

		int weekNum = isTypeA ? 0 : 1;
		boolean removed = false;

		// Update map
		for (SessionBean sb : ia.get(weekNum).keySet()) {
			if (Weekday.getWeekday(sb.getDate()).ordinal() == day
					&& sb.getStartTime() == time) {
				if (ia.get(weekNum).get(sb).contains(c)) {
					sb.removeClinician(c);
					ia.get(weekNum).get(sb).remove(c);
					removed = true;
				}
			}
		}

		// Update SessionNameBean list
		if (weekNum == 0) {
			for (int i = 0; i < iaSessionsA.size(); i++) {
				if (iaSessionsA.get(i).getDayOfWeek().ordinal() == day
						&& iaSessionsA.get(i).getStartTime() == time
						&& iaSessionsA.get(i).getClinicianName()
								.equals(clinicianName)) {
					iaSessionsA.remove(i);
					break;
				}
			}
		} else {
			for (int i = 0; i < iaSessionsB.size(); i++) {
				if (iaSessionsB.get(i).getDayOfWeek().ordinal() == day
						&& iaSessionsB.get(i).getStartTime() == time
						&& iaSessionsB.get(i).getClinicianName()
								.equals(clinicianName)) {
					iaSessionsB.remove(i);
					break;
				}
			}
		}

		// Update sessions
		for (SessionBean sb : sessions) {
			if (sb.getDayOfWeek().ordinal() == day && sb.getStartTime() == time
					&& sb.getType() == SessionType.IA) {
				sb.removeClinician(c);
			}
		}
		return removed;
	}

	/**
	 * Helper method to get instance of Clinician class from the clinician's
	 * name.
	 *
	 * @param name
	 *            Name of clinician
	 * @return Clinician object associated with clinician name
	 */
	private Clinician nameToClinician(String name) {
		for (Clinician c : clinicians.values()) {
			if (c.getClinicianBean().getName().equals(name)) {
				return c;
			}
		}
		return null;
	}

	/**
	 * Generates a title to display with current semester and year.
	 *
	 * @return semester title
	 */
	public String getSemesterTitle() {
		return calendar.getSemester() + " " + calendar.getYear();
	}
}
