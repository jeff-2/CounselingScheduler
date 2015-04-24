package gui.admin;

import generator.TestDataGenerator;
import gui.admin.scheduleviewer.ECScheduleFrame;
import gui.admin.scheduleviewer.IAScheduleFrame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;

import dao.ConnectionFactory;
import action.GenerateUnfilledScheduleAction;
import bean.Schedule;

public class AdminApplication extends JFrame implements ActionListener {
	
	private static final long serialVersionUID = 8557735241347619359L;
	private JTabbedPane pane;
	private ClinicianIDListEditor editor;
	private NewSemesterSettings settings;
	private IAScheduleFrame ia;
	private ECScheduleFrame ec;
	private JMenuBar menuBar;
	private JMenu menu;
	private JMenuItem print;
	private JMenuItem save;
	private JMenuItem generate;
	private Schedule schedule;
	
	public AdminApplication() throws SQLException {
		super("Some Title");
		schedule = Schedule.loadScheduleFromDB();
		ia = new IAScheduleFrame(schedule);
		ec = new ECScheduleFrame(schedule);
		pane = new JTabbedPane();
		editor = new ClinicianIDListEditor();
		settings = new NewSemesterSettings();
		pane.add(editor, "Edit Clinicians");
		pane.add(settings, "Change Settings");
		pane.add(ia, "IA Schedule");
		pane.add(ec, "EC schedule");
		initializeMenu();
		initializeFrame();
	}
	
	private void initializeMenu() {
		menuBar = new JMenuBar();
		menu = new JMenu("File");
		generate = new JMenuItem("Generate Schedule");
		generate.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, ActionEvent.CTRL_MASK));
		generate.addActionListener(this);
		print = new JMenuItem("Print");
		print.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK));
		print.addActionListener(this);
		save = new JMenuItem("Save");
		save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		save.addActionListener(this);
		menu.add(generate);
		menu.add(print);
		menu.add(save);
		menuBar.add(menu);
	}
	
	private void initializeFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setJMenuBar(menuBar);
		getContentPane().add(pane);
		pack();
		setVisible(true);
	}
	
	public static void main(String [] args) throws SQLException, ParseException {
		TestDataGenerator gen = new TestDataGenerator(ConnectionFactory.getInstance());
		gen.overwriteAndFillDemoData();
		AdminApplication tmp = new AdminApplication();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == generate) {
			try {
				Connection conn = ConnectionFactory.getInstance();
				GenerateUnfilledScheduleAction action = new GenerateUnfilledScheduleAction(conn);
				action.generateUnfilledSchedule();
				schedule = Schedule.loadScheduleFromDBAndAssignClinicians();
				pane.remove(ia);
				ia = new IAScheduleFrame(schedule);
				pane.add(ia, "IA Schedule");
				pane.remove(ec);
				ec = new ECScheduleFrame(schedule);
				pane.add(ec, "EC Schedule");
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		} else if (e.getSource() == print) {
			if (pane.getSelectedComponent() == ec) {
				ec.print();
			} else if (pane.getSelectedComponent() == ia) {
				ia.print();
			}
		} else if (e.getSource() == save) {
			if (pane.getSelectedComponent() == ec) {
				ec.save();
			} else if (pane.getSelectedComponent() == ia) {
				ia.save();
			}
		}
	}
}
