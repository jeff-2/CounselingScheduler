package gui.admin.scheduleviewer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

import bean.ECScheduleWeekBean;
import bean.Schedule;
import dao.ClinicianDAO;
import dao.ConnectionFactory;

/**
 * A GUI window for displaying the EC appointments for a semester.
 *
 * @author ramusa2, lim92
 */
public class ECScheduleFrame extends JPanel implements ActionListener {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -4271567771784608985L;

	/** The clinician dao. */
	private ClinicianDAO clinicianDao;

	/** The scroll panel. */
	private JScrollPane scrollPanel;

	/** The EC components. */
	private List<ECWeeklyComponent> ecComponents;

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
	public ECScheduleFrame(Schedule s) throws SQLException {
		clinicianDao = new ClinicianDAO(ConnectionFactory.getInstance());
		this.schedule = s;
		this.scrollPanel = new JScrollPane();
		loadEditableSchedule();
		this.scrollPanel.setPreferredSize(new Dimension(700, 750));
		this.scrollPanel.getVerticalScrollBar().setUnitIncrement(20);
		this.scrollPanel
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Save Schedule");
		fileChooser
				.setFileFilter(new FileNameExtensionFilter("PNG file", "png"));

		resetButton = new JButton("Reset");
		resetButton.addActionListener(this);

		controlPanel = new JPanel(new FlowLayout());
		controlPanel.add(resetButton);

		this.setLayout(new FlowLayout());
		this.add(scrollPanel);
		this.add(controlPanel);
	}

	/**
	 * Loads a JPanel that displays an editable EC schedule with data from the
	 * database.
	 *
	 * @throws SQLException
	 *             the SQL exception
	 */
	private void loadEditableSchedule() throws SQLException {
		ArrayList<ECScheduleWeekBean> weeks = ECScheduleWeekBean
				.getECScheduleWeekBeans(schedule);
		Vector<String> clinicianNames = clinicianDao.loadClinicianNames();
		ecComponents = new ArrayList<>();

		JPanel editableSchedule = new JPanel(new GridLayout(weeks.size() + 1,
				1, 0, 50));
		editableSchedule.add(new JLabel(schedule.getSemesterTitle()
				+ " - EC Schedule"));
		for (ECScheduleWeekBean week : weeks) {
			ECWeeklyComponent curr = new ECWeeklyComponent(week,
					clinicianNames, schedule);
			editableSchedule.add(curr);
			ecComponents.add(curr);
		}
		editableSchedule.setBackground(Color.white);
		scrollPanel.setViewportView(editableSchedule);
		repaint();
	}

	/**
	 * Gets the representation of the EC schedule as a grid of strings.
	 *
	 * @return the cells as strings in the ec schedule
	 */
	private List<List<List<String>>> getCells() {
		List<List<List<String>>> cells = new ArrayList<List<List<String>>>();
		for (ECWeeklyComponent comp : ecComponents) {
			List<List<String>> l = comp.toCellsList();
			cells.add(l);
		}
		return cells;
	}

	/**
	 * Saves the EC schedule to an image file.
	 */
	public void save() {
		List<List<List<String>>> cells = getCells();
		if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			if (!file.getName().contains(".")) {
				file = new File(file.getAbsoluteFile() + ".png");
			}
			try {
				new ECScheduleComponent(schedule.getSemesterTitle()
						+ " - EC Schedule", cells).save(file);
			} catch (IOException e2) {
				JOptionPane.showMessageDialog(this, "Unable to save to file: "
						+ file.getAbsolutePath() + ". Please try again.",
						"Error saving schedule", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * Prints the EC schedule.
	 */
	public void print() {
		List<List<List<String>>> cells = getCells();
		try {
			ECScheduleViewFrame frame = new ECScheduleViewFrame(
					new ECScheduleComponent(schedule.getSemesterTitle()
							+ " - EC Schedule", cells));
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
