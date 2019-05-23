package com.optimiza.ehope.lis.service;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.optimiza.core.base.service.GenericService;
import com.optimiza.core.common.helper.FilterablePageRequest;
import com.optimiza.ehope.lis.model.TestGroup;
import com.optimiza.ehope.lis.model.TestGroupDetail;
import com.optimiza.ehope.lis.repo.TestGroupDetailRepo;

/**
 * TestGroupDetailService.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Jan/24/2019
 **/

@Service("TestGroupDetailService")
public class TestGroupDetailService extends GenericService<TestGroupDetail, TestGroupDetailRepo> {

	@Autowired
	private TestGroupDetailRepo testGroupDetailsRepo;

	public List<TestGroupDetail> getTestGroupDetails(FilterablePageRequest filterablePageRequest) {

		return getRepository().find(filterablePageRequest.getFilters(), TestGroupDetail.class,
				filterablePageRequest.getSortObject(),
				"group", "priceList");
	}

	public List<TestGroupDetail> createGroupDetails(Collection<TestGroupDetail> groupDetails) {
		return getRepository().save(groupDetails);
	}

	public List<TestGroupDetail> updateGroupDetails(Collection<TestGroupDetail> groupDetails) {
		return getRepository().save(groupDetails);
	}

	public void deleteGroupDetails(List<TestGroupDetail> groupDetails) {
		getRepository().delete(groupDetails);
	}

	public void deleteAllByGroup(TestGroup testGroup) {
		getRepository().deleteAllByGroup(testGroup);
	}

	@Override
	protected TestGroupDetailRepo getRepository() {
		return testGroupDetailsRepo;
	}

}
