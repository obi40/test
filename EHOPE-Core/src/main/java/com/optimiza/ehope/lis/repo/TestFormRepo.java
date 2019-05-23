package com.optimiza.ehope.lis.repo;

import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.ehope.lis.model.TestDefinition;
import com.optimiza.ehope.lis.model.TestForm;

public interface TestFormRepo extends GenericRepository<TestForm> {

	@Query("select t from TestForm t where t.testDefinition = :testDefinition")
	public Set<TestForm> getByTestId(@Param("testDefinition") TestDefinition testDefinition);

}
