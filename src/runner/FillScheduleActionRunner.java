package runner;

import java.sql.Connection;
import java.sql.SQLException;

import dao.ConnectionFactory;
import action.FillScheduleAction;

public class FillScheduleActionRunner {

	
	public static void main(String[] args) {
		
		Connection conn;
		FillScheduleAction fillScheduleAction = null;
		try {
			conn = ConnectionFactory.getInstance();
			fillScheduleAction = new FillScheduleAction(conn);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		fillScheduleAction.fillSchedule();
	}
	
}
