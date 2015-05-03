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
 * Renders one page of the EC schedule.
 *
 * @author ramusa2, lim92
 */
public class ECScheduleComponent extends JComponent implements Printable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6604353427809987021L;

	/** The title. */
	private String title;

	/** The cells. */
	private List<List<List<String>>> cells;

	/** The Constant cols. */
	private static final int[] cols = new int[] { 25, 75, 175, 275, 375, 475,
			575 };

	/** The page breaks. */
	private int[] pageBreaks = null;

	/**
	 * Instantiates a new EC schedule component.
	 *
	 * @param title
	 *            the title
	 * @param cells
	 *            the cells
	 */
	public ECScheduleComponent(String title, List<List<List<String>>> cells) {
		this.title = title;
		this.cells = cells;
		this.setPreferredSize(new Dimension(600, 175 + cells.size() * 250));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#paint(java.awt.Graphics)
	 */
	public void paint(Graphics g1) {
		Graphics2D g = (Graphics2D) g1;
		this.setVisible(true);
		g.setColor(Color.BLACK);
		int x = 0;
		int y = 75;
		g.drawString(title, 250, 50);
		for (List<List<String>> grid : cells) {
			for (List<String> row : grid) {
				y = drawRow(g, x, y, row, cols);
			}
			y += 50;
		}
	}

	/**
	 * Draws a row of the ec schedule.
	 *
	 * @param g
	 *            the graphics
	 * @param x
	 *            the x-position
	 * @param y
	 *            the y-position
	 * @param cells
	 *            the cells
	 * @param cols
	 *            the columns
	 * @return the int
	 */
	private int drawRow(Graphics2D g, int x, int y, List<String> cells,
			int[] cols) {
		int maxNames = 1;
		int height = 30 + 20 * (maxNames);
		int ydelta = 25;
		int xdelta = 5;
		for (int i = 0; i < cols.length - 1; i++) {
			int x0 = x + cols[i];
			int x1 = x + cols[i + 1];
			String name = cells.get(i);
			Color prev = g.getColor();
			if (name.isEmpty()) {
				g.setPaint(Color.LIGHT_GRAY);
				g.fill(new Rectangle2D.Double(x0, y, x1 - x0, height));
			} else {
				int ydeltaTemp = ydelta;
				g.drawString(name, x0 + xdelta, y + ydeltaTemp);
				ydeltaTemp += 20;
			}
			g.setPaint(prev);
			g.drawRect(x0, y, x1 - x0, height);
		}
		return y + height;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.print.Printable#print(java.awt.Graphics,
	 * java.awt.print.PageFormat, int)
	 */
	@Override
	public int print(Graphics g, PageFormat pf, int page)
			throws PrinterException {
		Graphics2D g2d = (Graphics2D) g;
		g2d.translate(pf.getImageableX(), pf.getImageableY());
		double scale = pf.getImageableWidth() / this.getWidth();
		g2d.scale(scale, scale);

		g2d.drawString(title, 250, 50);
		if (pageBreaks == null) {
			int numBreaks = getHeight() / (int) pf.getImageableHeight();
			int numPerPage = cells.size() / numBreaks;
			pageBreaks = new int[numBreaks];
			for (int b = 0; b < numBreaks; b++) {
				pageBreaks[b] = (b + 1) * numPerPage;
			}
		}

		if (page > pageBreaks.length) {
			return NO_SUCH_PAGE;
		}

		int start = (page == 0) ? 0 : pageBreaks[page - 1];
		int end = (page == pageBreaks.length) ? cells.size() : pageBreaks[page];

		if (start == end) {
			return NO_SUCH_PAGE;
		}
		int x = 0;
		int y = 75;
		for (int i = start; i < end; i++) {
			List<List<String>> grid = cells.get(i);
			for (List<String> cell : grid) {
				y = drawRow(g2d, x, y, cell, cols);
			}
			y += 25;
		}
		return PAGE_EXISTS;
	}

	/**
	 * Gets an image with the ec schedule printed on it.
	 *
	 * @param component
	 *            the component
	 * @return the image from panel
	 */
	private BufferedImage getImageFromPanel(Component component) {
		BufferedImage image = new BufferedImage(600, 175 + cells.size() * 250,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = image.createGraphics();
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, 600, 175 + cells.size() * 250);
		g2d.setColor(Color.BLACK);
		this.print(g2d);
		g2d.dispose();
		return image;
	}

	/**
	 * Save the ec schedule to an image.
	 *
	 * @param imageFile
	 *            the image file
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void save(File imageFile) throws IOException {
		ImageIO.write(getImageFromPanel(this), "png", imageFile);
	}
}
