package com.optimiza.ehope.lis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.optimiza.core.base.service.GenericService;
import com.optimiza.ehope.lis.model.TestCodedResult;
import com.optimiza.ehope.lis.repo.TestCodedResultRepo;

/**
 * TestCodedResultService.java
 * 
 * @author Murad Poladian <mpoladian@optimizasolutions.com>
 * @since Jul/11/2018
 * 
 */
@Service("TestCodedResultService")
public class TestCodedResultService extends GenericService<TestCodedResult, TestCodedResultRepo> {

	@Autowired
	private TestCodedResultRepo repo;

	@Override
	protected TestCodedResultRepo getRepository() {
		return repo;
	}

	public TestCodedResult addTestCodedResult(TestCodedResult testCodedResult) {
		return repo.save(testCodedResult);
	}

	public TestCodedResult editTestCodedResult(TestCodedResult testCodedResult) {
		return repo.save(testCodedResult);
	}

}
