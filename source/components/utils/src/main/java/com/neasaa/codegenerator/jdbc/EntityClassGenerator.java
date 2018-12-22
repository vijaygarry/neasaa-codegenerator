package com.neasaa.codegenerator.jdbc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.neasaa.codegenerator.java.JavaClassDef;
import com.neasaa.codegenerator.java.JavaFieldDef;
import com.neasaa.util.FileUtils;

/**
 * @author Vijay Garothaya
 * @version 1.0 Dec 5, 2018
 */
public class EntityClassGenerator extends AbstractJavaClassGenerator {
	
	public static void generateEntityClassForTable (TableDefinition aTableDefinition, String aSrcMainJavaPath) throws Exception {
		String className = DbHelper.getClassNameFromTableName (aTableDefinition.getTableName());
		String packageName = "com.saix.common.entity";
		String javaClassFile = aSrcMainJavaPath + getClassFileName(packageName, className);
		FileUtils.createEmptyFile(javaClassFile, false);
		List<JavaFieldDef> fields = new ArrayList<>();
		Map<String, JavaFieldDef> columnNameToFieldMap = new HashMap<>();
		
		for(ColumnDefinition colDef: aTableDefinition.getColumnDefinitions()) {
			JavaFieldDef javaFieldDef = TableToJavaHelper.getJavaFieldDefFromCol( aTableDefinition, colDef );
			fields.add(javaFieldDef);	
			columnNameToFieldMap.put(colDef.getColumnName(), javaFieldDef);
		}

		JavaClassDef classDef = new JavaClassDef();
		classDef.setHeader(getCopyrightHeaderForClass());
		classDef.setPackageName(packageName);
		addSlf4jImports (classDef);
		addUtilDateImport (classDef);
		
		classDef.setClassName(className);
		
		classDef.setParentClass("BaseEntity");
		
		classDef.setFields(fields);
		System.out.println("Creating Entity java class " + javaClassFile);
		FileUtils.writeStringToFile(javaClassFile, classDef.generateJavaClass(), false);
		
	}
}
