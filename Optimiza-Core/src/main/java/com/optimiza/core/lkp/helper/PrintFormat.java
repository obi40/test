package com.optimiza.core.lkp.helper;

public enum PrintFormat {
	PDF("PDF"),
	RTF("RTF");

	private PrintFormat(String value) {
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
