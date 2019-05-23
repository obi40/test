package com.optimiza.core.admin.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.optimiza.core.admin.helper.AdminRights;
import com.optimiza.core.admin.model.SecGroup;
import com.optimiza.core.admin.model.SecGroupRole;
import com.optimiza.core.admin.model.SecGroupUser;
import com.optimiza.core.admin.repo.SecGroupRepo;
import com.optimiza.core.base.helper.SearchCriterion;
import com.optimiza.core.base.service.GenericService;
import com.optimiza.core.common.annotation.InterceptorFree;
import com.optimiza.core.common.util.CollectionUtil;

/**
 * SecGroupService.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Sep/27/2017
 **/

@Service("SecGroupService")
public class SecGroupService extends GenericService<SecGroup, SecGroupRepo> {

	@Autowired
	private SecGroupRepo secGroupRepo;

	@Autowired
	private SecGroupRoleService secGroupRoleService;

	@Autowired
	private SecGroupUserService secGroupUserService;

	@PreAuthorize("hasAuthority('" + AdminRights.ADD_GROUP + "')")
	public SecGroup createGroup(SecGroup secGroup, List<SecGroupRole> secGroupRoleList, List<SecGroupUser> secGroupUserList) {

		SecGroup newSecGroup = getRepository().save(secGroup);

		if (!CollectionUtil.isCollectionEmpty(secGroupRoleList)) {
			secGroupRoleList.stream().forEach(sgu -> sgu.setSecGroup(newSecGroup));
			secGroupRoleService.createGroupRole(secGroupRoleList);
		}

		if (!CollectionUtil.isCollectionEmpty(secGroupUserList)) {
			secGroupUserList.stream().forEach(sur -> sur.setSecGroup(newSecGroup));
			secGroupUserService.createGroupUser(secGroupUserList);
		}

		return newSecGroup;
	}

	@PreAuthorize("hasAuthority('" + AdminRights.DEL_GROUP + "')")
	public void deleteGroup(SecGroup secGroup) {

		secGroupRoleService.deleteAllGroupRoleByGroup(secGroup);
		secGroupUserService.deleteGroupUserByGroup(secGroup);
		getRepository().delete(secGroup);
	}

	public List<SecGroup> findGroups() {
		return getRepository().find(new ArrayList<>(), SecGroup.class);
	}

	@PreAuthorize("hasAuthority('" + AdminRights.VIEW_GROUPS_MANAGEMENT + "')")
	public Set<SecGroup> findGroupJoinRoles() {

		Set<SecGroup> groupWithRoles = new HashSet<>(getRepository().find(new ArrayList<>(), SecGroup.class, "secGroupRoles.secRole"));
		for (SecGroup sg : groupWithRoles) {
			for (SecGroupRole sgr : sg.getSecGroupRoles()) {
				sg.getGroupRoles().add(sgr.getSecRole());
			}
		}

		return groupWithRoles;
	}

	@PreAuthorize("hasAuthority('" + AdminRights.UPD_GROUP + "')")
	public SecGroup updateGroup(SecGroup secGroup, List<SecGroupRole> secGroupRoleList, List<SecGroupUser> secGroupUserList) {
		SecGroup newSecGroup = getRepository().save(secGroup);

		secGroupRoleService.deleteAllGroupRoleByGroup(secGroup);
		if (!CollectionUtil.isCollectionEmpty(secGroupRoleList)) {
			secGroupRoleList.stream().forEach(sgu -> sgu.setSecGroup(newSecGroup));
			secGroupRoleService.createGroupRole(secGroupRoleList);
		}

		secGroupUserService.deleteGroupUserByGroup(secGroup);
		if (!CollectionUtil.isCollectionEmpty(secGroupUserList)) {
			secGroupUserList.stream().forEach(sur -> sur.setSecGroup(newSecGroup));
			secGroupUserService.createGroupUser(secGroupUserList);
		}

		return newSecGroup;
	}

	@InterceptorFree
	public List<SecGroup> findGroupsExcluded(List<SearchCriterion> filters, String... joins) {
		return getRepository().find(filters, SecGroup.class, joins);
	}

	@Override
	protected SecGroupRepo getRepository() {
		return secGroupRepo;
	}

}
