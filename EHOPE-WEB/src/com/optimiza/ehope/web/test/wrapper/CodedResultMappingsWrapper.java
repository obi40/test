package com.optimiza.ehope.web.test.wrapper;

import java.util.List;

import com.optimiza.ehope.lis.model.TestCodedResultMapping;
import com.optimiza.ehope.lis.model.TestResult;

public class CodedResultMappingsWrapper {

	private List<TestCodedResultMapping> mappings;
	private TestResult testResult;

	public List<TestCodedResultMapping> getMappings() {
		return mappings;
	}

	public void setMappings(List<TestCodedResultMapping> mappings) {
		this.mappings = mappings;
	}

	public TestResult getTestResult() {
		return testResult;
	}

	public void setTestResult(TestResult testResult) {
		this.testResult = testResult;
	}

}
