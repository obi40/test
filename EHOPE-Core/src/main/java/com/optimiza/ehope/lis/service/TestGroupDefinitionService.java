package com.optimiza.ehope.lis.service;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.optimiza.core.base.service.GenericService;
import com.optimiza.ehope.lis.model.TestDefinition;
import com.optimiza.ehope.lis.model.TestGroup;
import com.optimiza.ehope.lis.model.TestGroupDefinition;
import com.optimiza.ehope.lis.repo.TestGroupDefinitionRepo;

/**
 * TestGroupDefinitionService.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Dec/11/2018
 **/

@Service("TestGroupDefinitionService")
public class TestGroupDefinitionService extends GenericService<TestGroupDefinition, TestGroupDefinitionRepo> {

	@Autowired
	private TestGroupDefinitionRepo testGroupDefinitionRepo;

	public List<TestGroupDefinition> createGroupDefinitions(Collection<TestGroupDefinition> groupDefinitions) {
		return getRepository().save(groupDefinitions);
	}

	public List<TestGroupDefinition> updateGroupDefinitions(Collection<TestGroupDefinition> groupDefinitions) {
		return getRepository().save(groupDefinitions);
	}

	public void deleteAllByTestGroup(TestGroup testGroup) {
		getRepository().deleteAllByTestGroup(testGroup);
	}

	public void deleteAllByTestDefinition(TestDefinition testDefinition) {
		getRepository().deleteAllByTestDefinition(testDefinition);
	}

	@Override
	protected TestGroupDefinitionRepo getRepository() {
		return this.testGroupDefinitionRepo;
	}

}
