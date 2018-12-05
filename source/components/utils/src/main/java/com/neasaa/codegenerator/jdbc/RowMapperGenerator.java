package com.neasaa.codegenerator.jdbc;

import com.neasaa.codegenerator.java.JavaClassDef;
import com.neasaa.codegenerator.java.JavaFieldDef;
import com.neasaa.codegenerator.java.JavaMethodDef;
import com.neasaa.util.StringUtils;

/**
 * @author Vijay Garothaya
 * @version 1.0 Dec 5, 2018
 */
public class RowMapperGenerator {
	
	private String className;
	private String packageName;
	private String entityName;
	private TableDefinition tableDefinition;
	
	
	/**
	 * @param aClassName
	 * @param aPackageName
	 * @param aEntityName
	 * @param aTableDefinition
	 */
	public RowMapperGenerator ( String aClassName, String aPackageName, String aEntityName,
			TableDefinition aTableDefinition ) {
		super();
		this.className = aClassName;
		this.packageName = aPackageName;
		this.entityName = aEntityName;
		this.tableDefinition = aTableDefinition;
	}

	public JavaClassDef buildClass () throws Exception {
		JavaClassDef classDef = new JavaClassDef();
		classDef.setClassName( this.className );
		classDef.setPackageName( this.packageName );
		classDef.addImportClass( "java.sql.ResultSet" );
		classDef.addImportClass( "java.sql.SQLException" );
		classDef.addImportClass( "java.util.List" );
		
		classDef.addImportClass( "org.slf4j.Logger" );
		classDef.addImportClass( "org.slf4j.LoggerFactory" );
		classDef.addImportClass( "org.springframework.jdbc.core.RowMapper" );
		classDef.addMethod(getmapRowMethod());
		return classDef;
	}
	
	private JavaMethodDef getmapRowMethod () throws Exception {
		JavaMethodDef method = new JavaMethodDef("mapRow", "ResultSet aRs, int aRowNum");
		method.setReturnType(this.entityName);
		method.addMethodException("SQLException");
		method.addAnnotation("@Override");
		
		StringBuilder sb = new StringBuilder();
		String entityVarName = StringUtils.lowerFirstChar (this.entityName); 
		sb.append("\t\t" + this.entityName).append( " " ).append( entityVarName ) .append( " = new " ).append( this.entityName ).append( " ();\n" );
		for(ColumnDefinition colDef :this.tableDefinition.getColumnDefinitions()){
			JavaFieldDef javaFieldDef = TableToJavaHelper.getJavaFieldDefFromCol( this.tableDefinition, colDef );
			String setterMethodName = JavaClassDef.getSetterMethodName( javaFieldDef );
			sb.append("\t\t" + entityVarName).append( "." ).append( setterMethodName ) .append( " (" ).append( SqlStatementHelper.getResultSetMethod( "aRs", colDef.getDataType(), colDef.getColumnName() ) ).append( " );\n" );	
		}
		sb.append("\t\treturn " + entityVarName).append( ";" );
		method.addMethodImplementation(sb.toString());
		return method;
	}
}