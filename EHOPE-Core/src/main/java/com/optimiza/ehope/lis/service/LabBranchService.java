package com.optimiza.ehope.lis.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.optimiza.core.base.helper.SearchCriterion;
import com.optimiza.core.base.service.GenericService;
import com.optimiza.core.common.annotation.InterceptorFree;
import com.optimiza.core.common.util.ReflectionUtil;
import com.optimiza.ehope.lis.helper.EhopeRights;
import com.optimiza.ehope.lis.model.LabBranch;
import com.optimiza.ehope.lis.model.SysSerial;
import com.optimiza.ehope.lis.repo.LabBranchRepo;

@Service("LabBranchService")
public class LabBranchService extends GenericService<LabBranch, LabBranchRepo> {

	@Autowired
	private LabBranchRepo repo;
	@Autowired
	private LabBranchSeparationFactorService branchSeparationFactorService;
	@Autowired
	private SysSerialService sysSerialService;
	@Autowired
	public EntityManager entityManager;

	@Override
	protected LabBranchRepo getRepository() {
		return repo;
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.ADD_BRANCH + "')")
	public List<LabBranch> createBranch(List<LabBranch> branches) {
		for (LabBranch branch : branches) {
			branch.setBalance(BigDecimal.ZERO);
		}
		branches = getRepository().save(branches);
		List<SysSerial> tenantSerials = sysSerialService.find(new ArrayList<>(), SysSerial.class, "serialType", "serialFormat", "labBranch")
														.stream().filter(ss -> ss.getLabBranch() == null).collect(Collectors.toList());
		for (LabBranch branch : branches) {
			ReflectionUtil.disableIntercepterFilters(entityManager, true, true,
					() -> branchSeparationFactorService.createBranchFactors(branch));
			List<SysSerial> branchSerials = new ArrayList<>();
			for (SysSerial ss : tenantSerials) {
				SysSerial dummySS = new SysSerial();
				dummySS.setIsBranchLevel(Boolean.FALSE);
				dummySS.setLabBranch(branch);
				dummySS.setSerialFormat(ss.getSerialFormat());
				dummySS.setSerialType(ss.getSerialType());
				dummySS.setCurrentValue(0L);
				dummySS.setDelimiter("-");
				branchSerials.add(dummySS);
			}
			sysSerialService.createSerialNoAuth(branchSerials);
		}
		return branches;
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.UPD_BRANCH + "')")
	public List<LabBranch> updateBranch(List<LabBranch> branches) {
		return repo.save(branches);
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.UPD_BRANCH + "')")
	public LabBranch updateBranch(LabBranch branches) {
		return repo.save(branches);
	}

	//	@PreAuthorize("hasAuthority('" + EhopeRights.DEL_BRANCH + "')")
	//	public void deleteBranch(Long branchRid) {
	//		balanceService.deleteBalance(BalanceType.LAB_CASH_DRAWER, branchRid);
	//		balanceService.deleteBalance(BalanceType.LAB_SALES, branchRid);
	//		sectionBranchService.deleteAllByLabBranch(branchRid);
	//		branchSeparationFactorService.deleteAllByBranch(branchRid);
	//		repo.delete(branchRid);
	//	}

	@PreAuthorize("hasAuthority('" + EhopeRights.ACTIVATE_BRANCH + "')")
	public LabBranch activateBranch(LabBranch branch) {
		branch.setIsActive(Boolean.TRUE);
		return getRepository().save(branch);

	}

	@PreAuthorize("hasAuthority('" + EhopeRights.DEACTIVATE_BRANCH + "')")
	public LabBranch deactivateBranch(LabBranch branch) {
		branch.setIsActive(Boolean.FALSE);
		return getRepository().save(branch);
	}

	public List<LabBranch> findBranchList(List<SearchCriterion> searchCriterionList, Sort sort, String... joins) {
		return getRepository().find(searchCriterionList, LabBranch.class, sort, joins);
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.VIEW_BRANCH + "')")
	public List<LabBranch> getBranches() {
		return getRepository().find(new ArrayList<>(), LabBranch.class, new Sort(Direction.ASC, "rid"), "city", "country");
	}

	@InterceptorFree
	public List<LabBranch> findBranchesExcluded(List<SearchCriterion> searchCriterionList, Sort sort, String... joins) {
		return getRepository().find(searchCriterionList, LabBranch.class, sort, joins);
	}
}
