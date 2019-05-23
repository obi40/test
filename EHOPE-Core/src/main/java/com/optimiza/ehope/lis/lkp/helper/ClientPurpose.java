package com.optimiza.ehope.lis.lkp.helper;

public enum ClientPurpose {

	SOURCE("SOURCE"),
	DESTINATION("DESTINATION");

	private String value;

	private ClientPurpose(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
