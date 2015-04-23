package linearprogram;

import java.sql.SQLException;

import bean.Schedule;
import bean.SessionBean;

public class ScheduleProgramTest {

	
	// Week tests
	
	public static void main(String[] args) throws SQLException {
		Schedule schedule = Schedule.loadScheduleFromDBAndAssignClinicians();
		for(SessionBean session : schedule.getSessions()) {
			System.out.println(session.getVariableString()+" : "+session.getClinicians());
		}
	}
}
