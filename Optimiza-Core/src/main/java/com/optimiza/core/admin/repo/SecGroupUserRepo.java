package com.optimiza.core.admin.repo;

import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.optimiza.core.admin.model.SecGroup;
import com.optimiza.core.admin.model.SecGroupUser;
import com.optimiza.core.admin.model.SecUser;
import com.optimiza.core.base.repo.GenericRepository;

/**
 * SecGroupUserRepo.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Sep/27/2017
 **/

@Repository("SecGroupUserRepo")
public interface SecGroupUserRepo extends GenericRepository<SecGroupUser> {

	void deleteAllBySecUser(SecUser secUser);

	void deleteAllBySecGroup(SecGroup secGroup);

	@Query("SELECT DISTINCT sgu FROM SecGroupUser sgu "
			+ "LEFT JOIN sgu.secUser user "
			+ "LEFT JOIN FETCH sgu.secGroup g "
			+ "LEFT JOIN FETCH g.secGroupRoles gr "
			+ "LEFT JOIN FETCH gr.secRole sr "
			+ "LEFT JOIN FETCH sr.secRoleRights srr "
			+ "LEFT JOIN FETCH srr.secRight "
			+ "WHERE "
			+ "user.rid = :userRid")
	Set<SecGroupUser> getBySecUser(@Param("userRid") Long userRid);

}
