package com.optimiza.ehope.lis.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.optimiza.core.admin.model.SecTenant;
import com.optimiza.core.admin.service.SecTenantService;
import com.optimiza.core.base.entity.BaseEntity;
import com.optimiza.core.base.helper.SearchCriterion;
import com.optimiza.core.base.helper.SearchCriterion.FilterOperator;
import com.optimiza.core.base.helper.SearchCriterion.JunctionOperator;
import com.optimiza.core.base.service.GenericService;
import com.optimiza.core.common.annotation.InterceptorFree;
import com.optimiza.core.common.business.exception.BusinessException;
import com.optimiza.core.common.business.exception.BusinessException.ErrorSeverity;
import com.optimiza.core.common.helper.FilterablePageRequest;
import com.optimiza.core.common.util.SecurityUtil;
import com.optimiza.ehope.lis.helper.EhopeRights;
import com.optimiza.ehope.lis.model.ComTenantMessage;
import com.optimiza.ehope.lis.repo.ComTenantMessageRepo;

/**
 * ComTenantMessageService.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Sep/12/2017
 **/

@Service("ComTenantMessageService")
public class ComTenantMessageService extends GenericService<ComTenantMessage, ComTenantMessageRepo> {

	@Autowired
	private ComTenantMessageRepo comTenantMessagesRepo;

	@Autowired
	private SecTenantService tenantService;

	public boolean isDuplicate(ComTenantMessage comTenantMessage) {
		return getRepository().findOneByCodeIgnoreCase(comTenantMessage.getCode()) != null;
	}

	@CacheEvict(cacheNames = "ComTenantMessage", allEntries = false, keyGenerator = "customCacheKeyGenerator")
	public void createTenantMessage(ComTenantMessage tenantMessage, Class<? extends BaseEntity> entityClass) {
		SecurityUtil.authorizeApplicationAdmin();
		if (isDuplicate(tenantMessage)) {
			throw new BusinessException(
					"Duplication Message with Code: " + tenantMessage.getCode(), "messageNotUnique",
					ErrorSeverity.ERROR);
		}

		//Create new label for all tenants
		List<SecTenant> tenants = tenantService.find(new ArrayList<>(), SecTenant.class);
		for (SecTenant tenant : tenants) {
			ComTenantMessage msg = new ComTenantMessage();
			msg.setCode(tenantMessage.getCode());
			msg.setLkpMessagesType(tenantMessage.getLkpMessagesType());
			msg.setDescription(tenantMessage.getDescription());
			msg.setTenantId(tenant.getRid());
			getRepository().save(msg);
		}

	}

	@InterceptorFree
	public void deleteTenantMessage(ComTenantMessage tenantMessage) {
		SecurityUtil.authorizeApplicationAdmin();
		//Delete label from all tenants
		List<SearchCriterion> filters = tenantService	.find(new ArrayList<>(), SecTenant.class).stream().map(
				t -> new SearchCriterion("tenantId", t.getRid(), FilterOperator.eq, JunctionOperator.Or))
														.collect(Collectors.toList());
		filters.add(new SearchCriterion("code", tenantMessage.getCode(), FilterOperator.eq, JunctionOperator.And));
		List<ComTenantMessage> toDeleteMsgs = getRepository().find(filters, ComTenantMessage.class);
		for (ComTenantMessage msg : toDeleteMsgs) {
			getRepository().delete(msg);
		}
	}

	@Cacheable(cacheNames = "ComTenantMessage", keyGenerator = "customCacheKeyGenerator")
	public List<ComTenantMessage> findLabels(Class<? extends BaseEntity> entityClass) {
		return getRepository().find(new ArrayList<>(), ComTenantMessage.class);
	}

	@Cacheable(cacheNames = "ComTenantMessage", key = "#entityClass.getSimpleName()")
	@InterceptorFree
	public List<ComTenantMessage> findDefaultLabels(Class<? extends BaseEntity> entityClass) {
		return getRepository().find(Arrays.asList(new SearchCriterion("tenantId", SecurityUtil.DEFAULT_TENANT, FilterOperator.eq)),
				ComTenantMessage.class);
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.VIEW_TENANT_MESSAGES + "')")
	public Page<ComTenantMessage> findTenantMessagesList(FilterablePageRequest filterablePageRequest) {
		return getRepository().find(filterablePageRequest.getFilters(), filterablePageRequest.getPageRequest(), ComTenantMessage.class,
				"lkpMessagesType");
	}

	@PreAuthorize("hasAuthority('" + EhopeRights.UPD_TENANT_MESSAGE + "')")
	@CacheEvict(cacheNames = "ComTenantMessage", allEntries = false, keyGenerator = "customCacheKeyGenerator")
	public ComTenantMessage updateTenantMessage(ComTenantMessage tenantMessage, Class<? extends BaseEntity> entityClass) {
		ComTenantMessage msg = getRepository().findOne(
				Arrays.asList(new SearchCriterion("code", tenantMessage.getCode(), FilterOperator.eq)), ComTenantMessage.class);
		//the user tried to create a new label,if this user is applicaiton admin then allow to create this new message
		if (msg == null && !SecurityUtil.isApplicationAdmin()) {
			throw new BusinessException("Only Application Admin can create a new Label", "requiresApplicationAdmin", ErrorSeverity.ERROR);
		}
		return getRepository().save(tenantMessage);
	}

	/**
	 * Excluded from custom logger.
	 * 
	 * @param filters
	 * @return List
	 */
	@InterceptorFree
	public List<ComTenantMessage> findTenantMessagesExcluded(List<SearchCriterion> filters) {
		return getRepository().find(filters, ComTenantMessage.class);
	}

	@Override
	protected ComTenantMessageRepo getRepository() {
		return comTenantMessagesRepo;
	}

}
