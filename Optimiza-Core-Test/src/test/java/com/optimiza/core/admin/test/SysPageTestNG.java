package com.optimiza.core.admin.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import com.optimiza.core.system.model.SysPage;
import com.optimiza.core.system.service.SysPageService;

/**
 * SysPageTestNG.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Dec/05/2017
 **/

@Test(priority = 1)
@ContextConfiguration(locations = { "classpath:spring-test-config.xml" })
public class SysPageTestNG extends AbstractTestNGSpringContextTests {

	@Autowired
	private SysPageService sysPageService;

	private Long createdSysPageRid;

	@Test(priority = 1, enabled = true)
	public void testCreateSysPage() {
		System.out.println("##########");
		System.out.println("testCreateSysPage()");

		SysPage sysPage = new SysPage();
		sysPage = this.sysPageService.createSysPage(sysPage);
		this.createdSysPageRid = sysPage.getRid();

		System.out.println("Created SysPage with Id[" + sysPage.getRid() + "]");
		System.out.println("##########");

	}

	@Test(priority = 2, enabled = true)
	public void testFindSysPageById() {
		System.out.println("##########");
		System.out.println("testFindSysPageById()");

		SysPage sysPage = this.sysPageService.findSysPageById(this.createdSysPageRid);

		System.out.println("Found SysPage with Id[" + sysPage.getRid() + "]: " + sysPage);
		System.out.println("##########");

	}

	@Test(priority = 3, enabled = true)
	public void testUpdateSysPage() {
		System.out.println("##########");
		System.out.println("testUpdateSysPage()");

		SysPage sysPage = this.sysPageService.findSysPageById(this.createdSysPageRid);

		// UPDATE VALUES

		sysPage = this.sysPageService.updateSysPage(sysPage);

		System.out.println("Updated SysPage with Id[" + sysPage.getRid() + "]: " + sysPage);
		System.out.println("##########");

	}

	@Test(priority = 4, enabled = true)
	public void testDeleteSysPage() {
		System.out.println("##########");
		System.out.println("testDeleteSysPage()");

		SysPage sysPage = this.sysPageService.findSysPageById(this.createdSysPageRid);

		System.out.println("CreationDate: " + sysPage.getCreationDate());

		this.sysPageService.deleteSysPage(sysPage);
		System.out.println("Deleted SysPage with Id[" + sysPage.getRid() + "]");

		System.out.println("##########");

	}

}
