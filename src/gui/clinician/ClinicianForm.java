package gui.clinician;

import java.awt.Dimension;
import java.awt.Font;
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
public class ClinicianForm extends JPanel implements ActionListener {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -5377691259929030865L;

    /** The commitments pane. */
    private JScrollPane timeAwayPane, commitmentsPane;

    /** The time away. */
    private JList<TimeAwayBean> timeAway;

    /** The commitments. */
    private JList<String> commitments;

    /** The time away end date. */
    private JTextField timeAwayName, timeAwayStartDate, timeAwayEndDate;

    /** The name field. */
    private JTextField commitmentDescription, nameField;

    /** The ec hours. */
    private JTextField iaHours, ecHours;

    /** The time away label. */
    private JLabel nameLabel, preferenceFormLabel, periodLabel, timeAwayLabel;

    /** The time away end date label. */
    private JLabel timeAwayNameLabel, timeAwayStartDateLabel,
	    timeAwayEndDateLabel;

    /** The commitment frequency label. */
    private JLabel commitmentStartTimeLabel, commitmentEndTimeLabel,
	    commitmentFrequencyLabel;

    /** The external commitment label. */
    private JLabel commitmentDayLabel, commitmentDescriptionLabel,
	    externalCommitmentLabel;

    /** The rank label. */
    private JLabel timeLabel, rankLabel;

    /** The afternoon label. */
    private JLabel morningLabel, noonLabel, afternoonLabel;

    /** The ec times label. */
    private JLabel ecPreferencesLabel, conflictsLabel, iaTimesLabel,
	    ecTimesLabel;

    /** The ec hours label. */
    private JLabel iaHoursLabel, ecHoursLabel;

    /** The conflicts description. */
    private JTextArea timeAwayDescription, ecPreferencesDescription,
	    conflictsDescription;

    /** The remove time away button. */
    private JButton addTimeAwayButton, removeTimeAwayButton;

    /** The remove commitment button. */
    private JButton addCommitmentButton, removeCommitmentButton;

    /** The submit button. */
    private JButton clearButton, submitButton;

    /** The frequency box. */
    private JComboBox<String> daysOfWeekBox, startTimeBox, endTimeBox,
	    frequencyBox;

    /** The afternoon rank box. */
    private JComboBox<Integer> morningRankBox, noonRankBox, afternoonRankBox;

    /** The external commitment. */
    private JCheckBox externalCommitment;

    /** The commitment list. */
    private List<List<CommitmentBean>> commitmentList;

    /** The semester. */
    private Semester semester;

    /** The year. */
    private int year;

    /** The date range. */
    private DateRange dateRange;

    /** The is admin. */
    private boolean isAdmin;

    /** The clinician name. */
    private String clinicianName;

    /**
     * Instantiates a new clinician form.
     *
     * @param semester
     *            the semester
     * @param year
     *            the year
     * @param dateRange
     *            the date range
     * @param isAdmin
     *            the is admin
     * @param name
     *            the name
     */
    public ClinicianForm(Semester semester, int year, DateRange dateRange,
	    boolean isAdmin, String name) {
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
	initializePanel();
    }

    /**
     * Initialize components.
     */
    private void initializeComponents() {
	initializeTextFields();
	initializeLabels();
	initializeTextAreas();
	initializeButtons();
	initializeScrollPanes();
	initializeComboBoxes();
    }

    /**
     * Initialize text fields.
     */
    private void initializeTextFields() {
	nameField = new JTextField(16);
	nameField.setName("nameField");
	timeAwayName = new JTextField(16);
	timeAwayName.setName("timeAwayName");
	timeAwayStartDate = new JTextField(8);
	timeAwayStartDate.setName("timeAwayStartDate");
	timeAwayEndDate = new JTextField(8);
	timeAwayEndDate.setName("timeAwayEndDate");
	commitmentDescription = new JTextField(16);
	commitmentDescription.setName("commitmentDescription");
	if (isAdmin) {
	    iaHours = new JTextField(8);
	    iaHours.setName("iaHours");
	    ecHours = new JTextField(8);
	    ecHours.setName("ecHours");
	}
    }

