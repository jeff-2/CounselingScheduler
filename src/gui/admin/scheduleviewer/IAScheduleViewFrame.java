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
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
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
	
	private JPanel panel;
	private JScrollPane pane;

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
	
	private List<List<List<String>>> weekACells, weekBCells;
	private IAScheduleComponent iaComponent;

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
	public IAScheduleViewFrame(List<List<List<String>>> weekACells, List<List<List<String>>> weekBCells) throws SQLException {
		super("View IA Schedule");
		dao = new ScheduleDAO(ConnectionFactory.getInstance());
		this.iaComponent = new IAScheduleComponent(weekACells, weekBCells);
		this.panel = new JPanel();
		this.panel.setPreferredSize(new Dimension(600, 1400));
		this.panel.setLayout(new BoxLayout(this.panel, BoxLayout.Y_AXIS));
		this.panel.setBackground(Color.WHITE);
		this.panel.add(iaComponent);
		this.pane = new JScrollPane(this.panel);
		this.pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		this.setContentPane(this.pane);
		this.weekACells = weekACells;
		this.weekBCells = weekBCells;
		this.initializeFrame();
		this.setLocationRelativeTo(null); 	// Center JFrame in middle of screen
		JButton tmp = new JButton("Save");
		this.panel.add(tmp);
		tmp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				iaComponent.save();
			}
		});
		
	}

	/**
	 * Set up the components of this JFrame, pack, and make it visible
	 */
	private void initializeFrame() {
		// Initialize menu
		//this.initializeMenu();
		// Set preferred size
		this.getContentPane().setPreferredSize(new Dimension(700, 800));
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
		IAScheduleFrame frame = new IAScheduleFrame();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == this.print) {
			
			this.iaComponent.save();
			//this.openPrintDialog();
		}
	}

	private void openPrintDialog() {
        PrinterJob job = PrinterJob.getPrinterJob();
        Book book = new Book();
        PageFormat pf = new PageFormat();
        //pf.setPaper(new Paper());
        book.append((Printable) this.iaComponent, pf);
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
