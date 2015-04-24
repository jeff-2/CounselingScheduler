package linearprogram;

import gurobi.GRB;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBVar;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
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
import bean.Weekday;

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
	private HashMap<String, GRBVar> ecVars;

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
		this.initializeModel();
		this.generateSchedule();
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
			this.ecVars = new HashMap<String, GRBVar>();

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
								this.ecVars.put(varName, var);
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

			int afternoonECmin = ((weeks.size()*5) / clinicianList.size())-1;
			
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
					model.addConstr(expr, GRB.LESS_EQUAL, session.getDuration()+4, session+"_filled");
				}
			}
			// Each clinician scheduled for EC no more than once per week, for IA no more than once per day
			for(Clinician clinician : this.clinicianList) {
				int ecHours = clinician.getClinicianPreferencesBean().getECHours();
				int iaHours = clinician.getClinicianPreferencesBean().getIAHours();
				GRBLinExpr ecsPerSemester = new GRBLinExpr();
				GRBLinExpr afternoonECs = new GRBLinExpr();
				GRBLinExpr iasPerWeek = new GRBLinExpr();
				for(Week week : this.weeks) {
					ArrayList<Collection<SessionBean>> lateSessions = new ArrayList<Collection<SessionBean>>();
					for(int i=0; i<5; i++) {
						lateSessions.add(new ArrayList<SessionBean>());
					}
					for(SessionBean session : this.sessionsByWeek.get(week)) {
						if(session.getStartTime() >= 16) {
							int day = -1;
							day = session.getDayOfWeek().ordinal();
							lateSessions.get(day).add(session);
						}
					}
					if(clinicianWeekVars.get(clinician).get(week) != null) {
						if(week.type == IAWeektype.A) {
							iasPerWeek = new GRBLinExpr();
						}
						GRBLinExpr ec_expr = new GRBLinExpr();
						HashMap<String, GRBLinExpr> iaDayConstraints = new HashMap<String, GRBLinExpr>();
						for(GRBVar var : clinicianWeekVars.get(clinician).get(week)) {
							String label = var.get(GRB.StringAttr.VarName);
							if(label.endsWith("EC")) {
								ec_expr.addTerm(1.0, var);
								ecsPerSemester.addTerm(1.0, var);
								if(label.contains("_16_")) {
									afternoonECs.addTerm(1.0, var);
								}
								else if(label.contains("_12_")) {
									String iaName = clinician.getClinicianBean().getName();
									iaName += "_"+label.split("_")[2]+"_13_IA_"+week.type;
									GRBVar nextIA = this.iaVars.get(iaName);
									if(nextIA != null) {
										GRBLinExpr pairExpr = new GRBLinExpr();
										pairExpr.addTerm(1.0, var);
										pairExpr.addTerm(1.0, nextIA);
										model.addConstr(pairExpr, GRB.LESS_EQUAL, 1.0, clinician.getClinicianBean().getName()+"_"+week+"_"+label.split("_")[1]+"_12pm1pmscontr");
									}
								}
								else if(label.contains("_8_")) {
									boolean available = true;
									int prevDay = Weekday.getIndexOfDay(label.split("_")[2]);
									if(prevDay >= 0) {
										for(SessionBean lateSesh : lateSessions.get(prevDay)) {
											if(!clinician.canCover(lateSesh)) {
												available = false;
											}
										}	
										if(available) {
											for(SessionBean lateSesh : lateSessions.get(prevDay)) {
												String varName = clinician.getClinicianBean().getName()+"_"+lateSesh.getVariableString();
												GRBVar ecVar = this.ecVars.get(varName);
												if(ecVar != null) {
													GRBLinExpr pairExpr = new GRBLinExpr();
													pairExpr.addTerm(1.0, var);
													pairExpr.addTerm(1.0, ecVar);
													model.addConstr(pairExpr, GRB.LESS_EQUAL, 1.0, clinician.getClinicianBean().getName()+"_"+week+"_"+prevDay+"_"+label.split("_")[1]+"_4pm8amconstr");										
												}
											}
										}								
									}
								}
							}
							else {
								String weekday = label.split("_")[1];
								GRBLinExpr expr = iaDayConstraints.get(weekday);
								if(expr == null) {
									expr = new GRBLinExpr();
								}
								expr.addTerm(1.0, var);
								iasPerWeek.addTerm(1.0, var);
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
						if(week.type == IAWeektype.B) {
							//model.addConstr(iasPerWeek, GRB.GREATER_EQUAL, iaHours-1, clinician+"_"+week+"_ias_isequalto_"+iaHours);
							model.addConstr(iasPerWeek, GRB.LESS_EQUAL, iaHours+1, clinician+"_"+week+"_ias_isequalto_"+iaHours);
						}
					}
				}
				model.addConstr(ecsPerSemester, GRB.LESS_EQUAL, ecHours, clinician+"_ecs_islessthan_"+ecHours);
				model.addConstr(afternoonECs, GRB.GREATER_EQUAL, afternoonECmin, clinician+"_afternoon_ecs_ismorethan_"+afternoonECmin);
			}

			// Set the model's object function			
			model.setObjective(objective, GRB.MAXIMIZE);

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
