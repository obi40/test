package com.optimiza.core.common.util;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import com.optimiza.core.admin.model.SecUser;
import com.optimiza.core.common.business.exception.BusinessException;
import com.optimiza.core.common.business.exception.BusinessException.ErrorSeverity;

/**
 * SecurityUtil.java, Used to access spring security
 * 
 * @author Wa'el Abu Rahmeh <waburahemh@optimizasolutions.com>
 * @since 21/05/2017
 **/
@Component
public class SecurityUtil {

	private static SecureRandom random = new SecureRandom();

	public static Long DEFAULT_TENANT;
	public static Long DEFAULT_BRANCH;
	public static Long APPLICATION_ADMIN;

	@Value("${system.defaultTenant}")
	public void setDefaultTenant(String value) {
		DEFAULT_TENANT = new Long(value);
	}

	@Value("${system.defaultBranch}")
	public void setDefaultBranch(String value) {
		DEFAULT_BRANCH = new Long(value);
	}

	@Value("${system.admin}")
	public void setApplicationAdmin(String value) {
		APPLICATION_ADMIN = new Long(value);
	}

	public static String encode(String str) {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String hashedPassword = passwordEncoder.encode(str);
		return hashedPassword;
	}

	public static String generatePassword() {
		return new BigInteger(64, random).toString(32);
	}

	public static boolean isPasswordsMatch(String password, String encodedPassword) {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		return passwordEncoder.matches(password, encodedPassword);

	}

	public static String MD5(String text) {
		return DigestUtils.md5DigestAsHex(text.getBytes());
	}

	/**
	 * Get current authenticated user
	 *
	 * @return SecUser
	 */
	public static SecUser getCurrentUser() {
		SecUser user = null;

		try {
			user = (SecUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		} catch (Exception e) {
			throw new BusinessException("USER_NOT_FOUND");
		}

		return user;
	}

	/**
	 * Checks whether the argument branchId is allowed for the current user.
	 * A branchId is allowed if the user is tenanted, or it matches a branched user's branchId.
	 *
	 * @param branchId the branchId to check
	 * @return {@code true} if the branchId is allowed;
	 *         {@code false} otherwise.
	 */
	public static Boolean isBranchIdAllowed(Long branchId) {
		Long userBranchId = getCurrentUser().getBranchId();
		if (userBranchId == null || branchId.equals(userBranchId)) {
			return true;
		}
		return false;
	}

	/**
	 * Get current authenticated user If not null else get System internal user
	 *
	 * @return SecUser
	 */
	public static SecUser getCurrentUserElseInternal() {
		SecUser user = null;

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication != null) {
			user = (SecUser) authentication.getPrincipal();

		} else {
			return getSystemUser();
		}

		return user;

	}

	public static SecUser getSystemUser() {
		SecUser secUser = new SecUser();
		secUser.setRid(0L);
		secUser.setTenantId(0L);
		secUser.setBranchId(0L);
		return secUser;
	}

	public static boolean isApplicationAdmin() {
		SecUser user = getCurrentUser();
		return user.getRid().equals(APPLICATION_ADMIN) && user.getTenantId().equals(DEFAULT_TENANT);
	}

	public static void authorizeApplicationAdmin() {
		if (!isApplicationAdmin()) {
			throw new BusinessException("Requires Application Admin", "requiresApplicationAdmin", ErrorSeverity.ERROR);
		}
	}

	public static boolean isUserLoggedIn() {
		try {
			SecUser user = (SecUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean isUserAuthorized(String right) {
		Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
		for (GrantedAuthority grantedAuth : authorities) {
			if (grantedAuth.getAuthority().equals(right)) {
				return true;
			}
		}
		return false;
	}
}
