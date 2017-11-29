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
 * For Testing purposes
 * @author damongeorge
 *
 */
@ShellComponent
public class Testing {
	
//	@ShellMethodAvailability({"listAdmin", "add"})
//    public Availability adminAvailability() {
//        return Session.admin
//            ? Availability.available()
//            : Availability.unavailable("requires admin priveleges");
//    }
	
//	@ShellMethodAvailability({"lists"})
//    public Availability userAvailibility() {
//        return Session.admin
//            ? Availability.unavailable("not an admin feature")
//            : Availability.available();
//    }
//	
//	
//    @ShellMethod("Add two integers together.")
//    public void add(int a, int b) {
//        System.out.println( a + b);
//        
//        
//        String[][] array = {{"hey", "dog"}, {"whats", "up"}};
//        ArrayTableModel model = new ArrayTableModel(array);
//        TableBuilder builder = new TableBuilder(model);
//        builder.addFullBorder(BorderStyle.oldschool);
//        System.out.println(builder.build().render(50));
//    }
    
 
    
}
