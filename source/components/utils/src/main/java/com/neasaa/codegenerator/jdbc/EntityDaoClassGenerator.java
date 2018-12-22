package com.neasaa.codegenerator.jdbc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.neasaa.codegenerator.java.JavaClassDef;
import com.neasaa.codegenerator.java.JavaFieldDef;
import com.neasaa.codegenerator.java.JavaInterfaceDef;
import com.neasaa.codegenerator.java.JavaMethodDef;
import com.neasaa.util.FileUtils;

/**
 * This class will generate interface with following method: <br>
 * void insertAccountEntity (AccountEntity aAccount);<br>
 * void deleteAccountEntityById (String aAccountId);<br>
 * void updateAccountEntityById (AccountEntity aAccount);<br>
 * AccountEntity fetchAccountEntityById (String aAccountId);
 * 
 * @author Vijay G
 * @version 1.0 Dec 21, 2018
 */
public class EntityDaoClassGenerator extends AbstractJavaClassGenerator {

	public static String  getDaoInterfaceName (String aEntityClassName) {
		return aEntityClassName + "Dao";
	}
	
	public static String  getDaoImplName (String aEntityClassName) {
		return getDaoInterfaceName (aEntityClassName) + "Impl";
	}
	
	public static String getInsertMethodName (String aEntityClassName) {
		return "insert" + aEntityClassName;
	}
	
	public static void generateDaoInterfaceCode (String aEntityClassName, String aSrcMainJavaPath) throws Exception {
		String className = getDaoInterfaceName (aEntityClassName);
		String packageName = "com.saix.common.dao";
		String javaClassFile = aSrcMainJavaPath + getClassFileName(packageName, className);
		FileUtils.createEmptyFile(javaClassFile, false);

		JavaInterfaceDef classDef = new JavaInterfaceDef();
		classDef.setHeader(getCopyrightHeaderForClass());
		classDef.setPackageName(packageName);
		addSlf4jImports(classDef);
		addUtilDateImport(classDef);

		classDef.setClassName(className);

		classDef.setParentClass("BaseEntity");
		addInterfaceMethods(aEntityClassName, classDef);
		System.out.println("Creating Dao Interface java class " + javaClassFile);
		FileUtils.writeStringToFile(javaClassFile, classDef.generateJavaClass(), false);
		
	}
	
	public static void generateDaoImplCode (String aEntityClassName, String aSrcMainJavaPath, TableDefinition aTableDefinition) throws Exception {
		String className = getDaoImplName (aEntityClassName);
		String packageName = "com.saix.common.dao.pg";
		String javaClassFile = aSrcMainJavaPath + getClassFileName(packageName, className);
		FileUtils.createEmptyFile(javaClassFile, false);

		JavaClassDef classDef = new JavaClassDef();
		classDef.setHeader(getCopyrightHeaderForClass());
		classDef.setPackageName(packageName);
		addSlf4jImports(classDef);
		addUtilDateImport(classDef);

		classDef.setClassName(className);

		classDef.setParentClass("PgAbstractDao");
		classDef.setInterfaces(new ArrayList<>(Arrays.asList(getDaoInterfaceName(aEntityClassName))));
		addImplMethods(aEntityClassName, classDef, aTableDefinition);
		
		Map<String, JavaFieldDef> columnNameToFieldMap = new HashMap<>();
		List<JavaFieldDef> fields = new ArrayList<>();
		for(ColumnDefinition colDef: aTableDefinition.getColumnDefinitions()) {
			JavaFieldDef javaFieldDef = TableToJavaHelper.getJavaFieldDefFromCol( aTableDefinition, colDef );
			fields.add(javaFieldDef);	
			columnNameToFieldMap.put(colDef.getColumnName(), javaFieldDef);
		}
		addInsertStatementMethod(classDef, aTableDefinition, columnNameToFieldMap);
		
		System.out.println("Creating Dao Impl java class " + javaClassFile);
		FileUtils.writeStringToFile(javaClassFile, classDef.generateJavaClass(), false);
		
	}
	
	
	public static void addInterfaceMethods (String aEntityClassName, JavaInterfaceDef aInterfaceDef) {
		
		JavaMethodDef method = new JavaMethodDef("insert" + aEntityClassName, aEntityClassName + " a" + aEntityClassName);
		method.addMethodException("SQLException");
		method.setAbstractMethod(true);
		aInterfaceDef.addMethod(method);
		
		method = new JavaMethodDef("delete" + aEntityClassName, aEntityClassName + " a" + aEntityClassName);
		method.addMethodException("SQLException");
		method.setAbstractMethod(true);
		aInterfaceDef.addMethod(method);
		
		method = new JavaMethodDef("update" + aEntityClassName, aEntityClassName + " a" + aEntityClassName);
		method.addMethodException("SQLException");
		method.setAbstractMethod(true);
		aInterfaceDef.addMethod(method);
		
		
		method = new JavaMethodDef("fetch" + aEntityClassName, aEntityClassName + " a" + aEntityClassName);
		method.setReturnType(aEntityClassName);
		method.addMethodException("SQLException");
		method.setAbstractMethod(true);
		aInterfaceDef.addMethod(method);

	}
	
