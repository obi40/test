package com.optimiza.ehope.lis.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import com.optimiza.core.admin.model.SecTenant;
import com.optimiza.core.admin.service.SecTenantService;
import com.optimiza.core.base.helper.SearchCriterion;
import com.optimiza.core.base.helper.SearchCriterion.FilterOperator;

/**
 * SecTenantTestNG.class
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Aug/3/2017
 **/
@Test(priority = 1)
@ContextConfiguration(locations = { "classpath:spring-test-config.xml" })
public class SecTenantTestNG extends AbstractTestNGSpringContextTests {

	@Autowired
	private SecTenantService service;

	private SecTenant createdSt;

	@Test(priority = 1, enabled = true)
	public void testCreateLkpTenantStatus() {

		SecTenant st = new SecTenant();
		st.setCreatedBy(1L);
		st.setCreationDate(new Date());

		System.out.println("##########");

		this.createdSt = this.service.createSecTenant(st);

		System.out.println("Created St with Id[" + this.createdSt.getRid() + "]");

		System.out.println("##########");

	}

	@Test(priority = 2, enabled = true)
	public void testFindLkpTenantStatusById() {

		System.out.println("##########");

		SecTenant st = this.service.findSecTenantById(this.createdSt.getRid());
		System.out.println("Found St with Id[" + this.createdSt.getRid() + "]: " + st);

		System.out.println("##########");
	}

	@Test(priority = 3, enabled = true)
	public void testUpdateLkpTenantStatus() {

		System.out.println("##########");

		createdSt.setPhoneNo(886641858L);
		createdSt = this.service.updateSecTenant(createdSt);

		System.out.println("Updated St with Id[" + this.createdSt.getRid() + "]: " + createdSt);

		System.out.println("##########");

	}

	@Test(priority = 4, enabled = true)
	public void testDeleteLkpTenantLicenseType() {

		System.out.println("##########");
		createdSt = this.service.findSecTenantById(4L);
		System.out.println("~~~~~~~" + createdSt.getCreationDate());
		this.service.deleteSecTenant(createdSt);
		System.out.println("Deleted St with Id[" + this.createdSt.getRid() + "]");

		System.out.println("##########");

	}

	@Test(priority = 5, enabled = true)
	public void testFindSecTenantsMap() {

		System.out.println("##########");

		List<SearchCriterion> filters = new ArrayList<>();
		SearchCriterion sc = new SearchCriterion();
		sc.setField("phoneNo");
		sc.setValue(123L);
		sc.setOperator(FilterOperator.eq);
		filters.add(sc);

		List<Order> orders = new ArrayList<>();
		Order order1 = new Order(Sort.Direction.DESC, "rid");
		Order order2 = new Order(Sort.Direction.DESC, "lkpTenantStatus");
		orders.add(order1);
		orders.add(order2);

		int pageIndex = 0;
		int pageSize = 2;

		//this.service.findSecTenantsPage(pageIndex, pageSize, orders, filters);

	}

}
