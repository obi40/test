package com.optimiza.ehope.lis.repo;

import org.springframework.stereotype.Repository;

import com.optimiza.core.base.repo.GenericRepository;
import com.optimiza.ehope.lis.model.BillBalanceTransaction;

/**
 * BillBalanceTransactionRepo.java
 * 
 * @author Abdullah Imran <aImran@optimizasolutions.com>
 * @since Jul/09/2018
 **/

@Repository("BillBalanceTransactionRepo")
public interface BillBalanceTransactionRepo extends GenericRepository<BillBalanceTransaction> {

}
