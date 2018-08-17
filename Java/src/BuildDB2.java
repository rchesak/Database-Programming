import java.sql.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.List;
import java.text.SimpleDateFormat;

public class BuildDB2 {

	/***********************************************************************
	*  call all methods in main()...
	***********************************************************************/
	public static void main( String[] args ) {
		Connection conn = null;
		Statement stmt = null;
		String[] tableNames = new String[] {"movies", "moviesgenres", "agegroups", "occupationgroups", "users", "ratings"};

		File moviesFile = new File("movies.dat");
		File ageGroupsFile = new File("age_groups.dat");
		File occGroupsFile = new File("occupation_groups.dat");
		File usersFile = new File("users.dat");
		File ratingsFile = new File("ratings.dat");
		
		String[] tableDefs = new String[] {
			"CREATE TABLE movies" +
			"(movieid INTEGER," + 
			"movietitle VARCHAR2(100)," +  
			"yearmade INTEGER," + 
			"PRIMARY KEY ( movieid ) " +
			" )",
			"CREATE TABLE moviesgenres" +
			"(movieid INTEGER," + 
			"genre VARCHAR2(100)," +  
			"PRIMARY KEY ( movieid, genre )," +
			"FOREIGN KEY ( movieid )" +
			"	REFERENCES movies ( movieid ) " +
			" )",
			"CREATE TABLE agegroups" +
			"(ageid INTEGER," + 
			"age VARCHAR2(10)," +  
			"PRIMARY KEY ( ageid ) " +
			" )",
			"CREATE TABLE occupationgroups" +
			"(occupationid INTEGER," + 
			"occupation VARCHAR2(35)," +  
			"PRIMARY KEY ( occupationid ) " +
			" )",
			"CREATE TABLE users" +
			"(userid INTEGER," + 
			"gender VARCHAR2(1)," +
			"ageid INTEGER," + 
			"occupationid INTEGER," + 
			"zipcode VARCHAR2(15)," + 
			"PRIMARY KEY ( userid )," + 
			"FOREIGN KEY ( ageid )" +
			"	REFERENCES agegroups ( ageid )," +
			"FOREIGN KEY ( occupationid )" +
			"	REFERENCES occupationgroups ( occupationid )" +					
			" )",
			"CREATE TABLE ratings" +
			"(userid INTEGER," + 
			"movieid INTEGER," + 
			"rating INTEGER," + 
			"truedate DATE," + 
			"PRIMARY KEY ( userid, movieid )," + 
			"FOREIGN KEY ( userid )" +
			"	REFERENCES users ( userid )," +
			"FOREIGN KEY ( movieid )" +
			"	REFERENCES movies ( movieid )" +					
			" )"
		};		

		try{
			grabDriver();
			conn = establishConnection(conn);
			//create an object by which we will pass SQL stmts to the database
			stmt = conn.createStatement();	
			dropTables(stmt, tableNames);
			createTables(stmt, tableDefs, tableNames);
			parseMovies(conn, moviesFile);
			parseAgeGroups(conn, ageGroupsFile);
			parseOccGroups(conn, occGroupsFile);
			parseUsers(conn, usersFile);
			parseRatings(conn, ratingsFile);
			displayCounts(stmt, tableNames);
			sqlQuery(stmt, tableNames);
		}//end try

		catch (SQLException se) {
			System.out.println(se);
		 }		

		/* Finally ensures that connections get closed in the 
		 * event of a crash, which saves system resources. */
		finally{ 
			try {
				stmt.close();
				conn.close();
			}//end try
			catch (SQLException e) {
				e.printStackTrace();
			}//end catch
			
		}//end finally

	} // end main


	// function for padding strings on the right
	public static String rPad(String str, int n) {
	     return String.format("%1$-" + n + "s", str);  
	}	

	/***********************************************************************
	*  determine if the JDBC driver exists and load it...
	***********************************************************************/
	public static void grabDriver() {
		System.out.print( "\nLoading JDBC driver...\n\n" );
		try {
		 Class.forName("oracle.jdbc.OracleDriver");
		 }
		catch(ClassNotFoundException e) {
		 System.out.println(e);
		 System.exit(1);
		 } 
	}//end method

	/***********************************************************************
	*  establish a connection to the database... 
	***********************************************************************/
	public static Connection establishConnection(Connection conn) {
		try {
			System.out.print( "Connecting to DEF database...\n\n" );
			//String url = dataSource + dbName;

			conn = DriverManager.getConnection("jdbc:oracle:thin:@140.192.30.237:1521:def", "rchesak", "demo");

			/*conn = dbms.equals("localAccess") ? DriverManager.getConnection(url)
			: DriverManager.getConnection(url, userName, password );*/
			System.out.println( "Connected to database DEF...\n\n" );

		 }
		catch (SQLException se) {
			System.out.println(se);
			System.exit(1);
		 }
		
		return conn;
		
	}//end method	

