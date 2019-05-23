package com.optimiza.core.admin.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.mail.MailException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import com.optimiza.core.admin.helper.AdminRights;
import com.optimiza.core.admin.lkp.model.LkpUserStatus;
import com.optimiza.core.admin.model.SecGroupRole;
import com.optimiza.core.admin.model.SecGroupUser;
import com.optimiza.core.admin.model.SecTenant;
import com.optimiza.core.admin.model.SecUser;
import com.optimiza.core.admin.model.SecUserRole;
import com.optimiza.core.admin.repo.SecUserRepo;
import com.optimiza.core.base.helper.SearchCriterion;
import com.optimiza.core.base.helper.SearchCriterion.FilterOperator;
import com.optimiza.core.base.service.GenericService;
import com.optimiza.core.common.annotation.InterceptorFree;
import com.optimiza.core.common.business.exception.BusinessException;
import com.optimiza.core.common.business.exception.BusinessException.ErrorSeverity;
import com.optimiza.core.common.data.model.TransField;
import com.optimiza.core.common.helper.Email;
import com.optimiza.core.common.helper.FilterablePageRequest;
import com.optimiza.core.common.util.CollectionUtil;
import com.optimiza.core.common.util.EmailUtil;
import com.optimiza.core.common.util.ReflectionUtil;
import com.optimiza.core.common.util.SecurityUtil;
import com.optimiza.core.common.util.StringUtil;
import com.optimiza.core.common.util.TokenUtil;
import com.optimiza.core.lkp.model.ComLanguage;
import com.optimiza.core.lkp.model.ComTenantLanguage;
import com.optimiza.core.lkp.model.LkpGender;
import com.optimiza.core.lkp.service.ComTenantLanguageService;

import freemarker.template.TemplateException;
import io.jsonwebtoken.Claims;

/**
 * SecUserService.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Sep/27/2017
 **/
@Service("SecUserService")
public class SecUserService extends GenericService<SecUser, SecUserRepo> {

	@Autowired
	private TokenUtil tokenUtils;
	@Autowired
	private SecUserRepo secUserRepo;
	@Autowired
	private SecGroupUserService secGroupUserService;
	@Autowired
	private SecUserRoleService secUserRoleService;
	@Autowired
	private SecTenantService tenantService;
	@Autowired
	private ComTenantLanguageService tenantLanguageService;
	@Autowired
	private EmailUtil emailUtil;
	@Value("${system.website.url}")
	private String serverUrl;

	/**
	 * For Admin when creating a user.
	 * 
	 * @param secUser
	 * @param secGroupUserList
	 * @param secUserRoleList
	 * @return
	 * @throws MailException
	 * @throws InterruptedException
	 * @throws MessagingException
	 * @throws TemplateException
	 * @throws IOException
	 */
	@PreAuthorize("hasAuthority('" + AdminRights.ADD_USER + "')")
	public SecUser createUser(SecUser secUser, List<SecGroupUser> secGroupUserList, List<SecUserRole> secUserRoleList) {
		//we are checking if username exists from outside the function because we need the intercepter work inside this function
		if (findUserByEmail(secUser.getEmail()) != null) {
			throw new BusinessException("Email is used", "emailExist", ErrorSeverity.ERROR);
		}

		String generatedPassword = SecurityUtil.generatePassword();
		String encryptedPassword = SecurityUtil.encode(generatedPassword);
		secUser.setPassword(encryptedPassword);
		secUser.setEmail(secUser.getEmail().toLowerCase());
		SecUser newSecUser = getRepository().save(secUser);

		if (!CollectionUtil.isCollectionEmpty(secGroupUserList)) {
			secGroupUserList.stream().forEach(sgu -> sgu.setSecUser(newSecUser));
			secGroupUserService.createGroupUser(secGroupUserList);
		}
		if (!CollectionUtil.isCollectionEmpty(secUserRoleList)) {
			secUserRoleList.stream().forEach(sur -> sur.setSecUser(newSecUser));
			secUserRoleService.createUserRole(secUserRoleList);
		}

		Map<String, String> templateValues = new HashMap<>();
		templateValues.put("username", secUser.getUsername());
		templateValues.put("password", generatedPassword);
		templateValues.put("loginUrl", serverUrl + "/login");
		templateValues.put("userProfileUrl", serverUrl + "/user-profile");
		Email email = new Email("email-new-user", secUser.getFirstName().entrySet().iterator().next().getValue(), secUser.getEmail(),
				templateValues);
		emailUtil.sendMailTemplate(email);
		return newSecUser;

	}

