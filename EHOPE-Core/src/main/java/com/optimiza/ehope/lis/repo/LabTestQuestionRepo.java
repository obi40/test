package com.optimiza.ehope.lis.repo;

import org.springframework.stereotype.Repository;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.ehope.lis.model.TestQuestion;

@Repository("TestQuestionRepo")
public interface LabTestQuestionRepo extends GenericRepository<TestQuestion> {

	//	@Query("select q from LabTestQuestion q  where q.labTestSetupLine IN :testDefinition")
	//	public List<TestQuestion> findTestQuestions(@Param("testDefinition") List<TestDefinition> testDefinition);

}
