package com.optimiza.ehope.lis.onboarding.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.optimiza.core.admin.model.SecTenant;
import com.optimiza.core.base.helper.SearchCriterion;
import com.optimiza.core.base.service.GenericService;
import com.optimiza.core.common.util.SecurityUtil;
import com.optimiza.ehope.lis.onboarding.model.BrdPlan;
import com.optimiza.ehope.lis.onboarding.model.BrdTenantSubscription;
import com.optimiza.ehope.lis.onboarding.repo.BrdTenantSubscriptionRepo;

/**
 * TenantSubscriptionService.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since May/20/2018
 **/

@Service("BrdTenantSubscriptionService")
public class BrdTenantSubscriptionService extends GenericService<BrdTenantSubscription, BrdTenantSubscriptionRepo> {

	@Autowired
	private BrdTenantSubscriptionRepo tenantSubscriptionRepo;

	public BrdTenantSubscription createTenantSubscription(SecTenant tenant, Date expiryDate, BrdPlan plan) {
		BrdTenantSubscription tenantSubscription = new BrdTenantSubscription();
		tenantSubscription.setTenant(tenant);
		tenantSubscription.setExpiryDate(expiryDate);
		tenantSubscription.setPlan(plan);
		tenantSubscription.setUpdatedBy(SecurityUtil.getSystemUser().getRid());// since the webhook updates the tenant and no user is set there
		return getRepository().save(tenantSubscription);
	}

	public List<BrdTenantSubscription> findTenantSubscriptions(List<SearchCriterion> filters, Sort sort, String... joins) {
		return getRepository().find(filters, BrdTenantSubscription.class, sort, joins);
	}

	public BrdTenantSubscription findTenantActiveSubscription(Long tenantRid) {
		return getRepository().findTenantActiveSubscription(tenantRid, new Date());
	}

	public BrdTenantSubscription updateTenantSubscription(BrdTenantSubscription tenantSubscription) {
		tenantSubscription.setUpdatedBy(SecurityUtil.getSystemUser().getRid());// since the webhook updates the tenant and no user is set there
		return getRepository().save(tenantSubscription);
	}

	@Override
	protected BrdTenantSubscriptionRepo getRepository() {
		return this.tenantSubscriptionRepo;
	}

}
