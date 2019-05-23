package com.optimiza.core.lkp.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.optimiza.core.admin.helper.AdminRights;
import com.optimiza.core.base.entity.BaseAuditableTenantedEntity;
import com.optimiza.core.base.helper.SearchCriterion;
import com.optimiza.core.base.helper.SearchCriterion.FilterOperator;
import com.optimiza.core.base.service.GenericService;
import com.optimiza.core.common.util.ReflectionUtil;
import com.optimiza.core.common.util.SecurityUtil;
import com.optimiza.core.lkp.model.LkpMaster;
import com.optimiza.core.lkp.repo.LkpMasterRepo;

/**
 * LkpMasterService.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Dec/12/2017
 **/

@Service("LkpMasterService")
public class LkpMasterService extends GenericService<LkpMaster, LkpMasterRepo> {

	@Autowired
	private LkpMasterRepo lkpMasterRepo;

	@Autowired
	private EntityManager entityManager;

	public LkpMaster createLkpMaster(LkpMaster lkpMaster) {
		return getRepository().save(lkpMaster);
	}

	@PreAuthorize("hasAuthority('" + AdminRights.VIEW_LKP_MANAGEMENT + "')")
	public List<LkpMaster> findLkpMasters() {
		List<LkpMaster> lkpMasters = getRepository().find(Arrays.asList(new SearchCriterion("entity", null, FilterOperator.isnotnull)),
				LkpMaster.class, new Sort(Direction.ASC, "entity"));
		if (SecurityUtil.isApplicationAdmin()) {
			return lkpMasters;
		} else {
			List<LkpMaster> result = new ArrayList<>();
			for (LkpMaster master : lkpMasters) {
				Class<?> masterClass = ReflectionUtil.getEntityClassByName(master.getEntity(), entityManager);
				if (masterClass != null && masterClass.getSuperclass().equals(BaseAuditableTenantedEntity.class)) {
					result.add(master);
				}
			}
			return result;
		}

	}

	@Override
	protected LkpMasterRepo getRepository() {
		return lkpMasterRepo;
	}

}
