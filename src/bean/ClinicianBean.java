package bean;

/**
 * The Class Clinician handles the storage of clinician data.
 */
public class ClinicianBean implements Comparable {
	
	/** The clinician id. */
	private int clinicianID;
	
	/** The name. */
	private String name;
	
	/** The username */
	private String username;
	
	/**
	 * Instantiates a new clinician.
	 *
	 * @param id the id
	 * @param n the n
	 */
	public ClinicianBean(int id, String n) {
		this(id, n, n);
	}

	/**
	 * Instantiates a new clinician.
	 *
	 * @param id the id
	 * @param n the n
	 * @param username
	 */
	public ClinicianBean(int id, String n, String username) {
		clinicianID = id;
		name = n;
		this.username = username;
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
	
	/**
	 * Gets the username.
	 *
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Sets the username.
	 *
	 * @param n the new username
	 */
	public void setUsername(String username) {
		this.username = username;
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
		if (!(other instanceof ClinicianBean)) {
			return false;
		}
		ClinicianBean clinician = (ClinicianBean)other;
		return clinicianID == clinician.clinicianID && name.equals(clinician.name);
	}
	
	@Override
	public int compareTo(Object arg0) {
		if(!(arg0 instanceof ClinicianBean)) {
			return -1;
		}
		return this.name.compareTo(((ClinicianBean)arg0).name);
	}
	
	@Override
	public String toString() {
		return this.name;
	}
}
