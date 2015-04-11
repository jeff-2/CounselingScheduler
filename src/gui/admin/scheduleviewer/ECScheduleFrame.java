package gui.admin.scheduleviewer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

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
	private JPanel editableSchedule;

	/**
	 * Dropdown menu for printing (and eventually saving/loading?) the schedule
	 */
	private JMenu menu;

	/**
	 * JMenuItem for printing
	 */
	private JMenuItem print;
	
	private JMenuItem save;
	
	private List<ECWeeklyComponent> ecComponents;
	
	/**
	 * Create an empty client ID list
	 * @throws SQLException 
	 */
	public ECScheduleFrame() throws SQLException {
		super("View EC Schedule");
		dao = new ScheduleDAO(ConnectionFactory.getInstance());
		ClinicianDAO cDao = new ClinicianDAO(ConnectionFactory.getInstance());
		this.scrollPanel = new JScrollPane();

		JPanel p = new JPanel(new MigLayout("gap rel 0", "grow"));
		p.setBackground(Color.WHITE);

		// Initialize menu
		this.initializeMenu();
		
		ecComponents = new ArrayList<ECWeeklyComponent>();
		editableSchedule = getEditableSchedule(dao, cDao);
		
		this.scrollPanel = new JScrollPane(editableSchedule);
		this.scrollPanel.setPreferredSize(new Dimension(700, 800));
		this.scrollPanel.getVerticalScrollBar().setUnitIncrement(20);
		this.scrollPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		this.scrollPanel.setViewportView(editableSchedule);
		
	
		// Finish
		this.getContentPane().add(scrollPanel);
		this.pack();
		this.setVisible(true);
		this.setLocationRelativeTo(null); 	// Center JFrame in middle of screen
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
			ECWeeklyComponent curr = new ECWeeklyComponent(week, clinicianNames);
			editableSchedule.add(curr);
			ecComponents.add(curr);
		}
		
		editableSchedule.setBackground(Color.white);
		
		return editableSchedule;
	}

	/**
	 * Set up the dropdown menu
	 */
	private void initializeMenu() {
		this.menu = new JMenu("Options");
		this.menu.setName("Options");
		this.print = new JMenuItem("Print");
		this.print.addActionListener(this);
		this.save = new JMenuItem("Save");
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
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setDialogTitle("Save Schedule");
			fileChooser.setFileFilter(new FileNameExtensionFilter("PNG file", "png"));
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
		}
	}
}
