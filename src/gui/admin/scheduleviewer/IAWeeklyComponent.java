package gui.admin.scheduleviewer;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextPane;

import bean.SessionNameBean;

/**
 * Editable component displaying the clinicians assigned to IA sessions for one week. 
 * @author Yusheng and Denise
 *
 */
public class IAWeeklyComponent extends JPanel implements MouseListener {

	private static final String[] weekdays = new String[]{"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"};
	private List<String> clinicianNames;
	
	/**
	 * Creates a component that displays the clinicians assigned to IA sessions for a particular week.
	 * @param sessionNames
	 * @param weekType type of IA session week
	 */
	public IAWeeklyComponent(List<SessionNameBean> sessionNames, List<String> clinicianNames, String weekType) {
		this.setLayout(new GridLayout(5,6, 10, 10));
		this.setBorder(BorderFactory.createLineBorder(Color.black));
		this.clinicianNames = clinicianNames;
		JComponent comp;
		ArrayList<ArrayList<ArrayList<String>>> cells = getCells(sessionNames, weekType);

		comp = new JLabel("Week " + weekType);
		add(comp);
		comp.setBorder(BorderFactory.createLineBorder(Color.black));
		
		for (String weekday: weekdays) {
			comp = new JLabel(weekday);
			add(comp);
			comp.setBorder(BorderFactory.createLineBorder(Color.black));
		}
		
		for (int i = 11; i <= 15; i++) {
			if (i != 12) {
				comp = new JLabel("" + i + ":00");
				add(comp);
				comp.setBorder(BorderFactory.createLineBorder(Color.black));

				for (ArrayList<String> names: cells.get(i-11)) {
					String labelStr = "";
					for (String name: names) {
						labelStr += name + "\n";
					}
					JTextArea text = new JTextArea(labelStr); 
					text.setEditable(false);
					add(text);
					text.setBorder(BorderFactory.createLineBorder(Color.black));
					text.addMouseListener(this);
				}
			}
		}
	}
	
	/**
	 * Create a vector of check boxes labeled with the list of names
	 * @param names list of names
	 */
	private Vector<JCheckBox> createNameCheckBoxes(List<String> names) {
		Vector<JCheckBox> checkBoxes = new Vector<>(names.size());
		for (String name: names) {
			checkBoxes.add(new JCheckBox(name));
		}
		
		return checkBoxes;
	}
	
	private ArrayList<ArrayList<ArrayList<String>>> getCells(List<SessionNameBean> sessionNames, String weekType) {
		ArrayList<ArrayList<ArrayList<String>>> entries = new ArrayList<ArrayList<ArrayList<String>>>();
		for(int timeslot = 11; timeslot <=15; timeslot++) {
			ArrayList<ArrayList<String>> temp = new ArrayList<ArrayList<String>>();
			for(int day = 0; day < weekdays.length; day++) {
				temp.add(new ArrayList<String>());
			}
			entries.add(temp);
		}
		
		for(SessionNameBean sessionName : sessionNames) {
			if(checkWeekType(sessionName, weekType)) {
				entries.get(sessionName.getStartTime()-11).get(getDayIndex(sessionName)).add(sessionName.getClinicianName());
			}
		}
		return entries;
	}

	private boolean checkWeekType(SessionNameBean bean, String weekType) {
		String beanWeek = (bean.getWeekType()==0) ? "A" : "B";
		return beanWeek.equalsIgnoreCase(weekType);
	}

	private int getDayIndex(SessionNameBean bean) {
		for(int d=0; d<weekdays.length; d++) {
			if(weekdays[d].equalsIgnoreCase(bean.getDayOfWeek())) {
				return d;
			}
		}
		return -1;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		JTextArea source = (JTextArea)e.getSource();
		List<JCheckBox> boxes = new ArrayList<JCheckBox>();
		List<String> listedNames = Arrays.asList(source.getText().split("\n"));
		for (String name: this.clinicianNames) {
			JCheckBox box = new JCheckBox(name);
			if (listedNames.contains(name)) {
				box.setSelected(true);
			}
			boxes.add(box);
		}
		String msg = "Check the clinicians assigned to this session: ";
		ArrayList<Object> msgContent = new ArrayList<>();
		msgContent.add(msg);
		for (JCheckBox cb : boxes) {
			msgContent.add(cb);
		}
		int n = JOptionPane.showConfirmDialog(null, msgContent.toArray(), "Select Clinicians", JOptionPane.YES_NO_OPTION);
		System.out.println(n);
		if (n == JOptionPane.YES_OPTION) {
			String names = "";
			for (int i = 0; i < boxes.size(); i++) {
				if (boxes.get(i).isSelected()) {
					names += this.clinicianNames.get(i) + "\n";
				}
			}
			source.setText(names);
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
