package com.optimiza.ehope.lis.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import com.optimiza.core.admin.model.SecRole;
import com.optimiza.core.admin.service.SecRoleService;
import com.optimiza.core.common.data.model.TransField;

/**
 * SecRoleTestNG.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Sep/27/2017
 **/

@Test(priority = 1)
@ContextConfiguration(locations = { "classpath:spring-test-config.xml" })
public class SecRoleTestNG extends AbstractTestNGSpringContextTests {

	@Autowired
	private SecRoleService secRoleService;

	private Long createdSecRoleRid;

	@Test(priority = 1, enabled = true)
	public void testCreateSecRole() {
		System.out.println("##########");
		System.out.println("testCreateSecRole()");

		SecRole secRole = new SecRole();
		for (int i = 1; i <= 5; i++) {
			secRole = new SecRole();
			TransField tf = new TransField();
			tf.put("en_us", "Role " + i);
			tf.put("ar_jo", "دور" + i);
			secRole.setName(tf);
			//secRole = this.secRoleService.createSecRole(secRole);
			this.createdSecRoleRid = secRole.getRid();
		}
		System.out.println("Created SecRole with Id[" + secRole.getRid() + "]");
		System.out.println("##########");

	}

	@Test(priority = 2, enabled = true)
	public void testFindSecRoleById() {
		System.out.println("##########");
		System.out.println("testFindSecRoleById()");

		SecRole secRole = this.secRoleService.findSecRoleById(this.createdSecRoleRid);

		System.out.println("Found SecRole with Id[" + secRole.getRid() + "]: " + secRole);
		System.out.println("##########");

	}

	@Test(priority = 3, enabled = true)
	public void testUpdateSecRole() {
		System.out.println("##########");
		System.out.println("testUpdateSecRole()");

		SecRole secRole = this.secRoleService.findSecRoleById(this.createdSecRoleRid);

		// UPDATE VALUES

		//secRole = this.secRoleService.updateSecRole(secRole);

		System.out.println("Updated SecRole with Id[" + secRole.getRid() + "]: " + secRole);
		System.out.println("##########");

	}

	@Test(priority = 4, enabled = true)
	public void testDeleteSecRole() {
		System.out.println("##########");
		System.out.println("testDeleteSecRole()");

		SecRole secRole = this.secRoleService.findSecRoleById(this.createdSecRoleRid);

		System.out.println("CreationDate: " + secRole.getCreationDate());

		this.secRoleService.deleteSecRole(secRole);
		System.out.println("Deleted SecRole with Id[" + secRole.getRid() + "]");

		System.out.println("##########");

	}

}
