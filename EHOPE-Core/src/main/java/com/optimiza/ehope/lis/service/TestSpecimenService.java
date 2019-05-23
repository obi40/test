package com.optimiza.ehope.lis.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.optimiza.core.base.service.GenericService;
import com.optimiza.core.common.business.exception.BusinessException;
import com.optimiza.core.common.business.exception.BusinessException.ErrorSeverity;
import com.optimiza.ehope.lis.model.TestDefinition;
import com.optimiza.ehope.lis.model.TestSpecimen;
import com.optimiza.ehope.lis.repo.TestSpecimenRepo;

@Service("TestSpecimenService")
public class TestSpecimenService extends GenericService<TestSpecimen, TestSpecimenRepo> {

	@Autowired
	private TestSpecimenRepo repo;

	@Override
	protected TestSpecimenRepo getRepository() {
		return repo;
	}

	public List<TestSpecimen> saveTestSpecimens(List<TestSpecimen> testSpecimens) {
		List<TestSpecimen> savedSpecimens = new ArrayList<TestSpecimen>();
		Integer defaults = 0;
		for (TestSpecimen testSpecimen : testSpecimens) {
			if (testSpecimen.getMarkedForDeletion()) {
				if (testSpecimen.getRid() != null) {
					repo.delete(testSpecimen.getRid());
				}
				continue;
			} else if (testSpecimen.getIsDefault()) {
				defaults++;
			}
			savedSpecimens.add(repo.save(testSpecimen));
		}
		if (defaults == 0) {
			throw new BusinessException("The default specimen should exist", "defaultSpecimenRequired", ErrorSeverity.ERROR);
		} else if (defaults > 1) {
			throw new BusinessException("Only one default specimen is allowed", "onlyOneDefaultSpecimen", ErrorSeverity.ERROR);
		}

		return savedSpecimens;
	}

	public TestSpecimen saveTestSpecimen(TestSpecimen testSpecimen) {
		return repo.save(testSpecimen);
	}

	public TestSpecimen addTestSpecimen(TestSpecimen testSpecimen) {
		return repo.save(testSpecimen);
	}

	public TestSpecimen editTestSpecimen(TestSpecimen testSpecimen) {
		return repo.save(testSpecimen);
	}

	public void deleteTestSpecimen(Long id) {
		repo.delete(id);
	}

	public void deleteAllTestSpecimens(List<TestSpecimen> specimens) {
		repo.delete(specimens);
	}

	public List<TestSpecimen> getByTestId(TestDefinition testDefinition) {
		return repo.getByTestId(testDefinition);
	}

}
