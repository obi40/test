package com.optimiza.core.common.data.model.converter;

import javax.persistence.AttributeConverter;

public class IdAttConverter implements AttributeConverter<String, Long> {

	@Override
	public Long convertToDatabaseColumn(String attribute) {
		if (attribute == null) {
			return null;
		}
		return Long.parseLong(attribute);
	}

	@Override
	public String convertToEntityAttribute(Long dbData) {
		if (dbData == null) {
			throw new IllegalArgumentException("Row can't exist without an Id");
		}
		return Long.toString(dbData);
	}

}
