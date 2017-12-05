package zoo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import org.springframework.shell.table.BorderStyle;
import org.springframework.shell.table.TableBuilder;

/**
 * Contains the worker methods for animal functionality
 * @author damongeorge
 *
 */
public class Animals {


	/**
	 * List all animals the user handles (or all animals if admin)
	 */
    public static void listAllAnimals() {
    	PreparedStatement query = null;
		ResultSet result = null;
				
		try {
			if (Session.admin) {
				query = Session.conn.prepareStatement(
						   "SELECT a.animal_id, a.name, s.species_name, s.common_name, a.birthday, a.last_feeding "
						   + "FROM animal a JOIN species s USING (species_name)");
			} else {
				query = Session.conn.prepareStatement(
						   "SELECT a.animal_id, a.name, s.species_name, s.common_name, a.birthday, a.last_feeding "
						   + "FROM animal a JOIN species s USING (species_name) JOIN employee_training t USING (species_name) "
						   + "WHERE t.username = ?");
				query.setString(1, Session.currentUser);
			}
			
			result = query.executeQuery();
			TableBuilding.printBasicTable(result);
				
		} catch (Exception e) {
			Session.log.info("SQL Error: " + e.toString());
			System.out.println("Something went wrong!");
		} finally {
			//close everything
			try {
				result.close();
				query.close();
			}catch(Exception e) {
				//If closing errors out
				Session.log.info("DB Closing Error: " + e.toString());
			}
		}	
    }   
    
    /**
     * Search animals by the animal information
     * @param search
     */
    public static void searchAnimalsByAnimal(String search) {
    	PreparedStatement query = null;
		ResultSet result = null;
		search = "%" + search.toLowerCase() + "%";
		
		try {
			
			if(Session.admin) {
				query = Session.conn.prepareStatement(
						   "SELECT animal_id, a.name, s.species_name, s.common_name, a.birthday, a.last_feeding "
						   + "FROM animal a JOIN species s USING (species_name) "
						   + "WHERE LOWER(a.animal_id) LIKE ? "
						   + "OR LOWER(a.name) LIKE ? "
						   + "OR LOWER(s.species_name) LIKE ? "
						   + "OR LOWER(s.common_name) LIKE ? "
						   + "OR LOWER(a.birthday) LIKE ? "
						   + "OR LOWER(a.last_feeding) LIKE ?");
				for(int i = 1; i<=6; i++) {
					query.setString(i, search);
				}
			} else {
				query = Session.conn.prepareStatement(
						   "SELECT animal_id, a.name, s.species_name, s.common_name, a.birthday, a.last_feeding "
						   + "FROM animal a JOIN species s USING (species_name) JOIN employee_training t USING (species_name)"
						   + "WHERE t.username = ? AND "
						   + "(LOWER(a.animal_id) LIKE ? "
						   + "OR LOWER(a.name) LIKE ? "
						   + "OR LOWER(s.species_name) LIKE ? "
						   + "OR LOWER(s.common_name) LIKE ? "
						   + "OR LOWER(a.birthday) LIKE ? "
						   + "OR LOWER(a.last_feeding) LIKE ?) ");
				query.setString(1, Session.currentUser);
				for(int i = 1; i<=6; i++) {
					query.setString(i+1, search);
				}
			}
			
			
			result = query.executeQuery();
			TableBuilding.printBasicTable(result);
		} catch (Exception e) {
			Session.log.info("SQL Error: " + e.toString());
			System.out.println("Something went wrong!");
		} finally {
			//close everything
			try {
				result.close();
				query.close();
			}catch(Exception e) {
				//If closing errors out
				Session.log.info("DB Closing Error: " + e.toString());
			}
		}	
    }
    
    
    
