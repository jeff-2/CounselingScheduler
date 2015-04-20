package linearprogram;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import gurobi.*;

public class ECProgram {


	private GRBEnv env;
	private GRBModel model;

	private String[] clinicians;
	private String[] weeks;
	private String[] days;
	private String[] slots;
	private String[] sessions;


	private HashMap<String, HashMap<String, Collection<GRBVar>>> clinicianWeekVars;
	private HashMap<String, Collection<GRBVar>> sessionVars;


	/**
	 * Initializes the clinician and session lists
	 */
	public ECProgram(int numClinicians, int numWeeks) {		
		// Initialize clinican names
		clinicians = new String[numClinicians];
		for(int i=0; i<numClinicians; i++) {
			clinicians[i] = "Clinician"+((char)(i+65));
		}

		// Initialize list of weeks
		weeks = new String[numWeeks];
		for(int i=0; i<numWeeks; i++) {
			weeks[i] = "week"+i;
		}

		// Initialize list of days (constant)
		days = new String[]{"Mon", "Tue", "Wed", "Thu", "Fri"};
		//days = new String[]{"Mon"};

		// Initialize list of slots (constant)
		slots = new String[]{"8am", "12pm", "4pm"};

		// Initialize session list
		sessions = new String[numWeeks*days.length*slots.length];
		int s=0;
		for(int w=0; w<numWeeks; w++) {
			for(int d=0; d<days.length; d++) {
				for(int sl=0; sl < slots.length; sl++) {
					sessions[s] = weeks[w]+"_"+days[d]+"_"+slots[sl];
					s++;
				}
			}
		}
	}

	public void generateECSchedule() {
		System.out.println("Initializing model...");
		initializeModel();
		System.out.println("Generating schedule...");
		generateSchedule();
		System.out.println("Results:");
		printAssignment();
	}

	private void initializeModel() {
		try {
			// Initialize model
			env = new GRBEnv("ecilp.log");
			model = new GRBModel(env);
			
			// Initialize variables
			sessionVars = new HashMap<String, Collection<GRBVar>>();
			clinicianWeekVars = new HashMap<String, HashMap<String, Collection<GRBVar>>>();
			for(String clinician : clinicians) {
				HashMap<String, Collection<GRBVar>> map = new HashMap<String, Collection<GRBVar>>();
				for(String week : weeks) {
					map.put(week, new ArrayList<GRBVar>());
				}
				clinicianWeekVars.put(clinician, map);
			}
			for(String session : sessions) {
				ArrayList<GRBVar> vars = new ArrayList<GRBVar>();
				String week = session.split("_")[0];
				for(String clinician : clinicians) {
					String varName = clinician+"_"+session;
					GRBVar var = model.addVar(0.0, 1.0, 1.0, GRB.BINARY, varName);
					double rand = Math.random();
					double bar = 0.4;
					if(rand >= bar) {
						vars.add(var);
						clinicianWeekVars.get(clinician).get(week).add(var);
					}
				}
				sessionVars.put(session, vars);
			}
			// Integrate variables
			model.update();

			// Add constraints:
			// Every session is filled by exactly one clinician
			for(String session : sessions) {
				GRBLinExpr expr = new GRBLinExpr();
				for(GRBVar var : sessionVars.get(session)) {
					expr.addTerm(1.0, var);
				}
				model.addConstr(expr, GRB.EQUAL, 1.0, session+"_filled");
			}
			// Each clinician scheduled no more than once per week
			for(String clinician : clinicians) {
				for(String week : clinicianWeekVars.get(clinician).keySet()) {
					GRBLinExpr expr = new GRBLinExpr();
					for(GRBVar var : clinicianWeekVars.get(clinician).get(week)) {
						expr.addTerm(1.0, var);
					}
					model.addConstr(expr, GRB.LESS_EQUAL, 1.0, clinician+"_"+week+"_lessthan1");
				}
			}
			
			// Set objective:
			GRBLinExpr expr = new GRBLinExpr();
			for(String session : sessions) {
				for(GRBVar var : sessionVars.get(session)) {
					expr.addTerm(1.0, var);
				}
			}
			model.setObjective(expr);
		} catch (GRBException e) {
			System.out.println("Error code: " + e.getErrorCode() + ". " +
					e.getMessage());
		}
	}

	private void generateSchedule() {
		if(model == null) {
			return;
		}
		// Optimize model
		try {
			model.optimize();
		}catch (GRBException e) {
			System.out.println("Error code: " + e.getErrorCode() + ". " +
					e.getMessage());
		}
	}

	private void printAssignment() {
		try {
			// Print results
			for(String session : sessions) {
				Collection<GRBVar> vars = sessionVars.get(session);
				int assigned = 0;
				String clinicianAssigned = "";
				for(GRBVar var : vars) {
					String label = var.get(GRB.StringAttr.VarName);
					double val = var.get(GRB.DoubleAttr.X);
					if(val > 0.0) {
						assigned++;
						clinicianAssigned = label.split("_")[0];
					}
				}
				if(assigned != 1) {
					System.out.println("ERROR: session "+session+" assigned "+assigned+" clinicians");
				}
				else {
					System.out.println(session+"  \t"+clinicianAssigned);
				}
			}
			// Dispose of model and environment
			model.dispose();
			env.dispose();
		} catch (GRBException e) {
			System.out.println("Error code: " + e.getErrorCode() + ". " +
					e.getMessage());
		}
	}
	
	public static void main(String[] args) {
		ECProgram program = new ECProgram(25, 20);
		program.generateECSchedule();
	}
}
