package com.optimiza.ehope.web.security.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.optimiza.core.admin.model.SecUser;
import com.optimiza.core.admin.model.UserData;
import com.optimiza.core.admin.service.SecUserService;
import com.optimiza.core.common.business.exception.BusinessException;
import com.optimiza.core.common.business.exception.BusinessException.ErrorSeverity;
import com.optimiza.core.common.util.SecurityUtil;
import com.optimiza.core.common.util.TokenUtil;
import com.optimiza.ehope.lis.helper.EhopeRights;
import com.optimiza.ehope.web.security.common.AuthorizationRequest;

/**
 * AuthorizationController.java
 * 
 * @author Murad Poladian <mpoladian@optimizasolutions.com>
 * @since Nov/8/2017
 **/
@RestController
@RequestMapping("/services")
public class AuthorizationController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private TokenUtil tokenUtils;

	@Autowired
	private SecUserService userService;

	@RequestMapping(value = "/login.pub.srvc", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> login(@RequestBody AuthorizationRequest authorizationRequest) {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						authorizationRequest.getUsername(),
						authorizationRequest.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		UserData userData = (UserData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return new ResponseEntity<Map<String, Object>>(tokenUtils.loginData(userData.getSecUser().get(), userData.getAuthorities()),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/generateDummyToken.pub.srvc", method = RequestMethod.POST)
	public ResponseEntity<String> generateDummyToken() {
		return new ResponseEntity<String>(tokenUtils.generateDummyToken(), HttpStatus.OK);
	}

	@RequestMapping(value = "/generateTenantOnboardingToken.pub.srvc", method = RequestMethod.POST)
	public ResponseEntity<String> generateTenantOnboardingToken(@RequestBody Long tenantRid) {
		List<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority(EhopeRights.VIEW_BRANCH));
		authorities.add(new SimpleGrantedAuthority(EhopeRights.ADD_BRANCH));
		authorities.add(new SimpleGrantedAuthority(EhopeRights.UPD_BRANCH));
		authorities.add(new SimpleGrantedAuthority(EhopeRights.DEL_BRANCH));
		authorities.add(new SimpleGrantedAuthority(EhopeRights.VIEW_SERIAL));
		authorities.add(new SimpleGrantedAuthority(EhopeRights.ADD_SERIAL));
		authorities.add(new SimpleGrantedAuthority(EhopeRights.UPD_SERIAL));
		return new ResponseEntity<String>(tokenUtils.generateTenantOnboardingToken(tenantRid, authorities), HttpStatus.OK);
	}

	@RequestMapping(value = "/generateBranchedToken.srvc", method = RequestMethod.POST)
	public ResponseEntity<String> generateBranchedToken(@RequestBody Long branchRid, HttpServletRequest request) {
		SecUser user = userService.findById(SecurityUtil.getCurrentUser().getRid());
		if (user.getBranchId() != null) {
			throw new BusinessException("Can't set branch rid in a user belongs to a branch", "userHasBranch", ErrorSeverity.ERROR);
		}
		return new ResponseEntity<String>(tokenUtils.getBranchedToken(branchRid, request), HttpStatus.OK);
	}

	@RequestMapping(value = "/getCustomTokenData.pub.srvc", method = RequestMethod.POST)
	public ResponseEntity<Object> getCustomTokenData(@RequestBody String token) {
		//custom tokens such as forget password, these tokens should save their whole data in a "data" key inside the token
		return new ResponseEntity<Object>(this.tokenUtils.getClaimsFromToken(token).get("data"), HttpStatus.OK);
	}

}