    /**
     * TODO: Can regular users run this query??? no?
     * @param search
     */
    public static void searchAnimalByEmployee(String search) {
    	PreparedStatement query = null;
		ResultSet result = null;
		search = "%" + search.toLowerCase() + "%";
		
		try {
			query = Session.conn.prepareStatement(
					   "SELECT a.animal_id, a.name, s.species_name, s.common_name, a.birthday, a.last_feeding "
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
			
			TableBuilding.printBasicTable(result);
			
		} catch (Exception e) {
			Session.log.info("SQL Error: " + e.toString());
			System.out.println("Something went wrong!");
		} finally {
			//close everything
			try {
				result.close();
				query.close();
			}catch(Exception e) {
				//If closing errors out
				Session.log.info("DB Closing Error: " + e.toString());
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
			if(Session.admin) {
				query = Session.conn.prepareStatement(
						   "SELECT a.animal_id, a.name, s.species_name, s.common_name, a.birthday, a.last_feeding  "
						   + "FROM animal a JOIN species s USING (species_name) JOIN enclosure e USING (enclosure_id)"
						   + "WHERE LOWER(e.enclosure_id) LIKE ? "
						   + "OR LOWER(e.name) LIKE ? "
						   + "OR LOWER(e.environment) LIKE ? ");
				for(int i = 1; i<=3; i++) {
					query.setString(i, search);
				}
			} else {
				query = Session.conn.prepareStatement(
						   "SELECT a.animal_id, a.name, s.species_name, s.common_name, a.birthday, a.last_feeding  "
						   + "FROM animal a JOIN species s USING (species_name) JOIN enclosure e USING (enclosure_id) JOIN employee_training t USING (species_name)"
						   + "WHERE t.username = ? AND "
						   + "(LOWER(e.enclosure_id) LIKE ? "
						   + "OR LOWER(e.name) LIKE ? "
						   + "OR LOWER(e.environment) LIKE ? )");
				query.setString(1, Session.currentUser);
				for(int i = 1; i<=3; i++) {
					query.setString(i+1, search);
				}
			}
			
			result = query.executeQuery();
			TableBuilding.printBasicTable(result);
			
		} catch (Exception e) {
			Session.log.info("SQL Error: " + e.toString());
			System.out.println("Something went wrong!");
		} finally {
			//close everything
			try {
				result.close();
				query.close();
			}catch(Exception e) {
				//If closing errors out
				Session.log.info("DB Closing Error: " + e.toString());
			}
		}	
    }
    
    
    /**
     * Check if the given animal exists
     * @param username
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
			if(result.next()) 
				exists = true;
			
		} catch (Exception e) {
			Session.log.info("SQL Error: " + e.toString());
			System.out.println("Something went wrong!");
		} finally {
			//close everything
			try {
				result.close();
				query.close();
			}catch(Exception e) {
				//If closing errors out
				Session.log.info("DB Closing Error: " + e.toString());
			}
		}
		return exists;
    }
    
    /**
     * View the details of a given animal and its species.
     * @param username
     */
    public static void viewAnimal(String animalId) {
    	PreparedStatement animalQuery = null;
		ResultSet animalResult = null;
    	
		try {
			animalQuery = Session.conn.prepareStatement(
						"SELECT * "
					   + "FROM animal a JOIN species s USING (species_name) "
					   + "WHERE animal_id = ?");
			animalQuery.setString(1, animalId);
			animalResult = animalQuery.executeQuery();
			
			if(animalResult.next())  {
				int animalColumns[] = {2,3,4,5,6,7};
				int speciesColumns[] = {1,8,9,10};
				TableBuilder table1 = TableBuilding.getAsciiTable(animalResult, true, animalColumns);
				TableBuilder table2 = TableBuilding.getAsciiTable(animalResult, true, speciesColumns);

				table1.addHeaderAndVerticalsBorders(BorderStyle.oldschool);
				table2.addHeaderAndVerticalsBorders(BorderStyle.oldschool);
				TableBuilding.printDoubleTable(table1, "Animal: ", table2, "Species: ", Session.terminalWidth/2);

				
			} else 
				System.out.println("No animal found...");
			
		} catch (Exception e) {
			Session.log.info("SQL Error: " + e.toString());
			System.out.println("Something went wrong!");
		} finally {
			//close everything
			try {
				animalResult.close();
				animalQuery.close();
			}catch(Exception e) {
				//If closing errors out
				Session.log.info("DB Closing Error: " + e.toString());
			}
		}	
    }
    
    

    /**
     * Update an animal with the given list of values:
     * name, description, species_name, and birthday
     * @param oldId
     * @param newValues
     */
    public static void updateAnimal(String oldId, String[] newValues) {    	
    	PreparedStatement query = null;
    	
    	try {
			
			query = Session.conn.prepareStatement(
					   "UPDATE animal "
					   + "SET name = ?, description = ?, species_name = ?, birthday = ?, food_quantity = ? "  
					   + "WHERE animal_id = ?");
				
			for(int i = 1; i <= 5; i++) {
				query.setString(i, newValues[i-1]);
			}
			query.setString(6, oldId);
			query.executeUpdate();
			
    	} catch (Exception e) {
			Session.log.info("SQL Error: " + e.toString());
			System.out.println("Something went wrong!");
		} finally {
			//close everything
			try {
				query.close();
			}catch(Exception e) {
				//If closing errors out
				Session.log.info("DB Closing Error: " + e.toString());
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
				
			for(int i = 1; i <= 6; i++) {
				query.setString(i, newValues[i-1]);
			}
			query.setTimestamp(7, new Timestamp(System.currentTimeMillis()));

			query.executeUpdate();
			
    	} catch (Exception e) {
			Session.log.info("SQL Error: " + e.toString());
			System.out.println("Something went wrong!");
		} finally {
			//close everything
			try {
				query.close();
			}catch(Exception e) {
				//If closing errors out
				Session.log.info("DB Closing Error: " + e.toString());
			}
		}	
    }
    
    
    
}
