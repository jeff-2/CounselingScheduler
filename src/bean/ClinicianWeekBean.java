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

    /** The sessions. */
    private List<SessionBean> sessions;

    /**
     * Instantiates a new clinician week bean.
     */
    public ClinicianWeekBean() {
	sessions = new ArrayList<SessionBean>();
    }

    /**
     * Gets the sessions beans in this week.
     *
     * @return the sessions
     */
    public List<SessionBean> getSessions() {
	return sessions;
    }

    /**
     * Adds the session bean to this week.
     *
     * @param sb
     *            the sb
     */
    public void addSession(SessionBean sb) {
	sessions.add(sb);
    }

    /**
     * Removes the session bean from this week.
     *
     * @param sb
     *            the sb
     */
    public void removeSession(SessionBean sb) {
	sessions.remove(sb);
    }
}
