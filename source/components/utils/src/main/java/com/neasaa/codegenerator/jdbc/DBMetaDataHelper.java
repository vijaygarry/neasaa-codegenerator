package com.neasaa.codegenerator.jdbc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.JDBCType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.neasaa.util.StringUtils;

/**
 * @author Vijay Garothaya
 */
public class DBMetaDataHelper {

	/**
	 * Information how to get list of tables: Get Table RS by
	 * metadata.getTables. This RS will have the following columns
	 * 
	 * <pre>
	 * Col Name                           Type Id                  Type Name                
	-------------------------------------------------------------------------------------
	table_cat                          1111                     unknown                  
	table_schem                        12                       name                     
	table_name                         12                       name                     
	table_type                         12                       text                     
	remarks                            12                       text
	 * </pre>
	 * 
	 * Below commented to code to see column name and their data type.
	 * 
	 * <pre>
	 * ResultSetMetaData tableMetadata = tableRs.getMetaData();
	 * int columnCount = tableMetadata.getColumnCount();
	 * System.out.println(rPad("Col Name", 35, ' ') + rPad("Type Id", 25, ' ') + rPad("Type Name", 25, ' '));
	 * System.out.println(rPad("", 85, '-'));
	 * 
	 * for (int i = 1; i <= columnCount; i++) {
	 * 	String colName = tableMetadata.getColumnName(i);
	 * 	int dataTypeId = tableMetadata.getColumnType(i);
	 * 	String dataTypeName = tableMetadata.getColumnTypeName(i);
	 * 	System.out.println(
	 * 			rPad(colName, 35, ' ') + rPad(String.valueOf(dataTypeId), 25, ' ') + rPad(dataTypeName, 25, ' '));
	 * }
	 * </pre>
	 */
	public static List<String> getAllTablesInSchema(Connection aConnection, String aSchemaName) throws Exception {
		ResultSet tableRs = null;
		try {

			DatabaseMetaData metaData = aConnection.getMetaData();
			tableRs = metaData.getTables(null, aSchemaName, "%", new String[] { "TABLE" });

			List<String> tableNames = new ArrayList<>();
			while (tableRs.next()) {
				String tableName = tableRs.getString("table_name");
				tableNames.add(tableName);
			}
			return tableNames;
		} finally {
			if (tableRs != null) {
				try {
					tableRs.close();
				} catch (Exception e) {
				}
			}

		}
	}

	/**
	 * Get list of primary key columns for specified table in specified schema.
	 * 
	 * @param aConnection
	 * @param aSchemaName
	 * @param aTableName
	 * @return
	 * @throws SQLException
	 */
	public static Set<String> getPrimaryKeyColumnsForTable(Connection aConnection, String aSchemaName,
			String aTableName) throws SQLException {
		ResultSet primaryKeysRs = null;
		Set<String> primaryKeys = null;

		try {
			DatabaseMetaData metaData = aConnection.getMetaData();
			primaryKeysRs = metaData.getPrimaryKeys(null, aSchemaName, aTableName);
			primaryKeys = new HashSet<>();
			while (primaryKeysRs.next()) {
				String string = primaryKeysRs.getString("COLUMN_NAME");
				primaryKeys.add(string);
			}
		} finally {
			if (primaryKeysRs != null) {
				try {
					primaryKeysRs.close();
				} catch (Exception e) {
				}
			}

		}
		return primaryKeys;
	}
	
	public static TableDefinition buildTableDefinition (Connection aConnection, String aTableName, String aSchemaName ) throws Exception {
		DatabaseMetaData metaData = aConnection.getMetaData();
		List<ColumnDefinition> columnDefinitions = new ArrayList<>();
//		List<String> colNamesWithDate = new ArrayList<>();
		ResultSet colResultSet = metaData.getColumns( null, aSchemaName, aTableName, null );

		Set<String> primaryKeysFromDb =
				DBMetaDataHelper.getPrimaryKeyColumnsForTable( aConnection, aSchemaName, aTableName );
		
		System.out.println( "Primary key for table " + aTableName + " in PG: " + primaryKeysFromDb );
		System.out.println( StringUtils.rightPad( "Col Name", ' ', 35 ) + StringUtils.rightPad( "Type Id", ' ', 25 ) + StringUtils.rightPad("Type Name", ' ', 25) );
		System.out.println( StringUtils.rightPad( "", '-', 85 ) );
			
		while ( colResultSet.next() ) {
			String colName = colResultSet.getString( "COLUMN_NAME" );
			int dataTypeId = colResultSet.getInt( "DATA_TYPE" );
			String dataTypeName = colResultSet.getString( "TYPE_NAME" );
			System.out.println( StringUtils.rightPad( colName, ' ', 35 ) + StringUtils.rightPad( String.valueOf( dataTypeId ), ' ', 25 ) + StringUtils.rightPad( dataTypeName, ' ', 25 ) );
			boolean pk = primaryKeysFromDb.contains( colName );
			
			String normalizeColumnName = colName.toUpperCase();
			boolean autoGenerated = false;
			if(dataTypeName != null && dataTypeName.toUpperCase().contains("SERIAL")) {
				autoGenerated = true;
			}
			ColumnDefinition colDef = new ColumnDefinition(normalizeColumnName, JDBCType.valueOf(dataTypeId), pk, null, false, autoGenerated);
			
//			if(normalizeColumnName.contains( "DATE" )) {
//				colNamesWithDate.add( normalizeColumnName );
//			}
			columnDefinitions.add( colDef );
		}
		
		TableDefinition tabDef =
				new TableDefinition( aTableName.toUpperCase(), columnDefinitions );
		return tabDef;
	}

}
