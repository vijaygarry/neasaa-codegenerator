package com.neasaa.codegenerator.jdbc;

import java.sql.Connection;

import com.neasaa.util.config.BaseConfig;

public class CodeGenerator {
	private static String tableName = "saix_application";
	private static String srcMainJavaPath = "/Users/vijay/work/saix/projects/ApiService/source/components/entity-processor/build/";
	public static void main(String[] args) throws Exception {
		
		String configFileInClasspath = "metadata.config";
		try {
			BaseConfig.initialize(configFileInClasspath);
		} catch (Exception ex) {
			System.err.println("Fail to load the property file " + configFileInClasspath);
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

		//If mode is print table names, then call this method.
		CodeGeneratorHelper.printAllTableDetailsForSchema(connection, "");
		
		//If mode is Print table details, then call this method:
		CodeGeneratorHelper.printTableDetails(connection, tableName, null);
		
		TableDefinition tableDefinition = DBMetaDataHelper.getTableDefinition (connection, tableName, null);
		
		EntityClassGenerator.generateEntityClassForTable(tableDefinition, srcMainJavaPath);
		
		String entityClassName = DbHelper.getClassNameFromTableName (tableDefinition.getTableName());
		RowMapperGenerator.generateRowMapperClassForTable( entityClassName, tableDefinition , srcMainJavaPath);
		
		EntityDaoClassGenerator.generateDaoInterfaceCode(entityClassName, srcMainJavaPath);
		EntityDaoClassGenerator.generateDaoImplCode(entityClassName, srcMainJavaPath, tableDefinition);
		
//		System.out.println("Class code" + generateJavaCodeForTable(tableDefinition));
			// Create RowMapper using table def

			// Create TableHelper
			// Class with insert statement, update statement and delete
			// statement
			// This class should also have default select statement
		
	}

	
//	private static String generateJavaCodeForTable (TableDefinition aTableDefinition) throws Exception {
//			String header = "/** This is class header */";
//			
//			
//			List<JavaFieldDef> fields = new ArrayList<>();
//			Map<String, JavaFieldDef> columnNameToFieldMap = new HashMap<>();
//			
//			for(ColumnDefinition colDef: aTableDefinition.getColumnDefinitions()) {
//				JavaFieldDef javaFieldDef = TableToJavaHelper.getJavaFieldDefFromCol( aTableDefinition, colDef );
//				fields.add(javaFieldDef);	
//				columnNameToFieldMap.put(colDef.getColumnName(), javaFieldDef);
//			}
//						
//			JavaClassDef classDef = new JavaClassDef();
//			classDef.setHeader(header);
//			classDef.setPackageName("com.neasaa.util");
//			//addSlf4jImports (classDef);
//			//addUtilDateImport (classDef);
//			classDef.setClassName(DbHelper.getClassNameFromTableName (aTableDefinition.getTableName()));
//			classDef.setParentClass("BaseEntity");
////			classDef.setInterfaces(interfaces);
//			
//			classDef.setFields(fields);
////			addInsertStatementMethod(classDef, aTableDefinition, columnNameToFieldMap);
//			
//			return classDef.generateJavaClass();
//	}
	
}
