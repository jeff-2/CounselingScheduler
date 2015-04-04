package gui.admin;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import validator.DateRangeValidator;
import validator.InvalidDateRangeException;
import action.ImportClinicianMeetingsAction;
import bean.CalendarBean;
import bean.HolidayBean;
import bean.Semester;
import net.miginfocom.swing.MigLayout;
import dao.CalendarDAO;
import dao.ConnectionFactory;
import dao.HolidayDAO;

/**
 * Interface for the administrator to initialize the settings for the new semester. 
 * 
 * @author jmfoste2
 * @author nbeltr2
 */
public class NewSemesterSettings extends JFrame implements ActionListener {

	private static final long serialVersionUID = 992467448521859468L;
	private final JPanel panel;
	private JLabel semesterStartDateLabel, semesterEndDateLabel, clinicianHoursLabel;
	private JLabel holidayNameLabel, holidayStartDateLabel, holidayEndDateLabel;
	private JLabel IALabel, ECLabel, IAHoursLabel, ECHoursLabel;
	private JTextField holidayNameText, startHolidayText, endHolidayText, startDateText, endDateText, IAHoursText, ECHoursText;
	private JButton submitButton, addHolidayButton, removeHolidayButton;
	private JComboBox<Semester> semesterSeasonBox;
	private JScrollPane listScrollPane;
	private JList<String> holidayStringList;
	private List<HolidayBean> holidayList = new ArrayList<HolidayBean>();
	private JButton importMeetingsButton;
	private JFileChooser fileChooser;
	private File excelFile;


	/**
	 * Instantiates a new semester settings.
	 */
	public NewSemesterSettings() {
		super("Create New Semester Settings");
		panel = new JPanel();
		panel.setLayout(new MigLayout("gap rel", "grow"));
		fileChooser = new JFileChooser();
		initializeComponents();
		initializeFrame();
	}

	/**
	 * Initialize labels.
	 */
	private void initializeLabels() {
		semesterStartDateLabel = new JLabel("Semester Start Date");
		semesterEndDateLabel = new JLabel("Semester End Date");
		holidayNameLabel = new JLabel("Holiday");
		holidayStartDateLabel = new JLabel("Start Date");
		holidayEndDateLabel = new JLabel("End Date");
		clinicianHoursLabel = new JLabel("Clinician Hours");
		IALabel = new JLabel("IA");
		ECLabel = new JLabel("EC");
		IAHoursLabel = new JLabel("hours/week");
		ECHoursLabel = new JLabel("hours/week");
	}

	/**
	 * Initialize text fields.
	 */
	private void initializeTextFields() {
		holidayNameText = new JTextField(10);
		holidayNameText.setName("holidayNameText");
		startHolidayText = new JTextField(7);
		startHolidayText.setName("startHolidayText");
		endHolidayText = new JTextField(7);
		endHolidayText.setName("endHolidayText");
		startDateText = new JTextField(7);
		startDateText.setName("startDateText");
		endDateText = new JTextField(7);
		endDateText.setName("endDateText");
		IAHoursText = new JTextField(7);
		IAHoursText.setName("IAHoursText");
		ECHoursText = new JTextField(7);
		ECHoursText.setName("ECHoursText");
	}

	/**
	 * Initialize buttons.
	 */
	private void initializeButtons() {
		addHolidayButton = new JButton("Add Holiday");
		addHolidayButton.setName("addHolidayButton");
		removeHolidayButton = new JButton("Remove Holiday");
		removeHolidayButton.setName("removeHolidayButton");
		submitButton = new JButton("create");
		submitButton.setName("submitButton");
		importMeetingsButton = new JButton("Import Meetings");
		importMeetingsButton.setName("importMeetingsButton");
	}

	/**
	 * Initialize scroll pane.
	 */
	private void initializeScrollPane() {
		holidayStringList = new JList<String>();
		holidayStringList.setModel(new DefaultListModel<String>());
		listScrollPane = new JScrollPane(holidayStringList);
	}

	/**
	 * Initialize season box.
	 */
	private void initializeSeasonBox() {
		semesterSeasonBox = new JComboBox<Semester>(Semester.values());
	}

	/**
	 * Initialize components.
	 */
	private void initializeComponents() {
		initializeLabels();
		initializeTextFields();
		initializeButtons();
		initializeScrollPane();
		initializeSeasonBox();
	}

	/**
	 * Adds the semester date components.
	 */
	private void addSemesterDateComponents() {
		panel.add(semesterStartDateLabel);
		panel.add(startDateText);
		panel.add(semesterEndDateLabel);
		panel.add(endDateText);
		panel.add(semesterSeasonBox, "center, wrap");
	}

	/**
	 * Adds the holiday components.
	 */
	private void addHolidayComponents() {
		panel.add(holidayNameLabel);
		panel.add(holidayNameText);
		panel.add(holidayStartDateLabel);
		panel.add(startHolidayText);
		panel.add(holidayEndDateLabel);
		panel.add(endHolidayText, "wrap");
		panel.add(listScrollPane, "grow, span, wrap");

		removeHolidayButton.addActionListener(this);
		addHolidayButton.addActionListener(this);
		panel.add(addHolidayButton);
		panel.add(removeHolidayButton, "wrap");
	}

