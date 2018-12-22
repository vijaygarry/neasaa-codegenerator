package com.neasaa.codegenerator.java;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.neasaa.util.StringUtils;

public class JavaClassDef {
	
	private String header;
	private String packageName;
	private Set<String> importClasses = new HashSet<>();
	private String className;
	private List<String> interfaces;
	private String parentClass;
	private List<JavaFieldDef> fields;
	private List<JavaMethodDef> methods = new ArrayList<>();
	
	protected static final String NEW_LINE = "\n";
	

	public String getHeader() {
		return this.header;
	}

	public void setHeader(String aHeader) {
		this.header = aHeader;
	}

	public String getPackageName() {
		return this.packageName;
	}

	public void setPackageName(String aPackageName) {
		this.packageName = aPackageName;
	}

	public Set<String> getImportClasses() {
		return this.importClasses;
	}

	public String getClassName() {
		return this.className;
	}

	public void setClassName(String aClassName) {
		this.className = aClassName;
	}

	public List<String> getInterfaces() {
		return this.interfaces;
	}

	public void setInterfaces(List<String> aInterfaces) {
		this.interfaces = aInterfaces;
	}

	public String getParentClass() {
		return this.parentClass;
	}

	public void setParentClass(String aParentClass) {
		this.parentClass = aParentClass;
	}

	public List<JavaFieldDef> getFields() {
		return this.fields;
	}

	public void setFields(List<JavaFieldDef> aFields) {
		this.fields = aFields;
	}

	public List<JavaMethodDef> getMethods() {
		return this.methods;
	}

	public void addImportClass (String aQualifiedClassName) {
		this.importClasses.add(aQualifiedClassName);
	}
	
	public void addMethod(JavaMethodDef aMethod) {
		this.methods.add(aMethod);
	}
	
	protected void appendHeader (StringBuilder aStringBuilder) {
		if(!StringUtils.isEmpty(this.header)) {
			aStringBuilder.append(this.header).append(NEW_LINE);
		}
		aStringBuilder.append(NEW_LINE);
	}
	
	protected void appendPackage (StringBuilder aStringBuilder) {
		if(!StringUtils.isEmpty(this.packageName)) {
			aStringBuilder.append("package ").append(this.packageName).append(";").append(NEW_LINE);
		}
		aStringBuilder.append(NEW_LINE);
	}
	
	public String generateJavaClass () {
		StringBuilder sb = new StringBuilder();
		
		appendHeader(sb);
		appendPackage(sb);
		
		//Add all imports
		appendImports(sb);
		
		//Add class declaration
		appendClassDeclaration(sb);
		
		sb.append(" {").append(NEW_LINE);
		sb.append(getFieldDefs(this.fields));
		appendMethods(sb);
		sb.append(getGetterSetterForFields(this.fields));
		sb.append("}").append(NEW_LINE);
		return sb.toString();
	}
	
	protected void appendMethods(StringBuilder aStringBuilder) {
		if (this.methods != null && this.methods.size() > 0) {
			for(JavaMethodDef method : this.methods) {
				aStringBuilder.append(method.generateMethodCode());
				aStringBuilder.append(NEW_LINE);
			}
		}
	}
	
	
	protected void appendImports(StringBuilder aStringBuilder) {
		if (this.importClasses == null || this.importClasses.size() == 0) {
			return;
		}
		for (String importClass : this.importClasses) {
			aStringBuilder.append("import ").append(importClass).append(";").append(NEW_LINE);
		}

		aStringBuilder.append(NEW_LINE);
	}
	
	protected void appendClassDeclaration (StringBuilder sb) {
		sb.append("public class ").append(this.className);	
		
		if(!StringUtils.isEmpty(this.parentClass)) {
			sb.append(" extends " ).append(this.parentClass);
		}
		
		if(this.interfaces != null && this.interfaces.size() > 0) {
			sb.append(" implements ");
			boolean first = true;
			for(String interfaceName : this.interfaces) {
				if(first) {
					first = false;
				} else {
					sb.append(", ");
				}
				sb.append(interfaceName);
			}
		}
	}
	
	private String getFieldDefs (List<JavaFieldDef> aFields) {
		StringBuilder sb = new StringBuilder();
		if(aFields == null || aFields.size() ==0) {
			return "";
		}
		sb.append(NEW_LINE);
		for (JavaFieldDef field: aFields) {
			sb.append("\t").append("private ").append(field.getDataType()).append(" ").append(field.getFieldName()).append(";").append(NEW_LINE);
		}
		sb.append(NEW_LINE);
		return sb.toString();
	}
	
	
	private String getGetterSetterForFields (List<JavaFieldDef> aFields) {
		StringBuilder sb = new StringBuilder();
		if(aFields == null || aFields.size() ==0) {
			return "";
		}
		sb.append(NEW_LINE);
		for (JavaFieldDef field: aFields) {
			String capitalizeFieldName = StringUtils.capitalize(field.getFieldName());
			
			sb.append("\t").append("public ").append(field.getDataType()).append(" " ).append(getGetterMethodName(field)).append(" () {").append(NEW_LINE);
			sb.append("\t\treturn this.").append(field.getFieldName()).append(";").append(NEW_LINE);
			sb.append("\t").append("}").append(NEW_LINE);
			
			String setterParamName = "a" + capitalizeFieldName;
			sb.append("\t").append("public void ").append(getSetterMethodName(field)).append(" (").append(field.getDataType()).append(" ").append(setterParamName).append(") {").append(NEW_LINE);
			sb.append("\t\tthis.").append(field.getFieldName()).append(" = ").append(setterParamName).append(";").append(NEW_LINE);
			sb.append("\t").append("}").append(NEW_LINE).append(NEW_LINE);

		}
		sb.append(NEW_LINE);
		return sb.toString();
	}
	
	public static String getGetterMethodName (JavaFieldDef field) {
		String capitalizeFieldName = StringUtils.capitalize(field.getFieldName());
		return "get" + capitalizeFieldName;
	}
	
	public static String getSetterMethodName (JavaFieldDef field) {
		String capitalizeFieldName = StringUtils.capitalize(field.getFieldName());
		return "set" + capitalizeFieldName ;
	}
	
	public static void main(String[] args) {
		String header = "/** This is class header */";
					
		JavaMethodDef method = new JavaMethodDef("buildInsertStatement", "PreparedStatement aPreparedStatement");
		method.setReturnType("String");
		method.addMethodImplementation("\t\t//This is sample method.\n");
		method.addMethodImplementation("\t\treturn null;");
		
		List<JavaMethodDef> methods = new ArrayList<>();
		methods.add(method);
		
		List<String> interfaces = new ArrayList<>();
		interfaces.add("RowMapper<TableName>");
		
		List<JavaFieldDef> fields = new ArrayList<>();
		fields.add(new JavaFieldDef("String", "userName"));
		fields.add(new JavaFieldDef("String", "password"));
		fields.add(new JavaFieldDef("Date", "dateCreated"));
		
		JavaClassDef classDef = new JavaClassDef();
		classDef.setHeader(header);
		classDef.setPackageName("com.neasaa.util");
		classDef.addImportClass("org.slf4j.Logger");
		classDef.addImportClass("org.slf4j.LoggerFactory");
		
		classDef.setClassName("StringUtils");
		classDef.setParentClass("BaseConfig");
		classDef.setInterfaces(interfaces);
		
		classDef.setFields(fields);
		classDef.addMethod(method);
		
		System.out.println(classDef.generateJavaClass());
	}
}
