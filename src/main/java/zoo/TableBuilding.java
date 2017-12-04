package zoo;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.springframework.shell.table.BorderStyle;
import org.springframework.shell.table.TableBuilder;
import org.springframework.shell.table.TableModelBuilder;

public class TableBuilding {
	public static TableBuilder getAsciiTable(ResultSet result, boolean transpose) throws SQLException {
		TableModelBuilder<String> builder = new TableModelBuilder<>();
		
		ResultSetMetaData rsmd = result.getMetaData();
		int columns = rsmd.getColumnCount();
		System.out.println(columns);
		builder.addRow();
		
		for(int i=1; i<=columns; i++){
			builder.addValue(fixColumnTitle(rsmd.getColumnLabel(i)));
		}
		
		result.beforeFirst();
		while(result.next()) {
			builder.addRow();
			for(int i=1; i<=columns; i++){
				builder.addValue(result.getString(i));
			}
		}
		if(transpose)	
			return new TableBuilder(builder.build().transpose());
		else 
			return new TableBuilder(builder.build());
	}
	
	public static void printBasicTable(TableBuilder table) {
		table.addHeaderAndVerticalsBorders(BorderStyle.oldschool);
		System.out.println(table.build().render(Session.terminalWidth));
	}
	
	public static void printDoubleTable(TableBuilder leftTable, String leftTitle, TableBuilder rightTable, String rightTitle, int leftWidth) {
		TableModelBuilder<String> builder = new TableModelBuilder<>();
		builder.addRow();
		builder.addValue(leftTitle + ": \n" + leftTable.build().render(leftWidth));
		builder.addValue("          ");
		builder.addValue(rightTitle + ": \n" + rightTable.build().render(Session.terminalWidth - leftWidth - 10));
		
		TableBuilder table = new TableBuilder(builder.build());
		System.out.println(table.build().render(Session.terminalWidth));
	}
	
	
	private static String fixColumnTitle(String label) {
		label = label.replaceAll("_", " ");
		StringBuilder b = new StringBuilder(label);
		int i = 0;
		do {
		  b.replace(i, i + 1, b.substring(i,i + 1).toUpperCase());
		  i =  b.indexOf(" ", i) + 1;
		} while (i > 0 && i < b.length());

		return b.toString();
	}
}
