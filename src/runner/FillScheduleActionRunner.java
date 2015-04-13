package runner;

import generator.TestDataGenerator;

import java.sql.Connection;

import action.FillScheduleAction;
import action.GenerateUnfilledScheduleAction;
import dao.ConnectionFactory;

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
