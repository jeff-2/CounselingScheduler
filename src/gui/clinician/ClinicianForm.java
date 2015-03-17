package gui.clinician;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
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

import bean.ClinicianPreferencesBean;
import bean.CommitmentBean;
import bean.OperatingHours;
import bean.TimeAwayBean;
import bean.Utility;
import bean.Weekday;
import action.ClinicianPreferencesAction;
import dao.ClinicianDAO;
import dao.ClinicianPreferencesDAO;
import dao.ConnectionFactory;
import validator.DateRangeValidator;
import validator.InvalidDateRangeException;
import net.miginfocom.swing.MigLayout;

/**
 * The Class ClinicianForm.
 *
 * @author Yusheng Hou and Kevin Lim
 */
public class ClinicianForm extends JFrame implements ActionListener {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -5377691259929030865L;
	
	/** The panel. */
	private JPanel panel;
	
	/** The name label. */
	private JLabel nameLabel, preferenceFormLabel, periodLabel, timeAwayLabel;
	
	/** The commitments pane. */
	private JScrollPane timeAwayPane, commitmentsPane;
	
	/** The time away. */
	private JList<TimeAwayBean> timeAway;
	
	/** The commitments. */
	private JList<CommitmentBean> commitments;
	
	/** The time away end date. */
	private JTextField timeAwayName, timeAwayStartDate, timeAwayEndDate;
	
	/** The commitment description. */
	private JTextField commitmentDescription;
	
	/** The time away end date label. */
	private JLabel timeAwayNameLabel, timeAwayStartDateLabel, timeAwayEndDateLabel;
	
	/** The commitment description label. */
	private JLabel commitmentHourLabel, commitmentDayLabel, commitmentDescriptionLabel;
	
	/** The rank label. */
	private JLabel timeLabel, rankLabel;
	
	/** The afternoon label. */
	private JLabel morningLabel, noonLabel, afternoonLabel;
	
	/** The remove time away button. */
	private JButton addTimeAwayButton, removeTimeAwayButton;
	
	/** The remove commitment button. */
	private JButton addCommitmentButton, removeCommitmentButton;
	
	/** The submit button. */
	private JButton clearButton, submitButton;
	
	/** The operating hours box. */
	private JComboBox<String> daysOfWeekBox, operatingHoursBox;
	
	/** The afternoon rank box. */
	private JComboBox<Integer> morningRankBox, noonRankBox, afternoonRankBox;
	
	/** The name field. */
	private JTextField nameField;
	
	/** The Constant NAME_LENGTH. */
	private static final int NAME_LENGTH = 20;

	/**
	 * Instantiates a new clinician form.
	 */
	public ClinicianForm() {
		super("Clinician Input Form");
		panel = new JPanel();
		panel.setLayout(new MigLayout("gap rel", "grow"));
		initializeComponents();
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
	}
	
	/**
	 * Initialize labels.
	 */
	private void initializeLabels() {
		nameLabel = new JLabel("NAME:");
		preferenceFormLabel = new JLabel( "Spring 2015 IA/EC Preference Form");
		periodLabel = new JLabel("This covers the period from 1/19/2015-5/10/2015.");
		timeAwayLabel = new JLabel("Spring Semester 2015 \"Time Away\" Plans");
		timeAwayNameLabel = new JLabel("Description");
		timeAwayStartDateLabel = new JLabel("Start Date");
		timeAwayEndDateLabel = new JLabel("End Date");
		commitmentHourLabel = new JLabel("Hour");
		commitmentDayLabel = new JLabel("Day");
		commitmentDescriptionLabel = new JLabel("Description");
		timeLabel = new JLabel("Time");
		rankLabel = new JLabel("Rank");
		morningLabel = new JLabel("8:00am");
		noonLabel = new JLabel("12:00pm");
		afternoonLabel = new JLabel("4:00pm");
	}
	