	@PreAuthorize("hasAuthority('" + AdminRights.DEACTIVATE_USER + "')")
	public void deactivateUser(SecUser secUser) {
		secUser.setIsActive(Boolean.FALSE);
		secUser.setPassword(getPasswordById(secUser.getRid()));
		getRepository().save(secUser);
	}

	@PreAuthorize("hasAuthority('" + AdminRights.ACTIVATE_USER + "')")
	public void activateUser(SecUser secUser) {
		secUser.setIsActive(Boolean.TRUE);
		secUser.setPassword(getPasswordById(secUser.getRid()));
		getRepository().save(secUser);
	}

	/**
	 * For Admin when editing the user.
	 * 
	 * @param secUser
	 * @param secGroupUserList
	 * @param secUserRoleList
	 * @return
	 */
	@PreAuthorize("hasAuthority('" + AdminRights.UPD_USER + "')")
	public SecUser updateUser(SecUser secUser, List<SecGroupUser> secGroupUserList,
			List<SecUserRole> secUserRoleList) {
		SecUser user = findUserByEmail(secUser.getEmail());
		if (user != null && !user.equals(secUser)) {
			throw new BusinessException("Email is used", "emailExist", ErrorSeverity.ERROR);
		}
		secUser.setPassword(getPasswordById(secUser.getRid()));
		SecUser updatedSecUser = getRepository().save(secUser);

		secGroupUserService.deleteGroupUserByUser(updatedSecUser);
		if (!CollectionUtil.isCollectionEmpty(secGroupUserList)) {
			secGroupUserList.stream().forEach(sgu -> sgu.setSecUser(updatedSecUser));
			secGroupUserService.createGroupUser(secGroupUserList);
		}

		secUserRoleService.deleteUserRoleByUser(updatedSecUser);
		if (!CollectionUtil.isCollectionEmpty(secUserRoleList)) {
			secUserRoleList.stream().forEach(sur -> sur.setSecUser(updatedSecUser));
			secUserRoleService.createUserRole(secUserRoleList);
		}

		return updatedSecUser;
	}

	/**
	 * For users when updating their profiles.
	 * 
	 * @param secUser
	 * @param token : getting the authorities from the token
	 * @return
	 */
	public Map<String, Object> updateUserProfile(SecUser secUser, String token) {
		secUser.setPassword(getPasswordById(secUser.getRid()));
		SecUser updatedUser = getRepository().save(secUser);
		updatedUser = refillUserData(updatedUser);
		Collection<GrantedAuthority> authorities = this.tokenUtils.getAuthToken(token).getAuthorities();//get authorities from token since it didn't change
		return tokenUtils.loginData(updatedUser, authorities);//generate new token
	}

	/**
	 * For Admin , to reset the secUser password
	 * 
	 * @param secUser
	 * @return
	 * @throws MailException
	 * @throws InterruptedException
	 * @throws MessagingException
	 * @throws TemplateException
	 * @throws IOException
	 */
	@PreAuthorize("hasAuthority('" + AdminRights.RESET_PASS_USER + "')")
	public String resetUserPassword(SecUser secUser) {

		String generatedPassword = SecurityUtil.generatePassword();
		String encryptedPassword = SecurityUtil.encode(generatedPassword);
		secUser.setPassword(encryptedPassword);
		getRepository().save(secUser);

		Map<String, String> templateValues = new HashMap<>();
		templateValues.put("password", generatedPassword);
		Email email = new Email("email-password-new", secUser.getFirstName().entrySet().iterator().next().getValue(), secUser.getEmail(),
				templateValues);
		emailUtil.sendMailTemplate(email);

		return generatedPassword;

	}