	/***********************************************************************
	*  drop old tables if they exist
	***********************************************************************/
	public static void dropTables(Statement stmt, String[] tableNames) {
		System.out.print( "Dropping old tables...\n\n" );
		for( int i = 0; i < tableNames.length; i++ ) {
			try {
				String dropString = new String("DROP TABLE " + tableNames[i] + " CASCADE CONSTRAINTS");
				stmt.executeUpdate(dropString);
				System.out.print( "\t" + tableNames[i] + " table dropped...\n\n" );
			}//end try

			catch (SQLException se) {
				System.out.print( "\t" + tableNames[i] + ": " + se );
			 }//end catch	

		}//end for

	}//end method	


	/***********************************************************************
	*  create the new tables...
	***********************************************************************/
	public static void createTables(Statement stmt, String[] tableDefs, String[] tableNames) {	
		String createString;
		try { 
			System.out.print( "Building new tables...\n\n" );
			for( int i = 0; i < tableDefs.length; i++ ) {
				createString = new String(tableDefs[i]);
				stmt.executeUpdate(createString);
				System.out.print( "\t" + "SUCCESS table" + tableNames[i] + "built\n\n" );	
			}//end for
		}//end try

		catch (SQLException se) {
			System.out.println(se);
		 }//end catch		

	}//end method        


	/********************
	parse movies.dat
	********************/
	public static void parseMovies(Connection conn, File moviesFile) {
		Scanner myscanner;
		Pattern regex1;
		Pattern regex2;
		PreparedStatement updateMovies;
		PreparedStatement updateMoviesGenres;
		Matcher match1;
		Matcher match2;
		Integer yearMade;
		String newTitle;
		String[] genres;
		String[] linei;

		try {
			System.out.print( "Parsing movies...\n\n" );
			myscanner = new Scanner(moviesFile);
			//prepare regex to grab the year made from the title
			regex1 = Pattern.compile("(\\d{4})"); 
			//prepare regex to delete '(year)' from the title
			regex2 = Pattern.compile("\\(\\d{4}\\)"); 
			updateMovies = conn.prepareStatement( "INSERT INTO movies VALUES ( ?, ?, ?)" );
			updateMoviesGenres = conn.prepareStatement( "INSERT INTO moviesgenres VALUES ( ?, ? )" );		        	 

			while (myscanner.hasNextLine()) {
				linei = myscanner.nextLine().split("::"); //split the line on delimiter
				
				// grab the year made from the title
				match1 = regex1.matcher( linei[1] );
				if(match1.find()) {
					yearMade = Integer.parseInt(match1.group(0));
				} else {
					System.out.print( "MovieID " + linei[0] + " had an issue with the YearMade.\n");
					yearMade = -1; //this will quietly signify that the process has gone wrong
				}	 			   //without disrupting the program	
				
				// delete the '(year)' from the title
				match2 = regex2.matcher( linei[1] );
				if(match2.find()) {
					newTitle = match2.replaceAll("");
				} else {
					System.out.print( "MovieID " + linei[0] + " had an issue with the Title.\n");
					newTitle = null; //this will quietly signify that the process has gone wrong
				}	 				 //without disrupting the program	
						 
				//populate movies table
				conn.setAutoCommit(false);
				updateMovies.setInt( 1, Integer.parseInt(linei[0].toString()) );
				updateMovies.setString( 2, newTitle );
				updateMovies.setInt( 3, yearMade );
				updateMovies.executeUpdate();
				conn.commit();	

				//populate moviesgenres table
				genres = linei[2].split("\\|"); // split the genres on the delimiter
				conn.setAutoCommit(false);
				// load each movie title and genre pair to the moviesgenres table
				for( int i = 0; i < genres.length; i++ ) {
					updateMoviesGenres.setInt( 1, Integer.parseInt(linei[0].toString()) );
					updateMoviesGenres.setString( 2, genres[i] );
					updateMoviesGenres.executeUpdate();
				}

				conn.commit();				         
			} // end while loop

			myscanner.close();
			updateMovies.close(); // DON'T FORGET THIS STEP 
			updateMoviesGenres.close(); // DON'T FORGET THIS STEP
			System.out.print( "\tSUCCESS movies and moviesgenres populated\n\n" );
		} //end try 

		catch (FileNotFoundException e) {
			e.printStackTrace();
		}//end catch

		catch (SQLException se) {
			System.out.println(se);
		 }//end catch			

	}//end method	 
		 
