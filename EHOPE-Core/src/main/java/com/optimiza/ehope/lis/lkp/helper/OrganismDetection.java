package com.optimiza.ehope.lis.lkp.helper;

public enum OrganismDetection {
	GROWTH("GROWTH"),
	NO_GROWTH("NO_GROWTH");

	private OrganismDetection(String value) {
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
