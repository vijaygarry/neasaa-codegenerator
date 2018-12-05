package com.neasaa.codegenerator.java;

public class JavaFieldDef {
	private final String dataType;
	private final String fieldName;
	
	public JavaFieldDef(String aDataType, String aFieldName) {
		super();
		this.dataType = aDataType;
		this.fieldName = aFieldName;
	}
	
	public String getDataType() {
		return this.dataType;
	}
	
	public String getFieldName() {
		return this.fieldName;
	}
}
