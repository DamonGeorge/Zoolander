package zoo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.springframework.shell.table.BorderStyle;
import org.springframework.shell.table.TableBuilder;

public class Food {
	
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
	
	
}


	
