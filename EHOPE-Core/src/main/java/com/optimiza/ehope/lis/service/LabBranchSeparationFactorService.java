package com.optimiza.ehope.lis.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.optimiza.core.base.helper.SearchCriterion;
import com.optimiza.core.base.helper.SearchCriterion.FilterOperator;
import com.optimiza.core.base.service.GenericService;
import com.optimiza.core.common.annotation.InterceptorFree;
import com.optimiza.core.common.util.CollectionUtil;
import com.optimiza.core.common.util.SecurityUtil;
import com.optimiza.ehope.lis.helper.EhopeRights;
import com.optimiza.ehope.lis.model.LabBranch;
import com.optimiza.ehope.lis.model.LabBranchSeparationFactor;
import com.optimiza.ehope.lis.repo.LabBranchSeparationFactorRepo;

/**
 * LabBranchSeparationFactorService.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Jan/07/2018
 **/

@Service("LabBranchSeparationFactorService")
public class LabBranchSeparationFactorService
		extends GenericService<LabBranchSeparationFactor, LabBranchSeparationFactorRepo> {

	@Autowired
	private LabBranchSeparationFactorRepo labBranchSeparationFactorRepo;

	@Autowired
	public EntityManager entityManager;

	@InterceptorFree
	public List<LabBranchSeparationFactor> createBranchFactors(LabBranch branch) {
		List<LabBranchSeparationFactor> branchFactors = getRepository().find(
				Arrays.asList(new SearchCriterion("branchId", branch.getRid(), FilterOperator.eq),
						new SearchCriterion("tenantId", branch.getTenantId(), FilterOperator.eq)),
				LabBranchSeparationFactor.class);
		if (!CollectionUtil.isCollectionEmpty(branchFactors)) {
			return branchFactors;
		}
		List<LabBranchSeparationFactor> defaultFactors = getRepository().find(
				Arrays.asList(new SearchCriterion("branchId", SecurityUtil.DEFAULT_BRANCH, FilterOperator.eq),
						new SearchCriterion("tenantId", SecurityUtil.DEFAULT_TENANT, FilterOperator.eq)),
				LabBranchSeparationFactor.class);
		List<LabBranchSeparationFactor> factors = new ArrayList<>();
		for (LabBranchSeparationFactor factor : defaultFactors) {
			LabBranchSeparationFactor bsf = new LabBranchSeparationFactor();
			BeanUtils.copyProperties(factor, bsf, "rid");
			bsf.setTenantId(branch.getTenantId());
			bsf.setBranchId(branch.getRid());
			factors.add(bsf);
		}

		factors = getRepository().save(factors);
		return factors;
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.DEL_LAB_FACTORS + "')")
	public void deleteAll() {
		List<LabBranchSeparationFactor> factorsByLab = findSeparationFactorByBranch();
		getRepository().delete(factorsByLab);
	}

	/**
	 * Used in branch page.
	 * 
	 * @param branch
	 */
	@InterceptorFree
	public void deleteAllByBranch(Long branchRid) {
		getRepository().deleteAllByBranchId(branchRid);
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.UPD_LAB_FACTORS + "')")
	public List<LabBranchSeparationFactor> updateBranchSeparationFactor(
			List<LabBranchSeparationFactor> labBranchSeparationFactorList) {
		return getRepository().save(labBranchSeparationFactorList);
	}

	/**
	 * Get lab factors by the lab branch.
	 * 
	 * @param labBranchRid
	 * @return
	 */
	@PreAuthorize("hasAuthority('" + EhopeRights.VIEW_LAB_FACTORS + "')")
	public List<LabBranchSeparationFactor> findSeparationFactorByBranch() {
		return getRepository().find(new ArrayList<>(), LabBranchSeparationFactor.class, "labSeparationFactor");
	}

	@Override
	protected LabBranchSeparationFactorRepo getRepository() {
		return labBranchSeparationFactorRepo;
	}

}
