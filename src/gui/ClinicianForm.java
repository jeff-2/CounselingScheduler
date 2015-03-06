package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import action.ClinicianPreferencesAction;
import db.ClinicianDao;
import db.ClinicianPreferencesDao;
import db.ConnectionFactory;
import validator.DateRangeValidator;
import net.miginfocom.swing.MigLayout;
import forms.ClinicianPreferences;
import forms.Commitment;
import forms.OperatingHours;
import forms.TimeAway;
import forms.Weekday;
import forms.Utility;

/**
 * 
 * @author Yusheng Hou and Kevin Lim
 *
 */
public class ClinicianForm extends JPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5377691259929030865L;
	
	private JScrollPane timeAwayPane, commitmentsPane;
	private JList<TimeAway> timeAway;
	private JList<Commitment> commitments;
	private JTextField timeAwayName, timeAwayStartDate, timeAwayEndDate;
	private JTextField commitmentDescription;
	private JLabel timeAwayNameLabel, timeAwayStartDateLabel, timeAwayEndDateLabel;
	private JLabel commitmentHourLabel, commitmentDayLabel, commitmentDescriptionLabel;
	private JLabel timeLabel, rankLabel;
	private JLabel morningLabel, noonLabel, afternoonLabel;
	private JButton addTimeAwayButton, removeTimeAwayButton;
	private JButton addCommitmentButton, removeCommitmentButton;
	private JButton clearButton, submitButton;
	private JComboBox<String> daysOfWeekBox, operatingHoursBox;
	private JComboBox<Integer> morningRankBox, noonRankBox, afternoonRankBox;
	
	private JTextField nameField;
	private static final int NAME_LENGTH = 20;

	public ClinicianForm() {
		this.setLayout(new MigLayout("gap rel", "grow"));

		// create fields for adding names
		this.add(new JLabel("NAME:"));
		nameField = new JTextField(NAME_LENGTH);
		nameField.setName("nameField");
		this.add(nameField, "wrap, align center");

		String preferenceFormText = "Spring 2015 IA/EC Preference Form";
		String periodText = "This covers the period from 1/19/2015-5/10/2015.";
		this.add(new JLabel(preferenceFormText), "align center, span, wrap");
		this.add(new JLabel(periodText), "align center, span, wrap");

		String timeAwayText = "Spring Semester 2015 \"Time Away\" Plans";
		this.add(new JLabel(timeAwayText), "align center, span, wrap");
		
		timeAwayName = new JTextField(7);
		timeAwayName.setName("timeAwayName");
		timeAwayStartDate = new JTextField(7);
		timeAwayStartDate.setName("timeAwayStartDate");
		timeAwayEndDate = new JTextField(7);
		timeAwayEndDate.setName("timeAwayEndDate");
		
		timeAwayNameLabel = new JLabel("Description");
		timeAwayStartDateLabel = new JLabel("Start Date");
		timeAwayEndDateLabel = new JLabel("End Date");
		
		timeAway = new JList<TimeAway>();
		timeAway.setModel(new DefaultListModel<TimeAway>());
		timeAwayPane = new JScrollPane(timeAway);
		
		addTimeAwayButton = new JButton("Add Time Away");
		addTimeAwayButton.setName("addTimeAwayButton");
		addTimeAwayButton.addActionListener(this);
		removeTimeAwayButton = new JButton("Remove Time Away");
		removeTimeAwayButton.addActionListener(this);
		
		this.add(timeAwayNameLabel);
		this.add(timeAwayName);
		this.add(timeAwayStartDateLabel);
		this.add(timeAwayStartDate);
		this.add(timeAwayEndDateLabel);
		this.add(timeAwayEndDate, "wrap");
		
		this.add(timeAwayPane, "grow, span, wrap");
		
		this.add(addTimeAwayButton);
		this.add(removeTimeAwayButton, "wrap");
		
		commitments = new JList<Commitment>();
		commitments.setModel(new DefaultListModel<Commitment>());
		commitmentsPane = new JScrollPane(commitments);
		
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
		commitmentDescription = new JTextField(7);
		commitmentDescription.setName("commitmentDescription");

		commitmentHourLabel = new JLabel("Hour");
		commitmentDayLabel = new JLabel("Day");
		commitmentDescriptionLabel = new JLabel("Description");
		
		addCommitmentButton = new JButton("Add Commitment");
		addCommitmentButton.setName("addCommitmentButton");
		addCommitmentButton.addActionListener(this);
		removeCommitmentButton = new JButton("Remove Commitment");
		removeCommitmentButton.addActionListener(this);

		this.add(commitmentHourLabel);
		this.add(operatingHoursBox);
		this.add(commitmentDayLabel);
		this.add(daysOfWeekBox);
		this.add(commitmentDescriptionLabel);
		this.add(commitmentDescription, "wrap");
		
		this.add(commitmentsPane, "grow, span, wrap");
		
		this.add(addCommitmentButton);
		this.add(removeCommitmentButton, "wrap");
		
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
		
		timeLabel = new JLabel("Time");
		rankLabel = new JLabel("Rank");
		morningLabel = new JLabel("8:00am");
		noonLabel = new JLabel("12:00pm");
		afternoonLabel = new JLabel("4:00pm");
		
		this.add(timeLabel);
		this.add(morningLabel);
		this.add(noonLabel);
		this.add(afternoonLabel, "wrap");
		this.add(rankLabel);
		this.add(morningRankBox);
		this.add(noonRankBox);
		this.add(afternoonRankBox, "wrap");

		clearButton = new JButton("Clear");
		clearButton.addActionListener(this);
		submitButton = new JButton("Submit");
		submitButton.setName("submitButton");
		submitButton.addActionListener(this);
		this.add(clearButton);
		this.add(submitButton, "wrap");
	}

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
	
	private void submit() throws SQLException {
		
		String clinicianName = nameField.getText().trim();
		int morningRank = ((Integer)morningRankBox.getSelectedItem()).intValue();
		int noonRank = ((Integer)noonRankBox.getSelectedItem()).intValue();
		int afternoonRank = ((Integer)afternoonRankBox.getSelectedItem()).intValue();
		
		Connection conn = ConnectionFactory.getInstance();
		ClinicianDao clinicianDao = new ClinicianDao(conn);
		
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
		
		ClinicianPreferencesDao clinicianPreferencesDao = new ClinicianPreferencesDao(conn);
		ClinicianPreferences preferences = new ClinicianPreferences(clinicianID, morningRank, noonRank,afternoonRank);
		ClinicianPreferences existing = clinicianPreferencesDao.loadClinicianPreferences(clinicianID);

		
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
		DefaultListModel<TimeAway> model = (DefaultListModel<TimeAway>) timeAway.getModel();
		try {
			start = DateRangeValidator.parseDate(startDate);
			end = DateRangeValidator.parseDate(endDate);
		} catch (ParseException e) {
			throw new RuntimeException("Should never happen");
		}
		model.add(model.size(), new TimeAway(-1, name, start, end));
	}
	
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
		
		DefaultListModel<Commitment> model = (DefaultListModel<Commitment>) commitments.getModel();

		String hr = hourOfDay.replaceAll("\\D+", "");
		int hour;
		try {
			hour = Integer.parseInt(hr);
		} catch (NumberFormatException e) {
			throw new RuntimeException("Should never happen");
		}
		if (hourOfDay.contains("pm")) {
			hour += 12;
		}

		model.add(model.size(), new Commitment(-1, hour, dayOfWeek, description));
	}
	
	private void removeTimeAway() {
		int index = timeAway.getSelectedIndex();
		if (index >= 0) {
			DefaultListModel<TimeAway> oldModel = (DefaultListModel<TimeAway>) timeAway.getModel();
			oldModel.remove(index);
		}
	}
	
	private void removeCommitment() {
		int index = commitments.getSelectedIndex();
		if (index >= 0) {
			DefaultListModel<Commitment> oldModel = (DefaultListModel<Commitment>) commitments.getModel();
			oldModel.remove(index);
		}
	}
	
	/**
	 * Clear all fields in this form
	 */
	private void clearFields() {
		nameField.setText("");
		timeAwayName.setText("");
		timeAwayStartDate.setText("");
		timeAwayEndDate.setText("");
		commitmentDescription.setText("");
		((DefaultListModel<Commitment>)commitments.getModel()).clear();
		((DefaultListModel<TimeAway>)timeAway.getModel()).clear();
		this.repaint();
	}
}
