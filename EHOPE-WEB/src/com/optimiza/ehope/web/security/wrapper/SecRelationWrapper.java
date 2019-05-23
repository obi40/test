package com.optimiza.ehope.web.security.wrapper;

import java.util.List;

import com.optimiza.core.base.entity.BaseWrapper;

/**
 * SecRelationWrapper.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Oct/31/2017
 * 
 *        T: Master Relation (e.g. SecUser)
 *        S, U, V: Relation Tables (e.g. SecUser with SeGroupUser,SecUserRole,...) Max=3, Min=1 of types
 *        If a T does not use all types of relation tables then put anything in the remaining types.
 **/
public class SecRelationWrapper<T, S, U, V> extends BaseWrapper {

	private static final long serialVersionUID = 1L;

	private T master;
	private List<S> relationTableOne;
	private List<U> relationTableTwo;
	private List<V> relationTableThree;

	public T getMaster() {
		return master;
	}

	public void setMaster(T master) {
		this.master = master;
	}

	public List<S> getRelationTableOne() {
		return relationTableOne;
	}

	public void setRelationTableOne(List<S> relationTableOne) {
		this.relationTableOne = relationTableOne;
	}

	public List<U> getRelationTableTwo() {
		return relationTableTwo;
	}

	public void setRelationTableTwo(List<U> relationTableTwo) {
		this.relationTableTwo = relationTableTwo;
	}

	public List<V> getRelationTableThree() {
		return relationTableThree;
	}

	public void setRelationTableThree(List<V> relationTableThree) {
		this.relationTableThree = relationTableThree;
	}

}
