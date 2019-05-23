package com.optimiza.ehope.lis.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.optimiza.core.base.helper.SearchCriterion;
import com.optimiza.core.base.helper.SearchCriterion.FilterOperator;
import com.optimiza.core.base.service.GenericService;
import com.optimiza.core.common.business.exception.BusinessException;
import com.optimiza.core.common.business.exception.BusinessException.ErrorSeverity;
import com.optimiza.ehope.lis.model.ExtraTest;
import com.optimiza.ehope.lis.model.TestDefinition;
import com.optimiza.ehope.lis.repo.ExtraTestRepo;

@Service("ExtraTestService")
public class ExtraTestService extends GenericService<ExtraTest, ExtraTestRepo> {

	@Autowired
	private ExtraTestRepo repo;

	@Override
	protected ExtraTestRepo getRepository() {
		return repo;
	}

	public ExtraTest addExtraTest(ExtraTest extraTest) {
		return repo.save(extraTest);
	}

	public ExtraTest editExtraTest(ExtraTest extraTest) {
		return repo.save(extraTest);
	}

	public void deleteExtraTest(Long rid) {
		repo.delete(rid);
	}

	public List<ExtraTest> getByTestDefinition(TestDefinition testDefinition) {
		return repo.find(Arrays.asList(new SearchCriterion("test", testDefinition, FilterOperator.eq)), ExtraTest.class);
	}

	public void saveExtraTests(List<ExtraTest> extraTestList, TestDefinition testDefinition) {
		List<ExtraTest> oldExtraTests = getByTestDefinition(testDefinition);

		oldExtraTests.forEach(oldExtraTest ->
			{
				if (!extraTestList.contains(oldExtraTest)) {
					deleteExtraTest(oldExtraTest.getRid());
				}
			});
		extraTestList.forEach(extraTest ->
			{
				if (extraTest.getExtraTest().equals(testDefinition)) {
					throw new BusinessException("A test cannot be its own extra-test!", "selfExtraTestError", ErrorSeverity.ERROR);
				}
				extraTest.setTest(testDefinition);
				repo.save(extraTest);
			});

	}

}
