package gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.miginfocom.swing.MigLayout;

import db.Clinician;

/**
 * A GUI element for adding and removing clinicians from the clinician ID list. 
 * Allows saving and loading clinician list to disk.
 * 
 * @author ramusa2
 * @author dtli2
 * 
 */
public class ClinicianIDListEditor extends JFrame 
				implements ActionListener, KeyListener, ListSelectionListener {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = -5942052228994817185L;
	
	/**
	 * The main panel for the GUI frame
	 */
	private final JPanel panel;
	
	/**
	 * JLabel for new clinican username field
	 */
	private JLabel newUsernameLabel;
	
	/**
	 * JTextField for entering the username of a new clinician
	 */
	private JTextField newUsernameField;
	
	/**
	 * JLabel for new clinican name field
	 */
	private JLabel newFullnameLabel;
	
	/**
	 * JTextField for entering the name of a new clinician
	 */
	private JTextField newFullnameField;

	/**
	 * JButton for adding the current clinician ID to the list
	 */
	private JButton addButton;
	
	/**
	 * JButton for removing the currently selected clinican ID in the list
	 */
	private JButton removeButton;
	
	/**
	 * JList storing the current list of Clinicians
	 */
	private JList<Clinician> clinicianList;
	
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
		// Add label and username field for new clinicians
		this.newUsernameLabel = new JLabel("New Clinician Username:");
		this.panel.add(newUsernameLabel, "gapleft 30");
		this.newUsernameField = new JTextField();
		this.newUsernameField.addKeyListener(this);
		this.panel.add(newUsernameField, "span, grow, wrap 15px");
		// Add label and full name field for new clinicians
		this.newFullnameLabel = new JLabel("New Clinician Name:");
		this.panel.add(newFullnameLabel, "gapleft 30");
		this.newFullnameField = new JTextField();
		this.newFullnameField.addKeyListener(this);
		this.panel.add(newFullnameField, "span, grow, wrap 15px");
		// Add "Add" and "Remove" buttons
		this.addButton = new JButton("Add Clinician");
		this.addButton.addActionListener(this);
		this.panel.add(addButton, "gapleft 30");
		this.removeButton = new JButton("Remove Clinician");
		this.removeButton.addActionListener(this);
		this.panel.add(removeButton, "gap unrelated, wrap 15px");
		// Add list & scrollpane
		this.clinicianList = new JList<>();
		this.clinicianList.addListSelectionListener(this);
		this.listScrollPane = new JScrollPane(clinicianList);
		this.panel.add(listScrollPane, "grow, push, span");
		// Pack and make visible
		this.updateButtonStatus();
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
		this.updateButtonStatus();
	}

	/**
	 * If there is no new clinician ID text, gray out add user button.
	 * If no selected clinician in list, gray out remove user button.
	 * @param e 
	 */
	private void updateButtonStatus() {
		boolean emptyClinicianID = this.newUsernameField.getText().isEmpty() || this.newFullnameField.getText().isEmpty();
		this.addButton.setEnabled(!emptyClinicianID);
		boolean noListSelection = this.clinicianList.getSelectedIndex() == -1;
		this.removeButton.setEnabled(!noListSelection);
	}

	/**
	 * Adds a new clinician ID to the list, where the ID is taken
	 * from the input text box. Checks that the new ID is non-empty
	 * and that the ID does not already exist. Creates an error dialog
	 * if this is not the case.
	 */
	private void addNewClinicianID() {
		String newClinicianID = this.newUsernameField.getText().trim();
		String newClinicianName = this.newFullnameField.getText().trim();
		this.newUsernameField.setText("");
		this.newFullnameField.setText("");
		if(newClinicianID.isEmpty() || newClinicianName.isEmpty()) {
			JOptionPane.showMessageDialog(this,
				    "Cannot add a clinician without a username and full name to the list. " +
				    "Please enter a username and name in the text boxex above.",
				    "Adding empty ID",
				    JOptionPane.ERROR_MESSAGE);
		}
		else {
			Clinician newClinician = new Clinician(0, newClinicianName, newClinicianID);
			ArrayList<Clinician> data = new ArrayList<>();
			ListModel<Clinician> model = this.clinicianList.getModel();
			for(int i=0; i<model.getSize(); i++) {
				data.add(model.getElementAt(i));
			}
			if (data.contains(newClinician)) {
				JOptionPane.showMessageDialog(this,
					    "Cannot add a duplicate clinician ID to the list. " +
					    "Please enter a unique clinician ID in the text box above.",
					    "Adding duplicate ID",
					    JOptionPane.ERROR_MESSAGE);
			}
			else {
				// Add new clinician ID to alphanumerically-sorted list
				data.add(newClinician);
				Collections.sort(data);
				DefaultListModel<Clinician> newModel = new DefaultListModel<>();
				for(Clinician id : data) {
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
			ListModel<Clinician> oldModel = this.clinicianList.getModel();
			DefaultListModel<Clinician> newModel = new DefaultListModel<>();
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

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// do nothing	
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getSource() == this.newUsernameField || e.getSource() == this.newFullnameField) {
			this.updateButtonStatus();
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		this.updateButtonStatus();
	}

}
