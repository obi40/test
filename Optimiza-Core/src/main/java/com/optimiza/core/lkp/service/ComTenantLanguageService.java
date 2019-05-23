package com.optimiza.core.lkp.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;

import com.optimiza.core.base.helper.SearchCriterion;
import com.optimiza.core.base.helper.SearchCriterion.FilterOperator;
import com.optimiza.core.base.service.GenericService;
import com.optimiza.core.common.annotation.InterceptorFree;
import com.optimiza.core.common.util.CollectionUtil;
import com.optimiza.core.lkp.model.ComTenantLanguage;
import com.optimiza.core.lkp.repo.ComTenantLanguageRepo;

/**
 * ComTenantLanguageService.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Nov/07/2017
 **/

@Service("ComTenantLanguageService")
public class ComTenantLanguageService extends GenericService<ComTenantLanguage, ComTenantLanguageRepo> {

	@Autowired
	private ComTenantLanguageRepo comTenantLanguageRepo;

	@InterceptorFree
	public List<ComTenantLanguage> createTenantLanguages(List<ComTenantLanguage> tenantLanguages) {
		return getRepository().save(tenantLanguages);
	}

	public List<ComTenantLanguage> updateTenantLanguages(List<ComTenantLanguage> tenantLanguages) {
		if (CollectionUtil.isCollectionEmpty(tenantLanguages)) {
			return new ArrayList<>();
		}
		getRepository().deleteAllByTenantId(tenantLanguages.get(0).getTenantId());
		getRepository().flush();//it causes an exception for unique key because save occurs before delete
		return getRepository().save(tenantLanguages);
	}

	public List<ComTenantLanguage> findTenantLanguages(List<SearchCriterion> filters, Sort sort, String... joins) {
		return getRepository().find(filters, ComTenantLanguage.class, sort, joins);
	}

	public List<ComTenantLanguage> findTenantExcelLanguages() {
		Sort sort = new Sort(new Order(Direction.DESC, "isPrimary"));
		return getRepository().find(new ArrayList<>(), ComTenantLanguage.class, sort, "comLanguage");
	}

	@InterceptorFree
	public List<ComTenantLanguage> findTenantLanguagesExcluded(List<SearchCriterion> filters, Sort sort, String... joins) {
		return getRepository().find(filters, ComTenantLanguage.class, sort, joins);
	}

	public String getTenantNamePrimary() {
		return getRepository().findOne(Arrays.asList(new SearchCriterion("isNamePrimary", Boolean.TRUE, FilterOperator.eq)),
				ComTenantLanguage.class, "comLanguage").getComLanguage().getLocale();
	}

	@Override
	protected ComTenantLanguageRepo getRepository() {
		return comTenantLanguageRepo;
	}

}
