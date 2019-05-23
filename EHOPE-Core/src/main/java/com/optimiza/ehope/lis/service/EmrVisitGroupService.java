package com.optimiza.ehope.lis.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.optimiza.core.base.service.GenericService;
import com.optimiza.ehope.lis.model.EmrVisit;
import com.optimiza.ehope.lis.model.EmrVisitGroup;
import com.optimiza.ehope.lis.model.TestGroup;
import com.optimiza.ehope.lis.repo.EmrVisitGroupRepo;

/**
 * EmrVisitGroupService.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Feb/19/2019
 **/

@Service("EmrVisitGroupService")
public class EmrVisitGroupService extends GenericService<EmrVisitGroup, EmrVisitGroupRepo> {

	@Autowired
	private EmrVisitGroupRepo emrVisitGroupRepo;

	public List<EmrVisitGroup> createVisitGroups(List<EmrVisitGroup> visitGroups) {
		return getRepository().save(visitGroups);
	}

	public void deleteAllByTestGroup(TestGroup testGroup) {
		getRepository().deleteAllByTestGroup(testGroup);
	}

	public void deleteAllByEmrVisit(EmrVisit emrVisit) {
		getRepository().deleteAllByEmrVisit(emrVisit);
	}

	@Override
	protected EmrVisitGroupRepo getRepository() {
		return emrVisitGroupRepo;
	}

}
