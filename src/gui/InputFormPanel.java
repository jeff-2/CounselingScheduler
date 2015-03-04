package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import forms.PreferenceInputForm;
import forms.Semester;

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
	private JTextField commitmentHour, commitmentDay, commitmentDescription;
	private JLabel timeAwayNameLabel, timeAwayStartDateLabel, timeAwayEndDateLabel;
	private JLabel commitmentHourLabel, commitmentDayLabel, commitmentDescriptionLabel;
	private JButton addTimeAwayButton, removeTimeAwayButton;
	private JButton addCommitmentButton, removeCommitmentButton;
	private JButton clearButton, restoreButton, submitButton;
	
	private JTextField nameField;
	private int nameLength;

 
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
		nameLength = 20;
		nameField = new JTextField(nameLength);
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
		
		commitmentHour = new JTextField(7);
		commitmentDay = new JTextField(7);
		commitmentDescription = new JTextField(7);	

		commitmentHourLabel = new JLabel("Hour");
		commitmentDayLabel = new JLabel("Day");
		commitmentDescriptionLabel = new JLabel("Description");
		
		addCommitmentButton = new JButton("Add Commitment");
		addCommitmentButton.addActionListener(this);
		removeCommitmentButton = new JButton("Remove Commitment");
		removeCommitmentButton.addActionListener(this);

		this.add(commitmentHourLabel);
		this.add(commitmentHour);
		this.add(commitmentDayLabel);
		this.add(commitmentDay);
		this.add(commitmentDescriptionLabel);
		this.add(commitmentDescription, "wrap");
		
		this.add(commitmentsPane, "grow, span, wrap");
		
		this.add(addCommitmentButton);
		this.add(removeCommitmentButton, "wrap");
		

		JTable ecPreferenceTable = new JTable(2, 4);
		ecPreferenceTable.setValueAt("Times", 0, 0);
		ecPreferenceTable.setValueAt("8:00 a.m.", 0, 1);
		ecPreferenceTable.setValueAt("12:00 noon", 0, 2);
		ecPreferenceTable.setValueAt("4:00 p.m.", 0, 3);
		ecPreferenceTable.setValueAt("Rank", 1, 0);
		this.add(ecPreferenceTable, "wrap, align center, span");

		clearButton = new JButton("Clear");
		clearButton.addActionListener(this);
		restoreButton = new JButton("Restore Saved");
		submitButton = new JButton("Submit");
		this.add(clearButton);
		this.add(restoreButton);
		this.add(submitButton, "wrap");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == clearButton) {
			clearFields();
		} else if (e.getSource() == restoreButton) {
			
		} else if (e.getSource() == submitButton) {
			// do validation
		} else if (e.getSource() == addTimeAwayButton) {
			
		} else if (e.getSource() == removeTimeAwayButton) {
			
		} else if (e.getSource() == addCommitmentButton) {
			// do validation
		} else if (e.getSource() == removeCommitmentButton) {
			
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
