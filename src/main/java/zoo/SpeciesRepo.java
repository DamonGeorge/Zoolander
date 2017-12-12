package zoo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Contains the worker methods for species functionality
 * @author damongeorge
 * @author anthonyniehuser
 */
public class SpeciesRepo {


	/**
	 * List all the species the user handles.
	 * If admin, list all.
	 */
    public static void listAllSpecies() {
    	PreparedStatement query = null;
		ResultSet result = null;
				
		try { 
			if (Session.admin) { //If admin, show all species
				query = Session.conn.prepareStatement(
						   "SELECT * "
						   + "FROM species");
			
			} else { //Otherwise just show the species that the user handles
				query = Session.conn.prepareStatement(
						   "SELECT s.species_name, s.common_name, s.enclosure_id, s.description "
						   + "FROM species s JOIN employee_training t USING (species_name) "
						   + "WHERE t.username = ?");
				query.setString(1, Session.currentUser);
			}
			
			//Execute query and print results
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
     * Check if a species exists in the database
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
			
			//Execute the query and check if the result contains any entries
			result = query.executeQuery();
			if(result.next()) 
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
     * Add new species
     * @param newValues The array of values starting with species name and ending with description
     */
    public static void addSpecies(String[] newValues) {
    	PreparedStatement query = null;
    	
    	try {
			query = Session.conn.prepareStatement(
					   "INSERT INTO species VALUES (?, ?, ?, ?)");
				
			for(int i = 1; i <= 4; i++) { //Loop through new values and add each to the query
				query.setString(i, newValues[i-1]);
			}

			query.executeUpdate();
			
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

    
    public static void speciesHandlerAndFoodStats(String species){
		PreparedStatement handleStmt = null;
		ResultSet handleRs = null;
		PreparedStatement foodStmt = null;
		ResultSet foodRs = null;
		try{
			handleStmt =Session.conn.prepareStatement(
					"SELECT DISTINCT e.username, CONCAT(CONCAT(e.first_name, ' '), e.last_name) AS Name "
					+ "FROM employee e NATURAL JOIN employee_training et1 "
					+ "WHERE et1.species_name=? OR e.admin=1");
			handleStmt.setString(1, species);
			handleRs = handleStmt.executeQuery();
			
			foodStmt = Session.conn.prepareStatement(
					"SELECT f.food_id, f.name, f.brand, f.quantity, f.cost "
					+ "FROM food f NATURAL JOIN species_eats se "
					+ "WHERE se.species_name=?");
			foodStmt.setString(1, species);
			foodRs = foodStmt.executeQuery();
			
			System.out.println("Handlers for: " + species);
			AsciiTableHelper.printBasicTable(handleRs);
			System.out.println("Food for: " + species);
			AsciiTableHelper.printBasicTable(foodRs);
			
			
			//AsciiTableHelper.printDoubleTable(table1, "Handlers for: " + species, table2, "Food for: " + species, Session.terminalWidth *1/2);
			
		} catch (Exception e) {
			Session.log.warning("SQL Error: " + e.toString());
			System.out.println("Something went wrong: " + e.getMessage());
		} finally {
			//close everything
			try {
				if(handleStmt!=null) 	handleStmt.close();
				if(handleRs!=null)		handleRs.close();
			}catch(Exception e) {
				//If closing errors out
				Session.log.warning("DB Closing Error: " + e.toString());
			}
		}
		
		
	}
}
