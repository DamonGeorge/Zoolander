package zoo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.shell.table.ArrayTableModel;
import org.springframework.shell.table.BorderStyle;
import org.springframework.shell.table.TableBuilder;
import org.springframework.shell.table.TableModelBuilder;

/**
 * Contains the worker methods for employee functionality
 * @author damongeorge
 *
 */
public class Employees {


	/**
	 * Done
	 */
    public static void listAllEmployees() {
    	PreparedStatement listQuery = null;
		ResultSet result = null;
		
		TableModelBuilder<String> builder = new TableModelBuilder<>();

				
		try {
			listQuery = Session.conn.prepareStatement(
					   "SELECT username, first_name, last_name, email, birthday, salary "
					   + "FROM user");
			result = listQuery.executeQuery();
			builder.addRow();
			builder.addValue("Username");
			builder.addValue("First Name");
			builder.addValue("Last Name");
			builder.addValue("Email" );
			builder.addValue("Birthday");
			builder.addValue("Salary");	
			
			while(result.next()) {
				builder.addRow();
				builder.addValue(result.getString(1));
				builder.addValue(result.getString(2));
				builder.addValue(result.getString(3));
				builder.addValue(result.getString(4));
				builder.addValue(result.getString(5));
				builder.addValue(result.getString(6));				
			}
			TableBuilder table = new TableBuilder(builder.build());
			table.addHeaderAndVerticalsBorders(BorderStyle.oldschool);
			System.out.println(table.build().render(100));
			
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
    
}
