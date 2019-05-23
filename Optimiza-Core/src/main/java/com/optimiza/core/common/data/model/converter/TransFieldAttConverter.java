package com.optimiza.core.common.data.model.converter;

import javax.persistence.AttributeConverter;

import com.optimiza.core.common.data.model.TransField;
import com.optimiza.core.common.util.JSONUtil;
import com.optimiza.core.common.util.StringUtil;

/**
 * 
 * TransFieldAttConverter.java is responsible to convert JSON to TransField and vis versa
 * 
 * @author Wa'el Abu Rahmeh <waburahmeh@optimizasolutions.com>
 * @since 21/05/2017
 */
public class TransFieldAttConverter implements AttributeConverter<TransField, String> {

	@Override
	public String convertToDatabaseColumn(TransField attribute) {
		if (attribute == null) {
			return null;
		}
		return JSONUtil.convertTransFieldToJson(attribute);
	}

	@Override
	public TransField convertToEntityAttribute(String dbData) {
		if (StringUtil.isEmpty(dbData)) {
			return new TransField();
		}
		return JSONUtil.convertJsonToTransField(dbData);
	}

}
