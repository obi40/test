package com.optimiza.core.admin.repo;

import org.springframework.stereotype.Repository;

import com.optimiza.core.admin.model.SecGroup;
import com.optimiza.core.base.repo.GenericRepository;

/**
 * SecGroupRepo.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Sep/27/2017
 **/

@Repository("SecGroupRepo")
public interface SecGroupRepo extends GenericRepository<SecGroup> {

}
