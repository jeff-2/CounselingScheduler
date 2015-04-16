package gui.clinician;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListModel;

import net.miginfocom.swing.MigLayout;
import validator.DateRangeValidator;
import validator.InvalidDateRangeException;
import action.ClinicianPreferencesAction;
import action.ImportClinicianMeetingsAction;
import bean.CalendarBean;
import bean.ClinicianPreferencesBean;
import bean.CommitmentBean;
import bean.OperatingHours;
import bean.Semester;
import bean.TimeAwayBean;
import bean.Weekday;
import dao.CalendarDAO;
import dao.ClinicianDAO;
import dao.ClinicianPreferencesDAO;
import dao.CommitmentsDAO;
import dao.ConnectionFactory;
import dao.TimeAwayDAO;

/**
 * The Class ClinicianForm.
 *
 * @author Yusheng Hou and Kevin Lim
 */
public class ClinicianForm extends JFrame implements ActionListener {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -5377691259929030865L;
	
	private JPanel panel;
	private JLabel nameLabel, preferenceFormLabel, periodLabel, timeAwayLabel;
	private JScrollPane timeAwayPane, commitmentsPane;
	private JList<TimeAwayBean> timeAway;
	private JList<String> commitments;
	private List<List<CommitmentBean>> commitmentList;
	private JTextField timeAwayName, timeAwayStartDate, timeAwayEndDate;
	private JTextField commitmentDescription;
	private JLabel timeAwayNameLabel, timeAwayStartDateLabel, timeAwayEndDateLabel;
	private JTextArea timeAwayDescription, ecPreferencesDescription, conflictsDescription;
	private JLabel commitmentStartTimeLabel, commitmentEndTimeLabel, commitmentFrequencyLabel, commitmentDayLabel, commitmentDescriptionLabel, externalCommitmentLabel;
	private JLabel timeLabel, rankLabel;
	private JLabel morningLabel, noonLabel, afternoonLabel;
	private JLabel ecPreferencesLabel, conflictsLabel, iaTimesLabel, ecTimesLabel;
	private JButton addTimeAwayButton, removeTimeAwayButton;
	private JButton addCommitmentButton, removeCommitmentButton;
	private JButton clearButton, submitButton;
	private JComboBox<String> daysOfWeekBox, startTimeBox, endTimeBox, frequencyBox;
	private JComboBox<Integer> morningRankBox, noonRankBox, afternoonRankBox;
	private JCheckBox externalCommitment;
	private JTextField nameField;
	private static final int NAME_LENGTH = 20;
	private Semester semester;
	private int year;
	private Date startDate, endDate;
	private boolean isAdmin;
	private JLabel iaHoursLabel, ecHoursLabel;
	private JTextField iaHours, ecHours;
	private String clinicianName;

