package com.optimiza.ehope.lis.test;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import com.optimiza.ehope.lis.model.LabSection;
import com.optimiza.ehope.lis.service.LabSectionService;

@Test(priority = 1)
@ContextConfiguration(locations = { "classpath:spring-test-config.xml" })
public class LabSectionServiceTest extends AbstractTestNGSpringContextTests {

	@Autowired
	private LabSectionService service;

	@Test(priority = 1, enabled = true)
	public void testSearchSections() {
		List<LabSection> sectionsList = service.sectionSearch();

		sectionsList.stream().forEach(section ->
			{
				System.out.println(section.getName().get("en_us"));

				section.getTestDefinition().stream().forEach(test ->
					{
						System.out.println(test.getDescription());
					});

				System.out.println();
			});
	}

}
