package com.optimiza.ehope.web.lkp.wrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;

import com.optimiza.core.base.entity.BaseWrapper;
import com.optimiza.core.base.helper.SearchCriterion;
import com.optimiza.core.common.helper.FilterablePageRequest;

public class LkpWrapper extends BaseWrapper {

	private static final long serialVersionUID = 1L;

	private String className;
	private FilterablePageRequest filterablePageRequest;
	private String[] joins;

	public LkpWrapper() {
	}

	public List<SearchCriterion> getFilterableFilters() {
		return Optional	.ofNullable(this.filterablePageRequest)
						.map(FilterablePageRequest::getFilters).orElse(new ArrayList<>());
	}

	public Sort getFilterableSort() {
		Sort sort = new Sort(new Order(Direction.ASC, "code"));//default
		return Optional	.ofNullable(this.filterablePageRequest)
						.map(FilterablePageRequest::getSortObject).orElse(sort);
	}

	public String[] getJoins() {
		return Optional.ofNullable(joins).orElse(new String[] {});
	}

	public void setJoins(String[] joins) {
		this.joins = joins;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public FilterablePageRequest getFilterablePageRequest() {
		return filterablePageRequest;
	}

	public void setFilterablePageRequest(FilterablePageRequest filterablePageRequest) {
		this.filterablePageRequest = filterablePageRequest;
	}

}
