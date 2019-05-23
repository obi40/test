package com.optimiza.ehope.lis.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.optimiza.core.base.service.GenericService;
import com.optimiza.core.common.util.CollectionUtil;
import com.optimiza.ehope.lis.model.TestDisclaimer;
import com.optimiza.ehope.lis.repo.TestDisclaimerRepo;

@Service("TestDisclaimerService")
public class TestDisclaimerService extends GenericService<TestDisclaimer, TestDisclaimerRepo> {

	@Autowired
	private TestDisclaimerRepo repo;

	@Override
	protected TestDisclaimerRepo getRepository() {
		return repo;
	}

	public List<TestDisclaimer> createTestDisclaimers(List<TestDisclaimer> testDisclaimers) {
		if (CollectionUtil.isCollectionEmpty(testDisclaimers)) {
			return new ArrayList<>();
		}
		repo.delete(testDisclaimers.stream().filter(td -> td.getMarkedForDeletion() && td.getRid() != null).collect(Collectors.toList()));
		testDisclaimers.removeIf(TestDisclaimer::getMarkedForDeletion);
		return repo.save(testDisclaimers);
	}

}
