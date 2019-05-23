package com.optimiza.ehope.lis.lkp.helper;

public enum ReportType {
	DEFAULT("DEFAULT"),
	CBC("CBC"),
	NEONATAL("NEONATAL"),
	STOOL("STOOL"),
	ALLERGY("ALLERGY"),
	CULTURE("CULTURE"),
	URINE("URINE"),
	PROTEIN_ELECTRO("PROTEIN_ELECTRO"),
	TRIPLE("TRIPLE");

	private ReportType(String value) {
		this.value = value;
	}

	private String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public static ReportType getByValue(String value) {
		for (ReportType pm : ReportType.values()) {
			if (pm.getValue().equals(value))
				return pm;
		}
		return null;
	}

}
