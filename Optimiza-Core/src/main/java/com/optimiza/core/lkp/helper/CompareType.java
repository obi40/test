package com.optimiza.core.lkp.helper;

public enum CompareType {
	EQ("EQ"),
	NEQ("NEQ"),
	GT("GT"),
	GTE("GTE"),
	LT("LT"),
	LTE("LTE");

	private CompareType(String value) {
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

//case GT:
//	if (fieldType == FieldType.NUM) {
//		valid = (Long) toTestDefVal > (Long) inSampleTestDefVal;
//	} else if (fieldType == FieldType.DATE) {
//		valid = DateUtil.isAfter((Date) toTestDefVal, (Date) inSampleTestDefVal);
//	}
//	break;
//case GTE:
//	if (fieldType == FieldType.NUM) {
//		valid = (Long) toTestDefVal >= (Long) inSampleTestDefVal;
//	} else if (fieldType == FieldType.DATE) {
//		valid = DateUtil.isAfterOrEqual((Date) toTestDefVal, (Date) inSampleTestDefVal);
//	}
//	break;
//case LT:
//	if (fieldType == FieldType.NUM) {
//		valid = (Long) toTestDefVal < (Long) inSampleTestDefVal;
//	} else if (fieldType == FieldType.DATE) {
//		valid = DateUtil.isBefore((Date) toTestDefVal, (Date) inSampleTestDefVal);
//	}
//	break;
//case LTE:
//	if (fieldType == FieldType.NUM) {
//		valid = (Long) toTestDefVal <= (Long) inSampleTestDefVal;
//	} else if (fieldType == FieldType.DATE) {
//		valid = DateUtil.isBeforeOrEqual((Date) toTestDefVal, (Date) inSampleTestDefVal);
//	}
//	break;