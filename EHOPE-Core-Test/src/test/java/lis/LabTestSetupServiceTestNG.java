package lis;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import com.optimiza.core.base.helper.SearchCriterion;
import com.optimiza.core.base.helper.SearchCriterion.FilterOperator;
import com.optimiza.core.common.helper.FilterablePageRequest;
import com.optimiza.core.common.helper.FilterablePageRequest.JunctionOperator;
import com.optimiza.core.common.helper.FilterablePageRequest.OrderObject;
import com.optimiza.ehope.lis.model.LabTestSetup;
import com.optimiza.ehope.lis.service.LabTestSetupService;

@Test(priority = 1)
@ContextConfiguration(locations = { "classpath:spring-test-config.xml" })
public class LabTestSetupServiceTestNG extends AbstractTestNGSpringContextTests {

	@Autowired
	private LabTestSetupService service;

	@Test(priority = 1, enabled = false)
	public void testFindAll() {
		System.out.println("############ LabTestSetup count = " + service.findAll().size());
	}

	@Test(priority = 2, enabled = false)
	public void testAdd() {
		LabTestSetup labTestSetup = new LabTestSetup();
		//				labTestSetup.setContainerTypeId(1L);
		//		labTestSetup.setMethodId(1L);
		//		labTestSetup.setName("Test 1");
		//		labTestSetup.setPrice(1L);
		//		labTestSetup.setPrintSeq(1L);
		//		labTestSetup.setSectionId(1L);
		//				labTestSetup.setTestLineModeId(1L);
		//				labTestSetup.setTestTypeId(1L);
		//		labTestSetup.setStatusId(1);
		System.out.println("############ LabTestSetup count = " + service.addTestSetup(labTestSetup));
	}

	@Test(priority = 1, enabled = true)
	public void testFindPag() {

		FilterablePageRequest filterablePageRequest = new FilterablePageRequest();
		filterablePageRequest.setPage(0);
		filterablePageRequest.setSize(10);
		filterablePageRequest.setSortList(new ArrayList<OrderObject>());

		List<SearchCriterion> filters = new ArrayList<SearchCriterion>();

		SearchCriterion searchCriterion = new SearchCriterion();
		searchCriterion.setField("code");
		searchCriterion.setValue("cbc");
		searchCriterion.setOperator(FilterOperator.eq);
		filters.add(searchCriterion);

		filterablePageRequest.setFilters(filters);
		Page<LabTestSetup> result = service.find(filterablePageRequest.getFilters(), filterablePageRequest.getPageRequest(),
				LabTestSetup.class, JunctionOperator.And, "testLineMode");

		System.out.println("WWWWWWWWWWWWW  ################# " + result.getContent().size());
	}

}
