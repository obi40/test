package com.optimiza.core.admin.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.optimiza.core.admin.model.SecRole;
import com.optimiza.core.admin.model.SecUser;
import com.optimiza.core.admin.model.SecUserRole;
import com.optimiza.core.admin.repo.SecUserRoleRepo;
import com.optimiza.core.base.helper.SearchCriterion;
import com.optimiza.core.base.helper.SearchCriterion.FilterOperator;
import com.optimiza.core.base.helper.SearchCriterion.JunctionOperator;
import com.optimiza.core.base.service.GenericService;
import com.optimiza.core.common.annotation.InterceptorFree;

/**
 * SecUserRoleService.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Sep/27/2017
 **/

@Service("SecUserRoleService")
public class SecUserRoleService extends GenericService<SecUserRole, SecUserRoleRepo> {

	@Autowired
	private SecUserRoleRepo secUserRoleRepo;

	public List<SecUserRole> createUserRole(List<SecUserRole> secUserRoleList) {
		return getRepository().save(secUserRoleList);
	}

	public void deleteUserRoleByRole(SecRole secRole) {
		getRepository().deleteAllBySecRole(secRole);
	}

	public void deleteUserRoleByUser(SecUser secUser) {
		getRepository().deleteAllBySecUser(secUser);
	}

	@InterceptorFree
	public List<SecUserRole> findUserRolesExcluded(List<SearchCriterion> filters, String... joins) {
		return getRepository().find(filters, SecUserRole.class, joins);
	}

	public List<SecUserRole> findUserRolesByRole(SecRole secRole) {

		return getRepository().find(
				Arrays.asList(new SearchCriterion("secRole", secRole.getRid(), FilterOperator.eq, JunctionOperator.And)),
				SecUserRole.class, "secRole", "secUser");
	}

	public List<SecUserRole> findUserRolesByUser(SecUser secUser) {
		return getRepository().find(
				Arrays.asList(new SearchCriterion("secUser", secUser.getRid(), FilterOperator.eq, JunctionOperator.And)),
				SecUserRole.class, "secRole", "secUser");
	}

	@Override
	protected SecUserRoleRepo getRepository() {
		return secUserRoleRepo;
	}

}
