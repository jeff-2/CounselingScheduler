package gui.admin.scheduleviewer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import net.miginfocom.swing.MigLayout;

/**
 * A GUI window for displaying the EC appointments for a semester
 * 
 * @author ramusa2, lim92
 *
 */
public class ECScheduleViewFrame extends JFrame {

	private static final long serialVersionUID = -4271567771784608985L;
	
	/**
	 * The scrollable content pane for weeks A & B
	 */
	private JScrollPane scrollPanel;

	private ECScheduleComponent ecComponent;
	
	/**
	 * Create an empty client ID list
	 * @throws SQLException 
	 */
	public ECScheduleViewFrame(ECScheduleComponent ecComponent) throws SQLException {
		super("View EC Schedule");
		this.scrollPanel = new JScrollPane();

		JPanel p = new JPanel(new MigLayout("gap rel 0", "grow"));
		p.setBackground(Color.WHITE);
		
		p.validate();
		this.scrollPanel = new JScrollPane(p);
		this.scrollPanel.setPreferredSize(new Dimension(700, 800));
		this.scrollPanel.getVerticalScrollBar().setUnitIncrement(20);
		this.scrollPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		this.scrollPanel.setViewportView(p);
		
		this.ecComponent = ecComponent;
		p.add(ecComponent);
	
		// Finish
		this.getContentPane().add(scrollPanel);
		this.pack();
		this.setVisible(true);
		this.setLocationRelativeTo(null); 	// Center JFrame in middle of screen
	}
	
	private void openPrintDialog() {
        PrinterJob job = PrinterJob.getPrinterJob();
        Book book = new Book();
        PageFormat pf = new PageFormat();
        book.append((Printable) ecComponent, pf);
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
