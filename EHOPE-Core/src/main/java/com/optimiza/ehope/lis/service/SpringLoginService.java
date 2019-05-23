package com.optimiza.ehope.lis.service;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.optimiza.core.admin.model.SecGroupRole;
import com.optimiza.core.admin.model.SecGroupUser;
import com.optimiza.core.admin.model.SecRight;
import com.optimiza.core.admin.model.SecRoleRight;
import com.optimiza.core.admin.model.SecTenant;
import com.optimiza.core.admin.model.SecUser;
import com.optimiza.core.admin.model.SecUserRole;
import com.optimiza.core.admin.model.UserData;
import com.optimiza.core.admin.repo.SecGroupUserRepo;
import com.optimiza.core.admin.repo.SecTenantRepo;
import com.optimiza.core.admin.repo.SecUserRepo;
import com.optimiza.core.admin.repo.SecUserRoleRepo;
import com.optimiza.core.base.helper.SearchCriterion;
import com.optimiza.core.base.helper.SearchCriterion.FilterOperator;
import com.optimiza.core.common.annotation.InterceptorFree;
import com.optimiza.core.common.business.exception.BusinessException;
import com.optimiza.core.common.business.exception.BusinessException.ErrorSeverity;
import com.optimiza.core.common.util.CollectionUtil;
import com.optimiza.core.lkp.repo.ComTenantLanguageRepo;
import com.optimiza.ehope.lis.model.LabBranch;
import com.optimiza.ehope.lis.onboarding.model.BrdTenantSubscription;
import com.optimiza.ehope.lis.onboarding.repo.BrdTenantSubscriptionRepo;
import com.optimiza.ehope.lis.repo.LabBranchRepo;

/**
 *
 * LoginService.java, Used to implement spring security loadUserByUsername
 * method, to check for user authentication
 *
 */
@Service("SpringLoginService")
@InterceptorFree
public class SpringLoginService implements UserDetailsService {

	@Autowired
	private SecUserRepo userRepo;
	@Autowired
	private SecUserRoleRepo userRoleRepo;
	@Autowired
	private SecGroupUserRepo groupUserRepo;
	@Autowired
	private ComTenantLanguageRepo tenantLanguageRepo;
	@Autowired
	private SecTenantRepo tenantRepo;
	@Autowired
	private BrdTenantSubscriptionRepo tenantSubscriptionRepo;
	@Autowired
	private LabBranchRepo branchRepo;

	/**
	 * get the User data from database by user name and throws an exception when
	 * user not found
	 *
	 * @param username
	 * @throws UsernameNotFoundException
	 */
	@Override
	@Transactional(readOnly = false)
	public UserData loadUserByUsername(String username) throws UsernameNotFoundException {
		SecUser user = userRepo.findByUsernameIgnoreCase(username);
		if (user == null) {
			throw new UsernameNotFoundException("User " + username + " was not found !!");
		}
		Set<SecUserRole> userRoles = userRoleRepo.getBySecUser(user.getRid());
		Set<SecGroupUser> userGroups = groupUserRepo.getBySecUser(user.getRid());
		if (CollectionUtil.isCollectionEmpty(userGroups) && CollectionUtil.isCollectionEmpty(userRoles)) {
			// The message of the error is the same as the code because spring wraps the exception in another way and in exception handler we read Exceptions
			// by getting the message if the cause is not null(BusinessException is not null here ) so we get the message which is the errorCode
			throw new BusinessException("userNoRights", "userNoRights", ErrorSeverity.ERROR);
		}
		SecTenant tenant = tenantRepo.fetchTenantDataById(user.getTenantId());
		LabBranch branch = null;
		if (user.getBranchId() != null) {
			branch = branchRepo.findOne(Arrays.asList(new SearchCriterion("rid", user.getBranchId(), FilterOperator.eq)), LabBranch.class,
					"country.currency");
		}
		BrdTenantSubscription subscription = tenantSubscriptionRepo.findTenantActiveSubscription(user.getTenantId(), new Date());
		boolean canLogin = tenant.getIsActive();
		if (branch != null) {// if user belongs to an in-active branch
			canLogin = canLogin && branch.getIsActive();
		}
		if (subscription == null) {// user belong to a tenant with no subscription or expired
			canLogin = false;
		}
		canLogin = canLogin && user.getIsActive();
		user.setTenant(tenant);
		user.setTenantLanguages(tenantLanguageRepo.fetchTenantLanguages(user.getTenantId()));
		if (branch != null) {
			user.setBranch(branch);
			user.setCountry(branch.getCountry());
		} else {
			user.setCountry(tenant.getCountry());
		}
		user.setUserGroups(new HashSet<>());
		for (SecGroupUser sgu : userGroups) {
			user.getUserGroups().add(sgu.getSecGroup());
		}
		userRepo.updateLastLoginTime(user.getRid(), new Date());
		UserData userDetails = new UserData(Optional.of(user), getRights(userRoles, userGroups), canLogin);
		return userDetails;
	}

	private Set<SecRight> getRights(Set<SecUserRole> userRoles, Set<SecGroupUser> userGroups) {
		Set<SecRight> rights = new HashSet<>();
		for (SecUserRole sur : userRoles) {
			for (SecRoleRight srr : sur.getSecRole().getSecRoleRights()) {
				rights.add(srr.getSecRight());
			}
		}
		for (SecGroupUser sgu : userGroups) {
			for (SecGroupRole sgr : sgu.getSecGroup().getSecGroupRoles()) {
				for (SecRoleRight srr : sgr.getSecRole().getSecRoleRights()) {
					rights.add(srr.getSecRight());
				}
			}
		}
		return rights;
	}

}
