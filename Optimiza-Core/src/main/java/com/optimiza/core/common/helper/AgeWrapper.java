package com.optimiza.core.common.helper;

import java.time.temporal.ChronoUnit;

public class AgeWrapper {

	private Long age;
	private ChronoUnit unit;

	public Long getAge() {
		return age;
	}

	public void setAge(Long age) {
		this.age = age;
	}

	public ChronoUnit getUnit() {
		return unit;
	}

	public void setUnit(ChronoUnit unit) {
		this.unit = unit;
	}

	@Override
	public String toString() {
		return age + " " + unit.toString();
	}

}
