package com.optimiza.ehope.lis.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.ehope.lis.model.TestRequestForm;
import com.optimiza.ehope.lis.model.TestRequestFormTestDefinition;

@Repository("TestRequestFormTestDefinitionRepo")
public interface TestRequestFormTestDefinitionRepo extends GenericRepository<TestRequestFormTestDefinition> {

	@Query("SELECT t FROM TestRequestFormTestDefinition t WHERE t.testRequestForm = :requestForm")
	public List<TestRequestFormTestDefinition> getByRequestForm(@Param("requestForm") TestRequestForm requestForm);

}