	/**
	 * Initialize buttons.
	 */
	private void initializeButtons() {
		addTimeAwayButton = new JButton("Add Time Away");
		addTimeAwayButton.setName("addTimeAwayButton");
		addTimeAwayButton.addActionListener(this);
		removeTimeAwayButton = new JButton("Remove Time Away");
		removeTimeAwayButton.addActionListener(this);
		addCommitmentButton = new JButton("Add Commitment");
		addCommitmentButton.setName("addCommitmentButton");
		addCommitmentButton.addActionListener(this);
		removeCommitmentButton = new JButton("Remove Commitment");
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
		timeAway.setModel(new DefaultListModel<TimeAwayBean>());
		timeAwayPane = new JScrollPane(timeAway);
		commitments = new JList<CommitmentBean>();
		commitments.setModel(new DefaultListModel<CommitmentBean>());
		commitmentsPane = new JScrollPane(commitments);
	}
	
	/**
	 * Initialize combo boxes.
	 */
	private void initializeComboBoxes() {
		operatingHoursBox = new JComboBox<String>();
		operatingHoursBox.setName("operatingHoursBox");
		for (String hour : OperatingHours.getOperatingHours()) {
			operatingHoursBox.addItem(hour);
		}
		
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
		panel.setPreferredSize(new Dimension(600, 600));
		
		panel.add(nameLabel);
		panel.add(nameField, "wrap, align center");
		panel.add(preferenceFormLabel, "align center, span, wrap");
		panel.add(periodLabel, "align center, span, wrap");
		
		addTimeAwayComponents();
		addCommitmentComponents();
		addPreferenceComponents();
		
		panel.add(clearButton);
		panel.add(submitButton, "wrap");
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.getContentPane().add(panel);
		this.pack();
		this.setVisible(true);
	}
	
	/**
	 * Adds the time away components.
	 */
	private void addTimeAwayComponents() {
		panel.add(timeAwayLabel, "align center, span, wrap");
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
		panel.add(commitmentHourLabel);
		panel.add(operatingHoursBox);
		panel.add(commitmentDayLabel);
		panel.add(daysOfWeekBox);
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
		panel.add(timeLabel);
		panel.add(morningLabel);
		panel.add(noonLabel);
		panel.add(afternoonLabel, "wrap");
		panel.add(rankLabel);
		panel.add(morningRankBox);
		panel.add(noonRankBox);
		panel.add(afternoonRankBox, "wrap");
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
				return;
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
		
		ClinicianPreferencesDAO clinicianPreferencesDao = new ClinicianPreferencesDAO(conn);
		ClinicianPreferencesBean preferences = new ClinicianPreferencesBean(clinicianID, morningRank, noonRank,afternoonRank);
		ClinicianPreferencesBean existing = clinicianPreferencesDao.loadClinicianPreferences(clinicianID);

		
		ClinicianPreferencesAction action = new ClinicianPreferencesAction(preferences, 
				Utility.toCommitmentList(commitments.getModel()), Utility.toTimeAwayList(timeAway.getModel()), conn);
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
		String hourOfDay = ((String)operatingHoursBox.getSelectedItem());
		
		
		if (description.isEmpty()) {
			JOptionPane.showMessageDialog(this,
				    "You must enter in a description. ",
				    "Adding invalid commitment description",
				    JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		commitmentDescription.setText("");
		
		DefaultListModel<CommitmentBean> model = (DefaultListModel<CommitmentBean>) commitments.getModel();

		model.add(model.size(), new CommitmentBean(-1, OperatingHours.toInt(hourOfDay), dayOfWeek, description));
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
			DefaultListModel<CommitmentBean> oldModel = (DefaultListModel<CommitmentBean>) commitments.getModel();
			oldModel.remove(index);
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
		((DefaultListModel<CommitmentBean>)commitments.getModel()).clear();
		((DefaultListModel<TimeAwayBean>)timeAway.getModel()).clear();
		this.repaint();
	}
}
