package com.optimiza.core.system.repo;

import org.springframework.stereotype.Repository;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.core.system.model.SysPage;

/**
 * SysPageRepo.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Dec/05/2017
 **/

@Repository("SysPageRepo")
public interface SysPageRepo extends GenericRepository<SysPage> {

}
