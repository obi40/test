package com.optimiza.ehope.lis.helper;

public enum SMSKey {

	BASE_URL("BASE_URL"),
	MOBILE("MOBILE"),
	MESSAGE("MESSAGE");

	private String value;

	private SMSKey(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
