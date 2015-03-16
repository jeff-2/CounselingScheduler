package action;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import bean.ClinicianPreferencesBean;
import bean.SessionBean;
import dao.ClinicianDAO;
import dao.ClinicianPreferencesDAO;
import dao.SessionsDAO;
import bean.ClinicianBean;

/**
 * Automates assigning clinicians to each session 
 * 
 * @author lim92, dtli2
 *
 */
public class FillScheduleAction {

	protected Connection conn;

	public FillScheduleAction(Connection connection) {
		conn = connection;
	}

	public void fillSchedule() {
		try {
			ClinicianDAO clinicianDAO = new ClinicianDAO(this.conn);
			ClinicianPreferencesDAO clinicianPreferencesDAO = new ClinicianPreferencesDAO(this.conn);
			SessionsDAO sessionsDAO = new SessionsDAO(this.conn);

			List<SessionBean> sessions = sessionsDAO.loadSessions();

			HashMap<Integer, Integer> ecAssignments = new HashMap<Integer, Integer>();
			ArrayList<Integer> morningClinicianIDs = new ArrayList<Integer>();
			ArrayList<Integer> noonClinicianIDs = new ArrayList<Integer>();
			ArrayList<Integer> afternoonClinicianIDs = new ArrayList<Integer>();
			int lastDayOfWeek = 10;
			for (SessionBean sb : sessions) {
				int currDayOfWeek = sb.getDayOfWeek().ordinal();
				
				// for every new week regenerate our 3 arraylists for morning, noon, afternoon clinician ids
				if (currDayOfWeek < lastDayOfWeek) {
					ecAssignments = generateEcAssignments(clinicianDAO, clinicianPreferencesDAO);
					morningClinicianIDs = new ArrayList<Integer>();
					noonClinicianIDs = new ArrayList<Integer>();
					afternoonClinicianIDs = new ArrayList<Integer>();
					for (Integer key : ecAssignments.keySet()) {
						if (ecAssignments.get(key) == 1) {
							morningClinicianIDs.add(key);
						} else if (ecAssignments.get(key) == 2) {
							noonClinicianIDs.add(key);
						} else {
							afternoonClinicianIDs.add(key);
						}
					}
				}
				
				// get new clinician id for session
				List<Integer> clinicianID = new ArrayList<Integer>();
				if (sb.getStartTime() == 8) {
					clinicianID.add(morningClinicianIDs.get(0));
					morningClinicianIDs.remove(0);
				} else if (sb.getStartTime() == 12) {
					clinicianID.add(noonClinicianIDs.get(0));
					noonClinicianIDs.remove(0);
				} else {
					clinicianID.add(afternoonClinicianIDs.get(0));
					afternoonClinicianIDs.remove(0);
				}
				
				sessionsDAO.insertSessionClinicians(sb.getID(), clinicianID);
				
				lastDayOfWeek = currDayOfWeek;
			}
			
			int[] nextDayOfWeek = new int[] {0, 0, 0};

			for (Integer key : ecAssignments.keySet()) {
				nextDayOfWeek[ecAssignments.get(key) - 1]++;
				System.out.println(key + " " + ecAssignments.get(key));
			}

		}
		catch(Exception e) { }
	}
	
