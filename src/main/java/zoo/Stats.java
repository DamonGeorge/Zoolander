package zoo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.NumberFormat;

import org.springframework.shell.table.BorderStyle;
import org.springframework.shell.table.TableBuilder;
import org.springframework.shell.table.TableModelBuilder;

/**
 * Stat functions
 * Employees: trainings to renew, salary stats (max, min, average, monthly)
 * Animals: # of species, # of animals per species, 
 * Enclosures: # of animals, # enclosures, open vs. closed
 * Food: quantities, ones that need to be purchased, ...
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
		TableModelBuilder<String> trainingBuilder = new TableModelBuilder<>();
		TableModelBuilder<String> salaryBuilder = new TableModelBuilder<>();
		TableModelBuilder<String> finalBuilder = new TableModelBuilder<>();
		trainingBuilder.addRow().addValue("First Name").addValue("Last Name").addValue("Trainings");
		NumberFormat moneyFormat = NumberFormat.getCurrencyInstance();
				
		try {
			trainingQuery = Session.conn.prepareStatement(
					   "SELECT e.first_name, e.last_name, COUNT(*) "
					   + "FROM employee e JOIN employee_training t USING (username) "
					   + "GROUP BY e.username, e.first_name, e.last_name");
			trainingResult = trainingQuery.executeQuery();
			
			salaryQuery = Session.conn.prepareStatement(
					   "SELECT MAX(salary), MIN(salary), AVG(salary), SUM(salary) "
					   + "FROM employee ");
			salaryResult = salaryQuery.executeQuery();
			
			
			while(trainingResult.next()) {
				trainingBuilder.addRow();
				for(int i = 1; i <=3; i++ ) {
					trainingBuilder.addValue(trainingResult.getString(i));
				}
			}
			salaryResult.next();
			salaryBuilder.addRow().addValue("Max: ").addValue(moneyFormat.format(salaryResult.getDouble(1)));
			salaryBuilder.addRow().addValue("Min: ").addValue(moneyFormat.format(salaryResult.getDouble(2)));
			salaryBuilder.addRow().addValue("Avg: ").addValue(moneyFormat.format(salaryResult.getDouble(3)));
			salaryBuilder.addRow().addValue("Total Yearly: ").addValue(moneyFormat.format(salaryResult.getDouble(4)));
			salaryBuilder.addRow().addValue("Total Monthly: ").addValue(moneyFormat.format(salaryResult.getDouble(4) / 12));
			
			TableBuilder trainingTable = new TableBuilder(trainingBuilder.build());
			trainingTable.addHeaderAndVerticalsBorders(BorderStyle.oldschool);
			TableBuilder salaryTable = new TableBuilder(salaryBuilder.build());
			salaryTable.addInnerBorder(BorderStyle.air);

			finalBuilder.addRow().addValue("Employee Trainings: \n" + trainingTable.build().render(Session.terminalWidth/2));
			finalBuilder.addValue("          ");
			finalBuilder.addValue("Salary Stats: \n" + salaryTable.build().render(Session.terminalWidth/2));
			TableBuilder finalTable = new TableBuilder(finalBuilder.build());
			System.out.println(finalTable.build().render(Session.terminalWidth));
			
			
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
		TableModelBuilder<String> speciesBuilder = new TableModelBuilder<>();
		TableModelBuilder<String> animalBuilder = new TableModelBuilder<>();
		TableModelBuilder<String> finalBuilder = new TableModelBuilder<>();
		animalBuilder.addRow().addValue("Species").addValue("Common Name").addValue("Animals");
				
		try {
			speciesQuery = Session.conn.prepareStatement(
					   "SELECT COUNT(*) "
					   + "FROM species");
			speciesResult = speciesQuery.executeQuery();
			
			animalQuery = Session.conn.prepareStatement(
					   "SELECT s.species_name, s.common_name, COUNT(*) "
					   + "FROM species s JOIN animal a USING (species_name) "
					   + "GROUP BY s.species_name, s.common_name ");
			animalResult = animalQuery.executeQuery();
			
			
			while(animalResult.next()) {
				animalBuilder.addRow();
				for(int i = 1; i <=3; i++ ) {
					animalBuilder.addValue(animalResult.getString(i));
				}
			}
			if(speciesResult.next()) {
				speciesBuilder.addRow().addValue("# of Species: ").addValue(speciesResult.getString(1));
			}
			TableBuilder animalTable = new TableBuilder(animalBuilder.build());
			animalTable.addHeaderAndVerticalsBorders(BorderStyle.oldschool);
			TableBuilder speciesTable = new TableBuilder(speciesBuilder.build());
			speciesTable.addInnerBorder(BorderStyle.air);

			finalBuilder.addRow().addValue("Animals Per Species: \n" + animalTable.build().render(Session.terminalWidth/2));
			finalBuilder.addValue("          ");
			finalBuilder.addValue(speciesTable.build().render(Session.terminalWidth/2));
			TableBuilder finalTable = new TableBuilder(finalBuilder.build());
			System.out.println(finalTable.build().render(Session.terminalWidth));
			
			
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
