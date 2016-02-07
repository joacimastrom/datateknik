use db110;
SET foreign_key_checks = 0;

DROP TABLE IF EXISTS Users;
DROP TABLE IF EXISTS Theaters;
DROP TABLE IF EXISTS Movies;
DROP TABLE IF EXISTS Reservations;
DROP TABLE IF EXISTS Performances;
SET foreign_key_checks = 1;

create table Users (
id int PRIMARY KEY NOT NULL AUTO_INCREMENT,
username char(20) NOT NULL UNIQUE,
name char(20) NOT NULL,
adress varchar(256),
phoneNumber char(20) NOT NULL

);

create table Movies (
id int PRIMARY KEY NOT NULL AUTO_INCREMENT, 
name char(20) NOT NULL UNIQUE
);

create table Theaters(
id int PRIMARY KEY NOT NULL AUTO_INCREMENT,
name char(20) NOT NULL UNIQUE,
seats int NOT NULL
);

create table Performances (
id int PRIMARY KEY NOT NULL AUTO_INCREMENT,
movieId int NOT NULL,
theaterId int NOT NULL,
theDate DATE NOT NULL,
FOREIGN KEY(movieId)
  REFERENCES Movies(id),
FOREIGN KEY(theaterId)
  REFERENCES Theaters(id),
CONSTRAINT oncePerDay UNIQUE (movieId, theDate)
);

create table Reservations (
reservationNumber int PRIMARY KEY NOT NULL AUTO_INCREMENT,
performanceId int NOT NULL,
userId int NOT NULL,
FOREIGN KEY(performanceId)
  REFERENCES Performances(id),
FOREIGN KEY(userId)
  REFERENCES Users(id),
CONSTRAINT oncePerPerformance UNIQUE (performanceId,userId)
);

SET foreign_key_checks = 1;

insert into Users(username, name, adress, phoneNumber) values('Attoff', 'Viktor Attoff', 'Delphi', '123');
insert into Users(username, name, adress, phoneNumber) values('Jocke', 'Joacim Åström', 'Kämnärs 1', '456');
insert into Users(username, name, adress, phoneNumber) values('Kukas', 'Kukas Brune', 'Kämnärs 2', '789');

insert into Movies(name) values('Team America');
insert into Movies(name) values('Anchorman');
insert into Movies(name) values('Zoolander');

insert into Theaters(name, seats) values('Mansion', 8);
insert into Theaters(name, seats) values('SF', 120);
insert into Theaters(name, seats) values('Stol', 1);

insert into Performances(movieId, theaterId, theDate) values(1, 1, '2016-02-24');
insert into Performances(movieId, theaterId, theDate) values(2, 1, '2016-02-24');
insert into Performances(movieId, theaterId, theDate) values(3, 3, '2016-02-24');

-- Lista alla filmer som visas
select Movies.name, Performances.theDate as date
from Movies, Performances
where Movies.id = Performances.movieId;

-- Visa när Team America visas
select Performances.theDate as date, Movies.name
from Performances, Movies
where Performances.movieId = Movies.id and Movies.name = 'Team America';

-- hämta info på performance 1
select Movies.name as movieName, Theaters.name as theaterName, Performances.theDate as date
from Movies, Theaters, Performances
where Performances.movieId = Movies.id and Performances.theaterId = Theaters.id and Performances.id = 1;

insert into Reservations(performanceId, userId) values(1, 2);

-- samma film 2 ggr
insert into Movies(name) values('Team America');

-- samma film 2 ggr på en dag
insert into Performances(movieId, theaterId, theDate) values(1, 1, '2016-02-24');

-- film på biograf som inte finns
insert into Performances(movieId, theaterId, theDate) values(1, 5, '2016-02-27');

-- Användare som inte finns
insert into Reservations(performanceId, userId) values(1, 10);


-- Om stolen blir full medan en annan användare skapar sin reservation blir det race condition error, reservation utan ledig sits



