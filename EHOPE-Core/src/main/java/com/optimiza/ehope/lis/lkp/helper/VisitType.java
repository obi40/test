package com.optimiza.ehope.lis.lkp.helper;

public enum VisitType {
	REFERRAL("REFERRAL"), //REFERRAL IN
	WALK_IN("WALK_IN");

	private VisitType(String value) {
		this.value = value;
	}

	private String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public static VisitType getByValue(String value) {
		for (VisitType vt : VisitType.values()) {
			if (vt.getValue().equals(value))
				return vt;
		}
		return null;
	}

}
