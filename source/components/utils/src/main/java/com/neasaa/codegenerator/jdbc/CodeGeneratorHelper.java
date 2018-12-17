package com.neasaa.codegenerator.jdbc;

import java.sql.Connection;
import java.util.List;

import com.neasaa.util.StringUtils;

public class CodeGeneratorHelper {
	
	public static void printAllTableDetailsForSchema (Connection aDbConnection, String aSchemaName) {
		List<String> tableNames = null;
		try {
			tableNames = DBMetaDataHelper.getAllTablesInSchema(aDbConnection, aSchemaName);
			System.out.println(StringUtils.rightPad(" Table Name", ' ', 35));
			System.out.println(StringUtils.rightPad("", '=', 35));
			for(String tableName : tableNames) {
				System.out.println(" " + tableName);
			}
		} catch (Exception ex) {
			System.err.println("Fail to get list of tables from db");
			ex.printStackTrace();
			System.exit(3);
		}
	}
	
	public static void printTableDetails (Connection aConnection, String aTableName, String aSchemaName) throws Exception {
		System.out.println();
		System.out.println( StringUtils.rightPad( "", '=', 85 ) );
		System.out.print( "Details for Table: " );
		if(!StringUtils.isEmpty(aSchemaName)) {
			System.out.print( aSchemaName + ".");
		}
		System.out.println( aTableName);
		System.out.println( StringUtils.rightPad( "", '-', 85 ) );
		System.out.println( StringUtils.rightPad( "Column Name", ' ', 35 ) + StringUtils.rightPad( "Data Type", ' ', 25 ) + StringUtils.rightPad("Primary Key", ' ', 15) );
		System.out.println( StringUtils.rightPad( "", '-', 85 ) );
		TableDefinition tableDefinition = DBMetaDataHelper.getTableDefinition(aConnection, aTableName, aSchemaName);
		for(ColumnDefinition column : tableDefinition.getColumnDefinitions()) {
			System.out.println( StringUtils.rightPad( column.getColumnName(), ' ', 35 ) + StringUtils.rightPad( column.getDataType().getName(), ' ', 25 ) + StringUtils.rightPad( (column.isPrimaryKey()==true? "Yes" : "No"), ' ', 15 ) );	
		}		
	}
	
	
}
