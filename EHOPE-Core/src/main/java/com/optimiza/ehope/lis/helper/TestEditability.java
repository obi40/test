package com.optimiza.ehope.lis.helper;

public enum TestEditability {

	INACTIVE("INACTIVE"),
	USED("USED"),
	EDITABLE("EDITABLE");

	private String value;

	private TestEditability(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