    /**
     * Initialize labels.
     */
    private void initializeLabels() {
	nameLabel = new JLabel("NAME:");
	preferenceFormLabel = new JLabel(semester.name() + " " + year
		+ " IA/EC Preference Form");
	Font f = preferenceFormLabel.getFont().deriveFont(Font.BOLD)
		.deriveFont(16f);
	preferenceFormLabel.setFont(f);
	periodLabel = new JLabel("<html>This covers the period from <b>"
		+ DateRangeValidator.formatDateLong(dateRange.getStartDate())
		+ " through "
		+ DateRangeValidator.formatDateLong(dateRange.getEndDate())
		+ ", " + year + ".</b></html>");
	timeAwayLabel = new JLabel(semester.name() + " Semester " + year
		+ " \"Time Away\" Plans");
	f = timeAwayLabel.getFont().deriveFont(Font.BOLD);
	timeAwayLabel.setFont(f);
	timeAwayNameLabel = new JLabel("Description");
	timeAwayStartDateLabel = new JLabel("Start Date");
	timeAwayEndDateLabel = new JLabel("End Date");
	conflictsLabel = new JLabel(
		"Initial Appointment / Emergency Coverage Conflicts");
	f = conflictsLabel.getFont().deriveFont(Font.BOLD);
	conflictsLabel.setFont(f);
	iaTimesLabel = new JLabel(
		"We offer IAs at 11:00 a.m., 1:00 p.m., 2:00 p.m. and 3:00 p.m. during the "
			+ semester + " Semester.");
	ecTimesLabel = new JLabel(
		"1-Hour Emergency Coverage shifts are at 8:00 a.m., Noon, and 4:00 p.m. every day.");
	commitmentStartTimeLabel = new JLabel("Start Time");
	commitmentEndTimeLabel = new JLabel("End Time");
	commitmentFrequencyLabel = new JLabel("Frequency");
	externalCommitmentLabel = new JLabel("External");
	commitmentDayLabel = new JLabel("Day of Week");
	commitmentDescriptionLabel = new JLabel("Activity or Meeting");
	ecPreferencesLabel = new JLabel("E.C. Preferences");
	f = ecPreferencesLabel.getFont().deriveFont(Font.BOLD);
	ecPreferencesLabel.setFont(f);
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
     * Initialize text areas.
     */
    private void initializeTextAreas() {
	Font font = nameLabel.getFont();
	timeAwayDescription = new JTextArea(
		"Please list the dates/days/times etc. that you know you will be away from the Center."
			+ " Please include VACATIONS, CONFERENCES, SICK DAYS, JURY DUTY or any other circumstances that will take you out of the Center.");
	initializeTextArea(timeAwayDescription, font);
	conflictsDescription = new JTextArea(
		"Please indicate \"impossible\" shift times due to other activities, such as groups and meetings/committees."
			+ " You do not need to list vacation/conferences again, since you have already listed them above.");
	initializeTextArea(conflictsDescription, font);
	ecPreferencesDescription = new JTextArea(
		"Please rank your preferences for E.C. shifts. We will try to take your preferences"
			+ " into account but we cannot guarantee they will be reflected in your schedule.");
	initializeTextArea(ecPreferencesDescription, font);
    }

    /**
     * Setup textarea to mimic jlabel.
     *
     * @param textArea
     *            the text area
     * @param font
     *            the font
     */
    private void initializeTextArea(JTextArea textArea, Font font) {
	textArea.setLineWrap(true);
	textArea.setWrapStyleWord(true);
	textArea.setSize(580, 200);
	textArea.setEditable(false);
	textArea.setBackground(getBackground());
	textArea.setFont(font);
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
	    String midHour = hour.replaceAll(":00", ":30");
	    startTimeBox.addItem(hour);
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
     * Initialize panel.
     */
    private void initializePanel() {

	setLayout(new MigLayout("gap unrel", "grow"));
	setPreferredSize(new Dimension(700, 800));
	add(nameLabel, "align label, split 2, span");
	add(nameField, "wrap");
	add(preferenceFormLabel, "align center, span, wrap");
	add(periodLabel, "align center, gapbottom 15, span, wrap");

	addTimeAwayComponents();
	addCommitmentComponents();
	addPreferenceComponents();
	if (isAdmin) {
	    addAdminComponents();
	}

	add(clearButton,
		"split 2, span, gap top 25, align center, sizegroup bttn");
	add(submitButton, "wrap, gapleft 50, sizegroup bttn");
    }

    /**
     * Adds the time away components.
     */
    private void addTimeAwayComponents() {
	add(timeAwayLabel, "align center, span, wrap");
	add(timeAwayDescription, "align center, span, wrap");
	add(timeAwayNameLabel, "align label, split 2");
	add(timeAwayName);
	add(timeAwayStartDateLabel, "align label, split 2");
	add(timeAwayStartDate);
	add(timeAwayEndDateLabel, "align label, split 2");
	add(timeAwayEndDate, "wrap");
	add(timeAwayPane, "grow, span, wrap");
	add(addTimeAwayButton,
		"split 2, span, align center, sizegroup timeBttn");
	add(removeTimeAwayButton, "wrap, gapleft 50, sizegroup timeBttn");
    }

    /**
     * Adds the commitment components.
     */
    private void addCommitmentComponents() {
	add(conflictsLabel, "align center, span, wrap");
	add(iaTimesLabel, "align center, span, wrap");
	add(ecTimesLabel, "align center, span, wrap");
	add(conflictsDescription, "align center, span, wrap");
	add(commitmentDescriptionLabel, "split 2");
	add(commitmentDescription);
	add(commitmentStartTimeLabel, "split 2");
	add(startTimeBox);
	add(commitmentEndTimeLabel, "split 2");
	add(endTimeBox, "wrap");
	add(commitmentDayLabel, "split 2");
	add(daysOfWeekBox);
	add(commitmentFrequencyLabel, "split 2");
	add(frequencyBox);
	add(externalCommitmentLabel, "split 2");
	add(externalCommitment, "wrap");
	add(commitmentsPane, "grow, span, wrap");
	add(addCommitmentButton,
		"split 2, span, align center, sizegroup commitmentBttn");
	add(removeCommitmentButton,
		"wrap, gapleft 50, sizegroup commitmentBttn");
    }

    /**
     * Adds the preference components.
     */
    private void addPreferenceComponents() {
	add(ecPreferencesLabel, "align center, span, wrap");
	add(ecPreferencesDescription, "align center, span, wrap");
	add(timeLabel, "align label, split 4, span, growx 25");
	add(morningLabel, "growx 25");
	add(noonLabel, "growx 25");
	add(afternoonLabel, "wrap, growx 25");
	add(rankLabel, "align label, growx 23, split 4, span");
	add(morningRankBox, "growx 25");
	add(noonRankBox, "growx 25");
	add(afternoonRankBox, "growx 25, wrap");
    }

    /**
     * Adds the admin components.
     */
    private void addAdminComponents() {
	add(iaHoursLabel, "align label, split 2, span");
	add(iaHours, "wrap");
	add(ecHoursLabel, "align label, split 2, span");
	add(ecHours, "wrap");
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
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
     * Validates the data in the form and submits if valid, otherwise displays
     * error message for invalid field. If preferences already set for this
     * clinician, prompts if you wish to overwrite these preferences.
     *
     * @throws SQLException
     *             the SQL exception
     * @throws InvalidFormDataException
     *             the invalid form data exception
     */
    private void submit() throws SQLException, InvalidFormDataException {
	String clinicianName = nameField.getText().trim();
	int morningRank = ((Integer) morningRankBox.getSelectedItem())
		.intValue();
	int noonRank = ((Integer) noonRankBox.getSelectedItem()).intValue();
	int afternoonRank = ((Integer) afternoonRankBox.getSelectedItem())
		.intValue();
	String myIAHours = null;
	String myECHours = null;
	if (isAdmin) {
	    myIAHours = iaHours.getText();
	    myECHours = ecHours.getText();
	}

	ClinicianPreferencesBean preferences = ClinicianFormValidator
		.validatePreferences(clinicianName, morningRank, noonRank,
			afternoonRank, myIAHours, myECHours, isAdmin);
	Connection conn = ConnectionFactory.getInstance();
	ClinicianFormAction action = new ClinicianFormAction(conn, preferences,
		commitmentList, timeAway.getModel());
	boolean isUpdate = action.willOverwritePreferences();
	if (isUpdate) {
	    int result = JOptionPane
		    .showConfirmDialog(
			    this,
			    "Are you sure you wish to update your preferences? Your prior preferences will be overwritten.",
			    "Update clinician preferences",
			    JOptionPane.YES_NO_OPTION);
	    if (result == JOptionPane.NO_OPTION
		    || result == JOptionPane.CLOSED_OPTION
		    || result == JOptionPane.CANCEL_OPTION) {
		return;
	    }
	}
	action.submit(isUpdate);
    }

    /**
     * Displays a helpful db error message.
     *
     * @param e
     *            the SQLException
     */
    private void displayDBErrorMessage(SQLException e) {
	JOptionPane
		.showMessageDialog(
			this,
			"Failed to connect to the remote SQL database; please contact the network administrator.",
			"Database connection error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Displays a helpful form data error message.
     *
     * @param e
     *            the InvalidFormDataException
     */
    private void displayFormDataErrorMessage(InvalidFormDataException e) {
	JOptionPane.showMessageDialog(this, e.getMessage(), e.getContext(),
		JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Adds the entered time away to current list of time away, or displays a
     * message if it is invalid.
     */
    private void addTimeAway() {
	String name = timeAwayName.getText().trim();
	String startDate = timeAwayStartDate.getText().trim();
	String endDate = timeAwayEndDate.getText().trim();

	try {
	    TimeAwayBean timeAwayBean = ClinicianFormValidator
		    .validateTimeAway(name, startDate, endDate);
	    DefaultListModel<TimeAwayBean> model = (DefaultListModel<TimeAwayBean>) timeAway
		    .getModel();
	    model.add(model.size(), timeAwayBean);
	    clearTimeAwayFields();
	} catch (InvalidFormDataException e) {
	    displayFormDataErrorMessage(e);
	}
    }

    /**
     * Adds the commitment to the current list of commitments, or displays a
     * message if it is invalid.
     */
    private void addCommitment() {
	String description = commitmentDescription.getText().trim();
	String dayOfWeek = ((String) daysOfWeekBox.getSelectedItem());
	String startTime = ((String) startTimeBox.getSelectedItem());
	String endTime = ((String) endTimeBox.getSelectedItem());
	String frequency = ((String) frequencyBox.getSelectedItem());
	boolean isExternal = externalCommitment.isSelected();

	try {
	    String commitmentString = description + ": "
		    + (!frequency.isEmpty() ? frequency + " on " : "")
		    + dayOfWeek + " from " + startTime + " to " + endTime;
	    if (isExternal) {
		commitmentString += " [External]";
	    }
	    List<CommitmentBean> list = ClinicianFormValidator
		    .validateCommitment(dateRange, description, startTime,
			    endTime, dayOfWeek, frequency, isExternal);
	    commitmentList.add(list);
	    DefaultListModel<String> model = (DefaultListModel<String>) commitments
		    .getModel();
	    model.add(model.size(), commitmentString);
	    clearCommitmentFields();
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
	    DefaultListModel<TimeAwayBean> oldModel = (DefaultListModel<TimeAwayBean>) timeAway
		    .getModel();
	    oldModel.remove(index);
	}
    }

    /**
     * Removes the commitment from the current list.
     */
    private void removeCommitment() {
	int index = commitments.getSelectedIndex();
	if (index >= 0) {
	    DefaultListModel<String> oldModel = (DefaultListModel<String>) commitments
		    .getModel();
	    oldModel.remove(index);
	    commitmentList.remove(index);
	}
    }

    /**
     * Clear all fields in this form.
     */
    private void clearFields() {
	nameField.setText("");
	clearTimeAwayFields();
	((DefaultListModel<TimeAwayBean>) timeAway.getModel()).clear();
	clearCommitmentFields();
	((DefaultListModel<String>) commitments.getModel()).clear();
	commitmentList.clear();
	clearPreferenceFields();
	if (isAdmin) {
	    clearAdminFields();
	}
	this.repaint();
    }

    /**
     * Clear admin fields.
     */
    private void clearAdminFields() {
	iaHours.setText("");
	ecHours.setText("");
    }

    /**
     * Clear time away fields.
     */
    private void clearTimeAwayFields() {
	timeAwayName.setText("");
	timeAwayStartDate.setText("");
	timeAwayEndDate.setText("");
    }

    /**
     * Clear commitment fields.
     */
    private void clearCommitmentFields() {
	commitmentDescription.setText("");
	externalCommitment.setSelected(false);
	daysOfWeekBox.setSelectedIndex(0);
	startTimeBox.setSelectedIndex(0);
	endTimeBox.setSelectedIndex(0);
	frequencyBox.setSelectedIndex(0);
    }

    /**
     * Clear preference fields.
     */
    private void clearPreferenceFields() {
	morningRankBox.setSelectedIndex(0);
	noonRankBox.setSelectedIndex(0);
	afternoonRankBox.setSelectedIndex(0);
    }

    /**
     * Loads the preferences for the clinician with clinicianName from the
     * database and populates this form with them.
     *
     * @throws SQLException
     *             the SQL exception
     */
    private void loadPreferences() throws SQLException {
	Connection conn = ConnectionFactory.getInstance();
	ClinicianLoadPreferencesAction action = new ClinicianLoadPreferencesAction(
		conn, clinicianName);
	ClinicianPreferencesBean preferences = action
		.loadClinicianPreferences();
	List<TimeAwayBean> timesAway = action.loadTimesAway();
	commitmentList = action.loadCommitments();
	List<String> commitmentStrings = action
		.loadCommitmentDescriptions(commitmentList);

	// populate form with data
	DefaultListModel<String> commitmentsModel = (DefaultListModel<String>) commitments
		.getModel();
	for (String commitment : commitmentStrings) {
	    commitmentsModel.addElement(commitment);
	}
	nameField.setText(clinicianName);
	DefaultListModel<TimeAwayBean> timeAwayModel = (DefaultListModel<TimeAwayBean>) timeAway
		.getModel();
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
