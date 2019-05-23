package com.optimiza.ehope.lis.repo;

import org.springframework.stereotype.Repository;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.ehope.lis.model.Interpretation;

/**
 * InterpretationRepo.java
 * 
 * @author Murad Poladian <mpoladian@optimizasolutions.com>
 * @since Dec/16/2018
 **/
@Repository("InterpretationRepo")
public interface InterpretationRepo extends GenericRepository<Interpretation> {

}
