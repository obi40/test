package com.optimiza.ehope.lis.helper;

public enum BalanceTransactionType {

	PATIENT("PATIENT"),
	INSURANCE("INSURANCE"),
	LAB_CASH_DRAWER("LAB_CASH_DRAWER"),
	LAB_SALES("LAB_SALES");

	private String value;

	private BalanceTransactionType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
