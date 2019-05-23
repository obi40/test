package com.optimiza.ehope.lis.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import com.optimiza.core.admin.model.SecGroupRole;
import com.optimiza.core.admin.service.SecGroupRoleService;
import com.optimiza.core.admin.service.SecGroupService;
import com.optimiza.core.admin.service.SecRoleService;

/**
 * SecGroupRoleTestNG.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Sep/27/2017
 **/

@Test(priority = 1)
@ContextConfiguration(locations = { "classpath:spring-test-config.xml" })
public class SecGroupRoleTestNG extends AbstractTestNGSpringContextTests {

	@Autowired
	private SecGroupRoleService secGroupRoleService;

	@Autowired
	private SecGroupService secGroupService;

	@Autowired
	private SecRoleService secRoleService;

	private Long createdSecGroupRoleRid;

	@Test(priority = 1, enabled = true)
	public void testCreateSecGroupRole() {
		System.out.println("##########");
		System.out.println("testCreateSecGroupRole()");

		SecGroupRole secGroupRole = new SecGroupRole();
		secGroupRole.setSecGroup(secGroupService.findById(8L));
		secGroupRole.setSecRole(secRoleService.findById(2L));
		secGroupRole = this.secGroupRoleService.createSecGroupRole(secGroupRole);

		secGroupRole = new SecGroupRole();
		secGroupRole.setSecGroup(secGroupService.findById(8L));
		secGroupRole.setSecRole(secRoleService.findById(3L));
		secGroupRole = this.secGroupRoleService.createSecGroupRole(secGroupRole);

		secGroupRole = new SecGroupRole();
		secGroupRole.setSecGroup(secGroupService.findById(8L));
		secGroupRole.setSecRole(secRoleService.findById(4L));
		secGroupRole = this.secGroupRoleService.createSecGroupRole(secGroupRole);

		this.createdSecGroupRoleRid = secGroupRole.getRid();

		System.out.println("Created SecGroupRole with Id[" + secGroupRole.getRid() + "]");
		System.out.println("##########");

	}

	@Test(priority = 2, enabled = true)
	public void testFindSecGroupRoleById() {
		System.out.println("##########");
		System.out.println("testFindSecGroupRoleById()");

		SecGroupRole secGroupRole = this.secGroupRoleService.findSecGroupRoleById(this.createdSecGroupRoleRid);

		System.out.println("Found SecGroupRole with Id[" + secGroupRole.getRid() + "]: " + secGroupRole);
		System.out.println("##########");

	}

	@Test(priority = 3, enabled = true)
	public void testUpdateSecGroupRole() {
		System.out.println("##########");
		System.out.println("testUpdateSecGroupRole()");

		SecGroupRole secGroupRole = this.secGroupRoleService.findSecGroupRoleById(this.createdSecGroupRoleRid);

		// UPDATE VALUES

		secGroupRole = this.secGroupRoleService.updateSecGroupRole(secGroupRole);

		System.out.println("Updated SecGroupRole with Id[" + secGroupRole.getRid() + "]: " + secGroupRole);
		System.out.println("##########");

	}

	@Test(priority = 4, enabled = false)
	public void testDeleteSecGroupRole() {
		System.out.println("##########");
		System.out.println("testDeleteSecGroupRole()");

		SecGroupRole secGroupRole = this.secGroupRoleService.findSecGroupRoleById(this.createdSecGroupRoleRid);

		System.out.println("CreationDate: " + secGroupRole.getCreationDate());

		//this.secGroupRoleService.deleteSecGroupRole(secGroupRole);
		System.out.println("Deleted SecGroupRole with Id[" + secGroupRole.getRid() + "]");

		System.out.println("##########");

	}

}
