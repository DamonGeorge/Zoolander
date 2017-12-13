package zoo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import org.springframework.shell.table.BorderStyle;
import org.springframework.shell.table.TableBuilder;

/**
 * Contains the database access methods for animal functionality
 * @author damongeorge
 *
 */
public class AnimalRepo {


	/**
	 * List all animals the user handles (or all animals if admin)
	 */
    public static void listAllAnimals() {
    	PreparedStatement query = null;
		ResultSet result = null;
				
		try {
			if (Session.admin) { //If user is admin, show all animals
				query = Session.conn.prepareStatement(
						   "SELECT a.animal_id, a.name, s.species_name, s.common_name, a.birthday, a.last_feeding "
						   + "FROM animal a JOIN species s USING (species_name)");
			
			} else { //Otherwise just show the animals the current user handles
				query = Session.conn.prepareStatement(
						   "SELECT a.animal_id, a.name, s.species_name, s.common_name, a.birthday, a.last_feeding "
						   + "FROM animal a JOIN species s USING (species_name) JOIN employee_training t USING (species_name) "
						   + "WHERE t.username = ?");
				query.setString(1, Session.currentUser);
			}
			
			//execute query and print results
			result = query.executeQuery();
			AsciiTableHelper.printBasicTable(result);
			
		} catch (Exception e) {
			Session.log.warning("SQL Error: " + e.toString());
			System.out.println("Something went wrong!");
		} finally { //close everything
			try {
				result.close();
				query.close();
			}catch(Exception e) { //If closing errors out
				Session.log.warning("DB Closing Error: " + e.toString());
			}
		}	
    }   
    
    /**
     * Search animals by their animal details
     * @param search The search value
     */
    public static void searchAnimalsByAnimal(String search) {
    	PreparedStatement query = null;
		ResultSet result = null;
		search = "%" + search.toLowerCase() + "%"; //% for the LIKE search queries
		
		try {
			
			if(Session.admin) { //If user is admin, show all animals
				query = Session.conn.prepareStatement(
						   "SELECT DISTINCT(animal_id), a.name, s.species_name, s.common_name, a.birthday, a.last_feeding "
						   + "FROM animal a JOIN species s USING (species_name) "
						   + "WHERE LOWER(a.animal_id) LIKE ? "
						   + "OR LOWER(a.name) LIKE ? "
						   + "OR LOWER(s.species_name) LIKE ? "
						   + "OR LOWER(s.common_name) LIKE ? "
						   + "OR LOWER(a.birthday) LIKE ? "
						   + "OR LOWER(a.last_feeding) LIKE ?");
				
				for(int i = 1; i<=6; i++) { //Set all 6 params to the same value
					query.setString(i, search);
				}
				
			} else { //Otherwise just show the animals the current user handles
				query = Session.conn.prepareStatement(
						   "SELECT DISTINCT(animal_id), a.name, s.species_name, s.common_name, a.birthday, a.last_feeding "
						   + "FROM animal a JOIN species s USING (species_name) JOIN employee_training t USING (species_name)"
						   + "WHERE t.username = ? AND "
						   + "(LOWER(a.animal_id) LIKE ? "
						   + "OR LOWER(a.name) LIKE ? "
						   + "OR LOWER(s.species_name) LIKE ? "
						   + "OR LOWER(s.common_name) LIKE ? "
						   + "OR LOWER(a.birthday) LIKE ? "
						   + "OR LOWER(a.last_feeding) LIKE ?) ");
				
				query.setString(1, Session.currentUser);
				for(int i = 2; i<=7; i++) { //Set the last 6 params to the same value
					query.setString(i, search);
				}
			}
			
			//execute query and print results
			result = query.executeQuery();
			AsciiTableHelper.printBasicTable(result);
			
		} catch (Exception e) {
			Session.log.warning("SQL Error: " + e.toString());
			System.out.println("Something went wrong!");
		} finally { //close everything
			try {
				result.close();
				query.close();
			}catch(Exception e) { //If closing errors out
				Session.log.warning("DB Closing Error: " + e.toString());
			}
		}	
    }
    
    
    
