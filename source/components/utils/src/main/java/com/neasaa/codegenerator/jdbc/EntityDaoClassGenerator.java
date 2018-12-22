package com.neasaa.codegenerator.jdbc;

import java.util.ArrayList;
import java.util.Arrays;

import com.neasaa.codegenerator.java.JavaClassDef;
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
		sb.append("String selectQuery = \"select * from " + aTableDefinition.getTableName() + " where ");
		for(ColumnDefinition columnDefinition : aTableDefinition.getColumnDefinitions()) {
			if(columnDefinition.isPrimaryKey()) {
				sb.append( columnDefinition.getColumnName() + " = ? " );
			}
		}
		sb.append("return getJdbcTemplate().queryForObject(selectQuery, new " + RowMapperGenerator.getRowMapperClassName(aEntityClassName) + "(), ");
		for(ColumnDefinition columnDefinition : aTableDefinition.getColumnDefinitions()) {
			if(columnDefinition.isPrimaryKey()) {
				sb.append(DbHelper.getFieldNameFromColumnName(aTableDefinition.getTableName(), columnDefinition.getColumnName()));
				sb.append( "," );
			}
		}
		
		method.addMethodImplementation(sb.toString());
		aJavaClassDef.addMethod(method);
		
	}
}
