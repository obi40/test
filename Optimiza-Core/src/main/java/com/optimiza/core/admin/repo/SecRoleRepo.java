package com.optimiza.core.admin.repo;

import org.springframework.stereotype.Repository;

import com.optimiza.core.admin.model.SecRole;
import com.optimiza.core.base.repo.GenericRepository;

/**
 * SecRoleRepo.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Sep/27/2017
 **/

@Repository("SecRoleRepo")
public interface SecRoleRepo extends GenericRepository<SecRole> {

}
