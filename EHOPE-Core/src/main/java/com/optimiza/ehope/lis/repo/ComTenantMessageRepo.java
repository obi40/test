package com.optimiza.ehope.lis.repo;

import org.springframework.stereotype.Repository;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.ehope.lis.model.ComTenantMessage;

/**
 * ComTenantMessageRepo.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Sep/12/2017
 **/

@Repository("ComTenantMessageRepo")
public interface ComTenantMessageRepo extends GenericRepository<ComTenantMessage> {

	ComTenantMessage findOneByCodeIgnoreCase(String code);

}
