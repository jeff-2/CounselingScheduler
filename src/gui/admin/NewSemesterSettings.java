package gui.admin;

import forms.Semester;
import gui.InvalidDateRangeException;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

/**
 * Interface for the administrator to initialize the settings for the new semester. 
 * 
 * @author jmfoste2
 * @author nbeltr2
 */
public class NewSemesterSettings extends JFrame implements ActionListener {

	private static final long serialVersionUID = 992467448521859468L;
	private final JPanel panel;
	private JLabel semesterStartDateLabel, semesterEndDateLabel, clinicianHoursLabel;
	private JLabel holidayNameLabel, holidayStartDateLabel, holidayEndDateLabel;
	private JLabel IALabel, ECLabel, IAHoursLabel, ECHoursLabel;
	private JTextField holidayNameText, startHolidayText, endHolidayText, startDateText, endDateText, IAHoursText, ECHoursText;
	private JButton submitButton, addHolidayButton, removeHolidayButton;
	private JComboBox<String> semesterSeasonBox;
	private JScrollPane listScrollPane;
	private JList<String> holidayList;
	
	
	/**
	 * Instantiates a new semester settings.
	 */
	public NewSemesterSettings() {
		super("Create New Semester Settings");
		panel = new JPanel();
		panel.setLayout(new MigLayout("gap rel", "grow"));
		initializeComponents();
		initializeFrame();
	}
	
	/**
	 * Initialize labels.
	 */
	private void initializeLabels() {
		semesterStartDateLabel = new JLabel("Semester Start Date");
		semesterEndDateLabel = new JLabel("Semester End Date");
		holidayNameLabel = new JLabel("Holiday");
		holidayStartDateLabel = new JLabel("Start Date");
		holidayEndDateLabel = new JLabel("End Date");
		clinicianHoursLabel = new JLabel("Clinician Hours");
		IALabel = new JLabel("IA");
		ECLabel = new JLabel("EC");
		IAHoursLabel = new JLabel("hours/week");
		ECHoursLabel = new JLabel("hours/week");
	}
	
	/**
	 * Initialize text fields.
	 */
	private void initializeTextFields() {
		holidayNameText = new JTextField(10);
		holidayNameText.setName("holidayNameText");
		startHolidayText = new JTextField(7);
		startHolidayText.setName("startHolidayText");
		endHolidayText = new JTextField(7);
		endHolidayText.setName("endHolidayText");
		startDateText = new JTextField(7);
		endDateText = new JTextField(7);
		IAHoursText = new JTextField(7);
		ECHoursText = new JTextField(7);
	}
	
	/**
	 * Initialize buttons.
	 */
	private void initializeButtons() {
		addHolidayButton = new JButton("Add Holiday");
		addHolidayButton.setName("addHolidayButton");
		removeHolidayButton = new JButton("Remove Holiday");
		removeHolidayButton.setName("removeHolidayButton");
		submitButton = new JButton("create");
	}
	
	/**
	 * Initialize scroll pane.
	 */
	private void initializeScrollPane() {
		holidayList = new JList<String>();
		holidayList.setModel(new DefaultListModel<String>());
		listScrollPane = new JScrollPane(holidayList);
	}
	
	/**
	 * Initialize season box.
	 */
	private void initializeSeasonBox() {
		int[] seasons = Semester.getValues();
		String[] seasonNames = new String[seasons.length];
		for (int i = 0; i < seasons.length; i++) {
			seasonNames[i] = Semester.asString(seasons[i]);
		}
		semesterSeasonBox = new JComboBox<String>(seasonNames);
	}
	
	/**
	 * Initialize components.
	 */
	private void initializeComponents() {
		initializeLabels();
		initializeTextFields();
		initializeButtons();
		initializeScrollPane();
		initializeSeasonBox();
	}
	
	/**
	 * Adds the semester date components.
	 */
	private void addSemesterDateComponents() {
		panel.add(semesterStartDateLabel);
		panel.add(startDateText);
		panel.add(semesterEndDateLabel);
		panel.add(endDateText);
		panel.add(semesterSeasonBox, "center, wrap");
	}
	
	/**
	 * Adds the holiday components.
	 */
	private void addHolidayComponents() {
		panel.add(holidayNameLabel);
		panel.add(holidayNameText);
		panel.add(holidayStartDateLabel);
		panel.add(startHolidayText);
		panel.add(holidayEndDateLabel);
		panel.add(endHolidayText, "wrap");
		panel.add(listScrollPane, "grow, span, wrap");
		
		removeHolidayButton.addActionListener(this);
		addHolidayButton.addActionListener(this);
		panel.add(addHolidayButton);
		panel.add(removeHolidayButton, "wrap");
	}
	
