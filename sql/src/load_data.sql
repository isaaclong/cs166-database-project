COPY usr
FROM '/Users/isaaclong/Developer/CS166_Database_Project/data/usr_short.csv'
WITH DELIMITER ',' CSV;

COPY connection_usr
FROM '/Users/isaaclong/Developer/CS166_Database_Project/data/connection_short.csv'
WITH DELIMITER ',' CSV;
