package forms;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


/**
 * 
 * @author Yusheng Hou and Kevin Lim
 *
 */
public class PreferenceInputForm {
	private String name;
	private Semester semester;
	private int year;
	private Date periodStart;
	private Date periodEnd;
	private ArrayList<TimeAwayPlan> timesAway;
	private ArrayList<WeeklyConflict> weeklyConflicts;
	private EmergencyCoveragePreference preference;
	
	/**
	 * Creates a preference form using the given semester and current date's year.
	 * @param semester
	 */
	public PreferenceInputForm(Semester semester)
	{
		this(semester, Calendar.getInstance().get(Calendar.YEAR));
	}
	
	/**
	 * Creates a preference form using a given semester and year
	 * @param semester
	 * @param year
	 */
	public PreferenceInputForm(Semester semester, int year)
	{
		this.semester = semester;
		this.year = year;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Semester getSemester() {
		return semester;
	}

	public void setSemester(Semester semester) {
		this.semester = semester;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public Date getPeriodStart() {
		return periodStart;
	}

	public void setPeriodStart(Date periodStart) {
		this.periodStart = periodStart;
	}

	public Date getPeriodEnd() {
		return periodEnd;
	}

	public void setPeriodEnd(Date periodEnd) {
		this.periodEnd = periodEnd;
	}

	public ArrayList<TimeAwayPlan> getTimesAway() {
		return timesAway;
	}

	public void setTimesAway(ArrayList<TimeAwayPlan> timesAway) {
		this.timesAway = timesAway;
	}

	public ArrayList<WeeklyConflict> getWeeklyConflicts() {
		return weeklyConflicts;
	}

	public void setWeeklyConflicts(ArrayList<WeeklyConflict> weeklyConflicts) {
		this.weeklyConflicts = weeklyConflicts;
	}

	public EmergencyCoveragePreference getPreference() {
		return preference;
	}

	public void setPreference(EmergencyCoveragePreference preference) {
		this.preference = preference;
	}
	
}
