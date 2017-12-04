package zoo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.shell.table.ArrayTableModel;
import org.springframework.shell.table.BorderStyle;
import org.springframework.shell.table.TableBuilder;
import org.springframework.shell.table.TableModelBuilder;

/**
 * Contains the worker methods for species functionality
 * @author damongeorge
 *
 */
public class Species {


	/**
	 * Check
	 */
    public static void listAllSpecies() {
    	PreparedStatement query = null;
		ResultSet result = null;
				
		try {
			if (Session.admin) {
				query = Session.conn.prepareStatement(
						   "SELECT * "
						   + "FROM species");
			} else {
				query = Session.conn.prepareStatement(
						   "SELECT s.species_name, s.common_name, s.enclosure_id, s.description "
						   + "FROM species s JOIN employee_training t USING (species_name) "
						   + "WHERE t.username = ?");
				query.setString(1, Session.currentUser);
			}
			
			result = query.executeQuery();
			if(result.next())
				TableBuilding.printBasicTable(result);
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
     * TODO: Check
     * @param username
     * @return
     */
    public static boolean speciesExists(String speciesName) {
    	PreparedStatement query = null;
		ResultSet result = null;
		boolean exists = false;
		
		try {
			query = Session.conn.prepareStatement(
					   "SELECT species_name "
					   + "FROM species WHERE species_name = ?");
			query.setString(1, speciesName);
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
    

}
