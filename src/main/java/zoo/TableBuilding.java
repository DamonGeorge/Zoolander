package zoo;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.springframework.shell.table.TableBuilder;
import org.springframework.shell.table.TableModelBuilder;

public class TableBuilding {
	public static TableBuilder getAsciiTable(ResultSet result, boolean transpose) throws SQLException {
		TableModelBuilder<String> builder = new TableModelBuilder<>();
		
		ResultSetMetaData rsmd = result.getMetaData();
		
		builder.addRow();
		
		for(int i=1; i<=rsmd.getColumnCount(); i++){
			builder.addValue(rsmd.getColumnLabel(i).replaceAll("_", " "));
		}
		
		result.beforeFirst();
		while(result.next()) {
			builder.addRow();
			for(int i=1; i<6; i++){
				builder.addValue(result.getString(i));
			}
		}
		if(transpose)	
			return new TableBuilder(builder.build().transpose());
		else 
			return new TableBuilder(builder.build());
	}
}
