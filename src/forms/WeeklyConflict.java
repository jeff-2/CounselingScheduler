package forms;

/**
 * Represents weekly events when a clinician cannot be on call. 
 * 
 * @author Yusheng Hou and Kevin Lim
 */
public class WeeklyConflict {
	private int hourOfDay;
	private int dayOfWeek;
	private String description;
	
	/**
	 * Creates a weekly event when a clinician is unavailable.
	 * 
	 * @param hour in 24-hour format
	 * @param day of week, use Calendar constants
	 * @param description of conflict
	 */
	public WeeklyConflict(int hour, int day, String description) {
		this.hourOfDay = hour;
		this.dayOfWeek = day;
		this.description = description;
	}
	
	public int getHourOfDay() {
		return hourOfDay;
	}
	public void setHourOfDay(int hourOfDay) {
		this.hourOfDay = hourOfDay;
	}
	public int getDayOfWeek() {
		return dayOfWeek;
	}
	public void setDayOfWeek(int dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
