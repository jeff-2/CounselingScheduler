package forms;

import java.util.Date;

public class Calendar {
	private int id;
	private Date startDate;
	private Date endDate;
	private int iaMinHours;
	private int ecMinHours;
	private int semester;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
	public int getIaMinHours() {
		return iaMinHours;
	}
	public void setIaMinHours(int iaMinHours) {
		this.iaMinHours = iaMinHours;
	}
	public int getEcMinHours() {
		return ecMinHours;
	}
	public void setEcMinHours(int ecMinHours) {
		this.ecMinHours = ecMinHours;
	}
	public int getSemester() {
		return semester;
	}
	public void setSemester(int semester) {
		this.semester = semester;
	}

}
