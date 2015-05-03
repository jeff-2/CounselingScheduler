package action;

import static org.junit.Assert.assertTrue;
import generator.TestDataGenerator;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import bean.Clinician;
import bean.CommitmentBean;
import bean.Schedule;
import bean.TimeAwayBean;
import dao.ConnectionFactory;

/**
 * The Class ValidateScheduleActionTest tests the ValidateScheduleAction
 * functionality.
 */
public class ValidateScheduleActionTest {

	/** The validate action. */
	private ValidateScheduleAction validateAction;

	/** The conn. */
	private Connection conn;

	/** The gen. */
	private TestDataGenerator gen;

	/** The sch. */
	private Schedule sch;

	/**
	 * Sets the test up.
	 *
	 * @throws SQLException
	 *             the SQL exception
	 * @throws ParseException
	 *             the parse exception
	 */
	@Before
	public void setUp() throws SQLException, ParseException {
		conn = ConnectionFactory.getInstance();
		gen = new TestDataGenerator(conn);
		gen.clearTables();
		gen.generateStandardDataset();

		sch = Schedule.loadScheduleFromDBAndAssignClinicians();
		validateAction = new ValidateScheduleAction();
	}

	/**
	 * Test validate ec schedule with conflicts.
	 */
	@Test
	public void testValidateECScheduleConflicts() {
		Clinician cl1 = sch.getECClinician(1, 2, 8);
		Clinician cl2 = sch.getECClinician(2, 3, 8);

		int id1 = cl1.getClinicianBean().getClinicianID();
		int id2 = cl2.getClinicianBean().getClinicianID();
		CommitmentBean cmt1 = new CommitmentBean(id1, 7, 9,
				Date.valueOf("2015-1-28"), "");
		TimeAwayBean tm2 = new TimeAwayBean(id2, "", Date.valueOf("2015-2-5"),
				Date.valueOf("2015-2-5"));
		cl1.getCommitmentBeans().add(cmt1);
		cl2.getTimeAwayBeans().add(tm2);

		Set<Clinician> violations = validateAction
				.validateECScheduleConflicts(sch);
		assertTrue(violations.contains(cl1));
		assertTrue(violations.contains(cl2));
	}

	/**
	 * Test validate same day noon ec, ia conflicts.
	 */
	@Test
	public void testValidateSameDayNoonECIAConflicts() {
		Clinician cl1 = sch.getECClinician(1, 2, 12);
		Clinician cl2 = sch.getECClinician(2, 3, 12);

		sch.addIAClinician(true, 2, 13, cl1.getClinicianBean().getName());
		sch.addIAClinician(false, 3, 13, cl2.getClinicianBean().getName());

		Set<Clinician> violations = validateAction
				.validateSameDayNoonECIAConflicts(sch);
		assertTrue(violations.contains(cl1));
		assertTrue(violations.contains(cl2));
	}

	/**
	 * Test validate afternoon meeting morning ec conflicts.
	 */
	@Test
	public void testValidateAfternoonMeetingMorningECConflicts() {
		Clinician cl1 = sch.getECClinician(1, 2, 8);
		Clinician cl2 = sch.getECClinician(2, 3, 8);

		int id1 = cl1.getClinicianBean().getClinicianID();
		int id2 = cl2.getClinicianBean().getClinicianID();
		CommitmentBean cmt1 = new CommitmentBean(id1, 5, 6,
				Date.valueOf("2015-1-27"), "");
		CommitmentBean cmt2 = new CommitmentBean(id2, 6, 7,
				Date.valueOf("2015-2-4"), "");
		cl1.getCommitmentBeans().add(cmt1);
		cl2.getCommitmentBeans().add(cmt2);

		Set<Clinician> violations = validateAction
				.validateAfternoonMeetingMorningECConflicts(sch);
		assertTrue(violations.contains(cl1));
		assertTrue(violations.contains(cl2));
	}

	/**
	 * Test validate morning meeting afternoon ec conflicts.
	 */
	@Test
	public void testValidateMorningMeetingAfternoonECConflicts() {
		Clinician cl1 = sch.getECClinician(1, 2, 16);
		Clinician cl2 = sch.getECClinician(2, 3, 16);

		int id1 = cl1.getClinicianBean().getClinicianID();
		int id2 = cl2.getClinicianBean().getClinicianID();
		CommitmentBean cmt1 = new CommitmentBean(id1, 8, 9,
				Date.valueOf("2015-1-29"), "");
		CommitmentBean cmt2 = new CommitmentBean(id2, 7, 8,
				Date.valueOf("2015-2-6"), "");
		cl1.getCommitmentBeans().add(cmt1);
		cl2.getCommitmentBeans().add(cmt2);

		Set<Clinician> violations = validateAction
				.validateMorningMeetingAfternoonECConflicts(sch);
		assertTrue(violations.contains(cl1));
		assertTrue(violations.contains(cl2));
	}

	/**
	 * Test validate one ia per day.
	 */
	@Test
	public void testValidateOneIAPerDay() {
		Clinician cl1 = sch.getIAClinician(true, 0, 11).get(0);
		Clinician cl2 = sch.getIAClinician(false, 1, 13).get(0);

		sch.addIAClinician(true, 0, 13, cl1.getClinicianBean().getName());
		sch.addIAClinician(false, 1, 15, cl2.getClinicianBean().getName());

		Set<Clinician> violations = validateAction.validateOneIAPerDay(sch);
		assertTrue(violations.contains(cl1));
		assertTrue(violations.contains(cl2));
	}

	/**
	 * Test validate one ec per week.
	 */
	@Test
	public void testValidateOneECPerWeek() {
		Clinician cl1 = sch.getECClinician(1, 0, 8);
		sch.editEC(Date.valueOf("2015-1-28"), 12, cl1.getClinicianBean()
				.getName());

		Clinician cl2 = sch.getECClinician(2, 1, 12);
		sch.editEC(Date.valueOf("2015-2-6"), 16, cl2.getClinicianBean()
				.getName());

		Set<Clinician> violations = validateAction.validateOneECPerWeek(sch);
		assertTrue(violations.contains(cl1));
		assertTrue(violations.contains(cl2));
	}

}
