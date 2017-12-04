package zoo;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.text.NumberFormat;
import java.util.ArrayList;

import org.springframework.shell.table.BorderStyle;
import org.springframework.shell.table.TableBuilder;
import org.springframework.shell.table.TableModelBuilder;

public class TableBuilding {
	public static TableBuilder getAsciiTable(ResultSet result, boolean transpose) throws SQLException {
		return getAsciiTable(result, transpose, null);
	}
	
	public static TableBuilder getAsciiTable(ResultSet result, boolean transpose, int[] columns) throws SQLException  {
		TableModelBuilder<String> builder = new TableModelBuilder<>();
		ResultSetMetaData rsmd = result.getMetaData();
		NumberFormat moneyFormat = NumberFormat.getCurrencyInstance();
		int columnNumber = rsmd.getColumnCount();
		ArrayList<Integer> bools = new ArrayList<Integer>();
		ArrayList<Integer> doubles = new ArrayList<Integer>();
		result.beforeFirst();
		builder.addRow();
		
		
		
		if(columns == null || columns.length == 0) {
			
			for(int i=1; i<=columnNumber; i++){
				builder.addValue(fixColumnTitle(rsmd.getColumnLabel(i)));
				if(rsmd.getColumnType(i) == Types.BIT) {
					bools.add(i);
				} else if (rsmd.getColumnType(i) == Types.DOUBLE) {
					doubles.add(i);
				}
			}
			
			while(result.next()) {
				builder.addRow();
				for(int i=1; i<=columnNumber; i++){
					if(bools.contains(i)) 
						builder.addValue(result.getBoolean(4) ? "yes" : "no");
					else if (doubles.contains(i))
						builder.addValue(moneyFormat.format(result.getDouble(i)).substring(1));
					else
						builder.addValue(result.getString(i));
				}
			}
		} else {
			for(int i : columns) {
				builder.addValue(fixColumnTitle(rsmd.getColumnLabel(i)));
				if(rsmd.getColumnType(i) == Types.BIT) {
					bools.add(i);
				}
			}
			while(result.next()) {
				builder.addRow();
				for(int i : columns) {
					if(bools.contains(i)) 
						builder.addValue(result.getBoolean(4) ? "yes" : "no");
					else
						builder.addValue(result.getString(i));
				}
			}
		}
		
		
		if(transpose)	
			return new TableBuilder(builder.build().transpose());
		else 
			return new TableBuilder(builder.build());
	}
	
	public static void printBasicTable(ResultSet result) throws SQLException {
		printBasicTable(getAsciiTable(result, false));
	}
	
	public static void printBasicTable(TableBuilder table) {
		table.addHeaderAndVerticalsBorders(BorderStyle.oldschool);
		System.out.println(table.build().render(Session.terminalWidth));
	}
	
	public static void printDoubleTable(TableBuilder leftTable, String leftTitle, TableBuilder rightTable, String rightTitle, int leftWidth) {
		TableModelBuilder<String> builder = new TableModelBuilder<>();
		builder.addRow();
		builder.addValue(leftTitle + "\n" + leftTable.build().render(leftWidth));
		builder.addValue("          ");
		builder.addValue(rightTitle + "\n" + rightTable.build().render(Session.terminalWidth - leftWidth - 10));
		
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
