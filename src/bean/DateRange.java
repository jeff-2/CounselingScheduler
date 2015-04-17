package bean;

import java.util.Date;

public class DateRange {
	private Date startDate;
	private Date endDate;
	
	public DateRange(Date start, Date end) {
		startDate = start;
		endDate = end;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date start) {
		startDate = start;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date end) {
		endDate = end;
	}
}