	/********************
	parse age_groups.dat
	********************/
	public static void parseAgeGroups(Connection conn, File ageGroupsFile) {
		Scanner myscanner;
		PreparedStatement updateAgeGroups = null;
		String[] linei;

		try {
			System.out.print( "Parsing age_groups...\n\n" );
			myscanner = new Scanner(ageGroupsFile);

			updateAgeGroups = conn.prepareStatement( "INSERT INTO agegroups VALUES ( ?, ? )" );		        	 

			while (myscanner.hasNextLine()) {
				linei = myscanner.nextLine().split("::"); // split line on delimiter 	

				//populate movies table
				conn.setAutoCommit(false);
				updateAgeGroups.setInt( 1, Integer.parseInt(linei[0].toString()) );
				updateAgeGroups.setString( 2, linei[1] );
				updateAgeGroups.executeUpdate();
				conn.commit();			        		 
			} //end while loop

			myscanner.close();
			updateAgeGroups.close(); // DON'T FORGET THIS STEP
			System.out.print( "\tSUCCESS agregroups populated\n\n" );
		}//end try

		catch (FileNotFoundException e) {
			e.printStackTrace();
		}//end catch

		catch (SQLException se) {
			System.out.println(se);
		 }//end catch			

	}//end method


	/********************
	parse occupation_groups.dat
	********************/
	public static void parseOccGroups(Connection conn, File occGroupsFile) {
		Scanner myscanner;
		PreparedStatement updateOccupationGroups;
		String[] linei;

		try {
			System.out.print( "Parsing occupation_groups...\n\n" );
			myscanner = new Scanner(occGroupsFile);

			updateOccupationGroups = conn.prepareStatement( "INSERT INTO occupationgroups VALUES ( ?, ? )" );		        	 

			while (myscanner.hasNextLine()) {
				linei = myscanner.nextLine().split("::"); // split line on delimiter	

				//populate movies table
				conn.setAutoCommit(false);
				updateOccupationGroups.setInt( 1, Integer.parseInt(linei[0].toString()) );
				updateOccupationGroups.setString( 2, linei[1] );
				updateOccupationGroups.executeUpdate();
				conn.commit();			        		 
			} //end while loop

			myscanner.close();
			updateOccupationGroups.close(); // DON'T FORGET THIS STEP
			System.out.print( "\tSUCCESS occupationgroups populated\n\n" );	
		}//end try

		catch (FileNotFoundException e) {
			e.printStackTrace();
		}//end catch

		catch (SQLException se) {
			System.out.println(se);
		 }//end catch			

	}//end method			

	/********************
	parse users.dat
	********************/
	public static void parseUsers(Connection conn, File usersFile) {
		Scanner myscanner;
		PreparedStatement updateUsers;
		String[] linei;

		try {
			System.out.print( "Parsing users...\n\n" );
			myscanner = new Scanner(usersFile);

			updateUsers = conn.prepareStatement( "INSERT INTO users VALUES ( ?, ?, ?, ?, ? )" );		        	 

			while (myscanner.hasNextLine()) {
				linei = myscanner.nextLine().split("::"); // split line on delimiter	
			 
				//populate movies table
				conn.setAutoCommit(false);
				updateUsers.setInt( 1, Integer.parseInt(linei[0].toString()) );
				updateUsers.setString( 2, linei[1] );
				updateUsers.setInt( 3, Integer.parseInt(linei[2].toString()) );
				updateUsers.setInt( 4, Integer.parseInt(linei[3].toString()) );
				updateUsers.setString( 5, linei[4] );
				updateUsers.executeUpdate();
				conn.commit();			        		 
			} //end while loop

			myscanner.close();
			updateUsers.close(); // DON'T FORGET THIS STEP
			System.out.print( "\tSUCCESS users populated\n\n" );	
		}//end try	

		catch (FileNotFoundException e) {
			e.printStackTrace();
		}//end catch	

		catch (SQLException se) {
			System.out.println(se);
		 }//end catch			

	}//end method

