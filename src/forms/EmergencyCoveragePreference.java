package forms;

/**
 * Represents a clinician's preference for emergency coverage times
 * 
 * @author Yusheng Hou and Kevin Lim
 */
public class EmergencyCoveragePreference {
	private int morningRank;
	private int noonRank;
	private int afternoonRank;

	/**
	 * Creates a clinician's coverage for emergence coverage sessions
	 * @param morning ranking, between 1 and 3, cannot overlap with others
	 * @param noon ranking, between 1 and 3, cannot overlap with others
	 * @param afternoon ranking, between 1 and 3, cannot overlap with others
	 */
	public EmergencyCoveragePreference(int morning, int noon, int afternoon) {
		this.morningRank = morning;
		this.noonRank = noon;
		this.afternoonRank = afternoon;
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
