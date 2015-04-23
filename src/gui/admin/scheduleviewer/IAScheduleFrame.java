package gui.admin.scheduleviewer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileNameExtensionFilter;

import bean.IAWeektype;
import bean.Schedule;
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
	
	private ScheduleDAO dao;
	private ClinicianDAO clinicianDao;
	private JSplitPane panel;
	private JPanel weekA;
	private JPanel weekB;
	private JMenu menu;
	private JMenuItem print;
	private JMenuItem save;
	private JButton resetButton;
	private JPanel controlPanel;
	private JFileChooser fileChooser;
	private Schedule schedule;
	/**
	 * Create an empty client ID list
	 * @throws SQLException 
	 */
	public IAScheduleFrame(Schedule s) throws SQLException {
		super("View IA Schedule");
		dao = new ScheduleDAO(ConnectionFactory.getInstance());
		clinicianDao = new ClinicianDAO(ConnectionFactory.getInstance());
		this.schedule = s;
		
		this.panel = new JSplitPane();
		this.panel.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		this.panel.setResizeWeight(0.5);
		this.panel.setDividerSize(25);
		this.loadEditableSchedule();
		
		resetButton = new JButton("Reset");
		resetButton.addActionListener(this);
		
		controlPanel = new JPanel(new FlowLayout());
		controlPanel.add(resetButton);
		
		fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Save Schedule");
		fileChooser.setFileFilter(new FileNameExtensionFilter("PNG file", "png"));


		this.add(this.panel, BorderLayout.CENTER);
		this.add(controlPanel, BorderLayout.SOUTH);
		this.initializeFrame();
		this.setLocationRelativeTo(null); 	// Center JFrame in middle of screen
	}
	
	/**
	 * Loads a JPanel that displays an editable IA schedule with data from the database
	 * @throws SQLException 
	 */
	private void loadEditableSchedule() throws SQLException {
		List<String> clinicianNames = clinicianDao.loadClinicianNames();
		
		this.weekA = new IAWeeklyComponent(schedule.getIASessionsA(), clinicianNames, IAWeektype.A);
		this.weekB = new IAWeeklyComponent(schedule.getIASessionsB(), clinicianNames, IAWeektype.B);
		
		this.panel.setLeftComponent(weekA);
		this.panel.setRightComponent(weekB);
		repaint();
	}

	/**
	 * Set up the components of this JFrame, pack, and make it visible
	 */
	private void initializeFrame() {
		this.initializeMenu();
		this.getContentPane().setPreferredSize(new Dimension(1200, 800));
		this.pack();
		this.setVisible(true);
	}

	/**
	 * Set up the dropdown menu
	 */
	private void initializeMenu() {
		this.menu = new JMenu("Options");
		this.menu.setName("Options");
		this.print = new JMenuItem("Print");
		this.print.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK));
		this.print.addActionListener(this);
		this.menu.add(this.print);
		this.save = new JMenuItem("Save");
		this.save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		this.save.setName("Save");
		this.save.addActionListener(this);
		this.menu.add(this.save);
		JMenuBar mb = new JMenuBar();
		mb.add(menu);
		this.setJMenuBar(mb);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == this.print) {
			IAScheduleViewFrame frame;
			try {
				frame = new IAScheduleViewFrame(((IAWeeklyComponent)this.weekA).toCellsArray(), ((IAWeeklyComponent)this.weekB).toCellsArray());
				frame.printSchedule();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} else if (e.getSource() == this.save) {
			if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				if (!file.getName().contains(".")) {
					file = new File(file.getAbsoluteFile() + ".png");
				}
				try {
					new IAScheduleComponent(((IAWeeklyComponent)this.weekA).toCellsArray(), ((IAWeeklyComponent)this.weekB).toCellsArray()).save(file);
				} catch (IOException e2) {
					JOptionPane.showMessageDialog(this,
						"Unable to save to file: " + file.getAbsolutePath() + ". Please try again.",
						"Error saving schedule",
						JOptionPane.ERROR_MESSAGE);
				}
			}
		} else if (e.getSource() == this.resetButton){
			try {
				this.loadEditableSchedule();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}
}
