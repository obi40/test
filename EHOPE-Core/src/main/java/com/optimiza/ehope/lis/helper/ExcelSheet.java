package com.optimiza.ehope.lis.helper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;

import com.optimiza.core.base.entity.BaseEntity;

public class ExcelSheet {

	private String name;
	private Class<? extends BaseEntity> entityType;
	private List<ExcelColumn> columns;

	private List<ExcelSheet> subSheets;

	private LinkedHashMap<Row, Map<String, String>> failedRows;

	//used for children
	private String parentField;
	private String identifierField;
	private String identifierTitle;

	/**
	 * Use for the ROOT WITHOUT CHILDREN
	 * 
	 * @param name The name of the sheet
	 * @param entityType The class of the entity this sheet represents
	 * @param columns The excel columns to be put in this sheet
	 * 
	 * @return ExcelSheet
	 */
	public ExcelSheet(String name, Class<? extends BaseEntity> entityType, List<ExcelColumn> columns) {
		super();
		this.name = name;
		this.entityType = entityType;
		this.columns = columns;
	}

	/**
	 * Use for the ROOT WITH CHILDREN
	 * 
	 * @param name The name of the sheet
	 * @param entityType The class of the entity this sheet represents
	 * @param columns The excel columns to be put in this sheet
	 * 
	 * @return ExcelSheet
	 */
	public ExcelSheet(String name, Class<? extends BaseEntity> entityType, List<ExcelColumn> columns, List<ExcelSheet> subSheets) {
		super();
		this.name = name;
		this.entityType = entityType;
		this.columns = columns;
		this.subSheets = subSheets;
	}

	/**
	 * Use for LEAF objects
	 * 
	 * @param name The name of the sheet
	 * @param entityType The class of the entity this sheet represents
	 * @param columns The excel columns to be put in this sheet
	 * @param parentField The field-name in the parent entity which the data in this sheet represents
	 * @param identifierField The field-name in the parent entity used to connect the child-objects in this sheet to the parent
	 * @param identifierTitle The title to display for the identifierField
	 * 
	 * @return ExcelSheet
	 */
	public ExcelSheet(String name, Class<? extends BaseEntity> entityType, List<ExcelColumn> columns,
			String parentField, String identifierField, String identifierTitle) {
		super();
		this.name = name;
		this.entityType = entityType;
		this.columns = columns;
		this.parentField = parentField;
		this.identifierField = identifierField;
		this.identifierTitle = identifierTitle;
	}

	/**
	 * Use for CHILDREN with CHILDREN
	 * 
	 * @param name The name of the sheet
	 * @param entityType The class of the entity this sheet represents
	 * @param columns The excel columns to be put in this sheet
	 * @param subSheets The sheets which represent foreign-key relations for this entity
	 * @param parentField The field-name in the parent entity which the data in this sheet represents
	 * @param identifierField The field-name in the parent entity used to connect the child-objects in this sheet to the parent
	 * @param identifierTitle The title to display for the identifierField
	 * 
	 * @return ExcelSheet
	 */
	public ExcelSheet(String name, Class<? extends BaseEntity> entityType, List<ExcelColumn> columns, List<ExcelSheet> subSheets,
			String parentField, String identifierField, String identifierTitle) {
		super();
		this.name = name;
		this.entityType = entityType;
		this.columns = columns;
		this.subSheets = subSheets;
		this.parentField = parentField;
		this.identifierField = identifierField;
		this.identifierTitle = identifierTitle;
	}

	public void addToFailedRows(Row row, Map<String, String> values) {
		if (this.failedRows == null) {
			this.failedRows = new LinkedHashMap<>();
		}
		this.failedRows.put(row, values);
	}

	public LinkedHashMap<Row, Map<String, String>> getFailedRows() {
		return failedRows;
	}

	public void setFailedRows(LinkedHashMap<Row, Map<String, String>> failedRows) {
		this.failedRows = failedRows;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Class<? extends BaseEntity> getEntityType() {
		return entityType;
	}

	public void setEntityType(Class<? extends BaseEntity> entityType) {
		this.entityType = entityType;
	}

	public List<ExcelColumn> getColumns() {
		return columns;
	}

	public void setColumns(List<ExcelColumn> columns) {
		this.columns = columns;
	}

	public List<ExcelSheet> getSubSheets() {
		return subSheets;
	}

	public void setSubSheets(List<ExcelSheet> subSheets) {
		this.subSheets = subSheets;
	}

	public String getParentField() {
		return parentField;
	}

	public void setParentField(String parentField) {
		this.parentField = parentField;
	}

	public String getIdentifierField() {
		return identifierField;
	}

	public void setIdentifierField(String identifierField) {
		this.identifierField = identifierField;
	}

	public String getIdentifierTitle() {
		return identifierTitle;
	}

	public void setIdentifierTitle(String identifierTitle) {
		this.identifierTitle = identifierTitle;
	}

}
