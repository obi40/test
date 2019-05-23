package com.optimiza.core.common.util;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.optimiza.core.common.business.exception.BusinessException;
import com.optimiza.core.common.data.model.FlexField;
import com.optimiza.core.common.data.model.TransField;
import com.optimiza.core.lkp.model.ComTenantLanguage;

public class JSONUtil {

	/**
	 * convert any object or list to JSON
	 *
	 * @param object
	 * @return String
	 */
	public static String convertObjectToJSON(Object object) {
		ObjectMapper mapper = new ObjectMapper();

		Hibernate5Module hbm = new Hibernate5Module();
		hbm.disable(Hibernate5Module.Feature.SERIALIZE_IDENTIFIER_FOR_LAZY_NOT_LOADED_OBJECTS);

		mapper.registerModule(hbm);
		mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
		try {
			return (mapper.writeValueAsString(object));
		} catch (IOException e) {
			e.printStackTrace();
			throw new BusinessException(e.getMessage());
		}
	}

	/**
	 * convert JSON on jsonString to Object of type targetClass
	 *
	 * @param jsonString
	 * @param targetClass
	 * @return T Object of type targetClass
	 */
	public static <T> T convertJSONToObject(String jsonString, Class<T> targetClass) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
		mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
		try {
			return (mapper.readValue(jsonString, targetClass));
		} catch (IOException e) {
			e.printStackTrace();
			throw new BusinessException(e.getMessage());
		}
	}

	/**
	 * convert JSON in jsonString to List of type targetClass
	 *
	 * @param jsonString
	 * @param targetClass
	 * @return List<targetClass>
	 */
	public static <T> List<T> convertJSONToList(String jsonString, Class<T> targetClass) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
		mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
		try {
			return (mapper.readValue(jsonString, mapper.getTypeFactory().constructCollectionType(List.class, targetClass)));
		} catch (IOException e) {
			e.printStackTrace();
			throw new BusinessException(e.getMessage());
		}
	}

	/**
	 * convert JSON in jsonString to Map
	 *
	 * @param jsonString
	 * @param keyClass
	 * @param valueClass
	 * @return Map<keyClass, valueClass>
	 */
	public static <T1, T2> Map<T1, T2> convertJSONToMap(String jsonString, Class<T1> keyClass, Class<T2> valueClass) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
		try {
			return mapper.readValue(jsonString, mapper.getTypeFactory().constructMapType(Map.class, keyClass, valueClass));
		} catch (IOException e) {
			e.printStackTrace();
			throw new BusinessException(e.getMessage());
		}
	}

	public static <T1, T2> String convertMapToJSON(Map<T1, T2> map) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);//ignore writing keys with null values
		try {
			return mapper.writeValueAsString(map);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new BusinessException(e.getMessage());
		}
	}

	public static String convertTransFieldToJson(TransField transField) {
		return convertMapToJSON(transField);
	}

	public static TransField convertJsonToTransField(String json) {
		TransField transField = new TransField();
		transField.putAll(convertJSONToMap(json, String.class, String.class));
		return transField;
	}

	//TODO check this later, was used for transfield but it made things dirty
	public static Map<String, String> convertJSONToTransFieldHelper(String jsonString) {
		Map<String, String> mapToReturn = convertJSONToMap(jsonString, String.class, String.class);
		List<ComTenantLanguage> langs = SecurityUtil.getCurrentUser().getTenantLanguages();
		String primaryLang = "en_us"; //fallback
		for (ComTenantLanguage lang : langs) {
			if (lang.getIsPrimary()) {
				primaryLang = lang.getComLanguage().getLocale();
				break;
			}
		}
		for (ComTenantLanguage lang : langs) {
			String langKey = lang.getComLanguage().getLocale();
			if (mapToReturn.get(langKey) == null) {
				mapToReturn.put(langKey, mapToReturn.get(primaryLang));
			}
		}
		return mapToReturn;
	}

	public static String convertFlexFieldToJson(FlexField flexField) {
		return convertObjectToJSON(flexField);
	}

	public static FlexField convertJsonToFlexField(String json) {
		return convertJSONToObject(json, FlexField.class);
	}

}
