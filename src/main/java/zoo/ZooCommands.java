package zoo;

import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.table.ArrayTableModel;
import org.springframework.shell.table.BorderStyle;
import org.springframework.shell.table.Table;
import org.springframework.shell.table.TableBuilder;

/**
 * All the zoo commands should go in this file
 * These functions will call the actual worker function in other classes
 * @author damongeorge
 *
 */
@ShellComponent
public class ZooCommands {

    
    @ShellMethod(value="list all entries for a given entity", key={"list", "ls"} )
    public void listAdmin(boolean employees, boolean animals, boolean enclosures, boolean food) {
       
    	if (employees) {
    		Employees.listAllEmployees();
    	} else if (animals) {
    		
    	} else if (enclosures) {
    		
    	} else if (food) {
    		
    	} else {
    		
    	}
    }
    
//    @ShellMethod(value="list all entries for a given entity", key={"list", "ls"} )
//    public void listReg(boolean animals, boolean enclosures, boolean food) {
//       
//    	if (animals) {
//    		
//    	} else if (enclosures) {
//    		
//    	} else if (food) {
//    		
//    	} else {
//    		
//    	}
//    }
    
    
}
