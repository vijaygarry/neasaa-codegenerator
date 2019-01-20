package com.neasaa.codegenerator.jdbc;

import java.sql.Connection;
import java.util.Arrays;

import com.neasaa.codegenerator.CodeGenerationCliParameter;
import com.neasaa.util.config.BaseConfig;

public class CodeGenerator {
	private static String tableName = "saix_appentitlement";
	public static void main(String[] args) throws Exception {
		
		CodeGenerationCliParameter cliParams = CodeGenerationCliParameter.parseCommandLineParams(args);
		
		String [] configFiles;
		if(cliParams.getApplicationConfigFilename() != null) {
			configFiles = new String[2];
			configFiles[0] =  "metadata.config";
			configFiles[1] =  cliParams.getApplicationConfigFilename();
		} else {
			configFiles = new String[1];
			configFiles[0] =  "metadata.config";
		}
		try {
			System.out.println( "Loading configurtion file " + Arrays.asList(configFiles));
			BaseConfig.initialize(configFiles);
		} catch (Exception ex) {
			System.err.println("Fail to load configuration file. " + ex.getMessage());
			ex.printStackTrace();
			System.exit(1);
		}

		Connection connection = null;
		try {
			connection = DbHelper.getDbConnection();
		} catch (Exception ex) {
			System.err.println("Fail to create db connection");
			ex.printStackTrace();
			System.exit(2);
		}
		
		switch (cliParams.getMode()) {
		case DB_TABLE_LIST:
			//If mode is print table names, then call this method.
			CodeGeneratorHelper.printAllTableDetailsForSchema(connection, "");
			break;
		case DB_TABLE_DETAILS:
			//If mode is Print table details, then call this method:
			CodeGeneratorHelper.printTableDetails(connection, tableName, null);
			break;
		case TABLE_CREATE_ENTITY_CLASS:
			createTableEntityCode (connection, tableName, null);
			break;
		case TABLE_CREATE_ROWMAPPER_CLASS:
			createTableRowMapperCode (connection, tableName, null);
			break;
		case TABLE_CREATE_DAO_CLASS:
			createTableDaoCode  (connection, tableName, null);
			break;
		default:
			System.out.println ( "Invalid mode " + cliParams.getMode());
		}
	}
	
	
	private static void createTableEntityCode (Connection aConnection, String aTableName, String aSchemaName) throws Exception {
		TableDefinition tableDefinition = DBMetaDataHelper.getTableDefinition (aConnection, aTableName, aSchemaName);
		EntityClassGenerator.generateEntityClassForTable(tableDefinition);
	}
	
	private static void createTableRowMapperCode (Connection aConnection, String aTableName, String aSchemaName) throws Exception {
		TableDefinition tableDefinition = DBMetaDataHelper.getTableDefinition (aConnection, aTableName, aSchemaName);
		String entityClassName = DbHelper.getClassNameFromTableName (tableDefinition.getTableName());
		RowMapperGenerator.generateRowMapperClassForTable( entityClassName, tableDefinition);
	}
	
	private static void createTableDaoCode (Connection aConnection, String aTableName, String aSchemaName) throws Exception {
		TableDefinition tableDefinition = DBMetaDataHelper.getTableDefinition (aConnection, aTableName, aSchemaName);
		String entityClassName = DbHelper.getClassNameFromTableName (tableDefinition.getTableName());
		EntityDaoClassGenerator.generateDaoInterfaceCode(entityClassName);
		EntityDaoClassGenerator.generateDaoImplCode(entityClassName, tableDefinition);
	}
	
}
