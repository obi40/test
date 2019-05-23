package com.optimiza.ehope.lis.onboarding.helper;

public enum PlanFieldType {

	ORDERS("ORDERS"),
	USERS("USERS");

	private String value;

	private PlanFieldType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public static PlanFieldType getByValue(String value) {
		for (PlanFieldType pt : PlanFieldType.values()) {
			if (pt.getValue().equals(value))
				return pt;
		}
		return null;
	}
}
