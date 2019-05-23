package com.optimiza.ehope.web.patient.wrapper;

import java.util.List;

import com.optimiza.core.base.entity.BaseWrapper;
import com.optimiza.ehope.lis.model.EmrVisit;
import com.optimiza.ehope.lis.model.TestDefinition;
import com.optimiza.ehope.lis.model.TestGroup;

public class VisitTestsWrapper extends BaseWrapper {

	private static final long serialVersionUID = 1L;

	private EmrVisit visit;
	private List<TestDefinition> testDefinitionList;
	private List<TestGroup> testGroupList;

	public List<TestGroup> getTestGroupList() {
		return testGroupList;
	}

	public void setTestGroupList(List<TestGroup> testGroupList) {
		this.testGroupList = testGroupList;
	}

	public EmrVisit getVisit() {
		return this.visit;
	}

	public void setVisit(EmrVisit visit) {
		this.visit = visit;
	}

	public List<TestDefinition> getTestDefinitionList() {
		return testDefinitionList;
	}

	public void setTestDefinitionList(List<TestDefinition> testDefinitionList) {
		this.testDefinitionList = testDefinitionList;
	}

}
