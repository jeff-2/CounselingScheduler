package gui.admin.scheduleviewer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.sql.SQLException;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * A GUI window for displaying the IA appointment for weeks A and B
 * 
 * @author ramusa2, lim92
 *
 */
public class IAScheduleViewFrame extends JFrame {

	private static final long serialVersionUID = -4271567771784608985L;
	
	private JPanel panel;
	private JScrollPane pane;
	private IAScheduleComponent iaComponent;
	
	/**
	 * Create an empty client ID list
	 * @throws SQLException 
	 */
	public IAScheduleViewFrame(List<List<List<String>>> weekACells, List<List<List<String>>> weekBCells) throws SQLException {
		super("View IA Schedule");
		this.iaComponent = new IAScheduleComponent(weekACells, weekBCells);
		this.panel = new JPanel();
		this.panel.setPreferredSize(new Dimension(600, 1400));
		this.panel.setLayout(new BoxLayout(this.panel, BoxLayout.Y_AXIS));
		this.panel.setBackground(Color.WHITE);
		this.panel.add(iaComponent);
		this.pane = new JScrollPane(this.panel);
		this.pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		this.setContentPane(this.pane);
		this.initializeFrame();
		this.setLocationRelativeTo(null); 	// Center JFrame in middle of screen
	}

	/**
	 * Set up the components of this JFrame, pack, and make it visible
	 */
	private void initializeFrame() {
		this.getContentPane().setPreferredSize(new Dimension(700, 800));
		this.pack();
		this.setVisible(true);
	}

	private void openPrintDialog() {
        PrinterJob job = PrinterJob.getPrinterJob();
        Book book = new Book();
        PageFormat pf = new PageFormat();
        book.append((Printable) this.iaComponent, pf);
        job.setPageable(book);
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
