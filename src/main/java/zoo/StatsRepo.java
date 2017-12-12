package zoo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.NumberFormat;

import org.springframework.shell.table.BorderStyle;
import org.springframework.shell.table.TableBuilder;

/**
 * Stat functions
 * Employees: trainings to renew, salary stats (max, min, average, monthly)
 * Animals: # of species, # of animals per species, 
 * Enclosures: # of animals, # enclosures, open vs. closed
 * Food: quantities, ones that need to be purchased
 *
 * @author damongeorge
 *
 */
public class StatsRepo {

	/**
	 * Displays the max, min, average, yearly and monthly salaries
	 */
	public static void salaryStats() {
		PreparedStatement maxQuery = null, minQuery = null, avgQuery = null;
		ResultSet maxResult = null, minResult = null, avgResult = null;
		
		//Currency formatter for output
		NumberFormat moneyFormat = NumberFormat.getCurrencyInstance();
		
		try {
			//Get all 3 queries
			maxQuery = Session.conn.prepareStatement(
					   "SELECT e.first_name, e.last_name, salary "
					   + "FROM employee e, (SELECT MAX(salary) AS max FROM employee) AS sub "
					   + "WHERE e.salary = sub.max");
			 
			minQuery = Session.conn.prepareStatement(
					   "SELECT e.first_name, e.last_name, e.salary "
					   + "FROM employee e, (SELECT MIN(salary) AS min FROM employee) AS sub "
					   + "WHERE e.salary = sub.min");
			
			avgQuery = Session.conn.prepareStatement(
					   "SELECT AVG(salary), SUM(salary), SUM(salary)/12 "
					   + "FROM employee");
			
			//execute the queries
			maxResult = maxQuery.executeQuery();
			minResult = minQuery.executeQuery();
			avgResult = avgQuery.executeQuery();
			
			System.out.println("Salary Stats: ");
			if (maxResult.next()) { //print the max salary and who it belongs to
				System.out.printf ("Max..........:  %s  (", 
									moneyFormat.format(maxResult.getDouble(3)));
				maxResult.beforeFirst();
				
				while(maxResult.next()) {//Print all employees with max salary
					System.out.printf("%s %s, ", 
							maxResult.getString(1), 
							maxResult.getString(2));
				}
				System.out.println("\b\b) "); 
			}
			
			if (minResult.next()) { //print the min salary and who it belongs to
				System.out.printf ("Min..........:  %s  (", 
									moneyFormat.format(minResult.getDouble(3)));
				minResult.beforeFirst();
				
				while(minResult.next()) { //Print all employees with min salary
					System.out.printf("%s %s, ", 
							minResult.getString(1), 
							minResult.getString(2));
				}
				System.out.println("\b\b) "); 
			}
			
			if (avgResult.next()) { //Print out other stats
				System.out.println("Avg..........:  " + moneyFormat.format(avgResult.getDouble(1)));
				System.out.println("Yearly Total.:  " + moneyFormat.format(avgResult.getDouble(2)));
				System.out.println("Monthly Total:  " + moneyFormat.format(avgResult.getDouble(3)));
			}
			System.out.println();
			
		} catch (Exception e) {
			Session.log.warning("SQL Error: " + e.toString());
			System.out.println("Something went wrong!");
		} finally { //close everything
			try {
				maxResult.close();
				minResult.close();
				avgResult.close();
				maxQuery.close();
				minQuery.close();
				avgQuery.close();
			}catch(Exception e) { //If closing errors out
				Session.log.warning("DB Closing Error: " + e.toString());
			}
		}	
	}
	
	/**
	 * List all employees with the number of trainings they have
	 */
	public static void handlerStats() {
		PreparedStatement trainingQuery = null;
		ResultSet trainingResult = null;
				
		try {
			trainingQuery = Session.conn.prepareStatement(
					   "SELECT e.first_name, e.last_name, COUNT(*) AS Trainings "
					   + "FROM employee e JOIN employee_training t USING (username) "
					   + "GROUP BY e.username, e.first_name, e.last_name "
					   + "ORDER BY Trainings DESC ");
			
			trainingResult = trainingQuery.executeQuery();
			
			if(trainingResult.next()) 
				AsciiTableHelper.printBasicTable(trainingResult);
			
		} catch (Exception e) {
			Session.log.warning("SQL Error: " + e.toString());
			System.out.println("Something went wrong!");
		} finally { //close everything
			try {
				trainingResult.close();
				trainingQuery.close();
			}catch(Exception e) { //If closing errors out
				Session.log.warning("DB Closing Error: " + e.toString());
			}
		}	
	}
	
	
	/**
	 * total species
	 * species with # animals
	 * species with no animals
	 */
	public static void speciesStats() {
		PreparedStatement totalSpeciesQuery = null, animalQuery = null, noAnimalQuery = null;
		ResultSet totalSpeciesResult = null, animalResult = null, noAnimalResult = null;
				
		try {
			totalSpeciesQuery = Session.conn.prepareStatement(
					   "SELECT COUNT(*) "
					   + "FROM species");
			
			
			animalQuery = Session.conn.prepareStatement(
					   "SELECT s.species_name, s.common_name, COUNT(*) AS Animals "
					   + "FROM species s JOIN animal a USING (species_name) "
					   + "GROUP BY s.species_name, s.common_name "
					   + "ORDER BY Animals DESC");
			
			noAnimalQuery  = Session.conn.prepareStatement(
						"SELECT s.species_name, s.common_name "
						+ "FROM species s LEFT JOIN animal a USING (species_name) "
						+ "WHERE a.animal_id IS NULL ");
					
			
			totalSpeciesResult = totalSpeciesQuery.executeQuery();
			animalResult = animalQuery.executeQuery();
			noAnimalResult = noAnimalQuery.executeQuery();
			
			totalSpeciesResult.next();
			System.out.println("Total Species: " + totalSpeciesResult.getString(1) + "\n");
			
			
			TableBuilder table1 = AsciiTableHelper.getAsciiTable(animalResult, false);
			TableBuilder table2 = AsciiTableHelper.getAsciiTable(noAnimalResult, false);
			table1.addHeaderAndVerticalsBorders(BorderStyle.oldschool);
			table2.addHeaderAndVerticalsBorders(BorderStyle.oldschool);
			
			AsciiTableHelper.printDoubleTable(table1, "Animals Per Species: ", table2, "Species Without Animals: ", Session.terminalWidth *2/3);
		
			
		} catch (Exception e) {
			Session.log.warning("SQL Error: " + e.toString());
			System.out.println("Something went wrong!");
		} finally { //close everything
			try {
				totalSpeciesResult.close();
				animalResult.close();
				noAnimalResult.close();
				totalSpeciesQuery.close();
				animalQuery.close();
				noAnimalQuery.close();
			}catch(Exception e) { //If closing errors out
				Session.log.warning("DB Closing Error: " + e.toString());
			}
		}	
	}
	