	/**
	 * Send an email to the user containing a token to change the password.
	 * 
	 * @param username
	 * 
	 * 
	 * @throws MailException
	 * @throws InterruptedException
	 * @throws MessagingException
	 * @throws TemplateException
	 * @throws IOException
	 */
	@InterceptorFree
	public void forgotPassword(String username) {
		SecUser lostUser = findUserByUsername(username);
		// we don't notify the user that the user name exists or not
		if (lostUser == null) {
			return;
		}
		Map<String, Object> data = new HashMap<>();
		Map<String, Object> tokenData = new HashMap<>();
		tokenData.put("userRid", lostUser.getRid());
		tokenData.put("username", username);
		data.put("data", tokenData);
		String token = this.tokenUtils.generateToken(data, this.tokenUtils.FORGOT_TOKEN_EXPIRATION);
		String url = serverUrl + "/password-reset?t=" + token;
		Map<String, String> templateValues = new HashMap<>();
		templateValues.put("resetPasswordLink", url);
		Email springEmail = new Email("email-password-reset", lostUser.getFirstName().entrySet().iterator().next().getValue(),
				lostUser.getEmail(),
				templateValues);
		emailUtil.sendMailTemplate(springEmail);
	}

	/**
	 * After verification of the token this method is called to apply the new password
	 * 
	 * @param token
	 * @param newPassword
	 */
	@InterceptorFree
	public void changeForgottenPassword(String token, String newPassword) {
		try {
			Claims claims = this.tokenUtils.getClaimsFromToken(token);
			Map<String, Object> data = (Map<String, Object>) claims.get("data");
			Long userRid = Long.parseLong(data.get("userRid").toString());
			String encryptedPassword = SecurityUtil.encode(newPassword);
			SecUser user = getRepository().findOne(Arrays.asList(new SearchCriterion("rid", userRid, FilterOperator.eq)),
					SecUser.class,
					"lkpGender", "lkpUserStatus", "comLanguage");
			user.setPassword(encryptedPassword);
			getRepository().save(user);
		} catch (Exception e) {
			throw new BusinessException(e.getMessage(), "invalidToken", ErrorSeverity.ERROR);
		}
	}

	/**
	 * Change user's password by the user.
	 * 
	 * @param secUser
	 * @param token : getting the authorities from the token
	 * @param originalPassword
	 * @param newPassword
	 * @param newEmail
	 * @return
	 * @throws MailException
	 * @throws InterruptedException
	 * @throws MessagingException
	 * @throws TemplateException
	 * @throws IOException
	 */
	@PreAuthorize("hasAuthority('" + AdminRights.CHANGE_PASS_EMAIL_USER + "')")
	public Map<String, Object> updateUserEmailAndPass(SecUser user, String token, String originalPassword, String newPassword,
			String newEmail) {
		SecUser duplicatedUser = findUserByEmail(newEmail);
		if (duplicatedUser != null && !duplicatedUser.equals(user)) {
			throw new BusinessException("Email is used", "emailExist", ErrorSeverity.ERROR);
		}

		if (SecurityUtil.isPasswordsMatch(originalPassword, getPasswordById(user.getRid()))) {
			user = getRepository().findOne(Arrays.asList(new SearchCriterion("rid", user.getRid(), FilterOperator.eq)), SecUser.class,
					"lkpGender", "lkpUserStatus", "comLanguage");
			user.setEmail(newEmail);
			String password = !StringUtil.isEmpty(newPassword) ? newPassword : originalPassword;
			String encryptedPassword = SecurityUtil.encode(password);
			user.setPassword(encryptedPassword);
			user = getRepository().save(user);
			user = refillUserData(user);
			Collection<GrantedAuthority> authorities = this.tokenUtils.getAuthToken(token).getAuthorities();//get authorities from token since it didn't change

			Map<String, String> templateValues = new HashMap<>();
			templateValues.put("password", password);
			Email email = new Email("email-password-new", user.getFirstName().entrySet().iterator().next().getValue(), newEmail,
					templateValues);
			emailUtil.sendMailTemplate(email);

			return tokenUtils.loginData(user, authorities);//generate new token
		} else {
			throw new BusinessException("Password Does Not Match", "passwordInvalid", ErrorSeverity.ERROR);
		}

	}

