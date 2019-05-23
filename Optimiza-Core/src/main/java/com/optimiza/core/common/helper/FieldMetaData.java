package com.optimiza.core.common.helper;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class FieldMetaData {

	private String name;
	@JsonIgnore
	private String columnName;
	private boolean isNotNull;
	private boolean isSized;
	private boolean isEmail;
	private boolean isUpdatable;
	private int integer;
	private int fraction;
	private int min;
	private int max;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isNotNull() {
		return isNotNull;
	}

	public void setNotNull(boolean isNotNull) {
		this.isNotNull = isNotNull;
	}

	public boolean isSized() {
		return isSized;
	}

	public void setSized(boolean isSized) {
		this.isSized = isSized;
	}

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public int getInteger() {
		return integer;
	}

	public void setInteger(int integer) {
		this.integer = integer;
	}

	public int getFraction() {
		return fraction;
	}

	public void setFraction(int fraction) {
		this.fraction = fraction;
	}

	public boolean isEmail() {
		return isEmail;
	}

	public void setEmail(boolean isEmail) {
		this.isEmail = isEmail;
	}

	public boolean isUpdatable() {
		return isUpdatable;
	}

	public void setUpdatable(boolean isUpdatable) {
		this.isUpdatable = isUpdatable;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

}