	/**
	 * List the number of animals in each enclosure and
	 * list the enclosures that are closed
	 */
	public static void enclosureStats() {
		PreparedStatement animalQuery = null, closedQuery = null;
		ResultSet animalResult = null, closedResult = null;
				
		try {
			animalQuery = Session.conn.prepareStatement(
					   "SELECT e.enclosure_ID AS ID, e.name, COUNT(*) AS Animals "
					   + "FROM enclosure e JOIN species s USING (enclosure_id) JOIN animal a USING (species_name) "
					   + "GROUP BY ID, Name "
					   + "ORDER BY Animals ");
			
			closedQuery  = Session.conn.prepareStatement(
						"SELECT e.enclosure_ID AS ID, e.name "
						+ "FROM enclosure e "
						+ "WHERE e.open = false ");
					
			
			animalResult = animalQuery.executeQuery();
			closedResult = closedQuery.executeQuery();
			
			
			TableBuilder table1 = AsciiTableHelper.getAsciiTable(animalResult, false);
			TableBuilder table2 = AsciiTableHelper.getAsciiTable(closedResult, false);
			table1.addHeaderAndVerticalsBorders(BorderStyle.oldschool);
			table2.addHeaderAndVerticalsBorders(BorderStyle.oldschool);
			
			AsciiTableHelper.printDoubleTable(table1, "Animals Per Enclosure: ", table2, "Closed Enclosures: ", Session.terminalWidth *2/3);
		
			
		} catch (Exception e) {
			Session.log.warning("SQL Error: " + e.toString());
			System.out.println("Something went wrong!");
		} finally { //close everything
			try {
				animalResult.close();
				closedResult.close();
				animalQuery.close();
				closedQuery.close();
			}catch(Exception e) { //If closing errors out
				Session.log.warning("DB Closing Error: " + e.toString());
			}
		}	
	}
	
	
	/**
	 * 1. Shows stats for foods that feed the most number of animals
	 * 2. Displays food which currently is low in stock (<=100 pounds)
	 */
	public static void foodStats() {
		PreparedStatement lowFoodStmt = null;
		PreparedStatement mostStmt = null;
		ResultSet lowFoodRs = null;
		ResultSet mostRs = null;

		try{
			mostStmt = Session.conn.prepareStatement(
							"SELECT se.food_id AS ID, f.name AS Name, COUNT(*) AS Animals "
							+ "FROM food f NATURAL JOIN species_eats se JOIN animal a USING(species_name) "
							+ "GROUP BY se.food_id, f.name "
							+ "HAVING Animals >= ALL(SELECT COUNT(*) "
							+ "							FROM species_eats se1 JOIN animal a1 USING(species_name) "
							+ "							GROUP BY se1.food_id)");
			mostRs = mostStmt.executeQuery();
			
			lowFoodStmt = Session.conn.prepareStatement(
					"SELECT food_id, name, brand, quantity "
					+ "FROM food "
					+ "WHERE quantity <= 100");
			lowFoodRs = lowFoodStmt.executeQuery();
			
			
	
			System.out.println("Food(s) that feed the most animals: ");
			AsciiTableHelper.printBasicTable(mostRs);
			System.out.println("Food(s) that are running low: ");
			if(!lowFoodRs.next())
				System.out.println("All food is adequately stocked! ");
			else {
				lowFoodRs.beforeFirst();
				AsciiTableHelper.printBasicTable(lowFoodRs);
			}
			//AsciiTableHelper.printDoubleTable(table1, "Food(s) that Feed the Most Animals: ", table2, "Food Stores Running Low: ", Session.terminalWidth *2/3);
			
		} catch (Exception e) {
			Session.log.warning("SQL Error: " + e.toString());
			System.out.println("Something went wrong: " + e.getMessage());
		} finally {
			//close everything
			try {
				if(lowFoodStmt!=null) 	lowFoodStmt.close();
				if(lowFoodRs!=null)		lowFoodRs.close();
				if(mostStmt!=null) 		mostStmt.close();
				if(mostRs!=null)		mostRs.close();
			}catch(Exception e) {
				//If closing errors out
				Session.log.warning("DB Closing Error: " + e.toString());
			}
		}
	}
	
}
