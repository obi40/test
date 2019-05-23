package com.optimiza.ehope.lis.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import com.optimiza.core.common.data.model.TransField;
import com.optimiza.ehope.lis.lkp.model.LkpResultValueType;
import com.optimiza.ehope.lis.lkp.service.LkpResultValueTypeService;

/**
 * LkpResultTypeTestNG.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Oct/12/2017
 **/

@Test(priority = 1)
@ContextConfiguration(locations = { "classpath:spring-test-config.xml" })
public class LkpResultValueTypeTestNG extends AbstractTestNGSpringContextTests {

	@Autowired
	private LkpResultValueTypeService lkpResultTypeService;

	private Long createdLkpResultTypeRid;

	@Test(priority = 1, enabled = true)
	public void testCreateLkpResultType() {
		System.out.println("##########");
		System.out.println("testCreateLkpResultType()");

		LkpResultValueType lkpResultType = new LkpResultValueType();
		TransField tf = new TransField();
		tf.put("k", "v");
		lkpResultType.setName(tf);
		lkpResultType.setCode("code");
		lkpResultType = this.lkpResultTypeService.createLkpResultType(lkpResultType);
		this.createdLkpResultTypeRid = lkpResultType.getRid();

		System.out.println("Created LkpResultType with Id[" + lkpResultType.getRid() + "]");
		System.out.println("##########");

	}

	@Test(priority = 2, enabled = true)
	public void testFindLkpResultTypeById() {
		System.out.println("##########");
		System.out.println("testFindLkpResultTypeById()");

		LkpResultValueType lkpResultType = this.lkpResultTypeService.findLkpResultTypeById(this.createdLkpResultTypeRid);

		System.out.println("Found LkpResultType with Id[" + lkpResultType.getRid() + "]: " + lkpResultType);
		System.out.println("##########");

	}

	@Test(priority = 3, enabled = true)
	public void testUpdateLkpResultType() {
		System.out.println("##########");
		System.out.println("testUpdateLkpResultType()");

		LkpResultValueType lkpResultType = this.lkpResultTypeService.findLkpResultTypeById(this.createdLkpResultTypeRid);

		// UPDATE VALUES

		lkpResultType = this.lkpResultTypeService.updateLkpResultType(lkpResultType);

		System.out.println("Updated LkpResultType with Id[" + lkpResultType.getRid() + "]: " + lkpResultType);
		System.out.println("##########");

	}

	@Test(priority = 4, enabled = true)
	public void testDeleteLkpResultType() {
		System.out.println("##########");
		System.out.println("testDeleteLkpResultType()");

		LkpResultValueType lkpResultType = this.lkpResultTypeService.findLkpResultTypeById(this.createdLkpResultTypeRid);

		System.out.println("CreationDate: " + lkpResultType.getCreationDate());

		this.lkpResultTypeService.deleteLkpResultType(lkpResultType);
		System.out.println("Deleted LkpResultType with Id[" + lkpResultType.getRid() + "]");

		System.out.println("##########");

	}

}
