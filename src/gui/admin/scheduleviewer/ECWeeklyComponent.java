package gui.admin.scheduleviewer;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import bean.ECScheduleWeekBean;

/**
 * Editable component displaying the clinicians assigned to EC sessions for one week. 
 * @author Yusheng, Denise
 */
public class ECWeeklyComponent extends JPanel {

	/**
	 * Creates a component that displays the clinicians assigned to EC sessions for a particular week.
	 * @param week 
	 */
	public ECWeeklyComponent(ECScheduleWeekBean week, Vector<String> clinicianNames) {
		this.setLayout(new GridLayout(4,6));
		this.setBorder(BorderFactory.createLineBorder(Color.black));

		ArrayList<ArrayList<String>> cells = week.getCells();

		String item;
		JComponent comp;
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
					comp = assignedClinician;
					assignedClinician.setSelectedItem(cells.get(r).get(c));
					assignedClinician.setActionCommand(cells.get(0).get(c) + cells.get(r).get(0));
				}
				
				add(comp);
				comp.setBorder(BorderFactory.createLineBorder(Color.black));
			}
		}
	}
}