	private HashMap<Integer, Integer> generateEcAssignments(ClinicianDAO clinicianDAO, ClinicianPreferencesDAO clinicianPreferencesDAO) throws SQLException {
		List<ClinicianPref> clinicians = buildPreferencesList(clinicianDAO, clinicianPreferencesDAO);
		HashMap<Integer, Integer> ecAssignments = new HashMap<Integer, Integer>(); // mapping id to assignment
		int[] prefOneCounts = countRankings(clinicians, 1);
		
		// Ideal case with balanced first preferences
		if (prefOneCounts[0] == 5 && prefOneCounts[1] == 5) {
			ecAssignments = assignECSlotsForE5E5E5(clinicians);
		}

		// Almost-ideal case with only one time slot == 5
		// Fill the 5 count and the <5 count time slots with first
		//// preferences and split the remainder based on second
		//// preferences.
		if ((prefOneCounts[0] == 5 && prefOneCounts[1] != 5) ||
				(prefOneCounts[1] == 5 && prefOneCounts[0] != 5) ||
				(prefOneCounts[2] == 5 && prefOneCounts[0] != 5)) {
			ecAssignments = assignECSlotsForLT5E5GT5(clinicians, prefOneCounts);
		}

		// Non-ideal case with one time slot count < 5
		// Fill non-popular choice and then fill the other two
		// Built on the assumption that after completely filling the
		//// unpopular time slot, everyone else can get their first choice.
		if ((prefOneCounts[0] > 5 && prefOneCounts[1] > 5) ||
				(prefOneCounts[0] > 5 && prefOneCounts[2] > 5) ||
				(prefOneCounts[1] > 5 && prefOneCounts[2] > 5)) {
			ecAssignments = assignECSlotsForLT5GT5GT5(clinicians, prefOneCounts);
		}

		// Non-ideal case with two time slot counts < 5
		// Fill non-popular choices and then fill the other one
		// Built on the assumption that all clinicians left to be assigned
		//// after the first round of assignments want the same time slot
		//// for their first choice.
		if ((prefOneCounts[0] < 5 && prefOneCounts[1] < 5) ||
				(prefOneCounts[0] < 5 && prefOneCounts[2] < 5) ||
				(prefOneCounts[1] < 5 && prefOneCounts[2] < 5)) {
			ecAssignments = assignECSlotsForLT5LT5GT5(clinicians, prefOneCounts);
		}
		
		return ecAssignments;
	}

	/**
	 * Assignment algorithm for case with all counts == 5
	 * @param clinicians
	 * @return ecAssignments
	 */
	private HashMap<Integer, Integer> assignECSlotsForE5E5E5(List<ClinicianPref> clinicians) {
		HashMap<Integer, Integer> ecAssignments = new HashMap<Integer, Integer>();
		for (ClinicianPref pref : clinicians) {
			ecAssignments.put(new Integer(pref.id), new Integer(pref.prefOne));
		}
		return ecAssignments;
	}

	/**
	 * Assignment algorithm for case where one count < 5, one count == 5, and one count > 5
	 * @param clinicians
	 * @param prefOneCounts
	 * @return ecAssignments
	 */
	private HashMap<Integer, Integer> assignECSlotsForLT5E5GT5(List<ClinicianPref> clinicians, int[] prefOneCounts) {
		HashMap<Integer, Integer> ecAssignments = new HashMap<Integer, Integer>();
		int indexFor5 = -1;
		int indexForLT5 = -1;
		int indexForGT5 = -1;

		if (prefOneCounts[0] == 5) {
			indexFor5 = 0;
		} else if (prefOneCounts[0] > 5) {
			indexForGT5 = 0;
		} else {
			indexForLT5 = 0;
		}

		if (prefOneCounts[1] == 5) {
			indexFor5 = 1;
		} else if (prefOneCounts[1] > 5) {
			indexForGT5 = 1;
		} else {
			indexForLT5 = 1;
		}

		if (prefOneCounts[2] == 5) {
			indexFor5 = 2;
		} else if (prefOneCounts[2] > 5) {
			indexForGT5 = 2;
		} else {
			indexForLT5 = 2;
		}

		int[] assignmentCounts = new int[] {0, 0, 0};

		// Assign clinicians with unpopular preferences to their first choice
		for (int i = 14; i >= 0; i--) {
			if (clinicians.get(i).prefOne == indexFor5 + 1 ||
					clinicians.get(i).prefOne == indexForLT5 + 1) {
				ecAssignments.put(new Integer(clinicians.get(i).id), new Integer(clinicians.get(i).prefOne));
				assignmentCounts[clinicians.get(i).prefOne - 1]++;
				clinicians.remove(i);
			}
		}

		// First try to assign remaining indexForLT5 slots based on prefTwo
		Collections.shuffle(clinicians);
		for (int i = clinicians.size() - 1; i >= 0; i--) {
			if (clinicians.get(i).prefTwo == indexForLT5 + 1 && 
					assignmentCounts[indexForLT5] < 5) {
				ecAssignments.put(new Integer(clinicians.get(i).id), new Integer(clinicians.get(i).prefTwo));
				assignmentCounts[clinicians.get(i).prefTwo - 1]++;
				clinicians.remove(i);
			}
		}

		// In case we still aren't done, assign the rest randomly
		if (clinicians.size() > 0) {
			Collections.shuffle(clinicians);
			// Assign the remaining indexForLT5 slots
			for (int i = 0; i < (5 - assignmentCounts[indexForLT5]); i++) {
				ecAssignments.put(new Integer(clinicians.get(0).id), new Integer(indexForLT5 + 1));
				assignmentCounts[indexForLT5]++;
				clinicians.remove(0);
			}
			// Assign the remaining slots
			for (int i = 0; i < 5; i++) {
				ecAssignments.put(new Integer(clinicians.get(0).id), new Integer(indexForGT5 + 1));
				assignmentCounts[indexForGT5]++;
				clinicians.remove(0);
			}
		}

		return ecAssignments;
	}

