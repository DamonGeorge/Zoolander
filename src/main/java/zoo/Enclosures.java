package zoo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.shell.table.ArrayTableModel;
import org.springframework.shell.table.BorderStyle;
import org.springframework.shell.table.TableBuilder;
import org.springframework.shell.table.TableModelBuilder;

/**
 * Contains the worker methods for enclosure functionality
 * @author damongeorge
 *
 */
public class Enclosures {


	/**
	 * Check
	 */
    public static void listAllEnclosures() {
    	PreparedStatement query = null;
		ResultSet result = null;
				
		try {
			if (Session.admin) {
				query = Session.conn.prepareStatement(
						   "SELECT * "
						   + "FROM enclosure");
			} else {
				query = Session.conn.prepareStatement(
						   "SELECT e.enclosure_id, e.name, e.environment, e.open "
						   + "FROM enclosure e JOIN species s USING (enclosure_id) JOIN employee_training t USING (species_name) "
						   + "WHERE t.username = ?");
				query.setString(1, Session.currentUser);
			}
			
			result = query.executeQuery();
			if(result.next())
				printEnclosuresAsciiTable(result);
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
    public static boolean enclosureExists(String enclosureId) {
    	PreparedStatement query = null;
		ResultSet result = null;
		boolean exists = false;
		
		try {
			query = Session.conn.prepareStatement(
					   "SELECT enclosure_id "
					   + "FROM enclosure WHERE enclosure_id = ?");
			query.setString(1, enclosureId);
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
     * TODO: remove throws declaration???
     * @param result
     * @throws SQLException
     */
    private static void printEnclosuresAsciiTable(ResultSet result) throws SQLException{
    	TableModelBuilder<String> builder = new TableModelBuilder<>();

    	builder.addRow();
		builder.addValue("ID ");
		builder.addValue("Name");
		builder.addValue("Environment");
		builder.addValue("Open");
		
//		try {
			result.beforeFirst();
			while(result.next()) {
				builder.addRow();
				for(int i = 1; i <=3; i++ ){
					builder.addValue(result.getString(i));
				}			
				builder.addValue(result.getString(4).equals("1") ? "yes" : "no");
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
