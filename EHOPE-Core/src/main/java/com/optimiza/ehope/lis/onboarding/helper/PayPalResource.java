package com.optimiza.ehope.lis.onboarding.helper;

public enum PayPalResource {
	//value in small letters
	SALE("sale"),
	AGREEMENT("agreement");

	private String value;

	private PayPalResource(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public static PayPalResource getByValue(String value) {
		for (PayPalResource ppr : PayPalResource.values()) {
			if (ppr.getValue().equals(value))
				return ppr;
		}
		return null;
	}
}