	/**
	 * Adds the clinician components.
	 */
	private void addClinicianComponents() {
		panel.add(clinicianHoursLabel, "wrap");
		panel.add(IALabel);
		panel.add(IAHoursText);
		panel.add(IAHoursLabel, "wrap");
		panel.add(ECLabel);
		panel.add(ECHoursText);
		panel.add(ECHoursLabel, "wrap");
		submitButton.addActionListener(this);
		panel.add(submitButton, "right");
	}
	
	/**
	 * Initialize frame.
	 */
	private void initializeFrame() {
		panel.setPreferredSize(new Dimension(500, 500));
		
		addSemesterDateComponents();
		addHolidayComponents();
		addClinicianComponents();
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.getContentPane().add(panel);
		this.pack();
		this.setVisible(true);
	}
	
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == addHolidayButton) {
			addHoliday();			
		} else if (e.getSource() == removeHolidayButton) {
			removeHoliday();
		} else if (e.getSource() == submitButton) {
			createSemester();
		}
	}
	
	/**
	 * Creates the semester.
	 */
	private void createSemester() {
		String IAHours = IAHoursText.getText().trim();
		String ECHours = ECHoursText.getText().trim();
		String semesterStart = startDateText.getText().trim(); 
		String semesterEnd = endDateText.getText().trim();
		
		try {
			double IAHrs = Double.parseDouble(IAHours);
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this,
				    "Cannot create new semester. " +
				    "IA hours assigned must be a non-negative number",
				    "Setting invalid semester settings",
				    JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		try {
			double ECHrs = Double.parseDouble(ECHours);
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this,
				    "Cannot create new semester. " +
				    "EC hours assigned must be a non-negative number",
				    "Setting invalid semester settings",
				    JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		try {
			checkDateRange(semesterStart, semesterEnd);
		} catch (InvalidDateRangeException e) {
			JOptionPane.showMessageDialog(this,
				    "Cannot create new semester. " +
				    e.getMessage(),
				    "Setting invalid semester settings",
				    JOptionPane.ERROR_MESSAGE);
			return;
		}
	}
	
	/**
	 * Parses the date. Ensures it is in the proper date format MM/dd/yyyy with a year 
	 * between 1800 and 10000. 
	 *
	 * @param date the date
	 * @return the date
	 * @throws ParseException the parse exception
	 */
	private Date parseDate(String date) throws ParseException {
		Date d;
		try {
			SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
			format.setLenient(false);
			d = format.parse(date);
		} catch (ParseException e) {
			throw new ParseException("The date " + date + " must be a valid date of the form mm/dd/yyyy. For example March 3, 1994 should be entered as 3/3/1994.", 0);
		}
		if (d.before(new SimpleDateFormat("MM/dd/yyyy").parse("01/01/1800"))) {
			throw new ParseException("The year must be a year of the form yyyy, where yyyy is at least 1800", 0);
		}
		if (d.after(new SimpleDateFormat("MM/dd/yyyy").parse("12/31/9999"))) {
			throw new ParseException("The year must be a year of the form yyyy, where yyyy is less than 10000", 0);
		}
		
		return d;
	}
	
	/**
	 * Check for valid date range.
	 *
	 * @param startDate the start date
	 * @param endDate the end date
	 * @throws InvalidDateRangeException the invalid date range exception
	 */
	private void checkDateRange(String startDate, String endDate) throws InvalidDateRangeException {
		try {
			Date lower = parseDate(startDate);
			Date upper = parseDate(endDate);
			if (lower.after(upper))
				throw new InvalidDateRangeException("The start date must be before the end date.");
		} catch (ParseException e) {
			throw new InvalidDateRangeException(e.getMessage());
		}
	}
	
	/**
	 * Removes the holiday from the list of displayed holidays.
	 */
	private void removeHoliday() {
		int index = holidayList.getSelectedIndex();
		if (index >= 0) {
			DefaultListModel<String> oldModel = (DefaultListModel<String>) holidayList.getModel();
			oldModel.remove(index);
		}
	}
	
	/**
	 * Adds the holiday to the list of displayed holidays.
	 */
	private void addHoliday() {
		String holiday = holidayNameText.getText().trim();
		String startDate = startHolidayText.getText().trim();
		String endDate = endHolidayText.getText().trim();
		
		if (holiday.isEmpty()) {
			JOptionPane.showMessageDialog(this,
				    "You must enter in the name for the holiday. ",
				    "Adding invalid holiday",
				    JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		try {
			checkDateRange(startDate, endDate);
		} catch (InvalidDateRangeException e) {
			JOptionPane.showMessageDialog(this,
				    "Cannot add the given holiday to the list. " +
				    e.getMessage(),
				    "Adding invalid holiday",
				    JOptionPane.ERROR_MESSAGE);
			return;
		}

		holidayNameText.setText("");
		startHolidayText.setText("");
		endHolidayText.setText("");

		DefaultListModel<String> model = (DefaultListModel<String>) holidayList.getModel();
		model.add(model.size(), holiday + " " + startDate + "-" + endDate);
	}
}
