package gui.admin;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;

import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import utils.Logger;
import net.miginfocom.swing.MigLayout;


/**
 * A GUI element for selecting an admin routine to run. Current tasks are:
 * 
 *  (1) Setting Semester parameters
 *  (2) Editing the clinician ID list
 *  *(3) Setting meeting schedules for a semester
 *  (4) Generating a schedule
 * 
 * @author ramusa2, jmfoste2
 * 
 */
public class AdminTaskSelector extends JFrame implements ActionListener {

	/**
	 * Generated serial version UID
	 */
	private static final long serialVersionUID = -5684205646507101495L;

	/**
	 * The main panel for the GUI frame
	 */
	private final JPanel panel;

	/**
	 * JLabel for task selection
	 */
	private JLabel instrLabel;

	/**
	 * ButtonGroup for selecting the current admin task
	 */
	private ButtonGroup choicesGroup;

	/**
	 * JRadioButton for selecting "set semester parameters"
	 */
	private JRadioButton setSemParamsButton;
	private String semParamsString = "Set semester parameters";

	/**
	 * JRadioButton for selecting "view/edit clinicians"
	 */
	private JRadioButton editCliniciansButton;
	private String editCliniciansString = "View/edit clinicians";

	/**
	 * JRadioButton for selecting "view/edit meetings"
	 */
	private JRadioButton editMeetingsButton;
	private String editMeetingsString = "View/edit meetings";

	/**
	 * JRadioButton for selecting "generate schedule"
	 */
	private JRadioButton generateScheduleButton;
	private String generateScheduleString = "Generate schedule";

	/**
	 * JButton for running the currently-selected item
	 */
	private JButton runButton;

	/**
	 * Create an empty client ID list
	 */
	public AdminTaskSelector() {
		super("Select Admin Task");
		this.panel = new JPanel(new MigLayout("gap rel 0", "grow"));
		this.initializeFrame();
		this.setCloseBehavior();
		this.setLocationRelativeTo(null); 	// Center JFrame in middle of screen
	}


	/**
	 * Set up the components of this JFrame, pack, and make it visible
	 */
	private void initializeFrame() {
		// Set preferred size
		this.panel.setPreferredSize( new Dimension( 300, 180 ) );
		// Set exit behavior
		//this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Add radio buttons for each task
		this.instrLabel = new JLabel("Choose a task below:");
		this.panel.add(this.instrLabel, "span");
		this.panel.add(new JLabel(" "), "span");
		this.choicesGroup = new ButtonGroup();
		this.setSemParamsButton = this.addRadioButton(this.semParamsString);
		this.editCliniciansButton = this.addRadioButton(this.editCliniciansString);
		this.editMeetingsButton = this.addRadioButton(this.editMeetingsString);
		this.generateScheduleButton = this.addRadioButton(this.generateScheduleString);

		this.panel.add(new JLabel(" "), "span");
		this.runButton = new JButton("Run task");
		this.runButton.setEnabled(false);
		this.runButton.addActionListener(this);
		this.panel.add(this.runButton, "grow");

		// Pack and make visible
		this.getContentPane().add(panel);
		this.pack();
		this.setVisible(true);
	}

	/**
	 * When closing the frame, log this event
	 */
	private void setCloseBehavior() {
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				Logger.logln("Closing admin application.");
				Logger.closeFileForLogging();
				//this.dispose();
			}
		});
	}


	/**
	 * Helper method for adding a button to the panel/button group
	 * @param button
	 * @param desc
	 */
	private JRadioButton addRadioButton(String desc) {
		JRadioButton button = new JRadioButton(desc);
		button.setActionCommand(desc);
		button.addActionListener(this);
		choicesGroup.add(button);	
		this.panel.add(button, "gapleft 30, span");
		return button;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == this.setSemParamsButton) {
			this.runButton.setEnabled(true);
		}
		else if(e.getSource() == this.editCliniciansButton) {
			this.runButton.setEnabled(true);
		}
		else if(e.getSource() == this.editMeetingsButton) {
			this.runButton.setEnabled(true);
		}
		else if(e.getSource() == this.generateScheduleButton) {
			this.runButton.setEnabled(true);
		}
		else if(e.getSource() == this.runButton) {
			ButtonModel cur = this.choicesGroup.getSelection();
			if(cur == this.setSemParamsButton.getModel()) {
				this.setSemesterParams();
			}
			else if(cur == this.editCliniciansButton.getModel()) {
				this.editClinicians();
			}
			else if(cur == this.editMeetingsButton.getModel()) {
				this.editMeetings();
			}
			else if(cur == this.generateScheduleButton.getModel()) {
				try {
					this.generateSchedule();
				}
				catch(SQLException ex) {
					JOptionPane.showMessageDialog(this,
							"Failed to connect to the remote SQL database; please contact the network administrator.",
							"Database connection error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
			this.runButton.setEnabled(false);
			this.choicesGroup.clearSelection();
		}
	}


	private void setSemesterParams() {
		NewSemesterSettings semester = new NewSemesterSettings();
		semester.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		Logger.logln("Started edit semester settings task.");
	}


	private void editClinicians() {
		ClinicianIDListEditor editor = new ClinicianIDListEditor();
		editor.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		Logger.logln("Started edit clinicians task.");
	}


	private void editMeetings() {
		// TODO Implement me!
		Logger.logln("Tried to start edit meetings task.");
		JOptionPane.showMessageDialog(this,
				"TODO: Implement edit meetings task",
				"TODO: Implement edit meetings task",
				JOptionPane.WARNING_MESSAGE);
	}


	private void generateSchedule() throws SQLException {
		// Fill empty slots in Sessions table
		// TODO: run TestDataGenerator.generateStandardDataset before demoing this part
		//GenerateUnfilledScheduleAction genAction = new GenerateUnfilledScheduleAction(ConnectionFactory.getInstance());
		//genAction.generateUnfilledSchedule();
		// TODO Implement the rest of me!
		Logger.logln("Tried to start generate schedule task.");
		JOptionPane.showMessageDialog(this,
				"TODO: Implement generate schedule task",
				"TODO: Implement generate schedule task",
				JOptionPane.WARNING_MESSAGE);
	}
}