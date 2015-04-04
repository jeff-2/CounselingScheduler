package gui.admin.scheduleviewer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.ArrayList;

import javax.swing.JComponent;

import bean.ECScheduleWeekBean;

/**
 * Renders one page of the EC schedule
 * @author ramusa2, lim92
 *
 */
public class ECScheduleComponent extends JComponent implements Printable {

	private static final long serialVersionUID = 6604353427809987021L;

	/**
	 * If first, print the schedule title
	 */
	private boolean first;
	
	/**
	 * Title of first page
	 */
	private String title;
	
	/**
	 * Each pages has 4-5 weeks (maybe fewer if it's the last page)
	 */
	private ArrayList<ECScheduleWeekBean> weeks;
	
	public ECScheduleComponent(ArrayList<ECScheduleWeekBean> myWeeks) {
		weeks = myWeeks;
		title = "";
		first = false;
		this.setPreferredSize(new Dimension(700, 175+weeks.size()*200));
	}
	
	public ECScheduleComponent(ArrayList<ECScheduleWeekBean> myWeeks, String myTitle) {
		weeks = myWeeks;
		title = myTitle;
		first = true;
		this.setPreferredSize(new Dimension(700, 975));
	}


	public void paint (Graphics g1) {
		Graphics2D g = (Graphics2D) g1;
		this.setVisible(true);
		//g.setBackground(Color.WHITE);
		int x = 0; 
		int y = 0;
		if(this.first) {
			g.drawString(title, 250, 50);
			y = 50;
		}
		
		for(ECScheduleWeekBean bean : this.weeks) {
			y = drawWeek(g, x, y, bean);
		}
		this.setMinimumSize(new Dimension(700, y+25));
		this.setPreferredSize(new Dimension(700, y+25));
		
		//y = drawWeek(g, x, y, this.weeks.get(0));
	}

	private int drawWeek(Graphics2D g, int x, int y, ECScheduleWeekBean bean) {
		int xoffset = x+25;
		int yoffset = y+50;
		int[] cols = new int[]{25, 75, 175, 275, 375, 475, 575};

		ArrayList<ArrayList<String>> cells = bean.getCells();
		for(int i=0; i<cells.size(); i++) {
			if(i==0) {
				yoffset = this.drawRow(g, xoffset, yoffset, 25, 15, cells.get(i), cols, false, "");
			}
			else {
				String type = (i==1) ? bean.weekType() : "";
				yoffset = this.drawRow(g, xoffset, yoffset, 50, 20, cells.get(i), cols, true, type);
			}
		}
		return yoffset;
	}
	
	
	private int drawRow(Graphics2D g, int x, int y, int height, int ydelta,
			ArrayList<String> cells, int[] cols, boolean colorAllowed, String type) {
		int xdelta = 5;
		for(int i=0; i<cols.length-1; i++) {
			int x0 = x + cols[i];
			int x1 = x + cols[i+1];
			String name = cells.get(i);
			if(name == null) {
				g.setPaint(Color.LIGHT_GRAY);
				g.fill(new Rectangle2D.Double(x0, y, x1-x0, height));
				g.setPaint(Color.BLACK);
			}
			else {
				g.drawString(name, x0+xdelta, y+ydelta);
				if(i==0) {
					g.drawString(type, x0+xdelta, y+ydelta+20);
				}
				if(colorAllowed && name.isEmpty()) {
					g.setPaint(Color.LIGHT_GRAY);
					g.fill(new Rectangle2D.Double(x0, y, x1-x0, height));
					g.setPaint(Color.BLACK);
				}
			}
			g.drawRect(x0, y, x1-x0, height);
		}
		return y+height;
	}

	@Override
	public int print(Graphics g, PageFormat pf, int page)
			throws PrinterException {
		Graphics2D g2d = (Graphics2D)g;      
		g2d.translate(pf.getImageableX(), pf.getImageableY());
		//double yscale = pf.getImageableHeight()/this.getHeight();
		double scale = pf.getImageableWidth()/this.getWidth();
		//double scale = Math.min(xscale, yscale);
		//System.out.println(scale);
		((Graphics2D)g).scale(scale, scale);

		/* Now print the window and its visible contents */
		this.printAll(g);
		((Graphics2D)g).scale(1/scale, 1/scale);
		return PAGE_EXISTS;
	}

}
