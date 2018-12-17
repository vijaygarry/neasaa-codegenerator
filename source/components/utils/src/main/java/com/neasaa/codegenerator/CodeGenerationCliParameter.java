package com.neasaa.codegenerator;

public class CodeGenerationCliParameter {
	
	public static final String MODE_DB_TABLE_LIST = "DB_TABLE_LIST";
	public static final String MODE_DB_TABLE_DETAILS = "DB_TABLE_DETAILS";
	public static final String TABLE_CREATE_ENTITY_CLASS = "TABLE_CREATE_ENTITY_CLASS";
	public static final String TABLE_CREATE_ROWMAPPER_CLASS = "TABLE_CREATE_ROWMAPPER_CLASS";
	public static final String TABLE_CREATE_DAO_CLASS = "TABLE_CREATE_DAO_CLASS";
	public static final String TABLE_CREATE_ALL_CLASSES = "TABLE_CREATE_ALL_CLASSES";
	public static final String UPDATE_CLASS_HEADER = "UPDATE_CLASS_HEADER";
	
	
	private String mode = MODE_DB_TABLE_LIST;
	
	
	public String getMode() {
		return this.mode;
	}
	
	
}
