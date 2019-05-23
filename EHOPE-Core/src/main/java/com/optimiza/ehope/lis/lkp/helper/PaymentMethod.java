package com.optimiza.ehope.lis.lkp.helper;

public enum PaymentMethod {
	CASH("CASH"),
	CHEQUE("CHEQUE"),
	CREDIT_CARD("CREDIT_CARD"),
	VOUCHER("VOUCHER");

	private PaymentMethod(String value) {
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