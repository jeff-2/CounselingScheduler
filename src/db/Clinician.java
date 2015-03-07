package db;

/**
 * The Class Clinician handles the storage of clinician data.
 * 
 * @author jmfoste2, lim92
 */
public class Clinician implements Comparable {
	
	/** The clinician id. */
	private int clinicianID;
	
	/** The name. */
	private String name;
	
	/**
	 * Instantiates a new clinician.
	 *
	 * @param id the id
	 * @param n the n
	 */
	public Clinician(int id, String n) {
		clinicianID = id;
		name = n;
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
	 * @param id the new clinician id
	 */
	public void setClinicianID(int id) {
		clinicianID = id;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param n the new name
	 */
	public void setName(String n) {
		name = n;
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
		if (!(other instanceof Clinician)) {
			return false;
		}
		Clinician clinician = (Clinician)other;
		return clinicianID == clinician.clinicianID && name.equals(clinician.name);
	}

	@Override
	public int compareTo(Object arg0) {
		if(!(arg0 instanceof Clinician)) {
			return -1;
		}
		return this.name.compareTo(((Clinician)arg0).name);
	}
	
	@Override
	public String toString() {
		return this.name;
	}
}
