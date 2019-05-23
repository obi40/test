package com.optimiza.ehope.lis.lkp.helper;

public enum AmountType {
	PAT("PAT"),
	INS("INS"),
	SAL("SAL");

	private AmountType(String value) {
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
