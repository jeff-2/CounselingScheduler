package gui.admin.scheduleviewer;

import java.awt.Color;
import java.awt.Component;
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

	private static final long serialVersionUID = 1181653974079435258L;
	private Component [][] pane;
	private int row, column;

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
					comp = assignedClinician;
					assignedClinician.setSelectedItem(cells.get(r).get(c));
					assignedClinician.setActionCommand(cells.get(0).get(c) + cells.get(r).get(0));
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
}
