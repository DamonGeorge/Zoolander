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
		if(EmployeeRepo.employeeExists(values[0])) {
			System.out.println("Employee " + values[0] + " already exists!");
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
    		if(AnimalRepo.animalExists(values[i])) {
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
		if(!SpeciesRepo.speciesExists(values[i])){
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
	
	public static boolean getEnclosureInfo(String[] values) {
		System.out.print("Enclosure ID....: ");
		values[0] = Session.scan.nextLine();
		if(!values[0].matches("\\d+")) {
			System.out.println("Please input a number for the enclosure id");
			return false;
		}
		if(EnclosureRepo.enclosureExists(values[0])) {
			System.out.println("Enclosure #" + values[0] + " already exists! ");
			return false;
		}
		System.out.print("Name.....: ");
		values[1] = Session.scan.nextLine();
		System.out.print("Environment......: ");
		values[2] = Session.scan.nextLine();
		System.out.print("Open (y/n)......: ");
		values[3] = Session.scan.nextLine().toLowerCase().equals("y") ? "1" : "0";
		return true;
	}
	
	public static boolean getSpeciesInfo(String[] values) {
		System.out.print("Species Name....: ");
		values[0] = Session.scan.nextLine();
		if(SpeciesRepo.speciesExists(values[0])) {
			System.out.println("Species " + values[0] + " already exists! ");
			return false;
		}
		System.out.print("Common Name.....: ");
		values[1] = Session.scan.nextLine();
		System.out.print("Enclosure ID......: ");
		values[2] = Session.scan.nextLine();
		if(!values[2].matches("\\d+")) {
			System.out.println("Please input a number for the enclosure id");
			return false;
		}
		if(!EnclosureRepo.enclosureExists(values[2])) {
			System.out.println("Enclosure #" + values[2] + " doesn't exist! ");
			return false;
		}
		
		System.out.print("Description......: ");
		values[3] = Session.scan.nextLine();
		return true;
	}
	
}