	/**
	 * Assignment algorithm for case where one count < 5 and two counts > 5
	 * @param clinicians
	 * @param prefOneCounts
	 * @return ecAssignments
	 */
	private HashMap<Integer, Integer> assignECSlotsForLT5GT5GT5(List<ClinicianPref> clinicians, int[] prefOneCounts) {
		HashMap<Integer, Integer> ecAssignments = new HashMap<Integer, Integer>();

		int indexForLT5 = -1;
		int indexForGT5a = -1;
		int indexForGT5b = -1;

		if (prefOneCounts[0] < 5) {
			indexForLT5 = 0;
			indexForGT5a = 1;
			indexForGT5b = 2;
		} else if (prefOneCounts[1] < 5) {
			indexForLT5 = 1;
			indexForGT5a = 0;
			indexForGT5b = 2;
		} else {
			indexForLT5 = 2;
			indexForGT5a = 0;
			indexForGT5b = 1;
		}

		// Assign clinicians with least popular time slot as first choice
		for (int i = 14; i >= 0; i--) {
			if (clinicians.get(i).prefOne - 1 == indexForLT5) {
				ecAssignments.put(new Integer(clinicians.get(i).id), new Integer(indexForLT5 + 1));
				clinicians.remove(i);
			}
		}

		Collections.shuffle(clinicians);

		// Assign clinicians with first pref = GT5a to LT5 based on second pref
		int excessGT5a = prefOneCounts[indexForGT5a] - 5;
		int countExcessAssignedGT5a = 0;
		for (int i = clinicians.size() - 1; i >= 0; i--) {
			if (clinicians.get(i).prefOne - 1 == indexForGT5a && clinicians.get(i).prefTwo - 1 == indexForLT5) {
				ecAssignments.put(new Integer(clinicians.get(i).id), new Integer(indexForLT5 + 1));
				clinicians.remove(i);
				countExcessAssignedGT5a++;
			}
			if (excessGT5a == countExcessAssignedGT5a) {
				break;
			}
		}

		// Assign clinicians with first pref = GT5b to LT5 based on second pref
		int excessGT5b = prefOneCounts[indexForGT5b] - 5;
		int countExcessAssignedGT5b = 0;
		for (int i = clinicians.size() - 1; i >= 0; i--) {
			if (clinicians.get(i).prefOne - 1 == indexForGT5b && clinicians.get(i).prefTwo - 1 == indexForLT5) {
				ecAssignments.put(new Integer(clinicians.get(i).id), new Integer(indexForLT5 + 1));
				clinicians.remove(i);
				countExcessAssignedGT5b++;
			}
			if (excessGT5b == countExcessAssignedGT5b) {
				break;
			}
		}

		// Assign excess clinicians with first pref = GT5a randomly
		if (excessGT5a > countExcessAssignedGT5a) {
			for (int i = clinicians.size() - 1; i >= 0; i++) {
				if (excessGT5a > countExcessAssignedGT5a) {
					if (clinicians.get(i).prefOne - 1 == indexForGT5a) {
						ecAssignments.put(new Integer(clinicians.get(i).id), new Integer(indexForLT5 + 1));
						clinicians.remove(i);
						countExcessAssignedGT5a++;
					}
				} else {
					break;
				}
			}
		}

		// Assign excess clinicians with first pref = GT5b randomly
		if (excessGT5b > countExcessAssignedGT5b) {
			for (int i = clinicians.size() - 1; i >= 0; i++) {
				if (excessGT5b > countExcessAssignedGT5b) {
					if (clinicians.get(i).prefOne - 1 == indexForGT5b) {
						ecAssignments.put(new Integer(clinicians.get(i).id), new Integer(indexForLT5 + 1));
						clinicians.remove(i);
						countExcessAssignedGT5b++;
					}
				} else {
					break;
				}
			}
		}

		// Assign remaining clinicians to their first pref
		for (ClinicianPref pref : clinicians) {
			ecAssignments.put(new Integer(pref.id), new Integer(pref.prefOne));
		}

		return ecAssignments;
	}

