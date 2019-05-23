package com.optimiza.ehope.lis.repo;

import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.ehope.lis.model.TestQuestion;
import com.optimiza.ehope.lis.model.TestQuestionOption;

public interface TestQuestionOptionRepo extends GenericRepository<TestQuestionOption> {

	@Query("select t from TestQuestionOption t where t.testQuestion = :testQuestion")
	public Set<TestQuestionOption> getByQuestionId(@Param("testQuestion") TestQuestion testQuestion);

}
