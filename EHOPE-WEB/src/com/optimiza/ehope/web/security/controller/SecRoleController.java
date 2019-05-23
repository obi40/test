package com.optimiza.ehope.web.security.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.optimiza.core.admin.model.SecGroupRole;
import com.optimiza.core.admin.model.SecRole;
import com.optimiza.core.admin.model.SecRoleRight;
import com.optimiza.core.admin.model.SecUserRole;
import com.optimiza.core.admin.service.SecRoleService;
import com.optimiza.ehope.web.security.wrapper.SecRelationWrapper;

/**
 * SecRoleService.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Oct/17/2017
 **/
@RestController
@RequestMapping("/services")
public class SecRoleController {

	@Autowired
	private SecRoleService secRoleService;

	@RequestMapping(value = "/createSecRole.srvc", method = RequestMethod.POST)
	public ResponseEntity<SecRole> createSecRole(
			@RequestBody SecRelationWrapper<SecRole, SecUserRole, SecGroupRole, SecRoleRight> secRelationWrapper) {

		SecRole secRole = secRelationWrapper.getMaster();
		List<SecUserRole> secUserRoleList = secRelationWrapper.getRelationTableOne();
		List<SecGroupRole> secGroupRoleList = secRelationWrapper.getRelationTableTwo();
		List<SecRoleRight> secRoleRightList = secRelationWrapper.getRelationTableThree();
		return new ResponseEntity<SecRole>(secRoleService.createRole(secRole, secGroupRoleList, secRoleRightList, secUserRoleList),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/deleteSecRole.srvc", method = RequestMethod.POST)
	public ResponseEntity<Object> deleteSecRole(@RequestBody SecRole secRole) {
		this.secRoleService.deleteRole(secRole);
		return new ResponseEntity<Object>(HttpStatus.OK);
	}

	@RequestMapping(value = "/updateSecRole.srvc", method = RequestMethod.POST)
	public ResponseEntity<SecRole> updateSecRole(
			@RequestBody SecRelationWrapper<SecRole, SecUserRole, SecGroupRole, SecRoleRight> secRelationWrapper) {

		SecRole secRole = secRelationWrapper.getMaster();
		List<SecUserRole> secUserRoleList = secRelationWrapper.getRelationTableOne();
		List<SecGroupRole> secGroupRoleList = secRelationWrapper.getRelationTableTwo();
		List<SecRoleRight> secRoleRightList = secRelationWrapper.getRelationTableThree();

		return new ResponseEntity<SecRole>(secRoleService.updateRole(secRole, secGroupRoleList, secRoleRightList, secUserRoleList),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/getSecRoleList.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<SecRole>> getSecRoleList() {
		return new ResponseEntity<List<SecRole>>(secRoleService.findRoles(), HttpStatus.OK);
	}

	@RequestMapping(value = "/getSecRoleWithRightsGroupsList.srvc", method = RequestMethod.POST)
	public ResponseEntity<List<SecRole>> getSecRoleWithRightsGroupsList() {
		return new ResponseEntity<List<SecRole>>(secRoleService.findRoleJoinRightsGroups(), HttpStatus.OK);
	}

}
