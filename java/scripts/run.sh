#!/bin/bash

#run the java program
#Use your database name, port number and login
java -cp $DIR/../classes:$DIR/../lib/pg73jdbc3.jar ProfNetwork $DB_NAME $PGPORT $USER
