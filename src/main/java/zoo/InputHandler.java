package zoo;


/**
 * Contains the methods that handle user input
 * @author damongeorge
 *
 */
public class InputHandler {

	public static boolean getEmployeeInfo(String[] values) {
		System.out.print("Username....: ");
		values[0] = Session.scan.nextLine();
		if(Employees.employeeExists(values[0])) {
			System.out.println("Username already exists! ");
			return false;
		}
		System.out.print("First Name.....: ");
		values[1] = Session.scan.nextLine();
		System.out.print("Last Name......: ");
		values[2] = Session.scan.nextLine();
		System.out.print("Birthday....: ");
		values[3] = Session.scan.nextLine();
		if(!values[3].matches("\\d\\d\\d\\d-\\d\\d-\\d\\d")) {
			System.out.println("Please enter dates in the form yyyy-mm-dd");
		}
		
		System.out.print("Email.....: ");
		values[4] = Session.scan.nextLine();
		System.out.print("Salary......: ");
		values[5] = Session.scan.nextLine();
		if(!values[5].matches("\\d+.\\d\\d")){
			System.out.println("Please enter a decimal number for the salary!");
			return false;
		}
		System.out.print("Active (y/n)......: ");
		values[6] = Session.scan.nextLine().toLowerCase().equals("y") ? "1" : "0";
		System.out.print("Admin (y/n)......: ");
		values[7] = Session.scan.nextLine().toLowerCase().equals("y") ? "1" : "0";
		return true;
	}
	
	
	public static boolean getAnimalInfo(String[] values, boolean newAnimal) {
		int i = 0;
		if(newAnimal) {
			System.out.print("Animal ID....: ");
    		values[i] = Session.scan.nextLine();
    		if(!values[i].matches("\\d+")) {
    			System.out.println("Please input a number for the animal id");
    			return false;
    		}
    		if(Animals.animalExists(values[i])) {
    			System.out.println("Animal #" + values[i] + " already exists! ");
    			return false;
    		}
    		i++;
		}
	
		System.out.print("Name......: ");
		values[i++] = Session.scan.nextLine();
		System.out.print("Description..: ");
		values[i++] = Session.scan.nextLine();
		System.out.print("Species....: ");
		values[i] = Session.scan.nextLine();
		if(!Species.speciesExists(values[i])){
			System.out.println("Species " + values[i] + " doesn't exist!");
			return false;
		}
		i++;
		System.out.print("Birthday.....: ");
		values[i] = Session.scan.nextLine();
		if(!values[i].matches("\\d\\d\\d\\d-\\d\\d-\\d\\d")) {
			System.out.println("Please enter dates in the form yyyy-mm-dd");
			return false;
		}
		i++;
		System.out.print("Food Quantity......: ");
		values[i] = Session.scan.nextLine();
		if(!values[i].matches("\\d+")){
			System.out.println("Please enter a number for the quantity!");
			return false;
		}
		
		return true;
	}
}