	/**
	 * Get a page of users without joining roles or groups
	 * 
	 * @param filterablePageRequest
	 * @return page of users
	 */
	@PreAuthorize("hasAuthority('" + AdminRights.VIEW_USERS_MANAGEMENT + "')")
	public Page<SecUser> findUserPage(FilterablePageRequest filterablePageRequest) {

		String[] joins = new String[] { "comLanguage", "lkpGender", "lkpUserStatus" };

		Page<SecUser> page = getRepository().find(filterablePageRequest.getFilters(), filterablePageRequest.getPageRequest(),
				SecUser.class, joins);

		return page;
	}

	/**
	 * Get a page of users without joining roles, used in Groups Management
	 * 
	 * @param filterablePageRequest
	 * @return page of users
	 */

	public Page<SecUser> findUserPageJoinGroups(FilterablePageRequest filterablePageRequest) {

		Page<SecUser> page = getRepository().find(filterablePageRequest.getFilters(), filterablePageRequest.getPageRequest(),
				SecUser.class);
		if (page.getNumberOfElements() == 0) {
			return page;
		}
		List<SecUser> users = getRepository()	.find(
				Arrays.asList(new SearchCriterion("rid", page.getContent().stream().map(SecUser::getRid).collect(Collectors.toList()),
						FilterOperator.in)),
				SecUser.class, filterablePageRequest.getSortObject(), "secGroupUsers.secGroup")
												.stream().distinct().collect(Collectors.toList());
		for (SecUser su : users) {
			for (SecGroupUser sgu : su.getSecGroupUsers()) {
				su.getUserGroups().add(sgu.getSecGroup());
			}
		}
		Page<SecUser> usersPage = new PageImpl<>(users, filterablePageRequest.getPageRequest(), page.getTotalElements());

		return usersPage;
	}

	/**
	 * Get a page of users without joining groups, used in Roles Management
	 * 
	 * @param filterablePageRequest
	 * @return page of users
	 */

	public Page<SecUser> findUserPageJoinRolesGroups(FilterablePageRequest filterablePageRequest) {

		Page<SecUser> page = getRepository().find(filterablePageRequest.getFilters(), filterablePageRequest.getPageRequest(),
				SecUser.class);
		if (page.getNumberOfElements() == 0) {
			return page;
		}
		List<SecUser> users = getRepository()	.find(
				Arrays.asList(new SearchCriterion("rid", page.getContent().stream().map(SecUser::getRid).collect(Collectors.toList()),
						FilterOperator.in)),
				SecUser.class, filterablePageRequest.getSortObject(), "secUserRoles.secRole",
				"secGroupUsers.secGroup.secGroupRoles.secRole")
												.stream().distinct().collect(Collectors.toList());
		for (SecUser su : users) {
			for (SecUserRole sur : su.getSecUserRoles()) {
				su.getUserRoles().add(sur.getSecRole());
			}
			for (SecGroupUser sgu : su.getSecGroupUsers()) {
				for (SecGroupRole sgr : sgu.getSecGroup().getSecGroupRoles()) {
					su.getUserRoles().add(sgr.getSecRole());
				}
			}
		}
		Page<SecUser> usersPage = new PageImpl<>(users, filterablePageRequest.getPageRequest(), page.getTotalElements());
		return usersPage;
	}

	public SecUser findUserByEmail(String email) {
		return getRepository().findByEmailIgnoreCase(email);
	}

	@InterceptorFree
	public SecUser findUserByUsername(String username) {
		return getRepository().findByUsernameIgnoreCase(username);
	}

	/*
	 * To set the password for the SecUser from the Database, because of the JSON ignore
	 */
	public String getPasswordById(Long Id) {
		return getRepository().fetchPasswordById(Id);
	}

	public void updateLastLoginTime(Long userRid) {
		getRepository().updateLastLoginTime(userRid, new Date());
	}

