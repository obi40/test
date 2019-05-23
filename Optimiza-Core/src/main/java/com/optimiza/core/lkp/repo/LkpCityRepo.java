package com.optimiza.core.lkp.repo;

import org.springframework.stereotype.Repository;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.core.lkp.model.LkpCity;

/**
 * LkpCityRepo.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Oct/08/2017
 **/

@Repository("LkpCityRepo")
public interface LkpCityRepo extends GenericRepository<LkpCity> {

}
