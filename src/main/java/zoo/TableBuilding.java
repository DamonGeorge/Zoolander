package zoo;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.springframework.shell.table.BorderStyle;
import org.springframework.shell.table.TableBuilder;
import org.springframework.shell.table.TableModelBuilder;

public class TableBuilding {
	public static void printAsciiTable(ResultSet result) throws SQLException {
		TableModelBuilder<String> builder = new TableModelBuilder();
		
		ResultSetMetaData rsmd = result.getMetaData();
		
		builder.addRow();
		
		for(int i=1; i<=rsmd.getColumnCount(); i++){
			builder.addValue(rsmd.getColumnName(i));
		}
		
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
