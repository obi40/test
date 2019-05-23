package com.optimiza.ehope.lis.lkp.helper;

public enum SectionType {
	MICROBIOLOGY("MICROBIOLOGY"),
	ALLERGY("ALLERGY");

	private SectionType(String value) {
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
