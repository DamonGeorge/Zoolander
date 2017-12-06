package zoo;

import org.springframework.shell.ExitRequest;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.commands.Quit;

/**
 * Override the default spring shell exit command
 * to also close the database connection and the JLine terminal
 * @author damongeorge
 *
 */
@ShellComponent
public class OverridenCommands implements Quit.Command {
    
    @ShellMethod(value = "Exit the shell.", key = {"quit", "exit"})
	public void quit() {
    	try {
    		//Close the database connection and the JLine Terminal
        	Session.conn.close();
        	Session.terminal.close();
        } catch(Exception e) {
        	//If closing errors out, something seriously went wrong. 
        	//Just force exit
			Session.log.warning("Error quiting application: " + e.toString());
			System.out.println("Something went wrong!");
        }
    	
    	throw new ExitRequest();
	}
}
