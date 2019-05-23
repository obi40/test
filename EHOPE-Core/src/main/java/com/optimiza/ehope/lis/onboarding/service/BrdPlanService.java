package com.optimiza.ehope.lis.onboarding.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.optimiza.core.base.helper.SearchCriterion;
import com.optimiza.core.base.service.GenericService;
import com.optimiza.ehope.lis.onboarding.model.BrdPlan;
import com.optimiza.ehope.lis.onboarding.repo.BrdPlanRepo;

/**
 * PlanService.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since May/20/2018
 **/

@Service("BrdPlanService")
public class BrdPlanService extends GenericService<BrdPlan, BrdPlanRepo> {

	@Autowired
	private BrdPlanRepo planRepo;

	public List<BrdPlan> findPlanList(List<SearchCriterion> searchCriterionList, Sort sort, String... joins) {
		return getRepository().find(searchCriterionList, BrdPlan.class, sort, joins);
	}

	@Override
	protected BrdPlanRepo getRepository() {
		return this.planRepo;
	}

}
