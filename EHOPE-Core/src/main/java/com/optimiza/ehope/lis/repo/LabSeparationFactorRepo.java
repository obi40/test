package com.optimiza.ehope.lis.repo;

import org.springframework.stereotype.Repository;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.ehope.lis.model.LabSeparationFactor;

/**
 * LabSeparationFactorRepo.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Jan/07/2018
 **/

@Repository("LabSeparationFactorRepo")
public interface LabSeparationFactorRepo extends GenericRepository<LabSeparationFactor> {

}
