package com.optimiza.core.lkp.repo;

import org.springframework.stereotype.Repository;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.core.lkp.model.LkpMaster;

/**
 * LkpMasterRepo.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Dec/12/2017
 **/

@Repository("LkpMasterRepo")
public interface LkpMasterRepo extends GenericRepository<LkpMaster> {

}
