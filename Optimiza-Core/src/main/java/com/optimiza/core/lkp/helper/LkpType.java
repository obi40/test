package com.optimiza.core.lkp.helper;

public enum LkpType {

	CITY("CITY", "LkpCity"),
	COUNTRY("COUNTRY", "LkpCountry"),
	COMPARE_TYPE("COMPARE_TYPE", "LkpCompareType"),
	CURRENCY("CURRENCY", "LkpCurrency"),
	FIELD_TYPE("FIELD_TYPE", "LkpFieldType"),
	GENDER("GENDER", "LkpGender"),
	PRINT_FORMAT("PRINT_FORMAT", "LkpPrintFormat");

	private String value;
	private String entity;

	private LkpType(String value, String entity) {
		this.value = value;
		this.entity = entity;
	}

	public String getEntity() {
		return entity;
	}

	public String getValue() {
		return value;
	}

}
