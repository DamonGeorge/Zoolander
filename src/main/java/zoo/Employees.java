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
	 * List all the Employees in the database
	 */
    public static void listAllEmployees() {
    	PreparedStatement listQuery = null;
		ResultSet result = null;
				
		try {
			listQuery = Session.conn.prepareStatement(
					   "SELECT username, first_name, last_name, email, birthday, salary "
					   + "FROM employee");
			result = listQuery.executeQuery();
			
			printEmployeesAsciiTable(result);
			
		} catch (Exception e) {
			Session.log.info("SQL Error: " + e.toString());
			System.out.println("Something went wrong!");
		} finally {
			//close everything
			try {
				result.close();
				listQuery.close();
			}catch(Exception e) {
				//If closing errors out
				Session.log.info("DB Closing Error: " + e.toString());
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
		search = "%" + search.toLowerCase() + "%";
		
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
			for(int i = 1; i<=6; i++) {
				query.setString(i, search);
			}
			
			result = query.executeQuery();
			if(result.next()) 
				printEmployeesAsciiTable(result);
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
			for(int i = 1; i<=5; i++) {
				query.setString(i, search);
			}
			
			result = query.executeQuery();
			if(result.next()) 
				printEmployeesAsciiTable(result);
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
			for(int i = 1; i<=3; i++) {
				query.setString(i, search);
			}
			
			result = query.executeQuery();
			if(result.next()) 
				printEmployeesAsciiTable(result);
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
     * Prints the general employee table from a given resultset of employee
     * @param result employee results containing username, first_name, last_name, email, birthday, and salary
     * @throws SQLException
     */
    private static void printEmployeesAsciiTable(ResultSet result) throws SQLException{
    	TableModelBuilder<String> builder = new TableModelBuilder<>();

    	builder.addRow();
		builder.addValue("Username");
		builder.addValue("First Name");
		builder.addValue("Last Name");
		builder.addValue("Email" );
		builder.addValue("Birthday");
		builder.addValue("Salary");	
		
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
			System.out.println(table.build().render(Session.terminalWidth));
//		} catch (SQLException e) {
//			Session.log.info("SQL Error: " + e.toString());
//			System.out.println("Something went wrong!");
//		}
    }
    
    /**
     * View the details of a single employee and the animals he/she handles
     * @param username
     */
    public static void viewEmployee(String username) {
    	PreparedStatement userQuery = null, animalQuery = null;
		ResultSet userResult = null, animalResult = null;
		TableModelBuilder<String> empBuilder = new TableModelBuilder<>();
		TableModelBuilder<String> anBuilder = new TableModelBuilder<>();
		TableModelBuilder<String> finalBuilder = new TableModelBuilder<>();
		
		empBuilder.addRow();
    	empBuilder.addValue("Username").addValue("First Name").addValue("Last Name")
    	.addValue("Email" ).addValue("Birthday").addValue("Salary").addValue("active").addValue("admin");	
		
    	anBuilder.addRow();
    	anBuilder.addValue("Animal ID").addValue("Name").addValue("Species").addValue("Enclosure ID").addValue("Enclosure Name").addValue("Open");
    	
    	finalBuilder.addRow();
    	
		try {
			userQuery = Session.conn.prepareStatement(
					   "SELECT username, first_name, last_name, email, birthday, salary, active, admin "
					   + "FROM employee WHERE username = ?");
			userQuery.setString(1, username);
			
			
			userResult = userQuery.executeQuery();
			if(userResult.next())  {
				empBuilder.addRow();
				for(int i = 1; i <= 8; i++) {
					empBuilder.addValue(userResult.getString(i));
				}		
				
				
				
				
				animalQuery = Session.conn.prepareStatement(
						"SELECT a.animal_id, a.name, a.species_name, e.enclosure_id, e.name, e.open "
						+ "FROM animal a JOIN employee_training t USING (species_name) JOIN employee u USING (username) JOIN species s USING (species_name) JOIN enclosure e USING (enclosure_id) "
						+ "WHERE u.username = ?");
				animalQuery.setString(1, username);
				animalResult = animalQuery.executeQuery();
				while(animalResult.next()) {
					anBuilder.addRow();
					for(int i = 1; i <= 6; i++) {
						anBuilder.addValue(animalResult.getString(i));
					}
				}
				
				TableBuilder table1 = new TableBuilder(empBuilder.build().transpose());
				table1.addHeaderAndVerticalsBorders(BorderStyle.oldschool);
				TableBuilder table2 = new TableBuilder(anBuilder.build());
				table2.addHeaderAndVerticalsBorders(BorderStyle.oldschool);
				finalBuilder.addValue("Employee: \n" + table1.build().render(Session.terminalWidth/3));
				finalBuilder.addValue("          ");
				finalBuilder.addValue("Animals: \n" + table2.build().render(Session.terminalWidth*2/3));
				TableBuilder table = new TableBuilder(finalBuilder.build());
				System.out.println(table.build().render(Session.terminalWidth));
				
				
			} else 
				System.out.println("No employee found...");
			
		} catch (Exception e) {
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
				Session.log.info("DB Closing Error: " + e.toString());
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
		
		try {
			userQuery = Session.conn.prepareStatement(
					   "SELECT username "
					   + "FROM employee WHERE username = ?");
			userQuery.setString(1, username);
			userResult = userQuery.executeQuery();
			if(userResult.next()) 
				return true;
			else 
				return false;
			
		} catch (Exception e) {
			Session.log.info("SQL Error: " + e.toString());
			System.out.println("Something went wrong!");
		} finally {
			//close everything
			try {
				userResult.close();
				userQuery.close();
			}catch(Exception e) {
				//If closing errors out
				Session.log.info("DB Closing Error: " + e.toString());
			}
		}
		return false;
    }
    
    /**
     * Update all the values of a user
     * @param oldUsername Username of employee to update
     * @param newValues Array of all new values starting with username
     */
    public static void updateEmployee(String oldUsername, String[] newValues) {
    	if(!employeeExists(oldUsername)) {
    		System.out.println("Employee " + oldUsername + " doesn't exist!");
    		return;
    	}
    	
    	PreparedStatement query = null;
    	
    	try {
			
			query = Session.conn.prepareStatement(
					   "UPDATE employee "
					   + "SET username = ?, first_name = ?, last_name = ?, birthday = ?, "
					   + "email = ?, salary = ?, active = ?, admin = ? "  
					   + "WHERE username = ?");
				
			for(int i = 1; i <= 8; i++) {
				query.setString(i, newValues[i-1]);
			}
			query.setString(9, oldUsername);
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
    
    /**
     * Update the given attribute of the given employee
     * @param username Employee to update
     * @param attribute Which attribute of User to update
     * @param value The new value of the attribute 
     */
    public static void updateEmployee(String username, String attribute, String value) {
    	if(!employeeExists(username)) {
    		System.out.println("Employee " + username + " doesn't exist!");
    		return;
    	}
    	
    	PreparedStatement query = null;
		
		try {
			
			query = Session.conn.prepareStatement(
					   "UPDATE employee SET " + attribute + " = ? WHERE username = ?");
				
			query.setString(1, value);
			query.setString(2, username);
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
    
    /**
     * Add new employee
     * @param newValues The array of values starting with username and ending with admin
     */
    public static void addEmployee(String[] newValues) {
    	if(employeeExists(newValues[0])) {
    		System.out.println("Employee " + newValues[0] + " already exists!");
    		return;
    	}
    	
    	PreparedStatement query = null;
    	
    	try {
			
			query = Session.conn.prepareStatement(
					   "INSERT INTO employee VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
				
			for(int i = 1; i <= 9; i++) {
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
