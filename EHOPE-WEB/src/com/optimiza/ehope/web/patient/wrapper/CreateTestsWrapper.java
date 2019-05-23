package com.optimiza.ehope.web.patient.wrapper;

import java.util.List;

import com.optimiza.core.base.entity.BaseWrapper;
import com.optimiza.ehope.lis.model.TestDefinition;
import com.optimiza.ehope.lis.model.TestGroup;

public class CreateTestsWrapper extends BaseWrapper {

	private static final long serialVersionUID = 1L;

	private Long visitRid;
	private List<TestDefinition> testDefinitionList;
	private List<TestGroup> testGroupList;

	public List<TestGroup> getTestGroupList() {
		return testGroupList;
	}

	public void setTestGroupList(List<TestGroup> testGroupList) {
		this.testGroupList = testGroupList;
	}

	public Long getVisitRid() {
		return visitRid;
	}

	public void setVisitRid(Long visitRid) {
		this.visitRid = visitRid;
	}

	public List<TestDefinition> getTestDefinitionList() {
		return testDefinitionList;
	}

	public void setTestDefinitionList(List<TestDefinition> testDefinitionList) {
		this.testDefinitionList = testDefinitionList;
	}

}
