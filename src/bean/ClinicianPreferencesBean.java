package bean;

// TODO: Auto-generated Javadoc
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

    /** Ranking in order. */
    private int[] ranking;

    /** The ia hours. */
    private int iaHours;

    /** The ec hours. */
    private int ecHours;

    /**
     * Creates a clinician's coverage for emergence coverage sessions.
     *
     * @param id
     *            the id
     * @param morning
     *            ranking, between 1 and 3, cannot overlap with others
     * @param noon
     *            ranking, between 1 and 3, cannot overlap with others
     * @param afternoon
     *            ranking, between 1 and 3, cannot overlap with others
     * @param iaHrs
     *            the ia hrs
     * @param ecHrs
     *            the ec hrs
     */
    public ClinicianPreferencesBean(int id, int morning, int noon,
	    int afternoon, int iaHrs, int ecHrs) {
	clinicianID = id;
	morningRank = morning;
	noonRank = noon;
	afternoonRank = afternoon;
	ranking = new int[4];
	ranking[morning] = 1;
	ranking[noon] = 2;
	ranking[afternoon] = 3;
	iaHours = iaHrs;
	ecHours = ecHrs;
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
     * @param clinicianID
     *            the new clinician id
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
     * @param morningRank
     *            the new morning rank
     */
    public void setMorningRank(int morningRank) {
	this.morningRank = morningRank;
	ranking[morningRank] = 1;
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
     * @param noonRank
     *            the new noon rank
     */
    public void setNoonRank(int noonRank) {
	this.noonRank = noonRank;
	ranking[noonRank] = 2;
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
     * @param afternoonRank
     *            the new afternoon rank
     */
    public void setAfternoonRank(int afternoonRank) {
	this.afternoonRank = afternoonRank;
	ranking[afternoonRank] = 3;
    }

    /**
     * Returns the time by ranking where morning = 1, noon = 2, and afternoon =
     * 3.
     *
     * @param rank
     *            1-3
     * @return the ranking
     */
    public int getRanking(int rank) {
	return ranking[rank];
    }

    /**
     * Returns the ranking array.
     *
     * @param startTime
     *            the start time
     * @return the ranking from time
     */
    public int getRankingFromTime(int startTime) {
	int timeToGet = startTime == 8 ? 1 : (startTime == 12 ? 2 : 3);
	for (int i = 1; i < this.ranking.length; i++) {
	    if (this.ranking[i] == timeToGet) {
		return i;
	    }
	}
	return 0;
    }

    /**
     * Gets the IA hours.
     *
     * @return the IA hours
     */
    public int getIAHours() {
	return iaHours;
    }

    /**
     * Sets the IA hours.
     *
     * @param hours
     *            the new IA hours
     */
    public void setIAHours(int hours) {
	iaHours = hours;
    }

    /**
     * Gets the EC hours.
     *
     * @return the EC hours
     */
    public int getECHours() {
	return ecHours;
    }

    /**
     * Sets the EC hours.
     *
     * @param hours
     *            the new EC hours
     */
    public void setECHours(int hours) {
	ecHours = hours;
    }

    /*
     * (non-Javadoc)
     * 
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
	ClinicianPreferencesBean preference = (ClinicianPreferencesBean) other;
	return morningRank == preference.morningRank
		&& noonRank == preference.noonRank
		&& afternoonRank == preference.afternoonRank
		&& clinicianID == preference.clinicianID
		&& iaHours == preference.iaHours
		&& ecHours == preference.ecHours;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
	throw new UnsupportedOperationException(
		"Hash code is not implemented for this class");
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
	return "morningRank:" + morningRank + " noonRank:" + noonRank
		+ " afternoonRank:" + afternoonRank + " clinicianID:"
		+ clinicianID + " iaHours:" + iaHours + " ecHours:" + ecHours;
    }
}
