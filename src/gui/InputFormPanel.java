package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableModel;

import validator.DateRangeValidator;
import net.miginfocom.swing.MigLayout;
import forms.OperatingHours;
import forms.PreferenceInputForm;
import forms.Semester;
import forms.Weekday;

/**
 * 
 * @author Yusheng Hou and Kevin Lim
 *
 */
public class InputFormPanel extends JPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5377691259929030865L;

	private PreferenceInputForm form;
	
	private JScrollPane timeAwayPane, commitmentsPane;
	private JList<String> timeAway, commitments;
	private JTextField timeAwayName, timeAwayStartDate, timeAwayEndDate;
	private JTextField commitmentDescription;
	private JLabel timeAwayNameLabel, timeAwayStartDateLabel, timeAwayEndDateLabel;
	private JLabel commitmentHourLabel, commitmentDayLabel, commitmentDescriptionLabel;
	private JButton addTimeAwayButton, removeTimeAwayButton;
	private JButton addCommitmentButton, removeCommitmentButton;
	private JButton clearButton, submitButton;
	private JComboBox<String> daysOfWeekBox, operatingHoursBox;
	
	private JTextField nameField;
	private static final int NAME_LENGTH = 20;

	private JTable ecPreferenceTable;

	/**
	 * 
	 */
	public InputFormPanel() {
		this(new PreferenceInputForm(Semester.SPRING, new Date(2015 - 1900,
				1 - 1, 20), new Date(2015 - 1900, 5 - 1, 15)));
	}

	public InputFormPanel(PreferenceInputForm form) {
		this.form = form;
		this.setLayout(new MigLayout("gap rel", "grow"));

		// create fields for adding names
		this.add(new JLabel("NAME:"));
		nameField = new JTextField(NAME_LENGTH);
		nameField.setName("nameField");
		this.add(nameField, "wrap, align center");

		String preferenceFormText = "" + Semester.asString(form.getSemester()) + ' '
				+ form.getYear() + " IA/EC Preference Form";
		SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM d, Y");
		String periodText = "This covers the period from "
				+ dateFormat.format(form.getPeriodStart()) + " through "
				+ dateFormat.format(form.getPeriodEnd()) + ".";
		this.add(new JLabel(preferenceFormText), "align center, span, wrap");
		this.add(new JLabel(periodText), "align center, span, wrap");

		String timeAwayText = "" + form.getSemester() + " Semester "
				+ form.getYear() + "\"Time Away\" Plans";
		this.add(new JLabel(timeAwayText), "align center, span, wrap");
		
		timeAwayName = new JTextField(7);
		timeAwayStartDate = new JTextField(7);
		timeAwayEndDate = new JTextField(7);
		
		timeAwayNameLabel = new JLabel("Description");
		timeAwayStartDateLabel = new JLabel("Start Date");
		timeAwayEndDateLabel = new JLabel("End Date");
		
		timeAway = new JList<String>();
		timeAway.setModel(new DefaultListModel<String>());
		timeAwayPane = new JScrollPane(timeAway);
		
		addTimeAwayButton = new JButton("Add Time Away");
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
		
		commitments = new JList<String>();
		commitments.setModel(new DefaultListModel<String>());
		commitmentsPane = new JScrollPane(commitments);
		
		operatingHoursBox = new JComboBox<String>();
		for (String hour : OperatingHours.getOperatingHours()) {
			operatingHoursBox.addItem(hour);
		}
		
		daysOfWeekBox = new JComboBox<String>();
		for (Weekday day : Weekday.values()) {
			daysOfWeekBox.addItem(day.name());
		}
		commitmentDescription = new JTextField(7);	

		commitmentHourLabel = new JLabel("Hour");
		commitmentDayLabel = new JLabel("Day");
		commitmentDescriptionLabel = new JLabel("Description");
		
		addCommitmentButton = new JButton("Add Commitment");
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
		

		ecPreferenceTable = new JTable(2, 4);
		ecPreferenceTable.setValueAt("Times", 0, 0);
		ecPreferenceTable.setValueAt("8:00 a.m.", 0, 1);
		ecPreferenceTable.setValueAt("12:00 noon", 0, 2);
		ecPreferenceTable.setValueAt("4:00 p.m.", 0, 3);
		ecPreferenceTable.setValueAt("Rank", 1, 0);
		this.add(ecPreferenceTable, "wrap, align center, span");

		clearButton = new JButton("Clear");
		clearButton.addActionListener(this);
		submitButton = new JButton("Submit");
		submitButton.addActionListener(this);
		this.add(clearButton);
		this.add(submitButton, "wrap");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == clearButton) {
			clearFields();
		} else if (e.getSource() == submitButton) {
			submit();
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
	
	private void submit() {
		// TODO: ensure name is a valid clinician id
		String clinicianName = nameField.getText().trim();
		System.out.println("'" + clinicianName + "'");
		if (clinicianName.isEmpty()) {
			JOptionPane.showMessageDialog(this,
				    "You must enter in a valid clinician name. ",
				    "Adding invalid clinician name",
				    JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		TableModel model = ecPreferenceTable.getModel();
		String morningRank = (String)model.getValueAt(1, 1);
		String noonRank = (String)model.getValueAt(1, 2);
		String afternoonRank = (String)model.getValueAt(1, 3);
		System.out.println("8am: " + morningRank + " 12pm:" + noonRank + " 4pm: " + afternoonRank);
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

		DefaultListModel<String> model = (DefaultListModel<String>) timeAway.getModel();
		model.add(model.size(), name + " " + startDate + "-" + endDate);
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
		
		DefaultListModel<String> model = (DefaultListModel<String>) commitments.getModel();
		model.add(model.size(), hourOfDay + " " + dayOfWeek + " " + description);
	}
	
	private void removeTimeAway() {
		int index = timeAway.getSelectedIndex();
		if (index >= 0) {
			DefaultListModel<String> oldModel = (DefaultListModel<String>) timeAway.getModel();
			oldModel.remove(index);
		}
	}
	
	private void removeCommitment() {
		int index = commitments.getSelectedIndex();
		if (index >= 0) {
			DefaultListModel<String> oldModel = (DefaultListModel<String>) commitments.getModel();
			oldModel.remove(index);
		}
	}
	
	/**
	 * Clear all fields in this form
	 */
	private void clearFields() {
		nameField.setText("");
		this.repaint();
		// TODO
	}
}
