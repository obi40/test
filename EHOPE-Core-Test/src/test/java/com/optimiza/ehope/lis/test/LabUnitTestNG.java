package com.optimiza.ehope.lis.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import com.optimiza.ehope.lis.model.LabUnit;
import com.optimiza.ehope.lis.service.LabUnitService;

/**
 * LabUnitTestNG.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Oct/12/2017
 **/

@Test(priority = 1)
@ContextConfiguration(locations = { "classpath:spring-test-config.xml" })
public class LabUnitTestNG extends AbstractTestNGSpringContextTests {

	@Autowired
	private LabUnitService labUnitService;

	private Long createdLabUnitRid;

	@Test(priority = 1, enabled = true)
	public void testCreateLabUnit() {
		System.out.println("##########");
		System.out.println("testCreateLabUnit()");

		LabUnit labUnit = new LabUnit();
		labUnit = this.labUnitService.createLabUnit(labUnit);
		this.createdLabUnitRid = labUnit.getRid();

		System.out.println("Created LabUnit with Id[" + labUnit.getRid() + "]");
		System.out.println("##########");

	}

	@Test(priority = 2, enabled = true)
	public void testFindLabUnitById() {
		System.out.println("##########");
		System.out.println("testFindLabUnitById()");

		LabUnit labUnit = this.labUnitService.findLabUnitById(this.createdLabUnitRid);

		System.out.println("Found LabUnit with Id[" + labUnit.getRid() + "]: " + labUnit);
		System.out.println("##########");

	}

	@Test(priority = 3, enabled = true)
	public void testUpdateLabUnit() {
		System.out.println("##########");
		System.out.println("testUpdateLabUnit()");

		LabUnit labUnit = this.labUnitService.findLabUnitById(this.createdLabUnitRid);

		// UPDATE VALUES

		labUnit = this.labUnitService.updateLabUnit(labUnit);

		System.out.println("Updated LabUnit with Id[" + labUnit.getRid() + "]: " + labUnit);
		System.out.println("##########");

	}

	@Test(priority = 4, enabled = true)
	public void testDeleteLabUnit() {
		System.out.println("##########");
		System.out.println("testDeleteLabUnit()");

		LabUnit labUnit = this.labUnitService.findLabUnitById(this.createdLabUnitRid);

		System.out.println("CreationDate: " + labUnit.getCreationDate());

		this.labUnitService.deleteLabUnit(labUnit);
		System.out.println("Deleted LabUnit with Id[" + labUnit.getRid() + "]");

		System.out.println("##########");

	}

}
