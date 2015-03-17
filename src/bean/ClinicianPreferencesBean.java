package bean;

/**
 * The Class ClinicianPreferences handles the storage of clinician preferences.
 * 
 * @author jmfoste2, lim92
 */
public class ClinicianPreferencesBean {
	
	/** The clinician id. */
	private int clinicianID;
	
	/** The morning rank. */
	private int morningRank;
	
	/** The noon rank. */
	private int noonRank;
	
	/** The afternoon rank. */
	private int afternoonRank;

	/**
	 * Creates a clinician's coverage for emergence coverage sessions.
	 *
	 * @param id the id
	 * @param morning ranking, between 1 and 3, cannot overlap with others
	 * @param noon ranking, between 1 and 3, cannot overlap with others
	 * @param afternoon ranking, between 1 and 3, cannot overlap with others
	 */
	public ClinicianPreferencesBean(int id, int morning, int noon, int afternoon) {
		clinicianID = id;
		morningRank = morning;
		noonRank = noon;
		afternoonRank = afternoon;
	}
	
	/**
	 * Gets the clinician id.
	 *
	 * @return the clinician id
	 */
	public int getClinicianID() {
		return clinicianID;
	}

	/**
	 * Sets the clinician id.
	 *
	 * @param clinicianID the new clinician id
	 */
	public void setClinicianID(int clinicianID) {
		this.clinicianID = clinicianID;
	}
	
	/**
	 * Gets the morning rank.
	 *
	 * @return the morning rank
	 */
	public int getMorningRank() {
		return morningRank;
	}
	
	/**
	 * Sets the morning rank.
	 *
	 * @param morningRank the new morning rank
	 */
	public void setMorningRank(int morningRank) {
		this.morningRank = morningRank;
	}
	
	/**
	 * Gets the noon rank.
	 *
	 * @return the noon rank
	 */
	public int getNoonRank() {
		return noonRank;
	}
	
	/**
	 * Sets the noon rank.
	 *
	 * @param noonRank the new noon rank
	 */
	public void setNoonRank(int noonRank) {
		this.noonRank = noonRank;
	}
	
	/**
	 * Gets the afternoon rank.
	 *
	 * @return the afternoon rank
	 */
	public int getAfternoonRank() {
		return afternoonRank;
	}
	
	/**
	 * Sets the afternoon rank.
	 *
	 * @param afternoonRank the new afternoon rank
	 */
	public void setAfternoonRank(int afternoonRank) {
		this.afternoonRank = afternoonRank;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other == null) {
			return false;
		}
		if (!(other instanceof ClinicianPreferencesBean)) {
			return false;
		}
		ClinicianPreferencesBean preference = (ClinicianPreferencesBean)other;
		return morningRank == preference.morningRank && noonRank == preference.noonRank 
				&& afternoonRank == preference.afternoonRank && clinicianID == preference.clinicianID;
	}
}
