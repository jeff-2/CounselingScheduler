CREATE TABLE Calendar (
	startDate date NOT NULL,
	endDate date NOT NULL,
	term int NOT NULL,
	iaMinHours int NOT NULL,
	ecMinHours int NOT NULL,
	id int NOT NULL,
	PRIMARY KEY (id)
);

CREATE TABLE ClinicianPreferences (
	id int NOT NULL,
	morningRank int NOT NULL,
	noonRank int NOT NULL,
	afternoonRank int NOT NULL,
	PRIMARY KEY (id)
);

CREATE TABLE Clinicians (
	id int NOT NULL,
	name varchar(50) NOT NULL,
	PRIMARY KEY(id)
);

CREATE TABLE Commitments (
	id int NOT NULL,
	hour int NOT NULL,
	day varchar(9) NOT NULL,
	description varchar(50) NOT NULL,
);

CREATE TABLE Holiday (
	id int NOT NULL,
	calendarId int NOT NULL,
	name varchar(50) NOT NULL,
	startDate date NOT NULL,
	endDate date NOT NULL
);

CREATE TABLE TimeAway (
	id int NOT NULL,
	startDate date NOT NULL,
	endDate date NOT NULL,
	description varchar(50) NOT NULL
);

CREATE TABLE Sessions (
	id int,
	startTime int NOT NULL,
	duration int NOT NULL,
	weekday varchar(9) NOT NULL,
	sDate date NOT NULL,
	sType int NOT NULL,
	semester int,
	weektype int,
	PRIMARY KEY (id)
);

CREATE TABLE SessionClinicians (
	clinicianID int NOT NULL,
	sessionID int NOT NULL
);