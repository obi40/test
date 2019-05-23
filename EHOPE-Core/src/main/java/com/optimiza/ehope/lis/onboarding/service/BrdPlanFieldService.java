package com.optimiza.ehope.lis.onboarding.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.optimiza.core.base.helper.SearchCriterion;
import com.optimiza.core.base.service.GenericService;
import com.optimiza.ehope.lis.onboarding.model.BrdPlanField;
import com.optimiza.ehope.lis.onboarding.repo.BrdPlanFieldRepo;

/**
 * PlanFieldService.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since May/20/2018
 **/

@Service("BrdPlanFieldService")
public class BrdPlanFieldService extends GenericService<BrdPlanField, BrdPlanFieldRepo> {

	@Autowired
	private BrdPlanFieldRepo planFieldRepo;

	public List<BrdPlanField> findPlanFields(List<SearchCriterion> filters, Sort sort, String... joins) {
		return getRepository().find(filters, BrdPlanField.class, sort, joins);
	}

	@Override
	protected BrdPlanFieldRepo getRepository() {
		return this.planFieldRepo;
	}

}
