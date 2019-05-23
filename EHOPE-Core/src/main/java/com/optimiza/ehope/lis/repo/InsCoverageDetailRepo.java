package com.optimiza.ehope.lis.repo;

import org.springframework.stereotype.Repository;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.ehope.lis.model.InsCoverageDetail;
import com.optimiza.ehope.lis.model.InsProviderPlan;

/**
 * InsCoverageDetailRepo.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Jan/23/2018
 **/

@Repository("InsCoverageDetailRepo")
public interface InsCoverageDetailRepo extends GenericRepository<InsCoverageDetail> {

	void deleteAllByInsProviderPlan(InsProviderPlan providerPlan);
}
