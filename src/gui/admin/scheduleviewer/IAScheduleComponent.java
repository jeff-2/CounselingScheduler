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
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

/**
 * Swing component for rendering one week's IA schedule.
 *
 * @author ramusa2, lim92
 */
public class IAScheduleComponent extends JComponent implements Printable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -7813470335774759257L;

	/** The week b cells. */
	private List<List<List<String>>> weekACells, weekBCells;

	/** The page breaks. */
	private List<Integer> pageBreaks = null;

	/** The semester title. */
	private String semesterTitle;

	/**
	 * Instantiates a new IA schedule component.
	 *
	 * @param semesterTitle
	 *            the semester title
	 * @param weekACells
	 *            the week a cells
	 * @param weekBCells
	 *            the week b cells
	 */
	public IAScheduleComponent(String semesterTitle,
			List<List<List<String>>> weekACells,
			List<List<List<String>>> weekBCells) {
		this.semesterTitle = semesterTitle;
		this.weekACells = weekACells;
		this.weekBCells = weekBCells;
		this.setVisible(true);
		this.setBackground(Color.WHITE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#paint(java.awt.Graphics)
	 */
	public void paint(Graphics g1) {
		super.paint(g1);
		buildGrid((Graphics2D) g1);
	}

	/**
	 * Required height for this IA schedule.
	 *
	 * @return the int
	 */
	public int requiredHeight() {
		int requiredHeight = 0;
		for (List<List<String>> c : weekACells) {
			int max = 0;
			for (List<String> cell : c) {
				max = Math.max(max, cell.size());
			}
			requiredHeight += 30 + 20 * max;
		}
		for (List<List<String>> c : weekBCells) {
			int max = 0;
			for (List<String> cell : c) {
				max = Math.max(max, cell.size());
			}
			requiredHeight += 30 + 20 * max;
		}
		return requiredHeight + 225;
	}

	/**
	 * Builds the grid for this IA schedule.
	 *
	 * @param g
	 *            the graphics
	 */
	private void buildGrid(Graphics2D g) {

		g.drawString(semesterTitle, 300, 50);
		g.drawString("Week A", 350, 75);
		int xoffset = 50;
		int yoffset = 100;
		int[] cols = new int[] { 25, 75, 175, 275, 375, 475, 575 };
		String[] rowLabels = new String[] { "", "11:00", "NOON", "1:00",
				"2:00", "3:00" };

		for (int i = 0; i < rowLabels.length; i++) {
			yoffset = this.drawRow(g, xoffset, yoffset, rowLabels[i],
					weekACells.get(i), cols);
		}

		yoffset += 50;
		g.drawString("Week B", 350, yoffset);
		yoffset += 25;

		for (int i = 0; i < rowLabels.length; i++) {
			yoffset = this.drawRow(g, xoffset, yoffset, rowLabels[i],
					weekBCells.get(i), cols);
		}
	}

	/**
	 * Draw row of the IA schedule.
	 *
	 * @param g
	 *            the graphics
	 * @param x
	 *            the x-position
	 * @param y
	 *            the y-position
	 * @param rowLabel
	 *            the row label
	 * @param entries
	 *            the string entries
	 * @param cols
	 *            the columns
	 * @return the int
	 */
	private int drawRow(Graphics2D g, int x, int y, String rowLabel,
			List<List<String>> entries, int[] cols) {
		int maxNames = 0;
		for (List<String> cell : entries) {
			maxNames = Math.max(maxNames, cell.size());
		}
		int height = 30 + 20 * (maxNames);
		int ydelta = 25;
		int xdelta = 5;
		for (int i = 0; i < cols.length - 1; i++) {
			int x0 = x + cols[i];
			int x1 = x + cols[i + 1];
			if (i == 0) {
				g.drawString(rowLabel, x0 + xdelta, y + ydelta);
			} else {
				List<String> names = entries.get(i);
				Color prev = g.getColor();
				if (names.isEmpty()) {
					g.setPaint(Color.LIGHT_GRAY);
					g.fill(new Rectangle2D.Double(x0, y, x1 - x0, height));
				} else {
					int ydeltaTemp = ydelta;
					for (String name : names) {
						g.drawString(name, x0 + xdelta, y + ydeltaTemp);
						ydeltaTemp += 20;
					}
				}
				g.setPaint(prev);
			}
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

		if (pageBreaks == null) {
			pageBreaks = new ArrayList<Integer>();
			int height = (int) pf.getImageableHeight();
			int ht = 0;
			int row = 0;
			for (List<List<String>> grid : weekACells) {
				int maxHt = 0;
				for (List<String> cell : grid) {
					maxHt = Math.max(maxHt, 30 + cell.size() * 20);
				}
				if (ht + maxHt > height) {
					pageBreaks.add(row);
					ht = 0;
				}
				ht += maxHt;
				row++;
			}

			pageBreaks.add(row);
			ht = 0;
			for (List<List<String>> grid : weekBCells) {
				int maxHt = 0;
				for (List<String> cell : grid) {
					maxHt = Math.max(maxHt, 30 + cell.size() * 20);
				}
				if (ht + maxHt > height) {
					pageBreaks.add(row);
					ht = 0;
				}
				ht += maxHt;
				row++;
			}
		}

		if (page > pageBreaks.size()) {
			return NO_SUCH_PAGE;
		}

		int start = (page == 0) ? 0 : pageBreaks.get(page - 1);
		int end = (page == pageBreaks.size()) ? weekACells.size()
				+ weekBCells.size() : pageBreaks.get(page);

		int xoffset = 50;
		int yoffset = 100;
		int[] cols = new int[] { 25, 75, 175, 275, 375, 475, 575 };
		String[] rowLabels = new String[] { "", "11:00", "NOON", "1:00",
				"2:00", "3:00" };
		if (start < weekACells.size()) {
			g.drawString("Week A", 350, 75);
			for (int i = start; i < end && i < weekACells.size(); i++) {
				yoffset = drawRow(g2d, xoffset, yoffset, rowLabels[i],
						weekACells.get(i), cols);
			}
		}

		if (start >= weekACells.size()) {
			g.drawString("Week B", 350, 75);
			for (int i = start - weekACells.size(); i < end - weekACells.size(); i++) {
				yoffset = drawRow(g2d, xoffset, yoffset, rowLabels[i],
						weekBCells.get(i), cols);
			}
		}
		return PAGE_EXISTS;
	}

	/**
	 * Gets image representation of this IA schedule.
	 *
	 * @param component
	 *            the component
	 * @return the image from panel
	 */
	private BufferedImage getImageFromPanel(Component component) {
		int height = requiredHeight();
		BufferedImage image = new BufferedImage(700, height,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = image.createGraphics();
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, 700, height);
		g2d.setColor(Color.BLACK);
		this.buildGrid(g2d);
		g2d.dispose();
		return image;
	}

	/**
	 * Save this IA schedule to an image.
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
