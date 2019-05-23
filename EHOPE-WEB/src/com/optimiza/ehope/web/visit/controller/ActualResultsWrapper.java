package com.optimiza.ehope.web.visit.controller;

import java.util.List;

import com.optimiza.core.base.entity.BaseWrapper;
import com.optimiza.ehope.lis.model.EmrVisit;
import com.optimiza.ehope.lis.model.LabTestActualResult;

public class ActualResultsWrapper extends BaseWrapper {

	private static final long serialVersionUID = 1L;

	private List<LabTestActualResult> actualResults;
	private EmrVisit order;

	public List<LabTestActualResult> getActualResults() {
		return actualResults;
	}

	public void setActualResults(List<LabTestActualResult> actualResults) {
		this.actualResults = actualResults;
	}

	public EmrVisit getOrder() {
		return order;
	}

	public void setOrder(EmrVisit order) {
		this.order = order;
	}

}
