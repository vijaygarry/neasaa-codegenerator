package com.neasaa.codegenerator.jdbc;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.neasaa.codegenerator.java.JavaClassDef;
import com.neasaa.codegenerator.java.JavaFieldDef;
import com.neasaa.codegenerator.java.JavaMethodDef;
import com.neasaa.util.config.BaseConfig;

public class CodeGenerator {
	private static String tableName = "operation";
	
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

		// TODO:
		// Get list of tables from db connection
		List<String> tableNames = null;
		try {
			tableNames = DBMetaDataHelper.getAllTablesInSchema(connection, "");
		} catch (Exception ex) {
			System.err.println("Fail to get list of tables from db");
			ex.printStackTrace();
			System.exit(3);
		}

		System.out.println("Table names from db: " + tableNames);
		// For each table generate the code
		for (String tableName : tableNames) {
			
			if (ignoreTable(tableName)) {
				continue;
			}
			if(tableName.equalsIgnoreCase(CodeGenerator.tableName)) {
				TableDefinition tableDefinition = DBMetaDataHelper.buildTableDefinition (connection, tableName, null);
				System.out.println("Table:" + tableDefinition);
				System.out.println("Class code" + generateJavaCodeForTable(tableDefinition));
			}
						
			// Create RowMapper using table def

			// Create TableHelper
			// Class with insert statement, update statement and delete
			// statement
			// This class should also have default select statement
		}
	}


	private static boolean ignoreTable(String aTableName) {
		return BaseConfig.getBooleanProperty(aTableName.toUpperCase() + ".IGNORE", false);
	}

	
	private static String generateJavaCodeForTable (TableDefinition aTableDefinition) throws Exception {
			String header = "/** This is class header */";
			
//			List<String> interfaces = new ArrayList<>();
//			interfaces.add("RowMapper<TableName>");
			String rowMapperClassName = DbHelper.getClassNameFromTableName (aTableDefinition.getTableName()) + "RowMapper";
			String entityClassName = DbHelper.getClassNameFromTableName (aTableDefinition.getTableName());
			RowMapperGenerator rowMapperGenerator = new RowMapperGenerator( rowMapperClassName, "com.rowmapper", entityClassName, aTableDefinition );
			JavaClassDef buildClass = rowMapperGenerator.buildClass();
//			System.out.println( "Row mapper class:\n" + buildClass.generateJavaClass());
			
			if(1==1){
				return buildClass.generateJavaClass();
			}
			
			List<JavaFieldDef> fields = new ArrayList<>();
			Map<String, JavaFieldDef> columnNameToFieldMap = new HashMap<>();
			
			for(ColumnDefinition colDef: aTableDefinition.getColumnDefinitions()) {
				JavaFieldDef javaFieldDef = TableToJavaHelper.getJavaFieldDefFromCol( aTableDefinition, colDef );
				fields.add(javaFieldDef);	
				columnNameToFieldMap.put(colDef.getColumnName(), javaFieldDef);
			}
						
			JavaClassDef classDef = new JavaClassDef();
			classDef.setHeader(header);
			classDef.setPackageName("com.neasaa.util");
			addSlf4jImports (classDef);
			addUtilDateImport (classDef);
			classDef.setClassName(DbHelper.getClassNameFromTableName (aTableDefinition.getTableName()));
			classDef.setParentClass("BaseEntity");
//			classDef.setInterfaces(interfaces);
			
			classDef.setFields(fields);
			addInsertStatementMethod(classDef, aTableDefinition, columnNameToFieldMap);
			
			return classDef.generateJavaClass();
	}
	
	
	public static void addInsertStatementMethod (JavaClassDef aClassDef, TableDefinition aTableDefinition, Map<String, JavaFieldDef> aColumnNameToFieldMap) throws Exception {
		String classParamName = "a"+ aClassDef.getClassName();
		JavaMethodDef method = new JavaMethodDef("buildInsertStatement", "Connection aConection, " + aClassDef.getClassName() + " " + classParamName);
		method.setReturnType("PreparedStatement");
		method.addMethodException("SQLException");
//		method.addAnnotation("@Override");
		String sqlString = InsertSqlStatementGenerator.buildInsertSqlStatement (aTableDefinition);
		System.out.println("sqlString: " + sqlString);
		String sqlStatmentVar = "String sqlStatement = \"" + sqlString + "\";\n";
		StringBuilder sb = new StringBuilder();
		sb.append("\t\t" + sqlStatmentVar).append("\n");
		sb.append("\t\t").append("PreparedStatement prepareStatement = aConection.prepareStatement(sqlStatement);").append("\n");
		sb.append(getSetterStatements(aClassDef, aTableDefinition, aColumnNameToFieldMap));
		sb.append("\t\t").append("return prepareStatement;");
		
		method.addMethodImplementation(sb.toString());
		
//		prepareStatement.setNull(1, Types.VARCHAR);
		
		aClassDef.addMethod(method);
		
		aClassDef.addImportClass("java.sql.Connection");
		aClassDef.addImportClass("java.sql.PreparedStatement");
		aClassDef.addImportClass("java.sql.Types");
		
	}
	
	private static String getSetterStatements (JavaClassDef aClassDef, TableDefinition aTableDefinition, Map<String, JavaFieldDef> aColumnNameToFieldMap) throws Exception {
		List<ColumnDefinition> columnsToAddInInsertStatement = new ArrayList<>();
		// Loop thru all column and create a primary column list
		for ( ColumnDefinition columnDefinition : aTableDefinition.getColumnDefinitions() ) {
			// continue if this column to ignore on target database.
			if ( columnDefinition.isIgnoreColumn() ) {
				continue;
			}
			if ( columnDefinition.isAutoGenerated()) {
				continue;
			}
			columnsToAddInInsertStatement.add( columnDefinition );
		}
		String classParamName = "a"+ aClassDef.getClassName();
		int index = 0;
		StringBuilder sb = new StringBuilder();
		for ( ColumnDefinition columnDef : columnsToAddInInsertStatement ) {
			index++;
			 JavaFieldDef javaFieldDef = aColumnNameToFieldMap.get(columnDef.getColumnName());
			 String getterMethodName = JavaClassDef.getGetterMethodName(javaFieldDef);
			sb.append("\t\t").append(SqlStatementHelper.generateSetterStatement(columnDef.getDataType(), String.valueOf(index), classParamName + "." + getterMethodName));
			sb.append("\n");
		}
		return sb.toString();
	}
	public static void addSlf4jImports (JavaClassDef aClassDef) {
		aClassDef.addImportClass("org.slf4j.Logger");
		aClassDef.addImportClass("org.slf4j.LoggerFactory");
	}
	
	public static void addUtilDateImport (JavaClassDef aClassDef) {
		aClassDef.addImportClass("java.util.Date");
	}
}
