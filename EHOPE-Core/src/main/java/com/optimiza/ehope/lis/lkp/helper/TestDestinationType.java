package com.optimiza.ehope.lis.lkp.helper;

public enum TestDestinationType {

	LOCAL("LOCAL"),
	WORKBENCH("WORKBENCH"),
	ACCULAB("ACCULAB"),
	EXTERNAL("EXTERNAL");

	private String value;

	private TestDestinationType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static TestDestinationType getByValue(String value) {
		for (TestDestinationType it : TestDestinationType.values()) {
			if (it.getValue().equals(value))
				return it;
		}
		return null;
	}

}
