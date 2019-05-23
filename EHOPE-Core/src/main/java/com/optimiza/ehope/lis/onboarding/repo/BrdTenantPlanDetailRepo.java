package com.optimiza.ehope.lis.onboarding.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.ehope.lis.onboarding.model.BrdTenantPlanDetail;

/**
 * TenantPlanDetailRepo.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since May/20/2018
 **/

@Repository("BrdTenantPlanDetailRepo")
public interface BrdTenantPlanDetailRepo extends GenericRepository<BrdTenantPlanDetail> {

	void deleteAllByTenantRid(Long rid);

	@Query("SELECT btpd FROM BrdTenantPlanDetail btpd "
			+ "LEFT JOIN btpd.tenant t "
			+ "LEFT JOIN btpd.planField pf "
			+ "LEFT JOIN pf.lkpPlanFieldType lpft "
			+ "WHERE t.rid = :tenantRid AND lpft.code = :planFieldType")
	BrdTenantPlanDetail findByPlanFieldType(@Param("tenantRid") Long tenantRid, @Param("planFieldType") String planFieldType);
}
