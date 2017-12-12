package zoo;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.springframework.shell.table.BorderStyle;
import org.springframework.shell.table.TableBuilder;

public class FoodRepo {
	
	/**
	 * Displays all food in database
	 */
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
	
	/**
	 * Checks if a food_id exists in the context of the database
	 * @param id	food_id to check
	 * @return
	 */
	public static boolean foodExists(String id){
		PreparedStatement query = null;
		ResultSet result = null;
		boolean exists = false;
		
		try {
			query = Session.conn.prepareStatement(
					"SELECT food_id "
					+ "FROM food WHERE food_id=?");
			query.setString(1, id);
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
	
	/**
	 * Decrements food quantity given an animals nutritional requirement and a
	 * foods nutritional index
	 * @param animal_id	the animal to be fed
	 * @param food_id	the type of food to feed the animal
	 */
	public static void feedAnimal(String animal_id, String food_id)
	{
		PreparedStatement statement = null;
		ResultSet result = null;
		
		try
		{
			//first check for admin privileges
			if(!Session.admin){
				statement = Session.conn.prepareStatement(
						"SELECT et.date_trained, et.years_to_renew "
						+ "FROM animal a JOIN employee_training et USING(species_name) "
						+ "WHERE et.username=? AND a.animal_id=?");	
				statement.setString(1, Session.currentUser);
				statement.setString(2, animal_id);
				result = statement.executeQuery();
				
				//check if employee is trained to handle that species or
				//if training is still valid
				if(!result.next() || !isTrainingValid(result.getDate(1), result.getInt(2))){
					throw new Exception("Employee not trained to feed this species.");
				}
				statement.close();	statement = null;
				result.close(); 	result = null;
			}
			statement = Session.conn.prepareStatement(
					"SELECT f.quantity, a.food_quantity, f.nutritional_index "
					+ "FROM food f NATURAL JOIN species_eats se JOIN animal a USING(species_name) "
					+ "WHERE f.food_id=? AND a.animal_id=?");
			statement.setString(1, food_id);
			statement.setString(2, animal_id);
			result = statement.executeQuery();
			
			//Throw error for invalid food and species combination
			if(!result.next())
				throw new Exception("Connot feed this animal species with food_id=" + food_id);
			
			double newQuantity = result.getDouble(1)-(result.getDouble(2)/result.getDouble(3));
			
			//Throw error if feeding an animal requires more food than available
			if(newQuantity<0)
				throw new Exception("Not enough food to feed this animal!");
			
			
			statement = Session.conn.prepareStatement(
						"UPDATE food f, animal a "
						+ "SET f.quantity=?, a.last_feeding=? "
						+ "WHERE f.food_id=? AND a.animal_id=?"
						);
			statement.setDouble(1, newQuantity);
			statement.setDate(2, new Date(System.currentTimeMillis()));
			statement.setString(3, food_id);
			statement.setString(4, animal_id);
			statement.execute();
			
		} 
		catch (Exception e)
		{
			Session.log.warning("SQL Error: " + e.toString());
			System.out.println("Something went wrong: " + e.getMessage());
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
	/**
	 * Determines if an employee's training is still valid
	 * @param date	date that employee was trained
	 * @param years_valid	how many years the training is valid
	 * @return
	 */
	@SuppressWarnings("deprecation")
	private static boolean isTrainingValid(Date date, int years_valid)
	{
		Date currentDate = new Date(System.currentTimeMillis());
		Date expriationDate = (Date) date.clone();
		//Calendar.set(Calendar.YEAR, Calendar.get(Calendar.YEAR) + years_valid)).newDateCalendar.get(Calendar.YEAR) - 1900.
		expriationDate.setYear(expriationDate.getYear()+years_valid);
		
		return expriationDate.after(currentDate);
	}

	/**
	 * Increments food quantity in database 
	 * @param food_id food quantity to increment
	 * @param quantity what to increment quantity by
	 */
	public static void buyFood(String food_id, String quantity)
	{
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			//Only admin has capability of purchasing food
			if(!Session.admin)
				throw new Exception("Must be admin to purchase food.");
			
			stmt = Session.conn.prepareStatement(
					"SELECT cost "
					+ "FROM food "
					+ "WHERE food_id=?");
			stmt.setString(1, food_id);
			
			rs = stmt.executeQuery();
			
			//Throw error for invalid food_id
			if(!rs.next())
				throw new Exception("The following food_id does not exist: " + food_id);
			
			//TODO:: retrieve cost and calculate total cost for purchase
			//		currently no zoo funds
			
			stmt.close();
			stmt = Session.conn.prepareStatement(
					"UPDATE food "
					+ "SET quantity=quantity+? "
					+ "WHERE food_id=?");
			stmt.setString(1, quantity);
			stmt.setString(2, food_id);
			stmt.execute();
							
		}catch(Exception e){
			Session.log.warning("SQL Error: " + e.toString());
			System.out.println("Something went wrong: " + e.getMessage());
		} finally {
			//close everything
			try {
				if(stmt!=null) 	stmt.close();
				if(rs!=null)	rs.close();
			}catch(Exception e) {
				//If closing errors out
				Session.log.warning("DB Closing Error: " + e.toString());
			}
		}
		
	}

	/**
	 * Adds a new food to the database
	 * @param newFood array of length 6 containing valid DB insert info
	 */
	public static void addFood(String[] newFood)
	{
		PreparedStatement stmt = null;
		
		try{
			stmt = Session.conn.prepareStatement(
					"INSERT INTO food VALUES(?,?,?,?,?,?)");
			
			for(int i=1; i<=6; i++){
				stmt.setString(i, newFood[i-1]);
			}
			
			stmt.execute();
			
		} catch (Exception e) {
			Session.log.warning("SQL Error: " + e.toString());
			System.out.println("Something went wrong!");
		} finally { //close everything
			try {
				stmt.close();
			}catch(Exception e) { //If closing errors out
				Session.log.warning("DB Closing Error: " + e.toString());
			}
		}	
	}
}


	
