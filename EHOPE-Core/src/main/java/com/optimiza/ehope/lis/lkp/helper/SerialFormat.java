package com.optimiza.ehope.lis.lkp.helper;

public enum SerialFormat {

	SERIAL("SERIAL"),
	ANNUAL("ANNUAL"),
	LOCATION("LOCATION");

	private String value;

	private SerialFormat(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
