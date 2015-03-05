package forms;

public class Commitment {
	
	private int clinicianID;
	private int hourOfDay;
	private String dayOfWeek;
	private String description;
	
	/**
	 * Creates a weekly event when a clinician is unavailable.
	 * 
	 * @param hour in 24-hour format
	 * @param day of week, use Calendar constants
	 * @param description of conflict
	 */
	public Commitment(int id, int hour, String day, String desc) {
		id = clinicianID;
		hourOfDay = hour;
		dayOfWeek = day;
		description = desc;
	}
	
	public int getClinicianID() {
		return clinicianID;
	}
	
	public void setClinicianID(int id) {
		clinicianID = id;
	}
	
	public int getHourOfDay() {
		return hourOfDay;
	}
	
	public void setHourOfDay(int hour) {
		hourOfDay = hour;
	}
	
	public String getDayOfWeek() {
		return dayOfWeek;
	}
	
	public void setDayOfWeek(String day) {
		dayOfWeek = day;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String desc) {
		description = desc;
	}
	
	public String toString() {
		String hour = "";
		if (hourOfDay >= 12) {
			hour = (hourOfDay - 12) + " pm";
		} else {
			hour = hourOfDay + " am";
		}
		return hour + " " + dayOfWeek + " " + description;
	}
}