	/**
	 * Adds the clinician components.
	 */
	private void addClinicianComponents() {
		panel.add(clinicianHoursLabel, "wrap");
		panel.add(IALabel);
		panel.add(IAHoursText);
		panel.add(IAHoursLabel, "wrap");
		panel.add(ECLabel);
		panel.add(ECHoursText);
		panel.add(ECHoursLabel, "wrap");
		submitButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (createSemester()) {
					CalendarBean calendar = new CalendarBean();
					SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
					try {
						calendar.setStartDate(format.parse(startDateText.getText()));
						calendar.setEndDate(format.parse(endDateText.getText()));
					} catch (ParseException e1) {
						e1.printStackTrace();
					}

					calendar.setSemester(semesterSeasonBox.getSelectedIndex());
					calendar.setEcMinHours(Integer.parseInt(ECHoursText.getText()));
					calendar.setIaMinHours(Integer.parseInt(IAHoursText.getText()));

					try {
						Connection conn = ConnectionFactory.getInstance();
						CalendarDAO calendarDao = new CalendarDAO(conn);
						HolidayDAO holidayDao = new HolidayDAO(conn);
						int calendarId = calendarDao.getNextAvailableId();
						calendar.setId(calendarId);
						calendarDao.insertCalendar(calendar);
						for (int i = 0; i < holidayList.size(); i++) {
							HolidayBean holiday = holidayList.get(i);
							holidayDao.insertHoliday(holiday, calendarId, i);
						}
						if (excelFile != null) {
							ImportClinicianMeetingsAction action = new ImportClinicianMeetingsAction(conn, excelFile);
							action.insertImportedMeetings(calendar.getEndDate());
						}
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		panel.add(submitButton, "right, wrap");
	}
	
	private void addMeetingComponents() {
		panel.add(importMeetingsButton, "wrap");
		importMeetingsButton.addActionListener(this);
	}

	/**
	 * Initialize frame.
	 */
	private void initializeFrame() {
		panel.setPreferredSize(new Dimension(500, 500));

		addSemesterDateComponents();
		addHolidayComponents();
		addClinicianComponents();
		addMeetingComponents();

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.getContentPane().add(panel);
		this.pack();
		this.setVisible(true);
	}


	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == addHolidayButton) {
			try {
				addHoliday();
			} catch (ParseException e1) {
				e1.printStackTrace();
			}			
		} else if (e.getSource() == removeHolidayButton) {
			removeHoliday();
		} else if (e.getSource() == importMeetingsButton) {
			int choice = fileChooser.showOpenDialog(this);
			if (choice == JFileChooser.APPROVE_OPTION) {
				excelFile = fileChooser.getSelectedFile();
			}
		}
	}

	/**
	 * Creates the semester.
	 */
	private boolean createSemester() {
		String IAHours = IAHoursText.getText().trim();
		String ECHours = ECHoursText.getText().trim();
		String semesterStart = startDateText.getText().trim(); 
		String semesterEnd = endDateText.getText().trim();

		try {
			Double.parseDouble(IAHours);
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this,
					"Cannot create new semester. " +
							"IA hours assigned must be a non-negative number",
							"Setting invalid semester settings",
							JOptionPane.ERROR_MESSAGE);
			return false;
		}

		try {
			Double.parseDouble(ECHours);
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this,
					"Cannot create new semester. " +
							"EC hours assigned must be a non-negative number",
							"Setting invalid semester settings",
							JOptionPane.ERROR_MESSAGE);
			return false;
		}

		try {
			DateRangeValidator.validate(semesterStart, semesterEnd);
		} catch (InvalidDateRangeException e) {
			JOptionPane.showMessageDialog(this,
					"Cannot create new semester. " +
							e.getMessage(),
							"Setting invalid semester settings",
							JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return true;
	}

	/**
	 * Removes the holiday from the list of displayed holidays.
	 */
	private void removeHoliday() {
		int index = holidayStringList.getSelectedIndex();
		if (index >= 0) {
			DefaultListModel<String> oldModel = (DefaultListModel<String>) holidayStringList.getModel();
			oldModel.remove(index);
			holidayList.remove(index);
		}
	}

	/**
	 * Adds the holiday to the list of displayed holidays.
	 * @throws ParseException 
	 */
	private void addHoliday() throws ParseException {
		String holiday = holidayNameText.getText().trim();
		String startDate = startHolidayText.getText().trim();
		String endDate = endHolidayText.getText().trim();

		if (holiday.isEmpty()) {
			JOptionPane.showMessageDialog(this,
					"You must enter in the name for the holiday. ",
					"Adding invalid holiday",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		try {
			DateRangeValidator.validate(startDate, endDate);
		} catch (InvalidDateRangeException e) {
			JOptionPane.showMessageDialog(this,
					"Cannot add the given holiday to the list. " +
							e.getMessage(),
							"Adding invalid holiday",
							JOptionPane.ERROR_MESSAGE);
			return;
		}

		holidayNameText.setText("");
		startHolidayText.setText("");
		endHolidayText.setText("");

		DefaultListModel<String> model = (DefaultListModel<String>) holidayStringList.getModel();
		model.add(model.size(), holiday + " " + startDate + "-" + endDate);

		
		HolidayBean h = new HolidayBean();
		h.setName(holiday);
		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
		h.setStartDate(format.parse(startDate));
		h.setEndDate(format.parse(endDate));
		holidayList.add(h);
	}
}
