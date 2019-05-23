package com.optimiza.ehope.lis.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import com.optimiza.core.common.data.model.TransField;
import com.optimiza.ehope.lis.lkp.service.LkpMessagesTypeService;
import com.optimiza.ehope.lis.model.ComTenantMessage;
import com.optimiza.ehope.lis.service.ComTenantMessageService;

/**
 * ComTenantMessageTestNG.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Sep/12/2017
 **/

@Test(priority = 1)
@ContextConfiguration(locations = { "classpath:spring-test-config.xml" })
public class ComTenantMessageTestNG extends AbstractTestNGSpringContextTests {

	@Autowired
	private ComTenantMessageService comTenantMessageService;

	@Autowired
	private LkpMessagesTypeService lkpMessagesTypeService;

	private Long createdComTenantMessageRid;

	@Test(priority = 1, enabled = false)
	public void testCreateComTenantMessage() {
		System.out.println("##########");
		System.out.println("testCreateComTenantMessage()");

		ComTenantMessage comTenantMessages = new ComTenantMessage();
		TransField tf = new TransField();
		tf.put("en_us", "Msg1");
		tf.put("ar_jo", "رسالة1");
		comTenantMessages.setDescription(tf);
		comTenantMessages.setCode("patient.notDead.yet");
		comTenantMessages.setLkpMessagesType(this.lkpMessagesTypeService.findById(59L));
		//comTenantMessages = this.comTenantMessageService.createComTenantMessage(comTenantMessages);

		//this.createdComTenantMessageRid = comTenantMessages.getRid();

		//System.out.println("Created ComTenantMessage with Id[" + comTenantMessages.getRid() + "]");
		System.out.println("##########");

	}

	@Test(priority = 2, enabled = false)
	public void testFindComTenantMessageById() {
		System.out.println("##########");
		System.out.println("testFindComTenantMessageById()");

		//ComTenantMessage comTenantMessages = this.comTenantMessageService.findById(this.createdComTenantMessageRid);

		//System.out.println("Found ComTenantMessage with Id[" + comTenantMessages.getRid() + "]: " + comTenantMessages);
		System.out.println("##########");

	}

	@Test(priority = 3, enabled = false)
	public void testUpdateComTenantMessage() {
		System.out.println("##########");
		System.out.println("testUpdateComTenantMessage()");

		//ComTenantMessage comTenantMessages = this.comTenantMessageService.findById(this.createdComTenantMessageRid);

		// UPDATE VALUES

		//comTenantMessages = this.comTenantMessageService.updateComTenantMessage(comTenantMessages);

		//System.out.println("Updated ComTenantMessage with Id[" + comTenantMessages.getRid() + "]: " + comTenantMessages);
		System.out.println("##########");

	}

	@Test(priority = 4, enabled = false)
	public void testDeleteComTenantMessage() {
		System.out.println("##########");
		System.out.println("testDeleteComTenantMessage()");

		//ComTenantMessage comTenantMessages = this.comTenantMessageService.findById(1063L);

		//System.out.println("CreationDate: " + comTenantMessages.getCreationDate());

		//this.comTenantMessageService.deleteComTenantMessage(comTenantMessages, ComTenantMessage.class);
		//System.out.println("Deleted ComTenantMessage with Id[" + comTenantMessages.getRid() + "]");

		System.out.println("##########");

	}

}
