package gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListModel;

import net.miginfocom.swing.MigLayout;

/**
 * A GUI element for adding and removing clinicians from the clinician ID list. 
 * Allows saving and loading clinician list to disk.
 * 
 * @author ramusa2
 * @author dtli2
 * 
 */
public class ClinicianIDListEditor extends JFrame implements ActionListener {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = -5942052228994817185L;
	
	/**
	 * The main panel for the GUI frame
	 */
	private final JPanel panel;
	
	/**
	 * JLabel for new clinican field
	 */
	private JLabel newClinicianLabel;
	
	/**
	 * JTextField for entering the ID of a new clinician
	 */
	private JTextField newIDField;
	
	/**
	 * JButton for adding the current clinician ID to the list
	 */
	private JButton addButton;
	
	/**
	 * JButton for removing the currently selected clinican ID in the list
	 */
	private JButton removeButton;
	
	/**
	 * JList storing the current list of clinician IDs
	 */
	private JList<String> clinicianList;
	
	/**
	 * JScrollPane displaying the current list of clinican IDs
	 */
	private JScrollPane listScrollPane;
	
	/**
	 * Create an empty client ID list
	 */
	public ClinicianIDListEditor() {
		super("Edit Clinician ID List");
		this.panel = new JPanel(new MigLayout("gap rel 0", "grow"));
		this.initializeFrame();
		this.setLocationRelativeTo(null); 	// Center JFrame in middle of screen
		// TODO: initialize Clinician ID list data structure
	}
	
	/**
	 * Set up the components of this JFrame, pack, and make it visible
	 */
	private void initializeFrame() {
		// Set preferred size
		this.panel.setPreferredSize( new Dimension( 400, 480 ) );
		// Set exit behavior
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// Add label and ID field for new clinicians
		this.newClinicianLabel = new JLabel("New Clinician ID:");
		this.panel.add(newClinicianLabel, "gapleft 30");
		this.newIDField = new JTextField();
		this.panel.add(newIDField, "span, grow, wrap 15px");
		// Add "Add" and "Remove" buttons
		this.addButton = new JButton("Add Clinician");
		this.addButton.addActionListener(this);
		this.panel.add(addButton, "gapleft 30");
		this.removeButton = new JButton("Remove Clinician");
		this.removeButton.addActionListener(this);
		this.panel.add(removeButton, "gap unrelated, wrap 15px");
		// Add list & scrollpane
		this.clinicianList = new JList<String>();
		this.listScrollPane = new JScrollPane(clinicianList);
		this.panel.add(listScrollPane, "grow, push, span");
		// Pack and make visible
		this.getContentPane().add(panel);
		this.pack();
		this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == this.addButton) {
			this.addNewClinicianID();
		}
		if(e.getSource() == this.removeButton) {
			this.removeClinicianID();
		}
	}

	/**
	 * Adds a new clinician ID to the list, where the ID is taken
	 * from the input text box. Checks that the new ID is non-empty
	 * and that the ID does not already exist. Creates an error dialog
	 * if this is not the case.
	 */
	private void addNewClinicianID() {
		String newClinicianID = this.newIDField.getText().trim();
		this.newIDField.setText("");
		if(newClinicianID.isEmpty()) {
			JOptionPane.showMessageDialog(this,
				    "Cannot add an empty clinician ID to the list. " +
				    "Please enter a new clinician ID in the text box above.",
				    "Adding empty ID",
				    JOptionPane.ERROR_MESSAGE);
			return;
		}
		else {
			ArrayList<String> data = new ArrayList<String>();
			ListModel<String> model = 
					(ListModel<String>) this.clinicianList.getModel();
			for(int i=0; i<model.getSize(); i++) {
				data.add(model.getElementAt(i));
			}
			if (data.contains(newClinicianID)) {
				JOptionPane.showMessageDialog(this,
					    "Cannot add a duplicate clinician ID to the list. " +
					    "Please enter a unique clinician ID in the text box above.",
					    "Adding duplicate ID",
					    JOptionPane.ERROR_MESSAGE);
				return;
			}
			else {
				// Add new clinician ID to alphanumerically-sorted list
				data.add(newClinicianID);
				Collections.sort(data);
				DefaultListModel<String> newModel = new DefaultListModel<String>();
				for(String id : data) {
					newModel.addElement(id);
				}
				this.clinicianList.setModel(newModel);
				revalidate();
			    repaint();
			}
		}
	}

	/**
	 * Removes the selected clinician ID from the list. Checks that
	 * a clinician ID is actually selected.
	 */
	private void removeClinicianID() {
		int index = this.clinicianList.getSelectedIndex();
		if(index >= 0) {
			// Add new clinician ID to alphanumerically-sorted list
			ListModel<String> oldModel = 
					(ListModel<String>) this.clinicianList.getModel();
			DefaultListModel<String> newModel = new DefaultListModel<String>();
			for(int i=0; i<oldModel.getSize(); i++) {
				if(i != index) {
					newModel.addElement(oldModel.getElementAt(i));
				}
			}
			this.clinicianList.setModel(newModel);
			revalidate();
		    repaint();
		}
		revalidate();
	    repaint();
	}

}
