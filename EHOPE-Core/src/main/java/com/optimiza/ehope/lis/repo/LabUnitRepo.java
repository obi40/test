package com.optimiza.ehope.lis.repo;

import org.springframework.stereotype.Repository;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.ehope.lis.model.LabUnit;

/**
 * LabUnitRepo.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Oct/12/2017
 **/

@Repository("LabUnitRepo")
public interface LabUnitRepo extends GenericRepository<LabUnit> {

}
