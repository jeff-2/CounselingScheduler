package bean;

/**
 * Struct to hold clinician IDs with their first and second time slot preferences for EC.
 * @author dtli2, lim92
 *
 */
public class ClinicianPref {

	public int id;
	public int prefOne;
	public int prefTwo;

	public ClinicianPref(int id, int prefOne, int prefTwo) {
		this.id = id;
		this.prefOne = prefOne;
		this.prefTwo = prefTwo;
	}
}