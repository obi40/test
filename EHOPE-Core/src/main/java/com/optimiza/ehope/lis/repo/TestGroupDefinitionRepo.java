package com.optimiza.ehope.lis.repo;

import org.springframework.stereotype.Repository;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.ehope.lis.model.TestDefinition;
import com.optimiza.ehope.lis.model.TestGroup;
import com.optimiza.ehope.lis.model.TestGroupDefinition;

/**
 * TestGroupDefinitionRepo.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Dec/11/2018
 **/

@Repository("TestGroupDefinitionRepo")
public interface TestGroupDefinitionRepo extends GenericRepository<TestGroupDefinition> {

	void deleteAllByTestGroup(TestGroup testGroup);

	void deleteAllByTestDefinition(TestDefinition testDefinition);
}
