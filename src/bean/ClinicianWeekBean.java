package bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores the list of sessionBeans for a given week.
 * 
 * @author dtli2, lim92, ramusa2
 *
 */
public class ClinicianWeekBean {

	private List<SessionBean> sessions;
	
	public ClinicianWeekBean() {
		sessions = new ArrayList<SessionBean>();
	}
	
	public List<SessionBean> getSessions() {
		return sessions;
	}
	
	public void addSession(SessionBean sb) {
		sessions.add(sb);
	}
	
	public void removeSession(SessionBean sb) {
		sessions.remove(sb);
	}	
}
