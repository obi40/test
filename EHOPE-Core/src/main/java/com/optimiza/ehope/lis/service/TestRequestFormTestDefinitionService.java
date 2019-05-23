package com.optimiza.ehope.lis.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.optimiza.core.base.service.GenericService;
import com.optimiza.ehope.lis.model.TestRequestForm;
import com.optimiza.ehope.lis.model.TestRequestFormTestDefinition;
import com.optimiza.ehope.lis.repo.TestRequestFormTestDefinitionRepo;

@Service("TestRequestFormTestDefinitionService")
public class TestRequestFormTestDefinitionService extends GenericService<TestRequestFormTestDefinition, TestRequestFormTestDefinitionRepo> {

	@Autowired
	private TestRequestFormTestDefinitionRepo repo;

	@Override
	protected TestRequestFormTestDefinitionRepo getRepository() {
		return repo;
	}

	public TestRequestFormTestDefinition addTestRequestFormTestDefinition(TestRequestFormTestDefinition testRequestFormTestDefinition) {
		return repo.save(testRequestFormTestDefinition);
	}

	public List<TestRequestFormTestDefinition> addTestRequestFormTestDefinitions(
			List<TestRequestFormTestDefinition> testRequestFormTestDefinitions) {
		return repo.save(testRequestFormTestDefinitions);
	}

	public TestRequestFormTestDefinition editTestRequestFormTestDefinition(TestRequestFormTestDefinition testRequestFormTestDefinition) {
		return repo.save(testRequestFormTestDefinition);
	}

	public List<TestRequestFormTestDefinition> getByRequestForm(TestRequestForm requestForm) {
		return repo.getByRequestForm(requestForm);
	}

	public void deleteTestRequestFormTestDefinition(TestRequestFormTestDefinition testRequestFormTestDefinition) {
		repo.delete(testRequestFormTestDefinition);
	}

}
