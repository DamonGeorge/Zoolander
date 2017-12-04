package zoo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.shell.table.ArrayTableModel;
import org.springframework.shell.table.BorderStyle;
import org.springframework.shell.table.TableBuilder;
import org.springframework.shell.table.TableModelBuilder;

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
			if(result.next())
				printAnimalsAsciiTable(result);
			else {
				System.out.println("No results found...");
			}
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
     * TODO: check
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
			if(result.next()) 
				printAnimalsAsciiTable(result);
			else 
				System.out.println("No results found...");
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
			if(result.next()) 
				printAnimalsAsciiTable(result);
			else 
				System.out.println("No results found...");
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
     * TODO: check
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
			if(result.next()) 
				printAnimalsAsciiTable(result);
			else 
				System.out.println("No results found...");
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
     * TODO: remove throws declaration???
     * @param result
     * @throws SQLException
     */
    private static void printAnimalsAsciiTable(ResultSet result) throws SQLException{
    	TableModelBuilder<String> builder = new TableModelBuilder<>();

    	builder.addRow();
		builder.addValue("Animal ID");
		builder.addValue("Name");
		builder.addValue("Species");
		builder.addValue("Common Name");
		builder.addValue("Birthday" );
		builder.addValue("Last Feeding");
		
//		try {
			result.beforeFirst();
			while(result.next()) {
				builder.addRow();
				for(int i = 1; i <=6; i++ ){
					builder.addValue(result.getString(i));
				}			
			}
			
			TableBuilder table = new TableBuilder(builder.build());
			table.addHeaderAndVerticalsBorders(BorderStyle.oldschool);
			System.out.println(table.build().render(Session.terminalWidth));
//		} catch (SQLException e) {
//			Session.log.info("SQL Error: " + e.toString());
//			System.out.println("Something went wrong!");
//		}
    }
    
    
    /**
     * TODO: Check
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
     * TODO: finish checking
     * @param username
     */
    public static void viewAnimal(String animalId) {
    	PreparedStatement animalQuery = null;
		ResultSet animalResult = null;
		TableModelBuilder<String> anBuilder = new TableModelBuilder<>();
		TableModelBuilder<String> spBuilder = new TableModelBuilder<>();
    	TableModelBuilder<String> finalBuilder = new TableModelBuilder<>();
    	
		anBuilder.addRow();
    	anBuilder.addValue("ID").addValue("Name").addValue("Birthday").addValue("Food Quantity" ).addValue("Last Feeding");
    	
		
    	spBuilder.addRow();
    	spBuilder.addValue("Species").addValue("Common Name").addValue("Enclosure ID").addValue("Description");
    	
    	finalBuilder.addRow();
    	
    	
		try {
			animalQuery = Session.conn.prepareStatement(
						"SELECT * "
					   + "FROM animal a JOIN species s USING (species_name) "
					   + "WHERE animal_id = ?");
			animalQuery.setString(1, animalId);
			animalResult = animalQuery.executeQuery();
			
			if(animalResult.next())  {
				anBuilder.addRow();
				anBuilder.addValue(animalResult.getString("animal_id"));	
				anBuilder.addValue(animalResult.getString("name"));	
				anBuilder.addValue(animalResult.getString("birthday"));	
				anBuilder.addValue(animalResult.getString("food_quantity"));	
				anBuilder.addValue(animalResult.getString("last_feeding"));	
				
				spBuilder.addRow();
				spBuilder.addValue(animalResult.getString("species_name"));
				spBuilder.addValue(animalResult.getString("common_name"));
				spBuilder.addValue(animalResult.getString("enclosure_id"));
				spBuilder.addValue(animalResult.getString("s.description"));
				
				TableBuilder animalTable = new TableBuilder(anBuilder.build().transpose());
				animalTable.addHeaderAndVerticalsBorders(BorderStyle.oldschool);

				TableBuilder speciesTable = new TableBuilder(spBuilder.build().transpose());
				speciesTable.addHeaderAndVerticalsBorders(BorderStyle.oldschool);
				
				finalBuilder.addValue("Animal: \n" + animalTable.build().render(Session.terminalWidth/2)
						+ "\nDescription: \n" + animalResult.getString("a.description"));
				finalBuilder.addValue("          ");
				finalBuilder.addValue("Species: \n" + speciesTable.build().render(Session.terminalWidth/2));
				
				TableBuilder finalTable = new TableBuilder(finalBuilder.build());
				System.out.println(finalTable.build().render(Session.terminalWidth));
				
			} else 
				System.out.println("No employee found...");
			
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
     * TODO: Check
     * TODO: check if species exists
     * @param oldId
     * @param newValues
     */
    public static void updateAnimal(String oldId, String[] newValues) {
    	if(!animalExists(oldId)) {
    		System.out.println("Animal with id " + oldId + " doesn't exist!");
    		return;
    	}
    	
    	PreparedStatement query = null;
    	
    	try {
			
			query = Session.conn.prepareStatement(
					   "UPDATE animal "
					   + "SET name = ?, description = ?, species_name = ?, birthday = ?, "
					   + "food_quantity = ?, last_feeding = ? "  
					   + "WHERE animal_id = ?");
				
			for(int i = 1; i <= 6; i++) {
				query.setString(i, newValues[i-1]);
			}
			query.setString(7, oldId);
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
