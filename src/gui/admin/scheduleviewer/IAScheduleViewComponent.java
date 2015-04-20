package gui.admin.scheduleviewer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import bean.SessionNameBean;

/**
 * Swing component for rendering one week's IA schedule
 * @author ramusa2, lim92
 *
 */
public class IAScheduleViewComponent extends JComponent implements Printable {
	private static final long serialVersionUID = -7813470335774759257L;

	private static final String[] weekdays = new String[]{"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"};

	private final String week;

	private final List<SessionNameBean> sessions;

	public IAScheduleViewComponent(String weekLetter, List<SessionNameBean> sessionList) {
		week = weekLetter;
		sessions = sessionList;
	}

	public void paint (Graphics g1) {
		Graphics2D g = (Graphics2D) g1;
		this.setVisible(true);
		g.setBackground(Color.WHITE);
		this.buildGrid(g);
	}

	private void buildGrid(Graphics2D g) {
		this.writeTitle(g);
		int xoffset = 50;
		int yoffset =100;
		int[] cols = new int[]{25, 75, 175, 275, 375, 475, 575};
		String[] rowLabels = new String[]{"", "11:00", "NOON", "1:00", "2:00", "3:00"};

		ArrayList<ArrayList<ArrayList<String>>> cells = getCells();
		for(int i=0; i<rowLabels.length; i++) {
			if(i==0) {
				yoffset = this.drawHeader(g, xoffset, yoffset, rowLabels[i], weekdays, cols);
			}
			else {
				yoffset = this.drawRow(g, xoffset, yoffset, rowLabels[i], cells.get(i-1), cols);
			}
		}
	}

	private void writeTitle(Graphics2D g) {
		g.drawString("IA Schedule - Spring 2015", 300, 50);
		g.drawString("Week "+week, 350, 75);
	}

	private int drawHeader(Graphics2D g, int x, int y, String rowLabel, String[] colLabels, int[] cols) {
		int height = 20;
		int ydelta = 15;
		int xdelta = 5;
		for(int i=0; i<cols.length-1; i++) {
			int x0 = x + cols[i];
			int x1 = x + cols[i+1];
			g.drawRect(x0, y, x1-x0, height);
			String str = (i==0) ? rowLabel : colLabels[i-1];
			g.drawString(str, x0+xdelta, y+ydelta);
		}
		return y+height;
	}

	private ArrayList<ArrayList<ArrayList<String>>> getCells() {
		ArrayList<ArrayList<ArrayList<String>>> entries = new ArrayList<ArrayList<ArrayList<String>>>();
		for(int timeslot = 11; timeslot <=15; timeslot++) {
			ArrayList<ArrayList<String>> temp = new ArrayList<ArrayList<String>>();
			for(int day = 0; day < weekdays.length; day++) {
				temp.add(new ArrayList<String>());
			}
			entries.add(temp);
		}
		
		for(SessionNameBean bean : sessions) {
			if(checkWeekType(bean)) {
				entries.get(bean.getStartTime()-11).get(getDayIndex(bean)).add(bean.getClinicianName());
			}
		}
		return entries;
	}

	private boolean checkWeekType(SessionNameBean bean) {
		String beanWeek = (bean.getWeekType()==0) ? "A" : "B";
		return beanWeek.equalsIgnoreCase(this.week);
	}

	private int getDayIndex(SessionNameBean bean) {
		for(int d=0; d<weekdays.length; d++) {
			if(weekdays[d].equalsIgnoreCase(bean.getDayOfWeek().name())) {
				return d;
			}
		}
		return -1;
	}

	private int drawRow(Graphics2D g, int x, int y, String rowLabel,
			ArrayList<ArrayList<String>> entries, int[] cols) {
		int maxNames = 0;
		for(ArrayList<String> cell : entries) {
			maxNames = Math.max(maxNames, cell.size());
		}
		int height = 30 + 20*(maxNames);
		int ydelta = 25;
		int xdelta = 5;
		for(int i=0; i<cols.length-1; i++) {
			int x0 = x + cols[i];
			int x1 = x + cols[i+1];
			if(i==0) {
				g.drawString(rowLabel, x0+xdelta, y+ydelta);
			}
			else {
				ArrayList<String> names = entries.get(i-1);
				if(names.isEmpty()) {
					g.setPaint(Color.LIGHT_GRAY);
					g.fill(new Rectangle2D.Double(x0, y, x1-x0, height));
					g.setPaint(Color.BLACK);
				}
				else {
					int ydeltaTemp = ydelta;
					for(String name : names) {
						g.drawString(name, x0+xdelta, y+ydeltaTemp);
						ydeltaTemp += 20;
					}
				}
			}
			g.drawRect(x0, y, x1-x0, height);
		}
		return y+height;
	}


	@Override
	public int print(Graphics g, PageFormat pf, int page) throws
	PrinterException {
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