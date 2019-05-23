package com.optimiza.ehope.lis.lkp.repo;

import org.springframework.stereotype.Repository;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.ehope.lis.lkp.model.LkpResultValueType;

/**
 * LkpResultTypeRepo.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Oct/12/2017
 **/

@Repository("LkpResultValueTypeRepo")
public interface LkpResultValueTypeRepo extends GenericRepository<LkpResultValueType> {

}
