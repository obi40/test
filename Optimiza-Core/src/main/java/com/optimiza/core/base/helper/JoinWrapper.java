package com.optimiza.core.base.helper;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.persistence.criteria.Fetch;

public class JoinWrapper<T> implements Map<String, JoinWrapper<T>> {

	private Map<String, JoinWrapper<T>> join;
	private Fetch<String, T> fetch;

	public JoinWrapper() {
		super();
		this.join = new HashMap<String, JoinWrapper<T>>();
	}

	public Map<String, JoinWrapper<T>> getJoin() {
		return join;
	}

	public void setJoin(Map<String, JoinWrapper<T>> join) {
		this.join = join;
	}

	public Fetch<String, T> getFetch() {
		return fetch;
	}

	public void setFetch(Fetch<String, T> fetch) {
		this.fetch = fetch;
	}

	@Override
	public int size() {
		return join.size();
	}

	@Override
	public boolean isEmpty() {
		return join.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return join.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return join.containsValue(value);
	}

	@Override
	public JoinWrapper<T> get(Object key) {
		return join.get(key);
	}

	@Override
	public JoinWrapper<T> put(String key, JoinWrapper<T> value) {
		return join.put(key, value);
	}

	@Override
	public JoinWrapper<T> remove(Object key) {
		return join.remove(key);
	}

	@Override
	public void putAll(Map<? extends String, ? extends JoinWrapper<T>> m) {
		join.putAll(m);
	}

	@Override
	public void clear() {
		join.clear();
	}

	@Override
	public Set<String> keySet() {
		return join.keySet();
	}

	@Override
	public Collection<JoinWrapper<T>> values() {
		return join.values();
	}

	@Override
	public Set<Map.Entry<String, JoinWrapper<T>>> entrySet() {
		return join.entrySet();
	}

}
