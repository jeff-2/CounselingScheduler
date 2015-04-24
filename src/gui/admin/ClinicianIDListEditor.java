package gui.admin;

import gui.clinician.ClinicianForm;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.Connection;
import java.sql.SQLException;
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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.miginfocom.swing.MigLayout;
import bean.CalendarBean;
import bean.ClinicianBean;
import bean.DateRange;
import dao.CalendarDAO;
import dao.ClinicianDAO;
import dao.ClinicianPreferencesDAO;
import dao.ConnectionFactory;

/**
 * A GUI element for adding and removing clinicians from the clinician ID list. 
 * Allows saving and loading clinician list to disk.
 * 
 * @author ramusa2
 * @author dtli2
 * 
 */
public class ClinicianIDListEditor extends JPanel 
implements ActionListener, KeyListener, ListSelectionListener {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = -5942052228994817185L;
	
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
	
	private JButton editButton;

	/**
	 * JList storing the current list of Clinicians
	 */
	private JList<ClinicianBean> clinicianList;
	
	/**
	 * JScrollPane displaying the current list of clinican IDs
	 */
	private JScrollPane listScrollPane;

	/**
	 * Connection DAO to database (could be remote)
	 */
	private ClinicianDAO dao;

	private ClinicianPreferencesDAO prefsDAO;
	/**
	 * Local cache of current list of clinicians
	 */
	private List<ClinicianBean> localClinicians;

	/**
	 * Create an empty client ID list
	 */
	public ClinicianIDListEditor() {
		//super("Edit Clinician ID List");
		this.initializeConnectionToDB();
		this.updateClinicianList();
		setLayout(new MigLayout("gap rel 0", "grow"));
		this.initializePanel();
		//this.setLocationRelativeTo(null); 	// Center JFrame in middle of screen
	}

	/**
	 * Initializes the Connections object that allows us to communicate with the database.
	 */
	private void initializeConnectionToDB() {
		try {
			Connection conn = ConnectionFactory.getInstance();
			dao = new ClinicianDAO(conn);
			prefsDAO = new ClinicianPreferencesDAO(conn);
		}
		catch(Exception e) {
			handleDBException(e);
		}
	}

	private void handleDBException(Exception e) {
		e.printStackTrace();
		JOptionPane.showMessageDialog(this,
				"Failed to connect to the remote SQL database; please contact the network administrator.",
				"Database connection error",
				JOptionPane.ERROR_MESSAGE);
	}

	private void updateClinicianList() {
		try {
			this.localClinicians = this.dao.loadClinicians();
		}
		catch(Exception e) {
			handleDBException(e);
		}
	}

	/**
	 * Set up the components of this JPanel
	 */
	private void initializePanel() {
		// Set preferred size
		setPreferredSize(new Dimension(550, 550));
		// Set exit behavior
		//this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// Add label and full name field for new clinicians
		this.newFullnameLabel = new JLabel("New Clinician Name:");
		add(newFullnameLabel, "align label");
		this.newFullnameField = new JTextField();
		this.newFullnameField.setName("newFullnameField");
		this.newFullnameField.addKeyListener(this);
		add(newFullnameField, "span, grow, wrap 15px");
		// Add "Add" and "Remove" buttons
		this.addButton = new JButton("Add Clinician");
		this.addButton.setName("addButton");
		this.addButton.addActionListener(this);
		add(addButton, "split 3, align center, span, sizegroup bttn");
		this.removeButton = new JButton("Remove Clinician");
		this.removeButton.setName("removeButton");
		this.removeButton.addActionListener(this);
		add(removeButton, "gapleft 15, sizegroup bttn");
		this.editButton = new JButton("Edit Clinician Preferences");
		this.editButton.setName("editButton");
		this.editButton.addActionListener(this);
		add(editButton, "gapleft 15, sizegroup bttn, wrap");
		// Add list & scrollpane
		this.clinicianList = new JList<ClinicianBean>();
		this.populateClinicianList();
		this.clinicianList.addListSelectionListener(this);
		this.listScrollPane = new JScrollPane(clinicianList);
		add(listScrollPane, "grow, push, span");
		// Pack and make visible
		this.updateButtonStatus();
		//this.getContentPane().add(panel);
		//this.pack();
		//this.setVisible(true);
	}

	/**
	 * Update the JList to reflect the current list of clinicians in the database
	 */
	private void populateClinicianList() {
		this.updateClinicianList();
		Collections.sort(this.localClinicians);
		DefaultListModel<ClinicianBean> newModel = new DefaultListModel<ClinicianBean>();
		for(ClinicianBean c : this.localClinicians) {
			newModel.addElement(c);
		}
		this.clinicianList.setModel(newModel);
		revalidate();
		repaint();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == this.addButton) {
			this.addNewClinicianID();			
		} else if (e.getSource() == this.removeButton) {
			this.removeClinicianID();
		} else if (e.getSource() == this.editButton) {
			CalendarDAO calendarDAO;
			try {
				calendarDAO = new CalendarDAO(ConnectionFactory.getInstance());
				CalendarBean calendarBean = calendarDAO.loadCalendar();
				JFrame frame = new JFrame("Clinician Input Form");
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				ClinicianForm form = new ClinicianForm(calendarBean.getSemester(), calendarBean.getYear(), new DateRange(calendarBean.getStartDate(), calendarBean.getEndDate()), true, clinicianList.getSelectedValue().getName());
				frame.getContentPane().add(form);
				frame.pack();
				frame.setVisible(true);
			} catch (SQLException e1) {
				handleDBException(e1);
			}
		}
		this.updateButtonStatus();
	}

	/**
	 * If there is no new clinician ID text, gray out add user button.
	 * If no selected clinician in list, gray out remove user button.
	 * @param e 
	 */
	private void updateButtonStatus() {
		boolean emptyClinicianID = this.newFullnameField.getText().isEmpty();
		this.addButton.setEnabled(!emptyClinicianID);
		boolean noListSelection = this.clinicianList.getSelectedIndex() == -1;
		this.removeButton.setEnabled(!noListSelection);
		// TODO: assert that preferences exist
		try {
			this.editButton.setEnabled(!noListSelection 
				&& prefsDAO.preferencesExist(this.clinicianList.getSelectedValue().getClinicianID()));
		} catch (SQLException e) {
			handleDBException(e);
		}
	}

	/**
	 * Adds a new clinician ID to the list, where the ID is taken
	 * from the input text box. Checks that the new ID is non-empty
	 * and that the ID does not already exist. Creates an error g
	 * if this is not the case.
	 */
	private void addNewClinicianID() {
		String newClinicianName = this.newFullnameField.getText().trim();
		this.newFullnameField.setText("");
		if(newClinicianName.isEmpty()) {
			JOptionPane.showMessageDialog(this,
					"Cannot add an empty clinician ID to the list. " +
							"Please enter a new clinician ID in the text box above.",
							"Adding empty ID",
							JOptionPane.ERROR_MESSAGE);
		}
		else if(clinicianNameExists(newClinicianName)) {
			JOptionPane.showMessageDialog(this,
					"Cannot clinician with a duplicate name to the list. " +
							"Please enter a new clinician ID in the text box above.",
							"Adding duplicate clinician",
							JOptionPane.ERROR_MESSAGE);
		}
		else {
			try {
				int newClinicianID = this.dao.getNextClinicianID();
				ClinicianBean newClinician = new ClinicianBean(newClinicianID, newClinicianName);
				this.dao.insert(newClinician);
				this.populateClinicianList();
			}
			catch(Exception e) {
				handleDBException(e);
			}			
		}
	}

	private boolean clinicianNameExists(String newClinicianName) {
		for(ClinicianBean c : this.localClinicians) {
			if(c.getName().equals(newClinicianName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Removes the selected clinician ID from the list. Checks that
	 * a clinician ID is actually selected.
	 */
	private void removeClinicianID() {
		try {
			int index = this.clinicianList.getSelectedIndex();
			ClinicianBean oldClinician = this.clinicianList.getModel().getElementAt(index);
			this.dao.delete(oldClinician.getClinicianID());
			this.populateClinicianList();
		}
		catch(Exception e) {
			handleDBException(e);
		}	
	}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {}

	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getSource() == this.newFullnameField) {
			this.updateButtonStatus();
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		this.updateButtonStatus();
	}

}
