package com.optimiza.core.admin.repo;

import org.springframework.stereotype.Repository;

import com.optimiza.core.admin.model.SecGroup;
import com.optimiza.core.admin.model.SecGroupRole;
import com.optimiza.core.admin.model.SecRole;
import com.optimiza.core.base.repo.GenericRepository;

/**
 * SecGroupRoleRepo.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Sep/27/2017
 **/

@Repository("SecGroupRoleRepo")
public interface SecGroupRoleRepo extends GenericRepository<SecGroupRole> {

	void deleteAllBySecRole(SecRole secRole);

	void deleteAllBySecGroup(SecGroup secGroup);

}
