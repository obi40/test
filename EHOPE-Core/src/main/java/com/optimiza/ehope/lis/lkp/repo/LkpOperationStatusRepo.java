package com.optimiza.ehope.lis.lkp.repo;

import org.springframework.stereotype.Repository;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.ehope.lis.lkp.model.LkpOperationStatus;

/**
 * LkpOperationStatusRepo.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Mar/14/2018
 **/

@Repository("LkpOperationStatusRepo")
public interface LkpOperationStatusRepo extends GenericRepository<LkpOperationStatus> {

}
