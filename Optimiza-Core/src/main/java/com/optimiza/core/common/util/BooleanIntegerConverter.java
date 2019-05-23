package com.optimiza.core.common.util;

import javax.persistence.AttributeConverter;

/**
 * BooleanToIntegerAttributeConverter.java, Used to convert entity attribute value from integer to boolean and vice versa
 * 
 * @author Wa'el Abu Rahmeh <waburahemh@optimizasolutions.com>
 * @since May/21/2017
 *
 */
public class BooleanIntegerConverter implements AttributeConverter<Boolean, Integer> {

	@Override
	public Integer convertToDatabaseColumn(Boolean attribute) {
		if (attribute == null) {
			return null;
		}
		return attribute ? 1 : 0;
	}

	@Override
	public Boolean convertToEntityAttribute(Integer dbData) {
		if (dbData == null) {
			return null;
		}
		return dbData == 0 ? false : true;
	}

}
