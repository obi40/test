package com.optimiza.core.lkp.helper;

public enum FieldType {
	STRING("STRING"),
	TRANS_FIELD("TRANS_FIELD"),
	NUMBER("NUMBER"),
	BOOLEAN("BOOLEAN"),
	DATE("DATE"),
	OBJECT("OBJECT");

	private FieldType(String value) {
		this.value = value;
	}

	private String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
