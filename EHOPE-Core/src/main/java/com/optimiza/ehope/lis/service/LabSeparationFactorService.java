package com.optimiza.ehope.lis.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.optimiza.core.base.helper.SearchCriterion;
import com.optimiza.core.base.helper.SearchCriterion.FilterOperator;
import com.optimiza.core.base.service.GenericService;
import com.optimiza.core.common.util.StringUtil;
import com.optimiza.ehope.lis.helper.EhopeRights;
import com.optimiza.ehope.lis.helper.SeparationFactorType;
import com.optimiza.ehope.lis.model.LabBranchSeparationFactor;
import com.optimiza.ehope.lis.model.LabSeparationFactor;
import com.optimiza.ehope.lis.repo.LabSeparationFactorRepo;

/**
 * LabSeparationFactorService.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Jan/07/2018
 **/

@Service("LabSeparationFactorService")
public class LabSeparationFactorService extends GenericService<LabSeparationFactor, LabSeparationFactorRepo> {

	@Autowired
	private LabSeparationFactorRepo repo;
	@Autowired
	private LabBranchSeparationFactorService branchSeparationFactorService;

	@PreAuthorize("hasAuthority('" + EhopeRights.VIEW_LAB_FACTORS + "')")
	public List<LabSeparationFactor> findLabSeparationFactorList() {
		Order orderByName = new Order(Direction.ASC, "fieldName");
		Order orderByFixed = new Order(Direction.DESC, "isFixed");
		Sort sort = new Sort(Arrays.asList(orderByFixed, orderByName));
		return getRepository().find(new ArrayList<>(), LabSeparationFactor.class, sort);
	}

	/**
	 * Get the active factors.
	 * Must keep the sort by rid because in the end when we compare strings so factor order is important.
	 * 
	 * @return LabSeparationFactors
	 */
	public List<LabSeparationFactor> findActiveFactorsByBranch() {
		SearchCriterion isActiveSC = new SearchCriterion("isActive", "1", FilterOperator.eq);
		Sort sort = new Sort(new Order(Direction.ASC, "labSeparationFactor.rid"));//Must keep the sort by rid because in the end when we compare strings so factor order is important

		List<LabSeparationFactor> separationFactors = branchSeparationFactorService	.find(Arrays.asList(isActiveSC),
				LabBranchSeparationFactor.class, sort, "labSeparationFactor").stream()
																					.map(LabBranchSeparationFactor::getLabSeparationFactor)
																					.collect(Collectors.toList());
		separationFactors.removeIf(lsf -> StringUtil.isEmpty(lsf.getFieldName()));//since we are showing a factor without an actual fieldName i.e. 30 min
		//separationFactors.removeIf(lsf -> SeparationFactorType.getByValue(lsf.getFieldName()) == SeparationFactorType.IS_SEPARATE_SAMPLE);//remove this factor because it
		return separationFactors;
	}

	public Boolean isFactorActive(SeparationFactorType separationFactorType) {
		SearchCriterion fieldNameFilter = new SearchCriterion("labSeparationFactor.fieldName", separationFactorType.getValue(),
				FilterOperator.eq);
		SearchCriterion isActiveFilter = new SearchCriterion("isActive", "1", FilterOperator.eq);
		return branchSeparationFactorService.findOne(Arrays.asList(isActiveFilter, fieldNameFilter), LabBranchSeparationFactor.class,
				"labSeparationFactor") != null;
	}

	@Override
	protected LabSeparationFactorRepo getRepository() {
		return repo;
	}

}
