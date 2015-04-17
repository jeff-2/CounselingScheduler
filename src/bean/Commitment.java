package bean;

public class Commitment {
	
	private int id;
	private int startHour;
	private int endHour;
	private String description;
	private Weekday dayOfWeek;
	
	public Commitment(int id, int sHour, int eHour, String desc, Weekday day) {
		this.id = id;
		startHour = sHour;
		endHour = eHour;
		description = desc;
		dayOfWeek = day;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getStartHour() {
		return startHour;
	}

	public void setStartHour(int startHour) {
		this.startHour = startHour;
	}

	public int getEndHour() {
		return endHour;
	}

	public void setEndHour(int endHour) {
		this.endHour = endHour;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Weekday getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(Weekday dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}
	
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		
		if (other == null) {
			return false;
		}
		
		if (!(other instanceof Commitment)) {
			return false;
		}
		
		Commitment commitment = (Commitment) other;
		return id == commitment.id && startHour == commitment.startHour && endHour == commitment.endHour
				&& description.equals(commitment.description) && dayOfWeek.equals(commitment.dayOfWeek);
	}
	
	@Override
	public int hashCode() {
		int hashCode = 31;
		hashCode = hashCode * 31 + id;
		hashCode = hashCode * 31 + startHour;
		hashCode = hashCode * 31 + endHour;
		hashCode = hashCode * 31 + description.hashCode();
		hashCode = hashCode * 31 + dayOfWeek.hashCode();
		return hashCode;
	}
}
