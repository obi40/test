package com.optimiza.ehope.lis.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import com.optimiza.core.lkp.model.ComLanguage;
import com.optimiza.core.lkp.service.ComLanguageService;

/**
 * ComLanguageTestNG.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Sep/27/2017
 **/

@Test(priority = 1)
@ContextConfiguration(locations = { "classpath:spring-test-config.xml" })
public class ComLanguageTestNG extends AbstractTestNGSpringContextTests {

	@Autowired
	private ComLanguageService comLanguageService;

	private Long createdComLanguageRid;

	@Test(priority = 1, enabled = true)
	public void testCreateComLanguage() {
		System.out.println("##########");
		System.out.println("testCreateComLanguage()");

		ComLanguage comLanguage = new ComLanguage();
		comLanguage.setName("en_us");
		comLanguage = this.comLanguageService.createComLanguage(comLanguage);
		this.createdComLanguageRid = comLanguage.getRid();

		System.out.println("Created ComLanguage with Id[" + comLanguage.getRid() + "]");
		System.out.println("##########");

	}

	@Test(priority = 2, enabled = false)
	public void testFindComLanguageById() {
		System.out.println("##########");
		System.out.println("testFindComLanguageById()");

		ComLanguage comLanguage = this.comLanguageService.findComLanguageById(this.createdComLanguageRid);

		System.out.println("Found ComLanguage with Id[" + comLanguage.getRid() + "]: " + comLanguage);
		System.out.println("##########");

	}

	@Test(priority = 3, enabled = false)
	public void testUpdateComLanguage() {
		System.out.println("##########");
		System.out.println("testUpdateComLanguage()");

		ComLanguage comLanguage = this.comLanguageService.findComLanguageById(this.createdComLanguageRid);

		// UPDATE VALUES

		comLanguage = this.comLanguageService.updateComLanguage(comLanguage);

		System.out.println("Updated ComLanguage with Id[" + comLanguage.getRid() + "]: " + comLanguage);
		System.out.println("##########");

	}

	@Test(priority = 4, enabled = false)
	public void testDeleteComLanguage() {
		System.out.println("##########");
		System.out.println("testDeleteComLanguage()");

		ComLanguage comLanguage = this.comLanguageService.findComLanguageById(this.createdComLanguageRid);

		System.out.println("CreationDate: " + comLanguage.getCreationDate());

		this.comLanguageService.deleteComLanguage(comLanguage);
		System.out.println("Deleted ComLanguage with Id[" + comLanguage.getRid() + "]");

		System.out.println("##########");

	}

}
