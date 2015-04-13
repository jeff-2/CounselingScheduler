package gui.admin.scheduleviewer;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;

import bean.SessionNameBean;
import bean.Utility;
import bean.Weekday;


public class IAWeeklyComponent extends JPanel implements ActionListener, MouseListener {

	private static final long serialVersionUID = -2863270676214624155L;
	private Component[][] pane;
	private String[] weekdayLabels;
	private List<String> clinicianNames;
	private JPopupMenu menu;
	private JMenuItem add, remove;
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

		setName("IAWeeklyComponent" + weekType);
		this.clinicianNames = clinicianNames;
		
		menu = new JPopupMenu();
		add = new JMenuItem("Add Clinician");
		add.addActionListener(this);
		menu.add(add);
		remove = new JMenuItem("Remove Clinician");
		remove.addActionListener(this);
		menu.add(remove);
		
		weekdayLabels = new String[5];
		for (int i = 0; i < weekdayLabels.length; i++) {
			weekdayLabels[i] = Weekday.values()[i].name();
		}
		
		int compNo = 0;

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
					currentList.addMouseListener(this);
					currentList.setComponentPopupMenu(menu);
					currentList.setName("JList" + weekType + compNo);
					pane[row][col] = new JScrollPane(currentList);
					pane[row][col].setName("JScrollPane" + compNo++);
					add(pane[row][col]);
				}
			}
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == add) {
			@SuppressWarnings("unchecked")
			JList<String> list = (JList<String>)menu.getInvoker();
			List<String> remainingNames = new ArrayList<String>(clinicianNames);
			DefaultListModel<String> model = (DefaultListModel<String>) list.getModel();
			for (int i = 0; i < model.getSize(); i++) {
				if (remainingNames.contains(model.getElementAt(i))) {
					remainingNames.remove(model.getElementAt(i));
				}
			}
			JComboBox<String> choices = new JComboBox<String>();
			for (String name : remainingNames) {
				choices.addItem(name);
			}
			int n = JOptionPane.showConfirmDialog(this, choices, "Add Clinician", JOptionPane.OK_CANCEL_OPTION);
			if (n == JOptionPane.OK_OPTION) {
				model.addElement((String)choices.getSelectedItem());
			}
		} else if (e.getSource() == remove) {
			@SuppressWarnings("unchecked")
			JList<String> list = (JList<String>)menu.getInvoker();
			DefaultListModel<String> model = (DefaultListModel<String>) list.getModel();
			int index = list.getSelectedIndex();
			if (index >= 0 && index < list.getModel().getSize()) {
				model.remove(index);
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

	@Override
	public void mouseClicked(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.isPopupTrigger()) {
			menu.show(e.getComponent(), e.getX(), e.getY());
		}
		@SuppressWarnings("unchecked")
		JList<String> list = (JList<String>) e.getComponent();
		list.setSelectedIndex(list.locationToIndex(e.getPoint()));
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.isPopupTrigger()) {
			menu.show(e.getComponent(), e.getX(), e.getY());
		}
		@SuppressWarnings("unchecked")
		JList<String> list = (JList<String>) e.getComponent();
		list.setSelectedIndex(list.locationToIndex(e.getPoint()));
	}
}
