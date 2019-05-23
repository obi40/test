package com.optimiza.ehope.lis.test;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import com.optimiza.core.admin.lkp.model.LkpTenantLicenseType;
import com.optimiza.core.admin.lkp.service.LkpTenantLicenseTypeService;
import com.optimiza.core.common.data.model.TransField;

/**
 * LkpTenantLicenseTypeServiceTestNG.class
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Aug/3/2017
 **/
@Test(priority = 1)
@ContextConfiguration(locations = { "classpath:spring-test-config.xml" })
public class LkpTenantLicenseTypeServiceTestNG extends AbstractTestNGSpringContextTests {

	@Autowired
	private LkpTenantLicenseTypeService service;

	private LkpTenantLicenseType createdLtLt;

	@Test(priority = 1, enabled = true)
	public void testCreateLkpTenantLicenseType() {

		LkpTenantLicenseType ltlt = new LkpTenantLicenseType();
		ltlt.setCode("1010");

		TransField name = new TransField();
		name.put("en_us", "Aji");
		name.put("Fr", "");
		name.put("ar_jo", "ابجد");

		ltlt.setName(name);
		ltlt.setCreatedBy(1L);
		ltlt.setCreationDate(new Date());

		System.out.println("##########");

		this.createdLtLt = this.service.createTenLicType(ltlt);

		System.out.println("Created LtLt with Id[" + this.createdLtLt.getRid() + "]");

		System.out.println("##########");
	}

	@Test(priority = 2, enabled = true)
	public void testFindLkpTenantLicenseTypeById() {

		System.out.println("##########");

		LkpTenantLicenseType ltlt = this.service.findTenLicTypeById(this.createdLtLt.getRid());
		System.out.println("Found LtLt with Id[" + this.createdLtLt.getRid() + "]: " + ltlt);

		System.out.println("##########");
	}

	@Test(priority = 3, enabled = true)
	public void testUpdateLkpTenantLicenseType() {

		System.out.println("##########");

		createdLtLt.setCode("20001");
		createdLtLt = this.service.updateTenLicType(createdLtLt);

		System.out.println("Updated LtLt with Id[" + this.createdLtLt.getRid() + "]: " + createdLtLt);

		System.out.println("##########");

	}

	@Test(priority = 4, enabled = true)
	public void testDeleteLkpTenantLicenseType() {

		System.out.println("##########");

		createdLtLt = this.service.findById(5L);
		this.service.deleteTenLicType(createdLtLt);
		System.out.println("Deleted LtLt with Id[" + this.createdLtLt.getRid() + "]");

		System.out.println("##########");

	}

}
