package com.optimiza.ehope.lis.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import com.optimiza.ehope.lis.model.EmrPatientInfo;
import com.optimiza.ehope.lis.service.LabTestSetupService;

@Test(priority = 1)
@ContextConfiguration(locations = { "classpath:spring-test-config.xml" })
public class LabTestSetupServiceTestNG extends AbstractTestNGSpringContextTests {

	@Autowired
	private LabTestSetupService service;

	@Test(priority = 1, enabled = true)
	public void testFindAll() {

		System.out.println("############ LabTestSetup count = " + service.find(EmrPatientInfo.class, null, null, null).size());
		System.out.println("############ LabTestSetup count = " + service.find(EmrPatientInfo.class, null, null, null).size());
		System.out.println("############ LabTestSetup count = " + service.find(EmrPatientInfo.class, null, null, null).size());
		System.out.println("############ LabTestSetup count = " + service.find(EmrPatientInfo.class, null, null, null).size());
		System.out.println("############ LabTestSetup count = " + service.find(EmrPatientInfo.class, null, null, null).size());
	}

}