	/**
	 * Instantiates a new clinician form.
	 */
	public ClinicianForm(Semester semester, int year, Date startDate, Date endDate, boolean isAdmin, String name) {
		super("Clinician Input Form");
		panel = new JPanel();
		panel.setLayout(new MigLayout("gap rel", "grow"));
		this.semester = semester;
		this.year = year;
		this.startDate = startDate;
		this.endDate = endDate;
		this.isAdmin = isAdmin;
		this.clinicianName = name;
		commitmentList = new ArrayList<List<CommitmentBean>>();
		initializeComponents();
		if (isAdmin) {
			try {
				loadPreferences();
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(this,
						"Failed to connect to the remote SQL database; please contact the network administrator.",
						"Database connection error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
		initializeFrame();
	}
	
	/**
	 * Initialize components.
	 */
	private void initializeComponents() {
		initializeTextFields();
		initializeLabels();
		initializeButtons();
		initializeScrollPanes();
		initializeComboBoxes();
	}
	
	/**
	 * Initialize text fields.
	 */
	private void initializeTextFields() {
		nameField = new JTextField(NAME_LENGTH);
		nameField.setName("nameField");
		timeAwayName = new JTextField(7);
		timeAwayName.setName("timeAwayName");
		timeAwayStartDate = new JTextField(7);
		timeAwayStartDate.setName("timeAwayStartDate");
		timeAwayEndDate = new JTextField(7);
		timeAwayEndDate.setName("timeAwayEndDate");
		commitmentDescription = new JTextField(7);
		commitmentDescription.setName("commitmentDescription");
		if (isAdmin) {
			iaHours = new JTextField(7);
			iaHours.setName("iaHours");
			ecHours = new JTextField(7);
			ecHours.setName("ecHours");
		}
	}
	
	/**
	 * Initialize labels.
	 */
	private void initializeLabels() {
		nameLabel = new JLabel("NAME:");
		preferenceFormLabel = new JLabel(semester.name() + " " + year + " IA/EC Preference Form");
		periodLabel = new JLabel("This covers the period from " + DateRangeValidator.formatDateLong(startDate) + " through " + DateRangeValidator.formatDateLong(endDate) + ", " + year + ".");
		timeAwayLabel = new JLabel(semester.name() + " Semester " + year + " \"Time Away\" Plans");
		timeAwayDescription = new JTextArea("Please list the dates/days/times etc. that you know you will be away from the Center. Please include VACATIONS, CONFERENCES, SICK DAYS, JURY DUTY or any other circumstances that will take you out of the Center.");
		timeAwayDescription.setLineWrap(true);
		timeAwayDescription.setWrapStyleWord(true);
		timeAwayDescription.setSize(880, 200);
		timeAwayDescription.setEditable(false);
		timeAwayDescription.setBackground(this.getBackground());
		timeAwayDescription.setFont(periodLabel.getFont());
		timeAwayNameLabel = new JLabel("Description");
		timeAwayStartDateLabel = new JLabel("Start Date");
		timeAwayEndDateLabel = new JLabel("End Date");
		conflictsLabel = new JLabel("Initial Appointment / Emergency Coverage Conflicts");
		iaTimesLabel = new JLabel("We offer IAs at 11:00 a.m., 1:00 p.m., 2:00 p.m. and 3:00 p.m. during the " + semester + " Semester.");
		ecTimesLabel = new JLabel("1-Hour Emergency Coverage shifts are at 8:00 a.m., Noon, and 4:00 p.m. every day.");
		conflictsDescription = new JTextArea("Please indicate \"impossible\" shift times due to other activities, such as groups and meetings/committees. You do not need to list vacation/conferences again, since you have already listed them above.");
		conflictsDescription.setLineWrap(true);
		conflictsDescription.setWrapStyleWord(true);
		conflictsDescription.setSize(880, 200);
		conflictsDescription.setEditable(false);
		conflictsDescription.setBackground(this.getBackground());
		conflictsDescription.setFont(periodLabel.getFont());
		commitmentStartTimeLabel = new JLabel("Start Time");
		commitmentEndTimeLabel = new JLabel("End Time");
		commitmentFrequencyLabel = new JLabel("Frequency");
		externalCommitmentLabel = new JLabel("External");
		commitmentDayLabel = new JLabel("Day of Week");
		commitmentDescriptionLabel = new JLabel("Activity or Meeting");
		ecPreferencesLabel = new JLabel("E.C. Preferences");
		ecPreferencesDescription = new JTextArea("Please rank your preferences for E.C. shifts. We will try to take your preferences into account but we cannot guarantee they will be reflected in your schedule.");
		ecPreferencesDescription.setLineWrap(true);
		ecPreferencesDescription.setWrapStyleWord(true);
		ecPreferencesDescription.setSize(780, 200);
		ecPreferencesDescription.setEditable(false);
		ecPreferencesDescription.setBackground(this.getBackground());
		ecPreferencesDescription.setFont(periodLabel.getFont());
		timeLabel = new JLabel("Time");
		rankLabel = new JLabel("Rank");
		morningLabel = new JLabel("8:00am");
		noonLabel = new JLabel("12:00pm");
		afternoonLabel = new JLabel("4:00pm");
		if (isAdmin) {
			iaHoursLabel = new JLabel("IA Hours Assigned");
			ecHoursLabel = new JLabel("EC Hours Assigned");
		}
	}
	
	/**
	 * Initialize buttons.
	 */
	private void initializeButtons() {
		addTimeAwayButton = new JButton("Add Time Away");
		addTimeAwayButton.setName("addTimeAwayButton");
		addTimeAwayButton.addActionListener(this);
		removeTimeAwayButton = new JButton("Remove Time Away");
		removeTimeAwayButton.setName("removeTimeAwayButton");
		removeTimeAwayButton.addActionListener(this);
		addCommitmentButton = new JButton("Add Conflict");
		addCommitmentButton.setName("addCommitmentButton");
		addCommitmentButton.addActionListener(this);
		removeCommitmentButton = new JButton("Remove Conflict");
		removeCommitmentButton.setName("removeCommitmentButton");
		removeCommitmentButton.addActionListener(this);
		clearButton = new JButton("Clear");
		clearButton.addActionListener(this);
		submitButton = new JButton("Submit");
		submitButton.setName("submitButton");
		submitButton.addActionListener(this);
	}
	
	/**
	 * Initialize scroll panes.
	 */
	private void initializeScrollPanes() {
		timeAway = new JList<TimeAwayBean>();
		timeAway.setName("timeAway");
		timeAway.setModel(new DefaultListModel<TimeAwayBean>());
		timeAwayPane = new JScrollPane(timeAway);
		commitments = new JList<String>();
		commitments.setName("commitments");
		commitments.setModel(new DefaultListModel<String>());
		commitmentsPane = new JScrollPane(commitments);
	}
	
	/**
	 * Initialize combo boxes.
	 */
	private void initializeComboBoxes() {
		startTimeBox = new JComboBox<String>();
		startTimeBox.setName("startTimeBox");
		endTimeBox = new JComboBox<String>();
		endTimeBox.setName("endTimeBox");
		for (String hour : OperatingHours.getOperatingHours()) {
			startTimeBox.addItem(hour);
			String midHour = hour.replaceAll(":00", ":30");
			startTimeBox.addItem(midHour);
			endTimeBox.addItem(hour);
			endTimeBox.addItem(midHour);
		}
		frequencyBox = new JComboBox<String>();
		frequencyBox.setName("frequencyBox");
		frequencyBox.addItem("Weekly");
		frequencyBox.addItem("Biweekly");
		frequencyBox.addItem("Monthly");
		externalCommitment = new JCheckBox();
		externalCommitment.setSelected(false);
		
		daysOfWeekBox = new JComboBox<String>();
		daysOfWeekBox.setName("daysOfWeekBox");
		for (Weekday day : Weekday.values()) {
			daysOfWeekBox.addItem(day.name());
		}
		morningRankBox = new JComboBox<Integer>();
		morningRankBox.setName("morningRankBox");
		noonRankBox = new JComboBox<Integer>();
		noonRankBox.setName("noonRankBox");
		afternoonRankBox = new JComboBox<Integer>();
		afternoonRankBox.setName("afternoonRankBox");
		for (int i = 1; i <= 3; i++) {
			morningRankBox.addItem(i);
			noonRankBox.addItem(i);
			afternoonRankBox.addItem(i);
		}
	}
	
	/**
	 * Initialize frame.
	 */
	private void initializeFrame() {
		panel.setPreferredSize(new Dimension(900, 800));
		
		panel.add(nameLabel);
		panel.add(nameField, "wrap, align center");
		panel.add(preferenceFormLabel, "align center, span, wrap");
		panel.add(periodLabel, "align center, span, wrap");
		
		addTimeAwayComponents();
		addCommitmentComponents();
		addPreferenceComponents();
		if (isAdmin) {
			addAdminComponents();
		}
		
		panel.add(clearButton);
		panel.add(submitButton, "wrap");
		
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.getContentPane().add(panel);
		this.pack();
		this.setVisible(true);
	}
	
	/**
	 * Adds the time away components.
	 */
	private void addTimeAwayComponents() {
		panel.add(timeAwayLabel, "align center, span, wrap");
		panel.add(timeAwayDescription, "align center, span, wrap");
		panel.add(timeAwayNameLabel);
		panel.add(timeAwayName);
		panel.add(timeAwayStartDateLabel);
		panel.add(timeAwayStartDate);
		panel.add(timeAwayEndDateLabel);
		panel.add(timeAwayEndDate, "wrap");
		panel.add(timeAwayPane, "grow, span, wrap");
		panel.add(addTimeAwayButton);
		panel.add(removeTimeAwayButton, "wrap");
	}
	
	/**
	 * Adds the commitment components.
	 */
	private void addCommitmentComponents() {
		panel.add(conflictsLabel, "align center, span, wrap");
		panel.add(iaTimesLabel, "align center, span, wrap");
		panel.add(ecTimesLabel, "align center, span, wrap");
		panel.add(conflictsDescription, "align center, span, wrap");
		panel.add(commitmentStartTimeLabel);
		panel.add(startTimeBox);
		panel.add(commitmentEndTimeLabel);
		panel.add(endTimeBox);
		panel.add(commitmentDayLabel);
		panel.add(daysOfWeekBox);
		panel.add(commitmentFrequencyLabel);
		panel.add(frequencyBox);
		panel.add(externalCommitmentLabel);
		panel.add(externalCommitment);
		panel.add(commitmentDescriptionLabel);
		panel.add(commitmentDescription, "wrap");
		panel.add(commitmentsPane, "grow, span, wrap");
		panel.add(addCommitmentButton);
		panel.add(removeCommitmentButton, "wrap");
	}
	
	/**
	 * Adds the preference components.
	 */
	private void addPreferenceComponents() {
		panel.add(ecPreferencesLabel, "align center, span, wrap");
		panel.add(ecPreferencesDescription, "align center, span, wrap");
		panel.add(timeLabel);
		panel.add(morningLabel);
		panel.add(noonLabel);
		panel.add(afternoonLabel, "wrap");
		panel.add(rankLabel);
		panel.add(morningRankBox);
		panel.add(noonRankBox);
		panel.add(afternoonRankBox, "wrap");
	}
	
	private void addAdminComponents() {
		panel.add(iaHoursLabel);
		panel.add(iaHours);
		panel.add(ecHoursLabel);
		panel.add(ecHours, "wrap");
	}


	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == clearButton) {
			clearFields();
		} else if (e.getSource() == submitButton) {
			try {
				submit();
			} catch (SQLException e1) {
				JOptionPane.showMessageDialog(this,
					    "An error occurred accessing the database. Please contact your system administrator. " + e1.getMessage(),
					    "Error accessing database",
					    JOptionPane.ERROR_MESSAGE);
			}
		} else if (e.getSource() == addTimeAwayButton) {
			addTimeAway();
		} else if (e.getSource() == removeTimeAwayButton) {
			removeTimeAway();
		} else if (e.getSource() == addCommitmentButton) {
			addCommitment();
		} else if (e.getSource() == removeCommitmentButton) {
			removeCommitment();
		}
	}
	
	/**
	 * Validates field in clinician form and submits clinician preferences and other entered data.
	 *
	 * @throws SQLException the SQL exception
	 */
	private void submit() throws SQLException {
		
		String clinicianName = nameField.getText().trim();
		int morningRank = ((Integer)morningRankBox.getSelectedItem()).intValue();
		int noonRank = ((Integer)noonRankBox.getSelectedItem()).intValue();
		int afternoonRank = ((Integer)afternoonRankBox.getSelectedItem()).intValue();
		
		Connection conn = ConnectionFactory.getInstance();
		ClinicianDAO clinicianDao = new ClinicianDAO(conn);
		
		int clinicianID = clinicianDao.getClinicianID(clinicianName);
		if (clinicianID == -1) {
			JOptionPane.showMessageDialog(this,
				    "You must enter in a valid clinician name. ",
				    "Adding invalid clinician name",
				    JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (morningRank == noonRank || afternoonRank == morningRank || noonRank == afternoonRank) {
			JOptionPane.showMessageDialog(this,
				    "You must enter unique ranks for each time preference.",
				    "Adding clinician ec preferences",
				    JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		int iaNumHours, ecNumHours;
		if (isAdmin) {
			String myIAHours = iaHours.getText();
			if (myIAHours.isEmpty()) {
				JOptionPane.showMessageDialog(this,
					    "You must enter the assigned number of IA hours",
					    "Adding clinician ia preferences",
					    JOptionPane.ERROR_MESSAGE);
				return;
			}
			try {
				iaNumHours = Integer.parseInt(myIAHours);
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(this,
					    "You must enter a valid integer for the assigned number of IA hours.",
					    "Adding clinician ec preferences",
					    JOptionPane.ERROR_MESSAGE);
				return;
			}
			String myECHours = ecHours.getText();
			if (myECHours.isEmpty()) {
				JOptionPane.showMessageDialog(this,
					    "You must enter the assigned number of EC hours.",
					    "Adding clinician ec preferences",
					    JOptionPane.ERROR_MESSAGE);
				return;
			}
			try {
				ecNumHours = Integer.parseInt(myECHours);
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(this,
					    "You must enter a valid integer for the assigned number of EC hours.",
					    "Adding clinician ec preferences",
					    JOptionPane.ERROR_MESSAGE);
				return;
			}
			
		} else {
			// load values from calendar
			CalendarDAO calendarDAO = new CalendarDAO(conn);
			CalendarBean calendar = calendarDAO.loadCalendar();
			iaNumHours = calendar.getIaMinHours();
			ecNumHours = calendar.getEcMinHours();
		}
		
		ClinicianPreferencesDAO clinicianPreferencesDao = new ClinicianPreferencesDAO(conn);
		ClinicianPreferencesBean preferences = new ClinicianPreferencesBean(clinicianID, morningRank, noonRank, afternoonRank, iaNumHours, ecNumHours);
		ClinicianPreferencesBean existing = clinicianPreferencesDao.loadClinicianPreferences(clinicianID);	

		List<CommitmentBean> allCommitments = new ArrayList<CommitmentBean>();

		for (List<CommitmentBean> list : commitmentList) {
			for (CommitmentBean commitment: list) {
				allCommitments.add(commitment);
			}
		}
		
		ClinicianPreferencesAction action = new ClinicianPreferencesAction(preferences, allCommitments, toTimeAwayList(timeAway.getModel()), conn);
		if (existing == null) {
			action.insertPreferences();
		} else {
			int result = JOptionPane.showConfirmDialog(this,
				    "Are you sure you wish to update your preferences? Your prior preferences will be overwritten.",
				    "Update clinician preferences",
				    JOptionPane.YES_NO_OPTION);
			if (result == JOptionPane.YES_OPTION) {
				action.updatePreferences();
			} else if (result == JOptionPane.NO_OPTION || result == JOptionPane.CLOSED_OPTION) {
				return;
			}
		}
		
		JOptionPane.showMessageDialog(this,
			    "Successfully inserted clinician preferences!",
			    "SUCCESS",
			    JOptionPane.INFORMATION_MESSAGE);
	}
	
	private static List<TimeAwayBean> toTimeAwayList(ListModel<TimeAwayBean> model) {
		List<TimeAwayBean> tsAway = new ArrayList<TimeAwayBean>();
		for (int i = 0; i < model.getSize(); i++) {
			tsAway.add(model.getElementAt(i));
		}
		return tsAway;
	}

	/**
	 * Adds the entered time away to current list of time away, or displays a message if it is invalid.
	 */
	private void addTimeAway() {
		String name = timeAwayName.getText().trim();
		String startDate = timeAwayStartDate.getText().trim();
		String endDate = timeAwayEndDate.getText().trim();
		
		if (name.isEmpty()) {
			JOptionPane.showMessageDialog(this,
				    "You must enter in a description. ",
				    "Adding invalid time away description",
				    JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		try {
			DateRangeValidator.validate(startDate, endDate);
		} catch (InvalidDateRangeException e) {
			JOptionPane.showMessageDialog(this,
				    "Cannot add the time away to the list. " +
				    e.getMessage(),
				    "Adding invalid time away",
				    JOptionPane.ERROR_MESSAGE);
			return;
		}

		timeAwayName.setText("");
		timeAwayStartDate.setText("");
		timeAwayEndDate.setText("");

		Date start, end;
		DefaultListModel<TimeAwayBean> model = (DefaultListModel<TimeAwayBean>) timeAway.getModel();
		try {
			start = DateRangeValidator.parseDate(startDate);
			end = DateRangeValidator.parseDate(endDate);
		} catch (ParseException e) {
			throw new RuntimeException("Should never happen");
		}
		model.add(model.size(), new TimeAwayBean(-1, name, start, end));
	}
	
	/**
	 * Adds the commitment to the current list of commitments, or displays a message if it is invalid.
	 */
	private void addCommitment() {
		String description = commitmentDescription.getText().trim();
		String dayOfWeek = ((String)daysOfWeekBox.getSelectedItem());
		String startTime = ((String)startTimeBox.getSelectedItem());
		String endTime = ((String)endTimeBox.getSelectedItem());
		String frequency = ((String)frequencyBox.getSelectedItem());
		boolean isExternal = externalCommitment.isSelected();
		
		String commitmentString = "";
		if (isExternal) {
			commitmentString += "External ";
		}
		commitmentString += "Meeting: " + description + " "  + frequency + " on " + dayOfWeek + " from " + startTime + " to " + endTime;
		
		if (description.isEmpty()) {
			JOptionPane.showMessageDialog(this,
				    "You must enter in a description. ",
				    "Adding invalid commitment description",
				    JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		commitmentDescription.setText("");
		
		DefaultListModel<String> model = (DefaultListModel<String>) commitments.getModel();
		
		startTime = startTime.replaceAll(" ", "");
		endTime = endTime.replaceAll(" ", "");
		// give half hour for travel before/after
		if (isExternal) {
			if (startTime.contains(":30")) {
				startTime = startTime.replaceAll(":30", ":00");
			} else {
				// decrement hour, and set to :30
				int hour = Integer.parseInt(startTime.substring(0, startTime.indexOf(":"))) - 1;
				if (hour == 11) {
					startTime = startTime.replaceAll("pm", "am");
				}
				startTime = hour + ":30" + startTime.substring(startTime.indexOf('m') - 1);
			}
			if (endTime.contains(":30")) {
				// increment hour and set to :00
				int hour = Integer.parseInt(endTime.substring(0, endTime.indexOf(":"))) + 1;
				if (hour == 12) {
					endTime = endTime.replaceAll("am", "pm");
				} else if (hour > 12) {
					hour -= 12;
				}
				endTime = hour + ":00" + endTime.substring(endTime.indexOf('m') - 1);
			} else {
				endTime = endTime.replaceAll(":00", ":30");
			}
		}
		
		int startHour = parseTime(startTime, false);
		int endHour = parseTime(endTime, true);
		List<CommitmentBean> list = new ArrayList<CommitmentBean>();
		// from start date to end date, go through and add commitments for these
		List<Date> meetingDates;
		if (frequency.equals("Weekly")) {
			meetingDates = ImportClinicianMeetingsAction.getMeetingDatesWeekly(startDate, endDate, 7, dayOfWeek);
		} else if (frequency.equals("Biweekly")) {
			meetingDates = ImportClinicianMeetingsAction.getMeetingDatesWeekly(startDate, endDate, 14, dayOfWeek);
		} else {
			meetingDates = ImportClinicianMeetingsAction.getMeetingDatesMonthly(startDate, endDate, 1, dayOfWeek);
		}
		for (Date meetingDate : meetingDates) {
			list.add(new CommitmentBean(-1, startHour, endHour, meetingDate, description));
		}
		commitmentList.add(list);
		model.add(model.size(), commitmentString);
	}
	
	private int parseTime(String timeString, boolean roundUp) {
		int colonIndex = timeString.indexOf(":");
		int time = Integer.parseInt(timeString.substring(0, colonIndex));
		if (roundUp && timeString.contains(":30")) {
			time = (Integer.parseInt(timeString.substring(0, colonIndex)) + 1);
		}
		if (timeString.contains("pm") && time < 12) {
			time += 12;
		}
		return time;
	}
	
	/**
	 * Removes the time away from the current list.
	 */
	private void removeTimeAway() {
		int index = timeAway.getSelectedIndex();
		if (index >= 0) {
			DefaultListModel<TimeAwayBean> oldModel = (DefaultListModel<TimeAwayBean>) timeAway.getModel();
			oldModel.remove(index);
		}
	}
	
	/**
	 * Removes the commitment from the current list.
	 */
	private void removeCommitment() {
		int index = commitments.getSelectedIndex();
		if (index >= 0) {
			DefaultListModel<String> oldModel = (DefaultListModel<String>) commitments.getModel();
			oldModel.remove(index);
			commitmentList.remove(index);
		}
	}
	
	/**
	 * Clear all fields in this form.
	 */
	private void clearFields() {
		nameField.setText("");
		timeAwayName.setText("");
		timeAwayStartDate.setText("");
		timeAwayEndDate.setText("");
		commitmentDescription.setText("");
		((DefaultListModel<String>)commitments.getModel()).clear();
		commitmentList.clear();
		((DefaultListModel<TimeAwayBean>)timeAway.getModel()).clear();
		this.repaint();
	}
	
	private class Commitment {
		int id;
		int startHour;
		int endHour;
		String description;
		Weekday dayOfWeek;
		
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
	}
	
	private List<List<CommitmentBean>> getCommitmentList(List<CommitmentBean> commitments) {
		Map<Commitment, List<CommitmentBean>> map = new HashMap<Commitment, List<CommitmentBean>>();
		for (CommitmentBean commitment : commitments) {
			Date date = commitment.getDate();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			int day = calendar.get(Calendar.DAY_OF_WEEK);
			Weekday dayOfWeek = Weekday.values()[day - 2];
			Commitment current = new Commitment(commitment.getClinicianID(), commitment.getStartHour(), commitment.getEndHour(), commitment.getDescription(), dayOfWeek);
			if (map.containsKey(current)) {
				List<CommitmentBean> list = map.get(current);
				list.add(commitment);
			} else {
				List<CommitmentBean> list = new ArrayList<CommitmentBean>();
				list.add(commitment);
				map.put(current, list);
			}
		}
		List<List<CommitmentBean>> commitmentList = new ArrayList<List<CommitmentBean>>();
		for (List<CommitmentBean> list : map.values()) {
			Collections.sort(list, new Comparator<CommitmentBean>() {
				@Override
				public int compare(CommitmentBean arg0, CommitmentBean arg1) {
					return arg0.getDate().compareTo(arg1.getDate());
				}
			});
			commitmentList.add(list);
		}
		return commitmentList;
	}
	
	private List<String> getCommitmentStrings(List<List<CommitmentBean>> commitmentList) {
		List<String> commitmentStrings = new ArrayList<String>();
		for (List<CommitmentBean> list : commitmentList) {
			CommitmentBean b1 = list.get(0);

			String frequency = null;
			if (list.size() == 2) {
				CommitmentBean b2 = list.get(1);
				long diff = b2.getDate().getTime() - b1.getDate().getTime();
				long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
				if (days == 7) {
					frequency = "Weekly";
				} else if (days == 14) {
					frequency = "Biweekly";
				} else {
					frequency = "Monthly";
				}
			}
			Date date = b1.getDate();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			int day = calendar.get(Calendar.DAY_OF_WEEK);
			Weekday dayOfWeek = Weekday.values()[day - 2];

			String commitmentString;
			if (frequency != null) {
				commitmentString = "Meeting: " + b1.getDescription() + " "  + frequency + " on " + dayOfWeek + " from " + b1.getStartHour() + " to " + b1.getEndHour();
			} else {
				int year = calendar.get(Calendar.YEAR);
				commitmentString = "Meeting: " + b1.getDescription() + " on " + DateRangeValidator.formatDateLong(date) + ", " + year +  " from " + b1.getStartHour() + " to " + b1.getEndHour();;
			}
			commitmentStrings.add(commitmentString);
			continue;
		}
		
		return commitmentStrings;
	}
	
	private void loadPreferences() throws SQLException {
		Connection conn = ConnectionFactory.getInstance();
		ClinicianDAO clinicianDAO = new ClinicianDAO(conn);
		CommitmentsDAO commitmentsDAO = new CommitmentsDAO(conn);
		TimeAwayDAO timeAwayDAO = new TimeAwayDAO(conn);
		ClinicianPreferencesDAO clinicianPreferencesDAO = new ClinicianPreferencesDAO(conn);
		int clinicianID = clinicianDAO.getClinicianID(clinicianName);
		List<CommitmentBean> cmtList = commitmentsDAO.loadCommitments(clinicianID);
		commitmentList = getCommitmentList(cmtList);
		List<String> commitmentStrings = getCommitmentStrings(commitmentList);
		DefaultListModel<String> commitmentsModel = (DefaultListModel<String>)commitments.getModel();
		for (String commitment : commitmentStrings) {
			commitmentsModel.addElement(commitment);
		}
		List<TimeAwayBean> timesAway = timeAwayDAO.loadTimeAway(clinicianID);
		ClinicianPreferencesBean preferences = clinicianPreferencesDAO.loadClinicianPreferences(clinicianID);
		nameField.setText(clinicianName);
		DefaultListModel<TimeAwayBean> timeAwayModel = (DefaultListModel<TimeAwayBean>)timeAway.getModel();
		for (TimeAwayBean timeAway : timesAway) {
			timeAwayModel.addElement(timeAway);
		}
		morningRankBox.setSelectedIndex(preferences.getMorningRank() - 1);
		noonRankBox.setSelectedIndex(preferences.getNoonRank() - 1);
		afternoonRankBox.setSelectedIndex(preferences.getAfternoonRank() - 1);
		iaHours.setText("" + preferences.getIAHours());
		ecHours.setText("" + preferences.getECHours());
	}
}
