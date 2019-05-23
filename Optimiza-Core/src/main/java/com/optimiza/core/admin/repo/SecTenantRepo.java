package com.optimiza.core.admin.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.optimiza.core.admin.model.SecTenant;
import com.optimiza.core.base.repo.GenericRepository;

/**
 * SecTenantRepo.class
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Aug/3/2017
 **/
@Repository("SecTenantRepo")
public interface SecTenantRepo extends GenericRepository<SecTenant> {

	@Query("SELECT st FROM SecTenant st "
			+ "LEFT JOIN FETCH st.country c "
			+ "LEFT JOIN FETCH c.currency "
			+ "WHERE st.rid = :rid")
	SecTenant fetchTenantDataById(@Param("rid") Long rid);

	SecTenant findByEmail(String email);

	SecTenant findByCode(String code);

}
