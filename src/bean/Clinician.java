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
}
