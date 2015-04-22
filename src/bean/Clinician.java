package bean;

import java.util.List;

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
			return this == oth;
		}
		Clinician other = (Clinician) oth;
		return this.clinicianBean.equals(other.clinicianBean)
				&& this.clinicianPreferencesBean.equals(oth);
	}

	public boolean canCover(SessionBean session) {
		// TODO: implement me to return true iff this clinician is available to 
		// cover this session based on meetings and time away (ignoring other IA/EC constraints)
		return false;
	}
}
