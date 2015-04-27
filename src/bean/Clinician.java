package bean;

import java.util.Date;
import java.util.List;

/**
 * Contains all the information for a clinician, from ClinicianBean,
 * ClinicianPreferencesBean, CommitmentBeans, and TimeAwayBeans.
 * 
 * @author dlit2, lim92, ramusa2
 *
 */
public class Clinician {
	
	/** The clinician bean. */
	private ClinicianBean clinicianBean;
	
	/** The clinician preferences bean. */
	private ClinicianPreferencesBean clinicianPreferencesBean;
	
	/** The commitment beans. */
	private List<CommitmentBean> commitmentBeans;
	
	/** The time away beans. */
	private List<TimeAwayBean> timeAwayBeans;
	
	/**
	 * Instantiates a new clinician.
	 *
	 * @param cb the cb
	 * @param cbPref the cb pref
	 * @param cmb the cmb
	 * @param tab the tab
	 */
	public Clinician(ClinicianBean cb, ClinicianPreferencesBean cbPref, List<CommitmentBean> cmb, List<TimeAwayBean> tab) {
		clinicianBean = cb;
		clinicianPreferencesBean = cbPref;
		commitmentBeans = cmb;
		timeAwayBeans = tab;
	}

	/**
	 * Gets the clinician bean.
	 *
	 * @return the clinician bean
	 */
	public ClinicianBean getClinicianBean() {
		return clinicianBean;
	}
	
	/**
	 * Gets the clinician preferences bean.
	 *
	 * @return the clinician preferences bean
	 */
	public ClinicianPreferencesBean getClinicianPreferencesBean() {
		return clinicianPreferencesBean;
	}

	/**
	 * Gets the time away beans.
	 *
	 * @return the time away beans
	 */
	public List<TimeAwayBean> getTimeAwayBeans() {
		return timeAwayBeans;
	}
	
	/**
	 * Gets the commitment beans.
	 *
	 * @return the commitment beans
	 */
	public List<CommitmentBean> getCommitmentBeans() {
		return commitmentBeans;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override 
	public int hashCode() {
		return this.clinicianBean.hashCode();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object oth) {
		if(!(oth instanceof Clinician)) {
			return false;
		}
		Clinician other = (Clinician) oth;
		return this.clinicianBean.equals(other.clinicianBean);
	}

	/**
	 * Can cover.
	 *
	 * @param session the session
	 * @return true, if successful
	 */
	public boolean canCover(SessionBean session) {
		for(CommitmentBean cb : this.commitmentBeans) {
			if(cb.getDate().equals(session.getDate())
					&& cb.getStartHour() <= session.getStartTime()
					&& cb.getEndHour() >= (session.getDuration()+session.getStartTime())) {
				return false;
			}
		}
		if(session.getType() == SessionType.EC) {
			Date sd = session.getDate();
			for(TimeAwayBean tb : this.timeAwayBeans) {
				Date tbStart = tb.getStartDate();
				Date tbEnd = tb.getEndDate();
				if(!sd.before(tbStart)
						&& !sd.after(tbEnd)) {
					return false;
				}
			}
		}
		return true;
	}
}
