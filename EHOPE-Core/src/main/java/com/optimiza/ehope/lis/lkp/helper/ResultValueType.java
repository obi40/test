package com.optimiza.ehope.lis.lkp.helper;

public enum ResultValueType {
	NAR("NAR"),
	CE("CE"),
	QN("QN"),
	QN_SC("QN_SC"),
	QN_QL("QN_QL"),
	ORG("ORG"),
	RATIO("RATIO");

	private ResultValueType(String value) {
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