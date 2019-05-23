package com.optimiza.ehope.lis.repo;

import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.ehope.lis.model.BillChargeSlip;

/**
 * BillChargeSlipRepo.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Feb/19/2018
 **/

@Repository("BillChargeSlipRepo")
public interface BillChargeSlipRepo extends GenericRepository<BillChargeSlip> {

	@Query("SELECT DISTINCT bcs FROM BillChargeSlip bcs "
			+ "LEFT JOIN FETCH bcs.labTestActual lta "
			+ "LEFT JOIN lta.labSample "
			+ "WHERE lta.labSample IN "
			+ "(SELECT ls FROM LabSample ls WHERE ls.emrVisit.rid = :visitRid)")
	Set<BillChargeSlip> findByVisit(@Param("visitRid") Long visitRid);

	@Query("SELECT DISTINCT bcs FROM BillChargeSlip bcs "
			+ "LEFT JOIN FETCH bcs.billPatientTransactionList bpt "
			+ "LEFT JOIN FETCH bpt.lkpTransactionType "
			+ "LEFT JOIN FETCH bcs.labTestActual lta "
			+ "LEFT JOIN FETCH lta.testDestination td "
			+ "LEFT JOIN FETCH td.destinationBranch "
			+ "LEFT JOIN FETCH td.type "
			+ "LEFT JOIN lta.labSample "
			+ "WHERE lta.labSample IN "
			+ "(SELECT ls FROM LabSample ls WHERE ls.emrVisit.rid = :visitRid)")
	Set<BillChargeSlip> getCancelData(@Param("visitRid") Long visitRid);

	@Query("SELECT DISTINCT bcs FROM BillChargeSlip bcs "
			+ "LEFT JOIN FETCH bcs.labTestActual lta "
			+ "LEFT JOIN FETCH lta.testDestination td "
			+ "LEFT JOIN FETCH td.destinationBranch "
			+ "LEFT JOIN FETCH td.type "
			+ "LEFT JOIN lta.labSample "
			+ "WHERE "
			+ "bcs.isCancelled = false AND "
			+ "lta.labSample IN (SELECT ls FROM LabSample ls WHERE ls.emrVisit.rid = :visitRid)")
	Set<BillChargeSlip> getRecalculateData(@Param("visitRid") Long visitRid);

}
