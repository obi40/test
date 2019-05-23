package com.optimiza.ehope.lis.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.optimiza.core.base.helper.SearchCriterion;
import com.optimiza.core.base.service.GenericService;
import com.optimiza.core.common.annotation.InterceptorFree;
import com.optimiza.core.common.helper.FilterablePageRequest;
import com.optimiza.ehope.lis.helper.EhopeRights;
import com.optimiza.ehope.lis.model.LabUnit;
import com.optimiza.ehope.lis.repo.LabUnitRepo;

/**
 * LabUnitService.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Oct/12/2017
 **/

@Service("LabUnitService")
public class LabUnitService extends GenericService<LabUnit, LabUnitRepo> {

	@Autowired
	private LabUnitRepo labUnitRepo;

	@InterceptorFree
	public List<LabUnit> findUnitsExcluded(List<SearchCriterion> filters, String... joins) {
		return labUnitRepo.find(filters, LabUnit.class, joins);
	}

	@Override
	protected LabUnitRepo getRepository() {
		return labUnitRepo;
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.VIEW_LAB_UNIT + "')")
	public Page<LabUnit> findLabUnitList(FilterablePageRequest filterablePageRequest) {
		return getRepository().find(filterablePageRequest.getFilters(), filterablePageRequest.getPageRequest(), LabUnit.class);
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.ADD_LAB_UNIT + "')")
	public LabUnit createUnit(LabUnit unit) {
		return labUnitRepo.save(unit);
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.UPD_LAB_UNIT + "')")
	public LabUnit editLabUnit(LabUnit newUnit) {
		return labUnitRepo.save(newUnit);
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.DEL_LAB_UNIT + "')")
	public void deleteLabUnit(Long labUnitId) {
		labUnitRepo.delete(labUnitId);
	}

}
