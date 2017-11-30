package zoo;

import java.sql.SQLException;

import org.springframework.shell.ExitRequest;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.commands.Quit;

@ShellComponent
public class OverridenCommands implements Quit.Command {
    
    @ShellMethod(value = "Exit the shell.", key = {"quit", "exit"})
	public void quit() {
    	try {
        	Session.conn.close();
        } catch(SQLException e) {
        	//If closing errors out
			Session.log.info("Error closing DB Connection: " + e.toString());
			System.out.println("Something went wrong!");
        }
    	
    	throw new ExitRequest();
	}
}
