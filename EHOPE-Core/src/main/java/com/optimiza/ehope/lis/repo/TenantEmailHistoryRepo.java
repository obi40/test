package com.optimiza.ehope.lis.repo;

import org.springframework.stereotype.Repository;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.ehope.lis.model.TenantEmailHistory;

/**
 * TenantEmailHistoryRepo.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Apr/07/2019
 **/

@Repository("TenantEmailHistoryRepo")
public interface TenantEmailHistoryRepo extends GenericRepository<TenantEmailHistory> {

}
