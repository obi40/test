package com.optimiza.ehope.lis.repo;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.ehope.lis.model.HistoricalResult;

/**
 * HistoricalResultRepo.java
 * 
 * @author Murad Poladian <mpoladian@optimizasolutions.com>
 * @since Nov/11/2018
 * 
 */
@Repository("HistoricalResultRepo")
public interface HistoricalResultRepo extends GenericRepository<HistoricalResult> {

	@Query("SELECT DISTINCT hr FROM HistoricalResult hr "
			+ "LEFT JOIN FETCH hr.test ht "
			+ "LEFT JOIN FETCH ht.order ho "
			+ "WHERE ho.patientFileNo = :patientFileNo "
			+ "AND ht.testCode = :testCode "
			+ "AND hr.resultCode = :resultCode "
			+ "ORDER BY ho.orderDate DESC")
	public List<HistoricalResult> getLatestHistoricalResults(@Param("patientFileNo") String patientFileNo,
			@Param("testCode") String testCode,
			@Param("resultCode") String resultCode,
			Pageable pageable);

}
