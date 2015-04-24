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
	private HashMap<String, Clinician> clinicianNameLookup;
	private HashMap<String, GRBVar> iaVars;

	private ScheduleProgram(Schedule scheduleToAssign) {
		this.schedule = scheduleToAssign;
		this.clinicianList = this.schedule.getClinicians();
		this.clinicianNameLookup = new HashMap<String, Clinician>();
		for(Clinician clinician : this.clinicianList) {
			this.clinicianNameLookup.put(clinician.getClinicianBean().getName(), clinician);
		}
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
		this.weeks = Week.getSemesterWeeks(this.schedule.getCalendar());
		//this.weeks.addAll(this.sessionsByWeek.keySet());
		Collections.sort(this.weeks);
		for(Week week : this.weeks) {
			if(this.sessionsByWeek.get(week) == null) {
				this.sessionsByWeek.put(week, new ArrayList<SessionBean>());
			}
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
			this.iaVars = new HashMap<String, GRBVar>();

			// Initialize variables themselves
			for(Week week : this.weeks) {
				for(SessionBean session : this.sessionsByWeek.get(week)) {
					ArrayList<GRBVar> seshVars = new ArrayList<GRBVar>();
					boolean sessionsIsEC = session.getType() == SessionType.EC;
					String sessionString = session.getVariableString();
					for(Clinician clinician : clinicianList) {
						if(clinician.canCover(session)) {
							GRBVar var;
							String varName = clinician.getClinicianBean().getName()+"_"+sessionString;
							if(sessionsIsEC) {
								double[] prefs = new double[]{0.0, 1.0, 2.0, 5.0}; // TODO: check behavior with these preference weights
								int start = session.getStartTime();
								int time = start==8 ? 1 : (start==12 ? 2 : 3);
								double pref = prefs[clinician.getClinicianPreferencesBean().getRankingFromTime(time)];
								var = model.addVar(0.0, 1.0, pref, GRB.BINARY, varName);
								objective.addTerm(pref, var);
							}
							else {
								
								var = this.iaVars.get(varName);					
								if(var == null) {									
									var = model.addVar(0.0, 1.0, 1.0, GRB.BINARY, varName);
									objective.addTerm(1.0, var);
									this.iaVars.put(varName, var);
								}
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
			//for(GRBVar var : model.getVars()) {
			//	System.out.println(var.get(GRB.StringAttr.VarName));
			//}

			// Add constraints: //
			// Every EC session is filled by exactly one clinician,
			// every IA session is filled by at least the number we need 
			for(SessionBean session : this.sessions) {
				GRBLinExpr expr = new GRBLinExpr();
				for(GRBVar var : sessionVars.get(session)) {
					expr.addTerm(1.0, var);
				}
				if(session.getType() == SessionType.EC) {
					model.addConstr(expr, GRB.EQUAL, 1.0, session+"_filled");
				}
				else {
					// Note: duration is instead the number of slots.
					model.addConstr(expr, GRB.GREATER_EQUAL, session.getDuration(), session+"_filled");
				}
			}
			// Each clinician scheduled for EC no more than once per week
			for(Clinician clinician : this.clinicianList) {
				for(Week week : clinicianWeekVars.get(clinician).keySet()) {
					GRBLinExpr ec_expr = new GRBLinExpr();
					HashMap<String, GRBLinExpr> iaDayConstraints = new HashMap<String, GRBLinExpr>();
					for(GRBVar var : clinicianWeekVars.get(clinician).get(week)) {
						String label = var.get(GRB.StringAttr.VarName);
						if(label.endsWith("EC")) {
							ec_expr.addTerm(1.0, var);
						}
						else {
							String weekday = label.split("_")[2];
							GRBLinExpr expr = iaDayConstraints.get(weekday);
							if(expr == null) {
								expr = new GRBLinExpr();
							}
							expr.addTerm(1.0, var);
							iaDayConstraints.put(weekday, expr);
						}
					}
					model.addConstr(ec_expr, GRB.LESS_EQUAL, 1.0, clinician+"_"+week+"_eclessthan1");
					for(String weekday : iaDayConstraints.keySet()) {
						GRBLinExpr expr = iaDayConstraints.get(weekday);
						if(expr != null) {
							model.addConstr(expr, GRB.LESS_EQUAL, 1.0, clinician+"_"+week+"_"+weekday+"_islessthan1");
						}
					}
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
						Clinician clinician = this.clinicianNameLookup.get(label.split("_")[0]);
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
