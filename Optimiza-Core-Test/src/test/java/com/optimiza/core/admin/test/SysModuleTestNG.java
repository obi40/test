package com.optimiza.core.admin.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import com.optimiza.core.common.data.model.TransField;
import com.optimiza.core.system.model.SysModule;
import com.optimiza.core.system.service.SysModuleService;

/**
 * SysModuleTestNG.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Dec/05/2017
 **/

@Test(priority = 1)
@ContextConfiguration(locations = { "classpath:spring-test-config.xml" })
public class SysModuleTestNG extends AbstractTestNGSpringContextTests {

	@Autowired
	private SysModuleService sysModuleService;

	private Long createdSysModuleRid;

	@Test(priority = 1, enabled = true)
	public void testCreateSysModule() {
		System.out.println("##########");
		System.out.println("testCreateSysModule()");

		SysModule sysModule = new SysModule();
		TransField tf = new TransField();
		tf.put("test", "test");
		sysModule.setName(tf);
		sysModule = this.sysModuleService.createSysModule(sysModule);
		this.createdSysModuleRid = sysModule.getRid();

		System.out.println("Created SysModule with Id[" + sysModule.getRid() + "]");
		System.out.println("##########");

	}

	@Test(priority = 2, enabled = false)
	public void testFindSysModuleById() {
		System.out.println("##########");
		System.out.println("testFindSysModuleById()");

		SysModule sysModule = this.sysModuleService.findSysModuleById(this.createdSysModuleRid);

		System.out.println("Found SysModule with Id[" + sysModule.getRid() + "]: " + sysModule);
		System.out.println("##########");

	}

	@Test(priority = 3, enabled = false)
	public void testUpdateSysModule() {
		System.out.println("##########");
		System.out.println("testUpdateSysModule()");

		SysModule sysModule = this.sysModuleService.findSysModuleById(this.createdSysModuleRid);

		// UPDATE VALUES

		sysModule = this.sysModuleService.updateSysModule(sysModule);

		System.out.println("Updated SysModule with Id[" + sysModule.getRid() + "]: " + sysModule);
		System.out.println("##########");

	}

	@Test(priority = 4, enabled = false)
	public void testDeleteSysModule() {
		System.out.println("##########");
		System.out.println("testDeleteSysModule()");

		SysModule sysModule = this.sysModuleService.findSysModuleById(this.createdSysModuleRid);

		System.out.println("CreationDate: " + sysModule.getCreationDate());

		this.sysModuleService.deleteSysModule(sysModule);
		System.out.println("Deleted SysModule with Id[" + sysModule.getRid() + "]");

		System.out.println("##########");

	}

}