    /**
     * Searches for animals by their handlers' information
     * @param search
     */
    public static void searchAnimalByEmployee(String search) {
    	PreparedStatement query = null;
		ResultSet result = null;
		search = "%" + search.toLowerCase() + "%";
		
		try {
			query = Session.conn.prepareStatement(
					   "SELECT DISTINCT(a.animal_id), a.name, s.species_name, s.common_name, a.birthday, a.last_feeding "
					   + "FROM employee u JOIN employee_training t USING (username) JOIN Animal a USING (species_name) JOIN species s USING (species_name)"
					   + "WHERE LOWER(u.username) LIKE ? "
					   + "OR LOWER(u.first_name) LIKE ? "
					   + "OR LOWER(u.last_name) LIKE ? "
					   + "OR LOWER(u.email) like ? "
					   + "OR LOWER(u.birthday) LIKE ? ");
			for(int i = 1; i<=5; i++) {
				query.setString(i, search);
			}
			
			result = query.executeQuery();
			
			AsciiTableHelper.printBasicTable(result);
			
		} catch (Exception e) {
			Session.log.warning("SQL Error: " + e.toString());
			System.out.println("Something went wrong!");
		} finally { //close everything
			try {
				result.close();
				query.close();
			}catch(Exception e) { //If closing errors out
				Session.log.warning("DB Closing Error: " + e.toString());
			}
		}	
    }

	/**
     * Search animals by the enclosures they live in
     * @param search
     */
    public static void searchAnimalsByEnclosure(String search) {
    	PreparedStatement query = null;
		ResultSet result = null;
		search = "%" + search.toLowerCase() + "%";
		
		try {
			if(Session.admin) { //List all results if admin
				query = Session.conn.prepareStatement(
						   "SELECT DISTINCT(a.animal_id), a.name, s.species_name, s.common_name, a.birthday, a.last_feeding  "
						   + "FROM animal a JOIN species s USING (species_name) JOIN enclosure e USING (enclosure_id)"
						   + "WHERE LOWER(e.enclosure_id) LIKE ? "
						   + "OR LOWER(e.name) LIKE ? "
						   + "OR LOWER(e.environment) LIKE ? ");
				
				for(int i = 1; i<=3; i++) { //Set all params to the same value
					query.setString(i, search);
				}
				
			} else { //Otherwise, list only the animals the user handles
				query = Session.conn.prepareStatement(
						   "SELECT DISTINCT(a.animal_id), a.name, s.species_name, s.common_name, a.birthday, a.last_feeding  "
						   + "FROM animal a JOIN species s USING (species_name) JOIN enclosure e USING (enclosure_id) JOIN employee_training t USING (species_name)"
						   + "WHERE t.username = ? AND "
						   + "(LOWER(e.enclosure_id) LIKE ? "
						   + "OR LOWER(e.name) LIKE ? "
						   + "OR LOWER(e.environment) LIKE ? )");
				
				query.setString(1, Session.currentUser);
				for(int i = 2; i<=4; i++) { //Set last 3 params to the same value
					query.setString(i, search);
				}
			}
			
			//Execute and print results
			result = query.executeQuery();
			AsciiTableHelper.printBasicTable(result);
			
		} catch (Exception e) {
			Session.log.warning("SQL Error: " + e.toString());
			System.out.println("Something went wrong!");
		} finally { //close everything
			try {
				result.close();
				query.close();
			}catch(Exception e) { //If closing errors out
				Session.log.warning("DB Closing Error: " + e.toString());
			}
		}	
    }
    
    
    /**
     * Check if the given animal exists
     * @param animalId
     * @return
     */
    public static boolean animalExists(String animalId) {
    	PreparedStatement query = null;
		ResultSet result = null;
		boolean exists = false;
		
		try {
			query = Session.conn.prepareStatement(
					   "SELECT animal_id "
					   + "FROM animal WHERE animal_id = ?");
			query.setString(1, animalId);
			result = query.executeQuery();
			
			if(result.next()) //Check if any results were found
				exists = true;
			
		} catch (Exception e) {
			Session.log.warning("SQL Error: " + e.toString());
			System.out.println("Something went wrong!");
		} finally { //close everything
			try {
				result.close();
				query.close();
			}catch(Exception e) { //If closing errors out
				Session.log.warning("DB Closing Error: " + e.toString());
			}
		}
		return exists;
    }
    
