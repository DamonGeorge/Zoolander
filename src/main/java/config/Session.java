package config;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
import java.util.Scanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Session {

	//Global connection and scanner
	public static Connection conn;
	public static Scanner scan;

	
	public static void main(String[] args) {
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

		SpringApplication.run(Session.class, args);

	}

}
