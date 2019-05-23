package com.optimiza.ehope.lis.repo;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.ehope.lis.model.TestDefinition;
import com.optimiza.ehope.lis.model.TestResult;

public interface TestResultRepo extends GenericRepository<TestResult> {

	@Query("select t from TestResult t where t.testDefinition = :testDefinition")
	public Set<TestResult> getByTestId(@Param("testDefinition") TestDefinition testDefinition);

	@Query("select t from TestResult t where t.loincCode = :loincCode")
	public List<TestResult> getByLoincCode(@Param("loincCode") String loincCode);

	@Query("select tr from TestResult tr where tr.testDefinition IN :testsDefinition")
	public List<TestResult> testsResultFetch(@Param("testsDefinition") List<TestDefinition> testsDefinition);

}
