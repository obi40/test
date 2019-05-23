package com.optimiza.ehope.lis.repo;

import org.springframework.stereotype.Repository;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.ehope.lis.model.ActualOrganism;

/**
 * ActualOrganismRepo.java
 * 
 * @author Murad Poladian <mpoladian@optimizasolutions.com>
 * @since Dec/10/2018
 * 
 */
@Repository("ActualOrganismRepo")
public interface ActualOrganismRepo extends GenericRepository<ActualOrganism> {

}
