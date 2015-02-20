package forms;

import java.util.Date;
import java.text.SimpleDateFormat;

import javax.swing.JButton;
import javax.swing.JTextField;
import java.awt.Container;

/**
 * Represents days when a Clinician will be away from the Counseling Center.
 * Keeps track of gui components for each entry
 * 
 * @author Yusheng Hou and Kevin Lim
 */
public class TimeAwayPlan {
	private String description;
	private Date startDate;
	private Date endDate;
	
	private JTextField descriptionField;
	private JTextField startDateField;
	private JTextField endDateField;
	private JButton addRemoveButton;
	
	private Container container;
	
	/**
	 * Creates a default entry for entering time away plans
	 */
	public TimeAwayPlan()
	{
		this("", null, null);
		addRemoveButton.setText("Add");
	}
	
	/**
	 * Creates a time when Clinician will be away from the Counseling Center
	 * 
	 * @param description of event
	 * @param start date
	 * @param end date
	 */
	public TimeAwayPlan(String description, Date start, Date end)
	{
		this.description = description;
		this.startDate = start;
		this.endDate = end;
		
		this.descriptionField = new JTextField(this.description);
		this.startDateField = new JTextField();
		if (startDate != null) {
			startDateField.setText(Utility.formatDate(startDate));
		}
		this.endDateField = new JTextField();
		if (endDate != null) {
			endDateField.setText(Utility.formatDate(endDate));
		}
		this.addRemoveButton = new JButton("Remove");
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String planDescription) {
		this.description = planDescription;
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