	/**
	 * Assignment algorithm for case where two counts < 5 and one count > 5
	 * @param clinicians
	 * @param prefOneCounts
	 * @return ecAssignments
	 */
	private HashMap<Integer, Integer> assignECSlotsForLT5LT5GT5(List<ClinicianPref> clinicians, int[] prefOneCounts) {
		HashMap<Integer, Integer> ecAssignments = new HashMap<Integer, Integer>();

		int indexForLT5a = -1;
		int indexForLT5b = -1;
		int indexForGT5 = -1;

		if (prefOneCounts[0] > 5) {
			indexForGT5 = 0;
			indexForLT5a = 1;
			indexForLT5b = 2;
		} else if (prefOneCounts[1] > 5) {
			indexForGT5 = 1;
			indexForLT5a = 0;
			indexForLT5b = 2;
		} else {
			indexForGT5 = 2;
			indexForLT5a = 0;
			indexForLT5b = 1;
		}

		// Assign clinicians with first pref < 5
		for (int i = 14; i >= 0; i--) {
			if (clinicians.get(i).prefOne - 1 == indexForLT5a ||
					clinicians.get(i).prefOne - 1 == indexForLT5b) {
				ecAssignments.put(new Integer(clinicians.get(i).id), new Integer(clinicians.get(i).prefOne));
				clinicians.remove(i);
			}
		}

		Collections.shuffle(clinicians);

		// Try to assign GT5 clinicians to second pref
		int excessLT5a = 5 - prefOneCounts[indexForLT5a];
		int excessLT5b = 5 - prefOneCounts[indexForLT5b];
		int countExcessAssignedLT5a = 0;
		int countExcessAssignedLT5b = 0;
		for (int i = clinicians.size() - 1; i >= 0; i--) {
			if (clinicians.get(i).prefTwo - 1 == indexForLT5a && countExcessAssignedLT5a < excessLT5a) {
				ecAssignments.put(new Integer(clinicians.get(i).id), new Integer(indexForLT5a + 1));
				clinicians.remove(i);
				countExcessAssignedLT5a++;
			} else if (clinicians.get(i).prefTwo - 1 == indexForLT5b && countExcessAssignedLT5b < excessLT5b) {
				ecAssignments.put(new Integer(clinicians.get(i).id), new Integer(indexForLT5b + 1));
				clinicians.remove(i);
				countExcessAssignedLT5b++;
			}
		}

		Collections.shuffle(clinicians);

		// Assign LT5a randomly
		if (countExcessAssignedLT5a < excessLT5a) {
			for (int i = 0; i < excessLT5a - countExcessAssignedLT5a; i++) {
				ecAssignments.put(new Integer(clinicians.get(0).id), new Integer(indexForLT5a + 1));
				clinicians.remove(0);
			}
		}

		// Assign LT5b randomly
		if (countExcessAssignedLT5b < excessLT5b) {
			for (int i = 0; i < excessLT5b - countExcessAssignedLT5b; i++) {
				ecAssignments.put(new Integer(clinicians.get(0).id), new Integer(indexForLT5b + 1));
				clinicians.remove(0);
			}
		}

		// Assign the rest to pref one
		for (int i = 0; i < 5; i++) {
			ecAssignments.put(new Integer(clinicians.get(i).id), new Integer(clinicians.get(i).prefOne));
		}

		return ecAssignments;
	}

