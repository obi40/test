package com.optimiza.ehope.lis.repo;

import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.ehope.lis.model.TestDefinition;
import com.optimiza.ehope.lis.model.TestQuestion;

public interface TestQuestionRepo extends GenericRepository<TestQuestion> {

	@Query("select t from TestQuestion t where t.testDefinition = :testDefinition")
	public Set<TestQuestion> getByTestId(@Param("testDefinition") TestDefinition testDefinition);

}
