package com.optimiza.ehope.lis.lkp.repo;

import org.springframework.stereotype.Repository;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.ehope.lis.lkp.model.LkpOrganismType;

/**
 * LkpOrganismTypeRepo.java
 * 
 * @author Murad Poladian <mpoladian@optimizasolutions.com>
 * @since Dec/3/2018
 * 
 */
@Repository("LkpOrganismTypeRepo")
public interface LkpOrganismTypeRepo extends GenericRepository<LkpOrganismType> {

}
