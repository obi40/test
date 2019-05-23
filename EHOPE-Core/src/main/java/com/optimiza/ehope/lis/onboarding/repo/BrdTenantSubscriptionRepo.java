package com.optimiza.ehope.lis.onboarding.repo;

import java.util.Date;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.ehope.lis.onboarding.model.BrdTenantSubscription;

/**
 * TenantSubscriptionRepo.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since May/20/2018
 **/

@Repository("BrdTenantSubscriptionRepo")
public interface BrdTenantSubscriptionRepo extends GenericRepository<BrdTenantSubscription> {

	@Query("SELECT bts FROM BrdTenantSubscription bts "
			+ "LEFT JOIN FETCH bts.plan "
			+ "LEFT JOIN FETCH bts.tenant t "
			+ "WHERE t.rid = :tenantRid AND (bts.expiryDate IS NOT NULL AND :currentDate BETWEEN bts.creationDate AND bts.expiryDate)")
	BrdTenantSubscription findTenantActiveSubscription(@Param("tenantRid") Long tenantRid, @Param("currentDate") Date currentDate);
}
