package com.optimiza.core.admin.repo;

import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.optimiza.core.admin.model.SecRole;
import com.optimiza.core.admin.model.SecUser;
import com.optimiza.core.admin.model.SecUserRole;
import com.optimiza.core.base.repo.GenericRepository;

/**
 * SecUserRoleRepo.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Sep/27/2017
 **/

@Repository("SecUserRoleRepo")
public interface SecUserRoleRepo extends GenericRepository<SecUserRole> {

	void deleteAllBySecUser(SecUser secUser);

	void deleteAllBySecRole(SecRole secRole);

	@Query("SELECT DISTINCT sur FROM SecUserRole sur "
			+ "LEFT JOIN sur.secUser user "
			+ "LEFT JOIN FETCH sur.secRole r "
			+ "LEFT JOIN FETCH r.secRoleRights rr "
			+ "LEFT JOIN FETCH rr.secRight "
			+ "WHERE "
			+ "user.rid = :userRid")
	Set<SecUserRole> getBySecUser(@Param("userRid") Long userRid);
}
