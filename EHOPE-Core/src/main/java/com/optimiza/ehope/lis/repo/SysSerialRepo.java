package com.optimiza.ehope.lis.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.ehope.lis.model.SysSerial;

/**
 * SysSerialRepo.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Aug/06/2018
 **/
@Repository("SysSerialRepo")
public interface SysSerialRepo extends GenericRepository<SysSerial> {

	@Query("SELECT ss FROM SysSerial ss "
			+ "LEFT JOIN FETCH ss.serialFormat "
			+ "LEFT JOIN FETCH ss.serialType st "
			+ "LEFT JOIN FETCH ss.labBranch lb "
			+ "WHERE "
			+ "st.code = :serialType AND "
			+ "(lb IS NULL OR lb.rid = :branchRid)")
	List<SysSerial> findBySerialTypeBranch(@Param("serialType") String serialType, @Param("branchRid") Long branchRid);
}
