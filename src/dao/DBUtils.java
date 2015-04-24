package dao;

import java.sql.SQLException;

import generator.TestDataGenerator;

public class DBUtils {
	public static void clearAllTablesExceptClinicians() throws SQLException {
		TestDataGenerator generator = new TestDataGenerator(ConnectionFactory.getInstance());
		generator.clearCalendarTable();
		generator.clearClinicianPreferencesTable();
		generator.clearCommitmentsTable();
		generator.clearTimeAwayTable();
		generator.clearHolidayTable();
		generator.clearSessionCliniciansTable();
		generator.clearSessionsTable();
	}
}
