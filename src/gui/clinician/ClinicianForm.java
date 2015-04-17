package gui.clinician;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

import net.miginfocom.swing.MigLayout;
import validator.ClinicianFormValidator;
import validator.DateRangeValidator;
import action.ClinicianFormAction;
import action.ClinicianLoadPreferencesAction;
import action.InvalidFormDataException;
import bean.ClinicianPreferencesBean;
import bean.CommitmentBean;
import bean.DateRange;
import bean.OperatingHours;
import bean.Semester;
import bean.TimeAwayBean;
import bean.Weekday;
import dao.ConnectionFactory;

/**
 * The Class ClinicianForm.
 *
 * @author Yusheng Hou and Kevin Lim
 */
public class ClinicianForm extends JFrame implements ActionListener {

	private static final long serialVersionUID = -5377691259929030865L;
	
	private JPanel panel;
	private JScrollPane timeAwayPane, commitmentsPane;
	private JList<TimeAwayBean> timeAway;
	private JList<String> commitments;
	private JTextField timeAwayName, timeAwayStartDate, timeAwayEndDate;
	private JTextField commitmentDescription, nameField;
	private JTextField iaHours, ecHours;
	private JLabel nameLabel, preferenceFormLabel, periodLabel, timeAwayLabel;
	private JLabel timeAwayNameLabel, timeAwayStartDateLabel, timeAwayEndDateLabel;
	private JLabel commitmentStartTimeLabel, commitmentEndTimeLabel, commitmentFrequencyLabel;
	private JLabel commitmentDayLabel, commitmentDescriptionLabel, externalCommitmentLabel;
	private JLabel timeLabel, rankLabel;
	private JLabel morningLabel, noonLabel, afternoonLabel;
	private JLabel ecPreferencesLabel, conflictsLabel, iaTimesLabel, ecTimesLabel;
	private JLabel iaHoursLabel, ecHoursLabel;
	private JTextArea timeAwayDescription, ecPreferencesDescription, conflictsDescription;
	private JButton addTimeAwayButton, removeTimeAwayButton;
	private JButton addCommitmentButton, removeCommitmentButton;
	private JButton clearButton, submitButton;
	private JComboBox<String> daysOfWeekBox, startTimeBox, endTimeBox, frequencyBox;
	private JComboBox<Integer> morningRankBox, noonRankBox, afternoonRankBox;
	private JCheckBox externalCommitment;
	
	private List<List<CommitmentBean>> commitmentList;
	private Semester semester;
	private int year;
	private DateRange dateRange;
	private boolean isAdmin;
	private String clinicianName;

