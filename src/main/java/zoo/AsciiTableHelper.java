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

/**
 * Provides helper functions for displaying Spring Ascii Tables from JDBC ResultSets
 * @author damongeorge
 * @author anthonyniehuser
 *
 */
public class AsciiTableHelper {
	
	/**
	 * Make a Table Builder using all the columns from the given result set
	 * @param result The result set to turn into a table with column headers
	 * @param transpose Whether to flip the table to have headers in the first column
	 * @return
	 * @throws SQLException
	 */
	public static TableBuilder getAsciiTable(ResultSet result, boolean transpose) throws SQLException {
		return getAsciiTable(result, transpose, null);
	}
	
	/**
	 * Make a Table Builder using the specified column numbers from the given result set
	 * @param result The result set to turn into a table with column headers
	 * @param transpose Whether to flip the table to have headers in the first column
	 * @param columns Which columns to include in the table (starting with 1)
	 * @return
	 * @throws SQLException
	 */
	public static TableBuilder getAsciiTable(ResultSet result, boolean transpose, int[] columns) throws SQLException  {
		TableModelBuilder<String> builder = new TableModelBuilder<>();
		//initialize result set and the builder
		result.beforeFirst();
		builder.addRow();
		
		if(!result.next()) { //If no results, return simple table with message
			builder.addValue("No Results Found...");
			return new TableBuilder(builder.build());
		}
		
		//Get the metadata in order to get the column data from the result set
		ResultSetMetaData rsmd = result.getMetaData();
		int columnNumber = rsmd.getColumnCount();
		
		//Number format for formatting doubles in the table output
		NumberFormat moneyFormat = NumberFormat.getCurrencyInstance();
		
		//Arrays for keeping track of boolean and double columns
		ArrayList<Integer> bools = new ArrayList<Integer>();
		ArrayList<Integer> doubles = new ArrayList<Integer>();
		
		result.beforeFirst(); //reset results
		
		if(columns == null || columns.length == 0) { //If no column data provide, use all columns
			for(int i=1; i<=columnNumber; i++){ //Loop through all columns
				builder.addValue(fixColumnTitle(rsmd.getColumnLabel(i))); //Add column title to headers
				
				//If column is a boolean or double, add its index to the corresponding list
				if(rsmd.getColumnType(i) == Types.BIT) {
					bools.add(i);
				} else if (rsmd.getColumnType(i) == Types.DOUBLE) {
					doubles.add(i);
				}
			}
			
			while(result.next()) { //Loop through the result, adding data from all columns
				builder.addRow();
				for(int i=1; i<=columnNumber; i++){
					if(bools.contains(i))  //Convert boolean to yes/no
						builder.addValue(result.getBoolean(4) ? "yes" : "no");
					else if (doubles.contains(i)) //print double with commas
						builder.addValue(moneyFormat.format(result.getDouble(i)).substring(1));
					else //else just print the data
						builder.addValue(result.getString(i)); 
				}
			}
		} else { //Otherwise if specific columns were provided, only use those
			for(int i : columns) { //Loop through provided columns, adding to the headers
				builder.addValue(fixColumnTitle(rsmd.getColumnLabel(i)));
				
				//If column is a boolean or double, add its index to the corresponding list
				if(rsmd.getColumnType(i) == Types.BIT) {
					bools.add(i);
				} else if (rsmd.getColumnType(i) == Types.DOUBLE) {
					doubles.add(i);
				}
			}
			while(result.next()) { //Loop through the result, adding data from specified columns
				builder.addRow();
				for(int i : columns) {
					if(bools.contains(i)) //Convert boolean to yes/no
						builder.addValue(result.getBoolean(4) ? "yes" : "no");
					else if (doubles.contains(i)) //print double with commas
						builder.addValue(moneyFormat.format(result.getDouble(i)).substring(1));
					else //else just print the data
						builder.addValue(result.getString(i));
				}
			}
		}
		
		//Return the finished Table Builder either regularly or transposed
		if(transpose)	
			return new TableBuilder(builder.build().transpose());
		else 
			return new TableBuilder(builder.build());
	}
	
	/**
	 * Prints a basic table from a result set using old school borders and headers
	 * @param result The result set to populate the table and headers
	 * @throws SQLException
	 */
	public static void printBasicTable(ResultSet result) throws SQLException {
		printBasicTable(getAsciiTable(result, false));
	}
	
	/**
	 * Prints a basic table from a Table Builder using old school borders and headers
	 * @param table The table builder used to print the table
	 */
	public static void printBasicTable(TableBuilder table) {
		table.addHeaderAndVerticalsBorders(BorderStyle.oldschool);
		System.out.println(table.build().render(Session.terminalWidth));
	}
	
	/**
	 * Used to print 2 tables side by side in the console with their own titles	
	 * @param leftTable
	 * @param leftTitle
	 * @param rightTable
	 * @param rightTitle
	 * @param leftWidth
	 */
	public static void printDoubleTable(TableBuilder leftTable, String leftTitle, TableBuilder rightTable, String rightTitle, int leftWidth) {
		TableModelBuilder<String> builder = new TableModelBuilder<>();
		builder.addRow();
		
		//Add left title and table to the first cell in the table
		builder.addValue(leftTitle + "\n" + leftTable.build().render(leftWidth));
		
		//Padding between the two tables
		builder.addValue("          ");
		
		//Add right title and table to the last cell in the table
		builder.addValue(rightTitle + "\n" + rightTable.build().render(Session.terminalWidth - leftWidth - 10));
		
		//Build and print the final table
		TableBuilder table = new TableBuilder(builder.build());
		System.out.println(table.build().render(Session.terminalWidth));
	}
	
	/**
	 * Turns column labels taken from a result set 
	 * into Capitalized and Spaced Titles for use in Ascii Output Tables
	 * @param label The column label from a result set's metadata
	 * @return
	 */
	private static String fixColumnTitle(String label) {
		label = label.replaceAll("_", " "); //Replace underscores with spaces
		
		StringBuilder b = new StringBuilder(label); //create string builder
		int i = 0;
		
		//Capitalize the first letter and other subsequent letters that follow a space
		do { 
		  b.replace(i, i + 1, b.substring(i,i + 1).toUpperCase());
		  i =  b.indexOf(" ", i) + 1;
		} while (i > 0 && i < b.length());

		return b.toString();
	}
}
