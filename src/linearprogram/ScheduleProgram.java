package linearprogram;

import gurobi.GRB;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBVar;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import bean.CalendarBean;
import bean.Clinician;
import bean.IAWeektype;
import bean.Schedule;
import bean.SessionBean;
import bean.SessionType;

/**
 * Solves an integer linear program to construct an optimal IA and EC schedule.
 * 
 * @author ramusa2
 *
 */
public class ScheduleProgram {

	// Store state from Schedule
	private Schedule schedule;
	private HashMap<Week, List<SessionBean>> sessionsByWeek;
	private List<Clinician> clinicianList;
	private List<SessionBean> sessions;
	private List<Week> weeks;

	// Store ILP model, variables for constraints, and predictions
	private GRBEnv env;
	private GRBModel model;
	private HashMap<Clinician, HashMap<Week, List<GRBVar>>> clinicianWeekVars;
	private HashMap<SessionBean, List<GRBVar>> sessionVars;
	private HashMap<SessionBean, List<Clinician>> assignments;	


	//private String[] clinicians;
	//private String[] weeks;
	//private String[] days;
	//private String[] slots;
	//private String[] sessions;


	//private HashMap<String, HashMap<String, Collection<GRBVar>>> clinicianWeekVars;
	//private HashMap<String, Collection<GRBVar>> sessionVars;

	private ScheduleProgram(Schedule scheduleToAssign) {
		this.schedule = scheduleToAssign;
		this.clinicianList = this.schedule.getClinicians();
		this.sessions = schedule.getSessions();
		this.sessionsByWeek = new HashMap<Week, List<SessionBean>>();
		for(SessionBean session : this.sessions) {
			Week week = Week.getWeek(session.getDate(), this.schedule.getCalendar());
			List<SessionBean> temp = this.sessionsByWeek.get(week);
			if(temp == null) {
				temp = new ArrayList<SessionBean>();
			}
			temp.add(session);
			this.sessionsByWeek.put(week, temp);
		}
		this.weeks = new ArrayList<Week>();
		this.weeks.addAll(this.sessionsByWeek.keySet());
		Collections.sort(this.weeks);
		for(Week week : this.sessionsByWeek.keySet()) {
			Collections.sort(this.sessionsByWeek.get(week));
		}
	}

	private void assignClinicians() {
		System.out.println("Initializing model...");
		this.initializeModel();
		System.out.println("Generating schedule...");
		this.generateSchedule();
		System.out.println("Results:");
		this.updateAssignments();
	}

	private void initializeModel() {
		try {
			// Initialize model
			env = new GRBEnv("ecilp.log");
			model = new GRBModel(env);
			GRBLinExpr objective = new GRBLinExpr();

			// Initialize variable data structures
			this.clinicianWeekVars = new HashMap<Clinician, HashMap<Week, List<GRBVar>>>();
			for(Clinician clinician : this.clinicianList) {
				HashMap<Week,List<GRBVar>> temp = new HashMap<Week,List<GRBVar>>();
				for(Week week : this.weeks) {
					temp.put(week, new ArrayList<GRBVar>());
				}
				this.clinicianWeekVars.put(clinician, temp);
			}
			this.sessionVars = new HashMap<SessionBean, List<GRBVar>>();

			// Initialize variables themselves
			for(Week week : this.weeks) {
				for(SessionBean session : this.sessionsByWeek.get(week)) {
					ArrayList<GRBVar> seshVars = new ArrayList<GRBVar>();
					boolean sessionsIsEC = session.getType() == SessionType.EC;
					String sessionString = session.getVariableString();
					for(Clinician clinician : clinicianList) {
						if(clinician.canCover(session)) {
							String varName = clinician+"_"+sessionString;
							GRBVar var;
							if(sessionsIsEC) {
								double[] prefs = new double[]{0.0, 1.0, 2.0, 5.0}; // TODO: check behavior with these preference weights
								int start = session.getStartTime();
								int time = start==8 ? 1 : (start==12 ? 2 : 3);
								double pref = prefs[clinician.getClinicianPreferencesBean().getRankingFromTime(time)];
								var = model.addVar(0.0, 1.0, pref, GRB.BINARY, varName);
							}
							else {
								var = model.addVar(0.0, 1.0, 1.0, GRB.BINARY, varName);
							}
							seshVars.add(var);
							clinicianWeekVars.get(clinician).get(week).add(var);
						}
					}
					sessionVars.put(session, seshVars);
				}
			}

			// Integrate variables
			model.update();

			// Add constraints: //
			// Every session is filled by exactly one clinician
			for(SessionBean session : this.sessions) {
				GRBLinExpr expr = new GRBLinExpr();
				for(GRBVar var : sessionVars.get(session)) {
					expr.addTerm(1.0, var);
				}
				model.addConstr(expr, GRB.EQUAL, 1.0, session+"_filled");
			}
			// Each clinician scheduled no more than once per week
			for(Clinician clinician : this.clinicianList) {
				for(Week week : clinicianWeekVars.get(clinician).keySet()) {
					GRBLinExpr expr = new GRBLinExpr();
					for(GRBVar var : clinicianWeekVars.get(clinician).get(week)) {
						expr.addTerm(1.0, var);
					}
					model.addConstr(expr, GRB.LESS_EQUAL, 1.0, clinician+"_"+week+"_lessthan1");
				}
			}

			// Set the model's object function			
			model.setObjective(objective);

		} catch (GRBException e) {
			System.out.println("Error code: " + e.getErrorCode() + ". " +
					e.getMessage());
		}
	}

	private void generateSchedule() {
		if(model == null) {
			return;
		}
		try {
			// Optimize model
			model.optimize();
			
			// Extract assignments
			this.assignments = new HashMap<SessionBean, List<Clinician>>();
			for(SessionBean session : this.sessionVars.keySet()) {
				for(GRBVar var : this.sessionVars.get(session)) {
					String label = var.get(GRB.StringAttr.VarName);
					double val = var.get(GRB.DoubleAttr.X);
					if(val > 0.0) {
						Clinician clinician = null; // TODO: find clinician object from label
						session.addClinician(clinician);
					}
				}
			}
		}catch (GRBException e) {
			System.out.println("Error code: " + e.getErrorCode() + ". " +
					e.getMessage());
		}
	}

	private void updateAssignments() {
		if(this.assignments != null) {
			for(SessionBean session : this.assignments.keySet()) {
				List<Clinician> assigned = this.assignments.get(session);
				List<Integer> ids = new ArrayList<Integer>();
				for(Clinician c : assigned) {
					ids.add(c.getClinicianBean().getClinicianID());
				}
				session.setClinicians(ids);
			}
		}
	}

	/**
	 * Given a schedule (which includes sessions and a list of clinicians,
	 * including their availability), assign an optimal set of clinicians to 
	 * each session.
	 */
	public static void assignClinicians(Schedule scheduleToAssign) {
		ScheduleProgram program = new ScheduleProgram(scheduleToAssign);
		program.assignClinicians();
	}

}

