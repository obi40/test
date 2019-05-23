/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.optimiza.core.common.data.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * The FlexField class implements the logic of flex-field.
 *
 * @author Wa'el Abu Rahmeh <waburahmeh@optimizasolutions.com>
 * @version 1.0
 * @since May 21, 2017
 */
public class FlexField implements Map<String, String> {

	protected Map<String, String> flexFields = new HashMap();
	private static final String EMPTY_FIELD = null;

	@Override
	public int size() {
		return flexFields.size();
	}

	@Override
	public boolean isEmpty() {
		return flexFields.isEmpty();
	}

	@Override
	@SuppressWarnings("element-type-mismatch")
	public boolean containsKey(Object key) {
		return flexFields.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return flexFields.containsValue(value);
	}

	@Override
	public String get(Object key) {

		if (flexFields.get(key) == null) {
			flexFields.put((String) key, EMPTY_FIELD);
		}

		return flexFields.get(key);
	}

	@Override
	public String put(String key, String value) {

		return flexFields.put(key, value);

	}

	@Override
	public String remove(Object key) {
		return flexFields.remove(key);
	}

	@Override
	public void putAll(Map m) {
		flexFields.putAll(m);

	}

	@Override
	public void clear() {
		flexFields.clear();
	}

	@Override
	public Set keySet() {
		return flexFields.keySet();
	}

	@Override
	public Collection values() {
		return flexFields.values();
	}

	@Override
	public Set entrySet() {
		return flexFields.entrySet();

	}

}
