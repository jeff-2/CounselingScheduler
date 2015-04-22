package gui.admin.scheduleviewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

import bean.ECScheduleWeekBean;
import bean.Schedule;
import dao.ClinicianDAO;
import dao.ConnectionFactory;
import dao.ScheduleDAO;

/**
 * A GUI window for displaying the EC appointments for a semester
 * 
 * @author ramusa2, lim92
 *
 */
public class ECScheduleFrame extends JFrame implements ActionListener {

	private static final long serialVersionUID = -4271567771784608985L;
	
	private ScheduleDAO dao;
	private ClinicianDAO clinicianDao;
	private JScrollPane scrollPanel;
	private JMenu menu;
	private JMenuItem print;
	private JMenuItem save;
	private List<ECWeeklyComponent> ecComponents;
	private JButton resetButton;
	private JPanel controlPanel;
	private JFileChooser fileChooser;
	private Schedule schedule;
	
	/**
	 * Create an empty client ID list
	 * @throws SQLException 
	 */
	public ECScheduleFrame(Schedule s) throws SQLException {
		super("View EC Schedule");
		dao = new ScheduleDAO(ConnectionFactory.getInstance());
		clinicianDao = new ClinicianDAO(ConnectionFactory.getInstance());
		this.schedule = s;
		this.scrollPanel = new JScrollPane();

		// Initialize menu
		this.initializeMenu();
		
		this.scrollPanel = new JScrollPane();
		loadEditableSchedule();
		this.scrollPanel.setPreferredSize(new Dimension(700, 800));
		this.scrollPanel.getVerticalScrollBar().setUnitIncrement(20);
		this.scrollPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Save Schedule");
		fileChooser.setFileFilter(new FileNameExtensionFilter("PNG file", "png"));
		
		resetButton = new JButton("Reset");
		resetButton.addActionListener(this);
		
		controlPanel = new JPanel(new FlowLayout());
		controlPanel.add(resetButton);
		
		// Finish
		this.add(scrollPanel, BorderLayout.CENTER);
		this.add(controlPanel, BorderLayout.SOUTH);
		this.pack();
		this.setVisible(true);
		this.setLocationRelativeTo(null); 	// Center JFrame in middle of screen
	}

	/**
	 * Loads a JPanel that displays an editable EC schedule with data from the database
	 * @throws SQLException 
	 */
	private void loadEditableSchedule() throws SQLException {
		ArrayList<ECScheduleWeekBean> weeks = ECScheduleWeekBean.getECScheduleWeekBeans(schedule);
		Vector<String> clinicianNames = clinicianDao.loadClinicianNames();
		ecComponents = new ArrayList<>();
		
		JPanel editableSchedule = new JPanel(new GridLayout(weeks.size() + 1, 1, 0, 50));
		editableSchedule.add(new JLabel("Spring 2015 - EC Schedule"));
		for (ECScheduleWeekBean week: weeks) {
			ECWeeklyComponent curr = new ECWeeklyComponent(week, clinicianNames);
			editableSchedule.add(curr);
			ecComponents.add(curr);
		}
		
		editableSchedule.setBackground(Color.white);
		
		scrollPanel.setViewportView(editableSchedule);
		repaint();
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
		this.save = new JMenuItem("Save");
		this.save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		this.save.setName("Save");
		this.save.addActionListener(this);
		this.menu.add(this.save);
		this.menu.add(this.print);
		JMenuBar mb = new JMenuBar();
		mb.add(menu);
		this.setJMenuBar(mb);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		List<List<List<String>>> cells = new ArrayList<List<List<String>>>();
		for (ECWeeklyComponent comp : ecComponents) {
			List<List<String>> l = comp.toCellsList();
			cells.add(l);
		}
		
		if(e.getSource() == this.print) {
			try {
				ECScheduleViewFrame frame = new ECScheduleViewFrame(new ECScheduleComponent(cells));
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
					new ECScheduleComponent(cells).save(file);
				} catch (IOException e2) {
					JOptionPane.showMessageDialog(this,
						"Unable to save to file: " + file.getAbsolutePath() + ". Please try again.",
						"Error saving schedule",
						JOptionPane.ERROR_MESSAGE);
				}
			}
		} else if (e.getSource() == this.resetButton) {
				try {
					this.loadEditableSchedule();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
		}
	}
}
