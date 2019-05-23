package com.optimiza.ehope.lis.lkp.helper;

public enum TransactionType {
	PAYMENT("PAYMENT"),
	CANCEL("CANCEL"),
	RECALCULATE("RECALCULATE"),
	REFUND("REFUND");

	private TransactionType(String value) {
		this.value = value;
	}

	private String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public static TransactionType getByValue(String value) {
		for (TransactionType ppr : TransactionType.values()) {
			if (ppr.getValue().equals(value))
				return ppr;
		}
		return null;
	}
}
