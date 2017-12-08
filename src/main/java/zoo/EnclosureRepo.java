package zoo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
 
/**
 * Contains the database query methods for enclosure functionality
 * @author damongeorge
 *
 */
public class EnclosureRepo {

	/**
	 * List all enclosures in the zoo
	 */
    public static void listAllEnclosures() {
    	PreparedStatement query = null;
		ResultSet result = null;
				
		try {
			if (Session.admin) { //If admin show all enclosures
				query = Session.conn.prepareStatement(
						   "SELECT * "
						   + "FROM enclosure");
				
			} else { //Otherwise just display the enclosures the user works with
				query = Session.conn.prepareStatement(
						   "SELECT e.enclosure_id, e.name, e.environment, e.open "
						   + "FROM enclosure e JOIN species s USING (enclosure_id) JOIN employee_training t USING (species_name) "
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
     * Check if the given enclosure exists
     * @param username
     * @return
     */
    public static boolean enclosureExists(String enclosureId) {
    	PreparedStatement query = null;
		ResultSet result = null;
		boolean exists = false;
		
		try {
			query = Session.conn.prepareStatement(
					   "SELECT enclosure_id "
					   + "FROM enclosure WHERE enclosure_id = ?");
			query.setString(1, enclosureId);
			
			//Execute query and check if the results contain any entries
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
     * Add new enclosure
     * @param newValues The array of values starting with enclosure id and ending with open
     */
    public static void addEnclosure(String[] newValues) {
    	PreparedStatement query = null;
    	
    	try {
			query = Session.conn.prepareStatement(
					   "INSERT INTO enclosure VALUES (?, ?, ?, ?)");
				
			for(int i = 1; i <= 4; i++) { //Loop through new values, adding each to the query
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
    
    /**
     * Set the value of open for the given enclosure
     * @param newValues
     */
    public static void setOpen(String enclosureId, boolean open) {
    	PreparedStatement query = null;
    	
    	try {
			query = Session.conn.prepareStatement(
					   "UPDATE enclosure SET open = ? WHERE enclosure_id = ?");
				
			query.setBoolean(1, open);; //Set boolean value
			query.setString(2, enclosureId);

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
    
}
