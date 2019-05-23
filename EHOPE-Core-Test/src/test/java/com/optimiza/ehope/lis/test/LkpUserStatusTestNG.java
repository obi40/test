package com.optimiza.ehope.lis.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import com.optimiza.core.admin.lkp.model.LkpUserStatus;
import com.optimiza.core.admin.lkp.service.LkpUserStatusService;
import com.optimiza.core.common.data.model.TransField;

/**
 * LkpUserStatusTestNG.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Sep/27/2017
 **/

@Test(priority = 1)
@ContextConfiguration(locations = { "classpath:spring-test-config.xml" })
public class LkpUserStatusTestNG extends AbstractTestNGSpringContextTests {

	@Autowired
	private LkpUserStatusService lkpUserStatusService;

	private Long createdLkpUserStatusRid;

	@Test(priority = 1, enabled = true)
	public void testCreateLkpUserStatus() {
		System.out.println("##########");
		System.out.println("testCreateLkpUserStatus()");

		LkpUserStatus lkpUserStatus = new LkpUserStatus();
		lkpUserStatus.setCode("c1");
		TransField tf = new TransField();
		tf.put("t", "v");
		lkpUserStatus.setName(tf);
		lkpUserStatus = this.lkpUserStatusService.createLkpUserStatus(lkpUserStatus);
		this.createdLkpUserStatusRid = lkpUserStatus.getRid();

		System.out.println("Created LkpUserStatus with Id[" + lkpUserStatus.getRid() + "]");
		System.out.println("##########");

	}

	@Test(priority = 2, enabled = true)
	public void testFindLkpUserStatusById() {
		System.out.println("##########");
		System.out.println("testFindLkpUserStatusById()");

		LkpUserStatus lkpUserStatus = this.lkpUserStatusService.findLkpUserStatusById(this.createdLkpUserStatusRid);

		System.out.println("Found LkpUserStatus with Id[" + lkpUserStatus.getRid() + "]: " + lkpUserStatus);
		System.out.println("##########");

	}

	@Test(priority = 3, enabled = true)
	public void testUpdateLkpUserStatus() {
		System.out.println("##########");
		System.out.println("testUpdateLkpUserStatus()");

		LkpUserStatus lkpUserStatus = this.lkpUserStatusService.findLkpUserStatusById(this.createdLkpUserStatusRid);

		// UPDATE VALUES

		lkpUserStatus = this.lkpUserStatusService.updateLkpUserStatus(lkpUserStatus);

		System.out.println("Updated LkpUserStatus with Id[" + lkpUserStatus.getRid() + "]: " + lkpUserStatus);
		System.out.println("##########");

	}

	@Test(priority = 4, enabled = true)
	public void testDeleteLkpUserStatus() {
		System.out.println("##########");
		System.out.println("testDeleteLkpUserStatus()");

		LkpUserStatus lkpUserStatus = this.lkpUserStatusService.findLkpUserStatusById(this.createdLkpUserStatusRid);

		System.out.println("CreationDate: " + lkpUserStatus.getCreationDate());

		//this.lkpUserStatusService.deleteLkpUserStatus(lkpUserStatus);
		System.out.println("Deleted LkpUserStatus with Id[" + lkpUserStatus.getRid() + "]");

		System.out.println("##########");

	}

}
