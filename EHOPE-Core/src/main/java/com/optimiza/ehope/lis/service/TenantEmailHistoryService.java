package com.optimiza.ehope.lis.service;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.optimiza.core.base.helper.SearchCriterion;
import com.optimiza.core.base.helper.SearchCriterion.FilterOperator;
import com.optimiza.core.base.service.GenericService;
import com.optimiza.core.common.helper.FilterablePageRequest;
import com.optimiza.ehope.lis.model.TenantEmailHistory;
import com.optimiza.ehope.lis.repo.TenantEmailHistoryRepo;

/**
 * TenantEmailHistoryService.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Apr/07/2019
 **/

@Service("TenantEmailHistoryService")
public class TenantEmailHistoryService extends GenericService<TenantEmailHistory, TenantEmailHistoryRepo> {

	@Autowired
	private TenantEmailHistoryRepo tenantEmailHistoryRepo;

	public TenantEmailHistory createTenantEmailHistory(String email) {
		email = email.toLowerCase();
		TenantEmailHistory previous = getRepository().findOne(Arrays.asList(new SearchCriterion("email", email, FilterOperator.eq)),
				TenantEmailHistory.class);
		//Silent return without exception
		if (previous != null) {
			return null;
		}
		TenantEmailHistory teh = new TenantEmailHistory();
		teh.setEmail(email);
		return getRepository().save(teh);
	}

	public Page<TenantEmailHistory> getTenantEmailHistoryPage(FilterablePageRequest filterablePageRequest) {
		String email = filterablePageRequest.getStringFilter("email").toLowerCase();
		return getRepository().find(Arrays.asList(new SearchCriterion("email", email, FilterOperator.startswith)),
				filterablePageRequest.getPageRequest(), TenantEmailHistory.class);
	}

	@Override
	protected TenantEmailHistoryRepo getRepository() {
		return tenantEmailHistoryRepo;
	}

}