    /**
     * View the details of a given animal and its species.
     * @param animalId 
     */
    public static void viewAnimal(String animalId) {
    	PreparedStatement animalQuery = null, foodQuery = null;
		ResultSet animalResult = null, foodResult = null;
    	
		try {
			animalQuery = Session.conn.prepareStatement(
						"SELECT * "
					   + "FROM animal a "
					   + "WHERE animal_id = ?");
			animalQuery.setString(1, animalId);
			
			foodQuery = Session.conn.prepareStatement(
						"SELECT f.food_id, f.name, f.brand, f.quantity "
						+ "FROM animal a JOIN species_eats se USING (species_name) JOIN food f USING (food_id) "
						+ "WHERE a.animal_id = ?");
			foodQuery.setString(1, animalId);
			
			//Get animals details and species data
			animalResult = animalQuery.executeQuery();
			foodResult = foodQuery.executeQuery();
			
			if(animalResult.next())  { //If animal was found
				int animalColumns[] = {1,2,4,5,6,7,3}; //columns to display in left table
				
				//Build the animal and species tables
				TableBuilder table1 = AsciiTableHelper.getAsciiTable(animalResult, true, animalColumns);
				TableBuilder table2 = AsciiTableHelper.getAsciiTable(foodResult, false);

				//Add borders and print the two tables inside a double table
				table1.addHeaderAndVerticalsBorders(BorderStyle.oldschool);
				table2.addHeaderAndVerticalsBorders(BorderStyle.oldschool);
				AsciiTableHelper.printDoubleTable(table1, "Animal: ", table2, "Foods: ", Session.terminalWidth/2);

			} else 
				System.out.println("No animal found...");
			
		} catch (Exception e) {
			Session.log.warning("SQL Error: " + e.toString());
			System.out.println("Something went wrong! " + e.getMessage());
		} finally { //close everything
			try {
				animalResult.close();
				foodResult.close();
				animalQuery.close();
				foodQuery.close();
			}catch(Exception e) { //If closing errors out
				Session.log.warning("DB Closing Error: " + e.toString());
			}
		}	
    }
    
    

    /**
     * Update an animal with the given list of values:
     * name, description, species_name, and birthday
     * @param oldId The animal's id
     * @param newValues The new name, description, species, birthday, and food quantity
     */
    public static void updateAnimal(String oldId, String[] newValues) {    	
    	PreparedStatement query = null;
    	
    	try {
			query = Session.conn.prepareStatement(
					   "UPDATE animal "
					   + "SET name = ?, description = ?, species_name = ?, birthday = ?, food_quantity = ? "  
					   + "WHERE animal_id = ?");
				
			for(int i = 1; i <= 5; i++) { //Add params to query from the list
				query.setString(i, newValues[i-1]);
			}
			query.setString(6, oldId);
			
			//Execute query and display success
			query.executeUpdate();
			System.out.println("Animal #" + oldId + " successfully updated!");
			
    	} catch (Exception e) {
			Session.log.warning("SQL Error: " + e.toString());
			System.out.println("Something went wrong!");
		} finally { //close everything
			try {
				query.close();
			}catch(Exception e) { //If closing errors out
				Session.log.warning("DB Closing Error: " + e.toString());
			}
		}	
    }
    
    /**
     * Add new animal
     * @param newValues The array of values starting with animal id and ending with last_feeding
     */
    public static void addAnimal(String[] newValues) {   	
    	PreparedStatement query = null;
    	
    	try {
			query = Session.conn.prepareStatement(
					   "INSERT INTO animal VALUES (?, ?, ?, ?, ?, ?, ?)");
				
			for(int i = 1; i <= 6; i++) { //Add all parameters from the list to the query
				query.setString(i, newValues[i-1]);
			}
			//Set last feeding to current time
			query.setTimestamp(7, new Timestamp(System.currentTimeMillis()));

			//Update and display success
			query.executeUpdate();
			System.out.println("Animal successfully created!");
    	
    	} catch (Exception e) {
			Session.log.warning("SQL Error: " + e.toString());
			System.out.println("Something went wrong!");
		} finally {	//close everything
			try {
				query.close();
			}catch(Exception e) {//If closing errors out
				Session.log.warning("DB Closing Error: " + e.toString());
			}
		}	
    }
    
}
