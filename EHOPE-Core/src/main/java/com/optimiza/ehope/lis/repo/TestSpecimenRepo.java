package com.optimiza.ehope.lis.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.ehope.lis.model.TestDefinition;
import com.optimiza.ehope.lis.model.TestSpecimen;

public interface TestSpecimenRepo extends GenericRepository<TestSpecimen> {

	@Query("select t from TestSpecimen t where t.testDefinition = :testDefinition")
	public List<TestSpecimen> getByTestId(@Param("testDefinition") TestDefinition testDefinition);

}
