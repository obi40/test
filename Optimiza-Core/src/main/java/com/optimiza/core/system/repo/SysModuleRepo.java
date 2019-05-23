package com.optimiza.core.system.repo;

import org.springframework.stereotype.Repository;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.core.system.model.SysModule;

/**
 * SysModuleRepo.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Dec/05/2017
 **/

@Repository("SysModuleRepo")
public interface SysModuleRepo extends GenericRepository<SysModule> {

}
