package com.optimiza.ehope.web.actualTest.wrapper;

import java.util.List;
import java.util.Map;

import com.optimiza.core.base.entity.BaseWrapper;

public class UpdateSamplesWrapper extends BaseWrapper {

	private static final long serialVersionUID = 1L;

	private Long visitRid;
	private Map<Long, List<Long>> samplesTests;
	private Map<Long, Map<Long, Object>> testFactorValues;

	public Map<Long, Map<Long, Object>> getTestFactorValues() {
		return testFactorValues;
	}

	public void setTestFactorValues(Map<Long, Map<Long, Object>> testFactorValues) {
		this.testFactorValues = testFactorValues;
	}

	public Long getVisitRid() {
		return visitRid;
	}

	public void setVisitRid(Long visitRid) {
		this.visitRid = visitRid;
	}

	public Map<Long, List<Long>> getSamplesTests() {
		return samplesTests;
	}

	public void setSamplesTests(Map<Long, List<Long>> samplesTests) {
		this.samplesTests = samplesTests;
	}

}