	/**
	 * In some cases we need to get the current logged in user data after we update the secUser.
	 * Same as login data.
	 * 
	 * @param toRefillSecUser
	 * @return
	 */
	//TODO : SHOULD USE THE SAME LOGIN IN SpringLoginService
	public SecUser refillUserData(SecUser toRefillSecUser) {
		SecTenant tenant = tenantService.findOne(
				Arrays.asList(new SearchCriterion("rid", toRefillSecUser.getTenantId(), FilterOperator.eq)), SecTenant.class,
				"country.currency");

		Object branch = toRefillSecUser.getBranchId() != null
				? ReflectionUtil.getRepository("LabBranch").findOne(toRefillSecUser.getBranchId())
				: null;
		List<ComTenantLanguage> tenantLanguages = tenantLanguageService.findTenantLanguages(
				Arrays.asList(new SearchCriterion("tenantId", tenant.getRid(), FilterOperator.eq)), null, "comLanguage");
		toRefillSecUser.setTenantLanguages(tenantLanguages);
		toRefillSecUser.setCountry(tenant.getCountry());
		toRefillSecUser.setTenant(tenant);
		toRefillSecUser.setBranch(branch);
		return toRefillSecUser;
	}

	/**
	 * Generate Hareq & Mareq user(full privileges) for tenant then sending an email.
	 * 
	 * @param tenant
	 * @param username : we are sending the username from outside because we are checking if username exists or not in that service
	 * 
	 * @return SecUser
	 */
	@InterceptorFree
	public SecUser createTenantAdminUser(SecTenant tenant, String username, LkpGender gender, LkpUserStatus status,
			List<ComTenantLanguage> tenantLanguages) {

		TransField name = new TransField();
		TransField address = new TransField();
		ComLanguage primaryLanguage = null;
		for (ComTenantLanguage lang : tenantLanguages) {
			name.put(lang.getComLanguage().getLocale(), tenant.getCode() + "-admin");//not null
			if (tenant.getAddress() != null) {
				address.put(lang.getComLanguage().getLocale(), tenant.getAddress());
			}
			if (lang.getIsPrimary()) {
				primaryLanguage = lang.getComLanguage();
			}
		}

		String generatedPassword = SecurityUtil.generatePassword();
		String encryptedPassword = SecurityUtil.encode(generatedPassword);

		SecUser admin = new SecUser();
		admin.setUsername(username);
		admin.setEmail(tenant.getEmail());
		admin.setFirstName(name);
		admin.setSecondName(name);
		admin.setThirdName(name);
		admin.setLastName(name);
		admin.setAddress(address);
		admin.setMobileNo(tenant.getPhoneNo() != null ? tenant.getPhoneNo() : "1");
		admin.setNationalId(1L);
		admin.setPassword(encryptedPassword);
		admin.setIsActive(Boolean.TRUE);
		admin.setLkpUserStatus(status);
		admin.setLkpGender(gender);
		admin.setComLanguage(primaryLanguage);
		admin.setTenantId(tenant.getRid());
		admin.setCreatedBy(SecurityUtil.getSystemUser().getRid());
		admin = getRepository().save(admin);

		Map<String, String> templateValues = new HashMap<>();
		templateValues.put("username", admin.getUsername());
		templateValues.put("password", generatedPassword);
		templateValues.put("loginUrl", serverUrl + "/login");
		templateValues.put("userProfileUrl", serverUrl + "/user-profile");
		Email email = new Email("email-new-user", admin.getFirstName().entrySet().iterator().next().getValue(), admin.getEmail(),
				templateValues);
		emailUtil.sendMailTemplate(email);
		return admin;
	}

	public void deleteUser(Long rid) {
		getRepository().delete(rid);
	}

	public String getUserLocale(Long userRid) {
		return getRepository().findOne(Arrays.asList(new SearchCriterion("rid", userRid, FilterOperator.eq)),
				SecUser.class, "comLanguage").getComLanguage().getLocale();
	}

	@Override
	protected SecUserRepo getRepository() {
		return secUserRepo;
	}

}
