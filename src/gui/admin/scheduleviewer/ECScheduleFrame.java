package gui.admin.scheduleviewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.ScrollPaneLayout;

import net.miginfocom.swing.MigLayout;
import bean.ECScheduleWeekBean;
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
	
	/**
	 * DAO to get schedule information from DB
	 */
	private ScheduleDAO dao;
	
	/**
	 * The scrollable content pane for weeks A & B
	 */
	private JScrollPane scrollPanel;
	/**
	 * The scrollable content pane for weeks A & B
	 */
	private JPanel panel;
	
	/**
	 * JPanel for week A
	 */
	private ArrayList<ECScheduleComponent> pages;

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
	
	
	private List<ECWeeklyComponent> weeklyComponents;
	/**
	 * Create an empty client ID list
	 * @throws SQLException 
	 */
	public ECScheduleFrame() throws SQLException {
		super("View EC Schedule");
		dao = new ScheduleDAO(ConnectionFactory.getInstance());
		ClinicianDAO cDao = new ClinicianDAO(ConnectionFactory.getInstance());
		weeklyComponents = new ArrayList<>();
		
		// Initialize menu
		this.initializeMenu();

		// Add schedule pages
		this.pages = getECSchedulePages(dao);
		
		JPanel editableSchedule = getEditableSchedule(dao, cDao);

		JPanel controlPanel = new JPanel(new FlowLayout());
		JButton saveButton = new JButton("Save");
		saveButton.addActionListener(this);
//		controlPanel.add(saveButton);
//		JButton resetButton = new JButton("Reset");
		
		this.scrollPanel = new JScrollPane(editableSchedule);
		this.scrollPanel.setPreferredSize(new Dimension(700, 800));
		this.scrollPanel.getVerticalScrollBar().setUnitIncrement(20);
		this.scrollPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		this.scrollPanel.setViewportView(editableSchedule);
		
		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.add(scrollPanel, BorderLayout.CENTER);
		contentPane.add(controlPanel, BorderLayout.SOUTH);

		// Finish
		this.setContentPane(contentPane);
		this.pack();
		this.setVisible(true);
		this.setLocationRelativeTo(null); 	// Center JFrame in middle of screen
	}

	/**
	 * Fills the schedule from the database and renders each week
	 * @throws SQLException 
	 */
	private ArrayList<ECScheduleComponent> getECSchedulePages(ScheduleDAO dao) throws SQLException {
		ArrayList<ECScheduleWeekBean> weeks = ECScheduleWeekBean.getECScheduleWeekBeans(dao);
		// Build components
		ArrayList<ECScheduleComponent> comps = new ArrayList<ECScheduleComponent>();
		int numOnFirstPage = 4;
		int numOnOtherPages = 5;
		int w = 0;
		// First page
		String title = "Spring 2015 - EC Schedule";
		ArrayList<ECScheduleWeekBean> firstWeeks = new ArrayList<ECScheduleWeekBean>();
		for(; w<numOnFirstPage && w < weeks.size(); w++) {
			firstWeeks.add(weeks.get(w));
		}
		comps.add(new ECScheduleComponent(firstWeeks, title));
		// Other pages
		while(w < weeks.size()) {
			int curW = 0;
			ArrayList<ECScheduleWeekBean> curWeeks = new ArrayList<ECScheduleWeekBean>();
			for(; curW<numOnOtherPages && w < weeks.size(); curW++) {
				curWeeks.add(weeks.get(w));
				w++;
			}
			comps.add(new ECScheduleComponent(curWeeks));
		}
		return comps;
	}
	
	/**
	 * Creates a JPanel that displays an editable EC schedule from the database
	 * @param schDAO
	 * @param cDAO
	 * @return
	 * @throws SQLException 
	 */
	private JPanel getEditableSchedule(ScheduleDAO schDAO, ClinicianDAO cDAO) throws SQLException {
		ArrayList<ECScheduleWeekBean> weeks = ECScheduleWeekBean.getECScheduleWeekBeans(dao);
		Vector<String> clinicianNames = cDAO.loadClinicianNames();
		
		
		JPanel editableSchedule = new JPanel(new GridLayout(weeks.size() + 1, 1, 0, 50));
		editableSchedule.add(new JLabel("Spring 2015 - EC Schedule"));
		for (ECScheduleWeekBean week: weeks) {
			ECWeeklyComponent wc = new ECWeeklyComponent(week, clinicianNames);
			editableSchedule.add(wc);
			weeklyComponents.add(wc);
		}
		
		editableSchedule.setBackground(Color.white);
		
		return editableSchedule;
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
		new ECScheduleFrame();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JComponent source = (JComponent) e.getSource();
		if(source == this.print) {
			ECScheduleViewFrame frame;
			try {
				frame = new ECScheduleViewFrame();
				frame.printSchedule();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		else if (e.getActionCommand().equals("Save")) {
			for (ECWeeklyComponent wc: weeklyComponents) {
				wc.saveChanges();
			}
		}
	}
}
