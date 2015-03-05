package db;

public class Clinician {
	
	private int clinicianID;
	private String name;
	
	public Clinician(int id, String n) {
		clinicianID = id;
		name = n;
	}

	public int getClinicianID() {
		return clinicianID;
	}

	public void setClinicianID(int id) {
		clinicianID = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String n) {
		name = n;
	}
}
