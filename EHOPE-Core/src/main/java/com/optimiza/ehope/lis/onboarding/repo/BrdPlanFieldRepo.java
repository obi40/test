package com.optimiza.ehope.lis.onboarding.repo;

import org.springframework.stereotype.Repository;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.ehope.lis.onboarding.model.BrdPlanField;

/**
 * PlanFieldRepo.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since May/20/2018
 **/

@Repository("BrdPlanFieldRepo")
public interface BrdPlanFieldRepo extends GenericRepository<BrdPlanField> {

}
