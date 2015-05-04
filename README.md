# CounselingScheduler:
This application is specifically tailored for the Counseling Center as a way to automate the scheduling process of clinicians to appointment slots. It comes with two levels of permissions: an administrator level and clinician level.

# Usage:
The typical workflow of the scheduling application follows the following process:  
1. An administrator will revise and update the list of clinicians which are available for a particular semester.   
2. The administrator will then enter in the new semester settings establishing start/end dates, holidays and number of hours assigned to each clinician for IA and EC appointments by default.   
3. The clinicians will each submit their preferences to the system which includes time they need off and their preferred time slots to work.  
4. The administrator can then go through and modify and clinician preferences or hours assigned to a particular clinician until they are satisfied.   
5. The administrator can then generate a schedule which creates an IA and EC appointment schedule for the semester. They can freely modify the generated schedule and will be notified if they break any schedule constraints. When they are content with the final schedule they can choose to print or save the schedule to a file.  

# Documentation:
Documentation on the project proposal from the Counseling Center is located in the doc folder. The javadoc documentation can be generated from eclipse by selecting Project > Generate Javadoc once the project has been imported into eclipse.  

# Installation:
The following resources are required for the usage of this application:  
-Gurobi  
  This application uses the Gurobi optimizer (v 6.0.3) in the scheduling algorithm of the application. To download Gurobi, visit http://user.gurobi.com/download/gurobi-optimizer and download the files for version 6.0.3. In addition to downloading the README file, make sure to choose the right version of the installer for your machine (e.g. Windows 64). 
  To install Gurobi, double-click the .msi file you downloaded or follow the instructions in the README file. follow the instructions in the Quick Start Guide, available at http://www.gurobi.com/documentation/ . An academic license may be obtained with an @illinois.edu email address. Before running Gurobi or the scheduling application, the PATH environment variable must be set to point to the Gurobi install directory (see the Quick Start Guide).  
-Java JRE 7 
  To install Java, contact your system administrator or visit https://www.java.com/en/download/help/index_installing.xml .   
-Microsoft SQL Server  (earliest supported version is 2005)  
  This application uses a Microsoft SQL Server database for persistent storage of data. The information for this database server must be provided in the db_connection_config.properties file which specifies the database name, user name, password and url to allow the application to connect to the database.  

# Running the Application:
The release/release 1.0 directory contains all the files required for running this application other than the pre-requisite applications listed in the Installation section. The db_connection_config.properties must be filled out as specified in the Installation section as well. Then running the application can be done as follows:  
- Administrator:
  To generate a new semester schedule, run the application by double-clicking on the "admin.exe" executable file. After inputting the semester information, contact the clinicians and wait for them to input their preferences (you can close the application after setting up the semester information). Once preferences are entered, open the application again and follow steps in the documentation to generate the final schedule. 
- Clinicians:
  To input your preferences for the upcoming semester, run the application by double-clicking on the "clincian.exe" executable file.

# Team Members:  
-Jeffrey Foster (jmfoste2)  
-Kevin Lim (lim92)  
-Yusheng Hou (yhou8)  
-Nathan Beltran (nbeltr2)  
-Ryan Musa (ramusa2)  
-Denise Li (dtli2)  

