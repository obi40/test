package com.optimiza.ehope.lis.repo;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.ehope.lis.model.BillChargeSlip;
import com.optimiza.ehope.lis.model.BillPatientTransaction;

/**
 * BillPatientTransactionRepo.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Feb/19/2018
 **/

@Repository("BillPatientTransactionRepo")
public interface BillPatientTransactionRepo extends GenericRepository<BillPatientTransaction> {

	@Query("SELECT DISTINCT bpt FROM BillPatientTransaction bpt "
			+ "LEFT JOIN FETCH bpt.lkpAmountType "
			+ "LEFT JOIN FETCH bpt.lkpPaymentMethod "
			+ "LEFT JOIN FETCH bpt.lkpPaymentCurrency "
			+ "LEFT JOIN FETCH bpt.lkpTransactionType ltt "
			+ "LEFT JOIN FETCH bpt.billChargeSlip bcs "
			+ "LEFT JOIN bcs.labTestActual lta "
			+ "WHERE "
			+ "lta.labSample IN (SELECT ls FROM LabSample ls WHERE ls.emrVisit.rid = :visitRid)")
	Set<BillPatientTransaction> findByVisit(@Param("visitRid") Long visitRid);

	void deleteAllByBillChargeSlipIn(List<BillChargeSlip> billChargeSlips);

}
