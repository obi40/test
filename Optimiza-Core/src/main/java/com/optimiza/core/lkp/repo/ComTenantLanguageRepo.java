package com.optimiza.core.lkp.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.core.lkp.model.ComTenantLanguage;

/**
 * ComTenantLanguageRepo.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Nov/07/2017
 **/

@Repository("ComTenantLanguageRepo")
public interface ComTenantLanguageRepo extends GenericRepository<ComTenantLanguage> {

	// Used in SpringLoginService, since we don't have the tenant id injected yet in this phase
	@Query("SELECT ctl FROM ComTenantLanguage ctl "
			+ "LEFT JOIN FETCH ctl.comLanguage "
			+ "WHERE ctl.tenantId = :tenantId")
	List<ComTenantLanguage> fetchTenantLanguages(@Param("tenantId") Long tenantId);

	void deleteAllByTenantId(Long tenantId);
}
