package runner;

import static org.junit.Assert.assertEquals;
import generator.TestDataGenerator;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import bean.CalendarBean;
import bean.SessionBean;
import bean.Weekday;
import dao.CalendarDAO;
import dao.ConnectionFactory;
import dao.SessionsDAO;
import action.FillScheduleAction;
import action.GenerateUnfilledScheduleAction;

public class FillScheduleActionRunner {

	
	public static void main(String[] args) {
		
		Connection conn;
		
		
		FillScheduleAction fillScheduleAction = null;
		try {
			conn = ConnectionFactory.getInstance();
			TestDataGenerator gen = new TestDataGenerator(conn);
			gen.clearTables();
			gen.generateStandardDataset();
			GenerateUnfilledScheduleAction action = new GenerateUnfilledScheduleAction(conn);
			action.generateUnfilledSchedule();
			fillScheduleAction = new FillScheduleAction(conn);
			fillScheduleAction.fillSchedule();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
}