	/**
	 * Instantiates a new clinician form.
	 *
	 * @param semester the semester
	 * @param year the year
	 * @param dateRange the date range
	 * @param isAdmin the is admin
	 * @param name the name
	 */
	public ClinicianForm(Semester semester, int year, DateRange dateRange, boolean isAdmin, String name) {
		super("Clinician Input Form");
		panel = new JPanel();
		panel.setLayout(new MigLayout("gap rel", "grow"));
		this.semester = semester;
		this.year = year;
		this.dateRange = dateRange;
		this.isAdmin = isAdmin;
		this.clinicianName = name;
		commitmentList = new ArrayList<List<CommitmentBean>>();
		initializeComponents();
		if (isAdmin) {
			try {
				loadPreferences();
			} catch (SQLException e) {
				displayDBErrorMessage(e);
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
		nameField = new JTextField(20);
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
		periodLabel = new JLabel("This covers the period from " + DateRangeValidator.formatDateLong(dateRange.getStartDate())
				+ " through " + DateRangeValidator.formatDateLong(dateRange.getEndDate()) + ", " + year + ".");
		timeAwayLabel = new JLabel(semester.name() + " Semester " + year + " \"Time Away\" Plans");
		timeAwayDescription = new JTextArea("Please list the dates/days/times etc. that you know you will be away from the Center." + 
		" Please include VACATIONS, CONFERENCES, SICK DAYS, JURY DUTY or any other circumstances that will take you out of the Center.");
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
		conflictsDescription = new JTextArea("Please indicate \"impossible\" shift times due to other activities, such as groups and meetings/committees."
				+ " You do not need to list vacation/conferences again, since you have already listed them above.");
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
		ecPreferencesDescription = new JTextArea("Please rank your preferences for E.C. shifts. We will try to take your preferences"
				+ " into account but we cannot guarantee they will be reflected in your schedule.");
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
	
	/**
	 * Adds the admin components.
	 */
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
				displayDBErrorMessage(e1);
			} catch (InvalidFormDataException e1) {
				displayFormDataErrorMessage(e1);
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
	 * Validates the data in the form and submits if valid, otherwise displays error message for invalid field.
	 * If preferences already set for this clinician, prompts if you wish to overwrite these preferences.
	 *
	 * @throws SQLException the SQL exception
	 * @throws InvalidFormDataException the invalid form data exception
	 */
	private void submit() throws SQLException, InvalidFormDataException {
		String clinicianName = nameField.getText().trim();
		int morningRank = ((Integer)morningRankBox.getSelectedItem()).intValue();
		int noonRank = ((Integer)noonRankBox.getSelectedItem()).intValue();
		int afternoonRank = ((Integer)afternoonRankBox.getSelectedItem()).intValue();
		String myIAHours = null;
		String myECHours = null;
		if (isAdmin) {
			myIAHours = iaHours.getText();
			myECHours = ecHours.getText();
		}
		
		ClinicianPreferencesBean preferences = ClinicianFormValidator.validatePreferences(clinicianName, morningRank, noonRank, afternoonRank, myIAHours, myECHours, isAdmin);
		Connection conn = ConnectionFactory.getInstance();
		ClinicianFormAction action = new ClinicianFormAction(conn, preferences, commitmentList, timeAway.getModel());
		boolean isUpdate = action.willOverwritePreferences();
		if (isUpdate) {
			int result = JOptionPane.showConfirmDialog(this,
				    "Are you sure you wish to update your preferences? Your prior preferences will be overwritten.",
				    "Update clinician preferences",
				    JOptionPane.YES_NO_OPTION);
			if (result == JOptionPane.NO_OPTION || result == JOptionPane.CLOSED_OPTION || result == JOptionPane.CANCEL_OPTION) {
				return;
			}
		}
		action.submit(isUpdate);
	}
	
	/**
	 * Displays a helpful db error message.
	 *
	 * @param e the SQLException
	 */
	private void displayDBErrorMessage(SQLException e) {
		JOptionPane.showMessageDialog(this,
			    e.getMessage(),
			    "Context",
			    JOptionPane.ERROR_MESSAGE);
	}
	
	/**
	 * Displays a helpful form data error message.
	 *
	 * @param e the InvalidFormDataException
	 */
	private void displayFormDataErrorMessage(InvalidFormDataException e) {
		JOptionPane.showMessageDialog(this,
			    e.getMessage(),
			    e.getContext(),
			    JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Adds the entered time away to current list of time away, or displays a message if it is invalid.
	 */
	private void addTimeAway() {
		String name = timeAwayName.getText().trim();
		String startDate = timeAwayStartDate.getText().trim();
		String endDate = timeAwayEndDate.getText().trim();
		
		try {
			TimeAwayBean timeAwayBean = ClinicianFormValidator.validateTimeAway(name, startDate, endDate);
			DefaultListModel<TimeAwayBean> model = (DefaultListModel<TimeAwayBean>) timeAway.getModel();
			model.add(model.size(), timeAwayBean);
			timeAwayName.setText("");
			timeAwayStartDate.setText("");
			timeAwayEndDate.setText("");
		} catch (InvalidFormDataException e) {
			displayFormDataErrorMessage(e);
		}
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
		
		try {
			String commitmentString = "";
			if (isExternal) {
				commitmentString += "External ";
			}
			commitmentString += "Meeting: " + description + " "  + frequency + " on " + dayOfWeek + " from " + startTime + " to " + endTime;
			List<CommitmentBean> list = ClinicianFormValidator.validateCommitment(dateRange, description, startTime, endTime, dayOfWeek, frequency, isExternal);
			commitmentDescription.setText("");
			commitmentList.add(list);
			DefaultListModel<String> model = (DefaultListModel<String>) commitments.getModel();
			model.add(model.size(), commitmentString);
		} catch (InvalidFormDataException e) {
			displayFormDataErrorMessage(e);
		}
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
	
	/**
	 * Loads the preferences for the clinician with clinicianName from the database and populates this form with them.
	 *
	 * @throws SQLException the SQL exception
	 */
	private void loadPreferences() throws SQLException {
		Connection conn = ConnectionFactory.getInstance();
		ClinicianLoadPreferencesAction action = new ClinicianLoadPreferencesAction(conn, clinicianName);
		ClinicianPreferencesBean preferences = action.loadClinicianPreferences();
		List<TimeAwayBean> timesAway = action.loadTimesAway();
		commitmentList = action.loadCommitments();
		List<String> commitmentStrings = action.loadCommitmentDescriptions(commitmentList);
		
		// populate form with data
		DefaultListModel<String> commitmentsModel = (DefaultListModel<String>)commitments.getModel();
		for (String commitment : commitmentStrings) {
			commitmentsModel.addElement(commitment);
		}
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
