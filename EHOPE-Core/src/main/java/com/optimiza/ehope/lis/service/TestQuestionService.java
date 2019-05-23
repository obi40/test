package com.optimiza.ehope.lis.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.optimiza.core.base.service.GenericService;
import com.optimiza.core.common.util.CollectionUtil;
import com.optimiza.ehope.lis.model.TestDefinition;
import com.optimiza.ehope.lis.model.TestQuestion;
import com.optimiza.ehope.lis.repo.TestQuestionRepo;

@Service("TestQuestionService")
public class TestQuestionService extends GenericService<TestQuestion, TestQuestionRepo> {

	@Autowired
	private TestQuestionRepo repo;

	@Override
	protected TestQuestionRepo getRepository() {
		return repo;
	}

	public List<TestQuestion> createTestQuestions(List<TestQuestion> testQuestions) {
		if (CollectionUtil.isCollectionEmpty(testQuestions)) {
			return new ArrayList<>();
		}
		repo.delete(testQuestions.stream().filter(tq -> tq.getMarkedForDeletion() && tq.getRid() != null).collect(Collectors.toList()));
		testQuestions.removeIf(TestQuestion::getMarkedForDeletion);
		return repo.save(testQuestions);
	}

	public TestQuestion addTestQuestion(TestQuestion testQuestion) {
		return repo.save(testQuestion);
	}

	public void deleteTestQuestion(Long rid) {
		repo.delete(rid);
	}

	public Set<TestQuestion> getByTestId(TestDefinition testDefinition) {
		return repo.getByTestId(testDefinition);
	}

}
