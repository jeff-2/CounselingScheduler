package gui.admin.scheduleviewer;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 * A utility class for printing JFrames
 * @author ramusa2, lim92
 *
 */
public class IASchedulePrinter implements Printable {
	
	/**
	 * The schedule frames to print
	 */
	private JComponent weekA, weekB;
	
	public IASchedulePrinter(JComponent weekASchedule, JComponent weekBSchedule) {
		weekA = weekASchedule;
		weekB = weekBSchedule;
	}

	@Override
    public int print(Graphics g, PageFormat pf, int page) throws
                                                        PrinterException {
		System.out.println("Printing page: "+page);
		JComponent week;
		if(page == 0) {
			week = weekA;
		}
		else if(page == 1) {
			week = weekB;
		}
		else {
			return this.NO_SUCH_PAGE;
		}
        return printPage(week, g, pf);
    }
	
	private int printPage(JComponent week, Graphics g, PageFormat pf) {
        Graphics2D g2d = (Graphics2D)g;      
        g2d.translate(pf.getImageableX(), pf.getImageableY());
        double yscale = pf.getImageableHeight()/week.getHeight();
        double xscale = pf.getImageableWidth()/week.getWidth();
        double scale = Math.min(xscale, yscale);
        ((Graphics2D)g).scale(scale, scale);

        /* Now print the window and its visible contents */
        week.printAll(g);
        ((Graphics2D)g).scale(1/scale, 1/scale);
        return PAGE_EXISTS;
	}

    public void actionPerformed(ActionEvent e) {
         PrinterJob job = PrinterJob.getPrinterJob();
         job.setPrintable(this);
         boolean ok = job.printDialog();
         if (ok) {
             try {
                  job.print();
             } catch (PrinterException ex) {
              /* The job did not successfully complete */
             }
         }
    }
}
