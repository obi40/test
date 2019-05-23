package com.optimiza.ehope.lis.helper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum Comparator {

	lt("<"),
	lte("<="),
	gt(">"),
	gte(">=");

	private String value;

	private Comparator(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static Comparator fromString(String value) {
		for (Comparator comparator : Comparator.values()) {
			if (comparator.value.equalsIgnoreCase(value)) {
				return comparator;
			}
		}

		if (value == null) {
			return null;
		}

		Pattern r = Pattern.compile("^up ?to ?$", Pattern.CASE_INSENSITIVE);
		Matcher m = r.matcher(value);
		if (m.find()) {
			return Comparator.lte;
		}

		return null;
	}

}
