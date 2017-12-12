package zoo;


/**
 * Contains the methods that handle user input
 * @author damongeorge
 * @author anthonyniehuser
 *
 */
public class InputHandler {

	public static boolean getEmployeeInfo(String[] values) {
		values[0] = Session.reader.readLine("Username....: ");
		if(EmployeeRepo.employeeExists(values[0])) {
			System.out.println("Employee " + values[0] + " already exists!");
			return false;
		}
		values[1] = Session.reader.readLine("First Name.....: ");
		values[2] = Session.reader.readLine("Last Name......: ");
		values[3] = Session.reader.readLine("Birthday....: ");
		if(!values[3].matches("\\d\\d\\d\\d-\\d\\d-\\d\\d")) {
			System.out.println("Please enter dates in the form yyyy-mm-dd");
			return false;
		}
		
		values[4] = Session.reader.readLine("Email.....: ");
		values[5] = Session.reader.readLine("Salary......: ");
		if(!values[5].matches("\\d+.\\d\\d")){
			System.out.println("Please enter a decimal number for the salary!");
			return false;
		}
		values[6] = Session.reader.readLine("Active (y/n)......: ").toLowerCase().equals("y") ? "1" : "0";
		values[7] = Session.reader.readLine("Admin (y/n)......: ").toLowerCase().equals("y") ? "1" : "0";
		return true;
	}
	
	
	public static boolean getAnimalInfo(String[] values, boolean newAnimal) {
		int i = 0;
		if(newAnimal) {
    		values[i] = Session.reader.readLine("Animal ID....: ");
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
	
		values[i++] = Session.reader.readLine("Name......: ");
		values[i++] = Session.reader.readLine("Description..: ");
		values[i] = Session.reader.readLine("Species....: ");
		if(!SpeciesRepo.speciesExists(values[i])){
			System.out.println("Species " + values[i] + " doesn't exist!");
			return false;
		}
		i++;
		values[i] = Session.reader.readLine("Birthday.....: ");
		if(!values[i].matches("\\d\\d\\d\\d-\\d\\d-\\d\\d")) {
			System.out.println("Please enter dates in the form yyyy-mm-dd");
			return false;
		}
		i++;
		values[i] = Session.reader.readLine("Food Quantity......: ");
		if(!values[i].matches("\\d+")){
			System.out.println("Please enter a number for the quantity!");
			return false;
		}
		
		return true;
	}
	
	public static boolean getEnclosureInfo(String[] values) {
		values[0] = Session.reader.readLine("Enclosure ID....: ");
		if(!values[0].matches("\\d+")) {
			System.out.println("Please input a number for the enclosure id");
			return false;
		}
		if(EnclosureRepo.enclosureExists(values[0])) {
			System.out.println("Enclosure #" + values[0] + " already exists! ");
			return false;
		}
		values[1] = Session.reader.readLine("Name.....: ");
		values[2] = Session.reader.readLine("Environment......: ");
		values[3] = Session.reader.readLine("Open (y/n)......: ").toLowerCase().equals("y") ? "1" : "0";
		return true;
	}
	
	public static boolean getSpeciesInfo(String[] values) {
		values[0] = Session.reader.readLine("Species Name....: ");
		if(SpeciesRepo.speciesExists(values[0])) {
			System.out.println("Species " + values[0] + " already exists! ");
			return false;
		}
		values[1] = Session.reader.readLine("Common Name.....: ");
		values[2] = Session.reader.readLine("Enclosure ID......: ");
		if(!values[2].matches("\\d+")) {
			System.out.println("Please input a number for the enclosure id");
			return false;
		}
		if(!EnclosureRepo.enclosureExists(values[2])) {
			System.out.println("Enclosure #" + values[2] + " doesn't exist! ");
			return false;
		}
		
		values[3] = Session.reader.readLine("Description......: ");
		return true;
	}
	
	public static boolean getFoodInfo(String[] values) {
		values[0] = Session.reader.readLine("Food_ID.........: ");
		if(!values[0].matches("\\d+") || FoodRepo.foodExists(values[0])){
			System.out.println("Food of id: " + values[0] + "already exists!");
			return false;
		}
		values[1] = Session.reader.readLine("Food Name.......: ");
		values[2] = Session.reader.readLine("Food Brand......: ");
		values[3] = Session.reader.readLine("Food Cost.......: ");
		if(!values[3].matches("\\d+(\\.\\d*)?")) {
			System.out.println("Please input a number for Food Cost.");
			return false;
		}
		values[4] = "0";
		values[5] = Session.reader.readLine("Nutritional Index: ");
		if(!values[5].matches("\\d+(\\.\\d*)?")) {
			System.out.println("Please input a number for Food Cost.");
			return false;
		}
		return true;
		
	}

	public static boolean getFoodQuanity(String[] value) {
		value[0] = Session.reader.readLine("Quantity: ");
		if(!value[0].matches("\\d+(\\.\\d*)?")){
			System.out.println("Quntity must be a number!");
			return false;
		}
		return true;
	}
	
	public static boolean getAnimalID(String[] value) {
		value[0] = Session.reader.readLine("Animal's ID: ");
		if(!value[0].matches("\\d+(\\.\\d*)?")){
			System.out.println("Animal's ID must be a number!");
			return false;
		}
		return true;
	}
}



