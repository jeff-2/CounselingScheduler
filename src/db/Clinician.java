package db;

/**
 * The Class Clinician handles the storage of clinician data.
 * 
 * @author jmfoste2, lim92
 */
public class Clinician implements Comparable<Clinician>{
	
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
	public Clinician(int id, String n) {
		this(id, n, n);
	}

	/**
	 * Instantiates a new clinician.
	 *
	 * @param id the id
	 * @param n the n
	 * @param username
	 */
	public Clinician(int id, String n, String username) {
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
		if (!(other instanceof Clinician)) {
			return false;
		}
		Clinician clinician = (Clinician)other;
		return clinicianID == clinician.clinicianID && name.equals(clinician.name);
	}

	/**
	 * Represent a Clinician as a String using their username and real name
	 * 
	 * @return String representation of Clinician as username (full name)
	 */
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return username + " (" + name + ")";
	}

	/**
	 * Compares Clinician using their usernames
	 * 
	 * @param o Clinician to compare to
	 * @return how this Clinician's username compares to the other Clinician's username
	 */
	@Override
	public int compareTo(Clinician o) {
		if (o != null)
		{
			return getUsername().compareTo(o.getUsername());
		}
		else
		{
			return 1;
		}
	}
	
}
