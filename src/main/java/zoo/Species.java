package zoo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
    
    /**
     * Add new species
     * @param newValues The array of values starting with species name and ending with description
     */
    public static void addSpecies(String[] newValues) {
    	PreparedStatement query = null;
    	
    	try {
			
			query = Session.conn.prepareStatement(
					   "INSERT INTO species VALUES (?, ?, ?, ?)");
				
			for(int i = 1; i <= 4; i++) {
				query.setString(i, newValues[i-1]);
			}

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
