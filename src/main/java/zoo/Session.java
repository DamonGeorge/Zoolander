package zoo;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

@SpringBootApplication
public class Session {

	//Global connection and scanner
	public static Connection conn;
	public static Scanner scan;
	public static String currentUser;
	public static boolean admin;
	public static Logger log;
	public static Terminal terminal;
	public static int terminalWidth;

	
	public static void main(String[] args) {
		//Get Logger
		getLogger();
		//Get the input scanner
		scan = new Scanner(System.in);

		getDbConnection();
		
		getTerminal();
		
		if(!login()) {
			System.out.println("Exiting...");
			closeDbConnection();
			return;
		}
		
		
		
		System.out.println("\n====== Welcome To The Zoo ======");
		
		//FOR TESTING PURPOSES JUST CALL WORKER METHODS YOUR TESTING HERE
		//ONCE YOUR DONE WITH THE METHOD, REMOVE IT FROM HERE AND WRITE ITS JAVADOC
		//=======================================================================


		
		//=======================================================================
//		log.info("Login of " + currentUser);
//		
//		String[] disabledCommands = {"--spring.shell.command.script.enabled=false", "--spring.shell.command.stacktrace.enabled=false"}; 
//        String[] fullArgs = StringUtils.concatenateStringArrays(args, disabledCommands);
//		
//        SpringApplication app = new SpringApplication(Session.class);
//        app.setBannerMode(Banner.Mode.OFF);
//        app.run(fullArgs);

	}
	
	
	private static void getLogger() {
		Logger logger = Logger.getLogger("Zoolander");
		// suppress the logging output to the console
        Logger rootLogger = Logger.getLogger("");
        Handler[] handlers = rootLogger.getHandlers();
        if (handlers[0] instanceof ConsoleHandler) {
            rootLogger.removeHandler(handlers[0]);
        }
        
        try {
        	FileHandler file = new FileHandler("debug.log");
        	file.setFormatter(new SimpleFormatter());
			logger.addHandler(file);
		} catch (IOException e) {
			System.out.println("Couldn't get logger!");
		} 
        log = logger;
        
       
	}
	
	private static void getTerminal() {
		try {
			terminal = TerminalBuilder.terminal();
			terminalWidth = terminal.getWidth() - 10;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.warning("Error finding terminal dimensions" + e.toString());
			System.out.println("Couldn't initialize terminal");
		}
	}
	
	
	private static boolean login() {
		
		PreparedStatement loginQuery = null;
		ResultSet result = null;
		int i = 0;
				
		try {
			loginQuery = conn.prepareStatement(
					   "select username, admin, active from employee where username = ?");
			for(i = 0; i < 3; i++) {
				System.out.println("Username: ");
				String username = scan.nextLine();
				loginQuery.setString(1, username);
				result = loginQuery.executeQuery();
				//If a country with that code exists, close queries and return
				if(result.next()) {
					if(!result.getBoolean(3)) {
						System.out.println("User " + username + " isn't active! Please Try Again");
					} else {
						currentUser = result.getString(1);
						admin = result.getBoolean(2);
						return true;
					}
				} else {
					System.out.println("User " + username + " doesn't exist! Please Try Again");
				}
			}
			return false;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Something went wrong!");
			return false;
		} finally {
			//close everything
			try {
				result.close();
				loginQuery.close();
			}catch(Exception e) {
				//If closing errors out
				e.printStackTrace();
				System.out.println("Something went wrong!");
				return false;
			}
		}
				   
	}
	
	private static void getDbConnection() {
		try {	//Get database connection from properties file
			Properties prop = new Properties();
			FileInputStream in = new FileInputStream("application.properties");
			prop.load(in);
			in.close();
	        
			// connect to database
			String ip = prop.getProperty("hostIP");
			String port = prop.getProperty("hostPort");
			String db = prop.getProperty("database");
			String user = prop.getProperty("username");
            String pass = prop.getProperty("password");
	        
            String url = "jdbc:mariadb://" + ip + ":" + port + "/" + db;
            conn = DriverManager.getConnection(url, user, pass);		
	
		}catch (Exception e) { //if error, quit 
			System.out.println("Error: Couldn't connect to database!");
			e.printStackTrace();
			return;
		}
	}
	
	private static void closeDbConnection() {
		try {
			conn.close();
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}

}
