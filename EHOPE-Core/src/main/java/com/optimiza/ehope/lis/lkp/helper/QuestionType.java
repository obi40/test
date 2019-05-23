package com.optimiza.ehope.lis.lkp.helper;

public enum QuestionType {

	DATE("DATE"),
	DATE_TIME("DATE_TIME"),
	NUMBER("NUMBER"),
	BOOLEAN("BOOLEAN"),
	NARRATIVE("NARRATIVE");

	private QuestionType(String value) {
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