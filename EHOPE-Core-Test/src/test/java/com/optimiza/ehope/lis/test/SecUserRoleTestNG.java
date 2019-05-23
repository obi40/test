package com.optimiza.ehope.lis.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import com.optimiza.core.admin.model.SecUserRole;
import com.optimiza.core.admin.service.SecRoleService;
import com.optimiza.core.admin.service.SecUserRoleService;
import com.optimiza.core.admin.service.SecUserService;

/**
 * SecUserRoleTestNG.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Sep/27/2017
 **/

@Test(priority = 1)
@ContextConfiguration(locations = { "classpath:spring-test-config.xml" })
public class SecUserRoleTestNG extends AbstractTestNGSpringContextTests {

	@Autowired
	private SecUserRoleService secUserRoleService;

	@Autowired
	private SecUserService secUserService;

	@Autowired
	private SecRoleService secRoleService;

	private Long createdSecUserRoleRid;

	@Test(priority = 1, enabled = false)
	public void testCreateSecUserRole() {
		System.out.println("##########");
		System.out.println("testCreateSecUserRole()");

		SecUserRole secUserRole = new SecUserRole();
		secUserRole.setSecRole(secRoleService.findById(2L));
		secUserRole.setSecUser(secUserService.findById(1L));
		secUserRole = this.secUserRoleService.createSecUserRole(secUserRole);

		secUserRole = new SecUserRole();
		secUserRole.setSecRole(secRoleService.findById(2L));
		secUserRole.setSecUser(secUserService.findById(2L));
		secUserRole = this.secUserRoleService.createSecUserRole(secUserRole);

		this.createdSecUserRoleRid = secUserRole.getRid();

		System.out.println("Created SecUserRole with Id[" + secUserRole.getRid() + "]");
		System.out.println("##########");

	}

	@Test(priority = 2, enabled = false)
	public void testFindSecUserRoleById() {
		System.out.println("##########");
		System.out.println("testFindSecUserRoleById()");

		SecUserRole secUserRole = this.secUserRoleService.findSecUserRoleById(this.createdSecUserRoleRid);

		System.out.println("Found SecUserRole with Id[" + secUserRole.getRid() + "]: " + secUserRole);
		System.out.println("##########");

	}

	@Test(priority = 3, enabled = false)
	public void testUpdateSecUserRole() {
		System.out.println("##########");
		System.out.println("testUpdateSecUserRole()");

		SecUserRole secUserRole = this.secUserRoleService.findSecUserRoleById(this.createdSecUserRoleRid);

		// UPDATE VALUES

		secUserRole = this.secUserRoleService.updateSecUserRole(secUserRole);

		System.out.println("Updated SecUserRole with Id[" + secUserRole.getRid() + "]: " + secUserRole);
		System.out.println("##########");

	}

	@Test(priority = 4, enabled = false)
	public void testDeleteSecUserRole() {
		System.out.println("##########");
		System.out.println("testDeleteSecUserRole()");

		SecUserRole secUserRole = this.secUserRoleService.findSecUserRoleById(this.createdSecUserRoleRid);

		System.out.println("CreationDate: " + secUserRole.getCreationDate());

		//this.secUserRoleService.deleteSecUserRole(secUserRole);
		System.out.println("Deleted SecUserRole with Id[" + secUserRole.getRid() + "]");

		System.out.println("##########");

	}

}