	public static void addImplMethods (String aEntityClassName, JavaClassDef aJavaClassDef, TableDefinition aTableDefinition) {
		JavaMethodDef method = new JavaMethodDef("fetch" + aEntityClassName, aEntityClassName + " a" + aEntityClassName);
		method.setReturnType(aEntityClassName);
		method.addMethodException("SQLException");
		method.addAnnotation("@Override");
		method.addAnnotation("@Transactional (transactionManager= \"transactionManager\", propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = Exception.class)");
		
		StringBuilder sb = new StringBuilder();
		boolean firstCol = true;
		String selectQueryColNames = " "; 
		for(ColumnDefinition columnDefinition : aTableDefinition.getColumnDefinitions()) {
			if(firstCol) {
				firstCol = false;
			} else {
				selectQueryColNames = selectQueryColNames + ", ";
			}
			selectQueryColNames = selectQueryColNames + columnDefinition.getColumnName() + " ";
		}
		sb.append("\t\tString selectQuery = \"select " + selectQueryColNames + " from " + aTableDefinition.getTableName());
		firstCol = true;
		for(ColumnDefinition columnDefinition : aTableDefinition.getColumnDefinitions()) {
			if(columnDefinition.isPrimaryKey()) {
				if(firstCol) {
					firstCol = false;
					sb.append(" where ");
				} else {
					sb.append( " and ");
				}
				sb.append( columnDefinition.getColumnName() + " = ? " );
			}
		}
		sb.append("\";\n");
		sb.append("\t\treturn getJdbcTemplate().queryForObject(selectQuery, new " + RowMapperGenerator.getRowMapperClassName(aEntityClassName) + "()");
		for(ColumnDefinition columnDefinition : aTableDefinition.getColumnDefinitions()) {
			if(columnDefinition.isPrimaryKey()) {
					sb.append(", ");
				sb.append(DbHelper.getFieldNameFromColumnName(aTableDefinition.getTableName(), columnDefinition.getColumnName()));
			}
		}
		sb.append(");\n");
		
		method.addMethodImplementation(sb.toString());
		aJavaClassDef.addMethod(method);
		
	}
	
	public static void addInsertStatementMethod (JavaClassDef aClassDef, TableDefinition aTableDefinition, Map<String, JavaFieldDef> aColumnNameToFieldMap) throws Exception {
		
		String classParamName = "a"+ aClassDef.getClassName();
		JavaMethodDef method = new JavaMethodDef("buildInsertStatement", "Connection aConection, " + aClassDef.getClassName() + " " + classParamName);
		method.setReturnType("PreparedStatement");
		method.addMethodException("SQLException");
		String sqlString = InsertSqlStatementGenerator.buildInsertSqlStatement (aTableDefinition);
//		System.out.println("sqlString: " + sqlString);
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
			sb.append("\t\t").append(SqlStatementHelper.generateSetterStatement(columnDef.getDataType(), String.valueOf(index), classParamName + "." + getterMethodName + " ()"));
			sb.append("\n");
		}
		return sb.toString();
	}
}
