package gui.admin.scheduleviewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
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
import bean.ECScheduleWeekViewBean;
import dao.CalendarDAO;
import dao.ConnectionFactory;
import dao.HolidayDAO;
import dao.ScheduleDAO;

/**
 * A GUI window for displaying the EC appointments for a semester
 * 
 * @author ramusa2, lim92
 *
 */
public class ECScheduleViewFrame extends JFrame implements ActionListener {

	private static final long serialVersionUID = -4271567771784608985L;
	
	/**
	 * DAO to get schedule information from DB
	 */
	private ScheduleDAO schedDao;
	/**
	 * DAO to get holiday information from DB
	 */
	private HolidayDAO holiDao;
	/**
	 * DAO to get calendar information from DB
	 */
	private CalendarDAO calDao;
	
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
	private ArrayList<ECScheduleViewComponent> pages;

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
	 * Create an empty client ID list
	 * @throws SQLException 
	 */
	public ECScheduleViewFrame() throws SQLException {
		super("View EC Schedule");
		Connection conn = ConnectionFactory.getInstance();
		schedDao = new ScheduleDAO(conn);
		calDao = new CalendarDAO(conn);
		holiDao = new HolidayDAO(conn);
		this.scrollPanel = new JScrollPane();

		JPanel p = new JPanel(new MigLayout("gap rel 0", "grow"));
		//JPanel p = new JPanel(); 
		//p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.setBackground(Color.WHITE);
		//p.setMinimumSize(new Dimension(700, 800));
		//p.setPreferredSize(new Dimension(700, 800));

		// Initialize menu
		this.initializeMenu();
		// Set preferred size
		//this.getContentPane().setPreferredSize( new Dimension(700, 800) );
		//this.panel.setPreferredSize( new Dimension(700, 800) );
		// Add schedule pages
		this.pages = getECSchedulePages();
		
		
		for(ECScheduleViewComponent ecComp : this.pages) {
			//p.setPreferredSize(new Dimension(p.getWidth(), p.getHeight()+ecComp.getHeight()));
			//p.setMaximumSize(new Dimension(700, 999999));
			p.add(ecComp, "span, grow, wrap 15px");
			//p.revalidate();
			//p.repaint();
		}		
		
		//p.add(this.pages.get(0));
		//p.add(this.pages.get(1));
		//p.add(this.pages.get(2));
		p.validate();
		this.scrollPanel = new JScrollPane(p);
		this.scrollPanel.setPreferredSize(new Dimension(700, 800));
		this.scrollPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		//this.scrollPanel.
		this.scrollPanel.setViewportView(p);
		
		
		// Set exit behavior
		// Finish
		//scrollPanel.setVisible(true);
		this.getContentPane().add(scrollPanel);
		//this.getContentPane().add(p);
		//this.panel.revalidate();
		this.pack();
		this.setVisible(true);
		this.setLocationRelativeTo(null); 	// Center JFrame in middle of screen
		
		
		
		//this.panel = new JPanel();
		//this.panel.setLayout(new BoxLayout(this.panel, BoxLayout.Y_AXIS));
		//this.scrollPanel = new JScrollPane(this.panel);
		//this.tabbedPanel = new JTabbedPane();
		//this.setContentPane(this.tabbedPanel);
		//this.panel.setLayout(new ScrollPaneLayout());
		//this.panel.setBackground(Color.WHITE);
		//this.tabbedPanel.setLayout(new BoxLayout(this.tabbedPanel));
		//this.tabbedPanel.setBackground(Color.WHITE);
		//this.pages = new ArrayList<ECScheduleComponent>();
		//this.initializeFrame();
	}

	/**
	 * Set up the components of this JFrame, pack, and make it visible
	 * @throws SQLException 
	 */
	private void initializeFrame() throws SQLException {
		// Initialize menu
		this.initializeMenu();
		// Set preferred size
		this.getContentPane().setPreferredSize( new Dimension(700, 800) );
		//this.panel.setPreferredSize( new Dimension(700, 800) );
		// Add schedule pages
		this.pages = getECSchedulePages();
		for(ECScheduleViewComponent ecComp : this.pages) {
			ecComp.setVisible(true);
			this.panel.add(ecComp);
		}		
		// Set exit behavior
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// Finish
		//scrollPanel.setVisible(true);
		//this.getContentPane().add(scrollPanel);
		this.getContentPane().add(panel);
		//this.panel.revalidate();
		this.pack();
		this.setVisible(true);
	}

	/**
	 * Fills the schedule from the database and renders each week
	 * @throws SQLException 
	 */
	private ArrayList<ECScheduleViewComponent> getECSchedulePages() throws SQLException {
		ArrayList<ECScheduleWeekViewBean> weeks = 
				ECScheduleWeekViewBean.getECScheduleWeekViewBeans(schedDao, calDao, holiDao);
		// Build components
		ArrayList<ECScheduleViewComponent> comps = new ArrayList<ECScheduleViewComponent>();
		int numOnFirstPage = 4;
		int numOnOtherPages = 4;
		int w = 0;
		// First page
		String title = "Spring 2015 - EC Schedule";
		ArrayList<ECScheduleWeekViewBean> firstWeeks = new ArrayList<ECScheduleWeekViewBean>();
		for(; w<numOnFirstPage && w < weeks.size(); w++) {
			firstWeeks.add(weeks.get(w));
		}
		comps.add(new ECScheduleViewComponent(firstWeeks, title));
		// Other pages
		while(w < weeks.size()) {
			int curW = 0;
			ArrayList<ECScheduleWeekViewBean> curWeeks = new ArrayList<ECScheduleWeekViewBean>();
			for(; curW<numOnOtherPages && w < weeks.size(); curW++) {
				curWeeks.add(weeks.get(w));
				w++;
			}
			comps.add(new ECScheduleViewComponent(curWeeks));
		}
		return comps;
	}

	/**
	 * Set up the dropdown menu
	 */
	private void initializeMenu() {
		this.menu = new JMenu("Options");
		this.edit = new JMenuItem("Edit schedule");
		this.edit.setEnabled(false);
		this.menu.add(this.edit);
		this.menu.add(new JSeparator());
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
		ECScheduleViewFrame frame = new ECScheduleViewFrame();
		System.out.println("");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == this.print) {
			this.openPrintDialog();
		}
	}

	private void openPrintDialog() {
        PrinterJob job = PrinterJob.getPrinterJob();
        Book book = new Book();
        PageFormat pf = new PageFormat();
        //Paper paper = new Paper();
        //paper.setImageableArea(); // TODO: widen the margins here
        //pf.setPaper(paper);
        for(ECScheduleViewComponent ecPage : this.pages) {
            book.append((Printable) ecPage, pf);
        }
        job.setPageable(book);
        //job.setPrintable(new IASchedulePrinter(a, b));
        boolean ok = job.printDialog();
        if (ok) {
            try {
                 job.print();
            } catch (PrinterException ex) {
             /* The job did not successfully complete */
            }
        }
	}

	public void printSchedule() {
		this.openPrintDialog();
	}
}
