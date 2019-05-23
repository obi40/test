package com.optimiza.ehope.lis.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import com.optimiza.core.admin.model.SecRight;
import com.optimiza.core.admin.service.SecRightService;
import com.optimiza.core.common.data.model.TransField;

/**
 * SecRightTestNG.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Sep/27/2017
 **/

@Test(priority = 1)
@ContextConfiguration(locations = { "classpath:spring-test-config.xml" })
public class SecRightTestNG extends AbstractTestNGSpringContextTests {

	@Autowired
	private SecRightService secRightService;

	private Long createdSecRightRid;

	@Test(priority = 1, enabled = false)
	public void testCreateSecRight() {
		System.out.println("##########");
		System.out.println("testCreateSecRight()");

		SecRight secRight = new SecRight();
		TransField tf = new TransField();
		tf.put("en_us", "DEVELOPER");
		tf.put("ar_jo", "مطور");
		secRight.setName(tf);
		secRight.setCode("DEVELOPER");
		secRight = this.secRightService.createSecRight(secRight);

		System.out.println("Created SecRight with Id[" + secRight.getRid() + "]");
		System.out.println("##########");

	}

	@Test(priority = 2, enabled = false)
	public void testFindSecRightById() {
		System.out.println("##########");
		System.out.println("testFindSecRightById()");

		SecRight secRight = this.secRightService.findSecRightById(this.createdSecRightRid);

		System.out.println("Found SecRight with Id[" + secRight.getRid() + "]: " + secRight);
		System.out.println("##########");

	}

	@Test(priority = 3, enabled = false)
	public void testUpdateSecRight() {
		System.out.println("##########");
		System.out.println("testUpdateSecRight()");

		SecRight secRight = this.secRightService.findSecRightById(this.createdSecRightRid);

		// UPDATE VALUES

		secRight = this.secRightService.updateSecRight(secRight);

		System.out.println("Updated SecRight with Id[" + secRight.getRid() + "]: " + secRight);
		System.out.println("##########");

	}

	@Test(priority = 4, enabled = false)
	public void testDeleteSecRight() {
		System.out.println("##########");
		System.out.println("testDeleteSecRight()");

		SecRight secRight = this.secRightService.findSecRightById(this.createdSecRightRid);

		System.out.println("CreationDate: " + secRight.getCreationDate());

		//this.secRightService.deleteSecRight(secRight);
		System.out.println("Deleted SecRight with Id[" + secRight.getRid() + "]");

		System.out.println("##########");

	}

}
