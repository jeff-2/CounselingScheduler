package gui.admin.scheduleviewer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
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
	private List<List<List<String>>> cells;
	
	public ECScheduleComponent(List<List<List<String>>> cells) {
		title = "";
		first = false;
		this.cells = cells;
		this.setPreferredSize(new Dimension(700, cells.size() * 250));
	}

	public void paint (Graphics g1) {
		Graphics2D g = (Graphics2D) g1;
		this.setVisible(true);
		//g.setBackground(Color.WHITE);
		g.setColor(Color.BLACK);
		int x = 0; 
		int y = 0;
		if(this.first) {
			g.drawString(title, 250, 50);
			y = 50;
		}
		int [] cols = new int[] {25, 75, 175, 275, 375, 475, 575};
		for(List<List<String>> grid : cells) {
			for (List<String> row : grid) {
				y = drawRow(g, x, y, row, cols);
			}
			y += 50;
		}
	}
	
	private int drawRow(Graphics2D g, int x, int y, List<String> cells, int [] cols) {
		int maxNames = 1;
		int height = 30 + 20*(maxNames);
		int ydelta = 25;
		int xdelta = 5;
		for(int i = 0; i < cols.length-1; i++) {
			int x0 = x + cols[i];
			int x1 = x + cols[i+1];
			String name = cells.get(i);
			Color prev = g.getColor();
			if (name.isEmpty()) {
				g.setPaint(Color.LIGHT_GRAY);
				g.fill(new Rectangle2D.Double(x0, y, x1-x0, height));
			}
			else {
				int ydeltaTemp = ydelta;
				g.drawString(name, x0+xdelta, y+ydeltaTemp);
				ydeltaTemp += 20;
			}
			g.setPaint(prev);
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

		/* Now print the window and its visible contents */
		this.printAll(g);
		((Graphics2D)g).scale(1/scale, 1/scale);
		return PAGE_EXISTS;
	}

	private BufferedImage getImageFromPanel(Component component) {
        BufferedImage image = new BufferedImage(700, cells.size() * 250, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, 700, cells.size() * 250);
        g2d.setColor(Color.BLACK);
        this.print(g2d);
        g2d.dispose();
        return image;
    }

	public void save(File imageFile) throws IOException {
		ImageIO.write(getImageFromPanel(this), "png", imageFile);
	}
}
