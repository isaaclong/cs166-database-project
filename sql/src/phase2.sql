/*
 * Isaac Long, SID: 861007434, ilong001@ucr.edu
 * Benjamin Davis, SID: 861020676, bdavi015@ucr.edu
 * Group ID: 46
 */

DROP TABLE Users;
DROP TABLE Work_Experience;
DROP TABLE Educational_Details;
DROP TABLE Message;
DROP TABLE Connection;

CREATE TABLE Users ( userid char(10) UNIQUE NOT NULL,
                    password char(10) NOT NULL,
                    email char(50) NOT NULL,
                    name char(50),
                    dateofbirth date,
                    PRIMARY KEY (userid) );

CREATE TABLE Work_Experience ( userid char(10) NOT NULL,
                               company char(50) NOT NULL,
                               role char(50) NOT NULL,
                               location char(50),
                               startdate date NOT NULL,
                               enddate date,
                               PRIMARY KEY (userid, company, role, startdate),
                               FOREIGN KEY (userid) REFERENCES Users ON DELETE CASCADE );

CREATE TABLE Educational_Details ( userid char(10) NOT NULL,
                                   institutionname char(50) NOT NULL,
                                   major char(50) NOT NULL,
                                   degree char (50) NOT NULL,
                                   startdate date,
                                   enddate date,
                                   PRIMARY KEY (userid, major, degree),
                                   FOREIGN KEY (userid) REFERENCES Users ON DELETE CASCADE );

CREATE TABLE Message ( messageid int UNIQUE NOT NULL,
                       senderid char(10) NOT NULL,
                       receiverid char(10) NOT NULL,
                       contents text NOT NULL,
                       sendtime timestamp NOT NULL,
                       deletestatus int,
                       status char(30) NOT NULL,
                       PRIMARY KEY (messageid),
                       FOREIGN KEY (senderid) REFERENCES Users,
                       FOREIGN KEY (receiverid) REFERENCES Users );

CREATE TABLE Connection ( userid char(10) NOT NULL,
                          connectionid char(10) NOT NULL,
                          status char(30) NOT NULL,
                          PRIMARY KEY (userid, connectionid),
                          FOREIGN KEY (userid) REFERENCES Users );



