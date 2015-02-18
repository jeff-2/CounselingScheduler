package gui;

import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

/**
 * A GUI element for adding and removing clinicians from the clinician ID list. 
 * Allows saving and loading clinician list to disk.
 * 
 * @author ramusa2
 * @author dtli2
 * 
 */
public class ClinicianIDListEditor extends JFrame {

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
	private JList clinicianList;
	
	/**
	 * JScrollPane displaying the current list of clinican IDs
	 */
	private JScrollPane listScrollPane;
	
	/**
	 * Create an empty client ID list
	 */
	public ClinicianIDListEditor() {
		super("Edit Clinician ID List");
		panel = new JPanel(new MigLayout());
		initializeFrame();
		setLocationRelativeTo(null); 	// Center JFrame in middle of screen
		// TODO: initialize Clinician ID list data structure
	}
	
	/**
	 * Set up the components of this JFrame, pack, and make it visible
	 */
	private void initializeFrame() {
		// Set preferred size
		panel.setPreferredSize( new Dimension( 400, 480 ) );
		// Set exit behavior
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// Add label and ID field for new clinicians
		newClinicianLabel = new JLabel("New Clinician ID:");
		panel.add(newClinicianLabel);
		newIDField = new JTextField();
		panel.add(newIDField, "grow, wrap");
		// Add "Add" and "Remove" buttons
		addButton = new JButton("Add Clinician");
		panel.add(addButton, "gap");
		removeButton = new JButton("Remove Clinician");
		panel.add(removeButton, "wrap");
		// Add list & scrollpane
		clinicianList = new JList();
		listScrollPane = new JScrollPane(clinicianList);
		panel.add(listScrollPane, "grow, push, span");
		// Pack and make visible
		this.getContentPane().add(panel);
		this.pack();
		this.setVisible(true);
	}

}
