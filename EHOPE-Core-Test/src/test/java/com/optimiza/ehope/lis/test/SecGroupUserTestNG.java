package com.optimiza.ehope.lis.test;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import com.optimiza.core.admin.model.SecGroupUser;
import com.optimiza.core.admin.service.SecGroupService;
import com.optimiza.core.admin.service.SecGroupUserService;
import com.optimiza.core.admin.service.SecUserService;

/**
 * SecGroupUserTestNG.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Sep/27/2017
 **/

@Test(priority = 1)
@ContextConfiguration(locations = { "classpath:spring-test-config.xml" })
@Transactional
public class SecGroupUserTestNG extends AbstractTestNGSpringContextTests {

	@Autowired
	private SecGroupUserService secGroupUserService;

	@Autowired
	private SecUserService secUserService;

	@Autowired
	private SecGroupService secGroupService;

	private Long createdSecGroupUserRid;

	@Test(priority = 1, enabled = false)
	public void testCreateSecGroupUser() {
		System.out.println("##########");
		System.out.println("testCreateSecGroupUser()");

		SecGroupUser secGroupUser = new SecGroupUser();
		secGroupUser.setSecGroup(this.secGroupService.findById(8L));
		secGroupUser.setSecUser(secUserService.findById(1L));
		secGroupUser = this.secGroupUserService.createSecGroupUser(secGroupUser);

		secGroupUser = new SecGroupUser();
		secGroupUser.setSecGroup(this.secGroupService.findById(8L));
		secGroupUser.setSecUser(secUserService.findById(2L));
		secGroupUser = this.secGroupUserService.createSecGroupUser(secGroupUser);

		this.createdSecGroupUserRid = secGroupUser.getRid();

		System.out.println("Created SecGroupUser with Id[" + secGroupUser.getRid() + "]");
		System.out.println("##########");

	}

	@Test(priority = 2, enabled = false)
	public void testFindSecGroupUserById() {
		System.out.println("##########");
		System.out.println("testFindSecGroupUserById()");

		SecGroupUser secGroupUser = this.secGroupUserService.findSecGroupUserById(this.createdSecGroupUserRid);

		System.out.println("Found SecGroupUser with Id[" + secGroupUser.getRid() + "]: " + secGroupUser);
		System.out.println("##########");

	}

	@Test(priority = 3, enabled = false)
	public void testUpdateSecGroupUser() {
		System.out.println("##########");
		System.out.println("testUpdateSecGroupUser()");

		SecGroupUser secGroupUser = this.secGroupUserService.findSecGroupUserById(this.createdSecGroupUserRid);

		// UPDATE VALUES

		secGroupUser = this.secGroupUserService.updateSecGroupUser(secGroupUser);

		System.out.println("Updated SecGroupUser with Id[" + secGroupUser.getRid() + "]: " + secGroupUser);
		System.out.println("##########");

	}

	@Test(priority = 4, enabled = false)
	public void testDeleteSecGroupUser() {
		System.out.println("##########");
		System.out.println("testDeleteSecGroupUser()");

		SecGroupUser secGroupUser = this.secGroupUserService.findSecGroupUserById(this.createdSecGroupUserRid);

		System.out.println("CreationDate: " + secGroupUser.getCreationDate());

		this.secGroupUserService.deleteSecGroupUser(secGroupUser);
		System.out.println("Deleted SecGroupUser with Id[" + secGroupUser.getRid() + "]");

		System.out.println("##########");

	}

}
