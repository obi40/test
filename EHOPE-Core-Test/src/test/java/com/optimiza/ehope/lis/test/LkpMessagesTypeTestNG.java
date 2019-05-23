package com.optimiza.ehope.lis.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import com.optimiza.core.common.data.model.TransField;
import com.optimiza.ehope.lis.lkp.model.LkpMessagesType;
import com.optimiza.ehope.lis.lkp.service.LkpMessagesTypeService;

/**
 * LkpMessagesTypeTestNG.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Sep/12/2017
 **/

@Test(priority = 1)
@ContextConfiguration(locations = { "classpath:spring-test-config.xml" })
public class LkpMessagesTypeTestNG extends AbstractTestNGSpringContextTests {

	@Autowired
	private LkpMessagesTypeService lkpMessagesTypeService;

	private Long createdLkpMessagesTypeRid;

	@Test(priority = 1, enabled = true)
	public void testCreateLkpMessagesType() {
		System.out.println("##########");
		System.out.println("testCreateLkpMessagesType()");

		LkpMessagesType lkpMessagesType = new LkpMessagesType();
		TransField tf = new TransField();
		tf.put("en_us", "type1");
		tf.put("ar_jo", "نوع1");
		lkpMessagesType.setName(tf);
		//lkpMessagesType = this.lkpMessagesTypeService.createLkpMessagesType(lkpMessagesType);
		this.createdLkpMessagesTypeRid = lkpMessagesType.getRid();

		System.out.println("Created LkpMessagesType with Id[" + lkpMessagesType.getRid() + "]");
		System.out.println("##########");

	}

	@Test(priority = 2, enabled = true)
	public void testFindLkpMessagesTypeById() {
		System.out.println("##########");
		System.out.println("testFindLkpMessagesTypeById()");

		//LkpMessagesType lkpMessagesType = this.lkpMessagesTypeService.findLkpMessagesTypeById(this.createdLkpMessagesTypeRid);

		//System.out.println("Found LkpMessagesType with Id[" + lkpMessagesType.getRid() + "]: " + lkpMessagesType);
		System.out.println("##########");

	}

	@Test(priority = 3, enabled = true)
	public void testUpdateLkpMessagesType() {
		System.out.println("##########");
		System.out.println("testUpdateLkpMessagesType()");

		//LkpMessagesType lkpMessagesType = this.lkpMessagesTypeService.findLkpMessagesTypeById(this.createdLkpMessagesTypeRid);

		// UPDATE VALUES

		//lkpMessagesType = this.lkpMessagesTypeService.updateLkpMessagesType(lkpMessagesType,LkpMessagesType.class);

		//System.out.println("Updated LkpMessagesType with Id[" + lkpMessagesType.getRid() + "]: " + lkpMessagesType);
		System.out.println("##########");

	}

	@Test(priority = 4, enabled = true)
	public void testDeleteLkpMessagesType() {
		System.out.println("##########");
		System.out.println("testDeleteLkpMessagesType()");

		LkpMessagesType lkpMessagesType = this.lkpMessagesTypeService.findLkpMessagesTypeById(62L);

		System.out.println("CreationDate: " + lkpMessagesType.getCreationDate());

		try {
			this.lkpMessagesTypeService.deleteLkpMessagesType(lkpMessagesType, LkpMessagesType.class);
		} catch (DataIntegrityViolationException ex) {

		}
		System.out.println("Deleted LkpMessagesType with Id[" + lkpMessagesType.getRid() + "]");

		System.out.println("##########");

	}

}
