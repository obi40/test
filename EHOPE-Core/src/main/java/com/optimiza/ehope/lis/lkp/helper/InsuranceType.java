package com.optimiza.ehope.lis.lkp.helper;

public enum InsuranceType {
	TPA("TPA"),
	INSURANCE("INSURANCE"),
	SELF_FUNDED("SELF_FUNDED"),
	LAB("LAB"),
	LAB_NETWORK("LAB_NETWORK"),
	PUBLIC_SECTOR("PUBLIC_SECTOR"),
	INDIVIDUALS("INDIVIDUALS");

	private InsuranceType(String value) {
		this.value = value;
	}

	private String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public static InsuranceType getByValue(String value) {
		for (InsuranceType it : InsuranceType.values()) {
			if (it.getValue().equals(value))
				return it;
		}
		return null;
	}

}
