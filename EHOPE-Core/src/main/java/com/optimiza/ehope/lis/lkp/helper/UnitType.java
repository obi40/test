package com.optimiza.ehope.lis.lkp.helper;

public enum UnitType {
	SI("SI"),
	CONV("CONV");

	private UnitType(String value) {
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