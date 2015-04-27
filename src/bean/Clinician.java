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
	
	private ClinicianBean clinicianBean;
	private ClinicianPreferencesBean clinicianPreferencesBean;
	private List<CommitmentBean> commitmentBeans;
	private List<TimeAwayBean> timeAwayBeans;
	
	public Clinician(ClinicianBean cb, ClinicianPreferencesBean cbPref, List<CommitmentBean> cmb, List<TimeAwayBean> tab) {
		clinicianBean = cb;
		clinicianPreferencesBean = cbPref;
		commitmentBeans = cmb;
		timeAwayBeans = tab;
	}

	public ClinicianBean getClinicianBean() {
		return clinicianBean;
	}
	
	public ClinicianPreferencesBean getClinicianPreferencesBean() {
		return clinicianPreferencesBean;
	}

	public List<TimeAwayBean> getTimeAwayBeans() {
		return timeAwayBeans;
	}
	
	public List<CommitmentBean> getCommitmentBeans() {
		return commitmentBeans;
	}
	
	@Override 
	public int hashCode() {
		return this.clinicianBean.hashCode();
	}
	
	@Override
	public boolean equals(Object oth) {
		if(!(oth instanceof Clinician)) {
			return false;
		}
		Clinician other = (Clinician) oth;
		return this.clinicianBean.equals(other.clinicianBean);
	}

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
