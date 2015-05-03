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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

import utils.Utility;
import action.ValidateScheduleAction;
import bean.Clinician;
import bean.IAWeektype;
import bean.Schedule;
import bean.SessionNameBean;
import bean.Weekday;

/**
 * The Class IAWeeklyComponent.
 */
public class IAWeeklyComponent extends JPanel implements ActionListener,
		MouseListener {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -2863270676214624155L;

	/** The pane. */
	private Component[][] pane;

	/** The clinician names. */
	private List<String> clinicianNames;

	/** The menu. */
	private JPopupMenu menu;

	/** The remove. */
	private JMenuItem add, remove;

	/** The schedule. */
	private Schedule schedule;

	/** The Constant rowLabels. */
	private static final int[] rowLabels = { 11, 12, 13, 14, 15 };

	/**
	 * Converts IA components to a grid of strings.
	 *
	 * @return the list
	 */
	public List<List<List<String>>> toCellsArray() {
		List<List<List<String>>> cells = new ArrayList<List<List<String>>>();
		for (int row = 0; row < pane.length; row++) {
			List<List<String>> rowContents = new ArrayList<List<String>>();
			for (int col = 0; col < pane[0].length; col++) {
				List<String> cellContents;
				if (pane[row][col] instanceof JLabel) {
					cellContents = new ArrayList<String>();
					cellContents.add(((JLabel) pane[row][col]).getText());
				} else {
					@SuppressWarnings("unchecked")
					JList<String> list = (JList<String>) ((JScrollPane) pane[row][col])
							.getViewport().getView();
					cellContents = Utility.toStringList(list.getModel());
				}
				rowContents.add(cellContents);
			}
			cells.add(rowContents);
		}
		return cells;
	}

	/**
	 * Instantiates a new IA weekly component.
	 *
	 * @param sessionNames
	 *            the session names
	 * @param clinicianNames
	 *            the clinician names
	 * @param weekType
	 *            the week type
	 * @param schedule
	 *            the schedule
	 */
	public IAWeeklyComponent(List<SessionNameBean> sessionNames,
			List<String> clinicianNames, IAWeektype weekType, Schedule schedule) {

		setName("IAWeeklyComponent" + weekType);
		this.clinicianNames = clinicianNames;
		this.schedule = schedule;

		menu = new JPopupMenu();
		add = new JMenuItem("Add Clinician");
		add.addActionListener(this);
		menu.add(add);
		remove = new JMenuItem("Remove Clinician");
		remove.addActionListener(this);
		menu.add(remove);

		int compNo = 0;

		setLayout(new GridLayout(6, 6, 10, 10));
		pane = new Component[6][6];
		for (int row = 0; row < pane.length; row++) {
			for (int col = 0; col < pane[0].length; col++) {
				if (row == 0 || col == 0) {
					if (row == 0 && col == 0) {
						pane[row][col] = new JLabel("Week " + weekType);
					} else if (row == 0) {
						pane[row][col] = new JLabel(
								Weekday.values()[col - 1].name());
					} else {
						pane[row][col] = new JLabel(rowLabels[row - 1] + ":00");
					}
					add(pane[row][col]);
					((JLabel) pane[row][col])
							.setHorizontalAlignment(JLabel.CENTER);
					((JLabel) pane[row][col]).setBorder(BorderFactory
							.createLineBorder(Color.gray));
				} else {
					JList<String> currentList = new JList<String>();
					DefaultListModel<String> currentModel = new DefaultListModel<String>();
					HashSet<String> uniqueNames = new HashSet<String>();
					for (String name : getClinicians(sessionNames,
							Weekday.values()[col - 1], rowLabels[row - 1])) {
						uniqueNames.add(name);
					}
					for (String name : uniqueNames) {
						currentModel.addElement(name);
					}
					currentList.setModel(currentModel);
					currentList.setTransferHandler(new ToTransferHandler(
							TransferHandler.MOVE, row, col, this));
					currentList.setDragEnabled(true);
					currentList
							.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
					currentList.setDropMode(DropMode.INSERT);
					currentList.addMouseListener(this);
					currentList.setComponentPopupMenu(menu);
					currentList.setName("JList" + weekType + compNo);
					currentList.setName(weekType + "-"
							+ Weekday.values()[col - 1].ordinal() + "-"
							+ rowLabels[row - 1]);
					pane[row][col] = new JScrollPane(currentList);
					pane[row][col].setName("JScrollPane" + compNo++);
					pane[row][col].setName(weekType + "-"
							+ Weekday.values()[col - 1].ordinal() + "-"
							+ rowLabels[row - 1]);
					add(pane[row][col]);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == add) {
			@SuppressWarnings("unchecked")
			JList<String> list = (JList<String>) menu.getInvoker();
			List<String> remainingNames = new ArrayList<String>(clinicianNames);
			DefaultListModel<String> model = (DefaultListModel<String>) list
					.getModel();
			for (int i = 0; i < model.getSize(); i++) {
				if (remainingNames.contains(model.getElementAt(i))) {
					remainingNames.remove(model.getElementAt(i));
				}
			}
			JComboBox<String> choices = new JComboBox<String>();
			for (String name : remainingNames) {
				choices.addItem(name);
			}
			int n = JOptionPane.showConfirmDialog(this, choices,
					"Add Clinician", JOptionPane.OK_CANCEL_OPTION);
			if (n == JOptionPane.OK_OPTION) {
				model.addElement((String) choices.getSelectedItem());
			}
		} else if (e.getSource() == remove) {
			@SuppressWarnings("unchecked")
			JList<String> list = (JList<String>) menu.getInvoker();
			DefaultListModel<String> model = (DefaultListModel<String>) list
					.getModel();
			int index = list.getSelectedIndex();
			if (index >= 0 && index < list.getModel().getSize()) {
				model.remove(index);
			}
		}
	}

	/**
	 * Gets the clinicians.
	 *
	 * @param sessions
	 *            the sessions
	 * @param day
	 *            the day
	 * @param time
	 *            the time
	 * @return list of clinicians
	 */
	private List<String> getClinicians(List<SessionNameBean> sessions,
			Weekday day, int time) {
		ArrayList<String> clinicianNames = new ArrayList<String>();
		for (SessionNameBean b : sessions) {
			if (b.getStartTime() == time && day.equals(b.getDayOfWeek())) {
				clinicianNames.add(b.getClinicianName());
			}
		}
		return clinicianNames;
	}

	/**
	 * The Class ToTransferHandler provides implementation for drag and drop
	 * functionalities.
	 */
	private class ToTransferHandler extends TransferHandler {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1157878033666829034L;

		/** The action. */
		private int action;

		/** The column. */
		private int row, column;

		/** The clinician name. */
		private String clinicianName;

		/** The weekly component. */
		private IAWeeklyComponent weeklyComponent;

		/** The export time. */
		private int exportDay, exportTime;

		/** The import time. */
		private int importDay, importTime;

		/** The import is type a. */
		private boolean exportIsTypeA, importIsTypeA;

		/**
		 * Instantiates a new to transfer handler.
		 *
		 * @param action
		 *            the action
		 * @param row
		 *            the row
		 * @param col
		 *            the column
		 * @param weeklyComponent
		 *            the weekly component
		 */
		public ToTransferHandler(int action, int row, int col,
				IAWeeklyComponent weeklyComponent) {
			this.action = action;
			this.row = row;
			this.column = col;
			this.weeklyComponent = weeklyComponent;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * javax.swing.TransferHandler#canImport(javax.swing.TransferHandler
		 * .TransferSupport)
		 */
		public boolean canImport(TransferHandler.TransferSupport support) {
			if (!support.isDrop()) {
				return false;
			}

			if (!support.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				return false;
			}

			@SuppressWarnings("unchecked")
			JList<String> list = ((JList<String>) support.getComponent());
			DefaultListModel<String> model = (DefaultListModel<String>) list
					.getModel();
			String data;
			try {
				data = (String) support.getTransferable().getTransferData(
						DataFlavor.stringFlavor);
			} catch (UnsupportedFlavorException e) {
				return false;
			} catch (IOException e) {
				return false;
			}
			this.clinicianName = data;

			if (model.contains(data)) {
				return false;
			}

			if ((action & support.getSourceDropActions()) == action) {
				support.setDropAction(action);
				return true;
			}
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * javax.swing.TransferHandler#importData(javax.swing.TransferHandler
		 * .TransferSupport)
		 */
		public boolean importData(TransferHandler.TransferSupport support) {
			if (!canImport(support)) {
				return false;
			}

			String componentName = support.getComponent().getName();
			importIsTypeA = componentName.split("-")[0].equals("A");
			importDay = Integer.parseInt(componentName.split("-")[1]);
			importTime = Integer.parseInt(componentName.split("-")[2]);

			schedule.addIAClinician(importIsTypeA, importDay, importTime,
					clinicianName);

			JList.DropLocation dl = (JList.DropLocation) support
					.getDropLocation();

			int index = dl.getIndex();

			String data;
			try {
				data = (String) support.getTransferable().getTransferData(
						DataFlavor.stringFlavor);
			} catch (UnsupportedFlavorException e) {
				return false;
			} catch (IOException e) {
				return false;
			}
			this.clinicianName = data;
			@SuppressWarnings("unchecked")
			JList<String> list = ((JList<String>) support.getComponent());
			DefaultListModel<String> model = (DefaultListModel<String>) list
					.getModel();
			model.insertElementAt(data, index);

			Rectangle rect = list.getCellBounds(index, index);
			list.scrollRectToVisible(rect);
			list.setSelectedIndex(index);
			list.requestFocusInWindow();

			return true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * javax.swing.TransferHandler#getSourceActions(javax.swing.JComponent)
		 */
		public int getSourceActions(JComponent comp) {
			return MOVE;
		}

		/** The index. */
		private int index = 0;

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * javax.swing.TransferHandler#createTransferable(javax.swing.JComponent
		 * )
		 */
		public Transferable createTransferable(JComponent comp) {
			@SuppressWarnings("unchecked")
			JList<String> l = (JList<String>) ((JScrollPane) pane[row][column])
					.getViewport().getView();
			index = l.getSelectedIndex();
			if (index < 0 || index >= l.getModel().getSize()) {
				return null;
			}

			return new StringSelection((String) l.getSelectedValue());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.TransferHandler#exportDone(javax.swing.JComponent,
		 * java.awt.datatransfer.Transferable, int)
		 */
		public void exportDone(JComponent comp, Transferable trans, int action) {
			if (action != MOVE) {
				return;
			}
			String componentName = comp.getName();
			exportIsTypeA = componentName.split("-")[0].equals("A");
			exportDay = Integer.parseInt(componentName.split("-")[1]);
			exportTime = Integer.parseInt(componentName.split("-")[2]);

			schedule.removeIAClinician(exportIsTypeA, exportDay, exportTime,
					clinicianName);
			validate();

			@SuppressWarnings("unchecked")
			JList<String> l = (JList<String>) ((JScrollPane) pane[row][column])
					.getViewport().getView();
			((DefaultListModel<String>) l.getModel()).removeElementAt(index);
		}

		/**
		 * Validates the ia schedule.
		 */
		private void validate() {
			Set<Clinician> clinicians = new ValidateScheduleAction()
					.validateSchedule(schedule);
			String cliniciansString = "";
			for (Clinician clinician : clinicians) {
				cliniciansString += clinician.getClinicianBean().getName()
						+ ", ";
			}
			cliniciansString = cliniciansString.substring(0,
					cliniciansString.length() - 2)
					+ ".";
			String errorString = "The previous change created conflicts with the following clinicians:\n\n "
					+ cliniciansString;

			JOptionPane.showMessageDialog(weeklyComponent, errorString,
					"Validation Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent e) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent e) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		if (e.isPopupTrigger()) {
			menu.show(e.getComponent(), e.getX(), e.getY());
		}
		@SuppressWarnings("unchecked")
		JList<String> list = (JList<String>) e.getComponent();
		list.setSelectedIndex(list.locationToIndex(e.getPoint()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
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
