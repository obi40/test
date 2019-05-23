package com.optimiza.ehope.lis.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import com.optimiza.ehope.lis.service.testImport.ImportTestSetupService;

@Test(priority = 1)
@ContextConfiguration(locations = { "classpath:spring-test-config.xml" })
public class ImportTestSetupServiceTestNG extends AbstractTestNGSpringContextTests {

	@Autowired
	private ImportTestSetupService service;

	@Test(priority = 1, enabled = true)
	public void importAll() {
		//1st call this
		//service.importTestsFromExcel();

		//2nd this
		//service.importExtraTestsFromExcel();

		//3rd this
		//service.bindMayoToLoinc();

		//extract data from html into json
		//service.extractFromHtml();

		//then import json
		//service.importJson();

		//now parse the normal ranges (incomplete)
		service.parseAllRefVals();

		//parse loinc attributes
		//service.parseLoincAttributes();

	}

}
