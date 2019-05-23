package com.optimiza.ehope.lis.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.optimiza.core.base.helper.SearchCriterion;
import com.optimiza.core.base.helper.SearchCriterion.FilterOperator;
import com.optimiza.core.base.service.GenericService;
import com.optimiza.ehope.lis.model.TestCodedResultMapping;
import com.optimiza.ehope.lis.model.TestResult;
import com.optimiza.ehope.lis.repo.TestCodedResultMappingRepo;

/**
 * TestCodedResultMappingService.java
 * 
 * @author Murad Poladian <mpoladian@optimizasolutions.com>
 * @since Jul/11/2018
 * 
 */
@Service("TestCodedResultMappingService")
public class TestCodedResultMappingService extends GenericService<TestCodedResultMapping, TestCodedResultMappingRepo> {

	@Autowired
	private TestCodedResultMappingRepo repo;

	@Override
	protected TestCodedResultMappingRepo getRepository() {
		return repo;
	}

	public TestCodedResultMapping saveTestCodedResultMapping(TestCodedResultMapping testCodedResultMapping) {
		return repo.save(testCodedResultMapping);
	}

	public List<TestCodedResultMapping> saveTestCodedResultMappings(List<TestCodedResultMapping> mappings, TestResult testResult) {
		List<SearchCriterion> filters = new ArrayList<SearchCriterion>();
		filters.add(new SearchCriterion("testResult", testResult, FilterOperator.eq));
		List<TestCodedResultMapping> oldMappings = repo.find(filters, TestCodedResultMapping.class, "testCodedResult");

		for (int i = 0; i < oldMappings.size(); i++) {
			TestCodedResultMapping oldMapping = oldMappings.get(i);
			if (!mappings.contains(oldMapping)) {
				repo.delete(oldMapping);
			}
		}
		for (TestCodedResultMapping mapping : mappings) {
			mapping.setTestResult(testResult);
		}

		return repo.save(mappings);
	}

	public List<TestCodedResultMapping> getTestCodedResultMappingsByResult(TestResult testResult) {
		List<SearchCriterion> filters = new ArrayList<SearchCriterion>();
		filters.add(new SearchCriterion("testResult", testResult, FilterOperator.eq));
		List<TestCodedResultMapping> codedResultMappings = repo.find(filters, TestCodedResultMapping.class,
				"testCodedResult");
		return codedResultMappings;
	}

}
