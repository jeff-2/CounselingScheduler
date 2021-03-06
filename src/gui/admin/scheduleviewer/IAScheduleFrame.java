package gui.admin.scheduleviewer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import bean.IAWeektype;
import bean.Schedule;
import dao.ClinicianDAO;
import dao.ConnectionFactory;

/**
 * A GUI window for displaying the IA appointment for weeks A and B.
 *
 * @author ramusa2, lim92
 */
public class IAScheduleFrame extends JPanel implements ActionListener {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -4271567771784608985L;

	/** The clinician dao. */
	private ClinicianDAO clinicianDao;

	/** The panel. */
	private JSplitPane panel;

	/** The week a. */
	private JPanel weekA;

	/** The week b. */
	private JPanel weekB;

	/** The reset button. */
	private JButton resetButton;

	/** The control panel. */
	private JPanel controlPanel;

	/** The file chooser. */
	private JFileChooser fileChooser;

	/** The schedule. */
	private Schedule schedule;

	/**
	 * Create an empty client ID list.
	 *
	 * @param s
	 *            the schedule
	 * @throws SQLException
	 *             the SQL exception
	 */
	public IAScheduleFrame(Schedule s) throws SQLException {
		clinicianDao = new ClinicianDAO(ConnectionFactory.getInstance());
		this.schedule = s;

		this.panel = new JSplitPane();
		this.panel.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		this.panel.setResizeWeight(0.5);
		this.panel.setDividerSize(25);
		this.panel.setPreferredSize(new Dimension(1000, 750));
		this.loadEditableSchedule();

		resetButton = new JButton("Reset");
		resetButton.addActionListener(this);

		controlPanel = new JPanel(new FlowLayout());
		controlPanel.add(resetButton);

		fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Save Schedule");
		fileChooser
				.setFileFilter(new FileNameExtensionFilter("PNG file", "png"));

		this.add(this.panel, BorderLayout.CENTER);
		this.add(controlPanel, BorderLayout.SOUTH);
	}

	/**
	 * Loads a JPanel that displays an editable IA schedule with data from the
	 * database.
	 *
	 * @throws SQLException
	 *             the SQL exception
	 */
	private void loadEditableSchedule() throws SQLException {
		List<String> clinicianNames = clinicianDao.loadClinicianNames();

		this.weekA = new IAWeeklyComponent(schedule.getIASessionsA(),
				clinicianNames, IAWeektype.A, schedule);
		this.weekB = new IAWeeklyComponent(schedule.getIASessionsB(),
				clinicianNames, IAWeektype.B, schedule);

		this.panel.setLeftComponent(weekA);
		this.panel.setRightComponent(weekB);
		repaint();
	}

	/**
	 * Saves the IA schedule to an image.
	 */
	public void save() {
		if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			if (!file.getName().contains(".")) {
				file = new File(file.getAbsoluteFile() + ".png");
			}
			try {
				new IAScheduleComponent(schedule.getSemesterTitle()
						+ " - IA Schedule",
						((IAWeeklyComponent) this.weekA).toCellsArray(),
						((IAWeeklyComponent) this.weekB).toCellsArray())
						.save(file);
			} catch (IOException e2) {
				JOptionPane.showMessageDialog(this, "Unable to save to file: "
						+ file.getAbsolutePath() + ". Please try again.",
						"Error saving schedule", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * Prints the IA schedule.
	 */
	public void print() {
		try {
			IAScheduleViewFrame frame = new IAScheduleViewFrame(
					schedule.getSemesterTitle() + " - IA Schedule",
					((IAWeeklyComponent) this.weekA).toCellsArray(),
					((IAWeeklyComponent) this.weekB).toCellsArray());
			frame.printSchedule();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == this.resetButton) {
			try {
				schedule = Schedule.loadScheduleFromDBAndAssignClinicians();
				this.loadEditableSchedule();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}
}
