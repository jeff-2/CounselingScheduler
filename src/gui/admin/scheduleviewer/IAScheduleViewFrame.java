package gui.admin.scheduleviewer;

import java.awt.Color;
import java.awt.Dimension;
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
 * A GUI window for displaying the IA appointment for weeks A and B.
 *
 * @author ramusa2, lim92
 */
public class IAScheduleViewFrame extends JFrame {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -4271567771784608985L;

    /** The panel. */
    private JPanel panel;
    
    /** The pane. */
    private JScrollPane pane;
    
    /** The ia component. */
    private IAScheduleComponent iaComponent;

    /**
     * Create an empty client ID list.
     *
     * @param semesterTitle the semester title
     * @param weekACells the week a cells
     * @param weekBCells the week b cells
     * @throws SQLException the SQL exception
     */
    public IAScheduleViewFrame(String semesterTitle, List<List<List<String>>> weekACells,
	    List<List<List<String>>> weekBCells) throws SQLException {
	super("View IA Schedule");
	this.iaComponent = new IAScheduleComponent(semesterTitle, weekACells, weekBCells);
	this.panel = new JPanel();
	this.panel.setPreferredSize(new Dimension(700, iaComponent
		.requiredHeight()));
	this.panel.setLayout(new BoxLayout(this.panel, BoxLayout.Y_AXIS));
	this.panel.setBackground(Color.WHITE);
	this.panel.add(iaComponent);
	this.pane = new JScrollPane(this.panel);
	this.pane
		.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	this.setContentPane(this.pane);
	this.initializeFrame();
	this.setLocationRelativeTo(null); // Center JFrame in middle of screen
    }

    /**
     * Set up the components of this JFrame, pack, and make it visible.
     */
    private void initializeFrame() {
	this.getContentPane().setPreferredSize(new Dimension(700, 800));
	this.pack();
	this.setVisible(true);
    }

    /**
     * Open print dialog and prints ia schedule.
     */
    private void openPrintDialog() {
	PrinterJob job = PrinterJob.getPrinterJob();
	job.setPrintable((Printable) iaComponent);
	boolean ok = job.printDialog();
	if (ok) {
	    try {
		job.print();
	    } catch (PrinterException ex) {
		/* The job did not successfully complete */
	    }
	}
    }

    /**
     * Prints the ia schedule.
     */
    public void printSchedule() {
	this.openPrintDialog();
    }
}
