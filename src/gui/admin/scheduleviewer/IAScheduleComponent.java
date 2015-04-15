package gui.admin.scheduleviewer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
/**
 * Swing component for rendering one week's IA schedule
 * @author ramusa2, lim92
 *
 */
public class IAScheduleComponent extends JComponent implements Printable {
	
	private static final long serialVersionUID = -7813470335774759257L;
	private List<List<List<String>>> weekACells, weekBCells;

	public IAScheduleComponent(List<List<List<String>>> weekACells, List<List<List<String>>> weekBCells) {
		this.weekACells = weekACells;
		this.weekBCells = weekBCells;
		this.setVisible(true);
		this.setBackground(Color.WHITE);
	}

	public void paint(Graphics g1) {
		super.paint(g1);
		buildGrid((Graphics2D) g1);
	}

	private void buildGrid(Graphics2D g) {
		
		g.drawString("IA Schedule - Spring 2015", 300, 50);
		g.drawString("Week A", 350, 75);
		int xoffset = 50;
		int yoffset = 100;
		int [] cols = new int[] {25, 75, 175, 275, 375, 475, 575};
		String [] rowLabels = new String[] {"", "11:00", "NOON", "1:00", "2:00", "3:00"};

		for(int i = 0; i < rowLabels.length; i++) {
			yoffset = this.drawRow(g, xoffset, yoffset, rowLabels[i], weekACells.get(i), cols);
		}
		
		yoffset = 725;
		g.drawString("Week B", 350, 700);
		
		for (int i = 0; i < rowLabels.length; i++) {
			yoffset = this.drawRow(g, xoffset, yoffset, rowLabels[i], weekBCells.get(i), cols);
		}
	}

	private int drawRow(Graphics2D g, int x, int y, String rowLabel, List<List<String>> entries, int[] cols) {
		int maxNames = 0;
		for(List<String> cell : entries) {
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
				List<String> names = entries.get(i);
				Color prev = g.getColor();
				if(names.isEmpty()) {
					g.setPaint(Color.LIGHT_GRAY);
					g.fill(new Rectangle2D.Double(x0, y, x1-x0, height));
				}
				else {
					int ydeltaTemp = ydelta;
					for(String name : names) {
						g.drawString(name, x0+xdelta, y+ydeltaTemp);
						ydeltaTemp += 20;
					}
				}
				g.setPaint(prev);
			}
			g.drawRect(x0, y, x1-x0, height);
		}
		return y+height;
	}


	@Override
	public int print(Graphics g, PageFormat pf, int page) throws PrinterException {
		Graphics2D g2d = (Graphics2D)g;      
		g2d.translate(pf.getImageableX(), pf.getImageableY());
		double scale = pf.getImageableWidth()/this.getWidth();
		((Graphics2D)g).scale(scale, scale);

		// Now print the window and its visible contents 
		this.printAll(g);
		((Graphics2D)g).scale(1/scale, 1/scale);
		return PAGE_EXISTS;
	}
	
	private BufferedImage getImageFromPanel(Component component) {
        BufferedImage image = new BufferedImage(800, 1400, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, 800, 1400);
        g2d.setColor(Color.BLACK);
        this.buildGrid(g2d);
        g2d.dispose();
        return image;
    }

	public void save(File imageFile) throws IOException {
		ImageIO.write(getImageFromPanel(this), "png", imageFile);
	}
}
