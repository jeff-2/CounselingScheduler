package gui.admin.scheduleviewer;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import dao.ClinicianDAO;
import dao.ConnectionFactory;
import dao.ScheduleDAO;

/**
 * A GUI window for displaying the IA appointment for weeks A and B
 * 
 * @author ramusa2, lim92
 *
 */
public class IAScheduleFrame extends JFrame implements ActionListener {

	private static final long serialVersionUID = -4271567771784608985L;
	
	/**
	 * DAO to get schedule information from DB
	 */
	private ScheduleDAO dao;
	
	private JSplitPane panel;
	
	/**
	 * JPanel for week A
	 */
	private JPanel weekA;
	
	/**
	 * JPanel for week B
	 */
	private JPanel weekB;

	/**
	 * Dropdown menu for printing (and eventually saving/loading?) the schedule
	 */
	private JMenu menu;

	/**
	 * JMenuItem for printing
	 */
	private JMenuItem print;

	/**
	 * JMenuItem for editing
	 */
	private JMenuItem edit;

	/**
	 * The graphics component for the schedule
	 */
	//private IAScheduleComponent scheduleComponent;

	/**
	 * Graphics object for rendering schedule
	 */
	//private final Graphics2D g;

	/**
	 * Create an empty client ID list
	 * @throws SQLException 
	 */
	public IAScheduleFrame() throws SQLException {
		super("View IA Schedule");
		dao = new ScheduleDAO(ConnectionFactory.getInstance());
		ClinicianDAO cDao = new ClinicianDAO(ConnectionFactory.getInstance());
		List<String> clinicianNames = cDao.loadClinicianNames();
		this.panel = new JSplitPane();
		this.panel.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		this.panel.setResizeWeight(0.5);
		this.panel.setDividerSize(25);
		this.setContentPane(this.panel);
		this.weekA = new IAWeeklyComponent(dao.loadScheduleType(0), clinicianNames, "A");
		this.weekB = new IAWeeklyComponent(dao.loadScheduleType(1), clinicianNames, "B");
		this.panel.setLeftComponent(weekA);
		this.panel.setRightComponent(weekB);
		this.initializeFrame();
		this.setLocationRelativeTo(null); 	// Center JFrame in middle of screen
	}

	/**
	 * Set up the components of this JFrame, pack, and make it visible
	 */
	private void initializeFrame() {
		// Initialize menu
		this.initializeMenu();
		// Set preferred size
		this.getContentPane().setPreferredSize(new Dimension(1200, 800));
		// Draw stuff
		//this.add(panel);
		//this.getContentPane().add(scheduleComponent);
		// Finish
		//this.getContentPane().add(panel);
		this.pack();
		this.setVisible(true);
	}

	/**
	 * Set up the dropdown menu
	 */
	private void initializeMenu() {
		this.menu = new JMenu("Options");
		/*
		this.edit = new JMenuItem("Edit schedule");
		this.edit.setEnabled(false);
		this.menu.add(this.edit);
		this.menu.add(new JSeparator());
		*/
		this.print = new JMenuItem("Print");
		this.print.addActionListener(this);
		this.menu.add(this.print);
		JMenuBar mb = new JMenuBar();
		mb.add(menu);
		this.setJMenuBar(mb);
	}

	/**
	 * Main tester
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws SQLException {
		IAScheduleFrame frame = new IAScheduleFrame();
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == this.print) {
			IAScheduleViewFrame frame;
			try {
				frame = new IAScheduleViewFrame();
				frame.printSchedule();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
}
