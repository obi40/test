package com.optimiza.ehope.lis.repo;

import org.springframework.stereotype.Repository;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.ehope.lis.model.ConfTenantSMS;

/**
 * ConfTenantSMSRepo.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Nov/27/2018
 **/

@Repository("ConfTenantSMSRepo")
public interface ConfTenantSMSRepo extends GenericRepository<ConfTenantSMS> {

	void deleteAllByTenantId(Long rid);
}
