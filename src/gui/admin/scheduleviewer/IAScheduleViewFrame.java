package gui.admin.scheduleviewer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.sql.SQLException;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;

import dao.ConnectionFactory;
import dao.ScheduleDAO;

/**
 * A GUI window for displaying the IA appointment for weeks A and B
 * 
 * @author ramusa2, lim92
 *
 */
public class IAScheduleViewFrame extends JFrame implements ActionListener {

	private static final long serialVersionUID = -4271567771784608985L;
	
	/**
	 * DAO to get schedule information from DB
	 */
	private ScheduleDAO dao;
	
	/**
	 * The tabbed content pane for weeks A & B
	 */
	private JTabbedPane panel;
	
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
	public IAScheduleViewFrame() throws SQLException {
		super("View IA Schedule");
		dao = new ScheduleDAO(ConnectionFactory.getInstance());
		this.panel = new JTabbedPane();
		this.setContentPane(this.panel);
		this.weekA = this.getWeekPanel("A");
		this.weekB = this.getWeekPanel("B");
		this.panel.addTab("Week A", weekA);
		this.panel.addTab("Week B", weekB);
		this.initializeFrame();
		this.setLocationRelativeTo(null); 	// Center JFrame in middle of screen
	}

	private JPanel getWeekPanel(String aOrB) throws SQLException {
		JPanel p = new JPanel(); 
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.setBackground(Color.WHITE);
		int type = aOrB.equals("A") ? 0 : 1;
		p.add(new IAScheduleViewComponent(aOrB, dao.loadScheduleType(type)));
		return p;
	}

	/**
	 * Set up the components of this JFrame, pack, and make it visible
	 */
	private void initializeFrame() {
		// Initialize menu
		this.initializeMenu();
		// Set preferred size
		this.getContentPane().setPreferredSize( new Dimension(700, 800) );
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
		IAScheduleViewFrame frame = new IAScheduleViewFrame();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == this.print) {
			this.openPrintDialog();
		}
	}

	private void openPrintDialog() {
        PrinterJob job = PrinterJob.getPrinterJob();
        IAScheduleViewComponent a = (IAScheduleViewComponent) weekA.getComponent(0);
        IAScheduleViewComponent b = (IAScheduleViewComponent) weekB.getComponent(0);
        Book book = new Book();
        PageFormat pf = new PageFormat();
        //pf.setPaper(new Paper());
        book.append((Printable) a, pf);
        book.append((Printable) b, pf);
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
