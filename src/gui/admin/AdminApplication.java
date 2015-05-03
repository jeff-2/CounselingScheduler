package gui.admin;

import gui.admin.scheduleviewer.ECScheduleFrame;
import gui.admin.scheduleviewer.IAScheduleFrame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import action.GenerateUnfilledScheduleAction;
import bean.ClinicianBean;
import bean.Schedule;
import dao.ClinicianDAO;
import dao.ClinicianPreferencesDAO;
import dao.ConnectionFactory;

/**
 * The Class AdminApplication provides the interface for the administrator to
 * setup and generate/edit the schedule. Combines admin components to provide
 * this functionality.
 */
public class AdminApplication extends JFrame implements ActionListener,
		ChangeListener {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 8557735241347619359L;

	/** The tabbed pane. */
	private JTabbedPane tabbedPane;

	/** The editor. */
	private ClinicianIDListEditor editor;

	/** The settings. */
	private NewSemesterSettings settings;

	/** The ia. */
	private IAScheduleFrame ia;

	/** The ec. */
	private ECScheduleFrame ec;

	/** The menu bar. */
	private JMenuBar menuBar;

	/** The menu. */
	private JMenu menu;

	/** The print. */
	private JMenuItem print;

	/** The save. */
	private JMenuItem save;

	/** The generate. */
	private JMenuItem generate;

	/** The schedule. */
	private Schedule schedule;

	/**
	 * Instantiates a new admin application.
	 *
	 * @throws SQLException
	 *             the SQL exception
	 */
	public AdminApplication() throws SQLException {
		super("Generate IA/EC schedule");
		schedule = Schedule.loadScheduleFromDB();
		ia = new IAScheduleFrame(schedule);
		ec = new ECScheduleFrame(schedule);
		ia.setName("IAScheduleFrame");
		ec.setName("ECScheduleFrame");
		tabbedPane = new JTabbedPane();
		tabbedPane.addChangeListener(this);
		tabbedPane.setName("tabbedPane");
		editor = new ClinicianIDListEditor();
		editor.setName("ClinicianIDListEditor");
		settings = new NewSemesterSettings();
		settings.setName("NewSemesterSettings");
		tabbedPane.add(editor, "Edit Clinicians");
		tabbedPane.add(settings, "Change Settings");
		tabbedPane.add(ia, "IA Schedule");
		tabbedPane.add(ec, "EC Schedule");
		initializeMenu();
		initializeFrame();
	}

	/**
	 * Initialize menu.
	 */
	private void initializeMenu() {
		menuBar = new JMenuBar();
		menu = new JMenu("File");
		generate = new JMenuItem("Generate Schedule");
		generate.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G,
				ActionEvent.CTRL_MASK));
		generate.addActionListener(this);
		generate.setName("Generate Schedule");
		print = new JMenuItem("Print");
		print.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P,
				ActionEvent.CTRL_MASK));
		print.addActionListener(this);
		print.setName("Print");
		print.setEnabled(false);
		save = new JMenuItem("Save");
		save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				ActionEvent.CTRL_MASK));
		save.addActionListener(this);
		save.setName("Save");
		save.setEnabled(false);
		menu.add(generate);
		menu.add(print);
		menu.add(save);
		menuBar.add(menu);
	}

	/**
	 * Initialize frame.
	 */
	private void initializeFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setJMenuBar(menuBar);
		getContentPane().add(tabbedPane);
		pack();
		setVisible(true);
	}

	/**
	 * Runs the application.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.out
					.println("Can't set system look and feel. Using default.");
		}
		try {
			new AdminApplication();
		} catch (SQLException e) {
			JOptionPane
					.showMessageDialog(
							null,
							"Unable to launch application due to error with database. "
									+ "Please check the database connection configuration properties file. ",
							"ERROR", JOptionPane.INFORMATION_MESSAGE);
			e.printStackTrace();
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
		if (e.getSource() == generate) {
			// ensure that all clinician preferences are entered first
			List<ClinicianBean> clinicians = null;
			try {
				ClinicianDAO clinicianDAO = new ClinicianDAO(
						ConnectionFactory.getInstance());
				clinicians = clinicianDAO.loadClinicians();

				if (clinicians.size() == 0) {
					JOptionPane
							.showMessageDialog(
									null,
									"A schedule can't be generated without any clinicians.",
									"ERROR", JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				ClinicianPreferencesDAO preferencesDAO = new ClinicianPreferencesDAO(
						ConnectionFactory.getInstance());
				for (ClinicianBean clinician : clinicians) {
					if (!preferencesDAO.preferencesExist(clinician
							.getClinicianID())) {
						JOptionPane
								.showMessageDialog(
										null,
										"A schedule can't be generated because "
												+ clinician.getName()
												+ " has not entered his/her preferences.",
										"ERROR",
										JOptionPane.INFORMATION_MESSAGE);
						return;
					}
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
			try {
				Connection conn = ConnectionFactory.getInstance();
				GenerateUnfilledScheduleAction action = new GenerateUnfilledScheduleAction(
						conn);
				action.generateUnfilledSchedule();
				schedule = Schedule.loadScheduleFromDBAndAssignClinicians();
				tabbedPane.remove(ia);
				ia = new IAScheduleFrame(schedule);
				ia.setName("IAScheduleFrame");
				tabbedPane.add(ia, "IA Schedule");
				tabbedPane.remove(ec);
				ec = new ECScheduleFrame(schedule);
				ec.setName("ECScheduleFrame");
				tabbedPane.add(ec, "EC Schedule");
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		} else if (e.getSource() == print) {
			if (tabbedPane.getSelectedComponent() == ec) {
				ec.print();
			} else if (tabbedPane.getSelectedComponent() == ia) {
				ia.print();
			}
		} else if (e.getSource() == save) {
			if (tabbedPane.getSelectedComponent() == ec) {
				ec.save();
			} else if (tabbedPane.getSelectedComponent() == ia) {
				ia.save();
			}
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		// only enable print and save options when viewing ia or ec schedule
		if (e.getSource() == tabbedPane && save != null && print != null) {
			if (tabbedPane.getSelectedComponent() == ec
					|| tabbedPane.getSelectedComponent() == ia) {
				save.setEnabled(true);
				print.setEnabled(true);
			} else if (tabbedPane.getSelectedComponent() == editor
					|| tabbedPane.getSelectedComponent() == settings) {
				save.setEnabled(false);
				print.setEnabled(false);
			}
		}
	}
}
