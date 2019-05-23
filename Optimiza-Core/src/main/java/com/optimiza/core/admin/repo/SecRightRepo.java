package com.optimiza.core.admin.repo;

import org.springframework.stereotype.Repository;

import com.optimiza.core.admin.model.SecRight;
import com.optimiza.core.base.repo.GenericRepository;

/**
 * SecRightRepo.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Sep/27/2017
 **/

@Repository("SecRightRepo")
public interface SecRightRepo extends GenericRepository<SecRight> {

}
