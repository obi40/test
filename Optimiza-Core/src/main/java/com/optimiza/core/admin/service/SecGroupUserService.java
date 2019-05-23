package com.optimiza.core.admin.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.optimiza.core.admin.model.SecGroup;
import com.optimiza.core.admin.model.SecGroupUser;
import com.optimiza.core.admin.model.SecUser;
import com.optimiza.core.admin.repo.SecGroupUserRepo;
import com.optimiza.core.base.helper.SearchCriterion;
import com.optimiza.core.base.helper.SearchCriterion.FilterOperator;
import com.optimiza.core.base.helper.SearchCriterion.JunctionOperator;
import com.optimiza.core.base.service.GenericService;

/**
 * SecGroupUserService.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Sep/27/2017
 **/

@Service("SecGroupUserService")
public class SecGroupUserService extends GenericService<SecGroupUser, SecGroupUserRepo> {

	@Autowired
	private SecGroupUserRepo secGroupUserRepo;

	public List<SecGroupUser> createGroupUser(List<SecGroupUser> secGroupUserList) {
		return getRepository().save(secGroupUserList);
	}

	public void deleteGroupUserByGroup(SecGroup secGroup) {
		getRepository().deleteAllBySecGroup(secGroup);
	}

	public void deleteGroupUserByUser(SecUser secUser) {
		getRepository().deleteAllBySecUser(secUser);
	}

	public List<SecGroupUser> findGroupUserByGroup(SecGroup secGroup) {

		return getRepository().find(
				Arrays.asList(new SearchCriterion("secGroup", secGroup.getRid(), FilterOperator.eq, JunctionOperator.And)),
				SecGroupUser.class, "secGroup", "secUser");
	}

	public List<SecGroupUser> findGroupUserByUser(SecUser secUser) {
		return getRepository().find(
				Arrays.asList(new SearchCriterion("secUser", secUser.getRid(), FilterOperator.eq, JunctionOperator.And)),
				SecGroupUser.class, "secGroup", "secUser");
	}

	@Override
	protected SecGroupUserRepo getRepository() {
		return secGroupUserRepo;
	}

}
