package com.optimiza.ehope.lis.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import com.optimiza.core.admin.model.SecGroup;
import com.optimiza.core.admin.service.SecGroupService;
import com.optimiza.core.common.data.model.TransField;

/**
 * SecGroupTestNG.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Sep/27/2017
 **/

@Test(priority = 1)
@ContextConfiguration(locations = { "classpath:spring-test-config.xml" })
public class SecGroupTestNG extends AbstractTestNGSpringContextTests {

	@Autowired
	private SecGroupService secGroupService;

	private Long createdSecGroupRid;

	@Test(priority = 1, enabled = false)
	public void testCreateSecGroup() {
		System.out.println("##########");
		System.out.println("testCreateSecGroup()");

		SecGroup secGroup = new SecGroup();

		for (int i = 1; i <= 5; i++) {
			secGroup = new SecGroup();
			TransField tf = new TransField();
			tf.put("en_us", "Group " + i);
			tf.put("ar_jo", "جروب" + i);
			secGroup.setName(tf);

			//secGroup = this.secGroupService.createSecGroup(secGroup);
			this.createdSecGroupRid = secGroup.getRid();
		}

		System.out.println("Created SecGroup with Id[" + secGroup.getRid() + "]");
		System.out.println("##########");

	}

	@Test(priority = 2, enabled = false)
	public void testFindSecGroupById() {
		System.out.println("##########");
		System.out.println("testFindSecGroupById()");

		SecGroup secGroup = this.secGroupService.findSecGroupById(this.createdSecGroupRid);

		System.out.println("Found SecGroup with Id[" + secGroup.getRid() + "]: " + secGroup);
		System.out.println("##########");

	}

	@Test(priority = 3, enabled = false)
	public void testUpdateSecGroup() {
		System.out.println("##########");
		System.out.println("testUpdateSecGroup()");

		SecGroup secGroup = this.secGroupService.findSecGroupById(this.createdSecGroupRid);

		// UPDATE VALUES

		//secGroup = this.secGroupService.updateSecGroup(secGroup);

		System.out.println("Updated SecGroup with Id[" + secGroup.getRid() + "]: " + secGroup);
		System.out.println("##########");

	}

	@Test(priority = 4, enabled = false)
	public void testDeleteSecGroup() {
		System.out.println("##########");
		System.out.println("testDeleteSecGroup()");

		SecGroup secGroup = this.secGroupService.findSecGroupById(this.createdSecGroupRid);

		System.out.println("CreationDate: " + secGroup.getCreationDate());

		//this.secGroupService.deleteSecGroup(secGroup);
		System.out.println("Deleted SecGroup with Id[" + secGroup.getRid() + "]");

		System.out.println("##########");

	}

	@Test(priority = 4, enabled = true)
	public void test() {
		//this.secGroupService.findSecGroupWithRolesList();
	}

}
