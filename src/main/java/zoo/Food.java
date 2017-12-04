package zoo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.springframework.shell.table.ArrayTableModel;
import org.springframework.shell.table.BorderStyle;
import org.springframework.shell.table.TableBuilder;
import org.springframework.shell.table.TableModelBuilder;

public class Food {
	
	public static void listAllFood() {
		PreparedStatement listQuery = null;
		ResultSet result = null;
		
		try  {
			listQuery = Session.conn.prepareStatement(
					"SELECT food_id AS Food_ID, name AS Name, brand AS Brand, quantity AS Pounds_In_Storage, cost AS Cost_Per_Pound "
					+ "FROM food");
			result = listQuery.executeQuery();
			
			TableBuilder table = TableBuilding.getAsciiTable(result, false);
			table.addHeaderAndVerticalsBorders(BorderStyle.oldschool);
			System.out.println(table.build().render(Session.terminalWidth));
			
		} catch (Exception e) {
			Session.log.info("SQL Error" + e.toString());
			System.out.println("Never have I had so little hair!");
		} finally {
			try {
				result.close();
				listQuery.close();
			} catch(Exception e) {
				Session.log.info("DB Closing Error: " + e.toString());
			}
		}
	}
	
	public static void printFoodAsciiTable(ResultSet result) throws SQLException {
		TableModelBuilder<String> builder = new TableModelBuilder();
		
		ResultSetMetaData rsmd = result.getMetaData();
		
		
		builder.addRow();
		
		for(int i=1; i<=rsmd.getColumnCount(); i++){
			builder.addValue(rsmd.getColumnName(i));
		}
		
		
		//builder.addValue("Food Id");
		//builder.addValue("Name");
		//builder.addValue("Brand");
		//builder.addValue("Pounds");
		//builder.addValue("Cost/Pound");
		
//		try {
			result.beforeFirst();
			while(result.next()) {
				builder.addRow();
				for(int i=1; i<6; i++){
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


	
