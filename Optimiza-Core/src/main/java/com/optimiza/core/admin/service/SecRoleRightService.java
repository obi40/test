package com.optimiza.core.admin.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.optimiza.core.admin.model.SecRole;
import com.optimiza.core.admin.model.SecRoleRight;
import com.optimiza.core.admin.repo.SecRoleRightRepo;
import com.optimiza.core.base.helper.SearchCriterion;
import com.optimiza.core.base.service.GenericService;
import com.optimiza.core.common.annotation.InterceptorFree;

/**
 * SecRoleRightService.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Sep/27/2017
 **/

@Service("SecRoleRightService")
public class SecRoleRightService extends GenericService<SecRoleRight, SecRoleRightRepo> {

	@Autowired
	private SecRoleRightRepo roleRightRepo;

	public List<SecRoleRight> createRoleRight(List<SecRoleRight> secRoleRight) {
		return getRepository().save(secRoleRight);
	}

	public void deleteAllRoleRightByRole(SecRole secRole) {
		getRepository().deleteAllBySecRole(secRole);
	}

	@InterceptorFree
	public List<SecRoleRight> findRoleRightsExcluded(List<SearchCriterion> filters, String... joins) {
		return getRepository().find(filters, SecRoleRight.class, joins);
	}

	@Override
	protected SecRoleRightRepo getRepository() {
		return roleRightRepo;
	}

}
