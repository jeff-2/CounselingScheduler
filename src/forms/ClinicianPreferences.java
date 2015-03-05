package forms;

public class ClinicianPreferences {
	
	private int clinicianID;
	private int morningRank;
	private int noonRank;
	private int afternoonRank;

	/**
	 * Creates a clinician's coverage for emergence coverage sessions
	 * @param morning ranking, between 1 and 3, cannot overlap with others
	 * @param noon ranking, between 1 and 3, cannot overlap with others
	 * @param afternoon ranking, between 1 and 3, cannot overlap with others
	 */
	public ClinicianPreferences(int id, int morning, int noon, int afternoon) {
		clinicianID = id;
		morningRank = morning;
		noonRank = noon;
		afternoonRank = afternoon;
	}
	
	public int getClinicianID() {
		return clinicianID;
	}

	public void setClinicianID(int clinicianID) {
		this.clinicianID = clinicianID;
	}
	
	public int getMorningRank() {
		return morningRank;
	}
	
	public void setMorningRank(int morningRank) {
		this.morningRank = morningRank;
	}
	
	public int getNoonRank() {
		return noonRank;
	}
	
	public void setNoonRank(int noonRank) {
		this.noonRank = noonRank;
	}
	
	public int getAfternoonRank() {
		return afternoonRank;
	}
	
	public void setAfternoonRank(int afternoonRank) {
		this.afternoonRank = afternoonRank;
	}
}
