package com.optimiza.core.admin.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import com.optimiza.core.lkp.model.ComTenantLanguage;
import com.optimiza.core.lkp.service.ComLanguageService;
import com.optimiza.core.lkp.service.ComTenantLanguageService;

/**
 * ComTenantLanguageTestNG.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Nov/07/2017
 **/

@Test(priority = 1)
@ContextConfiguration(locations = { "classpath:spring-test-config.xml" })
public class ComTenantLanguageTestNG extends AbstractTestNGSpringContextTests {

	@Autowired
	private ComTenantLanguageService comTenantLanguageService;

	@Autowired
	private ComLanguageService comLanguageService;

	private Long createdComTenantLanguageRid;

	@Test(priority = 1, enabled = false)
	public void testCreateComTenantLanguage() {
		System.out.println("##########");
		System.out.println("testCreateComTenantLanguage()");

		ComTenantLanguage comTenantLanguage = new ComTenantLanguage();
		comTenantLanguage.setComLanguage(comLanguageService.findComLanguageById(1L));
		comTenantLanguage.setTenantId(1L);
		comTenantLanguage.setIsPrimary(Boolean.TRUE);
		//comTenantLanguage = this.comTenantLanguageService.createComTenantLanguage(comTenantLanguage);

		this.createdComTenantLanguageRid = comTenantLanguage.getRid();

		System.out.println("Created ComTenantLanguage with Id[" + comTenantLanguage.getRid() + "]");
		System.out.println("##########");

	}

	@Test(priority = 2, enabled = false)
	public void testFindComTenantLanguageById() {
		System.out.println("##########");
		System.out.println("testFindComTenantLanguageById()");

		ComTenantLanguage comTenantLanguage = this.comTenantLanguageService.findComTenantLanguageById(this.createdComTenantLanguageRid);

		System.out.println("Found ComTenantLanguage with Id[" + comTenantLanguage.getRid() + "]: " + comTenantLanguage);
		System.out.println("##########");

	}

	@Test(priority = 3, enabled = false)
	public void testUpdateComTenantLanguage() {
		System.out.println("##########");
		System.out.println("testUpdateComTenantLanguage()");

		ComTenantLanguage comTenantLanguage = this.comTenantLanguageService.findComTenantLanguageById(this.createdComTenantLanguageRid);

		// UPDATE VALUES

		comTenantLanguage = this.comTenantLanguageService.updateComTenantLanguage(comTenantLanguage);

		System.out.println("Updated ComTenantLanguage with Id[" + comTenantLanguage.getRid() + "]: " + comTenantLanguage);
		System.out.println("##########");

	}

	@Test(priority = 4, enabled = false)
	public void testDeleteComTenantLanguage() {
		System.out.println("##########");
		System.out.println("testDeleteComTenantLanguage()");

		ComTenantLanguage comTenantLanguage = this.comTenantLanguageService.findComTenantLanguageById(this.createdComTenantLanguageRid);

		System.out.println("CreationDate: " + comTenantLanguage.getCreationDate());

		this.comTenantLanguageService.deleteComTenantLanguage(comTenantLanguage);
		System.out.println("Deleted ComTenantLanguage with Id[" + comTenantLanguage.getRid() + "]");

		System.out.println("##########");

	}

}
