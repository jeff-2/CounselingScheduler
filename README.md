# CounselingScheduler
This application is specifically tailored for the Counseling Center as a way to automate the scheduling process of clinicians to appointment slots. It comes with two levels of permissions: an administrator level and clinician level.

# Usage
The typical workflow of the scheduling application follows the following process:  
1. An administrator will revise and update the list of clinicians which are available for a particular semester.   
2. The administrator will then enter in the new semester settings establishing start/end dates, holidays and number of hours assigned to each clinician for IA and EC appointments by default.   
3. The clinicians will each submit their preferences to the system which includes time they need off and their preferred time slots to work.  
4. The administrator can then go through and modify and clinician preferences or hours assigned to a particular clinician until they are satisfied.   
5. The administrator can then generate a schedule which creates an IA and EC appointment schedule for the semester. They can freely modify the generated schedule and will be notified if they break any schedule constraints. When they are content with the final schedule they can choose to print or save the schedule to a file.  

# Documentation
Documentation on the project proposal from the Counseling Center is located in the doc folder. The javadoc documentation can be generated from eclipse by selecting Project > Generate Javadoc once the project has been imported into eclipse.  

# Installation
The following are required for the usage of this application:  
-Gurobi  
  This application uses the Gurobi optimizer (v 6.0.3) in the scheduling algorithm of the application. This must be installed and the PATH environment variable must be set to point to its install directory.  
-Java JRE 7  
-Microsoft SQL Server  (earliest supported version is 2005)  
  This application uses a Microsoft SQL Server database for persistent storage of data. The information for this database server must be provided in the database_config.properties file which specifies the database name, user name, password and url to allow the application to connect to the database.  
  


# Team Members:  
-Jeffrey Foster (jmfoste2)  
-Kevin Lim (lim92)  
-Yusheng Hou (yhou8)  
-Nathan Beltran (nbeltr2)  
-Ryan Musa (ramusa2)  
-Denise Li (dtli2)  

