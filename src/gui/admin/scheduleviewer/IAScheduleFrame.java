package gui.admin.scheduleviewer;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.filechooser.FileNameExtensionFilter;

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
	private JSplitPane panel;
	private JPanel weekA;
	private JPanel weekB;
	private JMenu menu;
	private JMenuItem print;
	private JMenuItem edit;	
	private JMenuItem save;

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
		this.print = new JMenuItem("Print");
		this.print.addActionListener(this);
		this.menu.add(this.print);
		this.save = new JMenuItem("Save");
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
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileFilter(new FileNameExtensionFilter("PNG file", "png"));
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
		}
	}
}