	private int[] countRankings(List<ClinicianPref> clinicianPrefs, int rank) {
		int[] counts = new int[] {0 , 0 , 0};

		for(ClinicianPref pref : clinicianPrefs) {
			if (rank == 1) {
				//System.out.println("pref one: " + pref.id + " " + pref.prefOne);
				counts[pref.prefOne - 1]++;
			}
			if (rank == 2) {
				counts[pref.prefTwo - 1]++;
			}
		}

		return counts;
	}

	private List<ClinicianPref> buildPreferencesList(ClinicianDAO clincianDAO, ClinicianPreferencesDAO clinicianPreferencesDAO) throws SQLException {
		List<ClinicianBean> allClinicians = clincianDAO.loadClinicians();
		List<ClinicianPref> clinicians = new ArrayList<ClinicianPref>();

		Collections.shuffle(allClinicians);

		if(allClinicians.size() >= 15) {
			List<ClinicianBean> beans = new ArrayList<ClinicianBean> (allClinicians.subList(0, 15));
			for (ClinicianBean bean : beans) {
				int[] ranks = getRanks(clinicianPreferencesDAO.loadClinicianPreferences(bean.getClinicianID()));
				clinicians.add(new ClinicianPref(bean.getClinicianID(), ranks[0], ranks[1]));
			}
		}
		else {
			List<ClinicianBean> beans = new ArrayList<ClinicianBean> (allClinicians.subList(0, (int) (15 - allClinicians.size() * Math.floor(15.0 / allClinicians.size()))));
			for (ClinicianBean bean : beans) {
				int[] ranks = getRanks(clinicianPreferencesDAO.loadClinicianPreferences(bean.getClinicianID()));
				clinicians.add(new ClinicianPref(bean.getClinicianID(), ranks[0], ranks[1]));
			}
			for (int c = 0; c < allClinicians.size(); c++) {
				for (int i = 0; i < Math.floor(15.0 / allClinicians.size()); i++) {
					ClinicianBean cb = allClinicians.get(c);
					ClinicianPreferencesBean bean = clinicianPreferencesDAO.loadClinicianPreferences(cb.getClinicianID());
					int[] ranks = getRanks(bean);
					clinicians.add(new ClinicianPref(cb.getClinicianID(), ranks[0], ranks[1]));
				}
			}
		}
		return clinicians;		
	}

	private int[] getRanks(ClinicianPreferencesBean bean) {
		int first = -1;
		int second = -1;
		// Morning: 1    Noon: 2    Afternoon: 3
		if (bean.getMorningRank() == 1) {
			first = 1;
		}
		if (bean.getMorningRank() == 2) {
			second = 1;
		}
		if (bean.getNoonRank() == 1) {
			first = 2;
		}
		if (bean.getNoonRank() == 2) {
			second = 2;
		}
		if (bean.getAfternoonRank() == 1) {
			first = 3;
		}
		if (bean.getAfternoonRank() == 2) {
			second = 3;
		}
		return new int[] {first, second};
	}
}

final class ClinicianPref {

	int id;
	int prefOne;
	int prefTwo;

	public ClinicianPref(int id, int prefOne, int prefTwo) {
		this.id = id;
		this.prefOne = prefOne;
		this.prefTwo = prefTwo;
	}
}