	/********************
	parse ratings.dat
	********************/
	public static void parseRatings(Connection conn, File ratingsFile) {
		Scanner myscanner;
		PreparedStatement updateRatings;
		String[] linei;
		Long sec;
		Long ms;
		Date trueDate;
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
		String dateStr;	

		try {
			System.out.print( "Parsing ratings...\n\n" );
			myscanner = new Scanner(ratingsFile);

			updateRatings = conn.prepareStatement( "INSERT INTO ratings VALUES ( ?, ?, ?, ? )" );		        	 

			while (myscanner.hasNextLine()) {
				linei = myscanner.nextLine().split("::"); // split line on delimiter	

				//convert milliseconds since epoch to a date
				sec = Long.parseLong(linei[3]);
				ms = (sec * 1000);
				trueDate = new Date(ms);
				dateStr = sdf.format(trueDate);

				//populate movies table
				conn.setAutoCommit(false);
				updateRatings.setInt( 1, Integer.parseInt(linei[0].toString()) );
				updateRatings.setInt( 2, Integer.parseInt(linei[1].toString()) );
				updateRatings.setInt( 3, Integer.parseInt(linei[2].toString()) );
				updateRatings.setString( 4, dateStr );
				updateRatings.executeUpdate();
				conn.commit();			        		 
			} //end while loop

			myscanner.close();
			updateRatings.close(); // DON'T FORGET THIS STEP
			System.out.print( "\tSUCCESS ratings populated\n\n" );				        	 
		}//end try	

		catch (FileNotFoundException e) {
			e.printStackTrace();
		}//end catch	

		catch (SQLException se) {
			System.out.println(se);
		 }//end catch				

	}//end method

	/***********************************************************************
	*  display all counts in the database...
	***********************************************************************/
	public static void displayCounts(Statement stmt, String[] tableNames) {
		ResultSet rset = null;
		try{
			System.out.println( "Final table record counts:\n" );
			for( int i = 0; i < tableNames.length; i++ ) {
				rset = stmt.executeQuery( "SELECT COUNT(*) FROM " + tableNames[i] );
				while( rset.next() ){
					System.out.println( "\t" + rPad(tableNames[i] + ": ", 20) + rset.getString("COUNT(*)") + "\n" );
				}//end while

				rset.close();
			} // end for loop	

		}//end try

		catch (SQLException se) {
			System.out.println(se);
		 }//end catch		

	}//end method	

	/***********************************************************************
	*  run an interesting query...
	***********************************************************************/
	//// For each job type, what are the top 5 highest-rated movie genres? 
	public static void sqlQuery(Statement stmt, String[] tableNames) {
		String occCursor = null;
		ResultSet rOccCursor = null;
		String curOcc = null;
		List<String> occupations = new ArrayList<String>();
		String query1;
		ResultSet rset1 = null;
		Integer rank;
		try {
			System.out.println("\n\nTop 5 genres per occupation group:");

			occCursor = new String(
				"SELECT occupation FROM occupationgroups" 
				);
			
			//load all occupations into an ArrayList<>
			rOccCursor = stmt.executeQuery( occCursor );
			while( rOccCursor.next() ) {
				curOcc = new String(rOccCursor.getString("occupation"));
				occupations.add(curOcc);
			}//end rOccCursor while loop
			rOccCursor.close();

			// For each occupation, print out the top 5 highest-rated movie genres
			for( int i = 0; i < occupations.size(); i++ ) {
				System.out.println("\n"+ occupations.get(i) + ":");
				System.out.println(rPad("Rank",7) + rPad("Genre", 15) + "Avg. Rating");
				System.out.println("----------------------------------");
				
				query1 = new String(
					"SELECT " +
					"ROUND(AVG(join2.rating),2) Avg_Rating, " +
					"moviesgenres.genre " + 
					"FROM " + 
					    "moviesgenres INNER JOIN (" +
					      "SELECT * " + 
					      "FROM ratings INNER JOIN ( " +
					        "SELECT * " +
					        "FROM users INNER JOIN occupationgroups " + 
					          "ON users.occupationid = occupationgroups.occupationid " + 
					          ") join1 " +
					      "ON ratings.userid = join1.userid " +
					      ") join2 " +
					      "ON moviesgenres.movieid = join2.movieid " + 
					"WHERE occupation = '" + occupations.get(i) +   // notice we are inserting the occupation
					"' GROUP BY genre " +
					"ORDER BY Avg_Rating DESC " 		        		 
					);		         	
			 rset1 = stmt.executeQuery( query1 );
			 
			 rank = 1;
			 int j = 0;
			 while( rset1.next() && j < 5 ) { 
				 System.out.println(rPad(Integer.toString(rank++),7) + 
						 rPad(rset1.getString("genre"), 15) + 
						 rPad(rset1.getString("Avg_Rating"), 15) );
				 j++;
			 }//end while loop
			rset1.close();
			}//end occupations for loop

		} // end try
		catch(SQLException se) {
			System.out.println( "SQL ERROR: " + se );
		}//end catch

	}//end method

}  // end class	 
