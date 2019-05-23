package com.optimiza.core.admin.lkp.repo;

import org.springframework.stereotype.Repository;

import com.optimiza.core.admin.lkp.model.LkpUserStatus;
import com.optimiza.core.base.repo.GenericRepository;

/**
 * LkpUserStatusRepo.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Sep/27/2017
 **/

@Repository("LkpUserStatusRepo")
public interface LkpUserStatusRepo extends GenericRepository<LkpUserStatus> {

}
