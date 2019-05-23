package com.optimiza.ehope.lis.helper;

import com.optimiza.core.common.util.StringUtil;

//this is not for a lkp
public enum SeparationFactorType {

	SPECIMEN_CONTAINER_TYPE("specimenContainerType"),
	LKP_TESTING_METHOD("lkpTestingMethod"),
	SPECIAL_INSTRUCTIONS("specialInstructions"),
	SECTION("section"),
	SPECIMEN_TYPE("specimenType"),
	IS_SEPARATE_SAMPLE("isSeparateSample"),
	DESTINATION("destination"),
	MINUTES_30("MINUTES_30");//it does not have a field

	private String value;

	private SeparationFactorType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	/**
	 * we didn't use valueOf because we are using a custom value for Enum
	 * 
	 * @param value
	 * @return SeparationFactorType
	 */
	public static SeparationFactorType getByValue(String value) {
		if (StringUtil.isEmpty(value)) {
			return null;
		}
		value = value.toLowerCase();
		SeparationFactorType[] all = SeparationFactorType.values();
		for (int i = 0; i < all.length; i++) {
			if (all[i].value.toLowerCase().equals(value)) {
				return all[i];
			}
		}
		return null;
	}

}
