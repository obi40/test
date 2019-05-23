package com.optimiza.ehope.lis.repo;

import org.springframework.stereotype.Repository;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.ehope.lis.model.Organism;

/**
 * OrganismRepo.java
 * 
 * @author Murad Poladian <mpoladian@optimizasolutions.com>
 * @since Dec/3/2018
 * 
 */
@Repository("OrganismRepo")
public interface OrganismRepo extends GenericRepository<Organism> {

}
