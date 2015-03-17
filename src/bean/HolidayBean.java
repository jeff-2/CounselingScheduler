package bean;

import java.util.Date;

/**
 * Represents a holiday that an administrator adds to the settings for a new semester
 *  
 * @author nbeltr2
 * @author dtli2
 */
public class HolidayBean {
	int id;
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
}
