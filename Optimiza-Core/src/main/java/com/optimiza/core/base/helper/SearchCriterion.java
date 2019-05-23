package com.optimiza.core.base.helper;

import java.util.Arrays;
import java.util.List;

import com.optimiza.core.base.entity.BaseWrapper;

public class SearchCriterion extends BaseWrapper {

	private static final long serialVersionUID = 1L;

	private String field;
	private Object value;
	private FilterOperator operator;
	private JunctionOperator junctionOperator;

	public SearchCriterion() {
		super();
		this.junctionOperator = JunctionOperator.And;
	}

	public SearchCriterion(String field, Object value, FilterOperator operator) {
		super();
		this.field = field;
		this.value = value;
		this.operator = operator;
		this.junctionOperator = JunctionOperator.And;
	}

	public SearchCriterion(String field, Object value, FilterOperator operator, JunctionOperator junctionOperator) {
		super();
		this.field = field;
		this.value = value;
		this.operator = operator;
		this.junctionOperator = junctionOperator;
	}

	public enum FilterOperator {
		eq,
		neq,
		isnull,
		isnotnull,
		lt,
		lte,
		gt,
		gte,
		//the following are only for strings
		startswith,
		endswith,
		contains,
		doesnotcontain,
		isempty,
		isnotempty,
		//used for lists only
		in;

	}

	public enum JunctionOperator {
		And,
		Or;
	}

	public static List<SearchCriterion> generateRidFilter(Long rid, FilterOperator filterOperator) {
		return Arrays.asList(new SearchCriterion("rid", rid, filterOperator));
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public FilterOperator getOperator() {
		return operator;
	}

	public void setOperator(FilterOperator operator) {
		this.operator = operator;
	}

	public JunctionOperator getJunctionOperator() {
		return junctionOperator;
	}

	public void setJunctionOperator(JunctionOperator junctionOperator) {
		this.junctionOperator = junctionOperator;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((field == null) ? 0 : field.hashCode());
		result = prime * result + ((junctionOperator == null) ? 0 : junctionOperator.hashCode());
		result = prime * result + ((operator == null) ? 0 : operator.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SearchCriterion other = (SearchCriterion) obj;
		if (field == null) {
			if (other.field != null)
				return false;
		} else if (!field.equals(other.field))
			return false;
		if (junctionOperator != other.junctionOperator)
			return false;
		if (operator != other.operator)
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SearchCriterion [field=" + field + ", value=" + value + ", operator=" + operator + ", junctionOperator=" + junctionOperator
				+ "]";
	}

}
