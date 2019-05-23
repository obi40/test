package com.optimiza.ehope.lis.service;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.optimiza.core.base.service.GenericService;
import com.optimiza.ehope.lis.model.TestQuestion;
import com.optimiza.ehope.lis.model.TestQuestionOption;
import com.optimiza.ehope.lis.repo.TestQuestionOptionRepo;

@Service("TestQuestionOptionService")
public class TestQuestionOptionService extends GenericService<TestQuestionOption, TestQuestionOptionRepo> {

	@Autowired
	private TestQuestionOptionRepo repo;

	@Override
	protected TestQuestionOptionRepo getRepository() {
		return repo;
	}

	public TestQuestionOption addTestQuestionOption(TestQuestionOption testQuestionOption) {
		return repo.save(testQuestionOption);
	}

	public TestQuestionOption editTestQuestionOption(TestQuestionOption testQuestionOption) {
		return repo.save(testQuestionOption);
	}

	public void deleteTestQuestionOption(Long rid) {
		repo.delete(rid);
	}

	public Set<TestQuestionOption> getByQuestionId(TestQuestion testQuestion) {
		return repo.getByQuestionId(testQuestion);
	}

}
