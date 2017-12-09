package zoo;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.StringUtils;

/**
 * Main Class for initializing and running the CLI shell
 * @author damongeorge
 *
 */
@SpringBootApplication
public class Session {

	//Global variables for use throughout the app
	//Initialized in main()
	public static Connection conn;
	public static Scanner scan;
	public static String currentUser;
	public static boolean admin;
	public static Logger log;
	public static Terminal terminal;
	public static int terminalWidth;

	
	public static void main(String[] args) {
		getLogger();
		
		scan = new Scanner(System.in); 	//Get the input scanner

		if(!getDbConnection()) { //If database connection failed, exit
			System.out.println("Exiting...");
			return;
		}
		
		if(!login()) { //If login credentials were bad, exit
			System.out.println("Exiting...");
			closeDbConnection();
			return;
		}
		
		getTerminal();
		System.out.println("\n====== Welcome To The Zoo ======");
		
		
		/**
		 * TODOS
		 * TODO: employee activate/deactivate ???
		 * TODO: employee add/remove trainings ???
		 * TODO: food - feed and purchase
		 * TODO: more stats functionality
		 * TODO: view species, enclosure, food ??? - prob unnecessary
		 * TODO: special employee update function - remove??
		 */
		
		//FOR TESTING PURPOSES JUST CALL WORKER METHODS YOUR TESTING HERE
		//=======================================================================
		executeSQLScript("DatabaseCreates.sql");
		executeSQLScript("DatabaseInserts.sql");
		
		//=======================================================================
		//Disable some default commands from the spring shell
		String[] disabledCommands = {"--spring.shell.command.script.enabled=false", "--spring.shell.command.stacktrace.enabled=false"}; 
        String[] fullArgs = StringUtils.concatenateStringArrays(args, disabledCommands);
		
        //Start the app with the disabled commands and disable the banner
        SpringApplication app = new SpringApplication(Session.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(fullArgs);

	}
	
	/**
	 * Initialize the debugging logger.
	 * This logger will be used by the app to log errors
	 * to the 'debug.log' file. 
	 */
	private static void getLogger() {
		log = Logger.getLogger("Zoolander"); //new logger
		
		//suppress the logging output to the console
		//by removing the default console handler
        Logger rootLogger = Logger.getLogger("");
        Handler[] handlers = rootLogger.getHandlers();
        if (handlers[0] instanceof ConsoleHandler) {
            rootLogger.removeHandler(handlers[0]);
        }
        
        try { //Open the log file and add the simple text formatter
        	FileHandler file = new FileHandler("debug.log");
        	file.setFormatter(new SimpleFormatter());
			log.addHandler(file);
		} catch (IOException e) {
			log.severe("Log file error: " + e.toString());
			System.out.println("Couldn't get logger!");
		}         

	}
	
	/**
	 * Get the JLine Terminal instance and get the initial terminal width
	 */
	private static void getTerminal() {
		try {
			terminal = TerminalBuilder.terminal();
			terminalWidth = terminal.getWidth() - 10;
		} catch (IOException e) {
			log.severe("Error initializing JLine Terminal: " + e.toString());
			System.out.println("Couldn't initialize terminal");
		}
	}
	
	/**
	 * Login the user
	 * @return False if the user provides bad credentials more than three times
	 */
	private static boolean login() {
		PreparedStatement loginQuery = null;
		ResultSet result = null;
		boolean loggedIn = false;
		int i = 0;
				
		try {
			loginQuery = conn.prepareStatement(
					   "select username, admin, active from employee where username = ?");
			
			for(i = 0; i < 3; i++) { //User gets three tries to login
				//Get the user's username, and execute the query with it
				System.out.println("Username: ");
				String username = scan.nextLine();
				loginQuery.setString(1, username);
				result = loginQuery.executeQuery();
				
				
				if(result.next()) { 
					if(!result.getBoolean(3)) { //If user exists, but isn't active, reject the login
						System.out.println("User " + username + " isn't active! Please Try Again");
					} else { //Otherwise login the user
						currentUser = result.getString(1);
						admin = result.getBoolean(2);
						loggedIn =  true;
						break;
					}
				} else {
					System.out.println("User " + username + " doesn't exist! Please Try Again");
				}
			}
					
		} catch (SQLException e) {
			log.warning("SQL Error: " + e.toString());
			System.out.println("Something went wrong!");
		} finally { //close everything
			try {
				result.close();
				loginQuery.close();
			}catch(Exception e) { //If closing errors out
				log.warning("DB Closing Error: " + e.toString());
				System.out.println("Something went wrong!");
			}
		}
		
		return loggedIn;		   
	}
	
	/**
	 * Get Database Connection
	 * @return False if connection failed
	 */
	private static boolean getDbConnection() {
		try {	
			//Get database connection from properties file
			Properties prop = new Properties();
			FileInputStream in = new FileInputStream("application.properties");
			prop.load(in);
			in.close();
	        
			// connect to database with given properties from file
			String ip = prop.getProperty("hostIP");
			String port = prop.getProperty("hostPort");
			String db = prop.getProperty("database");
			String user = prop.getProperty("username");
            String pass = prop.getProperty("password");
	        
            //Construct url and get connection
            String url = "jdbc:mariadb://" + ip + ":" + port + "/" + db;
            conn = DriverManager.getConnection(url, user, pass);		
            
            //True since no error occurred
            return true; 
            
		}catch (Exception e) { //if error, quit 
			log.severe("Couldn't connect to database: " + e.toString());
			System.out.println("Error: Couldn't connect to database!");
			return false; //False if database connection wasn't made
		}
		
	}
	
	/**
	 * Close the database connection
	 */
	private static void closeDbConnection() {
		try {
			conn.close();
		} catch(SQLException e) {
			log.severe("Couldn't close database connection: " + e.toString());
			System.out.println("Error: Couldn't close database connection!");
		}
	}
	
	
	/**
	 * Executes an .sql script to the database
	 * Automatically paths to ~/Zoolander/sql/*.sql directory
	 * @param filename 
	 * 
	 */
	private static void executeSQLScript(String filename)
	{
		Statement stmt = null;
		Reader reader = null;
		FileInputStream in = null;
		
		//TODO:: remove hardcode of sql folderpath
		String projectPath = System.getProperty("user.dir");
		String sqlFolderPath = "/sql/";
		
		try{
			//System.out.println(projectPath + sqlFolderPath + filename);
			in = new FileInputStream(projectPath + sqlFolderPath + filename);
			reader = new InputStreamReader(in);
			
			StringBuilder builder = new StringBuilder();
			int character = reader.read();
		
			while(character>=0)
			{
				
				//System.out.print(Character.toChars(character));
				if(character==';'){
					//System.out.println(builder.toString());
					stmt = Session.conn.createStatement();
					stmt.executeUpdate(builder.toString());
					stmt.close();
					builder.setLength(0);
				}
				else {
					//System.out.println(builder.toString());
					builder.append(Character.toChars(character));
				}
				character = reader.read();
			}
			System.out.println("\n---SQL Script=="+filename+"==Successufully Executed---\n");
		}
		catch(Exception e) {
			Session.log.warning("SQL Error: " + e.toString());
			System.out.println("Something went wrong!");
		} finally { //close everything
			try {
				if(stmt!=null) 	stmt.close();
				if(in!=null)	in.close();
				if(reader!=null) reader.close();
			}catch(Exception e) { //If closing errors out
				Session.log.warning("DB Closing Error: " + e.toString());
			}
		}
	}
}
