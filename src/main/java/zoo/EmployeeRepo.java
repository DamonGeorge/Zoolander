package zoo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.springframework.shell.table.BorderStyle;
import org.springframework.shell.table.TableBuilder;

/**
 * Contains the database query methods for employee functionality
 * @author damongeorge
 * @author anthonyniehuser
 *
 */
public class EmployeeRepo {


	/**
	 * List all the Employees in the database
	 */
    public static void listAllEmployees() {
    	PreparedStatement listQuery = null;
		ResultSet result = null;
				
		try {
			listQuery = Session.conn.prepareStatement(
					   "SELECT username, first_name, last_name, email, birthday, salary "
					   + "FROM employee");
			
			//Execute and print results in Ascii Table
			result = listQuery.executeQuery();
			AsciiTableHelper.printBasicTable(result);
			
		} catch (Exception e) {
			Session.log.warning("SQL Error: " + e.toString());
			System.out.println("Something went wrong!");
		} finally { //close everything
			try {
				result.close();
				listQuery.close();
			}catch(Exception e) { //If closing errors out
				Session.log.warning("DB Closing Error: " + e.toString());
			}
		}	
    }   
    
    /**
     * Search All Employees using employee information
     * @param search
     */
    public static void searchEmployeesByEmployee(String search) {
    	PreparedStatement query = null;
		ResultSet result = null;
		search = "%" + search.toLowerCase() + "%"; //For SQL LIKE Syntax
		
		try {
			query = Session.conn.prepareStatement(
					   "SELECT username, first_name, last_name, email, birthday, salary "
					   + "FROM employee "
					   + "WHERE LOWER(username) LIKE ? "
					   + "OR LOWER(first_name) LIKE ? "
					   + "OR LOWER(last_name) LIKE ? "
					   + "OR LOWER(email) LIKE ? "
					   + "OR LOWER(birthday) LIKE ? "
					   + "OR LOWER(salary) LIKE ?");
			
			for(int i = 1; i<=6; i++) { //Set all params to the search value
				query.setString(i, search);
			}
			
			//Execute query and print result
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
     * Search all employees using the information of animals they handle
     * @param search
     */
    public static void searchEmployeesByAnimal(String search) {
    	PreparedStatement query = null;
		ResultSet result = null;
		search = "%" + search.toLowerCase() + "%";
		
		try {
			query = Session.conn.prepareStatement(
					   "SELECT u.username, u.first_name, u.last_name, u.email, u.birthday, u.salary "
					   + "FROM employee u JOIN employee_training t USING (username) JOIN Animal a USING (species_name) JOIN Species s USING (species_name)"
					   + "WHERE LOWER(a.animal_id) LIKE ? "
					   + "OR LOWER(a.name) LIKE ? "
					   + "OR LOWER(a.species_name) LIKE ? "
					   + "OR LOWER(s.common_name) like ? "
					   + "OR LOWER(a.birthday) LIKE ? ");
			
			for(int i = 1; i<=5; i++) { //Set all params to the search value
				query.setString(i, search);
			}
			
			//Execute query and print result
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
     * Search all employees using the enclosure information of animals they handle
     * @param search
     */
    public static void searchEmployeesByEnclosure(String search) {
    	PreparedStatement query = null;
		ResultSet result = null;
		search = "%" + search.toLowerCase() + "%";
		
		try {
			query = Session.conn.prepareStatement(
					   "SELECT u.username, u.first_name, u.last_name, u.email, u.birthday, u.salary "
					   + "FROM employee u JOIN employee_training t USING (username) JOIN species USING (species_name) JOIN enclosure e USING (enclosure_id)"
					   + "WHERE LOWER(e.enclosure_id) LIKE ? "
					   + "OR LOWER(e.name) LIKE ? "
					   + "OR LOWER(e.environment) LIKE ? ");
			
			for(int i = 1; i<=3; i++) { //Set all params to the search value
				query.setString(i, search);
			}
			
			//Execute query and print result
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
     * View the details of a single employee and the animals he/she handles
     * @param username
     */
    public static void viewEmployee(String username) {
    	PreparedStatement userQuery = null, animalQuery = null;
		ResultSet userResult = null, animalResult = null;
    	
		try {
			userQuery = Session.conn.prepareStatement(
					   "SELECT username, first_name, last_name, email, birthday, salary, active, admin "
					   + "FROM employee WHERE username = ?");
			
			//Get details of the specified user from the employee table
			userQuery.setString(1, username);
			userResult = userQuery.executeQuery();
			
			
			if(userResult.next())  { //if employee actually exists, get their animals
				
				animalQuery = Session.conn.prepareStatement(
						"SELECT a.animal_id, a.name, a.species_name, e.enclosure_id, e.name, e.open "
						+ "FROM animal a JOIN employee_training t USING (species_name) JOIN employee u USING (username) JOIN species s USING (species_name) JOIN enclosure e USING (enclosure_id) "
						+ "WHERE u.username = ?");
				//Execute the query with the employee's username
				animalQuery.setString(1, username);
				animalResult = animalQuery.executeQuery();
				
				//Get the two table builders from the two result sets
				TableBuilder table1 = AsciiTableHelper.getAsciiTable(userResult, true);
				TableBuilder table2 = AsciiTableHelper.getAsciiTable(animalResult, false);

				//Add borders and print out side by side in a double table
				table1.addHeaderAndVerticalsBorders(BorderStyle.oldschool);
				table2.addHeaderAndVerticalsBorders(BorderStyle.oldschool);
				AsciiTableHelper.printDoubleTable(table1, "Employee: ", table2, "Species: ", Session.terminalWidth/3);
				
			} else 
				System.out.println("No employee found...");
			
		} catch (Exception e) {
			Session.log.warning("SQL Error: " + e.toString());
			System.out.println("Something went wrong!");
		} finally { //close everything
			try {
				userResult.close();
				userQuery.close();
				animalResult.close();
				animalQuery.close();
			}catch(Exception e) { //If closing errors out
				Session.log.warning("DB Closing Error: " + e.toString());
			}
		}	
    }
    
    /**
     * Check if the given username exists in the database
     * @param username
     * @return
     */
    public static boolean employeeExists(String username) {
    	PreparedStatement userQuery = null;
		ResultSet userResult = null;
		boolean exists = false;
		
		try {
			userQuery = Session.conn.prepareStatement(
					   "SELECT username "
					   + "FROM employee WHERE username = ?");
			userQuery.setString(1, username);
			
			//Execute query and check if it has any results
			userResult = userQuery.executeQuery();
			if(userResult.next()) 
				exists = true;
			
		} catch (Exception e) {
			Session.log.warning("SQL Error: " + e.toString());
			System.out.println("Something went wrong!");
		} finally { //close everything
			try {
				userResult.close();
				userQuery.close();
			}catch(Exception e) { //If closing errors out
				Session.log.warning("DB Closing Error: " + e.toString());
			}
		}
		return exists;
    }
    
    /**
     * Update all the values of a user
     * @param oldUsername Username of employee to update
     * @param newValues Array of all new values starting with username
     */
    public static void updateEmployee(String oldUsername, String[] newValues) {
    	PreparedStatement query = null;
    	
    	try {
			query = Session.conn.prepareStatement(
					   "UPDATE employee "
					   + "SET username = ?, first_name = ?, last_name = ?, birthday = ?, "
					   + "email = ?, salary = ?, active = ?, admin = ? "  
					   + "WHERE username = ?");
				
			for(int i = 1; i <= 8; i++) { //Loop through new values, adding to query
				query.setString(i, newValues[i-1]);
			}
			query.setString(9, oldUsername); //Set last param to the employee's original username
			query.executeUpdate(); //execute the udpate
			
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
     * Add new employee
     * @param newValues The array of values starting with username and ending with admin
     */
    public static void addEmployee(String[] newValues) {
    	PreparedStatement query = null;
    	
    	try {
			query = Session.conn.prepareStatement(
					   "INSERT INTO employee VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
				
			for(int i = 1; i <= 8; i++) { //Loop through new values, adding them to the query
				query.setString(i, newValues[i-1]);
			}

			query.executeUpdate(); //execute the update
			
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
     * either updates an employee's training date, or creates a new row
     * if the employee was not previously trained to handle a given species
     * @param username	employee to be trained
     * @param newValues	training info
     */
    public static void trainEmployee(String username, String[] newValues)
    {
    	if(!Session.admin){
    		System.out.println("Only an admin can train employees.");
    		return;
    	}
    	PreparedStatement stmt = null;
    	ResultSet rs = null;
    	try {
    		stmt = Session.conn.prepareStatement(
    				"SELECT * "
    				+ "FROM employee_training "
    				+ "WHERE username=? AND species_name=?");
    		stmt.setString(1, username);
    		stmt.setString(2, newValues[0]);
    		rs = stmt.executeQuery();
    		stmt.close();
    		
    		//if employee previously trained, update training info
    		if(rs.next()){
    			stmt = Session.conn.prepareStatement(
        				"UPDATE employee_training "
        				+ "SET date_trained=?, years_to_renew=? "
        				+ "WHERE username=? AND species_name=?");
    			stmt.setString(1, newValues[1]);
    			stmt.setString(2, newValues[2]);
    			stmt.setString(3, username);
    			stmt.setString(4, newValues[0]);
    		} else { //otherwise insert new row to employee_training
    			stmt = Session.conn.prepareStatement(
    					"INSERT INTO employee_training VALUES (?,?,?,?)"
    					);
    			stmt.setString(1,  username);
    			for(int i=0; i<=2; i++){
    				stmt.setString(i+2, newValues[i]);
    			}
    		}
    		stmt.execute();
    		
    		
    		
    	} catch (Exception e) {
			Session.log.warning("SQL Error: " + e.toString());
			System.out.println("Something went wrong! " + e.getMessage() );
		} finally { //close everything
			try {
				if(stmt!=null) stmt.close();
				if(rs!=null) rs.close();
			}catch(Exception e) { //If closing errors out
				Session.log.warning("DB Closing Error: " + e.toString());
			}
		}	
    }
}
