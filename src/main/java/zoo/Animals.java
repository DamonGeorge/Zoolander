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
					   + "FROM user u JOIN employee_training t USING (username) JOIN Animal a USING (species_name) JOIN species s USING (species_name)"
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
     * TODO: Want search terms in command or in this method
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
			System.out.println(table.build().render(100));
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
		
		try {
			query = Session.conn.prepareStatement(
					   "SELECT animal_id "
					   + "FROM animal WHERE animal_id = ?");
			query.setString(1, animalId);
			result = query.executeQuery();
			if(result.next()) 
				return true;
			else 
				return false;
			
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
		return false;
    }
    
    /**
     * TODO: What do we want to see from this query
     * @param username
     */
    public static void viewAnimal(String animalId) {
    	PreparedStatement animalQuery = null, speciesQuery = null;
		ResultSet animalResult = null, speciesResult = null;
		TableModelBuilder<String> anBuilder = new TableModelBuilder<>();
		TableModelBuilder<String> spBuilder = new TableModelBuilder<>();
    	
		anBuilder.addRow();
    	anBuilder.addValue("ID").addValue("Name").addValue("Birthday").addValue("Food Quantity" ).addValue("Last Feeding");
    	
		
    	spBuilder.addRow();
    	spBuilder.addValue("Species").addValue("Common Name").addValue("Enclosure ID").addValue("Description");
    	
    	
    	
		try {
			animalQuery = Session.conn.prepareStatement(
						"SELECT a.animal_id, a.name, a.birthday, a.food_quantity, a.last_feeding  "
					   + "FROM animal WHERE animal_id = ?");
			animalQuery.setString(1, animalId);
			
			
			animalResult = animalQuery.executeQuery();
			if(animalResult.next())  {
				anBuilder.addRow();
				for(int i = 1; i <= 8; i++) {
					anBuilder.addValue(animalResult.getString(i));
				}		
				
				TableBuilder table = new TableBuilder(anBuilder.build().transpose());
				table.addHeaderAndVerticalsBorders(BorderStyle.oldschool);
				System.out.println("Animal: ");
				System.out.println(table.build().render(100));
				
				
//				speciesQuery = Session.conn.prepareStatement(
//						"SELECT a.animal_id, a.name, a.species_name, e.enclosure_id, e.name, e.open "
//						+ "FROM animal a JOIN employee_training t USING (species_name) JOIN user u USING (username) JOIN species s USING (species_name) JOIN enclosure e USING (enclosure_id) "
//						+ "WHERE u.username = ?");
//				speciesQuery.setString(1, username);
//				speciesResult = speciesQuery.executeQuery();
//				while(speciesResult.next()) {
//					spBuilder.addRow();
//					for(int i = 1; i <= 6; i++) {
//						spBuilder.addValue(speciesResult.getString(i));
//					}
//				}
//				
//				table = new TableBuilder(spBuilder.build());
//				table.addHeaderAndVerticalsBorders(BorderStyle.oldschool);
//				System.out.println("Animals: ");
//				System.out.println(table.build().render(100));
				
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
				speciesResult.close();
				speciesQuery.close();
			}catch(Exception e) {
				//If closing errors out
				Session.log.info("DB Closing Error: " + e.toString());
			}
		}	
    }
    
    
}
