package com.optimiza.ehope.lis.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import com.optimiza.core.admin.model.SecRoleRight;
import com.optimiza.core.admin.service.SecRightService;
import com.optimiza.core.admin.service.SecRoleRightService;
import com.optimiza.core.admin.service.SecRoleService;

/**
 * SecRoleRightTestNG.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Sep/27/2017
 **/

@Test(priority = 1)
@ContextConfiguration(locations = { "classpath:spring-test-config.xml" })
public class SecRoleRightTestNG extends AbstractTestNGSpringContextTests {

	@Autowired
	private SecRoleRightService secRoleRightService;

	@Autowired
	private SecRoleService secRoleService;

	@Autowired
	private SecRightService secRightService;

	private Long createdSecRoleRightRid;

	@Test(priority = 1, enabled = true)
	public void testCreateSecRoleRight() {
		System.out.println("##########");
		System.out.println("testCreateSecRoleRight()");

		SecRoleRight secRoleRight = new SecRoleRight();
		secRoleRight.setSecRight(secRightService.findById(3L));
		secRoleRight.setSecRole(secRoleService.findById(2L));
		secRoleRight = this.secRoleRightService.createSecRoleRight(secRoleRight);

		secRoleRight = new SecRoleRight();
		secRoleRight.setSecRight(secRightService.findById(3L));
		secRoleRight.setSecRole(secRoleService.findById(3L));
		secRoleRight = this.secRoleRightService.createSecRoleRight(secRoleRight);

		secRoleRight = new SecRoleRight();
		secRoleRight.setSecRight(secRightService.findById(3L));
		secRoleRight.setSecRole(secRoleService.findById(4L));
		secRoleRight = this.secRoleRightService.createSecRoleRight(secRoleRight);

		this.createdSecRoleRightRid = secRoleRight.getRid();

		System.out.println("Created SecRoleRight with Id[" + secRoleRight.getRid() + "]");
		System.out.println("##########");

	}

	@Test(priority = 2, enabled = true)
	public void testFindSecRoleRightById() {
		System.out.println("##########");
		System.out.println("testFindSecRoleRightById()");

		SecRoleRight secRoleRight = this.secRoleRightService.findSecRoleRightById(this.createdSecRoleRightRid);

		System.out.println("Found SecRoleRight with Id[" + secRoleRight.getRid() + "]: " + secRoleRight);
		System.out.println("##########");

	}

	@Test(priority = 3, enabled = true)
	public void testUpdateSecRoleRight() {
		System.out.println("##########");
		System.out.println("testUpdateSecRoleRight()");

		SecRoleRight secRoleRight = this.secRoleRightService.findSecRoleRightById(this.createdSecRoleRightRid);

		// UPDATE VALUES

		secRoleRight = this.secRoleRightService.updateSecRoleRight(secRoleRight);

		System.out.println("Updated SecRoleRight with Id[" + secRoleRight.getRid() + "]: " + secRoleRight);
		System.out.println("##########");

	}

	@Test(priority = 4, enabled = false)
	public void testDeleteSecRoleRight() {
		System.out.println("##########");
		System.out.println("testDeleteSecRoleRight()");

		SecRoleRight secRoleRight = this.secRoleRightService.findSecRoleRightById(this.createdSecRoleRightRid);

		System.out.println("CreationDate: " + secRoleRight.getCreationDate());

		//this.secRoleRightService.deleteSecRoleRight(secRoleRight);
		System.out.println("Deleted SecRoleRight with Id[" + secRoleRight.getRid() + "]");

		System.out.println("##########");

	}

}
