package com.optimiza.core.admin.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.optimiza.core.admin.model.SecGroup;
import com.optimiza.core.admin.model.SecGroupRole;
import com.optimiza.core.admin.model.SecRole;
import com.optimiza.core.admin.repo.SecGroupRoleRepo;
import com.optimiza.core.base.helper.SearchCriterion;
import com.optimiza.core.base.service.GenericService;
import com.optimiza.core.common.annotation.InterceptorFree;

/**
 * SecGroupRoleService.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Sep/27/2017
 **/

@Service("SecGroupRoleService")
public class SecGroupRoleService extends GenericService<SecGroupRole, SecGroupRoleRepo> {

	@Autowired
	private SecGroupRoleRepo groupRoleRepo;

	public List<SecGroupRole> createGroupRole(List<SecGroupRole> secGroupRoleList) {
		return getRepository().save(secGroupRoleList);
	}

	public void deleteAllGroupRoleByRole(SecRole secRole) {
		getRepository().deleteAllBySecRole(secRole);
	}

	public void deleteAllGroupRoleByGroup(SecGroup secGroup) {
		getRepository().deleteAllBySecGroup(secGroup);
	}

	@InterceptorFree
	public List<SecGroupRole> findGroupRolesExcluded(List<SearchCriterion> filters, String... joins) {
		return getRepository().find(filters, SecGroupRole.class, joins);
	}

	@Override
	protected SecGroupRoleRepo getRepository() {
		return groupRoleRepo;
	}

}
