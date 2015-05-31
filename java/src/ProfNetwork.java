/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class ProfNetwork {

   // reference to physical database connection.
   private Connection _connection = null;

   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));

   /**
    * Creates a new instance of ProfNetwork
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public ProfNetwork (String dbname, String dbport, String user, String passwd) throws SQLException {

      System.out.print("Connecting to database...");
      try{
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println ("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      }catch (Exception e){
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      }//end catch
   }//end ProfNetwork

   /**
    * Method to execute an update SQL statement.  Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate (String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the update instruction
      stmt.executeUpdate (sql);

      // close the instruction
      stmt.close ();
   }//end executeUpdate

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQueryAndPrintResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and output them to standard out.
      boolean outputHeader = true;
      while (rs.next()){
	 if(outputHeader){
	    for(int i = 1; i <= numCol; i++){
		System.out.print(rsmd.getColumnName(i) + "\t");
	    }
	    System.out.println();
	    outputHeader = false;
	 }
         for (int i=1; i<=numCol; ++i)
            System.out.print (rs.getString (i) + "\t");
         System.out.println ();
         ++rowCount;
      }//end while
      stmt.close ();
      return rowCount;
   }//end executeQuery

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the results as
    * a list of records. Each record in turn is a list of attribute values
    *
    * @param query the input query string
    * @return the query result as a list of records
    * @throws java.sql.SQLException when failed to execute the query
    */
   public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and saves the data returned by the query.
      boolean outputHeader = false;
      List<List<String>> result  = new ArrayList<List<String>>();
      while (rs.next()){
          List<String> record = new ArrayList<String>();
         for (int i=1; i<=numCol; ++i)
            record.add(rs.getString(i));
         result.add(record);
      }//end while
      stmt.close ();
      return result;
   }//end executeQueryAndReturnResult

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the number of results
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery (String query) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();

       // issues the query instruction
       ResultSet rs = stmt.executeQuery (query);
       ResultSetMetaData rsmd = rs.getMetaData ();
       int numCol = rsmd.getColumnCount ();
       int rowCount = 0;

       boolean outputHeader = true;
       while (rs.next()){
 	 if(outputHeader){
 	    for(int i = 1; i <= numCol; i++){
 		//System.out.print(rsmd.getColumnName(i) + "\t");
 	    }
 	    //System.out.println();
 	    outputHeader = false;
 	 }
          for (int i=1; i<=numCol; ++i);
             //System.out.print (rs.getString (i) + "\t");
          //System.out.println ();
          ++rowCount;
       }//end while
       stmt.close ();
       return rowCount;
   }

   /**
    * Method to fetch the last value from sequence. This
    * method issues the query to the DBMS and returns the current
    * value of sequence used for autogenerated keys
    *
    * @param sequence name of the DB sequence
    * @return current value of a sequence
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int getCurrSeqVal(String sequence) throws SQLException {
	Statement stmt = this._connection.createStatement ();

	ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
	if (rs.next())
		return rs.getInt(1);
	return -1;
   }

   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup(){
      try{
         if (this._connection != null){
            this._connection.close ();
         }//end if
      }catch (SQLException e){
         // ignored.
      }//end try
   }//end cleanup

   // Moved main method to seperate file, it used to be here

    /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
    */
   public static void main (String[] args) {
      if (args.length != 3) {
         System.err.println (
            "Usage: " +
            "java [-classpath <classpath>] " +
            ProfNetwork.class.getName () +
            " <dbname> <port> <user>");
         return;
      }//end if

      Greeting();
      ProfNetwork esql = null;
      try{
         // use postgres JDBC driver.
         Class.forName ("org.postgresql.Driver").newInstance ();
         // instantiate the ProfNetwork object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new ProfNetwork (dbname, dbport, user, "");

         boolean keepon = true;
         while(keepon) {
            // These are sample SQL statements
            System.out.println("LOGIN");
            System.out.println("-----");
            System.out.println("1. Create user");
            System.out.println("2. Log in");
            System.out.println("9. < EXIT");
            String authorisedUser = null;
            switch (readChoice()) {
               case 1: CreateUser(esql); break;
               case 2: {
                   authorisedUser = LogIn(esql);
                   if(authorisedUser == null)
                       System.out.println("Invalid username or password! Please try logging in again");
                   break;
               }
               case 9: keepon = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }//end switch

            if (authorisedUser != null) {
              // create a User here and refresh it each time Main Menu is printed
              // so we have quick access to friends, requests, messages, etc.
              User userData = new User(esql, authorisedUser);
              boolean usermenu = true;
              while(usermenu) {
                userData.refresh(esql, authorisedUser);
                System.out.println("MAIN MENU");
                System.out.println("---------");
                System.out.println("1. Go to Friend List");
                System.out.println("2. Update Profile");
                System.out.println("3. Write a new message");
                System.out.println("4. Send Friend Request");
                if(userData.pendingRequests.isEmpty())
                    System.out.println("5. You have no new friend requests :(");
                else
                    System.out.println("5. You have new friend requests! Choose 5 to accept or reject them.");
                System.out.println("6. ................");
                System.out.println("9. Log out");
                switch (readChoice()){
                   case 1: friendList(esql, userData); break;
                   case 2: UpdateProfile(esql, authorisedUser); break;
                   case 3: /*NewMessage(esql);*/ break;
                   case 4: SendRequest(esql, userData); break;
                   case 5: manageRequests(esql, userData); break;
                   case 9: usermenu = false; break;
                   default : System.out.println("Unrecognized choice!"); break;
                }
              }
            }
         }//end while
      }catch(Exception e) {
         System.err.println (e.getMessage ());
      }finally{
         // make sure to cleanup the created table and close the connection.
         try{
            if(esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup ();
               System.out.println("Done\n\nBye !");
            }//end if
         }catch (Exception e) {
            // ignored.
         }//end try
      }//end try
   }//end main

   public static void Greeting(){
      System.out.println(
         "\n\n*******************************************************\n" +
         "              User Interface      	               \n" +
         "*******************************************************\n");
   }//end Greeting

   /*
    * Reads the users choice given from the keyboard
    * @int
    **/
   public static int readChoice() {
      int input;
      // returns only if a correct value is given.
      do {
         System.out.print("Please make your choice: ");
         try { // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      }while (true);
      return input;
   }//end readChoice

   /*
    * Creates a new user with provided login, password and phoneNum
    * An empty block and contact list would be generated and associated with a user
    **/
   public static void CreateUser(ProfNetwork esql){
      try{
         System.out.print("\tEnter user login: ");
         String login = in.readLine();

         // make sure login isn't already taken.
         // Should this be done using a pre-query or responding to an insert "error"
         // (I believe that users are unique in the db); do we have to handle this?
         String checkUsersQuery = String.format("SELECT userid FROM usr WHERE userid = '%s'", login);
         int userExists = esql.executeQuery(checkUsersQuery);
         while(userExists > 0 || login.length() > 30) {
            if(userExists > 0 ) System.out.print("\tSorry, that username is taken! Please try another.\n");
            if(login.length() > 30) System.out.print("\tThat login is too long! Shorten it to a max of 30 characters.");
            System.out.print("\tEnter user login: ");
            login = in.readLine();
            checkUsersQuery = String.format("SELECT userid FROM usr WHERE userid = '%s'", login);
         }

         System.out.print("\tEnter user password: ");
         String password = in.readLine();
         System.out.print("\tEnter user email: ");
         String email = in.readLine();

         // TODO: get name and dateofbirth here as well?

	 //Creating empty contact\block lists for a user
     // removed contact list from the query here
	 //String query = String.format("INSERT INTO USR (userid, password, email, contact_list) VALUES ('%s','%s','%s')", login, password, email);
         String query = String.format("INSERT INTO USR (userid, password, email) VALUES ('%s','%s','%s')", login, password, email);
         esql.executeUpdate(query);
         System.out.println ("User successfully created!");
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end

   /*
    * Check log in credentials for an existing user
    * @return User login or null is the user does not exist
    **/
   public static String LogIn(ProfNetwork esql){
      try{
         System.out.print("\tEnter user login: ");
         String login = in.readLine();
         System.out.print("\tEnter user password: ");
         String password = in.readLine();

         String query = String.format("SELECT * FROM USR WHERE userid = '%s' AND password = '%s'", login, password);
         int userNum = esql.executeQuery(query);
	     if (userNum > 0)
		    return login;
         return null;
      }catch(Exception e){
         System.err.println (e.getMessage());
         return null;
      }
   }//end

// Rest of the functions definition go in here

   /*
    * Presents update profile menu to a given user, where they can
    * change their password and other profile information.
    *
    * Consider making change password and other options their own functions to avoid clutter
    **/
   public static void UpdateProfile(ProfNetwork esql, String authorisedUser) {
       try {
           // Menu
           boolean updateProfileMenu = true;
           while(updateProfileMenu) {
               System.out.println("UPDATE PROFILE");
               System.out.println("---------------");
               System.out.println("1. Change Password");
               System.out.println("...................");
               // TODO: add more options here for basic profile information
               switch(readChoice()) {
                   case 1: {
                       // this should be a function, but for now I'm going to leave it in here
                       boolean passwordsMatch = false;
                       String newPassword = "";
                       while(!passwordsMatch) {
                           System.out.print("Please type your new password: ");
                           newPassword = in.readLine();
                           System.out.print("Please re-type your new password: ");
                           if(newPassword.equals(in.readLine())) passwordsMatch = true;
                           else System.out.println("Your passwords didn't match! Please try again.");
                       }
                       String updatePasswordQuery = String.format("UPDATE usr SET password = '%s' WHERE userid = '%s'", newPassword, authorisedUser);
                       esql.executeUpdate(updatePasswordQuery);
                       System.out.println("Password updated.");
                       break;
                   }
                   default: {
                       break;
                   }
               }
               break;
           } // end while menu
       } // end try
       catch (Exception e) {
           System.out.println("Password update failed, invalid input: " + e.getMessage());
       }
   } // end


   /*
    * calculates and returns the number of free connections remaining for a given user.
    * Simply counts the number of connections (friend or pending) they have. returns
    * -1 on error.
    *
    * Connection statuses: 'friend' or 'pending'
    **/
   public static int freeConnectionsRemaining(ProfNetwork esql, String authorisedUser) {
       try {
           String query = String.format("SELECT * FROM connection_usr WHERE userid = '%s' OR connectionid = '%s'", authorisedUser, authorisedUser);
           int numConnections = esql.executeQuery(query);
           //System.out.println("num connections: " + numConnections);
           if(numConnections >= 5) return 0;
           else if(numConnections <= 0) return 5;
           else return (5 - numConnections);
       }
       catch(Exception e) {
           System.out.println("An error occured while calculating your free connections: " + e.getMessage());
       }
       return -1;
   }

   /*
    * Interface for sending a connection request to an existing user. If the current user has
    * free connections remaining, the depth constraint of 3 for connections
    * is not checked. If they have used up their free connections, the depth
    * constraint is checked. If the user is already friends with the sending user,
    * they are told so and it exits the menu
    **/
   public static void SendRequest(ProfNetwork esql, User userData) {
       String authorisedUser = userData.username;
       try {
           System.out.print("Enter the user to send your friend request to or exit to exit: ");
           String user = in.readLine();

           // check for exit
           if(user.equals("exit")) return;

           String query = String.format("SELECT userid FROM usr WHERE userid = '%s'", user);
           // check if user exists
           while(esql.executeQuery(query) == 0) {
               System.out.println("That user doesn't exist! Please enter an existing user or exit to exit: ");
               user = in.readLine();
               if(user.equals("exit")) return;
               query = String.format("SELECT userid FROM usr WHERE userid = '%s'", user);
           }

           // check if you are already friends
           for(int i = 0; i < userData.friends.size(); i++) {
               for(int j = 0; j < userData.friends.get(i).size(); j++) {
                   if(userData.friends.get(i).get(j).trim().equals(user)) {
                       System.out.println("You are already friends with " + user + "!");
                       return;
                   }
               }
           }

           // check if you have already sent them a request
           String checkDuplicateRequest = String.format("SELECT * FROM connection_usr WHERE userid = '%s' AND connectionid = '%s'",authorisedUser, user);
           if(esql.executeQuery(checkDuplicateRequest) > 0) {
               System.out.println("You have already sent a friend request to " + user + "! Please wait for them to respond.");
               return;
           }

           // don't check connection 3 depth constraint
           if(freeConnectionsRemaining(esql, authorisedUser) > 0) {
               query = String.format("INSERT INTO connection_usr (userid, connectionid, status) VALUES ('%s', '%s', '%s')", authorisedUser, user, "pending");
               esql.executeUpdate(query);
               System.out.println("Friend request sent.");
           }

           // if they have used their 5 free friends, check the connection 3 depth constraint
           else {
               System.out.println("You've exceeded your free friends! Let's check the lvl 3 constraint.");

              Set<String> startingSet = new HashSet<String>();
              Set<String> finalSet = new HashSet<String>();

              // initialize starting set and final set with all friends of current user
              List<String> friends1 = getColumn(userData.friends, 0);
              List<String> friends2 = getColumn(userData.friends, 1);

              //System.out.println("INITIAL STARTING SET: ");
              for(String s : friends1) {
                  if(!s.trim().equals(authorisedUser)) {
                      startingSet.add(s);
                      finalSet.add(s);
                  }
              }

              for(String s : friends2) {
                  if(!s.trim().equals(authorisedUser)) {
                      startingSet.add(s);
                      finalSet.add(s);
                  }
              }

              //for(String s : startingSet) System.out.println(s);

              for(int i = 0; i < 3; i++) {
                  // calculate all friends of starting set and add into intermediate set and final set
                  for(String username : startingSet) {
                      //System.out.println("USERNAME: " + username);
                      String getFriendsQuery = String.format("SELECT * FROM connection_usr WHERE (userid = '%s' OR connectionid = '%s') AND status = 'friend'", username, username);
                      List<List<String>> friends = esql.executeQueryAndReturnResult(getFriendsQuery);
                      Set<String> intermediateSet = new HashSet<String>();

                      friends1 = getColumn(friends, 0);
                      friends2 = getColumn(friends, 1);

                      for(String s : friends1) {
                          if(!s.trim().equals(authorisedUser) && !s.trim().equals(username)) {
                              intermediateSet.add(s);
                              finalSet.add(s);
                          }
                      }

                      for(String s : friends2) {
                          if(!s.trim().equals(authorisedUser) && !s.trim().equals(username)) {
                              intermediateSet.add(s);
                              finalSet.add(s);
                          }
                      }

                      // new starting set is set difference of intermediate and previous startingSet
                      //System.out.println("INTERMEDIATE SET ON ITERATION " + i);
                      //for(String s : intermediateSet) System.out.println(s);
                      intermediateSet.removeAll(startingSet);
                      startingSet = intermediateSet;
                      //System.out.println("NEW STARTING SET ON ITERATION " + i);
                      //for(String s : startingSet) System.out.println(s);
                  }
              }

              boolean inLevel = false;
              for(String s : finalSet) {
                  if(s.trim().equals(user)) {
                      System.out.println("Found a match! You can add " + user + "! :)");
                      inLevel = true;
                  }
              }

              if(inLevel) {
                  query = String.format("INSERT INTO connection_usr (userid, connectionid, status) VALUES ('%s', '%s', '%s')", authorisedUser, user, "pending");
                  esql.executeUpdate(query);
                  System.out.println("Friend request sent.");
              }
              else {
                  System.out.println("Sorry, you cannot add " + user + ".");
              }
           }
       }
       catch(Exception e) {
           System.out.println("An error occured when sending your connection request: " + e.getMessage());
       }
   }

   public static List<String> getColumn(List<List<String>> list, int colNum) {
       List<String> columnVals = new ArrayList<String>();
       for(int i = 0; i < list.size(); i++) {
           columnVals.add(list.get(i).get(colNum).trim());
       }
       return columnVals;
   }

   /*
    * Allows users to manage their friend requests, if they have any.
    * Users type in y <username> to accept, or n <username> to reject. rejected
    * requests are simply deleted; accepted requests change connection status
    * from 'pending' to 'friend' and that user will show up in each user's friends list.
    * Users will remain in Manage Requests after each approval or denial of a friend,
    * and they can type exit to return to main menu. They will automatically exit
    * if there are no remaining pending requests.
    *
    * TODO: change status to request and accept and add a reject status? (for compatability with bulk excel data)
    **/
    public static void manageRequests(ProfNetwork esql, User userData) throws IOException, SQLException {
        System.out.println("MANAGE REQUESTS");
        System.out.println("---------------");

        // get all users pending requests and print them
        List<String> pendingUsernames = new ArrayList<String>();
        for(int i = 0; i < userData.pendingRequests.size(); i++)
        {
            System.out.println(userData.pendingRequests.get(i).get(0) + " wants to be your friend!");
            pendingUsernames.add(userData.pendingRequests.get(i).get(0).trim());
        }


        String input = "";
        if(!(pendingUsernames.size() == 0)) {
            System.out.println("---------------");
            System.out.println("Type y <username> to accept friend request and n <username> to deny friend request or exit to exit.");
            System.out.print("-->");
            input = in.readLine();
        }

        String[] splitInput = input.split(" ");

        // check user input
        boolean inputGood = true;
        while(true)
        {
            // check for exit
            if(input.equals("exit") || pendingUsernames.size() == 0) {
                if(pendingUsernames.size() == 0) System.out.println("You have no pending requests!");
                System.out.println("exiting...");
                return;
            }

            // check size
            else if(splitInput.length < 2 || splitInput.length > 2) {
                inputGood = false;
                System.out.println("The number of parameters you gave was different than expected.");
                System.out.println("Make sure you put a space between y/n and <username>.");
            }

            // check first element
            else if(!splitInput[0].equals("y") && !splitInput[0].equals("n")) {
                inputGood = false;
                System.out.println("Your first parameter was different than expected.");
                System.out.println("It should be either 'y' (yes) or n (no).");
            }

            // check second element
            else if(!pendingUsernames.contains(splitInput[1])) {
                System.out.println(splitInput[1]+"aaa");
                System.out.println(pendingUsernames.get(0)+"aaa");
                if(splitInput[1].equals(pendingUsernames.get(0))) System.out.println("YEEE");
                inputGood = false;
                System.out.println("That username was not found.");
                System.out.println("Make sure what you typed matches what's in the list exactly.");
            }

            // either accept or deny the friend request
            if(inputGood) {
                if(splitInput[0].equals("y")) {
                    String query = String.format("UPDATE connection_usr SET status = 'friend' WHERE userid = '%s' AND connectionid = '%s'", splitInput[1], userData.username);
                    esql.executeUpdate(query);
                    pendingUsernames.remove(splitInput[1]); // line is purely for UI so when list is printed they are not shown
                    System.out.println("You and " + splitInput[1] + " are now friends!");
                }
                else if(splitInput[0].equals("n")) {
                    String query = String.format("DELETE FROM connection_usr WHERE userid = '%s' AND connectionid = '%s'", splitInput[1], userData.username);
                    pendingUsernames.remove(splitInput[1]);
                    System.out.println("You rejected " + splitInput[1] + "'s friend request. Harsh!'");
                }

                System.out.println("---------------");
                for(String s : pendingUsernames)
                {
                    System.out.println(s + " wants to be your friend!");
                }
            }

            if(!(pendingUsernames.size() == 0)) {
                System.out.println("---------------");
                System.out.println("Type y <username> to accept friend request and n <username> to deny friend request or exit to exit.");
                System.out.print("-->");
                input = in.readLine();
            }

            splitInput = input.split(" ");
        }
    }

    // simply prints all friends right now, will add on to this later, added this
    // basic functionality for testing
    public static void friendList(ProfNetwork esql, User userData) {
        System.out.println("FRIENDS");
        System.out.println("-------");
        for(int i = 0; i < userData.friends.size(); i++) {
            if(userData.friends.get(i).get(0).trim().equals(userData.username))
                System.out.println(userData.friends.get(i).get(1));
            else
                System.out.println(userData.friends.get(i).get(0));
        }
        System.out.println("-------");
    }

}//end ProfNetwork
