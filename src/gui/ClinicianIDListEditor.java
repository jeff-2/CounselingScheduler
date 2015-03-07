package gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

import db.Clinician;
import db.ClinicianDao;
import db.ClinicianPreferencesDao;
import db.ConnectionFactory;
import net.miginfocom.swing.MigLayout;

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
	private JList<Clinician> clinicianList;

	/**
	 * JScrollPane displaying the current list of clinican IDs
	 */
	private JScrollPane listScrollPane;

	/**
	 * Connection DAO to database (could be remote)
	 */
	private ClinicianDao dao;

	/**
	 * Local cache of current list of clinicians
	 */
	private List<Clinician> localClinicians;

	/**
	 * Create an empty client ID list
	 */
	public ClinicianIDListEditor() {
		super("Edit Clinician ID List");
		this.initializeConnectionToDB();
		this.updateClinicianList();
		this.panel = new JPanel(new MigLayout("gap rel 0", "grow"));
		this.initializeFrame();
		this.setLocationRelativeTo(null); 	// Center JFrame in middle of screen
		// TODO: initialize Clinician ID list data structure
	}

	/**
	 * Initializes the Connections object that allows us to communicate with the database.
	 */
	private void initializeConnectionToDB() {
		try {
			Connection conn = ConnectionFactory.getInstance();
			dao = new ClinicianDao(conn);
		}
		catch(Exception e) {
			JOptionPane.showMessageDialog(this,
					"Failed to connect to the remote SQL database; please contact the network administrator.",
					"Database connection error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void updateClinicianList() {
		try {
			this.localClinicians = this.dao.loadClinicians();
		}
		catch(Exception e) {
			JOptionPane.showMessageDialog(this,
					"Failed to connect to the remote SQL database; please contact the network administrator.",
					"Database connection error",
					JOptionPane.ERROR_MESSAGE);
		}
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
		this.newIDField.addKeyListener(this);
		this.panel.add(newIDField, "span, grow, wrap 15px");
		// Add "Add" and "Remove" buttons
		this.addButton = new JButton("Add Clinician");
		this.addButton.addActionListener(this);
		this.panel.add(addButton, "gapleft 30");
		this.removeButton = new JButton("Remove Clinician");
		this.removeButton.addActionListener(this);
		this.panel.add(removeButton, "gap unrelated, wrap 15px");
		// Add list & scrollpane
		this.clinicianList = new JList<Clinician>();
		this.populateClinicianList();
		this.clinicianList.addListSelectionListener(this);
		this.listScrollPane = new JScrollPane(clinicianList);
		this.panel.add(listScrollPane, "grow, push, span");
		// Pack and make visible
		this.updateButtonStatus();
		this.getContentPane().add(panel);
		this.pack();
		this.setVisible(true);
	}

	/**
	 * Update the JList to reflect the current list of clinicians in the database
	 */
	private void populateClinicianList() {
		this.updateClinicianList();
		ArrayList<Clinician> data = new ArrayList<Clinician>();
		ListModel<Clinician> model = 
				(ListModel<Clinician>) this.clinicianList.getModel();
		for(int i=0; i<model.getSize(); i++) {
			data.add(model.getElementAt(i));
		}
		Collections.sort(data);
		DefaultListModel<Clinician> newModel = new DefaultListModel<Clinician>();
		for(Clinician c : data) {
			newModel.addElement(c);
		}
		this.clinicianList.setModel(newModel);
		revalidate();
		repaint();
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
		boolean emptyClinicianID = this.newIDField.getText().isEmpty();
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
		String newClinicianName = this.newIDField.getText().trim();
		this.newIDField.setText("");
		if(newClinicianName.isEmpty()) {
			JOptionPane.showMessageDialog(this,
					"Cannot add an empty clinician ID to the list. " +
							"Please enter a new clinician ID in the text box above.",
							"Adding empty ID",
							JOptionPane.ERROR_MESSAGE);
		}
		else {
			try {
				int newClinicianID = this.dao.getNextClinicianID();
				Clinician newClinician = new Clinician(newClinicianID, newClinicianName);
				this.dao.insert(newClinician);
				this.populateClinicianList();
			}
			catch(Exception e) {
				JOptionPane.showMessageDialog(this,
						"Failed to connect to the remote SQL database; please contact the network administrator.",
						"Database connection error",
						JOptionPane.ERROR_MESSAGE);
			}			
		}
	}

	/**
	 * Removes the selected clinician ID from the list. Checks that
	 * a clinician ID is actually selected.
	 */
	private void removeClinicianID() {
		try {
			int index = this.clinicianList.getSelectedIndex();
			Clinician oldClinician = this.clinicianList.getModel().getElementAt(index);
			this.dao.delete(oldClinician.getClinicianID());
			this.populateClinicianList();
		}
		catch(Exception e) {
			JOptionPane.showMessageDialog(this,
					"Failed to connect to the remote SQL database; please contact the network administrator.",
					"Database connection error",
					JOptionPane.ERROR_MESSAGE);
		}	
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
		if(e.getSource() == this.newIDField) {
			this.updateButtonStatus();
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		this.updateButtonStatus();
	}

}
