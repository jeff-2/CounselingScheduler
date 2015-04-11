package gui.admin.scheduleviewer;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;

import bean.SessionNameBean;
import bean.Utility;
import bean.Weekday;


public class IAWeeklyComponent extends JPanel {

	private static final long serialVersionUID = -2863270676214624155L;
	private Component[][] pane;
	private String[] weekdayLabels;
	private static final int [] rowLabels = {11, 12, 13, 14, 15};
	
	public List<List<List<String>>> toCellsArray() {
		List<List<List<String>>> cells = new ArrayList<List<List<String>>>();
		for (int row = 0; row < pane.length; row++) {
			List<List<String>> rowContents = new ArrayList<List<String>>();
			for (int col = 0; col < pane[0].length; col++) {
				List<String> cellContents;
				if (pane[row][col] instanceof JLabel) {
					cellContents = new ArrayList<String>();
					cellContents.add(((JLabel)pane[row][col]).getText());
				} else {
					@SuppressWarnings("unchecked")
					JList<String> list = (JList<String>) ((JScrollPane) pane[row][col]).getViewport().getView();
					cellContents= Utility.toStringList(list.getModel());
				}
				rowContents.add(cellContents);
			}
			cells.add(rowContents);
		}
		return cells;
	}

	public IAWeeklyComponent(List<SessionNameBean> sessionNames, List<String> clinicianNames, String weekType) {

		weekdayLabels = new String[5];
		for (int i = 0; i < weekdayLabels.length; i++) {
			weekdayLabels[i] = Weekday.values()[i].name();
		}

		setLayout(new GridLayout(6, 6, 10, 10));
		pane = new Component[6][6];
		for (int row = 0; row < pane.length; row++) {
			for (int col = 0; col < pane[0].length; col++) {
				if (row == 0 || col == 0) {
					if (row == 0 && col == 0) {
						pane[row][col] = new JLabel("Week " + weekType);
					} else if (row == 0) {
						pane[row][col] = new JLabel(weekdayLabels[col - 1]);
					} else {
						pane[row][col] = new JLabel(rowLabels[row - 1] + ":00");
					}
					add(pane[row][col]);
					((JLabel) pane[row][col]).setHorizontalAlignment(JLabel.CENTER);
					((JLabel) pane[row][col]).setBorder(BorderFactory.createLineBorder(Color.gray));
				} else {
					JList<String> currentList = new JList<String>();
					DefaultListModel<String> currentModel = new DefaultListModel<String>();
					for (String name : getClinicians(sessionNames, weekdayLabels[col - 1], rowLabels[row - 1])) {
						currentModel.addElement(name);
					}
					currentList.setModel(currentModel);
					currentList.setTransferHandler(new ToTransferHandler(TransferHandler.MOVE, row, col));
					currentList.setDragEnabled(true);
					currentList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
					currentList.setDropMode(DropMode.INSERT);
					currentList.setName(weekType + ", " + row + ", " + col);
					pane[row][col] = new JScrollPane(currentList);
					add(pane[row][col]);
				}
			}
		}
	}
	
	private List<String> getClinicians(List<SessionNameBean> sessions, String day, int time) {
		ArrayList<String> clinicianNames = new ArrayList<String>();
		for (SessionNameBean b : sessions) {
			if (b.getStartTime() == time && day.equalsIgnoreCase(b.getDayOfWeek())) {
				clinicianNames.add(b.getClinicianName());
			}
		}
		return clinicianNames;
	}

	private class ToTransferHandler extends TransferHandler {

		private static final long serialVersionUID = 1157878033666829034L;
		private int action;
		private int row, column;

		public ToTransferHandler(int action, int row, int col) {
			this.action = action;
			this.row = row;
			this.column = col;
		}

		public boolean canImport(TransferHandler.TransferSupport support) {
			if (!support.isDrop()) {
				return false;
			}
			
			if (!support.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				return false;
			}

			if ((action & support.getSourceDropActions()) == action) {
				support.setDropAction(action);
				return true;
			}

			return false;
		}

		public boolean importData(TransferHandler.TransferSupport support) {
			if (!canImport(support)) {
				return false;
			}

			JList.DropLocation dl = (JList.DropLocation) support.getDropLocation();

			int index = dl.getIndex();

			String data;
			try {
				data = (String) support.getTransferable().getTransferData(DataFlavor.stringFlavor);
			} catch (UnsupportedFlavorException e) {
				return false;
			} catch (IOException e) {
				return false;
			}

			@SuppressWarnings("unchecked")
			JList<String> list = ((JList<String>) support.getComponent());
			DefaultListModel<String> model = (DefaultListModel<String>) list.getModel();
			model.insertElementAt(data, index);

			Rectangle rect = list.getCellBounds(index, index);
			list.scrollRectToVisible(rect);
			list.setSelectedIndex(index);
			list.requestFocusInWindow();

			return true;
		}

		public int getSourceActions(JComponent comp) {
			return MOVE;
		}

		private int index = 0;

		public Transferable createTransferable(JComponent comp) {

			@SuppressWarnings("unchecked")
			JList<String> l = (JList<String>) ((JScrollPane) pane[row][column]).getViewport().getView();
			index = l.getSelectedIndex();
			if (index < 0 || index >= l.getModel().getSize()) {
				return null;
			}

			return new StringSelection((String) l.getSelectedValue());
		}

		public void exportDone(JComponent comp, Transferable trans, int action) {
			if (action != MOVE) {
				return;
			}

			@SuppressWarnings("unchecked")
			JList<String> l = (JList<String>) ((JScrollPane) pane[row][column]).getViewport().getView();
			((DefaultListModel<String>) l.getModel()).removeElementAt(index);
		}
	}
}
