package com.optimiza.ehope.web.config;

import java.io.Serializable;

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.optimiza.core.common.util.SecurityUtil;

@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {

	@Override
	public boolean hasPermission(
			Authentication auth, Object targetDomainObject, Object permission) {
		if ((auth == null) || !(permission instanceof String)) {
			return false;
		}
		return SecurityUtil.isApplicationAdmin() ? true : hasAuthority(auth, (String) permission);
	}

	@Override
	public boolean hasPermission(
			Authentication auth, Serializable targetId, String targetType, Object permission) {
		if ((auth == null) || !(permission instanceof String)) {
			return false;
		}
		return SecurityUtil.isApplicationAdmin() ? true : hasAuthority(auth, (String) permission);
	}

	private boolean hasAuthority(Authentication auth, String permission) {
		for (GrantedAuthority grantedAuth : auth.getAuthorities()) {
			if (grantedAuth.getAuthority().equals(permission)) {
				return true;
			}
		}
		return false;
	}

}
