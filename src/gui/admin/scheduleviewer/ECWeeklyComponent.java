package gui.admin.scheduleviewer;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import action.ValidateScheduleAction;
import bean.Clinician;
import bean.ECScheduleWeekBean;
import bean.Schedule;

/**
 * Editable component displaying the clinicians assigned to EC sessions for one week. 
 * @author Yusheng, Denise
 */  
public class ECWeeklyComponent extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1181653974079435258L;
	private Component [][] pane;
	private int row, column;
	private List<JComboBox<String>> comboBoxes;
	private Map<String, String> sessionIDs;
	private Schedule schedule;
	
	/**
	 * Creates a component that displays the clinicians assigned to EC sessions for a particular week.
	 * @param week 
	 */
	public ECWeeklyComponent(ECScheduleWeekBean week, Vector<String> clinicianNames, Schedule schedule) {
		this.setLayout(new GridLayout(4,6));
		this.setBorder(BorderFactory.createLineBorder(Color.black));
		comboBoxes = new ArrayList<>();
		sessionIDs = new HashMap<>();
		this.schedule = schedule;

		ArrayList<ArrayList<String>> cells = week.getCellContent();
		ArrayList<ArrayList<String>> ids = week.getCellIDs();

		String item;
		JComponent comp;
		row = cells.size();
		column = cells.get(0).size();
		pane = new Component[row][column];
		for(int r=0; r<cells.size(); r++) {
			for (int c=0; c<cells.get(r).size(); c++) {
				item = cells.get(r).get(c);
				
				if (r==0 || c==0) {
					comp = new JLabel(item);
				} else if (item == null) {
					comp = new JPanel();
					comp.setBackground(Color.gray);
				} else {
					JComboBox<String> assignedClinician = new JComboBox<String>(clinicianNames);
					String key = cells.get(0).get(c) + ", " + cells.get(r).get(0);
					comp = assignedClinician;
					assignedClinician.setSelectedItem(cells.get(r).get(c));
					assignedClinician.setActionCommand(key);
					assignedClinician.addActionListener(this);
					assignedClinician.putClientProperty("Month", week.monthAbbrev());
					assignedClinician.putClientProperty("Day", cells.get(0).get(c).split(" ")[1]);
					assignedClinician.putClientProperty("Time", cells.get(r).get(0));
					
					comboBoxes.add(assignedClinician);
					sessionIDs.put(key, ids.get(r).get(c));
					assignedClinician.setName("" + ids.get(r).get(c));
					
				}
				
				pane[r][c] = comp;
				add(comp);
				comp.setBorder(BorderFactory.createLineBorder(Color.black));
			}
		}
	}
	
	public List<List<String>> toCellsList() {
		List<List<String>> cellsList = new ArrayList<List<String>>();
		for (int i = 0; i < row; i++) {
			List<String> cells = new ArrayList<String>();
			for (int j = 0; j < column; j++) {
				Component current = pane[i][j];
				if (current instanceof JPanel) {
					cells.add("");
				} else if (current instanceof JComboBox<?>) {
					@SuppressWarnings("unchecked")
					JComboBox<String> comboBox = (JComboBox<String>)current;
					cells.add((String)comboBox.getSelectedItem());
				} else {
					JLabel label = (JLabel)current;
					cells.add(label.getText());
				}
			}
			cellsList.add(cells);
		}
		return cellsList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void actionPerformed(ActionEvent e) {
		String month = (String) ((JComboBox<String>)e.getSource()).getClientProperty("Month");
		String day = (String) ((JComboBox<String>)e.getSource()).getClientProperty("Day");
		String time = (String) ((JComboBox<String>)e.getSource()).getClientProperty("Time");
		String clinicianName = ((JComboBox<String>)e.getSource()).getSelectedItem().toString();
		
		Date date = getDate(month, day);
		int timeInt = getTime(time);
		
		schedule.editEC(date, timeInt, clinicianName);
		Set<Clinician> clinicians = new ValidateScheduleAction().validateSchedule(schedule);
		if(clinicians.size() != 0) {
			String cliniciansString = "";
			for(Clinician clinician : clinicians) {
				cliniciansString += clinician.getClinicianBean().getName() + ", ";
			}
			cliniciansString = cliniciansString.substring(0, cliniciansString.length()-2) + ".";
			String errorString = "The previous change created conflicts with the following clinicians on "
					+ month + " " + day + ":\n\n " + cliniciansString;
			JOptionPane.showMessageDialog(this,
					errorString, "Validation Error",
					JOptionPane.ERROR_MESSAGE);
		}
		
		
	}

	private int getTime(String time) {
		if(time.charAt(0) == '8') {
			return 8;
		}
		else if(time.charAt(0) == '4') {
			return 16;
		}
		else return 12;
	}

	private Date getDate(String month, String day) {
		@SuppressWarnings("deprecation")
		String date = month + " " + day + ", " + (schedule.getCalendar().getStartDate().getYear() +1900);
		DateFormat format = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
		try {
			return format.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
}
