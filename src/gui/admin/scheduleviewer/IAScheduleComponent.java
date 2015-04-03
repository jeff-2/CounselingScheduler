package gui.admin.scheduleviewer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.ArrayList;

import javax.swing.JComponent;

/**
 * Swing component for rendering one week's IA schedule
 * @author ramusa2, lim92
 *
 */
public class IAScheduleComponent extends JComponent implements Printable {
	private static final long serialVersionUID = -7813470335774759257L;
	
	private final String week;
	
	private JComponent grid;

	public IAScheduleComponent(String weekLetter) {
		week = weekLetter;
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
		String[] colLabels = new String[]{"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"};
		String[] rowLabels = new String[]{"", "11:00", "NOON", "1:00", "2:00", "3:00"};
		for(int i=0; i<rowLabels.length; i++) {
			if(i==0) {
				yoffset = this.drawHeader(g, xoffset, yoffset, rowLabels[i], colLabels, cols);
			}
			else {
				ArrayList<ArrayList<String>> entries = getRowEntries(i==2);
				yoffset = this.drawRow(g, xoffset, yoffset, rowLabels[i], entries, cols);
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

	private ArrayList<ArrayList<String>> getRowEntries(boolean noon) {
		// TODO: actually read this information from schedule, instead of generating fake names
		ArrayList<ArrayList<String>> entries = new ArrayList<ArrayList<String>>();
		String[] fakeNames = new String[]{"Amanda", "Bert", "Carl", "Devan", "Eric"};
		for(int i=0; i<5; i++) {
			if(noon) {
				// NOON
				ArrayList<String> names = new ArrayList<String>();
				int n = (int) (Math.random()+0.5);
				for(int j=0; j<n; j++) {
					names.add(fakeNames[j]);
				}
				entries.add(names);
			}
			else {
				ArrayList<String> names = new ArrayList<String>();
				int n = (int) (Math.random()*4) + 1;
				for(int j=0; j<n; j++) {
					names.add(fakeNames[j]);
				}
				entries.add(names);
			}
		}
		return entries;
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
					g.setColor(Color.GRAY);
					g.drawRect(x0, y, x1-x0, height);
					g.setColor(Color.BLACK);
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
        //((Graphics2D)g).scale(scale, scale);

        /* Now print the window and its visible contents */
        this.printAll(g);
        ((Graphics2D)g).scale(1/scale, 1/scale);
        return PAGE_EXISTS;
    }

}
