package com.neasaa.codegenerator.jdbc;

import java.sql.JDBCType;

import com.neasaa.util.StringUtils;


public class ColumnDefinition {

	private final String columnName;
	private final JDBCType dataType;
	private final boolean primaryKey;
	private final String converter;

	// Ignore this column in target database.
	private final boolean ignoreColumn;
	private final boolean autoGenerated;
	
	
	public ColumnDefinition(String aColumnName, JDBCType aDataType, boolean aPrimaryKey, String aConverter, boolean aIgnoreColumn, boolean aAutoGenerated) throws Exception {
		super();
		if (StringUtils.isEmpty(aColumnName)) {
			throw new Exception("Source Column Name is not defined");
		} else {
			this.columnName = normalizeColumnNameToUpper(aColumnName);
		}
		
		this.dataType = aDataType;
		this.primaryKey = aPrimaryKey;
		
		this.converter = aConverter;
		this.ignoreColumn = aIgnoreColumn;
		this.autoGenerated = aAutoGenerated;
	}


	/**
	 * Trim and change the case to upper.
	 * 
	 * @param aColumnName
	 * @return
	 */
	private String normalizeColumnNameToUpper(String aColumnName) {
		return aColumnName.trim().toUpperCase();
	}


	public String getColumnName() {
		return this.columnName;
	}


	public JDBCType getDataType() {
		return this.dataType;
	}


	public boolean isPrimaryKey() {
		return this.primaryKey;
	}

	public String getConverter() {
		return this.converter;
	}


	public boolean isIgnoreColumn() {
		return this.ignoreColumn;
	}


	public boolean isAutoGenerated() {
		return this.autoGenerated;
	}


	@Override
	public String toString() {
		return "ColumnDefinition [columnName=" + this.columnName + ", dataType=" + this.dataType + ", primaryKey="
				+ this.primaryKey + ", converter=" + this.converter + ", ignoreColumn=" + this.ignoreColumn
				+ ", autoGenerated=" + this.autoGenerated + "]";
	}


}