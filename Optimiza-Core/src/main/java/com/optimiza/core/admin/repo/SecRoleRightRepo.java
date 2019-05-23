package com.optimiza.core.admin.repo;

import org.springframework.stereotype.Repository;

import com.optimiza.core.admin.model.SecRight;
import com.optimiza.core.admin.model.SecRole;
import com.optimiza.core.admin.model.SecRoleRight;
import com.optimiza.core.base.repo.GenericRepository;

/**
 * SecRoleRightRepo.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Sep/27/2017
 **/

@Repository("SecRoleRightRepo")
public interface SecRoleRightRepo extends GenericRepository<SecRoleRight> {

	void deleteAllBySecRole(SecRole secRole);

	void deleteAllBySecRight(SecRight secRight);
}
