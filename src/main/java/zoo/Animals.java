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
	 * Done
	 */
    public static void listAllAnimals() {
    	PreparedStatement listQuery = null;
		ResultSet result = null;
				
		try {
			listQuery = Session.conn.prepareStatement(
					   "SELECT a.animal_id, a.name, s.species_name, s.common_name, a.birthday, a.last_feeding "
					   + "FROM animal a JOIN species s USING (species_name)");
			result = listQuery.executeQuery();
			
			printAnimalsAsciiTable(result);
			
		} catch (SQLException e) {
			Session.log.info("SQL Error: " + e.toString());
			System.out.println("Something went wrong!");
		} finally {
			//close everything
			try {
				result.close();
				listQuery.close();
			}catch(Exception e) {
				//If closing errors out
				Session.log.info("SQL Error: " + e.toString());
				System.out.println("Something went wrong!");
			}
		}	
    }   
    
    /**
     * TODO: Want search terms in command or in this method
     * @param search
     */
    public static void searchAnimalsByAnimal(String search) {
    	PreparedStatement query = null;
		ResultSet result = null;
		search = "%" + search.toLowerCase() + "%";
		
		try {
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
			
			result = query.executeQuery();
			if(result.next()) 
				printAnimalsAsciiTable(result);
			else 
				System.out.println("No results found...");
		} catch (SQLException e) {
			Session.log.info("SQL Error: " + e.toString());
			System.out.println("Something went wrong!");
		} finally {
			//close everything
			try {
				result.close();
				query.close();
			}catch(Exception e) {
				//If closing errors out
				Session.log.info("SQL Error: " + e.toString());
				System.out.println("Something went wrong!");
			}
		}	
    }
    
    
    
    /**
     * TODO: Want search terms in command or in this method
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
		} catch (SQLException e) {
			Session.log.info("SQL Error: " + e.toString());
			System.out.println("Something went wrong!");
		} finally {
			//close everything
			try {
				result.close();
				query.close();
			}catch(Exception e) {
				//If closing errors out
				Session.log.info("SQL Error: " + e.toString());
				System.out.println("Something went wrong!");
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
			query = Session.conn.prepareStatement(
					   "SELECT a.animal_id, a.name, s.species_name, s.common_name, a.birthday, a.last_feeding  "
					   + "FROM animal a JOIN species s USING (species_name) JOIN enclosure e USING (enclosure_id)"
					   + "WHERE LOWER(e.enclosure_id) LIKE ? "
					   + "OR LOWER(e.name) LIKE ? "
					   + "OR LOWER(e.environment) LIKE ? ");
			for(int i = 1; i<=3; i++) {
				query.setString(i, search);
			}
			
			result = query.executeQuery();
			if(result.next()) 
				printAnimalsAsciiTable(result);
			else 
				System.out.println("No results found...");
		} catch (SQLException e) {
			Session.log.info("SQL Error: " + e.toString());
			System.out.println("Something went wrong!");
		} finally {
			//close everything
			try {
				result.close();
				query.close();
			}catch(Exception e) {
				//If closing errors out
				Session.log.info("SQL Error: " + e.toString());
				System.out.println("Something went wrong!");
			}
		}	
    }
    
    /* TODO: Change from employee to animal
    public static void viewEmployee(String username) {
    	PreparedStatement userQuery = null, animalQuery = null;
		ResultSet userResult = null, animalResult = null;
		TableModelBuilder<String> empBuilder = new TableModelBuilder<>();
		TableModelBuilder<String> anBuilder = new TableModelBuilder<>();
    	
		empBuilder.addRow();
    	empBuilder.addValue("Username").addValue("First Name").addValue("Last Name")
    	.addValue("Email" ).addValue("Birthday").addValue("Salary").addValue("active").addValue("admin");	
		
    	anBuilder.addRow();
    	anBuilder.addValue("Animal ID").addValue("Name").addValue("Species").addValue("Enclosure ID").addValue("Enclosure Name").addValue("Open");
    	
    	
    	
		try {
			userQuery = Session.conn.prepareStatement(
					   "SELECT username, first_name, last_name, email, birthday, salary, active, admin "
					   + "FROM user WHERE username = ?");
			userQuery.setString(1, username);
			
			
			userResult = userQuery.executeQuery();
			if(userResult.next())  {
				empBuilder.addRow();
				for(int i = 1; i <= 8; i++) {
					empBuilder.addValue(userResult.getString(i));
				}		
				
				TableBuilder table = new TableBuilder(empBuilder.build().transpose());
				table.addHeaderAndVerticalsBorders(BorderStyle.oldschool);
				System.out.println("Employee: ");
				System.out.println(table.build().render(100));
				
				
				animalQuery = Session.conn.prepareStatement(
						"SELECT a.animal_id, a.name, a.species_name, e.enclosure_id, e.name, e.open "
						+ "FROM animal a JOIN employee_training t USING (species_name) JOIN user u USING (username) JOIN species s USING (species_name) JOIN enclosure e USING (enclosure_id) "
						+ "WHERE u.username = ?");
				animalQuery.setString(1, username);
				animalResult = animalQuery.executeQuery();
				while(animalResult.next()) {
					anBuilder.addRow();
					for(int i = 1; i <= 6; i++) {
						anBuilder.addValue(animalResult.getString(i));
					}
				}
				
				table = new TableBuilder(anBuilder.build());
				table.addHeaderAndVerticalsBorders(BorderStyle.oldschool);
				System.out.println("Animals: ");
				System.out.println(table.build().render(100));
				
			} else 
				System.out.println("No employee found...");
			
		} catch (SQLException e) {
			Session.log.info("SQL Error: " + e.toString());
			System.out.println("Something went wrong!");
		} finally {
			//close everything
			try {
				userResult.close();
				userQuery.close();
				animalResult.close();
				animalQuery.close();
			}catch(Exception e) {
				//If closing errors out
				Session.log.info("SQL Error: " + e.toString());
				System.out.println("Something went wrong!");
			}
		}	
    }
    */
    
    
    
    
    
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
    
}
