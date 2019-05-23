package com.optimiza.core.admin.repo;

import java.util.Date;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.optimiza.core.admin.model.SecUser;
import com.optimiza.core.base.repo.GenericRepository;

/**
 * SecUserRepo.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Sep/27/2017
 **/

@Repository("SecUserRepo")
public interface SecUserRepo extends GenericRepository<SecUser> {

	@Query("SELECT su.password FROM SecUser su WHERE su.rid=?1")
	String fetchPasswordById(Long rid);

	SecUser findByUsernameIgnoreCase(String username);

	SecUser findByEmailIgnoreCase(String email);

	@Modifying
	@Query("UPDATE SecUser su SET su.lastLoginTime=?2 WHERE su.rid=?1")
	void updateLastLoginTime(Long rid, Date date);

}
