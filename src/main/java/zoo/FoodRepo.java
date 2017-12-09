package zoo;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.shell.table.BorderStyle;
import org.springframework.shell.table.TableBuilder;

public class FoodRepo {
	
	public static void listAllFood() {
		PreparedStatement listQuery = null;
		ResultSet result = null;
		
		try  {
			listQuery = Session.conn.prepareStatement(
					"SELECT food_id AS Food_ID, name AS Name, brand AS Brand, quantity AS Pounds_In_Storage, cost AS Cost_Per_Pound "
					+ "FROM food");
			result = listQuery.executeQuery();
			
			TableBuilder table = AsciiTableHelper.getAsciiTable(result, false);
			table.addHeaderAndVerticalsBorders(BorderStyle.oldschool);
			System.out.println(table.build().render(Session.terminalWidth));
			
		} catch (Exception e) {
			Session.log.warning("SQL Error" + e.toString());
			System.out.println("Never have I had so little hair!");
		} finally {
			try {
				result.close();
				listQuery.close();
			} catch(Exception e) {
				Session.log.warning("DB Closing Error: " + e.toString());
			}
		}
	}
	
	public static boolean foodExists(int id){
		PreparedStatement query = null;
		ResultSet result = null;
		boolean exists = false;
		
		try {
			query = Session.conn.prepareStatement(
					"SELECT food_id "
					+ "FROM food WHERE food_id=?");
			query.setInt(1, id);
			result = query.executeQuery();
			
			if(result.next())
				exists = true;
			
		} catch (Exception e) {
			Session.log.warning("SQL Error: " + e.toString());
			System.out.println("Something went wrong!");
		} finally {
			//close everything
			try {
				result.close();
				query.close();
			}catch(Exception e) {
				//If closing errors out
				Session.log.warning("DB Closing Error: " + e.toString());
			}
		}
		return exists;
	}
	
	public static void feedAnimal(int food_id, int animal_id)
	{
		PreparedStatement statement = null;
		ResultSet result = null;
		
		try
		{
			//first check for admin privileges
			if(!Session.admin){
				statement = Session.conn.prepareStatement(
						"SELECT et.date_trained, et.years_to_renew"
						+ "FROM animal a JOIN species s USING(species_name) JOIN employee_training et USING(species_name) "
						+ "WHERE et.username=? AND a.animal_id=?");	
				statement.setString(1, Session.currentUser);
				statement.setInt(2, animal_id);
				result = statement.executeQuery();
				
				//check if employee is trained to handle that species or
				//if training is still valid
				if(!result.next() || !isTrainingValid(result.getDate(1), result.getInt(2))){
					//throw some error, employee can't feed this animal
					throw new SQLException("Employee not trained to feed this species.");
				}
				statement.close();	statement = null;
				result.close(); 	result = null;
			}
			statement = Session.conn.prepareStatement(
						"UPDATE f, a "
						+ "SET a.last_feeding=?, f.quantity=("
						+ "		SELECT f1.quantity-(a1.food_quantity/f1.nutritional_index) "
						+ "		FROM food f1 NATURAL JOIN species_eats se NATURAL JOIN animal a1 "
						+ "		WHERE f.food_id=? AND a.animal_id=?) "
						+ "FROM food f, animal a "
						+ "WHERE f.food_id=? AND a.animal_id=?"
						);
			statement.setDate(1, new Date(System.currentTimeMillis()));
			statement.setInt(2, food_id);
			statement.setInt(3, animal_id);
			statement.setInt(4, food_id);
			statement.setInt(5, animal_id);
			
			if(!statement.execute())
				throw new SQLException("Invalid food for species type.");
		} 
		catch (Exception e)
		{
			Session.log.warning("SQL Error: " + e.toString());
			System.out.println("Something went wrong!");
		} finally { //close everything
			try {
				if(result!=null) 	result.close();
				if(statement!=null) statement.close();
			}catch(Exception e) { //If closing errors out
				Session.log.warning("DB Closing Error: " + e.toString());
			}
		}
	}
	
	//TODO:: implement Calendar class to remove deprecated method calls
	@SuppressWarnings("deprecation")
	private static boolean isTrainingValid(Date date, int years_valid)
	{
		Date curr = new Date(System.currentTimeMillis());
		Date exDate = (Date) date.clone();
		//Calendar.set(Calendar.YEAR, Calendar.get(Calendar.YEAR) + years_valid)).newDateCalendar.get(Calendar.YEAR) - 1900.
		exDate.setYear(exDate.getYear()+years_valid);
		
		return exDate.after(curr);
	}
}


	
