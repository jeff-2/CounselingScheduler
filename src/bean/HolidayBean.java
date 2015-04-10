package bean;

import java.util.Date;

/**
 * Represents a holiday that an administrator adds to the settings for a new semester
 *  
 * @author nbeltr2
 * @author dtli2
 */
public class HolidayBean {
	
	private int id;
	private String name;
	private Date startDate;
	private Date endDate;
	
	/**
	 * Default constructor
	 */
	public HolidayBean() {}
	
	/**
	 * Full constructor
	 */
	public HolidayBean(int index, String n, Date start, Date end) {
		id = index;
		name = n;
		startDate = start;
		endDate = end;
	}
	
	public int getID() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	public String toString() {
		return "id: " + id + " name: " + name + " startDate: " + startDate + " endDate: " + endDate;
	}
	
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		
		if (other == null) {
			return false;
		}
		
		if (!(other instanceof HolidayBean)) {
			return false;
		}
		
		HolidayBean holiday = (HolidayBean)other;
		return id == holiday.id && startDate.equals(holiday.startDate) 
				&& endDate.equals(holiday.endDate) && name.equals(holiday.name);
	}
}
