package com.optimiza.ehope.lis.test;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import com.optimiza.core.admin.lkp.model.LkpTenantStatus;
import com.optimiza.core.admin.lkp.service.LkpTenantStatusService;
import com.optimiza.core.common.data.model.TransField;

/**
 * LkpTenantStatusServiceTestNG.class
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Aug/3/2017
 **/
@Test(priority = 1)
@ContextConfiguration(locations = { "classpath:spring-test-config.xml" })
public class LkpTenantStatusServiceTestNG extends AbstractTestNGSpringContextTests {

	@Autowired
	private LkpTenantStatusService service;

	private LkpTenantStatus createdLts;

	@Test(priority = 1, enabled = true)
	public void testCreateLkpTenantStatus() {

		LkpTenantStatus lts = new LkpTenantStatus();

		lts.setCode("1010");

		TransField name = new TransField();
		name.put("en_us", "Flava");
		name.put("Fr", "");
		name.put("ar_jo", "Ø³Ø§Ù�Ø§");

		lts.setName(name);
		lts.setCreatedBy(1L);
		lts.setCreationDate(new Date());

		System.out.println("##########");

		this.createdLts = this.service.createLkpTenantStatus(lts);

		System.out.println("Created Lts with Id[" + this.createdLts.getRid() + "]");

		System.out.println("##########");

	}

	@Test(priority = 2, enabled = true)
	public void testFindLkpTenantStatusById() {

		System.out.println("##########");

		LkpTenantStatus ltlt = this.service.findLkpTenantStatusById(this.createdLts.getRid());
		System.out.println("Found Lts with Id[" + this.createdLts.getRid() + "]: " + ltlt);

		System.out.println("##########");
	}

	@Test(priority = 3, enabled = true)
	public void testUpdateLkpTenantStatus() {

		System.out.println("##########");

		createdLts.setCode("20002");
		createdLts = this.service.updateLkpTenantStatus(createdLts);

		System.out.println("Updated Lts with Id[" + this.createdLts.getRid() + "]: " + createdLts);

		System.out.println("##########");

	}

	@Test(priority = 4, enabled = true)
	public void testDeleteLkpTenantLicenseType() {

		System.out.println("##########");
		createdLts = this.service.findById(5L);
		this.service.deleteLkpTenantStatus(createdLts);
		System.out.println("Deleted Lts with Id[" + this.createdLts.getRid() + "]");

		System.out.println("##########");

	}

}
