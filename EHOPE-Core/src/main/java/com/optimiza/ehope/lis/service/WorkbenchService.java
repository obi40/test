
package com.optimiza.ehope.lis.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.optimiza.core.base.helper.SearchCriterion;
import com.optimiza.core.base.helper.SearchCriterion.FilterOperator;
import com.optimiza.core.base.service.GenericService;
import com.optimiza.core.common.annotation.InterceptorFree;
import com.optimiza.core.common.business.exception.BusinessException;
import com.optimiza.core.common.business.exception.BusinessException.ErrorSeverity;
import com.optimiza.core.common.helper.FilterablePageRequest;
import com.optimiza.core.common.util.SecurityUtil;
import com.optimiza.ehope.lis.helper.EhopeRights;
import com.optimiza.ehope.lis.model.Workbench;
import com.optimiza.ehope.lis.repo.WorkbenchRepo;

/**
 * WorkbenchService.java
 * 
 * @author Murad Poladian <mpoladian@optimizasolutions.com>
 * @since Sep/18/2018
 **/
@Service("WorkbenchService")
public class WorkbenchService extends GenericService<Workbench, WorkbenchRepo> {

	@Autowired
	private WorkbenchRepo repo;

	@PreAuthorize("hasAuthority('" + EhopeRights.ADD_WORKBENCH + "')")
	public Workbench createWorkbench(Workbench workbench) {
		return repo.save(workbench);
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.UPD_WORKBENCH + "')")
	public Workbench updateWorkbench(Workbench workbench) {
		return repo.save(workbench);
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.DEL_WORKBENCH + "')")
	public void deleteWorkbench(Long workbenchRid) {
		repo.delete(workbenchRid);
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.VIEW_WORKBENCH + "')")
	public Page<Workbench> getWorkbenchPage(FilterablePageRequest filterablePageRequest) {
		return repo.find(filterablePageRequest.getFilters(), filterablePageRequest.getPageRequest(), Workbench.class);
	}

	public void deleteAllInBatch() {
		repo.deleteAllInBatch();
	}

	@InterceptorFree
	@PreAuthorize("hasAuthority('" + EhopeRights.VIEW_WORKBENCH + "')")
	public List<Workbench> getWorkbenchList(Long sourceBranchRid) {
		Long branchRid;
		Long tenantRid = SecurityUtil.getCurrentUser().getTenantId();
		if (SecurityUtil.isBranchIdAllowed(sourceBranchRid)) {
			branchRid = sourceBranchRid;
		} else {
			throw new BusinessException("Source branch is not the same as the user branch!", "sourceBranchIsNotUserBranch",
					ErrorSeverity.ERROR);
		}
		return repo.find(Arrays.asList(
				new SearchCriterion("branchId", branchRid, FilterOperator.eq),
				new SearchCriterion("tenantId", tenantRid, FilterOperator.eq)), Workbench.class);
	}

	@Override
	protected WorkbenchRepo getRepository() {
		return repo;
	}

}
