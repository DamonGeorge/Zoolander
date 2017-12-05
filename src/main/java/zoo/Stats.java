package zoo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
public class Stats {

	/**
	 * TODO: trainings to renew??, get names of max, min, etc. - subquery
	 */
	public static void employeeStats() {
		PreparedStatement trainingQuery = null, salaryQuery = null;
		ResultSet trainingResult = null, salaryResult = null;
				
		try {
			trainingQuery = Session.conn.prepareStatement(
					   "SELECT e.first_name, e.last_name, COUNT(*) AS Trainings "
					   + "FROM employee e JOIN employee_training t USING (username) "
					   + "GROUP BY e.username, e.first_name, e.last_name "
					   + "ORDER BY Trainings DESC");
			trainingResult = trainingQuery.executeQuery();
			
			salaryQuery = Session.conn.prepareStatement(
					   "SELECT MAX(salary) AS Max, MIN(salary) AS Min, AVG(salary) AS AVG, SUM(salary) AS Yearly_Total, SUM(Salary)/12 AS Monthly_Total "
					   + "FROM employee ");
			salaryResult = salaryQuery.executeQuery();
			
			
			TableBuilder table1 = TableBuilding.getAsciiTable(trainingResult, false);
			TableBuilder table2 = TableBuilding.getAsciiTable(salaryResult, true);
			table1.addHeaderAndVerticalsBorders(BorderStyle.oldschool);
			table2.addInnerBorder(BorderStyle.air);
			
			TableBuilding.printDoubleTable(table1, "Trainings: ", table2, "Salary Stats: ", Session.terminalWidth * 2/3);

			
		} catch (Exception e) {
			Session.log.info("SQL Error: " + e.toString());
			System.out.println("Something went wrong!");
		} finally {
			//close everything
			try {
				trainingResult.close();
				salaryResult.close();
				trainingQuery.close();
				salaryQuery.close();
			}catch(Exception e) {
				//If closing errors out
				Session.log.info("DB Closing Error: " + e.toString());
			}
		}	
	}
	
	
	/**
	 * TODO: other stats???
	 */
	public static void animalStats() {
		PreparedStatement speciesQuery = null, animalQuery = null;
		ResultSet speciesResult = null, animalResult = null;
				
		try {
			speciesQuery = Session.conn.prepareStatement(
					   "SELECT COUNT(*) AS Total_Species "
					   + "FROM species");
			speciesResult = speciesQuery.executeQuery();
			
			animalQuery = Session.conn.prepareStatement(
					   "SELECT s.species_name, s.common_name, COUNT(*) AS Animals "
					   + "FROM species s JOIN animal a USING (species_name) "
					   + "GROUP BY s.species_name, s.common_name "
					   + "ORDER BY Animals ");
			animalResult = animalQuery.executeQuery();
			
			
			TableBuilder table1 = TableBuilding.getAsciiTable(animalResult, false);
			TableBuilder table2 = TableBuilding.getAsciiTable(speciesResult, true);
			table1.addHeaderAndVerticalsBorders(BorderStyle.oldschool);
			table2.addInnerBorder(BorderStyle.air);
			
			TableBuilding.printDoubleTable(table1, "Animals Per Species: ", table2, "", Session.terminalWidth /2);
		
			
		} catch (Exception e) {
			Session.log.info("SQL Error: " + e.toString());
			System.out.println("Something went wrong!");
		} finally {
			//close everything
			try {
				speciesResult.close();
				animalResult.close();
				speciesQuery.close();
				animalQuery.close();
			}catch(Exception e) {
				//If closing errors out
				Session.log.info("DB Closing Error: " + e.toString());
			}
		}	
	}
	
}
