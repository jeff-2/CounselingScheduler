package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
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

	private PreferenceInputForm form;
	
	private JPanel timeAwayPanel;
	private JPanel conflictsPanel;
	private JPanel ecPreferencePanel;
	
	//private ArrayList<>

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
		this.setLayout(new MigLayout("fillx", "", ""));

		// create fields for adding names
		this.add(new JLabel("NAME:"), "split, align center");
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

		JTable timeAwayTable = new JTable(5, 4);
		timeAwayTable.setValueAt("Plan", 0, 0);
		timeAwayTable.setValueAt("Start Date", 0, 1);
		timeAwayTable.setValueAt("End Date", 0, 2);
		timeAwayTable.setValueAt("Add/Remove", 0, 3);
		this.add(timeAwayTable, "wrap, align center, span");

		JTable weeklyConflictTable = new JTable(5, 4);
		weeklyConflictTable.setValueAt("Time", 0, 0);
		weeklyConflictTable.setValueAt("Day of Week", 0, 1);
		weeklyConflictTable.setValueAt("Activity or Meeting", 0, 2);
		weeklyConflictTable.setValueAt("Add/Remove", 0, 3);
		this.add(weeklyConflictTable, "wrap, align center, span");

		JTable ecPreferenceTable = new JTable(2, 4);
		ecPreferenceTable.setValueAt("Times", 0, 0);
		ecPreferenceTable.setValueAt("8:00 a.m.", 0, 1);
		ecPreferenceTable.setValueAt("12:00 noon", 0, 2);
		ecPreferenceTable.setValueAt("4:00 p.m.", 0, 3);
		ecPreferenceTable.setValueAt("Rank", 1, 0);
		this.add(ecPreferenceTable, "wrap, align center, span");

		JButton clearButton = new JButton("Clear");
		clearButton.addActionListener(this);
		JButton restoreButton = new JButton("Restore Saved");
		JButton submitButton = new JButton("Submit");
		this.add(clearButton, "split 3, align center");
		this.add(restoreButton, "align center");
		this.add(submitButton, "align center");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
		case "Clear":
			clearFields();
			
			break;
		case "Restore Saved":
			break;
		case "Submit":
			break;
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
