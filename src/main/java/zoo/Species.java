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
				printSpeciesAsciiTable(result);
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
		
		try {
			query = Session.conn.prepareStatement(
					   "SELECT species_name "
					   + "FROM species WHERE species_name = ?");
			query.setString(1, speciesName);
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
     * TODO: remove throws declaration???
     * @param result
     * @throws SQLException
     */
    private static void printSpeciesAsciiTable(ResultSet result) throws SQLException{
    	TableModelBuilder<String> builder = new TableModelBuilder<>();

    	builder.addRow();
		builder.addValue("Species Name");
		builder.addValue("Common Name");
		builder.addValue("Enclosure ID");
		builder.addValue("Description");
		
//		try {
			result.beforeFirst();
			while(result.next()) {
				builder.addRow();
				for(int i = 1; i <=4; i++ ){
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
}
